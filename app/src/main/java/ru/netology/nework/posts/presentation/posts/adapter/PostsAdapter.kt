package ru.netology.nework.posts.presentation.posts.adapter

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import ru.netology.nework.R
import ru.netology.nework.core.utils.disableViewDuringAnimation
import ru.netology.nework.databinding.ItemPostBinding

class PostsAdapter(
    private val listener: PostEventListener
) : ListAdapter<PostItem, PostsAdapter.PostViewHolder>(PostDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPostBinding.inflate(inflater, parent, false)
        return PostViewHolder(binding = binding, listener = listener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: PostViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            payloads.forEach { payload ->
                when (payload) {
                    is LikePayload -> holder.updateLike(payload.likeByMe, payload.likeCount)
                    is ContentPayload -> holder.updateContent(payload.content)
                    is InProgressToDeletePayload -> holder.updateInProgressToDelete(payload.inProgressToDelete)
                    else -> holder.bind(getItem(position))
                }
            }
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    class PostViewHolder(
        private val binding: ItemPostBinding,
        private val listener: PostEventListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: PostItem) {
            with(binding) {
                tvAuthor.text = post.author
                tvPost.text = post.content
                tvDate.text = post.published
                ivIcon.load(post.authorAvatar) {
                    transformations(CircleCropTransformation())
                    placeholder(R.drawable.placeholder_load)
                    error(R.drawable.error_placeholder)
                }
                val likeIcon = if (post.likedByMe) R.drawable.ic_favorite_fill
                else R.drawable.ic_favorite_empty
                binding.ivLike.setImageResource(likeIcon)
                binding.tvLikeCount.text = post.likeCount.toString()

                ivMore.isInvisible = post.ownedByMe.not() || post.inProgressToDelete
                progressBar.isVisible = post.inProgressToDelete

                ivLike.setOnClickListener {
                    listener.onLikeClick(post.id, post.likedByMe)
                }

                root.setOnClickListener {
                    listener.onPostClick(post.id)
                }

                ivMore.setOnClickListener {
                    showPopup(post)
                }
            }
        }

        fun updateLike(likeByMe: Boolean, likeCount: Int) {
            animateLike(likeByMe)
            val likeIcon = if (likeByMe) R.drawable.ic_favorite_fill
            else R.drawable.ic_favorite_empty
            binding.ivLike.setImageResource(likeIcon)
            binding.tvLikeCount.text = likeCount.toString()
        }

        fun updateContent(content: String) {
            binding.tvPost.text = content
        }

        fun updateInProgressToDelete(inProgressToDelete: Boolean) {
            binding.progressBar.isVisible = inProgressToDelete
            binding.ivMore.isInvisible = inProgressToDelete
        }

        private fun showPopup(post: PostItem) {
            if (post.ownedByMe.not()) return
            PopupMenu(binding.root.context, binding.ivMore).apply {
                inflate(R.menu.menu_user_action)

                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.remove -> {
                            listener.onDeleteClick(post.id)
                            true
                        }
                        R.id.edit -> {
                            listener.onEditClick(post)
                            true
                        }
                        else -> false
                    }
                }
            }.show()
        }

        private fun animateLike(isLike: Boolean) {
            if (isLike.not()) return
            val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.5f)
            val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.5f)
            ObjectAnimator.ofPropertyValuesHolder(binding.ivLike, scaleX, scaleY).apply {
                duration = 400L
                repeatCount = 3
                repeatMode = ObjectAnimator.REVERSE
                disableViewDuringAnimation(binding.ivLike)
            }.start()
        }
    }

    class PostDiffUtil : DiffUtil.ItemCallback<PostItem>() {

        override fun areItemsTheSame(oldItem: PostItem, newItem: PostItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PostItem, newItem: PostItem): Boolean {
            return oldItem.likedByMe == newItem.likedByMe
                    && oldItem.content == newItem.content
                    && oldItem.likeCount == newItem.likeCount
                    && oldItem.inProgressToDelete == newItem.inProgressToDelete
        }

        override fun getChangePayload(oldItem: PostItem, newItem: PostItem): Any? {
            return when {
                oldItem.likedByMe != newItem.likedByMe && oldItem.likeCount != newItem.likeCount -> {
                    LikePayload(newItem.likedByMe, newItem.likeCount)
                }

                oldItem.content != newItem.content -> {
                    ContentPayload(newItem.content)
                }

                oldItem.inProgressToDelete != newItem.inProgressToDelete -> {
                    InProgressToDeletePayload(newItem.inProgressToDelete)
                }
                else -> super.getChangePayload(oldItem, newItem)
            }
        }
    }

    interface PostEventListener {
        fun onLikeClick(postId: Long, isLike: Boolean)
        fun onPostClick(postId: Long)
        fun onDeleteClick(postId: Long)
        fun onEditClick(post: PostItem)
    }
}

private data class LikePayload(
    val likeByMe: Boolean,
    val likeCount: Int
)

private data class ContentPayload(
    val content: String
)

private data class InProgressToDeletePayload(
    val inProgressToDelete: Boolean
)
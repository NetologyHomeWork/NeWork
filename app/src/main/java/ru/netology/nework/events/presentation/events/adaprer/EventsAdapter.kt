package ru.netology.nework.events.presentation.events.adaprer

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
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import coil.transform.CircleCropTransformation
import ru.netology.nework.R
import ru.netology.nework.core.utils.disableViewDuringAnimation
import ru.netology.nework.databinding.ItemEventBinding

class EventsAdapter(
    private val listener: EventListener
) : ListAdapter<EventItem, EventsAdapter.EventViewHolder>(EventDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemEventBinding.inflate(inflater, parent, false)
        return EventViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: EventViewHolder,
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

    class EventViewHolder(
        private val binding: ItemEventBinding,
        private val listener: EventListener
    ) : ViewHolder(binding.root) {

        fun bind(item: EventItem) {
            with (binding) {
                tvAuthor.text = item.author
                tvJob.text = item.authorJob
                tvDate.text = item.published
                tvContent.text = item.content
                tvLikeCount.text = item.likeCount.toString()

                val likeIcon = if (item.likedByMe) R.drawable.ic_favorite_fill
                else R.drawable.ic_favorite_empty
                binding.ivLike.setImageResource(likeIcon)

                ivMore.isInvisible = item.ownedByMe.not() || item.inProgressToDelete
                progressBar.isVisible = item.inProgressToDelete

                ivIcon.load(item.authorAvatar) {
                    transformations(CircleCropTransformation())
                    placeholder(R.drawable.placeholder_load)
                    error(R.drawable.error_placeholder)
                }

                ivLike.setOnClickListener {
                    listener.onLikeClick(item.id, item.likedByMe)
                }

                root.setOnClickListener {
                    listener.onEventClick(item.id)
                }

                ivMore.setOnClickListener {
                    showPopup(item)
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
            binding.tvContent.text = content
        }

        fun updateInProgressToDelete(inProgressToDelete: Boolean) {
            binding.progressBar.isVisible = inProgressToDelete
            binding.ivMore.isInvisible = inProgressToDelete
        }

        private fun showPopup(event: EventItem) {
            if (event.ownedByMe.not()) return
            PopupMenu(binding.root.context, binding.ivMore).apply {
                inflate(R.menu.menu_user_action)

                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.remove -> {
                            listener.onDeleteClick(event.id)
                            true
                        }
                        R.id.edit -> {
                            listener.onEditClick(event)
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

    class EventDiffUtil : DiffUtil.ItemCallback<EventItem>() {

        override fun areItemsTheSame(oldItem: EventItem, newItem: EventItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EventItem, newItem: EventItem): Boolean {
            return oldItem.likedByMe == newItem.likedByMe
                    && oldItem.content == newItem.content
                    && oldItem.likeCount == newItem.likeCount
                    && oldItem.inProgressToDelete == newItem.inProgressToDelete
        }

        override fun getChangePayload(oldItem: EventItem, newItem: EventItem): Any? {
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

    interface EventListener {
        fun onLikeClick(eventId: Long, isLike: Boolean)
        fun onEventClick(eventId: Long)
        fun onDeleteClick(eventId: Long)
        fun onEditClick(event: EventItem)
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
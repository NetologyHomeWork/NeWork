package ru.netology.nework.posts.presentation.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import ru.netology.nework.R
import ru.netology.nework.core.domain.entities.AttachmentType
import ru.netology.nework.core.utils.assistedViewModels
import ru.netology.nework.core.utils.observeWhenOnCreated
import ru.netology.nework.core.utils.observeWhenOnStarted
import ru.netology.nework.core.utils.viewBinding
import ru.netology.nework.create.presentation.edit.post.EditPostFragment
import ru.netology.nework.databinding.FragmentPostDetailBinding
import javax.inject.Inject

@AndroidEntryPoint
class PostDetailFragment : Fragment(R.layout.fragment_post_detail) {

    private val binding by viewBinding<FragmentPostDetailBinding>()

    private val args by navArgs<PostDetailFragmentArgs>()

    @Inject
    lateinit var factory: PostDetailViewModel.Factory

    private val viewModel by assistedViewModels { factory.create(args.postId) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.postFlow.observeWhenOnStarted(viewLifecycleOwner) { post ->
            with(binding) {
                ivIcon.load(post.authorAvatar) {
                    transformations(CircleCropTransformation())
                    placeholder(R.drawable.placeholder_load)
                    error(R.drawable.error_placeholder)
                }

                tvAuthor.text = post.author
                tvDate.text = post.published
                tvPost.text = post.content

                val like = if (post.likedByMe) R.drawable.ic_favorite_fill
                else R.drawable.ic_favorite_empty
                ivLike.setImageResource(like)
                tvLikeCount.text = post.likeCount.toString()

                post.attachment?.let { attachment ->
                    if (attachment.type == AttachmentType.IMAGE) {
                        ivAttachment.isVisible = true
                        ivAttachment.load(attachment.url) {
                            placeholder(R.drawable.placeholder_load)
                            error(R.drawable.error_placeholder)
                        }
                    }
                }

                tvInMap.isVisible = post.coords != null
                ivMore.isVisible = post.ownedByMe
            }

            binding.threeStateView.observeStateView(viewModel.threeStateFlow, viewLifecycleOwner)
        }

        viewModel.commands.observeWhenOnCreated(viewLifecycleOwner) { command ->
            when (command) {
                is PostDetailViewModel.PostDetailCommands.OpenUrl -> {
                    val gmmIntentUri = Uri.parse(command.url)
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                        startActivity(mapIntent)
                    } else {
                        viewModel.onOpenMapError()
                    }
                }

                is PostDetailViewModel.PostDetailCommands.ShowSnackbar -> {
                    Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        command.message,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                is PostDetailViewModel.PostDetailCommands.NavigateOnBackPressed -> {
                    setFragmentResult(
                        LIKE_RESULT_REQ_KEY,
                        bundleOf(
                            LIKE_RESULT_BUNDLE_KEY to LikeResult(
                                postId = command.postId,
                                isLike = command.isLike,
                                likeCount = command.likeCount
                            )
                        )
                    )
                    findNavController().navigateUp()
                }

                is PostDetailViewModel.PostDetailCommands.NavigateToEditPost -> {
                    val direction = PostDetailFragmentDirections
                        .actionPostDetailFragmentToEditPostFragment(command.postId, command.content)
                    findNavController().navigate(direction)
                }

                is PostDetailViewModel.PostDetailCommands.NavigateUpWhenPostDeleted -> {
                    setFragmentResult(
                        DELETE_RESULT_REQ_KEY,
                        bundleOf(DELETE_RESULT_BUNDLE_KEY to command.postId)
                    )
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun setupListeners() {
        binding.ivLike.setOnClickListener {
            viewModel.onLikeClick()
        }

        binding.ivMore.setOnClickListener {
            showPopup()
        }

        binding.tvInMap.setOnClickListener {
            viewModel.onInMapClick()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.onBackPressedClick()
        }

        setFragmentResultListener(EditPostFragment.SAVE_POST_REQ_KEY) { _, bundle ->
            val content = bundle.getString(EditPostFragment.SAVE_POST_CONTENT_KEY, "")
           viewModel.onPostUpdated(content)
        }
    }

    private fun showPopup() {
        PopupMenu(binding.root.context, binding.ivMore).apply {
            inflate(R.menu.menu_user_action)

            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.remove -> {
                        viewModel.onDeleteClick()
                        true
                    }
                    R.id.edit -> {
                        viewModel.onEditClick()
                        true
                    }
                    else -> false
                }
            }
        }.show()
    }

    @Parcelize
    data class LikeResult(
        val postId: Long,
        val isLike: Boolean,
        val likeCount: Int
    ) : Parcelable

    companion object {
        const val LIKE_RESULT_REQ_KEY = "LIKE_RESULT_REQ_KEY"
        const val LIKE_RESULT_BUNDLE_KEY = "LIKE_RESULT_BUNDLE_KEY"
        const val DELETE_RESULT_REQ_KEY = "DELETE_RESULT_REQ_KEY"
        const val DELETE_RESULT_BUNDLE_KEY = "DELETE_RESULT_BUNDLE_KEY"
    }
}
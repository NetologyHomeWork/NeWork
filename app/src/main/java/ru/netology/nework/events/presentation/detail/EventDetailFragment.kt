package ru.netology.nework.events.presentation.detail

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
import ru.netology.nework.create.presentation.edit.event.EditEventFragment
import ru.netology.nework.databinding.FragmentEventDetailBinding
import javax.inject.Inject

@AndroidEntryPoint
class EventDetailFragment : Fragment(R.layout.fragment_event_detail) {

    private val binding by viewBinding<FragmentEventDetailBinding>()

    private val args by navArgs<EventDetailFragmentArgs>()

    @Inject lateinit var factory: EventDetailViewModel.Factory

    private val viewModel by assistedViewModels { factory.create(args.eventId) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.eventFlow.observeWhenOnStarted(viewLifecycleOwner) { event ->
            with(binding) {
                ivIcon.load(event.authorAvatar) {
                    transformations(CircleCropTransformation())
                    placeholder(R.drawable.placeholder_load)
                    error(R.drawable.error_placeholder)
                }

                tvAuthor.text = event.author
                tvJob.text = event.authorJob
                tvDate.text = event.published
                tvEventType.text = event.type
                tvContent.text = event.content
                tvDateTime.text = event.datetime

                val like = if (event.likedByMe) R.drawable.ic_favorite_fill
                else R.drawable.ic_favorite_empty
                ivLike.setImageResource(like)
                tvLikeCount.text = event.likeCount.toString()

                event.attachment?.let { attachment ->
                    if (attachment.type == AttachmentType.IMAGE) {
                        ivAttachment.isVisible = true
                        ivAttachment.load(attachment.url) {
                            placeholder(R.drawable.placeholder_load)
                            error(R.drawable.error_placeholder)
                        }
                    }
                }

                tvInMap.isVisible = event.coords != null
                ivMore.isVisible = event.ownedByMe
            }
        }

        binding.threeStateView.observeStateView(viewModel.threeStateFlow, viewLifecycleOwner)

        viewModel.commands.observeWhenOnCreated(viewLifecycleOwner) { command ->
            when (command) {
                is EventDetailViewModel.EventDetailCommands.OpenUrl -> {
                    val gmmIntentUri = Uri.parse(command.url)
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                        startActivity(mapIntent)
                    } else {
                        viewModel.onOpenMapError()
                    }
                }

                is EventDetailViewModel.EventDetailCommands.ShowSnackbar -> {
                    Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        command.message,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                is EventDetailViewModel.EventDetailCommands.NavigateOnBackPressed -> {
                    setFragmentResult(
                        LIKE_EVENT_RESULT_REQ_KEY,
                        bundleOf(
                            LIKE_EVENT_RESULT_BUNDLE_KEY to command.likeResult
                        )
                    )
                    findNavController().navigateUp()
                }

                is EventDetailViewModel.EventDetailCommands.NavigateUpWhenEventDeleted -> {
                    setFragmentResult(
                        DELETE_EVENT_RESULT_REQ_KEY,
                        bundleOf(DELETE_EVENT_RESULT_BUNDLE_KEY to command.eventId)
                    )
                    findNavController().navigateUp()
                }

                is EventDetailViewModel.EventDetailCommands.NavigateToEditEvent -> {
                    val direction = EventDetailFragmentDirections
                        .actionEventDetailFragmentToEditEventFragment(
                            eventId = command.eventId,
                            content = command.content,
                            dateTime = command.dataTime,
                            type = command.type
                        )
                    findNavController().navigate(direction)
                }
            }
        }
    }

    private fun setupListeners() {
        binding.threeStateView.setErrorButtonClickListener {
            viewModel.onRetryClick()
        }

        binding.tvInMap.setOnClickListener {
            viewModel.onInMapClick()
        }

        binding.ivLike.setOnClickListener {
            viewModel.onLikeClick()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.onBackPressedClick()
        }

        binding.ivMore.setOnClickListener {
            showPopup()
        }

        setFragmentResultListener(EditEventFragment.SAVE_EVENT_REQ_KEY) { _, bundle ->
            val content = bundle.getString(EditEventFragment.SAVE_EVENT_CONTENT_KEY, "")
            viewModel.onEventUpdated(content)
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
    data class LikeEventResult(
        val eventId: Long,
        val isLike: Boolean,
        val likeCount: Int
    ) : Parcelable

    companion object {
        const val LIKE_EVENT_RESULT_REQ_KEY = "LIKE_EVENT_RESULT_REQ_KEY"
        const val LIKE_EVENT_RESULT_BUNDLE_KEY = "LIKE_EVENT_RESULT_BUNDLE_KEY"
        const val DELETE_EVENT_RESULT_REQ_KEY = "DELETE_EVENT_RESULT_REQ_KEY"
        const val DELETE_EVENT_RESULT_BUNDLE_KEY = "DELETE_EVENT_RESULT_BUNDLE_KEY"
    }
}
package ru.netology.nework.events.presentation.events

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.core.utils.observeWhenOnCreated
import ru.netology.nework.core.utils.observeWhenOnStarted
import ru.netology.nework.core.utils.viewBinding
import ru.netology.nework.create.presentation.edit.event.EditEventFragment
import ru.netology.nework.databinding.FragmentEventsBinding
import ru.netology.nework.events.presentation.events.adaprer.EventItem
import ru.netology.nework.events.presentation.events.adaprer.EventsAdapter

@AndroidEntryPoint
class EventsFragment : Fragment(R.layout.fragment_events) {

    private val binding by viewBinding<FragmentEventsBinding>()

    private val viewModel by viewModels<EventsViewModel>()

    private var adapter: EventsAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupObservers()
        setupListeners()
    }

    private fun setupView() {
        adapter = EventsAdapter(
            object : EventsAdapter.EventListener {
                override fun onLikeClick(eventId: Long, isLike: Boolean) {
                    viewModel.likeEvent(eventId, isLike)
                }

                override fun onEventClick(eventId: Long) {
                    val direction = EventsFragmentDirections.actionEventsFragmentToEventDetailFragment(eventId)
                    findNavController().navigate(direction)
                }

                override fun onDeleteClick(eventId: Long) {
                    viewModel.onDeletePostClick(eventId)
                }

                override fun onEditClick(event: EventItem) {
                    val direction = EventsFragmentDirections.actionEventsFragmentToEditEventFragment(
                        eventId = event.id,
                        content = event.content,
                        dateTime = event.datetime
                    )
                    findNavController().navigate(direction)
                }
            }
        )

        binding.rvEvents.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.eventsFlow.observeWhenOnCreated(viewLifecycleOwner) { events ->
            adapter?.submitList(events)
        }

        viewModel.commands.observeWhenOnCreated(viewLifecycleOwner) { command ->
            when (command) {
                is EventsViewModel.EventsCommand.ShowSnackbar -> {
                    Snackbar.make(
                        requireContext(),
                        binding.root,
                        command.message,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.threeStateView.observeStateView(viewModel.threeStateFlow, viewLifecycleOwner)

        viewModel.swipeRefreshingFlow.observeWhenOnStarted(viewLifecycleOwner) { isRefreshing ->
            binding.swipeRefresh.isRefreshing = isRefreshing
        }
    }

    private fun setupListeners() {
        binding.threeStateView.setErrorButtonClickListener(viewModel::onRetryClick)

        binding.swipeRefresh.setOnRefreshListener(viewModel::onSwipeRefresh)

        setFragmentResultListener(
            EditEventFragment.SAVE_EVENT_REQ_KEY
        ) { _, bundle ->
            val eventId = bundle.getLong(EditEventFragment.SAVE_EVENT_ID_KEY)
            val content = bundle.getString(EditEventFragment.SAVE_EVENT_CONTENT_KEY, "")
            viewModel.updateEvent(eventId, content)
        }
    }
}
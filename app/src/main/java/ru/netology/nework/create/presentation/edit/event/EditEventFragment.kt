package ru.netology.nework.create.presentation.edit.event

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.core.utils.extractUnixTimestampOrNull
import ru.netology.nework.core.utils.observeWhenOnCreated
import ru.netology.nework.core.utils.showKeyboard
import ru.netology.nework.core.utils.viewBinding
import ru.netology.nework.create.presentation.utils.showCalendarDialog
import ru.netology.nework.databinding.FragmentCreateEventBinding

@AndroidEntryPoint
class EditEventFragment : Fragment(R.layout.fragment_create_event) {

    private val binding by viewBinding<FragmentCreateEventBinding>()

    private val viewModel by viewModels<EditEventViewModel>()

    private val args by navArgs<EditEventFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupListener()
        setupObserver()
    }

    private fun setupView() {
        binding.etEvent.requestFocus()
        binding.etEvent.setText(args.content)
        binding.tvDate.text = args.dateTime
        binding.etEvent.showKeyboard()
    }

    private fun setupListener() {
        with(binding) {
            fabSave.setOnClickListener {
                viewModel.editEvent(args.eventId, etEvent.text.toString(), tvDate.text.toString())
            }

            tvDate.setOnClickListener {
                showCalendarDialog(
                    context = requireContext(),
                    data = tvDate.extractUnixTimestampOrNull(),
                    onDateChose = { date ->
                        tvDate.text = date
                    }
                )
            }
        }
    }

    private fun setupObserver() {
        binding.threeStateView.observeStateView(viewModel.treeStateFlow, viewLifecycleOwner)

        viewModel.commands.observeWhenOnCreated(viewLifecycleOwner) { command ->
            when (command) {
                is EditEventViewModel.EditEventCommands.ShowAlertDialog -> {
                    showAlertDialog(command.message)
                }
                is EditEventViewModel.EditEventCommands.NavigateToBack -> {
                    setFragmentResult(
                        requestKey = SAVE_EVENT_REQ_KEY,
                        bundleOf(
                            SAVE_EVENT_ID_KEY to args.eventId,
                            SAVE_EVENT_CONTENT_KEY to command.content
                        )
                    )
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun showAlertDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.error))
            .setMessage(message)
            .setPositiveButton(R.string.understand) { _, _ -> }
            .create()
            .show()
    }

    companion object {
        const val SAVE_EVENT_REQ_KEY = "SAVE_EVENT_REQ_KEY"
        const val SAVE_EVENT_ID_KEY = "SAVE_EVENT_ID_KEY"
        const val SAVE_EVENT_CONTENT_KEY = "SAVE_EVENT_CONTENT_KEY"
    }
}
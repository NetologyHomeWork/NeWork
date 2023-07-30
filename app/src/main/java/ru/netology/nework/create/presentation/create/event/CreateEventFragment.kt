package ru.netology.nework.create.presentation.create.event

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.core.utils.observeWhenOnCreated
import ru.netology.nework.core.utils.showKeyboard
import ru.netology.nework.core.utils.viewBinding
import ru.netology.nework.create.presentation.create.post.CreatePostFragmentDirections
import ru.netology.nework.create.presentation.success.SuccessFragment
import ru.netology.nework.create.presentation.utils.showCalendarDialog
import ru.netology.nework.databinding.FragmentCreateEventBinding

@AndroidEntryPoint
class CreateEventFragment : Fragment(R.layout.fragment_create_event) {

    private val binding by viewBinding<FragmentCreateEventBinding>()

    private val viewModel by viewModels<CreateEventViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupListener()
        setupObserver()
    }

    private fun setupView() {
        binding.etEvent.requestFocus()
        binding.etEvent.showKeyboard()
    }

    private fun setupListener() {
        with(binding) {
            fabSave.setOnClickListener {
                viewModel.createEvent(etEvent.text.toString(), tvDate.text.toString())
            }

            ibCalendar.setOnClickListener {
                showCalendarDialog(requireContext()) { date ->
                    tvDate.text = date
                }
            }
        }
    }

    private fun setupObserver() {
        binding.threeStateView.observeStateView(viewModel.treeStateFlow, viewLifecycleOwner)

        viewModel.commands.observeWhenOnCreated(viewLifecycleOwner) { command ->
            when (command) {
                is CreateEventViewModel.CreateEventCommands.ShowAlertDialog -> {
                    showAlertDialog(command.message)
                }
                CreateEventViewModel.CreateEventCommands.NavigateToSuccess -> {
                    val direction = CreatePostFragmentDirections
                        .actionCreatePostFragmentToSuccessFragment(
                            successType = SuccessFragment.TypeSuccess.EventSuccess
                        )
                    findNavController().navigate(direction)
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
}
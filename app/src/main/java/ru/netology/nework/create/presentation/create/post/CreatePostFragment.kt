package ru.netology.nework.create.presentation.create.post

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.core.utils.hideKeyboard
import ru.netology.nework.core.utils.observeWhenOnCreated
import ru.netology.nework.core.utils.showKeyboard
import ru.netology.nework.core.utils.viewBinding
import ru.netology.nework.create.presentation.success.SuccessFragment
import ru.netology.nework.databinding.FragmentCreatePostBinding

@AndroidEntryPoint
class CreatePostFragment : Fragment(R.layout.fragment_create_post) {

    private val binding by viewBinding<FragmentCreatePostBinding>()

    private val viewModel by viewModels<CreatePostViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupObserver()
        setupListeners()
    }

    private fun setupView() {
        binding.etPost.requestFocus()
        binding.etPost.showKeyboard()
    }

    private fun setupObserver() {
        binding.threeStateView.observeStateView(viewModel.treeStateFlow, viewLifecycleOwner)

        viewModel.commands.observeWhenOnCreated(viewLifecycleOwner) { command ->
            when (command) {
                is CreatePostViewModel.CreatePostCommands.ShowAlertDialog -> {
                    showAlertDialog(command.message)
                }
                CreatePostViewModel.CreatePostCommands.NavigateToSuccess -> {
                    val direction = CreatePostFragmentDirections
                        .actionCreatePostFragmentToSuccessFragment(
                            successType = SuccessFragment.TypeSuccess.PostSuccess
                        )
                    findNavController().navigate(direction)
                }
            }
        }
    }

    private fun setupListeners() {
        with(binding) {
            fabSave.setOnClickListener {
                viewModel.createPost(etPost.text.toString())
                it.hideKeyboard()
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
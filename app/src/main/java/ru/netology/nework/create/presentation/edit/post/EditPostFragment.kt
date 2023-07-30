package ru.netology.nework.create.presentation.edit.post

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
import ru.netology.nework.core.utils.hideKeyboard
import ru.netology.nework.core.utils.observeWhenOnCreated
import ru.netology.nework.core.utils.showKeyboard
import ru.netology.nework.core.utils.viewBinding
import ru.netology.nework.databinding.FragmentCreatePostBinding

@AndroidEntryPoint
class EditPostFragment : Fragment(R.layout.fragment_create_post) {

    private val binding by viewBinding<FragmentCreatePostBinding>()

    private val viewModel by viewModels<EditPostViewModel>()

    private val args by navArgs<EditPostFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupObserver()
        setupListeners()
    }

    private fun setupView() {
        binding.etPost.requestFocus()
        binding.etPost.showKeyboard()
        binding.etPost.setText(args.content)
    }

    private fun setupObserver() {
        binding.threeStateView.observeStateView(viewModel.treeStateFlow, viewLifecycleOwner)

        viewModel.commands.observeWhenOnCreated(viewLifecycleOwner) { command ->
            when (command) {
                is EditPostViewModel.EditPostCommands.ShowAlertDialog -> {
                    showAlertDialog(command.message)
                }
                is EditPostViewModel.EditPostCommands.NavigateToBack -> {
                    setFragmentResult(
                        requestKey = SAVE_POST_REQ_KEY,
                        bundleOf(
                            SAVE_POST_ID_KEY to args.postId,
                            SAVE_POST_CONTENT_KEY to command.content
                        )
                    )
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun setupListeners() {
        with(binding) {
            fabSave.setOnClickListener {
                viewModel.editPost(args.postId, etPost.text.toString())
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

    companion object {
        const val SAVE_POST_REQ_KEY = "SAVE_POST_REQ_KEY"
        const val SAVE_POST_ID_KEY = "SAVE_POST_ID_KEY"
        const val SAVE_POST_CONTENT_KEY = "SAVE_POST_CONTENT_KEY"
    }
}
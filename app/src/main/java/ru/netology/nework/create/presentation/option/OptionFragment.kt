package ru.netology.nework.create.presentation.option

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.core.utils.observeWhenOnStarted
import ru.netology.nework.core.utils.viewBinding
import ru.netology.nework.databinding.FragmentOptionBinding

@AndroidEntryPoint
class OptionFragment : Fragment(R.layout.fragment_option) {

    private val binding by viewBinding<FragmentOptionBinding>()

    private val viewModel by viewModels<OptionViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObserver()
        setupListener()
    }

    private fun setupObserver() {
        viewModel.optionScreenStateFlow.observeWhenOnStarted(viewLifecycleOwner) { state ->
            if (state == null) return@observeWhenOnStarted
            with(binding) {
                if (state.userName == null && state.isError) {
                    tvName.text = getString(R.string.no_user_data)
                    tvName.maxLines = 2
                } else {
                    ivAvatar.load(state.userAvatar) {
                        transformations(CircleCropTransformation())
                        placeholder(R.drawable.placeholder_load)
                        error(R.drawable.error_placeholder)
                    }

                    tvName.text = state.userName
                    tvJob.text = state.userJob
                }
            }
        }

        binding.threeStateView.observeStateView(viewModel.threeStateFlow, viewLifecycleOwner)
    }

    private fun setupListener() {
        binding.postContainer.setOnClickListener {
            findNavController().navigate(R.id.action_optionFragment_to_createPostFragment)
        }

        binding.eventContainer.setOnClickListener {
            findNavController().navigate(R.id.action_optionFragment_to_createEventFragment)
        }
    }
}
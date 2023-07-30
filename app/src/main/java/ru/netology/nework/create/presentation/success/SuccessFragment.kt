package ru.netology.nework.create.presentation.success

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import ru.netology.nework.R
import ru.netology.nework.core.utils.findMainContainerNavController
import ru.netology.nework.core.utils.viewBinding
import ru.netology.nework.databinding.FragmentSuccessBinding

class SuccessFragment : Fragment(R.layout.fragment_success) {

    private val binding by viewBinding<FragmentSuccessBinding>()

    private val args by navArgs<SuccessFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupListeners()
    }

    private fun setupView() {
        binding.tvLabel.text = getString(args.successType.message)
    }

    private fun setupListeners() {
        with(binding) {
            btnCreateAnother.setOnClickListener {
                findNavController().navigate(R.id.action_successFragment_to_createPostFragment)
            }

            btnToPostList.setOnClickListener {
                findMainContainerNavController().navigate(R.id.nav_main, null, navOptions {
                    popUpTo(R.id.nav_main) { inclusive = true }
                })
            }
        }
    }

    enum class TypeSuccess(@StringRes val message: Int) {
        PostSuccess(R.string.post_success_saving),
        EventSuccess(R.string.event_success_saving)
    }
}
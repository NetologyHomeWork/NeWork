package ru.netology.nework.profile.presentation.profile

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.core.utils.observeWhenOnCreated
import ru.netology.nework.core.utils.viewBinding
import ru.netology.nework.databinding.FragmentProfileBinding
import ru.netology.nework.profile.presentation.add_job.AddJobFragment
import ru.netology.nework.profile.presentation.profile.adapter.ProfileAdapter
import ru.netology.nework.profile.presentation.profile.adapter.ProfileItems

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val binding by viewBinding<FragmentProfileBinding>()

    private val viewModel by viewModels<ProfileViewModel>()

    private val adapter: ProfileAdapter = initAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvJobs.adapter = adapter

        setupObserver()
        setupListeners()
    }

    private fun setupObserver() {
        viewModel.commands.observeWhenOnCreated(viewLifecycleOwner) { command ->
            when (command) {
                is ProfileViewModel.ProfileCommands.ShowSnackbar -> {
                    Snackbar.make(
                        requireContext(),
                        binding.root,
                        command.message,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }

        viewModel.screenStateFlow.observeWhenOnCreated(viewLifecycleOwner) { state ->
            if (state == null) return@observeWhenOnCreated
            with(binding) {
                ivAvatar.load(state.avatar) {
                    placeholder(R.drawable.placeholder_load)
                    error(R.drawable.ic_profile_placeholder)
                }
                tvName.text = state.name
                tvJobTitle.text = state.currentJob
                adapter.submitList(state.jobList)
            }
        }

        binding.threeStateView.observeStateView(
            flow = viewModel.threeStateFlow,
            owner = viewLifecycleOwner
        )
    }

    private fun setupListeners() {
        binding.ivLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setMessage(getString(R.string.logout_confirm))
                .setPositiveButton(R.string.confirm) { _, _ ->
                    viewModel.onLogoutClick()
                }
                .setNegativeButton(R.string.cancel) { _, _ ->

                }
                .create()
                .show()
        }

        binding.threeStateView.setErrorButtonClickListener {
            viewModel.onRetryClick()
        }

        setFragmentResultListener(AddJobFragment.REQ_KEY_ADD_JOB) { _, bundle ->
            val jobItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable(
                    AddJobFragment.BUNDLE_KEY_ADD_JOB,
                    ProfileItems.JobItem::class.java
                )
            } else {
                @Suppress("DEPRECATION")
                bundle.getParcelable(AddJobFragment.BUNDLE_KEY_ADD_JOB)
            }
            jobItem?.let {
                viewModel.onJobListUpdated(it)
            }
        }
    }

    private fun initAdapter(): ProfileAdapter {
        return ProfileAdapter(
            onEditClick = { jobItem ->
                val direction = ProfileFragmentDirections.actionProfileFragmentToAddJobFragment(jobItem)
                findNavController().navigate(direction)
            },
            onDeleteClick = { jobId ->
                AlertDialog.Builder(requireContext())
                    .setMessage(getString(R.string.delete_job_confirm))
                    .setPositiveButton(R.string.confirm) { _, _ ->
                        viewModel.onDeleteClick(jobId)
                    }
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .create()
                    .show()

            },
            onItemClick = {
                findNavController().navigate(R.id.action_profileFragment_to_addJobFragment)
            }
        )
    }
}
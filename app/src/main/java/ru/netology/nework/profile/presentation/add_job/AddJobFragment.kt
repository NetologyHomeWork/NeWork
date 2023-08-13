package ru.netology.nework.profile.presentation.add_job

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.core.utils.extractUnixTimestampOrNull
import ru.netology.nework.core.utils.hideKeyboard
import ru.netology.nework.core.utils.observeWhenOnCreated
import ru.netology.nework.core.utils.viewBinding
import ru.netology.nework.create.presentation.utils.showCalendarDialog
import ru.netology.nework.databinding.FragmentAddJobBinding
import ru.netology.nework.profile.domain.entity.AddJobField

@AndroidEntryPoint
class AddJobFragment : Fragment(R.layout.fragment_add_job) {

    private val binding by viewBinding<FragmentAddJobBinding>()

    private val viewModel by viewModels<AddJobViewModel>()

    private val args by navArgs<AddJobFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupListeners()
        setupObservers()
    }

    private fun setupView() {
        args.jobItem?.let { jobItem ->
            with(binding) {
                etName.setText(jobItem.company)
                etPosition.setText(jobItem.position)
                etStartDate.setText(jobItem.startJob)
                etFinishDate.setText(jobItem.finishJob)
                cbIsCurrentWork.isChecked = jobItem.finishJob == null
                tilFinishDate.isEnabled = jobItem.finishJob != null
            }
        }
    }

    private fun setupListeners() {
        binding.cbIsCurrentWork.setOnCheckedChangeListener { _, isChecked ->
            binding.tilFinishDate.isEnabled = isChecked.not()
            binding.etFinishDate.text?.clear()
        }

        binding.etStartDate.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                showCalendarDialog(
                    context = requireContext(),
                    data = binding.etStartDate.extractUnixTimestampOrNull(),
                    onDateChose = { date ->
                        binding.etStartDate.setText(date)
                        view.clearFocus()
                    },
                    onDismiss = {
                        view.clearFocus()
                    }
                )
            }
        }

        binding.etFinishDate.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                showCalendarDialog(
                    context = requireContext(),
                    data = binding.etFinishDate.extractUnixTimestampOrNull(),
                    onDateChose = { date ->
                        binding.etFinishDate.setText(date)
                        view.clearFocus()
                    },
                    onDismiss = {
                        view.clearFocus()
                    }
                )
            }
        }

        binding.btnSave.setOnClickListener {
            val id = args.jobItem?.id ?: 0L
            viewModel.onSaveClick(
                id = id,
                name = binding.etName.text.toString(),
                position = binding.etPosition.text.toString(),
                start = binding.etStartDate.text.toString(),
                finish = binding.etFinishDate.text.toString(),
                isCurrentJob = binding.cbIsCurrentWork.isChecked
            )
            it.hideKeyboard()
        }

        binding.etName.doAfterTextChanged { clearError(binding.tilName) }
        binding.etPosition.doAfterTextChanged { clearError(binding.tilPosition) }
        binding.etStartDate.doAfterTextChanged { clearError(binding.tilStartDate) }
    }

    private fun setupObservers() {
        binding.threeStateView.observeStateView(
            flow = viewModel.threeStateFlow,
            owner = viewLifecycleOwner
        )

        viewModel.commands.observeWhenOnCreated(viewLifecycleOwner) { command ->
            when (command) {
                is AddJobViewModel.AddJobCommands.ShowAlertDialog -> {
                    showAlertDialog(command.message)
                }
                is AddJobViewModel.AddJobCommands.ShowInputError -> {
                    val textInputLayout = getTextInputLayoutByField(command.field)
                    textInputLayout.isErrorEnabled = true
                    textInputLayout.error = getString(R.string.error_input)
                }
                is AddJobViewModel.AddJobCommands.NavigateToProfile -> {
                    setFragmentResult(
                        requestKey = REQ_KEY_ADD_JOB,
                        result = bundleOf(
                            BUNDLE_KEY_ADD_JOB to command.jobItem
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

    private fun getTextInputLayoutByField(field: AddJobField): TextInputLayout {
        return when (field) {
            AddJobField.COMPANY -> binding.tilName
            AddJobField.POSITION -> binding.tilPosition
            AddJobField.START_DATE -> binding.tilStartDate
        }
    }

    private fun clearError(textInputLayout: TextInputLayout) {
        if (textInputLayout.isErrorEnabled) {
            textInputLayout.isErrorEnabled = false
            textInputLayout.error = null
        }
    }

    companion object {
        const val REQ_KEY_ADD_JOB = "REQ_KEY_ADD_JOB"
        const val BUNDLE_KEY_ADD_JOB = "BUNDLE_KEY_ADD_JOB"
    }
}

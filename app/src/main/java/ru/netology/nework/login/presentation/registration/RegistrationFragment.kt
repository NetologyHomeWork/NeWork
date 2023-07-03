package ru.netology.nework.login.presentation.registration

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.core.utils.hideKeyboard
import ru.netology.nework.core.utils.observeWhenOnCreated
import ru.netology.nework.core.utils.observeWhenOnStarted
import ru.netology.nework.core.utils.viewBinding
import ru.netology.nework.databinding.BottomDialogAddImageBinding
import ru.netology.nework.databinding.FragmentRegistrationBinding
import ru.netology.nework.login.domain.entities.AuthField

@AndroidEntryPoint
class RegistrationFragment : Fragment(R.layout.fragment_registration) {

    private val binding by viewBinding<FragmentRegistrationBinding>()

    private val viewModel by viewModels<RegistrationViewModel>()

    private val photoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        when (it.resultCode) {
            ImagePicker.RESULT_ERROR -> {
               showAlertDialog(message = getString(R.string.error_load_image))
            }
            else -> {
                val uri = it.data?.data ?: return@registerForActivityResult
                viewModel.onPhotoUploaded(uri, uri.toFile())
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListener()
        setupObserver()
    }

    private fun setupListener() {
        with (binding) {
            btnSignUp.setOnClickListener {
                viewModel.onRegistrationClick(
                    login = etLogin.text.toString(),
                    name = etName.text.toString(),
                    password = etPassword.text.toString(),
                    repeatPassword = etRepeatPassword.text.toString()
                )
                it.hideKeyboard()
                tilLogin.clearFocus()
                tilName.clearFocus()
                tilPassword.clearFocus()
                tilRepeatPassword.clearFocus()
            }

            ivDeletePhoto.setOnClickListener {
                viewModel.onPhotoRemoved()
            }

            ivAvatar.setOnClickListener {
                showBottomDialog()
            }

            etLogin.doAfterTextChanged { clearError(tilLogin) }
            etName.doAfterTextChanged { clearError(tilName) }
            etPassword.doAfterTextChanged { clearError(tilPassword) }
            etRepeatPassword.doAfterTextChanged { clearError(tilRepeatPassword) }
        }
    }

    private fun setupObserver() {
        binding.threeStateView.observeStateView(viewModel.threeStateFlow, viewLifecycleOwner)

        viewModel.commands.observeWhenOnCreated(viewLifecycleOwner) { commands ->
            when (commands) {
                is RegistrationViewModel.RegistrationCommands.ShowAlertDialog -> {
                    showAlertDialog(
                        message = commands.message,
                        title = commands.title
                    )
                }
                is RegistrationViewModel.RegistrationCommands.ShowInputError -> {
                    val textInputLayout = getTextInputLayoutByField(commands.field)
                    textInputLayout.isErrorEnabled = true
                    textInputLayout.error = getString(R.string.error_input)
                }
                RegistrationViewModel.RegistrationCommands.NavigateToMainScreen -> {
                    findNavController().navigate(R.id.nav_main, null, navOptions {
                        popUpTo(R.id.nav_auth) { inclusive = true }
                    })
                }
            }
        }

        viewModel.photoFlow.observeWhenOnStarted(viewLifecycleOwner) { photo ->
            if (photo != null) {
                binding.ivDeletePhoto.isVisible = true
                binding.ivAvatar.setImageURI(photo.uri)
            } else {
                binding.ivDeletePhoto.isVisible = false
                binding.ivAvatar.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_add_avatar)
                )
            }
        }
    }

    private fun showAlertDialog(message: String, title: String? = null) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.understand) { _, _ ->

            }
            .create()
            .show()
    }

    private fun clearError(textInputLayout: TextInputLayout) {
        if (textInputLayout.isErrorEnabled) {
            textInputLayout.isErrorEnabled = false
            textInputLayout.error = null
        }
    }

    private fun getTextInputLayoutByField(field: AuthField): TextInputLayout {
        return when (field) {
            AuthField.LOGIN -> binding.tilLogin
            AuthField.NAME -> binding.tilName
            AuthField.PASSWORD -> binding.tilPassword
            AuthField.REPEAT_PASSWORD -> binding.tilRepeatPassword
        }
    }

    private fun showBottomDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())

        bottomSheetDialog.apply {
            val dialogBinding = BottomDialogAddImageBinding.inflate(layoutInflater)
            setContentView(dialogBinding.root)

            dialogBinding.groupPhoto.setOnClickListener {
                ImagePicker.with(this@RegistrationFragment)
                    .crop()
                    .compress(IMAGE_MAX_SIZE)
                    .provider(ImageProvider.CAMERA)
                    .createIntent(photoLauncher::launch)
                dismiss()
            }

            dialogBinding.groupGallery.setOnClickListener {
                ImagePicker.with(this@RegistrationFragment)
                    .crop()
                    .compress(IMAGE_MAX_SIZE)
                    .provider(ImageProvider.GALLERY)
                    .galleryMimeTypes(
                        arrayOf(IMAGE_PNG_TYPE, IMAGE_JPEG_TYPE)
                    )
                    .createIntent(photoLauncher::launch)
                dismiss()
            }
        }.show()
    }

    private companion object {
        private const val IMAGE_MAX_SIZE = 200
        private const val IMAGE_PNG_TYPE = "image/png"
        private const val IMAGE_JPEG_TYPE = "image/jpeg"
    }
}
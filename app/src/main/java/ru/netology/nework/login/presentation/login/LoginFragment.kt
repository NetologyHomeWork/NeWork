package ru.netology.nework.login.presentation.login

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.core.utils.hideKeyboard
import ru.netology.nework.core.utils.observeWhenOnCreated
import ru.netology.nework.core.utils.viewBinding
import ru.netology.nework.databinding.FragmentLoginBinding
import ru.netology.nework.login.domain.entities.AuthField

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val binding by viewBinding<FragmentLoginBinding>()

    private val viewModel by viewModels<LoginViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListener()
        setupObserver()
    }

    private fun setupListener() {
        with(binding) {
            tvSignUp.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
            }

            btnSignIn.setOnClickListener {
                viewModel.onLoginClick(
                    login = etLogin.text.toString(),
                    password = etPassword.text.toString()
                )
                it.hideKeyboard()
                binding.tilLogin.clearFocus()
                binding.tilPassword.clearFocus()
            }

            etLogin.doAfterTextChanged {
                if (tilLogin.isErrorEnabled) {
                    tilLogin.isErrorEnabled = false
                    tilLogin.error = null
                }
            }

            etPassword.doAfterTextChanged {
                if (tilPassword.isErrorEnabled) {
                    tilPassword.isErrorEnabled = false
                    tilPassword.error = null
                }
            }
        }
    }

    private fun setupObserver() {
        binding.threeStateView.observeStateView(viewModel.threeStateFlow, viewLifecycleOwner)

        viewModel.commands.observeWhenOnCreated(viewLifecycleOwner) { command ->
            when (command) {
                is LoginViewModel.LoginCommands.ShowAlertDialog -> {
                    showAlertDialog(command.message, command.title)
                }

                is LoginViewModel.LoginCommands.ShowInputError -> {
                    when (command.field) {
                        AuthField.LOGIN -> {
                            binding.tilLogin.isErrorEnabled = true
                            binding.tilLogin.error = getString(R.string.error_input)
                        }
                        AuthField.PASSWORD -> {
                            binding.tilPassword.isErrorEnabled = true
                            binding.tilPassword.error = getString(R.string.error_input)
                        }
                        else -> { /* no-op */ }
                    }
                }

                LoginViewModel.LoginCommands.NavigateToMainScreen -> {
                    findNavController().navigate(R.id.nav_main, null, navOptions {
                        popUpTo(R.id.nav_auth) { inclusive = true }
                    })
                }
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
}
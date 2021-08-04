package com.killua.ideenplattform.authorization.ui.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.killua.ideenplattform.authorization.AuthenticationActivity
import com.killua.ideenplattform.databinding.FragmentRegisterBinding
import com.killua.ideenplattform.ideamain.editprofile.BaseFragment
import com.killua.ideenplattform.ideamain.safeNavigate
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment : BaseFragment() {

    private val registerViewModel by viewModel<RegisterViewModel>()

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                registerViewModel.setIntent(RegisterAction.InputHasChanged)
            }
        }
        with(binding) {
            etFirstName.editText?.addTextChangedListener(afterTextChangedListener)
            etLastName.editText?.addTextChangedListener(afterTextChangedListener)
            etEmail.editText?.addTextChangedListener(afterTextChangedListener)
            etPassword.editText?.addTextChangedListener(afterTextChangedListener)
            etPasswordConfirm.editText?.addTextChangedListener(afterTextChangedListener)
        }
        lifecycleScope.launchWhenCreated {
            with(registerViewModel) {
                getStateDataBinding.collect {
                    binding.stateDb = it
                    binding.executePendingBindings()
                }
            }
        }
        lifecycleScope.launchWhenCreated {
            setupUIState(registerViewModel)
        }

        lifecycleScope.launchWhenResumed {
            registerViewModel.getViewEffects.collect {
                when (it) {
                    RegisterEffect.NavigateToLogin -> return@collect
                    is RegisterEffect.MakeToast -> navigateToLogIn()
                }
            }

        }

        (requireActivity() as AuthenticationActivity).supportActionBar?.show()
        (requireActivity() as AuthenticationActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun navigateToLogIn() {
        val action=RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
        findNavController().safeNavigate(action)
    }

    private suspend fun setupUIState(viewModel: RegisterViewModel) {
        viewModel.getState.collect { state ->
            with(binding) {
                progressBar.visibility = if (state.loadingProgressBar) View.VISIBLE else View.GONE
                btnRegister.isEnabled = state.isDataValid
                state.firstNameError.let { errorMessage ->
                    if (errorMessage != null)
                        etFirstName.error = getString(errorMessage)
                    etFirstName.isErrorEnabled =  errorMessage!=null
                }
                state.lastNameError.let { errorMessage ->
                    if (errorMessage != null)
                        etLastName.error = getString(errorMessage)
                     etLastName.isErrorEnabled = errorMessage!=null
                }

                state.emailError.let { errorMessage ->
                    if (errorMessage != null)
                        etEmail.error = getString(errorMessage)
                     etEmail.isErrorEnabled =  errorMessage!=null
                }
                state.passwordError.let { errorMessage ->
                    if (errorMessage != null)
                        etPassword.error = getString(errorMessage)
                     etPassword.isErrorEnabled =  errorMessage!=null
                }
                state.passwordConfirmError.let { errorMessage ->
                    if (errorMessage != null)
                        etPasswordConfirm.error = getString(errorMessage)
                     etPasswordConfirm.isErrorEnabled =  errorMessage!=null
                }

            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
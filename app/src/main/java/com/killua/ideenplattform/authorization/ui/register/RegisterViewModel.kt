package com.killua.ideenplattform.authorization.ui.register

import android.util.Patterns
import com.killua.ideenplattform.R
import com.killua.ideenplattform.data.repository.MainRepository
import com.killua.ideenplattform.data.requests.UserCreateReq
import com.killua.ideenplattform.ideamain.uiutils.BaseViewModel
import kotlinx.coroutines.flow.collect

class RegisterViewModel(private val repo: MainRepository) :
    BaseViewModel<RegisterUiStateDb, RegisterEffect, RegisterUiState>(RegisterUiState(),
        RegisterUiStateDb()) {

    fun setIntent(action: RegisterAction) {
        when (action) {
            is RegisterAction.InputHasChanged -> registerDataChanged()
            RegisterAction.RegisterClicked -> registerAccount()
        }
    }


    private fun registerAccount() {
        reducer {
            copy(loadingProgressBar = true)
        }
        launchCoroutine {
            var currentStateDb: RegisterUiStateDb? = null
            reducerDB {
                currentStateDb = this
                copy()
            }
            currentStateDb?.let { stateDb ->
                repo.createUser(UserCreateReq(stateDb.email,
                    stateDb.firstName,
                    stateDb.lastName,
                    stateDb.password)).collect { repoResult ->
                    if (repoResult.networkErrorMessage == "Email already registered") {
                        reducer {
                            RegisterUiState(emailError = R.string.email_already_registerd,
                                isDataValid = false,
                                loadingProgressBar = false)
                        }
                        return@collect
                    }
                    if(repoResult.data==true) {
                        postEffect(RegisterEffect.NavigateToLogin).also {
                            postEffect(RegisterEffect.MakeToast(R.string.created))
                        }

                    }
                }
            }
        }

    }

    private fun registerDataChanged() {
        reducerDB {
            if (!isNameValid(firstName)) {
                reducer {
                    RegisterUiState(firstNameError = R.string.firstname_error)
                }
            } else
                if (!isNameValid(lastName)) {
                    reducer {
                        RegisterUiState(lastNameError = R.string.lastname_error)
                    }
                } else

                    if (!isEmailValid(email)) {
                        reducer {
                            RegisterUiState(emailError = R.string.email_error)
                        }
                    } else
                        if (!isPasswordValid(password)) {
                            reducer {
                                RegisterUiState(passwordError = R.string.password_error)
                            }
                        } else if (!isPasswordConfirmValid(password, passwordConfirm)) {
                            reducer {
                                RegisterUiState(passwordConfirmError = R.string.passowrd_confirm_error)
                            }
                        } else
                            reducer {
                                RegisterUiState(isDataValid = true)
                            }
            copy()
        }
    }

    private fun isNameValid(name: String): Boolean {
        return name.matches(regex = Regex("[a-zA-z]+([ '-][a-zA-Z]+)*"))

    }

    private fun isEmailValid(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    private fun isPasswordConfirmValid(password: String, passwordConfirm: String): Boolean {
        return password == passwordConfirm
    }
}
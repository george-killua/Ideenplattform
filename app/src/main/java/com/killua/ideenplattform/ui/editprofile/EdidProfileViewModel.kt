package com.killua.ideenplattform.ui.editprofile

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.killua.ideenplattform.data.models.local.UserCaching
import com.killua.ideenplattform.data.repository.MainRepository
import com.killua.ideenplattform.data.requests.UserCreateReq
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking

class EdidProfileViewModel(private val mainRepository: MainRepository) : BaseObservable() {
    private var currentUser = UserCaching()
    private val stateLivaDate = MutableLiveData<State>()


    @get:Bindable
    var imageUri = ""

    @get:Bindable
    var firstName = ""

    @get:Bindable
    var lastname = ""

    @get:Bindable
    var password = ""

    @get:Bindable
    var passwordConfirm = ""

    data class State(
        val toastMessage: String? = null,
        val firstNameError: Boolean = false,
        var imageUriError: Boolean = false,
        var lastNameError: Boolean = false,
        var passwordError: Boolean = false,
        var passwordConfirmError: Boolean = false,
        var navigateToProfile: Boolean = false
    )

    sealed class Action {
        object SetupFragment : Action()
        object FirstNameError : Action()
        object ImageUriError : Action()
        object LastnameError : Action()
        object PasswordError : Action()
        object PasswordConfirmError : Action()
        object NavigateToProfile : Action()

    }

    fun onAction(action: Action) {
        when (action) {
            Action.SetupFragment -> {
                runBlocking {
                    mainRepository.getMe().collect { repoResult ->
                        repoResult.data?.let {
                            currentUser=it
                            firstName = it.firstname
                            lastname = it.lastname
                            imageUri = it.profilePicture
                        }

                    }
                }
            }
            Action.FirstNameError -> {
                stateLivaDate.postValue(
                    State(
                        toastMessage = "firstname error",
                        firstNameError = true
                    )
                )
            }
            Action.ImageUriError -> {
                stateLivaDate.postValue(State(toastMessage = "image error", imageUriError = true))
            }
            Action.LastnameError -> {
                stateLivaDate.postValue(
                    State(
                        toastMessage = "lastname error",
                        lastNameError = true
                    )
                )
            }
            Action.NavigateToProfile -> {
                stateLivaDate.postValue(
                    State(
                        toastMessage = "update saved",
                        navigateToProfile = true
                    )
                )
            }
            Action.PasswordConfirmError -> {
                stateLivaDate.postValue(
                    State(
                        toastMessage = "password confirmation error",
                        passwordConfirmError = true
                    )
                )
            }
            Action.PasswordError -> {
                stateLivaDate.postValue(
                    State(
                        toastMessage = "password error",
                        passwordError = true
                    )
                )

            }

        }
    }

    private fun inputValidator(): Boolean {
        when {
            firstName.isBlank() -> {
                onAction(Action.FirstNameError)
                return false
            }
            lastname.isBlank() -> {
                onAction(Action.LastnameError)
                return false
            }
            password.isBlank() -> {
                onAction(Action.PasswordError)
                return false
            }
            passwordConfirm.isBlank() -> {
                onAction(Action.PasswordConfirmError)
                return false
            }
            passwordConfirm != password -> {
                onAction(Action.PasswordConfirmError)
                return false
            }
            imageUri.isBlank() -> {
                onAction(Action.ImageUriError)
                return false
            }
            else -> return true
        }
    }

    fun saveClick() {
        if (!inputValidator()) return
        runBlocking {
            var firstStep=false

            mainRepository.updateUser(
                UserCreateReq(
                    email = currentUser.email,
                    firstname = firstName,
                    lastname = lastname,
                    password = password
                )
            ).collect {
                if (it.data == true)

                else
                    stateLivaDate.postValue(
                        State(
                            toastMessage = it.networkErrorMessage
                        )
                    )

            }
            mainRepository.uploadUserImage(imageUri).collect {

            }
        }

    }
}
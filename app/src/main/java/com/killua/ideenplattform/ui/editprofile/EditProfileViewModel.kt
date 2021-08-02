package com.killua.ideenplattform.ui.editprofile

import androidx.lifecycle.MutableLiveData
import com.killua.ideenplattform.data.models.local.UserCaching
import com.killua.ideenplattform.data.repository.MainRepository
import com.killua.ideenplattform.data.requests.UserCreateReq
import com.killua.ideenplattform.ui.editprofile.EditProfileViewModel.*
import com.killua.ideenplattform.ui.uiutils.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import java.io.File

class EditProfileViewModel(private val mainRepository: MainRepository) :
    BaseViewModel<EditProfileStateDB, Effect, State>(State(),EditProfileStateDB()) {
    private var currentUser = UserCaching()
    private val stateLivaDate = MutableLiveData<State>()

    data class EditProfileStateDB(
        var imageUri: String = "",
        var firstName: String = "",
        var lastname: String = "",
        var password: String = "",
        var passwordConfirm: String = "",
    )

    sealed class Effect {
        object NavigateToProfile : Effect()
        data class MakeToast(val toastMessage: String) : Effect()
    }

    data class State(
        val loading: Boolean = false,
        val firstNameError: Pair<Boolean, String?> = Pair(false, null),
        var imageUriError: Pair<Boolean, String?> = Pair(false, null),
        var lastNameError: Pair<Boolean, String?> = Pair(false, null),
        var passwordError: Pair<Boolean, String?> = Pair(false, null),
        var passwordConfirmError: Pair<Boolean, String?> = Pair(false, null),
    )

    sealed class Action {
        class LoadImageFromGallery(file: File) : Action()
        object FirstNameError : Action()
        object ImageUriError : Action()
        object LastnameError : Action()
        object PasswordError : Action()
        object PasswordConfirmError : Action()
        object SaveChangesClicked : Action()
        object RemoveImage : Action()
        object InsertImage:Action()
    }

    init {
        reducer { copy(loading = true) }
        launchCoroutine {
            mainRepository.getMe().collect { repoResult ->
                repoResult.data?.let { user ->
                    reducerDB {  copy(imageUri = user.profilePicture,
                        firstName = user.firstname,
                        lastname = user.lastname)}
                    currentUser = user

                }

            }
        }
    }

    fun setIntent(action: Action) {
        when (action) {
            Action.FirstNameError ->
                reducer { copy(firstNameError = Pair(true, "firstname error")) }
            Action.ImageUriError ->
                reducer { copy(imageUriError = Pair(true, "image error")) }
            Action.LastnameError ->
                reducer { copy(lastNameError = Pair(true, "lastname error")) }
            Action.SaveChangesClicked -> saveClick()
            Action.PasswordConfirmError ->
                reducer { copy(passwordConfirmError = Pair(true, "lastname error")) }
            Action.PasswordError ->
                reducer { copy(passwordError = Pair(true, "lastname error")) }
            Action.InsertImage -> TODO()
            Action.RemoveImage -> removeImage()
        }
    }

    private fun removeImage() {
        launchCoroutine {

        }
    }

    private fun inputValidator(): Boolean {
        var isDone = false
        launchCoroutine {
            getStateDataBinding().run { val it =this
                when {
                    it.firstName.isBlank() -> {
                        setIntent(Action.FirstNameError)
                        isDone = false
                    }
                    it.lastname.isBlank() -> {
                        setIntent(Action.LastnameError)
                        isDone = false
                    }
                    it.password.isBlank() -> {
                        setIntent(Action.PasswordError)
                        isDone = false
                    }
                    it.passwordConfirm.isBlank() -> {
                        setIntent(Action.PasswordConfirmError)
                        isDone = false
                    }
                    it.passwordConfirm != it.password -> {
                        setIntent(Action.PasswordConfirmError)
                        isDone = false

                    }
                    else -> isDone = true
                }
            }

        }
        return isDone
    }

    private fun saveClick() {
        if (!inputValidator()) return
        launchCoroutine {
            val stateDB = getStateDataBinding()

            reducer {
                copy(loading = true)
            }
            mainRepository.updateUser(
                UserCreateReq(
                    email = currentUser.email,
                    firstname = stateDB.firstName,
                    lastname = stateDB.lastname,
                    password = stateDB.password)
            ).collect {
                if (!it.isNetworkingData)
                    postEffect(Effect.MakeToast(toastMessage = it.networkErrorMessage!!))
            }
            mainRepository.uploadUserImage(File(stateDB.imageUri)).collect {
                if (!it.isNetworkingData)
                    postEffect(Effect.MakeToast(toastMessage = it.networkErrorMessage!!))
            }
        }
        reducer { copy(loading = false) }
        postEffect(Effect.NavigateToProfile)
    }
}
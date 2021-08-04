package com.killua.ideenplattform.ideamain.editprofile

import android.content.Context
import android.net.Uri
import androidx.databinding.BaseObservable
import com.killua.ideenplattform.R
import com.killua.ideenplattform.data.models.local.UserCaching
import com.killua.ideenplattform.data.repository.MainRepository
import com.killua.ideenplattform.data.requests.UserCreateReq
import com.killua.ideenplattform.ideamain.editprofile.EditProfileViewModel.*
import com.killua.ideenplattform.ideamain.uiutils.BaseViewModel
import kotlinx.coroutines.flow.collect
import java.io.File
import java.lang.ref.WeakReference

class EditProfileViewModel(
    private val mainRepository: MainRepository,
    private val context: WeakReference<Context>,
) :
    BaseViewModel<EditProfileStateDB, Effect, State>(State(), EditProfileStateDB()) {
    private var currentUser = UserCaching()

    data class EditProfileStateDB(
        var imageUri: String? = null,
        var firstName: String = "",
        var lastname: String = "",
        var password: String = "",
        var passwordConfirm: String = "",
        var isLoading: Boolean = false,
    ) : BaseObservable()

    sealed class Effect {
        object NavigateToProfile : Effect()
        data class MakeToast(val toastMessage: String) : Effect()
        object ChooseImage : Effect()
    }

    data class State(
        val firstNameError: Boolean = false,
        var lastNameError: Boolean = false,
        var passwordError: Boolean = false,
        var passwordConfirmError: Boolean = false
    )

    sealed class Action {
        object FirstNameError : Action()
        object LastnameError : Action()
        object PasswordError : Action()
        object PasswordConfirmError : Action()
        object SaveChangesClicked : Action()
        object ChooseImageClicked : Action()
        object RemoveImage : Action()
        data class InsertImage(val pathUri: String) : Action()
    }

    init {
        reducerDB { copy(isLoading = true) }
        launchCoroutine {
            mainRepository.getMe().collect { repoResult ->
                repoResult.data?.let { user ->
                    reducerDB {
                        copy(imageUri = user.profilePicture,
                            firstName = user.firstname,
                            lastname = user.lastname)
                    }
                    currentUser = user

                }

            }
        }
        reducerDB { copy(isLoading = false) }

    }

    fun setIntent(action: Action) {
        when (action) {
            Action.FirstNameError ->
                reducer { copy(firstNameError = true) }
            Action.LastnameError ->
                reducer { copy(lastNameError = true) }
            Action.SaveChangesClicked -> saveClick()
            Action.PasswordConfirmError ->
                reducer { copy(passwordConfirmError = true) }
            Action.PasswordError ->
                reducer { copy(passwordError = true) }
            Action.RemoveImage -> removeImage()
            is Action.InsertImage -> insertImage(action.pathUri)
            Action.ChooseImageClicked -> postEffect(Effect.ChooseImage)
        }
    }

    private fun insertImage(pathUri: String) {
        reducerDB { copy(imageUri = pathUri) }
        launchCoroutine {
            val uri = Uri.parse(pathUri)
            mainRepository.uploadUserImage(File(uri.path ?: "")).collect { repoResult ->
                if (repoResult.isNetworkingData) {
                    context.get()?.let {
                        postEffect(Effect.MakeToast(it.getString(R.string.updated)))
                    }
                } else
                    context.get()?.let {
                        postEffect(Effect.MakeToast(it.getString(R.string.internet_error)))
                    }
            }
        }
    }

    private fun removeImage() {
        launchCoroutine {
            mainRepository.deleteImageOfUser().collect { repoResult ->
                if (repoResult.isNetworkingData)
                    context.get()?.let {
                        postEffect(Effect.MakeToast(it.getString(R.string.deleted)))
                    }
                else    context.get()?.let {
                    postEffect(Effect.MakeToast(it.getString(R.string.internet_error)))
                }
            }

            reducerDB { copy(imageUri = "") }
        }
    }

    private fun inputValidator(): Boolean {
        var isDone = false
        launchCoroutine {
            getStateDataBinding().run {
                val it = this
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

            reducerDB {
                copy(isLoading = true)
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
            stateDB.imageUri?.let { path ->
                mainRepository.uploadUserImage(File(path))
                    .collect { (_, isNetworkingData, networkErrorMessage) ->
                        if (!isNetworkingData)
                            postEffect(Effect.MakeToast(toastMessage = networkErrorMessage!!))
                    }
            }
        }
        reducerDB { copy(isLoading = false) }
        postEffect(Effect.NavigateToProfile)
    }
}
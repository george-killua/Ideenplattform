package com.killua.ideenplattform.ui.login

import android.content.Context
import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.killua.ideenplattform.data.repository.MainRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking

class LoginViewModel(val context: Context, private val userRepository: MainRepository) :
    BaseObservable() {

  val stateLiveData: MutableLiveData<LoginViewModel.State> = MutableLiveData<LoginViewModel.State>()

    @get: Bindable
    var userName: String = ""

    @get: Bindable
    var password: String = ""

    fun loginAc(){
        onAction(Action.LoginClicked)
    }
    fun registerAc(){
        onAction(Action.RegisterClicked)
    }


    data class State(
        var userName: String = "",
        var password: String = "",
        val toastMessage: String? = null,
        var userNameIsEmpty: Boolean = false,
        var passwordIsEmpty: Boolean = false
    )

    init {
        runBlocking {
            userRepository.login(userName, password).collect {
                if (it.data == true) Log.d("User",it.networkErrorMessage ?: "success")
            }
        }
    }
    sealed class Action {
        data class UserNameIsChanged(val userName: String): Action()
        data class PasswordIsChanged(val password: String): Action()
        object LoginClicked : Action()
        object RegisterClicked : Action()
    }

    private var currentState: State = State()

    fun onAction(action: Action) {
        when(action) {
            is Action.UserNameIsChanged -> currentState.copy(
                userName = action.userName,
                toastMessage = null
            )
            is Action.PasswordIsChanged -> currentState.copy(
                password = action.password,
                toastMessage = null
            )
            is Action.LoginClicked ->
                when {
                    userName.isBlank() -> {
                        stateLiveData.postValue(
                            State(
                                toastMessage = "missing username",
                                userNameIsEmpty = true
                            )
                        )
                    }
                    password.isBlank() -> {
                        stateLiveData.postValue(
                            State(
                                toastMessage = "missing password",
                                passwordIsEmpty = true
                            )
                        )
                    }
                }
        }

    }





}



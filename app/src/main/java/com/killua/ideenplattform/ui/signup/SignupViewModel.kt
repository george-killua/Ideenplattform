package com.killua.ideenplattform.ui.signup

import android.content.Context
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.killua.ideenplattform.data.repository.MainRepository
import com.killua.ideenplattform.ui.login.LoginViewModel

class SignupViewModel(val context: Context, private val userRepository: MainRepository) :
    BaseObservable() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val stateLiveData: MutableLiveData<LoginViewModel.State> =
        MutableLiveData<LoginViewModel.State>()

    val text: LiveData<String> = _text

    @get: Bindable
    var firstName: String = ""

    @get: Bindable
    var lastName: String = ""

    @get: Bindable
    var email: String = ""

    @get: Bindable
    var password: String = ""

    @get: Bindable
    var passwordConfirm: String = ""




}
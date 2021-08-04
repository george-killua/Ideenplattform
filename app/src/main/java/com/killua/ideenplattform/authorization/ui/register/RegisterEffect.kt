package com.killua.ideenplattform.authorization.ui.register

import androidx.annotation.StringRes

/**
 * Authentication result : success (user details) or error message.
 */
sealed class RegisterEffect {
    data class MakeToast(@StringRes val message:Int): RegisterEffect()
    object NavigateToLogin : RegisterEffect()


}
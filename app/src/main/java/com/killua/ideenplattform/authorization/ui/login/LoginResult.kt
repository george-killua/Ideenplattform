package com.killua.ideenplattform.authorization.ui.login

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginEffect(
    val error: Int? = null,
)
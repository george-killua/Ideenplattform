package com.killua.ideenplattform.authorization.ui.register

/**
 * Data validation state of the login form.
 */
data class RegisterUiState(
    val firstNameError: Int? = null,
    val lastNameError: Int? = null,
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val passwordConfirmError: Int? = null,
    val isDataValid: Boolean = false,
    val loadingProgressBar:Boolean=false
)

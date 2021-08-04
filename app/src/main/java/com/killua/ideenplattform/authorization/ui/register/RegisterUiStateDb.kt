package com.killua.ideenplattform.authorization.ui.register

/**
 * User details post authentication that is exposed to the UI
 */
data class RegisterUiStateDb(
    var firstName: String="",
    var lastName: String="",
    var email: String="",
    var password: String="",
    var passwordConfirm: String=""
)

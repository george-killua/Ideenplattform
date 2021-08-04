package com.killua.ideenplattform.authorization.ui.register

sealed class RegisterAction {
    object RegisterClicked:RegisterAction()
    object InputHasChanged:RegisterAction()

}
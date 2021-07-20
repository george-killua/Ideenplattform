package com.killua.ideenplattform.ui.home

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.killua.ideenplattform.data.models.api.CommentList
import com.killua.ideenplattform.data.repository.MainRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HomeViewModel(val context: Context) : BaseObservable(), KoinComponent {
    private val userRepository: MainRepository by inject()

    @Bindable
    val adapter: IdeasAdapter = IdeasAdapter()

    fun showComments(ideaCommentList: CommentList) {

    }

    fun addIdea() {

    }

    private fun dialog(): AlertDialog {
        val dialog = AlertDialog.Builder(context)
        return dialog.create()
    }

    data class State(
        val firstname: String = "",
        val lastname: String = "",
        val toastMessage: String? = null,
        val dialogShow: Dialog? = null
    )

    sealed class Action {
        object LoadIdeas : Action()
        data class FirstnameETChanged(val firstname: String) : Action()
        data class LastnameETChanged(val lastname: String) : Action()
        object AddClicked : Action()
        object dialogShowAction : Action()
    }

    private var currentState: State = State()

    private var onStateChanged: ((State) -> Unit)? = null

    fun onAction(action: Action) {
        currentState = when (action) {
            is Action.LoadIdeas -> {
                adapter.submitList(userRepository.getAllIdeas())
                currentState.copy(
                    toastMessage = null
                )
            }
            is Action.FirstnameETChanged -> currentState.copy(
                firstname = action.firstname,
                toastMessage = null
            )
            is Action.LastnameETChanged -> currentState.copy(
                lastname = action.lastname,
                toastMessage = null,
            )
            is Action.dialogShowAction ->
                currentState.copy(
                    dialogShow = dialog()
                )

            is Action.AddClicked -> (
                    when {
                        currentState.firstname.isBlank() -> {
                            currentState.copy(
                                toastMessage = "Firstname is missed"
                            )
                        }
                        currentState.lastname.isBlank() -> {
                            currentState.copy(
                                toastMessage = "Lastname is missed"
                            )
                        }
                        else -> {
                            currentState.apply {
                                /*    userRepository.insertUser(
                                        UserEntity(
                                            firstname = firstname
                                            lastname = lastname
                                        )
                                    )*/
                                adapter.submitList(userRepository.getAllIdeas())
                                this.copy(
                                    firstname = "",
                                    lastname = "",
                                    toastMessage = "User Added"
                                )
                            }
                        }
                    }
                    )
        }
        onStateChanged?.invoke(currentState)
    }

    fun subscribeToStateChanges(onStateChanged: (State) -> Unit) {
        onStateChanged.invoke(currentState)
        this.onStateChanged = onStateChanged
    }

}
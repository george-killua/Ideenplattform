package com.killua.ideenplattform.ui.profile

import com.killua.ideenplattform.data.repository.MainRepository
import com.killua.ideenplattform.ui.uiutils.BaseViewModel
import kotlinx.coroutines.flow.collect

sealed class ProfileAction {
    object OnEditProfileAction : ProfileAction()
    object OnAddNewIdeaAction : ProfileAction()
    object OnSignOutAction : ProfileAction()
}

data class StateViewDataBinding(
    val imageProfileUrl: String? = null,
    val fullName: String,
    val email: String,
    val firstname: String,
    val lastname: String,
    val rol: String,
)

data class ProfileState(
    val toastMessage: String? = null,
    val isLoading: Boolean? = null,
)

sealed class ProfileEffect {
    object NavigateToEditProfile : ProfileEffect()
    object NavigateToAddIdea : ProfileEffect()
    object NavigateToLoginFragment : ProfileEffect()
}

class ProfileViewModel(val repository: MainRepository) :
    BaseViewModel<StateViewDataBinding, ProfileEffect, ProfileState>(ProfileState()) {

    init {
        reducer {
            copy(isLoading = true)
        }
        launchCoroutine {

            repository.getMe().collect {
                val userCaching = it.data
                if (userCaching != null) {
                    postStateDataBinding(StateViewDataBinding(
                        imageProfileUrl = userCaching.profilePicture,
                        fullName = userCaching.fullName,
                        email = userCaching.email,
                        firstname = userCaching.firstname,
                        lastname = userCaching.lastname,
                        rol = if (userCaching.isManager) "Ideas Manager" else "User"))
                }
                reducer {
                    copy(isLoading = false)
                }
            }


        }
    }

        fun setIntent(intent: ProfileAction) {
            when (intent) {

                ProfileAction.OnAddNewIdeaAction -> navigateToAddIdea()
                ProfileAction.OnEditProfileAction -> navigateToEditProfile()
                ProfileAction.OnSignOutAction -> navigateToLoginFragment()
            }
        }

        private fun navigateToLoginFragment() {
            postEffect(ProfileEffect.NavigateToLoginFragment)
        }

        private fun navigateToAddIdea() {
            postEffect(ProfileEffect.NavigateToAddIdea)
        }

        private fun navigateToEditProfile() {
            postEffect(ProfileEffect.NavigateToEditProfile)
        }


    }
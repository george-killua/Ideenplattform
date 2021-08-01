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
    BaseViewModel<StateViewDataBinding, ProfileEffect, ProfileState>() {

    init { //set default for the view state
        state.postValue(ProfileState())
        val currentViewState = getState()
        val newViewState = currentViewState?.copy(isLoading = true)
        state.postValue(newViewState)
        launchCoroutine {
            repository.getMe().collect {
                when (val userCaching = it.data) {
                    null -> {
                        state.postValue(ProfileState())


                    }
                    else -> {
                        stateDataBinding.postValue(StateViewDataBinding(
                            imageProfileUrl = userCaching.profilePicture,
                            fullName = userCaching.fullName,
                            email = userCaching.email,
                            firstname = userCaching.firstname,
                            lastname = userCaching.lastname,
                            rol = if (userCaching.isManager) "Ideas Manager" else "User"))
                    }
                }

            }
            val myViewState = currentViewState?.copy(isLoading = false)
            state.postValue(myViewState)
        }


    }


    //User related actions will trigger this method
    fun setIntent(intent: ProfileAction) {
        when (intent) {

            ProfileAction.OnAddNewIdeaAction -> navigateToAddIdea()
            ProfileAction.OnEditProfileAction -> navigateToEditProfile()
            ProfileAction.OnSignOutAction -> navigateToLoginFragment()
        }
    }

    /*  private fun getWalletAmount() {
          val currentViewState = getViewState()
          launchRequest {
              //  val walletAmount = walletService.getWalletAmount()
              //   val newViewState = currentViewState?.copy(walletAmount = walletAmount)
              // dataStates.postValue(DataState.Success(newViewState))
          }
      }
  */
    private fun navigateToLoginFragment() {
        viewEffect.postValue(ProfileEffect.NavigateToLoginFragment)
    }

    private fun navigateToAddIdea() {
        viewEffect.postValue(ProfileEffect.NavigateToAddIdea)
    }

    private fun navigateToEditProfile() {
        viewEffect.postValue(ProfileEffect.NavigateToEditProfile)
    }


}
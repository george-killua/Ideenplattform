package com.killua.ideenplattform.ui.home

import android.util.Log
import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavDirections
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.killua.ideenplattform.applicationmanager.MyApplication
import com.killua.ideenplattform.data.repository.MainRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import java.util.*

class HomeViewModel(private val mainRepository: MainRepository) : BaseObservable(), IdeaOnClick {
    private val lang: String = Locale.getDefault().displayLanguage
    var stateLiveData = MutableLiveData<State>()


    @get:Bindable
    val adapter by lazy {
        IdeasAdapter(this)
    }

    @get:Bindable
    var isOnline = false
    var view: View? = null

    init {
        runBlocking {
            mainRepository.login("max.mustermann@example.org", "supersecurepassword1234").collect {
                if (it.data == true) Log.e("george", it.networkErrorMessage ?: "success")
            }
        }
        isOnline = MyApplication.instance.isOnline()
    }

    data class State(
        val isLoadingProgressBar: Boolean = false,
        val navToDetail: String? = null,
        val showIsNotOnline: Boolean? = null,
        val toastMessage: String? = null,
        val navToNewIdea:Boolean?=false
    )

    sealed class Action {
        data class SetupFragment(val view: View, val isTopRank: Boolean = false) :
            Action()

        data class DetailsIdea(val ideaId: String) : Action()
        object AddIdea : Action()
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.SetupFragment -> {
                view = action.view

                runBlocking {
                    stateLiveData.postValue(State(isLoadingProgressBar = true))
                    mainRepository.getAllIdeas().collect { repoResult ->
                        val ideaType = repoResult.data
                        when {
                            ideaType.isNullOrEmpty() -> {
                                stateLiveData.postValue(State(toastMessage = "some thing went wrong"))
                            }
                            repoResult.isNetworkingData -> {
                                if (action.isTopRank) {
                                    ideaType.sortedBy { obj -> obj.ratings.size }
                                }
                                with(adapter) { submitList(ideaType) }
                                stateLiveData.postValue(
                                    State(
                                        isLoadingProgressBar = false,
                                        showIsNotOnline = isOnline
                                    )
                                )
                            }

                        }
                    }

                }
            }


            is Action.DetailsIdea -> {
                if (!isOnline) {
                    stateLiveData.postValue(State(toastMessage = "this can't be done offline"))
                    return
                }

            }
            Action.AddIdea -> {
            stateLiveData.postValue(State(navToNewIdea = true))

            }
        }
    }

    override fun clicked(ideaId: String) {
        onAction(Action.DetailsIdea(ideaId))
    }

    fun addIdea() {
        onAction(Action.AddIdea)
    }

}


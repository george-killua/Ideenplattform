package com.killua.ideenplattform.ui.home

import android.content.Context
import android.util.Log
import android.util.Log.d
import com.killua.ideenplattform.R
import com.killua.ideenplattform.applicationmanager.MyApplication
import com.killua.ideenplattform.data.models.local.CategoryCaching
import com.killua.ideenplattform.data.repository.MainRepository
import com.killua.ideenplattform.ui.uiutils.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import java.lang.ref.WeakReference
import java.util.*


class HomeViewModel(
    private val mainRepository: MainRepository,
    val context: WeakReference<Context>,
) :
    BaseViewModel<HomeViewModel.StateUiDb, HomeViewModel.Effect, HomeViewModel.State
            >(State(), StateUiDb()), IdeaOnClick, CategoryOnClick {


    data class StateUiDb(
        val ideasAdapter: IdeasAdapter? = null,
        val categoriesAdapter: CategoryAdapter? = null,
    )

    data class State(
        val isLoadingProgressBar: Boolean = false,
    )

    sealed class Effect {
        data class NavToDetail(val id: String) : Effect()
        data class ToastMessage(val message: String) : Effect()
        object NavToNewIdea : Effect()

    }

    sealed class Action {
        data class SetupFragment(val isTopRank: Boolean = false) :
            Action()

        data class DetailsIdea(val ideaId: String) : Action()
        data class LoadIdeasWithId(val ideaId: String) : Action()
        object AddIdea : Action()
    }

    init {
        runBlocking {
            mainRepository.login("max.mustermann@example.org", "supersecurepassword1234").collect {
                if (it.data == true) Log.e("george", it.networkErrorMessage ?: "success")
            }
        }
        val ideasAdapter = IdeasAdapter(this@HomeViewModel)
        val categoriesAdapter = CategoryAdapter(this@HomeViewModel)
        reducerDB {
            copy(ideasAdapter = ideasAdapter, categoriesAdapter = categoriesAdapter)
        }


    }


    fun setIntent(action: Action) {
        when (action) {

            is Action.SetupFragment -> {
                launchCoroutine {
                    reducer { (copy(isLoadingProgressBar = true)) }
                    setIdeasListAdapter(action.isTopRank)

                    setCategoryListAdapter()
                    reducer { copy(isLoadingProgressBar = false) }

                }
            }


            is Action.DetailsIdea -> {
                if (!MyApplication.instance.isOnline()) {
                    context.get()?.let {
                        postEffect(Effect.ToastMessage(message = it.getString(R.string.internet_error)))
                    }
                    return
                }
                postEffect(Effect.NavToDetail(id = action.ideaId))


            }
            Action.AddIdea -> postEffect(Effect.NavToNewIdea)


            is Action.LoadIdeasWithId -> context.get()?.let {
                launchCoroutine {
                    if (it.getString(R.string.all_ideas)==action.ideaId)
                        setIdeasListAdapter(isTopRank = false)
                    else getWithId(action.ideaId)
                }
            }
        }
    }

    private suspend fun setIdeasListAdapter(
        isTopRank: Boolean
    ) {
        mainRepository.getAllIdeas().collect { repoResult ->
            repoResult.data?.let { ideasList ->


                ideasList.sortedByDescending { it.lastUpdated }

                if (isTopRank) {
                    ideasList.sortedBy { obj -> obj.ratings.size }
                }
                reducerDB {
                    runBlocking(Dispatchers.Main) {
                        ideasAdapter!!.submitList(ideasList)
                    }
                    copy(ideasAdapter = ideasAdapter)
                }
            }
        }
    }

    private suspend fun setCategoryListAdapter() {
        mainRepository.getAllCategories().collect { repoResultResult ->
            repoResultResult.data?.let { categoriesList ->
                reducerDB {
                    runBlocking(Dispatchers.Main) {
                       context.get()?.let {
                        categoriesList.add(0, CategoryCaching(it.getString(R.string.all_ideas),it.getString(R.string.all_ideas),it.getString(R.string.all_ideas)))

                       }
                        categoriesAdapter!!.submitList(categoriesList)
                    }
                    copy(categoriesAdapter = categoriesAdapter)
                }
            }
        }
    }

    private fun getWithId(id: String) {
        launchCoroutine {
            reducer { (copy(isLoadingProgressBar = true)) }
            mainRepository.getAllIdeas(id).collect { repoResult ->
                val ideaType = repoResult.data
                when {
                    ideaType.isNullOrEmpty() -> {
                        postEffect(Effect.ToastMessage(message = "some thing went wrong"))
                    }
                    repoResult.isNetworkingData -> {
                        d("myArrayBe", ideaType.toString())
                        ideaType.sortedByDescending { it.lastUpdated }
                        d("myArrayBe", ideaType.toString())


                        reducerDB {
                            runBlocking(Dispatchers.Main) {
                                ideasAdapter!!.submitList(ideaType)
                            }
                            copy(ideasAdapter = ideasAdapter)
                        }
                    }
                }
            }
            reducer { copy(isLoadingProgressBar = false) }

        }
    }

    override fun clicked(ideaId: String) {
        setIntent(Action.DetailsIdea(ideaId))
    }

    override fun categoryClicked(ideaId: String) {
        setIntent(Action.LoadIdeasWithId(ideaId))
    }
}


package com.killua.ideenplattform.ui.newidee

import android.net.Uri
import com.killua.ideenplattform.applicationmanager.MyApplication
import com.killua.ideenplattform.data.models.api.Idea
import com.killua.ideenplattform.data.repository.MainRepository
import com.killua.ideenplattform.data.requests.CreateIdeeReq
import com.killua.ideenplattform.ui.uiutils.BaseViewModel
import kotlinx.coroutines.flow.collect
import java.io.File
import java.util.*

class ManagementIdeeViewModel(private val userRepository: MainRepository) :
    BaseViewModel<ManagementIdeeViewModel.ManagementStateDB, ManagementIdeeViewModel.Effect,
            ManagementIdeeViewModel.State>(State(), ManagementStateDB()) {



    data class ManagementStateDB(
        val titleIsToEdit: Boolean = false,
        var imagePathUri: Uri? = null,
        var ideaName: String = "",
        var description: String = "",
        val categoriesArray: List<String> = listOf(),
        val selectedCategory: Int = -1,
        val btnSubmitTextIsToSave: Boolean = false,
    )

    private val lang: String = Locale.getDefault().displayLanguage


    sealed class Effect {

        data class MakeToast(val toastMessage: String? = null) : Effect()
        object NavigateToDetailScreen : Effect()
        data class NavigateToDetailWithIdea(val idea: Idea) : Effect()

    }


    data class State(
        val categoryNotSelected: Boolean = false,
        val titleISEmpty: Boolean = false,
        val descriptionIsEmpty: Boolean = false,
        val internetConnectionError: Boolean = false,
    )

    sealed class Action {
        data class SetupMe(val ideaId: String? = null) : Action()
        data class SaveChanges(val ideaId: String? = null, val file: File?) : Action()
    }


    fun setIntent(action: Action) {
        when (action) {
            is Action.SetupMe -> setupMyFragment(action.ideaId)


            is Action.SaveChanges -> saveChanges(action.ideaId, action.file)

        }


    }

    private fun saveChanges(ideaId: String?, file: File?) {
        if (!validator()) return

        getStateDataBinding().run {
            launchCoroutine {
                var categoryId = ""
                userRepository.getAllCategories().collect { repoResult ->
                    try {
                        val categoryName: String = categoriesArray[selectedCategory]

                        categoryId =
                            repoResult.data?.first { it.name_de == categoryName || it.name_en == categoryName }?.id!!
                    } catch (e: Exception) {
                        postEffect(Effect.MakeToast(e.localizedMessage))
                    }
                }
                val createIdeeReq = CreateIdeeReq(
                    ideaName,
                    categoryId,
                    description
                )
                if (ideaId == null) userRepository.createNewIdea(createIdeeReq, file).collect {
                    if (it.data != null)
                        postEffect(Effect.NavigateToDetailWithIdea(it.data))
                    else postEffect(Effect.MakeToast(it.networkErrorMessage))
                }
                else userRepository.updateIdeaWithId(ideaId, createIdeeReq).collect {
                    if (it.data == true)
                        postEffect(Effect.NavigateToDetailScreen)
                    else postEffect(Effect.MakeToast(it.networkErrorMessage))
                }
            }
        }
    }

    private fun validator(): Boolean {
        var valid = MyApplication.instance.isOnline()
        getStateDataBinding().run {
            if (ideaName.isBlank()) {
                reducer { copy(titleISEmpty = true) }
                valid = false
            } else
                reducer { copy(titleISEmpty = false) }
            if (description.isBlank()) {
                reducer { copy(descriptionIsEmpty = true) }
                valid = false
            } else
                reducer { copy(descriptionIsEmpty = false) }

            if (selectedCategory < 1) {
                reducer { copy(categoryNotSelected = true) }
                valid = false
            } else
                reducer { copy(categoryNotSelected = false) }


            if (!valid) reducer { copy(internetConnectionError = false) }

        }
        return valid

    }


    private fun setupMyFragment(ideaId: String?) {
        launchCoroutine {

            userRepository.getAllCategories().collect { repoResultResult ->
                repoResultResult.data?.let { categoriesList ->
                    reducerDB {
                        copy(categoriesArray = categoriesList.map {
                            if (lang == Locale.ENGLISH.language) it.name_en
                            else it.name_de
                        }
                        )
                    }
                }
            }
            if (!ideaId.isNullOrBlank()) {
                userRepository.getIdeaWithId(ideaId).collect {
                    val idea = it.data
                    if (idea is Idea) {
                        reducerDB {
                            copy(
                                titleIsToEdit = true,
                                ideaName = idea.title,
                                description = idea.description,
                                selectedCategory = categoriesArray.indexOf(
                                    if (lang == Locale.ENGLISH.language) idea.category.name_en
                                    else idea.category.name_de),
                                btnSubmitTextIsToSave = true)
                        }
                    } else reducer { copy(internetConnectionError = true) }
                }
            } else reducerDB {
                copy(
                    titleIsToEdit = false
                )
            }
        }
    }
}



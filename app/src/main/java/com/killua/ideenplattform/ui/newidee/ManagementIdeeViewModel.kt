package com.killua.ideenplattform.ui.newidee


import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.killua.ideenplattform.R
import com.killua.ideenplattform.applicationmanager.MyApplication
import com.killua.ideenplattform.data.models.api.Idea
import com.killua.ideenplattform.data.models.local.CategoryCaching
import com.killua.ideenplattform.data.models.local.IdeaCaching
import com.killua.ideenplattform.data.repository.MainRepository
import com.killua.ideenplattform.data.requests.CreateIdeeReq
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.*

class ManagementIdeeViewModel(val context: Context, private val userRepository: MainRepository) :
    BaseObservable() {
    private val arrayListed: ArrayList<String> = arrayListOf()
    private val lang: String = Locale.getDefault().displayLanguage
    private val categoriesArray: ArrayList<CategoryCaching> = arrayListOf()
    private var selectedCategoryCaching: CategoryCaching? = null
    val stateLiveData: MutableLiveData<State> = MutableLiveData<State>()
    val loadEffectLiveData: MutableLiveData<LoadEffect> = MutableLiveData<LoadEffect>()

    // xml prop
    @get:Bindable
    var imagePathUri: String = ""
    var imageFile: File? = null

    @get:Bindable
    val adapter: ArrayAdapter<String>
        get() {
            val ins =
                ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, arrayListed)
            ins.addAll(*arrayListed.toTypedArray())
            return ins
        }

    @get:Bindable
    var titleQuality: String = ""

    @get:Bindable
    var description: String = ""


    fun addIdea() {
        onAction(Action.AddClicked)
    }
// end

    init {
        runBlocking {
            userRepository.login("max.mustermann@example.org", "supersecurepassword1234").collect {
                if (it.data == true) Log.e("george", it.networkErrorMessage ?: "success")
            }
            userRepository.getAllCategories().collect { repoResultResult ->
                repoResultResult.data?.let { categoriesList ->
                    categoriesList.forEach {
                        Log.e("categoryApi", it.toString())
                        if (lang == Locale.ENGLISH.language)
                            arrayListed.add(it.name_en)
                        else
                            arrayListed.add(it.name_de)

                    }
                    categoriesArray.addAll(categoriesList)
                }
            }

        }
    }

    data class LoadEffect(
        val title: String,
        val name: String,
        val description: String,
        val selectedCategoryPosition: Int,
        val btnUploadText: String,
        val btnSubmitText: String
    )


    data class State(
        val toastMessage: String? = null,
        val categoryNotSelected: Boolean = false,
        val titleISEmpty: Boolean = false,
        val descriptionIsEmpty: Boolean = false,
        val navigateToOtherScreen: Boolean = false,
        val idOfIdea: String? = null

    )

    sealed class Action {
        data class LoadImageFromGallery(val file: File) : Action()
        object AddClicked : Action()
        data class SelectCategory(val position: Int) : Action()
        data class LoadIdToEdit(val id: String? = null, val view: View) : Action()
    }


    fun onAction(action: Action) {
        when (action) {
            is Action.LoadImageFromGallery -> {


                imageFile = action.file
                stateLiveData.postValue(
                    State(
                        toastMessage = "no Image selected"
                    )
                )
            }
            is Action.SelectCategory -> {
                try {
                    selectedCategoryCaching = categoriesArray[action.position]
                } catch (d: Exception) {
                    stateLiveData.postValue(
                        State(
                            toastMessage = "Please select a category"
                        )
                    )
                }

            }
            is Action.AddClicked ->
                when {
                    titleQuality.isBlank() -> {
                        stateLiveData.postValue(
                            State(
                                toastMessage = "title is Empty",
                                titleISEmpty = true
                            )
                        )

                    }
                    description.isBlank() -> {
                        stateLiveData.postValue(
                            State(
                                toastMessage = "description is Empty",
                                descriptionIsEmpty = true

                            )
                        )
                    }
                    selectedCategoryCaching == null -> {
                        stateLiveData.postValue(
                            State(
                                toastMessage = "category not selected",
                                categoryNotSelected = true
                            )
                        )
                    }
                    /*  !MyApplication.instance.isOnline() -> {
                          currentState.copy(toastMessage = "you are offline")
                      }*/
                    else -> {
                        MyApplication.instance.isOnline()
                        val createIdeeReq = CreateIdeeReq(
                            titleQuality,
                            selectedCategoryCaching!!.id, description
                        )
                        runBlocking {
                            userRepository.createNewIdea(createIdeeReq, imageFile).collect {
                                Log.e("idea", it.data.toString())
                                stateLiveData.postValue(
                                    State(
                                        toastMessage = "Idea created",
                                        navigateToOtherScreen = true,
                                        idOfIdea = it.data!!.id
                                    )
                                )
                            }
                        }
                    }
                }
            is Action.LoadIdToEdit -> {

                runBlocking {
                    if (action.id != null) userRepository.getIdeaWithId(action.id).collect {
                        val idea = it.data
                        if (idea is IdeaCaching) {
                            loadEffectLiveData.postValue(
                                LoadEffect(
                                    title = action.view.context.getString(R.string.title_edit_idea),
                                    name = idea.title,
                                    description = idea.description,
                                    selectedCategoryPosition = categoriesArray.indexOf(
                                        categoriesArray.first { it.id == idea.categoryId }),
                                    btnUploadText = action.view.context.getString(R.string.edit),
                                    btnSubmitText = action.view.context.getString(R.string.edit)
                                )
                            )
                        } else if (idea is Idea) {
                            loadEffectLiveData.postValue(
                                LoadEffect(
                                    title = action.view.context.getString(R.string.title_edit_idea),
                                    name = idea.title,
                                    description = idea.description,
                                    selectedCategoryPosition = categoriesArray.indexOf(idea.category),
                                    btnUploadText = action.view.context.getString(R.string.upload),
                                    btnSubmitText = action.view.context.getString(R.string.edit)
                                )
                            )

                        }

                    }
                    else loadEffectLiveData.postValue(
                        LoadEffect(
                            title = action.view.context.getString(R.string.name),
                            name = action.view.context.getString(R.string.name),
                            description = action.view.context.getString(R.string.name),
                            selectedCategoryPosition = 0,
                            btnUploadText = action.view.context.getString(R.string.upload),
                            btnSubmitText = action.view.context.getString(R.string.submit)
                        )
                    )
                }
            }
        }
    }


}
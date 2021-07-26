package com.killua.ideenplattform.ui.newidee


import android.content.Context
import android.util.Log
import android.widget.ArrayAdapter
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.killua.ideenplattform.applicationmanager.MyApplication
import com.killua.ideenplattform.data.models.local.CategoryCaching
import com.killua.ideenplattform.data.repository.MainRepository
import com.killua.ideenplattform.data.requests.CreateIdeeReq
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.internal.wait
import java.io.File
import java.util.*

class ManagementIdeeViewModel(val context: Context, private val userRepository: MainRepository) :
    BaseObservable() {
    private val arrayListed: ArrayList<String> = arrayListOf()
    private val lang: String = Locale.getDefault().displayLanguage
    private val categoriesArray: ArrayList<CategoryCaching> = arrayListOf()
    private var selectedCategoryCaching: CategoryCaching? = null
     val stateLiveData: MutableLiveData<State> = MutableLiveData<State>()

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
    }

    private var currentState: State = State()

    // private var onStateChanged: ((State) -> Unit)? = null

    fun onAction(action: Action) {
        when (action) {
            is Action.LoadImageFromGallery -> {


                imageFile = action.file
                currentState.copy(
                    toastMessage = "no Image selected"
                )
            }
            is Action.SelectCategory -> {
                try {
                    selectedCategoryCaching = categoriesArray[action.position]
                    currentState.copy(categoryNotSelected = false)
                } catch (d: Exception) {
                    currentState.copy(
                        toastMessage = "Please select a category"
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
                                Log.e("idea", it.data.toString() ?: " nothing")
                                stateLiveData.postValue(State(
                                    toastMessage = "Idea created",
                                    navigateToOtherScreen = true,
                                    idOfIdea = it.data!!.id
                                ))
                            }
                        }
                    }
                }

        }
        //onStateChanged?.invoke(currentState)
    }

    fun subscribeToStateChanges(onStateChanged: (State) -> Unit) {
        onStateChanged.invoke(currentState)
        //   this.onStateChanged = onStateChanged
    }

}
package com.killua.ideenplattform.ideamain.details

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.killua.ideenplattform.R
import com.killua.ideenplattform.di.MyApplication
import com.killua.ideenplattform.data.models.api.Idea
import com.killua.ideenplattform.data.models.local.IdeaCaching
import com.killua.ideenplattform.data.models.local.UserCaching
import com.killua.ideenplattform.data.repository.MainRepository
import com.killua.ideenplattform.data.repository.RepoResultResult
import com.killua.ideenplattform.data.requests.CreateCommentReq
import com.killua.ideenplattform.data.requests.IdeaReleaseReq
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.*


class DetailViewModel(
    private val mainRepository: MainRepository
) : BaseObservable(), DeleteFromAdapter {
    private val lang = Locale.getDefault().displayLanguage
    var stateLiveData = MutableLiveData<State>()
    private var ideaId = ""
    private val currentUser: UserCaching by lazy {
        var temp = UserCaching()
        runBlocking {
            mainRepository.getMe().collect { repoResult ->
                repoResult.data?.let {
                    temp = it
                }
            }
        }
        temp
    }

    @get:Bindable
    var commentInput: String = ""

    @get:Bindable
    var idea: Idea? = null

    @get:Bindable
    var ideaCaching: IdeaCaching? = null

    @get:Bindable
    val adapter by lazy {
        CommentsAdapter(currentUser, this)
    }

    @get:Bindable
    var isOnline = false

    @get:Bindable
    var categoryName: String = ""


    @get:Bindable
    var userNameString: String = ""

    @get:Bindable
    var imageUrl: String = ""

    private val isManager by lazy { currentUser.isManager }

    init {
        isOnline = MyApplication.instance.isOnline()
    }

    data class State(
        val isMyIdea: Boolean = false,
        val isManager: Boolean = false,
        val navToEdit: Boolean = false,
        val showComment: Boolean = false,
        val toastMessage: String? = null,
        val commentEmpty: Boolean = false,
        val refreshLayout: Boolean=false
    )

    sealed class Action {
        data class SetupFragment(val ideaId: String) : Action()
        data class UpdateRelease(val view: View) : Action()
        data class EditIdea(val view: View) : Action()
        object CommentEmpty : Action()
        data class AddComment(val message: String) : Action()
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.SetupFragment -> {
                ideaId = action.ideaId
                runBlocking {
                    mainRepository.getIdeaWithId(ideaId).collect {
                        when (val ideaType = it.data) {
                            is Idea -> {
                                idea = ideaType
                                if (lang == Locale.ENGLISH.language)
                                    categoryName = "Category ${ideaType.category.name_en}"
                                else
                                    categoryName = "Kategorie ${ideaType.category.name_de}"

                                userNameString = ideaType.author.fullName
                                imageUrl = ideaType.imageUrl
                                adapter.submitList(ideaType.comments)

                                stateLiveData.postValue(
                                    State(
                                        isManager = currentUser.isManager,
                                        isMyIdea = currentUser.userId == ideaType.author.userId,
                                        showComment = true
                                    )
                                )
                            }
                            is IdeaCaching -> {
                                ideaCaching = ideaType
                                val category = mainRepository.getCategoryWithId(ideaType.categoryId)
                                    .first().data
                                val user = mainRepository.getUserId(ideaType.authorId).first().data
                                imageUrl = ideaType.imageUrl

                                if (lang == Locale.ENGLISH.language)
                                    categoryName = "Category ${category!!.name_en}"
                                else
                                    categoryName = "Kategorie ${category!!.name_de}"

                                userNameString = user!!.fullName

                                stateLiveData.postValue(
                                    State(
                                        toastMessage = "you are offline",
                                        isManager = currentUser.isManager,
                                        isMyIdea = currentUser.userId == ideaType.authorId,
                                        showComment = false
                                    )
                                )
                            }
                            else -> {
                                stateLiveData.postValue(State(toastMessage = "some thing went wrong"))

                            }
                        }
                    }
                }
            }
            is Action.UpdateRelease -> {

                if (!isOnline) {
                    stateLiveData.postValue(State(toastMessage = "this can't be done offline"))
                    return
                }
                if (isManager) {
                    dialogBuilder(action.view)
                }
            }

            is Action.EditIdea -> {
                if (!isOnline) {
                    stateLiveData.postValue(State(toastMessage = "this can't be done offline"))
                    return
                }
                stateLiveData.postValue(
                    State(
                        navToEdit = true
                    )
                )
            }
            Action.CommentEmpty -> {
                stateLiveData.postValue(
                    State(
                     commentEmpty = true
                    )
                )
            }
            is Action.AddComment -> {
                runBlocking {
                    mainRepository.createComment(
                        ideaId = ideaId,
                        createCommentReq = CreateCommentReq(action.message)
                    ).collect { repoResult: RepoResultResult<Boolean> ->
                        if (repoResult.data == true) {
                            stateLiveData.postValue(
                                State(
                                    toastMessage = "posted",
                                    refreshLayout = true
                                )

                            )
                        } else {
                            stateLiveData.postValue(
                                State(
                                    toastMessage = repoResult.networkErrorMessage
                                        ?: "something went wrong"
                                )
                            )

                        }
                    }

                }
            }
        }
    }


    private fun dialogBuilder(view: View): AlertDialog {
        val builder = AlertDialog.Builder(view.context)
        builder.setCancelable(true)
            .setPositiveButton(R.string.share_idea) { dialog, _ ->
                shareIdeaBtn(dialog)
            }
            .setNegativeButton(R.string.abort) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        return builder.create()
    }

    private fun shareIdeaBtn(dialog: DialogInterface) {
        if (idea != null) {
            runBlocking {
                mainRepository.releaseIdea(idea!!.id, IdeaReleaseReq(!idea!!.released))
                    .collect { repoResultResult ->
                        repoResultResult.data?.let { success ->
                            if (success) stateLiveData.postValue(State(toastMessage = "the idea release is updated"))
                            else stateLiveData.postValue(State(toastMessage = "the idea release is updated"))

                        }
                    }
            }
            dialog.dismiss()
        } else {
            stateLiveData.postValue(State(toastMessage = "this can't be done offline"))
            dialog.dismiss()
        }

    }

    override fun removeComment(ideaId: String) {

        runBlocking {
            mainRepository.deleteComments(idea!!.id, ideaId)
        }
    }

    fun addComment() {
        if (commentInput.isBlank()) {
            onAction(Action.CommentEmpty)
            return
        }
        onAction(Action.AddComment(commentInput))


    }
}


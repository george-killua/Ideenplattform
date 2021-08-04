package com.killua.ideenplattform.ideamain.home

import android.content.Context
import android.util.Log
import android.util.Log.d
import com.killua.ideenplattform.R
import com.killua.ideenplattform.di.MyApplication
import com.killua.ideenplattform.data.models.api.Idea
import com.killua.ideenplattform.data.models.local.CategoryCaching
import com.killua.ideenplattform.data.models.local.UserCaching
import com.killua.ideenplattform.data.repository.MainRepository
import com.killua.ideenplattform.data.requests.PostRating
import com.killua.ideenplattform.ideamain.uiutils.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.lang.ref.WeakReference
import java.util.*


class HomeViewModel(
    private val mainRepository: MainRepository,
    val context: WeakReference<Context>,
) :
    BaseViewModel<HomeViewModel.StateUiDb, HomeViewModel.Effect, HomeViewModel.State
            >(State(), StateUiDb()), IdeaOnClick, CategoryOnClick, SortItemOnClick {


    data class StateUiDb(
        val ideasAdapter: IdeasAdapter? = null,
        val categoriesAdapter: CategoryAdapter? = null,
        val sortTypeAdapter: SortItemAdapter? = null,
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
        data class RateIdea(val i: Int,val id: String) : Action()
        data class RemoveIdeaRate(val id: String) : Action()
        object AddIdea : Action()
    }

    init {
        runBlocking {
            mainRepository.login("max.mustermann@example.org", "supersecurepassword1234").collect {
                if (it.data == true) Log.e("george", it.networkErrorMessage ?: "success")
            }
        }
        var currentUser=UserCaching()
        launchCoroutine {
            mainRepository.getMe().collect {
                it.data?.let {
                    currentUser=it
                    val ideasAdapter = IdeasAdapter(this@HomeViewModel,currentUser)
                    val categoriesAdapter = CategoryAdapter(this@HomeViewModel)
                    val sortItemAdapter = SortItemAdapter(this@HomeViewModel)
                    reducerDB {
                        copy(ideasAdapter = ideasAdapter,
                            categoriesAdapter = categoriesAdapter,
                            sortTypeAdapter = sortItemAdapter)
                    }
                }
            }
        }


    }


    fun setIntent(action: Action) {
        when (action) {

            is Action.SetupFragment -> {
                launchCoroutine {
                    reducer { (copy(isLoadingProgressBar = true)) }
                    setIdeasListAdapter(action.isTopRank)

                    setCategoryListAdapter()
                    getSortTypeArray()
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
                    if (it.getString(R.string.all_ideas) == action.ideaId)
                        setIdeasListAdapter(isTopRank = false)
                    else getWithId(action.ideaId)
                }
            }
            is Action.RateIdea -> launchCoroutine {
                mainRepository.postRating(action.id, PostRating( action.i)).collect {
                    postEffect(Effect.ToastMessage(it.networkErrorMessage.orEmpty()))
                }

            }
            is Action.RemoveIdeaRate -> launchCoroutine {
                mainRepository.deleteRating(action.id, ).collect {
                    postEffect(Effect.ToastMessage(it.networkErrorMessage.orEmpty()))
                }
            }
        }
    }

    private suspend fun setIdeasListAdapter(
        isTopRank: Boolean,
    ) {
        mainRepository.getAllIdeas().collect { repoResult ->
            repoResult.data?.let { ideasList ->


                ideasList.toMutableList().sortWith { o1, o2 ->
                    val dateFormat =
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                    LocalDateTime.parse(o1?.created, dateFormat).compareTo(
                        LocalDateTime.parse(o2?.created, dateFormat))
                }

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
                            categoriesList.add(0,
                                CategoryCaching(it.getString(R.string.all_ideas),
                                    it.getString(R.string.all_ideas),
                                    it.getString(R.string.all_ideas)))

                        }
                        categoriesAdapter!!.submitList(categoriesList)
                    }
                    copy(categoriesAdapter = categoriesAdapter)
                }
            }
        }
    }

    private fun getSortTypeArray() {
        reducerDB {
            runBlocking(Dispatchers.Main) {
                context.get()?.let {


                    sortTypeAdapter!!.submitList(it.resources.getStringArray(R.array.sort_type)
                        .toList())
                }
            }
            copy(sortTypeAdapter = sortTypeAdapter)
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

                        ideaType.toMutableList().sortWith { o1, o2 ->
                            val dateFormat =
                                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                            LocalDateTime.parse(o1?.created, dateFormat).compareTo(
                                LocalDateTime.parse(o2?.created, dateFormat))
                        }


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

    override fun emojiOnClickInsert(i: Int, id: String) {
        setIntent(Action.RateIdea(i, id))

    }

    override fun emojiOnClickRemove(id: String) {
        setIntent(Action.RemoveIdeaRate( id))
    }

    override fun categoryClicked(ideaId: String) {
        setIntent(Action.LoadIdeasWithId(ideaId))
    }

    /*
            <item>most rated</item>  0
            <item>less rated</item>  1
            <item>create date</item> 2
            <item>last update</item>  3
            <item>most comments</item>  4
            <item>shuffle</item>  5
     */
    private fun mostRated(adapter: IdeasAdapter?): MutableList<Idea>? {
        val list = adapter?.currentList?.toMutableList()
        d("SortTypeLessRated", "shuffled")
        list?.sortedBy {
            it.ratings.count()
            d("SortTypeMostRated", it.ratings.count().toString())
        }
        return list
    }

    private fun lessRated(adapter: IdeasAdapter?): MutableList<Idea>? {
        val list = adapter?.currentList?.toMutableList()
        d("SortTypeLessRated", "shuffled")
        list?.sortedByDescending {
            it.ratings.count()
            d("SortTypeLessRated", it.ratings.count().toString())
        }
        return list
    }

    private fun createDate(adapter: IdeasAdapter?): MutableList<Idea>? {

        val list = adapter?.currentList?.toMutableList()
        d("SortTypeLessRated", "shuffled")
        list?.sortedByDescending {

            val dateFormat =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            LocalDateTime.parse(it.created, dateFormat)
            d("SortTypeLessRated", it.created)

        }
        return list
    }

    private fun lastUpdate(adapter: IdeasAdapter?): MutableList<Idea>? {
        val list = adapter?.currentList?.toMutableList()
        d("SortTypeLessRated", "shuffled")
        list?.sortedByDescending {

            val dateFormat =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            LocalDateTime.parse(it.lastUpdated, dateFormat)
            d("SortTypeLessRated", it.lastUpdated)

        }
        return list
    }

    private fun mostComments(adapter: IdeasAdapter?): MutableList<Idea>? {
        val list = adapter?.currentList?.toMutableList()
        d("SortTypeLessRated", "shuffled")
        list?.sortedByDescending {
            it.comments.count()
            d("SortTypeLessRated", it.comments.count().toString())
        }
        return list
    }

    private fun shuffle(adapter: IdeasAdapter?): MutableList<Idea>? {
        val list = adapter?.currentList?.toMutableList()
        d("SortTypeLessRated", "shuffled")
        list?.shuffle()
        return list
    }

    override fun itemClicked(ideaId: String) {
        reducerDB {
            val adapter = ideasAdapter
            val newList = when (sortTypeAdapter?.currentList?.indexOf(ideaId)) {
                0 -> mostRated(adapter)
                1 -> lessRated(adapter)
                2 -> createDate(adapter)
                3 -> lastUpdate(adapter)
                4 -> mostComments(adapter)
                5 -> shuffle(adapter)
                else -> null
            }
            runBlocking(Dispatchers.Main) {
                ideasAdapter?.submitList(newList)

            }
            copy(ideasAdapter = ideasAdapter)
        }
    }
}


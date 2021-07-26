package com.killua.ideenplattform.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.killua.ideenplattform.data.caching.CategoryDao
import com.killua.ideenplattform.data.caching.IdeaDao
import com.killua.ideenplattform.data.caching.UserDao
import com.killua.ideenplattform.data.models.api.Idea
import com.killua.ideenplattform.data.models.api.IdeaComment
import com.killua.ideenplattform.data.models.local.CategoryCaching
import com.killua.ideenplattform.data.models.local.IdeaCaching
import com.killua.ideenplattform.data.models.local.SharedPrefsUser
import com.killua.ideenplattform.data.models.local.UserCaching
import com.killua.ideenplattform.data.network.ApiServices
import com.killua.ideenplattform.data.repository.NetworkResult.ResponseHandler.safeApiCall
import com.killua.ideenplattform.data.requests.*
import com.killua.ideenplattform.data.utils.SharedPreferencesHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import java.lang.reflect.Type


class MainRepositoryImpl(
    val api: ApiServices,
    private val userDao: UserDao,
    private val ideaDao: IdeaDao,
    private val categoryDao: CategoryDao,
    context: Context
) :
    MainRepository {
    private val sharedPrefsHandler by lazy {
        SharedPreferencesHandler(context)
    }
    private var password: String = ""

    override suspend fun login(email: String, password: String): Flow<RepoResultResult<Boolean>> {
        val oldUser = sharedPrefsHandler.userLoader
        this.password = oldUser?.password ?: ""
        val newUser = SharedPrefsUser("", email, password)
        if (oldUser == null) {
            sharedPrefsHandler.saveUserContent(newUser)
            this.password = password
        } else if (oldUser != newUser) sharedPrefsHandler.saveUserContent(newUser)
        return flow {
            getMe().collect {
                emit(
                    RepoResultResult(
                        it.data != null,
                        isNetworkingData = it.isNetworkingData,
                        it.networkErrorMessage
                    )
                )
            }
        }.flowOn(Dispatchers.IO)
    }


    override suspend fun getAllUsers(): Flow<RepoResultResult<ArrayList<UserCaching>>> =
        flow {
            when (val res = safeApiCall(api.getAllUsers())) {

                is NetworkResult.Success -> {
                    userDao.addUsers(*res.data!!.toTypedArray())
                    emit(RepoResultResult(res.data, true))
                }
                is NetworkResult.Error -> {
                    val localeData: ArrayList<UserCaching> = arrayListOf()
                    userDao.getAllUsers()?.let { localeData.addAll(userDao.getAllUsers()!!) }
                    emit(RepoResultResult(localeData, false, res.message))


                }
            }
        }.flowOn(Dispatchers.IO)

    override suspend fun createUser(userCreateReq: UserCreateReq): Flow<RepoResultResult<Nothing>> =
        flow {
            when (val res = safeApiCall(api.createUser(userCreateReq))) {

                is NetworkResult.Success -> {
                    emit(RepoResultResult(null, true, "User add Successfully"))
                }
                is NetworkResult.Error -> {
                    emit(RepoResultResult(null, false, res.message))


                }
            }
            getAllUsers()
        }.flowOn(Dispatchers.IO)

    override suspend fun updateUser(userCreateReq: UserCreateReq): Flow<RepoResultResult<Nothing>> =
        flow {
            when (val res = safeApiCall(api.updateUser(userCreateReq))) {

                is NetworkResult.Success -> {
                    emit(RepoResultResult(null, true, "User updated Successfully"))
                }
                is NetworkResult.Error -> {
                    emit(RepoResultResult(null, false, res.message))


                }
            }
            getAllUsers()
        }.flowOn(Dispatchers.IO)

    override suspend fun getMe(): Flow<RepoResultResult<UserCaching?>> =
        flow {
            when (val res = safeApiCall(api.getMe())) {

                is NetworkResult.Success -> {
                    emit(RepoResultResult(res.data, true))
                    res.data?.let {
                        sharedPrefsHandler.saveUserContent(
                            SharedPrefsUser(
                                it.userId,
                                it.email,
                                password
                            )
                        )
                    }
                }
                is NetworkResult.Error -> {
                    val oldUser = sharedPrefsHandler.userLoader
                    var userCaching: UserCaching? = null
                    if (oldUser != null) userCaching = userDao.getUserWithId(oldUser.id)
                    emit(RepoResultResult(userCaching, false, res.message))


                }
            }
        }.flowOn(Dispatchers.IO)

    override suspend fun getUserId(id: String): Flow<RepoResultResult<UserCaching>> =
        flow {
            when (val res = safeApiCall(api.getUserId(id))) {

                is NetworkResult.Success -> {
                    emit(RepoResultResult(res.data, true))
                }
                is NetworkResult.Error -> {
                    val caching = userDao.getUserWithId(id)
                    emit(RepoResultResult(caching, false, res.message))


                }
            }
        }.flowOn(Dispatchers.IO)

    override suspend fun uploadUserImage(path: String): Flow<RepoResultResult<Nothing>> {
        val file = File(path)
        val reqFile: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val bodyImage: MultipartBody.Part =
            MultipartBody.Part.createFormData("user_pic", file.name, reqFile)
        return flow {
            when (val res = safeApiCall(api.uploadUserImage(bodyImage))) {

                is NetworkResult.Success -> {
                    emit(RepoResultResult(null, true))
                    getMe()
                }
                is NetworkResult.Error -> {
                    emit(RepoResultResult(null, false, res.message))
                }
            }
        }.flowOn(Dispatchers.IO)

    }

    override suspend fun deleteImageOfUser(): Flow<RepoResultResult<Nothing>> = flow {
        when (val res = safeApiCall(api.deleteImageOfUser())) {

            is NetworkResult.Success -> {
                emit(RepoResultResult(null, true, "image deleted Successfully"))
                getMe()
            }
            is NetworkResult.Error -> {

                emit(RepoResultResult(null, false, res.message))
            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun updateMangerStatus(
        userId: String,
        updateManagerStatus: UpdateManagerStatus
    ): Flow<RepoResultResult<Nothing>> = flow {
        when (val res = safeApiCall(api.updateMangerStatus(userId, updateManagerStatus))) {

            is NetworkResult.Success -> {
                emit(RepoResultResult(null, true, "rating deleted Successfully"))
            }
            is NetworkResult.Error -> {

                emit(RepoResultResult(null, false, res.message))
            }
        }
    }.flowOn(Dispatchers.IO)


    override suspend fun getAllCategories(): Flow<RepoResultResult<ArrayList<CategoryCaching>>> =
        flow {
            when (val res = safeApiCall(api.getCategory())) {

                is NetworkResult.Success -> {
                    categoryDao.getAllCategories()?.subtract(res.data!!)?.toTypedArray()
                        ?.let { categoryDao.remove(*it) }
                    categoryDao.addCategories(*res.data!!.toTypedArray())
                    emit(RepoResultResult(res.data, true))
                }
                is NetworkResult.Error -> {

                    val localeData: ArrayList<CategoryCaching> = arrayListOf()
                    categoryDao.getAllCategories()
                        ?.let { localeData.addAll(categoryDao.getAllCategories()!!) }
                    emit(RepoResultResult(localeData, false, res.message))


                }
            }
        }.flowOn(Dispatchers.IO)

    override suspend fun getCategoryWithId(id: String): Flow<RepoResultResult<CategoryCaching>> =
        flow {
            when (val res = safeApiCall(api.getCategoryWithId(id))) {

                is NetworkResult.Success -> {
                    emit(RepoResultResult(res.data, true, "rating deleted Successfully"))
                }
                is NetworkResult.Error -> {
                    val caching = categoryDao.getCategoryWithId(id)
                    emit(RepoResultResult(caching, false, res.message))
                }
            }
        }.flowOn(Dispatchers.IO)

    override suspend fun createNewIdea(
        createIdeeReq: CreateIdeeReq,
        file: File?
    ): Flow<RepoResultResult<Idea?>> {
//body of idea request
        val type: Type = object : TypeToken<CreateIdeeReq>() {}.type
        val stringBody = Gson().toJson(createIdeeReq, type)
        val requestBody = stringBody.toRequestBody("application/json".toMediaTypeOrNull())

// image of idea request
        val requestImage: RequestBody? = file?.asRequestBody("image/jpg".toMediaTypeOrNull())

        val image: MultipartBody.Part? =
            requestImage?.let { MultipartBody.Part.createFormData("files[0]", file.name, it) }

        val apiRequest = if (image == null) api.createNewIdea(requestBody) else api.createNewIdea(
            requestBody,
            image
        )





        return flow {
            when (val res = safeApiCall(apiRequest)) {

                is NetworkResult.Success -> {
                    emit(RepoResultResult(res.data, true))
                }
                is NetworkResult.Error -> {
                    emit(RepoResultResult(null, false, res.message))
                }
            }
        }.flowOn(Dispatchers.IO)
    }


    override suspend fun getAllIdeas() = flow {
        val ideasArray: ArrayList<Any> = arrayListOf()
        var isNetworkingData = false
        var networkErrorMessage: String? = null
        getAllCategories().map { repoResultResult ->
            if (repoResultResult.isNetworkingData) {
                repoResultResult.data?.forEach {
                    try {
                        val ideas = safeApiCall(api.getAllIdeas(it.id)).data!!
                        ideasArray.addAll(ideas)
                        getAllideaDBHelper(
                            ideas.take(10).map { idea ->
                                IdeaCaching(
                                    ideaCachingId = idea.id,
                                    authorId = idea.author.userId,
                                    title = idea.title,
                                    categoryId = idea.category.id,
                                    description = idea.description,
                                    created = idea.created,
                                    lastUpdated = idea.lastUpdated,
                                    imageUrl = idea.imageUrl
                                )
                            }
                        )
                    } catch (e: java.lang.Exception) {
                        networkErrorMessage = e.localizedMessage!!
                        ideaDao.getAllIdeas()?.let { it1 -> ideasArray.addAll(it1) }

                    }
                }
            }
            isNetworkingData = repoResultResult.isNetworkingData
            networkErrorMessage = repoResultResult.networkErrorMessage

        }
        emit(RepoResultResult(ideasArray, isNetworkingData, networkErrorMessage))
    }.flowOn(Dispatchers.IO)

    private fun getAllideaDBHelper(ideaCaching: List<IdeaCaching>) {
        ideaDao.removeAll()
        ideaDao.addIdeas(*ideaCaching.toTypedArray())
    }

    override suspend fun getIdeaWithId(ideaId: String): Flow<RepoResultResult<Any>> = flow {
        when (val res = safeApiCall(api.getIdeaWithId(ideaId))) {

            is NetworkResult.Success -> {
                emit(RepoResultResult(res.data, true))
            }
            is NetworkResult.Error -> {
                val caching = ideaDao.getIdeaWithId(ideaId)
                emit(RepoResultResult(caching, false, res.message))


            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun updateIdeaWithId(
        ideaId: String,
        createIdeeReq: CreateIdeeReq
    ): Flow<RepoResultResult<Nothing>> = flow {
        when (val res = safeApiCall(api.updateIdeaWithId(ideaId, createIdeeReq))) {

            is NetworkResult.Success -> {
                emit(RepoResultResult(null, true))
            }
            is NetworkResult.Error -> {
                emit(RepoResultResult(null, false, res.message))


            }
        }
        getAllIdeas()
    }.flowOn(Dispatchers.IO)

    override suspend fun deleteIdeaWithId(ideaId: String): Flow<RepoResultResult<Nothing>> = flow {
        when (val res = safeApiCall(api.deleteIdeaWithId(ideaId))) {

            is NetworkResult.Success -> {
                emit(RepoResultResult(null, true))
            }
            is NetworkResult.Error -> {
                emit(RepoResultResult(null, false, res.message))


            }
        }
        getAllIdeas()
    }.flowOn(Dispatchers.IO)

    override suspend fun searchIdeal(searchText: String) = flow {
        when (val res = safeApiCall(api.searchIdeal(searchText))) {

            is NetworkResult.Success -> {
                emit(RepoResultResult(res.data, true))
            }
            is NetworkResult.Error -> {
                val caching = ideaDao.getCityBySearchText(searchText)
                emit(RepoResultResult(caching, false, res.message))
            }
        }
    }.flowOn(Dispatchers.IO)


    override suspend fun releaseIdea(
        ideaId: String,
        releaseReq: IdeaReleaseReq
    ): Flow<RepoResultResult<Nothing>> = flow {
        when (val res = safeApiCall(api.releaseIdea(ideaId, releaseReq))) {

            is NetworkResult.Success -> {
                emit(RepoResultResult(null, true))
            }
            is NetworkResult.Error -> {
                emit(RepoResultResult(null, false, res.message))
            }
        }
    }.flowOn(Dispatchers.IO)


    override suspend fun createComment(
        ideaId: String,
        createCommentReq: CreateCommentReq
    ): Flow<RepoResultResult<Nothing>> = flow {
        when (val res = safeApiCall(api.createComment(ideaId, createCommentReq))) {

            is NetworkResult.Success -> {
                emit(RepoResultResult(null, true))
            }
            is NetworkResult.Error -> {
                emit(RepoResultResult(null, false, res.message))
            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getComments(ideaId: String): Flow<RepoResultResult<List<IdeaComment>>> = flow {
        when (val res = safeApiCall(api.getComments(ideaId))) {

            is NetworkResult.Success -> {
                emit(RepoResultResult(res.data, true))
            }
            is NetworkResult.Error -> {
                emit(RepoResultResult(null, false, res.message))


            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun deleteComments(
        ideaId: String,
        commentId: String
    ): Flow<RepoResultResult<Nothing>> = flow {
        when (val res = safeApiCall(api.deleteComments(ideaId, commentId))) {

            is NetworkResult.Success -> {
                emit(RepoResultResult(null, true))
            }
            is NetworkResult.Error -> {
                emit(RepoResultResult(null, false, res.message))


            }
        }
        getComments(ideaId)
    }.flowOn(Dispatchers.IO)

    override suspend fun postRating(
        ideaId: String,
        postRating: PostRating
    ): Flow<RepoResultResult<Nothing>> = flow {
        when (val res = safeApiCall(api.postRating(ideaId, postRating))) {

            is NetworkResult.Success -> {
                emit(RepoResultResult(null, true))
            }
            is NetworkResult.Error -> {
                emit(RepoResultResult(null, false, res.message))


            }
        }
        getAllIdeas()
    }.flowOn(Dispatchers.IO)

    override suspend fun deleteRating(ideaId: String): Flow<RepoResultResult<Nothing>> = flow {
        when (val res = safeApiCall(api.deleteRating(ideaId))) {

            is NetworkResult.Success -> {
                emit(RepoResultResult(null, true))
            }
            is NetworkResult.Error -> {
                emit(RepoResultResult(null, false, res.message))


            }
        }
        getAllIdeas()
    }.flowOn(Dispatchers.IO)

    override suspend fun uploadImageIdea(
        ideaId: String,
        path: String
    ): Flow<RepoResultResult<Nothing>> {
        val file = File(path)
        val reqFile: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val bodyImage: MultipartBody.Part =
            MultipartBody.Part.createFormData("user_pic", file.name, reqFile)
        return flow {
            when (val res = safeApiCall(api.uploadImageIdea(ideaId, bodyImage))) {

                is NetworkResult.Success -> {
                    emit(RepoResultResult(null, true))
                }
                is NetworkResult.Error -> {
                    emit(RepoResultResult(null, false, res.message))
                }
            }
        }.flowOn(Dispatchers.IO)
    }

}


data class RepoResultResult<out T>(
    val data: T?,
    val isNetworkingData: Boolean = false,
    val networkErrorMessage: String? = null
)


sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : NetworkResult<T>(data)
    class Error<T>(message: String, data: T? = null) : NetworkResult<T>(data, message)

    object ResponseHandler {
        fun <T> safeApiCall(apiCall: Response<T>): NetworkResult<T> {
            try {
                if (apiCall.isSuccessful) {
                    val body = apiCall.body()
                    body?.let {
                        return Success(body)
                    }
                }
                return error("${apiCall.code()} ${apiCall.message()}")
            } catch (e: Exception) {
                return error(e.message ?: e.toString())
            }
        }

        private fun <T> error(errorMessage: String): NetworkResult<T> =
            Error("Api call failed $errorMessage")

    }
}
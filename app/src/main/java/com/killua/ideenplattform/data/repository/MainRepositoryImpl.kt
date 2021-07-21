package com.killua.ideenplattform.data.repository

import com.killua.ideenplattform.data.caching.CategoryDao
import com.killua.ideenplattform.data.caching.IdeaDao
import com.killua.ideenplattform.data.caching.UserDao
import com.killua.ideenplattform.data.models.local.CategoryCaching
import com.killua.ideenplattform.data.network.ApiServices
import com.killua.ideenplattform.data.repository.NetworkResult.ResponseHandler.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import retrofit2.Response

class MainRepositoryImpl(
    val api: ApiServices,
    val userDao: UserDao,
    val ideaDao: IdeaDao,
    val categoryDao: CategoryDao
) :
    MainRepository {

    override suspend fun getAllCategories() = flow {
        when (val res = safeApiCall(api.getCategory())) {

            is NetworkResult.Success -> {
                categoryDao.addCategories(*res.data!!)
                emit(RepoResultResult.Networking(res.data))
            }
            is NetworkResult.Error -> {
                val localeData = categoryDao.getAllCategories()
                emit(RepoResultResult.Caching(localeData, res.message))


            }
        }
    }.flowOn(Dispatchers.IO)


    @InternalCoroutinesApi
    override suspend fun getAllIdeas() = flow {

runBlocking {
    getAllCategories().collect {

    }
}
        getAllCategories().collect { categories ->
            when (categories) {
                is RepoResultResult.Networking -> {
                    val
                    safeApiCall(api.getAllIdeas(categories.))
                }
                is RepoResultResult.Caching -> {


                }
            }
        }

    }.flowOn(Dispatchers.IO)

}

 open class RepoResultResult<out R> {
    data class Networking<out T>(val data: T) : RepoResultResult<T>()
    data class Caching<out T>(val data: T?, val networkErrorMessage: String?) :
        RepoResultResult<T>()

}

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
                        return NetworkResult.Success(body)
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
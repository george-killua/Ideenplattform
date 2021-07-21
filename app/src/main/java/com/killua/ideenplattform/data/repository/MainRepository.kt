package com.killua.ideenplattform.data.repository

import com.killua.ideenplattform.data.models.api.CommentList
import com.killua.ideenplattform.data.models.local.CategoryCaching
import com.killua.ideenplattform.data.models.local.UserCaching
import com.killua.ideenplattform.data.requests.*
import kotlinx.coroutines.flow.Flow

interface MainRepository {
    suspend fun getAllUsers(): Flow<RepoResultResult<ArrayList<UserCaching>>>
    suspend fun createUser(userCreateReq: UserCreateReq): Flow<RepoResultResult<Nothing>>
    suspend fun updateUser(userCreateReq: UserCreateReq): Flow<RepoResultResult<Nothing>>
    suspend fun getMe(): Flow<RepoResultResult<Nothing>>
    suspend fun getUserId(id: String): Flow<RepoResultResult<UserCaching>>
    suspend fun uploadUserImage(path: String): Flow<RepoResultResult<Nothing>>
    suspend fun deleteImageOfUser(): Flow<RepoResultResult<Nothing>>
    suspend fun updateMangerStatus(
        userId: String,
        updateManagerStatus: UpdateManagerStatus
    ): Flow<RepoResultResult<Nothing>>

    suspend fun getAllCategories(): Flow<RepoResultResult<ArrayList<CategoryCaching>>>
    suspend fun getCategoryWithId(id: String): Flow<RepoResultResult<CategoryCaching>>
    suspend fun createNewIdea(
        createIdeeReq: CreateIdeeReq,
        path:String
    ): Flow<RepoResultResult<Nothing>>

    suspend fun getAllIdeas(): Flow<RepoResultResult<ArrayList<Any>>>
    suspend fun getIdeaWithId( ideaId: String): Flow<RepoResultResult<Any>>
    suspend fun updateIdeaWithId(
        ideaId: String,
        createIdeeReq: CreateIdeeReq
    ): Flow<RepoResultResult<Nothing>>

    suspend fun deleteIdeaWithId(ideaId: String): Flow<RepoResultResult<Nothing>>
    suspend fun searchIdeal(searchText: String): Flow<RepoResultResult<java.util.ArrayList<out Any>>>
    suspend fun releaseIdea(
        ideaId: String,
        releaseReq: IdeaReleaseReq
    ): Flow<RepoResultResult<Nothing>>

    suspend fun createComment(
        ideaId: String,
        createCommentReq: CreateCommentReq
    ): Flow<RepoResultResult<Nothing>>

    suspend fun getComments(ideaId: String): Flow<RepoResultResult<CommentList>>
    suspend fun deleteComments(ideaId: String, commentId: String): Flow<RepoResultResult<Nothing>>
    suspend fun postRating(ideaId: String, postRating: PostRating): Flow<RepoResultResult<Nothing>>
    suspend fun deleteRating(ideaId: String): Flow<RepoResultResult<Nothing>>
    suspend fun uploadImageIdea(ideaId: String, path: String): Flow<RepoResultResult<Nothing>>
}
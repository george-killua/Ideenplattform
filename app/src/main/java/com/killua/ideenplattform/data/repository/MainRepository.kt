package com.killua.ideenplattform.data.repository

import com.killua.ideenplattform.data.models.api.Idea
import com.killua.ideenplattform.data.models.api.IdeaComment
import com.killua.ideenplattform.data.models.local.CategoryCaching
import com.killua.ideenplattform.data.models.local.UserCaching
import com.killua.ideenplattform.data.requests.*
import kotlinx.coroutines.flow.Flow
import java.io.File

interface MainRepository {
    suspend fun login(email: String, password: String): Flow<RepoResultResult<Boolean>>
    suspend fun getAllUsers(): Flow<RepoResultResult<ArrayList<UserCaching>>>
    suspend fun createUser(userCreateReq: UserCreateReq): Flow<RepoResultResult<Boolean>>
    suspend fun updateUser(userCreateReq: UserCreateReq): Flow<RepoResultResult<Nothing>>
    suspend fun getMe(): Flow<RepoResultResult<UserCaching?>>
    suspend fun getUserId(id: String): Flow<RepoResultResult<UserCaching>>
    suspend fun uploadUserImage(path: String): Flow<RepoResultResult<Nothing>>
    suspend fun deleteImageOfUser(): Flow<RepoResultResult<Nothing>>
    suspend fun updateMangerStatus(
        userId: String,
        updateManagerStatus: UpdateManagerStatus
    ): Flow<RepoResultResult<Nothing>>
    fun postscher()
    suspend fun getAllCategories(): Flow<RepoResultResult<ArrayList<CategoryCaching>>>
    suspend fun getCategoryWithId(id: String): Flow<RepoResultResult<CategoryCaching>>
    suspend fun createNewIdea(
        createIdeeReq: CreateIdeeReq,
        file: File?
    ): Flow<RepoResultResult<Idea?>>

    suspend fun getAllIdeas(): Flow<RepoResultResult<List<Idea>>>
    suspend fun getIdeaWithId(ideaId: String): Flow<RepoResultResult<Any>>
    suspend fun updateIdeaWithId(
        ideaId: String,
        createIdeeReq: CreateIdeeReq
    ): Flow<RepoResultResult<Nothing>>

    suspend fun deleteIdeaWithId(ideaId: String): Flow<RepoResultResult<Nothing>>
    suspend fun searchIdeal(searchText: String): Flow<RepoResultResult<List<Any>>>
    suspend fun releaseIdea(
        ideaId: String,
        releaseReq: IdeaReleaseReq
    ): Flow<RepoResultResult<Boolean>>

    suspend fun createComment(
        ideaId: String,
        createCommentReq: CreateCommentReq
    ): Flow<RepoResultResult<Boolean>>

    suspend fun getComments(ideaId: String): Flow<RepoResultResult<List<IdeaComment>>>
    suspend fun deleteComments(ideaId: String, commentId: String): Flow<RepoResultResult<Nothing>>
    suspend fun postRating(ideaId: String, postRating: PostRating): Flow<RepoResultResult<Nothing>>
    suspend fun deleteRating(ideaId: String): Flow<RepoResultResult<Nothing>>
    suspend fun uploadImageIdea(ideaId: String, path: String): Flow<RepoResultResult<Nothing>>
}
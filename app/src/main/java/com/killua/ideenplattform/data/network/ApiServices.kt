package com.killua.ideenplattform.data.network

import com.killua.ideenplattform.data.models.api.Idea
import com.killua.ideenplattform.data.models.api.IdeaComment
import com.killua.ideenplattform.data.models.local.CategoryCaching
import com.killua.ideenplattform.data.models.local.UserCaching
import com.killua.ideenplattform.data.requests.*
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiServices {
    @GET("user")
    suspend fun getAllUsers(): Response<ArrayList<UserCaching>>

    @POST("user")
    suspend fun createUser(@Body userCreateReq: UserCreateReq): Response<ResponseHandler>

    @PUT("user")
    suspend fun updateUser(@Body updateUser: UserCreateReq): Response<UserCaching>

    @GET("user/me")
    suspend fun getMe(): Response<UserCaching>

    @GET("user")
    suspend fun getUserId(@Query("userId") id: String): Response<UserCaching>

    //get photo od user and save it

    @Multipart
    @POST("user/image")
    suspend fun uploadUserImage(
        @Part() image: MultipartBody.Part
    ): Response<ResponseHandler>

    @DELETE("user/image")
    suspend fun deleteImageOfUser(): Response<ResponseHandler>

    @GET("user/{userId}/manager")
    suspend fun updateMangerStatus(
        @Path("userId") userId: String,
        @Body updateManagerStatus: UpdateManagerStatus
    ): Response<ResponseHandler>


    //category
    @GET("category")
    suspend fun getCategory(): Response<ArrayList<CategoryCaching>>

    @GET("category/")
    suspend fun getCategoryWithId(@Query("categoryId") categoryId: String): Response<CategoryCaching>

    @GET("idea")
    suspend fun getAllIdeas(@Query("categoryId") categoryId: String): Response<ArrayList<Idea>>

    //ideas api req
    @GET("idea")
    suspend fun getAllIdeas(): Response<List<Idea>>

    @Multipart
    @POST("idea")
    suspend fun createNewIdea(
        @Part("body") items: RequestBody,
        @Part image: MultipartBody.Part? = null
    ): Response<Idea>

    @GET("idea/{id}")
    suspend fun getIdeaWithId(@Path("id") ideaId: String): Response<Idea>

    @PUT("idea/")
    suspend fun updateIdeaWithId(
        @Query("ideaId") ideaId: String,
        @Body createIdeeReq: CreateIdeeReq
    ): Response<ResponseHandler>

    @DELETE("idea/")
    suspend fun deleteIdeaWithId(@Query("ideaId") ideaId: String): Response<ResponseHandler>

    @GET("idea/search")
    suspend fun searchIdeal(@Query("searchQuery") searchText: String): Response<ArrayList<Idea>>

    @POST("idea/{ideaId}/released")
    suspend fun releaseIdea(
        @Path("ideaId") ideaId: String,
        @Body releaseReq: IdeaReleaseReq
    ): Response<ResponseHandler>

    @POST("idea/{ideaId}/comment")
    suspend fun createComment(
        @Path("ideaId") ideaId: String,
        @Body createCommentReq: CreateCommentReq
    ): Response<ResponseHandler>

    @GET("idea/{ideaId}/")
    suspend fun getComments(@Path("ideaId") ideaId: String): Response<List<IdeaComment>>

    @DELETE("idea/{ideaId}/comment/{commentId}")
    suspend fun deleteComments(
        @Path("ideaId") ideaId: String,
        @Path("commentId") commentId: String
    ): Response<ResponseHandler>

    @POST("idea/{ideaId}/rating")
    suspend fun postRating(
        @Path("ideaId") ideaId: String,
        @Body postRating: PostRating
    ): Response<ResponseHandler>

    @DELETE("idea/{ideaId}/rating")
    suspend fun deleteRating(@Path("ideaId") ideaId: String): Response<ResponseHandler>

    @Multipart
    @POST("idea/{ideaId}/image")
    suspend fun uploadImageIdea(
        @Path("ideaId") ideaId: String,
        @Part image: MultipartBody.Part
    ): Response<ResponseHandler>
}

data class HttpClient(val okHttpClient: OkHttpClient)
package com.killua.ideenplattform.data.network

import com.killua.ideenplattform.data.models.api.*
import com.killua.ideenplattform.data.models.local.*
import com.killua.ideenplattform.data.requests.*
import okhttp3.*
import retrofit2.Response
import retrofit2.http.*

interface ApiServices {
    @GET("user")
    fun getAllUsers(): Response<ArrayList<UserCaching>>

    @POST("user")
    fun createUser(@Body userCreateReq: UserCreateReq): Response<ResponseHandler>

    @PUT("user")
    fun updateUser(@Body updateUser: UserCreateReq): Response<UserCaching>

    @GET("user/me")
    fun getMe(): Response<UserCaching>

    @GET("user/")
    fun getUserId(@Query("userId") id: String): Response<UserCaching>

    //get photo od user and save it

    @Multipart
    @POST("user/image")
    fun uploadUserImage(
        @Part("file\"; filename=\"pp.png\" ")image: MultipartBody.Part
    ):Response<ResponseHandler>

    @DELETE("user/image")
    fun deleteImageOfUser():Response<ResponseHandler>

    @GET("user/{userId}/manager")
    fun updateMangerStatus(
        @Path("userId") userId: String,
        @Body updateManagerStatus: UpdateManagerStatus
    ):Response<ResponseHandler>


    //category
    @GET("category")
    fun getCategory(): Response<ArrayList<CategoryCaching>>

    @GET("category/")
    fun getCategoryWithId(@Query("categoryId") categoryId: String): Response<CategoryCaching>


    //ideas api req
    @GET("idea")
    fun getAllIdeas(@QueryMap categoryId: String): Response<ArrayList<Idea>>

    @Multipart
    @POST("idea")
    fun createNewIdea(
        @Body createIdeeReq: CreateIdeeReq,
        @Part image: MultipartBody.Part
    ): Response<ResponseHandler>

    @GET("idea/")
    fun getIdeaWithId(@Query("ideaId") ideaId: String): Response<Idea>

    @PUT("idea/")
    fun updateIdeaWithId(@Query("ideaId") ideaId: String,@Body createIdeeReq: CreateIdeeReq): Response<ResponseHandler>

    @DELETE("idea/")
    fun deleteIdeaWithId(@Query("ideaId") ideaId: String): Response<ResponseHandler>

    @GET("idea/search")
    fun searchIdeal(@Query("searchQuery") searchText: String): Response<ArrayList<Idea>>

    @POST("idea/{ideaId}/released")
    fun releaseIdea(@Path("ideaId") ideaId: String, @Body releaseReq: IdeaReleaseReq): Response<ResponseHandler>

    @POST("idea/{ideaId}/comment")
    fun createComment(@Path("ideaId") ideaId: String, @Body createCommentReq: CreateCommentReq): Response<ResponseHandler>

    @GET("idea/{ideaId}/comment")
    fun getComments(@Path("ideaId") ideaId: String): Response<CommentList>

    @DELETE("idea/{ideaId}/comment/{commentId}")
    fun deleteComments(@Path("ideaId") ideaId: String, @Path("commentId") commentId: String): Response<ResponseHandler>

    @POST("idea/{ideaId}/rating")
    fun postRating(@Path("ideaId") ideaId: String,@Body postRating: PostRating): Response<ResponseHandler>

    @DELETE("idea/{ideaId}/rating")
    fun deleteRating(@Path("ideaId") ideaId: String): Response<ResponseHandler>

    @Multipart
    @POST("idea/{ideaId}/image")
    fun uploadImageIdea(
        @Path("ideaId") ideaId: String,
        @Part image: MultipartBody.Part
    ): Response<ResponseHandler>
}
data class HttpClient( val okHttpClient: OkHttpClient)
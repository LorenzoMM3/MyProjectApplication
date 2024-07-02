package com.example.myprojectapplication

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

data class SignUpRequest(val username: String, val password: String)
data class SignUpResponse(val username: String, val id: Int)
data class TokenResponse(val client_id: Int, val client_secret: String)
data class DeleteResponse(val detail: String)
data class ResponseMyUploads(val id: Int, val longitude: Double, val latitude: Double,  val hidden: Boolean, val uploaded: Boolean)
data class ResponseAllUploads(val id: Int, val longitude: Double, val latitude: Double)
data class ResponseMoreInfo(val longitude: Double, val latitude: Double, val id: Int, val creator_id: Int, val creator_username: String, val tags: Tags)
data class Tags(val bpm: Int, val danceability: Double, val loudness: Double, val mood: Map<String, Double>,val genre: Map<String, Double>, val instrument: Map<String, Double>)
data class ResponseHideFile(val detail: String)
data class ResponseShowFile(val detail: String)
data class ResponseDeleteFile(val detail: String)
data class ResponseUpload(val detail: String)

interface ApiService {
    @POST("auth")
    fun signUp(@Body request: SignUpRequest): Call<SignUpResponse>

    @FormUrlEncoded
    @POST("auth/token")
    fun getToken(@Field("username") username: String, @Field("password") password: String): Call<TokenResponse>

    @DELETE("auth/unsubscribe")
    fun deleteUser(@Header("Authorization") authHeader: String): Call<DeleteResponse>

    @GET("audio/my")
    fun seeMyUploads(@Header("Authorization") authHeader: String): Call<List<ResponseMyUploads>>

    @GET("audio/all")
    fun seeAllUploads(@Header("Authorization") authHeader: String): Call<List<ResponseAllUploads>>

    @GET("audio/{id}")
    fun seeMoreInfo(@Header("Authorization") authHeader: String, @Path("id") id:Int): Call<ResponseMoreInfo>

    @DELETE("audio/{id}")
    fun deleteFile(@Header("Authorization") authHeader: String, @Path("id") id:Int): Call<ResponseDeleteFile>

    @GET("audio/my/{id}/show")
    fun showFile(@Header("Authorization") authHeader: String, @Path("id") id:Int): Call<ResponseShowFile>

    @GET("audio/my/{id}/hide")
    fun hideFile(@Header("Authorization") authHeader: String, @Path("id") id:Int): Call<ResponseHideFile>

    @Multipart
    @POST("upload")
    fun uploadFile(
        @Header("Authorization") authHeader: String,
        @Query("longitude") longitude: Double,
        @Query("latitude") latitude: Double,
        @Part file: MultipartBody.Part
    ): Call<ResponseUpload>
}
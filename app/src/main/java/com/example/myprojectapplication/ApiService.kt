import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.POST

data class SignUpRequest(val username: String, val password: String)
data class SignUpResponse(val username: String, val id: Int)
data class TokenRequest(val username: String, val password: String)
data class TokenResponse(val client_id: Int, val client_secret: String)
data class DeleteResponse(val detail: String)


interface ApiService {
    @POST("auth")
    fun signUp(@Body request: SignUpRequest): Call<SignUpResponse>

    @FormUrlEncoded
    @POST("auth/token")
    fun getToken(@Field("username") username: String, @Field("password") password: String): Call<TokenResponse>

    @DELETE("auth/unsubscribe")
    fun deleteUser(@Header("Authorization") authHeader: String): Call<DeleteResponse>
}
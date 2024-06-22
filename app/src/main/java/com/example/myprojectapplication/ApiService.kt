import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

data class SignUpRequest(val username: String, val password: String)
data class SignUpResponse(val username: String, val id: Int)
data class TokenResponse(val client_id: Int, val client_secret: String)
data class DeleteResponse(val detail: String)
data class ResponseMyUploads(val id: Int, val longitude: Float, val latitude: Float,  val hidden: Boolean, val uploaded: Boolean)
data class ResponseAllUploads(val id: Int, val longitude: Float, val latitude: Float)
data class ResponseMoreInfo(val longitude: Float, val latitude: Float, val id: Int, val creator_id: Int, val creator_username: String, val tags: Tags)
data class Tags(val bpm: Int, val danceability: Float, val loudness: Float, val mood: Mood, val genre: Genre, val instrument: Instrument)
data class Mood(
    val action: Float,
    val adventure: Float,
    val advertising: Float,
    val background: Float,
    val ballad: Float,
    val calm: Float,
    val children: Float,
    val christmas: Float,
    val commercial: Float,
    val cool: Float,
    val corporate: Float,
    val dark: Float,
    val deep: Float,
    val documentary: Float,
    val drama: Float,
    val dramatic: Float,
    val dream: Float,
    val emotional: Float,
    val energetic: Float,
    val epic: Float,
    val fast: Float,
    val film: Float,
    val funn: Float,
    val funny: Float,
    val game: Float,
    val groovy: Float,
    val happy: Float,
    val heavy: Float,
    val holiday: Float,
    val hopeful: Float,
    val inspiring: Float,
    val love: Float,
    val meditative: Float,
    val melancholic: Float,
    val melodic: Float,
    val motivational: Float,
    val movie: Float,
    val nature: Float,
    val party: Float,
    val positive: Float,
    val powerful: Float,
    val relaxing: Float,
    val retro: Float,
    val romantic: Float,
    val sad: Float,
    val sexy: Float,
    val slow: Float,
    val soft: Float,
    val soundscape: Float,
    val space: Float,
    val sport: Float,
    val summer: Float,
    val trailer: Float,
    val travel: Float,
    val upbeat: Float,
    val uplifting: Float
)
data class Genre(
    val `60s`: Float,
    val `70s`: Float,
    val `80s`: Float,
    val `90s`: Float,
    val acidjazz: Float,
    val alternative: Float,
    val alternativerock: Float,
    val ambient: Float,
    val atmospheric: Float,
    val blues: Float,
    val bluesrock: Float,
    val bossanova: Float,
    val breakbeat: Float,
    val celtic: Float,
    val chanson: Float,
    val chillout: Float,
    val choir: Float,
    val classical: Float,
    val classicrock: Float,
    val club: Float,
    val contemporary: Float,
    val country: Float,
    val dance: Float,
    val darkambient: Float,
    val darkwave: Float,
    val deephouse: Float,
    val disco: Float,
    val downtempo: Float,
    val drumnbass: Float,
    val dub: Float,
    val dubstep: Float,
    val easylistening: Float,
    val edm: Float,
    val electronic: Float,
    val electronica: Float,
    val electropop: Float,
    val ethno: Float,
    val eurodance: Float,
    val experimental: Float,
    val folk: Float,
    val funk: Float,
    val fusion: Float,
    val groove: Float,
    val grunge: Float,
    val hard: Float,
    val hardrock: Float,
    val hiphop: Float,
    val house: Float,
    val idm: Float,
    val improvisation: Float,
    val indie: Float,
    val industrial: Float,
    val instrumentalpop: Float,
    val instrumentalrock: Float,
    val jazz: Float,
    val jazzfusion: Float,
    val latin: Float,
    val lounge: Float,
    val medieval: Float,
    val metal: Float,
    val minimal: Float,
    val newage: Float,
    val newwave: Float,
    val orchestral: Float,
    val pop: Float,
    val popfolk: Float,
    val poprock: Float,
    val postrock: Float,
    val progressive: Float,
    val psychedelic: Float,
    val punkrock: Float,
    val rap: Float,
    val reggae: Float,
    val rnb: Float,
    val rock: Float,
    val rocknroll: Float,
    val singersongwriter: Float,
    val soul: Float,
    val soundtrack: Float,
    val swing: Float,
    val symphonic: Float,
    val synthpop: Float,
    val techno: Float,
    val trance: Float,
    val triphop: Float,
    val world: Float,
    val worldfusion: Float
)
data class Instrument(
    val accordion: Float,
    val acousticbassguitar: Float,
    val acousticguitar: Float,
    val bass: Float,
    val beat: Float,
    val bell: Float,
    val bongo: Float,
    val brass: Float,
    val cello: Float,
    val clarinet: Float,
    val classicalguitar: Float,
    val computer: Float,
    val doublebass: Float,
    val drummachine: Float,
    val drums: Float,
    val electricguitar: Float,
    val electricpiano: Float,
    val flute: Float,
    val guitar: Float,
    val harmonica: Float,
    val harp: Float,
    val horn: Float,
    val keyboard: Float,
    val oboe: Float,
    val orchestra: Float,
    val organ: Float,
    val pad: Float,
    val percussion: Float,
    val piano: Float,
    val pipeorgan: Float,
    val rhodes: Float,
    val sampler: Float,
    val saxophone: Float,
    val strings: Float,
    val synthesizer: Float,
    val trombone: Float,
    val trumpet: Float,
    val viola: Float,
    val violin: Float,
    val voice: Float
)
data class ResponseHideFile(val detail: String)
data class ResponseShowFile(val detail: String)
data class ResponseDeleteFile(val detail: String)

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
    @DELETE("audio/my/{id}")
    fun deleteFile(@Header("Authorization") authHeader: String, @Path("id") id:Int): Call<ResponseDeleteFile>

    @GET("audio/my/{id}/show")
    fun showFile(@Header("Authorization") authHeader: String, @Path("id") id:Int): Call<ResponseShowFile>

    @GET("audio/my/{id}/hide")
    fun hideFile(@Header("Authorization") authHeader: String, @Path("id") id:Int): Call<ResponseHideFile>

}
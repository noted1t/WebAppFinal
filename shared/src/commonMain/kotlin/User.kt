import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("login") val login: String,
    @SerialName("username") val username: String,
    @SerialName("password") val password: String
)
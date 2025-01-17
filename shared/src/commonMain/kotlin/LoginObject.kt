import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginObject(
    @SerialName("login") val login: String,
    @SerialName("password") val password: String,
)
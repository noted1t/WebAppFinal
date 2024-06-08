import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.CanvasBasedWindow
import com.ptrby.webapp.App
import com.ptrby.webapp.di.network.Ktor.networkModule
import com.ptrby.webapp.di.settings.SettingsObject.settingsModule
import org.jetbrains.skiko.wasm.onWasmReady
import org.koin.core.context.startKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoin {
        modules(networkModule, settingsModule)
    }
    onWasmReady {
        CanvasBasedWindow("WebTaskApp") {
            App(Color.Green)
        }
    }
}

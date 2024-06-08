package com.ptrby.webapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.ptrby.webapp.base.mainscreen.MainScreen
import com.ptrby.webapp.theme.AppTheme
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory

@Composable
internal fun App(seedColor: Color) = AppTheme(seedColor) {
    Napier.base(DebugAntilog())
    MainScreen()
}

internal expect fun openUrl(url: String?)
internal expect fun provideKtorEngine(): HttpClientEngineFactory<HttpClientEngineConfig>
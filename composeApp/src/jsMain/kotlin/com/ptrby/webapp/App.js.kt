package com.ptrby.webapp

import io.ktor.client.engine.*
import io.ktor.client.engine.js.*
import kotlinx.browser.window

internal actual fun openUrl(url: String?) {
    url?.let { window.open(it) }
}

internal actual fun provideKtorEngine(): HttpClientEngineFactory<HttpClientEngineConfig> {
    return Js
}
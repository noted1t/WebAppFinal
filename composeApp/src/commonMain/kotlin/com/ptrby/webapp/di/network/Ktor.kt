package com.ptrby.webapp.di.network

import com.ptrby.webapp.di.settings.SettingsRepository
import com.ptrby.webapp.provideKtorEngine
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module

object Ktor {
    val networkModule = module {
        single {
            HttpClient(provideKtorEngine()) {
                expectSuccess = true
                install(ContentNegotiation) {
                    json(
                        Json {
                            isLenient = true
                            ignoreUnknownKeys = true
                            prettyPrint = true
                        }
                    )
                }
                defaultRequest {
                    if (get<SettingsRepository>().contains("login_key")) {
                        header("login_key", get<SettingsRepository>().getValue("login_key")!!)
                    }
                }
            }
        }

        single {
            KtorRepository(get<HttpClient>(), get<SettingsRepository>())
        }
    }
}

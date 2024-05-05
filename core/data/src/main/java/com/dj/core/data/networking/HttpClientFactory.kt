package com.dj.core.data.networking

import com.dj.core.data.BuildConfig
import com.dj.core.domain.AuthInfo
import com.dj.core.domain.SessionStorage
import com.dj.core.domain.util.Result
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import timber.log.Timber

class HttpClientFactory(
    private val sessionStorage: SessionStorage
) {

    fun build(): HttpClient {
        return HttpClient(
            engineFactory = CIO
        ) {
            install(plugin = ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
            install(plugin = Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Timber.d(message)
                    }
                }
                level = LogLevel.ALL
            }
            defaultRequest {
                contentType(type = ContentType.Application.Json)
                header(key = "x-api-key", BuildConfig.API_KEY)
            }

            install(plugin = Auth) {
                bearer {
                    loadTokens {
                        val info = sessionStorage.get()
                        BearerTokens(
                            accessToken = info?.accessToken ?: "",
                            refreshToken = info?.refreshToken ?: ""
                        )
                    }
                    refreshTokens {
                        val info = sessionStorage.get()
                        val response = client.post<AccessTokenRequest, AccessTokenResponse>(
                            route = "/accessToken",
                            body = AccessTokenRequest(
                                refreshToken = info?.refreshToken ?: "",
                                userId = info?.userId ?: ""
                            )
                        )
                        when (response) {
                            is Result.Success -> {
                                val newAuthInfo = AuthInfo(
                                    accessToken = response.data.accessToken,
                                    refreshToken = info?.refreshToken ?: "",
                                    userId = info?.userId ?: ""
                                )
                                sessionStorage.set(newAuthInfo)
                                BearerTokens(
                                    accessToken = newAuthInfo.accessToken,
                                    refreshToken = newAuthInfo.refreshToken
                                )
                            }

                            is Result.Error -> {
                                BearerTokens(
                                    accessToken = "",
                                    refreshToken = ""
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
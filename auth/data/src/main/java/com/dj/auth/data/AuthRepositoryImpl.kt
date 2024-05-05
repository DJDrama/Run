package com.dj.auth.data

import com.dj.auth.domain.AuthRepository
import com.dj.core.data.networking.post
import com.dj.core.domain.AuthInfo
import com.dj.core.domain.SessionStorage
import com.dj.core.domain.util.DataError
import com.dj.core.domain.util.EmptyResult
import com.dj.core.domain.util.Result
import com.dj.core.domain.util.asEmptyDataResult
import io.ktor.client.HttpClient

class AuthRepositoryImpl(
    private val httpClient: HttpClient,
    private val sessionStorage: SessionStorage
) : AuthRepository {
    override suspend fun login(email: String, password: String): EmptyResult<DataError.Network> {
        val result = httpClient.post<LoginRequest, LoginResponse>(
            route = "/login",
            body = LoginRequest(email = email, password = password)
        )
       if(result is Result.Success)
            sessionStorage.set(
                AuthInfo(
                    accessToken = result.data.accessToken,
                    refreshToken = result.data.refreshToken,
                    userId = result.data.userId
                )
            )
        return result.asEmptyDataResult()
    }

    override suspend fun register(email: String, password: String): EmptyResult<DataError.Network> {
        return httpClient.post<RegisterRequest, Unit>(
            route = "/register",
            body = RegisterRequest(email = email, password = password)
        )
    }
}
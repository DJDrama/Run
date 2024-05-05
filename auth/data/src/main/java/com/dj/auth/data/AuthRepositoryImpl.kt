package com.dj.auth.data

import com.dj.auth.domain.AuthRepository
import com.dj.core.data.networking.post
import com.dj.core.domain.util.DataError
import com.dj.core.domain.util.EmptyResult
import io.ktor.client.HttpClient

class AuthRepositoryImpl
constructor(
    private val httpClient: HttpClient
) : AuthRepository {
    override suspend fun register(email: String, password: String): EmptyResult<DataError.Network> {
        return httpClient.post<RegisterRequest, Unit>(
            route = "/register",
            body = RegisterRequest(email = email, password = password)
        )
    }
}
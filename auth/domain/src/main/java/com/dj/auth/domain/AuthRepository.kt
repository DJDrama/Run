package com.dj.auth.domain

import com.dj.core.domain.util.DataError
import com.dj.core.domain.util.EmptyResult

interface AuthRepository {
    suspend fun register(email: String, password: String): EmptyResult<DataError.Network>
}
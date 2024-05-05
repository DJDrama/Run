package com.dj.core.data.auth

import com.dj.core.domain.AuthInfo

fun AuthInfo.toAuthInfoSerializable(): AuthInfoSerializable {
    return AuthInfoSerializable(
        accessToken = accessToken, refreshToken = refreshToken, userId = userId
    )
}

fun AuthInfoSerializable.toAuthInfo(): AuthInfo {
    return AuthInfo(accessToken = accessToken, refreshToken = refreshToken, userId = userId)
}
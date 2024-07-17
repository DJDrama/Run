package com.dj.android_test

import com.dj.core.domain.AuthInfo
import com.dj.core.domain.SessionStorage

class SessionStorageFake : SessionStorage {

    private var authInfo: AuthInfo? = null

    override suspend fun get(): AuthInfo? {
        return this.authInfo
    }

    override suspend fun set(info: AuthInfo?) {
        this.authInfo = info
    }
}
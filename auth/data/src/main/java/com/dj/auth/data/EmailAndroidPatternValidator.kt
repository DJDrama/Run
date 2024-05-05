package com.dj.auth.data

import android.util.Patterns
import com.dj.auth.domain.PatternValidator

object EmailAndroidPatternValidator: PatternValidator  {
    override fun matches(value: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(value).matches()
    }
}
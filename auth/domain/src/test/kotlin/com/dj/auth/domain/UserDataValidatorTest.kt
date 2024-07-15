package com.dj.auth.domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class UserDataValidatorTest {

    private lateinit var userDataValidator: UserDataValidator
    private lateinit var patternValidator: PatternValidator

    @BeforeEach
    fun setup() {
        userDataValidator = UserDataValidator(
            // test double
            patternValidator = object : PatternValidator {
                override fun matches(value: String): Boolean {
                    return true
                }
            }
        )
    }

    /** POINTLESS TEST!
     * -> UserDataValidator depends on patternValidator which always returns true
     * **/
    @Test
    fun testValidateEmail() {
        val email = "test@test.com"

        val isValid = userDataValidator.isValidEmail(email = email)
        assertThat(isValid).isTrue()
    }

    /** Password validation test **/
    @ParameterizedTest
    @CsvSource(
        "Test12345, true",
        "test12345, false",
        "12345, false",
        "Test-1234, true",
        "Test, false",
        "0, false",
        "., false",
    )
    fun validatePassword(password: String, expectedIsValid: Boolean) {
        val state = userDataValidator.validatePassword(password = password)
        assertThat(actual = state.isValidPassword)
            .isEqualTo(expected = expectedIsValid)
    }
}
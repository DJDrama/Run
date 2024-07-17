package com.dj.auth.presentation.login

import androidx.compose.foundation.ExperimentalFoundationApi
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import com.dj.android_test.SessionStorageFake
import com.dj.android_test.TestMockEngine
import com.dj.android_test.loginResponseStub
import com.dj.auth.data.AuthRepositoryImpl
import com.dj.auth.data.EmailAndroidPatternValidator
import com.dj.auth.data.LoginRequest
import com.dj.auth.domain.UserDataValidator
import com.dj.core.data.networking.HttpClientFactory
import com.dj.test.MainCoroutineExtension
import io.ktor.client.engine.mock.MockEngineConfig
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.http.HttpStatusCode
import io.ktor.http.headers
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class LoginViewModelTest {

    companion object {
        @JvmField
        @RegisterExtension
        val mainCoroutineExtension = MainCoroutineExtension()
    }

    private lateinit var viewModel: LoginViewModel

    // Integrated test : no need to make Fake repository
    private lateinit var repository: AuthRepositoryImpl
    private lateinit var sessionStorageFake: SessionStorageFake
    private lateinit var mockEngine: TestMockEngine

    @BeforeEach
    fun setup() {
        sessionStorageFake = SessionStorageFake()

        val mockEngineConfig = MockEngineConfig().apply {
            requestHandlers.add { request ->
                val relativeUrl = request.url.encodedPath
                if (relativeUrl == "/login") {
                    respond(
                        content = ByteReadChannel(
                            text = Json.encodeToString(loginResponseStub)
                        ),
                        headers = headers {
                            set("Content-Type", "application/json")
                        }
                    )
                } else {
                    respond(
                        content = byteArrayOf(),
                        status = HttpStatusCode.InternalServerError
                    )
                }
            }
        }
        mockEngine = TestMockEngine(
            dispatcher = mainCoroutineExtension.testDispatcher,
            mockEngineConfig = mockEngineConfig
        )
        val httpClient = HttpClientFactory(
            sessionStorage = sessionStorageFake,
        ).build(engine = mockEngine)
        repository = AuthRepositoryImpl(
            httpClient = httpClient,
            sessionStorage = sessionStorageFake
        )

        viewModel = LoginViewModel(
            authRepository = repository,
            userDataValidator = UserDataValidator(
                patternValidator = EmailAndroidPatternValidator
            )
        )
    }

    @ExperimentalFoundationApi
    @Test
    fun testLogin() = runTest {
        assertThat(actual = viewModel.state.canLogin).isFalse()

        viewModel.state.email.edit {
            append("test@test.com")
        }
        viewModel.state.password.edit {
            append("Test12345")
        }

        viewModel.onAction(action = LoginAction.OnLoginClick)

        assertThat(viewModel.state.isLoggingIn).isFalse()
        assertThat(viewModel.state.email.text.toString()).isEqualTo("test@test.com")
        assertThat(viewModel.state.password.text.toString()).isEqualTo("Test12345")

        val loginRequest = mockEngine.mockEngine.requestHistory.find {
            it.url.encodedPath =="/login"
        }
        assertThat(loginRequest).isNotNull()
        assertThat(loginRequest!!.headers.contains("x-api-key")).isTrue()

        val loginBody = Json.decodeFromString<LoginRequest>(
            loginRequest.body.toByteArray().decodeToString()
        )
        assertThat(loginBody.email).isEqualTo("test@test.com")
        assertThat(loginBody.password).isEqualTo("Test12345")

        val session = sessionStorageFake.get()
        assertThat(session?.userId).isEqualTo(other = loginResponseStub.userId)
        assertThat(session?.accessToken).isEqualTo(other = loginResponseStub.accessToken)
        assertThat(session?.refreshToken).isEqualTo(other = loginResponseStub.refreshToken)

    }

}
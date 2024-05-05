package com.dj.core.domain.util

sealed interface Result<out D, out E : Error> {
    data class Success<out D>(val data: D) : Result<D, Nothing>

    data class Error<out E : com.dj.core.domain.util.Error>(val error: E) : Result<Nothing, E>

}

inline fun <T, E : Error, R> Result<T, E>.map(map: (T) -> R): Result<R, E> {
    return when (this) {
        is Result.Success -> Result.Success(data = map(data))
        is Result.Error -> Result.Error(error = error)
    }
}

fun <T, E: Error> Result<T, E>.asEmptyDataResult(): EmptyResult<E> {
    return map {

    }
}

typealias EmptyResult<E> = Result<Unit, E>
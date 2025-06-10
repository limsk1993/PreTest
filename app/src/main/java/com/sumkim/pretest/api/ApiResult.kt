package com.sumkim.pretest.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import retrofit2.awaitResponse
import java.io.Serializable

@JvmInline
value class ApiResult<out T> @PublishedApi internal constructor(
    @PublishedApi
    internal val value: Any?
) {
    val isSuccess: Boolean get() = !(value is Errors || value is Exception)
    val isErrors: Boolean get() = value is Errors
    val isException: Boolean get() = value is Exception

    fun getOrNull(): T? = when {
        isSuccess -> value as T
        else -> null
    }

    fun errorsOrNull(): String? = when (value) {
        is Errors -> value.errors
        else -> null
    }

    fun exceptionOrNull(): Throwable? = when (value) {
        is Exception -> value.exception
        else -> null
    }

    fun throwOnUnSuccessful() {
        if (value is Exception) throw value.exception
        else if (value is Errors) throw ApiErrorException(value.errors)
    }

    override fun toString(): String = when (value) {
        is Errors, is Exception -> value.toString()
        else -> "Success($value)"
    }

    companion object {
        fun <T> success(value: T): ApiResult<T> = ApiResult(value)
        fun <T> error(value: String): ApiResult<T> = ApiResult(Errors(value))
        fun <T> exception(value: Throwable): ApiResult<T> = ApiResult(Exception(value))
    }

    internal class Errors(
        @JvmField
        val errors: String
    ) : Serializable {
        override fun equals(other: Any?): Boolean = other is Errors && errors == other.errors
        override fun hashCode(): Int = errors.hashCode()
        override fun toString(): String = "Errors($errors)"
    }

    internal class Exception(
        @JvmField
        val exception: Throwable
    ) : Serializable {
        override fun equals(other: Any?): Boolean = other is Exception && exception == other.exception
        override fun hashCode(): Int = exception.hashCode()
        override fun toString(): String = "Exception($exception)"
    }
}

class ApiErrorException(errors: String?) : Exception("API response contains error")

inline fun <T> ApiResult<T>.onSuccess(action: (value: T) -> Unit): ApiResult<T> {
    if (isSuccess) action(value as T)
    return this
}

inline fun <T> ApiResult<T>.onError(action: (value: String) -> Unit): ApiResult<T> {
    errorsOrNull()?.let { action(it) }
    return this
}

inline fun <T> ApiResult<T>.onException(action: (value: Throwable) -> Unit): ApiResult<T> {
    exceptionOrNull()?.let { action(it) }
    return this
}

fun <T> ApiResult<T>.get(): T {
    throwOnUnSuccessful()
    return value as T
}

suspend fun <T> requestApi(
    call: Call<T>,
    onResponse: ((Response<T>) -> Unit)? = null
): ApiResult<T> = try {
    val response = withContext(Dispatchers.IO) {
        call.awaitResponse()
    }
    onResponse?.invoke(response)

    val body = response.body()
    if (body != null && response.isSuccessful) {
        ApiResult.success(body)
    } else {
        ApiResult.error(response.message())
    }
} catch (e: Exception) {
    ApiResult.exception(e)
}
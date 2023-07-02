package ru.netology.nework.core.domain

import kotlinx.serialization.SerializationException
import okio.IOException
import retrofit2.HttpException
import ru.netology.nework.core.utils.Resource
import java.net.SocketTimeoutException

open class AppExceptions(
    message: String = "",
    cause: Throwable? = null
) : Exception(message, cause)

class ConnectionException(
    cause: Throwable
) : AppExceptions(cause = cause)

class NetworkException(
    cause: Throwable
) : AppExceptions()

class BackendException(
    val code: Int,
    message: String
) : AppExceptions(message)

class ParseBackendResponseException(
    cause: Throwable
) : AppExceptions(cause = cause)

class InternalUnknownError(
    cause: Throwable
) : AppExceptions(cause = cause)

suspend fun <T> wrapServerException(block: suspend () -> T): Resource<T> {
    return try {
        Resource.Success(block.invoke())
    } catch (e: SerializationException) {
        Resource.Error(ParseBackendResponseException(e))
    } catch (e: HttpException) {
        Resource.Error(createBackendException(e))
    } catch (e: SocketTimeoutException) {
        Resource.Error(ConnectionException(e))
    } catch (e: IOException) {
        Resource.Error(NetworkException(e))
    } catch (e: Exception) {
        Resource.Error(InternalUnknownError(e))
    }
}

private fun createBackendException(e: HttpException): AppExceptions {
    return try {
        val errorBody = e.response()?.errorBody()?.string()
            ?: throw IllegalStateException("errorBody HttpException was null")
        BackendException(e.code(), errorBody)
    } catch (e: SerializationException) {
        throw ParseBackendResponseException(e)
    } catch (e: Exception) {
        throw InternalUnknownError(e)
    }
}
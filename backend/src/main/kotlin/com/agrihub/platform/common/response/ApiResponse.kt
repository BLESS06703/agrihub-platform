package com.agrihub.platform.common.response

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean = true,
    val data: T? = null,
    val meta: PaginationMeta? = null,
    val errors: List<ApiError>? = null,
    val timestamp: String = java.time.Instant.now().toString()
)

@Serializable
data class PaginationMeta(
    val page: Int,
    val perPage: Int,
    val total: Long,
    val totalPages: Int
)

@Serializable
data class ApiError(
    val code: String,
    val field: String? = null,
    val message: String,
    val details: Map<String, String>? = null
)

fun <T> success(data: T, meta: PaginationMeta? = null): ApiResponse<T> {
    return ApiResponse(success = true, data = data, meta = meta)
}

fun error(code: String, message: String, field: String? = null): ApiResponse<Nothing> {
    return ApiResponse(
        success = false,
        errors = listOf(ApiError(code = code, field = field, message = message))
    )
}

fun notFound(resource: String, id: String): ApiResponse<Nothing> {
    return error("NOT_FOUND", "$resource with id '$id' not found")
}

fun validationError(errors: List<ApiError>): ApiResponse<Nothing> {
    return ApiResponse(success = false, errors = errors)
}

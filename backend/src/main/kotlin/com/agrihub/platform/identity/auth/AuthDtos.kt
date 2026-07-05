package com.agrihub.platform.identity.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val user: UserSummary
)

@Serializable
data class RefreshRequest(
    val refreshToken: String
)

@Serializable
data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)

@Serializable
data class ForgotPasswordRequest(
    val email: String
)

@Serializable
data class ResetPasswordRequest(
    val token: String,
    val newPassword: String
)

@Serializable
data class UserSummary(
    val id: String,
    val email: String,
    val fullName: String,
    val tenantId: String,
    val tenantName: String,
    val roles: List<String>
)

@Serializable
data class AuthResponse(
    val success: Boolean = true,
    val data: LoginResponse? = null,
    val errors: List<ErrorDetail>? = null,
    val timestamp: String = java.time.Instant.now().toString()
)

@Serializable
data class ErrorDetail(
    val code: String,
    val message: String
)

package com.agrihub.platform.security.auth.password

import de.mkammerer.argon2.Argon2Factory

object PasswordService {
    
    private val argon2 = Argon2Factory.create(
        Argon2Factory.Argon2Types.ARGON2id,
        32, 64
    )
    
    private val iterations = 3
    private val memory = 65536
    private val parallelism = 4
    
    fun hash(password: String): String {
        validatePassword(password)
        return argon2.hash(iterations, memory, parallelism, password.toCharArray())
    }
    
    fun verify(hash: String, password: String): Boolean {
        return try {
            argon2.verify(hash, password.toCharArray())
        } catch (e: Exception) {
            false
        }
    }
    
    fun needsRehash(hash: String): Boolean {
        return try {
            !argon2.verify(hash, "dummy".toCharArray())
        } catch (e: Exception) {
            false
        }
    }
    
    private fun validatePassword(password: String) {
        require(password.length >= 8) { "Password must be at least 8 characters" }
        require(password.any { it.isUpperCase() }) { "Password must contain an uppercase letter" }
        require(password.any { it.isLowerCase() }) { "Password must contain a lowercase letter" }
        require(password.any { it.isDigit() }) { "Password must contain a digit" }
        require(password.any { !it.isLetterOrDigit() }) { "Password must contain a special character" }
    }
}

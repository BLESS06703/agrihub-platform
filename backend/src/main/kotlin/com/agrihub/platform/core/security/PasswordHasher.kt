package com.agrihub.platform.core.security

import de.mkammerer.argon2.Argon2Factory

object PasswordHasher {
    private val argon2 = Argon2Factory.create(
        Argon2Factory.Argon2Types.ARGON2id,
        32,
        64
    )
    
    fun hash(password: String): String {
        val iterations = 3
        val memory = 65536  // 64 MB
        val parallelism = 4
        return argon2.hash(iterations, memory, parallelism, password.toCharArray())
    }
    
    fun verify(hash: String, password: String): Boolean {
        return try {
            argon2.verify(hash, password.toCharArray())
        } catch (e: Exception) {
            false
        }
    }
}

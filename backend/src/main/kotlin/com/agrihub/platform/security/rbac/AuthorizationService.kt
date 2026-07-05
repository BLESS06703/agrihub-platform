package com.agrihub.platform.security.rbac

data class Permission(
    val resource: String,
    val action: String,
    val scope: String
) {
    val code: String get() = "$resource:$action:$scope"
    
    companion object {
        fun fromCode(code: String): Permission {
            val parts = code.split(":")
            require(parts.size == 3) { "Invalid permission format: $code" }
            return Permission(parts[0], parts[1], parts[2])
        }
    }
}

class AuthorizationService {
    
    fun hasPermission(userPermissions: List<String>, required: String): Boolean {
        if (userPermissions.contains("*:*:*")) return true
        
        val requiredPerm = Permission.fromCode(required)
        
        return userPermissions.any { perm ->
            val userPerm = Permission.fromCode(perm)
            
            resourceMatch(userPerm.resource, requiredPerm.resource) &&
            actionMatch(userPerm.action, requiredPerm.action) &&
            scopeMatch(userPerm.scope, requiredPerm.scope)
        }
    }
    
    fun hasAnyPermission(userPermissions: List<String>, required: List<String>): Boolean {
        return required.any { hasPermission(userPermissions, it) }
    }
    
    fun hasAllPermissions(userPermissions: List<String>, required: List<String>): Boolean {
        return required.all { hasPermission(userPermissions, it) }
    }
    
    private fun resourceMatch(userResource: String, required: String): Boolean {
        return userResource == "*" || userResource == required
    }
    
    private fun actionMatch(userAction: String, required: String): Boolean {
        if (userAction == "*") return true
        if (userAction == "manage") return true
        if (userAction == required) return true
        if (userAction == "read" && required == "read") return true
        if (userAction == "create" && required == "create") return true
        return false
    }
    
    private fun scopeMatch(userScope: String, required: String): Boolean {
        return when (userScope) {
            "all" -> true
            "tenant" -> required == "own" || required == "tenant"
            "own" -> required == "own"
            else -> false
        }
    }
    
    fun getDefaultPermissionsForRole(role: String): List<String> {
        return when (role) {
            "TENANT_OWNER" -> listOf("*:*:tenant")
            "FARM_MANAGER" -> listOf(
                "farm:*:tenant", "field:*:tenant", "crop:*:tenant",
                "harvest:*:tenant", "livestock:*:tenant",
                "inventory:*:tenant", "finance:read:tenant"
            )
            "FARMER" -> listOf(
                "farm:read:own", "field:read:own",
                "crop:create:own", "crop:read:own",
                "harvest:create:own", "harvest:read:own"
            )
            "ACCOUNTANT" -> listOf(
                "finance:*:tenant", "report:read:tenant", "report:export:tenant"
            )
            "VIEWER" -> listOf(
                "farm:read:tenant", "field:read:tenant",
                "crop:read:tenant", "harvest:read:tenant",
                "livestock:read:tenant", "finance:read:tenant"
            )
            else -> emptyList()
        }
    }
}

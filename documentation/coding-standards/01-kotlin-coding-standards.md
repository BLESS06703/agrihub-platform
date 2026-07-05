# AgriHub Malawi - Kotlin Coding Standards
# Version 1.0

## 1. Package Naming

Pattern: com.agrihub.{module}.{layer}

Examples:
com.agrihub.farm.api
com.agrihub.farm.service
com.agrihub.farm.repository
com.agrihub.farm.model
com.agrihub.farm.dto

com.agrihub.core.security
com.agrihub.core.tenant
com.agrihub.core.audit

## 2. File Naming

- One class per file (except sealed classes)
- File name matches primary class name
- Extensions: .kt (Kotlin), .sql (migrations)

Examples:
FarmService.kt
FarmRepository.kt
FarmDto.kt
CreateFarmRequest.kt
FarmResponse.kt

## 3. Class Naming

| Layer | Pattern | Example |
|-------|---------|---------|
| Service | {Entity}Service | FarmService |
| Repository | {Entity}Repository | FarmRepository |
| Controller/Route | {Entity}Routes | FarmRoutes |
| Domain Model | {Entity} | Farm, Field, Crop |
| DTO Request | {Action}{Entity}Request | CreateFarmRequest |
| DTO Response | {Entity}Response | FarmResponse |
| Exception | {Entity}{Error}Exception | FarmNotFoundException |

## 4. Method Naming

Services:
- get{Entity}(id): Get single
- list{Entity}s(filters): List with filters
- create{Entity}(request): Create
- update{Entity}(id, request): Update
- delete{Entity}(id): Soft delete

Repositories:
- findById(id): Single or null
- findAll(page, filters): Paginated list
- save(entity): Insert or update
- softDelete(id): Mark deleted

## 5. DTO Conventions

Request DTOs:
@Serializable
data class CreateFarmRequest(
    val name: String,
    val areaHectares: Double?,
    val latitude: Double?,
    val longitude: Double?,
    val soilType: String?,
    val waterSource: String?
)

Response DTOs:
@Serializable
data class FarmResponse(
    val id: String,
    val name: String,
    val areaHectares: Double?,
    val status: String,
    val fieldCount: Int,
    val createdAt: String
)

## 6. Service Conventions

class FarmService(
    private val farmRepository: FarmRepository,
    private val auditService: AuditService
) {
    suspend fun createFarm(request: CreateFarmRequest): FarmResponse {
        // 1. Validate
        // 2. Business logic
        // 3. Save
        // 4. Audit
        // 5. Return DTO
    }
}

## 7. Repository Conventions

class FarmRepository {
    suspend fun findAll(
        page: Int, 
        perPage: Int,
        filters: FarmFilters
    ): PaginatedResult<Farm> {
        return transaction {
            FarmTable
                .selectAll()
                .where { /* filters */ }
                .paginate(page, perPage)
                .map { it.toFarm() }
        }
    }
}

## 8. Exception Handling

Custom exceptions:
class FarmNotFoundException(id: UUID) 
    : NotFoundException("Farm not found: $id")

class DuplicateFarmException(name: String) 
    : ConflictException("Farm '$name' already exists")

class InsufficientPermissionException(permission: String) 
    : ForbiddenException("Missing permission: $permission")

Always use these, never throw generic Exception.

## 9. Logging Standards

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(FarmService::class.java)

Levels:
- DEBUG: Detailed flow (development only)
- INFO: Business events (farm created, harvest recorded)
- WARN: Recoverable issues (retry, fallback used)
- ERROR: Failures requiring attention

Format:
logger.info("Farm created: id={}, name={}, tenant={}", farmId, name, tenantId)

## 10. Dependency Injection

Use constructor injection (Koin):
class FarmService(
    private val farmRepository: FarmRepository,
    private val auditService: AuditService
)

Module declaration:
val farmModule = module {
    single { FarmRepository() }
    single { FarmService(get(), get()) }
    single { FarmRoutes(get()) }
}

## 11. Testing Conventions

Test class: {Class}Test
Test method: should_{expected_behavior}_when_{condition}

Example:
class FarmServiceTest {
    @Test
    fun `should return farm when valid id provided`() { ... }
    
    @Test
    fun `should throw NotFoundException when farm id does not exist`() { ... }
}

## 12. Null Safety

- Never use !! in production code
- Use ?.let, ?:, or requireNotNull with message
- Prefer lateinit var only in tests

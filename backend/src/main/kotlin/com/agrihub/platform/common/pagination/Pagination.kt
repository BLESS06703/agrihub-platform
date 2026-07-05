package com.agrihub.platform.common.pagination

import com.agrihub.platform.common.response.PaginationMeta

data class PaginationRequest(
    val page: Int = 1,
    val perPage: Int = 20
) {
    val offset: Long get() = ((page - 1) * perPage).toLong()
    
    init {
        require(page >= 1) { "Page must be >= 1" }
        require(perPage in 1..100) { "perPage must be between 1 and 100" }
    }
}

fun calculateMeta(page: Int, perPage: Int, total: Long): PaginationMeta {
    val totalPages = if (total == 0L) 0 else ((total + perPage - 1) / perPage).toInt()
    return PaginationMeta(
        page = page,
        perPage = perPage,
        total = total,
        totalPages = totalPages
    )
}

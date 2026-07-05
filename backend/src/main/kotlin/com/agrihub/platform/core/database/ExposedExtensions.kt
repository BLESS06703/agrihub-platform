package com.agrihub.platform.core.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

fun <T> paginatedQuery(
    page: Int,
    perPage: Int,
    query: Query
): Pair<List<ResultRow>, Long> {
    val total = query.count()
    val offset = ((page - 1) * perPage).toLong()
    val results = query.limit(perPage, offset).toList()
    return results to total
}

fun Query.activeOnly(): Query {
    return this.andWhere { 
        this@activeOnly.set
            .source
            .columns
            .find { it.name == "deleted_at" }!!
            .isNull()
    }
}

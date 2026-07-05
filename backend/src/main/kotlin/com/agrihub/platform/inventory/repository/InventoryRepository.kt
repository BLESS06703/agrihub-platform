package com.agrihub.platform.inventory.repository
import com.agrihub.platform.core.database.BaseTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.UUID

object ItemsTable : BaseTable("inventory_items") {
    val itemName = varchar("item_name", 255)
    val category = varchar("category", 50)
    val unit = varchar("unit", 20)
    val stockOnHand = decimal("stock_on_hand", 10, 2).default(0.toBigDecimal())
    val minimumStock = decimal("minimum_stock", 10, 2).default(0.toBigDecimal())
    val unitCost = decimal("unit_cost", 10, 2).nullable()
}

object StockTransactionsTable : BaseTable("stock_transactions") {
    val itemId = uuid("item_id")
    val direction = varchar("direction", 20)
    val quantity = decimal("quantity", 10, 2)
    val reason = varchar("reason", 50)
    val transactionDate = date("transaction_date")
}

class InventoryRepository {
    data class Item(val id: UUID, val itemName: String, val category: String, val stockOnHand: Double)
    data class Transaction(val id: UUID, val direction: String, val quantity: Double, val reason: String)
    
    fun findAll(tenantId: UUID, category: String? = null): List<Item> = transaction {
        var q = ItemsTable.selectAll().where { (ItemsTable.tenantId eq tenantId) and (ItemsTable.deletedAt.isNull()) }
        category?.let { q = q.andWhere { ItemsTable.category eq it } }
        q.map { Item(it[ItemsTable.id], it[ItemsTable.itemName], it[ItemsTable.category], it[ItemsTable.stockOnHand].toDouble()) }
    }
    
    fun create(tenantId: UUID, itemName: String, category: String, unit: String, createdBy: UUID): Item = transaction {
        val id = UUID.randomUUID()
        ItemsTable.insert {
            it[ItemsTable.id] = id; it[ItemsTable.tenantId] = tenantId; it[ItemsTable.itemName] = itemName
            it[ItemsTable.category] = category; it[ItemsTable.unit] = unit; it[ItemsTable.createdBy] = createdBy
            it[ItemsTable.createdAt] = Instant.now()
        }
        Item(id, itemName, category, 0.0)
    }
    
    fun recordTransaction(tenantId: UUID, itemId: UUID, direction: String, quantity: Double, reason: String, createdBy: UUID): Transaction = transaction {
        val id = UUID.randomUUID()
        StockTransactionsTable.insert {
            it[StockTransactionsTable.id] = id; it[StockTransactionsTable.tenantId] = tenantId
            it[StockTransactionsTable.itemId] = itemId; it[StockTransactionsTable.direction] = direction
            it[StockTransactionsTable.quantity] = quantity.toBigDecimal(); it[StockTransactionsTable.reason] = reason
            it[StockTransactionsTable.transactionDate] = java.time.LocalDate.now()
            it[StockTransactionsTable.createdBy] = createdBy; it[StockTransactionsTable.createdAt] = Instant.now()
        }
        val delta = if (direction == "IN") quantity else -quantity
        ItemsTable.update({ (ItemsTable.id eq itemId) and (ItemsTable.tenantId eq tenantId) }) {
            with(SqlExpressionBuilder) { it.update(stockOnHand, stockOnHand + delta.toBigDecimal()) }
        }
        Transaction(id, direction, quantity, reason)
    }
}

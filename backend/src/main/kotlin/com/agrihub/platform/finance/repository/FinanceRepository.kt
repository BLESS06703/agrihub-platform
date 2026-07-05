package com.agrihub.platform.finance.repository
import com.agrihub.platform.core.database.BaseTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.UUID

object IncomeTable : BaseTable("income_records") {
    val amount = decimal("amount", 12, 2); val source = varchar("source", 50)
    val transactionDate = date("transaction_date"); val paymentMethod = varchar("payment_method", 50)
    val paymentStatus = varchar("payment_status", 50).default("COMPLETED")
}

object ExpenseTable : BaseTable("expense_records") {
    val amount = decimal("amount", 12, 2); val category = varchar("category", 50)
    val transactionDate = date("transaction_date"); val paymentMethod = varchar("payment_method", 50)
}

class FinanceRepository {
    data class Record(val id: UUID, val amount: Double, val date: String, val category: String)
    
    fun recordIncome(tenantId: UUID, amount: Double, source: String, date: String, method: String, createdBy: UUID): Record = transaction {
        val id = UUID.randomUUID()
        IncomeTable.insert {
            it[IncomeTable.id] = id; it[IncomeTable.tenantId] = tenantId; it[IncomeTable.amount] = amount.toBigDecimal()
            it[IncomeTable.source] = source; it[IncomeTable.transactionDate] = java.time.LocalDate.parse(date)
            it[IncomeTable.paymentMethod] = method; it[IncomeTable.createdBy] = createdBy; it[IncomeTable.createdAt] = Instant.now()
        }
        Record(id, amount, date, source)
    }
    
    fun recordExpense(tenantId: UUID, amount: Double, category: String, date: String, method: String, createdBy: UUID): Record = transaction {
        val id = UUID.randomUUID()
        ExpenseTable.insert {
            it[ExpenseTable.id] = id; it[ExpenseTable.tenantId] = tenantId; it[ExpenseTable.amount] = amount.toBigDecimal()
            it[ExpenseTable.category] = category; it[ExpenseTable.transactionDate] = java.time.LocalDate.parse(date)
            it[ExpenseTable.paymentMethod] = method; it[ExpenseTable.createdBy] = createdBy; it[ExpenseTable.createdAt] = Instant.now()
        }
        Record(id, amount, date, category)
    }
    
    fun getIncome(tenantId: UUID): List<Record> = transaction {
        IncomeTable.selectAll().where { (IncomeTable.tenantId eq tenantId) and (IncomeTable.deletedAt.isNull()) }
            .map { Record(it[IncomeTable.id], it[IncomeTable.amount].toDouble(), it[IncomeTable.transactionDate].toString(), it[IncomeTable.source]) }
    }
    
    fun getExpenses(tenantId: UUID): List<Record> = transaction {
        ExpenseTable.selectAll().where { (ExpenseTable.tenantId eq tenantId) and (ExpenseTable.deletedAt.isNull()) }
            .map { Record(it[ExpenseTable.id], it[ExpenseTable.amount].toDouble(), it[ExpenseTable.transactionDate].toString(), it[ExpenseTable.category]) }
    }
    
    fun getProfitLoss(tenantId: UUID): Map<String, Double> = transaction {
        val income = IncomeTable.selectAll().where { (IncomeTable.tenantId eq tenantId) and (IncomeTable.deletedAt.isNull()) }.sumOf { it[IncomeTable.amount].toDouble() }
        val expenses = ExpenseTable.selectAll().where { (ExpenseTable.tenantId eq tenantId) and (ExpenseTable.deletedAt.isNull()) }.sumOf { it[ExpenseTable.amount].toDouble() }
        mapOf("totalIncome" to income, "totalExpenses" to expenses, "netProfit" to (income - expenses))
    }
}

package com.agrihub.platform.marketplace.repository
import com.agrihub.platform.core.database.BaseTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.UUID

object ListingsTable : BaseTable("produce_listings") {
    val sellerId = uuid("seller_id"); val cropId = uuid("crop_id")
    val qualityGrade = varchar("quality_grade", 20).default("GRADE_A")
    val quantityKg = decimal("quantity_kg", 10, 2); val pricePerKg = decimal("price_per_kg", 10, 2)
    val status = varchar("status", 50).default("ACTIVE"); val availableFrom = date("available_from")
}

object OffersTable : BaseTable("offers") {
    val listingId = uuid("listing_id"); val buyerId = uuid("buyer_id")
    val offeredPricePerKg = decimal("offered_price_per_kg", 10, 2); val quantityKg = decimal("quantity_kg", 10, 2)
    val status = varchar("status", 50).default("PENDING")
}

class MarketplaceRepository {
    data class Listing(val id: UUID, val quantityKg: Double, val pricePerKg: Double, val status: String)
    data class Offer(val id: UUID, val offeredPrice: Double, val quantity: Double, val status: String)
    
    fun findAll(tenantId: UUID): List<Listing> = transaction {
        ListingsTable.selectAll().where { (ListingsTable.tenantId eq tenantId) and (ListingsTable.deletedAt.isNull()) and (ListingsTable.status eq "ACTIVE") }
            .map { Listing(it[ListingsTable.id], it[ListingsTable.quantityKg].toDouble(), it[ListingsTable.pricePerKg].toDouble(), it[ListingsTable.status]) }
    }
    
    fun create(tenantId: UUID, sellerId: UUID, cropId: UUID, quantityKg: Double, pricePerKg: Double, createdBy: UUID): Listing = transaction {
        val id = UUID.randomUUID()
        ListingsTable.insert {
            it[ListingsTable.id] = id; it[ListingsTable.tenantId] = tenantId; it[ListingsTable.sellerId] = sellerId
            it[ListingsTable.cropId] = cropId; it[ListingsTable.quantityKg] = quantityKg.toBigDecimal()
            it[ListingsTable.pricePerKg] = pricePerKg.toBigDecimal(); it[ListingsTable.availableFrom] = java.time.LocalDate.now()
            it[ListingsTable.createdBy] = createdBy; it[ListingsTable.createdAt] = Instant.now()
        }
        Listing(id, quantityKg, pricePerKg, "ACTIVE")
    }
    
    fun makeOffer(tenantId: UUID, listingId: UUID, buyerId: UUID, price: Double, quantity: Double, createdBy: UUID): Offer = transaction {
        val id = UUID.randomUUID()
        OffersTable.insert {
            it[OffersTable.id] = id; it[OffersTable.tenantId] = tenantId; it[OffersTable.listingId] = listingId
            it[OffersTable.buyerId] = buyerId; it[OffersTable.offeredPricePerKg] = price.toBigDecimal()
            it[OffersTable.quantityKg] = quantity.toBigDecimal(); it[OffersTable.createdBy] = createdBy
            it[OffersTable.createdAt] = Instant.now()
        }
        Offer(id, price, quantity, "PENDING")
    }
}

# 5. Use Cases

## Primary Use Cases

### UC-001: Farmer Registers and Sets Up Farm
- Actor: Individual Farmer | Priority: CRITICAL
- Flow: Create account -> Verify email -> Select language -> Register farm with GPS -> Dashboard
- Alt: Email exists (show login), GPS unavailable (manual entry), Offline (local save)

### UC-002: Farmer Plants a Crop
- Actor: Farmer | Priority: CRITICAL
- Flow: Select field -> Browse crop catalog -> Enter planting details -> Deduct seeds -> Update field status
- Alt: No inventory (warning), Field occupied (error), Offline (queue sync)

### UC-003: Farmer Records Harvest
- Actor: Farmer | Priority: CRITICAL
- Flow: Select field -> Enter harvest details (date, qty, grade) -> Calculate yield -> Update inventory
- Alt: Direct sale (skip inventory), Partial harvest (allow multiple), Low yield (prompt reason)

### UC-004: Farmer Sells on Marketplace
- Actor: Farmer (Seller) | Priority: CRITICAL
- Flow: Select produce -> Set price -> Add photos/description -> Publish listing
- Alt: No inventory link (manual), Below market price (suggestion)

### UC-005: Buyer Purchases Produce
- Actor: Buyer | Priority: CRITICAL
- Flow: Browse listings -> Make offer -> Seller accepts -> Digital contract -> Delivery -> Payment -> Complete
- Alt: Rejection (notify), Counter offer (up to 5 rounds), Dispute (quality/quantity issue)

### UC-006: Cooperative Aggregates Produce
- Actor: Coop Manager | Priority: HIGH
- Flow: Create lot -> Members deliver -> Quality check -> Aggregate -> List on marketplace -> Sell -> Pay members
- Alt: Target not met (sell partial), Quality dispute (inspection review)

### UC-007: Extension Officer Visits Farmer
- Actor: Extension Officer | Priority: MEDIUM
- Flow: View assigned farmers -> Schedule visit -> Record observations -> Share recommendations -> Gov reporting
- Alt: Offline visit (sync later), Photo attachment (compress for upload)

### UC-008: Farmer Records Vaccination
- Actor: Farmer | Priority: HIGH
- Flow: Select animal -> Choose vaccine -> Enter details -> Auto-schedule next dose -> Certificate
- Alt: Batch vaccination (multi-select), Custom vaccine (manual entry)

### UC-009: Accountant Reviews Profitability
- Actor: Accountant | Priority: HIGH
- Flow: Select period -> Generate P&L -> Drill down categories -> Compare budget -> Export PDF
- Alt: Custom date range, Multi-farm comparison

### UC-010: Farmer Works Offline Then Syncs
- Actor: Farmer | Priority: HIGH
- Flow: Work offline (local save) -> Regain connectivity -> Auto-sync -> Resolve conflicts -> Confirmation
- Alt: Sync conflict (timestamp resolution), Partial failure (retry queue)

## Secondary Use Cases (V1.5+)

### UC-011: NGO Generates Donor Report
- Actor: NGO Manager | Priority: MEDIUM | Version: V1.5
- Flow: Select project/period -> Aggregate beneficiary data -> Add narrative -> Export branded PDF

### UC-012: Warehouse Issues Electronic Receipt
- Actor: Warehouse Manager | Priority: MEDIUM | Version: V1.5
- Flow: Intake inspection -> Record produce -> Generate e-receipt -> Receipt usable as collateral

### UC-013: Equipment Rental
- Actor: Equipment Owner | Priority: LOW | Version: V1.5
- Flow: Register equipment -> Farmer requests -> Approve -> Usage logged -> Invoice -> Payment

### UC-014: Microfinance Loan Review
- Actor: Microfinance Officer | Priority: MEDIUM | Version: V2.0
- Flow: Farmer applies -> System pre-fills farm data -> Officer reviews credit score -> Approve/Reject -> Track repayments

### UC-015: Government Agricultural Dashboard
- Actor: Government Officer | Priority: MEDIUM | Version: V1.5
- Flow: View dashboard -> Filter by district -> See production estimates -> Export for planning

## Use Case Priority Matrix

| UC | Actor | Priority | Complexity | Version |
|----|-------|----------|------------|---------|
| UC-001 | Farmer | CRITICAL | Medium | V1.0 |
| UC-002 | Farmer | CRITICAL | Medium | V1.0 |
| UC-003 | Farmer | CRITICAL | Medium | V1.0 |
| UC-004 | Farmer | CRITICAL | High | V1.0 |
| UC-005 | Buyer | CRITICAL | High | V1.0 |
| UC-006 | Coop Mgr | HIGH | High | V1.0 |
| UC-007 | Ext Officer | MEDIUM | Medium | V1.0 |
| UC-008 | Farmer | HIGH | Low | V1.0 |
| UC-009 | Accountant | HIGH | Medium | V1.0 |
| UC-010 | Farmer | HIGH | High | V1.0 |
| UC-011 | NGO Mgr | MEDIUM | Medium | V1.5 |
| UC-012 | Warehouse | MEDIUM | Medium | V1.5 |
| UC-013 | Equipment | LOW | High | V1.5 |
| UC-014 | Microfinance | MEDIUM | High | V2.0 |
| UC-015 | Government | MEDIUM | Medium | V1.5 |

## 3.3 Livestock Management (FR-051 to FR-065)

### FR-051: Animal Registration
**Priority:** HIGH
**Description:** Register individual animals with unique identifier.
**Acceptance Criteria:**
- Required: species, breed, tag_id (ear tag number), date_of_birth, sex
- Supported species: CATTLE, GOAT, SHEEP, PIG, CHICKEN, DUCK, RABBIT
- Generate unique animal UUID
- Optional: color, weight_kg, sire_tag, dam_tag, purchase_date, purchase_price
- Photo attachment
- Bulk registration via CSV upload

### FR-052: Animal Registry View
**Priority:** HIGH
**Description:** View all livestock with filtering.
**Acceptance Criteria:**
- List all animals scoped to tenant
- Filter by: species, breed, sex, status
- Statuses: ALIVE, SICK, SOLD, DECEASED, LOST
- Count by category displayed
- Search by tag_id or name
- Export animal list

### FR-053: Animal Detail
**Priority:** MEDIUM
**Description:** Comprehensive individual animal profile.
**Acceptance Criteria:**
- Basic info: tag, name, species, breed, DOB, age, sex, color
- Parent info: sire and dam
- Weight history chart
- Health records timeline
- Vaccination history
- Breeding history
- Offspring list
- Milk production records (if dairy)

### FR-054: Vaccination Records
**Priority:** CRITICAL
**Description:** Track all vaccinations administered.
**Acceptance Criteria:**
- Record: animal_id, vaccine_name, batch_number, date_administered, administered_by
- Next due date auto-calculated based on vaccine schedule
- Predefined vaccine schedules per species
- Due/overdue vaccination alerts
- Vaccination certificate generation (PDF)
- Bulk vaccination recording

### FR-055: Feeding Records
**Priority:** MEDIUM
**Description:** Track feeding activities and feed consumption.
**Acceptance Criteria:**
- Record: date, feed_type, quantity_kg, cost, feeding_time
- Feed types: GRAZE, HAY, SILAGE, CONCENTRATE, SUPPLEMENT, WATER
- Feed inventory deduction (optional link to inventory)
- Daily feeding schedule setup
- Feed cost per animal calculation

### FR-056: Breeding Management
**Priority:** MEDIUM
**Description:** Track breeding events and outcomes.
**Acceptance Criteria:**
- Record mating: female_tag, male_tag, mating_date, method (NATURAL/AI)
- AI records: semen_source, technician, straw_id
- Pregnancy check: date, result, expected_delivery_date
- Birth recording: date, offspring_count, birth_weight, complications
- Offspring auto-registered from birth record
- Breeding efficiency reports

### FR-057: Health Records
**Priority:** HIGH
**Description:** Track all health events and treatments.
**Acceptance Criteria:**
- Record: animal_id, condition, symptoms, diagnosis, date
- Severity: MILD, MODERATE, SEVERE, CRITICAL
- Treatment: medication, dosage, frequency, duration, cost
- Veterinarian details
- Follow-up date set
- Recovery status tracking
- Attach lab reports or photos

### FR-058: Weight Tracking
**Priority:** MEDIUM
**Description:** Record and monitor animal weights over time.
**Acceptance Criteria:**
- Record: animal_id, weight_kg, date, body_condition_score (1-5)
- Weight chart with growth curve
- Alert for weight loss > 5%
- Average daily gain calculation
- Compare with breed standard

### FR-059: Milk Production
**Priority:** MEDIUM
**Description:** Track daily milk production for dairy animals.
**Acceptance Criteria:**
- Record: animal_id, date, morning_yield_liters, evening_yield_liters
- Total daily yield auto-calculated
- Lactation curve visualization
- Milk quality: fat_percentage, protein_percentage (optional)
- Monthly production summary
- Compare across lactation periods

### FR-060: Egg Production
**Priority:** LOW
**Description:** Track egg production for poultry.
**Acceptance Criteria:**
- Record: date, flock_id, eggs_collected, broken_eggs
- Production rate: eggs/bird/day
- Flock-level tracking (not individual)
- Monthly production trends

### FR-061: Livestock Inventory
**Priority:** HIGH
**Description:** Current livestock counts and valuation.
**Acceptance Criteria:**
- Count by species, breed, category (CALF, HEIFER, COW, BULL, STEER)
- Current total valuation
- Movements: births added, deaths removed, sales removed, purchases added
- Inventory reconciliation monthly
- Historical inventory snapshots

### FR-062: Livestock Sales
**Priority:** HIGH
**Description:** Record animal sales.
**Acceptance Criteria:**
- Record: animal_id, sale_date, buyer_name, sale_price, weight_at_sale
- Sale reason: MARKET, CULL, BREEDING_STOCK
- Auto-update animal status to SOLD
- Link to finance module as income
- Sales report with profit calculation

### FR-063: Mortality Recording
**Priority:** MEDIUM
**Description:** Record animal deaths with cause.
**Acceptance Criteria:**
- Record: animal_id, death_date, cause_of_death, disposal_method
- Cause categories: DISEASE, ACCIDENT, PREDATOR, OLD_AGE, UNKNOWN
- Mortality rate calculation per species
- Alert if mortality rate exceeds threshold

### FR-064: Grazing Management
**Priority:** LOW
**Description:** Track grazing areas and rotation.
**Acceptance Criteria:**
- Define grazing paddocks with GPS boundary
- Record: paddock, date_in, date_out, head_count
- Rotation schedule
- Paddock rest period tracking
- Overgrazing alerts

### FR-065: Livestock Reports
**Priority:** MEDIUM
**Description:** Comprehensive livestock reports.
**Acceptance Criteria:**
- Inventory summary report
- Health summary report
- Vaccination compliance report
- Breeding efficiency report
- Milk/egg production report
- Profit/loss per animal/species
- Export in CSV/PDF

## 3.4 Inventory Management (FR-066 to FR-080)

### FR-066: Inventory Item Registration
**Priority:** HIGH
**Description:** Register items in inventory catalog.
**Acceptance Criteria:**
- Categories: SEEDS, FERTILIZER, PESTICIDE, HERBICIDE, FUNGICIDE, FUEL, SPARE_PARTS, TOOLS, FEED, ANIMAL_HEALTH, PACKAGING, OTHER
- Required: item_name, category, unit_of_measure (KG, LITER, PIECE, BAG, BOTTLE, SACHET)
- Optional: brand, manufacturer, description, minimum_stock_level
- Barcode/QR code scanning for quick lookup
- Item photo

### FR-067: Stock Intake
**Priority:** CRITICAL
**Description:** Record items received into inventory.
**Acceptance Criteria:**
- Record: item_id, quantity, unit_cost, total_cost, supplier, date_received, batch_number, expiry_date
- Auto-calculate total_cost = quantity * unit_cost
- Update stock_on_hand
- Multiple items per receipt (batch intake)
- Attach delivery note or invoice

### FR-068: Stock Issuance
**Priority:** CRITICAL
**Description:** Record items taken from inventory.
**Acceptance Criteria:**
- Record: item_id, quantity, issued_to (user or field), date, purpose
- Purpose: PLANTING, APPLICATION, FEEDING, MAINTENANCE, SALE
- Auto-deduct from stock_on_hand
- Warn if stock below minimum level
- Cannot issue more than available stock
- Link to farm activity (e.g., fertilizer application)

### FR-069: Stock Transfer
**Priority:** MEDIUM
**Description:** Transfer stock between locations/warehouses.
**Acceptance Criteria:**
- Record: item_id, quantity, from_location, to_location, date
- Both locations updated
- Transfer status: PENDING, IN_TRANSIT, RECEIVED
- Receiver confirmation required

### FR-070: Stock Level Monitoring
**Priority:** HIGH
**Description:** Real-time stock levels with alerts.
**Acceptance Criteria:**
- Current stock on hand per item
- Stock value (quantity * unit_cost)
- Reorder point alerts
- Low stock alerts (push notification + email)
- Expiry date tracking with alerts (90, 60, 30 days)
- Stock movement history per item

### FR-071: Stock Adjustment
**Priority:** MEDIUM
**Description:** Adjust stock for discrepancies.
**Acceptance Criteria:**
- Adjustment types: COUNT_DISCREPANCY, DAMAGE, THEFT, EXPIRY, OTHER
- Reason required for all adjustments
- Positive or negative adjustment
- Authorization required for adjustments > threshold
- Audit log for all adjustments

### FR-072: Stock Count
**Priority:** MEDIUM
**Description:** Physical stock count reconciliation.
**Acceptance Criteria:**
- Create count session: date, location, items to count
- Record counted quantity per item
- Auto-compare with system quantity
- Variance flagged for review
- Adjustments generated from count
- Count history with approval workflow

### FR-073: Supplier Management
**Priority:** MEDIUM
**Description:** Manage supplier information.
**Acceptance Criteria:**
- Supplier profile: name, contact_person, phone, email, address
- Supplier categories: SEED_SUPPLIER, AGROCHEMICAL, EQUIPMENT, FEED, VETERINARY
- Purchase history per supplier
- Supplier rating based on delivery and quality
- Active/Inactive status

### FR-074: Purchase Orders
**Priority:** MEDIUM
**Description:** Create and track purchase orders.
**Acceptance Criteria:**
- Create PO: supplier, items, quantities, unit_prices, expected_delivery_date
- Status: DRAFT, SUBMITTED, APPROVED, PARTIALLY_RECEIVED, RECEIVED, CANCELLED
- Approval workflow for POs above threshold
- Auto-convert to stock intake on receipt
- PO vs actual delivery comparison

### FR-075: Inventory Valuation
**Priority:** MEDIUM
**Description:** Calculate inventory value.
**Acceptance Criteria:**
- Valuation method: FIFO (First In First Out)
- Current total inventory value
- Value by category
- Value trend over time
- Monthly closing stock report

### FR-076: Seed Inventory
**Priority:** HIGH
**Description:** Specialized seed inventory tracking.
**Acceptance Criteria:**
- Seed-specific fields: variety, germination_rate, purity_percent, seed_class (CERTIFIED, STANDARD, FARMER_SAVED)
- Lot number tracking
- Germination test date and result
- Viability period alerts
- Link to planting records

### FR-077: Chemical Inventory
**Priority:** HIGH
**Description:** Track agricultural chemicals with safety compliance.
**Acceptance Criteria:**
- Chemical-specific: active_ingredient, concentration, toxicity_class, safety_interval_days
- Safety data sheet (SDS) attachment
- Restricted use flag
- Application records linked
- Pre-harvest interval compliance check

### FR-078: Fuel Management
**Priority:** LOW
**Description:** Track fuel stock and consumption.
**Acceptance Criteria:**
- Fuel types: DIESEL, PETROL, PARAFFIN
- Record: date, quantity_liters, equipment/vehicle, purpose, operator
- Fuel consumption rate per equipment
- Fuel cost tracking

### FR-079: Equipment Inventory
**Priority:** MEDIUM
**Description:** Track equipment, tools, and machinery.
**Acceptance Criteria:**
- Register: name, type, make, model, serial_number, purchase_date, purchase_price
- Current value (depreciation calculated)
- Assignment to user or farm
- Maintenance schedule
- Usage log with hours

### FR-080: Inventory Reports
**Priority:** MEDIUM
**Description:** Standard inventory reports.
**Acceptance Criteria:**
- Stock on hand report
- Stock movement report (in/out)
- Low stock report
- Expiry report
- Valuation report
- Consumption report by farm/field
- Export in CSV/PDF

## 3.5 Finance Management (FR-081 to FR-100)

### FR-081: Chart of Accounts
**Priority:** CRITICAL
**Description:** Predefined and customizable chart of accounts.
**Acceptance Criteria:**
- Default account categories:
  INCOME: Crop Sales, Livestock Sales, Equipment Rental, Grants, Other Income
  EXPENSE: Seeds, Fertilizer, Labor, Fuel, Equipment Maintenance, Transport, Storage, Marketing, Loan Interest, Insurance, Other
  ASSETS: Cash, Bank, Inventory, Equipment, Land, Livestock, Accounts Receivable
  LIABILITIES: Loans, Accounts Payable, Accrued Expenses
- Tenant can add custom accounts
- Account codes auto-generated
- Active/Inactive account status

### FR-082: Expense Recording
**Priority:** CRITICAL
**Description:** Record all farm expenses.
**Acceptance Criteria:**
- Required: amount, expense_date, category, payment_method
- Payment methods: CASH, MOBILE_MONEY, BANK_TRANSFER, CHEQUE, CREDIT
- Optional: description, supplier, receipt_photo, linked_farm, linked_field
- Recurring expense setup (monthly rent, salaries)
- Receipt photo capture and storage

### FR-083: Income Recording
**Priority:** CRITICAL
**Description:** Record all farm income.
**Acceptance Criteria:**
- Required: amount, income_date, source
- Income sources: PRODUCE_SALE, LIVESTOCK_SALE, EQUIPMENT_RENTAL, GRANT, SUBSIDY, INSURANCE_CLAIM, OTHER
- Optional: buyer, description, invoice_number, linked_harvest
- Link to marketplace sales auto-populated
- Deposit tracking (multiple payments per invoice)

### FR-084: Budget Management
**Priority:** HIGH
**Description:** Create and track budgets.
**Acceptance Criteria:**
- Budget types: ANNUAL, SEASONAL, PER_CROP, PER_FARM
- Budget periods with start and end dates
- Budget line items with planned amounts per category
- Actual vs budget comparison
- Budget variance alerts (>10% over budget)
- Budget approval workflow

### FR-085: Profit & Loss Statement
**Priority:** HIGH
**Description:** Generate profit and loss reports.
**Acceptance Criteria:**
- Period selection: monthly, quarterly, annual, custom
- Total income, total expenses, net profit calculated
- Gross margin per crop
- Cost breakdown by category
- Comparison with previous period
- Export in PDF and CSV

### FR-086: Cash Flow Management
**Priority:** MEDIUM
**Description:** Track cash inflows and outflows.
**Acceptance Criteria:**
- Cash flow statement by period
- Opening balance + inflows - outflows = closing balance
- Projected cash flow based on planned expenses
- Cash flow alerts for low balance

### FR-087: Loan Management
**Priority:** MEDIUM
**Description:** Track loans taken and repayment schedules.
**Acceptance Criteria:**
- Loan record: lender, principal_amount, interest_rate, disbursement_date, term_months
- Repayment schedule auto-generated
- Payment recording with date and amount
- Outstanding balance calculation
- Interest calculation (simple/compound)
- Upcoming payment reminders
- Loan status: ACTIVE, PAID_OFF, DEFAULTED

### FR-088: Invoice Generation
**Priority:** MEDIUM
**Description:** Generate invoices for sales.
**Acceptance Criteria:**
- Auto-generated invoice number (INV-YYYY-NNNNN)
- Invoice: seller details, buyer details, items, quantities, prices, total
- Due date and payment terms
- Status: DRAFT, SENT, PARTIALLY_PAID, PAID, OVERDUE, CANCELLED
- PDF generation and sharing
- Payment tracking against invoice

### FR-089: Payment Tracking
**Priority:** HIGH
**Description:** Track all payments made and received.
**Acceptance Criteria:**
- Payment record: amount, date, method, reference_number, payer/payee
- Link to invoice or expense
- Mobile money integration (Airtel Money, TNM Mpamba)
- Bank transfer reconciliation
- Payment status: PENDING, COMPLETED, FAILED, REFUNDED

### FR-090: Financial Dashboard
**Priority:** HIGH
**Description:** Overview of financial health.
**Acceptance Criteria:**
- Current balance (cash + bank)
- Monthly income vs expense chart
- Top expense categories
- Outstanding invoices
- Upcoming payments
- Profit margin trend

### FR-091: Tax Management
**Priority:** LOW
**Description:** Track tax-related information.
**Acceptance Criteria:**
- Tax types: VAT, INCOME_TAX, WITHHOLDING_TAX
- Tax rate configuration
- Tax collected/reportable calculation
- Tax period summary
- Export for tax filing

### FR-092: Financial Year Setup
**Priority:** HIGH
**Description:** Configure financial year periods.
**Acceptance Criteria:**
- Default: January - December
- Custom financial year start/end
- Multiple financial years tracked
- Year-end closing process
- Opening balances carried forward

### FR-093: Cost Per Crop Analysis
**Priority:** HIGH
**Description:** Calculate total cost and profit per crop.
**Acceptance Criteria:**
- All expenses allocated to specific crop
- Cost per hectare calculated
- Revenue per hectare from harvests
- Profit per hectare
- Compare profitability across crops
- Identify most/least profitable crops

### FR-094: Grant Management
**Priority:** LOW (NGO-focused)
**Description:** Track grants received and utilization.
**Acceptance Criteria:**
- Grant record: donor, amount, purpose, start_date, end_date
- Budget allocation per activity
- Expenditure tracking against grant
- Reporting period summaries
- Compliance documentation storage

### FR-095: Mobile Money Integration
**Priority:** HIGH (V1.5)
**Description:** Integrate with mobile money platforms.
**Acceptance Criteria:**
- Supported: Airtel Money, TNM Mpamba
- Receive payment notifications
- Initiate payment requests
- Transaction history sync
- Balance check
- USSD fallback for feature phones

### FR-096: Financial Approvals
**Priority:** MEDIUM
**Description:** Approval workflow for financial transactions.
**Acceptance Criteria:**
- Transactions requiring approval: expenses > threshold, budget changes, adjustments
- Multi-level approval: MANAGER -> OWNER
- Approval/rejection with comments
- Approval history audit trail
- Pending approvals notification

### FR-097: Reconciliation
**Priority:** MEDIUM
**Description:** Reconcile bank and mobile money statements.
**Acceptance Criteria:**
- Upload statement (CSV, PDF)
- Auto-match with recorded transactions
- Flag unmatched transactions
- Manual matching option
- Reconciliation report with discrepancies

### FR-098: Subsidy Management
**Priority:** MEDIUM
**Description:** Track government subsidy programs (e.g., FISP in Malawi).
**Acceptance Criteria:**
- Program registration
- Subsidy allocation per farmer
- Redemption tracking
- Subsidy balance
- Compliance reporting

### FR-099: Insurance Tracking
**Priority:** LOW
**Description:** Track crop and livestock insurance policies.
**Acceptance Criteria:**
- Policy record: insurer, type, coverage_amount, premium, start_date, end_date
- Premium payment tracking
- Claim filing with documentation
- Claim status tracking
- Policy renewal reminders

### FR-100: Financial Reports
**Priority:** HIGH
**Description:** Comprehensive financial reporting suite.
**Acceptance Criteria:**
- Profit & Loss Statement
- Balance Sheet
- Cash Flow Statement
- Budget vs Actual
- Cost per Crop Analysis
- Expense by Category
- Income by Source
- Tax Summary
- All exportable in PDF, Excel, CSV


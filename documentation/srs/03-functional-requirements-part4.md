## 3.6 Marketplace (FR-101 to FR-125)

### FR-101: Produce Listing
**Priority:** CRITICAL
**Description:** Farmers list produce for sale on the marketplace.
**Acceptance Criteria:**
- Required: crop_type, variety, quantity_kg, price_per_kg, available_from
- Quality grade: GRADE_A, GRADE_B, GRADE_C
- Optional: moisture_content, organic_certified, photos (max 5), description
- Location: farm GPS coordinates, nearest market town
- Listing status: ACTIVE, PENDING, SOLD, EXPIRED, CANCELLED
- Auto-expiry after 30 days if not sold

### FR-102: Produce Browsing
**Priority:** CRITICAL
**Description:** Buyers browse available produce listings.
**Acceptance Criteria:**
- Filter by: crop_type, variety, grade, location_radius, price_range, organic_only
- Sort by: price_low_to_high, price_high_to_low, nearest_first, newest_first
- Map view showing listing locations
- List view with thumbnail, crop, price, quantity, distance
- Save favorite searches

### FR-103: Produce Search
**Priority:** HIGH
**Description:** Full-text search across marketplace listings.
**Acceptance Criteria:**
- Search by: crop name, variety, location, seller name
- Recent searches saved
- Search suggestions as user types
- Results with relevance ranking
- Empty state with suggestions

### FR-104: Offer Management
**Priority:** CRITICAL
**Description:** Buyers make offers on listed produce.
**Acceptance Criteria:**
- Buyer submits: offered_price_per_kg, quantity_kg, message
- Offer can be below, at, or above asking price
- Offer status: PENDING, ACCEPTED, REJECTED, COUNTERED, EXPIRED, WITHDRAWN
- Seller receives notification of new offer
- Both parties can message within offer thread
- Multiple offers per listing supported

### FR-105: Counter Offer
**Priority:** MEDIUM
**Description:** Sellers can counter buyer offers.
**Acceptance Criteria:**
- Seller counters with new price or quantity
- Negotiation history tracked in thread
- Either party can accept or continue countering
- Max 5 rounds of counter offers
- Original offer terms preserved

### FR-106: Digital Contract
**Priority:** HIGH
**Description:** Generate digital contract upon offer acceptance.
**Acceptance Criteria:**
- Contract includes: parties, crop, quantity, price, delivery_terms, payment_terms, date
- Terms templates: DELIVERY_EX_FARM, DELIVERY_TO_BUYER, FOB
- Payment terms: FULL_ADVANCE, PARTIAL_ADVANCE, ON_DELIVERY, NET_7, NET_30
- Both parties digitally acknowledge (accept button = signature)
- Contract PDF generated
- Contract status: PENDING_ACKNOWLEDGMENT, ACTIVE, FULFILLED, BREACHED, CANCELLED

### FR-107: Order Management
**Priority:** HIGH
**Description:** Track orders from contract to delivery.
**Acceptance Criteria:**
- Order created upon contract acknowledgment
- Order status: CONFIRMED, PROCESSING, READY_FOR_PICKUP, IN_TRANSIT, DELIVERED, COMPLETED, CANCELLED
- Status updates with timestamp
- Both buyer and seller can update status
- Delivery date tracking
- Order notes and attachments

### FR-108: Delivery Tracking
**Priority:** MEDIUM
**Description:** Track produce delivery logistics.
**Acceptance Criteria:**
- Delivery methods: BUYER_COLLECTS, SELLER_DELIVERS, THIRD_PARTY_TRANSPORT
- Assign transporter (if third party)
- Pickup location and delivery location with GPS
- Estimated delivery time
- Delivery confirmation with photo
- Delivery issues reporting

### FR-109: Quality Inspection
**Priority:** HIGH
**Description:** Record quality inspection at delivery.
**Acceptance Criteria:**
- Inspection by: BUYER, THIRD_PARTY_INSPECTOR, WAREHOUSE_STAFF
- Grade verification: matches_contract, downgraded, rejected
- Parameters checked: moisture, purity, foreign_matter, damage, size
- Inspection result: PASSED, PASSED_WITH_NOTES, FAILED
- Photo evidence of inspection
- Linked to order and contract

### FR-110: Payment Processing
**Priority:** HIGH
**Description:** Track and process payments for marketplace orders.
**Acceptance Criteria:**
- Payment methods: CASH, MOBILE_MONEY, BANK_TRANSFER
- Payment status: PENDING, PARTIAL, COMPLETED, FAILED, REFUNDED
- Partial payments supported
- Payment proof upload (screenshot/receipt)
- Auto-calculate amount due
- Payment due reminders

### FR-111: Buyer Profile
**Priority:** MEDIUM
**Description:** Buyer profiles with ratings and history.
**Acceptance Criteria:**
- Buyer profile: business_name, contact, location, buyer_type
- Buyer types: INDIVIDUAL, WHOLESALER, RETAILER, PROCESSOR, EXPORTER, INSTITUTION
- Transaction history
- Rating from sellers (1-5 stars)
- Response rate and time
- Verified badge for trusted buyers

### FR-112: Seller Profile
**Priority:** MEDIUM
**Description:** Seller profiles with farm and produce history.
**Acceptance Criteria:**
- Seller profile linked to farm
- Produce history and quality ratings
- Rating from buyers (1-5 stars)
- Certifications displayed (Organic, Fair Trade)
- Response rate and time
- Repeat customer rate

### FR-113: Price Trends
**Priority:** MEDIUM
**Description:** Market price data for informed decisions.
**Acceptance Criteria:**
- Current average prices per crop across platform
- Price trend charts (7-day, 30-day, 90-day)
- Price by region/location
- Price by quality grade
- External market data integration (future: MIS market prices)
- Price alerts for significant changes

### FR-114: Favorites & Watchlist
**Priority:** LOW
**Description:** Users can save listings and sellers.
**Acceptance Criteria:**
- Save/favorite produce listings
- Watch specific sellers
- Notifications when watched sellers post new listings
- Notification for price drops on favorited items

### FR-115: Marketplace Notifications
**Priority:** HIGH
**Description:** Real-time notifications for marketplace activity.
**Acceptance Criteria:**
- Notifications for: new_offer, offer_accepted, offer_rejected, counter_offer, order_status_change, payment_received, new_message
- Push notification to mobile device
- In-app notification center
- Email digest of daily activity (optional)

### FR-116: Messaging System
**Priority:** HIGH
**Description:** In-app messaging between buyers and sellers.
**Acceptance Criteria:**
- Messages linked to specific listing/offer
- Real-time chat (WebSocket)
- Offline message queue
- Message status: SENT, DELIVERED, READ
- Photo sharing in chat
- Message history per conversation

### FR-117: Bulk Purchase
**Priority:** MEDIUM
**Description:** Buyers can create bulk purchase requests.
**Acceptance Criteria:**
- Buyer posts: crop, quantity_needed, max_price, delivery_location, deadline
- Multiple sellers can respond with offers
- Buyer compares offers side by side
- Can accept multiple partial offers
- Aggregate contract generation

### FR-118: Contract Farming
**Priority:** MEDIUM (V1.5)
**Description:** Buyers contract farmers for future harvests.
**Acceptance Criteria:**
- Contract: buyer, farmer, crop, area, expected_yield, agreed_price, delivery_schedule
- Advance payment tracking
- Input supply tracking (if buyer provides inputs)
- Progress monitoring through growing season
- Harvest and delivery tracking against contract

### FR-119: Dispute Resolution
**Priority:** MEDIUM
**Description:** Process for handling transaction disputes.
**Acceptance Criteria:**
- Dispute raised by either party with reason and evidence
- Dispute categories: QUALITY, QUANTITY, DELIVERY_DELAY, PAYMENT, OTHER
- Status: OPEN, UNDER_REVIEW, RESOLVED_BUYER, RESOLVED_SELLER, ESCALATED
- Admin mediation available
- Resolution notes and outcome recorded

### FR-120: Marketplace Analytics
**Priority:** MEDIUM
**Description:** Analytics dashboard for marketplace activity.
**Acceptance Criteria:**
- For sellers: views, offers, conversion_rate, total_sales, average_price
- For buyers: searches, offers_made, acceptance_rate, total_purchased, average_price_paid
- For admin: total_transactions, total_value, active_listings, dispute_rate
- Time period filtering

### FR-121: Recurring Orders
**Priority:** LOW
**Description:** Setup recurring purchase orders.
**Acceptance Criteria:**
- Frequency: WEEKLY, BIWEEKLY, MONTHLY
- Auto-generate order based on template
- Quantity and price can be fixed or market-based
- Renewal reminders
- Pause/cancel recurring orders

### FR-122: Produce Aggregation
**Priority:** MEDIUM
**Description:** Cooperatives aggregate member produce for bulk sale.
**Acceptance Criteria:**
- Cooperative creates aggregation lot
- Members contribute produce quantities
- Quality grading per contribution
- Combined listing on marketplace
- Revenue distribution to members
- Aggregation report per member

### FR-123: Transport Marketplace
**Priority:** LOW (V2.0)
**Description:** Connect loads with available transporters.
**Acceptance Criteria:**
- Shipper posts: pickup, delivery, cargo_type, quantity, date
- Transporters bid on loads
- Transporter profile with vehicle details
- Pricing: per_km, per_ton, fixed_route
- Load tracking during transit

### FR-124: Market Price Index
**Priority:** MEDIUM
**Description:** Official price index for key commodities.
**Acceptance Criteria:**
- Commodities tracked: Maize, Beans, Rice, Groundnuts, Soybeans, Tobacco, Tea
- Prices from major markets: Lilongwe, Blantyre, Mzuzu, Zomba
- Weekly price updates
- Integration with Ministry of Agriculture MIS data
- Price comparison across markets

### FR-125: Marketplace Compliance
**Priority:** HIGH
**Description:** Ensure marketplace complies with regulations.
**Acceptance Criteria:**
- No listing of prohibited items
- Minimum price rules for regulated commodities
- Tax compliance information
- Export documentation requirements (for cross-border)
- Terms of service acceptance
- Report inappropriate listings

## 3.7 Warehouse Management (FR-126 to FR-140)

### FR-126: Warehouse Registration
**Priority:** HIGH
**Description:** Register warehouse facilities.
**Acceptance Criteria:**
- Warehouse profile: name, location, capacity_kg, warehouse_type
- Types: PRIVATE, PUBLIC, COOPERATIVE, WAREHOUSE_RECEIPT_SYSTEM
- Storage conditions: AMBIENT, COLD_STORAGE, CONTROLLED_ATMOSPHERE
- Certification: WRS_CERTIFIED, GMP_CERTIFIED
- Warehouse layout (zones/sections)

### FR-127: Produce Intake
**Priority:** CRITICAL
**Description:** Record produce received into warehouse.
**Acceptance Criteria:**
- Intake record: farmer/supplier, crop, variety, quantity_kg, date_received
- Source: FARM, MARKETPLACE_ORDER, TRANSFER, RETURN
- Intake inspection: moisture_content, damage_percent, foreign_matter_percent
- Quality grade assigned on intake
- Bin/location assignment
- Intake receipt generated and shared

### FR-128: Quality Inspection (Warehouse)
**Priority:** HIGH
**Description:** Detailed quality inspection at warehouse intake.
**Acceptance Criteria:**
- Inspection parameters configurable per crop
- Common parameters: moisture, purity, germination (seeds), damage, insect_damage, mold, color, size_grade
- Pass/fail thresholds per parameter
- Multiple samples per lot
- Inspection report with recommendations
- Conditional acceptance (requires treatment)

### FR-129: Bin & Location Management
**Priority:** HIGH
**Description:** Manage storage locations within warehouse.
**Acceptance Criteria:**
- Location hierarchy: ZONE -> ROW -> RACK -> SHELF -> BIN
- Each location has unique code
- Location capacity tracking
- Location status: EMPTY, PARTIALLY_FILLED, FULL, RESERVED, MAINTENANCE
- Produce-to-location assignment
- Location movement tracking

### FR-130: Stock Management
**Priority:** CRITICAL
**Description:** Manage produce stock in warehouse.
**Acceptance Criteria:**
- Real-time stock levels per crop, variety, grade, location
- Stock aging (days in storage)
- Stock status: AVAILABLE, RESERVED, ON_HOLD, QUARANTINE, DAMAGED
- Stock splitting and merging
- Stock transfer between locations

### FR-131: Warehouse Receipt System
**Priority:** HIGH
**Description:** Issue electronic warehouse receipts.
**Acceptance Criteria:**
- Receipt: unique_receipt_number, depositor, crop, quantity, grade, date, location
- Receipt status: ACTIVE, PLEDGED, TRANSFERRED, REDEEMED, CANCELLED
- Receipt can be used as collateral (pledging)
- Transfer of ownership supported
- Partial redemption (partial withdrawal)
- Receipt verification by third parties
- Compliance with Malawi Warehouse Receipt Act

### FR-132: Fumigation Tracking
**Priority:** MEDIUM
**Description:** Track fumigation and pest control treatments.
**Acceptance Criteria:**
- Record: date, chemical_used, dosage, target_pest, treated_by
- Fumigation schedule per crop type
- Re-entry interval tracking
- Fumigation certificate generation
- Fumigation history per bin

### FR-133: Stock Condition Monitoring
**Priority:** MEDIUM
**Description:** Monitor produce condition during storage.
**Acceptance Criteria:**
- Regular inspection schedule
- Parameters: temperature, humidity, insect_activity, mold, odor, color_change
- Condition alerts if parameters exceed thresholds
- Corrective action recording
- Condition trend over time

### FR-134: Dispatch Management
**Priority:** CRITICAL
**Description:** Record produce leaving warehouse.
**Acceptance Criteria:**
- Dispatch: crop, quantity, destination, transporter, date
- Dispatch types: SALE, TRANSFER, RETURN_TO_FARMER, PROCESSING, DONATION
- Linked to marketplace order if applicable
- Dispatch note generated
- Loading supervision record
- Vehicle details and seal number

### FR-135: Stock Reconciliation
**Priority:** HIGH
**Description:** Regular stock count and reconciliation.
**Acceptance Criteria:**
- Scheduled physical counts
- Count by zone/location
- System vs physical comparison
- Variance investigation and resolution
- Adjustment with authorization
- Count history and accuracy metrics

### FR-136: Storage Fee Calculation
**Priority:** MEDIUM
**Description:** Calculate storage fees for depositors.
**Acceptance Criteria:**
- Fee structure: per_bag_per_day, per_kg_per_day, flat_monthly
- Free storage period (grace days)
- Fee calculation based on duration and quantity
- Fee invoice generation
- Payment tracking for storage fees
- Fee rate history

### FR-137: Warehouse Performance Metrics
**Priority:** LOW
**Description:** Monitor warehouse operational efficiency.
**Acceptance Criteria:**
- Occupancy rate (% of capacity used)
- Stock turnover rate
- Average storage duration
- Shrinkage/loss percentage
- Intake and dispatch volumes
- Revenue per square meter

### FR-138: Multi-Warehouse Support
**Priority:** MEDIUM
**Description:** Manage multiple warehouse locations.
**Acceptance Criteria:**
- Multiple warehouses per tenant
- Inter-warehouse transfers
- Consolidated stock view across warehouses
- Warehouse-specific reports
- Nearest warehouse finder

### FR-139: Cold Storage Management
**Priority:** LOW
**Description:** Specialized cold storage tracking.
**Acceptance Criteria:**
- Temperature logging (automated or manual)
- Temperature range alerts
- Door opening logs
- Defrost cycle tracking
- Product-specific temperature requirements
- Power outage impact recording

### FR-140: Warehouse Reports
**Priority:** HIGH
**Description:** Standard warehouse reports.
**Acceptance Criteria:**
- Stock report by crop/grade/location
- Intake and dispatch summary
- Storage fee statement per depositor
- Warehouse receipt register
- Quality inspection summary
- Loss and damage report
- Occupancy report

## 3.8 Reports & Analytics (FR-141 to FR-160)

### FR-141: Farm Productivity Report
**Priority:** HIGH
**Description:** Comprehensive farm productivity analysis.
**Acceptance Criteria:**
- Yield per crop per field per season
- Comparison with previous seasons
- Productivity trends (charts)
- Best and worst performing fields
- Crop rotation compliance
- Export in PDF/CSV

### FR-142: Financial Summary Report
**Priority:** CRITICAL
**Description:** Complete financial performance report.
**Acceptance Criteria:**
- Income by source
- Expenses by category
- Profit margin per crop
- Cash flow summary
- Budget vs actual comparison
- Period comparison (YoY, QoQ)

### FR-143: Harvest Report
**Priority:** HIGH
**Description:** Detailed harvest analysis.
**Acceptance Criteria:**
- Total harvest by crop
- Average yield per hectare
- Quality grade distribution
- Harvest labor efficiency
- Post-harvest losses
- Harvest calendar view

### FR-144: Input Usage Report
**Priority:** MEDIUM
**Description:** Analysis of input consumption.
**Acceptance Criteria:**
- Input quantity used by type
- Cost of inputs per crop
- Input efficiency (yield per kg of fertilizer)
- Comparison with recommended rates
- Over/under application alerts

### FR-145: Livestock Performance Report
**Priority:** MEDIUM
**Description:** Livestock productivity analysis.
**Acceptance Criteria:**
- Mortality rate by species
- Reproduction rate
- Weight gain trends
- Milk/egg production trends
- Feed conversion efficiency
- Health incident summary

### FR-146: Sustainability Report
**Priority:** LOW
**Description:** Environmental sustainability metrics.
**Acceptance Criteria:**
- Water usage efficiency
- Carbon footprint estimate
- Soil health trends
- Chemical usage tracking
- Biodiversity indicators
- Organic matter changes

### FR-147: Custom Report Builder
**Priority:** MEDIUM (V2.0)
**Description:** Users create custom reports.
**Acceptance Criteria:**
- Select data sources and fields
- Apply filters
- Choose visualization (table, bar, line, pie)
- Save report template
- Schedule recurring report generation
- Share report with team members

### FR-148: Dashboard Widgets
**Priority:** HIGH
**Description:** Configurable dashboard widgets.
**Acceptance Criteria:**
- Pre-built widgets: weather, tasks, stock alerts, recent activity, profit summary
- Drag to rearrange
- Add/remove widgets
- Widget-specific settings
- Data refresh on interaction

### FR-149: Scheduled Reports
**Priority:** MEDIUM
**Description:** Automated report generation on schedule.
**Acceptance Criteria:**
- Schedule: DAILY, WEEKLY, MONTHLY, QUARTERLY, ANNUALLY
- Delivery: email, in-app, download link
- Report format selection
- Recipient list management
- Schedule management (pause, edit, delete)

### FR-150: Data Visualization
**Priority:** MEDIUM
**Description:** Interactive charts and graphs.
**Acceptance Criteria:**
- Chart types: LINE, BAR, PIE, AREA, SCATTER, RADAR
- Interactive: zoom, hover details, filter
- Color-coded for accessibility
- Responsive to screen size
- Export chart as image

### FR-151: Benchmark Reports
**Priority:** LOW
**Description:** Compare performance against benchmarks.
**Acceptance Criteria:**
- Regional average yields
- Industry cost benchmarks
- Anonymized peer comparison
- Percentile ranking
- Gap analysis

### FR-152: Audit Report
**Priority:** HIGH
**Description:** Complete audit trail report.
**Acceptance Criteria:**
- All user actions with timestamps
- Filterable by: user, action, date_range, module
- Before/after values for changes
- Export for external audit
- Tamper-proof format

### FR-153: Compliance Report
**Priority:** MEDIUM
**Description:** Regulatory compliance reporting.
**Acceptance Criteria:**
- Certification status report
- Chemical usage compliance
- Labor compliance
- Environmental compliance
- Food safety compliance
- Export-ready format

### FR-154: KPI Dashboard
**Priority:** MEDIUM
**Description:** Key performance indicators at a glance.
**Acceptance Criteria:**
- Customizable KPIs
- Target vs actual visualization
- Red/amber/green status indicators
- Trend arrows (up/down/stable)
- Drill-down to details

### FR-155: Report Sharing
**Priority:** MEDIUM
**Description:** Share reports with stakeholders.
**Acceptance Criteria:**
- Share via: link, email, WhatsApp
- Access control: VIEW, DOWNLOAD
- Expiring links (1-30 days)
- Password protection option
- View tracking

### FR-156: Data Export API
**Priority:** MEDIUM
**Description:** API endpoints for data export.
**Acceptance Criteria:**
- Export endpoints for all modules
- Formats: JSON, CSV, XML
- Date range filtering
- Pagination for large datasets
- Async export for large requests
- API key authentication for programmatic access

### FR-157: Print-Friendly Reports
**Priority:** LOW
**Description:** Reports optimized for printing.
**Acceptance Criteria:**
- Print CSS styles
- PDF generation
- Header with logo, date, report title
- Page numbers
- Print preview

### FR-158: Report Archive
**Priority:** LOW
**Description:** Archive historical reports.
**Acceptance Criteria:**
- Auto-archive reports older than configurable period
- Search archive by date and type
- Restore from archive
- Archive storage optimization

### FR-159: Donor Report (NGO)
**Priority:** MEDIUM
**Description:** Custom reports for donor requirements.
**Acceptance Criteria:**
- Beneficiary reach metrics
- Project impact indicators
- Budget utilization
- Activity completion rates
- Success stories with photos
- Custom branding on reports

### FR-160: Government Report
**Priority:** MEDIUM
**Description:** Reports for government agricultural monitoring.
**Acceptance Criteria:**
- Production estimates by region
- Crop area estimates
- Input distribution tracking
- Food security indicators
- Export/import projections
- Anonymized aggregated data


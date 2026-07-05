## 3.2 Farm Management (FR-021 to FR-050)

### FR-021: Farm Registration
**Priority:** CRITICAL
**Description:** Users can register a new farm with location details.
**Acceptance Criteria:**
- Required fields: farm_name, location (GPS coordinates), area_hectares
- Optional: soil_type, water_source, elevation, description
- Farm receives unique identifier (UUID)
- Farm linked to creating tenant
- One tenant can have multiple farms
- GPS coordinates captured via device GPS or manual entry

### FR-022: Farm Listing
**Priority:** CRITICAL
**Description:** Users can view all farms belonging to their tenant.
**Acceptance Criteria:**
- List all farms scoped to tenant
- Sort by: name, area, created_date
- Filter by: status (active/inactive)
- Each farm shows: name, area, crop count, status
- Tap farm to view details

### FR-023: Farm Detail View
**Priority:** HIGH
**Description:** Comprehensive view of a single farm.
**Acceptance Criteria:**
- Farm info: name, location, area, soil type, water source
- Map view with farm boundary polygon
- List of fields within farm
- Active crops summary
- Recent activities timeline
- Weather widget for farm location

### FR-024: Farm Edit
**Priority:** HIGH
**Description:** Update farm information.
**Acceptance Criteria:**
- Editable fields: name, area, soil_type, description
- GPS coordinates change logged in audit trail
- Status can be changed: active -> inactive
- Inactive farms hidden from default views
- Edit history visible to farm manager

### FR-025: GPS Farm Mapping
**Priority:** HIGH
**Description:** Define farm boundaries using GPS.
**Acceptance Criteria:**
- Walk perimeter to capture boundary polygon
- Minimum 3 points required for polygon
- Area auto-calculated from polygon
- Satellite map overlay for verification
- Boundary editable by moving vertex points
- Accuracy display (GPS precision in meters)

### FR-026: Field Creation
**Priority:** CRITICAL
**Description:** Create subdivisions (fields) within a farm.
**Acceptance Criteria:**
- Required: field_name, field_area, farm_id
- Optional: soil_type, irrigation_type, drainage
- Field gets unique number within farm
- GPS polygon for field boundary
- Multiple fields per farm supported
- Total field area cannot exceed farm area

### FR-027: Field Management
**Priority:** HIGH
**Description:** View and manage all fields in a farm.
**Acceptance Criteria:**
- Grid/list view of all fields
- Each field shows: name, crop planted, area, status
- Statuses: EMPTY, PLANTED, FALLOW, HARVESTED
- Color-coded status indicators
- Quick actions: plant crop, add input, harvest

### FR-028: Soil Analysis Records
**Priority:** MEDIUM
**Description:** Record and track soil test results.
**Acceptance Criteria:**
- Record: pH level, nitrogen, phosphorus, potassium
- Organic matter percentage
- Soil texture classification
- Test date and lab information
- Attach lab report (PDF/Image)
- Compare historical soil tests

### FR-029: Weather Data Integration
**Priority:** MEDIUM
**Description:** Display weather for farm locations.
**Acceptance Criteria:**
- Current conditions: temperature, humidity, rainfall, wind
- 7-day forecast
- Historical rainfall data
- Severe weather alerts
- Data sourced from weather API
- Cached for offline viewing

### FR-030: Farm Activity Log
**Priority:** MEDIUM
**Description:** Chronological log of all farm activities.
**Acceptance Criteria:**
- Auto-logged: plantings, harvests, inputs applied
- Manual entries: observations, notes, photos
- Each entry has: date, user, activity_type, description
- Photo attachments supported (max 5 per entry)
- Filterable by date range and activity type
- Exportable as farm diary report

### FR-031: Crop Planning
**Priority:** HIGH
**Description:** Plan crop rotations and planting schedules.
**Acceptance Criteria:**
- Select crop from catalog
- Set planned planting date
- Estimated harvest date calculated
- Assign to specific field
- Multiple crops per plan
- Calendar view of planting schedule
- Conflict detection (overlapping plans on same field)

### FR-032: Crop Catalog
**Priority:** HIGH
**Description:** Comprehensive catalog of crops grown in Malawi.
**Acceptance Criteria:**
- Pre-loaded crops: Maize, Tobacco, Tea, Sugarcane, Groundnuts, Cotton, Beans, Cassava, Sweet Potatoes, Rice, Sorghum, Millet, Sunflower, Soybeans, Pigeon Peas, Cowpeas, Vegetables (Tomato, Onion, Cabbage, Leafy Greens), Fruits (Mango, Banana, Papaya, Citrus)
- Each crop: growing period, water needs, optimal soil pH, fertilizer recommendations, common pests, common diseases, expected yield range
- Images for each crop and growth stage
- Search and filter by category (Cereal, Legume, Vegetable, Fruit, Cash Crop)

### FR-033: Planting Record
**Priority:** CRITICAL
**Description:** Record crop planting details.
**Acceptance Criteria:**
- Required: farm, field, crop, planting_date, seed_variety
- Optional: seed_quantity_kg, seed_source, planting_method, spacing, expected_harvest_date
- GPS point for exact planting location
- Photo attachment of planted field
- Generates unique planting ID
- Links to seed inventory (deducts if tracked)

### FR-034: Growth Stage Tracking
**Priority:** MEDIUM
**Description:** Monitor crop through defined growth stages.
**Acceptance Criteria:**
- Predefined stages per crop type
- Manual stage advancement or date-based auto-advance
- Stage-specific recommendations displayed
- Growth stage timeline visualization
- Alert if stage duration exceeds normal
- Record observations per stage

### FR-035: Input Application Record
**Priority:** HIGH
**Description:** Track all inputs applied to crops.
**Acceptance Criteria:**
- Input types: FERTILIZER, PESTICIDE, HERBICIDE, FUNGICIDE, WATER
- Required: input_type, product_name, quantity, unit, application_date, field
- Optional: application_method, weather_conditions, safety_interval_days
- Links to inventory (deducts automatically)
- Pre-harvest interval warnings
- Cost automatically assigned from inventory price

### FR-036: Irrigation Tracking
**Priority:** MEDIUM
**Description:** Record irrigation activities.
**Acceptance Criteria:**
- Irrigation methods: DRIP, SPRINKLER, FLOOD, MANUAL, RAINFED
- Record: date, duration_hours, water_source, estimated_volume_liters
- Irrigation schedule setup (recurring)
- Alert for missed irrigation events
- Water usage reports per field/crop

### FR-037: Pest & Disease Monitoring
**Priority:** HIGH
**Description:** Record and track pest and disease occurrences.
**Acceptance Criteria:**
- Record: pest_or_disease_name, affected_crop, severity (LOW/MEDIUM/HIGH/CRITICAL), area_affected_percent, date_detected
- Photo attachment for visual documentation
- Treatment action recorded with date and product
- Follow-up observation date set
- Alert escalation if severity increases
- Links to crop catalog for known issues

### FR-038: Harvest Recording
**Priority:** CRITICAL
**Description:** Record harvest details for each crop.
**Acceptance Criteria:**
- Required: planting_record_id, harvest_date, quantity_kg
- Quality grade: GRADE_A, GRADE_B, GRADE_C, REJECTED
- Harvest method: MANUAL, MECHANICAL
- Destination: WAREHOUSE, DIRECT_SALE, PROCESSING
- Labor: number_of_workers, total_hours
- Photo of harvested produce
- Actual vs expected yield calculation

### FR-039: Yield Analysis
**Priority:** HIGH
**Description:** Calculate and analyze crop yields.
**Acceptance Criteria:**
- Yield per hectare auto-calculated: quantity_kg / field_area
- Comparison with expected yield from crop catalog
- Historical yield trends per field
- Yield by crop variety comparison
- Best/worst performing fields identified
- Exportable yield report

### FR-040: Farm Calendar
**Priority:** MEDIUM
**Description:** Calendar view of all farm activities.
**Acceptance Criteria:**
- Monthly/weekly/daily views
- Shows: plantings, input applications, harvests, tasks
- Color-coded by activity type
- Tap event for details
- Add manual tasks and reminders
- Sync with device calendar (optional)

### FR-041: Task Management
**Priority:** MEDIUM
**Description:** Create and assign farm tasks.
**Acceptance Criteria:**
- Create task: title, description, due_date, assigned_to, priority
- Priority levels: LOW, MEDIUM, HIGH, URGENT
- Status: PENDING, IN_PROGRESS, COMPLETED, CANCELLED
- Recurring tasks: daily, weekly, monthly, custom
- Task completion with notes and photo
- Task notifications to assigned user

### FR-042: Farm Photo Gallery
**Priority:** LOW
**Description:** Visual timeline of farm through photos.
**Acceptance Criteria:**
- Photos organized by field and date
- Auto-tagged with GPS location
- Before/after comparison view
- Growth progression timeline
- Storage optimized (compressed thumbnails)

### FR-043: Farm Documents
**Priority:** LOW
**Description:** Store farm-related documents.
**Acceptance Criteria:**
- Supported formats: PDF, DOC, XLS, Images
- Categories: LAND_TITLE, CERTIFICATION, CONTRACT, INVOICE, REPORT
- Upload from device or capture
- Document expiry tracking
- Search by name or category

### FR-044: Equipment Tracking
**Priority:** MEDIUM
**Description:** Track farm equipment and maintenance.
**Acceptance Criteria:**
- Register equipment: name, type, purchase_date, status
- Types: TRACTOR, PLOW, HARVESTER, IRRIGATION, SPRAYER, HAND_TOOL
- Maintenance schedule with alerts
- Usage logging: date, field, hours_used, operator
- Equipment status: AVAILABLE, IN_USE, MAINTENANCE, RETIRED

### FR-045: Labor Tracking
**Priority:** LOW
**Description:** Record labor activities and costs.
**Acceptance Criteria:**
- Record: date, worker_count, hours_worked, activity, rate_per_hour
- Total labor cost auto-calculated
- Labor by activity type report
- Seasonal labor patterns analysis

### FR-046: Certification Tracking
**Priority:** MEDIUM
**Description:** Track farm certifications (Organic, Fair Trade, etc.).
**Acceptance Criteria:**
- Certification types: ORGANIC, FAIR_TRADE, GLOBAL_GAP, RAINFOREST_ALLIANCE
- Record: certifying_body, certification_date, expiry_date, certificate_number
- Expiry reminders (90, 60, 30 days before)
- Attach certificate document
- Certification status on farm profile

### FR-047: Farm Comparison
**Priority:** LOW
**Description:** Compare performance across farms.
**Acceptance Criteria:**
- Select 2+ farms for comparison
- Metrics: yield_per_hectare, cost_per_hectare, profit_per_hectare
- Visual charts (bar, radar)
- Identify best practices

### FR-048: Seasonal Planning
**Priority:** MEDIUM
**Description:** Plan activities for upcoming season.
**Acceptance Criteria:**
- Select season: RAINY (Nov-Apr), DRY (May-Oct), WINTER (May-Aug)
- Budget allocation per crop
- Input procurement list
- Labor requirement estimate
- Expected revenue projection

### FR-049: Crop Rotation Planner
**Priority:** MEDIUM
**Description:** Plan crop rotations for soil health.
**Acceptance Criteria:**
- Suggest rotations based on previous crops
- Nutrient depletion/restoration tracking
- Rotation history visualization
- Alert for poor rotation practices
- Minimum 3-year rotation view

### FR-050: Farm Performance Score
**Priority:** LOW
**Description:** Overall farm performance rating.
**Acceptance Criteria:**
- Score 0-100 based on: yield, cost efficiency, sustainability
- Updated monthly
- Historical score trends
- Recommendations for improvement
- Benchmark against similar farms (anonymized)


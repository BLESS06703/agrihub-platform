## 3.3 Livestock Management - Supplementary

### FR-051-A: Pig-Specific Registration
**Priority:** HIGH
**Description:** Extended registration fields for pig farming.
**Acceptance Criteria:**
- Additional fields for pigs: breed_type (LARGE_WHITE, LANDRACE, DUROC, HAMPSHIRE, CROSSBREED, LOCAL), purpose (FATTENING, BREEDING_SOW, BREEDING_BOAR)
- Sow parity number (number of litters produced)
- Farrowing cycle status: OPEN, GESTATING, FARROWING, LACTATING, WEANED
- Expected farrowing date (calculated as breeding_date + 114 days)
- Litter tracking: litter_id, number_born, number_alive, number_weaned

### FR-051-B: Pig Litter Recording
**Priority:** HIGH
**Description:** Record pig farrowing events and litter details.
**Acceptance Criteria:**
- Record: sow_tag_id, farrowing_date, total_born, born_alive, stillborn, mummified
- Birth weight per piglet (or average)
- Weaning record: weaning_date, number_weaned, weaning_weight_avg
- Pre-weaning mortality calculated
- Litter performance comparison across sows
- Sow productivity index: pigs_weaned/sow/year

### FR-057-A: Pig Health Management
**Priority:** HIGH
**Description:** Pig-specific health protocols and disease tracking.
**Acceptance Criteria:**
- Common pig diseases tracked: AFRICAN_SWINE_FEVER, FOOT_AND_MOUTH, ERYSIPELAS, PRRS, SCOURS, PNEUMONIA, MANGE, WORMS
- Biosecurity status per pig house/unit
- Quarantine flag for new or sick animals
- Vaccination schedule: Hog Cholera, FMD, Erysipelas
- Deworming schedule tracking
- Iron injection for piglets (day 3-5)
- Tail docking and teeth clipping record

### FR-058-A: Pig Weight Tracking
**Priority:** MEDIUM
**Description:** Growth monitoring for fattening pigs.
**Acceptance Criteria:**
- Weight targets by age: weaning (8 weeks), grower (14 weeks), finisher (20-24 weeks)
- Average Daily Gain (ADG) calculation
- Feed Conversion Ratio (FCR): kg_feed / kg_gain
- Alert if ADG below target (< 500g/day)
- Market weight alert (typically 90-110 kg)
- Growth chart comparing with breed standards

### FR-059-A: Pig Feed Management
**Priority:** MEDIUM
**Description:** Track feed types and consumption for pigs.
**Acceptance Criteria:**
- Feed types: CREEP_FEED, GROWER_MEAL, FINISHER_MEAL, SOW_MEAL, SUPPLEMENTS
- Feed intake recorded per pen or per pig
- Daily feed allocation and actual consumption
- Feed cost per kg of weight gain
- Ration formulation tracking
- Feed inventory integration

### FR-062-A: Pig Sales
**Priority:** HIGH
**Description:** Record pig sales with pricing details.
**Acceptance Criteria:**
- Sale types: SLAUGHTER, LIVE_WEANER, LIVE_GROWER, BREEDING_STOCK
- Pricing: per_kg_live_weight or fixed_price_per_head
- Weight at sale recorded
- Grade/quality: GRADE_A, GRADE_B, GRADE_C
- Buyer type: BUTCHER, ABATTOIR, FARMER, TRADER
- Pork price per kg tracking for market analysis

### FR-064-A: Pig Housing Management
**Priority:** LOW
**Description:** Track pig housing and pen management.
**Acceptance Criteria:**
- Pen/house registration with capacity
- Pen assignment for pigs
- Pen cleaning schedule
- Stocking density monitoring
- Pen status: OCCUPIED, EMPTY, CLEANING, MAINTENANCE

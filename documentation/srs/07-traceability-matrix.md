# 7. Requirements Traceability Matrix

## 7.1 Functional Requirements to Business Objectives

| Business Objective | Related FRs |
|-------------------|-------------|
| Digital farm records | FR-021 to FR-050 |
| Increase crop yields | FR-033, FR-035, FR-039, FR-048 |
| Reduce production costs | FR-081, FR-082, FR-084, FR-093 |
| Weather alerts | FR-029 |
| Disease detection | FR-037, FR-057, FR-057-A |
| Direct produce sales | FR-101 to FR-125 |
| Member management (Coops) | FR-006, FR-008, FR-122 |
| Warehouse stock tracking | FR-126 to FR-140 |
| Financial management | FR-081 to FR-100 |
| Government monitoring | FR-160 |
| Extension officer support | FR-007, FR-042 |
| Donor reporting | FR-159 |
| Equipment access | FR-044, FR-079 |
| Market price access | FR-113, FR-124 |

## 7.2 Functional Requirements to Use Cases

| Use Case | Primary FRs |
|----------|-------------|
| UC-001 (Registration) | FR-001, FR-006, FR-021 |
| UC-002 (Planting) | FR-026, FR-032, FR-033 |
| UC-003 (Harvest) | FR-038, FR-039 |
| UC-004 (Sell) | FR-101, FR-102 |
| UC-005 (Buy) | FR-103, FR-104, FR-106, FR-107 |
| UC-006 (Aggregation) | FR-122 |
| UC-007 (Extension) | FR-042, FR-160 |
| UC-008 (Vaccination) | FR-054 |
| UC-009 (Profitability) | FR-085, FR-093, FR-141 |
| UC-010 (Offline) | FR-016 |
| UC-011 (Donor Report) | FR-159 |
| UC-012 (Warehouse) | FR-126, FR-131 |
| UC-013 (Equipment) | FR-044 |
| UC-014 (Loan) | FR-087 |
| UC-015 (Gov Dashboard) | FR-160 |

## 7.3 Non-Functional Requirements Traceability

| NFR Category | Key NFRs | Applies To |
|--------------|----------|------------|
| Performance | NFR-001 to NFR-010 | All modules |
| Security | NFR-011 to NFR-025 | Auth, API, Database |
| Reliability | NFR-026 to NFR-035 | Backend, Infrastructure |
| Scalability | NFR-036 to NFR-045 | Database, Storage, API |
| Usability | NFR-046 to NFR-055 | Mobile, Web |
| Maintainability | NFR-056 to NFR-065 | Development |
| Compatibility | NFR-066 to NFR-072 | Mobile, Web, API |
| Compliance | NFR-073 to NFR-078 | All modules |

## 7.4 Version 1.0 Scope Summary

Total Requirements for V1.0:
- Platform Core: FR-001 to FR-020 (20 requirements)
- Farm Management: FR-021 to FR-050 (30 requirements)
- Livestock: FR-051 to FR-065 (15 requirements)
- Inventory: FR-066 to FR-080 (15 requirements)
- Finance: FR-081 to FR-100 (20 requirements)
- Marketplace: FR-101 to FR-125 (25 requirements)
- Warehouse: FR-126 to FR-140 (15 requirements)
- Reports: FR-141 to FR-160 (20 requirements)

Total FRs: 160
Total NFRs: 78
Total Use Cases: 15
Total User Stories: 64
Estimated Story Points: 428

# AgriHub Malawi
# System Requirements Specification (SRS)
# Version 1.0

## Document Control

| Field | Value |
|-------|-------|
| Document ID | AGR-HUB-SRS-001 |
| Version | 1.0 |
| Status | Draft |
| Created | 2026-07-04 |
| Author | AgriHub Engineering Team |
| Based on | AGR-HUB-BRD-001 |

## 1. Introduction

### 1.1 Purpose
This SRS defines the complete functional and non-functional requirements
for AgriHub Malawi. It serves as the single source of truth for developers,
testers, and stakeholders throughout the development lifecycle.

### 1.2 Scope
This document covers Version 1.0 of AgriHub Malawi, including:
- Android mobile application
- Web administration portal
- REST API backend
- Multi-tenant PostgreSQL database
- Offline-capable data synchronization

Future phases (AI modules, iOS, USSD) are identified but specified
in separate documents.

### 1.3 Definitions & Acronyms

| Term | Definition |
|------|------------|
| Tenant | A registered organization with fully isolated data |
| Farm | A physical agricultural location owned by a tenant |
| Field | A subdivision of a farm where crops are planted |
| Plot | The smallest GPS-mapped growing area within a field |
| Produce | Harvested crop ready for sale or storage |
| Lot | A batch of produce with uniform quality grade |
| RBAC | Role-Based Access Control |
| JWT | JSON Web Token |
| MFA | Multi-Factor Authentication |
| RPO | Recovery Point Objective |
| RTO | Recovery Time Objective |
| CRUD | Create, Read, Update, Delete |
| GPS | Global Positioning System |
| USSD | Unstructured Supplementary Service Data |

### 1.4 References
- AGR-HUB-BRD-001: Business Requirements Document
- AGR-HUB-ARCH-001: System Architecture Document (to be created)
- AGR-HUB-API-001: API Specification (to be created)
- AGR-HUB-DB-001: Database Design Document (to be created)

### 1.5 Overview
Section 2: System Architecture Overview
Section 3: Functional Requirements (FR-001 through FR-XXX)
Section 4: Non-Functional Requirements (NFR-001 through NFR-XXX)
Section 5: Use Cases
Section 6: User Stories
Section 7: System Constraints
Section 8: Acceptance Criteria
Section 9: Traceability Matrix
Section 10: Appendices

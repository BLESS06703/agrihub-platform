# AgriHub Malawi - Enterprise System Architecture
# Version 1.0

## 1. Architecture Overview

### 1.1 Architectural Style
Modular Monolith with clear domain boundaries, designed for future
extraction into microservices when scale demands it.

### 1.2 High-Level System Context

                          ┌──────────────────┐
                          │   External APIs   │
                          │  - Weather Data   │
                          │  - Market Prices  │
                          │  - Mobile Money   │
                          │  - SMS Gateway    │
                          └────────┬─────────┘
                                   │
┌──────────┐   ┌──────────┐   ┌───┴───────────┐
│ Android  │   │   Web    │   │  AgriHub      │
│   App    │───│  Admin   │───│  Backend      │
│ (Kotlin) │   │ (React)  │   │  (Ktor)       │
└──────────┘   └──────────┘   └───┬───────────┘
                                   │
                    ┌──────────────┼──────────────┐
                    │              │              │
              ┌─────┴─────┐ ┌────┴────┐ ┌──────┴──────┐
              │ PostgreSQL │ │  Redis  │ │   MinIO     │
              │  Database  │ │  Cache  │ │  Storage    │
              └───────────┘ └─────────┘ └─────────────┘

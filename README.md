# AgriHub Platform

Enterprise Multi-Country Agricultural SaaS Platform.

## Deployments

| Country | Status | Languages |
|---------|--------|-----------|
| Malawi | Ready | English, Chichewa |
| Zambia | Planned | English, Bemba |
| Tanzania | Planned | Swahili, English |
| Kenya | Planned | English, Swahili |
| Rwanda | Planned | English, French |
| Mozambique | Planned | Portuguese |

## Architecture

agrihub-platform/
  backend/               Ktor + Exposed + PostgreSQL
  mobile-android/        Jetpack Compose, 16 screens
  web-admin/             Admin dashboard, 12 pages
  database/              22 migrations, 169 tables
  documentation/         40+ docs
  infrastructure/        Docker, Nginx, Prometheus

## Tech Stack

Backend: Kotlin, Ktor, Exposed ORM
Database: PostgreSQL 16, PostGIS
Cache: Redis 7
Storage: MinIO
Auth: JWT RS256, Argon2id, RBAC, RLS
Mobile: Kotlin, Jetpack Compose, Material 3
Infrastructure: Docker, Nginx, Prometheus, Grafana
CI/CD: GitHub Actions

## Quick Start

Prerequisites: JDK 17, Docker, PostgreSQL client

git clone git@github.com:BLESS06703/agrihub-platform.git
cd agrihub-platform
./BUILD_AND_DEPLOY.sh

## Modules

Core: Identity, Platform, Tenants, Users, Roles
Agriculture: Farms, Fields, Crops, Planting, Harvests
Livestock: Animals, Vaccinations, Health, Breeding, RFID
Operations: Inventory, Warehouse, Marketplace
Finance: Income, Expenses, Budgets, Loans, P&L
Intelligence: AI Assistant, Disease Detection, Yield Prediction
Innovation: IoT, Satellite, Digital Twin, Blockchain
Platform: Notifications, Learning, Community, Banking, Carbon

## API (115 endpoints)

GET    /health
POST   /v1/auth/login
GET    /v1/farms
POST   /v1/farms
GET    /v1/livestock/animals
GET    /v1/finance/profit-loss
POST   /v1/ai/chat
POST   /v1/ai/disease

## Key Features

AI-powered farming assistant with disease detection
Satellite crop monitoring with NDVI analysis
IoT sensor integration for smart farming
Digital twin farm simulations
Blockchain supply chain provenance
Offline-first mobile sync for rural areas
Multi-tenant isolation (3 tiers)
Mobile money integration (Airtel, TNM, M-Pesa)
QR/RFID livestock tagging
Carbon credit trading
Agricultural learning hub and community forums
6 languages across 6 African countries

## Security

JWT RS256, 15-minute access tokens
Argon2id password hashing
RBAC with resource:action:scope
PostgreSQL Row-Level Security
Rate limiting, audit logging, API keys
All credentials via environment variables

## Documentation

40+ documents covering BRD, SRS, architecture, API standards, database design, security handbook, coding standards, DevOps handbook, design system

## License

Proprietary. All rights reserved.

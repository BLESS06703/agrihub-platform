#!/bin/bash
set -e

echo "========================================================="
echo " AGRIHUB PLATFORM - SETUP"
echo "========================================================="
echo ""

# Check prerequisites
command -v java >/dev/null 2>&1 || { echo "Java 17 required. Install: sdk install java 17.0.9-tem"; exit 1; }
command -v docker >/dev/null 2>&1 || { echo "Docker required. Install from docker.com"; exit 1; }
command -v psql >/dev/null 2>&1 || { echo "PostgreSQL client required."; exit 1; }

echo "[1/6] Starting infrastructure..."
docker-compose -f infrastructure/docker-compose.yml up -d postgres redis minio
sleep 5

echo "[2/6] Running database migrations..."
for f in database/migrations/V*.sql; do
    echo "  Running: $f"
    PGPASSWORD=${DB_PASSWORD:-changeme} psql -h localhost -U agrihub -d agrihub -f "$f" -q
done
echo "  All migrations applied."

echo "[3/6] Seeding data..."
PGPASSWORD=${DB_PASSWORD:-changeme} psql -h localhost -U agrihub -d agrihub -f database/seeds/crop_catalog.sql -q 2>/dev/null || echo "  Seed data skipped (file not found)"

echo "[4/6] Building backend..."
cd backend && ./gradlew shadowJar --no-daemon && cd ..
echo "  Backend built."

echo "[5/6] Starting services..."
docker-compose -f infrastructure/docker-compose.yml up -d
sleep 5

echo "[6/6] Verifying deployment..."
curl -s http://localhost:8080/health | head -c 200
echo ""

echo ""
echo "========================================================="
echo " SETUP COMPLETE"
echo "========================================================="
echo ""
echo "API:        http://localhost:8080/v1"
echo "Health:     http://localhost:8080/health"
echo "Grafana:    http://localhost:3000 (admin/admin)"
echo "MinIO:      http://localhost:9001"
echo "========================================================="

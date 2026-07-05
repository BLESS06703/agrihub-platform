#!/bin/sh
echo "AgriHub Platform - Deployment"
echo "=============================="
echo "1. Building backend..."
cd ../backend && ./gradlew shadowJar
echo "2. Building Docker images..."
docker-compose build
echo "3. Running migrations..."
docker-compose run backend java -jar app.jar migrate
echo "4. Starting services..."
docker-compose up -d
echo "5. Health check..."
sleep 10
curl -s http://localhost:8080/health | head -c 200
echo ""
echo "Deployment complete. AgriHub Platform is live."

#!/bin/bash
set -e

BACKUP_DIR="/var/backups/agrihub"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
RETENTION_DAYS=30

mkdir -p "$BACKUP_DIR"

echo "[1/3] Backing up PostgreSQL..."
PGPASSWORD=${DB_PASSWORD} pg_dump -h localhost -U agrihub agrihub | gzip > "$BACKUP_DIR/db_${TIMESTAMP}.sql.gz"

echo "[2/3] Backing up MinIO storage..."
tar -czf "$BACKUP_DIR/storage_${TIMESTAMP}.tar.gz" /data/minio/agrihub/

echo "[3/3] Cleaning old backups..."
find "$BACKUP_DIR" -type f -mtime +$RETENTION_DAYS -delete

echo "Backup complete: $BACKUP_DIR/db_${TIMESTAMP}.sql.gz"
echo "Backup complete: $BACKUP_DIR/storage_${TIMESTAMP}.tar.gz"

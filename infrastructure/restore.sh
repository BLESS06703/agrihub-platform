#!/bin/bash
set -e

if [ -z "$1" ]; then
    echo "Usage: restore.sh <backup_timestamp>"
    echo "Example: restore.sh 20260705_120000"
    exit 1
fi

BACKUP_DIR="/var/backups/agrihub"
TIMESTAMP=$1

echo "[1/2] Restoring PostgreSQL..."
gunzip -c "$BACKUP_DIR/db_${TIMESTAMP}.sql.gz" | PGPASSWORD=${DB_PASSWORD} psql -h localhost -U agrihub agrihub

echo "[2/2] Restoring MinIO storage..."
tar -xzf "$BACKUP_DIR/storage_${TIMESTAMP}.tar.gz" -C /

echo "Restore complete from: $TIMESTAMP"

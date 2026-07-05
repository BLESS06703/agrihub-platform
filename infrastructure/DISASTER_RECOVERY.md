# AgriHub Platform - Disaster Recovery Runbook

## RTO: 4 hours | RPO: 1 hour

## Scenario 1: Database Failure
1. Verify failure: `psql -h localhost -U agrihub -c "SELECT 1"`
2. Check replication status: `psql -c "SELECT * FROM pg_stat_replication"`
3. Promote replica if needed: `pg_ctl promote`
4. Restore from backup if both fail: `./restore.sh <latest_timestamp>`
5. Verify: `curl http://localhost:8080/health`
6. Notify: Slack channel #agrihub-alerts

## Scenario 2: Application Crash
1. Check logs: `docker logs agrihub-backend`
2. Restart: `docker-compose restart backend`
3. Rollback if needed: `docker-compose up -d backend:<previous_version>`
4. Verify health endpoint

## Scenario 3: Storage Failure
1. Check MinIO: `curl http://localhost:9000/minio/health/live`
2. Restart: `docker-compose restart minio`
3. Restore from backup: `./restore.sh <timestamp>`

## Scenario 4: Full Region Failure
1. Deploy to secondary region using Terraform
2. Update DNS: `agrihub.mw` -> secondary load balancer
3. Restore database from offsite backup
4. Verify all services
5. Communicate to users via email/SMS

## Emergency Contacts
- Lead Engineer: [phone]
- DevOps: [phone]
- Database Admin: [phone]

## Verification Checklist
- [ ] Database accessible
- [ ] API responding
- [ ] Mobile app connecting
- [ ] Web admin loading
- [ ] File uploads working
- [ ] Notifications sending

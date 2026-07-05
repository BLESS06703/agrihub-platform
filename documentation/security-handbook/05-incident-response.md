# AgriHub Malawi - Incident Response Plan

## 1. Incident Severity Levels

| Level | Definition | Response Time | Example |
|-------|-----------|---------------|---------|
| P0 | Critical | Immediate (any time) | Cross-tenant data leak, JWT key compromise |
| P1 | High | Within 1 hour | Service down, SQL injection found |
| P2 | Medium | Within 4 hours | Suspicious activity, elevated error rate |
| P3 | Low | Within 24 hours | Minor bug, cosmetic issue |

## 2. Response Process

### Detection
- Prometheus alerts → on-call notification
- User reports → support ticket
- Automated security scanning
- Audit log anomalies

### Containment (P0/P1)
1. Acknowledge alert within 15 minutes
2. Assess scope and impact
3. Isolate affected systems (revoke tokens, block IPs, take instance offline)
4. Preserve evidence (logs, database snapshots)
5. Notify security lead

### Investigation
1. Determine root cause
2. Identify all affected tenants/users
3. Document timeline
4. Assess data exposure

### Remediation
1. Apply fix
2. Test in staging
3. Deploy to production
4. Verify fix works
5. Rotate compromised credentials

### Recovery
1. Restore from backup if needed
2. Re-enable services
3. Monitor for 24 hours

### Post-Incident
1. Write incident report within 48 hours
2. Conduct blameless post-mortem
3. Update runbooks and monitoring
4. Schedule follow-up improvements

## 3. Communication Template

For P0/P1 incidents affecting users:

"AgriHub is investigating [issue description]. 
Affected: [scope]. 
We will update within [timeframe]. 
Contact: [support contact]."

## 4. Emergency Contacts

| Role | Contact |
|------|---------|
| Security Lead | [to be defined] |
| Backend Lead | [to be defined] |
| DevOps Lead | [to be defined] |
| Legal/Compliance | [to be defined] |

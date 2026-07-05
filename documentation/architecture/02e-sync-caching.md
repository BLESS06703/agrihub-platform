## 6. Offline Sync Architecture

### 6.1 Sync Engine Design

              ANDROID DEVICE
   ┌──────────┐  ┌──────────┐  ┌───────────┐
   │ Room DB  │  │DataStore │  │Sync Engine│
   │(SQLite)  │  │(Prefs)   │  │           │
   └────┬─────┘  └────┬─────┘  └─────┬─────┘
        │             │              │
   ┌────┴─────────────┴──────────────┴─────┐
   │          Offline Queue                │
   │  [CREATE farm] [UPDATE field] ...    │
   └────────────────┬──────────────────────┘
                    │ Network Available?
                    │
            BACKEND SYNC API
   POST /v1/sync/push     (upload changes)
   GET  /v1/sync/pull     (download changes)

Conflict Resolution:
- Financial: Server wins
- Farm data: Timestamp merge
- Inventory: Last-write-wins + notify

### 6.2 Sync Protocol

PUSH (Device -> Server):
{
  "device_id": "device-uuid",
  "last_sync_timestamp": "2026-07-04T10:00:00Z",
  "changes": [
    {
      "id": "local-uuid",
      "table": "farms",
      "operation": "CREATE",
      "data": { "name": "New Farm" },
      "client_timestamp": "2026-07-04T10:05:00Z"
    }
  ]
}

PULL (Server -> Device):
GET /v1/sync/pull?since=2026-07-04T10:00:00Z
Response: { "changes": [...], "server_timestamp": "..." }

## 7. Caching Strategy

### 7.1 Cache Layers

L1: Caffeine (In-Memory)
- TTL: 5 minutes, Max: 100MB
- For: tenant config, roles, crop catalog

L2: Redis (Distributed)
- TTL: 15 minutes
- For: sessions, rate limiting, market prices

L3: PostgreSQL
- Source of truth

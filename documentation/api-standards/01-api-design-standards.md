# AgriHub Malawi - API Design Standards
# Version 1.0

## 1. Base URL

Production: https://api.agrihub.mw/v1
Staging: https://staging-api.agrihub.mw/v1
Development: http://localhost:8080/v1

## 2. URL Conventions

- Lowercase only
- Hyphens for multi-word resources (not underscores)
- Plural nouns for collections
- No verbs in URLs (use HTTP methods)
- Nested resources max 2 levels deep

Examples:
GET    /v1/farms
POST   /v1/farms
GET    /v1/farms/{farmId}
PUT    /v1/farms/{farmId}
DELETE /v1/farms/{farmId}

GET    /v1/farms/{farmId}/fields
POST   /v1/farms/{farmId}/fields
GET    /v1/farms/{farmId}/fields/{fieldId}

GET    /v1/farms/{farmId}/fields/{fieldId}/planting-records

## 3. HTTP Methods

| Method | Action | Idempotent | Safe |
|--------|--------|------------|------|
| GET | Read | Yes | Yes |
| POST | Create | No | No |
| PUT | Full update | Yes | No |
| PATCH | Partial update | No | No |
| DELETE | Soft delete | Yes | No |

## 4. Response Format

### Success (200/201)
{
  "success": true,
  "data": { ... },
  "meta": {
    "page": 1,
    "per_page": 20,
    "total": 150,
    "total_pages": 8
  },
  "timestamp": "2026-07-04T12:00:00Z"
}

### Success (List)
{
  "success": true,
  "data": [ ... ],
  "meta": {
    "page": 1,
    "per_page": 20,
    "total": 150,
    "total_pages": 8
  },
  "timestamp": "2026-07-04T12:00:00Z"
}

### Error
{
  "success": false,
  "data": null,
  "errors": [
    {
      "code": "VALIDATION_ERROR",
      "field": "farm_name",
      "message": "Farm name is required",
      "details": {}
    }
  ],
  "timestamp": "2026-07-04T12:00:00Z",
  "correlation_id": "550e8400-e29b-41d4-a716-446655440000"
}

## 5. HTTP Status Codes

| Code | Usage |
|------|-------|
| 200 | OK - Request succeeded |
| 201 | Created - Resource created |
| 204 | No Content - Delete successful |
| 400 | Bad Request - Invalid input |
| 401 | Unauthenticated - Missing/invalid token |
| 403 | Forbidden - Insufficient permissions |
| 404 | Not Found - Resource doesn't exist |
| 409 | Conflict - Duplicate or sync conflict |
| 422 | Unprocessable Entity - Validation error |
| 429 | Too Many Requests - Rate limited |
| 500 | Internal Server Error |

## 6. Pagination

Request:
GET /v1/farms?page=1&per_page=20

Response includes:
- meta.page: current page
- meta.per_page: items per page
- meta.total: total records
- meta.total_pages: total pages

Headers:
X-Total-Count: 150
Link: </v1/farms?page=2>; rel="next"

Default: page=1, per_page=20
Maximum: per_page=100

## 7. Filtering

GET /v1/farms?status=ACTIVE&district=Mzimba&area_min=2.5&area_max=10

Conventions:
- Exact match: field=value
- Range: field_min=X&field_max=Y
- Search: q=searchterm
- Date: date_from=2026-01-01&date_to=2026-12-31
- IN: field=val1,val2,val3

## 8. Sorting

GET /v1/farms?sort=name&order=asc
GET /v1/farms?sort=-created_at  (descending with - prefix)

Default sort varies by resource.

## 9. Field Selection (Sparse Fieldsets)

GET /v1/farms?fields=id,name,area_hectares,status

## 10. Versioning

URL-based: /v1/, /v2/
Deprecation: 6 months via Sunset header
Sunset: Sat, 31 Dec 2026 23:59:59 GMT
Deprecation: true header on old versions

## 11. Authentication

All endpoints (except /health, /auth/*):
Authorization: Bearer <jwt_token>

## 12. Rate Limiting

Headers on every response:
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 95
X-RateLimit-Reset: 1719000300

429 when exceeded with Retry-After header.

## 13. CORS

Allowed Origins: https://admin.agrihub.mw
Allowed Methods: GET, POST, PUT, PATCH, DELETE, OPTIONS
Allowed Headers: Authorization, Content-Type, Accept-Language
Max Age: 86400 (24 hours)

## 14. Content Types

Request: application/json
Response: application/json
File upload: multipart/form-data
File download: application/octet-stream or specific MIME

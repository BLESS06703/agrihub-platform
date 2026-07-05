# AgriHub Platform - RBAC Permission Matrix

## Roles

TENANT_OWNER - Full control of tenant
FARM_MANAGER - Farm operations management
FARMER - Daily farming activities
AGRONOMIST - Crop advisory
VET_OFFICER - Livestock health management
EXTENSION_OFFICER - Government field support
ACCOUNTANT - Financial operations
WAREHOUSE_MANAGER - Storage management
BUYER - Marketplace procurement
DATA_ANALYST - Reports and analytics
VIEWER - Read-only access

## Permission Format

resource:action:scope

Resources: farm, field, crop, harvest, livestock, animal,
           finance, inventory, marketplace, warehouse, report,
           user, tenant, settings

Actions: create, read, update, delete, approve, export, manage

Scopes: own (user's records), tenant (all in tenant), all (platform-wide)

## Farm Management Permissions

Permission          OWNER   MGR   FARMER  AGRON  EXT   VIEWER
farm:create:own       X      X      X      -      -      -
farm:read:tenant      X      X      X      X      X      X
farm:update:own       X      X      X      -      -      -
farm:delete:own       X      X      -      -      -      -
field:create:own      X      X      X      -      -      -
field:read:tenant     X      X      X      X      X      X
planting:create:own   X      X      X      -      -      -
planting:read:tenant  X      X      X      X      X      X
harvest:create:own    X      X      X      -      -      -
harvest:read:tenant   X      X      X      X      X      X
input:create:own      X      X      X      -      -      -
input:read:tenant     X      X      X      X      X      X

## Livestock Permissions

Permission           OWNER   MGR   FARMER  VET    VIEWER
animal:create:own     X      X      X      -       -
animal:read:tenant    X      X      X      X       X
animal:update:own     X      X      X      -       -
vaccine:create:own    X      X      X      X       -
vaccine:read:tenant   X      X      X      X       X
health:create:own     X      X      X      X       -
health:read:tenant    X      X      X      X       X

## Finance Permissions

Permission            OWNER   ACCT   MGR    FARMER  VIEWER
income:create:own      X      X      -       -       -
income:read:tenant     X      X      X       X       X
expense:create:own     X      X      -       -       -
expense:read:tenant    X      X      X       X       X
finance:approve        X      X      -       -       -
report:export:tenant   X      X      X       -       X

## Marketplace Permissions

Permission            OWNER   MGR   FARMER  BUYER   VIEWER
listing:create:own     X      X      X       -       -
listing:read:tenant    X      X      X       X       X
offer:create:own       -      -      -       X       -
offer:read:own         -      -      X       X       -
contract:manage:own    X      X      X       X       -
order:read:tenant      X      X      X       X       X

## Legend

X = Has permission
- = No access
own = Only records created by this user
tenant = All records in the tenant

## Example Permission Checks

User with FARMER role tries GET /v1/farms
- Requires: farm:read:tenant
- Farmer has this permission -> ALLOWED

User with VIEWER role tries POST /v1/farms
- Requires: farm:create:own
- Viewer lacks this permission -> 403 FORBIDDEN

User from Tenant A tries GET /v1/farms/123 (belongs to Tenant B)
- Tenant context mismatch -> 404 NOT FOUND (don't reveal existence)

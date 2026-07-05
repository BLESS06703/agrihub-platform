-- Demo tenant: Mzuzu Coffee Cooperative
INSERT INTO tenants (id, name, slug, tenant_type, tenant_tier, country_code, status, subscription_tier, max_users) VALUES
('00000000-0000-0000-0000-000000000001', 'Mzuzu Coffee Cooperative', 'mzuzu-coffee', 'COOPERATIVE', 'SCHEMA', 'MW', 'ACTIVE', 'PRO', 100);

INSERT INTO users (id, tenant_id, email, full_name, password_hash, status, email_verified) VALUES
('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 'scott@mzuzucoffee.mw', 'Scott Manda', 'CHANGE_ME_REPLACE_WITH_REAL_HASH', 'ACTIVE', TRUE);

INSERT INTO farms (id, tenant_id, name, area_hectares, district, status, created_by) VALUES
('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', 'Mzuzu Coffee Farm', 12.5, 'Mzimba', 'ACTIVE', '00000000-0000-0000-0000-000000000002');

INSERT INTO fields (id, tenant_id, farm_id, field_name, area_hectares, status, created_by) VALUES
('00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003', 'Field A', 4.2, 'PLANTED', '00000000-0000-0000-0000-000000000002'),
('00000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003', 'Field B', 3.8, 'PLANTED', '00000000-0000-0000-0000-000000000002');

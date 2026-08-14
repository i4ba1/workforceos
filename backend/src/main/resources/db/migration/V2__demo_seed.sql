INSERT INTO tenant (id, code, name, default_zone, locale, status, retention_days)
VALUES ('00000000-0000-0000-0000-000000000001', 'DEMO', 'Global Industrial Demo', 'Asia/Jakarta', 'en', 'ACTIVE', 365)
ON CONFLICT (id) DO NOTHING;

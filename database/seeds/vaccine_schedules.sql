INSERT INTO vaccine_schedules (id, species, vaccine_name, age_days, booster_days, is_mandatory) VALUES
(uuid_generate_v4(), 'CATTLE', 'Foot and Mouth Disease', 180, 180, TRUE),
(uuid_generate_v4(), 'CATTLE', 'Anthrax', 180, 365, TRUE),
(uuid_generate_v4(), 'CATTLE', 'Blackleg', 90, 365, TRUE),
(uuid_generate_v4(), 'GOAT', 'Peste des Petits Ruminants', 90, 365, TRUE),
(uuid_generate_v4(), 'PIG', 'Hog Cholera', 60, 180, TRUE),
(uuid_generate_v4(), 'CHICKEN_BROILER', 'Newcastle Disease', 14, 90, TRUE),
(uuid_generate_v4(), 'CHICKEN_LAYER', 'Newcastle Disease', 14, 60, TRUE),
(uuid_generate_v4(), 'CHICKEN_LAYER', 'Fowl Pox', 42, 365, FALSE);

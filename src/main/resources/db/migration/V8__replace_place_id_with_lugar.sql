-- Reemplaza place_id (UUID, referencia a place-service) por lugar (VARCHAR, código de zona de geo-service)
-- Ej: "ED_A", "FOOD_PLANET", "SPORT_FUTBOL"
-- Los parches existentes quedan con lugar = NULL

ALTER TABLE parches ADD COLUMN lugar VARCHAR(50);
ALTER TABLE parches DROP COLUMN place_id;

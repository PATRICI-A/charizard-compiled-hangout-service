-- Reemplaza la columna place (VARCHAR, texto libre) por place_id (UUID, referencia a place-service)
-- Los parches existentes quedan con place_id = NULL (dato histórico no migrable)

ALTER TABLE parches ADD COLUMN place_id UUID;
ALTER TABLE parches DROP COLUMN place;

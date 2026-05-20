-- V6: Rename captain_id to owner_id in parches
-- invitations.captain_id → inviter_id is handled by V5

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'parches' AND column_name = 'captain_id'
    ) THEN
        ALTER TABLE parches RENAME COLUMN captain_id TO owner_id;
    END IF;
END $$;

-- Rename index only if it still exists with old name
ALTER INDEX IF EXISTS idx_parches_captain_id RENAME TO idx_parches_owner_id;

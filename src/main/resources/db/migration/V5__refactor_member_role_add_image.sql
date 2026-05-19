-- V5: Remove member_role, add image_url to parches, fix quota min, rename captain_id in invitations

-- 1. Drop member_role from members
ALTER TABLE members DROP CONSTRAINT IF EXISTS chk_members_role;
ALTER TABLE members DROP COLUMN IF EXISTS member_role;

-- 2. Add image_url to parches (nullable)
ALTER TABLE parches ADD COLUMN IF NOT EXISTS image_url VARCHAR(500);

-- 3. Fix maximumQuota constraint: allow 1 instead of 2
ALTER TABLE parches DROP CONSTRAINT IF EXISTS parches_maximum_quota_check;
ALTER TABLE parches ADD CONSTRAINT chk_parches_quota CHECK (maximum_quota >= 1 AND maximum_quota <= 30);

-- 4. Rename captain_id to inviter_id in invitations
ALTER TABLE invitations RENAME COLUMN captain_id TO inviter_id;

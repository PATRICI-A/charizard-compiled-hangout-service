-- =============================================
-- V2__create_invitaciones_table.sql
-- =============================================

-- TABLE: invitations
CREATE TABLE invitations (
    id                  UUID            NOT NULL DEFAULT gen_random_uuid(),
    parche_id           UUID            NOT NULL,
    captain_id          UUID            NOT NULL,
    invited_student_id  UUID            NOT NULL,
    status              VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    sent_at             TIMESTAMP       NOT NULL,
    responded_at        TIMESTAMP,

    CONSTRAINT pk_invitations                  PRIMARY KEY (id),
    CONSTRAINT fk_invitations_parche           FOREIGN KEY (parche_id) REFERENCES parches (id),
    CONSTRAINT uq_invitations_parche_student   UNIQUE (parche_id, invited_student_id),
    CONSTRAINT chk_invitations_status          CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED'))
);

-- INDEXES: invitations
CREATE INDEX idx_invitations_parche_id        ON invitations (parche_id);
CREATE INDEX idx_invitations_student_id       ON invitations (invited_student_id);
CREATE INDEX idx_invitations_status           ON invitations (status);

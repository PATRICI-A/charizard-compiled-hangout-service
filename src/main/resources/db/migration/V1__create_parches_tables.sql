-- =============================================
-- V1__create_parches_tables.sql
-- =============================================

-- TABLE: parches
CREATE TABLE parches (
                         id              UUID            NOT NULL DEFAULT gen_random_uuid(),
                         name            VARCHAR(100)    NOT NULL,
                         description     VARCHAR(500),
                         type            VARCHAR(20)     NOT NULL,
                         maximum_quota   INTEGER         NOT NULL CHECK (maximum_quota >= 2 AND maximum_quota <= 30),
                         date_realization TIMESTAMP      NOT NULL,
                         status          VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE',
                         captain_id      UUID            NOT NULL,
                         creation_date   TIMESTAMP       NOT NULL DEFAULT now(),

                         CONSTRAINT pk_parches PRIMARY KEY (id),
                         CONSTRAINT chk_parches_type   CHECK (type   IN ('PUBLIC', 'PRIVATE')),
                         CONSTRAINT chk_parches_status CHECK (status IN ('ACTIVE', 'FILED'))
);

-- TABLE: members
CREATE TABLE members (
                         id            UUID        NOT NULL DEFAULT gen_random_uuid(),
                         parche_id     UUID        NOT NULL,
                         student_id    UUID        NOT NULL,
                         union_date    TIMESTAMP   NOT NULL DEFAULT now(),
                         member_role   VARCHAR(20) NOT NULL,

                         CONSTRAINT pk_members            PRIMARY KEY (id),
                         CONSTRAINT fk_members_parche     FOREIGN KEY (parche_id) REFERENCES parches (id),
                         CONSTRAINT uq_members_parche_student UNIQUE (parche_id, student_id),
                         CONSTRAINT chk_members_role      CHECK (member_role IN ('CAPTAIN', 'STUDENT'))
);

-- INDEXES: parches
CREATE INDEX idx_parches_captain_id       ON parches (captain_id);
CREATE INDEX idx_parches_status           ON parches (status);
CREATE INDEX idx_parches_date_realization ON parches (date_realization);

-- INDEXES: members
CREATE INDEX idx_members_student_id ON members (student_id);

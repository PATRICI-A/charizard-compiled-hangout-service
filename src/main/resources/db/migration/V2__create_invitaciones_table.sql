-- =============================================
-- V2__create_invitaciones_table.sql
-- =============================================

-- TABLE: invitaciones
CREATE TABLE invitaciones (
    id                      UUID            NOT NULL DEFAULT gen_random_uuid(),
    parche_id               UUID            NOT NULL,
    capitan_id              UUID            NOT NULL,
    estudiante_invitado_id  UUID            NOT NULL,
    estado                  VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    fecha_envio             TIMESTAMP       NOT NULL,
    fecha_respuesta         TIMESTAMP,

    CONSTRAINT pk_invitaciones PRIMARY KEY (id),
    CONSTRAINT fk_invitaciones_parche FOREIGN KEY (parche_id) REFERENCES parches (id),
    CONSTRAINT uq_invitaciones_parche_estudiante UNIQUE (parche_id, estudiante_invitado_id),
    CONSTRAINT chk_invitaciones_estado CHECK (estado IN ('PENDING', 'ACCEPTED', 'REJECTED'))
);

-- INDEXES: invitaciones
CREATE INDEX idx_invitaciones_parche_id ON invitaciones (parche_id);
CREATE INDEX idx_invitaciones_estudiante_id ON invitaciones (estudiante_invitado_id);
CREATE INDEX idx_invitaciones_estado ON invitaciones (estado);

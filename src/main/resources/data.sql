-- ═══════════════════════════════════════════════════════════════════
--  Seed Data — H2 Development Database
--  Charizard Compilado · PATRICI.A · DOSW 2026
-- ═══════════════════════════════════════════════════════════════════

-- Capitán (usa este UUID en Swagger → Authorize → X-User-Id)
-- UUID: 00000000-0000-0000-0000-000000000001

-- Estudiante de prueba
-- UUID: 00000000-0000-0000-0000-000000000002

-- ── Parche Público: "Parche de café" ──
INSERT INTO parches (
    id, name, description, place, type, date, hour,
    maximum_quota, date_realization, status, captain_id, creation_date
) VALUES (
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    'Parche de café',
    'Café y conversación en la plazoleta',
    'Café del edificio Bernardo',
    'PUBLIC',
    CURRENT_DATE() + 7,
    '14:00:00',
    10,
    TIMESTAMPADD(DAY, 7, CURRENT_TIMESTAMP()),
    'ACTIVE',
    '00000000-0000-0000-0000-000000000001',
    CURRENT_TIMESTAMP()
);

-- ── Parche Privado: "Estudio para parcial" ──
INSERT INTO parches (
    id, name, description, place, type, date, hour,
    maximum_quota, date_realization, status, captain_id, creation_date
) VALUES (
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    'Estudio para parcial de cálculo',
    'Repaso grupal de integrales y derivadas',
    'Sala de estudio 302, Biblioteca',
    'PRIVATE',
    CURRENT_DATE() + 3,
    '10:00:00',
    5,
    TIMESTAMPADD(DAY, 3, CURRENT_TIMESTAMP()),
    'ACTIVE',
    '00000000-0000-0000-0000-000000000001',
    CURRENT_TIMESTAMP()
);

-- ── Parche Público: "Partido de fútbol" ──
INSERT INTO parches (
    id, name, description, place, type, date, hour,
    maximum_quota, date_realization, status, captain_id, creation_date
) VALUES (
    'cccccccc-cccc-cccc-cccc-cccccccccccc',
    'Partido de fútbol',
    'Amistoso en las canchas de la ECI',
    'Canchas de la escuela',
    'PUBLIC',
    CURRENT_DATE() + 10,
    '16:00:00',
    20,
    TIMESTAMPADD(DAY, 10, CURRENT_TIMESTAMP()),
    'ACTIVE',
    '00000000-0000-0000-0000-000000000001',
    CURRENT_TIMESTAMP()
);

-- ── Members: Capitán registrado en los parches que creó ──
INSERT INTO members (id, parche_id, student_id, union_date, member_role)
VALUES (
    'd0000000-0000-0000-0000-000000000001',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    '00000000-0000-0000-0000-000000000001',
    CURRENT_TIMESTAMP(),
    'CAPTAIN'
);

INSERT INTO members (id, parche_id, student_id, union_date, member_role)
VALUES (
    'd0000000-0000-0000-0000-000000000002',
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    '00000000-0000-0000-0000-000000000001',
    CURRENT_TIMESTAMP(),
    'CAPTAIN'
);

INSERT INTO members (id, parche_id, student_id, union_date, member_role)
VALUES (
    'd0000000-0000-0000-0000-000000000003',
    'cccccccc-cccc-cccc-cccc-cccccccccccc',
    '00000000-0000-0000-0000-000000000001',
    CURRENT_TIMESTAMP(),
    'CAPTAIN'
);

-- ── Invitación pendiente al parche privado ──
INSERT INTO invitaciones (
    id, parche_id, capitan_id, estudiante_invitado_id,
    status, fecha_envio, fecha_respuesta
) VALUES (
    'e0000000-0000-0000-0000-000000000001',
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    '00000000-0000-0000-0000-000000000001',
    '00000000-0000-0000-0000-000000000002',
    'PENDING',
    CURRENT_TIMESTAMP(),
    NULL
);

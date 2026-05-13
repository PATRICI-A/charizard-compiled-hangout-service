-- =============================================
-- V4__seed_test_data.sql
-- Seed data for development / testing
-- =============================================
-- Usa estos UUIDs en Swagger (X-User-Id header):
--   00000000-0000-0000-0000-000000000001  →  capitán de prueba
--   00000000-0000-0000-0000-000000000002  →  estudiante de prueba

-- ── Parche público 1: "Parche de café" ──
INSERT INTO parches (id, name, description, place, category, type, date, hour, maximum_quota, status, captain_id, creation_date)
VALUES (
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    'Parche de café',
    'Café y conversación en la plazoleta',
    'Café del edificio Bernardo',
    'MUSIC',
    'PUBLIC',
    CURRENT_DATE + 7,
    '14:00:00'::time,
    10,
    'ACTIVE',
    '00000000-0000-0000-0000-000000000001',
    NOW()
);

-- ── Parche privado: "Estudio para parcial" ──
INSERT INTO parches (id, name, description, place, category, type, date, hour, maximum_quota, status, captain_id, creation_date)
VALUES (
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    'Estudio para parcial de cálculo',
    'Repaso grupal de integrales y derivadas',
    'Sala de estudio 302, Biblioteca',
    'PROGRAMMING',
    'PRIVATE',
    CURRENT_DATE + 3,
    '10:00:00'::time,
    5,
    'ACTIVE',
    '00000000-0000-0000-0000-000000000001',
    NOW()
);

-- ── Parche público 2: "Partido de fútbol" ──
INSERT INTO parches (id, name, description, place, category, type, date, hour, maximum_quota, status, captain_id, creation_date)
VALUES (
    'cccccccc-cccc-cccc-cccc-cccccccccccc',
    'Partido de fútbol',
    'Amistoso en las canchas de la ECI',
    'Canchas de la escuela',
    'SOCCER',
    'PUBLIC',
    CURRENT_DATE + 10,
    '16:00:00'::time,
    20,
    'ACTIVE',
    '00000000-0000-0000-0000-000000000001',
    NOW()
);

-- ── Membresías: capitán en sus parches ──
INSERT INTO members (id, parche_id, student_id, union_date, member_role)
VALUES (
    'd0000000-0000-0000-0000-000000000001',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    '00000000-0000-0000-0000-000000000001',
    NOW(),
    'CAPTAIN'
);

INSERT INTO members (id, parche_id, student_id, union_date, member_role)
VALUES (
    'd0000000-0000-0000-0000-000000000002',
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    '00000000-0000-0000-0000-000000000001',
    NOW(),
    'CAPTAIN'
);

INSERT INTO members (id, parche_id, student_id, union_date, member_role)
VALUES (
    'd0000000-0000-0000-0000-000000000003',
    'cccccccc-cccc-cccc-cccc-cccccccccccc',
    '00000000-0000-0000-0000-000000000001',
    NOW(),
    'CAPTAIN'
);

-- ── Membresía: estudiante 2 se unió al parche público 1 ──
INSERT INTO members (id, parche_id, student_id, union_date, member_role)
VALUES (
    'd0000000-0000-0000-0000-000000000004',
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    '00000000-0000-0000-0000-000000000002',
    NOW(),
    'STUDENT'
);

-- ── Invitación pendiente al parche privado ──
INSERT INTO invitations (id, parche_id, captain_id, invited_student_id, status, sent_at, responded_at)
VALUES (
    'e0000000-0000-0000-0000-000000000001',
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    '00000000-0000-0000-0000-000000000001',
    '00000000-0000-0000-0000-000000000002',
    'PENDING',
    NOW(),
    NULL
);

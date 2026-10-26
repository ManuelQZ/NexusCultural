-- Datos de prueba para tests de repositorio JPA

-- Insertar pedidos de prueba
INSERT INTO pedidos (id, comprador_id, articulo_id, cantidad, precio_monto, precio_moneda, estado, fecha_creacion, version)
VALUES
    ('550e8400-e29b-41d4-a716-446655440001', '110e8400-e29b-41d4-a716-446655440001', '220e8400-e29b-41d4-a716-446655440001', 2, 30000.00, 'COP', 'PENDIENTE', '2026-10-20 10:00:00', 0),
    ('550e8400-e29b-41d4-a716-446655440002', '110e8400-e29b-41d4-a716-446655440001', '220e8400-e29b-41d4-a716-446655440002', 1, 45000.00, 'COP', 'CONFIRMADO', '2026-10-21 11:00:00', 0),
    ('550e8400-e29b-41d4-a716-446655440003', '110e8400-e29b-41d4-a716-446655440002', '220e8400-e29b-41d4-a716-446655440001', 3, 45000.00, 'COP', 'ENTREGADO', '2026-10-22 12:00:00', 0),
    ('550e8400-e29b-41d4-a716-446655440004', '110e8400-e29b-41d4-a716-446655440002', '220e8400-e29b-41d4-a716-446655440003', 1, 15000.00, 'COP', 'ENTREGADO', '2026-10-23 13:00:00', 0),
    ('550e8400-e29b-41d4-a716-446655440005', '110e8400-e29b-41d4-a716-446655440003', '220e8400-e29b-41d4-a716-446655440001', 1, 15000.00, 'COP', 'EN_CAMINO', '2026-10-24 14:00:00', 0);

-- Insertar devoluciones de prueba
INSERT INTO devoluciones (id, pedido_id, motivo, fecha_solicitud, estado)
VALUES
    ('660e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440003', 'Producto llegó dañado', '2026-10-25 09:00:00', 'EN_PROCESO');

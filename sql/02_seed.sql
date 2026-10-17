-- 02_seed.sql
-- Тестовые данные. Идемпотентно: повторный запуск не создаёт дубликатов.

INSERT INTO departments(name, description) VALUES
    ('Первая линия', 'Приём и первичная обработка обращений'),
    ('Сетевые инженеры', 'Сеть, Wi-Fi, VPN, маршрутизация'),
    ('Системные администраторы', 'Серверы и рабочие станции')
ON CONFLICT (name) DO NOTHING;

INSERT INTO categories(name, description, default_priority) VALUES
    ('Сеть', 'Wi-Fi, LAN, VPN, доступ в Интернет', 'HIGH'),
    ('Программное обеспечение', 'Ошибки и установка ПО', 'MEDIUM'),
    ('Оборудование', 'ПК, принтеры и периферия', 'MEDIUM'),
    ('Доступы', 'Учётные записи и права', 'HIGH')
ON CONFLICT (name) DO NOTHING;

INSERT INTO users(name, email, role, department_id) VALUES
    ('Анна Петрова', 'anna@example.org', 'CUSTOMER', NULL),
    ('Иван Соколов', 'ivan.support@example.org', 'SUPPORT_AGENT', 1),
    ('Сергей Волков', 'sergey.net@example.org', 'SUPPORT_AGENT', 2),
    ('Олег Админов', 'admin@example.org', 'ADMIN', 3)
ON CONFLICT (email) DO NOTHING;

INSERT INTO tickets(
    title,
    description,
    status,
    priority,
    customer_id,
    category_id
)
SELECT
    'Не работает Wi-Fi',
    'Ноутбук подключается к сети, но Интернет недоступен',
    'NEW',
    'HIGH',
    (SELECT id FROM users WHERE email = 'anna@example.org'),
    (SELECT id FROM categories WHERE name = 'Сеть')
WHERE NOT EXISTS (SELECT 1 FROM tickets);

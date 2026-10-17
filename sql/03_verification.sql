-- 03_verification.sql
-- Проверочные запросы для преподавателя (пункт 37 методички).

\echo '=== tickets ==='
SELECT * FROM tickets ORDER BY id DESC;

\echo '=== ticket_comments ==='
SELECT *
FROM ticket_comments
ORDER BY ticket_id, created_at;

\echo '=== ticket_status_history ==='
SELECT *
FROM ticket_status_history
ORDER BY ticket_id, changed_at;

\echo '=== JOIN: заявка + клиент + исполнитель + категория ==='
SELECT t.id,
       t.title,
       t.status,
       customer.name AS customer,
       assignee.name AS assignee,
       c.name        AS category
FROM tickets t
JOIN users customer ON customer.id = t.customer_id
LEFT JOIN users assignee ON assignee.id = t.assignee_id
LEFT JOIN categories c ON c.id = t.category_id
ORDER BY t.id;

\echo '=== Отчёт: заявок по статусам ==='
SELECT status, COUNT(*) AS count
FROM tickets
GROUP BY status
ORDER BY status;

\echo '=== Отчёт: заявок по категориям ==='
SELECT c.name, COUNT(t.id) AS ticket_count
FROM categories c
LEFT JOIN tickets t ON t.category_id = c.id
GROUP BY c.id, c.name
ORDER BY ticket_count DESC;

\echo '=== Отчёт: нагрузка сотрудников ==='
SELECT u.name,
       COUNT(t.id) FILTER (
           WHERE t.status IN ('NEW', 'IN_PROGRESS')
       ) AS active_tickets
FROM users u
LEFT JOIN tickets t ON t.assignee_id = u.id
WHERE u.role = 'SUPPORT_AGENT'
GROUP BY u.id, u.name
ORDER BY active_tickets DESC;

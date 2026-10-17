-- 00_create_role_db.sql
-- Выполняется от суперпользователя postgres, база: postgres
-- Создание отдельного пользователя приложения и базы данных Help Desk.

DO
$$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'helpdesk_app') THEN
        CREATE ROLE helpdesk_app WITH LOGIN PASSWORD 'helpdesk_pwd_2026';
    END IF;
END
$$;

-- CREATE DATABASE нельзя выполнять внутри DO/транзакции,
-- поэтому проверка и создание выполняются из psql отдельно.
SELECT 'CREATE DATABASE helpdesk_db OWNER helpdesk_app ENCODING ''UTF8'''
WHERE NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'helpdesk_db')
\gexec

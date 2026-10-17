package model;

/**
 * Роль пользователя. Значения совпадают с PostgreSQL ENUM user_role.
 */
public enum UserRole {
    CUSTOMER,
    SUPPORT_AGENT,
    ADMIN
}

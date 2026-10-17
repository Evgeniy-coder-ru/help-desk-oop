package model;

/**
 * Пользователь Help Desk. Конкретная роль хранится в поле role,
 * как это принято в реляционной модели (см. таблицу users).
 */
public class User {

    private Long id;
    private String name;
    private String email;
    private UserRole role;
    private Long departmentId;
    private boolean active;

    public User() {
    }

    public User(long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.active = true;
    }

    public User(long id, String name, String email, UserRole role,
                Long departmentId, boolean active) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.departmentId = departmentId;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void performAction() {
        System.out.println(getName() + " работает с системой");
    }
}

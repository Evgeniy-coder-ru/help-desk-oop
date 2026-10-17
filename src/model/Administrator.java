package model;

/**
 * Администратор системы.
 */
public class Administrator extends User {

    public Administrator(long id, String name, String email) {
        super(id, name, email, UserRole.ADMIN, null, true);
    }

    @Override
    public void performAction() {
        System.out.println(getName() + " управляет системой");
    }
}

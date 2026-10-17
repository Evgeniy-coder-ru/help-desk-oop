package model;

/**
 * Клиент Help Desk.
 */
public class Customer extends User {

    public Customer(long id, String name, String email) {
        super(id, name, email, UserRole.CUSTOMER, null, true);
    }

    @Override
    public void performAction() {
        System.out.println(getName() + " создаёт заявку");
    }
}

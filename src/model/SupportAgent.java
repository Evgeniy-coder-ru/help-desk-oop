package model;

/**
 * Специалист поддержки.
 */
public class SupportAgent extends User {

    public SupportAgent(long id, String name, String email) {
        super(id, name, email, UserRole.SUPPORT_AGENT, null, true);
    }

    @Override
    public void performAction() {
        System.out.println(getName() + " обрабатывает заявку");
    }
}

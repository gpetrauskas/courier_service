package gytis.courier.domain.person;

public class Admin extends Person {
    public Admin(Long id, String name, Email email, String password) {
        super(id, name, email, password);
    }

    @Override
    public String getRole() { return "ADMIN"; }

    @Override
    public void delete(DeletionPolicy policy) {}
}

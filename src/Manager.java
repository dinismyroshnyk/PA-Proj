public class Manager extends User {
    // constructor
    public Manager(String login, String password, String name, String email) {
        super(login, password, name, email, "manager", "active");
    }

    // getters, setters, and other manager-specific methods
    public static Manager register (String login, String password, String name, String email) {
        return new Manager(login, password, name, email);
    }
}
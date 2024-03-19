public class Manager extends User {
    // constructor
    public Manager(String login, String password, byte[] salt, String name, String email) {
        super(login, password, salt, name, email, "manager", "active");
    }

    // getters, setters, and other manager-specific methods
    public static Manager register () {
        Main.clearConsole();
        String name = Validator.validateInput("Name", false);
        String email = Validator.validateInput("Email", true);
        String login = Validator.validateInput("Login", true);
        byte[] salt = Security.generateSalt();
        String password = Validator.validatePassword(salt);
        return new Manager(login, password, salt, name, email);
    }
}
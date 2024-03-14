import java.util.Scanner;

public abstract class User {
    private String login;
    private String password;
    private String name;
    private String email;
    private String type;
    private String status;

    // constructor
    public User(String login, String password, String name, String email, String type, String status) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.email = email;
        this.type = type;
        this.status = status;
    }

    // getters, setters, and other methods
    public static User createUser(String type, Scanner scanner) {
        switch (type) {
            case "manager":
                return Manager.register(scanner);
            case "author":
                return Author.register(scanner);
            case "reviewer":
                return Reviewer.register(scanner);
            default:
                throw new IllegalArgumentException("Invalid user type: " + type);
        }
    }
}
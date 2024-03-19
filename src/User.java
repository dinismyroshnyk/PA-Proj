import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.sql.Date;

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
    private String getLogin() {
        return login;
    }

    private String getPassword() {
        return password;
    }

    private String getName() {
        return name;
    }

    private String getEmail() {
        return email;
    }

    private String getType() {
        return type;
    }

    private String getStatus() {
        return status;
    }

    public static User registerNewUser(String type, byte[] salt) {
        switch (type) {
            case "manager":
                return Manager.register(salt);
            case "author":
                return Author.register(salt);
            case "reviewer":
                return Reviewer.register(salt);
            default:
                throw new IllegalArgumentException("Invalid user type: " + type);
        }
    }

    public static User createUserObject(List<Object> values) {
        String name = values.get(0).toString();
        String email = values.get(1).toString();
        String login = values.get(2).toString();
        String password = values.get(3).toString();
        String type = values.get(4).toString();
        switch (type) {
            case "manager":
                return new Manager(login, password, name, email);
            case "author":
                String nif = values.get(5).toString();
                String phone = values.get(6).toString();
                String address = values.get(7).toString();
                String style = values.get(8).toString();
                Date date = (Date) values.get(9);
                return new Author(login, password, name, email, nif, phone, address, style, date);
            case "reviewer":
                String nif2 = values.get(5).toString();
                String phone2 = values.get(6).toString();
                String address2 = values.get(7).toString();
                String specialization = values.get(8).toString();
                String academicBackground = values.get(9).toString();
                return new Reviewer(login, password, name, email, nif2, phone2, address2, specialization, academicBackground);
            default:
                throw new IllegalArgumentException("Invalid user type: " + type);
        }
    }

    private static final Map<String, Function<User, String>> getters = Map.of(
        "login", User::getLogin,
        "password", User::getPassword,
        "name", User::getName,
        "email", User::getEmail,
        "type", User::getType,
        "status", User::getStatus
    );

    public static String getValue (User user, String value) {
        if (getters.containsKey(value)) {
            return getters.get(value).apply(user);
        } else {
            switch (user.getType()) {
                case "author":
                    return Author.getValue((Author) user, value);
                case "reviewer":
                    return Reviewer.getValue((Reviewer) user, value);
                default:
                    throw new IllegalArgumentException("Invalid user type: " + user.getType());
            }
        }
    }

    public static void loggedUserLoop(User user) {
        switch (user.getType()) {
            case "manager":
                Manager.loggedUserLoop((Manager) user);
                break;
            case "author":
                Author.loggedUserLoop((Author) user);
                break;
            case "reviewer":
                Reviewer.loggedUserLoop((Reviewer) user);
                break;
            default:
                throw new IllegalArgumentException("Invalid user type: " + user.getType());
        }
    }
}
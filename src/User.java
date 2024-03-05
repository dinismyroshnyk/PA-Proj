import java.util.ArrayList;

public abstract class User {
    private static ArrayList<User> allUsers = new ArrayList<User>();
    private static ArrayList<String> allLogins = new ArrayList<String>();
    private static ArrayList<String> allEmails = new ArrayList<String>();
    private String login;
    private String password;
    private String name;
    private String email;
    private String type;
    private String status;

    // constructor
    public User(String login, String password, String name, String email, String type, String status) {
        if (allLogins.contains(login)) {
            System.out.println("Login already exists");
            return;
        }
        if (allEmails.contains(email)) {
            System.out.println("Email already exists");
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,}$")) {
            System.out.println("Please provide a valid email address");
            return;
        }
        if (!status.equals("active") && !status.equals("inactive")) {
            System.out.println("Status must be either active or inactive");
            return;
        }
        this.login = login;
        this.password = password;
        this.name = name;
        this.email = email;
        this.type = type;
        this.status = status;
        allUsers.add(this);
        allLogins.add(login);
        allEmails.add(email);
    }

    // getters, setters, and other methods
    public static ArrayList<User> getAllUsers() {
        return allUsers;
    }

    public static void welcomeUser(User user) {
        System.out.println("Welcome, " + user.name + "!");
    }

    public static void goodbyeUser(User user) {
        System.out.println("Goodbye, " + user.name + "!");
    }

    public static User login (String login, String password) {
        for (User user : allUsers) {
            if (user.login.equals(login) && user.password.equals(password)) {
                return user;
            }
        }
        return null;
    }
}
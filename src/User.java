public abstract class User {
    private static ArrayList<User> allUsers = new ArrayList<>();
    private static ArrayList<String> allLogins = new ArrayList<>();
    private static ArrayList<String> allEmails = new ArrayList<>();
    private String login;
    private String password;
    private String name;
    private String email;
    private String type;
    private String status;

    // constructor
    public User(String login, String password, String name, String email, Int type, Boolean status) {
        if(allLogins.contains(login)) {
            System.out.println("This login is already taken.");
        }
        if(allEmails.contains(email)) {
            System.out.println("This email is already taken.");
        }
        if(!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            System.out.println("Please enter a valid email address.");
        }
        if(!status.matches("active") && !status.matches("inactive")) {
            System.out.println("Status must be either active or inactive.");
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

    public static User login(String login, String password) {
        for (User user : allUsers) {
            if (user.login.equals(login) && user.password.equals(password)) {
                return user;
            }
        }
        return null;
    }
}

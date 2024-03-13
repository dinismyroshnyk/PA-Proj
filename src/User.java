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
    public static void welcomeUser(User user) {
        System.out.println("Welcome, " + user.name + "!");
    }

    public static void goodbyeUser(User user) {
        System.out.println("Goodbye, " + user.name + "!");
    }

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

    //public static String getLogin(Scanner scanner, String login) {
    //    while (Database.existsInDatabase(login, "login")) {
    //        System.out.print("Login: ");
    //        login = scanner.nextLine();
    //        Database.existsInDatabase(login, "login");
    //    }
    //    return login;
    //}

    public static boolean isEmailValid(String email) {
        return email.matches("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
    }

    
    public static String validateEmail(Scanner scanner) {
        String email;
        do {
            System.out.print("Email: ");
            email = scanner.nextLine();
            if (!isEmailValid(email)) {
                System.out.println("Invalid email. Please try again.");
            }
            if (Database.existsInDatabase(email, "email")) {
                System.out.println("Email already in use. Please try again.");
            }
        } while (!isEmailValid(email));
        return email;
    }

    public static boolean isValidNIF(String nif) {
        // A expressão regular verifica se a string contém exatamente 9 dígitos
        return nif.matches("^[0-9]{9}$");
    }

    public static String validateNIF(Scanner scanner) {
        String nif;
        do {
            System.out.print("NIF: ");
            nif = scanner.nextLine();
            if (!isValidNIF(nif)) {
                System.out.println("Invalid NIF. Please try again.");
            }
        } while (!isValidNIF(nif));
        return nif;
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^(9|2|3)\\d{8}$");
    }

    public static String validatePhone(Scanner scanner) {
        String phone;
        do {
            System.out.print("Phone: ");
            phone = scanner.nextLine();
            if (!isValidPhoneNumber(phone)) {
                System.out.println("Invalid phone number. Please try again.");
            }
        } while (!isValidPhoneNumber(phone));
        return phone;
    }

    public static String validateLogin(Scanner scanner) {
        String login;
        do {
            System.out.print("Login: ");
            login = scanner.nextLine();
            if (Database.existsInDatabase(login, "login")) {
                System.out.println("Login already in use. Please try again.");
            }
        } while (Database.existsInDatabase(login, "login"));
        return login;
    }
}
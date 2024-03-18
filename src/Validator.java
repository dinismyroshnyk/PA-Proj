import java.util.Scanner;
import java.util.function.Function;

public class Validator {
    public static boolean isValidEmail(String email) {
        return email.matches("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
    }

    public static boolean isValidNIF(String nif) {
        return nif.matches("^[0-9]{9}$");
    }

    private static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^(9|2|3)\\d{8}$");
    }

    private static boolean isValidName(String name) {
        return name.matches("^[A-Z](?=.{2,100}$)[A-Za-z]*(?:\\h+[A-Z][A-Za-z]*)*$");
    }

    private static boolean isValidPassword(String password) {
        return password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,20}$");
    }

    //private static boolean isValidAddress(String address) {}
    //private static boolean isValidLiteraryStyle(String literaryStyle) {}
    //private static boolean isValidAcademicBackground(String academicBackground) {}
    //private static boolean isValidSpecialization(String specialization) {}
    //private static boolean isValidDate(String date) {} // should probably be changed to Date type

    public static String validateInputInDatabase(Scanner scanner, String type, Function<String, Boolean> validator) {
        String input;
        boolean check = false;
        do {
            System.out.print(type + ": ");
            input = scanner.nextLine();
            boolean isValid = validator == null || validator.apply(input);
            boolean isUnique = !Database.existsInDatabase(input, type.toLowerCase());
            check = isValid && isUnique;
            if (!check) {
                if (!isValid) {
                    System.out.println("Invalid " + type + ". Please try again.");
                } else {
                    System.out.println(type + " already in use. Please try again.");
                }
            }
        } while (!check);
        return input;
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

    public static String validateName(Scanner scanner) {
        String name;
        do {
            System.out.print("Name: ");
            name = scanner.nextLine();
            if (!isValidName(name)) {
                System.out.println("Invalid name. Please try again.");
            }
        } while (!isValidName(name));
        return name;
    }

    public static String validatePassword(Scanner scanner, byte[] salt) {
        String password;
        do {
            String textBlock = """
            Password must contain:
                - Between 8 and 20 characters
                - At least one digit
                - At least one uppercase letter
                - At least one lowercase letter
                - No whitespaces
            """;
            System.out.println(textBlock);
            password = Security.maskPassword();
            if (password == null) {
                System.out.println("Failed to read password. Please try again.");
            }
            if (!isValidPassword(password) && password != null) {
                System.out.println("Invalid password. Please try again.");
            }
        } while (!isValidPassword(password));
        return Security.hashPassword(password, salt);
    }
}
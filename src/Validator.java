import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

public class Validator {
    private static boolean isValidEmail(String email) {
        return email.matches("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
    }

    private static boolean isValidNIF(String nif) {
        return nif.matches("^[0-9]{9}$");
    }

    private static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^(9|2|3)\\d{8}$");
    }

    private static boolean isValidName(String name) {
        return name.matches("^[A-Z](?=.{2,100}$)[A-Za-z]*(?:\\h+[A-Z][A-Za-z]*)*$");
    }

    private static boolean isValidLogin(String login) {
        return login.matches("^[A-Za-z0-9]{3,20}$");
    }

    private static boolean isValidPassword(String password) {
        return password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,20}$");
    }

    private static boolean isValidAddress(String address) {
        return address.matches("^[\\w\\s,\\.]{1,100}$");
    }

    private static boolean isValidLiteraryStyle(String literaryStyle) {
        return literaryStyle.matches("^[\\w\\s]{1,100}$");
    }

    private static boolean isValidAcademicBackground(String academicBackground) {
        return academicBackground.matches("^[\\w\\s,\\.]{1,100}$");
    }

    private static boolean isValidSpecialization(String specialization) {
        return specialization.matches("^[\\w\\s]{1,100}$");
    }

    //private static boolean isValidDate(String date) {} // should probably be changed to Date type

    private static final Map<String, Function<String, Boolean>> validators = Map.of(
        "email", Validator::isValidEmail,
        "nif", Validator::isValidNIF,
        "phone number", Validator::isValidPhoneNumber,
        "name", Validator::isValidName,
        "login", Validator::isValidLogin,
        "password", Validator::isValidPassword,
        "address", Validator::isValidAddress,
        "literary style", Validator::isValidLiteraryStyle,
        "academic background", Validator::isValidAcademicBackground,
        "specialization", Validator::isValidSpecialization
    );

    public static String validateInput(Scanner scanner, String type, boolean checkDatabase) {
        String input;
        boolean check = false;
        do {
            System.out.print(type + ": ");
            input = scanner.nextLine();
            boolean isValid = validators.get(type.toLowerCase()).apply(input);
            boolean isUnique = !checkDatabase || !Database.existsInDatabase(input, type.toLowerCase());
            check = isValid && isUnique;
            if (!isValid || !isUnique) {
                if (!isValid) {
                    System.out.println("Invalid " + type + ". Please try again.");
                } else {
                    System.out.println(type + " already in use. Please try again.");
                }
            }
        } while (!check);
        return input;
    }

    public static String validatePassword(byte[] salt) {
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
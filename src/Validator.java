import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
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
        return login.matches("^[A-Za-z0-9_-]{3,20}$");
    }

    private static boolean isValidPassword(String password) {
        return password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,20}$");
    }

    private static boolean isValidAddress(String address) {
        return address.matches("^[\\w\\s,\\.]{2,100}$");
    }

    private static boolean isValidLiteraryStyle(String literaryStyle) {
        return literaryStyle.matches("^[\\w\\s]{2,20}$");
    }

    private static boolean isValidAcademicBackground(String academicBackground) {
        return academicBackground.matches("^[\\w\\s,\\.]{2,100}$");
    }

    private static boolean isValidSpecialization(String specialization) {
        return specialization.matches("^[\\w\\s]{2,20}$");
    }

    private static boolean isValidTitle(String title) {
        return title.matches("^[\\w\\s,\\.]{2,100}$");
    }

    private static boolean isValidSubtitle(String subtitle) {
        return subtitle.matches("^$|^[\\w\\s,\\.]{2,100}$");
    }

    private static boolean isValidPublicationType(String publicationType) {
        return publicationType.matches("^[\\w\\s]{2,20}$");
    }

    private static boolean isValidLicenseNumber(String licenseNumber) {
        return licenseNumber.matches("^[A-Za-z0-9]{1,20}$");
    }

    private static boolean isValidComment(String comments) {
        return comments.matches("^$|^[^\\r\\n\\t]{2,100}$");
    }

    private static final Map<String, Function<String, Boolean>> validators = Map.ofEntries(
        Map.entry("email", Validator::isValidEmail),
        Map.entry("nif", Validator::isValidNIF),
        Map.entry("phone number", Validator::isValidPhoneNumber),
        Map.entry("name", Validator::isValidName),
        Map.entry("login", Validator::isValidLogin),
        Map.entry("address", Validator::isValidAddress),
        Map.entry("literary style", Validator::isValidLiteraryStyle),
        Map.entry("academic background", Validator::isValidAcademicBackground),
        Map.entry("specialization", Validator::isValidSpecialization),
        Map.entry("title", Validator::isValidTitle),
        Map.entry("subtitle", Validator::isValidSubtitle),
        Map.entry("publication type", Validator::isValidPublicationType),
        Map.entry("license number", Validator::isValidLicenseNumber),
        Map.entry("comments", Validator::isValidComment)
    );

    public static String validateInput(String typeString, boolean checkDatabase) {
        String input;
        boolean check = false;
        do {
            System.out.print(typeString + ": ");
            String type = typeString.replaceAll("\\s*\\(optional\\)|(?i)updated\\s+", "").toLowerCase();
            input = Input.readLine();
            boolean isValid = validators.get(type).apply(input);
            if (!isValid) {
                System.out.println("Invalid " + type + ". Please try again.");
                continue;
            }
            boolean isUnique = false;
            if (checkDatabase) {
                isUnique = !Database.existsInDatabase(input, type);
            } else {
                isUnique = true;
            }
            check = isValid && isUnique;
            if (!isUnique) {
                String capitalizedType = type.substring(0, 1).toUpperCase() + type.substring(1);
                System.out.println(capitalizedType + " already in use. Please try again.");
            }
        } while (!check);
        return input;
    }

    public static String validatePassword(byte[] salt, String type) {
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
            password = Security.maskPassword(type);
            if (password == null) {
                System.out.println("Failed to read password. Please try again.");
            }
            if (!isValidPassword(password) && password != null) {
                System.out.println("Invalid password. Please try again.");
            }
        } while (!isValidPassword(password));
        return Security.hashPassword(password, salt);
    }

    public static Date validateDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate currenDate = LocalDate.now();
        LocalDate date = null;
        do {
            System.out.print("Start date (dd-mm-yyyy): ");
            String input = Input.readLine();
            try {
                date = LocalDate.parse(input, formatter);
                if (date.isAfter(currenDate)) {
                    System.out.println("Date cannot be a future date. Please try again.");
                    date = null;
                }
            } catch (Exception e) {
                System.out.println("Invalid date. Please try again.");
            }
        } while (date == null);
        return Date.valueOf(date);
    }

    public static int validateInt(String prompt) {
        int input = 0;
        do {
            System.out.print(prompt + ": ");
            while (!Input.hasNextInt()) {
                System.out.println("Invalid input. Please try again.");
                System.out.print(prompt + ": ");
                Input.readLine();
            }
            input = Integer.parseInt(Input.readLine());
            if (input <= 0) {
                System.out.println("Input must be greater than 0. Please try again.");
            }
        } while (input <= 0);
        return input;
    }
}
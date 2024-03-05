import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        doUsersExist(scanner);
        mainLoop(scanner);
        scanner.close();
    }

    private static void doUsersExist(Scanner scanner) {
        if (User.getAllUsers().isEmpty()) {
            System.out.println("No users found. Please register a manager.");
            boolean valid = false;
            while (!valid) {
                System.out.print("Login: ");
                String login = scanner.nextLine();
                System.out.print("Password: ");
                String password = scanner.nextLine();
                System.out.print("Name: ");
                String name = scanner.nextLine();
                System.out.print("Email: ");
                String email = scanner.nextLine();
                Manager.register(login, password, name, email);
                if (!User.getAllUsers().isEmpty()) {
                    System.out.println("Manager created successfully.");
                    valid = true;
                }
            }
        }
    }

    private static void mainLoop(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.print("Enter login: ");
            String login = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            User user = User.login(login, password);
            if (user != null) {
                User.welcomeUser(user);

                // Rest of the application...

                User.goodbyeUser(user);
            } else System.out.println("Invalid login or password.");
            running = exitApplication(scanner);
        }
    }

    private static boolean exitApplication(Scanner scanner) {
        System.out.print("Do you want to exit the application? (y/n): ");
        String exit = scanner.nextLine().toLowerCase();
        if (exit.equals("y") || exit.equals("yes")) return false;
        else if (exit.equals("n") || exit.equals("no")) return true;
        else {
            System.out.println("Invalid input. Please try again.");
            return exitApplication(scanner);
        }
    }
}
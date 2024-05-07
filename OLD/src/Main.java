import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Main {
    public static void main(String[] args) {
        // Add a shutdown hook to close the input stream and restore the console mode
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            OS.toggleConsoleMode(OS.getHandle(), OS.getMode(), OS.ConsoleMode.SANE);
            IO.closeInput();
        }));
        // Start the timer
        Utils.getStartTime();
        // Prepare and connect to the database
        Database.setUpDatabase();
        // Application and SQL handling
        Database.databaseTaskWithErrorHandling(() -> {
            mainLoop();
        }, true);
        // Exit the application
        Utils.clearConsole();
        System.exit(0);
    }

    // Main loop of the application
    private static void mainLoop() {
        String title = "Menu";
        String[] menuItems = {
            "Login",
            "Register",
            "DB params",
            "Exit"
        };
        String footer = "Controls: c";
        final int[] selectedId = {0};
        int maxId = menuItems.length - 1;
        OS.toggleConsoleMode(OS.getHandle(), OS.getMode(), OS.ConsoleMode.RAW);
        Map<String, Consumer<Integer>> actions = new HashMap<>();
        actions.put("Login", id -> loginAction());
        actions.put("Register", id -> registerAction());
        actions.put("DB params", id -> dbParamsAction());
        actions.put("Exit", id -> exitAction());
        Utils.clearConsole();
        drawMenu(title, menuItems, selectedId, footer);
        while (true) {
            int input = checkArrowKeys();
            switch (input) {
                case 106:
                    Utils.clearConsole();
                    selectedId[0] = (selectedId[0] + 1) % (maxId + 1);
                    drawMenu(title, menuItems, selectedId, footer);
                    break;
                case 107:
                    Utils.clearConsole();
                    selectedId[0] = (selectedId[0] - 1 + (maxId + 1)) % (maxId + 1);
                    drawMenu(title, menuItems, selectedId, footer);
                    break;
                case 99:
                    Utils.clearConsole();
                    Output.drawControls();
                    Utils.pressEnterKey();
                    Utils.clearConsole();
                    drawMenu(title, menuItems, selectedId, footer);
                    break;
                case 10: case 13:
                    Utils.clearConsole();
                    actions.get(menuItems[selectedId[0]]).accept(selectedId[0]);
                    Utils.pressEnterKey();
                    if (menuItems[selectedId[0]].equals("Exit")) {
                        return;
                    } else {
                        Utils.clearConsole();
                        drawMenu(title, menuItems, selectedId, footer);
                    }
                    break;
                default:
                    // Do nothing
                    break;
            }
        }
    }

    // Show the execution time
    private static void showExecutionTime(LocalDateTime startTime) {
        LocalDateTime endTime = LocalDateTime.now();
        DateTimeFormatter date = DateTimeFormatter.ofPattern("EEEE; yyyy-MM-dd HH:mm:ss");
        long executionTimeMillis = Duration.between(startTime, endTime).toMillis();
        Duration duration = Duration.ofMillis(executionTimeMillis);
        String formattedExecutionTime = String.format("%d milliseconds (%d seconds; %d minutes; %d hours)", executionTimeMillis, duration.toSecondsPart(), duration.toMinutesPart(), duration.toHours());
        String title = "Execution Time";
        String[] menuItems = {
            "Process start: " + startTime.format(date),
            "Process end: " + endTime.format(date),
            "Total time: " + formattedExecutionTime
        };
        int[] outOfBoundsId = {-1};
        Output.drawBox(title, menuItems, outOfBoundsId);
    }

    // Check the arrow keys
    private static int checkArrowKeys() {
        int input = IO.readBufferedInt();
        if (input == 27) {
            input = IO.readBufferedInt();
            if (input == 91) {
                input = IO.readBufferedInt();
                switch (input) {
                    case 65:
                        input = 107;
                        break;
                    case 66:
                        input = 106;
                        break;
                    default:
                        // Do nothing
                        break;
                }
            }
        }
        return input;
    }

    // Draw the menu
    private static void drawMenu(String title, String[] menuItems, int[] selectedId, String footer) {
        OS.runTaskInSaneMode(() -> {
            System.out.println(" " + footer + "\n");
            Output.drawBox(title, menuItems, selectedId);
        });
    }

    // Login action
    private static void loginAction() {
        OS.runTaskInSaneMode(() -> {
            System.out.println("Login");
        });
    }

    // Register action
    private static void registerAction() {
        OS.runTaskInSaneMode(() -> {
            System.out.println("Register");
        });
    }

    private static void allParams(String[] paramWrapper, final int[] selectedId) {
        OS.toggleConsoleMode(OS.getHandle(), OS.getMode(), OS.ConsoleMode.SANE);
        Map <Integer, String> paramText = new HashMap<>( Map.of(
                0, "Enter new ip: ",
                1, "Enter new port: ",
                2, "Enter new database: ",
                3, "Enter new user: "
        ));
        if (selectedId[0] == 4){
            paramWrapper[0] = Security.maskPassword("Enter new password");
            return;

        } else {
            System.out.print(paramText.get(selectedId [0]));
            paramWrapper[0] = IO.readLine();
        }
        OS.toggleConsoleMode(OS.getHandle(), OS.getMode(), OS.ConsoleMode.RAW);
    }

    // DB params action
    private static void dbParamsAction() {
        String[] params = new String[5];
        params = Utils.readParamsFromFile(new File("Properties"), params);
        String[] ipWrapper = new String[]{params[0]};
        String title = "DB Params";
        String[] menuItems = {
            "ip:       " + params[0],
            "port:     " + params[1],
            "database: " + params[2],
            "user:     " + params[3],
            "password: " + "*".repeat(params[4].length())
        };
        String footer = "Confirm: c";
        final int[] selectedId = {0};
        int maxId = menuItems.length - 1;
        Map<String, Consumer<Integer>> actions = new HashMap<>();
        actions.put("ip", id -> allParams(ipWrapper, selectedId));
        actions.put("port", id -> allParams(ipWrapper, selectedId));
        actions.put("database", id -> allParams(ipWrapper, selectedId));
        actions.put("user", id -> allParams(ipWrapper, selectedId));
        actions.put("password", id -> allParams(ipWrapper, selectedId));
        Utils.clearConsole();
        drawMenu(title, menuItems, selectedId, footer);
        while (true) {
            int input = checkArrowKeys();
            switch (input) {
                case 106:
                    Utils.clearConsole();
                    selectedId[0] = (selectedId[0] + 1) % (maxId + 1);
                    drawMenu(title, menuItems, selectedId, footer);
                    break;
                case 107:
                    Utils.clearConsole();
                    selectedId[0] = (selectedId[0] - 1 + (maxId + 1)) % (maxId + 1);
                    drawMenu(title, menuItems, selectedId, footer);
                    break;
                case 99:
                    Utils.clearConsole();
                    return;
                case 10: case 13:
                    String selectedItem = menuItems[selectedId[0]]; // Obtém o item selecionado
                    // Extrai apenas a parte da chave que corresponde ao identificador da ação
                    String actionKey = selectedItem.split(":")[0].trim();
                    Consumer<Integer> action = actions.get(actionKey); // Busca a ação correspondente ao item selecionado
                    if (action != null) { // Se a ação existir
                        action.accept(selectedId[0]); // Executa a ação
                        params[selectedId[0]] = ipWrapper[0];
                        //Atualizar os itens do menu para refletir os novos valores de params
                        menuItems[0] = "ip:       " + params[0];
                        menuItems[1] = "port:     " + params[1];
                        menuItems[2] = "database: " + params[2];
                        menuItems[3] = "user:     " + params[3];
                        menuItems[4] = "password: " + "*".repeat(params[4].length());
                        Database.saveCredentialsToFile("Properties", params);
                        Utils.clearConsole();
                        drawMenu(title, menuItems, selectedId, footer);
                    } else {
                        System.out.println("Ação " + actionKey + " não encontrada"); // Adicionar uma mensagem de depuração
                        Utils.pressEnterKey();
                        Utils.clearConsole();
                        drawMenu(title, menuItems, selectedId, footer);} // Redesenha o menu
                    break;
                default:
                    // Do nothing
                    break;
            }
        }
    }

    // Exit action
    private static void exitAction() {
        OS.runTaskInSaneMode(() -> {
            showExecutionTime(Utils.getStartTime());
        });
    }
}
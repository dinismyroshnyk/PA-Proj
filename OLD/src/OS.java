import java.io.IOException;
import java.util.Map;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.StdCallLibrary;

public class OS {
    // Public methods
        // Console mode enum
        public enum ConsoleMode {
            RAW,
            SANE
        }

        // Get the console handle
        public static WinNT.HANDLE getHandle() {
            return osParams.getKey();
        }

        // Get the console mode
        public static WinDef.DWORDByReference getMode() {
            return osParams.getValue();
        }

        // Force running a task in SANE mode
        public static void runTaskInSaneMode(Runnable task) {
            toggleConsoleMode(getHandle(), getMode(), ConsoleMode.SANE);
            task.run();
            toggleConsoleMode(getHandle(), getMode(), ConsoleMode.RAW);
        }

        // Toggle the console mode
        public static void toggleConsoleMode(WinNT.HANDLE handle, WinDef.DWORDByReference mode, ConsoleMode toggle) {
            switch (toggle) {
                case RAW:
                    System.out.print("\33[?25l");
                    setConsoleMode(handle, mode, ConsoleMode.RAW);
                    break;
                case SANE:
                    System.out.print("\33[?25h");
                    setConsoleMode(handle, mode, ConsoleMode.SANE);
                    break;
                default:
                    System.out.println("Invalid toggle.");
                    break;
            }
        }

    // Helper methods
        // Class level variables
        private static Map.Entry<WinNT.HANDLE, WinDef.DWORDByReference> osParams = getOSParams();

        // JNA interface
        private interface Kernel32 extends StdCallLibrary {
            Kernel32 INSTANCE = (Kernel32) Native.load("kernel32", Kernel32.class);
            WinNT.HANDLE GetStdHandle(WinDef.DWORD stdHandle);
            boolean SetConsoleMode(WinNT.HANDLE hConsoleHandle, WinDef.DWORD dwMode);
            boolean GetConsoleMode(WinNT.HANDLE hConsoleHandle, WinDef.DWORDByReference lpMode);
        }

        // Detect the OS and get the console handle and mode
        private static Map.Entry<WinNT.HANDLE, WinDef.DWORDByReference> getOSParams() {
            String os = System.getProperty("os.name").toLowerCase();
            WinNT.HANDLE handle = null;
            WinDef.DWORDByReference mode = null;
            if (os.contains("win")) {
                handle = Kernel32.INSTANCE.GetStdHandle(new WinDef.DWORD(-10));
                mode = new WinDef.DWORDByReference();
                Kernel32.INSTANCE.GetConsoleMode(handle, mode);
            }
            return Utils.pair(handle, mode);
        }

        // Set the console mode
        private static void setConsoleMode(WinNT.HANDLE handle, WinDef.DWORDByReference mode, ConsoleMode conMode) {
            if (handle != null && mode != null) {
                WinDef.DWORD newMode = null;
                if (conMode == ConsoleMode.RAW) {
                    newMode = new WinDef.DWORD(mode.getValue().intValue() & ~0x0002 & ~0x0001 | 0x0200);
                } else {
                    newMode = mode.getValue();
                }
                Kernel32.INSTANCE.SetConsoleMode(handle, newMode);
            } else {
                executeBash("/bin/sh", "-c", "stty " + conMode.toString().toLowerCase() + " -echo </dev/tty", "Error setting terminal to " + conMode + " mode.");
            }
        }

        // Execute a bash command
        private static void executeBash(String... cmd) {
            try {
                Runtime.getRuntime().exec(cmd).waitFor();
            } catch (IOException | InterruptedException e) {
                System.out.println(cmd[2]);
                System.out.println("Exception: " + e);
                System.exit(1);
            }
        }
}
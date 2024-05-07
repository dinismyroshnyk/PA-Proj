package main;
import java.util.Map;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Contains methods for operating system-specific tasks.
 */
public class OS {
    // Public methods
        /**
         * Enumeration for console modes.
        */
        public enum ConsoleMode {
            RAW,
            SANE
        }

        /**
         * Gets the console handle
         *
         * @return The console handle.
         */
        public static WinNT.HANDLE getHandle() {
            return osParams.getKey();
        }

        /**
         * Gets the console mode
         *
         * @return The console mode.
         */
        public static WinDef.DWORDByReference getMode() {
            return osParams.getValue();
        }

        /**
         * Runs a task in raw mode.
         *
         * @param task The task to run.
         */
        public static void runTaskInSaneMode(Runnable task) {
            toggleConsoleMode(getHandle(), getMode(), ConsoleMode.SANE);
            task.run();
            toggleConsoleMode(getHandle(), getMode(), ConsoleMode.RAW);
        }

        /**
         * Toggles the console mode.
         *
         * @param handle The console handle.
         * @param mode The console mode.
         * @param toggle The mode to toggle to.
         */
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
        /**
         * Map entry of the console handle (key) and mode (value).
         */
        private static Map.Entry<WinNT.HANDLE, WinDef.DWORDByReference> osParams = getOSParams();

        /**
         * JNA Kernel32 interface.
         */
        private interface Kernel32 extends StdCallLibrary {
            Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class);
            WinNT.HANDLE GetStdHandle(WinDef.DWORD stdHandle);
            boolean SetConsoleMode(WinNT.HANDLE hConsoleHandle, WinDef.DWORD dwMode);
            boolean GetConsoleMode(WinNT.HANDLE hConsoleHandle, WinDef.DWORDByReference lpMode);
        }

        /**
         * Gets the console handle and mode for the operating system.
         *
         * @return A key-value pair of the console handle and mode.
         */
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

        /**
         * Sets the console mode.
         *
         * @param handle The console handle.
         * @param mode The console mode.
         * @param conMode The console mode to set.
         */
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

        /**
         * Executes a bash command.
         *
         * @param cmd The command to execute and the error message.
         */
        private static void executeBash(String... cmd) {
            IO.ioTaskWithErrorHandling(() -> {
                Runtime.getRuntime().exec(cmd).waitFor();
            }, cmd[3]);
        }
}
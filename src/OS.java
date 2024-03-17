import java.io.IOException;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.StdCallLibrary;

public class OS {
    public static Pair<WinNT.HANDLE, WinDef.DWORDByReference> prepareOS() {
        String os = System.getProperty("os.name").toLowerCase();
        WinNT.HANDLE handle = null;
        WinDef.DWORDByReference mode = null;
        if (os.contains("win")) {
            handle = Kernel32.INSTANCE.GetStdHandle(new WinDef.DWORD(-10));
            mode = new WinDef.DWORDByReference();
            Kernel32.INSTANCE.GetConsoleMode(handle, mode);
        }
        return new Pair<>(handle, mode);
    }

    public static void consoleRaw(WinNT.HANDLE handle, WinDef.DWORDByReference mode) {
        if (handle != null && mode != null) {
            Kernel32.INSTANCE.SetConsoleMode(handle, new WinDef.DWORD(mode.getValue().intValue() & ~0x0002));
        } else {
            String cmd[] = {"/bin/sh", "-c", "stty raw -echo </dev/tty"};
            try {
                Runtime.getRuntime().exec(cmd).waitFor();
            } catch (IOException | InterruptedException e) {
                System.out.println("Error setting terminal to raw mode.");
                System.out.println("Exception: " + e);
            }
        }
    }

    public static void consoleReset(WinNT.HANDLE handle, WinDef.DWORDByReference mode) {
        if (handle != null && mode != null) {
            Kernel32.INSTANCE.SetConsoleMode(handle, mode.getValue());
        } else {
            String cmd[] = {"/bin/sh", "-c", "stty sane </dev/tty"};
            try {
                Runtime.getRuntime().exec(cmd).waitFor();
            } catch (IOException | InterruptedException e) {
                System.out.println("Error setting terminal to sane mode.");
                System.out.println("Exception: " + e);
            }
        }
    }

    private interface Kernel32 extends StdCallLibrary {
        Kernel32 INSTANCE = (Kernel32) Native.load("kernel32", Kernel32.class);
        WinNT.HANDLE GetStdHandle(WinDef.DWORD stdHandle);
        boolean SetConsoleMode(WinNT.HANDLE hConsoleHandle, WinDef.DWORD dwMode);
        boolean GetConsoleMode(WinNT.HANDLE hConsoleHandle, WinDef.DWORDByReference lpMode);
    }
}

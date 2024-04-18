import java.io.IOException;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.StdCallLibrary;

public class OS {
    private static Pair<WinNT.HANDLE, WinDef.DWORDByReference> prepareOS() {
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

    public static WinNT.HANDLE getHandle() {
        return prepareOS().getKey();
    }

    public static WinDef.DWORDByReference getMode() {
        return prepareOS().getValue();
    }

    public static void toggleConsoleMode(WinNT.HANDLE handle, WinDef.DWORDByReference mode, String toggle) {
        switch (toggle) {
            case "raw":
                if (handle != null && mode != null) {
                    Kernel32.INSTANCE.SetConsoleMode(handle, new WinDef.DWORD(mode.getValue().intValue() & ~0x0002 & ~0x0001));
                    System.out.print("\33[?25l");
                } else {
                    String cmd[] = {"/bin/sh", "-c", "stty raw -echo </dev/tty"};
                    try {
                        Runtime.getRuntime().exec(cmd).waitFor();
                    } catch (IOException | InterruptedException e) {
                        System.out.println("Error setting terminal to raw mode.");
                        System.out.println("Exception: " + e);
                    }
                }
                break;
            case "sane":
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
                break;
            default:
                System.out.println("Invalid toggle.");
                break;
        }
    }

    private static void toggleCharacterSet(String toggle) {
        switch (toggle) {
            case "on":
                System.out.print("\33(0");
                break;
            case "off":
                System.out.print("\33(B");
                break;
            default:
                System.out.println("Invalid toggle.");
                break;
        }
    }

    private static int getBoxSize(String title, String[] menuItems) {
        int boxSize = title.length();
        for (String item : menuItems) {
            if (item.length() > boxSize) {
                boxSize = item.length();
            }
        }
        return boxSize;
    }

    public static void drawMenuBox(String title, String[] menuItems) {
        int boxSize = getBoxSize(title, menuItems);
        toggleCharacterSet("on");
        System.out.print("lq");
        for (int i = 0; i < boxSize; i++) {
            System.out.print("q");
        }
        System.out.println("qk");
        for (int i = 0; i < menuItems.length; i++) {
            System.out.print("x ");
            for (int j = 0; j < boxSize; j++) {
                System.out.print(" ");
            }
            System.out.println(" x");
        }
        System.out.print("mq");
        for (int i = 0; i < boxSize; i++) {
            System.out.print("q");
        }
        System.out.print("qj");
        toggleCharacterSet("off");
    }

    public static void insertBoxItems(String title, String[] menuItems) {
        int boxSize = getBoxSize(title, menuItems);
        System.out.print("\33[" + (boxSize + 2) + "D");
        System.out.print("\33[" + (menuItems.length + 1) + "A");
        System.out.print(title);
        System.out.print("\33[" + (title.length()) + "D");
        for (String item : menuItems) {
            System.out.print("\33[1B");
            System.out.print(item);
            System.out.print("\33[" + (item.length()) + "D");
        }
        System.out.print("\33[2B\33[2D");
    }

    private interface Kernel32 extends StdCallLibrary {
        Kernel32 INSTANCE = (Kernel32) Native.load("kernel32", Kernel32.class);
        WinNT.HANDLE GetStdHandle(WinDef.DWORD stdHandle);
        boolean SetConsoleMode(WinNT.HANDLE hConsoleHandle, WinDef.DWORD dwMode);
        boolean GetConsoleMode(WinNT.HANDLE hConsoleHandle, WinDef.DWORDByReference lpMode);
    }
}

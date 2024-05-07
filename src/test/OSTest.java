package test;
import main.OS;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

public class OSTest {
    @Test
    public void testGetHandle() {
        WinNT.HANDLE handle = OS.getHandle();
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            assertNotNull(handle);
        } else {
            assertNull(handle);
        }
    }

    @Test
    public void testGetMode() {
        WinDef.DWORDByReference mode = OS.getMode();
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            assertNotNull(mode);
        } else {
            assertNull(mode);
        }
    }
}

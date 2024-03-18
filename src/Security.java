import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

public class Security {
    // Encryption key
    private static final String ENCRYPTION_KEY = "6w|Uq)X.77hdh*=H8r[w[&EG*!i~HN]]";

    // Encrypt or decrypt a string using AES
    public static String encryptDecryptString(String string, String param) {
        try {
            SecretKeySpec key = new SecretKeySpec(ENCRYPTION_KEY.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            if(param.equals("-e")) {
                cipher.init(Cipher.ENCRYPT_MODE, key);
                byte[] encrypted = cipher.doFinal(string.getBytes("UTF-8"));
                return Base64.getEncoder().encodeToString(encrypted);
            } else if(param.equals("-d")) {
                cipher.init(Cipher.DECRYPT_MODE, key);
                byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(string));
                return new String(decrypted, "UTF-8");
            } else {
                System.out.println("Invalid parameter. Use -e to encrypt or -d to decrypt.");
                return null;
            }
        } catch (Exception e) {
            System.out.println("Error encrypting/decrypting string.");
            System.out.println("Exception: " + e);
            return null;
        }
    }

    // Hash the password using PBKDF2
    public static String hashPassword(String password, byte[] salt) {
        char[] passwordChars = password.toCharArray();
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            KeySpec spec = new PBEKeySpec(passwordChars, salt, 65536, 128);
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.out.println("Error hashing password.");
            System.out.println("Exception: " + e);
            return null;
        }
    }

    // Generate a random salt
    public static byte[] generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    // Password masking
    public static String maskPassword() {
        String password =  "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.print("Password: ");
            Pair<WinNT.HANDLE, WinDef.DWORDByReference> osInfo = OS.prepareOS();
            WinNT.HANDLE handle = osInfo.getKey();
            WinDef.DWORDByReference mode = osInfo.getValue();
            OS.consoleRaw(handle, mode);
            int c;
            while ((c = reader.read()) != -1) {
                switch (c) {
                    case 10: case 13:
                        OS.consoleReset(handle, mode);
                        return password;
                    case 8: case 127:
                        if (password.length() > 0) {
                            System.out.print("\b \b");
                            password = password.substring(0, password.length() - 1);
                        }
                        break;
                    default:
                        System.out.print("*");
                        password += (char) c;
                }
            }
            OS.consoleReset(handle, mode);
        } catch (IOException e) {
            System.out.println("Error reading password.");
            System.out.println("Exception: " + e);
        }
        return password;
    }
}
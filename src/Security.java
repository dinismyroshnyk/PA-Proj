import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

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

    // Hash the password using MD5
    public static String hashPassword(String password) {
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            byte pwd[] = algorithm.digest(password.getBytes("UTF-8"));
            StringBuilder pass = new StringBuilder();
            for (byte b : pwd) {
                pass.append(String.format("%02X", 0xFF & b));
            }
            return pass.toString();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            System.out.println("Error hashing password.");
            System.out.println("Exception: " + e);
            return null;
        }
    }

    // Password masking
    public static String maskPassword() {
        String password =  "";
        System.out.print("Password: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            consoleRaw();
            int c;
            while ((c = reader.read()) != -1) {
                switch (c) {
                    case 10: case 13:
                        consoleReset();
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
            consoleReset();
        } catch (IOException e) {
            System.out.println("Error reading password.");
            System.out.println("Exception: " + e);
        }
        return password;
    }

    private static void consoleRaw() {
        String cmd[] = {"/bin/sh", "-c", "stty raw -echo </dev/tty"};
        try {
            Runtime.getRuntime().exec(cmd).waitFor();
        } catch (IOException | InterruptedException e) {
            System.out.println("Error setting terminal to raw mode.");
            System.out.println("Exception: " + e);
        }
    }

    private static void consoleReset() {
        String reset[] = {"/bin/sh", "-c", "stty sane </dev/tty"};
        try {
            Runtime.getRuntime().exec(reset).waitFor();
        } catch (IOException | InterruptedException e) {
            System.out.println("Error resetting terminal.");
            System.out.println("Exception: " + e);
        }
    }
}
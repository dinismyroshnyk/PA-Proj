import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Security {
    // Public methods
        // Encryption enum
        public enum EncryptionParam {
            ENCRYPT,
            DECRYPT
        }

        // Encrypt or decrypt a string using AES
        public static String encryptDecryptString(String string, EncryptionParam param) {
            try {
                SecretKeySpec key = new SecretKeySpec(ENCRYPTION_KEY.getBytes("UTF-8"), "AES");
                Cipher cipher = Cipher.getInstance("AES");
                if(param == EncryptionParam.ENCRYPT) {
                    cipher.init(Cipher.ENCRYPT_MODE, key);
                    byte[] encrypted = cipher.doFinal(string.getBytes("UTF-8"));
                    return Base64.getEncoder().encodeToString(encrypted);
                } else if(param == EncryptionParam.DECRYPT) {
                    cipher.init(Cipher.DECRYPT_MODE, key);
                    byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(string));
                    return new String(decrypted, "UTF-8");
                } else {
                    System.out.println("Invalid parameter. Please use ENCRYPT or DECRYPT.");
                    return null;
                }
            } catch (Exception e) {
                Utils.clearConsole();
                System.out.println("Error encrypting/decrypting string.");
                System.out.println("Exception: " + e);
                return null;
            }
        }

        // Password masking
        public static String maskPassword(String type) {
            String password =  "";
            System.out.print(type + ": ");
            OS.toggleConsoleMode(OS.getHandle(), OS.getMode(), OS.ConsoleMode.RAW);
            int c;
            while ((c = IO.readBufferedInt()) != -1) {
                switch (c) {
                    case 10: case 13:
                        OS.toggleConsoleMode(OS.getHandle(), OS.getMode(), OS.ConsoleMode.SANE);
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
            OS.toggleConsoleMode(OS.getHandle(), OS.getMode(), OS.ConsoleMode.SANE);
            return password;
        }

    // Helper methods
        // Encryption key
        private static final String ENCRYPTION_KEY = "6w|Uq)X.77hdh*=H8r[w[&EG*!i~HN]]";
}
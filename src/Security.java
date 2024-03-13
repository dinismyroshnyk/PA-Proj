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
            e.printStackTrace();
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
            e.printStackTrace();
            return null;
        }
    }
}

package org.example;
import org.springframework.web.bind.annotation.*;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
class SecureCommController {
    private static final SecretKey AES_KEY = generateAESKey();
    private static final IvParameterSpec AES_IV = generateAESIv();
    private static final SecretKey HMAC_KEY = generateHMACKey();

    // Endpoint: Szyfrowanie wiadomości
    @PostMapping("/encrypt")
    public Map<String, String> encrypt(@RequestBody Map<String, String> payload) {
        try {
            String message = payload.get("message");
            if (message == null || message.isEmpty()) {
                throw new IllegalArgumentException("Brak wiadomości do zaszyfrowania");
            }
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, AES_KEY, AES_IV);
            byte[] encryptedBytes = cipher.doFinal(message.getBytes());
            String encrypted = Base64.getEncoder().encodeToString(encryptedBytes);

            // Generowanie HMAC dla zaszyfrowanej wiadomości
            String hmac = generateHmac(encrypted);

            Map<String, String> response = new HashMap<>();
            response.put("encrypted", encrypted);
            response.put("hmac", hmac);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas szyfrowania", e);
        }
    }

    // Endpoint: Deszyfrowanie wiadomości
    @PostMapping("/decrypt")
    public Map<String, String> decrypt(@RequestBody Map<String, String> payload) {
        try {
            String encrypted = payload.get("encrypted");
            String hmac = payload.get("hmac");
            if (encrypted == null || encrypted.isEmpty() || hmac == null || hmac.isEmpty()) {
                throw new IllegalArgumentException("Niepoprawne dane wejściowe");
            }

            // Weryfikacja HMAC
            String recalculatedHmac = generateHmac(encrypted);
            if (!hmac.equals(recalculatedHmac)) {
                throw new SecurityException("Nieprawidłowy HMAC");
            }

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, AES_KEY, AES_IV);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encrypted));
            String decrypted = new String(decryptedBytes);

            Map<String, String> response = new HashMap<>();
            response.put("decrypted", decrypted);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Niepoprawne dane wejściowe");
        }
    }

    private static SecretKey generateAESKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            return keyGen.generateKey();
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas generowania klucza AES", e);
        }
    }

    private static IvParameterSpec generateAESIv() {
        byte[] iv = new byte[16];
        new java.security.SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    private static SecretKey generateHMACKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            keyGen.init(256);
            return keyGen.generateKey();
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas generowania klucza HMAC", e);
        }
    }

    private String generateHmac(String message) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(HMAC_KEY);
        return Base64.getEncoder().encodeToString(mac.doFinal(message.getBytes()));
    }
}
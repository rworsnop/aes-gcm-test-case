package testcase;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class CipherTestCase {
    private static final int CHUNKS = 10_000;
    private static final int CHUNK_SIZE = 1024;
    public static void main(String[] args) throws Exception {
        byte[] ciphertext = encrypt();
        decrypt(ciphertext);
    }

    private static byte[] encrypt() throws GeneralSecurityException, IOException {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(
                Cipher.ENCRYPT_MODE,
                new SecretKeySpec(new byte[16], "AES"),
                new GCMParameterSpec(128, new byte[12])
        );
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (CipherOutputStream cos = new CipherOutputStream(bos, cipher)) {
            for (int i = 0; i < CHUNKS; i++) {
                cos.write(new byte[CHUNK_SIZE]);
            }
        }
        return bos.toByteArray();
    }

    private static void decrypt(byte[] ciphertext) throws GeneralSecurityException, IOException {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(
                Cipher.DECRYPT_MODE,
                new SecretKeySpec(new byte[16], "AES"),
                new GCMParameterSpec(128, new byte[12])
        );
        for (int i = 0; i < CHUNKS; i++) {
            byte[] plain = cipher.update(ciphertext, i * CHUNK_SIZE, CHUNK_SIZE);
            // Will always be 0 bytes
            System.out.printf("Can't stream because only %d bytes are emitted at a time.\n", plain.length);
        }
        byte[] finalPlain = cipher.doFinal(ciphertext, CHUNKS * CHUNK_SIZE, 16);
        System.out.printf("Can't stream because it waits until doFinal and then emits %d bytes\n", finalPlain.length);
    }
}

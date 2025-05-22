package com.fct.we_chat.utils;

import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;

public class RSASender {

    private static final int MAX_BLOCK_SIZE = 245;
    public static String encryptMessage(String message, Key clavePublica2) {
    try {
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.ENCRYPT_MODE, clavePublica2);

        byte[] messageBytes = message.getBytes("UTF-8");
        StringBuilder encryptedMessage = new StringBuilder();

        // Dividir el mensaje en bloques de 245 bytes
        for (int i = 0; i < messageBytes.length; i += MAX_BLOCK_SIZE) {
            int blockLength = Math.min(MAX_BLOCK_SIZE, messageBytes.length - i);
            byte[] block = new byte[blockLength];
            System.arraycopy(messageBytes, i, block, 0, blockLength);

            // Cifrar el bloque actual
            byte[] encryptedBlock = rsaCipher.doFinal(block);

            // Codificar en Base64 para enviar por la red
            encryptedMessage.append(Base64.getEncoder().encodeToString(encryptedBlock)).append(":");
        }

        // Eliminar el último ":" sobrante
        return encryptedMessage.substring(0, encryptedMessage.length() - 1);

    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}

}
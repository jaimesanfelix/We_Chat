package com.fct.we_chat.utils;

import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;

public class RSAReceiver {

    public static String decryptMessage(String encryptedMessage, Key privateKey) {
    try {
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);

        StringBuilder decryptedMessage = new StringBuilder();

        // Dividir el mensaje cifrado en bloques
        String[] encryptedBlocks = encryptedMessage.split(":");

        for (String block : encryptedBlocks) {

            // Limpiar la cadena Base64 de caracteres no válidos (por ejemplo, saltos de línea)
            block = block.replaceAll("[^A-Za-z0-9+/=]", "");

            // Asegurarse de que la longitud de la cadena Base64 sea un múltiplo de 4
            while (block.length() % 4 != 0) {
                block += "=";  // Añadir el relleno necesario
            }

            try {
                // Decodificar cada bloque de Base64
                byte[] encryptedBlock = Base64.getDecoder().decode(block);

                // Desencriptar el bloque
                byte[] decryptedBlock = rsaCipher.doFinal(encryptedBlock);

                // Convertir el bloque desencriptado a String y agregarlo al mensaje final
                decryptedMessage.append(new String(decryptedBlock, "UTF-8"));
            } catch (IllegalArgumentException e) {
                System.err.println("Error al decodificar Base64 para el bloque: " + block);
                e.printStackTrace();  // Para depuración más detallada
            }
        }

        return decryptedMessage.toString();

    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}
}

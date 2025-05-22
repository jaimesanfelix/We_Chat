package com.fct.we_chat.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

public class HybridEncryption {

    // Generar par de claves RSA (clave pública y privada)
    public static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

    // Generar clave AES
    public static SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        return keyGen.generateKey();
    }

    // Cifrar datos con AES
    public static byte[] encryptAES(byte[] data, SecretKey key, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        return cipher.doFinal(data);
    }

    // Descifrar datos con AES
    public static byte[] decryptAES(byte[] data, SecretKey key, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        return cipher.doFinal(data);
    }

    // Cifrar clave AES con RSA
    public static byte[] encryptRSA(byte[] data, PublicKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    // Descifrar clave AES con RSA
    public static byte[] decryptRSA(byte[] data, PrivateKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public static void main(String[] args) throws Exception {
        // 1️⃣ Generar claves RSA
        KeyPair rsaKeyPair = generateRSAKeyPair();
        PublicKey publicKey = rsaKeyPair.getPublic();
        PrivateKey privateKey = rsaKeyPair.getPrivate();

        // 2️⃣ Generar clave AES
        SecretKey aesKey = generateAESKey();

        // 3️⃣ Cifrar el mensaje con AES
        String message = "¡Hola, este es un mensaje secreto cifrado con AES y RSA!";
        IvParameterSpec iv = new IvParameterSpec(new byte[16]); // IV para AES-CBC
        byte[] encryptedMessage = encryptAES(message.getBytes(), aesKey, iv);

        // 4️⃣ Cifrar la clave AES con RSA
        byte[] encryptedAESKey = encryptRSA(aesKey.getEncoded(), publicKey);

        // 📦 Simulando el envío de encryptedMessage y encryptedAESKey

        // 5️⃣ Descifrar la clave AES con RSA
        byte[] decryptedAESKeyBytes = decryptRSA(encryptedAESKey, privateKey);
        SecretKey originalAESKey = new SecretKeySpec(decryptedAESKeyBytes, "AES");

        // 6️⃣ Descifrar el mensaje con AES
        byte[] decryptedMessage = decryptAES(encryptedMessage, originalAESKey, iv);

        // 7️⃣ Mostrar resultados
        System.out.println("Mensaje original: " + message);
        System.out.println("Mensaje cifrado (Base64): " + Base64.getEncoder().encodeToString(encryptedMessage));
        System.out.println("Clave AES cifrada (Base64): " + Base64.getEncoder().encodeToString(encryptedAESKey));
        System.out.println("Mensaje descifrado: " + new String(decryptedMessage));
    }
}

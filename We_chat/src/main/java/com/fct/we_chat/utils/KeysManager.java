package com.fct.we_chat.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Utility class for managing RSA key pairs.
 */
public class KeysManager {
    private static final String FICHERO_CLAVE_PUBLICA = "C:\\Users\\jaime\\Desktop\\Proyecto final\\We_chat\\src\\main\\java\\com\\fct\\we_chat\\utils\\public.key";
    private static final String FICHERO_CLAVE_PRIVADA = "C:\\Users\\jaime\\Desktop\\Proyecto final\\We_chat\\src\\main\\java\\com\\fct\\we_chat\\utils\\private.key";

    /**
     * Generates a new RSA key pair.
     * 
     * @return the generated RSA key pair
     * @throws NoSuchAlgorithmException if the RSA algorithm is not available
     */
    public static KeyPair generarClaves() throws NoSuchAlgorithmException {
        KeyPairGenerator generador = KeyPairGenerator.getInstance("RSA");
        generador.initialize(2048);
        KeyPair claves = generador.generateKeyPair();
        return claves;
    }

    /**
     * Saves the RSA key pair to files.
     * 
     * @param claves the RSA key pair to save
     * @throws Exception if an error occurs while saving the keys
     */
    public static void guardarClaves(KeyPair claves) throws Exception {
        FileOutputStream fos = new FileOutputStream(FICHERO_CLAVE_PUBLICA);
        fos.write(claves.getPublic().getEncoded());
        fos.close();
        fos = new FileOutputStream(FICHERO_CLAVE_PRIVADA);
        fos.write(claves.getPrivate().getEncoded());
        fos.close();
    }

    /**
     * Retrieves the public key from the file.
     * 
     * @return the public key
     * @throws Exception if an error occurs while retrieving the key
     */
    public static PublicKey getClavePublica() throws Exception {
        File ficheroClavePublica = new File(FICHERO_CLAVE_PUBLICA);
        byte[] bytesClavePublica = Files.readAllBytes(ficheroClavePublica.toPath());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(bytesClavePublica);
        PublicKey clavePublica = keyFactory.generatePublic(publicKeySpec);
        return clavePublica;
    }

    /**
     * Retrieves the private key from the file.
     * 
     * @return the private key
     * @throws Exception if an error occurs while retrieving the key
     */
    public static PrivateKey getClavePrivada() throws Exception {
        File ficheroClavePrivada = new File(FICHERO_CLAVE_PRIVADA);
        byte[] bytesClavePrivada = Files.readAllBytes(ficheroClavePrivada.toPath());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        EncodedKeySpec publicKeySpec = new PKCS8EncodedKeySpec(bytesClavePrivada);
        PrivateKey clavePrivada = keyFactory.generatePrivate(publicKeySpec);
        return clavePrivada;
    }

    /**
     * Main method to generate and save RSA key pairs.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            KeyPair claves = generarClaves();
            guardarClaves(claves);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for password hashing using SHA-256 algorithm.
 * Provides methods to hash passwords and verify them.
 * 
 * Required by Module 1: Passwords must not be stored in plain text.
 * Apply SHA-256 hashing before persisting to the database.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class PasswordHasher {
    
    private static final Logger LOGGER = Logger.getLogger(PasswordHasher.class.getName());
    private static final String ALGORITHM = "SHA-256";
    
    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private PasswordHasher() {
    }
    
    /**
     * Hashes a plain text password using SHA-256 algorithm.
     * 
     * @param plainPassword the plain text password to hash
     * @return the hexadecimal string representation of the hash,
     *         or null if hashing fails
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            LOGGER.log(Level.WARNING, "Cannot hash null or empty password");
            return null;
        }
        
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] encodedHash = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "SHA-256 algorithm not available: {0}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Verifies a plain text password against a stored hash.
     * 
     * @param plainPassword the plain text password to verify
     * @param storedHash the stored SHA-256 hash to compare against
     * @return true if the password matches the hash, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) {
            return false;
        }
        
        String hashedInput = hashPassword(plainPassword);
        return storedHash.equals(hashedInput);
    }
    
    /**
     * Converts a byte array to a hexadecimal string.
     * 
     * @param hash the byte array to convert
     * @return the hexadecimal string representation
     */
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    /**
     * Hashes a password and returns it in the format expected by the database.
     * Convenience method that delegates to hashPassword().
     * 
     * @param plainPassword the plain text password
     * @return the SHA-256 hash as a hexadecimal string
     */
    public static String encode(String plainPassword) {
        return hashPassword(plainPassword);
    }
    
    /**
     * Checks if a string appears to be a valid SHA-256 hash.
     * 
     * @param hash the string to check
     * @return true if the string is a 64-character hexadecimal string
     */
    public static boolean isValidHashFormat(String hash) {
        if (hash == null || hash.length() != 64) {
            return false;
        }
        return hash.matches("^[0-9a-fA-F]+$");
    }
}
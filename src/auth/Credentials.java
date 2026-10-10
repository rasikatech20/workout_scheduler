package auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Credentials entity class for storing and verifying user authentication credentials.
 * Supports salted SHA-256 password hashing for robust security and constant-time
 * hash verification to guard against timing attacks.
 */
public class Credentials {

    private String userId;
    private String salt;
    private String passwordHash;

    public static final String DELIMITER = ":";
    private static final int SALT_BYTES_LENGTH = 16;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Constructs new Credentials by generating a cryptographically secure salt
     * and hashing the entered raw password.
     *
     * @param userId   the unique identifier of the user (e.g. "1001" or username)
     * @param password the plaintext password to hash and store
     * @throws IllegalArgumentException if userId or password is null or empty
     */
    public Credentials(String userId, String password) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty.");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }
        this.userId = userId.trim();
        this.salt = generateSalt();
        this.passwordHash = hashPassword(password, this.salt);
    }

    /**
     * Overload constructor accepting integer userId for seamless integration
     * with User model instances.
     *
     * @param userId   the integer user identifier
     * @param password the plaintext password
     */
    public Credentials(int userId, String password) {
        this(String.valueOf(userId), password);
    }

    /**
     * Constructs Credentials with precomputed salt and password hash.
     * Typically invoked when loading stored records from file storage.
     *
     * @param userId       the user identifier
     * @param salt         the hexadecimal salt string
     * @param passwordHash the hexadecimal salted password hash
     * @throws IllegalArgumentException if any argument is null or empty
     */
    public Credentials(String userId, String salt, String passwordHash) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty.");
        }
        if (salt == null || salt.trim().isEmpty()) {
            throw new IllegalArgumentException("Salt cannot be null or empty.");
        }
        if (passwordHash == null || passwordHash.trim().isEmpty()) {
            throw new IllegalArgumentException("Password hash cannot be null or empty.");
        }
        this.userId = userId.trim();
        this.salt = salt.trim();
        this.passwordHash = passwordHash.trim();
    }

    /**
     * Verifies if an entered raw password matches this credential's stored hash
     * using the stored salt and constant-time byte comparison.
     *
     * @param enteredPassword the raw password entered by the user
     * @return true if credentials match, false otherwise
     */
    public boolean verifyPassword(String enteredPassword) {
        if (enteredPassword == null || enteredPassword.isEmpty()) {
            return false;
        }
        if (this.salt == null || this.passwordHash == null) {
            return false;
        }
        String computedHash = hashPassword(enteredPassword, this.salt);
        byte[] expectedBytes = this.passwordHash.getBytes(StandardCharsets.UTF_8);
        byte[] actualBytes = computedHash.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(expectedBytes, actualBytes);
    }

    /**
     * Generates a random cryptographic salt encoded in hexadecimal format.
     *
     * @return 32-character hexadecimal salt string
     */
    public static String generateSalt() {
        byte[] saltBytes = new byte[SALT_BYTES_LENGTH];
        SECURE_RANDOM.nextBytes(saltBytes);
        return bytesToHex(saltBytes);
    }

    /**
     * Hashes a password with the provided salt using the SHA-256 algorithm.
     *
     * @param password the raw password to hash
     * @param salt     the salt string
     * @return 64-character hexadecimal SHA-256 hash
     */
    public static String hashPassword(String password, String salt) {
        if (password == null || salt == null) {
            throw new IllegalArgumentException("Password and salt cannot be null for hashing.");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 cryptographic algorithm unavailable.", e);
        }
    }

    /**
     * Converts a byte array to a lowercase hexadecimal string.
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Formats this credential record into a persistent file format line:
     * {@code userId:salt:passwordHash}
     *
     * @return formatted record string
     */
    public String toFileFormat() {
        return userId + DELIMITER + salt + DELIMITER + passwordHash;
    }

    /**
     * Parses a credential record from a formatted file line.
     * Ignores surrounding whitespace. Returns null if the line is empty,
     * commented, or malformed.
     *
     * @param line the text line from the credentials file
     * @return a Credentials instance, or null if line is not a valid record
     */
    public static Credentials fromFileFormat(String line) {
        if (line == null) {
            return null;
        }
        String trimmed = line.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("#")) {
            return null; // Empty line or comment
        }
        String[] parts = trimmed.split(DELIMITER);
        if (parts.length != 3) {
            return null; // Malformed record line
        }
        String uId = parts[0].trim();
        String s = parts[1].trim();
        String hash = parts[2].trim();
        if (uId.isEmpty() || s.isEmpty() || hash.isEmpty()) {
            return null;
        }
        return new Credentials(uId, s, hash);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty.");
        }
        this.userId = userId.trim();
    }

    public String getSalt() {
        return salt;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    @Override
    public String toString() {
        // Safe string representation that never exposes sensitive passwords or hashes
        return "Credentials{userId='" + userId + "', password=******}";
    }
}

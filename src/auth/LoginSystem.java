package auth;

import exceptions.AuthenticationException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * LoginSystem class handling user login and authentication operations using
 * Java File I/O for secure credential storage and retrieval.
 */
public class LoginSystem {

    public static final String DEFAULT_CREDENTIALS_FILE = "data/credentials.txt";

    private final File credentialsFile;
    private boolean loggedIn;
    private String currentUserId;

    /**
     * Constructs a LoginSystem using the default credentials file path ("data/credentials.txt").
     */
    public LoginSystem() {
        this(DEFAULT_CREDENTIALS_FILE);
    }

    /**
     * Constructs a LoginSystem using a custom credentials file path.
     *
     * @param credentialsFilePath path to the credentials text file
     */
    public LoginSystem(String credentialsFilePath) {
        if (credentialsFilePath == null || credentialsFilePath.trim().isEmpty()) {
            this.credentialsFile = new File(DEFAULT_CREDENTIALS_FILE);
        } else {
            this.credentialsFile = new File(credentialsFilePath);
        }
        this.loggedIn = false;
        this.currentUserId = null;
    }

    /**
     * Constructs a LoginSystem using a specific File object.
     *
     * @param credentialsFile the credentials File instance
     */
    public LoginSystem(File credentialsFile) {
        if (credentialsFile == null) {
            this.credentialsFile = new File(DEFAULT_CREDENTIALS_FILE);
        } else {
            this.credentialsFile = credentialsFile;
        }
        this.loggedIn = false;
        this.currentUserId = null;
    }

    /**
     * Attempts to log in a user with the provided user ID and password.
     * If authentication succeeds, the session state is updated to logged in.
     *
     * @param userId   the user's identifier
     * @param password the user's password
     * @return true if login succeeds, false otherwise
     */
    public boolean login(String userId, String password) {
        if (authenticate(userId, password)) {
            this.loggedIn = true;
            this.currentUserId = userId.trim();
            return true;
        }
        return false;
    }

    /**
     * Overload method to log in a user with an integer user ID.
     *
     * @param userId   the integer user identifier
     * @param password the user's password
     * @return true if login succeeds, false otherwise
     */
    public boolean login(int userId, String password) {
        return login(String.valueOf(userId), password);
    }

    /**
     * Authenticates entered user ID and password against the stored credential records.
     * Returns false gracefully if credentials do not match, inputs are empty, or file is missing.
     *
     * @param userId   the user identifier
     * @param password the raw password
     * @return true if valid credentials, false otherwise
     */
    public boolean authenticate(String userId, String password) {
        if (userId == null || userId.trim().isEmpty() || password == null || password.isEmpty()) {
            return false;
        }

        if (!credentialsFile.exists() || !credentialsFile.isFile()) {
            return false;
        }

        Credentials stored = findCredentialsByUserId(userId.trim());
        if (stored == null) {
            return false;
        }

        return stored.verifyPassword(password);
    }

    /**
     * Overload method to authenticate using an integer user ID.
     *
     * @param userId   the integer user identifier
     * @param password the raw password
     * @return true if valid credentials, false otherwise
     */
    public boolean authenticate(int userId, String password) {
        return authenticate(String.valueOf(userId), password);
    }

    /**
     * Performs strict authentication, throwing descriptive AuthenticationException
     * for invalid inputs, missing files, unregistered user IDs, or incorrect passwords.
     *
     * @param userId   the user identifier
     * @param password the user password
     * @return true on successful authentication
     * @throws AuthenticationException if any validation or authentication check fails
     */
    public boolean authenticateStrict(String userId, String password) throws AuthenticationException {
        if (userId == null || userId.trim().isEmpty()) {
            throw new AuthenticationException("Authentication failed: User ID cannot be empty.");
        }
        if (password == null || password.isEmpty()) {
            throw new AuthenticationException("Authentication failed: Password cannot be empty.");
        }
        if (!credentialsFile.exists() || !credentialsFile.isFile()) {
            throw new AuthenticationException("Authentication failed: Credentials store file not found at " + credentialsFile.getPath());
        }

        Credentials stored = findCredentialsByUserId(userId.trim());
        if (stored == null) {
            throw new AuthenticationException("Authentication failed: User ID '" + userId.trim() + "' not found.");
        }

        if (!stored.verifyPassword(password)) {
            throw new AuthenticationException("Authentication failed: Invalid password for User ID '" + userId.trim() + "'.");
        }

        return true;
    }

    /**
     * Finds and retrieves a stored credential record for the specified user ID.
     * Reads the file line by line using BufferedReader and UTF-8 encoding.
     *
     * @param userId the user ID to search for
     * @return the matching Credentials instance, or null if not found or on I/O error
     */
    public Credentials findCredentialsByUserId(String userId) {
        if (userId == null || userId.trim().isEmpty() || !credentialsFile.exists()) {
            return null;
        }

        String searchId = userId.trim();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(credentialsFile), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Credentials cred = Credentials.fromFileFormat(line);
                if (cred != null && cred.getUserId().equalsIgnoreCase(searchId)) {
                    return cred;
                }
            }
        } catch (IOException e) {
            System.err.println("Notice: Error reading credentials file: " + e.getMessage());
        }
        return null;
    }

    /**
     * Loads all valid credential records from the credentials file.
     *
     * @return list of Credentials
     * @throws IOException if a file reading error occurs
     */
    public List<Credentials> loadAllCredentials() throws IOException {
        List<Credentials> list = new ArrayList<>();
        if (!credentialsFile.exists()) {
            return list;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(credentialsFile), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Credentials cred = Credentials.fromFileFormat(line);
                if (cred != null) {
                    list.add(cred);
                }
            }
        }
        return list;
    }

    /**
     * Saves a new credential record to the credentials file using append mode.
     * Ensures parent directories exist and verifies that the user ID does not already exist
     * to avoid overwriting or duplicate records.
     *
     * @param credentials the Credentials instance to save
     * @return true if successfully saved, false if user already exists or on I/O failure
     */
    public synchronized boolean saveCredentials(Credentials credentials) {
        if (credentials == null || credentials.getUserId() == null || credentials.getUserId().trim().isEmpty()) {
            return false;
        }

        // Check if user already exists
        if (isUserIdRegistered(credentials.getUserId())) {
            return false;
        }

        // Ensure parent directory exists
        File parentDir = credentialsFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(credentialsFile, true), StandardCharsets.UTF_8))) {
            // If file already has content and does not end with newline, write newline
            if (credentialsFile.length() > 0) {
                writer.newLine();
            }
            writer.write(credentials.toFileFormat());
            writer.flush();
            return true;
        } catch (IOException e) {
            System.err.println("Error saving credentials: " + e.getMessage());
            return false;
        }
    }

    /**
     * Overload helper to create and save credentials for a user ID and password.
     *
     * @param userId   the user ID
     * @param password the raw password
     * @return true if successfully created and saved, false otherwise
     */
    public boolean saveCredentials(String userId, String password) {
        if (userId == null || userId.trim().isEmpty() || password == null || password.isEmpty()) {
            return false;
        }
        try {
            Credentials credentials = new Credentials(userId, password);
            return saveCredentials(credentials);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Overload helper to create and save credentials for an integer user ID and password.
     *
     * @param userId   the integer user ID
     * @param password the raw password
     * @return true if successfully created and saved, false otherwise
     */
    public boolean saveCredentials(int userId, String password) {
        return saveCredentials(String.valueOf(userId), password);
    }

    /**
     * Checks if a user ID is already registered in the credentials file.
     *
     * @param userId the user ID to check
     * @return true if found, false otherwise
     */
    public boolean isUserIdRegistered(String userId) {
        return findCredentialsByUserId(userId) != null;
    }

    /**
     * Checks whether the credentials file currently exists.
     *
     * @return true if file exists, false otherwise
     */
    public boolean credentialsFileExists() {
        return credentialsFile.exists() && credentialsFile.isFile();
    }

    /**
     * Gets the credentials File object.
     *
     * @return File object
     */
    public File getCredentialsFile() {
        return credentialsFile;
    }

    /**
     * Gets the path to the credentials file.
     *
     * @return file path string
     */
    public String getCredentialsFilePath() {
        return credentialsFile.getPath();
    }

    /**
     * Checks if a user session is currently logged in.
     *
     * @return true if logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return loggedIn;
    }

    /**
     * Gets the currently logged in user ID.
     *
     * @return user ID string or null if not logged in
     */
    public String getCurrentUserId() {
        return currentUserId;
    }

    /**
     * Logs out the current user session.
     */
    public void logout() {
        this.loggedIn = false;
        this.currentUserId = null;
    }
}

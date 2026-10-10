package test;

import auth.Credentials;
import auth.LoginSystem;
import exceptions.AuthenticationException;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class AuthVerificationTest {

    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("       LOGIN & AUTHENTICATION MODULE VERIFICATION SUITE           ");
        System.out.println("==================================================================\n");

        int passed = 0;
        int total = 0;

        File testFile = new File("scratch/test_credentials.txt");
        if (testFile.exists()) {
            testFile.delete();
        }

        LoginSystem authSystem = new LoginSystem(testFile.getPath());

        // Test 1: Missing file handling
        total++;
        System.out.println("Test 1: Missing credentials file handling");
        boolean missingFileLogin = authSystem.login("1001", "SomePassword");
        if (!missingFileLogin && !authSystem.isLoggedIn()) {
            System.out.println("  [PASS] Missing file handled gracefully without crash.");
            passed++;
        } else {
            System.err.println("  [FAIL] Missing file did not return false.");
        }

        // Test 2: Saving new credential records
        total++;
        System.out.println("\nTest 2: Storing new user credentials safely");
        boolean save1 = authSystem.saveCredentials("1001", "SecurePass@2026");
        boolean save2 = authSystem.saveCredentials("1002", "UserPass!789");
        if (save1 && save2 && testFile.exists()) {
            System.out.println("  [PASS] Stored credentials for user 1001 and 1002 in file.");
            passed++;
        } else {
            System.err.println("  [FAIL] Failed to save credentials.");
        }

        // Test 3: Duplicate user ID prevention (no overwrite)
        total++;
        System.out.println("\nTest 3: Preventing duplicate user ID registration (no overwrite)");
        boolean dupSave = authSystem.saveCredentials("1001", "NewPassword");
        if (!dupSave) {
            System.out.println("  [PASS] Duplicate user ID 1001 was correctly rejected.");
            passed++;
        } else {
            System.err.println("  [FAIL] Duplicate user ID was allowed.");
        }

        // Test 4: Successful login with correct credentials
        total++;
        System.out.println("\nTest 4: Successful login with correct credentials");
        boolean loginSuccess = authSystem.login("1001", "SecurePass@2026");
        if (loginSuccess && authSystem.isLoggedIn() && "1001".equals(authSystem.getCurrentUserId())) {
            System.out.println("  [PASS] Successfully logged in as user 1001.");
            passed++;
        } else {
            System.err.println("  [FAIL] Login failed for valid credentials.");
        }

        // Test 5: Logout
        total++;
        System.out.println("\nTest 5: Session logout");
        authSystem.logout();
        if (!authSystem.isLoggedIn() && authSystem.getCurrentUserId() == null) {
            System.out.println("  [PASS] Successfully logged out.");
            passed++;
        } else {
            System.err.println("  [FAIL] Logout failed.");
        }

        // Test 6: Overloaded integer userId login
        total++;
        System.out.println("\nTest 6: Integer userId login overload");
        boolean intLogin = authSystem.login(1002, "UserPass!789");
        if (intLogin && authSystem.isLoggedIn()) {
            System.out.println("  [PASS] Integer userId login succeeded.");
            passed++;
            authSystem.logout();
        } else {
            System.err.println("  [FAIL] Integer userId login failed.");
        }

        // Test 7: Incorrect password
        total++;
        System.out.println("\nTest 7: Incorrect password rejection");
        boolean wrongPass = authSystem.login("1001", "WrongPassword123");
        if (!wrongPass && !authSystem.isLoggedIn()) {
            System.out.println("  [PASS] Incorrect password rejected.");
            passed++;
        } else {
            System.err.println("  [FAIL] Incorrect password was accepted.");
        }

        // Test 8: Non-existent user ID
        total++;
        System.out.println("\nTest 8: Non-existent user ID rejection");
        boolean nonExistent = authSystem.login("9999", "SomePassword");
        if (!nonExistent && !authSystem.isLoggedIn()) {
            System.out.println("  [PASS] Non-existent user rejected.");
            passed++;
        } else {
            System.err.println("  [FAIL] Non-existent user was accepted.");
        }

        // Test 9: Empty and null inputs
        total++;
        System.out.println("\nTest 9: Empty and null inputs");
        boolean emptyUser = authSystem.login("", "Pass");
        boolean nullUser = authSystem.login(null, "Pass");
        boolean emptyPass = authSystem.login("1001", "");
        boolean nullPass = authSystem.login("1001", null);
        if (!emptyUser && !nullUser && !emptyPass && !nullPass) {
            System.out.println("  [PASS] Empty/null inputs properly rejected.");
            passed++;
        } else {
            System.err.println("  [FAIL] Empty/null input handling failed.");
        }

        // Test 10: Strict authentication exception handling
        total++;
        System.out.println("\nTest 10: Strict authentication exceptions");
        boolean strictCaught = false;
        try {
            authSystem.authenticateStrict("1001", "WrongPass");
        } catch (AuthenticationException e) {
            strictCaught = true;
            System.out.println("  [PASS] AuthenticationException caught as expected: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("  [FAIL] Unexpected exception: " + e);
        }
        if (strictCaught) passed++;

        // Test 11: Handling malformed and commented lines in credentials file
        total++;
        System.out.println("\nTest 11: File with comments and malformed lines");
        try (PrintWriter pw = new PrintWriter(new FileWriter(testFile, true))) {
            pw.println("# This is a comment line");
            pw.println("malformed_line_without_colons");
            pw.println("another:invalid");
        } catch (Exception e) {
            System.err.println("Error writing to test file: " + e.getMessage());
        }

        boolean validUserStillWorks = authSystem.login("1001", "SecurePass@2026");
        if (validUserStillWorks) {
            System.out.println("  [PASS] Malformed/comment lines ignored without affecting valid users.");
            passed++;
            authSystem.logout();
        } else {
            System.err.println("  [FAIL] Malformed lines broke credential reading.");
        }

        // Test 12: Password masking in toString()
        total++;
        System.out.println("\nTest 12: Password privacy in toString()");
        Credentials cred = new Credentials("1001", "SecretPass");
        String credStr = cred.toString();
        if (!credStr.contains("SecretPass") && !credStr.contains(cred.getPasswordHash())) {
            System.out.println("  [PASS] toString() does not reveal password or raw hash: " + credStr);
            passed++;
        } else {
            System.err.println("  [FAIL] toString() leaked sensitive data: " + credStr);
        }

        // Clean up test file
        testFile.delete();

        // Ensure default data/credentials.txt exists with demo user accounts
        File defaultDataFile = new File(LoginSystem.DEFAULT_CREDENTIALS_FILE);
        if (!defaultDataFile.exists()) {
            LoginSystem defaultSystem = new LoginSystem(defaultDataFile.getPath());
            defaultSystem.saveCredentials("1001", "Password@123");
            defaultSystem.saveCredentials("admin", "Admin@123");
            System.out.println("\n[INFO] Initialized default credentials at: " + defaultDataFile.getPath());
        }

        System.out.println("\n==================================================================");
        System.out.println("                TEST SUMMARY: " + passed + " / " + total + " PASSED");
        System.out.println("==================================================================");
    }
}

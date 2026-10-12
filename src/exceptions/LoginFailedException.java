package exceptions;

/**
 * Exception thrown when a user cannot be authenticated.
 */
public class LoginFailedException extends Exception {
    public LoginFailedException(String message) {
        super(message);
    }
    public String toString()
    {    
        return "Invalid username ! login failed";
    }

}

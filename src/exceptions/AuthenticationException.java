package exceptions;

/**
 * Exception thrown when authentication fails due to invalid credentials,
 * user not found, empty inputs, or credential storage access errors.
 */
public class AuthenticationException extends WorkoutSchedulerException {
    
    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}

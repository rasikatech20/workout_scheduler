package exceptions;

/**
 * Exception thrown when a user cannot be authenticated.
 */
public class LoginFailedException extends WorkoutSchedulerException {
    public LoginFailedException(String message) {
        super(message);
    }
}

package exceptions;

/**
 * Exception thrown when user input data (e.g. age, weight, height, blood pressure, availability) is invalid.
 */
public class InvalidUserDataException extends Exception {
    public InvalidUserDataException(String message) {
        super(message);
    }
}

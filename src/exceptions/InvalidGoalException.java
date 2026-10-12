package exceptions;

/**
 * Exception thrown when health goal input parameters (e.g. target days, target weight, null goal) are invalid.
 */
public class InvalidGoalException extends Exception {
    public InvalidGoalException(String message) {
        super(message);
    }
    public String toString()
    {    
        return "goal cannot be null";
    }

}

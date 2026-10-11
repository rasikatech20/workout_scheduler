package exceptions;

/**
 * Exception thrown when health goal input parameters (e.g. target days, target weight, null goal) are invalid.
 */
public class InvalidGoalException extends WorkoutSchedulerException {
    public InvalidGoalException(String message) {
        super(message);
    }
}

package exceptions;

/**
 * Exception thrown when a user sets an unrealistic or medically unsafe health goal timeline
 * and declines to adjust it to recommended safe guidelines.
 */
public class UnrealisticGoalException extends Exception {
    public UnrealisticGoalException(String message) {
        super(message);
    }
}

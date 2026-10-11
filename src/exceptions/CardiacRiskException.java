package exceptions;

/**
 * Exception thrown when high-intensity exercise or unsafe scheduling is assigned to a user with cardiac conditions.
 */
public class CardiacRiskException extends WorkoutSchedulerException {
    public CardiacRiskException(String message) {
        super(message);
    }
}

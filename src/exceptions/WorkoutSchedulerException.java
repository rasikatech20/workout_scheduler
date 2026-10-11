package exceptions;

/**
 * Base custom exception for the Workout Management & Scheduler System.
 */
public class WorkoutSchedulerException extends Exception {
    public WorkoutSchedulerException(String message) {
        super(message);
    }

    public WorkoutSchedulerException(String message, Throwable cause) {
        super(message, cause);
    }
}

package tracker;

import exercises.Exercise;

/**
 * CalorieCalculator helper class as defined in the UML Class Diagram.
 * Calculates calories burned for a given exercise object.
 */
public class CalorieCalculator {

    /**
     * Calculates calories burned for an Exercise and stores the result inside the exercise.
     * @param exercise the exercise to calculate calories for
     * @return calculated calories burned as float
     */
    public float calculateCalories(Exercise exercise) {
        if (exercise == null) {
            return 0.0f;
        }
        float calories = exercise.calculateCaloriesBurned();
        exercise.setCalculatedCalories(calories);
        return calories;
    }
}

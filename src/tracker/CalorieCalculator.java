package tracker;

import exercises.Exercise;
import java.util.function.Function;

public class CalorieCalculator<T extends Exercise> {

    public <E extends T> float calculateCalories(E exercise) {
        if (exercise == null) {
            return 0.0f;
        }

        Function<E, Float> calorieComputer = ex -> ex.calculateCaloriesBurned();

        float calories = calorieComputer.apply(exercise);
        exercise.setCalculatedCalories(calories);
        return calories;
    }
}

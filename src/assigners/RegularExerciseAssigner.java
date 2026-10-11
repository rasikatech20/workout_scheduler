package assigners;

import exercises.Exercise;
import exercises.ExerciseRepository;
import exercises.WeightGainExercise;
import exercises.WeightLossExercise;
import goals.HealthGoal;
import goals.WeightGainGoal;
import goals.WeightLossGoal;
import java.util.ArrayList;
import java.util.List;

/**
 * Assigns primary exercises matching the user's HealthGoal and cardiac status.
 */
public class RegularExerciseAssigner extends ExerciseAssigner {

    public RegularExerciseAssigner() {
        super();
    }

    @Override
    public void setExercisesAssigned(boolean hasCardiacProblem, HealthGoal healthGoal) {
        suitableExercises.clear();

        if (healthGoal == null) {
            return;
        }

        if (healthGoal instanceof WeightLossGoal) {
            List<WeightLossExercise> pool = ExerciseRepository.getPredefinedWeightLossExercises();
            for (WeightLossExercise ex : pool) {
                if (hasCardiacProblem) {
                    if (ex.getIntensityLevel() <= 5.0f) { //if user has cardiac problem and that exercise is less intense-then accept
                        suitableExercises.add(ex);
                    }
                } else {
                    suitableExercises.add(ex);
                }
            }
        } else if (healthGoal instanceof WeightGainGoal) {
            List<WeightGainExercise> pool = ExerciseRepository.getPredefinedWeightGainExercises();
            for (WeightGainExercise ex : pool) {
                if (hasCardiacProblem) {
                    if (ex.getIntensityLevel() <= 5.5f) {
                        suitableExercises.add(ex);
                    }
                } else {
                    suitableExercises.add(ex);
                }
            }
        }
    }
}

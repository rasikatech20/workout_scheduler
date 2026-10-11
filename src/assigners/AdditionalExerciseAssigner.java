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
 * Assigns additional/supplemental exercises matching the user's HealthGoal and cardiac status.
 */
public class AdditionalExerciseAssigner implements ExerciseAssigner {
    private List<Exercise> suitableExercises;

    public AdditionalExerciseAssigner() {
        this.suitableExercises = new ArrayList<>();
    }

    @Override
    public void setExercisesAssigned(boolean hasCardiacProblem, HealthGoal healthGoal) {
        suitableExercises.clear();

        if (healthGoal == null) {
            return;
        }

        if (healthGoal instanceof WeightLossGoal) {
            List<WeightLossExercise> pool = ExerciseRepository.getAdditionalWeightLossExercises();
            for (WeightLossExercise ex : pool) {
                if (hasCardiacProblem) {
                    if (ex.getIntensityLevel() <= 4.5f) {
                        suitableExercises.add(ex);
                    }
                } else {
                    suitableExercises.add(ex);
                }
            }
        } else if (healthGoal instanceof WeightGainGoal) {
            List<WeightGainExercise> pool = ExerciseRepository.getAdditionalWeightGainExercises();
            for (WeightGainExercise ex : pool) {
                if (hasCardiacProblem) {
                    if (ex.getIntensityLevel() <= 4.5f) {
                        suitableExercises.add(ex);
                    }
                } else {
                    suitableExercises.add(ex);
                }
            }
        }
    }

    @Override
    public List<Exercise> getExerciseAssigned() {
        return new ArrayList<>(suitableExercises);
    }
}

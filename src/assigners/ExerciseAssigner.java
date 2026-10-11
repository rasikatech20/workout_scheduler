package assigners;

import exercises.Exercise;
import goals.HealthGoal;
import java.util.ArrayList;
import java.util.List;

public abstract class ExerciseAssigner {
    protected List<Exercise> suitableExercises;

    public ExerciseAssigner() {
        this.suitableExercises = new ArrayList<>();
    }

    public abstract void setExercisesAssigned(boolean hasCardiacProblem, HealthGoal healthGoal);

    public List<Exercise> getExerciseAssigned() {
        return new ArrayList<>(suitableExercises);
    }
}

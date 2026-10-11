package assigners;

import exercises.Exercise;
import goals.HealthGoal;
import java.util.List;

public interface ExerciseAssigner {
    void setExercisesAssigned(boolean hasCardiacProblem, HealthGoal healthGoal);
    List<Exercise> getExerciseAssigned();
}

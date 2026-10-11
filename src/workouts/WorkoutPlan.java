package workouts;

import java.util.ArrayList;
import java.util.List;

public class WorkoutPlan {
    private List<Workout> workouts;

    public WorkoutPlan() {
        this.workouts = new ArrayList<>();
    }

    public WorkoutPlan(List<Workout> workouts) {
        this.workouts = workouts != null ? workouts : new ArrayList<>();
    }

    public List<Workout> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(List<Workout> workouts) {
        this.workouts = workouts;
    }

    public void addWorkout(Workout workout) {
        if (workout != null) {
            this.workouts.add(workout);
        }
    }

    public Workout getWorkoutForDay(int dayNumber) {
        if (dayNumber >= 1 && dayNumber <= workouts.size()) {
            return workouts.get(dayNumber - 1);
        }
        return null;
    }
}

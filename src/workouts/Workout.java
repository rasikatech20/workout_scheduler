package workouts;

import exercises.Exercise;
import goals.HealthGoal;
import java.util.ArrayList;
import java.util.List;

public class Workout {
    private int workoutId;
    private HealthGoal healthGoal;
    private List<Exercise> exercises;
    private int workoutMinutes;

    public Workout(int workoutId, HealthGoal healthGoal) {
        this.workoutId = workoutId;
        this.healthGoal = healthGoal;
        this.exercises = new ArrayList<>();
        this.workoutMinutes = 0;
    }

    public Workout(int workoutId, HealthGoal healthGoal, List<Exercise> exercises) {
        this.workoutId = workoutId;
        this.healthGoal = healthGoal;
        this.exercises = exercises != null ? exercises : new ArrayList<>();
        calculateWorkoutDuration();
    }

    public int getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(int workoutId) {
        this.workoutId = workoutId;
    }

    public HealthGoal getHealthGoal() {
        return healthGoal;
    }

    public void setHealthGoal(HealthGoal healthGoal) {
        this.healthGoal = healthGoal;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
        calculateWorkoutDuration();
    }

    public int getWorkoutMinutes() {
        return workoutMinutes;
    }

    public void setWorkoutMinutes(int workoutMinutes) {
        this.workoutMinutes = workoutMinutes;
    }

    public void addExercise(Exercise exercise) {
        if (exercise != null) {
            this.exercises.add(exercise);
            calculateWorkoutDuration(); //recalculating duration after adding a new exercise into workout
        }
    }

    public void markCompletedExercise(int exerciseId) { //to mark an exercise of this workout as completed
        for (Exercise exercise : exercises) {
            if (exercise.getExerciseId() == exerciseId) {
                exercise.setComplete(true);
                break;
            }
        }
    }

    public void calculateWorkoutDuration() {
        int totalMinutes = 0;
        if (exercises != null) {
            for (Exercise exercise : exercises) {
                totalMinutes += exercise.getDurationMinutes();
            }
        }
        this.workoutMinutes = totalMinutes;
    }
}

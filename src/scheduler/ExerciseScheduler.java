package scheduler;

import exercises.Exercise;
import goals.HealthGoal;
import workouts.Workout;
import java.util.ArrayList;
import java.util.List;

public class ExerciseScheduler {

    private final int breakTime = 5;
    private float cumulativeTime;
    private int currentIndex = 0; 

    private List<Exercise> exercises;
    private int targetDays;
    private int daysPerWeek;

    public ExerciseScheduler(List<Exercise> exercises, int targetDays, int daysPerWeek) {
        this.exercises = exercises;
        this.targetDays = targetDays;
        this.daysPerWeek = daysPerWeek;
    }

    public int computeNoOFExercisesperday() {
        int totalWeeks = (targetDays + 6) / 7;
        int totalWorkoutDays = totalWeeks * daysPerWeek;

        if (totalWorkoutDays == 0) {
            return 0;
        }

        return (int) Math.ceil((double) exercises.size() / totalWorkoutDays);
    }

    public void displayScheduledExercises() {
        int exercisesPerDay = computeNoOFExercisesperday();
        cumulativeTime = 0;
    
        int count = 0;
        int limit = currentIndex + exercisesPerDay;
    
        while (currentIndex < limit && currentIndex < exercises.size()) {
            Exercise ex = exercises.get(currentIndex);
            int duration = ex.getDurationMinutes();
    
            if (count > 0) {
                cumulativeTime += breakTime;
                System.out.println("Break: " + breakTime + " minutes");
            }
    
            System.out.println(ex.getName() + " - " + duration + " minutes");
            cumulativeTime += duration;
            
            currentIndex++; // Advance to the next exercise for tomorrow
            count++;
        }
    
        System.out.println("Number of exercises: " + count);
        System.out.println("Total time: " + cumulativeTime + " minutes");
    }

    public List<Workout> getWorkoutSchedule(HealthGoal healthGoal) {
        List<Workout> schedule = new ArrayList<>();
        int totalWorkoutDays = ((targetDays + 6) / 7) * daysPerWeek;
        
        if (totalWorkoutDays == 0 || exercises.isEmpty()) {
            return schedule;
        }
        int workoutDays = Math.min(totalWorkoutDays, exercises.size());
        int perDay = exercises.size() / workoutDays;
        int extra = exercises.size() % workoutDays; 
        int start = 0;
        for (int day = 0; day < workoutDays; day++) {
            int count = perDay + (day < extra ? 1 : 0);
            int end = start + count;
            List<Exercise> dayExercises = new ArrayList<>(exercises.subList(start, end));
            schedule.add(new Workout(day + 1, healthGoal, dayExercises));
            start = end;
        }
        return schedule;
    }
}
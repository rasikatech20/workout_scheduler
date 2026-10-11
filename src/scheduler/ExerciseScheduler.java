package scheduler;

import exercises.Exercise;
import goals.HealthGoal;
import java.util.ArrayList;
import java.util.List;
import workouts.Workout;

public class ExerciseScheduler {

    private final int breakTime = 5;
    private float cumulativeTime;
    private int currentIndex = 0;

    private List<Exercise> exercises;
    private int targetDays;
    private int daysPerWeek;
    private float hoursPerDay;

    public ExerciseScheduler(List<Exercise> exercises, int targetDays,
                             int daysPerWeek, float hoursPerDay) {
        this.exercises = exercises;
        this.targetDays = targetDays;
        this.daysPerWeek = daysPerWeek;
        this.hoursPerDay = hoursPerDay;
    }

    public ExerciseScheduler(List<Exercise> exercises, int targetDays,
                             int daysPerWeek) {
        this(exercises, targetDays, daysPerWeek, 1.0f);
    }

    public int computeNoOFExercisesperday() {
        int totalWeeks = (targetDays + 6) / 7;
        int totalWorkoutDays = totalWeeks * daysPerWeek;

        if (totalWorkoutDays <= 0) {
            return 0;
        }

        return (int) Math.ceil(
            (double) exercises.size() / totalWorkoutDays
        );
    }

    public boolean hasMoreExercises() {
        return currentIndex < exercises.size();
    }

    public void resetScheduleIndex() {
        this.currentIndex = 0;
    }

    public void displayScheduledExercises() {
        int availableTime = (int) (hoursPerDay * 60);
        cumulativeTime = 0;
        int count = 0;

        while (currentIndex < exercises.size()) {
            Exercise ex = exercises.get(currentIndex);
            int duration = ex.getDurationMinutes();
            int timeNeeded = duration;

            if (count > 0) {
                timeNeeded += breakTime;
            }

            if (cumulativeTime + timeNeeded > availableTime) {
                break;
            }

            if (count > 0) {
                System.out.println("  Break: " + breakTime + " minutes");
                cumulativeTime += breakTime;
            }

            System.out.println("  - " + ex.getName()
                    + " (" + duration + " minutes)");

            cumulativeTime += duration;
            currentIndex++;
            count++;
        }

        System.out.println("  Number of exercises: " + count);
        System.out.println("  Total day time: " + cumulativeTime + " minutes");

        if (count == 0 && currentIndex < exercises.size()) {
            System.out.println(
                "No remaining exercise fits within today's time limit."
            );
        }
    }

    public List<Workout> getWorkoutSchedule(HealthGoal healthGoal) {
        List<Workout> schedule = new ArrayList<>();

        int totalWorkoutDays = ((targetDays + 6) / 7) * daysPerWeek;

        if (totalWorkoutDays <= 0 || hoursPerDay <= 0
                || exercises.isEmpty()) {
            return schedule;
        }

        int availableTime = (int) (hoursPerDay * 60);
        int workoutDays = Math.min(totalWorkoutDays, exercises.size());
        int perDay = exercises.size() / workoutDays;
        int extra = exercises.size() % workoutDays;
        int start = 0;

        for (int day = 0; day < workoutDays && start < exercises.size(); day++) {
            List<Exercise> dayExercises = new ArrayList<>();
            int cumulativeTime = 0;

            while (start < exercises.size()) {
                Exercise ex = exercises.get(start);
                int duration = ex.getDurationMinutes();
                int timeNeeded = duration;

                if (!dayExercises.isEmpty()) {
                    timeNeeded += breakTime;
                }

                if (cumulativeTime + timeNeeded > availableTime) {
                    break;
                }

                cumulativeTime += timeNeeded;
                dayExercises.add(ex);
                start++;
            }

            if (dayExercises.isEmpty()) {
                System.out.println(
                    "Exercise " + exercises.get(start).getName()
                    + " cannot fit within the daily time limit."
                );
                break;
            }

            schedule.add(new Workout(day + 1, healthGoal, dayExercises));
        }

        if (start < exercises.size()) {
            System.out.println("Exercises still needing scheduling: "
                    + (exercises.size() - start));
        }

        return schedule;
    }
}

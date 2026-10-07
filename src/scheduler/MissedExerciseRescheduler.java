package scheduler;

import exercises.Exercise;
import java.util.ArrayList;
import java.util.List;

public class MissedExerciseRescheduler {

    private int noOfDays;
    private float hoursPerDay;

    private final int breakTime = 5;

    public void rescheduleMissedExercises(
            List<Exercise> exercises, int noOfDays, float hoursPerDay) {

        this.noOfDays = noOfDays;
        this.hoursPerDay = hoursPerDay;

        if (noOfDays <= 0 || hoursPerDay <= 0) {
            System.out.println("Invalid schedule details.");
            return;
        }

        List<Exercise> missedExercises = new ArrayList<>();

        for (Exercise ex : exercises) {
            if (!ex.isComplete) {
                missedExercises.add(ex);
            }
        }

        if (missedExercises.isEmpty()) {
            System.out.println("No missed exercises to reschedule!");
            return;
        }

        int availableTime = (int) (hoursPerDay * 60);
        int index = 0;

        System.out.println("Missed exercises: " + missedExercises.size());

        for (int day = 1; day <= noOfDays; day++) {

            int cumulativeTime = 0;
            int count = 0;

            System.out.println("\nDay " + day + ":");

            while (index < missedExercises.size()) {

                Exercise ex = missedExercises.get(index);
                int duration = ex.getDurationMinutes;
                int timeNeeded = duration;

                if (count > 0) {
                    timeNeeded += breakTime;
                }

                if (cumulativeTime + timeNeeded > availableTime) {
                    break;
                }

                if (count > 0) {
                    System.out.println("Break: " + breakTime + " minutes");
                    cumulativeTime += breakTime;
                }

                System.out.println(
                    ex.getName() + " - " + duration + " minutes"
                );

                cumulativeTime += duration;
                count++;
                index++;
            }

            if (count == 0 && index < missedExercises.size()) {
                System.out.println(
                    "No remaining exercise fits within today's time limit."
                );
            }

            System.out.println("Total time: " + cumulativeTime + " minutes");

            if (index == missedExercises.size()) {
                break;
            }
        }

        if (index < missedExercises.size()) {
            System.out.println("\nExercises still needing rescheduling: "
                    + (missedExercises.size() - index));
        }
    }
     public List<Workout> getRescheduledWorkouts(List<Exercise> missedExercises,
                                            HealthGoal healthGoal,
                                            int startWorkoutId)
    {
        List<Workout> rescheduled = new ArrayList<>();
    
        float minutesPerDay = hoursPerDay * 60;
        int index = 0;
    
        for (int day = 0; day < noOfDays && index < missedExercises.size(); day++) {
            List<Exercise> dayExercises = new ArrayList<>();
            float used = 0;
    
            while (index < missedExercises.size()) {
                Exercise ex = missedExercises.get(index);
    
                float needed = ex.getDurationMinutes + (dayExercises.isEmpty() ? 0 : breakTime);
    
                if (used + needed > minutesPerDay && !dayExercises.isEmpty()) {
                    break;
                }
    
                dayExercises.add(ex);
                used += needed;
                index++;
            }
    
            rescheduled.add(new Workout(startWorkoutId + day, healthGoal, dayExercises));
        }
    
        return rescheduled;
    }
}

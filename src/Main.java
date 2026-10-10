import goals.*;
import exercises.*;
import assigners.*;
import workouts.*;
import tracker.*;
import analytics.*;
import scheduler.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Main demonstration class for the Workout Management & Scheduler System.
 * Demonstrates all modules implemented from the Class Diagram:
 * - Health Goals Validation
 * - Exercise Assignment (Regular & Additional)
 * - Workout Sessions & Duration Calculation
 * - Exercise Scheduling & Missed Exercise Rescheduling
 * - Workout History Tracking
 * - Performance Analysis & Calorie Tracking
 * - Simplified Generic CalorieCalculator with Lambda Expression
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("      WORKOUT MANAGEMENT SYSTEM - FULL MODULE DEMONSTRATION      ");
        System.out.println("==================================================================\n");

        // 1. Health Goals Validation
        System.out.println(">>> 1. HEALTH GOALS & REALISTIC GOAL VALIDATION <<<\n");

        HealthGoal weightLossGoal = new WeightLossGoal("Weight Loss Goal", 60, 70.0f, 80.0f, 1.0f);
        weightLossGoal.displayGoal();
        System.out.println("Is Goal Realistic? " + weightLossGoal.isRealisticGoal());

        System.out.println();
        HealthGoal weightGainGoal = new WeightGainGoal("Muscle & Weight Gain Goal", 90, 75.0f, 68.0f, 0.5f);
        weightGainGoal.displayGoal();
        System.out.println("Is Goal Realistic? " + weightGainGoal.isRealisticGoal());

        System.out.println("\n------------------------------------------------------------------\n");

        // 2. Exercise Assignment
        System.out.println(">>> 2. EXERCISE ASSIGNMENT (REGULAR & ADDITIONAL) <<<\n");

        ExerciseAssigner regularAssigner = new RegularExerciseAssigner();
        regularAssigner.setExercisesAssigned(false, weightLossGoal);
        List<Exercise> assignedExercises = regularAssigner.getExerciseAssigned();

        System.out.println("=== Assigned Regular Weight Loss Exercises (" + assignedExercises.size() + " total) ===");
        for (Exercise ex : assignedExercises) {
            ex.displayExercise();
            System.out.println();
        }

        ExerciseAssigner additionalAssigner = new AdditionalExerciseAssigner();
        additionalAssigner.setExercisesAssigned(false, weightLossGoal);
        List<Exercise> extraExercises = additionalAssigner.getExerciseAssigned();

        System.out.println("=== Assigned Additional Exercises (" + extraExercises.size() + " total) ===");
        for (Exercise ex : extraExercises) {
            ex.displayExercise();
            System.out.println();
        }

        System.out.println("------------------------------------------------------------------\n");

        // 3. Exercise Scheduler & Daily Plan Generation
        System.out.println(">>> 3. EXERCISE SCHEDULER & FULL WORKOUT SCHEDULE <<<\n");

        ExerciseScheduler scheduler = new ExerciseScheduler(assignedExercises, 60, 5);
        int perDayCount = scheduler.computeNoOFExercisesperday();
        System.out.println("Computed Exercises Per Day Quota: " + perDayCount);

        System.out.println("\n--- Full Day-by-Day Exercise Schedule ---");
        int dayNum = 1;
        while (scheduler.hasMoreExercises()) {
            System.out.println("\n[Day " + dayNum + " Schedule]:");
            scheduler.displayScheduledExercises();
            dayNum++;
        }

        scheduler.resetScheduleIndex();

        List<Workout> scheduledWorkouts = scheduler.getWorkoutSchedule(weightLossGoal);
        System.out.println("\nGenerated " + scheduledWorkouts.size() + " Workout Session Objects:");
        for (Workout w : scheduledWorkouts) {
            System.out.println("Workout Day #" + w.getWorkoutId() + " (Total Duration: " + w.getWorkoutMinutes() + " mins):");
            for (Exercise ex : w.getExercises()) {
                System.out.println("   - " + ex.getName() + " [" + ex.getDurationMinutes() + " mins]");
            }
        }

        System.out.println("\n------------------------------------------------------------------\n");

        // 4. Workout Session Management & Completion Marking
        System.out.println(">>> 4. WORKOUT SESSION MANAGEMENT <<<\n");

        Workout currentSession = new Workout(101, weightLossGoal, assignedExercises);
        System.out.println("Workout Session ID: " + currentSession.getWorkoutId());
        System.out.println("Calculated Total Workout Duration: " + currentSession.getWorkoutMinutes() + " mins");

        if (!assignedExercises.isEmpty()) {
            int exIdToComplete = assignedExercises.get(0).getExerciseId();
            System.out.println("\nMarking Exercise ID " + exIdToComplete + " as completed...");
            currentSession.markCompletedExercise(exIdToComplete);
        }

        System.out.println("\nUpdated Exercise Completion Statuses:");
        for (Exercise ex : currentSession.getExercises()) {
            System.out.println("- " + ex.getName() + ": " + (ex.isComplete() ? "[COMPLETED]" : "[PENDING]"));
        }

        System.out.println("\n------------------------------------------------------------------\n");

        // 5. Missed Exercise Rescheduling
        System.out.println(">>> 5. MISSED EXERCISE RESCHEDULER <<<\n");

        MissedExerciseRescheduler rescheduler = new MissedExerciseRescheduler();
        System.out.println("Rescheduling missed exercises over 3 days (1.5 hours/day limit):");
        rescheduler.rescheduleMissedExercises(assignedExercises, 3, 1.5f);

        System.out.println("\n------------------------------------------------------------------\n");

        // 6. Workout History Tracking
        System.out.println(">>> 6. WORKOUT HISTORY LOGGING & REPORTING <<<\n");

        WorkoutHistory history = new WorkoutHistory();

        List<Exercise> s1Completed = new ArrayList<>();
        List<Exercise> s1Missed = new ArrayList<>();
        if (assignedExercises.size() > 0) s1Completed.add(assignedExercises.get(0));
        if (assignedExercises.size() > 1) s1Missed.add(assignedExercises.get(1));
        history.addWorkoutRecord(s1Completed, s1Missed);

        List<Exercise> s2Completed = new ArrayList<>(assignedExercises);
        List<Exercise> s2Missed = new ArrayList<>();
        history.addWorkoutRecord(s2Completed, s2Missed);

        System.out.println(history.generateHistoryReport(1001));

        System.out.println("------------------------------------------------------------------\n");

        // 7. Performance Analysis, Calorie Tracking & Generic CalorieCalculator
        System.out.println(">>> 7. WORKOUT PERFORMANCE ANALYZER & GENERIC CALORIE CALCULATOR <<<\n");

        WorkoutPerformanceAnalyzer analyzer = new WorkoutPerformanceAnalyzer();

        float completionRate = analyzer.getCompletionRate(history);
        System.out.println("Overall Completion Rate: " + String.format("%.2f", completionRate) + "%");
        System.out.println("Completion Rate Analysis: " + analyzer.analyzeCompletionRate(completionRate));

        int currentStreak = analyzer.getCurrentStreak(history);
        System.out.println("\nCurrent Active Streak: " + currentStreak + " session(s)");
        System.out.println("Streak Feedback: " + analyzer.analyzeCurrentStreak(currentStreak));

        System.out.println("\nBest Performing Session: " + analyzer.getBestPerformingDay(history));

        // Testing Generic CalorieCalculator with Lambda Expression
        System.out.println("\n=== Testing Generic CalorieCalculator with Single Lambda Expression ===");
        CalorieCalculator<Exercise> calorieCalc = new CalorieCalculator<>();

        // Exercise Kind 1: WeightLossExercise
        WeightLossExercise lossEx = new WeightLossExercise(201, "HIIT Cardio", 35, 4, 20, "HIIT", 8.0f);
        float lossCals = calorieCalc.calculateCalories(lossEx);
        System.out.println("1. WeightLossExercise Calculated Calories: " + lossCals + " kcal");

        // Exercise Kind 2: WeightGainExercise
        WeightGainExercise gainEx = new WeightGainExercise(202, "Bench Press", 45, 5, 10, 80.0f, "Barbell", 7.5f);
        float gainCals = calorieCalc.calculateCalories(gainEx);
        System.out.println("2. WeightGainExercise Calculated Calories: " + gainCals + " kcal");

        // Calorie Tracker
        CalorieTracker tracker = new CalorieTracker(2000.0f, 1600.0f);
        tracker.logCalorieBurn(lossCals);
        tracker.logCalorieBurn(gainCals);
        System.out.println("\nTotal Calorie Burn Logged: " + tracker.getTotalCaloriesBurned() + " kcal");
        System.out.println("Calorie Progress Trend   : " + analyzer.getProgressTrend(tracker));

        System.out.println("\n==================================================================");
        System.out.println("                   DEMONSTRATION COMPLETE                         ");
        System.out.println("==================================================================");
    }
}

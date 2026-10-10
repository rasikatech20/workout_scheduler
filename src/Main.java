import goals.*;
import exercises.*;
import assigners.*;
import workouts.*;
import tracker.*;
import analytics.*;
import scheduler.*;
import models.*;
import controller.*;
import exceptions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Main demonstration class for the Workout Management & Scheduler System.
 * Demonstrates system operations with robust try-catch exception handling.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("            WORKOUT MANAGEMENT & SCHEDULER SYSTEM                 ");
        System.out.println("==================================================================\n");

        Controller controller = new Controller();

        // ------------------------------------------------------------------
        // 1. HEALTH GOAL & USER PROFILE SETUP WITH VALIDATION
        // ------------------------------------------------------------------
        System.out.println(">>> 1. USER REGISTRATION & HEALTH GOAL SETUP <<<\n");

        HealthGoal weightLossGoal = null;
        try {
            weightLossGoal = new WeightLossGoal("Weight Loss Goal", 60, 70.0f, 80.0f, 1.0f);
            weightLossGoal.displayGoal();
            System.out.println("Is Goal Realistic? " + weightLossGoal.isRealisticGoal());
        } catch (Exception e) {
            System.err.println("Error initializing health goal: " + e.getMessage());
        }

        System.out.println();

        // Demonstration of user registration error handling (Invalid input attempt)
        try {
            System.out.println("Validating user registration details for new account...");
            // Attempting to register user with invalid age
            User invalidUser = new User("John Doe", -5, 75.0f, 175.0f, weightLossGoal, 4, 1.5f);
        } catch (InvalidUserDataException e) {
            System.out.println("Registration Validation Notice: " + e.getMessage());
        } catch (InvalidGoalException e) {
            System.out.println("Goal Validation Notice: " + e.getMessage());
        }

        System.out.println();

        // Registering a valid user profile
        User mainUser = null;
        try {
            System.out.println("Registering user profile...");
            mainUser = new User("Sai Saranya", 22, 65.0f, 168.0f, false, weightLossGoal, 5, 1.5f);
            mainUser.displayUserProfile();
        } catch (WorkoutSchedulerException e) {
            System.err.println("User profile registration failed: " + e.getMessage());
            return;
        }

        System.out.println("\n------------------------------------------------------------------\n");

        // ------------------------------------------------------------------
        // 2. EXERCISE ASSIGNMENT & WORKOUT SCHEDULING
        // ------------------------------------------------------------------
        System.out.println(">>> 2. EXERCISE ASSIGNMENT & WORKOUT SCHEDULING <<<\n");

        List<Exercise> assignedExercises = new ArrayList<>();
        try {
            assignedExercises = controller.assignExercises(mainUser);
            System.out.println("Assigned " + assignedExercises.size() + " regular exercises for " + mainUser.getName() + ":");
            for (Exercise ex : assignedExercises) {
                System.out.println("  - " + ex.getName() + " (" + ex.getDurationMinutes() + " mins, " + ex.getSetsCount() + " sets x " + ex.getRepsCount() + " reps)");
            }

            System.out.println("\nGenerating user Workout Plan...");
            WorkoutPlan workoutPlan = controller.createWorkoutPlan(mainUser);
            System.out.println("Successfully generated Workout Plan containing " + workoutPlan.getWorkouts().size() + " workout sessions.");

            ExerciseScheduler scheduler = new ExerciseScheduler(assignedExercises, weightLossGoal.getTargetDays(), mainUser.getDaysPerWeek());
            System.out.println("Daily Exercise Quota: " + scheduler.computeNoOFExercisesperday() + " exercise(s) per day.");

            System.out.println("\n--- Initial Scheduled Workout Days ---");
            List<Workout> scheduledWorkouts = scheduler.getWorkoutSchedule(weightLossGoal);
            for (Workout w : scheduledWorkouts) {
                System.out.println("Session #" + w.getWorkoutId() + " (" + w.getWorkoutMinutes() + " mins total):");
                for (Exercise ex : w.getExercises()) {
                    System.out.println("   * " + ex.getName() + " [" + ex.getDurationMinutes() + " mins]");
                }
            }
        } catch (InvalidUserDataException e) {
            System.err.println("Scheduling error: " + e.getMessage());
        }

        System.out.println("\n------------------------------------------------------------------\n");

        // ------------------------------------------------------------------
        // 3. WORKOUT SESSION TRACKING & COMPLETION
        // ------------------------------------------------------------------
        System.out.println(">>> 3. WORKOUT SESSION TRACKING & COMPLETION <<<\n");

        try {
            Workout currentSession = new Workout(101, weightLossGoal, assignedExercises);
            System.out.println("Active Session ID: " + currentSession.getWorkoutId());

            if (!assignedExercises.isEmpty()) {
                int exIdToComplete = assignedExercises.get(0).getExerciseId();
                System.out.println("Logging exercise completion...");
                controller.completeExercise(currentSession, exIdToComplete);
            }

            System.out.println("\nExercise Status Summary:");
            for (Exercise ex : currentSession.getExercises()) {
                System.out.println("  - " + ex.getName() + ": " + (ex.isComplete() ? "[COMPLETED]" : "[PENDING]"));
            }
        } catch (Exception e) {
            System.err.println("Workout tracking error: " + e.getMessage());
        }

        System.out.println("\n------------------------------------------------------------------\n");

        // ------------------------------------------------------------------
        // 4. MISSED EXERCISE RESCHEDULING & HISTORY
        // ------------------------------------------------------------------
        System.out.println(">>> 4. MISSED EXERCISE RESCHEDULING & HISTORY LOGGING <<<\n");

        try {
            WorkoutHistory history = mainUser.getWorkoutHistory();

            List<Exercise> completed = new ArrayList<>();
            List<Exercise> missed = new ArrayList<>();
            if (assignedExercises.size() > 0) completed.add(assignedExercises.get(0));
            if (assignedExercises.size() > 1) missed.add(assignedExercises.get(1));
            history.addWorkoutRecord(completed, missed);

            controller.rescheduleMissedExercises(mainUser, 3, 1.5f);
        } catch (InvalidUserDataException e) {
            System.err.println("Rescheduling error: " + e.getMessage());
        }

        System.out.println("\n------------------------------------------------------------------\n");

        // ------------------------------------------------------------------
        // 5. SYSTEM RECOMMENDATION REPORT & CALORIE ANALYSIS
        // ------------------------------------------------------------------
        System.out.println(">>> 5. SYSTEM RECOMMENDATION REPORT & CALORIE ANALYSIS <<<\n");

        try {
            String report = controller.getRecommendation(mainUser);
            System.out.println(report);

            System.out.println("=== Calorie Calculator Evaluation ===");
            CalorieCalculator<Exercise> calorieCalc = new CalorieCalculator<>();

            WeightLossExercise lossEx = new WeightLossExercise(201, "HIIT Cardio", 35, 4, 20, "HIIT", 8.0f);
            float lossCals = calorieCalc.calculateCalories(lossEx);
            System.out.println("HIIT Cardio Burned Calories: " + lossCals + " kcal");

            WeightGainExercise gainEx = new WeightGainExercise(202, "Bench Press", 45, 5, 10, 80.0f, "Barbell", 7.5f);
            float gainCals = calorieCalc.calculateCalories(gainEx);
            System.out.println("Bench Press Burned Calories: " + gainCals + " kcal");

        } catch (InvalidUserDataException e) {
            System.err.println("Recommendation generation error: " + e.getMessage());
        }

        System.out.println("\n==================================================================");
        System.out.println("                   SYSTEM EXECUTION COMPLETE                      ");
        System.out.println("==================================================================");
    }
}

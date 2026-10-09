import goals.*;
import exercises.*;
import assigners.*;
import workouts.*;
import tracker.*;
import analytics.*;
import models.*;
import controller.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Integrated Demonstration Class for Workout Scheduler, Calorie Tracker, and Recommendation System.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("   WORKOUT SCHEDULER, CALORIE TRACKER & RECOMMENDATION SYSTEM     ");
        System.out.println("==================================================================\n");

        // 1. User Creation & BMR / Calorie Target Setup
        System.out.println(">>> 1. INITIALIZING USER PROFILE & CALORIE TRACKER <<<\n");

        HealthGoal weightLossGoal = new WeightLossGoal("Weight Loss Goal", 30, 70.0f, 80.0f, 1.0f);
        User user = new User("Alex", 25, 80.0f, 175.0f, false, weightLossGoal, 5, 1.5f);
        
        user.displayUserProfile();
        System.out.println();
        user.getCalorieTracker().displayCalorieStatus();

        System.out.println("\n------------------------------------------------------------------\n");

        // 2. Testing Calorie Calculator & Exercise Calories
        System.out.println(">>> 2. TESTING CALORIE CALCULATOR & EXERCISES <<<\n");

        CalorieCalculator calorieCalc = new CalorieCalculator();
        Exercise running = new WeightLossExercise(201, "Running / Jogging", 30, 1, 1, "Cardio", 7.5f);
        Exercise cycling = new WeightLossExercise(202, "Cycling", 45, 1, 1, "Cardio", 6.0f);

        float runCalories = calorieCalc.calculateCalories(running);
        float cycleCalories = calorieCalc.calculateCalories(cycling);

        System.out.println("Calculated Calories for '" + running.getName() + "': " + runCalories + " kcal");
        System.out.println("Calculated Calories for '" + cycling.getName() + "': " + cycleCalories + " kcal");

        // Log burn into User's CalorieTracker
        user.getCalorieTracker().logCalorieBurn(runCalories);
        user.getCalorieTracker().logCalorieBurn(cycleCalories);

        System.out.println("\nUpdated Calorie Tracker State:");
        user.getCalorieTracker().displayCalorieStatus();

        System.out.println("\n------------------------------------------------------------------\n");

        // 3. Controller & Workout Plan Creation
        System.out.println(">>> 3. CONTROLLER & WORKOUT PLAN GENERATION <<<\n");

        Controller controller = new Controller();
        List<Exercise> assignedExercises = controller.assignExercises(user);
        System.out.println("Assigned " + assignedExercises.size() + " exercises for user " + user.getName() + ":");
        for (Exercise ex : assignedExercises) {
            ex.displayExercise();
            System.out.println();
        }

        WorkoutPlan plan = controller.createWorkoutPlan(user);
        System.out.println("Created Workout Plan with " + plan.getWorkouts().size() + " scheduled workout session(s).");

        System.out.println("\n------------------------------------------------------------------\n");

        // 4. Simulating Workout Completion & History Tracking
        System.out.println(">>> 4. SIMULATING WORKOUT COMPLETION & HISTORY TRACKING <<<\n");

        WorkoutHistory history = user.getWorkoutHistory();

        // Session 1: Both assigned exercises completed
        List<Exercise> session1Completed = new ArrayList<>(assignedExercises);
        List<Exercise> session1Missed = new ArrayList<>();
        history.addWorkoutRecord(session1Completed, session1Missed);

        // Session 2: 1 Completed, 1 Missed
        List<Exercise> session2Completed = new ArrayList<>();
        List<Exercise> session2Missed = new ArrayList<>();
        if (!assignedExercises.isEmpty()) {
            session2Completed.add(assignedExercises.get(0));
        }
        if (assignedExercises.size() > 1) {
            session2Missed.add(assignedExercises.get(1));
        }
        history.addWorkoutRecord(session2Completed, session2Missed);

        System.out.println(history.generateHistoryReport(user.getUserId()));

        System.out.println("------------------------------------------------------------------\n");

        // 5. Testing Recommendation Services & Performance Analyzer
        System.out.println(">>> 5. TESTING RECOMMENDATION SERVICES & ANALYZER <<<\n");

        RecommendationServices recService = new RecommendationServices();

        System.out.println(recService.getCalorieBasedRecommendation(user.getCalorieTracker()));
        System.out.println();
        System.out.println(recService.getStreakBasedRecommendation(history));
        System.out.println();
        System.out.println(recService.getHistoryBasedRecommendation(history));
        System.out.println();
        System.out.println(recService.getCardiacSafeRecommendation(user.isHasCardiacProblems()));
        System.out.println();
        System.out.println(recService.getMissedWorkoutRecoveryRecommendation(history));

        System.out.println("\n------------------------------------------------------------------\n");

        // 6. Controller Full Recommendation Report Integration
        System.out.println(">>> 6. GENERATING FULL CONTROLLER RECOMMENDATION REPORT <<<\n");

        String fullReport = controller.getRecommendation(user);
        System.out.println(fullReport);

        System.out.println("\n==================================================================");
        System.out.println("             SYSTEM VERIFICATION COMPLETED SUCCESSFULLY            ");
        System.out.println("==================================================================");
    }
}

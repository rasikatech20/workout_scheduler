import analytics.WorkoutPerformanceAnalyzer;
import controller.Controller;
import exceptions.InvalidGoalException;
import exceptions.InvalidUserDataException;
import exceptions.LoginFailedException;
import exercises.Exercise;
import goals.HealthGoal;
import goals.WeightGainGoal;
import goals.WeightLossGoal;
import java.util.List;
import java.util.Scanner;
import login.Login;
import models.User;
import workouts.Workout;
import workouts.WorkoutHistory;
import workouts.WorkoutPlan;

/**
 * Clean UI / Presentation entry point for the Workout Management & Scheduler System.
 * Main only handles user prompts and coordinates method calls to Controller and Login.
 * All domain logic resides in Controller, ExerciseScheduler, CalorieTracker, and RecommendationServices.
 */
public class Main {
    private static Scanner scanner = new Scanner(System.in);

    private static String promptString(String prompt, String defaultValue) {
        System.out.print(prompt);
        try {
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                return line.isEmpty() ? defaultValue : line;
            }
        } catch (Exception ignored) {}
        return defaultValue;
    }

    private static int promptInt(String prompt, int defaultValue) {
        System.out.print(prompt);
        try {
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) {
                    return Integer.parseInt(line);
                }
            }
        } catch (Exception ignored) {}
        return defaultValue;
    }

    private static float promptFloat(String prompt, float defaultValue) {
        System.out.print(prompt);
        try {
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) {
                    return Float.parseFloat(line);
                }
            }
        } catch (Exception ignored) {}
        return defaultValue;
    }

    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("            WORKOUT MANAGEMENT & SCHEDULER SYSTEM                 ");
        System.out.println("==================================================================\n");

        Controller controller = new Controller();
        Login authSystem = new Login();

        // ------------------------------------------------------------------
        // 1. AUTHENTICATION (LOGIN / REGISTER VIA LOGIN)
        // ------------------------------------------------------------------
        System.out.println(">>> 1. USER AUTHENTICATION & PROFILE SETUP <<<\n");
        System.out.println("Choose authentication option:");
        System.out.println("  [1] Login with Existing Account");
        System.out.println("  [2] Register New User Profile");
        
        int authChoice = promptInt("Enter choice (1/2, default 1): ", 1);
        User currentUser = null;

        if (authChoice == 1) {
            String loginName = promptString("Enter username to login (default 'Sai Saranya'): ", "Sai Saranya");
            try {
                System.out.println("Authenticating '" + loginName + "' using Login service...");
                currentUser = authSystem.authenticate(loginName);
            } catch (LoginFailedException e) {
                System.out.println("Login Notice: " + e.getMessage());
                System.out.println("Switching to registration mode for new profile creation...\n");
                authChoice = 2;
            }
        }

        if (authChoice == 2 || currentUser == null) {
            System.out.println("\n--- New User Registration ---");
            String regName = promptString("Enter Full Name (default 'Rasika'): ", "Rasika");
            int age = promptInt("Enter Age (default 22): ", 22);
            float weight = promptFloat("Enter Current Weight in kg (default 62.0): ", 62.0f);
            float height = promptFloat("Enter Height in cm (default 168.0): ", 168.0f);
            
            String gender = promptString("Enter Gender (male/female, default 'female'): ", "female");
            float bp = promptFloat("Enter Blood Pressure (systolic mmHg, default 120.0): ", 120.0f);
            
            String cardiacInput = promptString("Do you have any cardiac / heart conditions? (y/n, default n): ", "n");
            boolean hasCardiac = cardiacInput.equalsIgnoreCase("y") || cardiacInput.equalsIgnoreCase("yes");

            System.out.println("\nSelect Health Goal:");
            System.out.println("  [1] Weight Loss Goal");
            System.out.println("  [2] Weight Gain Goal");
            int goalChoice = promptInt("Enter goal choice (1/2, default 1): ", 1);

            float targetWeight = (goalChoice == 2)
                ? promptFloat("Enter Target Weight in kg (default " + (weight + 5.0f) + "): ", weight + 5.0f)
                : promptFloat("Enter Target Weight in kg (default " + (weight - 5.0f) + "): ", weight - 5.0f);

            int targetDays = promptInt("Enter Target Duration in days (e.g. 30, 45, 60, default 45): ", 45);

            HealthGoal userGoal = (goalChoice == 2)
                ? new WeightGainGoal("Weight Gain Goal", targetDays, targetWeight, weight)
                : new WeightLossGoal("Weight Loss Goal", targetDays, targetWeight, weight);

            userGoal.displayGoal();
            if (!userGoal.isRealisticGoal()) {
                System.out.println("\n>>> WARNING: Aggressive or Unrealistic Goal Detected! <<<");
                float diff = Math.abs(weight - targetWeight);
                float maxSafeRate = (goalChoice == 2) ? 1.0f : 1.5f;
                int recommendedDays = (int) Math.ceil((diff / maxSafeRate) * 7.0f);
                System.out.println("Recommended target duration for " + diff + " kg change: At least " + recommendedDays + " days.");

                String adjust = promptString("Would you like to adjust target duration to a realistic " + recommendedDays + " days? (y/n, default y): ", "y");
                if (adjust.equalsIgnoreCase("y") || adjust.equalsIgnoreCase("yes")) {
                    targetDays = recommendedDays;
                    userGoal = (goalChoice == 2)
                        ? new WeightGainGoal("Weight Gain Goal", targetDays, targetWeight, weight)
                        : new WeightLossGoal("Weight Loss Goal", targetDays, targetWeight, weight);
                    System.out.println("Goal updated successfully to a realistic timeline!");
                    userGoal.displayGoal();
                }
            }

            int daysPerWeek = promptInt("\nEnter Workout Days available per week (1-7, default 4): ", 4);
            float hoursPerDay = promptFloat("Enter Workout Hours available per day (0.5 - 3.0, default 1.5): ", 1.5f);

            try {
                System.out.println("\nRegistering user profile with database via Login...");
                currentUser = authSystem.register(regName, age, weight, height, bp, gender, hasCardiac, userGoal, daysPerWeek, hoursPerDay);
            } catch (InvalidUserDataException | InvalidGoalException e) {
                System.err.println("Registration failed: " + e.getMessage());
                return;
            }
        }

        System.out.println("\nTotal Successful Logins Recorded: " + authSystem.noOfUserLogins());
        System.out.println("\n================ ACTIVE USER PROFILE ================");
        currentUser.displayUserProfile();
        if (currentUser.getHealthGoal() != null) {
            currentUser.getHealthGoal().displayGoal();
        }
        System.out.println("=====================================================\n");

        // ------------------------------------------------------------------
        // 2. WORKOUT PLAN GENERATION VIA CONTROLLER
        // ------------------------------------------------------------------
        System.out.println("------------------------------------------------------------------");
        System.out.println("            GENERATING WORKOUT PLAN VIA CONTROLLER                ");
        System.out.println("------------------------------------------------------------------\n");

        WorkoutPlan workoutPlan = null;
        try {
            List<Exercise> assigned = controller.assignExercises(currentUser);
            System.out.println("Assigned " + assigned.size() + " personalized exercises for " + currentUser.getName() + ":");
            for (Exercise ex : assigned) {
                System.out.println("  - " + ex.getName() + " (" + ex.getDurationMinutes() + " mins, " 
                                   + ex.getSetsCount() + " sets x " + ex.getRepsCount() + " reps)");
            }

            workoutPlan = controller.createWorkoutPlan(currentUser);
        } catch (InvalidUserDataException e) {
            System.err.println("Error generating plan: " + e.getMessage());
            return;
        }

        List<Workout> scheduledWorkouts = workoutPlan.getWorkouts();
        int totalSessions = scheduledWorkouts.size();
        int daysPerWeek = currentUser.getDaysPerWeek();
        int totalWeeks = (int) Math.ceil((double) totalSessions / daysPerWeek);

        System.out.println("\n>>> WORKOUT SCHEDULE CALENDAR & INSTRUCTIONS <<<");
        System.out.println("This comprehensive workout plan spans " + totalSessions + " scheduled workout sessions (~" 
                           + totalWeeks + " weeks, " + daysPerWeek + " days/week).\n");
        System.out.println("Guidelines for the User:");
        System.out.println("  - Complete each session on its designated workout day.");
        System.out.println("  - Rest on non-workout days to allow muscles to recover.");
        System.out.println("  - Stay hydrated and follow the built-in 5-minute break intervals.\n");

        System.out.println("--- Full Schedule Breakdown ---");
        for (int i = 0; i < totalSessions; i++) {
            Workout w = scheduledWorkouts.get(i);
            int weekNum = (i / daysPerWeek) + 1;
            int dayInWeek = (i % daysPerWeek) + 1;
            System.out.println("Week " + weekNum + " - Day " + dayInWeek + " (Session #" + w.getWorkoutId() 
                               + ") [Total Duration: " + w.getWorkoutMinutes() + " mins]:");
            for (Exercise ex : w.getExercises()) {
                System.out.println("   * " + ex.getName() + " [" + ex.getDurationMinutes() + " mins, "
                                   + ex.getSetsCount() + " sets x " + ex.getRepsCount() + " reps]");
            }
        }

        System.out.println("\n------------------------------------------------------------------");
        System.out.println("       REAL-LIFE WORKOUT SIMULATION & PROGRESS TRACKING           ");
        System.out.println("------------------------------------------------------------------\n");

        String simChoice = promptString("Would you like to interactively track completion for each exercise? (y/n, default y): ", "y");
        boolean interactiveSimulation = simChoice.equalsIgnoreCase("y") || simChoice.equalsIgnoreCase("yes");

        // ------------------------------------------------------------------
        // 3. DAY-BY-DAY LIVE WORKOUT SIMULATION
        // ------------------------------------------------------------------
        WorkoutPerformanceAnalyzer performanceAnalyzer = new WorkoutPerformanceAnalyzer();

        for (int i = 0; i < totalSessions; i++) {
            Workout currentSession = scheduledWorkouts.get(i);
            int weekNum = (i / daysPerWeek) + 1;
            int dayInWeek = (i % daysPerWeek) + 1;

            System.out.println("\n==================================================================");
            System.out.println(">>> LIVE WORKOUT SIMULATION: WEEK " + weekNum + " - DAY " + dayInWeek 
                               + " (Session #" + currentSession.getWorkoutId() + ") <<<");
            System.out.println("Target Duration: " + currentSession.getWorkoutMinutes() + " mins | Exercises: " 
                               + currentSession.getExercises().size());
            System.out.println("==================================================================");
            System.out.println("Please mark whether you completed each exercise:");

            float sessionBurnedCalories = 0.0f;
            int sessionCompletedCount = 0;

            for (Exercise ex : currentSession.getExercises()) {
                boolean completed = true;
                if (interactiveSimulation) {
                    String answer = promptString("Did you complete '" + ex.getName() + "' (" 
                                                 + ex.getDurationMinutes() + " mins)? (y/n, default y): ", "y");
                    completed = answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes");
                }

                if (completed) {
                    controller.completeExercise(currentSession, ex.getExerciseId());
                    sessionBurnedCalories += ex.getCalculatedCalories();
                    sessionCompletedCount++;
                } else {
                    System.out.println("  -> Marked '" + ex.getName() + "' as MISSED.");
                }
            }

            // Record session completion and calories via Controller
            controller.recordWorkoutSession(currentUser, currentSession, sessionBurnedCalories);

            System.out.println("\n--- Session #" + currentSession.getWorkoutId() + " (Day " + dayInWeek + ") Summary ---");
            System.out.println("Completed Exercises: " + sessionCompletedCount + " / " + currentSession.getExercises().size());
            System.out.println("Calories Burned Today: " + String.format("%.1f", sessionBurnedCalories) + " kcal");
            System.out.println("Cumulative Burn to Date: " + String.format("%.1f", currentUser.getCalorieTracker().getTotalCaloriesBurned()) + " kcal");

            boolean isEndOfWeek = (dayInWeek == daysPerWeek) || (i == totalSessions - 1);
            if (isEndOfWeek) {
                System.out.println("\n******************************************************************");
                System.out.println("               WEEK " + weekNum + " PROGRESS REPORT                 ");
                System.out.println("******************************************************************");
                System.out.println("Sessions Completed in Plan  : " + (i + 1) + " / " + totalSessions);
                System.out.println("Cumulative Calories Burned  : " + String.format("%.1f", currentUser.getCalorieTracker().getTotalCaloriesBurned()) + " kcal");
                System.out.println("Cumulative Missed Calories  : " + String.format("%.1f", currentUser.getCalorieTracker().getTotalMissedCalories()) + " kcal");
                System.out.println("Active Workout Streak       : " + performanceAnalyzer.getCurrentStreak(currentUser.getWorkoutHistory()) + " session(s)");
                System.out.println("Overall Completion Rate     : " + String.format("%.1f", performanceAnalyzer.getCompletionRate(currentUser.getWorkoutHistory())) + "%");
                System.out.println("Progress Trend              : " + performanceAnalyzer.getProgressTrend(currentUser.getCalorieTracker()));
                System.out.println("******************************************************************");
            }
        }

        // ------------------------------------------------------------------
        // 4. POST-ROUTINE RESCHEDULING & SIMULATION VIA CONTROLLER
        // ------------------------------------------------------------------
        System.out.println("\n==================================================================");
        System.out.println("   POST-ROUTINE RESCHEDULING & MAKE-UP WORKOUT SIMULATION         ");
        System.out.println("==================================================================");

        try {
            List<Workout> makeupSessions = controller.rescheduleMissedExercises(
                currentUser, currentUser.getDaysPerWeek(), currentUser.getHoursPerDay()
            );

            if (makeupSessions != null && !makeupSessions.isEmpty()) {
                System.out.println("\nGenerated " + makeupSessions.size() + " Make-Up Workout Session(s).");
                String doMakeup = promptString("Would you like to simulate your make-up sessions now? (y/n, default y): ", "y");

                if (doMakeup.equalsIgnoreCase("y") || doMakeup.equalsIgnoreCase("yes")) {
                    for (Workout makeupWorkout : makeupSessions) {
                        System.out.println("\n>>> MAKE-UP WORKOUT SIMULATION (Session #" + makeupWorkout.getWorkoutId() + ") <<<");
                        System.out.println("Duration: " + makeupWorkout.getWorkoutMinutes() + " mins | Exercises: " + makeupWorkout.getExercises().size());

                        float makeupBurn = 0.0f;
                        for (Exercise ex : makeupWorkout.getExercises()) {
                            String ans = promptString("Did you complete make-up exercise '" + ex.getName() 
                                                      + "' (" + ex.getDurationMinutes() + " mins)? (y/n, default y): ", "y");
                            if (ans.equalsIgnoreCase("y") || ans.equalsIgnoreCase("yes")) {
                                controller.completeExercise(makeupWorkout, ex.getExerciseId());
                                makeupBurn += ex.getCalculatedCalories();
                            } else {
                                System.out.println("  -> Marked '" + ex.getName() + "' as MISSED.");
                            }
                        }

                        controller.recordWorkoutSession(currentUser, makeupWorkout, makeupBurn);
                        System.out.println("Make-Up Session #" + makeupWorkout.getWorkoutId() + " Finished! Calories Burned: " 
                                           + String.format("%.1f", makeupBurn) + " kcal");
                    }
                }
            } else {
                System.out.println("Congratulations! All assigned exercises were completed successfully.");
                System.out.println("No make-up sessions required!");
            }
        } catch (InvalidUserDataException e) {
            System.err.println("Rescheduling error: " + e.getMessage());
        }

        // ------------------------------------------------------------------
        // 5. PERFORMANCE ANALYZER METRICS
        // ------------------------------------------------------------------
        System.out.println("\n==================================================================");
        System.out.println("             WORKOUT PERFORMANCE ANALYZER REPORT                  ");
        System.out.println("==================================================================");

        WorkoutHistory history = currentUser.getWorkoutHistory();
        float completionRate = performanceAnalyzer.getCompletionRate(history);
        int currentStreak = performanceAnalyzer.getCurrentStreak(history);

        System.out.println("Overall Completion Rate : " + String.format("%.1f", completionRate) + "%");
        System.out.println("Rate Evaluation         : " + performanceAnalyzer.analyzeCompletionRate(completionRate));
        System.out.println("Current Workout Streak  : " + currentStreak + " session(s)");
        System.out.println("Streak Evaluation       : " + performanceAnalyzer.analyzeCurrentStreak(currentStreak));
        System.out.println("Calorie Progress Trend  : " + performanceAnalyzer.getProgressTrend(currentUser.getCalorieTracker()));
        System.out.println("Best Performing Session : " + performanceAnalyzer.getBestPerformingDay(history));

        System.out.println();
        currentUser.getCalorieTracker().displayCalorieStatus();

        System.out.println();
        System.out.println(history.generateHistoryReport(currentUser.getUserId()));

        // ------------------------------------------------------------------
        // 6. SYSTEM RECOMMENDATIONS VIA CONTROLLER
        // ------------------------------------------------------------------
        System.out.println("==================================================================");
        System.out.println("           FINAL RECOMMENDATIONS VIA CONTROLLER                   ");
        System.out.println("==================================================================");

        try {
            String recommendations = controller.getRecommendation(currentUser);
            System.out.println(recommendations);
        } catch (InvalidUserDataException e) {
            System.err.println("Recommendation generation error: " + e.getMessage());
        }

        System.out.println("\n==================================================================");
        System.out.println("               SYSTEM SIMULATION COMPLETED SUCCESSFULLY           ");
        System.out.println("==================================================================");
    }
}

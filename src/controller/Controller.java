package controller;

import analytics.RecommendationServices;
import assigners.ExerciseAssigner;
import assigners.RegularExerciseAssigner;
import exceptions.CardiacRiskException;
import exceptions.InvalidUserDataException;
import exercises.Exercise;
import java.util.ArrayList;
import java.util.List;
import models.User;
import scheduler.ExerciseScheduler;
import scheduler.MissedExerciseRescheduler;
import tracker.CalorieCalculator;
import tracker.CalorieTracker;
import workouts.Workout;
import workouts.WorkoutHistory;
import workouts.WorkoutPlan;

public class Controller {
    private RecommendationServices recommendationServices;
    private ExerciseScheduler exerciseScheduler;
    private ExerciseAssigner exerciseAssigner;
    private MissedExerciseRescheduler missedExerciseRescheduler;
    private WorkoutHistory workoutHistory;
    private CalorieTracker calorieTracker;
    private CalorieCalculator<Exercise> calorieCalculator;

    public Controller() {
        this.recommendationServices = new RecommendationServices();
        this.exerciseAssigner = new RegularExerciseAssigner();
        this.missedExerciseRescheduler = new MissedExerciseRescheduler();
        this.workoutHistory = new WorkoutHistory();
        this.calorieTracker = new CalorieTracker();
        this.calorieCalculator = new CalorieCalculator<>();
    }

    public RecommendationServices getRecommendationServices() {
        return recommendationServices;
    }

    public CalorieTracker getCalorieTracker() {
        return calorieTracker;
    }

    public WorkoutHistory getWorkoutHistory() {
        return workoutHistory;
    }

    /**
     * Clears the workout history logs for the given user.
     */
    public void clearWorkoutHistory(User user) {
        if (user != null && user.getWorkoutHistory() != null) {
            user.getWorkoutHistory().clearHistory(user.getUserId());
            System.out.println("Workout history successfully reset for User ID " + user.getUserId() + " (" + user.getName() + ").");
        }
    }

    /**
     * Assigns exercises to user based on user's cardiac status and health goal.
     * Throws CardiacRiskException if high-intensity exercise reaches a cardiac user.
     */
    public List<Exercise> assignExercises(User user) throws InvalidUserDataException, CardiacRiskException {
        if (user == null) {
            throw new InvalidUserDataException("Cannot assign exercises: User instance is null.");
        }
        exerciseAssigner.setExercisesAssigned(user.isHasCardiacProblems(), user.getHealthGoal());
        List<Exercise> assigned = exerciseAssigner.getExerciseAssigned();

        // Recalculate duration & intensity dynamically based on user Blood Pressure
        if (assigned != null && user.getBp() > 120.0f) {
            for (Exercise ex : assigned) {
                ex.recalculateDuration(user.getBp(), ex.getIntensityLevel());
            }
        }

        // Safety verification: Ensure no high-intensity exercise reaches a cardiac user
        if (user.isHasCardiacProblems() && assigned != null) {
            for (Exercise ex : assigned) {
                if (ex.getIntensityLevel() > 5.0f) {
                    throw new CardiacRiskException("'" + ex.getName() + "' is too intense for a user with cardiac conditions.");
                }
            }
        }

        return assigned;
    }

    /**
     * Creates a WorkoutPlan for the user by assigning exercises and scheduling them over user target days.
     */
    public WorkoutPlan createWorkoutPlan(User user) throws InvalidUserDataException, CardiacRiskException {
        if (user == null) {
            throw new InvalidUserDataException("Cannot create workout plan: User instance is null.");
        }
        List<Exercise> assigned = assignExercises(user);
        int targetDays = user.getHealthGoal() != null ? user.getHealthGoal().getTargetDays() : 30;
        this.exerciseScheduler = new ExerciseScheduler(assigned, targetDays, user.getDaysPerWeek(), user.getHoursPerDay());
        List<Workout> workouts = exerciseScheduler.getWorkoutSchedule(user.getHealthGoal());

        // Workout-level safety check when workouts are built
        if (user.isHasCardiacProblems() && workouts != null) {
            for (Workout workout : workouts) {
                for (Exercise ex : workout.getExercises()) {
                    if (ex.getIntensityLevel() > 5.0f) {
                        throw new CardiacRiskException("'" + ex.getName() + "' is too intense for a user with cardiac conditions.");
                    }
                }
            }
        }

        return new WorkoutPlan(workouts);
    }

    /**
     * Generates a comprehensive recommendation report for the user.
     */
    public String getRecommendation(User user) throws InvalidUserDataException {
        if (user == null) {
            throw new InvalidUserDataException("Cannot generate recommendation report: User instance is null.");
        }

        CalorieTracker activeTracker = user.getCalorieTracker() != null ? user.getCalorieTracker() : this.calorieTracker;
        WorkoutHistory activeHistory = user.getWorkoutHistory() != null ? user.getWorkoutHistory() : this.workoutHistory;

        StringBuilder report = new StringBuilder();
        report.append("==================================================================\n");
        report.append("           SYSTEM RECOMMENDATION REPORT FOR ").append(user.getName().toUpperCase()).append("\n");
        report.append("==================================================================\n\n");

        report.append(recommendationServices.getCalorieBasedRecommendation(activeTracker)).append("\n\n");
        report.append(recommendationServices.getStreakBasedRecommendation(activeHistory)).append("\n\n");
        report.append(recommendationServices.getHistoryBasedRecommendation(activeHistory)).append("\n\n");
        report.append(recommendationServices.getCardiacSafeRecommendation(user.isHasCardiacProblems())).append("\n\n");
        report.append(recommendationServices.getMissedWorkoutRecoveryRecommendation(activeHistory)).append("\n\n");
        report.append(recommendationServices.getAdditionalExerciseRecommendation(user));

        return report.toString();
    }

    /**
     * Reschedules missed exercises using MissedExerciseRescheduler and returns make-up workouts.
     */
    public List<Workout> rescheduleMissedExercises(User user, int noOfDays, float hoursPerDay) throws InvalidUserDataException {
        if (user == null) {
            throw new InvalidUserDataException("Cannot reschedule missed exercises: User instance is null.");
        }
        if (noOfDays <= 0 || hoursPerDay <= 0.0f) {
            throw new InvalidUserDataException("Invalid rescheduling target: noOfDays and hoursPerDay must be greater than zero.");
        }
        WorkoutHistory history = user.getWorkoutHistory();
        List<Exercise> allMissed = new ArrayList<>();
        if (history != null) {
            for (List<Exercise> sessionMissed : history.getMissedExercises()) {
                allMissed.addAll(sessionMissed);
            }
        }
        System.out.println(">>> Rescheduling " + allMissed.size() + " missed exercise(s) for user " + user.getName() + "...");
        missedExerciseRescheduler.rescheduleMissedExercises(allMissed, noOfDays, hoursPerDay);
        int startId = (history != null ? history.getCompletedExercises().size() + 1 : 1);
        return missedExerciseRescheduler.getRescheduledWorkouts(allMissed, user.getHealthGoal(), startId);
    }

    /**
     * Records the completed and missed exercises of a workout session into user history
     * and logs the session's burned and missed calories.
     */
    public void recordWorkoutSession(User user, Workout workout, float sessionBurnedCalories) {
        if (user == null || workout == null) return;

        List<Exercise> completed = new ArrayList<>();
        List<Exercise> missed = new ArrayList<>();

        for (Exercise ex : workout.getExercises()) {
            if (ex.isComplete()) {
                completed.add(ex);
            } else {
                missed.add(ex);
                float missedCalories = calorieCalculator.calculateCalories(ex);
                user.getCalorieTracker().logMissedCalories(missedCalories);
            }
        }

        user.getCalorieTracker().logCalorieBurn(sessionBurnedCalories);

        WorkoutHistory history = user.getWorkoutHistory();
        if (history != null) {
            history.addWorkoutRecord(completed, missed);
        }
    }

    /**
     * Marks an exercise as completed in a Workout session and logs calculated calories into CalorieTracker.
     */
    public void completeExercise(Workout workout, int exerciseId) {
        if (workout == null) return;
        Exercise completed = workout.markCompletedExercise(exerciseId);
        if (completed != null) {
            float burned = calorieCalculator.calculateCalories(completed);
            calorieTracker.logCalorieBurn(burned);
            System.out.println("Exercise '" + completed.getName() + "' (ID: " + exerciseId + ") completed! Logged " 
                               + String.format("%.1f", burned) + " kcal into CalorieTracker.");
        }
    }
}
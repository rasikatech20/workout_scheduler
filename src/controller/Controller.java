package controller;

import analytics.RecommendationServices;
import assigners.ExerciseAssigner;
import assigners.RegularExerciseAssigner;
import exercises.Exercise;
import exceptions.InvalidUserDataException;
import models.User;
import scheduler.ExerciseScheduler;
import scheduler.MissedExerciseRescheduler;
import tracker.CalorieCalculator;
import tracker.CalorieTracker;
import workouts.Workout;
import workouts.WorkoutHistory;
import workouts.WorkoutPlan;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller class as defined in the UML Class Diagram.
 * Central manager delegating actions to RecommendationServices, ExerciseScheduler,
 * ExerciseAssigner, MissedExerciseRescheduler, WorkoutHistory, and CalorieTracker.
 */
public class Controller {
    private RecommendationServices recommendationServices;
    private ExerciseScheduler exerciseScheduler;
    private ExerciseAssigner exerciseAssigner;
    private MissedExerciseRescheduler missedExerciseRescheduler;
    private WorkoutHistory workoutHistory;
    private CalorieTracker calorieTracker;
    private CalorieCalculator calorieCalculator;

    public Controller() {
        this.recommendationServices = new RecommendationServices();
        this.exerciseAssigner = new RegularExerciseAssigner();
        this.missedExerciseRescheduler = new MissedExerciseRescheduler();
        this.workoutHistory = new WorkoutHistory();
        this.calorieTracker = new CalorieTracker();
        this.calorieCalculator = new CalorieCalculator();
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
     * Assigns exercises to user based on user's cardiac status and health goal.
     */
    public List<Exercise> assignExercises(User user) throws InvalidUserDataException {
        if (user == null) {
            throw new InvalidUserDataException("Cannot assign exercises: User instance is null.");
        }
        exerciseAssigner.setExercisesAssigned(user.isHasCardiacProblems(), user.getHealthGoal());
        return exerciseAssigner.getExerciseAssigned();
    }

    /**
     * Creates a WorkoutPlan for the user by assigning exercises and scheduling them over user target days.
     */
    public WorkoutPlan createWorkoutPlan(User user) throws InvalidUserDataException {
        if (user == null) {
            throw new InvalidUserDataException("Cannot create workout plan: User instance is null.");
        }
        List<Exercise> assigned = assignExercises(user);
        int targetDays = user.getHealthGoal() != null ? user.getHealthGoal().getTargetDays() : 30;
        this.exerciseScheduler = new ExerciseScheduler(assigned, targetDays, user.getDaysPerWeek());
        List<Workout> workouts = exerciseScheduler.getWorkoutSchedule(user.getHealthGoal());
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
        report.append(recommendationServices.getMissedWorkoutRecoveryRecommendation(activeHistory));

        return report.toString();
    }

    /**
     * Reschedules missed exercises using MissedExerciseRescheduler.
     */
    public void rescheduleMissedExercises(User user, int noOfDays, float hoursPerDay) throws InvalidUserDataException {
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
    }

    /**
     * Marks an exercise as completed in a Workout session and logs calculated calories into CalorieTracker.
     */
    public void completeExercise(Workout workout, int exerciseId) {
        if (workout == null) return;
        for (Exercise ex : workout.getExercises()) {
            if (ex.getExerciseId() == exerciseId) {
                ex.setComplete(true);
                float burned = calorieCalculator.calculateCalories(ex);
                calorieTracker.logCalorieBurn(burned);
                System.out.println("Exercise '" + ex.getName() + "' (ID: " + exerciseId + ") completed! Logged " 
                                   + String.format("%.1f", burned) + " kcal into CalorieTracker.");
                break;
            }
        }
    }
}

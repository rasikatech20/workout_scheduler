package analytics;

import tracker.CalorieTracker;
import workouts.WorkoutHistory;

/**
 * RecommendationServices class as defined in the UML Class Diagram.
 * Generates personalized, rule-based recommendations for users based on
 * calorie burn progress, workout streak, history performance, cardiac safety,
 * and missed workout recovery.
 */
public class RecommendationServices {

    /**
     * Generates recommendation based on user's CalorieTracker state.
     * Evaluates daily target burn vs. actual logged calories burned.
     */
    public String getCalorieBasedRecommendation(CalorieTracker calorieTracker) {
        if (calorieTracker == null) {
            return "Calorie Recommendation: No calorie tracking data available.";
        }

        float totalBurned = calorieTracker.getTotalCaloriesBurned();
        float dailyTarget = calorieTracker.getDailyTargetCalorie();
        float deficitOrSurplus = calorieTracker.calculateDailyDeficitOrSurplusToBurn();

        StringBuilder rec = new StringBuilder();
        rec.append("=== Calorie-Based Recommendation ===\n");
        rec.append("Total Burned: ").append(String.format("%.1f", totalBurned)).append(" kcal | Daily Target: ").append(String.format("%.1f", dailyTarget)).append(" kcal\n");

        if (deficitOrSurplus > 0) {
            rec.append("Advice: You need to burn ").append(String.format("%.1f", deficitOrSurplus))
               .append(" more kcal to hit your daily target. Add a 20-30 minute cardio session (e.g. Running, Cycling) or increase set counts.");
        } else {
            rec.append("Advice: Excellent! You have met or exceeded your daily calorie burn target by ")
               .append(String.format("%.1f", Math.abs(deficitOrSurplus)))
               .append(" kcal. Ensure proper hydration and recovery.");
        }

        return rec.toString();
    }

    /**
     * Generates recommendation based on user's active workout streak.
     */
    public String getStreakBasedRecommendation(WorkoutHistory history) {
        if (history == null) {
            return "Streak Recommendation: No workout history found.";
        }

        WorkoutPerformanceAnalyzer analyzer = new WorkoutPerformanceAnalyzer();
        int streak = analyzer.getCurrentStreak(history);

        StringBuilder rec = new StringBuilder();
        rec.append("=== Streak-Based Recommendation ===\n");
        rec.append("Current Streak: ").append(streak).append(" consecutive session(s)\n");

        if (streak >= 14) {
            rec.append("Advice: Incredible 14+ streak! Focus on active recovery, foam rolling, and mobility exercises to prevent burnout while keeping the momentum high.");
        } else if (streak >= 7) {
            rec.append("Advice: Fantastic 7+ streak! You are building great discipline. Maintain your current pace and ensure 7-8 hours of sleep.");
        } else if (streak > 0) {
            rec.append("Advice: Good start with a ").append(streak).append("-session streak! Keep logging daily workouts to push towards a 7-day milestone.");
        } else {
            rec.append("Advice: No active streak. Start your scheduled workout session today to kick off a new streak!");
        }

        return rec.toString();
    }

    /**
     * Generates recommendation based on overall historical completion rate.
     */
    public String getHistoryBasedRecommendation(WorkoutHistory history) {
        if (history == null) {
            return "History Recommendation: No workout history found.";
        }

        WorkoutPerformanceAnalyzer analyzer = new WorkoutPerformanceAnalyzer();
        float rate = analyzer.getCompletionRate(history);

        StringBuilder rec = new StringBuilder();
        rec.append("=== History-Based Recommendation ===\n");
        rec.append("Overall Completion Rate: ").append(String.format("%.1f", rate)).append("%\n");

        if (rate >= 85.0f) {
            rec.append("Advice: High performance history! You complete almost all assigned workouts. Consider progressive overload (increasing reps or weight by 5-10%).");
        } else if (rate >= 60.0f) {
            rec.append("Advice: Moderate completion rate. Aim to eliminate missed sessions by optimizing your workout scheduling.");
        } else {
            rec.append("Advice: Low completion rate. Reduce session length or split workouts into manageable morning and evening blocks.");
        }

        return rec.toString();
    }

    /**
     * Generates recommendation regarding cardiac safety considerations.
     */
    public String getCardiacSafeRecommendation(boolean hasCardiacProblems) {
        StringBuilder rec = new StringBuilder();
        rec.append("=== Cardiac Safety Recommendation ===\n");

        if (hasCardiacProblems) {
            rec.append("RESTRICTION ALERT: User flagged with cardiac considerations.\n")
               .append("Advice: Avoid high-intensity interval training (HIIT), heavy maximum weightlifting, and breath-holding (Valsalva maneuver).\n")
               .append("Recommended Exercises: Low-impact, steady-state cardio (brisk walking, light cycling, swimming) with intensity <= 6.0/10 and mandatory heart rate monitoring.");
        } else {
            rec.append("Status: Clear.\n")
               .append("Advice: No cardiac restrictions detected. Proceed with full standard intensity levels as assigned.");
        }

        return rec.toString();
    }

    /**
     * Generates recommendation for recovering missed workouts.
     */
    public String getMissedWorkoutRecoveryRecommendation(WorkoutHistory history) {
        if (history == null) {
            return "Missed Workout Recovery: No workout history found.";
        }

        int missedCount = history.getMissedWorkoutsCount();
        StringBuilder rec = new StringBuilder();
        rec.append("=== Missed Workout Recovery Recommendation ===\n");

        if (missedCount > 0) {
            rec.append("Missed Sessions Count: ").append(missedCount).append("\n")
               .append("Advice: You have missed workout sessions. Use the MissedExerciseRescheduler to spread missed exercises over future open workout slots without exceeding daily time limits.");
        } else {
            rec.append("Status: No missed sessions.\n")
               .append("Advice: Great job keeping up with your workout schedule!");
        }

        return rec.toString();
    }
}

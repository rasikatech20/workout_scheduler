package tracker;

import goals.HealthGoal;
import goals.WeightLossGoal;
import java.util.ArrayList;
import java.util.List;

public class CalorieTracker {
    private float totalCaloriesBurned;
    private float totalMissedCalories;
    private List<Float> caloriesBurntPerDay;
    private float dailyTargetCalorie;
    private float bmr;

    public CalorieTracker() {
        this.totalCaloriesBurned = 0.0f;
        this.totalMissedCalories = 0.0f;
        this.caloriesBurntPerDay = new ArrayList<>();
        this.dailyTargetCalorie = 0.0f;
        this.bmr = 0.0f;
    }

    public float getTotalCaloriesBurned() {
        return totalCaloriesBurned;
    }

    public void setTotalCaloriesBurned(float totalCaloriesBurned) {
        this.totalCaloriesBurned = totalCaloriesBurned;
    }

    public List<Float> getCaloriesBurntPerDay() {
        return caloriesBurntPerDay;
    }

    public void setCaloriesBurntPerDay(List<Float> caloriesBurntPerDay) {
        this.caloriesBurntPerDay = caloriesBurntPerDay;
    }

    public float getDailyTargetCalorie() {
        return dailyTargetCalorie;
    }

    public void setDailyTargetCalorie(float dailyTargetCalorie) {
        this.dailyTargetCalorie = dailyTargetCalorie;
    }

    public float getBmr() {
        return bmr;
    }

    public void setBmr(float bmr) {
        this.bmr = bmr;
    }

    //Returns the currently stored BMR.
    public float calculateBMR() {
        return bmr;
    }

    /**
     * Calculates BMR using the Mifflin-St Jeor equation:
     * BMR = (10 * weight_kg) + (6.25 * height_cm) - (5 * age) + s (s = +5 for male, -161 for female)
     * Updates and returns the calculated BMR.
     */
    //BMR-basal metabolic rate(no of calories our body burns at rest)
    public float calculateBMR(float weight_kg, float height_cm, int age, boolean isMale) {
        float genderFactor = isMale ? 5.0f : -161.0f;
        this.bmr = (10.0f * weight_kg) + (6.25f * height_cm) - (5.0f * age) + genderFactor;
        return this.bmr;
    }

    /**
     * Calculates the daily target calorie burn using user activity level and schedule,
     * with any weight-loss goal requirement applied as a lower bound.
     */
    public float calculateDailyTargetCalorie(HealthGoal healthGoal, int daysPerWeek, float hoursPerDay) {
        float activityMultiplier = 1.2f;
        if (daysPerWeek >= 5) {
            activityMultiplier = 1.55f;
        } else if (daysPerWeek >= 3) {
            activityMultiplier = 1.375f;
        }

        if (this.bmr > 0) {
            this.dailyTargetCalorie = (this.bmr * (activityMultiplier - 1.0f)) + (hoursPerDay * 200.0f);
        } else {
            this.dailyTargetCalorie = (daysPerWeek * hoursPerDay * 150.0f);
        }

        if (healthGoal instanceof WeightLossGoal) {
            WeightLossGoal wlg = (WeightLossGoal) healthGoal;
            float dailyDeficit = (wlg.getWeeklyLossRate() * 7700.0f) / 7.0f; //calculating the calories per day i should be under in my level so that i can lose my planned weight
            this.dailyTargetCalorie = Math.max(this.dailyTargetCalorie, dailyDeficit);//makes sure target is not less that deficity needed
        }

        return this.dailyTargetCalorie;
    }

    public float calculateDailyTargetCalorie() {
        if (this.dailyTargetCalorie == 0.0f && this.bmr > 0) {
            this.dailyTargetCalorie = this.bmr * 0.35f;
        }
        return this.dailyTargetCalorie;
    }

    /**
     * Logs calories burned in a workout session.
     * Appends the value to caloriesBurntPerDay and updates totalCaloriesBurned.
     */
    public void logCalorieBurn(float calories) {
        this.totalCaloriesBurned += calories;
        this.caloriesBurntPerDay.add(calories);
    }

    /**
     * Calculates remaining daily deficit or surplus required to reach dailyTargetCalorie.
     * Returns dailyTargetCalorie minus the latest logged daily calorie burn.
     */
    public float calculateDailyDeficitOrSurplusToBurn() {
        if (caloriesBurntPerDay.isEmpty()) {
            return dailyTargetCalorie;
        }
        float lastBurn = caloriesBurntPerDay.get(caloriesBurntPerDay.size() - 1);//recent
        return dailyTargetCalorie - lastBurn;
    }

    public float getTotalMissedCalories() {
        return totalMissedCalories;
    }

    public void setTotalMissedCalories(float totalMissedCalories) {
        this.totalMissedCalories = totalMissedCalories;
    }

    /**
     * Logs calories missed from missed exercises/sessions.
     * Appends to totalMissedCalories.
     */
    public void logMissedCalories(float calories) {
        this.totalMissedCalories += calories;
    }

    public void displayCalorieStatus() {
        System.out.println("=== Calorie Tracker Status ===");
        System.out.println("BMR                     : " + String.format("%.1f", bmr) + " kcal/day");
        System.out.println("Daily Target Burn       : " + String.format("%.1f", dailyTargetCalorie) + " kcal");
        System.out.println("Total Calories Burned   : " + String.format("%.1f", totalCaloriesBurned) + " kcal");
        System.out.println("Total Missed Calories   : " + String.format("%.1f", totalMissedCalories) + " kcal");
        System.out.println("Days Logged             : " + caloriesBurntPerDay.size());
        if (!caloriesBurntPerDay.isEmpty()) {
            float lastBurn = caloriesBurntPerDay.get(caloriesBurntPerDay.size() - 1);
            float remaining = calculateDailyDeficitOrSurplusToBurn();
            System.out.println("Latest Daily Burn       : " + String.format("%.1f", lastBurn) + " kcal");
            if (remaining > 0) {
                System.out.println("Remaining Calorie Burn  : " + String.format("%.1f", remaining) + " kcal to hit target");
            } else {
                System.out.println("Target Achieved Surplus : " + String.format("%.1f", Math.abs(remaining)) + " kcal over target!");
            }
        }
    }
}

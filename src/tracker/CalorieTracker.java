package tracker;

import java.util.ArrayList;
import java.util.List;

public class CalorieTracker {
    private float totalCaloriesBurned;
    private List<Float> caloriesBurntPerDay;
    private float dailyTargetCalorie;
    private float bmr;

    public CalorieTracker() {
        this.totalCaloriesBurned = 0.0f;
        this.caloriesBurntPerDay = new ArrayList<>();
        this.dailyTargetCalorie = 2000.0f;
        this.bmr = 1500.0f;
    }

    public CalorieTracker(float dailyTargetCalorie, float bmr) {
        this.totalCaloriesBurned = 0.0f;
        this.caloriesBurntPerDay = new ArrayList<>();
        this.dailyTargetCalorie = dailyTargetCalorie;
        this.bmr = bmr;
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

    /**
     * Returns the currently stored BMR.
     */
    public float calculateBMR() {
        return bmr;
    }

    /**
     * Calculates BMR using the Mifflin-St Jeor equation:
     * BMR = (10 * weight_kg) + (6.25 * height_cm) - (5 * age) + s (s = +5 for male, -161 for female)
     * Updates and returns the calculated BMR.
     */
    public float calculateBMR(float weight_kg, float height_cm, int age, boolean isMale) {
        float genderFactor = isMale ? 5.0f : -161.0f;
        this.bmr = (10.0f * weight_kg) + (6.25f * height_cm) - (5.0f * age) + genderFactor;
        return this.bmr;
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
        float lastBurn = caloriesBurntPerDay.get(caloriesBurntPerDay.size() - 1);
        return dailyTargetCalorie - lastBurn;
    }

    public void displayCalorieStatus() {
        System.out.println("=== Calorie Tracker Status ===");
        System.out.println("BMR                     : " + String.format("%.1f", bmr) + " kcal/day");
        System.out.println("Daily Target Burn       : " + String.format("%.1f", dailyTargetCalorie) + " kcal");
        System.out.println("Total Calories Burned   : " + String.format("%.1f", totalCaloriesBurned) + " kcal");
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

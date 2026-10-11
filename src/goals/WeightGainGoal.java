package goals;

/**
 * Concrete HealthGoal implementation for Weight Gain goals.
 */
public class WeightGainGoal extends HealthGoal {
    private float currentWeight;
    private float weeklyGainRate;

    public WeightGainGoal(String goalName, int targetDays, float targetWeight, float currentWeight) {
        super(goalName, targetDays, targetWeight);
        this.currentWeight = currentWeight;
        this.weeklyGainRate = computeWeeklyGainRate();
    }

    public WeightGainGoal(int targetDays, float targetWeight, float currentWeight) {
        super("Weight Gain Goal", targetDays, targetWeight);
        this.currentWeight = currentWeight;
        this.weeklyGainRate = computeWeeklyGainRate();
    }

    private float computeWeeklyGainRate() {
        if (targetDays <= 0) {
            return 0.0f;
        }
        float weightToGain = Math.max(0.0f, targetWeight - currentWeight);
        float weeks = targetDays / 7.0f;
        if (weeks <= 0.0f) {
            return 0.0f;
        }
        return weightToGain / weeks;
    }

    public float getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(float currentWeight) {
        this.currentWeight = currentWeight;
        this.weeklyGainRate = computeWeeklyGainRate();
    }

    public float getWeeklyGainRate() {
        return weeklyGainRate;
    }

    public void setWeeklyGainRate(float weeklyGainRate) {
        this.weeklyGainRate = computeWeeklyGainRate();
    }

    @Override
    public boolean isRealisticGoal() {
        if (targetWeight <= currentWeight || targetDays <= 0 || weeklyGainRate <= 0.0f) {
            return false;
        }

        return weeklyGainRate <= 1.0f;
    }

    @Override
    public void displayGoal() {
        System.out.println("================ HEALTH GOAL DETAILS ================");
        System.out.println("Goal Name        : " + goalName);
        System.out.println("Current Weight   : " + currentWeight + " kg");
        System.out.println("Target Weight    : " + targetWeight + " kg");
        System.out.println("Target Duration  : " + targetDays + " days (" + String.format("%.1f", targetDays / 7.0f) + " weeks)");
        System.out.println("Weekly Gain Rate : " + weeklyGainRate + " kg/week");
        System.out.println("Realistic Goal   : " + (isRealisticGoal() ? "Yes" : "No (Rate too aggressive or invalid targets)"));
        System.out.println("=====================================================");
    }
}

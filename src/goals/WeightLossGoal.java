package goals;

/**
 * Concrete HealthGoal implementation for Weight Loss goals.
 */
public class WeightLossGoal extends HealthGoal {
    private float currentWeight;
    private float weeklyLossRate;

    public WeightLossGoal(String goalName, int targetDays, float targetWeight, float currentWeight) {
        super(goalName, targetDays, targetWeight);
        this.currentWeight = currentWeight;
        this.weeklyLossRate = computeWeeklyLossRate();
    }

    public WeightLossGoal(int targetDays, float targetWeight, float currentWeight) {
        super("Weight Loss Goal", targetDays, targetWeight);
        this.currentWeight = currentWeight;
        this.weeklyLossRate = computeWeeklyLossRate();
    }

    private float computeWeeklyLossRate() {
        if (targetDays <= 0) {
            return 0.0f;
        }
        float weightToLose = Math.max(0.0f, currentWeight - targetWeight);
        float weeks = targetDays / 7.0f;
        if (weeks <= 0.0f) {
            return 0.0f;
        }
        return weightToLose / weeks; //formula for weeklyLossRate
    }

    public float getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(float currentWeight) {
        this.currentWeight = currentWeight;
        this.weeklyLossRate = computeWeeklyLossRate();
    }

    public float getWeeklyLossRate() {
        return weeklyLossRate;
    }

    public void setWeeklyLossRate(float weeklyLossRate) {
        this.weeklyLossRate = computeWeeklyLossRate();
    }

    @Override
    public boolean isRealisticGoal() {
        if (currentWeight <= targetWeight || targetDays <= 0 || weeklyLossRate <= 0.0f) {
            return false;
        }

        if (weeklyLossRate > 1.5f) {
            return false;
        }

        float totalWeightToLose = currentWeight - targetWeight;
        float weeksAvailable = targetDays / 7.0f;
        float requiredWeeklyRate = totalWeightToLose / weeksAvailable;

        return requiredWeeklyRate <= 1.5f;
    }

    @Override
    public void displayGoal() {
        System.out.println("================ HEALTH GOAL DETAILS ================");
        System.out.println("Goal Name        : " + goalName);
        System.out.println("Current Weight   : " + currentWeight + " kg");
        System.out.println("Target Weight    : " + targetWeight + " kg");
        System.out.println("Target Duration  : " + targetDays + " days (" + String.format("%.1f", targetDays / 7.0f) + " weeks)");
        System.out.println("Weekly Loss Rate : " + weeklyLossRate + " kg/week");
        System.out.println("Realistic Goal   : " + (isRealisticGoal() ? "Yes" : "No (Rate too aggressive or invalid targets)"));
        System.out.println("=====================================================");
    }
}

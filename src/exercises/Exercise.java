package exercises;

public abstract class Exercise {
    protected int exerciseId;
    protected String name;
    protected int durationMinutes;
    protected int setsCount;
    protected int repsCount;
    protected boolean isComplete;
    protected float calculatedCalories;

    public Exercise(int exerciseId, String name, int durationMinutes, int setsCount, int repsCount) {
        this.exerciseId = exerciseId;
        this.name = name;
        this.durationMinutes = durationMinutes;
        this.setsCount = setsCount;
        this.repsCount = repsCount;
        this.isComplete = false;
        this.calculatedCalories = 0.0f;
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getSetsCount() {
        return setsCount;
    }

    public void setSetsCount(int setsCount) {
        this.setsCount = setsCount;
    }

    public int getRepsCount() {
        return repsCount;
    }

    public void setRepsCount(int repsCount) {
        this.repsCount = repsCount;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public float getCalculatedCalories() {
        return calculatedCalories;
    }

    public void setCalculatedCalories(float calculatedCalories) {
        this.calculatedCalories = calculatedCalories;
    }

    public abstract void displayExercise();
    public abstract float calculateCaloriesBurned();
    public abstract int recalculateDuration(float bp, float intensityLevel);
    public abstract float getIntensityLevel();
}

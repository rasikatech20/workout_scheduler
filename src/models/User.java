package models;

import goals.HealthGoal;
import tracker.CalorieTracker;
import workouts.WorkoutHistory;

/**
 * User entity class matching the UML Class Diagram specifications.
 */
public class User {
    private int userId;
    private String name;
    private int age;
    private float weight_kg;
    private float height_cm;
    private float bp;
    private boolean hasCardiacProblems;
    private HealthGoal healthGoal;
    private WorkoutHistory workoutHistory;
    private CalorieTracker calorieTracker;
    private int daysPerWeek;
    private float hoursPerDay;

    private static int idCounter = 1001;

    public User(String name, int age, float weight_kg, float height_cm, HealthGoal healthGoal, int daysPerWeek, float hoursPerDay) {
        this(name, age, weight_kg, height_cm, false, healthGoal, daysPerWeek, hoursPerDay);
    }

    public User(String name, int age, float weight_kg, float height_cm, boolean hasCardiacProblems, HealthGoal healthGoal, int daysPerWeek, float hoursPerDay) {
        this.userId = idCounter++;
        this.name = name;
        this.age = age;
        this.weight_kg = weight_kg;
        this.height_cm = height_cm;
        this.bp = 120.0f;
        this.hasCardiacProblems = hasCardiacProblems;
        this.healthGoal = healthGoal;
        this.workoutHistory = new WorkoutHistory();
        this.calorieTracker = new CalorieTracker(2000.0f, 1600.0f);
        this.calorieTracker.calculateBMR(weight_kg, height_cm, age, true);
        this.daysPerWeek = daysPerWeek;
        this.hoursPerDay = hoursPerDay;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public float getWeight_kg() {
        return weight_kg;
    }

    public void setWeight_kg(float weight_kg) {
        this.weight_kg = weight_kg;
        if (calorieTracker != null) {
            calorieTracker.calculateBMR(weight_kg, height_cm, age, true);
        }
    }

    public float getHeight_cm() {
        return height_cm;
    }

    public void setHeight_cm(float height_cm) {
        this.height_cm = height_cm;
        if (calorieTracker != null) {
            calorieTracker.calculateBMR(weight_kg, height_cm, age, true);
        }
    }

    public float getBp() {
        return bp;
    }

    public void setBp(float bp) {
        this.bp = bp;
    }

    public boolean isHasCardiacProblems() {
        return hasCardiacProblems;
    }

    public void setHasCardiacProblems(boolean hasCardiacProblems) {
        this.hasCardiacProblems = hasCardiacProblems;
    }

    public HealthGoal getHealthGoal() {
        return healthGoal;
    }

    public void setHealthGoal(HealthGoal healthGoal) {
        this.healthGoal = healthGoal;
    }

    public WorkoutHistory getWorkoutHistory() {
        return workoutHistory;
    }

    public void setWorkoutHistory(WorkoutHistory workoutHistory) {
        this.workoutHistory = workoutHistory;
    }

    public CalorieTracker getCalorieTracker() {
        return calorieTracker;
    }

    public void setCalorieTracker(CalorieTracker calorieTracker) {
        this.calorieTracker = calorieTracker;
    }

    public int getDaysPerWeek() {
        return daysPerWeek;
    }

    public void setDaysPerWeek(int daysPerWeek) {
        this.daysPerWeek = daysPerWeek;
    }

    public float getHoursPerDay() {
        return hoursPerDay;
    }

    public void setHoursPerDay(float hoursPerDay) {
        this.hoursPerDay = hoursPerDay;
    }

    public void displayUserProfile() {
        System.out.println("=== USER PROFILE ===");
        System.out.println("User ID          : " + userId);
        System.out.println("Name             : " + name);
        System.out.println("Age              : " + age + " years");
        System.out.println("Weight           : " + weight_kg + " kg");
        System.out.println("Height           : " + height_cm + " cm");
        System.out.println("Blood Pressure   : " + bp);
        System.out.println("Cardiac Problems : " + (hasCardiacProblems ? "YES" : "NO"));
        System.out.println("Health Goal      : " + (healthGoal != null ? healthGoal.getGoalName() : "None"));
        System.out.println("Availability     : " + daysPerWeek + " days/week, " + hoursPerDay + " hrs/day");
    }
}

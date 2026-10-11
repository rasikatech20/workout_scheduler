package exercises;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository class providing predefined lists of WeightLoss and WeightGain concrete exercises.
 */
public class ExerciseRepository {

    public static List<WeightLossExercise> getPredefinedWeightLossExercises() {
        List<WeightLossExercise> list = new ArrayList<>();
        list.add(new WeightLossExercise(101, "Brisk Walking", 30, 1, 1, "Low-Impact Cardio", 3.5f));
        list.add(new WeightLossExercise(102, "Jogging", 25, 1, 1, "Moderate Cardio", 6.5f));
        list.add(new WeightLossExercise(103, "Outdoor Cycling", 40, 1, 1, "Low-Impact Cardio", 4.5f));
        list.add(new WeightLossExercise(104, "Jumping Jacks", 15, 3, 30, "HIIT Cardio", 8.5f));
        list.add(new WeightLossExercise(105, "Stair Climbing", 20, 4, 25, "High-Impact Cardio", 7.5f));
        list.add(new WeightLossExercise(106, "Aerobic Dance Routine", 30, 1, 1, "Low-Impact Cardio", 4.0f));
        list.add(new WeightLossExercise(107, "Incline Treadmill Walk", 25, 1, 1, "Low-Impact Cardio", 4.5f));
        list.add(new WeightLossExercise(108, "Jump Rope Intervals", 15, 5, 50, "HIIT Cardio", 9.0f));
        list.add(new WeightLossExercise(109, "Stationary Ergometer Cycling", 20, 1, 1, "Low-Impact Cardio", 3.8f));
        list.add(new WeightLossExercise(110, "Elliptical Trainer", 30, 1, 1, "Low-Impact Cardio", 5.2f));
        list.add(new WeightLossExercise(111, "Rowing Machine", 25, 1, 1, "Moderate Cardio", 6.0f));
        list.add(new WeightLossExercise(112, "Mountain Climbers", 12, 4, 20, "HIIT Cardio", 8.8f));
        list.add(new WeightLossExercise(113, "Power Walking", 35, 1, 1, "Low-Impact Cardio", 4.2f));
        list.add(new WeightLossExercise(114, "Swimming Laps", 30, 1, 1, "Low-Impact Cardio", 5.5f));
        list.add(new WeightLossExercise(115, "Burpees", 15, 3, 20, "HIIT Cardio", 9.2f));
        return list;
    }

    public static List<WeightLossExercise> getAdditionalWeightLossExercises() {
        List<WeightLossExercise> list = new ArrayList<>();
        list.add(new WeightLossExercise(116, "Cycling Sprints", 20, 5, 20, "HIIT Cardio", 8.7f));
        list.add(new WeightLossExercise(117, "Shadow Boxing", 18, 4, 25, "HIIT Cardio", 8.4f));
        list.add(new WeightLossExercise(118, "Treadmill Intervals", 20, 6, 30, "HIIT Cardio", 9.1f));
        list.add(new WeightLossExercise(119, "Zumba Session", 30, 1, 1, "Dance Cardio", 5.0f));
        list.add(new WeightLossExercise(120, "Kickboxing Circuit", 25, 3, 15, "Combat Cardio", 7.4f));
        list.add(new WeightLossExercise(121, "Hiking", 35, 1, 1, "Outdoor Cardio", 4.8f));
        list.add(new WeightLossExercise(122, "Step Aerobics", 25, 3, 20, "Low-Impact Cardio", 6.0f));
        list.add(new WeightLossExercise(123, "Circuit Jumps", 15, 4, 25, "HIIT Cardio", 8.9f));
        list.add(new WeightLossExercise(124, "Fast Walking with Weights", 30, 1, 1, "Low-Impact Cardio", 5.0f));
        list.add(new WeightLossExercise(125, "Battle Ropes", 15, 5, 30, "HIIT Cardio", 9.5f));
        list.add(new WeightLossExercise(126, "Jogging Intervals", 22, 6, 30, "Moderate Cardio", 7.5f));
        list.add(new WeightLossExercise(127, "Bodyweight Circuit", 25, 4, 20, "Moderate Cardio", 6.8f));
        list.add(new WeightLossExercise(128, "Skating", 30, 1, 1, "Low-Impact Cardio", 4.9f));
        list.add(new WeightLossExercise(129, "In-Line Skating", 25, 1, 1, "Low-Impact Cardio", 5.2f));
        list.add(new WeightLossExercise(130, "Vigorous Dance Workout", 20, 1, 1, "Dance Cardio", 6.5f));
        return list;
    }

    public static List<WeightGainExercise> getPredefinedWeightGainExercises() {
        List<WeightGainExercise> list = new ArrayList<>();
        list.add(new WeightGainExercise(201, "Barbell Squats", 30, 4, 10, 60.0f, "Barbell & Squat Rack", 7.5f));
        list.add(new WeightGainExercise(202, "Dumbbell Bench Press", 25, 4, 12, 20.0f, "Dumbbells & Bench", 6.5f));
        list.add(new WeightGainExercise(203, "Barbell Deadlift", 30, 3, 8, 80.0f, "Barbell & Weight Plates", 8.5f));
        list.add(new WeightGainExercise(204, "Dumbbell Bent-Over Rows", 20, 3, 12, 15.0f, "Dumbbells", 5.5f));
        list.add(new WeightGainExercise(205, "Standing Shoulder Press", 20, 3, 10, 12.0f, "Dumbbells", 5.0f));
        list.add(new WeightGainExercise(206, "Bodyweight Push-ups", 15, 3, 15, 0.0f, "Yoga Mat", 3.5f));
        list.add(new WeightGainExercise(207, "Seated Cable Row", 20, 3, 12, 25.0f, "Cable Machine", 4.5f));
        list.add(new WeightGainExercise(208, "Leg Press Machine", 25, 4, 12, 90.0f, "Leg Press Machine", 7.0f));
        list.add(new WeightGainExercise(209, "Bicep Dumbbell Curls", 15, 3, 12, 10.0f, "Dumbbells", 4.0f));
        list.add(new WeightGainExercise(210, "Overhead Press Machine", 20, 3, 10, 22.0f, "Machine", 5.2f));
        list.add(new WeightGainExercise(211, "Goblet Squats", 25, 4, 10, 18.0f, "Kettlebell", 6.0f));
        list.add(new WeightGainExercise(212, "Pull-Ups", 15, 4, 8, 0.0f, "Pull-Up Bar", 6.8f));
        list.add(new WeightGainExercise(213, "Romanian Deadlifts", 30, 3, 8, 35.0f, "Barbell", 7.2f));
        list.add(new WeightGainExercise(214, "Chest Press Machine", 22, 4, 10, 35.0f, "Chest Press Machine", 6.2f));
        list.add(new WeightGainExercise(215, "Lat Pulldown", 20, 4, 12, 28.0f, "Cable Machine", 5.8f));
        return list;
    }

    public static List<WeightGainExercise> getAdditionalWeightGainExercises() {
        List<WeightGainExercise> list = new ArrayList<>();
        list.add(new WeightGainExercise(216, "Incline Dumbbell Press", 24, 4, 10, 18.0f, "Dumbbells & Bench", 6.3f));
        list.add(new WeightGainExercise(217, "Single-Arm Dumbbell Row", 20, 3, 12, 15.0f, "Dumbbell", 5.1f));
        list.add(new WeightGainExercise(218, "Lunges with Dumbbells", 25, 4, 10, 18.0f, "Dumbbells", 6.5f));
        list.add(new WeightGainExercise(219, "Hip Thrusts", 25, 4, 10, 60.0f, "Barbell", 7.1f));
        list.add(new WeightGainExercise(220, "Dumbbell Shoulder Lateral Raise", 18, 4, 12, 8.0f, "Dumbbells", 4.3f));
        list.add(new WeightGainExercise(221, "Cable Flyes", 18, 3, 12, 15.0f, "Cable Machine", 4.8f));
        list.add(new WeightGainExercise(222, "Barbell Front Squats", 30, 4, 8, 55.0f, "Barbell", 7.4f));
        list.add(new WeightGainExercise(223, "Hammer Curls", 20, 3, 12, 12.0f, "Dumbbells", 4.9f));
        list.add(new WeightGainExercise(224, "Tricep Rope Pushdowns", 18, 3, 12, 10.0f, "Cable Machine", 4.6f));
        list.add(new WeightGainExercise(225, "Stepper Squats", 22, 4, 12, 12.0f, "Bodyweight / Bench", 5.8f));
        list.add(new WeightGainExercise(226, "Kettlebell Swings", 20, 4, 12, 20.0f, "Kettlebell", 6.7f));
        list.add(new WeightGainExercise(227, "Bulgarian Split Squats", 25, 3, 10, 16.0f, "Dumbbells", 6.9f));
        list.add(new WeightGainExercise(228, "Machine Chest Press", 22, 4, 10, 32.0f, "Machine", 6.0f));
        list.add(new WeightGainExercise(229, "Barbell Rows", 25, 4, 8, 45.0f, "Barbell", 6.8f));
        list.add(new WeightGainExercise(230, "Weighted Carry", 15, 4, 20, 35.0f, "Kettlebell / Dumbbell", 5.6f));
        return list;
    }
}

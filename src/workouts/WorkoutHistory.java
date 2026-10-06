package workouts;

import exercises.Exercise;
import java.util.ArrayList;
import java.util.List;

public class WorkoutHistory {
    private List<List<Exercise>> missedExercises;
    private List<List<Exercise>> completedExercises;

    public WorkoutHistory() {
        this.missedExercises = new ArrayList<>();
        this.completedExercises = new ArrayList<>();
    }

    public List<List<Exercise>> getMissedExercises() {
        return missedExercises;
    }

    public void setMissedExercises(List<List<Exercise>> missedExercises) {
        this.missedExercises = missedExercises;
    }

    public List<List<Exercise>> getCompletedExercises() {
        return completedExercises;
    }

    public void setCompletedExercises(List<List<Exercise>> completedExercises) {
        this.completedExercises = completedExercises;
    }

    public void addWorkoutRecord(List<Exercise> completed, List<Exercise> missed) {
        this.completedExercises.add(completed != null ? completed : new ArrayList<>());
        this.missedExercises.add(missed != null ? missed : new ArrayList<>());
    }

    public void updateWorkoutRecord(int index, List<Exercise> completed, List<Exercise> missed) {
        if (index >= 0 && index < completedExercises.size()) {
            if (completed != null) {
                completedExercises.set(index, completed);
            }
            if (missed != null) {
                missedExercises.set(index, missed);
            }
        }
    }

    public String generateHistoryReport(int userId) {
        StringBuilder report = new StringBuilder();
        report.append("=== WORKOUT HISTORY REPORT FOR USER ").append(userId).append(" ===\n");
        report.append("Total Sessions Tracked: ").append(completedExercises.size()).append("\n");

        int totalCompletedCount = 0;
        for (List<Exercise> session : completedExercises) {
            totalCompletedCount += session.size();
        }

        int totalMissedCount = 0;
        for (List<Exercise> session : missedExercises) {
            totalMissedCount += session.size();
        }

        report.append("Total Completed Exercises: ").append(totalCompletedCount).append("\n");
        report.append("Total Missed Exercises   : ").append(totalMissedCount).append("\n");
        report.append("Missed Sessions Count    : ").append(getMissedWorkoutsCount()).append("\n");
        return report.toString();
    }

    public int getMissedWorkoutsCount() {
        int count = 0;
        for (List<Exercise> missedList : missedExercises) {
            if (!missedList.isEmpty()) {
                count++;
            }
        }
        return count;
    }

    public List<List<Exercise>> getHistoryByCompletionStatus(String status) {
        if ("completed".equalsIgnoreCase(status)) {
            return completedExercises;
        } else if ("missed".equalsIgnoreCase(status)) {
            return missedExercises;
        }
        return new ArrayList<>();
    }

    public void clearHistory(int userId) { //to clear workout history logs for a user
        this.completedExercises.clear();
        this.missedExercises.clear();
    }
}

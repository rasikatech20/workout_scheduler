package login;

import exceptions.InvalidGoalException;
import exceptions.InvalidUserDataException;
import exceptions.LoginFailedException;
import goals.HealthGoal;
import goals.WeightGainGoal;
import goals.WeightLossGoal;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import models.User;

public class Login {
    private static final String FILE_NAME = "user_database.txt";
    private static int loginCount = 0;

    /**
     * Registers a new user with complete profile data and appends to user_database.txt using FileWriter.
     */
    public User register(String name, int age, float weight_kg, float height_cm, float bp, String gender,
                         boolean hasCardiacProblems, HealthGoal healthGoal, int daysPerWeek, float hoursPerDay)
            throws InvalidUserDataException, InvalidGoalException {

        if (name == null || name.trim().isEmpty()) {
            throw new InvalidUserDataException("Name cannot be empty.");
        }

        // Validate and create User object
        User newUser = new User(name, age, weight_kg, height_cm, bp, gender, hasCardiacProblems, healthGoal, daysPerWeek, hoursPerDay);

        // Check if user already exists
        if (findUser(name) != null) {
            throw new InvalidUserDataException("User '" + name + "' already exists. Please authenticate.");
        }

        // Append user to file using FileWriter
        try (FileWriter writer = new FileWriter(FILE_NAME, true)) {
            String goalName = (healthGoal != null) ? healthGoal.getGoalName() : "General";
            int targetDays = (healthGoal != null) ? healthGoal.getTargetDays() : 30;
            float targetWeight = (healthGoal != null) ? healthGoal.getTargetWeight() : weight_kg;
            String userGender = (newUser.getGender() != null) ? newUser.getGender() : "unspecified";

            String record = name + "," + age + "," + weight_kg + "," + height_cm + "," + hasCardiacProblems
                    + "," + goalName + "," + targetDays + "," + targetWeight + "," + daysPerWeek + "," + hoursPerDay
                    + "," + newUser.getBp() + "," + userGender + "\n";
            writer.write(record);
        } catch (IOException e) {
            throw new InvalidUserDataException("Error writing to file: " + e.getMessage());
        }

        loginCount++;
        System.out.println("User '" + name + "' registered successfully.");
        return newUser;
    }

    /**
     * Authenticates an existing user by reading from users.txt using FileReader.
     */
    public User authenticate(String name) throws LoginFailedException {
        if (name == null || name.trim().isEmpty()) {
            throw new LoginFailedException("Name cannot be empty.");
        }

        User user = findUser(name);
        if (user == null) {
            throw new LoginFailedException("User '" + name + "' not found.");
        }

        loginCount++;
        System.out.println("User '" + name + "' authenticated successfully.");
        return user;
    }

    /**
     * Returns total number of user logins.
     */
    public int noOfUserLogins() {
        return loginCount;
    }

    //to read user_database.txt using FileReader
    private User findUser(String name) {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        try (FileReader reader = new FileReader(file)) {
            int ch;
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
        } catch (IOException e) {
            return null;
        }

        String[] lines = sb.toString().split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] p = line.split(",");
            if (p.length >= 10 && p[0].trim().equalsIgnoreCase(name.trim())) {
                try {
                    int age = Integer.parseInt(p[1]);
                    float weight = Float.parseFloat(p[2]);
                    float height = Float.parseFloat(p[3]);
                    boolean hasCardiac = Boolean.parseBoolean(p[4]);
                    String goalName = p[5];
                    int targetDays = Integer.parseInt(p[6]);
                    float targetWeight = Float.parseFloat(p[7]);
                    int daysPerWeek = Integer.parseInt(p[8]);
                    float hoursPerDay = Float.parseFloat(p[9]);

                    HealthGoal goal = goalName.toLowerCase().contains("gain")
                            ? new WeightGainGoal(goalName, targetDays, targetWeight, weight)
                            : new WeightLossGoal(goalName, targetDays, targetWeight, weight);

                    String gender = (p.length >= 12 && !p[11].trim().equalsIgnoreCase("unspecified")) ? p[11].trim() : null;
                    float bpVal = 120.0f;
                    if (p.length >= 11) {
                        try {
                            float parsedBp = Float.parseFloat(p[10]);
                            if (parsedBp > 0.0f) {
                                bpVal = parsedBp;
                            }
                        } catch (Exception ignored) {}
                    }
                    User user = new User(p[0], age, weight, height, bpVal, gender, hasCardiac, goal, daysPerWeek, hoursPerDay);
                    return user;
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }
}

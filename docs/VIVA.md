# Viva Preparation — Workout Scheduler

[README](../README.md) · [HLD](HLD.md) · [LLD](LLD.md)

For second-year Java lab students at SSN College, Anna University. This guide explains the current code, including its limitations. Practise answers in your own words and open the linked class when revising.

## 1. A one-minute project explanation

“Our project is a console-based Workout Scheduler implemented in Java. It demonstrates validating and saving a user profile, choosing exercises based on the goal and cardiac flag, dividing exercises into workout sessions, marking completion, recording missed exercises, rescheduling them within a daily time limit, and generating rule-based recommendations. We use abstract classes for goals and exercises, an interface for assignment, collections for sessions and history, checked exceptions for invalid input, and file I/O for profile persistence. The current Main class is a hardcoded demo; it does not provide an interactive menu. Only profile fields persist, while workout state stays in memory.”

Do not claim that it has password authentication, a dated calendar, a SQL database, machine learning, complete object serialization, or medically validated calculations.

## Java concepts with real examples

| Concept | Beginner explanation | Real example and why it matters |
| --- | --- | --- |
| Classes and objects | A class defines fields/behavior; each object has its own state | [User](../src/models/User.java) is a class; `mainUser` refers to a particular profile |
| Encapsulation | Keep fields behind methods so updates can be controlled | User's fields are private; `setAge` validates and refreshes calculated state. Some list getters expose mutable state, so protection is incomplete |
| Abstraction | Expose a common contract while leaving details to implementations | [HealthGoal](../src/goals/HealthGoal.java) and [Exercise](../src/exercises/Exercise.java) are abstract; neither can be instantiated directly |
| Inheritance | A subclass reuses and extends a parent type | [WeightLossExercise](../src/exercises/WeightLossExercise.java) extends Exercise; it adds cardio type/intensity |
| Runtime polymorphism | A parent reference can call the behavior of its actual subclass | Calculator receives an Exercise-bounded reference; overridden `calculateCaloriesBurned` chooses the cardio or strength implementation |
| Method overriding | A subclass supplies the same method signature with different behavior | Both exercise subclasses implement `calculateCaloriesBurned` and use `@Override` |
| Interface | A type defining operations that implementations must provide | [ExerciseAssigner](../src/assigners/ExerciseAssigner.java) is implemented by regular/additional assigners |
| Method overloading | Same method name, different parameter lists; selected at compile time | [Controller](../src/controller/Controller.java) has two rescheduling overloads; [CalorieTracker](../src/tracker/CalorieTracker.java) has four target overloads |
| Constructor overloading/chaining | Multiple ways to create an object can reuse initialization logic | User's shorter constructor delegates with `this(...)`; subclasses initialize parent state with `super(...)` |
| Access modifiers | Restrict visibility to keep responsibilities clear | User fields are private; Exercise/HealthGoal fields are protected; public methods are caller-facing |
| `static` | State/method belongs to the class, rather than one instance | User ID counter, Login login counter, repository pool methods, and Main's entry method |
| `final` | A variable is assigned once | Scheduler breakTime is a final value of 5; final reference variables do not generally freeze referenced objects |
| Collections | Store ordered groups of values/objects | `List<Exercise>` backed by `ArrayList`; `List<List<Exercise>>` stores per-session history |
| Generics | Describe allowed types at compile time | [CalorieCalculator](../src/tracker/CalorieCalculator.java) uses `T extends Exercise`, preventing an unrelated type such as String |
| Generic methods | A method can introduce its own type parameter | `<E extends T> float calculateCalories(E exercise)` accepts an E within the class's bound |
| Lambda and functional interface | A lambda supplies one operation for a single-abstract-method interface | `Function<E, Float> calorieComputer = ex -> ex.calculateCaloriesBurned()` |
| Autoboxing/unboxing | Convert between primitive and wrapper types automatically | `Function` returns Float; assigning to float unboxes it. `List<Float>` boxes primitive burn values |
| Checked exceptions | The compiler requires callers to handle or declare these failures | [WorkoutSchedulerException](../src/exceptions/WorkoutSchedulerException.java) extends Exception; validation subclasses are checked |
| File I/O | Read/write data using Java library classes | [Login](../src/login/Login.java) uses FileReader/FileWriter and appends comma-separated fields |
| Try-with-resources | Close a resource automatically when leaving a try block | Login's reader/writer declarations inside `try (...)`, even when an exception occurs |
| Type inspection | Branch according to an object's runtime type | Assigners use `instanceof WeightLossGoal` and `instanceof WeightGainGoal` |
| Casting and numeric division | Convert types and avoid unintended integer truncation | Analyzer casts completed count to float before division; scheduler intentionally uses integer arithmetic for weeks |
| String construction | Build reports without repeatedly replacing a string | `StringBuilder` in history, login parsing, recommendations, and controller report assembly |
| Packages/imports | Organize classes and refer to types from other packages | `goals`, `exercises`, `tracker`, and other packages separate responsibilities |
| Serializable marker interface | Declares eligibility for serialization without adding interface methods | User declares it and serialVersionUID; full serialization remains incomplete because referenced classes do not implement it |
| Object references and shallow copies | Copying a list does not clone its elements | Assignment returns a new list containing the same exercise objects; history also retains references |

### Trace polymorphism in four lines

```java
Exercise exercise = new WeightLossExercise(
        101, "Brisk Walking", 30, 1, 1, "Low-Impact Cardio", 3.5f);
CalorieCalculator<Exercise> calculator = new CalorieCalculator<>();
float calories = calculator.calculateCalories(exercise);
```

The declared reference type is `Exercise`. The actual object is `WeightLossExercise`. The overridden cardio method runs and returns 264.0. Replacing the object with a WeightGainExercise would execute the strength calculation without changing the calculator's call.

### Trace overloading separately

```java
controller.rescheduleMissedExercises(user, 3, 1.5f);
controller.rescheduleMissedExercises(user, workout, 3, 1.5f);
```

The first reads previously recorded history. The second partitions the supplied workout, appends history, and logs missed calories. This is overload selection by argument list, not overriding.

## 3. Worked trace of a fresh demo

A “fresh demo” means no `users.txt` in the process working directory. Follow the README's temporary-directory run instructions.

| Step | What happens | Result to explain |
| --- | --- | --- |
| 1 | Construct loss goal: 80 to 70 kg, 60 days, rate 1 | Required loss/week = 10 / (60/7) ≈ 1.167; below the code's 1.5 threshold, so true |
| 2 | Try registration with age -5 | Constructor throws before assigning an ID or writing a record; Main catches it |
| 3 | Register valid profile: 65 kg, 168 cm, age 22 | New object gets ID 1001; a ten-field record is written; count becomes 1 |
| 4 | Authenticate Sai Saranya | File fields reconstruct a new User with ID 1002; count becomes 2; runtime history is fresh |
| 5 | Assign exercises | Non-cardiac loss path chooses all five regular cardio exercises |
| 6 | Create initial schedule | Nine rounded-up weeks × five days = 45 possible slots; five exercises create five sessions |
| 7 | Build demonstration session | Main puts all five exercises in a separate Workout 101 |
| 8 | Complete Brisk Walking | Flag becomes true; controller logs calories; Main logs into user tracker too |
| 9 | Inspect demonstration session | One completed and four missed; history stores both lists |
| 10 | Reschedule with three days × 1.5 hours | 90 minutes/day; print three missed exercises on day 1 and one on day 2 |
| 11 | Generate recommendations | 264 burned, 1134 missed, 20% completion, zero full-session streak |

### Numbers you can calculate by hand

```text
BMR = 10*65 + 6.25*168 - 5*22 + 5 = 1595
Activity target = 1595*(1.55 - 1) + 1.5*200 = 1177.25
Loss-rate target = 1*7700/7 = 1100
Stored target = max(1177.25, 1100) = 1177.25
Printed target rounded to one decimal = 1177.3

Brisk Walking calories = 30*(6 + 3.5*0.8) = 264
Remaining target = 1177.25 - 264 = 913.25, printed as 913.3

Missed calories:
Jogging = 25*(6 + 6.5*0.8) = 280
Cycling = 40*(6 + 4.5*0.8) = 384
Jumping Jacks = 15*(6 + 8.5*0.8) + 3*30*0.2 = 210
Stair Climbing = 20*(6 + 7.5*0.8) + 4*25*0.2 = 260
Total missed = 280 + 384 + 210 + 260 = 1134

Recovery day 1 = 25 + 5 + 40 + 5 + 15 = 90 minutes
Recovery day 2 = 20 minutes
Completion = 1/(1+4)*100 = 20%
Streak = 0 because the latest session has missed exercises
```

The saved goal does not retain its original 80 kg current weight. Authentication rebuilds it using the user's 65 kg weight and the 70 kg target, so the rebuilt loss goal is unrealistic. The demo's scheduler also still uses the original local goal object. This is an excellent example of why persistence schemas and consistent input data matter.

## 4. Viva questions and model answers

### 1. What is the difference between HLD and LLD?

HLD explains the modules and how they collaborate. LLD explains classes, methods, data, algorithms, and error paths. In this project, “controller uses scheduler” is HLD; the quotient/remainder distribution inside `getWorkoutSchedule` is LLD.

### 2. Why use an abstract class for Exercise?

Exercises share fields such as ID, duration, sets, and completion status, but cardio and strength calculations differ. The abstract class stores common state and requires each concrete subtype to implement the calculations and display behavior.

### 3. Why is ExerciseAssigner an interface?

It defines the operations all assignment implementations must support. Regular and additional assigners follow the same contract. The controller stores an interface reference, though its constructor currently selects only the regular implementation.

### 4. Where is runtime polymorphism demonstrated?

In the calculator's call to `calculateCaloriesBurned`. The actual exercise subtype supplies the implementation. Assigners also implement a shared interface. Avoid using overloaded methods as the runtime-polymorphism example.

### 5. How are overriding and overloading different?

Overriding replaces inherited behavior with the same signature; the actual object chooses the method at runtime. Overloading uses the same name with different parameter lists; the compiler selects the matching signature. Exercise subclasses override; Controller rescheduling overloads.

### 6. Does Java support multiple inheritance here?

A class extends only one class but can implement multiple interfaces. This project uses single class inheritance and interface implementation, not multiple inheritance of class state.

### 7. What do this and super do in these constructors?

`this(...)` delegates to another constructor of the same class. `super(...)` initializes the parent-class portion. User uses the first; concrete goals/exercises use the second.

### 8. Why declare variables as List rather than ArrayList?

List specifies the collection operations the caller needs. ArrayList is the selected implementation. Using the interface type keeps callers less dependent on one concrete collection implementation.

### 9. What does List<List<Exercise>> mean?

It is a list of sessions, each containing a list of exercises. The completed and missed history lists use matching outer indexes to describe the same session.

### 10. Does new ArrayList<>(oldList) copy every exercise?

It copies the container and its references. It does not clone exercise objects. Removing an element from the copy does not remove it from the original list, but changing an exercise field affects shared references.

### 11. What is the benefit of T extends Exercise?

It restricts the calculator's generic type to Exercise or a subclass. That allows it to invoke exercise methods safely and reject unrelated types at compile time.

### 12. What is Function<E, Float>?

It is a functional interface accepting E and producing Float. The lambda calls the exercise's calorie method; `apply` executes it. This wrapper is not required for the formula, but demonstrates a lambda and functional interface.

### 13. Why are these exceptions checked?

Their base extends Exception rather than RuntimeException. A caller must catch them or declare them in its throws clause. This makes anticipated input and lookup failures explicit in method signatures.

### 14. What is the difference between throw, throws, and catch?

`throw` raises an exception object, `throws` declares possible checked failures, and `catch` handles a raised exception. User throws an invalid-age exception; its constructor declares it; Main catches it.

### 15. Are all goal errors caught by InvalidGoalException?

No. User throws it for a null goal reference. Numeric realism is a separate boolean check in the goal classes, and constructors do not automatically reject unrealistic goals.

### 16. Why use try-with-resources?

FileReader and FileWriter implement the required closeable contract. Declaring them in `try (...)` closes them automatically, including exceptional exits, instead of depending on a manual close at the end.

### 17. Do we use BufferedReader and BufferedWriter?

The current Login class uses FileReader/FileWriter directly. Some Main strings and comments mention buffered classes, but those wrappers are not present in the implementation. Explain what the source actually does.

### 18. How does authentication work?

It reads users.txt, matches a name ignoring case, parses its fields, reconstructs a goal/User, and increments a counter. There is no password verification. It is a profile lookup demonstration.

### 19. Why is the login count 2 after one registration and login?

The code increments a static counter on both successful registration and authentication. It counts those operations in the current JVM, not unique users or durable login events.

### 20. What happens on the second demo run?

If it uses the same working directory, duplicate registration fails and Main returns early. A new temporary working directory supplies a fresh file without deleting existing data.

### 21. Is user ID stable across authentication or program restarts?

No. The ID is generated from a static in-memory counter and is not written into the record. Authentication creates another User, so it gets another ID; a new JVM starts the counter again.

### 22. How many workout days are created for the sample?

There are 45 possible days from nine weeks and five days/week, but the code creates only five sessions because there are five exercises. Each exercise appears once. No recurring calendar is generated.

### 23. Why is the completion rate 20%, not 100%?

Main's demonstration session contains five exercises. It completes one, so the recorded history is one completed and four missed. The initial plan's one-exercise sessions are separate objects/lists from that demo session.

### 24. Why is the streak zero despite completing an exercise?

The streak requires a session with at least one completed entry and no missed entries. The latest recorded session contains missed exercises, so the reverse scan stops immediately.

### 25. How does the rescheduler enforce time limits?

The printing method considers each exercise in order, adding a five-minute break after the first. If adding the next exercise exceeds available minutes, it moves to the next day without advancing that exercise. It does not split or reorder exercises.

### 26. What if an exercise is longer than every available day?

The printing method cannot place it and leaves it pending, which can block smaller exercises behind it. The object-returning recovery method behaves differently: it allows an oversized first exercise. That inconsistency is a future fix.

### 27. Are rescheduled workouts added to the original plan?

No. The controller calls the printing path. A separate method can return workout objects, but Main does not use it and no automatic plan update occurs.

### 28. Why are there two calorie trackers?

User owns one for personalized state; Controller creates another. Completion logs to Controller's tracker, and Main explicitly logs to User's tracker. Recommendations normally read the user's tracker. Consolidating ownership would simplify the design.

### 29. Does the calorie log really represent days?

There are no dates. Each burn call adds an entry, and the remaining target uses the latest entry. Multiple burns in one day are not summed by date. The field name is more ambitious than its current behavior.

### 30. Does Serializable mean our object persistence is complete?

No. User implements the marker interface, but its goal/history/tracker classes do not. Ordinary serialization of its non-null object graph would fail. Login saves selected text fields using character streams, not ObjectOutputStream.

### 31. Are recommendations AI-generated?

No. They are strings selected by if/else thresholds, based on calorie state, streak, history, cardiac flag, and missed sessions. The logic is deterministic.

### 32. Does the cardiac flag throw CardiacRiskException?

No. Assigners filter exercises by intensity. The exception class is defined but never thrown. BP adjustment methods also exist without being called automatically in this workflow.

### 33. Are repeated operations safe from duplicate accounting?

No. Completing an already-complete exercise still logs another burn; recording the same session again adds another history entry and missed total. An improvement would assign stable session IDs and prevent duplicate updates.

### 34. What is the role of StringBuilder?

It collects report fragments or file characters in a mutable buffer. Controller/history/recommendations call append repeatedly, then convert the result to a String.

### 35. Why cast before computing completion percentage?

Without the float cast, integer division could turn one divided by five into zero before multiplication. `((float) completedCount / total) * 100` yields 20.0.

### 36. What would you improve first?

A practical first improvement is consistent state ownership: route completion and history updates through one user-aware controller operation, make them idempotent, and add tests. Then improve persistence and align the two recovery methods' time-limit behavior. These are proposals, not current features.

## 5. Suggested revision order

1. **First 10 minutes:** read README, practise the one-minute explanation, and identify each package in the HLD.
2. **Next 15 minutes:** follow Main through login, assignment, completion, recovery, and report generation. Calculate the sample numbers by hand.
3. **Next 15 minutes:** open Exercise, HealthGoal, ExerciseAssigner, User, and CalorieCalculator. Explain abstraction, overriding, interface implementation, encapsulation, and generics using their code.
4. **Next 10 minutes:** practise exception/file-I/O questions and explain where data is stored and which state is lost.
5. **Final 10 minutes:** explain limitations and one concrete improvement without claiming it already exists.

Each team member should understand the full flow, even if they wrote only one module.

## 6. Hands-on learning exercises

Use a separate practice branch for code changes; these are exercises, not modifications made by this documentation branch.

| Exercise | Before running, predict | What to inspect |
| --- | --- | --- |
| Set cardiac flag true for a loss profile in a small harness | Walking and cycling pass regular threshold; other regular loss exercises are filtered | RegularExerciseAssigner |
| Create a gain goal and call regular assignment | Five gain exercises without cardiac filtering; only rows/shoulder press with cardiac flag | ExerciseRepository and RegularExerciseAssigner |
| Call AdditionalExerciseAssigner directly | Four supplemental loss exercises normally; three with cardiac flag | Assignment interface implementation |
| Add a history record with only completed exercises | New streak depends on consecutive fully completed records | WorkoutPerformanceAnalyzer |
| Log burns of 100 and 150 | Total becomes 250; remaining target uses 150 only; trend increases by 50 | CalorieTracker and analyzer |
| Give a recovery exercise 100 minutes with a 90-minute slot | Print path leaves it pending; returned-workout path can exceed capacity | Both rescheduler methods |
| Call completion twice for one ID | Calories are counted twice in controller tracker | Controller.completeExercise |

## 7. Final readiness checklist

- Can you locate the method implementing each feature in the README inventory?
- Can you distinguish an Exercise from a Workout, a WorkoutPlan, and a WorkoutHistory?
- Can you trace the sample output without relying only on memorized definitions?
- Can you explain which functions Main never calls?
- Can you explain shallow copies, two tracker objects, and text persistence accurately?
- Can you explain why a diagram's arrows represent use/references rather than network calls?
- Can you describe one improvement and the exact class where it belongs?

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ExerciseData
 * This class stores a set of predefined exercises for users to select when creating training plans.
 */
public class ExerciseData {

    private static final List<Exercise> DEFAULT_EXERCISES = new ArrayList<>();
    private static int nextId = 1; // Used for auto-generating IDs

    // Static initializer block to populate default exercise data
    static {
        // Chest exercises
        addExercise("Dumbbell Bench Press", BodyPart.CHEST, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("Barbell Bench Press", BodyPart.CHEST, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("Dumbbell Fly", BodyPart.CHEST, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("Butterfly Machine", BodyPart.CHEST, Exercise.ExerciseType.MACHINE);
    }

    private static void addExercise(String name, BodyPart bodyPart, Exercise.ExerciseType type) {
        DEFAULT_EXERCISES.add(new Exercise(name, bodyPart, type));
    }

    public static List<Exercise> getExercisesByBodyPart(BodyPart bodyPart) {
        return DEFAULT_EXERCISES.stream()
                .filter(exercise -> exercise.getBodyPart() == bodyPart)
                .collect(Collectors.toList());
    }

    public static List<Exercise> getAllExercises() {
        return new ArrayList<>(DEFAULT_EXERCISES);
    }

    public static List<Exercise> getAllDefaultExercises() {
        return new ArrayList<>(DEFAULT_EXERCISES);
    }
}

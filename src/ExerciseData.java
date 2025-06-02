import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ExerciseData 類別儲存了一組預設的運動項目。
 * 這些運動項目可供使用者在建立訓練計畫時選擇。
 * 每個運動都包含了名稱、目標身體部位以及類型（自由重量或器械）。
 */
public class ExerciseData {

    private static final List<Exercise> DEFAULT_EXERCISES = new ArrayList<>();
    private static int nextId = 1; // 用於自動產生 ID

    static {
        // 胸部 (Chest)
        addExercise("槓鈴臥推", BodyPart.CHEST, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("啞鈴臥推", BodyPart.CHEST, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("啞鈴飛鳥", BodyPart.CHEST, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("蝴蝶機", BodyPart.CHEST, Exercise.ExerciseType.MACHINE);
        addExercise("上斜胸推機", BodyPart.CHEST, Exercise.ExerciseType.MACHINE);

        // 背部 (Back)
        addExercise("槓鈴划船", BodyPart.BACK, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("單臂啞鈴划船", BodyPart.BACK, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("硬舉", BodyPart.BACK, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("坐姿划船機", BodyPart.BACK, Exercise.ExerciseType.MACHINE);
        addExercise("高位下拉機", BodyPart.BACK, Exercise.ExerciseType.MACHINE);
        addExercise("高位划船機", BodyPart.BACK, Exercise.ExerciseType.MACHINE);

        // 腿部 (Legs)
        addExercise("深蹲", BodyPart.LEGS, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("羅馬尼亞硬舉", BodyPart.LEGS, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("弓箭步蹲", BodyPart.LEGS, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("腿推機", BodyPart.LEGS, Exercise.ExerciseType.MACHINE);
        addExercise("腿屈伸機", BodyPart.LEGS, Exercise.ExerciseType.MACHINE);
        addExercise("腿彎舉機", BodyPart.LEGS, Exercise.ExerciseType.MACHINE);

        // 肩部 (Shoulders)
        addExercise("軍事推舉", BodyPart.SHOULDERS, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("阿諾推舉", BodyPart.SHOULDERS, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("側平舉", BodyPart.SHOULDERS, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("前平舉", BodyPart.SHOULDERS, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("後平舉", BodyPart.SHOULDERS, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("史密斯機肩推", BodyPart.SHOULDERS, Exercise.ExerciseType.MACHINE);
        addExercise("蝴蝶機反向飛鳥", BodyPart.SHOULDERS, Exercise.ExerciseType.MACHINE);

        // 手臂 (Arms)
        // 二頭肌 (Biceps)
        addExercise("槓鈴彎舉", BodyPart.ARMS, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("啞鈴彎舉", BodyPart.ARMS, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("錘式彎舉", BodyPart.ARMS, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("二頭肌彎舉機", BodyPart.ARMS, Exercise.ExerciseType.MACHINE);
        // 三頭肌 (Triceps)
        addExercise("窄握臥推", BodyPart.ARMS, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("頭頂三頭肌伸展", BodyPart.ARMS, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("臂屈伸", BodyPart.ARMS, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("三頭肌下拉機", BodyPart.ARMS, Exercise.ExerciseType.MACHINE);
        addExercise("繩索三頭肌下拉", BodyPart.ARMS, Exercise.ExerciseType.MACHINE);

        // 核心 (Core)
        addExercise("俄羅斯轉體", BodyPart.CORE, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("捲腹", BodyPart.CORE, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("側平板支撐", BodyPart.CORE, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("腹部滾輪", BodyPart.CORE, Exercise.ExerciseType.MACHINE);
        addExercise("腹肌訓練器", BodyPart.CORE, Exercise.ExerciseType.MACHINE);
    }

    private static void addExercise(String name, BodyPart bodyPart, Exercise.ExerciseType type) {
        DEFAULT_EXERCISES.add(new Exercise(nextId++, name, bodyPart, type));
    }

    public static List<Exercise> getAllDefaultExercises() {
        return new ArrayList<>(DEFAULT_EXERCISES);
    }

    public static List<Exercise> getAllExercises() {
        return new ArrayList<>(DEFAULT_EXERCISES);
    }

    public static List<Exercise> getExercisesByBodyPart(BodyPart bodyPart) {
        return DEFAULT_EXERCISES.stream()
                .filter(exercise -> exercise.getBodyPart() == bodyPart)
                .collect(Collectors.toList());
    }

    public static List<Exercise> getExercisesByType(Exercise.ExerciseType exerciseType) {
        return DEFAULT_EXERCISES.stream()
                .filter(exercise -> exercise.getType() == exerciseType)
                .collect(Collectors.toList());
    }

    public static Exercise getExerciseById(int id) {
        return DEFAULT_EXERCISES.stream()
                .filter(exercise -> exercise.getId() == id)
                .findFirst()
                .orElse(null);
    }
}

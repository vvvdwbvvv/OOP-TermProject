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

    // 使用靜態初始化區塊來填充預設的運動資料
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
        addExercise("硬舉", BodyPart.BACK, Exercise.ExerciseType.FREE_WEIGHT); // 主要練背，但也訓練全身
        addExercise("坐姿划船機", BodyPart.BACK, Exercise.ExerciseType.MACHINE);
        addExercise("高位下拉機", BodyPart.BACK, Exercise.ExerciseType.MACHINE);
        addExercise("高位划船機", BodyPart.BACK, Exercise.ExerciseType.MACHINE);

        // 腿部 (Legs)
        addExercise("深蹲", BodyPart.LEGS, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("羅馬尼亞硬舉", BodyPart.LEGS, Exercise.ExerciseType.FREE_WEIGHT); // 主要練腿後側與臀部
        addExercise("弓箭步蹲", BodyPart.LEGS, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("腿推機", BodyPart.LEGS, Exercise.ExerciseType.MACHINE);
        addExercise("腿屈伸機", BodyPart.LEGS, Exercise.ExerciseType.MACHINE);
        addExercise("腿彎舉機", BodyPart.LEGS, Exercise.ExerciseType.MACHINE);

        // 肩部 (Shoulders)
        addExercise("軍事推舉", BodyPart.SHOULDERS, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("阿諾推舉", BodyPart.SHOULDERS, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("側平舉", BodyPart.SHOULDERS, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("前平舉", BodyPart.SHOULDERS, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("後平舉", BodyPart.SHOULDERS, Exercise.ExerciseType.FREE_WEIGHT); // 也稱反向飛鳥或俯身側平舉
        addExercise("史密斯機肩推", BodyPart.SHOULDERS, Exercise.ExerciseType.MACHINE);
        addExercise("蝴蝶機反向飛鳥", BodyPart.SHOULDERS, Exercise.ExerciseType.MACHINE); // 器械式的後平舉

        // 手臂 (Arms) - 包含二頭肌與三頭肌
        // 二頭肌 (Biceps)
        addExercise("槓鈴彎舉", BodyPart.ARMS, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("啞鈴彎舉", BodyPart.ARMS, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("錘式彎舉", BodyPart.ARMS, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("二頭肌彎舉機", BodyPart.ARMS, Exercise.ExerciseType.MACHINE);

        // 三頭肌 (Triceps)
        addExercise("窄握臥推", BodyPart.ARMS, Exercise.ExerciseType.FREE_WEIGHT); // 主要練三頭，也練胸
        addExercise("頭頂三頭肌伸展", BodyPart.ARMS, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("臂屈伸", BodyPart.ARMS, Exercise.ExerciseType.FREE_WEIGHT); // Dips
        addExercise("三頭肌下拉機", BodyPart.ARMS, Exercise.ExerciseType.MACHINE);
        addExercise("繩索三頭肌下拉", BodyPart.ARMS, Exercise.ExerciseType.MACHINE);

        // 核心 (Core)
        addExercise("俄羅斯轉體", BodyPart.CORE, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("捲腹", BodyPart.CORE, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("側平板支撐", BodyPart.CORE, Exercise.ExerciseType.FREE_WEIGHT);
        addExercise("腹部滾輪", BodyPart.CORE, Exercise.ExerciseType.MACHINE); // 滾輪視為一種器材輔助
        addExercise("腹肌訓練器", BodyPart.CORE, Exercise.ExerciseType.MACHINE);

        // 你可以根據需要繼續添加更多有氧運動 (CARDIO) 或其他類型的動作
        // addExercise("跑步機", BodyPart.CARDIO, Exercise.ExerciseType.MACHINE);
        // addExercise("開合跳", BodyPart.CARDIO, Exercise.ExerciseType.FREE_WEIGHT);
    }

    /**
     * 私有輔助方法，用於創建 Exercise 物件並添加到列表中，同時自動分配 ID。
     * @param name 運動名稱
     * @param bodyPart 目標身體部位
     * @param type 運動類型
     */
    private static void addExercise(String name, BodyPart bodyPart, Exercise.ExerciseType type) {
        DEFAULT_EXERCISES.add(new Exercise(nextId++, name, bodyPart, type));
    }

    /**
     * 獲取所有預設的運動項目列表。
     * 回傳的是一個列表的副本，以防止外部修改原始列表。
     * @return 包含所有預設運動項目的列表 (List<Exercise>)
     */
    public static List<Exercise> getAllDefaultExercises() {
        return new ArrayList<>(DEFAULT_EXERCISES); // 回傳副本
    }

    /**
     * 根據指定的身體部位獲取預設的運動項目列表。
     * @param bodyPart 要篩選的身體部位 (BodyPart)
     * @return 包含符合條件運動項目的列表 (List<Exercise>)
     */
    public static List<Exercise> getExercisesByBodyPart(BodyPart bodyPart) {
        return DEFAULT_EXERCISES.stream()
                .filter(exercise -> exercise.getBodyPart() == bodyPart)
                .collect(Collectors.toList());
    }

    /**
     * 根據指定的運動類型獲取預設的運動項目列表。
     * @param exerciseType 要篩選的運動類型 (Exercise.ExerciseType)
     * @return 包含符合條件運動項目的列表 (List<Exercise>)
     */
    public static List<Exercise> getExercisesByType(Exercise.ExerciseType exerciseType) {
        return DEFAULT_EXERCISES.stream()
                .filter(exercise -> exercise.getType() == exerciseType)
                .collect(Collectors.toList());
    }

    /**
     * 根據 ID 查找特定的預設運動。
     * @param id 運動的 ID
     * @return 找到的 Exercise 物件，如果找不到則回傳 null
     */
    public static Exercise getExerciseById(int id) {
        return DEFAULT_EXERCISES.stream()
                .filter(exercise -> exercise.getId() == id)
                .findFirst()
                .orElse(null);
    }

    // 主方法僅用於測試目的，可以移除或註解掉
    // public static void main(String[] args) {
    //     System.out.println("所有預設運動項目:");
    //     getAllDefaultExercises().forEach(System.out::println);

    //     System.out.println("\n胸部 (CHEST) 的運動:");
    //     getExercisesByBodyPart(BodyPart.CHEST).forEach(System.out::println);

    //     System.out.println("\n自由重量 (FREE_WEIGHT) 的運動:");
    //     getExercisesByType(Exercise.ExerciseType.FREE_WEIGHT).forEach(System.out::println);

    //     System.out.println("\nID 為 3 的運動:");
    //     System.out.println(getExerciseById(3));
    // }
}

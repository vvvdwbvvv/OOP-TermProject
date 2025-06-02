import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WorkoutExercise {
    private int id;
    private int workoutSessionId;
    private int exerciseId;
    private Exercise exercise; // 關聯的運動項目定義
    private int sets;          // 計畫/實際完成的組數
    private int reps;          // 計畫/實際每組的次數
    private double weight;     // 使用的重量

    // 新增：用於記錄每組之後的休息時間（秒）
    private List<Long> restPeriodsInSeconds;

    public WorkoutExercise() {
        this.restPeriodsInSeconds = new ArrayList<>();
    }

    // ADDED CONSTRUCTOR
    public WorkoutExercise(Exercise exercise, int sets, int reps, double weight) {
        this.restPeriodsInSeconds = new ArrayList<>(); // Initialize rest periods
        this.exercise = exercise;
        if (exercise != null) {
            this.exerciseId = exercise.getId();
        }
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        // Note: 'id' and 'workoutSessionId' are not set here.
        // They are typically set when the WorkoutExercise is saved to the database
        // or associated with a WorkoutSession object that has an ID.
    }

    public WorkoutExercise(int workoutSessionId, int exerciseId, int sets, int reps, double weight) {
        this.workoutSessionId = workoutSessionId;
        this.exerciseId = exerciseId;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.restPeriodsInSeconds = new ArrayList<>();
    }

    public WorkoutExercise(int id, int workoutSessionId, int exerciseId, int sets, int reps, double weight) {
        this.id = id;
        this.workoutSessionId = workoutSessionId;
        this.exerciseId = exerciseId;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.restPeriodsInSeconds = new ArrayList<>();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWorkoutSessionId() {
        return workoutSessionId;
    }

    public void setWorkoutSessionId(int workoutSessionId) {
        this.workoutSessionId = workoutSessionId;
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
        if (exercise != null) {
            this.exerciseId = exercise.getId();
        }
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    // --- 組間休息時間相關方法 ---

    /**
     * 添加一次組間休息的時間記錄。
     * 這個方法應在使用者完成一次休息後被調用。
     * @param seconds 休息的秒數
     */
    public void addRestPeriod(long seconds) {
        if (this.restPeriodsInSeconds == null) { // 防禦性初始化
            this.restPeriodsInSeconds = new ArrayList<>();
        }
        this.restPeriodsInSeconds.add(seconds);
    }

    /**
     * 獲取所有已記錄的組間休息時間列表。
     * @return 一個包含每次休息秒數的列表 (List<Long>)
     */
    public List<Long> getRestPeriodsInSeconds() {
        // 回傳副本以保護內部列表不被外部直接修改
        return this.restPeriodsInSeconds == null ? new ArrayList<>() : new ArrayList<>(this.restPeriodsInSeconds);
    }

    /**
     * 清除所有已記錄的組間休息時間。
     */
    public void clearRestPeriods() {
        if (this.restPeriodsInSeconds != null) {
            this.restPeriodsInSeconds.clear();
        }
    }
    
    @Override
    public String toString() {
        String exerciseName = (exercise != null) ? exercise.getName() : "未知運動";
        StringBuilder sb = new StringBuilder();
        sb.append(exerciseName).append(": ")
          .append(sets).append(" 組 x ")
          .append(reps).append(" 次 x ")
          .append(weight).append(" 公斤");

        if (restPeriodsInSeconds != null && !restPeriodsInSeconds.isEmpty()) {
            sb.append(" (組間休息: ");
            sb.append(restPeriodsInSeconds.stream().map(String::valueOf).collect(Collectors.joining("s, ")));
            sb.append("s)");
        }
        return sb.toString();
    }
}

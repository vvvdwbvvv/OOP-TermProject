import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WeeklyPlan 類別用於規劃一週的訓練內容。
 * 現在它可以記錄每一天中，針對特定身體部位要訓練的具體運動項目。
 */
public class WeeklyPlan {
    // 資料結構更新：
    // 最外層 Map: DayOfWeek -> 該日的訓練計畫
    // 中間層 Map: BodyPart -> 該身體部位要進行的運動列表
    // 最內層 List: Exercise -> 具體的運動項目
    private Map<DayOfWeek, Map<BodyPart, List<Exercise>>> plan;

    /**
     * 建構函式，初始化一週的訓練計畫。
     * 為每一天都建立一個空的訓練計畫結構。
     */
    public WeeklyPlan() {
        this.plan = new HashMap<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            // 為每一天初始化一個新的 HashMap，用於儲存 BodyPart -> List<Exercise> 的對應關係
            plan.put(day, new HashMap<>());
        }
    }

    /**
     * 為指定日期的特定身體部位添加一個運動計畫。
     *
     * @param day      星期幾 (DayOfWeek)
     * @param part     要訓練的身體部位 (BodyPart)
     * @param exercise 要添加的運動項目 (Exercise)
     */
    public void addExerciseToPlan(DayOfWeek day, BodyPart part, Exercise exercise) {
        // 1. 取得或創建當天的身體部位訓練計畫 Map
        //    plan.get(day) 理應永不為 null，因為建構函式已初始化
        Map<BodyPart, List<Exercise>> exercisesForDay = plan.get(day);

        // 2. 取得或創建該身體部位的運動列表
        //    使用 computeIfAbsent 可以簡化代碼：如果 'part' 不存在，則創建一個新的 ArrayList 並放入 Map
        List<Exercise> exercisesForBodyPart = exercisesForDay.computeIfAbsent(part, k -> new ArrayList<>());

        // 3. 添加運動到列表中 (可選擇是否檢查重複)
        if (!exercisesForBodyPart.contains(exercise)) { // 避免重複添加相同的運動實例
            exercisesForBodyPart.add(exercise);
        }
    }

    /**
     * 取得指定日期特定身體部位的計畫運動列表。
     *
     * @param day  星期幾 (DayOfWeek)
     * @param part 要查詢的身體部位 (BodyPart)
     * @return 包含該身體部位計畫運動的列表 (List<Exercise>)。
     * 如果當天或該部位沒有計畫，則回傳一個空的列表。
     */
    public List<Exercise> getExercisesForDayAndPart(DayOfWeek day, BodyPart part) {
        Map<BodyPart, List<Exercise>> exercisesForDay = plan.get(day);
        if (exercisesForDay != null) {
            // 若 exercisesForDay.get(part) 為 null (即該部位無計畫)，則回傳一個新的空 ArrayList
            return exercisesForDay.getOrDefault(part, new ArrayList<>());
        }
        // 如果連當天的計畫都沒有 (理論上不會發生，除非 plan 被外部設為 null)，也回傳空列表
        return new ArrayList<>();
    }

    /**
     * 取得指定日期的完整訓練計畫。
     *
     * @param day 星期幾 (DayOfWeek)
     * @return 一個 Map，鍵為 BodyPart，值為對應的 Exercise 列表。
     * 如果當天沒有計畫，則回傳一個空的 Map。
     */
    public Map<BodyPart, List<Exercise>> getPlanForDay(DayOfWeek day) {
        return plan.getOrDefault(day, new HashMap<>());
    }


    /**
     * 取得完整的週計畫。
     *
     * @return 包含完整週計畫的 Map。
     */
    public Map<DayOfWeek, Map<BodyPart, List<Exercise>>> getPlan() {
        return plan;
    }

    /**
     * 設定完整的週計畫。
     * 通常用於從外部來源（如檔案或資料庫）載入計畫。
     *
     * @param plan 包含完整週計畫的 Map。
     */
    public void setPlan(Map<DayOfWeek, Map<BodyPart, List<Exercise>>> plan) {
        this.plan = plan;
    }

    /**
     * 清除指定日期特定身體部位的所有計畫運動。
     * @param day 星期幾
     * @param part 身體部位
     */
    public void clearExercisesForDayAndPart(DayOfWeek day, BodyPart part) {
        Map<BodyPart, List<Exercise>> exercisesForDay = plan.get(day);
        if (exercisesForDay != null) {
            List<Exercise> exercisesForBodyPart = exercisesForDay.get(part);
            if (exercisesForBodyPart != null) {
                exercisesForBodyPart.clear();
            }
        }
    }

    /**
     * 清除指定日期的所有訓練計畫。
     * @param day 星期幾
     */
    public void clearPlanForDay(DayOfWeek day) {
        Map<BodyPart, List<Exercise>> exercisesForDay = plan.get(day);
        if (exercisesForDay != null) {
            exercisesForDay.clear();
        }
    }

    /**
     * 移除指定日期特定身體部位的特定運動。
     * @param day 星期幾
     * @param part 身體部位
     * @param exercise 要移除的運動
     * @return 如果成功移除返回 true，否則 false
     */
    public boolean removeExerciseFromPlan(DayOfWeek day, BodyPart part, Exercise exercise) {
        Map<BodyPart, List<Exercise>> exercisesForDay = plan.get(day);
        if (exercisesForDay != null) {
            List<Exercise> exercisesForBodyPart = exercisesForDay.get(part);
            if (exercisesForBodyPart != null) {
                return exercisesForBodyPart.remove(exercise);
            }
        }
        return false;
    }
}

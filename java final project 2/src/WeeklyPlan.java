import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeeklyPlan {
    private Map<DayOfWeek, List<BodyPart>> plan;

    public WeeklyPlan() {
        this.plan = new HashMap<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            plan.put(day, new ArrayList<>());
        }
    }

    public void addBodyPart(DayOfWeek day, BodyPart part) {
        plan.get(day).add(part);
    }

    // Getters 和 Setters
    public Map<DayOfWeek, List<BodyPart>> getPlan() { return plan; }
    public void setPlan(Map<DayOfWeek, List<BodyPart>> plan) { this.plan = plan; }
}
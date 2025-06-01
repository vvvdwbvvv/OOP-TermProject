import java.time.LocalDateTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class WorkoutSession {
    private int id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<BodyPart> bodyParts;
    private List<WorkoutExercise> exercises;

    public WorkoutSession() {
        this.exercises = new ArrayList<>();
    }
    
    public WorkoutSession(LocalDateTime startTime, LocalDateTime endTime, List<BodyPart> bodyParts) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.bodyParts = bodyParts;
        this.exercises = new ArrayList<>();
    }
    
    public WorkoutSession(int id, LocalDateTime startTime, LocalDateTime endTime, List<BodyPart> bodyParts) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bodyParts = bodyParts;
        this.exercises = new ArrayList<>();
    }

    public Duration getDuration() {
        return Duration.between(startTime, endTime);
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public List<BodyPart> getBodyParts() { return bodyParts; }
    public void setBodyParts(List<BodyPart> bodyParts) { this.bodyParts = bodyParts; }
    public List<WorkoutExercise> getExercises() { return exercises; }
    public void setExercises(List<WorkoutExercise> exercises) { this.exercises = exercises; }
    
    public void addExercise(WorkoutExercise exercise) {
        this.exercises.add(exercise);
    }
    
    public String getBodyPartsString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bodyParts.size(); i++) {
            sb.append(bodyParts.get(i));
            if (i < bodyParts.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
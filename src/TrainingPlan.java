import java.time.DayOfWeek;

public class TrainingPlan {
	private DayOfWeek dayOfWeek;
	private String bodyPart;
	private String exerciseName;

	public TrainingPlan(DayOfWeek dayOfWeek, String bodyPart, String exerciseName) {
		this.dayOfWeek = dayOfWeek;
		this.bodyPart = bodyPart;
		this.exerciseName = exerciseName;
	}

	public DayOfWeek getDayOfWeek() {
		return dayOfWeek;
	}

	public String getBodyPart() {
		return bodyPart;
	}

	public String getExerciseName() {
		return exerciseName;
	}
}

public class WorkoutExercise {
    private int id;
    private int workoutSessionId;
    private int exerciseId;
    private Exercise exercise;
    private int sets;
    private int reps;
    private double weight;
    
    public WorkoutExercise() {
    }
    
    public WorkoutExercise(int workoutSessionId, int exerciseId, int sets, int reps, double weight) {
        this.workoutSessionId = workoutSessionId;
        this.exerciseId = exerciseId;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
    }
    
    public WorkoutExercise(int id, int workoutSessionId, int exerciseId, int sets, int reps, double weight) {
        this.id = id;
        this.workoutSessionId = workoutSessionId;
        this.exerciseId = exerciseId;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
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
    
    @Override
    public String toString() {
        String exerciseName = (exercise != null) ? exercise.getName() : "Unknown Exercise";
        return exerciseName + ": " + sets + " 組 x " + reps + " 次 x " + weight + " 公斤";
    }
}

public class Exercise {
    private int id;
    private String name;
    private BodyPart bodyPart;
    private ExerciseType type;
    
    public enum ExerciseType {
        FREE_WEIGHT, MACHINE;
        
        @Override
        public String toString() {
            return this == FREE_WEIGHT ? "自由重量" : "器械";
        }
    }
    
    public Exercise() {
    }
    
    public Exercise(String name, BodyPart bodyPart, ExerciseType type) {
        this.name = name;
        this.bodyPart = bodyPart;
        this.type = type;
    }
    
    public Exercise(int id, String name, BodyPart bodyPart, ExerciseType type) {
        this.id = id;
        this.name = name;
        this.bodyPart = bodyPart;
        this.type = type;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public BodyPart getBodyPart() {
        return bodyPart;
    }
    
    public void setBodyPart(BodyPart bodyPart) {
        this.bodyPart = bodyPart;
    }
    
    public ExerciseType getType() {
        return type;
    }
    
    public void setType(ExerciseType type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        return name + " (" + type + ")";
    }
}

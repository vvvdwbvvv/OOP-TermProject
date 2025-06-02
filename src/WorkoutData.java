import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class WorkoutData {
	private static final List<WorkoutExercise> workoutExercises = new ArrayList<>();

    public static void addWorkoutExercise(WorkoutExercise we) {
        workoutExercises.add(we);
    }

    public static List<WorkoutExercise> getWorkoutExercises() {
        return new ArrayList<>(workoutExercises);
    }

    public static void clearWorkoutExercises() {
        workoutExercises.clear();
    }
    
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String muscleGroup;
    private int cardioTime;

    public WorkoutData(LocalDateTime startTime, LocalDateTime endTime, String muscleGroup, int cardioTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.muscleGroup = muscleGroup;
        this.cardioTime = cardioTime;
    }

    public int getTotalDuration() {
        return (int) Duration.between(startTime, endTime).toMinutes();
    }

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public int getCardioTime() {
        return cardioTime;
    }

    public static List<WorkoutData> fetchRecentData() {
        List<WorkoutData> dataList = new ArrayList<>();
        try {
            String url = "jdbc:mysql://140.119.19.73:3315/TG10?useSSL=false";
            String user = "TG10";
            String password = "iRIzsI";

            Connection conn = DriverManager.getConnection(url, user, password);

            // 注意這裡只從 workout_sessions 撈資料，muscle group 直接來自 body_parts_trained 欄位
            String query =  """
                    SELECT start_time, end_time, body_parts_trained, cardio_time
                    FROM workout_sessions
                    ORDER BY start_time DESC
                    LIMIT 50
                """;

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                LocalDateTime start = rs.getTimestamp("start_time").toLocalDateTime();
                LocalDateTime end = rs.getTimestamp("end_time").toLocalDateTime();
                String group = rs.getString("body_parts_trained");
                int cardio = 0; // 如果未來你有要統計 cardios 可再補邏輯

                dataList.add(new WorkoutData(start, end, group, cardio));
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dataList;
    }
}

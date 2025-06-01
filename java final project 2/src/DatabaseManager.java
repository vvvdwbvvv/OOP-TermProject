import java.io.IOException; // 新增
import java.io.InputStream; // 新增
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties; // 新增

public class DatabaseManager {

    private static final String PROPERTIES_FILE = "db.properties"; // 設定檔名稱
    private static String dbUrl;
    private static String dbUsername;
    private static String dbPassword;
    // private static String dbDriver; // 如果您也想從設定檔讀取驅動程式名稱

    // 靜態初始化區塊，用於載入設定檔和JDBC驅動程式
    static {
        loadProperties(); // 載入設定檔
        try {
            // 如果您也從設定檔讀取驅動程式名稱，可以使用 Class.forName(dbDriver);
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("MySQL JDBC Driver not found!", e);
        }
    }

    // 新增方法：載入設定檔
    private static void loadProperties() {
        Properties props = new Properties();
        // 使用 ClassLoader 從 classpath 讀取檔案，這樣無論檔案在 src 或打包後的 jar 中都能找到
        try (InputStream input = DatabaseManager.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                System.err.println("Sorry, unable to find " + PROPERTIES_FILE);
                // 考慮拋出一個 RuntimeException 或者使用預設值
                throw new IllegalStateException("Database properties file '" + PROPERTIES_FILE + "' not found in classpath.");
            }
            props.load(input);

            dbUrl = props.getProperty("db.url");
            dbUsername = props.getProperty("db.username");
            dbPassword = props.getProperty("db.password");
            // dbDriver = props.getProperty("db.driver"); // 如果有設定

            if (dbUrl == null || dbUsername == null || dbPassword == null) {
                throw new IllegalStateException("Missing one or more required properties in " + PROPERTIES_FILE + " (db.url, db.username, db.password)");
            }

        } catch (IOException ex) {
            System.err.println("Error loading database properties: " + ex.getMessage());
            ex.printStackTrace();
            // 考慮拋出一個 RuntimeException
            throw new RuntimeException("Error loading database properties.", ex);
        }
    }

    public static Connection getConnection() throws SQLException {
        // 使用從設定檔載入的變數
        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }

    // ... (createTables, populateDefaultExercises, addExerciseToDB 等其他方法保持不變)
    // 您原有的 createTables, populateDefaultExercises, getExerciseFromDBByNameAndBodyPart,
    // addExerciseToDB, getExerciseFromDB, getAllExercisesFromDB 方法都不需要修改它們的內部邏輯，
    // 因為它們都依賴 getConnection() 方法，而 getConnection() 現在會使用設定檔中的資訊。

    // 以下是您原有的部分方法，它們不需要修改，因為它們呼叫的 getConnection() 已經更新了：
    public void createTables() {
        String createExercisesTableSQL = "CREATE TABLE IF NOT EXISTS exercises ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "name VARCHAR(255) NOT NULL,"
                + "body_part VARCHAR(50) NOT NULL,"
                + "type VARCHAR(50) NOT NULL"
                + ");";

        String createWorkoutSessionsTableSQL = "CREATE TABLE IF NOT EXISTS workout_sessions ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "start_time DATETIME NOT NULL,"
                + "end_time DATETIME,"
                + "body_parts_trained VARCHAR(255)"
                + ");";

        String createWorkoutExercisesTableSQL = "CREATE TABLE IF NOT EXISTS workout_exercises ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "workout_session_id INT NOT NULL,"
                + "exercise_id INT NOT NULL,"
                + "sets INT NOT NULL,"
                + "reps INT NOT NULL,"
                + "weight DOUBLE,"
                + "rest_periods_seconds VARCHAR(255),"
                + "FOREIGN KEY (workout_session_id) REFERENCES workout_sessions(id) ON DELETE CASCADE,"
                + "FOREIGN KEY (exercise_id) REFERENCES exercises(id) ON DELETE CASCADE"
                + ");";

        try (Connection conn = getConnection(); // getConnection() 現在會使用設定檔的資訊
             Statement stmt = conn.createStatement()) {
            
            System.out.println("Creating table 'exercises' if it does not exist...");
            stmt.executeUpdate(createExercisesTableSQL);
            System.out.println("Table 'exercises' created or already exists.");

            System.out.println("Creating table 'workout_sessions' if it does not exist...");
            stmt.executeUpdate(createWorkoutSessionsTableSQL);
            System.out.println("Table 'workout_sessions' created or already exists.");

            System.out.println("Creating table 'workout_exercises' if it does not exist...");
            stmt.executeUpdate(createWorkoutExercisesTableSQL);
            System.out.println("Table 'workout_exercises' created or already exists.");

        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void populateDefaultExercises() {
        List<Exercise> defaultExercises = ExerciseData.getAllDefaultExercises(); //
        String sql = "INSERT INTO exercises (name, body_part, type) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE name=name;";

        try (Connection conn = getConnection(); // getConnection() 現在會使用設定檔的資訊
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            for (Exercise ex : defaultExercises) {
                if (getExerciseFromDBByNameAndBodyPart(ex.getName(), ex.getBodyPart().name()) == null) {
                    pstmt.setString(1, ex.getName());
                    pstmt.setString(2, ex.getBodyPart().name());
                    pstmt.setString(3, ex.getType().name());
                    pstmt.addBatch();
                }
            }
            int[] results = pstmt.executeBatch();
            System.out.println("Populated " + results.length + " default exercises into the database.");

        } catch (SQLException e) {
            System.err.println("Error populating default exercises: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public Exercise getExerciseFromDBByNameAndBodyPart(String name, String bodyPart) {
        String sql = "SELECT id, name, body_part, type FROM exercises WHERE name = ? AND body_part = ?";
        try (Connection conn = getConnection(); // getConnection() 現在會使用設定檔的資訊
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, bodyPart);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Exercise(
                    rs.getInt("id"),
                    rs.getString("name"),
                    BodyPart.valueOf(rs.getString("body_part")), //
                    Exercise.ExerciseType.valueOf(rs.getString("type")) //
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching exercise by name and body part: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public int addExerciseToDB(Exercise exercise) { //
        String sql = "INSERT INTO exercises (name, body_part, type) VALUES (?, ?, ?)";
        try (Connection conn = getConnection(); // getConnection() 現在會使用設定檔的資訊
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, exercise.getName());
            pstmt.setString(2, exercise.getBodyPart().name());
            pstmt.setString(3, exercise.getType().name());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        exercise.setId(generatedKeys.getInt(1));
                        return exercise.getId();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding exercise: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public Exercise getExerciseFromDB(int id) { //
        String sql = "SELECT id, name, body_part, type FROM exercises WHERE id = ?";
        try (Connection conn = getConnection(); // getConnection() 現在會使用設定檔的資訊
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Exercise(
                    rs.getInt("id"),
                    rs.getString("name"),
                    BodyPart.valueOf(rs.getString("body_part")), //
                    Exercise.ExerciseType.valueOf(rs.getString("type")) //
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching exercise: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Exercise> getAllExercisesFromDB() { //
        List<Exercise> exercises = new ArrayList<>(); //
        String sql = "SELECT id, name, body_part, type FROM exercises";
        try (Connection conn = getConnection(); // getConnection() 現在會使用設定檔的資訊
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                exercises.add(new Exercise( //
                    rs.getInt("id"),
                    rs.getString("name"),
                    BodyPart.valueOf(rs.getString("body_part")), //
                    Exercise.ExerciseType.valueOf(rs.getString("type")) //
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all exercises: " + e.getMessage());
            e.printStackTrace();
        }
        return exercises;
    }
}
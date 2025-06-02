import java.sql.*;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class TrainingPlanDAO {
	public static List<TrainingPlan> getWeeklyPlansForDay(DayOfWeek day) {
		List<TrainingPlan> plans = new ArrayList<>();

		String url = "jdbc:mysql://140.119.19.73:3315/TG10?useSSL=false&serverTimezone=UTC";
		String user = "TG10";
		String password = "iRIzsI";

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");

			String sql = "SELECT day_of_week, body_part, exercise_name FROM weekly_plan WHERE day_of_week = ?";
			try (Connection conn = DriverManager.getConnection(url, user, password);
					PreparedStatement ps = conn.prepareStatement(sql)) {

				ps.setString(1, day.toString());

				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						DayOfWeek resultDay = DayOfWeek.valueOf(rs.getString("day_of_week").toUpperCase());
						String bodyPart = rs.getString("body_part");
						String exerciseName = rs.getString("exercise_name");
						plans.add(new TrainingPlan(resultDay, bodyPart, exerciseName));
					}
				}
			}
		} catch (ClassNotFoundException e) {
			System.err.println(
					"MySQL Driver not found! Please check if mysql-connector-java.jar is added to the classpath");
			e.printStackTrace();
		} catch (SQLException e) {
			System.err.println("Database connection failed! Error message: " + e.getMessage());
			e.printStackTrace();
		}
		return plans;
	}
}

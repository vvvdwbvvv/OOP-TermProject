import javax.swing.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.awt.*;

public class TrainingReminderGUI {

	public TrainingReminderGUI() {
		// 判斷是否要顯示提醒
		if (ReminderStatus.isTodayDismissed()) {
			return; // 今日已選擇不再顯示
		}
		if (!ReminderStatus.isSnoozed()) {
			ReminderStatus.clearSnooze();
		} else {
		}

		// 取得今天星期幾
		LocalDate today = LocalDate.now();
		DayOfWeek day = today.getDayOfWeek();

		// 從資料庫取
		String bodyPart = "";
		String exerciseName = "";
		try {
			List<TrainingPlan> plans = TrainingPlanDAO.getWeeklyPlansForDay(day);
			for (TrainingPlan plan : plans) {
				if (plan.getDayOfWeek() == day) {
					bodyPart = plan.getBodyPart();
					exerciseName = plan.getExerciseName();
					break;
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage());
		}

		showTrainingReminder(day, bodyPart, exerciseName);

	}

	private void showTrainingReminder(DayOfWeek day, String bodyPart, String exerciseName) {
		JDialog dialog = new JDialog((java.awt.Frame) null, "Today's Training Reminder", true);
		dialog.setLayout(new BorderLayout());

		List<TrainingPlan> plans = TrainingPlanDAO.getWeeklyPlansForDay(day);

		// 分組
		Map<String, List<String>> partToActions = new LinkedHashMap<>();
		for (TrainingPlan plan : plans) {
			partToActions.computeIfAbsent(plan.getBodyPart(), k -> new ArrayList<>()).add(plan.getExerciseName());
		}

		StringBuilder sb = new StringBuilder();
		sb.append("<html>").append("<div style='width:340px;'>")
				.append("<div style='text-align:center; color:#888888; font-size:20px; margin-bottom:16px;'>")
				.append("💪 Today is ").append(day).append("! 💪</div>")
				.append("<table width='90%' style='font-size:16px; line-height:2; margin-left:40px;'>");

		for (Map.Entry<String, List<String>> entry : partToActions.entrySet()) {
			String part = entry.getKey();
			List<String> actions = entry.getValue();

			sb.append("<tr>").append("<td align='left' style='color:#000; vertical-align:top;'>").append(part)
					.append("</td>")
					.append("<td align='left' style='color:#000; font-weight:bold; vertical-align:top;'>");

			// 每兩個動作換行
			for (int i = 0; i < actions.size(); i++) {
				if (i > 0 && i % 2 == 0)
					sb.append("<br>");
				else if (i > 0)
					sb.append("&nbsp;&nbsp;&nbsp;&nbsp;");
				sb.append(actions.get(i));
			}
			sb.append("</td></tr>");
		}
		sb.append("</table></div></html>");

		JLabel message = new JLabel(sb.toString(), SwingConstants.CENTER);
		message.setFont(new Font("SansSerif", Font.PLAIN, 18));


		JButton snoozeButton = new JButton("稍後提醒");
		snoozeButton.setBackground(new java.awt.Color(186, 200, 218));
		snoozeButton.setFocusPainted(false);
		snoozeButton.addActionListener(e -> {
			ReminderStatus.setSnoozed();
			dialog.dispose();
		});

		JButton dismissButton = new JButton("今日不再顯示");
		dismissButton.setBackground(new java.awt.Color(220, 220, 220));
		dismissButton.setFocusPainted(false);
		dismissButton.addActionListener(e -> {
			ReminderStatus.setTodayDismissed();
			dialog.dispose();
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(snoozeButton);
		buttonPanel.add(dismissButton);

		dialog.add(message, BorderLayout.CENTER);
		dialog.add(buttonPanel, BorderLayout.SOUTH);

		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		dialog.add(buttonPanel, BorderLayout.SOUTH);

		dialog.setSize(450, 250);
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}

	public static void main(String[] args) {
		try {
		    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
		    e.printStackTrace();
		}

		// 每次都重設「今日不再顯示」狀態（測試用）
		ReminderStatus.resetDismissed();
		SwingUtilities.invokeLater(() -> new TrainingReminderGUI());
	}
}

import javax.swing.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		// 設置淺灰色背景
		JPanel mainPanel = new JPanel(new BorderLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g2d.setColor(new Color(248, 249, 250));
				g2d.fillRect(0, 0, getWidth(), getHeight());
			}
		};

		List<TrainingPlan> plans = TrainingPlanDAO.getWeeklyPlansForDay(day);

		// 分組
		Map<String, List<String>> partToActions = new LinkedHashMap<>();
		for (TrainingPlan plan : plans) {
			partToActions.computeIfAbsent(plan.getBodyPart(), k -> new ArrayList<>()).add(plan.getExerciseName());
		}

		// 創建標題面板
		JPanel titlePanel = new JPanel();
		titlePanel.setOpaque(false);
		titlePanel.setBorder(BorderFactory.createEmptyBorder(25, 20, 20, 20));

		JLabel titleLabel = new JLabel("💪 Today is " + day + "! 💪", SwingConstants.CENTER);
		// 設置字體，包含 emoji 支援的回退選項
		Font titleFont;
		try {
			// 嘗試使用支援 emoji 的字體
			String os = System.getProperty("os.name").toLowerCase();
			if (os.contains("mac")) {
				titleFont = new Font("SF Pro Display", Font.BOLD, 20);
			} else if (os.contains("win")) {
				titleFont = new Font("Segoe UI Emoji", Font.BOLD, 20);
			} else {
				titleFont = new Font("Noto Color Emoji", Font.BOLD, 20);
			}
		} catch (Exception e) {
			titleFont = new Font("Microsoft YaHei", Font.BOLD, 20);
		}
		titleLabel.setFont(titleFont);
		titleLabel.setForeground(new Color(102, 102, 102));

		titlePanel.add(titleLabel);

		// 創建內容面板
		JPanel contentPanel = new JPanel();
		contentPanel.setOpaque(false);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

		for (Map.Entry<String, List<String>> entry : partToActions.entrySet()) {
			String part = entry.getKey();
			List<String> actions = entry.getValue();

			// 創建卡片式面板
			JPanel cardPanel = createCardPanel();
			cardPanel.setLayout(new BorderLayout());
			cardPanel.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

			// 部位標籤
			JLabel partLabel = new JLabel(part);
			partLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
			partLabel.setForeground(new Color(51, 51, 51));
			partLabel.setPreferredSize(new Dimension(80, 25));

			// 動作標籤
			StringBuilder actionText = new StringBuilder("<html><div style='line-height:1.5;'>");
			for (int i = 0; i < actions.size(); i++) {
				if (i > 0 && i % 2 == 0) {
					actionText.append("<br>");
				} else if (i > 0) {
					actionText.append("&nbsp;&nbsp;&nbsp;&nbsp;");
				}
				actionText.append(actions.get(i));
			}
			actionText.append("</div></html>");

			JLabel actionLabel = new JLabel(actionText.toString());
			actionLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
			actionLabel.setForeground(new Color(68, 68, 68));

			cardPanel.add(partLabel, BorderLayout.WEST);
			cardPanel.add(actionLabel, BorderLayout.CENTER);

			contentPanel.add(cardPanel);
			contentPanel.add(Box.createVerticalStrut(12)); // 卡片間距
		}

		// 創建按鈕面板
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
		buttonPanel.setOpaque(false);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 25, 20));

		// 稍後提醒按鈕
		JButton snoozeButton = createStyledButton("稍後提醒", new Color(186, 200, 218));
		snoozeButton.addActionListener(e -> {
			ReminderStatus.setSnoozed();
			dialog.dispose();
		});

		// 今日不再顯示按鈕
		JButton dismissButton = createStyledButton("今日不再顯示", new Color(220, 220, 220));
		dismissButton.addActionListener(e -> {
			ReminderStatus.setTodayDismissed();
			dialog.dispose();
		});

		buttonPanel.add(snoozeButton);
		buttonPanel.add(dismissButton);

		// 組裝面板
		mainPanel.add(titlePanel, BorderLayout.NORTH);
		mainPanel.add(contentPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		dialog.add(mainPanel);
		dialog.setSize(450, 380);
		dialog.setLocationRelativeTo(null);
		dialog.setResizable(false);

		// 設置窗口圓角效果
		try {
			dialog.setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, 450, 350, 15, 15));
		} catch (Exception e) {
			// 忽略不支持的系統
		}

		dialog.setVisible(true);
	}

	// 創建卡片式面板
	private JPanel createCardPanel() {
		return new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				// 繪製陰影
				g2d.setColor(new Color(0, 0, 0, 15));
				g2d.fillRoundRect(2, 2, getWidth()-2, getHeight()-2, 12, 12);
				g2d.setColor(new Color(0, 0, 0, 8));
				g2d.fillRoundRect(1, 1, getWidth()-1, getHeight()-1, 12, 12);

				// 繪製白色背景
				g2d.setColor(Color.WHITE);
				g2d.fillRoundRect(0, 0, getWidth()-2, getHeight()-2, 12, 12);

				// 繪製邊框
				g2d.setColor(new Color(230, 230, 230));
				g2d.drawRoundRect(0, 0, getWidth()-3, getHeight()-3, 12, 12);
			}

			@Override
			public Dimension getPreferredSize() {
				Dimension size = super.getPreferredSize();
				return new Dimension(size.width, Math.max(60, size.height));
			}
		};
	}

	// 創建帶陰影的圓角按鈕
	private JButton createStyledButton(String text, Color bgColor) {
		JButton button = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				// 繪製陰影
				g2d.setColor(new Color(0, 0, 0, 20));
				g2d.fillRoundRect(2, 3, getWidth()-2, getHeight()-3, 8, 8);
				g2d.setColor(new Color(0, 0, 0, 10));
				g2d.fillRoundRect(1, 2, getWidth()-1, getHeight()-2, 8, 8);

				// 繪製按鈕背景
				g2d.setColor(getBackground());
				g2d.fillRoundRect(0, 0, getWidth()-2, getHeight()-3, 8, 8);

				// 繪製文字
				FontMetrics fm = g2d.getFontMetrics();
				int x = (getWidth() - fm.stringWidth(getText())) / 2;
				int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent() - 1;
				g2d.setColor(getForeground());
				g2d.drawString(getText(), x, y);
			}
		};

		button.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
		button.setForeground(new Color(68, 68, 68));
		button.setBackground(bgColor);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setPreferredSize(new Dimension(120, 38));
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));

		// 添加懸停效果
		button.addMouseListener(new MouseAdapter() {
			Color originalColor = bgColor;
			Color hoverColor = bgColor.darker();

			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBackground(hoverColor);
				button.repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setBackground(originalColor);
				button.repaint();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				button.setBackground(hoverColor.darker());
				button.repaint();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				button.setBackground(hoverColor);
				button.repaint();
			}
		});

		return button;
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
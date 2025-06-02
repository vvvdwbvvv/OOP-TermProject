import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class AnalysisWindow extends JFrame {

    public AnalysisWindow(List<WorkoutData> data) {
        setTitle("🏋️ 訓練分析儀表板");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // 設置現代化的背景漸層
        JPanel root = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // 創建漸層背景
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(0xf8fafc),
                    0, getHeight(), new Color(0xe2e8f0)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        root.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        root.setLayout(new GridLayout(1, 3, 25, 0));

        WorkoutData latest = data.get(data.size() - 1);
        int latestTotal = latest.getTotalDuration();
        int latestCardio = latest.getCardioTime();
        double cardioRatio = (latestTotal == 0) ? 0 : (double) latestCardio / latestTotal;

        // 左區 - 總時長與有氧比例卡片
        JPanel leftCard = createModernCard();
        leftCard.setLayout(new BoxLayout(leftCard, BoxLayout.Y_AXIS));
        
        // 標題
        JLabel titleLabel = new JLabel("今日訓練時長", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft JhengHei", Font.BOLD, 16));
        titleLabel.setForeground(new Color(0x475569));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // 主要數據
        JLabel durationLabel = new JLabel(latestTotal + " 分鐘", SwingConstants.CENTER);
        durationLabel.setFont(new Font("Microsoft JhengHei", Font.BOLD, 42));
        durationLabel.setForeground(new Color(0x0f172a));
        durationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 有氧比例環形進度條
        JPanel progressPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int size = 80;
                int x = (getWidth() - size) / 2;
                int y = 10;
                
                // 背景圓環
                g2d.setStroke(new BasicStroke(8));
                g2d.setColor(new Color(0xe2e8f0));
                g2d.drawOval(x, y, size, size);
                
                // 進度圓環
                g2d.setColor(new Color(0x059669));
                int angle = (int) (360 * cardioRatio);
                g2d.drawArc(x, y, size, size, 90, -angle);
                
                // 中心文字
                String percentage = String.format("%.0f%%", cardioRatio * 100);
                FontMetrics fm = g2d.getFontMetrics(new Font("Microsoft JhengHei", Font.BOLD, 14));
                int textWidth = fm.stringWidth(percentage);
                int textHeight = fm.getHeight();
                g2d.setColor(new Color(0x0f172a));
                g2d.setFont(new Font("Microsoft JhengHei", Font.BOLD, 14));
                g2d.drawString(percentage, x + (size - textWidth) / 2, y + (size + textHeight) / 2 - 3);
            }
        };
        progressPanel.setOpaque(false);
        progressPanel.setPreferredSize(new Dimension(120, 100));
        progressPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel cardioTextLabel = new JLabel("有氧運動比例", SwingConstants.CENTER);
        cardioTextLabel.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        cardioTextLabel.setForeground(new Color(0x64748b));
        cardioTextLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftCard.add(titleLabel);
        leftCard.add(durationLabel);
        leftCard.add(Box.createRigidArea(new Dimension(0, 20)));
        leftCard.add(progressPanel);
        leftCard.add(Box.createRigidArea(new Dimension(0, 10)));
        leftCard.add(cardioTextLabel);

        // 中區 - 肌群統計卡片
        JPanel midCard = createModernCard();
        midCard.setLayout(new BoxLayout(midCard, BoxLayout.Y_AXIS));

        JLabel todayTitle = new JLabel("🎯 訓練統計", SwingConstants.LEFT);
        todayTitle.setFont(new Font("Microsoft JhengHei", Font.BOLD, 16));
        todayTitle.setForeground(new Color(0x475569));
        todayTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        String[] allMuscles = {"SHOULDER", "CHEST", "BACK", "LEG", "ARMS"};
        String[] muscleNames = {"肩膀", "胸部", "背部", "腿部", "手臂"};
        Color[] muscleColors = {
            new Color(0xf59e0b), new Color(0xef4444), new Color(0x3b82f6),
            new Color(0x10b981), new Color(0x8b5cf6)
        };
        
        int[] todayCounts = new int[allMuscles.length];
        int[] lastSevenCounts = new int[allMuscles.length];

        // 計算今日訓練
        WorkoutData today = data.get(0);
        String[] trainedMuscles = today.getMuscleGroup().split(",");
        for (String trained : trainedMuscles) {
            for (int i = 0; i < allMuscles.length; i++) {
                if (allMuscles[i].equalsIgnoreCase(trained.trim())) {
                    todayCounts[i]++;
                }
            }
        }

        // 計算最近七次訓練
        List<WorkoutData> history = data.subList(0, Math.min(7, data.size()));
        for (WorkoutData w : history) {
            String[] parts = w.getMuscleGroup().split(",");
            for (String part : parts) {
                for (int i = 0; i < allMuscles.length; i++) {
                    if (allMuscles[i].equalsIgnoreCase(part.trim())) {
                        lastSevenCounts[i]++;
                    }
                }
            }
        }

        midCard.add(todayTitle);
        
        // 今日訓練區塊
        JLabel todayLabel = new JLabel("今日訓練");
        todayLabel.setFont(new Font("Microsoft JhengHei", Font.BOLD, 14));
        todayLabel.setForeground(new Color(0x0f172a));
        todayLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        midCard.add(todayLabel);

        for (int i = 0; i < allMuscles.length; i++) {
            JPanel musclePanel = createMuscleStatPanel(muscleNames[i], todayCounts[i], muscleColors[i], false);
            midCard.add(musclePanel);
        }
        
        midCard.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // 近七次訓練區塊
        JLabel historyLabel = new JLabel("最近七次訓練");
        historyLabel.setFont(new Font("Microsoft JhengHei", Font.BOLD, 14));
        historyLabel.setForeground(new Color(0x0f172a));
        historyLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        midCard.add(historyLabel);

        for (int i = 0; i < allMuscles.length; i++) {
            JPanel musclePanel = createMuscleStatPanel(muscleNames[i], lastSevenCounts[i], muscleColors[i], true);
            midCard.add(musclePanel);
        }

        // 右區 - 智能建議卡片
        JPanel rightCard = createModernCard();
        rightCard.setLayout(new BoxLayout(rightCard, BoxLayout.Y_AXIS));

        JLabel suggestionTitle = new JLabel("💡 智能建議", SwingConstants.LEFT);
        suggestionTitle.setFont(new Font("Microsoft JhengHei", Font.BOLD, 16));
        suggestionTitle.setForeground(new Color(0x475569));
        suggestionTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // 生成建議
        StringBuilder advice = new StringBuilder();
        if (cardioRatio < 0.2) {
            advice.append("🏃‍♂️ 建議增加有氧運動比例\n   目前有氧比例偏低，增加心肺功能訓練\n\n");
        }

        boolean hasLeg = false;
        for (WorkoutData w : history) {
            String[] parts = w.getMuscleGroup().split(",");
            for (String part : parts) {
                if ("LEG".equalsIgnoreCase(part.trim())) {
                    hasLeg = true;
                    break;
                }
            }
            if (hasLeg) break;
        }

        if (!hasLeg) {
            advice.append("🦵 加強腿部訓練\n   腿部是身體最大肌群，別忽略了！\n\n");
        }

        if (cardioRatio >= 0.2 && history.size() >= 3) {
            advice.append("✅ 訓練分布均衡\n   目前的訓練安排很不錯，繼續保持！\n\n");
        }

        advice.append("💪 建議加強手臂訓練\n   手臂肌群需要更多關注");

        JTextArea adviceArea = new JTextArea(advice.toString());
        adviceArea.setWrapStyleWord(true);
        adviceArea.setLineWrap(true);
        adviceArea.setEditable(false);
        adviceArea.setOpaque(false);
        adviceArea.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 13));
        adviceArea.setForeground(new Color(0x374151));
        adviceArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        rightCard.add(suggestionTitle);
        rightCard.add(adviceArea);

        root.add(leftCard);
        root.add(midCard);
        root.add(rightCard);

        add(root);
        setVisible(true);
    }

    private JPanel createModernCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // 陰影效果
                g2d.setColor(new Color(0, 0, 0, 10));
                g2d.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, 15, 15);
                g2d.setColor(new Color(0, 0, 0, 8));
                g2d.fillRoundRect(1, 1, getWidth() - 1, getHeight() - 1, 15, 15);
                
                // 卡片背景
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 15, 15);
                
                // 邊框
                g2d.setColor(new Color(0xe2e8f0));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 15, 15);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        return card;
    }

    private JPanel createMuscleStatPanel(String muscleName, int count, Color color, boolean isHistory) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));

        // 顏色指示器
        JPanel colorIndicator = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(color);
                g2d.fillRoundRect(0, 2, 12, 12, 6, 6);
            }
        };
        colorIndicator.setOpaque(false);
        colorIndicator.setPreferredSize(new Dimension(12, 16));
        colorIndicator.setMaximumSize(new Dimension(12, 16));

        JLabel nameLabel = new JLabel(muscleName);
        nameLabel.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 13));
        nameLabel.setForeground(new Color(0x374151));

        JLabel countLabel = new JLabel(String.valueOf(count));
        countLabel.setFont(new Font("Microsoft JhengHei", Font.BOLD, 13));
        countLabel.setForeground(isHistory ? new Color(0x6b7280) : new Color(0x0f172a));

        panel.add(colorIndicator);
        panel.add(Box.createRigidArea(new Dimension(8, 0)));
        panel.add(nameLabel);
        panel.add(Box.createHorizontalGlue());
        panel.add(countLabel);

        return panel;
    }
}

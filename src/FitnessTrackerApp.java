import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.Text;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;

public class FitnessTrackerApp extends Application {

    private ComboBox<BodyPart> bodyPartComboBox;
    private TextField startTimeField;
    private TextField endTimeField;
    private ListView<Exercise> availableExercisesList;
    private ListView<WorkoutExercise> selectedExercisesList;
    private ComboBox<Integer> setsComboBox;
    private ComboBox<Integer> repsComboBox;
    private ComboBox<Double> weightComboBox;

    private WeeklyPlan weeklyPlanManager;
    private ComboBox<DayOfWeek> dayOfWeekComboBox;
    private ComboBox<BodyPart> planBodyPartComboBox;
    private ListView<Exercise> planAvailableExercisesList;
    private ListView<String> dailyPlanList;

    @Override
    public void start(Stage primaryStage) {
        System.out.println("Starting FitnessTrackerApp...");
        weeklyPlanManager = new WeeklyPlan();

        TabPane tabPane = new TabPane();

        Tab recordWorkoutTab = new Tab("記錄健身課程", createRecordWorkoutPane());
        Tab weeklyPlanTab = new Tab("每週計劃", createWeeklyPlanPane());
        Tab gymStatusTab = new Tab("健身房即時狀態", createGymStatusPane());

        tabPane.getTabs().addAll(recordWorkoutTab, weeklyPlanTab, gymStatusTab);

        Scene scene = new Scene(tabPane, 800, 600);
        primaryStage.setTitle("健身追蹤器");
        primaryStage.setScene(scene);
        primaryStage.show();
        System.out.println("FitnessTrackerApp started successfully.");
    }

    private Pane createRecordWorkoutPane() {
        VBox mainPane = new VBox(10);
        mainPane.setPadding(new Insets(10));

        HBox topPane = new HBox(10);
        topPane.setAlignment(Pos.CENTER_LEFT);

        topPane.getChildren().add(new Text("選擇部位:"));
        bodyPartComboBox = new ComboBox<>();
        bodyPartComboBox.getItems().addAll(BodyPart.values());
        bodyPartComboBox.setValue(BodyPart.CHEST);
        bodyPartComboBox.setOnAction(e -> updateAvailableExercises());
        topPane.getChildren().add(bodyPartComboBox);

        topPane.getChildren().add(new Text("開始時間 (YYYY-MM-DD HH:MM):"));
        startTimeField = new TextField("2025-05-20 09:48");
        topPane.getChildren().add(startTimeField);

        topPane.getChildren().add(new Text("結束時間 (YYYY-MM-DD HH:MM):"));
        endTimeField = new TextField("2025-05-20 10:48");
        topPane.getChildren().add(endTimeField);

        mainPane.getChildren().add(topPane);

        HBox centerPane = new HBox(10);

        VBox availablePane = new VBox(5);
        availablePane.getChildren().add(new Text("可用練習:"));
        availableExercisesList = new ListView<>();
        availablePane.getChildren().add(availableExercisesList);
        centerPane.getChildren().add(availablePane);

        VBox buttonPane = new VBox(10);
        Button addExerciseButton = new Button("添加練習 >>");
        Button removeExerciseButton = new Button("<< 移除練習");

        addExerciseButton.setOnAction(e -> addSelectedExercise());
        removeExerciseButton.setOnAction(e -> removeSelectedExercise());

        buttonPane.getChildren().addAll(addExerciseButton, removeExerciseButton);
        centerPane.getChildren().add(buttonPane);

        VBox selectedPane = new VBox(5);
        selectedPane.getChildren().add(new Text("已選練習:"));
        selectedExercisesList = new ListView<>();
        selectedPane.getChildren().add(selectedExercisesList);
        centerPane.getChildren().add(selectedPane);

        mainPane.getChildren().add(centerPane);

        HBox bottomPane = new HBox(10);
        bottomPane.setAlignment(Pos.CENTER);

        bottomPane.getChildren().add(new Text("組數:"));
        setsComboBox = new ComboBox<>();
        for (int i = 1; i <= 10; i++) setsComboBox.getItems().add(i);
        bottomPane.getChildren().add(setsComboBox);

        bottomPane.getChildren().add(new Text("次數:"));
        repsComboBox = new ComboBox<>();
        for (int i = 1; i <= 20; i++) repsComboBox.getItems().add(i);
        bottomPane.getChildren().add(repsComboBox);

        bottomPane.getChildren().add(new Text("重量 (kg):"));
        weightComboBox = new ComboBox<>();
        for (int i = 0; i <= 40; i++) weightComboBox.getItems().add(i * 2.5);
        bottomPane.getChildren().add(weightComboBox);

        Button recordSessionButton = new Button("記錄健身課程");
        Button callWindowButton = new Button("呼叫視窗");

        recordSessionButton.setOnAction(e -> recordWorkoutSession());
        callWindowButton.setOnAction(e -> {
            List<WorkoutData> data = WorkoutData.fetchRecentData();
            if (!data.isEmpty()) {
                new AnalysisWindow(data);
            } else {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("無數據");
                alert.setHeaderText(null);
                alert.setContentText("沒有可用的健身數據進行分析。");
                alert.showAndWait();
            }
        });

        bottomPane.getChildren().addAll(recordSessionButton, callWindowButton);

        mainPane.getChildren().add(bottomPane);

        return mainPane;
    }

    private Pane createWeeklyPlanPane() {
        VBox mainPane = new VBox(10);
        mainPane.setPadding(new Insets(10));

        HBox topPane = new HBox(10);
        topPane.setAlignment(Pos.CENTER_LEFT);

        topPane.getChildren().add(new Text("選擇星期:"));
        dayOfWeekComboBox = new ComboBox<>();
        dayOfWeekComboBox.getItems().addAll(DayOfWeek.values());
        dayOfWeekComboBox.setOnAction(e -> updateDailyPlanView());
        topPane.getChildren().add(dayOfWeekComboBox);

        mainPane.getChildren().add(topPane);

        SplitPane splitPane = new SplitPane();

        VBox leftPane = new VBox(10);
        leftPane.getChildren().add(new Text("添加運動到計劃:"));
        planBodyPartComboBox = new ComboBox<>();
        planBodyPartComboBox.getItems().addAll(BodyPart.values());
        planBodyPartComboBox.setOnAction(e -> updatePlanAvailableExercises());
        leftPane.getChildren().add(planBodyPartComboBox);

        planAvailableExercisesList = new ListView<>();
        leftPane.getChildren().add(planAvailableExercisesList);

        Button addExerciseToPlanButton = new Button("添加到計劃");
        addExerciseToPlanButton.setOnAction(e -> addExerciseToWeeklyPlan());
        leftPane.getChildren().add(addExerciseToPlanButton);

        splitPane.getItems().add(leftPane);

        VBox rightPane = new VBox(10);
        rightPane.getChildren().add(new Text("當日計劃:"));
        dailyPlanList = new ListView<>();
        rightPane.getChildren().add(dailyPlanList);

        Button removeExerciseFromPlanButton = new Button("從計劃中移除選定項");
        removeExerciseFromPlanButton.setOnAction(e -> removeExerciseFromWeeklyPlan());
        rightPane.getChildren().add(removeExerciseFromPlanButton);

        splitPane.getItems().add(rightPane);

        mainPane.getChildren().add(splitPane);

        return mainPane;
    }

    private Connection conn;
    private static final String DB_URL = "jdbc:mysql://140.119.19.73:3315/TG10?useSSL=false";
    private static final String DB_USER = "TG10";
    private static final String DB_PW = "iRIzsI";

    private Pane createGymStatusPane() {
        System.out.println("Initializing Gym Status Pane...");
        VBox mainPane = new VBox(10);
        mainPane.setPadding(new Insets(10));

        try {
            System.out.println("Connecting to database...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PW);
            System.out.println("Database connection established.");
        } catch (ClassNotFoundException e) {
            System.err.println("Database driver not found: " + e.getMessage());
            showAlert(AlertType.ERROR, "錯誤", "找不到資料庫驅動: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            showAlert(AlertType.ERROR, "錯誤", "資料庫連線失敗: " + e.getMessage());
        }

        HBox topPane = new HBox(10);
        topPane.setAlignment(Pos.CENTER);

        Text occupancyText = new Text("目前人數: -- / --");
        Circle lightIndicator = new Circle(15, Color.GRAY);
        Text suggestionText = new Text("建議: --");
        Button refreshButton = new Button("刷新狀態");

        refreshButton.setOnAction(e -> {
            System.out.println("Refreshing occupancy status...");
            updateOccupancy(occupancyText, lightIndicator, suggestionText);
        });

        topPane.getChildren().addAll(occupancyText, lightIndicator, suggestionText, refreshButton);
        mainPane.getChildren().add(topPane);

        // Load gym diagram image from file system
        ImageView gymDiagram = new ImageView(new Image(new File("src/images/gym_diagram.png").toURI().toString()));
        gymDiagram.setFitWidth(400);
        gymDiagram.setFitHeight(300);
        mainPane.getChildren().add(gymDiagram);

        HBox bottomPane = new HBox(10);
        bottomPane.setAlignment(Pos.CENTER);

        Text idLabel = new Text("會員ID:");
        TextField idField = new TextField();
        Button checkMembershipButton = new Button("查詢會員");
        Text membershipText = new Text("剩餘時效: --");

        bottomPane.getChildren().addAll(idLabel, idField, checkMembershipButton, membershipText);
        mainPane.getChildren().add(bottomPane);

        System.out.println("Gym Status Pane initialized successfully.");
        return mainPane;
    }

    private void showAlert(AlertType type, String title, String message) {
        System.out.println("Showing alert: " + message);
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateAvailableExercises() {
        BodyPart selectedPart = bodyPartComboBox.getValue();
        if (selectedPart != null) {
            List<Exercise> exercises = ExerciseData.getExercisesByBodyPart(selectedPart);
            availableExercisesList.getItems().setAll(exercises);
        }
    }

    private void updatePlanAvailableExercises() {
        BodyPart selectedPart = planBodyPartComboBox.getValue();
        if (selectedPart != null) {
            List<Exercise> exercises = ExerciseData.getExercisesByBodyPart(selectedPart);
            planAvailableExercisesList.getItems().setAll(exercises);
        }
    }

    private void updateDailyPlanView() {
        DayOfWeek selectedDay = dayOfWeekComboBox.getValue();
        dailyPlanList.getItems().clear();
        if (selectedDay != null) {
            Map<BodyPart, List<Exercise>> planForDay = weeklyPlanManager.getPlanForDay(selectedDay);
            if (planForDay != null && !planForDay.isEmpty()) {
                for (Map.Entry<BodyPart, List<Exercise>> entry : planForDay.entrySet()) {
                    BodyPart part = entry.getKey();
                    for (Exercise ex : entry.getValue()) {
                        dailyPlanList.getItems().add(part.toString() + ": " + ex.getName());
                    }
                }
            }
            if (dailyPlanList.getItems().isEmpty()) {
                dailyPlanList.getItems().add("本日無計劃");
            }
        }
    }

    private void addSelectedExercise() {
        Exercise ex = availableExercisesList.getSelectionModel().getSelectedItem();
        Integer sets = setsComboBox.getValue();
        Integer reps = repsComboBox.getValue();
        Double weight = weightComboBox.getValue();
        if (ex != null && sets != null && reps != null && weight != null) {
            WorkoutExercise we = new WorkoutExercise(ex, sets, reps, weight);
            selectedExercisesList.getItems().add(we);
        }
    }

    private void removeSelectedExercise() {
        WorkoutExercise selectedExercise = selectedExercisesList.getSelectionModel().getSelectedItem();
        if (selectedExercise != null) {
            selectedExercisesList.getItems().remove(selectedExercise);
        }
    }

    private void recordWorkoutSession() {
        System.out.println("Recording workout session...");
        // Logic to record the workout session
    }

    private void addExerciseToWeeklyPlan() {
        Exercise selectedExercise = planAvailableExercisesList.getSelectionModel().getSelectedItem();
        DayOfWeek selectedDay = dayOfWeekComboBox.getValue();
        BodyPart selectedPart = planBodyPartComboBox.getValue();
        if (selectedExercise != null && selectedDay != null && selectedPart != null) {
            weeklyPlanManager.addExerciseToPlan(selectedDay, selectedPart, selectedExercise);
            updateDailyPlanView();
        }
    }

    private void removeExerciseFromWeeklyPlan() {
        String selected = dailyPlanList.getSelectionModel().getSelectedItem();
        DayOfWeek selectedDay = dayOfWeekComboBox.getValue();
        if (selected != null && selectedDay != null) {
            String[] parts = selected.split(": ");
            BodyPart bodyPart = BodyPart.valueOf(parts[0]);
            String exName = parts[1];
            List<Exercise> list = weeklyPlanManager.getExercisesForDayAndPart(selectedDay, bodyPart);
            for (Exercise e : list) {
                if (e.getName().equals(exName)) {
                    weeklyPlanManager.removeExerciseFromPlan(selectedDay, bodyPart, e);
                    break;
                }
            }
            updateDailyPlanView();
        }
    }

    private void updateOccupancy(Text occupancyText, Circle lightIndicator, Text suggestionText) {
        System.out.println("Updating gym occupancy...");
        // Logic to update gym occupancy
    }

    public static void main(String[] args) {
        launch(args);
    }
}

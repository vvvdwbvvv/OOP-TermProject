import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import gpt.GptChatService;
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
import java.util.List;
import java.util.Map;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import javafx.application.Platform;

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
        Tab chatTab          = new Tab("AI 聊天", createChatPane());


//        new TrainingReminderGUI();

        recordWorkoutTab.setClosable(false);
        weeklyPlanTab.setClosable(false);
        gymStatusTab.setClosable(false);
        chatTab.setClosable(false);

        tabPane.getTabs().addAll(recordWorkoutTab, weeklyPlanTab, gymStatusTab, chatTab);

        Scene scene = new Scene(tabPane, 800, 600);
        URL lightCssUrl = getClass().getResource("/ui.css");
        if (lightCssUrl == null) {
            throw new IllegalStateException("無法找到 /ui.css，請確認已把 ui.css 放在 src/main/resources/");
        }
        String lightCss = lightCssUrl.toExternalForm();
        scene.getStylesheets().add(lightCss);
        System.out.println("成功載入 ui.css → " + lightCss);

        // 暗黑模式样式
        URL darkCssUrl = getClass().getResource("/dark.css");
        if (darkCssUrl == null) {
            throw new IllegalStateException("無法找到 /dark.css，請確認已把 dark.css 放在 src/main/resources/");
        }
        String darkCss = darkCssUrl.toExternalForm();
        System.out.println("成功載入 dark.css → " + darkCss);

        primaryStage.setTitle("健身追蹤器");
        primaryStage.setScene(scene);
        primaryStage.show();
        System.out.println("FitnessTrackerApp started successfully.");
    }

    private VBox createRecordWorkoutPane() {
        // 主容器 - 外圍加大一點 padding，並設定背景
        VBox mainPane = new VBox(15);
        mainPane.setPadding(new Insets(15));
        mainPane.getStyleClass().add("root");

        // ===== Top 區：身體部位 + 時間欄位 =====
        HBox topPane = new HBox(12);
        topPane.setAlignment(Pos.CENTER_LEFT);
        topPane.setPadding(new Insets(0, 0, 10, 0)); // 底部留點空白

        Label lblBody = new Label("選擇部位:");
        lblBody.setMinWidth(70);  // 設定 Label 固定寬度，好對齊
        bodyPartComboBox = new ComboBox<>();
        bodyPartComboBox.getItems().addAll(BodyPart.values());
        bodyPartComboBox.setValue(BodyPart.CHEST);
        bodyPartComboBox.setOnAction(e -> updateAvailableExercises());
        bodyPartComboBox.setPrefWidth(120);

        Label lblStart = new Label("開始時間 (YYYY-MM-DD HH:MM):");
        lblStart.setMinWidth(180);
        startTimeField = new TextField("2025-05-20 09:48");
        startTimeField.setPrefWidth(150);

        Label lblEnd = new Label("結束時間 (YYYY-MM-DD HH:MM):");
        lblEnd.setMinWidth(180);
        endTimeField = new TextField("2025-05-20 10:48");
        endTimeField.setPrefWidth(150);

        topPane.getChildren().addAll(
                lblBody, bodyPartComboBox,
                lblStart, startTimeField,
                lblEnd,   endTimeField
        );
        mainPane.getChildren().add(topPane);

        // ===== Center 區：可用練習 / 按鈕 / 已選練習 =====
        HBox centerPane = new HBox(15);
        centerPane.setAlignment(Pos.CENTER);
        centerPane.setPadding(new Insets(0, 0, 10, 0)); // 底部留空白

        // 左側：可用練習
        VBox availablePane = new VBox(6);
        availablePane.getStyleClass().add("card");
        availablePane.setPadding(new Insets(10));
        availablePane.setPrefWidth(250);
        availablePane.setMaxWidth(250);

        Label lblAvail = new Label("可用練習:");
        lblAvail.setStyle("-fx-font-weight: bold;");

        availableExercisesList = new ListView<>();
        availableExercisesList.getStyleClass().add("list-view-small");
        availableExercisesList.setPrefHeight(300);
        availableExercisesList.setPrefWidth(230);

        availablePane.getChildren().addAll(lblAvail, availableExercisesList);
        centerPane.getChildren().add(availablePane);

        // 中間：按鈕區
        VBox buttonPane = new VBox(12);
        buttonPane.getStyleClass().add("sub-card");
        buttonPane.setPadding(new Insets(15));
        buttonPane.setAlignment(Pos.CENTER);

        Button addExerciseButton = new Button("添加練習 >>");
        addExerciseButton.getStyleClass().add("primary-button");
        addExerciseButton.setPrefWidth(140);
        addExerciseButton.setOnAction(e -> addSelectedExercise());

        Button removeExerciseButton = new Button("<< 移除練習");
        removeExerciseButton.getStyleClass().add("secondary-button");
        removeExerciseButton.setPrefWidth(140);
        removeExerciseButton.setOnAction(e -> removeSelectedExercise());

        buttonPane.getChildren().addAll(addExerciseButton, removeExerciseButton);
        centerPane.getChildren().add(buttonPane);

        // 右側：已選練習
        VBox selectedPane = new VBox(6);
        selectedPane.getStyleClass().add("card");
        selectedPane.setPadding(new Insets(10));
        selectedPane.setPrefWidth(250);
        selectedPane.setMaxWidth(250);

        Label lblSel = new Label("已選練習:");
        lblSel.setStyle("-fx-font-weight: bold;");

        selectedExercisesList = new ListView<>();
        selectedExercisesList.getStyleClass().add("list-view-small");
        selectedExercisesList.setPrefHeight(300);
        selectedExercisesList.setPrefWidth(230);

        selectedPane.getChildren().addAll(lblSel, selectedExercisesList);
        centerPane.getChildren().add(selectedPane);

        VBox.setVgrow(centerPane, Priority.ALWAYS);

        mainPane.getChildren().add(centerPane);

        // ===== Bottom 區：組數／次數／重量／按鈕 =====
        HBox bottomPane = new HBox(12);
        bottomPane.setAlignment(Pos.CENTER);
        bottomPane.setPadding(new Insets(0, 0, 5, 0));

        bottomPane.getChildren().add(new Label("組數:"));
        setsComboBox = new ComboBox<>();
        setsComboBox.setPrefWidth(70);
        for (int i = 1; i <= 10; i++) setsComboBox.getItems().add(i);
        bottomPane.getChildren().add(setsComboBox);

        bottomPane.getChildren().add(new Label("次數:"));
        repsComboBox = new ComboBox<>();
        repsComboBox.setPrefWidth(70);
        for (int i = 1; i <= 20; i++) repsComboBox.getItems().add(i);
        bottomPane.getChildren().add(repsComboBox);

        bottomPane.getChildren().add(new Label("重量 (kg):"));
        weightComboBox = new ComboBox<>();
        weightComboBox.setPrefWidth(70);
        for (int i = 0; i <= 40; i++) weightComboBox.getItems().add(i * 2.5);
        bottomPane.getChildren().add(weightComboBox);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);  // 左半段和右半段之間留彈性空間
        bottomPane.getChildren().add(spacer);

        Button recordSessionButton = new Button("記錄健身課程");
        recordSessionButton.getStyleClass().add("primary-button");
        recordSessionButton.setMinWidth(120);
        recordSessionButton.setOnAction(e -> recordWorkoutSession());

        Button callWindowButton = new Button("呼叫視窗");
        callWindowButton.getStyleClass().add("secondary-button");
        callWindowButton.setMinWidth(120);
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

        HBox themeTogglePane = new HBox();
        themeTogglePane.setAlignment(Pos.CENTER_RIGHT);
        themeTogglePane.setPadding(new Insets(10, 0, 0, 0)); // 顶部留一些空隙
        themeTogglePane.setPrefHeight(40);

        ToggleButton darkModeToggle = new ToggleButton("暗黑模式");
        darkModeToggle.setFocusTraversable(false);
        darkModeToggle.setPrefWidth(100);
        // 在初始时，如果您想 Tab 打开时默认为浅色，可以先不设置样式
        // darkModeToggle.getStyleClass().add("secondary-button"); // 如需要自定义按钮样式可加

        themeTogglePane.getChildren().add(darkModeToggle);
        mainPane.getChildren().add(themeTogglePane);
        VBox.setVgrow(themeTogglePane, Priority.NEVER);

        // ──────────────────────────────────────────────────────────────────────

        // ─── 五、在这里为 darkModeToggle 绑定 CSS 切换逻辑 ─────────────────────────
        //    注意：此处假设 start(...) 中已经加载了 lightCss 和 darkCss
        //    你只需把这两行字符串改成你自己的路径变量：
        String lightCss = getClass().getResource("/ui.css").toExternalForm();
        String darkCss  = getClass().getResource("/dark.css").toExternalForm();

        darkModeToggle.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            Scene scene = mainPane.getScene(); // 拿到当前 Scene
            if (scene == null) return;

            if (isNowSelected) {
                // 切到暗黑
                scene.getStylesheets().remove(lightCss);
                if (!scene.getStylesheets().contains(darkCss)) {
                    scene.getStylesheets().add(darkCss);
                }
                darkModeToggle.setText("淺色模式");
            } else {
                // 切回明亮
                scene.getStylesheets().remove(darkCss);
                if (!scene.getStylesheets().contains(lightCss)) {
                    scene.getStylesheets().add(lightCss);
                }
                darkModeToggle.setText("暗黑模式");
            }
        });

        updateAvailableExercises();
        return mainPane;
    }


    private VBox createWeeklyPlanPane() {
        // 1. 最外層大卡片
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(20));
        mainContainer.getStyleClass().add("card");  // 外層卡片
        mainContainer.setPrefWidth(700);

        // 1.1. 上方標題
        Label title = new Label("每週訓練計劃");
        title.getStyleClass().add("status-title");
        mainContainer.getChildren().add(title);

        // 1.2. 「選擇星期」區塊 （可考慮也套 sub-card，但這裡直接放在 card 裡）
        HBox chooseDayBox = new HBox(10);
        chooseDayBox.setAlignment(Pos.CENTER_LEFT);
        chooseDayBox.setPadding(new Insets(5, 0, 10, 0)); // 上下稍微分隔

        Label lblDay = new Label("選擇星期：");
        lblDay.setMinWidth(80);
        lblDay.getStyleClass().add("status-text");

        dayOfWeekComboBox = new ComboBox<>();
        dayOfWeekComboBox.getItems().addAll(DayOfWeek.values());
        dayOfWeekComboBox.setPrefWidth(150);
        dayOfWeekComboBox.setOnAction(e -> updateDailyPlanView());

        chooseDayBox.getChildren().addAll(lblDay, dayOfWeekComboBox);
        mainContainer.getChildren().add(chooseDayBox);

        // 2. SplitPane：左側為「添加運動到計劃」，右側為「當日計劃」
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.47); // 左側大約47%，右側約53%
        splitPane.setPrefHeight(350);

        // ─── 2.1 左側子卡片：添加到計劃 ───────────────────────────────────
        VBox leftCard = new VBox(12);
        leftCard.getStyleClass().add("sub-card");      // 次級卡片
        leftCard.setPadding(new Insets(12));
        leftCard.setPrefWidth(300);

        // 左側標題
        Label lblAdd = new Label("添加運動到計劃：");
        lblAdd.getStyleClass().add("status-title");
        lblAdd.setMinHeight(25);

        // 選身體部位 ComboBox
        planBodyPartComboBox = new ComboBox<>();
        planBodyPartComboBox.getItems().addAll(BodyPart.values());
        planBodyPartComboBox.setPrefWidth(160);
        planBodyPartComboBox.setOnAction(e -> updatePlanAvailableExercises());

        // 可用運動列表
        planAvailableExercisesList = new ListView<>();
        planAvailableExercisesList.getStyleClass().add("list-view-small");
        planAvailableExercisesList.setPrefHeight(220);
        planAvailableExercisesList.setPrefWidth(260);
        VBox.setVgrow(planAvailableExercisesList, Priority.ALWAYS);

        // 添加按鈕
        Button addExerciseToPlanButton = new Button("添加到計劃");
        addExerciseToPlanButton.getStyleClass().add("primary-button");
        addExerciseToPlanButton.setPrefWidth(120);
        addExerciseToPlanButton.setPrefHeight(28);
        addExerciseToPlanButton.setOnAction(e -> addExerciseToWeeklyPlan());

        // 把元件塞進 leftCard
        leftCard.getChildren().addAll(lblAdd, planBodyPartComboBox, planAvailableExercisesList, addExerciseToPlanButton);

        // ─── 2.2 右側子卡片：當日計劃 ───────────────────────────────────
        VBox rightCard = new VBox(12);
        rightCard.getStyleClass().add("sub-card");
        rightCard.setPadding(new Insets(12));
        rightCard.setPrefWidth(300);

        // 右側標題
        Label lblTodayPlan = new Label("當日計劃：");
        lblTodayPlan.getStyleClass().add("status-title");
        lblTodayPlan.setMinHeight(25);

        // 當日計劃列表
        dailyPlanList = new ListView<>();
        dailyPlanList.getStyleClass().add("list-view-small");
        dailyPlanList.setPrefHeight(220);
        dailyPlanList.setPrefWidth(260);
        VBox.setVgrow(dailyPlanList, Priority.ALWAYS);

        // 移除按鈕
        Button removeExerciseFromPlanButton = new Button("從計劃中移除選定項");
        removeExerciseFromPlanButton.getStyleClass().add("secondary-button");
        removeExerciseFromPlanButton.setPrefWidth(180);
        removeExerciseFromPlanButton.setPrefHeight(28);
        removeExerciseFromPlanButton.setOnAction(e -> removeExerciseFromWeeklyPlan());

        // 把元件塞進 rightCard
        rightCard.getChildren().addAll(lblTodayPlan, dailyPlanList, removeExerciseFromPlanButton);

        // 將左/右卡片 放到 SplitPane
        splitPane.getItems().addAll(leftCard, rightCard);

        mainContainer.getChildren().add(splitPane);

        // 初始時填一次資料
        updatePlanAvailableExercises();
        updateDailyPlanView();

        return mainContainer;
    }

    private Connection conn;
    private static final String DB_URL = "jdbc:mysql://140.119.19.73:3315/TG10?useSSL=false";
    private static final String DB_USER = "TG10";
    private static final String DB_PW = "iRIzsI";

    private Pane createGymStatusPane() {
        // ─── 最外層：大卡片 (Card) ─────────────────────────────────────────
        // 這裡用 VBox 收三個子卡片：StatusCard、ChartCard、MemberCard
        VBox outerCard = new VBox(20);
        outerCard.setPadding(new Insets(20));
        outerCard.getStyleClass().add("card");  // .card: 白底、圓角、陰影
        outerCard.setFillWidth(true);

        // ─── 一、即時狀態子卡片 (Sub-Card) ────────────────────────────────────
        VBox statusCard = new VBox(10);
        statusCard.setPadding(new Insets(15));
        statusCard.getStyleClass().add("sub-card");  // 比 .card 稍微輕一點的陰影
        statusCard.setFillWidth(true);

        // 一.1 卡片標題
        Label statusTitle = new Label("健身房即時狀態");
        statusTitle.getStyleClass().add("status-title");
        statusCard.getChildren().add(statusTitle);

        // 一.2 HBox: 目前人數 / 燈號 / 建議 / （Spacer）/ 刷新按鈕
        HBox statusPane = new HBox(15);
        statusPane.setAlignment(Pos.CENTER_LEFT);

        Text occupancyText = new Text("目前人數: -- / --");
        occupancyText.getStyleClass().add("status-text");

        Circle lightIndicator = new Circle(8, Color.GRAY);

        Text suggestionText = new Text("建議: --");
        suggestionText.getStyleClass().add("status-text");

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        Button refreshButton = new Button("刷新狀態");
        refreshButton.getStyleClass().add("primary-button");
        refreshButton.setPrefHeight(28);
        refreshButton.setOnAction(e -> updateOccupancy(occupancyText, lightIndicator, suggestionText));

        statusPane.getChildren().addAll(
                occupancyText,
                lightIndicator,
                suggestionText,
                spacer1,
                refreshButton
        );
        statusCard.getChildren().add(statusPane);

        // ──────────────────────────────────────────────────────────────────────

        // ─── 二、圖表子卡片 (Chart Sub-Card) ─────────────────────────────────
        VBox chartCard = new VBox(10);
        chartCard.setPadding(new Insets(15));
        chartCard.getStyleClass().add("sub-card");
        chartCard.setFillWidth(true);

        VBox.setVgrow(chartCard, Priority.ALWAYS);

        Label chartTitle = new Label("健身教室進場人數分佈圖");
        chartCard.getChildren().add(chartTitle);

        // 這裡把容器預設高度改小一些（例如 200 或 250），避免圖片撐得過高
        Pane imageContainer = new Pane();
        imageContainer.setMinHeight(0);
        imageContainer.setPrefHeight(200);            // ← 這裡設定「預設顯示高度」
        VBox.setVgrow(imageContainer, Priority.ALWAYS);

        // 讀取本地圖片
        ImageView gymDiagram = new ImageView(
                new Image(new File("src/images/gym_diagram.png").toURI().toString())
        );
        gymDiagram.setPreserveRatio(true);
        gymDiagram.setSmooth(true);

        gymDiagram.fitWidthProperty().bind(imageContainer.widthProperty());
        gymDiagram.fitHeightProperty().bind(imageContainer.heightProperty());

        // 把 ImageView 放到 imageContainer 裡；同一張卡片內的 padding 可以靠 chartCard 設定
        imageContainer.getChildren().add(gymDiagram);

        chartCard.getChildren().add(imageContainer);

        // ──────────────────────────────────────────────────────────────────────

        // ─── 三、會員查詢子卡片 (Member Sub-Card) ─────────────────────────────
        VBox memberCard = new VBox(8);
        memberCard.setPadding(new Insets(15));
        memberCard.getStyleClass().add("sub-card");
        memberCard.setFillWidth(true);

        Label memberTitle = new Label("會員查詢");
        memberTitle.getStyleClass().add("status-title");
        memberCard.getChildren().add(memberTitle);

        HBox memberPane = new HBox(12);
        memberPane.setAlignment(Pos.CENTER_LEFT);

        Label lblId = new Label("會員ID：");
        lblId.getStyleClass().add("status-text");
        lblId.setMinWidth(60);

        TextField idField = new TextField();
        idField.setPrefWidth(140);
        idField.setPrefHeight(26);

        Button checkMembershipButton = new Button("查詢會員");
        checkMembershipButton.getStyleClass().add("secondary-button");
        checkMembershipButton.setPrefHeight(26);
        checkMembershipButton.setOnAction(e -> {
            // 這裡執行查詢資料庫取得剩餘天數，然後把結果顯示在下面的 expiryLabel
            // 目前先示範一個假的 Alert
            Alert alert = new Alert(AlertType.INFORMATION, "此功能尚未實作");
            alert.setTitle("會員查詢");
            alert.setHeaderText(null);
            alert.showAndWait();
        });

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        Label expiryLabel = new Label("剩餘時效：--");
        expiryLabel.getStyleClass().add("status-text");

        memberPane.getChildren().addAll(lblId, idField, checkMembershipButton, spacer2, expiryLabel);
        memberCard.getChildren().add(memberPane);

        // ──────────────────────────────────────────────────────────────────────

        // 把三大區塊加回最外層
        outerCard.getChildren().addAll(statusCard, chartCard, memberCard);

        // ───【注意】如果你一開啟就想顯示最新燈號，可以在這裡先嘗試連資料庫並拿一次資料：
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PW);
            updateOccupancy(occupancyText, lightIndicator, suggestionText);
        } catch (Exception ex) {
            showAlert(AlertType.ERROR, "錯誤", "資料庫連線或更新失敗: " + ex.getMessage());
        }

        return outerCard;
    }

    /**
     * 建立一個簡單的多輪對話 Panel，並為各節點加上 styleClass。
     */
    private BorderPane createChatPane() {
        // 最外層用 card 外觀
        BorderPane root = new BorderPane();
        root.getStyleClass().addAll("card", "chat-root");
        root.setPadding(new Insets(15));

        // ─── 1. 聊天顯示區 (中央)：用 TextArea，並加上自訂的 styleClass
        TextArea chatDisplay = new TextArea();
        chatDisplay.getStyleClass().addAll("chat-display", "text-area-base");
        chatDisplay.setEditable(false);
        chatDisplay.setWrapText(true);
        chatDisplay.setPromptText("對話內容會顯示在這裡……");
        // 也可以把字型大小放到 CSS 裡，這裡先加個預設的 inline style
        chatDisplay.getStyleClass().add("chat-display");
        chatDisplay.setEditable(false);
        chatDisplay.setWrapText(true);
        VBox.setVgrow(chatDisplay, Priority.ALWAYS);
        root.setCenter(chatDisplay);

        // ─── 2. 輸入區 (下方)：HBox + 自訂 styleClass
        HBox inputArea = new HBox(8);
        inputArea.getStyleClass().add("chat-input-area");
        inputArea.setPadding(new Insets(10, 0, 0, 0));
        inputArea.setAlignment(Pos.CENTER_LEFT);

        TextField userInputField = new TextField();
        userInputField.getStyleClass().add("chat-input-field");
        userInputField.setPromptText("請輸入要問 AI 的問題…");
        HBox.setHgrow(userInputField, Priority.ALWAYS);

        Button sendButton = new Button("送出");
        sendButton.getStyleClass().addAll("primary-button", "chat-send-button");
        sendButton.setDisable(false);

        inputArea.getChildren().addAll(userInputField, sendButton);
        root.setBottom(inputArea);

        // ─── 3. 取得 GptChatService
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            chatDisplay.appendText("[錯誤] 未設定 OPENAI_API_KEY\n");
        }
        GptChatService chatService = GptChatService.getInstance(apiKey, "你必須回答有關健身的問題，針對用戶提供任何有關健身與營養的問題給予幫助，如果是無關的問題請回答我不知道");

        // ─── 4. 「送出」按鈕事件
        sendButton.setOnAction(e -> {
            String question = userInputField.getText().trim();
            if (question.isEmpty()) {
                return;
            }
            sendButton.setDisable(true);
            userInputField.setDisable(true);

            chatDisplay.appendText("[User] " + question + "\n");
            userInputField.clear();

            CompletableFuture.supplyAsync(() -> {
                try {
                    return chatService.ask(question);
                } catch (Exception ex) {
                    return "[Error] 呼叫 GptChatService 發生例外: " + ex.getMessage();
                }
            }).whenComplete((reply, ex2) -> {
                Platform.runLater(() -> {
                    chatDisplay.appendText("[Assistant] " + reply + "\n\n");
                    sendButton.setDisable(false);
                    userInputField.setDisable(false);
                    userInputField.requestFocus();
                });
            });
        });

        userInputField.setOnAction(e -> sendButton.fire());

        return root;
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

    /**
     * 從資料庫撈取最新的 real_time_status，並更新燈號顏色、文字提示、佔用人數。
     *
     * @param occupancyText   用來顯示「目前人數: x / y」的 Text 節點
     * @param lightIndicator  用來顯示紅/橙/綠燈的 Circle 節點
     * @param suggestionText  用來顯示建議文字的 Text 節點
     */
    private void updateOccupancy(Text occupancyText, Circle lightIndicator, Text suggestionText) {
        String sql = "SELECT current_people, total_people FROM real_time_status ORDER BY time DESC LIMIT 1";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // 先把所有「顏色相關的 class」先拿掉
            lightIndicator.getStyleClass().removeAll("status-red", "status-orange", "status-green", "status-gray");
            suggestionText.getStyleClass().removeAll("status-red", "status-orange", "status-green", "status-gray");

            if (rs.next()) {
                int current = rs.getInt("current_people");
                int total   = rs.getInt("total_people");

                occupancyText.setText("目前人數: " + current + " / " + total);

                double ratio = (total > 0) ? (double) current / total : 0.0;

                if (ratio > 0.8) {
                    // 加紅燈 class
                    lightIndicator.getStyleClass().add("status-red");
                    suggestionText.setText("建議: 請避開高峰");
                    suggestionText.getStyleClass().add("status-red");

                } else if (ratio > 0.5) {
                    // 加橙燈 class
                    lightIndicator.getStyleClass().add("status-orange");
                    suggestionText.setText("建議: 適中，可斟酌");
                    suggestionText.getStyleClass().add("status-orange");

                } else {
                    // 加綠燈 class
                    lightIndicator.getStyleClass().add("status-green");
                    suggestionText.setText("建議: 舒適，適合前往");
                    suggestionText.getStyleClass().add("status-green");
                }

            } else {
                // 沒資料
                occupancyText.setText("目前人數: -- / --");
                lightIndicator.getStyleClass().add("status-gray");
                suggestionText.setText("建議: --");
                suggestionText.getStyleClass().add("status-gray");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "錯誤", "更新健身房佔用率時發生錯誤:\n" + e.getMessage());
        }
    }



    public static void main(String[] args) {
        launch(args);
    }
}

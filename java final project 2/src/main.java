//import java.util.List;
//
//public class main {
//    public main(){
//        
//    }
//    public static void main(String[] args) {
//        System.out.println("Initializing database connection and tables...");
//        DatabaseManager dbManager = new DatabaseManager();
//
//        // 1. 創建資料表 (僅需在首次執行或表不存在時運行)
//        dbManager.createTables();
//        
//        // 2. (可選) 如果您的 exercises 表是空的，可以從 ExerciseData 填充預設資料
//        // 檢查是否需要填充，例如查詢 exercises 表是否為空
//        if (dbManager.getAllExercisesFromDB().isEmpty()) {
//             System.out.println("Populating default exercises into the database...");
//             dbManager.populateDefaultExercises(); //
//        } else {
//             System.out.println("Exercises table already contains data.");
//        }
//
//        System.out.println("Database setup completed.");
//        System.out.println("Hello, World! Your fitness app is ready to connect to MySQL.");
//
//        // 之後您可以在這裡或其他地方調用 dbManager 的方法來與資料庫互動
//        // 例如，添加一個新的訓練項目
//        /*
//        Exercise newSquat = new Exercise("高腳杯深蹲", BodyPart.LEGS, Exercise.ExerciseType.FREE_WEIGHT);
//        int newExerciseId = dbManager.addExerciseToDB(newSquat);
//        if (newExerciseId != -1) {
//            System.out.println("Successfully added new exercise with ID: " + newExerciseId);
//            Exercise fetchedExercise = dbManager.getExerciseFromDB(newExerciseId);
//            if (fetchedExercise != null) {
//                System.out.println("Fetched exercise: " + fetchedExercise.getName());
//            }
//        }
//        */
//        
//        // 讀取所有運動項目並印出
//        System.out.println("\nAll exercises from DB:");
//        List<Exercise> allExercises = dbManager.getAllExercisesFromDB();
//        if (allExercises.isEmpty()) {
//            System.out.println("No exercises found in the database.");
//        } else {
//            for (Exercise ex : allExercises) {
//                System.out.println(ex.toString()); //
//            }
//        }
//    }
//}

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class main extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		Button btOK = new Button("OK");
		Scene scene = new Scene(btOK, 200, 250);
		primaryStage.setTitle("MyJavaFX");
		primaryStage.setScene(scene); // Ensure the scene is set
		primaryStage.show();          // Ensure the stage is shown
	}

	public static void main(String[] args) { // Use String[] args
		launch(args); // Launch the JavaFX application
	}
}
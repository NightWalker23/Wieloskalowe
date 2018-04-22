package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/MainView.fxml"));
        primaryStage.setTitle("Cellular Automation");
        primaryStage.setScene(new Scene(root, 800, 650));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(685);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
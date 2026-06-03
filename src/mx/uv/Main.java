package mx.uv;

import javafx.application.Application;
import javafx.stage.Stage;
import mx.uv.view.LoginWindow;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        new LoginWindow(primaryStage);
    }
    public static void main(String[] args) {
        launch(args);
    }
}

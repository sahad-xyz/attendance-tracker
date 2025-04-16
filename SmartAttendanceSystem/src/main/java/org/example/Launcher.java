package org.example;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Launcher extends Application {

    @Override
    public void start(Stage primaryStage) {
        Text title = new Text("📘 Smart Attendance System");
        title.setFont(Font.font("Arial", 24));
        title.setStyle("-fx-fill: #F5F5F5;");

        Button loginButton = new Button("Login");
        Button exitButton = new Button("Exit");

        loginButton.setPrefWidth(200);
        exitButton.setPrefWidth(200);

        loginButton.setOnAction(e -> {
            LoginApp loginApp = new LoginApp();
            try {
                loginApp.start(new Stage());
                primaryStage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        exitButton.setOnAction(e -> primaryStage.close());

        VBox layout = new VBox(20, title, loginButton, exitButton);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 50;");

        Scene scene = new Scene(layout, 400, 300);

        // ✅ Apply Tesla-style CSS
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Welcome");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
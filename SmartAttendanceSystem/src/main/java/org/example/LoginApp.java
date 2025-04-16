package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginApp extends Application {

    @Override
    public void start(Stage stage) {
        Label userLabel = new Label("Username:");
        TextField userField = new TextField();
        userField.setPromptText("Enter your username");

        Label passLabel = new Label("Password:");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Enter your password");

        Button loginButton = new Button("Login");

        loginButton.setOnAction(e -> {
            String username = userField.getText();
            String password = passField.getText();

            boolean isValid = DatabaseConnection.validateTeacherLogin(username, password);

            if (isValid) {
                Dashboard.setTeacherUsername(username);
                Dashboard dashboard = new Dashboard();

                try {
                    dashboard.start(new Stage());
                    ((Stage) loginButton.getScene().getWindow()).close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            } else {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Login Failed");
                error.setHeaderText(null);
                error.setContentText("Invalid username or password.");
                error.showAndWait();
            }
        });

        VBox layout = new VBox(15, userLabel, userField, passLabel, passField, loginButton);
        layout.setStyle("-fx-padding: 40; -fx-alignment: center; -fx-spacing: 20;");

        Scene scene = new Scene(layout, 350, 300);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Teacher Login");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
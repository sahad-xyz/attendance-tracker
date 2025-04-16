package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddStudent extends Application {

    @Override
    public void start(Stage stage) {
        Label nameLabel = new Label("Student Name:");
        TextField nameField = new TextField();

        Label courseIdLabel = new Label("Course ID:");
        TextField courseIdField = new TextField();

        Button saveButton = new Button("Add Student");

        saveButton.setOnAction(e -> {
            String name = nameField.getText();
            String courseIdText = courseIdField.getText();

            if (name.isEmpty() || courseIdText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "All fields are required.");
                return;
            }

            try {
                int courseId = Integer.parseInt(courseIdText);
                boolean success = insertStudent(name, courseId);

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Student added successfully.");
                    nameField.clear();
                    courseIdField.clear();
                }

                // if false, insertStudent() already shows error alert

            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Course ID must be a number.");
            }
        });

        VBox layout = new VBox(10, nameLabel, nameField, courseIdLabel, courseIdField, saveButton);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Scene scene = new Scene(layout, 300, 250);
        stage.setScene(scene);
        stage.setTitle("Add Student");
        stage.show();
    }

    private boolean insertStudent(String name, int courseId) {
        String checkSql = "SELECT course_id FROM courses WHERE course_id = ?";
        String insertSql = "INSERT INTO students (name, course_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            // Check if course exists first
            checkStmt.setInt(1, courseId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                showAlert(Alert.AlertType.ERROR, "Course ID " + courseId + " does not exist. Please create the course first.");
                return false;
            }

            // Course is valid, insert student
            PreparedStatement stmt = conn.prepareStatement(insertSql);
            stmt.setString(1, name);
            stmt.setInt(2, courseId);

            int rows = stmt.executeUpdate();
            System.out.println("Rows inserted: " + rows);
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Insert student failed: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database error: " + e.getMessage());
            return false;
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Student Form");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
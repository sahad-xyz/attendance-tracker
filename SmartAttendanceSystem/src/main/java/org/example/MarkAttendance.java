package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class MarkAttendance extends Application {

    private final HashMap<Integer, ComboBox<String>> attendanceMap = new HashMap<>();

    @Override
    public void start(Stage stage) {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-alignment: center;");

        Label courseLabel = new Label("Enter Course ID:");
        TextField courseField = new TextField();
        courseField.setPromptText("e.g., 101");

        Button loadStudentsBtn = new Button("Load Students");
        VBox studentListBox = new VBox(15);

        Button submitBtn = new Button("Submit Attendance");
        submitBtn.setDisable(true);

        layout.getChildren().addAll(courseLabel, courseField, loadStudentsBtn, studentListBox, submitBtn);

        loadStudentsBtn.setOnAction(e -> {
            studentListBox.getChildren().clear();
            attendanceMap.clear();
            submitBtn.setDisable(true);

            String courseIdText = courseField.getText().trim();
            if (courseIdText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Course ID is required.");
                return;
            }

            try {
                int courseId = Integer.parseInt(courseIdText);
                ArrayList<Integer> studentIds = new ArrayList<>();

                Connection conn = DatabaseConnection.getConnection();
                String sql = "SELECT student_id, name FROM students WHERE course_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, courseId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    int studentId = rs.getInt("student_id");
                    String name = rs.getString("name");

                    Label studentLabel = new Label(name);
                    ComboBox<String> statusBox = new ComboBox<>();
                    statusBox.getItems().addAll("Present", "Absent");
                    statusBox.setValue("Present");

                    attendanceMap.put(studentId, statusBox);

                    VBox row = new VBox(5, studentLabel, statusBox);
                    studentListBox.getChildren().add(row);

                    studentIds.add(studentId);
                }

                if (studentIds.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "No students found for this Course ID.");
                } else {
                    submitBtn.setDisable(false);
                }

            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Course ID must be a number.");
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Database error occurred.");
                ex.printStackTrace();
            }
        });

        submitBtn.setOnAction(e -> {
            try {
                Connection conn = DatabaseConnection.getConnection();
                String insertSQL = "INSERT INTO attendance_records (student_id, course_id, date, status) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(insertSQL);

                int courseId = Integer.parseInt(courseField.getText().trim());
                LocalDate date = LocalDate.now();

                for (int studentId : attendanceMap.keySet()) {
                    String status = attendanceMap.get(studentId).getValue();
                    stmt.setInt(1, studentId);
                    stmt.setInt(2, courseId);
                    stmt.setDate(3, Date.valueOf(date));
                    stmt.setString(4, status);
                    stmt.addBatch();
                }

                stmt.executeBatch();

                Alert confirm = new Alert(Alert.AlertType.INFORMATION);
                confirm.setTitle("Attendance Submitted");
                confirm.setHeaderText(null);
                confirm.setContentText("Attendance successfully recorded for today.");
                confirm.showAndWait();

                studentListBox.getChildren().clear();
                attendanceMap.clear();
                submitBtn.setDisable(true);

            } catch (SQLIntegrityConstraintViolationException ex) {
                showAlert(Alert.AlertType.ERROR, "Attendance already recorded today for one or more students.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Failed to record attendance.");
                ex.printStackTrace();
            }
        });

        Scene scene = new Scene(layout, 550, 600);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Mark Attendance");
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Mark Attendance");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
package org.example;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class AttendanceReport extends Application {

    private final TableView<String> table = new TableView<>();
    private final ObservableList<String> reportList = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) {
        Label courseIdLabel = new Label("Enter Course ID:");
        TextField courseIdField = new TextField();
        courseIdField.setPromptText("e.g., 101");

        Button loadBtn = new Button("Load Report");

        loadBtn.setOnAction(e -> {
            String input = courseIdField.getText();
            if (input.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Please enter a course ID.");
                return;
            }

            try {
                int courseId = Integer.parseInt(input);
                loadAttendance(courseId);
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Course ID must be a number.");
            }
        });

        table.setPlaceholder(new Label("No attendance records found."));
        TableColumn<String, String> column = new TableColumn<>("Attendance Records");
        column.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        column.setPrefWidth(450);
        table.getColumns().add(column);
        table.setItems(reportList);

        VBox layout = new VBox(20, courseIdLabel, courseIdField, loadBtn, table);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-alignment: center;");

        Scene scene = new Scene(layout, 600, 500);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Attendance Report");
        stage.show();
    }

    private void loadAttendance(int courseId) {
        reportList.clear();

        String sql = """
            SELECT s.name, ar.date, ar.status
            FROM attendance_records ar
            JOIN students s ON ar.student_id = s.student_id
            WHERE ar.course_id = ?
            ORDER BY ar.date DESC;
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();

            boolean found = false;
            while (rs.next()) {
                found = true;
                String name = rs.getString("name");
                String date = rs.getString("date");
                String status = rs.getString("status");

                reportList.add(date + " - " + name + " - " + status);
            }

            if (!found) {
                showAlert(Alert.AlertType.INFORMATION, "No attendance records found for this course.");
            }

        } catch (SQLException e) {
            System.out.println("Error loading attendance: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database error: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Attendance Report");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
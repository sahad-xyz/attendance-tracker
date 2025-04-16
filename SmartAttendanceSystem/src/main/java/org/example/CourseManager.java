package org.example;

import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class CourseManager extends Application {

    private final TableView<Course> table = new TableView<>();
    private final ObservableList<Course> courseList = FXCollections.observableArrayList();

    public static class Course {
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty name;
        private final SimpleStringProperty instructor;

        public Course(int id, String name, String instructor) {
            this.id = new SimpleIntegerProperty(id);
            this.name = new SimpleStringProperty(name);
            this.instructor = new SimpleStringProperty(instructor);
        }

        public int getId() { return id.get(); }
        public String getName() { return name.get(); }
        public String getInstructor() { return instructor.get(); }
    }

    @Override
    public void start(Stage stage) {
        Label nameLabel = new Label("Course Name:");
        TextField nameField = new TextField();
        nameField.setPromptText("e.g., Java Basics");

        Label instructorLabel = new Label("Instructor:");
        TextField instructorField = new TextField();
        instructorField.setPromptText("e.g., Mr. Chowdhury");

        Button addButton = new Button("Add Course");

        addButton.setOnAction(e -> {
            String courseName = nameField.getText();
            String instructor = instructorField.getText();

            if (courseName.isEmpty() || instructor.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "All fields are required.");
                return;
            }

            boolean success = insertCourse(courseName, instructor);
            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Course added successfully.");
                alert.showAndWait();

                nameField.clear();
                instructorField.clear();
                loadCourses();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to add course.");
            }
        });

        TableColumn<Course, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Course, String> nameCol = new TableColumn<>("Course Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<Course, String> instructorCol = new TableColumn<>("Instructor");
        instructorCol.setCellValueFactory(new PropertyValueFactory<>("instructor"));
        instructorCol.setPrefWidth(150);

        TableColumn<Course, Void> deleteCol = new TableColumn<>("Action");
        deleteCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("Delete");

            {
                deleteBtn.setOnAction(e -> {
                    Course course = getTableView().getItems().get(getIndex());

                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Confirm Deletion");
                    confirm.setHeaderText(null);
                    confirm.setContentText("Are you sure you want to delete this course?\nCourse: " + course.getName());

                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            deleteCourse(course.getId());
                            loadCourses();
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });

        table.getColumns().setAll(idCol, nameCol, instructorCol, deleteCol);
        table.setItems(courseList);
        table.setPlaceholder(new Label("No courses found."));

        VBox layout = new VBox(15, nameLabel, nameField, instructorLabel, instructorField, addButton, table);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-alignment: center;");

        loadCourses();

        Scene scene = new Scene(layout, 600, 500);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Course Manager");
        stage.show();
    }

    private boolean insertCourse(String name, String instructor) {
        String sql = "INSERT INTO courses (course_name, instructor) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, instructor);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Insert course failed: " + e.getMessage());
            return false;
        }
    }

    private void loadCourses() {
        courseList.clear();
        String sql = "SELECT course_id, course_name, instructor FROM courses";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("course_id");
                String name = rs.getString("course_name");
                String instructor = rs.getString("instructor");
                courseList.add(new Course(id, name, instructor));
            }

        } catch (SQLException e) {
            System.out.println("Load courses failed: " + e.getMessage());
        }
    }

    private void deleteCourse(int courseId) {
        String sql = "DELETE FROM courses WHERE course_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Could not delete course: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Course Manager");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
package org.example;

import javafx.util.converter.IntegerStringConverter;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class ManageStudents extends Application {

    private final TableView<Student> table = new TableView<>();
    private final ObservableList<Student> studentList = FXCollections.observableArrayList();

    public static class Student {
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty name;
        private final SimpleIntegerProperty courseId;

        public Student(int id, String name, int courseId) {
            this.id = new SimpleIntegerProperty(id);
            this.name = new SimpleStringProperty(name);
            this.courseId = new SimpleIntegerProperty(courseId);
        }

        public int getId() { return id.get(); }
        public String getName() { return name.get(); }
        public int getCourseId() { return courseId.get(); }

        public void setName(String name) { this.name.set(name); }
        public void setCourseId(int courseId) { this.courseId.set(courseId); }
    }

    @Override
    public void start(Stage stage) {
        TextField nameField = new TextField();
        nameField.setPromptText("Student Name");

        TextField courseIdField = new TextField();
        courseIdField.setPromptText("Course ID");

        Button addButton = new Button("Add Student");

        addButton.setOnAction(e -> {
            String name = nameField.getText();
            String courseIdText = courseIdField.getText();

            if (name.isEmpty() || courseIdText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "All fields are required.");
                return;
            }

            try {
                int courseId = Integer.parseInt(courseIdText);
                if (!courseExists(courseId)) {
                    showAlert(Alert.AlertType.ERROR, "Course ID does not exist.");
                    return;
                }

                insertStudent(name, courseId);
                nameField.clear();
                courseIdField.clear();
                showAlert(Alert.AlertType.INFORMATION, "Student added successfully.");
                loadStudents();

            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Course ID must be a number.");
            }
        });

        TableColumn<Student, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setOnEditCommit(e -> {
            Student student = e.getRowValue();
            student.setName(e.getNewValue());
            updateStudent(student);
        });

        TableColumn<Student, Integer> courseCol = new TableColumn<>("Course ID");
        courseCol.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        courseCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        courseCol.setOnEditCommit(e -> {
            Student student = e.getRowValue();
            int newCourseId = e.getNewValue();
            if (!courseExists(newCourseId)) {
                showAlert(Alert.AlertType.ERROR, "Course ID does not exist.");
                loadStudents(); // revert change
                return;
            }
            student.setCourseId(newCourseId);
            updateStudent(student);
        });

        TableColumn<Student, Void> deleteCol = new TableColumn<>("Action");
        deleteCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("Delete");

            {
                deleteBtn.setOnAction(e -> {
                    Student student = getTableView().getItems().get(getIndex());

                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Confirm Deletion");
                    confirm.setHeaderText(null);
                    confirm.setContentText("Are you sure you want to delete this student?\nName: " + student.getName());

                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            deleteStudent(student.getId());
                            loadStudents();
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

        table.setEditable(true);
        table.setItems(studentList);
        table.getColumns().addAll(idCol, nameCol, courseCol, deleteCol);
        table.setPlaceholder(new Label("No students found."));

        HBox form = new HBox(10, nameField, courseIdField, addButton);
        VBox layout = new VBox(20, form, table);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-alignment: center;");

        Scene scene = new Scene(layout, 700, 450);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Manage Students");
        stage.show();

        loadStudents();
    }

    private void loadStudents() {
        studentList.clear();
        String sql = "SELECT student_id, name, course_id FROM students";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                studentList.add(new Student(
                        rs.getInt("student_id"),
                        rs.getString("name"),
                        rs.getInt("course_id")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Load students failed: " + e.getMessage());
        }
    }

    private boolean courseExists(int courseId) {
        String sql = "SELECT course_id FROM courses WHERE course_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Course validation failed: " + e.getMessage());
            return false;
        }
    }

    private void insertStudent(String name, int courseId) {
        String sql = "INSERT INTO students (name, course_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, courseId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to insert student: " + e.getMessage());
        }
    }

    private void updateStudent(Student student) {
        String sql = "UPDATE students SET name = ?, course_id = ? WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getName());
            stmt.setInt(2, student.getCourseId());
            stmt.setInt(3, student.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to update student: " + e.getMessage());
        }
    }

    private void deleteStudent(int studentId) {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to delete student: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Manage Students");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
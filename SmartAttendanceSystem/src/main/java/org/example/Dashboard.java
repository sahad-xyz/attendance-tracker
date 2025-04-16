package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Dashboard extends Application {

    private static String teacherUsername;

    public static void setTeacherUsername(String username) {
        teacherUsername = username;
    }

    @Override
    public void start(Stage stage) {
        Label welcome = new Label("Welcome, " + teacherUsername + "!");

        Button manageStudentsBtn = new Button("Manage Students");
        Button markAttendanceBtn = new Button("Mark Attendance");
        Button viewReportBtn = new Button("View Attendance Report");
        Button courseManagerBtn = new Button("Manage Courses");

        // Manage Students Action
        manageStudentsBtn.setOnAction(e -> {
            ManageStudents manageStudents = new ManageStudents();
            try {
                manageStudents.start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Mark Attendance Action
        markAttendanceBtn.setOnAction(e -> {
            MarkAttendance markAttendance = new MarkAttendance();
            try {
                markAttendance.start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // View Attendance Report Action
        viewReportBtn.setOnAction(e -> {
            AttendanceReport report = new AttendanceReport();
            try {
                report.start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Course Manager Action
        courseManagerBtn.setOnAction(e -> {
            CourseManager courseManager = new CourseManager();
            try {
                courseManager.start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox layout = new VBox(15, welcome, manageStudentsBtn, markAttendanceBtn, viewReportBtn, courseManagerBtn);
        layout.setStyle("-fx-padding: 40; -fx-alignment: center; -fx-spacing: 20;");

        Scene scene = new Scene(layout, 500, 400);

        // ✅ Apply Tesla-style CSS
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Dashboard");
        stage.show();
    }
}
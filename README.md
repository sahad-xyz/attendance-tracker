# 📘 Smart Attendance System 📘

A JavaFX-based desktop application that allows teachers to manage student attendance efficiently through a user-friendly GUI, with persistent data storage powered by MySQL.

---

## Project Overview

This system enables teachers to:
- Manage courses and student enrollment
- Record attendance by date
- View attendance reports
- Use a modern UI inspired by Tesla's app (dark mode, minimal, sleek)
- Launch the system via a standalone `.exe` file for ease of use

---

## Technologies Used

- Java 17+
- JavaFX (UI)
- MySQL (Backend Database)
- IntelliJ IDEA (Development Environment)
- Launch4j (Packaging to `.exe`)
- Git & GitHub (Version Control)

---

## Project Structure

```
SmartAttendanceSystem/
├── src/
│   └── main/java/org/example/
│       ├── AddStudent.java
│       ├── AttendanceReport.java
│       ├── CourseManager.java
│       ├── Dashboard.java
│       ├── DatabaseConnection.java
│       ├── Launcher.java
│       ├── LoginApp.java
│       ├── ManageStudents.java
│       ├── MarkAttendance.java
│       ├── TestDB.java
│       └── Main.java
├── resources/
│   └── style.css
├── docs/
│   └── User_Implementation_Manual.txt
├── pom.xml
└── README.md
```

---

## Features

- **User Login**: Teachers log in securely to access features.
- **Course Management**: Add, delete, and list courses.
- **Student Management**: Add, edit, or remove students and assign them to courses.
- **Attendance Marking**: Mark Present/Absent for students daily.
- **Attendance Reports**: View all attendance data by course/date.
- **Confirmation Dialogs**: For important actions like delete.
- **Dynamic TableViews**: Edit in-place, hover styling, and scrollable.
- **Launcher**: Starts with a splash screen and login screen.
- **Modern UI**: Tesla app-inspired dark styling and UI polish.
- **.exe Output**: Standalone runnable file with custom icon.

---

## Object-Oriented Programming Concepts

- **Inheritance**: `Student` and `Course` objects extend `BaseEntity`.
- **Encapsulation**: Fields are private with public getters/setters.
- **Polymorphism**: `AlertHelper.show()` accepts multiple alert types.
- **Abstraction**: Database interactions are abstracted in `DatabaseConnection.java`.

---

## Database Schema

- **Tables**: `teachers`, `students`, `courses`, `attendance_records`
- **Relations**: 
  - `students.course_id` → `courses.course_id`
  - `attendance_records.student_id` → `students.student_id`

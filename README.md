# TrackIT - Smart Attendance Management System

## Team Members

| Name           | Register Number  |
| -------------- | ---------------- |
| Tushyent N P   | 3122 24 5001 189 |
| Thariq J       | 3122 24 5001 185 |
| Yogadharshan K | 3122 24 5001 201 |

---

## About TrackIT

TrackIT is a desktop-based **Student Attendance Management System** developed using **Java Swing/AWT** to automate attendance tracking and reporting in educational institutions.

The system provides a user-friendly interface for both faculty and students, offering real-time attendance tracking, intelligent analytics, and report generation.

### Key Highlights

* Real-time attendance tracking with multiple status options
* Separate portals for students and faculty
* Analytics for bunkable and required classes
* Visual timetable management with color-coded subjects
* Comprehensive report generation
* Flat-file storage for easy deployment (no database required)

---

## How to Run TrackIT

### Prerequisites

* Java Development Kit (JDK) 17 or higher
* Windows/Linux/Mac operating system
* Terminal or Command Prompt access

---

### Option 1: Using Batch Files (Windows)

#### Step 1: Clean Previous Build

```bash
clear.bat
```

Removes all previously compiled `.class` files from the `bin` directory.

#### Step 2: Compile the Project

```bash
compile.bat
```

Compiles all Java source files from the `src` directory and places the compiled classes in the `bin` directory.

#### Step 3: Run the Application

```bash
run.bat
```

Launches the TrackIT application and opens the login window.

---

### Option 2: Manual Compilation and Execution

#### Step 1: Clean (Optional)

```bash
rmdir /s /q bin
mkdir bin
```

#### Step 2: Compile

```bash
javac -d bin -sourcepath src src\com\trackit\main\TrackITApplication.java
```

#### Step 3: Run

```bash
java -cp bin com.trackit.main.TrackITApplication
```

---

## Default Credentials

### Faculty Login

* Faculty ID: `F001`
* Password: `pass123`

### Student Login

* Roll Number: `3122 24 5001 189`
* Password: `pass123`

> Additional credentials can be found under the `data/` directory.

---

## Project Structure

```
TrackIT/
│
├── src/
│   └── com/trackit/
│       ├── main/
│       │   └── TrackITApplication.java
│       ├── models/
│       ├── views/
│       ├── dao/
│       ├── services/
│       ├── components/
│       ├── utils/
│       └── exceptions/
│
├── data/
│   ├── students.txt
│   ├── faculty.txt
│   ├── subjects.txt
│   ├── student_timetables.txt
│   ├── faculty_timetables.txt
│   ├── attendance.txt
│   └── reports/
│
├── bin/
│
├── compile.bat
├── run.bat
├── clear.bat
└── README.md
```

---

## Technologies Used

* **Language:** Java 17
* **GUI Framework:** Swing/AWT
* **Data Storage:** Flat files (pipe-delimited text)
* **Build Tools:** Batch scripts (`compile.bat`, `run.bat`, `clear.bat`)
* **IDE Compatibility:** VS Code, IntelliJ IDEA, Eclipse

---

## Architecture Overview

### Layers

* **View Layer:** Swing-based UI components
* **Service Layer:** Business logic and orchestration
* **DAO Layer:** Data access and persistence
* **Model Layer:** Domain entities (POJOs)
* **Utility Layer:** File I/O, validation, color themes

### Design Principles

* Separation of Concerns
* Single Responsibility Principle
* DRY (Don't Repeat Yourself)
* Encapsulation and abstraction

---

## Object-Oriented Concepts

* **Encapsulation:** DAOs encapsulate file I/O; models protect data using private fields.
* **Inheritance:** Faculty and Student extend a common User base class.
* **Abstraction:** Services and DAOs abstract business and persistence logic.
* **Polymorphism:** Common interfaces and runtime binding (e.g., LoginManager returning User type).
* **Composition:** Timetable and Dashboard composed of multiple objects/panels.

---

## Data Formats

### students.txt

```
RollNo|Name|Password|Department|Section
```

Example: `3122 24 5001 189|Tushyent N P|pass123|CSE|A`

### faculty.txt

```
FacultyID|Name|Password|Department
```

Example: `F001|Dr. Kumar|pass123|CSE`

### subjects.txt

```
SubjectCode|Name|FacultyID|Department|Section|TotalHours
```

Example: `CS101|Data Structures|F001|CSE|A|60`

### attendance.txt

```
RollNo|SubjectCode|Date(yyyy-MM-dd)|Period|Status
```

Example: `3122 24 5001 189|CS101|2025-01-15|1|P`

Status Codes:

* P = Present
* A = Absent
* OD = On Duty
* NC = No Class

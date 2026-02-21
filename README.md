# IEMS — Integrated Employee Management System

A JavaFX desktop application for managing employee records, attendance tracking via fingerprint biometrics, shift scheduling, and automated DTR (Daily Time Record) generation. Built for local government unit (LGU) HR operations.

![Java](https://img.shields.io/badge/Java-21-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-21.0.2-blue)
![Maven](https://img.shields.io/badge/Maven-Build-red)
![MySQL](https://img.shields.io/badge/MySQL-8.x-blue)
![Build](https://github.com/Raver-Miradora/IEMS/actions/workflows/build.yml/badge.svg)
![License](https://img.shields.io/badge/License-Proprietary-lightgrey)

---

## Features

- **Employee Management** — Full CRUD for employee profiles including personal data sheets, educational background, civil service eligibility, work experience, and document uploads
- **Fingerprint Biometrics** — DigitalPersona U.are" U SDK integration for fingerprint enrollment, identification, and verification
- **Attendance Tracking** — Real-time time-in/time-out with fingerprint scanning, with audio feedback (TTS prompts on scan)
- **Shift Management** — Configurable work shifts with overtime authorization support
- **DTR Generation** — Automated Daily Time Record report generation in DOCX format
- **Attendance Reports** — Monthly attendance summaries with Excel export (Apache POI)
- **PDF Reports** — Employee data exports via iText and PDFBox
- **Holiday Calendar** — Special calendar management for holidays and non-working days
- **Time-off / Leave** — Employee time-off request and tracking
- **Department & Position Management** — Organizational structure configuration
- **Assignment Tracking** — Task/assignment management per employee
- **Daily Notices & Quotes** — Bulletin board for office-wide notices and motivational quotes
- **Activity Logging** — Audit trail of all system actions
- **System Backup** — Database backup functionality
- **Theme Support** — Multiple UI themes: Cupertino (Light/Dark), Dracula, Nord (Light/Dark), Primer (Light/Dark)
- **Adaptive Scaling** — UI auto-scales from 1920×1080 design resolution to any screen size
- **OCR** — Tesseract-based optical character recognition for document scanning

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| UI Framework | JavaFX 21.0.2 + FXML |
| UI Components | ControlsFX, GemsFX, Ikonli Icons |
| Build Tool | Apache Maven |
| Database | MySQL 8.x |
| DB Connector | MySQL Connector/J 8.2.0 |
| Biometrics | DigitalPersona U.are.U SDK |
| PDF Generation | iText 5, Apache PDFBox 2 |
| Excel Export | Apache POI 5 |
| OCR | Tess4j 5 (Tesseract) |
| HTTP Client | Retrofit 2 + OkHttp 4 |
| Security | jBCrypt (password hashing), AES encryption |
| Serialization | Gson |

## Project Structure

```
IEMSmaven/
├── pom.xml                          # Maven build config
├── IEMS DB/                         # MySQL database schema (20 SQL files)
├── tessdata/                        # Tesseract OCR trained data
├── lib/                             # Custom JARs (not on Maven Central)
│   ├── README.md                    # JAR list and install instructions
│   └── install-libs.ps1             # Auto-install script
├── .github/workflows/build.yml      # CI/CD pipeline
└── src/main/
    ├── java/com/raver/iemsmaven/
    │   ├── Main.java                # JavaFX Application entry point
    │   ├── RunMain.java             # Bootstrap launcher
    │   ├── Controller/              # 33 FXML controllers
    │   ├── Model/                   # 22 data model classes
    │   ├── Fingerprint/             # Biometric scanning (10 classes)
    │   ├── Session/                 # User session management
    │   └── Utilities/               # 15 utility classes
    │       ├── DatabaseUtil.java    # MySQL connection manager
    │       ├── Config.java          # Properties loader (secrets)
    │       ├── Encryption.java      # SHA-256 & AES encryption
    │       ├── SecurityUtil.java    # Security helpers
    │       ├── APIClient.java       # Retrofit HTTP client
    │       └── ...
    └── resources/
        ├── config.properties.example  # Template for secrets (copy to config.properties)
        ├── View/                      # 35 FXML view files
        ├── Style/                     # 12 CSS theme files
        ├── Images/                    # UI icons and assets
        ├── Audio/                     # Sound effects & TTS audio
        ├── DTR.docx                   # DTR report template
        └── LICENSE.txt                # EULA
```

## Prerequisites

- **Java 21** (JDK 21+)
- **Maven 3.8+**
- **MySQL 8.x** — running on `localhost:3306`
- **DigitalPersona U.are.U SDK** — for fingerprint hardware (optional; app works without scanner)
- **Tesseract OCR** — `tessdata/eng.traineddata` included in repo

## Setup & Installation

### 1. Clone the repository

```bash
git clone https://github.com/Raver-Miradora/IEMS.git
cd IEMS
```

### 2. Set up the database

Create a MySQL database named `iems_db` and import the schema files:

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS iems_db;"
cd "IEMS DB"
for %f in (*.sql) do mysql -u root -p iems_db < "%f"
```

Or import them manually via MySQL Workbench or phpMyAdmin.

> **Note:** If migrating from an older setup using `test19`, update your `config.properties` database URL accordingly.

### 3. Configure secrets

```bash
copy src\main\resources\config.properties.example src\main\resources\config.properties
```

Edit `src/main/resources/config.properties` and set your values:

```properties
database.secret.key=YOUR_ACTUAL_SECRET_KEY_HERE
database.url=jdbc:mysql://localhost:3306/iems_db
database.user=root
database.password=
```

### 4. Install custom dependencies

Some dependencies are local JARs not available on Maven Central. Place them in the `lib/` folder using the naming convention `groupId--artifactId--version.jar` and run the install script:

```powershell
.\lib\install-libs.ps1
```

See [`lib/README.md`](lib/README.md) for the full list of required JARs and manual install instructions.

### 5. Build & Run

```bash
# Build
mvn clean package

# Run with JavaFX
mvn javafx:run

# Package as Windows installer (MSI)
mvn clean package jpackage:jpackage
```

## Database Schema

The `IEMS DB/` folder contains individual SQL files for each table:

| Table | Description |
|---|---|
| `user` | Employee/admin accounts and personal info |
| `attendance` | Time-in / time-out records |
| `fingerprint` | Biometric fingerprint templates |
| `department` | Department definitions |
| `position` | Job positions |
| `shift` | Work shift configurations |
| `assignment` | Employee task assignments |
| `timeoff` / `user_timeoff` | Leave/time-off types and requests |
| `special_calendar` / `user_calendar` | Holidays and custom calendar events |
| `notices` / `quotes` | Daily notices and motivational quotes |
| `child` | Employee children records (for PDS) |
| `civil_service_eligibility` | CS eligibility records |
| `educational_background` | Education history |
| `work_experience` | Work history |
| `employee_documents` | Uploaded documents |
| `activity_logs` | System audit trail |

## License

Proprietary — RRK Solutions. See [LICENSE.txt](src/main/resources/LICENSE.txt) for the full EULA.

## Author

**Raver Miradora** — [GitHub](https://github.com/Raver-Miradora)

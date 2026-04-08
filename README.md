# EchoRoll v2 🦉

EchoRoll v2 is a modern, offline-first Android application designed to transform manual college attendance tracking into a seamless, automated, and visually engaging experience. 

Say goodbye to messy spreadsheets and notebook scribbles. EchoRoll empowers you to set attendance goals, map your weekly routine, handle unexpected schedule changes, and instantly view your academic standing through a beautifully crafted, highly intuitive Material 3 interface.

## 🚀 Core Features

* **Dynamic Class Replacements:** Life happens. Seamlessly swap scheduled lectures with replacement subjects directly from the Today tab. The app handles the complex logic of "Cancelled" vs. "Replacement" classes to ensure your statistics remain flawlessly accurate.
* **Smart Exam & Notification Engine:** EchoRoll works in the background to remind you of your classes. It even integrates with your Exam schedule, intelligently silencing class-end reminders on test days to prevent interruptions.
* **In-App Updater (GitHub Integration):** Never miss an update. EchoRoll silently pings the GitHub Releases API on startup and prompts users with a sleek Material dialog when a new version is available.
* **Interactive Dashboard:** View real-time statistics via striking circular progress indicators that calculate classes attended versus missed. It instantly translates raw numbers into actionable insights ("On track!" vs. "Need to attend").
* **Visual Subject & Routine Setup:** Add specific subjects detailing the code, professor, and required attendance percentages. Map exact class times using Material 3 time pickers to generate a fully integrated weekly timetable.
* **Frictionless UI:** Smooth swipe-to-delete gestures, dynamic chronological sorting, intelligent error validation, and a custom dark-mode aesthetic designed for daily student use.

## 🛠️ Tech Stack & Architecture

This project is built from the ground up using modern Android development standards:

* **Language:** Kotlin
* **UI Framework:** Jetpack Compose (Material 3)
* **Local Database:** Room (SQLite abstraction) with safe schema migrations.
* **Architecture:** MVVM (Model-View-ViewModel) utilizing Kotlin Coroutines and StateFlow for robust, reactive UI state management.
* **Background Processing:** `WorkManager` and `AlarmManager` for precise, reliable daily notification scheduling and background update checking.

## 💻 Getting Started for Developers

To clone and run this project locally via Android Studio:

1. Clone the repository:
   `git clone https://github.com/YourUsername/EchoRollV2.git`
2. Open the project in **Android Studio**.
3. Allow Gradle to sync the project dependencies.
4. Click the **Run** button to deploy the app to an emulator or physical device.

## 👨‍💻 Author

Developed by **Kumaresh Basu**.

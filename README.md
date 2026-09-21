# Habit Tracker

Android-first habit and task tracker built with Kotlin and Jetpack Compose.

## Included
- Habits with streak counting and completion history
- Visual weekly progress and heatmap-style history
- Nested to-dos, priorities, tags, due dates and search
- Dark/light/system theme support
- Offline-first Room database
- Notification-ready architecture
- Home-screen widget-ready dependency setup
- Firebase/Google sign-in dependencies for cross-device sync

## Open and run
1. Open this repository in Android Studio.
2. Let Gradle sync.
3. Run the app on Android 8.0+.
4. The local/offline app works without Firebase configuration.

## Enable Google sign-in and cloud sync
Create a Firebase Android app using package `za.co.habittracker`, enable Google authentication and Firestore, then download `google-services.json` into `app/`. The Google Services plugin is intentionally not applied until that file is present, so the project remains runnable locally.

## Architecture
Room is the source of truth. UI observes Room flows, which keeps the application usable offline. Cloud sync can mirror user-owned records to Firestore when authentication is configured.

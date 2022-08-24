# V2 Survey App

A simple survey app made with all the Modern Android Development tools (Flow, Jetpack ViewModel,
Room, and Retrofit), and built with Modern Android App Architecture (MVVM + Uncle Bob's Clean Arch).

## Features

- Get a survey that the user can answer.
- Can answer question and answer.
- Can also view previously answered questions.
- Material Design 3 and auto dark and light mode.

## Tech Stack

- Android SDK, for building app for android platform.
- Kotlin Language
- Kotlin Coroutines, for multithreading
- Retrofit and GSON, to handle network request and network response.
- Room, for saving data locally.
- Dagger-Hilt, for dependency injection.
- Timber, for better and improved logging.
- Easy Permission, for handling permission.

## Run Locally

Clone the project

```bash
  git clone https://github.com/kabirnayeem99/v2_survey_android_app.git
```

Go to the project directory

```bash
  cd v2_survey_android_app
```

To build a debug APK, open a command line and navigate to the root of your project directory. To
initiate a debug build, invoke the assembleDebug task:

```bash
  gradlew assembleDebug
```

This creates an APK named v2_survey_android_app-debug.apk in
v2_survey_android_app/build/outputs/apk/. The file is already signed with the debug key and aligned
with zipalign, so you can immediately install it on a device.

Or to build the APK and immediately install it on a running emulator or connected device, instead
invoke installDebug

```bash
  gradlew installDebug
```

## Authors

- [@kabirnayeem99](https://www.linkedin.com/in/kabirnayeem99/)


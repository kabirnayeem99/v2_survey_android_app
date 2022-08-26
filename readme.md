# V2 Survey App

A simple survey app made with all the Modern Android Development tools (Flow, Jetpack ViewModel,
Room, and Retrofit), and built with Modern Android App Architecture (MVVM + Uncle Bob's Clean Arch).
It is also well-tested by Unit-tests, and Instrumental tests.

## Features

- Get a survey that the user can answer.
- Can answer question and answer.
- Can also view previously answered questions.
- Material Design 3 and auto dark and light mode.

## Demo

<a href="https://ibb.co/Qbkc4BC"><img src="https://i.ibb.co/2nS6T2s/Screenshot-20220825-005210.png" alt="Screenshot-20220825-005210" border="0" width="200" height="340"></a>
<a href="https://ibb.co/cyL5ggH"><img src="https://i.ibb.co/9ptX88L/Screenshot-20220825-005226.png" alt="Screenshot-20220825-005226" border="0" width="200" height="340"></a>
<a href="https://ibb.co/pPScrw5"><img src="https://i.ibb.co/0XxTV2v/Screenshot-20220825-005308.png" alt="Screenshot-20220825-005308" border="0" width="200" height="340"></a>
<a href="https://ibb.co/VW4wLZ8"><img src="https://i.ibb.co/7JmkS5F/Screenshot-20220825-005332.png" alt="Screenshot-20220825-005332" border="0" width="200" height="340"></a>
<a href="https://ibb.co/yq6WDSh"><img src="https://i.ibb.co/L1xnM5Q/Screenshot-20220825-005344.png" alt="Screenshot-20220825-005344" border="0" width="200" height="340"></a>
<a href="https://ibb.co/pzyf9Pv"><img src="https://i.ibb.co/6PBHfZR/Screenshot-20220825-005358.png" alt="Screenshot-20220825-005358" border="0" width="200" height="340"></a>

## Tech Stack

- **Android SDK**, for building app for android platform.
- **Kotlin Language**
- **Kotlin Coroutines**, for multithreading
- **Retrofit and GSON**, to handle network request and network response.
- **Room**, for saving data locally.
- **Dagger-Hilt**, for dependency injection.
- **Timber**, for better and improved logging.
- **Easy Permission**, for handling permission.
- **JUnit and MockK**, for unit testing.
- **Espresso**, for instrumental and UI testing.

## Run Locally

Clone the project

```bash
  git clone https://github.com/kabirnayeem99/v2_survey_android_app.git
```

Go to the project directory

```bash
  cd v2_survey_android_app
```

To make sure everything is working, first run the unit tests. Invoke gradle test:

```bash
  gradlew test
```

And then invoke instrumental test:

```bash
  gradlew connectedAndroidTest
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


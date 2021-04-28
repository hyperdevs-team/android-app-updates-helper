# Android App Updates Helper
[![Release](https://jitpack.io/v/bq/android-app-updates-helper.svg)](https://jitpack.io/#bq/android-app-updates-helper)

This utility library aims to help Android developers to use the [Google Play In-App Updates API](https://developer.android.com/guide/app-bundle/in-app-updates) in an easy way.

> It's highly encouraged that you first read the [Google Play In-App Updates API](https://developer.android.com/guide/app-bundle/in-app-updates) documentation before using this library in order to understand the core concepts of the library.

## Setting Up
In your main `build.gradle`, add [jitpack.io](https://jitpack.io/) repository in the `allprojects` block:

<details open><summary>Groovy</summary>

```groovy
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
</details>

<details><summary>Kotlin</summary>

```kotlin
allprojects {
    repositories {
        maven(url = "https://jitpack.io")
    }
}
```
</details>


Add the following dependencies to your app or library's `build.gradle`:

<details open><summary>Groovy</summary>

```groovy
dependencies {
    implementation "com.github.bq:android-app-updates-helper:1.1.0"
}
```
</details>


<details><summary>Kotlin</summary>

```kotlin
dependencies {
    implementation("com.github.bq:android-app-updates-helper:1.1.0")
}
```
</details>

You'll also need to add support for Java 8 in your project. To do so:
<details open><summary>Groovy</summary>

```groovy
android {
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
```
</details>

<details><summary>Kotlin</summary>

```kotlin
android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
```
</details>

## How to use
* Create a new _AppUpdatesHelper_.
* Start listening for app update changes with _AppUpdatesHelper.startListening()_, for example in _Activity.onCreate()_ or in _Fragment.onViewCreated()_.
* Stop listening for app update changes with _AppUpdatesHelper.stopListening()_ in _Activity.onDestroy()_ or in _Fragment.onDestroyView()_.
* Request app update information with _AppUpdatesHelper.getAppUpdateInfo()_.
* Request a flexible or immediate update with _AppUpdatesHelper.startFlexibleUpdate()_ or _AppUpdatesHelper.startImmediateUpdate()_

Check the [example app](app) for more implementation details about [flexible](app/src/main/kotlin/com/bq/appupdateshelper/flexible/FlexibleUpdateActivity.kt)
and [immediate](app/src/main/kotlin/com/bq/appupdateshelper/immediate/ImmediateUpdateActivity.kt) updates. 

You can also use a [fake implementation](app/src/main/kotlin/com/bq/appupdateshelper/fake/FakeUpdateActivity.kt) to test in-app updates.

Keep in mind that you may not see in-app updates if these conditions don't match:
* The package name of the app is **exactly** the one you'd like to test in-app updates with.
* The app must be signed with the same keys that you used to sign the app you want to test in-app updates with.
* If the app is not published yet or you want to test with internal app sharing or closed tracks, 
ensure that any of your Google accounts in your device has access to said app in Google Play Store.
* Check if the Google Play Store displays updates for the app you want to use to test in-app updates.

Please ensure that all conditions apply when using this library in order to avoid unnecessary headaches.

### Using the example app
In order to ease using the example app with the sample data of your own app, 
you can create an `app_config.properties` file in the root of the project with the following content:
```properties
applicationId=your.application.id
keystorePath=/full/path/to/your/keystore/file
keystorePwd=your_keystore_password
keystoreAlias=your_keystore_alias
keystoreAliasPwd=your_keystore_alias_password
```

These values will be picked up by the compilation process of the example app 
and will set the application ID and signing configurations for you.

## Authors & Collaborators
* **[Adrián García](https://github.com/adriangl)** - *Author and maintainer*
* **[Daniel Sánchez Ceinos](https://github.com/danielceinos)** - *Contributor*

## Acnowledgements
The work in this respository up to April 28th, 2021 was done by [bq](https://github.com/bq)

## License
This project is licensed under the Apache Software License, Version 2.0.
```
Copyright (C) 2021 Hyperdevs

Copyright (C) 2019 BQ

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

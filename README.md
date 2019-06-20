# Android App Updates Helper
[![Release](https://jitpack.io/v/bq/android-app-updates-helper.svg)](https://jitpack.io/#bq/android-app-updates-helper)

This utility library aims to help Android developers to use the [Google Play In-App Updates API](https://developer.android.com/guide/app-bundle/in-app-updates) in an easy way.

**It's highly encouraged that you first read the [Google Play In-App Updates API](https://developer.android.com/guide/app-bundle/in-app-updates) documentation before using this library in order to understand the core concepts of the library.**

## Installation
Add the following dependencies to your main `build.gradle`:
```groovy
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

Add the following dependencies to your app's `build.gradle`:

* For Gradle < 4.0
    ```groovy
    dependencies {
        compile "com.github.bq:android-app-updates-helper:1.0.0"
    }
    ```

* For Gradle 4.0+
    ```groovy
    dependencies {
        implementation "com.github.bq:android-app-updates-helper:1.0.0"
    }
    ```

## Example usage
* Create a new _AppUpdatesHelper_.
* Start listening for app update changes with _AppUpdatesHelper.startListening()_, for example in _onCreate()_.
* Stop listening for app update changes with _AppUpdatesHelper.stopListening()_ in _onDestroy()_.
* Request app update information with _AppUpdatesHelper.getAppUpdateInfo()_.
* Request a flexible or immediate update with _AppUpdatesHelper.startFlexibleUpdate()_ or _AppUpdatesHelper.startImmediateUpdate()_

Check the [example app](app) for more implementation details about [flexible](app/src/main/kotlin/com/bq/appupdateshelper/flexible/FlexibleUpdateActivity.kt)
and [immediate](app/src/main/kotlin/com/bq/appupdateshelper/immediate/ImmediateUpdateActivity.kt) updates.

You can also use a [fake implementation](app/src/main/kotlin/com/bq/appupdateshelper/fake/FakeUpdateActivity.kt) to test in-app updates.

## Authors & Collaborators
* [Adrián García](https://github.com/adriangl) - Author

## License
```
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

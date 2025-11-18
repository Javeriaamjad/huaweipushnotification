# Huawei Push Notification Implementation Guide

This document provides a comprehensive step-by-step guide for implementing Huawei Push Notifications in an Android application.

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Project Setup](#project-setup)
3. [Dependencies Configuration](#dependencies-configuration)
4. [Huawei AppGallery Connect Configuration](#huawei-appgallery-connect-configuration)
5. [Keystore Configuration](#keystore-configuration)
6. [AndroidManifest.xml Configuration](#androidmanifestxml-configuration)
7. [Implementation Files](#implementation-files)
8. [Testing](#testing)

---

## Prerequisites

- Android Studio (latest version recommended)
- Huawei Developer Account
- App registered in Huawei AppGallery Connect
- Android device with HMS Core installed (or Huawei device)

---

## Project Setup

### 1. Create Android Project
- Create a new Android project in Android Studio
- Minimum SDK: 24
- Target SDK: 34
- Package name: `com.example.huaweipushtesting` (or your package name)

### 2. Add Huawei Maven Repository

#### Root `build.gradle.kts`
```kotlin
buildscript {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://developer.huawei.com/repo/") }
    }
    dependencies {
        classpath("com.huawei.agconnect:agcp:1.9.1.301")
    }
}
```

#### `settings.gradle.kts`
```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://developer.huawei.com/repo/") }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://developer.huawei.com/repo/") }
    }
}
```

---

## Dependencies Configuration

### 1. Version Catalog (`gradle/libs.versions.toml`)

```toml
[versions]
agp = "8.7.0-beta02"
appcompat = "1.7.1"
material = "1.13.0"
activity = "1.9.2"
constraintlayout = "2.2.1"
cardview = "1.0.0"
okhttp = "4.12.0"
agcp = "1.6.0.300"
huaweiPush = "6.13.0.300"

[libraries]
appcompat = { group = "androidx.appcompat", name = "appcompat", version.ref = "appcompat" }
material = { group = "com.google.android.material", name = "material", version.ref = "material" }
activity = { group = "androidx.activity", name = "activity", version.ref = "activity" }
constraintlayout = { group = "androidx.constraintlayout", name = "constraintlayout", version.ref = "constraintlayout" }
cardview = { group = "androidx.cardview", name = "cardview", version.ref = "cardview" }
okhttp = { group = "com.squareup.okhttp3", name = "okhttp", version.ref = "okhttp" }
huawei-push = { group = "com.huawei.hms", name = "push", version.ref = "huaweiPush" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
huawei-agconnect = { id = "com.huawei.agconnect", version.ref = "agcp" }
```

### 2. App-level `build.gradle.kts`

```kotlin
plugins {
    alias(libs.plugins.android.application)
}

apply(plugin = "com.huawei.agconnect")

android {
    namespace = "com.example.huaweipushtesting"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.huaweipushtesting"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    signingConfigs {
        create("release") {
            storeFile = file("keystore.jks")
            keyAlias = "huaweipush"
            keyPassword = "javeria123"
            storePassword = "javeria123"
            enableV1Signing = true
            enableV2Signing = true
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
        }
        debug {
            signingConfig = signingConfigs.getByName("release")
            isDebuggable = true
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.cardview)
    implementation(libs.okhttp)
    implementation(libs.huawei.push)
}
```

**Key Dependencies:**
- `com.huawei.hms:push:6.13.0.300` - Huawei Push Kit SDK
- `com.squareup.okhttp3:okhttp:4.12.0` - For HTTP requests to Huawei API
- `androidx.cardview:cardview:1.0.0` - For UI cards
- Material Design components for modern UI

---

## Huawei AppGallery Connect Configuration

### 1. Register Your App
1. Go to [Huawei AppGallery Connect](https://developer.huawei.com/consumer/en/service/jos/agc/index.html)
2. Create a new project or select existing one
3. Add your Android app
4. Enter package name: `com.example.huaweipushtesting`
5. Enable Push Kit service

### 2. Download `agconnect-services.json`

After registering your app, download the `agconnect-services.json` file from AppGallery Connect.

**File Location:** `app/agconnect-services.json`

**Important Fields in the JSON:**
```json
{
  "client": {
    "app_id": "115924709",           // Your App ID
    "package_name": "com.example.huaweipushtesting",
    "api_key": "...",                 // API Key for authentication
    "client_id": "...",               // OAuth Client ID
    "client_secret": "..."            // OAuth Client Secret
  },
  "oauth_client": {
    "client_id": "115924709"          // OAuth Client ID
  }
}
```

**Note:** Keep this file secure and never commit it to public repositories.

### 3. Get Your Credentials

From the `agconnect-services.json` file, extract:
- **App ID**: `115924709`
- **Client ID**: `115924709` (for OAuth)
- **Client Secret**: Found in the JSON file

These will be used in your API helper class.

---

## Keystore Configuration

### 1. Generate Keystore File

If you don't have a keystore, generate one using:

```bash
keytool -genkey -v -keystore keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias huaweipush
```

**File Location:** `app/keystore.jks`

### 2. Configure in `build.gradle.kts`

```kotlin
signingConfigs {
    create("release") {
        storeFile = file("keystore.jks")
        keyAlias = "huaweipush"
        keyPassword = "javeria123"      // Your keystore password
        storePassword = "javeria123"    // Your keystore password
        enableV1Signing = true
        enableV2Signing = true
    }
}
```

### 3. Upload SHA-256 Certificate to AppGallery Connect

1. Get SHA-256 fingerprint:
```bash
keytool -list -v -keystore keystore.jks -alias huaweipush
```

2. Copy the SHA-256 fingerprint
3. Go to AppGallery Connect → Your App → Project Settings → SHA-256 certificate fingerprint
4. Add the SHA-256 fingerprint

**Important:** The SHA-256 fingerprint must match the one uploaded in AppGallery Connect for Push Kit to work.

---

## AndroidManifest.xml Configuration

### Required Permissions

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### Push Service Declaration

```xml
<application
    android:allowBackup="true"
    android:icon="@mipmap/ic_launcher"
    android:label="@string/app_name"
    android:theme="@style/Theme.Huaweipushtesting">

    <!-- Main Activity -->
    <activity
        android:name=".MainActivity"
        android:exported="true">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
    </activity>

    <!-- Huawei Push Service -->
    <service
        android:name=".MyPushService"
        android:exported="false">
        <intent-filter>
            <action android:name="com.huawei.push.action.MESSAGING_EVENT" />
        </intent-filter>
    </service>
</application>
```

**Key Points:**
- The service must extend `HmsMessageService`
- Action: `com.huawei.push.action.MESSAGING_EVENT`
- `android:exported="false"` for security

---

## Implementation Files

### 1. Push Service (`MyPushService.java`)

**Location:** `app/src/main/java/com/example/huaweipushtesting/MyPushService.java`

```java
package com.example.huaweipushtesting;

import android.util.Log;
import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;

public class MyPushService extends HmsMessageService {
    private static final String TAG = "MyPushService";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.i(TAG, "NEW PUSH TOKEN RECEIVED: " + token);
        // Send token to your server or store it locally
    }

    @Override
    public void onMessageReceived(RemoteMessage message) {
        super.onMessageReceived(message);
        Log.i(TAG, "PUSH NOTIFICATION RECEIVED!");
        Log.i(TAG, "Title: " + message.getNotification().getTitle());
        Log.i(TAG, "Body: " + message.getNotification().getBody());
        // Notification automatically appears in notification tray
    }

    @Override
    public void onMessageSent(String msgId) {
        super.onMessageSent(msgId);
        Log.d(TAG, "Message sent successfully. Message ID: " + msgId);
    }

    @Override
    public void onSendError(String msgId, Exception exception) {
        super.onSendError(msgId, exception);
        Log.e(TAG, "Error sending message: " + exception.getMessage());
    }
}
```

**Purpose:**
- `onNewToken()`: Called when a new push token is generated
- `onMessageReceived()`: Called when a push notification is received
- `onMessageSent()`: Called when message is sent successfully
- `onSendError()`: Called when message sending fails

### 2. API Helper (`HuaweiApiHelper.java`)

**Location:** `app/src/main/java/com/example/huaweipush/HuaweiApiHelper.java`

This class handles:
- Getting OAuth access token from Huawei
- Sending push notifications via Huawei Push API

**Key Configuration:**
```java
private static final String TOKEN_URL = "https://oauth-login.cloud.huawei.com/oauth2/v3/token";
private static final String CLIENT_ID = "115924709";  // From agconnect-services.json
private static final String CLIENT_SECRET = "2af210cd6b4301d0704259b3df8f800d3b0927922fa5c60358410683ba7043d8";
```

**Methods:**
- `getAccessToken(TokenCallback callback)`: Gets OAuth access token
- `sendPushNotification(String accessToken, String pushToken, TokenCallback callback)`: Sends push notification

### 3. Main Activity (`MainActivity.java`)

**Location:** `app/src/main/java/com/example/huaweipushtesting/MainActivity.java`

**Key Functions:**
1. **Get Device Push Token:**
```java
String appId = "115924709";  // From agconnect-services.json
String token = HmsInstanceId.getInstance(this).getToken(appId, "HCM");
```

2. **Get Access Token:**
```java
HuaweiApiHelper.getAccessToken(new HuaweiApiHelper.TokenCallback() {
    @Override
    public void onSuccess(String token) {
        accessToken = token;
    }
    
    @Override
    public void onError(String error) {
        // Handle error
    }
});
```

3. **Send Push Notification:**
```java
HuaweiApiHelper.sendPushNotification(accessToken, devicePushToken, callback);
```

---

## Testing

### 1. Prerequisites for Testing
- Device must have HMS Core installed
- Device must be signed in with Huawei account (for some features)
- App must be installed with the same package name and SHA-256 certificate

### 2. Testing Steps

1. **Build and Install:**
   ```bash
   ./gradlew assembleDebug
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

2. **Check Logs:**
   ```bash
   adb logcat | grep -E "HuaweiApiHelper|MainActivity|MyPushService"
   ```

3. **Expected Flow:**
   - App starts → Requests device push token
   - Device push token received → Logs token
   - Requests OAuth access token
   - Access token received → Logs token
   - Sends test push notification
   - Push notification appears in notification tray

### 3. Testing via Huawei Console

1. Go to AppGallery Connect → Your App → Push Kit
2. Click "Send Test Message"
3. Enter device push token
4. Enter notification title and body
5. Send notification
6. Check device for notification

---

## Project Structure

```
huaweipushtesting/
├── app/
│   ├── agconnect-services.json      # Huawei configuration (DO NOT COMMIT)
│   ├── keystore.jks                  # Signing keystore (DO NOT COMMIT)
│   ├── build.gradle.kts              # App-level dependencies
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml   # Permissions and service declaration
│           └── java/
│               └── com/example/
│                   ├── huaweipush/
│                   │   └── HuaweiApiHelper.java    # API helper
│                   └── huaweipushtesting/
│                       ├── MainActivity.java       # Main activity
│                       └── MyPushService.java       # Push service
├── build.gradle.kts                  # Root build config
├── settings.gradle.kts               # Repository configuration
└── gradle/
    └── libs.versions.toml            # Version catalog
```

---

## Important Notes

### Security
1. **Never commit sensitive files:**
   - `agconnect-services.json`
   - `keystore.jks`
   - Add to `.gitignore`:
     ```
     app/agconnect-services.json
     app/keystore.jks
     *.jks
     ```

2. **Client Secret:**
   - Keep `CLIENT_SECRET` secure
   - Consider using environment variables or secure storage
   - Never expose in client-side code for production

### Common Issues

1. **Token Not Received:**
   - Check if HMS Core is installed
   - Verify SHA-256 certificate matches AppGallery Connect
   - Check internet connection

2. **Push Not Received:**
   - Verify device push token is correct
   - Check access token is valid
   - Ensure app is not in background restrictions
   - Check notification permissions

3. **Build Errors:**
   - Sync Gradle files
   - Clean and rebuild project
   - Verify all dependencies are downloaded

---

## API Endpoints Used

1. **OAuth Token Endpoint:**
   ```
   POST https://oauth-login.cloud.huawei.com/oauth2/v3/token
   ```

2. **Push Notification Endpoint:**
   ```
   POST https://push-api.cloud.huawei.com/v1/{app_id}/messages:send
   ```

---

## Version Information

- **Huawei Push Kit SDK:** 6.13.0.300
- **AGConnect Plugin:** 1.6.0.300
- **Min SDK:** 24
- **Target SDK:** 34
- **Compile SDK:** 34

---

## References

- [Huawei Push Kit Documentation](https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/push-introduction)
- [Huawei AppGallery Connect](https://developer.huawei.com/consumer/en/service/jos/agc/index.html)
- [Huawei Push Kit API Reference](https://developer.huawei.com/consumer/en/doc/development/HMS-References/push-sendapi)

---

## Support

For issues or questions:
- Check Huawei Developer Forums
- Review Huawei Push Kit documentation
- Contact Huawei Developer Support

---

**Last Updated:** 2024
**Project:** Huawei Push Notification Testing App



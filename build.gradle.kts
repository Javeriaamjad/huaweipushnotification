// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://developer.huawei.com/repo/") }
    }
    dependencies {
        classpath("com.huawei.agconnect:agcp:1.9.1.301")
        classpath("com.android.tools.build:gradle:8.2.0") // your Android Gradle plugin version
        // You can also add Kotlin plugin if needed:
        // classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
    }

}

plugins {
    alias(libs.plugins.android.application) apply false
}
import java.util.Properties
import java.io.FileInputStream


plugins {
    alias(libs.plugins.android.application)
}

apply(plugin = "com.huawei.agconnect")

// Load local.properties for sensitive data
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

android {
    namespace = "com.example.huaweipushtesting"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.huaweipushtesting"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // BuildConfig fields for Huawei Push Kit credentials
        buildConfigField("String", "HUAWEI_CLIENT_ID", "\"${localProperties.getProperty("huawei.client.id", "")}\"")
        buildConfigField("String", "HUAWEI_CLIENT_SECRET", "\"${localProperties.getProperty("huawei.client.secret", "")}\"")
        buildConfigField("String", "HUAWEI_TOKEN_URL", "\"${localProperties.getProperty("huawei.token.url", "")}\"")
        buildConfigField("String", "HUAWEI_PUSH_URL_BASE", "\"${localProperties.getProperty("huawei.push.url.base", "")}\"")
        buildConfigField("String", "HUAWEI_APP_ID", "\"${localProperties.getProperty("huawei.client.id", "")}\"")
    }

    buildFeatures {
        buildConfig = true
    }

    signingConfigs {
        create("release") {
            storeFile = file("keystore.jks")
            keyAlias = "huaweipush"
            keyPassword = localProperties.getProperty("keystore.key.password", "")
            storePassword = localProperties.getProperty("keystore.password", "")
            enableV1Signing = true
            enableV2Signing = true
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


}
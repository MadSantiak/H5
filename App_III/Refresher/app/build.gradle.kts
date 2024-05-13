plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "dk.tec.refresher"
    compileSdk = 34

    defaultConfig {
        applicationId = "dk.tec.refresher"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
// https://mvnrepository.com/artifact/com.google.android.gms/play-services-location
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("com.google.code.gson:gson:2.8.8")

}
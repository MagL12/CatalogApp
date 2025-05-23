plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.matlakhov.catalogapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.matlakhov.catalogapp"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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

    dependencies {
        // Стандартные зависимости Android
        implementation("androidx.appcompat:appcompat:1.6.1")
        implementation("com.google.android.material:material:1.10.0")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")

        // Retrofit для сетевых запросов
        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("com.squareup.retrofit2:converter-gson:2.9.0")

        // Glide для загрузки изображений
        implementation("com.github.bumptech.glide:glide:4.16.0")

        // RecyclerView для списков
        implementation("androidx.recyclerview:recyclerview:1.3.2")

        // SwipeRefreshLayout для обновления страницы
        implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    }
}
import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("kotlin-kapt")
    id("maven-publish")
    id("signing")
    id("com.vanniktech.maven.publish") version "0.29.0"
    id("com.gradleup.nmcp") version "0.0.8" apply false
}

val getVersionName: () -> String = {
    "1.0.5" // Replace with version Name
}

val getArtifactId: () -> String = {
    "qwary-android-sdk" // Replace with library name ID
}

android {
    namespace = "com.qwary"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    /*  publishing {
          singleVariant("release") {
              // if you don't want sources/javadoc, remove these lines
              withSourcesJar()
              withJavadocJar()
          }
      }*/
}


publishing {
    publications {
        create<MavenPublication>("release") {

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}

signing {
    useGpgCmd()
    useInMemoryPgpKeys(
        findProperty("signing.keyId") as String?,
        findProperty("signing.secretKey") as String?,
        findProperty("signing.password") as String?,
    )
    sign(publishing.publications["release"])
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.0")
}




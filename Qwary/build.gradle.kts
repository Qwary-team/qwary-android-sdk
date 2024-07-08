import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("kotlin-kapt")
    id("maven-publish")
    id("com.vanniktech.maven.publish") version "0.28.0" apply false
    id("com.gradleup.nmcp") version "0.0.7" apply false
}

val githubProperties = Properties().apply {
    load(FileInputStream(rootProject.file("github.properties")))
}

val getVersionName: () -> String = {
    "1.0.0" // Replace with version Name
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

    publishing {
        singleVariant("release") {
            // if you don't want sources/javadoc, remove these lines
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.0")
}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "com.qwary" // Replace with group ID
            artifactId = getArtifactId()
            version = getVersionName()
            artifact("$buildDir/outputs/aar/${artifactId}-release.aar")
        }
    }

    repositories {
        maven {
            name = "qwary-android-sdk"
            /** Configure path of your package repository on Github
             ** Replace GITHUB_USERID with your/organisation Github userID
             ** and REPOSITORY with the repository name on GitHub
             */
            url = uri("https://maven.pkg.github.com/Qwary-team/qwary-android-sdk")
            credentials {
                /** Create github.properties in root project folder file with
                 ** gpr.usr=GITHUB_USER_ID & gpr.key=PERSONAL_ACCESS_TOKEN
                 ** Set env variable GPR_USER & GPR_API_KEY if not adding a properties file**/
                username = githubProperties.getProperty("gpr.usr") ?: System.getenv("GPR_USER")
                password = githubProperties.getProperty("gpr.key") ?: System.getenv("GPR_API_KEY")
            }
        }
    }
}


/*
afterEvaluate{

    publishing {
        publications {
            register<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.qwary"
                artifactId = "qwary-android-sdk"
                version = "=1.0.0"

                pom {
                    pom {
                        name = "qwary-android-sdk"
                        description = "Test Description"
                        url = "http://yourwebsite.com"

                        licenses {
                            license {
                                name = "The Qwary Software License, Version 1.0"
                                url = "https://github.com/Qwary-team/qwary-android-sdk/blob/main/LICENSE"
                            }
                        }

                        developers {
                            developer {
                                id = "Qwary"
                                name = "Qwary"
                                email = "support@qwary.com"
                            }
                        }

                        scm {
                            connection = "scm:git:git://github.com/Qwary-team/qwary-android-sdk.git"
                            developerConnection = "scm:git:ssh://github.com:Qwary-team/qwary-android-sdk.git"
                            url = "https://github.com/Qwary-team/qwary-android-sdk/"
                        }
                    }
                }

            }
        }
    }
}*/



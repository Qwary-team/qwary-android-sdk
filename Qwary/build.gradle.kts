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
    "1.0.1" // Replace with version Name
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

            groupId = "com.qwary"
            artifactId = getArtifactId()
            version = getVersionName()

          /*  pom {
                name.set("Qwary Android SDK")
                description.set("The Qwary Android SDK allows you to seamlessly integrate surveys and feedback forms into your Android application. This guide will walk you through the process of downloading, importing, and configuring the SDK within your project.")
                url.set("https://github.com/Qwary-team/qwary-android-sdk/")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/Qwary-team/qwary-android-sdk/blob/main/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("Qwary Team")
                        name.set("Qwary SDK Team")
                        email.set("support@qwary.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/Qwary-team/qwary-android-sdk.git")
                    developerConnection.set("scm:git:ssh://github.com:Qwary-team/qwary-android-sdk.git")
                    url.set("https://github.com/Qwary-team/qwary-android-sdk/")
                }
            }*/
        }
    }
}

/*publishing {
    publications {
        create<MavenPublication>("release") {

            afterEvaluate {
                from(components["release"])
            }

            groupId = "com.qwary"
            artifactId = getArtifactId()
            version = getVersionName()

            pom {
                name.set("Qwary Android SDK")
                description.set("Description of your SDK")
                url.set("https://your_project_url")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("developerId")
                        name.set("Developer Name")
                        email.set("developer@example.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/username/repository.git")
                    developerConnection.set("scm:git:ssh://github.com:username/repository.git")
                    url.set("https://github.com/username/repository")
                }
            }
        }
    }
}*/

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




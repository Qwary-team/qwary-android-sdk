plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("kotlin-kapt")
    id("maven-publish")
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
    androidTestImplementation("androidx.test.ext`:junit:1.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.0")
}



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
}



//publishing {
//    publications {
//        create<MavenPublication>("maven") {
//            groupId = "com.github.vishaldrana"
//            artifactId = "qwary-android-sdk"
//            version = "1.0"
//        }
//    }
//}
//afterEvaluate {
//    publishing{
//        publications {
//            maven()
////            bar(MavenPublication) {
////          groupId = 'com.github.vishaldrana'
////                artifactId = 'qwary-android-sdk'
////                version = '1.0'
////            }
//        }
//    }
//}
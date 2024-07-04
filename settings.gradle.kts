pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
       /* maven {
            setUrl("https://jitpack.io")
            credentials {
                username = "vishal.d.rana@gmail.com"
                password = System.getProperty("authToken")
            }
        }*/
    }
}

rootProject.name = "DemoQwary"
include(":app")
include(":Qwary")
 
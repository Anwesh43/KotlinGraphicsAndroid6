pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "KotlinGraphicsAndroid6"
include(":app")
include(":linestartfromleftview")
include(":dropmultisemicirclesview")
include(":slantlinejoinarcview")
include(":stepquarterarcvleftview")
include(":biarcdividemoveview")
include(":dividelinearcjoinview")
include(":dividelinerotextendview")
include(":caparclinecircleview")
include(":concarclinejoinview")
include(":lineslantarcjoinview")
include(":rightanglebentdownview")
include(":linecapjoinrightview")
include(":slantarclinerightview")
include(":quarterhalfarcrotview")
include(":arclinecloserightview")
include(":linejoinclosearview")
include(":linerotarcleftview")
include(":halfarclinebentview")
include(":paralleloppositelinerightview")
include(":bentlinequarterarcview")

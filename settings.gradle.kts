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
include(":trilinearcbisectview")
include(":biarcedgedownview")
include(":closerotquarterarcview")
include(":rottrapeziumlineview")
include(":closelineendjoinview")
include(":triinsqrotrightview")
include(":linerottricapview")
include(":linemultiforkrotview")
include(":zigzaglinerightview")
include(":lineshiftjoindownview")
include(":strokeinnerarcrotview")
include(":sideconcarcdownview")
include(":linerightleftpieview")
include(":quarterarclinerightview")
include(":linecaparcrightview")
include(":bottombentleftlineview")

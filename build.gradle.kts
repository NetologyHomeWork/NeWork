// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath(deps.Plugins.safeArgs)
    }
}

plugins {
    id(deps.Plugins.application) version deps.Plugins.Version.applicationVersion apply false
    id(deps.Plugins.androidlibrary) version deps.Plugins.Version.androidlibraryVersion apply false
    id(deps.Plugins.kotlinAndroid) version deps.Plugins.Version.kotlinAndroidVersion apply false
    id(deps.Plugins.daggerHilt) version deps.Plugins.Version.daggerHiltVersion apply false
    kotlin(deps.Plugins.serialization) version deps.Plugins.Version.serializationVersion apply false
}
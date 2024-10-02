plugins {
    kotlin("jvm") version "2.0.20"
    id("fabric-loom") version "1.7-SNAPSHOT"
    id("com.diffplug.spotless") version "7.0.0.BETA2"
    id("com.github.breadmoirai.github-release") version "2.4.1"
}

group = "io.github.dyprex.poweredboats"
version = providers.gradleProperty("version").get()

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
}

loom {
    splitEnvironmentSourceSets()

    mods {
        create("powered-boats") {
            sourceSet(sourceSets["main"])
            sourceSet(sourceSets["client"])
        }
    }
    accessWidenerPath.set(file("src/main/resources/powered-boats.accesswidener"))
}

githubRelease {
    owner = "dyprex"
    repo = "powered-boats"
    token(providers.gradleProperty("ghToken"))

    tagName = "v${project.version}"
    targetCommitish = "master"
    generateReleaseNotes = true

    releaseAssets.from(layout.buildDirectory.dir("libs").map { it.asFile.listFiles() })
    prerelease = true
}

idea {
    module {
        excludeDirs.add(file("run"))
    }
}

spotless {
    kotlin {
        ktfmt("0.52").kotlinlangStyle()
    }
}

dependencies {
    minecraft("com.mojang:minecraft:1.21.1")
    mappings("net.fabricmc:yarn:1.21.1+build.3:v2")
    modImplementation("net.fabricmc:fabric-loader:0.16.5")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.105.0+1.21.1")
    modImplementation("net.fabricmc:fabric-language-kotlin:1.12.2+kotlin.2.0.20")
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") { expand(project.properties) }
    }

    withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }

    register("publish") {
        dependsOn(githubRelease)
    }
}

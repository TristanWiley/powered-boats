plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.spotless)
    alias(libs.plugins.githubRelease)
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
    accessWidenerPath.set(file("src/main/resources/powered-boats.accesswidener"))
    splitEnvironmentSourceSets()
    mods {
        create("powered-boats") {
            sourceSet(sourceSets["main"])
            sourceSet(sourceSets["client"])
        }
    }
    runs {
        named("client") {
            client()
            ideConfigGenerated(true)
            vmArg("-XX:+AllowEnhancedClassRedefinition")
        }
        named("server") {
            server()
            ideConfigGenerated(true)
            runDir("run/server")
        }
    }
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

idea {
    module {
        excludeDirs.add(file("run"))
    }
}

dependencies {
    minecraft(libs.minecraft)
    mappings(variantOf(libs.fabric.yarn) { classifier("v2") })
    modImplementation(libs.fabric.loader)

    modImplementation(libs.fabric.api)
    modImplementation(libs.fabric.languageKotlin)
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand("version" to version)
        }
    }

    withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }

    register("publish") {
        dependsOn(githubRelease)
    }
}

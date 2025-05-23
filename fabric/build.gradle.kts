import net.darkhax.curseforgegradle.Constants.RELEASE_TYPE_BETA
import net.darkhax.curseforgegradle.TaskPublishCurseForge

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.githubRelease)
    alias(libs.plugins.curseforgeGradle)
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
    runs {
        named("client") {
            client()
            ideConfigGenerated(true)
            programArg("--username=Player1")
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
            expand(
                "version" to version,
                "fabric_loader" to libs.versions.fabric.loader.get(),
                "minecraft" to libs.versions.minecraft.get()
            )
        }
    }

    withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }

    val publishCurseForge by registering(TaskPublishCurseForge::class) {
        group = "publishing"
        apiToken = providers.gradleProperty("cfToken")

        val curseForgeProjectId = 1113715
        val minecraftVersion = libs.versions.minecraft.get()
        val modJar = files(remapJar).singleFile // accessing like this to avoid introducing dependency to remapJar task
        upload(curseForgeProjectId, modJar).apply {
            displayName = "${project.version} (Fabric $minecraftVersion)"
            // CurseForge API considers all of these tags "game versions" (see net.darkhax.curseforgegradle.UploadArtifact.gameVersions)
            addGameVersion("Server", "Client", "Fabric", "Java 21", minecraftVersion)
            releaseType = RELEASE_TYPE_BETA
            changelog = "See https://github.com/dyprex/powered-boats/releases"
        }
    }

    register("publish") {
        dependsOn(githubRelease, publishCurseForge)
    }
}

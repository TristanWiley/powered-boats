pluginManagement {
	repositories {
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.fabricmc.net/") {
			name = "Fabric"
		}
	}
}

rootProject.name = "powered-boats"
include("fabric")
rootProject.children.forEach { it.name = "${rootProject.name}-${it.name}" }
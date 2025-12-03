//buildscript {
//    repositories {
//        maven("https://maven.fabricmc.net/") { name = "Fabric" }
//        maven("https://maven.architectury.dev/") { name = "Architectury" }
//        mavenCentral()
//        gradlePluginPortal()
//    }
//    dependencies {
//        classpath("net.fabricmc:fabric-loom:1.10-SNAPSHOT")
//    }
//}
//
//apply(plugin = "fabric-loom")

plugins {
    id("maven-publish")
    // Apply the multi-version plugin, this does all the configuration necessary for the preprocessor to
    // work. In particular it also applies `com.replaymod.preprocess`.
    // In addition it primarily also provides a `platform` extension which you can use in this build script
    // to get the version and mod loader of the current project.
    id("gg.essential.multi-version")
    id("gg.essential.defaults.loom")
    // If you do not care too much about the details, you can just apply essential-gradle-toolkits' defaults for
    // Minecraft, fabric-loader, forge, mappings, etc. versions.
    // You can also overwrite some of these if need be. See the `gg.essential.defaults.loom` README section.
    // Otherwise you'll need to configure those as usual for (architectury) loom.
    id("gg.essential.defaults")
}

val mod_version: String by project

base {
	archivesName.set(project.property("archives_base_name") as String)
}

repositories {
	// Add repositories to retrieve artifacts from in here.
	// You should only use this when depending on other mods because
	// Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
	// See https://docs.gradle.org/current/userguide/declaring_repositories.html
	// for more information about repositories.
}

//sourceSets {
//    val main by getting {
//        java.srcDir("src/main/java")
//        resources.srcDir("src/main/resources")
//    }
//    val client by getting {
//        java.srcDir("src/client/java")
//        resources.srcDir("src/client/resources")
//    }
//}

loom {
	splitEnvironmentSourceSets()

	mods {
		create("media") {
			sourceSet(sourceSets["main"])
			sourceSet(sourceSets["client"])
		}
	}
}

dependencies {
	// To change the versions see the gradle.properties file
//	minecraft("com.mojang:minecraft:${project.property("minecraft_version") as String}")
	mappings("net.fabricmc:yarn:${project.property("yarn_mappings") as String}:v2")
	modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version") as String}")

	// Fabric API. This is technically optional, but you probably want it anyway.
	modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version") as String}")
}

tasks.processResources {
    inputs.property("version", project.property("version") as String)
    filesMatching("fabric.mod.json") {
        expand("version" to project.property("version") as String)
    }
}

java {
	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()

	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

tasks.jar {
	from("LICENSE") {
		rename { "${it}_${project.base.archivesName.get()}"}
	}
}

// Add the Terraformers maven repo to your repositories block
repositories {
	maven("https://maven.terraformersmc.com/")
}

// Add Mod Menu as a dependency in your environment
dependencies {
	modImplementation("com.terraformersmc:modmenu:${project.property("modmenu_version") as String}")
}

repositories {
	maven("https://server.bbkr.space/artifactory/libs-release")
}

dependencies {
    modImplementation(include("io.github.cottonmc:LibGui:${project.property("libgui_version") as String}")!!)
}
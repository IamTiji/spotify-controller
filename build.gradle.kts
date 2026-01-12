repositories {
	mavenCentral()
	gradlePluginPortal()
	maven("https://server.bbkr.space/artifactory/libs-release")
	maven("https://maven.terraformersmc.com/")
	maven("https://maven.fabricmc.net/") { name = "Fabric" }
	maven("https://maven.architectury.dev/") { name = "Architectury" }
	maven("https://mvnrepository.com")
}

plugins {
	id("java")
    id("maven-publish")
	id("gg.essential.defaults.java")
	//id("gg.essential.defaults")
	//id("gg.essential.defaults.loom")
    id("gg.essential.multi-version")
}

val modVersion: String by project

base {
	val name = project.property("archives_base_name") as String +
			"-${project.property("mod_version")}" +
			"+mc${project.property("minecraft_version")}"

	archivesName.set(name)
}

loom {
	mods {
		create("spotify-controller") {
			sourceSet(sourceSets["main"])
		}
	}

    accessWidenerPath = file("../../src/main/resources/.accesswidener")
}

dependencies {
	modImplementation(fabricApi.module("fabric-lifecycle-events-v1", project.property("fabric_version") as String))
	modImplementation(fabricApi.module("fabric-resource-loader-v0",  project.property("fabric_version") as String))
	modImplementation(fabricApi.module("fabric-key-binding-api-v1",  project.property("fabric_version") as String))
    modImplementation(fabricApi.module("fabric-command-api-v2",      project.property("fabric_version") as String))

	modImplementation(project.property("essential.defaults.loom.fabric-loader")!! as String)

	modCompileOnly("com.terraformersmc:modmenu:3.0.0") // wise word from random person: don't touch it if it works

    mappings(loom.officialMojangMappings())
	minecraft(project.property("essential.defaults.loom.minecraft")!! as String)

}

tasks.processResources {
    inputs.property("version", project.property("version") as String)
    filesMatching("fabric.mod.json") {
        expand("version" to project.property("version") as String)
    }
}

java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

tasks.remapJar {
	archiveClassifier.set(null as String?)
}

tasks.jar {
	from("LICENSE_CODE") {
		rename { "LICENSE_${project.base.archivesName.get()}_code"}
	}
	from("LICENSE_ASSETS") {
		rename { "LICENSE_${project.base.archivesName.get()}_assets"}
	}
}
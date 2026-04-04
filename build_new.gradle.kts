// Identical to old script but this one doesnt have mappings

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://server.bbkr.space/artifactory/libs-release")
    maven("https://maven.terraformersmc.com/")
    maven("https://maven.fabricmc.net/") { name = "Fabric" }
    maven("https://maven.architectury.dev/") { name = "Architectury" }
    maven("https://mvnrepository.com")
    maven("https://maven.parchmentmc.org") { name = "Parchment" }
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

    accessWidenerPath.set(file("../../src/main/resources/new.accesswidener"))
}

sourceSets {
    main {
        resources.srcDir(file("../../src/main/resources"))
    }
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

dependencies {
    implementation(fabricApi.module("fabric-lifecycle-events-v1", project.property("fabric_version") as String))
    implementation(fabricApi.module("fabric-resource-loader-v0",  project.property("fabric_version") as String))
    implementation(fabricApi.module("fabric-key-mapping-api-v1",  project.property("fabric_version") as String))
    implementation(fabricApi.module("fabric-command-api-v2",      project.property("fabric_version") as String))

    implementation(project.property("essential.defaults.loom.fabric-loader")!! as String)

    compileOnly("com.terraformersmc:modmenu:18.0.0-alpha.8") // wise word from random person: don't touch it if it works

    minecraft(project.property("essential.defaults.loom.minecraft")!! as String)
}

tasks.processResources {
    filesMatching("**/fabric.mod.json") {
        var mcVersionRange = StringBuilder()
        val rawVer = project.property("minecraft_version") as String
        if (rawVer.contains("-")) {
            val left = rawVer.substringBefore("-")
            val right = rawVer.substringAfter("-")

            mcVersionRange.append(">=")
                .append(left)
                .append(" <=")
                .append(right)
        } else {
            mcVersionRange.append("=")
                .append(rawVer)
        }

        expand(
            "version" to (project.property("mod_version") as String),
            "accesswidener" to "new.accesswidener",
            "keybindingName" to "fabric-key-mapping-api-v1",
            "mc" to mcVersionRange
        )
    }
}

tasks.matching { it.name == "preprocessResources"} .configureEach {
    onlyIf { false } // nothing is done, and somehow causes crash so its disabled
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

tasks.jar {
    from("LICENSE_CODE") {
        rename { "LICENSE_${project.base.archivesName.get()}_code"}
    }
    from("LICENSE_ASSETS") {
        rename { "LICENSE_${project.base.archivesName.get()}_assets"}
    }
}

loom {
    runs {
        findByName("server")?.let { remove(it) }
    }
}
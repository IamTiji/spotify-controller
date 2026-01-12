repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://server.bbkr.space/artifactory/libs-release")
    maven("https://maven.terraformersmc.com/")
    maven("https://maven.fabricmc.net/") { name = "Fabric" }
    maven("https://maven.architectury.dev/") { name = "Architectury" }
}

plugins {
    // This marks the current project as the root of a multi-version project.
    // Any project using `gg.essential.multi-version` must have a parent with this root plugin applied.
    // Advanced users may use multiple (potentially independent) multi-version trees in different sub-projects.
    // This is currently equivalent to applying `com.replaymod.preprocess-root`.

    id("gg.essential.multi-version.root") version "0.6.2"
    id("gg.essential.loom") version "1.7.35"
}

preprocess.strictExtraMappings.set(true)

preprocess {
    // Here you first need to create a node per version you support and assign it an integer Minecraft version.
    // The mappings value is currently meaningless.

    // Unique versions: 1.21.1, 1.21.3, 1.21.4, 1.21.5, 1.21.7, 1.21.9
    // TODO: Work on new versions like 1.21.9, 1.21.11

    val fabric12101 = createNode("1.21.1-fabric", 12101, "yarn")
    //val fabric12102 = createNode("1.21.2-fabric", 12102, "yarn") // hotfixed version
    val fabric12103 = createNode("1.21.3-fabric", 12103, "yarn")
    val fabric12104 = createNode("1.21.4-fabric", 12104, "yarn")
    val fabric12105 = createNode("1.21.5-fabric", 12105, "yarn")
    //val fabric12106 = createNode("1.21.6-fabric", 12106, "yarn") // hotfixed version
    //val fabric12107 = createNode("1.21.7-fabric", 12107, "yarn")
    val fabric12108 = createNode("1.21.8-fabric", 12108, "yarn")
    val fabric12109 = createNode("1.21.9-fabric", 12109, "yarn")
    val fabric12111 = createNode("1.21.11-fabric", 12111, "yarn")

    // And then you need to tell the preprocessor which versions it should directly convert between.
    // This should form a directed graph with no cycles (i.e. a tree), which the preprocessor will then traverse to
    // produce source code for all versions from the main version.
    // Do note that the preprocessor can only convert between two projects when they are either on the same Minecraft
    // version (but use different mappings, e.g. 1.16.2 forge to fabric), or when they are using the same intermediary
    // mappings (but on different Minecraft versions, e.g. 1.12.2 forge to 1.8.9 forge, or 1.16.2 fabric to 1.18 fabric)
    // but not both at the same time, i.e. you cannot go straight from 1.12.2 forge to 1.16.2 fabric, you need to go via
    // an intermediary 1.16.2 forge project which has something in common with both.
    fabric12101.link(fabric12103)
    fabric12103.link(fabric12104)
    fabric12104.link(fabric12105)
    fabric12108.link(fabric12105)
    fabric12109.link(fabric12108)
    fabric12111.link(fabric12109)
}

dependencies {
    // Dummy dependencies so that it runs without being depressed

    modImplementation(fabricApi.module("fabric-lifecycle-events-v1", project.property("fabric_version") as String))
    modImplementation(fabricApi.module("fabric-resource-loader-v0",  project.property("fabric_version") as String))
    modImplementation(fabricApi.module("fabric-key-binding-api-v1",  project.property("fabric_version") as String))
    modImplementation(fabricApi.module("fabric-command-api-v2",      project.property("fabric_version") as String))

    modImplementation(project.property("essential.defaults.loom.fabric-loader")!! as String)

    modCompileOnly("com.terraformersmc:modmenu:3.0.0") // wise word from random person: don't touch it if it works

    minecraft("com.mojang:minecraft:${project.property("essential.defaults.loom.minecraft")!! as String}")
    mappings(loom.officialMojangMappings())
}

loom {
    accessWidenerPath = file("src/main/resources/.accesswidener")
}

val versions = listOf(
    "1.21.1-fabric",
    "1.21.3-fabric",
    "1.21.4-fabric",
    "1.21.5-fabric",
    "1.21.8-fabric",
    "1.21.9-fabric"
)

tasks.register("buildAll") {
    for (version in versions) {
        dependsOn(":$version:build")
    }
}
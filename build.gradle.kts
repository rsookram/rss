import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(libs.agp)
        classpath(libs.kotlin.gradle)

        classpath(libs.hilt.gradle)
        classpath(libs.sqldelight.gradle)
    }
}

allprojects {
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            freeCompilerArgs +=
                listOf(
                    "-Xopt-in=androidx.compose.foundation.ExperimentalFoundationApi", // Modifier.combinedClickable
                )
        }
    }
}

tasks.register("clean", Delete::class) { delete(rootProject.buildDir) }

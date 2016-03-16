package com.github.lkishalmi.gradle.gatling

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.scala.ScalaBasePlugin

/**
 *
 * @author Laszlo Kishalmi
 */
class GatlingPlugin implements Plugin<Project> {

    private final String GATLING_TASK_NAME = 'gatlingRun'
    private final String GATLING_MAIN_CLASS = 'io.gatling.app.Gatling'
    private Project project

    void apply(Project project) {
        this.project = project

        project.pluginManager.apply ScalaBasePlugin
        project.pluginManager.apply JavaPlugin

        def gatlingExt = project.extensions.create('gatling', GatlingExtension)

        gatlingExt.simulationsDir = "$project.projectDir/src/gatlingExt/simulations" as File
        gatlingExt.dataDir = "$project.projectDir/src/gatlingExt/data" as File
        gatlingExt.bodiesDir = "$project.projectDir/src/gatlingExt/bodies" as File
        gatlingExt.reportsDir = "$project.buildDir/reports/gatlingExt/" as File
        gatlingExt.confDir = "$project.projectDir/src/gatlingExt/conf" as File

        createConfiguration(gatlingExt)
        configureGatlingCompile(gatlingExt)

        def gatlingTask = project.tasks.create(GATLING_TASK_NAME, Gatling)
        gatlingTask.description = "Executes all Gatling scenarioes"
        gatlingTask.group = "Test"


        project.tasks.addRule('Pattern: gatlingExt<SimulationName>: Executes a named Gatling simulation.') {
            def taskName ->
                if (taskName.startsWith(GATLING_TASK_NAME) && !taskName.equals(GATLING_TASK_NAME)) {
                    def simulationName = taskName - GATLING_TASK_NAME
                    project.tasks.create(taskName, Gatling) {
                        simulation = simulationName
                    }
                }
        }

        project.tasks.withType(Gatling) { Gatling task ->
            task.dependsOn(project.gatlingClasses)
            configureGatlingTask(task, gatlingExt)
        }
    }

    protected void createConfiguration(GatlingExtension gatlingExtension) {
        project.sourceSets {
            gatling {
                scala.srcDirs 'src/gatling/scala'
                resources.srcDirs 'src/gatling/resources'
                compileClasspath += (project.configurations.compile + project.configurations.gatlingCompile + main.output)
                runtimeClasspath += project.configurations.runtime + main.output
            }
        }

        project.dependencies {
            gatlingCompile "io.gatling.highcharts:gatling-charts-highcharts:${gatlingExtension.toolVersion}"
        }
    }

    def configureGatlingCompile(GatlingExtension gatling) {
        def config = project.configurations['gatlingCompile']
        config.defaultDependencies { dependencies ->
            dependencies.add(this.project.dependencies.create("io.gatling.highcharts:gatling-charts-highcharts:${gatling.toolVersion}"))
        }
        def scalaCompile = project.tasks["compileGatlingScala"]
        scalaCompile.conventionMapping.with {
            description = { "Compiles Gatling simulations." }
//            source = { project.fileTree(dir: gatling.simulationsDir, includes: ['**/*.scala']) }
//            classpath = { config }
//            destinationDir = { project.file("${project.buildDir}/classes/gatling") }
        }
        project.gradle.projectsEvaluated {
            scalaCompile.scalaCompileOptions.incrementalOptions.with {
                if (!analysisFile) {
                    analysisFile = new File("$project.buildDir/tmp/scala/compilerAnalysis/${scalaCompile.name}.analysis")
                }
            }
        }
    }

    def configureGatlingTask(Gatling task, GatlingExtension gatling) {
        task.conventionMapping.with {
            simulationsDir = { gatling.simulationsDir }
            dataDir = { gatling.dataDir }
            bodiesDir = { gatling.bodiesDir }
            reportsDir = { gatling.reportsDir }
            confDir = { gatling.confDir }
//            classesDir = { project.gatlingCompile.destinationDir }
//            classpath = {
//                project.configurations['gatlingCompile'] + project.files(project.gatlingCompile.destinationDir)
//            }
            mute = { gatling.mute }
        }
    }
}


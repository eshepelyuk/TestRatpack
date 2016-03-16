package com.github.lkishalmi.gradle.gatling

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.scala.ScalaBasePlugin
import org.gradle.api.tasks.JavaExec
import org.gradle.plugins.ide.idea.IdeaPlugin

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

        project.tasks.create(name: GATLING_TASK_NAME, type: JavaExec) {
            dependsOn project.tasks.gatlingClasses
            main = "io.gatling.app.Gatling"
            description = "Executes all Gatling scenarios"
            group = "Test"

            classpath = (project.configurations.gatlingRuntime)
            args "-m", "-bf", "${project.sourceSets.gatling.output.classesDir}"
            args "-df", "${project.sourceSets.gatling.output.resourcesDir}/data"
            args "-bdf", "${project.sourceSets.gatling.output.resourcesDir}/bodies"
            args "-rf", "${project.buildDir}/gatling"
        }

//        project.tasks.addRule('Pattern: gatlingExt<SimulationName>: Executes a named Gatling simulation.') {
//            def taskName ->
//                if (taskName.startsWith(GATLING_TASK_NAME) && !taskName.equals(GATLING_TASK_NAME)) {
//                    def simulationName = taskName - GATLING_TASK_NAME
//                    project.tasks.create(taskName, Gatling) {
//                        simulation = simulationName
//                    }
//                }
//        }

        project.afterEvaluate {
            def hasIdea = project.plugins.findPlugin(IdeaPlugin)
            if (hasIdea) {
                project.idea {
                    module {
                        scopes.TEST.plus += [project.configurations.gatlingCompile]
                    }
                }
                project.idea {
                    module {
                        project.sourceSets.gatling.scala.srcDirs.each {
                            testSourceDirs += project.file(it)
                        }
                    }
                }
            }
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
        def scalaCompile = project.tasks["compileGatlingScala"]
        scalaCompile.conventionMapping.with {
            description = { "Compiles Gatling simulations." }
        }
        project.gradle.projectsEvaluated {
            scalaCompile.scalaCompileOptions.incrementalOptions.with {
                if (!analysisFile) {
                    analysisFile = new File("$project.buildDir/tmp/scala/compilerAnalysis/${scalaCompile.name}.analysis")
                }
            }
        }
    }
}


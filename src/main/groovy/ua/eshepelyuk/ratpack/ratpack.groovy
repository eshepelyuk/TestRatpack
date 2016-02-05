package ua.eshepelyuk.ratpack

import ua.eshepelyuk.ratpack.ratpack.config.internal.source.YamlConfigSource

import javax.annotation.Resources

import static ua.eshepelyuk.ratpack.ratpack.groovy.Groovy.*

ratpack {

    serverConfig {
        add(new YamlConfigSource(Resources.getResource("/ratpack.yml")))
            .onError(ratpack.func.Action.noop()) { e -> throw e }
            .require("/database", DatabaseConfig)

    }

    bindings {
        println("${serverConfig.get("/database", DatabaseConfig)}")
    }

    handlers {
        get("news") {
            render "All News"
        }
        post("news") {
            render "Create News"
        }
        get("news/:name") {
            render "Hello $pathTokens.name"
        }
    }
}
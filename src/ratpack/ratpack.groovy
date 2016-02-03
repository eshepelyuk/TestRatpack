import ratpack.config.internal.source.YamlConfigSource
import ua.eshepelyuk.ratpack.DatabaseConfig

import javax.annotation.Resources

import static ratpack.groovy.Groovy.*

ratpack {

    serverConfig {
        add(new YamlConfigSource(Resources.getResource("/ratpack.yml")))
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
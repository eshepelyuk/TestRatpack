import static ratpack.groovy.Groovy.ratpack

ratpack {
    bindings {
        ratpack.config.ConfigData
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
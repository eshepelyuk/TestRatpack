import com.google.common.io.Resources
import ratpack.func.Action
import ratpack.groovy.sql.SqlModule
import ratpack.h2.H2Module
import ua.eshepelyuk.ratpack.DatabaseConfig
import ua.eshepelyuk.ratpack.FlywayService
import ua.eshepelyuk.ratpack.NewsItem
import ua.eshepelyuk.ratpack.NewsItemDAO

import static ratpack.groovy.Groovy.ratpack
import static ratpack.http.Status.of
import static ratpack.jackson.Jackson.json

ratpack {
    serverConfig {
        onError(Action.throwException()).yaml(Resources.getResource("ratpack.yml")).require("/database", DatabaseConfig)
    }

    bindings {
        def dbCfg = serverConfig.get("/database", DatabaseConfig)
        module(new H2Module(dbCfg.user, dbCfg.password, dbCfg.url))

        bind(FlywayService)

        module(SqlModule)
        bind(NewsItemDAO)
    }

    handlers {
        path("news") { NewsItemDAO dao ->
            byMethod {
                post {
                    context.parse(NewsItem)
                        .blockingMap { dao.insert(it) }
                        .then { context.response.send(it.toString()) }
                }
                get {
                    render json(dao.findAll())
                }
            }
        }

        get("news/:id") { NewsItemDAO dao ->
            def item = dao.findById(pathTokens.id as Long)
            if (item) {
                render json(item)
            } else {
                context.response.status(of(404)).send("News Item not found by id=${pathTokens.id}")
            }
        }
    }
}
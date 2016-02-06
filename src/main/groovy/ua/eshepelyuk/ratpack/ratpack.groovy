package ua.eshepelyuk.ratpack

import com.google.common.io.Resources
import ratpack.func.Action
import ratpack.groovy.sql.SqlModule
import ratpack.h2.H2Module

import static ratpack.groovy.Groovy.ratpack

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
        post("news") { NewsItemDAO dao ->
            render "ID: ${context.parse(NewsItem).next { dao.insert(it) }}"
        }
        get("news") { NewsItemDAO dao ->
            render "All News : ${dao.findAll()}"
        }
        get("news/:id") { NewsItemDAO dao ->
            render "News: ${dao.findById(pathTokens.id as Long)}"
        }
    }
}
import com.google.common.io.Resources
import ratpack.func.Action
import ratpack.groovy.sql.SqlModule
import ratpack.h2.H2Module
import ua.eshepelyuk.ratpack.DatabaseConfig
import ua.eshepelyuk.ratpack.FlywayService
import ua.eshepelyuk.ratpack.NewsItemChainAction
import ua.eshepelyuk.ratpack.NewsItemDAO

import javax.validation.Validation
import javax.validation.Validator
import ratpack.handling.RequestLogger

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

        bind(NewsItemChainAction)

        bindInstance Validator, Validation.buildDefaultValidatorFactory().validator
    }

    handlers {
        all RequestLogger.ncsa()
        prefix("news") {
            all chain(NewsItemChainAction)
        }
    }
}
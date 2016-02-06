import com.google.common.io.Resources
import com.google.inject.name.Names
import ratpack.error.ServerErrorHandler
import ratpack.error.internal.DefaultDevelopmentErrorHandler
import ratpack.error.internal.DefaultProductionErrorHandler
import ratpack.func.Action
import ratpack.groovy.sql.SqlModule
import ratpack.h2.H2Module
import ratpack.handling.RequestLogger
import ua.eshepelyuk.ratpack.*

import javax.validation.Validation
import javax.validation.Validator

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

        binder {
            it.bind(ServerErrorHandler)
                .annotatedWith(Names.named("defaultServerErrorHandler"))
                .toInstance(serverConfig.isDevelopment() ? new DefaultDevelopmentErrorHandler() : new DefaultProductionErrorHandler())
        }

        bind(ServerErrorHandler, MyServerErrorHandler)
    }

    handlers {
        all RequestLogger.ncsa()
        prefix("news") {
            all chain(NewsItemChainAction)
        }
    }
}
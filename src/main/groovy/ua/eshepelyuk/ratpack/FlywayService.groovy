package ua.eshepelyuk.ratpack

import groovy.util.logging.Slf4j
import org.flywaydb.core.Flyway
import ratpack.server.Service
import ratpack.server.StartEvent

import javax.inject.Inject
import javax.inject.Singleton
import javax.sql.DataSource

@Slf4j
@Singleton
class FlywayService implements Service {

//    @Inject DataSource dataSource
    @Inject DatabaseConfig databaseConfig

    @Override
    void onStart(StartEvent event) throws Exception {
        println(databaseConfig)
        Flyway flyway = new Flyway()
        flyway.setDataSource(dataSource)
        flyway.migrate()
    }
}

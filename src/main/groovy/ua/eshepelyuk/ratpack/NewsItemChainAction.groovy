package ua.eshepelyuk.ratpack

import ratpack.exec.Blocking
import ratpack.groovy.handling.GroovyChainAction
import ratpack.jackson.Jackson

import javax.inject.Inject

import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND
import static ratpack.http.Status.of

class NewsItemChainAction extends GroovyChainAction {

    @Inject
    NewsItemDAO newsItemDAO

    @Override
    void execute() throws Exception {

        get(":id") {
            byContent {
                json {
                    Blocking.get { newsItemDAO.findById(pathTokens.id as Long) }
                        .onNull {
                        context.response.status(of(NOT_FOUND.code())).send("News Item not found by id=${pathTokens.id}")
                    }
                        .then { render Jackson.json(it) }
                }
            }
        }

        all {
            byContent {
                json {
                    byMethod {
                        post {
                            context.parse(NewsItem)
                                .blockingMap { newsItemDAO.insert(it) }
                                .then { context.response.send(it.toString()) }
                        }
                        get {
                            render Jackson.json(newsItemDAO.findAll())
                        }
                    }
                }
            }
        }
    }
}

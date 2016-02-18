package ua.eshepelyuk.ratpack

import ratpack.exec.Blocking
import ratpack.func.Action
import ratpack.groovy.handling.GroovyChainAction
import ratpack.jackson.Jackson

import javax.inject.Inject
import javax.validation.ConstraintViolationException
import javax.validation.Validator

import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND
import static io.netty.handler.codec.http.HttpResponseStatus.UNPROCESSABLE_ENTITY
import static ratpack.exec.Promise.error
import static ratpack.exec.Promise.value
import static ratpack.http.Status.of

class NewsItemChainAction extends GroovyChainAction {

    @Inject
    NewsItemDAO newsItemDAO

    @Inject
    Validator validator

    @Override
    void execute() throws Exception {

        get(":id") {
            byContent {
                json {
                    Blocking.get {
                        newsItemDAO.findById(pathTokens.id as Long)
                    } onNull {
                        context.response.status(of(NOT_FOUND.code())).send("News Item not found by id=${pathTokens.id}")
                    } then { render Jackson.json(it) }
                }
            }
        }

        all {
            byContent {
                json {
                    byMethod {
                        post {
                            context.parse(NewsItem).flatMap {
                                def violations = validator.validate(it)
                                if (violations.isEmpty()) {
                                    value(it)
                                } else {
                                    error(new ConstraintViolationException("NewsItem validation failed", violations))
                                }
                            } blockingMap { NewsItem item ->
                                newsItemDAO.insert(item)
                            } onError(IllegalArgumentException, {
                                context.response.status(UNPROCESSABLE_ENTITY.code()).send(it.message)
                            } as Action) then { context.response.send(it.toString()) }
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

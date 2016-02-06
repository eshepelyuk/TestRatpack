package ua.eshepelyuk.ratpack

import ratpack.groovy.handling.GroovyChainAction

import javax.inject.Inject

import static ratpack.http.Status.of
import static ratpack.jackson.Jackson.json

class NewsItemChainAction extends GroovyChainAction {

    @Inject
    NewsItemDAO newsItemDAO

    @Override
    void execute() throws Exception {

        get(":id") {
            def item = newsItemDAO.findById(pathTokens.id as Long)
            if (item) {
                render json(item)
            } else {
                context.response.status(of(404)).send("News Item not found by id=${pathTokens.id}")
            }
        }

        all {
            byMethod {
                post {
                    context.parse(NewsItem)
                        .blockingMap { newsItemDAO.insert(it) }
                        .then { context.response.send(it.toString()) }
                }
                get {
                    render json(newsItemDAO.findAll())
                }
            }
        }
    }
}

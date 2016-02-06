package ua.eshepelyuk.ratpack

import groovy.sql.Sql

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsItemDAO {

    @Inject
    Sql sql

//    @SqlUpdate("insert into news_item (title, author, content, publishDate) values (:title, :author, :content, :publishDate)")
//    @GetGeneratedKeys
    Long insert(NewsItem item) {
        println("@@@@ ${item}")
        1L
    }

    Collection<NewsItem> findAll() {
        sql.rows("select * from news_item").collect { new NewsItem(it) }
    }

    NewsItem findById(Long id) {
        def res = sql.firstRow("select * from news_item where id = ${id}")
        res != null ? new NewsItem(res) : null
    }
}

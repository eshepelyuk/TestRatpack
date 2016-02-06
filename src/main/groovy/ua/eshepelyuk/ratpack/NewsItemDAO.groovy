package ua.eshepelyuk.ratpack

import groovy.sql.Sql

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsItemDAO {
    @Inject
    Sql sql

    Long insert(NewsItem item) {
        def list = sql.executeInsert("insert into news_item (title, author, content, publishDate) values (:title, :author, :content, :publishDate)", item.properties)
        list[0][0] as Long
    }

    Collection<NewsItem> findAll() {
        sql.rows("select * from news_item").collect { new NewsItem(it) }
    }

    NewsItem findById(Long id) {
        def res = sql.firstRow("select * from news_item where id = ${id}")
        res != null ? new NewsItem(res) : null
    }
}

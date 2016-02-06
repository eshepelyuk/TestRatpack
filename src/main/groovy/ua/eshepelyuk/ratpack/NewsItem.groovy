package ua.eshepelyuk.ratpack

import groovy.transform.ToString
import groovy.transform.TupleConstructor

@ToString(includeNames = true)
@TupleConstructor()
class NewsItem {
    Long id
    String title
    String author
    String content
    Date publishDate = new Date()
}

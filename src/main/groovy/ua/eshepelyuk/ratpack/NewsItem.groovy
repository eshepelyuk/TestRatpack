package ua.eshepelyuk.ratpack

import groovy.transform.ToString

@ToString(includeNames = true)
class NewsItem {
    Long id
    String title
    String author
    String content
    Date publishDate
}

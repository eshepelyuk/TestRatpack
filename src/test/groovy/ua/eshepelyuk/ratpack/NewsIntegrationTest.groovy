package ua.eshepelyuk.ratpack

import groovy.json.JsonSlurper
import groovy.sql.Sql
import ratpack.groovy.test.GroovyRatpackMainApplicationUnderTest
import ratpack.impose.ImpositionsSpec
import ratpack.impose.ServerConfigImposition
import ratpack.test.ServerBackedApplicationUnderTest
import ratpack.test.http.TestHttpClient
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import static groovy.json.JsonOutput.toJson
import static ratpack.http.MediaType.APPLICATION_JSON

class NewsIntegrationTest extends Specification {

    static String DB_URL = "jdbc:h2:mem:${NewsIntegrationTest.name};DATABASE_TO_UPPER=false"

    @Shared
    @AutoCleanup
    Sql sql = Sql.newInstance(DB_URL, "sa", "")

    @Shared
    @AutoCleanup
    ServerBackedApplicationUnderTest aut = new GroovyRatpackMainApplicationUnderTest() {

        @Override
        protected void addImpositions(ImpositionsSpec impositions) {
            impositions.add(ServerConfigImposition.of {
                it.props "database.url": DB_URL
            })
        }
    }

    @Delegate
    TestHttpClient client = testHttpClient(aut)

    static def JSON = new JsonSlurper()

    def "when news posted then it appears in all items list"() {
        when: "posting news item"
        requestSpec {
            it.body.with {
                type(APPLICATION_JSON)
                text(toJson(new NewsItem(title: "title1", author: "author1", content: "content1")))
            }
        }
        and:
        def insertedId = postText("news")

        then: "response is OK, DB populated with corresponding item"
        response.statusCode == 200
        and:
        sql.firstRow("select count(*) from news_item where id = ${insertedId}")[0] == 1

        and: "getting list of items only one item is present, and id matches"
        requestSpec {
            it.body.type(APPLICATION_JSON)
        }
        JSON.parseText(getText("news"))[0].id == insertedId as Long
    }

    def "when news posted then it can be retrieved separately"() {
        given:
        NewsItem originalItem = new NewsItem(title: "title2", author: "author2", content: "content2");

        when: "posting news item"
        requestSpec {
            it.body.with {
                type(APPLICATION_JSON)
                text(toJson(originalItem))
            }
        }
        def insertedId = postText("news")

        then: "response is OK, DB populated with corresponding item"
        response.statusCode == 200
        and:
        sql.firstRow("select count(*) from news_item where id = ${insertedId}")[0] == 1

        when: "getting single item"
        requestSpec {
            it.body.type(APPLICATION_JSON)
        }
        def text = getText("news/$insertedId")
        def retrievedItem = JSON.parseText(text)

        then: "only one item is present, and all field match"
        retrievedItem.title == originalItem.title
        retrievedItem.author == originalItem.author
        retrievedItem.content == originalItem.content
    }
}
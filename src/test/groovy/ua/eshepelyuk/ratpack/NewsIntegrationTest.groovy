package ua.eshepelyuk.ratpack

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.sql.Sql
import ratpack.groovy.test.GroovyRatpackMainApplicationUnderTest
import ratpack.test.ServerBackedApplicationUnderTest
import ratpack.test.http.TestHttpClient
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import static ratpack.http.MediaType.APPLICATION_JSON

class NewsIntegrationTest extends Specification {

    @Shared
    @AutoCleanup
    ServerBackedApplicationUnderTest aut = new GroovyRatpackMainApplicationUnderTest()

    @Delegate
    TestHttpClient client = testHttpClient(aut)

    @Shared
    Sql sql = Sql.newInstance("jdbc:h2:mem:dev;DATABASE_TO_UPPER=false", "sa", "")

    def "when news posted then it appears in all items list"() {
        when: "posting news item"
        requestSpec {
            it.body.with {
                type(APPLICATION_JSON)
                text(JsonOutput.toJson(new NewsItem(title: "title1", author: "author1", content: "content1")))
            }
        }
        and:
        def insertedId = postText("news")

        then: "response is OK, DB populated with corresponding item"
        response.statusCode == 200
        insertedId != null
        and:
        sql.firstRow("select count(*) from news_item where id = ${insertedId}")[0] == 1

        when: "getting list of items"
        requestSpec {
            it.body.with {
                type(APPLICATION_JSON)
            }
        }
        and:
        def list = new JsonSlurper().parseText(getText("news"))

        then: "only one item is present, and id matches"
        list[0].id == insertedId as Long
    }

    def "when news posted then it can be retrieved separately"() {
        given:
        NewsItem originalItem = new NewsItem(title: "title2", author: "author2", content: "content2");

        when: "posting news item"
//        Response postNewsResp = NEWS.request(APPLICATION_JSON_TYPE).post(json(originalItem));
//        Long insertedId = postNewsResp.readEntity(Long.class);

        then: "response is OK, DB populated with corresponding item"
//        assertThat(postNewsResp.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
//        assertThat((Long) (jdbi.withHandle(handle -> (Long) handle.select("select count(*) as cnt from news_item where id = :id", insertedId).get(0).get("cnt")))).isEqualTo(1L);

        when: "getting single item"
//        NewsItem retrievedItem = NEWS.path("/" + insertedId).request(APPLICATION_JSON_TYPE).get(NewsItem.class);

        then: "only one item is present, and all field match"
//        assertThat(retrievedItem.getTitle()).isEqualTo(originalItem.getTitle());
//        assertThat(retrievedItem.getAuthor()).isEqualTo(originalItem.getAuthor());
//        assertThat(retrievedItem.getContent()).isEqualTo(originalItem.getContent());
//        assertThat(retrievedItem.getPublishDate()).isEqualTo(originalItem.getPublishDate());
    }
}
package ua.eshepelyuk.ratpack

import com.fasterxml.jackson.core.JsonParseException
import io.netty.handler.codec.http.HttpResponseStatus
import ratpack.jackson.JsonRender
import spock.lang.Specification

import javax.validation.ConstraintViolationException
import javax.validation.Validation

import static groovy.json.JsonOutput.toJson
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_ACCEPTABLE
import static ratpack.groovy.test.handling.GroovyRequestFixture.handle
import static ratpack.handling.internal.DefaultByContentSpec.TYPE_JSON
import static ratpack.handling.internal.DefaultByContentSpec.TYPE_XML

class NewsTest extends Specification {
    private static NewsItem createItem(Long id) {
        return new NewsItem(id, "title$id", "author$id", "content$id")
    }

    private static NewsItem createItemWithoutId() {
        return new NewsItem(null, "title", "author", "content")
    }

    def newsItemDAO = Mock(NewsItemDAO)

    def validator = Validation.buildDefaultValidatorFactory().validator
    def newsItemActionChain = new NewsItemChainAction(newsItemDAO: newsItemDAO, validator: validator)

    def "should use DAO for find all news"() {
        given:
        Collection<NewsItem> items = [createItem(1L)]
        and: "dao should be called once"
        1 * newsItemDAO.findAll() >> items

        when:
        def response = handle(newsItemActionChain) {
            uri ""
            method "GET"
            header "Accept", TYPE_JSON
        }

        then: "DAO called and proper response returned"
        response.rendered(JsonRender).object[0] == items[0]
    }

    def "should accept only JSON for find all news"() {
        given:
        def items = [createItem(1L)]
        and:
        0 * newsItemDAO.findAll()

        when: "using unsupported content type"
        def response = handle(newsItemActionChain) {
            uri ""
            method "GET"
            header "Accept", TYPE_XML
        }

        then: "DAO not called and HTTP status 4XX returned"
        response.status.nettyStatus == NOT_ACCEPTABLE
    }

    def "should use DAO for find single news"() {
        given:
        def item = createItem(2L)
        1 * newsItemDAO.findById(2L) >> item

        when:
        def response = handle(newsItemActionChain) {
            uri "2"
            method "GET"
            header "Accept", TYPE_JSON
        }

        then: "DAO called and proper result returned"
        response.rendered(JsonRender).object == item
    }

    def "should accept only JSON for find single news"() {
        given:
        NewsItem item = createItem(2L)
        0 * newsItemDAO.findById(2L)

        when: "using unsupported content type"
        def response = handle(newsItemActionChain) {
            uri "2"
            method "GET"
            header "Accept", TYPE_XML
        }

        then: "DAO not called and HTTP status 4XX returned"
        response.status.nettyStatus == NOT_ACCEPTABLE
    }

    def "should not return 200 if item not found"() {
        given:
        1 * newsItemDAO.findById(2L) >> null

        when:
        def response = handle(newsItemActionChain) {
            uri "2"
            method "GET"
            header "Accept", TYPE_JSON
        }

        then: "DAO called and proper result returned"
        response.status.nettyStatus == HttpResponseStatus.NOT_FOUND
    }

    def "should not return 200 if DB error"() {
        given: "DB throws exception"
        1 * newsItemDAO.findById(_) >> { throw new RuntimeException("DB error") }

        when:
        def response = handle(newsItemActionChain) {
            uri "2"
            method "GET"
            header "Accept", TYPE_JSON
        }

        then:
        response.exception(RuntimeException).message == "DB error"
    }

    def "should use DAO for adding news"() {
        given:
        NewsItem item = createItemWithoutId()
        1 * newsItemDAO.insert(_) >> 444L

        when:
        def response = handle(newsItemActionChain) {
            uri ""
            method "POST"
            body(toJson(item), TYPE_JSON)
            header "Accept", TYPE_JSON
        }

        then: "DAO called and proper result returned"
        response.bodyText as Long == 444L
    }

    def "should accept only JSON for adding news"() {
        given:
        NewsItem item = createItemWithoutId()
        0 * newsItemDAO.insert(_)

        when: "using unsupported content type"
        def response = handle(newsItemActionChain) {
            uri ""
            method "POST"
            body(toJson(item), TYPE_XML)
            header "Accept", TYPE_XML
        }

        then: "DAO not called and 4XX returned"
        response.status.nettyStatus == NOT_ACCEPTABLE
    }

    def "should accept only proper JSON for adding news"() {
        given:
        0 * newsItemDAO.insert(_)

        when: "posting arbitrary data instead of JSON"
        def response = handle(newsItemActionChain) {
            uri ""
            method "POST"
            body("qwerty", TYPE_JSON)
            header "Accept", TYPE_JSON
        }

        then: "DAO not called and 4XX returned"
        response.exception(JsonParseException).originalMessage.contains("qwerty")
    }

    def "should validate item when adding news"() {
        given: "item violating validation"
        NewsItem item = new NewsItem(null, null, "author5", "content5", new Date());
        0 * newsItemDAO.insert(_)

        when:
        def response = handle(newsItemActionChain) {
            uri ""
            method "POST"
            body(toJson(item), TYPE_JSON)
            header "Accept", TYPE_JSON
        }

        then: "DAO not called and 4XX returned"
        response.exception(ConstraintViolationException).constraintViolations.with {
            it.size() == 1 && it[0].invalidValue == null
        }
    }
}

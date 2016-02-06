package ua.eshepelyuk.ratpack

import groovy.json.JsonOutput
import io.netty.handler.codec.http.HttpResponseStatus
import ratpack.jackson.JsonRender
import spock.lang.Specification

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

    def newsItemActionChain = new NewsItemChainAction(newsItemDAO: newsItemDAO)

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
        0 * newsItemDAO.findAll() >> items

        when: "using unsupported content type"
        def response = handle(newsItemActionChain) {
            uri ""
            method "GET"
            header "Accept", TYPE_XML
        }

        then: "DAO not called and HTTP status 4XX returned"
        response.status.nettyStatus == NOT_ACCEPTABLE
    }

    def "should Use Dao For Find Single News"() {
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

    def "should Accept Only JSON For Find Single News"() {
        given:
        NewsItem item = createItem(2L)
        0 * newsItemDAO.findById(2L) >> item

        when: "using unsupported content type"
        def response = handle(newsItemActionChain) {
            uri "2"
            method "GET"
            header "Accept", TYPE_XML
        }

        then: "DAO not called and HTTP status 4XX returned"
        response.status.nettyStatus == NOT_ACCEPTABLE
    }

    def "should Not Return 200 If Item Not Found"() {
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

    def "should Not Return 200 If DB error"() {
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

    def "should Use Dao For Adding News"() {
        given:
        NewsItem item = createItemWithoutId()
        1 * newsItemDAO.insert(_) >> 444L

        when:
        def response = handle(newsItemActionChain) {
            uri ""
            method "POST"
            body(JsonOutput.toJson(item), TYPE_JSON)
            header "Accept", TYPE_JSON
        }

        then: "DAO called and proper result returned"
        response.bodyText as Long == 444L
    }

//    @Test
//    public void shouldAcceptOnlyJSONForAddingNews() {
//        given:
//        NewsItem item = createItemWithoutId();
//        when(itemsDao.insert(any(NewsItem.class))).thenReturn(444L);
//
//        when: using unsupported content type
//        Response response = NEWS.request(APPLICATION_ATOM_XML).post(json(item));
//
//        then: DAO not called and 4XX returned
//        verifyZeroInteractions(itemsDao);
//        assertThat(response.getStatus()).isEqualTo(NOT_ACCEPTABLE.getStatusCode());
//    }
//
//    @Test
//    public void shouldAcceptOnlyProperJSONForAddingNews() {
//        given:
//        when(itemsDao.insert(any(NewsItem.class))).thenReturn(444L);
//
//        when: posting arbitrary data instead of JSON
//        Response response = NEWS.request(APPLICATION_JSON_TYPE).post(text("test string"));
//
//        then: DAO not called and 4XX returned
//        verifyZeroInteractions(itemsDao);
//        assertThat(response.getStatus()).isEqualTo(UNSUPPORTED_MEDIA_TYPE.getStatusCode());
//    }
//
//    @Test
//    public void shouldValidateItemWhenAddingNews() {
//        given: item violating validation
//        NewsItem item = new NewsItem(null, "author5", "content5", new Date());
//        when(itemsDao.insert(any(NewsItem.class))).thenReturn(555L);
//
//        when:
//        Response response = NEWS.request(APPLICATION_JSON_TYPE).post(json(item));
//
//        then: DAO not called and 4XX returned
//        verifyZeroInteractions(itemsDao);
//        assertThat(response.getStatus()).isEqualTo(422);
//    }
}

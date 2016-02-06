package ua.eshepelyuk.ratpack

import groovy.json.JsonSlurper
import ratpack.jackson.JsonRender
import spock.lang.Specification

import static ratpack.groovy.test.handling.GroovyRequestFixture.handle

class NewsTest extends Specification {
    private static NewsItem createItem(Long id) {
        return new NewsItem(id, "title$id", "author$id", "content$id")
    }

    private static NewsItem createItemWithoutId() {
        return new NewsItem(null, "title", "author", "content")
    }

    static def JSON = new JsonSlurper()

    def newsItemDAO = Mock(NewsItemDAO)

    def newsItemActionChain = new NewsItemChainAction(newsItemDAO: newsItemDAO)

    def "should use DAO for find all news"() {
        given:
        Collection<NewsItem> items = [createItem(1L)]
        and: "dao should be called once"
        1 * newsItemDAO.findAll() >> items

        when:
        def result = handle(newsItemActionChain) {
            uri ""
            method "GET"
            header "Accept", "application/json"
        }

        then: "DAO called and proper result returned"
        result.rendered(JsonRender).object[0] == items[0]
    }

//    def shouldAcceptOnlyJSONForFindAllNews() {
//        given:
//        Collection<NewsItem> items = singletonList(createItem(1L));
//        when(itemsDao.findAll()).thenReturn(items);
//
//        when: using unsupported content type
//        Response response = NEWS.request(APPLICATION_ATOM_XML_TYPE).get();
//
//        then: DAO not called and HTTP status 4XX returned
//        verifyZeroInteractions(itemsDao);
//        assertThat(response.getStatus()).isEqualTo(NOT_ACCEPTABLE.getStatusCode());
//    }
//
//    def shouldUseDaoForFindSingleNews() {
//        given:
//        NewsItem item = createItem(2L);
//        when(itemsDao.findById(eq(222L))).thenReturn(item);
//
//        when:
//        NewsItem retrieved = NEWS.path("/222").request(APPLICATION_JSON_TYPE).get().readEntity(NewsItem.class);
//
//        then: DAO called and proper result returned
//        verify(itemsDao).findById(222L);
//        assertThat(retrieved.getTitle()).isEqualTo(item.getTitle());
//    }
//
//    @Test
//    public void shouldAcceptOnlyJSONForFindSingleNews() {
//        given:
//        NewsItem item = createItem(2L);
//        when(itemsDao.findById(eq(222L))).thenReturn(item);
//
//        when: using unsupported content type
//        Response response = NEWS.path("/222").request(APPLICATION_ATOM_XML_TYPE).get();
//
//        then: DAO not called and HTTP status 4XX returned
//        verifyZeroInteractions(itemsDao);
//        assertThat(response.getStatus()).isEqualTo(NOT_ACCEPTABLE.getStatusCode());
//    }
//
//    @Test
//    public void shouldNotReturn200IfItemNotFound() {
//        given:
//        when(itemsDao.findById(eq(333L))).thenReturn(null);
//
//        when:
//        Response response = NEWS.path("/333").request(APPLICATION_JSON_TYPE).get();
//
//        then: DAO called and proper result returned
//        verify(itemsDao).findById(333L);
//        assertThat(response.getStatus()).isNotEqualTo(Response.Status.OK.getStatusCode());
//    }
//
//    @Test(expected = ProcessingException.class)
//    public void shouldNotReturn200IfDbException() {
//        given: "DB throws exception"
//        when(itemsDao.findById(anyLong())).thenThrow(new DBIException("DB error") {
//        });
//
//        when:
//        NEWS.path("/333").request(APPLICATION_JSON_TYPE).get();
//
//        then: exception
//    }
//
//    @Test
//    public void shouldUseDaoForAddingNews() {
//        given:
//        NewsItem item = createItemWithoutId();
//        when(itemsDao.insert(any(NewsItem.class))).thenReturn(444L);
//
//        when:
//        Long insertedId = NEWS.request(APPLICATION_JSON_TYPE).post(json(item)).readEntity(Long.class);
//
//        then: DAO called and proper result returned
//        verify(itemsDao).insert(any(NewsItem.class));
//        assertThat(insertedId).isEqualTo(444L);
//    }
//
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

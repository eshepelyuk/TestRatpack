package ua.eshepelyuk.ratpack

import spock.lang.Specification


class NewsIntegrationTest extends Specification {
    def "when news posted then it appears in all items list"() {
        when: "posting news item"
//        Response postNewsResp = NEWS.request(APPLICATION_JSON_TYPE).post(json(new NewsItem("title1", "author1", "content1", new Date())));
//        Long insertedId = postNewsResp.readEntity(Long.class);

        then: "response is OK, DB populated with corresponding item"
//        assertThat(postNewsResp.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
//        assertThat((Long) (jdbi.withHandle(handle -> (Long) handle.select("select count(*) as cnt from news_item where id = :id", insertedId).get(0).get("cnt")))).isEqualTo(1L);

        when: "getting list of items"
//        Collection<NewsItem> items = NEWS.request(APPLICATION_JSON_TYPE).get(new GenericType<Collection<NewsItem>>() {
//        });

        then: "only one item is present, and id matches"
//        assertThat(items.size()).isEqualTo(1);
//        assertThat(items.iterator().next().getId()).isEqualTo(insertedId);
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
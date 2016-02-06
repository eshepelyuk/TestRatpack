package ua.eshepelyuk.ratpack

import groovy.transform.ToString
import groovy.transform.TupleConstructor
import org.hibernate.validator.constraints.Length
import org.hibernate.validator.constraints.NotBlank

import javax.validation.constraints.NotNull

@ToString(includeNames = true)
@TupleConstructor()
class NewsItem {
    Long id

    @Length(max = 255)
    @NotBlank
    String title

    @Length(max = 255)
    @NotBlank
    String author

    @Length(max = 2048)
    @NotBlank
    String content

    @NotNull
    Date publishDate = new Date()
}

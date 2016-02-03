package ua.eshepelyuk.ratpack

import groovy.transform.CompileStatic
import groovy.transform.ToString

@CompileStatic
@ToString
class DatabaseConfig {
    String user
    String password
    String url
}

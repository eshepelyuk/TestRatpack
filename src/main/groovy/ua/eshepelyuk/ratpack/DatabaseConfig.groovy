package ua.eshepelyuk.ratpack

import groovy.transform.ToString

@ToString(includeNames = true)
class DatabaseConfig {
    String user
    String password
    String url
}

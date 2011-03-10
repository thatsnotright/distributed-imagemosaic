//dataSource {
//    pooled = true
//    driverClassName = "org.hsqldb.jdbcDriver"
//    username = "sa"
//    password = ""
//}
dataSource {
    pooled = true
    driverClassName = "com.mysql.jdbc.Driver"
    username = "root"
    password = "chris"
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = true
    cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'
}
// environment specific settings
// url = "jdbc:hsqldb:mem:devDB"
// production url = "jdbc:hsqldb:file:prodDb;shutdown=true"
// url = "jdbc:mysql://localhost/mosaic"
environments {
    development {
        dataSource {
            dbCreate = "create-drop" // one of 'create', 'create-drop','update'
            url = "jdbc:mysql://localhost/mosaic"
        }
    }
    test {
        dataSource {
            dbCreate = "update"
            url = "jdbc:mysql://localhost/mosaic"
        }
    }
    production {
        dataSource {
            dbCreate = "update"
            url = "jdbc:mysql://mosaic.cndqhndk8zch.us-east-1.rds.amazonaws.com/mosaic"
            username = "mosaicUser"
            password = "124lext335"
        }
    }
}

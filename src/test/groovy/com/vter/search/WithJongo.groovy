package com.vter.search

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.github.fakemongo.Fongo
import com.gmongo.GMongo
import com.mongodb.DB
import com.mongodb.DBCollection
import org.jongo.Jongo
import org.jongo.marshall.jackson.JacksonMapper
import org.junit.rules.ExternalResource

class WithJongo extends ExternalResource {

  @Override
  protected void before() throws Throwable {
    fongo = new Fongo("test")
    jongo = new Jongo(
        fongo.getDB("test"),
        new JacksonMapper.Builder()
            .registerModule(new JodaModule())
            .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build()
    )
  }

  @Override
  protected void after() {
    fongo.dropDatabase("test")
  }

  public Jongo jongo() {
    return jongo
  }

  public DB db() {
    return new GMongo(fongo.mongo).getDB("test")
  }

  public DBCollection collection(String collection) {
    return db().getCollection(collection)
  }

  private Jongo jongo
  private Fongo fongo
}

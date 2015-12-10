package integration

import com.google.inject.Injector
import com.mongodb.DB
import groovy.json.JsonSlurper
import net.codestory.rest.FluentRestTest
import org.junit.ClassRule
import spock.lang.Specification

abstract class AbstractIntegrationSpecification extends Specification implements FluentRestTest {
  @ClassRule
  static WithLoadedEnvironment environment = new WithLoadedEnvironment()

  protected static JsonSlurper slurper = new JsonSlurper()

  void cleanup() {
    db.getCollectionNames().each {
      if (it != "system.indexes") {
        db[it].remove([:])
      }
    }
  }

  Injector getInjector() {
    return environment.injector()
  }

  DB getDb() {
    return environment.db()
  }

  @Override
  int port() {
    return environment.port()
  }
}

package com.vter.web.fluent

import net.codestory.http.Configuration
import net.codestory.rest.FluentRestTest
import org.junit.ClassRule
import spock.lang.Specification

abstract class AbstractProdWebServerSpecification extends Specification implements FluentRestTest {
  @ClassRule
  static WithProdWebServer server = new WithProdWebServer()

  @Override
  int port() {
    return server.port()
  }

  protected static void configure(Configuration configuration) {
    server.configure(configuration)
  }
}

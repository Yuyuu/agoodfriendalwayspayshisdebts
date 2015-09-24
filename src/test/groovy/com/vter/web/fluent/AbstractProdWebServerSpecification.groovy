package com.vter.web.fluent

import net.codestory.http.Configuration
import net.codestory.rest.FluentRestTest
import org.junit.ClassRule
import spock.lang.Specification

abstract class AbstractProdWebServerSpecification extends Specification implements FluentRestTest {
  @ClassRule
  public static WithProdWebServer server = new WithProdWebServer()

  @Override
  public int port() {
    return server.port()
  }

  protected void configure(Configuration configuration) {
    server.configure(configuration)
  }
}

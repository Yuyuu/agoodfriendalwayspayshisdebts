package com.vter.web.fluent

import net.codestory.http.Configuration
import net.codestory.http.WebServer
import net.codestory.http.misc.Env
import net.codestory.http.misc.MemoizingSupplier
import org.junit.rules.ExternalResource

import java.util.function.Supplier

class WithProdWebServer extends ExternalResource {
  private static Supplier<WebServer> server = MemoizingSupplier.memoize({ ->
    new WebServer() {
      @Override
      protected Env createEnv() {
        return Env.prod()
      }
    }.startOnRandomPort()
  })

  @Override
  protected void after() {
    server.get().configure(Configuration.NO_ROUTE)
  }

  static void configure(Configuration configuration) {
    server.get().configure(configuration)
  }

  static int port() {
    return server.get().port()
  }
}

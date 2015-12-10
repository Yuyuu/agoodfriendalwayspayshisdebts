package com.vter.web.fluent.status

import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.ListeningExecutorService
import com.google.common.util.concurrent.MoreExecutors
import com.vter.infrastructure.bus.ExecutionResult
import com.vter.web.fluent.AbstractProdWebServerSpecification
import net.codestory.http.Configuration
import net.codestory.http.Request
import net.codestory.http.Response
import net.codestory.http.compilers.CompilerFacade
import net.codestory.http.extensions.Extensions
import net.codestory.http.io.Resources
import net.codestory.http.misc.Env
import net.codestory.http.payload.Payload
import net.codestory.http.payload.PayloadWriter
import net.codestory.http.templating.Site

import java.util.concurrent.Callable

class AsyncResponseSpec extends AbstractProdWebServerSpecification {
  ListeningExecutorService executorService = MoreExecutors.newDirectExecutorService()

  def "can handle any content"() {
    given:
    routes { routes -> routes.get("/", new Payload("hello")) }

    expect:
    get("/").should().respond(200).contain("hello")
  }

  def "can handle an execution result"() {
    given:
    routes { routes -> routes.get("/", new Payload(future({ -> ExecutionResult.success("hello") }))) }

    expect:
    get("/").should().respond(200).contain("hello")
  }

  def "preserves the initial payload configuration"() {
    given:
    routes { routes ->
      routes.get(
          "/",
          new Payload("application/json", future({ -> ExecutionResult.success("hello") }), 201)
              .withCookie("i18next", "fr")
              .withHeader("hello", "hi")
      )
    }

    expect:
    get("/").should().haveType("application/json").respond(201).haveHeader("hello", "hi").haveCookie("i18next", "fr")
  }

  def "can handle an error execution result"() {
    given:
    routes { routes ->
      routes.get("/", new Payload(future({ -> ExecutionResult.error(new RuntimeException("hello")) })))
    }

    expect:
    get("/").should().respond(500).beEmpty()
  }

  private static void routes(Configuration configuration) {
    final Configuration additionalConfiguration = { routes ->
      routes.setExtensions(new Extensions() {
        @Override
        PayloadWriter createPayloadWriter(Request request, Response response, Env env, Site site, Resources resources, CompilerFacade compilers) {
          return new ApplicationPayloadWriter(request, response, env, site, resources, compilers, new ApplicationStatusService([] as Set))
        }
      })
    }
    configure(Configuration.override(configuration).with(additionalConfiguration))
  }

  private <T> ListenableFuture<T> future(Callable<T> callable) {
    return executorService.submit(callable as Callable<T>)
  }
}

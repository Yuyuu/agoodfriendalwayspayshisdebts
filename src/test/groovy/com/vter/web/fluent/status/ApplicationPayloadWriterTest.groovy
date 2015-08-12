package com.vter.web.fluent.status

import net.codestory.http.Request
import net.codestory.http.Response
import net.codestory.http.compilers.CompilerFacade
import net.codestory.http.io.Resources
import net.codestory.http.misc.Env
import net.codestory.http.templating.Site
import spock.lang.Shared
import spock.lang.Specification

class ApplicationPayloadWriterTest extends Specification {
  @Shared Env env = Env.dev();
  @Shared Resources resources = new Resources(env)
  @Shared Site site = new Site(env, resources)
  @Shared CompilerFacade compilerFacade = new CompilerFacade(env, resources)

  Request request = Mock(Request)
  Response response = Mock(Response)
  StatusService statusService = Mock(StatusService)

  ApplicationPayloadWriter writer = new ApplicationPayloadWriter(request, response, env, site, resources, compilerFacade, statusService)

  def "puts the error representation in the payload"() {
    given:
    def throwable = new RuntimeException("this is an error")
    statusService.getStatus(throwable) >> 400
    def representation = Mock(ErrorRepresentation)
    statusService.getRepresentation(throwable) >> representation

    when:
    def payload = writer.errorPage(throwable)

    then:
    payload.rawContentType() == "application/json;charset=UTF-8"
    payload.code() == 400
    payload.rawContent() == representation
  }
}

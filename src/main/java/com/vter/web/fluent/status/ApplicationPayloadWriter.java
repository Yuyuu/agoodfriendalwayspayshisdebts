package com.vter.web.fluent.status;

import net.codestory.http.Request;
import net.codestory.http.Response;
import net.codestory.http.compilers.CompilerFacade;
import net.codestory.http.io.Resources;
import net.codestory.http.misc.Env;
import net.codestory.http.payload.Payload;
import net.codestory.http.payload.PayloadWriter;
import net.codestory.http.templating.Site;

public class ApplicationPayloadWriter extends PayloadWriter {

  public ApplicationPayloadWriter(Request request, Response response, Env env, Site site, Resources resources, CompilerFacade compilers, StatusService statusService) {
    super(request, response, env, site, resources, compilers);
    this.statusService = statusService;
  }

  @Override
  protected Payload errorPage(Throwable e) {
    int statusCode = statusService.getStatus(e);
    ErrorRepresentation representation = statusService.getRepresentation(e);
    return new Payload("application/json;charset=UTF-8", representation, statusCode);
  }

  private final StatusService statusService;
}

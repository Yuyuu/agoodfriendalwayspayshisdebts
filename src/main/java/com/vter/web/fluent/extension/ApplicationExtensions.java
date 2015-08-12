package com.vter.web.fluent.extension;

import com.vter.web.fluent.status.ApplicationPayloadWriter;
import com.vter.web.fluent.status.StatusService;
import net.codestory.http.Request;
import net.codestory.http.Response;
import net.codestory.http.compilers.CompilerFacade;
import net.codestory.http.extensions.Extensions;
import net.codestory.http.io.Resources;
import net.codestory.http.misc.Env;
import net.codestory.http.payload.PayloadWriter;
import net.codestory.http.templating.Site;

public class ApplicationExtensions implements Extensions {

  private ApplicationExtensions(StatusService statusService) {
    this.statusService = statusService;
  }

  public static ApplicationExtensions withStatusService(StatusService statusService) {
    return new ApplicationExtensions(statusService);
  }

  @Override
  public PayloadWriter createPayloadWriter(Request request, Response response, Env env, Site site, Resources resources, CompilerFacade compilers) {
    return new ApplicationPayloadWriter(request, response, env, site, resources, compilers, statusService);
  }

  private final StatusService statusService;
}

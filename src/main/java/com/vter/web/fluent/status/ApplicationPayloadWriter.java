package com.vter.web.fluent.status;

import com.google.common.base.Throwables;
import com.vter.infrastructure.bus.ExecutionResult;
import net.codestory.http.Request;
import net.codestory.http.Response;
import net.codestory.http.compilers.CompilerFacade;
import net.codestory.http.io.Resources;
import net.codestory.http.misc.Env;
import net.codestory.http.payload.Payload;
import net.codestory.http.payload.PayloadWriter;
import net.codestory.http.templating.Site;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class ApplicationPayloadWriter extends PayloadWriter {

  public ApplicationPayloadWriter(Request request, Response response, Env env, Site site, Resources resources, CompilerFacade compilers, StatusService statusService) {
    super(request, response, env, site, resources, compilers);
    this.statusService = statusService;
  }

  /*
   * https://github.com/CodeStory/fluent-http/blob/fdb6126a412bf1f13129cac515b59138794c4247/src/main/java/net/codestory/http/payload/PayloadWriter.java#L100-L104
   *
   * Override this one because the default behavior to display an error page or populate the payload with some json
   * is not wanted when the original content is null.
   */
  @Override
  protected void writeAndCloseSync(Payload payload) throws IOException {
    write(payload);
    close();
  }

  @Override
  protected CompletableFuture<Void> writeAndCloseAsync(Payload payload) {
    final CompletableFuture<?> future = (CompletableFuture<?>) payload.rawContent();

    return future.thenAccept(result -> {
      try {
        Object content = (result instanceof ExecutionResult) ? contentOfExecutionResult(result) : result;
        writeAndCloseSync(clonePayloadWithContent(payload, content));
      } catch (Exception e) {
        writeErrorPage(e);
      }
    }).exceptionally(e -> {
      writeErrorPage(e);
      return null;
    });
  }

  @Override
  protected Payload errorPage(Throwable e) {
    final int statusCode = statusService.getStatus(e);
    final ErrorRepresentation representation = statusService.getRepresentation(e);
    return new Payload("application/json;charset=UTF-8", representation, statusCode);
  }

  private static Object contentOfExecutionResult(Object payloadContent) {
    final ExecutionResult executionResult = (ExecutionResult) payloadContent;
    if (!executionResult.isSuccess()) {
      Throwables.propagate(executionResult.error());
    }
    return executionResult.data();
  }

  private static Payload clonePayloadWithContent(Payload payload, Object content) {
    return new Payload(payload.rawContentType(), content, payload.code())
        .withHeaders(payload.headers())
        .withCookies(payload.cookies());
  }

  private final StatusService statusService;
}

package com.vter.web.actions;

import com.vter.infrastructure.bus.ExecutionResult;
import com.vter.search.*;
import net.codestory.http.constants.Headers;
import net.codestory.http.payload.Payload;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class BasePaginationAction extends BaseAction {

  public BasePaginationAction(SearchBus searchBus) {
    this.searchBus = searchBus;
  }

  protected abstract <TSearch extends Search<?>> String resourcePartialUri(TSearch tSearch);

  protected Payload paginationPayload(Search<? extends PaginatedSearchResult> search) {
    final ExecutionResult<? extends PaginatedSearchResult> executionResult = searchBus.sendAndWaitResponse(search);
    final Optional<? extends PaginatedSearchResult> optionalResult = getOptionalDataOrFail(executionResult);

    if (optionalResult.isPresent()) {
      return payloadWithLinkHeader(search, optionalResult.get());
    }
    return new Payload(optionalResult);
  }

  private Payload payloadWithLinkHeader(Search search, PaginatedSearchResult result) {
    final List<RelationshipLink> links = new LinkHeaderBuilder(search, result.totalCount).get();
    final String linkHeader = links.stream()
        .map(buildWith(resourcePartialUri(search)))
        .collect(Collectors.joining(", "));
    final Payload payload = new Payload(result);
    return linkHeader.isEmpty() ? payload : payload.withHeader(Headers.LINK, linkHeader);
  }

  private Function<RelationshipLink, String> buildWith(String partialUri) {
    return link -> "<" + partialUri + "page=" + link.page() + ">; rel=\"" + link.target() + "\"";
  }

  protected final SearchBus searchBus;
}

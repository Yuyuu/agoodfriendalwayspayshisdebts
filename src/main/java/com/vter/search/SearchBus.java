package com.vter.search;

import com.vter.infrastructure.bus.AsynchronousBus;

import javax.inject.Inject;
import java.util.Set;

public class SearchBus extends AsynchronousBus {

  @Inject
  public SearchBus(Set<SearchSynchronization> synchronizations, Set<SearchHandler> handlers) {
    super(synchronizations, handlers);
  }
}

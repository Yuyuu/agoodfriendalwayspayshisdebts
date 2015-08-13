package com.vter.search;

import com.google.common.collect.Sets;
import com.vter.infrastructure.bus.AsynchronousBus;

import javax.inject.Inject;
import java.util.Set;

public class SearchBus extends AsynchronousBus {

  @Inject
  public SearchBus(Set<SearchHandler> handlers) {
    super(Sets.newHashSet(), handlers);
  }
}

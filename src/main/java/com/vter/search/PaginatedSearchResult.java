package com.vter.search;

import java.util.List;

public abstract class PaginatedSearchResult<TItem> {
  public int totalCount;
  public List<TItem> items;

  protected PaginatedSearchResult() {}

  public PaginatedSearchResult(int totalCount, List<TItem> items) {
    this.totalCount = totalCount;
    this.items = items;
  }
}

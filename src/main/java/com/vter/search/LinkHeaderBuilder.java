package com.vter.search;

import com.google.common.collect.Lists;

import java.util.List;

public class LinkHeaderBuilder {

  public LinkHeaderBuilder(Search search, int totalResultCount) {
    this.search = search;
    this.totalResultCount = totalResultCount;
  }

  public List<RelationshipLink> get() {
    assert search.perPage() > 0 && search.page() > 0;
    final List<RelationshipLink> links = Lists.newArrayList();
    final int lastPage = (totalResultCount + search.perPage() - 1) / search.perPage();
    if (search.page() < lastPage) {
      links.add(next());
      links.add(last(lastPage));
    }
    if (search.page() > 1) {
      links.add(first());
      links.add(prev());
    }
    return links;
  }

  private RelationshipLink next() {
    return RelationshipLink.next(search.page() + 1);
  }

  private RelationshipLink last(int lastPage) {
    return RelationshipLink.last(lastPage);
  }

  private RelationshipLink first() {
    return RelationshipLink.first(1);
  }

  private RelationshipLink prev() {
    return RelationshipLink.prev(search.page() - 1);
  }

  private final Search search;
  private final int totalResultCount;
}

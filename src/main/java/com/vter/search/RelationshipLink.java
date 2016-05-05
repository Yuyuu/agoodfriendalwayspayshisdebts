package com.vter.search;

import com.google.common.base.Objects;

public class RelationshipLink {

  private RelationshipLink(PaginationTarget paginationTarget, int page) {
    this.paginationTarget = paginationTarget;
    this.page = page;
  }

  public static RelationshipLink next(int page) {
    return new RelationshipLink(PaginationTarget.NEXT, page);
  }

  public static RelationshipLink last(int page) {
    return new RelationshipLink(PaginationTarget.LAST, page);
  }

  public static RelationshipLink first(int page) {
    return new RelationshipLink(PaginationTarget.FIRST, page);
  }

  public static RelationshipLink prev(int page) {
    return new RelationshipLink(PaginationTarget.PREVIOUS, page);
  }

  public int page() {
    return page;
  }

  public PaginationTarget target() {
    return paginationTarget;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RelationshipLink that = (RelationshipLink) o;
    return page == that.page &&
        paginationTarget == that.paginationTarget;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(paginationTarget, page);
  }

  private final PaginationTarget paginationTarget;
  private final int page;
}

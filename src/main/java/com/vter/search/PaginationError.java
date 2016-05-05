package com.vter.search;

public class PaginationError extends RuntimeException {
  public PaginationError() {
    super("Bad pagination request");
  }
}

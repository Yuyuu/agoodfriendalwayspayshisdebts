package com.vter.model;

public class BusinessError extends RuntimeException {

  public BusinessError(String message) {
    super(message);
  }
}

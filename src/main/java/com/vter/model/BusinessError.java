package com.vter.model;

public abstract class BusinessError extends RuntimeException {

  public BusinessError(String message) {
    super(message);
  }
}

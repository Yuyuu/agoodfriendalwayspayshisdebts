package com.vter.infrastructure.bus;

public class BusError extends RuntimeException {

  public BusError(String message) {
    super(message);
  }
}

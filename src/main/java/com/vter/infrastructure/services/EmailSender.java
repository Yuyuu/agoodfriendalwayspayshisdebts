package com.vter.infrastructure.services;

public interface EmailSender {

  void send(Email email) throws Exception;
}

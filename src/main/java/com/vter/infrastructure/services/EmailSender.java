package com.vter.infrastructure.services;

import javax.mail.MessagingException;

public interface EmailSender {

  void send(Email email) throws MessagingException;
}

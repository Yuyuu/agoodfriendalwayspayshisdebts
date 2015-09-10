package com.vter.infrastructure.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SMTPEmailSender implements EmailSender {

  @Inject
  public SMTPEmailSender(Session session) {
    this.session = session;
  }

  @Override
  public void send(Email email) throws MessagingException {
    final MimeMessage message = new MimeMessage(session);
    message.setRecipient(Message.RecipientType.TO, address(email.to()));
    message.setSubject(email.subject());
    message.setContent(email.content(), "text/html;charset=utf-8");

    LOGGER.debug("Sending an email to {}", email.to());
    Transport.send(message);
  }

  private static Address address(String address) throws AddressException {
    return new InternetAddress(address);
  }

  private final Session session;
  protected final static Logger LOGGER = LoggerFactory.getLogger(SMTPEmailSender.class);
}

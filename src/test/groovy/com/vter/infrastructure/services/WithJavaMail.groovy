package com.vter.infrastructure.services

import org.junit.rules.ExternalResource
import org.jvnet.mock_javamail.Mailbox

import javax.mail.Session

class WithJavaMail extends ExternalResource {

  @Override
  protected void before() throws Throwable {
    Properties properties = new Properties()
    properties.setProperty("mail.smtp.host", "smtp.domain.com")
    properties.setProperty("mail.smtp.port", "4712")
    properties.setProperty("mail.smtp.from", "agoodfriendalwayspayshisdebts@email.com")
    properties.setProperty("mail.smtp.auth", "false")
    properties.setProperty("mail.smtp.starttls.enable", "false")

    session = Session.getDefaultInstance(properties)
  }

  @Override
  protected void after() {
    Mailbox.clearAll()
  }

  Session session() {
    return session
  }

  private Session session
}

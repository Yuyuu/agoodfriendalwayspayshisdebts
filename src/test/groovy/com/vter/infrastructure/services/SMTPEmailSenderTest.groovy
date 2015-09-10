package com.vter.infrastructure.services

import org.junit.Rule
import org.jvnet.mock_javamail.Mailbox
import spock.lang.Specification

import javax.mail.Message

class SMTPEmailSenderTest extends Specification {
  @Rule
  WithJavaMail withJavaMail = new WithJavaMail()

  SMTPEmailSender emailSender

  def setup() {
    emailSender = new SMTPEmailSender(withJavaMail.session())
  }

  def "sends an email with the given parameters"() {
    when:
    emailSender.send(new FakeEmail(to: "ben@email.fr", subject: "hi", content: "hello"))

    then:
    Mailbox.get("ben@email.fr").newMessageCount == 1
    Message message = Mailbox.get("ben@email.fr").first()
    message.allRecipients.first().toString() == "ben@email.fr"
    message.subject == "hi"
    message.content == "hello"
  }

  private static class FakeEmail implements Email {
    String to
    String subject
    String content

    @Override
    String to() {
      return to
    }

    @Override
    String subject() {
      return subject
    }

    @Override
    String content() {
      return content
    }
  }
}

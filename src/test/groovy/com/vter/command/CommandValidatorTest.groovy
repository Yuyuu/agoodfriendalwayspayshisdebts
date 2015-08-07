package com.vter.command

import com.vter.infrastructure.bus.Message
import org.hibernate.validator.constraints.NotEmpty
import spock.lang.Specification

import javax.validation.Validation
import javax.validation.ValidatorFactory

class CommandValidatorTest extends Specification {

  ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()

  def "can validate a command"() {
    given:
    def validator = commandValidator()

    when:
    validator.beforeExecution(new FakeMessage(""))

    then:
    thrown(ValidationException)
  }

  def "can give the error cause"() {
    given:
    def validator = commandValidator()

    when:
    validator.beforeExecution(new FakeMessage(""))

    then:
    ValidationException exception = thrown()
    exception.messages()[0]
  }

  private CommandValidator commandValidator() {
    new CommandValidator(validatorFactory.validator)
  }

  private class FakeMessage implements Message<String> {
    @NotEmpty
    String name

    FakeMessage(String name) {
      this.name = name;
    }
  }
}

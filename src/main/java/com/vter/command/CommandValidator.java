package com.vter.command;

import com.vter.infrastructure.bus.Message;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CommandValidator implements CommandSynchronization {

  @Inject
  public CommandValidator(Validator validator) {
    this.validator = validator;
  }

  @Override
  public void beforeExecution(Message<?> command) {
    validate(command);
  }

  private void validate(Message<?> command) {
    Set<ConstraintViolation<Message<?>>> violations = validator.validate(command);
    if (!violations.isEmpty()) {
      throw new ValidationException(asMessages(violations));
    }
  }

  private List<String> asMessages(Set<ConstraintViolation<Message<?>>> violations) {
    return violations.stream().map(
        violation -> violation.getPropertyPath() + " " + violation.getMessage()
    ).collect(Collectors.toList());
  }

  private final Validator validator;
}

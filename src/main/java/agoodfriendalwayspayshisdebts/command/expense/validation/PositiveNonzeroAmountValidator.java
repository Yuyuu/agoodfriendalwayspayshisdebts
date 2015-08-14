package agoodfriendalwayspayshisdebts.command.expense.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PositiveNonzeroAmountValidator implements ConstraintValidator<PositiveNonzeroAmount, Double> {
  @Override
  public void initialize(PositiveNonzeroAmount constraintAnnotation) {}

  @Override
  public boolean isValid(Double value, ConstraintValidatorContext context) {
    return (value == null) || (value > 0);
  }
}

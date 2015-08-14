package agoodfriendalwayspayshisdebts.command.expense.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PositiveNonzeroAmountValidator.class)
@Documented
public @interface PositiveNonzeroAmount {
  String message() default "";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}

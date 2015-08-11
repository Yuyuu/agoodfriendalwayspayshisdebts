package agoodfriendalwayspayshisdebts.command.event.validation;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ValidParticipantsValidator implements ConstraintValidator<ValidParticipants, List<Map<String, Object>>> {

  @Override
  public void initialize(ValidParticipants parameters) {}

  @Override
  public boolean isValid(List<Map<String, Object>> participants, ConstraintValidatorContext context) {
    if (participants == null) {
      return true;
    }

    Set<String> violationCodes = Sets.newHashSet();
    participants.stream().forEach(participant -> violationCodes.addAll(participantViolations(participant)));

    context.disableDefaultConstraintViolation();
    violationCodes.stream().forEach(
        code -> context.buildConstraintViolationWithTemplate(code).addConstraintViolation()
    );

    return violationCodes.isEmpty();
  }

  private static List<String> participantViolations(Map<String, Object> participant) {
    List<String> participantViolations = Lists.newArrayList();

    Object name = participant.get("name");
    Object share = participant.get("share");

    if (nameIsEmpty(name)) {
      participantViolations.add(PARTICIPANT_NAME_REQUIRED_CODE);
    }

    if (share == null) {
      participantViolations.add(PARTICIPANT_SHARE_REQUIRED_CODE);
    } else {
      assert share.getClass() == Integer.class;

      if ((int) share < MINIMUM_PARTICIPANT_SHARE) {
        participantViolations.add(INVALID_SHARE_CODE);
      }
    }

    return participantViolations;
  }

  private static boolean nameIsEmpty(Object name) {
    if (name == null) {
      return true;
    }

    assert name.getClass() == String.class;

    return ((String) name).trim().isEmpty();
  }

  private static final int MINIMUM_PARTICIPANT_SHARE = 1;
  private static final String PARTICIPANT_NAME_REQUIRED_CODE = "PARTICIPANT_NAME_REQUIRED";
  private static final String PARTICIPANT_SHARE_REQUIRED_CODE = "PARTICIPANT_SHARE_REQUIRED";
  private static final String INVALID_SHARE_CODE = "INVALID_SHARE";
}

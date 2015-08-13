package agoodfriendalwayspayshisdebts.web.action;

import net.codestory.http.errors.NotFoundException;

import java.util.UUID;

public class UuidParser {

  private UuidParser() {}

  public static UUID createOrThrowNotFoundExceptionOnInvalidUuid(String stringifiedUuid) {
    try {
      return UUID.fromString(stringifiedUuid);
    } catch (IllegalArgumentException e) {
      throw new NotFoundException();
    }
  }
}

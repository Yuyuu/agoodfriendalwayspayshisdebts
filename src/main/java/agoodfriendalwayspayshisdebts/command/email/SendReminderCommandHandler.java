package agoodfriendalwayspayshisdebts.command.email;

import agoodfriendalwayspayshisdebts.infrastructure.services.RecipientReport;
import agoodfriendalwayspayshisdebts.infrastructure.services.Reminder;
import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.search.event.result.model.CalculationResult;
import agoodfriendalwayspayshisdebts.search.event.result.model.ParticipantResult;
import com.vter.command.CommandHandler;
import com.vter.infrastructure.services.EmailSender;
import org.jongo.Jongo;

import javax.inject.Inject;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

public class SendReminderCommandHandler implements CommandHandler<SendReminderCommand, Iterable<RecipientReport>> {

  @Inject
  public SendReminderCommandHandler(Jongo jongo, EmailSender emailSender) {
    this.jongo = jongo;
    this.emailSender = emailSender;
  }

  @Override
  public Iterable<RecipientReport> execute(SendReminderCommand command) {
    final Event event = RepositoryLocator.events().get(command.eventId);
    final CalculationResult calculationResult = jongo.getCollection("eventresult_view")
        .findOne("{_id:#}", command.eventId)
        .as(CalculationResult.class);
    assert calculationResult != null;

    final Locale reminderLocale = locale(command.locale);
    final List<UUID> recipientsIds = toUuids(command.recipientsUuids);

    return event.participants().stream()
        .filter(participant -> recipientsIds.contains(participant.id()))
        .map(participant -> {
          final ParticipantResult participantResult = calculationResult.participantsResults.get(participant.id());
          final Reminder reminder = reminder(participantResult, reminderLocale)
              .withEventModel(event.name(), command.eventLink)
              .to(participant.email());

          try {
            emailSender.send(reminder);
            return RecipientReport.success(participant.name());
          } catch (Throwable e) {
            return RecipientReport.error(participant.name(), e);
          }
        })
        .collect(Collectors.toList());
  }

  private static List<UUID> toUuids(List<String> stringifiedUuids) {
    return stringifiedUuids.stream().map(UUID::fromString).collect(Collectors.toList());
  }

  private static Locale locale(String language) {
    if (language == null) {
      return null;
    }
    switch (language) {
      case "fr":
        return Locale.FRANCE;
      case "en":
        return Locale.UK;
      default:
        return null;
    }
  }

  private static Reminder reminder(ParticipantResult participantResult, Locale locale) {
    return (locale == null) ?
        Reminder.withDefaultLocale(participantResult) :
        Reminder.forLocale(locale, participantResult);
  }

  private final Jongo jongo;
  private final EmailSender emailSender;
}

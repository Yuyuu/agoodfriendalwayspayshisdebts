package agoodfriendalwayspayshisdebts.command.event;

import agoodfriendalwayspayshisdebts.infrastructure.services.RecipientReport;
import agoodfriendalwayspayshisdebts.infrastructure.services.Reminder;
import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.model.activity.Operation;
import agoodfriendalwayspayshisdebts.model.activity.OperationType;
import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.search.event.results.model.EventResults;
import agoodfriendalwayspayshisdebts.search.event.results.model.ParticipantResults;
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
    final EventResults eventResults = jongo.getCollection("eventresults_view")
        .findOne("{_id:#}", command.eventId)
        .as(EventResults.class);
    assert eventResults != null;

    final Locale reminderLocale = locale(command.locale);
    final List<UUID> recipientsIds = toUuids(command.recipientsUuids);

    final List<RecipientReport> reports = event.participants().stream()
        .filter(participant -> recipientsIds.contains(participant.id()))
        .map(participant -> {
          final ParticipantResults participantResults = eventResults.participantsResults.get(participant.id());
          final Reminder reminder = reminder(participantResults, reminderLocale)
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

    final String recipients = reports.stream().map(RecipientReport::recipientName).collect(Collectors.joining(", "));
    event.addOperation(new Operation(OperationType.NEW_REMINDER, recipients, event.getId()));

    return reports;
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

  private static Reminder reminder(ParticipantResults participantResults, Locale locale) {
    return (locale == null) ?
        Reminder.withDefaultLocale(participantResults) :
        Reminder.forLocale(locale, participantResults);
  }

  private final Jongo jongo;
  private final EmailSender emailSender;
}

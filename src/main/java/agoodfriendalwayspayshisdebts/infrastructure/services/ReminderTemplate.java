package agoodfriendalwayspayshisdebts.infrastructure.services;

import com.github.jknack.handlebars.TypeSafeTemplate;

public interface ReminderTemplate extends TypeSafeTemplate<ReminderTemplateModel> {

  ReminderTemplate setEventName(String eventName);

  ReminderTemplate setEventLink(String eventLink);
}

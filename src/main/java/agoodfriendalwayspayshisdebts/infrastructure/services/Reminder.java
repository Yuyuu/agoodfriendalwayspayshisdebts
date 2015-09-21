package agoodfriendalwayspayshisdebts.infrastructure.services;

import agoodfriendalwayspayshisdebts.search.event.result.model.ParticipantResult;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.cache.GuavaTemplateCache;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.vter.infrastructure.services.Email;

import java.io.IOException;
import java.util.Locale;

public class Reminder implements Email {

  private Reminder(ParticipantResult participantResult, Locale locale) {
    this.locale = locale;
    try {
      template = handlebars.compile(TEMPLATE_NAME + "_" + locale.getLanguage()).as(ReminderTemplate.class);
      templateModel = new ReminderTemplateModel(participantResult, locale);
    } catch (IOException e) {
      Throwables.propagate(e);
    }
  }

  public static Reminder forLocale(Locale locale, ParticipantResult participantResult) {
    return new Reminder(participantResult, locale);
  }

  public static Reminder withDefaultLocale(ParticipantResult participantResult) {
    return new Reminder(participantResult, Locale.UK);
  }

  @Override
  public String to() {
    return to;
  }

  @Override
  public String subject() {
    return subject;
  }

  @Override
  public String content() {
    try {
      return template.apply(templateModel);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  public Locale locale() {
    return locale;
  }

  public Reminder to(String email) {
    to = email;
    return this;
  }

  public Reminder withEventModel(String eventName, String eventLink) {
    subject = eventName;
    template.setEventName(eventName).setEventLink(eventLink);
    return this;
  }

  private String to;
  private String subject;
  private ReminderTemplateModel templateModel;
  private ReminderTemplate template;
  private final Locale locale;

  private final static String TEMPLATE_NAME = "debt_reminder";
  private final static Handlebars handlebars = new Handlebars(new ClassPathTemplateLoader("/templates", ".html"))
      .with(new GuavaTemplateCache(CacheBuilder.newBuilder().build()));
}

package agoodfriendalwayspayshisdebts.model;

import agoodfriendalwayspayshisdebts.model.event.EventRepository;

import javax.inject.Inject;

public abstract class RepositoryLocator {

  public static void initialize(RepositoryLocator instance) {
    RepositoryLocator.instance = instance;
  }

  public static EventRepository events() {
    return instance.getEvents();
  }

  protected abstract EventRepository getEvents();

  @Inject
  private static RepositoryLocator instance;
}

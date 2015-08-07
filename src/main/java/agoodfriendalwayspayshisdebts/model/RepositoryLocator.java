package agoodfriendalwayspayshisdebts.model;

import agoodfriendalwayspayshisdebts.model.event.EventRepository;

public abstract class RepositoryLocator {

  public static void initialize(RepositoryLocator instance) {
    RepositoryLocator.instance = instance;
  }

  public static EventRepository events() {
    return instance.getEvents();
  }

  protected abstract EventRepository getEvents();

  private static RepositoryLocator instance;
}

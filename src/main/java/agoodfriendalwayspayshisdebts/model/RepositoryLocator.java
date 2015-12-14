package agoodfriendalwayspayshisdebts.model;

import agoodfriendalwayspayshisdebts.model.activity.OperationRepository;
import agoodfriendalwayspayshisdebts.model.event.EventRepository;

import javax.inject.Inject;

public abstract class RepositoryLocator {

  public static void initialize(RepositoryLocator instance) {
    RepositoryLocator.instance = instance;
  }

  public static EventRepository events() {
    return instance.getEvents();
  }

  public static OperationRepository operations() {
    return instance.getOperations();
  }

  protected abstract EventRepository getEvents();

  protected abstract OperationRepository getOperations();

  @Inject
  private static RepositoryLocator instance;
}

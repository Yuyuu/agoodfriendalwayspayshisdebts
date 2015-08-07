package agoodfriendalwayspayshisdebts.infrastructure.persistence.mongo;

import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.model.event.EventRepository;
import com.vter.infrastructure.persistence.mongo.MongoLinkContext;

import javax.inject.Inject;

public class MongoLinkRepositoryLocator extends RepositoryLocator {

  @Inject
  public MongoLinkRepositoryLocator(MongoLinkContext mongoLinkContext) {
    this.mongoLinkContext = mongoLinkContext;
  }

  @Override
  protected EventRepository getEvents() {
    return new MongoLinkEventRepository(mongoLinkContext.currentSession());
  }

  private final MongoLinkContext mongoLinkContext;
}

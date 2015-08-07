package agoodfriendalwayspayshisdebts.infrastructure.persistence.memory

import agoodfriendalwayspayshisdebts.model.RepositoryLocator
import agoodfriendalwayspayshisdebts.model.event.EventRepository

class MemoryRepositoryLocator extends RepositoryLocator {

  @Override
  protected EventRepository getEvents() {
    return memoryEventRepository
  }

  private final MemoryEventRepository memoryEventRepository = new MemoryEventRepository()
}

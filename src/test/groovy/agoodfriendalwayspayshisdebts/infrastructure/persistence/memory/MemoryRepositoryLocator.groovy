package agoodfriendalwayspayshisdebts.infrastructure.persistence.memory

import agoodfriendalwayspayshisdebts.model.RepositoryLocator
import agoodfriendalwayspayshisdebts.model.activity.OperationRepository
import agoodfriendalwayspayshisdebts.model.event.EventRepository

class MemoryRepositoryLocator extends RepositoryLocator {

  @Override
  protected EventRepository getEvents() {
    return memoryEventRepository
  }

  @Override
  protected OperationRepository getOperations() {
    return memoryOperationRepository
  }

  private final MemoryEventRepository memoryEventRepository = new MemoryEventRepository()
  private final MemoryOperationRepository memoryOperationRepository = new MemoryOperationRepository()
}

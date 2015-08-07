package agoodfriendalwayspayshisdebts.infrastructure.persistence.memory

import agoodfriendalwayspayshisdebts.model.RepositoryLocator
import org.junit.rules.ExternalResource

class WithMemoryRepository extends ExternalResource {

  @Override
  protected void before() throws Throwable {
    MemoryRepositoryLocator.initialize(new MemoryRepositoryLocator())
  }

  @Override
  protected void after() {
    RepositoryLocator.initialize(null)
  }
}

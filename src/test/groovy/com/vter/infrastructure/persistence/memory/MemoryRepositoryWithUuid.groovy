package com.vter.infrastructure.persistence.memory

import com.vter.model.AggregateWithUuid
import com.vter.model.RepositoryWithUuid

class MemoryRepositoryWithUuid<TAggregate extends AggregateWithUuid> extends MemoryRepository<UUID, TAggregate> implements RepositoryWithUuid<TAggregate> {
}

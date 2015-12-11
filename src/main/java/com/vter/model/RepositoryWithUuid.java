package com.vter.model;

import java.util.UUID;

public interface RepositoryWithUuid<TAggregate extends AggregateWithUuid> extends Repository<UUID, TAggregate> {
}

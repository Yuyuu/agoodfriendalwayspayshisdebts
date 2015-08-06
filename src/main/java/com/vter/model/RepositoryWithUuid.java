package com.vter.model;

import java.util.UUID;

public interface RepositoryWithUuid<TEntity extends EntityWithUuid> extends Repository<UUID, TEntity> {
}

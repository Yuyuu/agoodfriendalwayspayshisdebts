package com.vter.infrastructure.persistence.memory;

import com.vter.model.Entity;

import java.util.UUID;

public class MemoryRepositoryWithUuid<TEntity extends Entity<UUID>> extends MemoryRepository<UUID, TEntity> {
}

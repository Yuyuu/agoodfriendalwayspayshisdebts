package com.vter.infrastructure.persistence.mongo;

import com.vter.command.CommandSynchronization;
import com.vter.infrastructure.bus.Message;
import org.mongolink.MongoSession;
import org.mongolink.MongoSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class MongoLinkContext implements CommandSynchronization {

  @Inject
  public MongoLinkContext(MongoSessionManager sessionManager) {
    sessions = ThreadLocal.withInitial(sessionManager::createSession);
  }

  @Override
  public void beforeExecution(Message<?> message) {
    LOGGER.debug("Starting a new session");
    sessions.get().start();
  }

  @Override
  public void afterExecution() {
    LOGGER.debug("Synchronization with MongoDB");
    sessions.get().flush();
  }

  @Override
  public void onError() {
    LOGGER.debug("Cleaning up on session error");
    sessions.get().clear();
  }

  @Override
  public void ultimately() {
    LOGGER.debug("Stopping the session");
    sessions.get().stop();
    sessions.remove();
  }

  public MongoSession currentSession() {
    return sessions.get();
  }

  private final ThreadLocal<MongoSession> sessions;
  private static final Logger LOGGER = LoggerFactory.getLogger(MongoLinkContext.class);
}

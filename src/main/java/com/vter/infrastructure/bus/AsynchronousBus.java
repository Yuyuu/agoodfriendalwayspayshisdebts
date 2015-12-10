package com.vter.infrastructure.bus;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.*;
import com.vter.command.ValidationException;
import com.vter.model.BusinessError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AsynchronousBus implements Bus {

  public AsynchronousBus(Set<? extends BusSynchronization> synchronizations, Set<? extends MessageHandler> handlers) {
    handlers.forEach(handler -> this.handlers.put(handler.commandType(), handler));
    this.synchronizations.addAll(synchronizations);
  }

  @Override
  public <TResponse> ExecutionResult<TResponse> sendAndWaitResponse(Message<TResponse> message) {
    return Futures.getUnchecked(submitWithExecutor(message, directExecutorService));
  }

  @Override
  public <TResponse> ListenableFuture<ExecutionResult<TResponse>> send(Message<TResponse> message) {
    return submitWithExecutor(message, executorService);
  }

  private <TResponse> ListenableFuture<ExecutionResult<TResponse>> submitWithExecutor(
      Message<TResponse> message, ListeningExecutorService executorService) {
    final Collection<MessageHandler> messageHandlers = handlers.get(message.getClass());
    if (messageHandlers.isEmpty()) {
      LOGGER.warn("Impossible to find a handler for {}", message.getClass());
      return Futures.immediateFuture(ExecutionResult.error(new BusError("Impossible to find a handler")));
    }

    LOGGER.debug("New message: {}", message.getClass().getSimpleName());
    final List<ListenableFuture<ExecutionResult<TResponse>>> futures = Lists.newArrayList();
    messageHandlers.forEach(handler -> futures.add(executorService.submit(execute(message, handler))));
    return futures.get(0);
  }

  private <TResponse> Callable<ExecutionResult<TResponse>> execute(
      Message<TResponse> message, MessageHandler<Message<TResponse>, TResponse> messageHandler) {
    return () -> {
      try {
        LOGGER.debug("Executing handler {}", messageHandler.getClass());
        synchronizations.forEach(synchronization -> synchronization.beforeExecution(message));
        final TResponse response = messageHandler.execute(message);
        synchronizations.forEach(BusSynchronization::afterExecution);
        return ExecutionResult.success(response);
      } catch (Throwable error) {
        synchronizations.forEach(BusSynchronization::onError);
        logErrorIfUnresolved(error);
        return ExecutionResult.error(error);
      } finally {
        synchronizations.forEach(synchronization -> synchronization.ultimately(message));
      }
    };
  }

  public void setSyncExecutor(ListeningExecutorService executor) {
    directExecutorService = MoreExecutors.listeningDecorator(executor);
  }

  public void setAsyncExecutor(ExecutorService executor) {
    executorService = MoreExecutors.listeningDecorator(executor);
  }

  protected void addSynchronization(BusSynchronization synchronization) {
    synchronizations.add(synchronization);
  }

  private static void logErrorIfUnresolved(Throwable error) {
    if (!ValidationException.class.isAssignableFrom(error.getClass()) &&
        !BusinessError.class.isAssignableFrom(error.getClass())) {
      LOGGER.error("Error on message", error);
    }
  }

  private final List<BusSynchronization> synchronizations = Lists.newArrayList();
  private final Multimap<Class<?>, MessageHandler> handlers = ArrayListMultimap.create();
  private ListeningExecutorService directExecutorService = MoreExecutors.newDirectExecutorService();
  private ListeningExecutorService executorService = MoreExecutors.listeningDecorator(
      Executors.newCachedThreadPool(
          new ThreadFactoryBuilder().setNameFormat(getClass().getSimpleName() + "-%d").build()
      )
  );
  protected final static Logger LOGGER = LoggerFactory.getLogger(AsynchronousBus.class);
}

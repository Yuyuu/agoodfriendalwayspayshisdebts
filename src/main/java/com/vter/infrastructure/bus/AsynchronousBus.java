package com.vter.infrastructure.bus;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.*;
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
    return Futures.getUnchecked(Futures.successfulAsList(futures(message))).get(0);
  }

  @Override
  public <TResponse> ListenableFuture<ExecutionResult<TResponse>> send(Message<TResponse> message) {
    return futures(message).get(0);
  }

  private <TResponse> List<ListenableFuture<ExecutionResult<TResponse>>> futures(Message<TResponse> message) {
    final Collection<MessageHandler> handlers = this.handlers.get(message.getClass());
    if (handlers.isEmpty()) {
      LOGGER.warn("Impossible to find a handler for {}", message.getClass());
      return Lists.newArrayList(
          Futures.immediateFuture(ExecutionResult.error(new BusError("Impossible to find a handler")))
      );
    }

    LOGGER.debug("Executing handlers for {}", message.getClass());
    final List<ListenableFuture<ExecutionResult<TResponse>>> futures = Lists.newArrayList();
    handlers.forEach(handler -> futures.add(executorService.submit(execute(message, handler))));
    return futures;
  }

  private <TResponse> Callable<ExecutionResult<TResponse>> execute(
      Message<TResponse> message, MessageHandler<Message<TResponse>, TResponse> messageHandler) {
    return () -> {
      try {
        synchronizations.forEach(synchronization -> synchronization.beforeExecution(message));
        final TResponse response = messageHandler.execute(message);
        synchronizations.forEach(BusSynchronization::afterExecution);
        return ExecutionResult.success(response);
      } catch (Throwable error) {
        synchronizations.forEach(BusSynchronization::onError);
        LOGGER.error("Error on message", error);
        return ExecutionResult.error(error);
      } finally {
        synchronizations.forEach(BusSynchronization::ultimately);
      }
    };
  }

  public void setExecutor(ExecutorService executor) {
    executorService = MoreExecutors.listeningDecorator(executor);
  }

  private final List<BusSynchronization> synchronizations = Lists.newArrayList();
  private final Multimap<Class<?>, MessageHandler> handlers = ArrayListMultimap.create();
  private ListeningExecutorService executorService = MoreExecutors.listeningDecorator(
      Executors.newCachedThreadPool(
          new ThreadFactoryBuilder().setNameFormat(getClass().getSimpleName() + "-%d").build()
      )
  );
  protected final static Logger LOGGER = LoggerFactory.getLogger(AsynchronousBus.class);
}

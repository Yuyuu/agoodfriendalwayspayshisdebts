package com.vter.infrastructure.bus;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public abstract class AsynchronousBus implements Bus {

  public AsynchronousBus(Set<? extends BusSynchronization> synchronizations, Set<? extends MessageHandler> handlers) {
    handlers.forEach(handler -> this.handlers.put(handler.commandType(), handler));
    this.synchronizations.addAll(synchronizations);
  }

  @Override
  public <TResponse> ExecutionResult<TResponse> sendAndWaitResponse(Message<TResponse> message) {
    return Futures.getUnchecked(send(message));
  }

  @Override
  public <TResponse> CompletableFuture<ExecutionResult<TResponse>> send(Message<TResponse> message) {
    final Collection<MessageHandler> handlers = this.handlers.get(message.getClass());
    if (handlers.size() == 0) {
      LOGGER.warn("Impossible to find a handler for {}", message.getClass());
      return CompletableFuture.completedFuture(ExecutionResult.error(new BusError("Impossible to find a handler")));
    }

    LOGGER.debug("Executing handler for {}", message.getClass());
    List<CompletableFuture<ExecutionResult<TResponse>>> futures = Lists.newArrayList();
    handlers.forEach(handler -> futures.add(CompletableFuture.supplyAsync(execute(message, handler), executorService)));
    return futures.get(0);
  }

  private <TResponse> Supplier<ExecutionResult<TResponse>> execute(Message<TResponse> message, MessageHandler<Message<TResponse>, TResponse> messageHandler) {
    return () -> {
      try {
        synchronizations.forEach(synchronization -> synchronization.beforeExecution(message));
        final TResponse response = messageHandler.execute(message);
        synchronizations.forEach(BusSynchronization::afterExecution);
        return ExecutionResult.success(response);
      } catch (Throwable e) {
        synchronizations.forEach(BusSynchronization::onError);
        LOGGER.error("Error on message", e);
        return ExecutionResult.error(e);
      } finally {
        synchronizations.forEach(BusSynchronization::ultimately);
      }
    };
  }

  public void setExecutor(ExecutorService executor) {
    executorService = executor;
  }

  private final List<BusSynchronization> synchronizations = Lists.newArrayList();
  private final Multimap<Class<?>, MessageHandler> handlers = ArrayListMultimap.create();
  private ExecutorService executorService = Executors.newCachedThreadPool(
      new ThreadFactoryBuilder().setNameFormat(getClass().getSimpleName() + "-%d").build()
  );
  protected final static Logger LOGGER = LoggerFactory.getLogger(AsynchronousBus.class);
}

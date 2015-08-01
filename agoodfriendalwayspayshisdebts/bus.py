import abc
import logging
import Queue
import concurrent.futures as futures
import threading

import locator


logger = logging.getLogger(__name__)

MAX_WORKER_THREADS_PER_BUS = 30
GLOBAL_SYNCHRONIZATION = 'ALL'


class BusSynchronization:
    __metaclass__ = abc.ABCMeta

    def __init__(self, message_type):
        self.message_type = message_type

    def before_execution(self, message):
        pass

    def on_error(self):
        pass

    def after_execution(self):
        pass

    def ultimately(self):
        pass


class ExecutionResult:
    def __init__(self, response=None, error=None, success=False):
        self.response = response
        self.error = error
        self.success = success

    @staticmethod
    def success(response):
        return ExecutionResult(response=response, success=True)

    @staticmethod
    def error(error):
        return ExecutionResult(error=error)

    def is_success(self):
        return self.success

    def is_error(self):
        return not self.success


class Bus:
    __metaclass__ = abc.ABCMeta

    def __init__(self, synchronizations, handlers):
        self.synchronizations = {}
        self.__init(self.synchronizations, synchronizations)

        self.handlers = {}
        self.__init(self.handlers, handlers)

        self.executor_service = futures.ThreadPoolExecutor(MAX_WORKER_THREADS_PER_BUS)

    def send_and_wait_response(self, command):
        return self.send(command).result()

    def send(self, command):
        handlers = self.handlers.get(command.__class__, [])
        if len(handlers) == 0:
            logger.warning('Impossible to find a handler for %s' % command.__class__)
            return CompletedFuture(ExecutionResult.error(BusError('Impossible to find a handler')))
        logger.debug('Executing handler for %s' % command.__class__)
        results = []
        for handler in handlers:
            results.append(self.executor_service.submit(self.__execute, command, handler))
        return results[0]

    def __execute(self, command, handler):
        synchronizations = self.synchronizations.get(command.__class__, []) +\
            self.synchronizations.get(GLOBAL_SYNCHRONIZATION, [])
        try:
            for synchronization in synchronizations:
                synchronization.before_execution(command)
            response = handler.execute(command)
            for synchronization in synchronizations:
                synchronization.after_execution()
            return ExecutionResult.success(response=response)
        except Exception as e:
            for synchronization in synchronizations:
                synchronization.on_error()
            logger.exception(e)
            return ExecutionResult.error(e)
        finally:
            for synchronization in synchronizations:
                synchronization.ultimately()

    @staticmethod
    def __init(class_property, init_values):
        for value in init_values:
            if class_property.get(value.message_type, None) is None:
                class_property[value.message_type] = []
            class_property[value.message_type].append(value)


class CommandBus(Bus):
    def __init__(self, synchronizations, handlers):
        Bus.__init__(self, synchronizations, handlers)


class SearchBus(Bus):
    def __init__(self, handlers):
        Bus.__init__(self, [], handlers)


class EventBus:
    __metaclass__ = abc.ABCMeta

    @abc.abstractmethod
    def publish(self, event):
        pass

    @staticmethod
    def get_instance():
        return locator.EventBusLocator.get_instance()


class InitializedThreadLocal(threading.local):
    events = Queue.Queue()


class AsynchronousEventBus(Bus, EventBus, BusSynchronization):
    thread_local = InitializedThreadLocal()

    def __init__(self, synchronizations, handlers):
        Bus.__init__(self, synchronizations, handlers)
        BusSynchronization.__init__(self, GLOBAL_SYNCHRONIZATION)

    def after_execution(self):
        logger.debug('Propagating events')
        while not self.thread_local.events.empty():
            event = self.thread_local.events.get()
            if event.is_synchronous:
                self.send_and_wait_response(event)
            else:
                self.send(event)

    def on_error(self):
        self.thread_local.events.queue.clear()

    def publish(self, event):
        self.thread_local.events.put(event)


class BusError(RuntimeError):
    def __init__(self, message):
        self.message = message


class CompletedFuture:
    def __init__(self, future_result):
        self.future_result = future_result

    def result(self):
        return self.future_result

import abc
import logging


logger = logging.getLogger(__name__)


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

    def send_and_wait_response(self, command):
        handlers = self.handlers.get(command.__class__, [])
        if len(handlers) == 0:
            logger.warning('Impossible to find a handler for %s' % command.__class__)
            return ExecutionResult.error(BusError('Impossible to find a handler'))
        logger.debug('Executing handler for %s' % command.__class__)
        results = []
        for handler in handlers:
            results.append(self.__execute(command, handler))
        return results[0]

    def __execute(self, command, handler):
        synchronizations = self.synchronizations.get(command.__class__, [])
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


class BusError(RuntimeError):
    def __init__(self, message):
        self.message = message

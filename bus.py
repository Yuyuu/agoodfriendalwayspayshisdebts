import logging


logger = logging.getLogger(__name__)

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
    def __init__(self, handlers):
        self.handlers = {}
        for handler in handlers:
            if self.handlers.get(handler.command_type, None) is None:
                self.handlers[handler.command_type] = []
            self.handlers[handler.command_type].append(handler)

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

    @staticmethod
    def __execute(command, handler):
        try:
            response = handler.execute(command)
            return ExecutionResult.success(response=response)
        except Exception as e:
            logger.exception(e)
            return ExecutionResult.error(e)


class CommandBus(Bus):
    def __init__(self, handlers):
        Bus.__init__(self, handlers)


class BusError(RuntimeError):
    def __init__(self, message):
        self.message = message

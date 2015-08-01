import unittest
import logging

from agoodfriendalwayspayshisdebts import bus, command_handlers


class BusTestCase(unittest.TestCase):
    def setUp(self):
        logging.disable(logging.CRITICAL)
        
    def test_can_execute_a_command(self):
        handler = FakeCommandHandler()
        b = bus_with_handler(handler)
        command = FakeCommand()

        b.send_and_wait_response(command)

        self.assertEqual(handler.command_received, command)

    def test_the_commands_are_encapsulated_into_the_synchronizations(self):
        synchronization = FakeCommandSynchronization()
        b = bus_with_synchronization(synchronization)
        command = FakeCommand()

        b.send_and_wait_response(command)

        self.assertTrue(synchronization.before_execution_called)
        self.assertTrue(synchronization.after_execution_called)
        self.assertTrue(synchronization.ultimately_called)

    def test_the_synchronizations_are_still_called_on_error(self):
        handler = FakeCommandHandler()
        handler.raise_exception = True
        synchronization = FakeCommandSynchronization()
        b = bus_with(synchronization, handler)

        b.send_and_wait_response(FakeCommand())

        self.assertTrue(synchronization.on_error_called)
        self.assertTrue(synchronization.ultimately_called)

    def test_returns_the_result_of_a_command(self):
        b = bus_with_handler(FakeCommandHandler())

        result = b.send_and_wait_response(FakeCommand())

        self.assertIsNotNone(result)

    def test_returns_a_result_on_error(self):
        handler = FakeCommandHandler()
        handler.raise_exception = True
        b = bus_with_handler(handler)

        result = b.send_and_wait_response(FakeCommand())

        self.assertIsNotNone(result)
        self.assertIsNotNone(result.error)
        self.assertIsInstance(result.error, RuntimeError)
        self.assertEqual(result.error.message, 'This is an error')

    def test_returns_an_error_if_there_is_no_handler_for_a_command(self):
        b = empty_bus()

        result = b.send_and_wait_response(FakeCommand())

        self.assertIsNotNone(result)
        self.assertIsNotNone(result.error)
        self.assertIsInstance(result.error, bus.BusError)


def bus_with(synchronization, handler):
    return bus.Bus([synchronization], [handler])


def bus_with_handler(handler):
    return bus.Bus([], [handler])


def bus_with_synchronization(synchronization):
    return bus_with(synchronization, FakeCommandHandler())


def empty_bus():
    return bus.Bus([], [])


class FakeCommand:
    def __init__(self):
        pass


class FakeCommandSynchronization(bus.BusSynchronization):
    def __init__(self):
        super(FakeCommandSynchronization, self).__init__(FakeCommand)
        self.before_execution_called = False
        self.after_execution_called = False
        self.on_error_called = False
        self.ultimately_called = False

    def before_execution(self, message):
        self.before_execution_called = True

    def after_execution(self):
        self.after_execution_called = True

    def on_error(self):
        self.on_error_called = True

    def ultimately(self):
        self.ultimately_called = True


class FakeCommandHandler(command_handlers.Handler):
    def __init__(self):
        command_handlers.Handler.__init__(self, FakeCommand)
        self.raise_exception = False
        self.command_received = None

    def execute(self, command):
        self.command_received = command
        if self.raise_exception:
            raise RuntimeError('This is an error')
        return "This is a yoke"

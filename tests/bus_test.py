import unittest
import logging

import bus
import handlers


def bus_with(handler):
    return bus.Bus([handler])

def empty_bus():
    return bus.Bus([])


class FakeCommand:
    def __init__(self):
        pass


class FakeCommandHandler(handlers.CommandHandler):
    def __init__(self):
        handlers.CommandHandler.__init__(self, FakeCommand)
        self.raise_exception = False
        self.command_received = None

    def execute(self, command):
        self.command_received = command
        if self.raise_exception:
            raise RuntimeError('This is an error')
        return "This is a yoke"


class BusTestCase(unittest.TestCase):
    def setUp(self):
        logging.disable(logging.CRITICAL)
        
    def test_can_execute_a_command(self):
        handler = FakeCommandHandler()
        b = bus_with(handler)
        command = FakeCommand()

        b.send_and_wait_response(command)

        self.assertEqual(handler.command_received, command)

    def test_returns_the_result_of_a_command(self):
        b = bus_with(FakeCommandHandler())

        result = b.send_and_wait_response(FakeCommand())

        self.assertIsNotNone(result)

    def test_returns_a_result_on_error(self):
        handler = FakeCommandHandler()
        handler.raise_exception = True
        b = bus_with(handler)

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

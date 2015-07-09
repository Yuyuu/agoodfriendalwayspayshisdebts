import unittest

import commands


class CommandsTestCase(unittest.TestCase):
    def test_create_event_command(self):
        name = 'Cool event'
        participants = ['Kim', 'Joe']

        create_event_command = commands.CreateEventCommand(name, participants)

        self.assertEqual('Cool event', create_event_command.name)
        self.assertEqual(['Kim', 'Joe'], create_event_command.participants)
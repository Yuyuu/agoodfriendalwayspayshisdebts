import unittest

import validators


class ValidatorTestCase(unittest.TestCase):
    def test_create_command_without_a_name_is_invalid(self):
        validator = validators.CreateEventCommandValidator({'participants': ['Bob']})
        with self.assertRaises(validators.ValidationException) as cm:
            validator.validate()

        exception = cm.exception
        self.assertEqual(exception.messages[0], 'An event requires a name')

    def test_create_command_without_participants_is_invalid(self):
        validator = validators.CreateEventCommandValidator({'name': 'Cool event'})
        with self.assertRaises(validators.ValidationException) as cm:
            validator.validate()

        exception = cm.exception
        self.assertEqual(exception.messages[0], 'At least one participant is required for an event')

    def test_create_command_requires_at_least_one_participant(self):
        validator = validators.CreateEventCommandValidator({'name': 'Cool event', 'participants': []})

        self.assertRaises(validators.ValidationException, validator.validate)

    def test_can_collect_all_errors(self):
        validator = validators.CreateEventCommandValidator({})
        with self.assertRaises(validators.ValidationException) as cm:
            validator.validate()

        exception = cm.exception
        self.assertEqual(len(exception.messages), 2)
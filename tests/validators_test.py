import unittest

import validators


class ValidatorTestCase(unittest.TestCase):
    def test_create_command_without_a_name_is_invalid(self):
        validator = validators.CreateEventCommandValidator({'participants': ['Bob']})
        with self.assertRaises(validators.ValidationException) as cm:
            validator.validate()

        exception = cm.exception
        self.assertEqual(exception.messages[0], 'EVENT_NAME_REQUIRED')

    def test_create_command_without_participants_is_invalid(self):
        validator = validators.CreateEventCommandValidator({'name': 'Cool event'})
        with self.assertRaises(validators.ValidationException) as cm:
            validator.validate()

        exception = cm.exception
        self.assertEqual(exception.messages[0], 'PARTICIPANTS_REQUIRED')

    def test_create_command_requires_at_least_one_participant(self):
        validator = validators.CreateEventCommandValidator({'name': 'Cool event', 'participants': []})

        self.assertRaises(validators.ValidationException, validator.validate)

    def test_can_collect_all_errors(self):
        validator = validators.CreateEventCommandValidator({})
        with self.assertRaises(validators.ValidationException) as cm:
            validator.validate()

        exception = cm.exception
        self.assertEqual(len(exception.messages), 2)
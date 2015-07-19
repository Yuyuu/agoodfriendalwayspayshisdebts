import unittest

import validators


class CreateEventCommandValidatorTestCase(unittest.TestCase):
    def test_create_event_command_without_a_name_is_invalid(self):
        validator = validators.CreateEventCommandValidator({'participants': [{'name': 'Bob', 'share': 1}]})
        with self.assertRaises(validators.ValidationException) as cm:
            validator.validate()

        exception = cm.exception
        self.assertEqual(exception.messages[0], 'EVENT_NAME_REQUIRED')

    def test_create_event_command_without_participants_is_invalid(self):
        validator = validators.CreateEventCommandValidator({'name': 'Cool event'})
        with self.assertRaises(validators.ValidationException) as cm:
            validator.validate()

        exception = cm.exception
        self.assertEqual(exception.messages[0], 'PARTICIPANTS_REQUIRED')

    def test_create_event_command_requires_at_least_one_participant(self):
        validator = validators.CreateEventCommandValidator({'name': 'Cool event', 'participants': []})

        self.assertRaises(validators.ValidationException, validator.validate)

    def test_all_participants_require_a_name(self):
        validator = validators.CreateEventCommandValidator({'name': 'Cool event', 'participants': [
            {'name': 'Bob', 'share': 1},
            {'name': '', 'share': 1}
        ]})

        self.assertRaises(validators.ValidationException, validator.validate)

    def test_all_participants_require_a_share(self):
        validator = validators.CreateEventCommandValidator({'name': 'Cool event', 'participants': [
            {'name': 'Bob', 'share': 1},
            {'name': 'Lea', 'email': 'lea@email.com'}
        ]})

        self.assertRaises(validators.ValidationException, validator.validate)

    def test_an_error_is_added_only_once(self):
        validator = validators.CreateEventCommandValidator({'name': 'Cool event', 'participants': [
            {'share': -1},
            {'name': ''},
            {}
        ]})

        with self.assertRaises(validators.ValidationException) as cm:
            validator.validate()

        exception = cm.exception
        self.assertEqual(len(exception.messages), 3)

    def test_can_collect_all_errors(self):
        validator = validators.CreateEventCommandValidator({})
        with self.assertRaises(validators.ValidationException) as cm:
            validator.validate()

        exception = cm.exception
        self.assertEqual(len(exception.messages), 2)

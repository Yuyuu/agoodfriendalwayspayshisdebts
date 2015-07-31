import unittest

from agoodfriendalwayspayshisdebts import validators, commands, errors


class CreateEventCommandValidatorTestCase(unittest.TestCase):
    def test_create_event_command_without_a_name_is_invalid(self):
        command = commands.CreateEventCommand(None, [{'name': 'Bob', 'share': 1}])
        validator = validators.CreateEventCommandValidator(None)
        with self.assertRaises(errors.ValidationError) as cm:
            validator.before_execution(command)

        exception = cm.exception
        self.assertEqual(exception.messages[0], 'EVENT_NAME_REQUIRED')

    def test_create_event_command_with_an_empty_name_is_invalid(self):
        command = commands.CreateEventCommand('', [{'name': 'Bob', 'share': 1}])
        validator = validators.CreateEventCommandValidator(None)
        with self.assertRaises(errors.ValidationError) as cm:
            validator.before_execution(command)

        exception = cm.exception
        self.assertEqual(exception.messages[0], 'EVENT_NAME_REQUIRED')

    def test_create_event_command_without_participants_is_invalid(self):
        command = commands.CreateEventCommand('Cool event', None)
        validator = validators.CreateEventCommandValidator(None)
        with self.assertRaises(errors.ValidationError) as cm:
            validator.before_execution(command)

        exception = cm.exception
        self.assertEqual(exception.messages[0], 'PARTICIPANTS_REQUIRED')

    def test_create_event_command_requires_at_least_one_participant(self):
        command = commands.CreateEventCommand('Cool event', [])
        validator = validators.CreateEventCommandValidator(None)

        self.assertRaises(errors.ValidationError, validator.before_execution, command)

    def test_all_participants_require_a_name(self):
        command = commands.CreateEventCommand('Cool event', [
            {'name': 'Bob', 'share': 1},
            {'name': '', 'share': 1}
        ])
        validator = validators.CreateEventCommandValidator(None)

        self.assertRaises(errors.ValidationError, validator.before_execution, command)

    def test_all_participants_require_a_share(self):
        command = commands.CreateEventCommand('Cool event', [
            {'name': 'Bob', 'share': 1},
            {'name': 'Lea', 'email': 'lea@email.com'}
        ])
        validator = validators.CreateEventCommandValidator(None)

        self.assertRaises(errors.ValidationError, validator.before_execution, command)

    def test_an_error_is_added_only_once(self):
        command = commands.CreateEventCommand('Cool event', [
            {'share': -1},
            {'name': ''},
            {}
        ])
        validator = validators.CreateEventCommandValidator(None)

        with self.assertRaises(errors.ValidationError) as cm:
            validator.before_execution(command)

        exception = cm.exception
        self.assertEqual(len(exception.messages), 3)

    def test_can_collect_all_errors(self):
        command = commands.CreateEventCommand(None, None)
        validator = validators.CreateEventCommandValidator(None)
        with self.assertRaises(errors.ValidationError) as cm:
            validator.before_execution(command)

        exception = cm.exception
        self.assertEqual(len(exception.messages), 2)


class AddPurchaseCommandValidatorTestCase(unittest.TestCase):
    def test_the_command_requires_a_purchaser(self):
        command = commands.AddPurchaseCommand(None, None, 'Gas', 10)
        validator = validators.AddPurchaseCommandValidator(None)
        with self.assertRaises(errors.ValidationError) as cm:
            validator.before_execution(command)

        exception = cm.exception
        self.assertEqual(exception.messages[0], 'PURCHASE_PURCHASER_REQUIRED')

    def test_the_command_requires_a_label(self):
        command = commands.AddPurchaseCommand(None, '123', None, 10)
        validator = validators.AddPurchaseCommandValidator(None)
        with self.assertRaises(errors.ValidationError) as cm:
            validator.before_execution(command)

        exception = cm.exception
        self.assertEqual(exception.messages[0], 'PURCHASE_LABEL_REQUIRED')

    def test_the_command_requires_an_amount(self):
        command = commands.AddPurchaseCommand(None, '123', 'Gas', None)
        validator = validators.AddPurchaseCommandValidator(None)
        with self.assertRaises(errors.ValidationError) as cm:
            validator.before_execution(command)

        exception = cm.exception
        self.assertEqual(exception.messages[0], 'PURCHASE_AMOUNT_REQUIRED')

    def test_the_amount_must_be_superior_to_0(self):
        command = commands.AddPurchaseCommand(None, '123', 'Gas', 0)
        validator = validators.AddPurchaseCommandValidator(None)
        with self.assertRaises(errors.ValidationError) as cm:
            validator.before_execution(command)

        exception = cm.exception
        self.assertEqual(exception.messages[0], 'INVALID_AMOUNT')

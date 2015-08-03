from bus import BusSynchronization
import errors
import commands


class Validator(BusSynchronization):
    def __init__(self, message_type):
        super(Validator, self).__init__(message_type)


class CreateEventCommandValidator(Validator):
    def __init__(self):
        super(CreateEventCommandValidator, self).__init__(commands.CreateEventCommand)

    def before_execution(self, command):
        self.__validate(command)

    def __validate(self, command):
        violations = []
        if not command.name:
            violations.append('EVENT_NAME_REQUIRED')
        if not command.participants or len(command.participants) < 1:
            violations.append('PARTICIPANTS_REQUIRED')
        else:
            violations.extend(self.__validate_participants(command.participants))

        if len(violations) > 0:
            raise errors.ValidationError(violations)

    @staticmethod
    def __validate_participants(participants):
        participants_violations = set([])

        for participant in participants:
            if 'name' not in participant or not participant['name']:
                participants_violations.add('PARTICIPANT_NAME_REQUIRED')
            if 'share' not in participant or not participant['share']:
                participants_violations.add('PARTICIPANT_SHARE_REQUIRED')
            else:
                if participant['share'] < 1:
                    participants_violations.add('INVALID_SHARE')
            if len(participants_violations) == 3:
                break

        return participants_violations


class AddPurchaseCommandValidator(Validator):
    def __init__(self):
        super(AddPurchaseCommandValidator, self).__init__(commands.AddPurchaseCommand)

    def before_execution(self, command):
        self.__validate(command)

    @staticmethod
    def __validate(command):
        violations = []

        if not command.purchaser_id:
            violations.append('PURCHASE_PURCHASER_REQUIRED')
        if not command.label:
            violations.append('PURCHASE_LABEL_REQUIRED')
        if command.amount is None:
            violations.append('PURCHASE_AMOUNT_REQUIRED')
        else:
            if command.amount <= 0:
                violations.append('INVALID_AMOUNT')

        if len(violations) > 0:
            raise errors.ValidationError(violations)

from bus import BusSynchronization
import errors


class Validator(BusSynchronization):
    def __init__(self, message_type):
        super(Validator, self).__init__(message_type)
        self.errors = []


class CreateEventCommandValidator(Validator):
    def before_execution(self, command):
        self.__validate(command)

    def __validate(self, command):
        if not command.name:
            self.errors.append('EVENT_NAME_REQUIRED')
        if not command.participants or len(command.participants) < 1:
            self.errors.append('PARTICIPANTS_REQUIRED')
        else:
            self.__validate_participants(command.participants)
        if len(self.errors) > 0:
            raise errors.ValidationError(self.errors)

    def __validate_participants(self, participants):
        validation_errors = set([])
        for participant in participants:
            if 'name' not in participant or not participant['name']:
                validation_errors.add('PARTICIPANT_NAME_REQUIRED')
            if 'share' not in participant or not participant['share']:
                validation_errors.add('PARTICIPANT_SHARE_REQUIRED')
            else:
                if participant['share'] < 1:
                    validation_errors.add('INVALID_SHARE')
            if len(validation_errors) == 3:
                break
        self.errors.extend(validation_errors)


class AddPurchaseCommandValidator(Validator):
    def before_execution(self, command):
        self.__validate(command)

    def __validate(self, command):
        if not command.purchaser_id:
            self.errors.append('PURCHASE_PURCHASER_REQUIRED')
        if not command.label:
            self.errors.append('PURCHASE_LABEL_REQUIRED')
        if command.amount is None:
            self.errors.append('PURCHASE_AMOUNT_REQUIRED')
        else:
            if command.amount <= 0:
                self.errors.append('INVALID_AMOUNT')
        if len(self.errors) > 0:
            raise errors.ValidationError(self.errors)

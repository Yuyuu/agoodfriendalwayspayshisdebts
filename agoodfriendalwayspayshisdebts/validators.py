import abc


class Validator:
    def __init__(self, data):
        self.data = data
        self.errors = []

    @abc.abstractmethod
    def validate(self):
        return


class CreateEventCommandValidator(Validator):
    def __validate_participants(self, participants):
        errors = set([])
        for participant in participants:
            if 'name' not in participant or not participant['name']:
                errors.add('PARTICIPANT_NAME_REQUIRED')
            if 'share' not in participant or not participant['share']:
                errors.add('PARTICIPANT_SHARE_REQUIRED')
            else:
                if participant['share'] < 1:
                    errors.add('INVALID_SHARE')
            if len(errors) == 3:
                break
        self.errors.extend(errors)

    def validate(self):
        if 'name' not in self.data or not self.data['name']:
            self.errors.append('EVENT_NAME_REQUIRED')
        if 'participants' not in self.data or len(self.data['participants']) < 1:
            self.errors.append('PARTICIPANTS_REQUIRED')
        else:
            self.__validate_participants(self.data['participants'])
        if len(self.errors) > 0:
            raise ValidationException(self.errors)


class AddPurchaseCommandValidator(Validator):
    def validate(self):
        if 'purchaserId' not in self.data or not self.data['purchaserId']:
            self.errors.append('PURCHASE_PURCHASER_REQUIRED')
        if 'label' not in self.data or not self.data['label']:
            self.errors.append('PURCHASE_LABEL_REQUIRED')
        if 'amount' not in self.data:
            self.errors.append('PURCHASE_AMOUNT_REQUIRED')
        else:
            if self.data['amount'] <= 0:
                self.errors.append('INVALID_AMOUNT')
        if len(self.errors) > 0:
            raise ValidationException(self.errors)


class ValidationException(RuntimeError):
    status_code = 400

    def __init__(self, messages):
        self.messages = messages

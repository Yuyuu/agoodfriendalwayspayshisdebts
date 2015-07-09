import abc


class Validator:
    def __init__(self, data):
        self.data = data

    @abc.abstractmethod
    def validate(self):
        return


class CreateEventCommandValidator(Validator):
    def validate(self):
        errors = []
        if 'name' not in self.data:
            errors.append('EVENT_NAME_REQUIRED')
        if 'participants' not in self.data or len(self.data['participants']) < 1:
            errors.append('PARTICIPANTS_REQUIRED')
        if len(errors) > 0:
            raise ValidationException(errors)


class ValidationException(Exception):
    status_code = 400

    def __init__(self, messages):
        self.messages = messages
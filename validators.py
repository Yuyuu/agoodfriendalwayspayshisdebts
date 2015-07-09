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
            errors.append('An event requires a name')
        if 'participants' not in self.data or len(self.data['participants']) < 1:
            errors.append('At least one participant is required for an event')
        if len(errors) > 0:
            raise ValidationException(errors)


class ValidationException(Exception):
    def __init__(self, messages):
        self.messages = messages
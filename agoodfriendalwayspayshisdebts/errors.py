class InvalidUUIDError(ValueError):
    status_code = 404


class ValidationError(RuntimeError):
    status_code = 400

    def __init__(self, messages):
        self.messages = messages


class EntityNotFoundError(RuntimeError):
    status_code = 404

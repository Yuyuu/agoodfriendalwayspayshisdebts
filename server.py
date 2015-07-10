import flask

import bus
import validators
import commands
import handlers


class Server(flask.Flask):
    def __init__(self, import_name):
        self.command_bus = None
        super(Server, self).__init__(import_name)

    def configure_commands(self):
        command_handlers = [handlers.CreateEventCommandHandler(commands.CreateEventCommand)]
        self.command_bus = bus.CommandBus(command_handlers)

app = Server(__name__)


@app.route('/')
def index():
    return flask.Response(status=200)

@app.route('/events', methods=['POST'])
def create_event():
    data = flask.request.json
    validators.CreateEventCommandValidator(data).validate()
    command = commands.CreateEventCommand(data['name'], data['participants'])
    result = app.command_bus.send_and_wait_response(command)
    if not result.is_success():
        raise result.error
    event_id = result.response
    http_response = flask.jsonify({'id': str(event_id)})
    http_response.status_code = 201
    return http_response


@app.errorhandler(validators.ValidationException)
def handle_validation_exception(exception):
    errors = []
    for message in exception.messages:
        errors.append({'message': message})
    response = flask.jsonify({'errors': errors})
    response.status_code = exception.status_code
    return response

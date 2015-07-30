import flask

import resources
import errors
import config
import server

config.configure_logging()
config.initialize_repository()

app = server.Server(__name__)

app.configure_commands()
app.configure_searches()

app.add_url_rule(
    '/',
    'index',
    resources.IndexResource().index
)

app.add_url_rule(
    '/events',
    'create_event',
    resources.EventsResource(app.command_bus).create,
    methods=['POST']
)

app.add_url_rule(
    '/events/<event_id>',
    'get_event',
    resources.EventResource(app.search_bus).represent,
    methods=['GET']
)

app.add_url_rule(
    '/events/<event_id>/purchases',
    'add_purchase',
    resources.PurchasesResource(app.command_bus).add,
    methods=['POST']
)

app.add_url_rule(
    '/events/<event_id>/results',
    'get_results',
    resources.ResultResource(app.search_bus).calculate,
    methods=['GET']
)


@app.errorhandler(errors.ValidationError)
def handle_validation_error(error):
    validation_errors = []
    for message in error.messages:
        validation_errors.append({'message': message})
    response = flask.jsonify({'errors': validation_errors})
    response.status_code = error.status_code
    return response


@app.errorhandler(errors.InvalidUUIDError)
def handle_invalid_uuid_error(error):
    return flask.Response(status=error.status_code)

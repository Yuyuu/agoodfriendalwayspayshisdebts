import flask
import searches
import commands
import validators
import serializers


class IndexResource:
    def index(self):
        return flask.Response(status=200)


class EventsResource:
    def __init__(self, command_bus):
        self.command_bus = command_bus

    def create(self):
        data = flask.request.json
        validators.CreateEventCommandValidator(data).validate()
        command = commands.CreateEventCommand(data['name'], data['participants'])
        result = self.command_bus.send_and_wait_response(command)
        if not result.is_success():
            raise result.error
        event_id = result.response
        http_response = flask.jsonify({'id': str(event_id)})
        http_response.status_code = 201
        return http_response


class EventResource:
    def __init__(self, search_bus):
        self.search_bus = search_bus

    def represent(self, event_id):
        search = searches.EventDetailsSearch(event_id)
        result = self.search_bus.send_and_wait_response(search)
        if not result.is_success():
            raise result.error
        event = result.response
        http_response = flask.jsonify(serializers.EventSerializer.serialize(event))
        return http_response


class PurchasesResource:
    def __init__(self, command_bus):
        self.command_bus = command_bus

    def add(self, event_id):
        data = flask.request.json
        validators.AddPurchaseCommandValidator(data).validate()
        command = commands.AddPurchaseCommand(event_id, data['purchaser'], data['label'], data['amount'])
        command.participants = data['participants'] if 'participants' in data else []
        command.description = data['description'] if 'description' in data else ''
        result = self.command_bus.send_and_wait_response(command)
        if not result.is_success():
            raise result.error
        purchase = result.response
        http_response = flask.jsonify(serializers.PurchaseSerializer.serialize(purchase))
        http_response.status_code = 201
        return http_response

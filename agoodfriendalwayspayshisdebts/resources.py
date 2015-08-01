from uuid import UUID

import flask
import searches
import commands
import serializers


class Resource(object):
    @staticmethod
    def _get_data(key):
        return flask.request.json.get(key, None)


class IndexResource(Resource):
    def index(self):
        return flask.Response(status=200)


class EventsResource(Resource):
    def __init__(self, command_bus):
        self.command_bus = command_bus

    def create(self):
        command = commands.CreateEventCommand(self._get_data('name'), self._get_data('participants'))

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
        search = searches.EventDetailsSearch(UUID(hex=event_id, version=4))
        result = self.search_bus.send_and_wait_response(search)
        if not result.is_success():
            raise result.error
        event = result.response
        http_response = flask.jsonify(serializers.EventSerializer.serialize(event))
        return http_response


class PurchasesResource(Resource):
    def __init__(self, command_bus):
        self.command_bus = command_bus

    def add(self, event_id):
        command = commands.AddPurchaseCommand(
            event_id,
            self._get_data('purchaserId'),
            self._get_data('label'),
            self._get_data('amount')
        )
        command.participants_ids = self._get_data('participantsIds')
        command.description = self._get_data('description')

        result = self.command_bus.send_and_wait_response(command)
        if not result.is_success():
            raise result.error

        purchase = result.response
        http_response = flask.jsonify(serializers.PurchaseSerializer.serialize(purchase))
        http_response.status_code = 201
        return http_response


class ResultResource(Resource):
    def __init__(self, search_bus):
        self.search_bus = search_bus

    def calculate(self, event_id):
        search = searches.EventDebtsResultSearch(event_id)
        result = self.search_bus.send_and_wait_response(search)
        if not result.is_success():
            raise result.error
        debt_result = result.response
        http_response = flask.jsonify(serializers.CalculationResultSerializer.serialize(debt_result))
        return http_response

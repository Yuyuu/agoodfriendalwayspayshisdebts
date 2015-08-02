import unittest
from uuid import uuid4

import mock
import flask

from agoodfriendalwayspayshisdebts import resources, model, errors
from agoodfriendalwayspayshisdebts.calculation import DebtsResultDetail


flask.request = mock.Mock()
flask.jsonify = mock.Mock()


class EventsResourceTestCase(unittest.TestCase):
    def setUp(self):
        self.result = mock.Mock()
        self.command_bus = mock.Mock()
        self.command_bus.send_and_wait_response.return_value = self.result

    def test_can_ask_to_create_an_event(self):
        def side_effect(*args):
            return flask.Response(args[0])
        self.result.is_success.return_value = True
        self.result.response = 'id123'
        resource = resources.EventsResource(self.command_bus)
        flask.jsonify.side_effect = side_effect

        response = resource.create()

        self.assertEqual(201, response.status_code)
        self.assertEqual('id123', response.response['id'])

    def test_propagates_the_error_if_any_occurred(self):
        self.result.is_success.return_value = False
        self.result.error = RuntimeError()

        resource = resources.EventsResource(self.command_bus)

        self.assertRaises(RuntimeError, resource.create)


class EventResourceTestCase(unittest.TestCase):
    def setUp(self):
        self.result = mock.Mock()
        self.search_bus = mock.Mock()
        self.search_bus.send_and_wait_response.return_value = self.result

    def test_an_error_is_raised_if_the_given_uuid_is_invalid(self):
        resource = resources.EventResource(self.search_bus)
        self.assertRaises(errors.InvalidUUIDError, resource.represent, 'hello')

    def test_returns_the_requested_event(self):
        def side_effect(*args):
            return flask.Response(args[0])
        event_id = uuid4()
        self.result.is_success.return_value = True
        self.result.response = model.Event('cool event', [], event_id)
        resource = resources.EventResource(self.search_bus)
        flask.jsonify.side_effect = side_effect

        response = resource.represent(str(event_id))

        self.assertEqual(200, response.status_code)
        self.assertEqual('cool event', response.response['name'])
        self.assertEqual(str(event_id), response.response['id'])

    def test_propagates_the_error_if_any_occurred(self):
        event_id = uuid4()
        self.result.is_success.return_value = False
        self.result.error = RuntimeError()

        resource = resources.EventResource(self.search_bus)

        self.assertRaises(RuntimeError, resource.represent, str(event_id))


class PurchasesResourceTestCase(unittest.TestCase):
    def setUp(self):
        self.result = mock.Mock()
        self.command_bus = mock.Mock()
        self.command_bus.send_and_wait_response.return_value = self.result

    def test_an_error_is_raised_if_the_given_event_uuid_is_invalid(self):
        resource = resources.PurchasesResource(self.command_bus)
        self.assertRaises(errors.InvalidUUIDError, resource.add, 'hello')

    def test_an_error_is_raised_if_the_given_purchaser_uuid_is_invalid(self):
        flask.request.json = {'purchaserId': 'hello'}
        event_id = uuid4()
        resource = resources.PurchasesResource(self.command_bus)
        self.assertRaises(errors.InvalidUUIDError, resource.add, str(event_id))

    def test_can_ask_to_add_a_purchase(self):
        def side_effect(*args):
            return flask.Response(args[0])
        event_id = uuid4()
        flask.request.json = {'purchaserId': str(uuid4())}
        self.result.is_success.return_value = True
        self.result.response = model.Purchase('123', 1, ['123'], 'label')
        resource = resources.PurchasesResource(self.command_bus)
        flask.jsonify.side_effect = side_effect

        response = resource.add(str(event_id))

        self.assertEqual(201, response.status_code)
        self.assertEqual('123', response.response['purchaserId'])
        self.assertEqual(1, response.response['amount'])

    def test_propagates_the_error_if_any_occurred(self):
        event_id = uuid4()
        flask.request.json = {'purchaserId': str(uuid4())}
        self.result.is_success.return_value = False
        self.result.error = RuntimeError()

        resource = resources.PurchasesResource(self.command_bus)

        self.assertRaises(RuntimeError, resource.add, str(event_id))


class ResultResourceTestCase(unittest.TestCase):
    def setUp(self):
        self.result = mock.Mock()
        self.search_bus = mock.Mock()
        self.search_bus.send_and_wait_response.return_value = self.result

    def test_an_error_is_raised_if_the_given_uuid_is_invalid(self):
        resource = resources.ResultResource(self.search_bus)
        self.assertRaises(errors.InvalidUUIDError, resource.calculate, 'hello')

    def test_returns_result_of_the_event(self):
        def side_effect(*args):
            return flask.Response(args[0])
        event_id = uuid4()
        result = DebtsResultDetail()
        result.add_result('123', {'total_spent': 0, 'total_debt': 0, 'debts_detail': {}})
        self.result.is_success.return_value = True
        self.result.response = result
        resource = resources.ResultResource(self.search_bus)
        flask.jsonify.side_effect = side_effect

        response = resource.calculate(str(event_id))

        self.assertEqual(200, response.status_code)
        self.assertEqual(0, response.response['123']['totalSpent'])

    def test_propagates_the_error_if_any_occurred(self):
        event_id = uuid4()
        self.result.is_success.return_value = False
        self.result.error = RuntimeError()

        resource = resources.ResultResource(self.search_bus)

        self.assertRaises(RuntimeError, resource.calculate, str(event_id))

import abc

from bson.objectid import ObjectId

from locator import RepositoryLocator
import events


class Handler:
    __metaclass__ = abc.ABCMeta

    def __init__(self, message_type):
        self.message_type = message_type

    @abc.abstractmethod
    def execute(self, message):
        return


class CreateEventCommandHandler(Handler):
    def execute(self, command):
        participants = map(self.__create_participant_from_json, command.participants)
        event = events.Event(command.name, participants)
        RepositoryLocator.events().add(event)
        return event.oid

    @staticmethod
    def __create_participant_from_json(json_participant):
        participant = events.Participant(json_participant['name'], json_participant['share'])
        if 'email' in json_participant and json_participant['email']:
            participant.email = json_participant['email']
        return participant


class AddPurchaseCommandHandler(Handler):
    def execute(self, command):
        purchase = events.Purchase(command.purchaser, command.title, command.amount)
        purchase.participants = command.participants
        purchase.description = command.description
        event = RepositoryLocator.events().get(ObjectId(command.event_id))
        event.add_purchase(purchase)
        RepositoryLocator.events().update(event.oid, event)
        return purchase


class SearchEventDetailsHandler(Handler):
    def execute(self, search):
        oid = ObjectId(search.id_as_string)
        event = RepositoryLocator.events().get(oid)
        return event

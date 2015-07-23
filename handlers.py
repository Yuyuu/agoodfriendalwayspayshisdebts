import abc
from uuid import UUID

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
        return event.uuid

    @staticmethod
    def __create_participant_from_json(json_participant):
        participant = events.Participant(json_participant['name'], json_participant['share'])
        if 'email' in json_participant and json_participant['email']:
            participant.email = json_participant['email']
        return participant


class AddPurchaseCommandHandler(Handler):
    def execute(self, command):
        event = RepositoryLocator.events().get(UUID(command.event_id))
        participants = command.participants or self.__get_all_event_participants_names(event)
        purchase = events.Purchase(command.purchaser, command.amount, participants, command.label)
        purchase.description = command.description
        event.add_purchase(purchase)
        RepositoryLocator.events().update(event.uuid, event)
        return purchase

    @staticmethod
    def __get_all_event_participants_names(event):
        return map((lambda participant: participant.name), event.participants)


class SearchEventDetailsHandler(Handler):
    def execute(self, search):
        event_uuid = UUID(hex=search.event_id)
        event = RepositoryLocator.events().get(event_uuid)
        return event

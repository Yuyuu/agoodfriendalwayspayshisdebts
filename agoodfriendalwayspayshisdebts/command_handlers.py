from uuid import UUID
import abc

from locator import RepositoryLocator
import events
from bus import EventBus
from internal_events import EventCreatedEvent
import commands


class Handler:
    __metaclass__ = abc.ABCMeta

    def __init__(self, message_type):
        self.message_type = message_type

    @abc.abstractmethod
    def execute(self, message):
        return


class CreateEventCommandHandler(Handler):
    def __init__(self):
        super(CreateEventCommandHandler, self).__init__(commands.CreateEventCommand)

    def execute(self, command):
        participants = map(self.__create_participant_from_json, command.participants)
        event = events.Event(command.name, participants)
        RepositoryLocator.events().add(event)
        EventBus.get_instance().publish(EventCreatedEvent(event.id))
        return event.id

    @staticmethod
    def __create_participant_from_json(json_participant):
        participant = events.Participant(json_participant['name'], json_participant['share'])
        if 'email' in json_participant and json_participant['email']:
            participant.email = json_participant['email']
        return participant


class AddPurchaseCommandHandler(Handler):
    def __init__(self):
        super(AddPurchaseCommandHandler, self).__init__(commands.AddPurchaseCommand)

    def execute(self, command):
        event = RepositoryLocator.events().get(command.event_id)
        participants_ids = self.__to_uuids(command.participants_ids) if command.participants_ids\
            else self.__get_all_event_participants_ids(event)
        purchase = events.Purchase(command.purchaser_id, command.amount, participants_ids, command.label)
        purchase.description = command.description

        event.add_purchase(purchase)
        RepositoryLocator.events().update(event.id, event)

        return purchase

    @staticmethod
    def __to_uuids(participants_ids):
        return map((lambda participant_id: UUID(participant_id, version=4)), participants_ids)

    @staticmethod
    def __get_all_event_participants_ids(event):
        return map((lambda participant: participant.id), event.participants)

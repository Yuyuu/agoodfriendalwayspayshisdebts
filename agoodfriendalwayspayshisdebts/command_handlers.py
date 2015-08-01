from uuid import UUID
import abc

from agoodfriendalwayspayshisdebts.locator import RepositoryLocator
import events
from errors import InvalidUUIDError, EntityNotFoundError
from calculation import DebtsCalculator
from bus import EventBus
from internal_events import EventCreatedEvent


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
        EventBus.get_instance().publish(EventCreatedEvent(event.uuid))
        return event.uuid

    @staticmethod
    def __create_participant_from_json(json_participant):
        participant = events.Participant(json_participant['name'], json_participant['share'])
        if 'email' in json_participant and json_participant['email']:
            participant.email = json_participant['email']
        return participant


class AddPurchaseCommandHandler(Handler):
    def execute(self, command):
        event = find_event_or_raise_error(command.event_id)
        participants_ids = self.__to_uuids(command.participants_ids) if command.participants_ids\
            else self.__get_all_event_participants_ids(event)
        purchase = events.Purchase(
            UUID(command.purchaser_id, version=4),
            command.amount,
            participants_ids,
            command.label
        )
        purchase.description = command.description

        event.add_purchase(purchase)
        RepositoryLocator.events().update(event.uuid, event)

        return purchase

    @staticmethod
    def __to_uuids(participants_ids):
        return map((lambda participant_id: UUID(participant_id, version=4)), participants_ids)

    @staticmethod
    def __get_all_event_participants_ids(event):
        return map((lambda participant: participant.id), event.participants)


class SearchEventDebtsResultHandler(Handler):
    def execute(self, search):
        event = find_event_or_raise_error(search.event_id)
        return DebtsCalculator(event).calculate()


def find_event_or_raise_error(uuid_to_parse):
    try:
        event_uuid = UUID(hex=uuid_to_parse, version=4)
    except ValueError:
        raise InvalidUUIDError()

    event = RepositoryLocator.events().get(event_uuid)
    if event is None:
        raise EntityNotFoundError()
    return event

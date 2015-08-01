from uuid import uuid4
import bus
from internal_events import PurchaseAddedEvent


class Event:
    def __init__(self, name, participants, uuid=None):
        if uuid is None:
            uuid = uuid4()
        self.uuid = uuid
        self.name = name
        self.participants = participants
        self.purchases = []

    def add_participant(self, participant):
        self.participants.append(participant)

    def add_purchase(self, purchase):
        self.purchases.append(purchase)
        bus.EventBus.get_instance().publish(PurchaseAddedEvent(self.uuid))

    def __get_bson_participants(self):
        return map((lambda participant: participant.to_bson()), self.participants)

    def __get_bson_purchases(self):
        return map((lambda purchase: purchase.to_bson()), self.purchases)

    def to_bson(self):
        return {
            'uuid': self.uuid,
            'name': self.name,
            'participants': self.__get_bson_participants(),
            'purchases': self.__get_bson_purchases()
        }


class Participant:
    def __init__(self, name, share, email='', uuid=None):
        if uuid is None:
            uuid = uuid4()
        self.id = uuid
        self.name = name
        self.email = email
        self.share = share

    def to_bson(self):
        return {'id': self.id, 'name': self.name, 'email': self.email, 'share': self.share}


class Purchase:
    def __init__(self, purchaser_id, amount, participants_ids, label):
        self.purchaser_id = purchaser_id
        self.label = label
        self.amount = amount
        self.participants_ids = participants_ids
        self.description = None

    def add_participant(self, participant_id):
        self.participants_ids.append(participant_id)

    def to_bson(self):
        return {
            'purchaser_id': self.purchaser_id,
            'label': self.label,
            'amount': self.amount,
            'participants_ids': self.participants_ids,
            'description': self.description
        }

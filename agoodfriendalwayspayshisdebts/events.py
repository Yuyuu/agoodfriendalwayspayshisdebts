from uuid import uuid4


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
    def __init__(self, name, share, email=''):
        self.name = name
        self.email = email
        self.share = share

    def to_bson(self):
        return {'name': self.name, 'email': self.email, 'share': self.share}


class Purchase:
    def __init__(self, purchaser, amount, participants, label):
        self.purchaser = purchaser
        self.label = label
        self.amount = amount
        self.participants = participants
        self.description = None

    def add_participant(self, participant):
        self.participants.append(participant)

    def to_bson(self):
        return {
            'purchaser': self.purchaser,
            'label': self.label,
            'amount': self.amount,
            'participants': self.participants,
            'description': self.description
        }

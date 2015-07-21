from bson.objectid import ObjectId


class Event:
    def __init__(self, name, participants):
        self.oid = ObjectId()
        self.name = name
        self.participants = participants
        self.purchases = []

    def add_participant(self, participant):
        self.participants.append(participant)

    def add_purchase(self, purchase):
        self.purchases.append(purchase)

    def __get_serialized_participants(self):
        return map((lambda participant: participant.serialize()), self.participants)

    def __get_serialized_purchases(self):
        return map((lambda purchase: purchase.serialize()), self.purchases)

    def serialize(self):
        return {
            '_id': self.oid,
            'name': self.name,
            'participants': self.__get_serialized_participants(),
            'purchases': self.__get_serialized_purchases()
        }


class Participant:
    def __init__(self, name, share, email=''):
        self.name = name
        self.email = email
        self.share = share

    def serialize(self):
        return {'name': self.name, 'email': self.email, 'share': self.share}


class Purchase:
    def __init__(self, purchaser, label, amount):
        self.purchaser = purchaser
        self.label = label
        self.amount = amount
        self.participants = []
        self.description = None

    def add_participant(self, participant):
        self.participants.append(participant)

    def serialize(self):
        return {
            'purchaser': self.purchaser,
            'label': self.label,
            'amount': self.amount,
            'participants': self.participants,
            'description': self.description
        }

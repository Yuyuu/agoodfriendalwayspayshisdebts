from bson.objectid import ObjectId


class Event:
    def __init__(self, name, participants):
        self.oid = ObjectId()
        self.name = name
        self.participants = participants
        self.purchases = []

    def add_purchase(self, purchase):
        self.purchases.append(purchase)

    def _get_serialized_purchases(self):
        serialized_purchases = []
        for purchase in self.purchases:
            serialized_purchases.append(purchase.serialize())
        return serialized_purchases

    def serialize(self):
        return {
            '_id': self.oid,
            'name': self.name,
            'participants': self.participants,
            'purchases': self._get_serialized_purchases()
        }


class Purchase:
    def __init__(self, purchaser, title, amount):
        self.purchaser = purchaser
        self.title = title
        self.amount = amount
        self.participants = []
        self.description = None

    def add_participant(self, participant):
        self.participants.append(participant)

    def serialize(self):
        return {
            'purchaser': self.purchaser,
            'title': self.title,
            'amount': self.amount,
            'participants': self.participants,
            'description': self.description
        }
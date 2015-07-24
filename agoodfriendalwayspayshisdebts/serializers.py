class EventSerializer:
    @staticmethod
    def serialize(event):
        return {
            'id': str(event.uuid),
            'name': event.name,
            'participants': map(ParticipantSerializer.serialize, event.participants),
            'purchases': map(PurchaseSerializer.serialize, event.purchases)
        }


class ParticipantSerializer:
    @staticmethod
    def serialize(participant):
        return {
            'name': participant.name,
            'email': participant.email,
            'share': participant.share
        }


class PurchaseSerializer:
    @staticmethod
    def serialize(purchase):
        return {
            'purchaser': purchase.purchaser,
            'label': purchase.label,
            'amount': purchase.amount,
            'participants': purchase.participants,
            'description': purchase.description
        }

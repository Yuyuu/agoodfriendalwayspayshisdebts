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


class CalculationResultSerializer:
    @staticmethod
    def serialize(result):
        return {
            participant_id: CalculationResultSerializer.__serialize_participant_result(participant_result)
            for participant_id, participant_result in result.results_per_participant.iteritems()
        }

    @staticmethod
    def __serialize_participant_result(participant_result):
        return {
            'total_spent': participant_result.total_spent,
            'total_debt': participant_result.total_debt,
            'debts_detail': participant_result.debts_detail
        }

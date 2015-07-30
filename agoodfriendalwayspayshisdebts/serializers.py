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
            'id': str(participant.id),
            'name': participant.name,
            'email': participant.email,
            'share': participant.share
        }


class PurchaseSerializer:
    @staticmethod
    def serialize(purchase):
        return {
            'purchaserId': str(purchase.purchaser_id),
            'label': purchase.label,
            'amount': purchase.amount,
            'participantsIds': map(lambda participant_id: str(participant_id), purchase.participants_ids),
            'description': purchase.description
        }


class CalculationResultSerializer:
    @staticmethod
    def serialize(result):
        return {
            str(participant_id): CalculationResultSerializer.__serialize_participant_result(participant_result)
            for participant_id, participant_result in result.results_per_participant.iteritems()
        }

    @staticmethod
    def __serialize_participant_result(participant_result):
        return {
            'totalSpent': participant_result.total_spent,
            'totalDebt': participant_result.total_debt,
            'debtsDetail': {
                str(creditor_id): amount for creditor_id, amount in participant_result.debts_detail.iteritems()
            }
        }

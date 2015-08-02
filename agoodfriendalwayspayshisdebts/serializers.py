class EventSerializer(object):
    @staticmethod
    def serialize(event):
        return {
            'id': str(event.id),
            'name': event.name,
            'participants': map(ParticipantSerializer.serialize, event.participants),
            'purchases': map(PurchaseSerializer.serialize, event.purchases)
        }


class ParticipantSerializer(object):
    @staticmethod
    def serialize(participant):
        return {
            'id': str(participant.id),
            'name': participant.name,
            'email': participant.email,
            'share': participant.share
        }


class PurchaseSerializer(object):
    @staticmethod
    def serialize(purchase):
        return {
            'purchaserId': str(purchase.purchaser_id),
            'label': purchase.label,
            'amount': purchase.amount,
            'participantsIds': map(lambda participant_id: str(participant_id), purchase.participants_ids),
            'description': purchase.description
        }


class DebtsResultDetailSerializer(object):
    @staticmethod
    def serialize(debts_result_detail):
        return {
            participant_id: DebtsResultDetailSerializer.__serialize_result(result)
            for (participant_id, result) in debts_result_detail.participants_results
        }

    @staticmethod
    def __serialize_result(result):
        return {
            'totalSpent': result['total_spent'],
            'totalDebt': result['total_debt'],
            'debtsDetail': {
                creditor_id: amount for creditor_id, amount in result['debts_detail'].iteritems()
            }
        }

import events
from calculation import DebtsResultDetail


class EventFactory(object):
    @staticmethod
    def create_event_from_document(document):
        participants = map(EventFactory.__create_participants, document['participants'])
        event = events.Event(document['name'], participants, document['uuid'])
        event.purchases = map(EventFactory.__create_purchases, document['purchases'])
        return event

    @staticmethod
    def __create_participants(bson_participant):
        return events.Participant(
            bson_participant['name'],
            bson_participant['share'],
            bson_participant['email'],
            bson_participant['id']
        )

    @staticmethod
    def __create_purchases(bson_purchase):
        purchase = events.Purchase(
            bson_purchase['purchaser_id'],
            bson_purchase['amount'],
            bson_purchase['participants_ids'],
            bson_purchase['label']
        )
        purchase.description = bson_purchase['description']
        return purchase


class ResultDetailFactory(object):
    @staticmethod
    def create_from_document(document):
        debts_result_detail = DebtsResultDetail()
        for participant_id, participant_debts in document['detail'].iteritems():
            debts_result_detail.add_result(participant_id, participant_debts)
        return debts_result_detail

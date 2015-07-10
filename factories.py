import events


class EventFactory():
    @staticmethod
    def create_event_from_document(document):
        event = events.Event(document['name'], document['participants'])
        event.oid = document['_id']
        purchases = []

        for bson_purchase in document['purchases']:
            purchase = events.Purchase(bson_purchase['purchaser'], bson_purchase['title'], bson_purchase['amount'])
            purchase.participants = bson_purchase['participants']
            purchase.description = bson_purchase['description']
            purchases.append(purchase)

        event.purchases = purchases
        return event

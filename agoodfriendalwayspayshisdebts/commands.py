class CreateEventCommand:
    def __init__(self, name, participants):
        self.name = name
        self.participants = participants


class AddPurchaseCommand:
    def __init__(self, event_id, purchaser_id, label, amount):
        self.event_id = event_id
        self.purchaser_id = purchaser_id
        self.label = label
        self.amount = amount
        self.participants_ids = None
        self.description = ''

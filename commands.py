class CreateEventCommand:
    def __init__(self, name, participants):
        self.name = name
        self.participants = participants


class AddPurchaseCommand:
    def __init__(self, event_id, purchaser, title, amount):
        self.event_id = event_id
        self.purchaser = purchaser
        self.title = title
        self.amount = amount
        self.participants = None
        self.description = ''

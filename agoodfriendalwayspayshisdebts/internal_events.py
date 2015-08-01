class Event(object):
    def __init__(self, is_synchronous):
        self.is_synchronous = is_synchronous


class EventCreatedEvent(Event):
    def __init__(self, event_id):
        super(EventCreatedEvent, self).__init__(True)
        self.event_id = event_id

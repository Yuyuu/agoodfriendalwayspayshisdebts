import abc

from locator import RepositoryLocator
from command_handlers import Handler
import internal_events


DB = None


class EventHandler(Handler):
    __metaclass__ = abc.ABCMeta

    @abc.abstractmethod
    def execute_event(self, event):
        pass

    def execute(self, event):
        self.execute_event(event)
        return None


class OnEventCreated(EventHandler):
    def __init__(self):
        super(OnEventCreated, self).__init__(internal_events.EventCreatedEvent)

    def execute_event(self, event):
        created_event = RepositoryLocator.events().get(event.event_id)
        DB['eventdetails_view'].insert(created_event.to_bson())

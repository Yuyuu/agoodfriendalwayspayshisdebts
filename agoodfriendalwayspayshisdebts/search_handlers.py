from command_handlers import Handler
import searches
from errors import EntityNotFoundError
from factories import EventFactory, ResultDetailFactory


DB = None


class SearchEventDetailsHandler(Handler):
    def __init__(self):
        super(SearchEventDetailsHandler, self).__init__(searches.EventDetailsSearch)

    def execute(self, search):
        event_details_document = DB['eventdetails_view'].find_one({'_id': search.event_id})
        if event_details_document is None:
            raise EntityNotFoundError()

        return EventFactory.create_event_from_document(event_details_document)


class SearchEventDebtsResultHandler(Handler):
    def __init__(self):
        super(SearchEventDebtsResultHandler, self).__init__(searches.EventDebtsResultSearch)

    def execute(self, search):
        event_result_document = DB['eventresult_view'].find_one({'event_id': search.event_id})
        if event_result_document is None:
            raise EntityNotFoundError()

        return ResultDetailFactory.create_from_document(event_result_document)

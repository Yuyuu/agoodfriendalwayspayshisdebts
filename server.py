import flask

import bus
import commands
import searches
import handlers


class Server(flask.Flask):
    def __init__(self, import_name):
        self.command_bus = None
        self.search_bus = None
        super(Server, self).__init__(import_name)

    def configure_commands(self):
        command_handlers = [
            handlers.CreateEventCommandHandler(commands.CreateEventCommand),
            handlers.AddPurchaseCommandHandler(commands.AddPurchaseCommand)
        ]
        self.command_bus = bus.CommandBus(command_handlers)

    def configure_searches(self):
        search_handlers = [handlers.SearchEventDetailsHandler(searches.EventDetailsSearch)]
        self.search_bus = bus.SearchBus(search_handlers)

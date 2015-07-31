import flask

import bus
import handlers
import searches
import commands
import validators


class Server(flask.Flask):
    def __init__(self, import_name):
        self.command_bus = None
        self.search_bus = None
        super(Server, self).__init__(import_name)

    def configure_commands(self):
        command_synchronizations = [
            bus.EventBus.get_instance(),
            validators.CreateEventCommandValidator(commands.CreateEventCommand),
            validators.AddPurchaseCommandValidator(commands.AddPurchaseCommand)
        ]
        command_handlers = [
            handlers.CreateEventCommandHandler(commands.CreateEventCommand),
            handlers.AddPurchaseCommandHandler(commands.AddPurchaseCommand)
        ]
        self.command_bus = bus.CommandBus(command_synchronizations, command_handlers)

    def configure_searches(self):
        search_handlers = [
            handlers.SearchEventDetailsHandler(searches.EventDetailsSearch),
            handlers.SearchEventDebtsResultHandler(searches.EventDebtsResultSearch)
        ]
        self.search_bus = bus.SearchBus(search_handlers)

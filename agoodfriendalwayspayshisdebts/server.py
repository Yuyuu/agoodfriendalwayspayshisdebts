import flask

import bus
import command_handlers as ch
import event_search_handlers as esh
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
        c_handlers = [
            ch.CreateEventCommandHandler(commands.CreateEventCommand),
            ch.AddPurchaseCommandHandler(commands.AddPurchaseCommand)
        ]
        self.command_bus = bus.CommandBus(command_synchronizations, c_handlers)

    def configure_searches(self):
        search_handlers = [
            esh.SearchEventDetailsHandler(),
            esh.SearchEventDebtsResultHandler()
        ]
        self.search_bus = bus.SearchBus(search_handlers)

import config
import server
import resources


config.configure_logging()
config.initialize_repository()

app = server.Server(__name__)

app.configure_commands()
app.configure_searches()

app.add_url_rule('/', 'index', resources.IndexResource().index)
app.add_url_rule('/events', 'create_event', resources.EventsResource(app.command_bus).create, methods=['POST'])
app.add_url_rule('/events/<event_id>', 'get_event', resources.EventResource(app.search_bus).represent, methods=['GET'])
app.add_url_rule(
    '/events/<event_id>/purchases',
    'add_purchase',
    resources.PurchasesResource(app.command_bus).add,
    methods=['POST']
)

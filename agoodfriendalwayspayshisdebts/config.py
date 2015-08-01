import sys
import logging
from os import environ

import pymongo
import pymongo.errors

import locator
import event_search_handlers as esh
from bus import AsynchronousEventBus


def initialize_events():
    event_synchronizations = []
    event_handlers = [esh.OnEventCreated(), esh.OnPurchaseAddedUpdateView(), esh.OnPurchaseAddedUpdateResult()]
    locator.EventBusLocator.initialize(AsynchronousEventBus(event_synchronizations, event_handlers))


def initialize_repository():
    mongo_uri = environ.get('AGFAPHD_API_MONGO_URI', 'mongodb://localhost:27017/agoodfriendalwayspayshisdebts')
    try:
        database = pymongo.MongoClient(mongo_uri).get_default_database()
        esh.DB = database
        locator.db = database
        locator.RepositoryLocator.initialize(locator.MongoRepositoryLocator())
    except (pymongo.errors.ConnectionFailure, pymongo.errors.AutoReconnect):
        print 'Could not connect to database on %s' % mongo_uri
        sys.exit(0)


def configure_logging():
    root = logging.getLogger()
    root.setLevel(logging.DEBUG)
    handler = logging.StreamHandler(sys.stdout)
    handler.setLevel(logging.DEBUG)
    formatter = logging.Formatter('[%(asctime)s] [%(name)s] [%(levelname)s] %(message)s')
    handler.setFormatter(formatter)
    root.addHandler(handler)

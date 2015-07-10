import sys
import logging

import pymongo
import pymongo.errors

import locator


def initialize_repository():
    host = 'localhost'
    port = 27017
    try:
        database = pymongo.MongoClient(host, port)['agoodfriendalwayspayshisdebts']
        locator.db = database
        locator.RepositoryLocator.initialize(locator.MongoRepositoryLocator())
    except (pymongo.errors.ConnectionFailure, pymongo.errors.AutoReconnect):
        print 'could not connect to database on mongodb://{0}:{1}/'.format(host, port)
        sys.exit(0)


def configure_logging():
    root = logging.getLogger()
    root.setLevel(logging.DEBUG)
    handler = logging.StreamHandler(sys.stdout)
    handler.setLevel(logging.DEBUG)
    formatter = logging.Formatter('%(asctime)s %(name)12s %(levelname)7s - %(message)s')
    handler.setFormatter(formatter)
    root.addHandler(handler)

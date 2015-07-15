import config
import server


config.configure_logging()
config.initialize_repository()

server.app.configure_commands()
server.app.configure_searches()

app = server.app

if __name__ == '__main__':
    server.app.run(port=8089)

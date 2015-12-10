package integration

import agoodfriendalwayspayshisdebts.web.configuration.GuiceConfiguration
import com.gmongo.GMongo
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Stage
import com.mongodb.DB
import com.mongodb.MongoClientURI
import com.mongodb.MongoURI
import com.vter.model.internal_event.InternalEventBus
import com.vter.model.internal_event.InternalEventBusLocator
import com.vter.web.fluent.extension.ApplicationExtensions
import com.vter.web.fluent.status.ApplicationStatusService
import net.codestory.http.Configuration
import net.codestory.http.WebServer
import net.codestory.http.injection.GuiceAdapter
import net.codestory.http.misc.Env
import net.codestory.http.misc.MemoizingSupplier
import org.junit.rules.ExternalResource

import java.util.function.Supplier

class WithLoadedEnvironment extends ExternalResource {

  WithLoadedEnvironment() {
    injector = Guice.createInjector(Stage.PRODUCTION, new GuiceConfiguration())
    configuration = { routes ->
      routes
          .setExtensions(ApplicationExtensions.withStatusService(injector.getInstance(ApplicationStatusService.class)))
          .setIocAdapter(new GuiceAdapter(injector))
          .autoDiscover("agoodfriendalwayspayshisdebts.web.actions")
    }
    server = MemoizingSupplier.memoize({ ->
      new WebServer() {
        @Override
        protected Env createEnv() {
          return Env.prod()
        }
      }
          .configure(configuration)
          .startOnRandomPort()
    })
    InternalEventBusLocator.initialize(injector.getInstance(InternalEventBus.class))
    MongoClientURI uri = injector.getInstance(MongoClientURI.class)
    gMongo = new GMongo(new MongoURI(uri))
  }

  static int port() {
    return server.get().port()
  }

  Injector injector() {
    return injector
  }

  DB db() {
    return gMongo.getDB("debts-test")
  }

  private final Injector injector
  private final Configuration configuration
  private final GMongo gMongo
  private static Supplier<WebServer> server
}

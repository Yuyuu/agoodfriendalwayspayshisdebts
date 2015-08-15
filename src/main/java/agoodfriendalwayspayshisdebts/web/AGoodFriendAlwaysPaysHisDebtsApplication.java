package agoodfriendalwayspayshisdebts.web;

import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.web.configuration.GuiceConfiguration;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.vter.model.internal_event.InternalEventBus;
import com.vter.model.internal_event.InternalEventBusLocator;
import com.vter.web.fluent.BaseApplication;
import com.vter.web.fluent.extension.ApplicationExtensions;
import com.vter.web.fluent.status.ApplicationStatusService;
import net.codestory.http.Configuration;
import net.codestory.http.injection.GuiceAdapter;
import net.codestory.http.payload.Payload;

import java.util.Optional;

public class AGoodFriendAlwaysPaysHisDebtsApplication extends BaseApplication {

  public AGoodFriendAlwaysPaysHisDebtsApplication() {
    injector = Guice.createInjector(stage(), new GuiceConfiguration());
    InternalEventBusLocator.initialize(injector.getInstance(InternalEventBus.class));
    RepositoryLocator.initialize(injector.getInstance(RepositoryLocator.class));
  }

  private Stage stage() {
    final String env = Optional.ofNullable(System.getenv("env")).orElse("dev");
    LOGGER.info("Configuration mode: {}", env);
    if (env.equals("dev")) {
      return Stage.DEVELOPMENT;
    }
    return Stage.PRODUCTION;
  }

  @Override
  protected Configuration routes() {
    return routes -> routes
        .setExtensions(ApplicationExtensions.withStatusService(new ApplicationStatusService()))
        .setIocAdapter(new GuiceAdapter(injector))
        .get("/", Payload.ok())
        .autoDiscover("agoodfriendalwayspayshisdebts.web.actions");
  }

  private final Injector injector;
}

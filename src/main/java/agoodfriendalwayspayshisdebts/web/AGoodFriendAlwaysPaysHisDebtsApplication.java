package agoodfriendalwayspayshisdebts.web;

import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.web.configuration.GuiceConfiguration;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.vter.web.fluent.BaseApplication;
import net.codestory.http.Configuration;
import net.codestory.http.injection.GuiceAdapter;
import net.codestory.http.payload.Payload;

import java.util.Optional;

public class AGoodFriendAlwaysPaysHisDebtsApplication extends BaseApplication {

  public AGoodFriendAlwaysPaysHisDebtsApplication() {
    injector = Guice.createInjector(stage(), new GuiceConfiguration());
    RepositoryLocator.initialize(injector.getInstance(RepositoryLocator.class));
  }

  private Stage stage() {
    final Optional<String> env = Optional.ofNullable(System.getenv("env"));
    LOGGER.info("Configuration mode: {}", env.orElse("dev"));
    if (env.orElse("dev").equals("dev")) {
      return Stage.DEVELOPMENT;
    }
    return Stage.PRODUCTION;
  }

  @Override
  protected Configuration routes() {
    return routes -> routes
        .setIocAdapter(new GuiceAdapter(injector))
        .get("/", Payload.ok())
        .autoDiscover("agoodfriendalwayspayshisdebts.web.action");
  }

  private final Injector injector;
}

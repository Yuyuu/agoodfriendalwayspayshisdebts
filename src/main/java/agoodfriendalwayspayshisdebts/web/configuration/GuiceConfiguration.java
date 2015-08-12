package agoodfriendalwayspayshisdebts.web.configuration;

import agoodfriendalwayspayshisdebts.infrastructure.persistence.mongo.MongoLinkRepositoryLocator;
import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.mongodb.MongoClientURI;
import com.vter.command.CommandBus;
import com.vter.command.CommandHandler;
import com.vter.command.CommandSynchronization;
import com.vter.command.CommandValidator;
import com.vter.infrastructure.bus.guice.HandlerScanner;
import com.vter.infrastructure.persistence.mongo.MongoLinkContext;
import com.vter.model.internal_event.AsynchronousInternalEventBus;
import com.vter.model.internal_event.InternalEventBus;
import com.vter.model.internal_event.InternalEventHandler;
import com.vter.model.internal_event.InternalEventSynchronization;
import org.mongolink.MongoSessionManager;
import org.mongolink.Settings;
import org.mongolink.UpdateStrategies;
import org.mongolink.domain.mapper.ContextBuilder;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Optional;

public class GuiceConfiguration extends AbstractModule {

  @Override
  protected void configure() {
    configurePersistence();
    configureCommands();
    configureEvents();
  }

  private void configurePersistence() {
    bind(MongoLinkContext.class).in(Singleton.class);

    bind(RepositoryLocator.class).to(MongoLinkRepositoryLocator.class).in(Singleton.class);
  }

  private void configureCommands() {
    final Multibinder<CommandSynchronization> multibinder = Multibinder.newSetBinder(binder(), CommandSynchronization.class);
    multibinder.addBinding().to(MongoLinkContext.class);
    multibinder.addBinding().to(CommandValidator.class);
    multibinder.addBinding().to(AsynchronousInternalEventBus.class);
    HandlerScanner.scanPackageAndBind("agoodfriendalwayspayshisdebts.command", CommandHandler.class, binder());
    bind(CommandBus.class).asEagerSingleton();
  }

  private void configureEvents() {
    final Multibinder<InternalEventSynchronization> multibinder = Multibinder.newSetBinder(binder(), InternalEventSynchronization.class);
    multibinder.addBinding().to(MongoLinkContext.class);
    HandlerScanner.scanPackageAndBind("agoodfriendalwayspayshisdebts.search", InternalEventHandler.class, binder());
    bind(InternalEventBus.class).to(AsynchronousInternalEventBus.class).asEagerSingleton();
  }

  @Provides
  public Validator validator() {
    return Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Provides
  @Singleton
  public MongoSessionManager mongoLink() {
    final MongoClientURI uri = new MongoClientURI(
        Optional.ofNullable(System.getenv("AGFAPHD_API_MONGO_URI"))
            .orElseThrow(() -> new IllegalStateException("Missing database configuration"))
    );
    Settings settings = Settings.defaultInstance().withDefaultUpdateStrategy(UpdateStrategies.DIFF)
        .withHost(extractHostname(uri.getHosts().get(0)))
        .withPort(extractPort(uri.getHosts().get(0)))
        .withDbName(uri.getDatabase());
    if (uri.getCredentials() != null) {
      settings = settings.withAuthentication(uri.getUsername(), new String(uri.getPassword()));
    }

    return MongoSessionManager.create(
        new ContextBuilder("agoodfriendalwayspayshisdebts.infrastructure.persistence.mongo.mapping"),
        settings
    );
  }

  private static String extractHostname(String host) {
    int colonIndex = findColonIndex(host);
    return host.substring(0, colonIndex);
  }

  private static int extractPort(String host) {
    int colonIndex = findColonIndex(host);
    return Integer.parseInt(host.substring(colonIndex + 1));
  }

  private static int findColonIndex(String host) {
    int colonIndex = host.indexOf(':');
    if (colonIndex < 0) {
      throw new IllegalStateException("Malformed database URI");
    }
    return colonIndex;
  }
}

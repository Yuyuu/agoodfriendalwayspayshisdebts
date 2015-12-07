package agoodfriendalwayspayshisdebts.web.configuration;

import agoodfriendalwayspayshisdebts.infrastructure.persistence.mongo.MongoLinkRepositoryLocator;
import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.mongodb.DB;
import com.mongodb.MongoClient;
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
import com.vter.search.SearchBus;
import com.vter.search.SearchHandler;
import com.vter.web.fluent.status.resolver.ExceptionResolver;
import org.jongo.Jongo;
import org.jongo.marshall.jackson.JacksonMapper;
import org.mongolink.MongoSessionManager;
import org.mongolink.Settings;
import org.mongolink.UpdateStrategies;
import org.mongolink.domain.mapper.ContextBuilder;

import javax.validation.Validation;
import javax.validation.Validator;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.Set;

public class GuiceConfiguration extends AbstractModule {

  @Override
  protected void configure() {
    configurePersistence();
    configureCommands();
    configureEvents();
    configureSearches();
    configureExceptionResolvers();
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

  private void configureSearches() {
    HandlerScanner.scanPackageAndBind("agoodfriendalwayspayshisdebts.search", SearchHandler.class, binder());
    bind(SearchBus.class).asEagerSingleton();
  }

  private void configureExceptionResolvers() {
    HandlerScanner.scanPackageAndBind("agoodfriendalwayspayshisdebts.model", ExceptionResolver.class, binder());
  }

  @Provides
  public Validator validator() {
    return Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Provides
  @Singleton
  private AsynchronousInternalEventBus asynchronousInternalEventBus(Set<InternalEventSynchronization> internalEventSynchronizations, Set<InternalEventHandler> internalEventHandlers) {
    return new AsynchronousInternalEventBus(internalEventSynchronizations, internalEventHandlers);
  }

  @Provides
  @Singleton
  public MongoSessionManager mongoLink(MongoClientURI mongoUri) {
    Settings settings = Settings.defaultInstance().withDefaultUpdateStrategy(UpdateStrategies.DIFF)
        .withHost(extractHostname(mongoUri.getHosts().get(0)))
        .withPort(extractPort(mongoUri.getHosts().get(0)))
        .withDbName(mongoUri.getDatabase());
    if (mongoUri.getCredentials() != null) {
      settings = settings.withAuthentication(mongoUri.getUsername(), new String(mongoUri.getPassword()));
    }

    return MongoSessionManager.create(
        new ContextBuilder("agoodfriendalwayspayshisdebts.infrastructure.persistence.mongo.mapping"),
        settings
    );
  }

  @Provides
  @Singleton
  public Jongo jongo(MongoClientURI mongoUri) throws UnknownHostException {
    final MongoClient mongoClient = new MongoClient(mongoUri);
    final DB db = mongoClient.getDB(mongoUri.getDatabase());
    return new Jongo(
        db,
        new JacksonMapper.Builder()
            .registerModule(new JodaModule())
            .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build()
    );
  }

  @Provides
  @Singleton
  private MongoClientURI mongoClientURI() {
    final Optional<String> optionalMongoUri = Optional.ofNullable(System.getenv("AGFAPHD_API_MONGO_URI"));
    return new MongoClientURI(
        optionalMongoUri.orElseThrow(() -> new IllegalStateException("Missing database configuration"))
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

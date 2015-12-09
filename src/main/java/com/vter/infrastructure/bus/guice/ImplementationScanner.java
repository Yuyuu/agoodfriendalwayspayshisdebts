package com.vter.infrastructure.bus.guice;

import com.google.inject.Binder;
import com.google.inject.multibindings.Multibinder;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.Set;

public final class ImplementationScanner {

  private ImplementationScanner() {}

  public static <T> void scanPackageAndBind(String packageName, Class<T> type, Binder binder) {
    final Reflections reflections = new Reflections(ClasspathHelper.forPackage("com.vter"), ClasspathHelper.forPackage(packageName));
    final Set<Class<? extends T>> subTypes = reflections.getSubTypesOf(type);
    final Multibinder<T> searchMultibinder = Multibinder.newSetBinder(binder, type);
    subTypes.forEach(subType -> {
      if (!Modifier.isAbstract(subType.getModifiers()) && subType.getCanonicalName().startsWith(packageName)) {
        LOGGER.debug("Found implementation for {}: {}", type, subType);
        searchMultibinder.addBinding().to(subType);
      }
    });
  }

  private static Logger LOGGER = LoggerFactory.getLogger(ImplementationScanner.class);
}

package com.vter.infrastructure.bus.guice;

import com.google.inject.Binder;
import com.google.inject.multibindings.Multibinder;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.Set;

public final class HandlerScanner {

  private HandlerScanner() {}

  public static <T> void scanPackageAndBind(String packageName, Class<T> type, Binder binder) {
    Reflections reflections = new Reflections(ClasspathHelper.forPackage("com.vter"), ClasspathHelper.forPackage(packageName));
    Set<Class<? extends T>> searches = reflections.getSubTypesOf(type);
    Multibinder<T> searchMultibinder = Multibinder.newSetBinder(binder, type);
    searches.forEach(foundType -> {
      if (!Modifier.isAbstract(foundType.getModifiers())) {
        LOGGER.debug("Found implementation for {}: {}", type, foundType);
        searchMultibinder.addBinding().to(foundType);
      }
    });
  }

  private static Logger LOGGER = LoggerFactory.getLogger(HandlerScanner.class);
}

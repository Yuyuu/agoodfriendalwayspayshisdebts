package com.vter.web.fluent;

import net.codestory.http.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseApplication {

  protected abstract Configuration routes();

  protected synchronized void start() throws Exception {
    LOGGER.info("Starting application");
  }

  protected static final Logger LOGGER = LoggerFactory.getLogger(BaseApplication.class);
}

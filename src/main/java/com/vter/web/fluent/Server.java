package com.vter.web.fluent;

import net.codestory.http.WebServer;

public class Server {

  public Server(BaseApplication application) {
    this.application = application;
    webServer = new WebServer().configure(application.routes());
  }

  public void start(int port) throws Exception {
    webServer.start(port);
    application.start();
  }

  private final BaseApplication application;
  private final WebServer webServer;
}

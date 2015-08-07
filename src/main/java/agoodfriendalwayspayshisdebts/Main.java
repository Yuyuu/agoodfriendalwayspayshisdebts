package agoodfriendalwayspayshisdebts;

import agoodfriendalwayspayshisdebts.web.AGoodFriendAlwaysPaysHisDebtsApplication;
import com.vter.web.fluent.Server;

import java.util.Optional;

public class Main {

  public static void main(String[] args) throws Exception {
    new Server(new AGoodFriendAlwaysPaysHisDebtsApplication()).start(port());
  }

  private static int port() {
    final Optional<String> port = Optional.ofNullable(System.getenv("PORT"));
    return Integer.parseInt(port.orElse("8089"));
  }
}

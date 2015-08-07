package agoodfriendalwayspayshisdebts.web;

import com.vter.web.fluent.BaseApplication;
import net.codestory.http.Configuration;
import net.codestory.http.payload.Payload;

public class AGoodFriendAlwaysPaysHisDebtsApplication extends BaseApplication {

  @Override
  protected Configuration routes() {
    return routes -> routes
        .get("/", Payload.ok());
  }
}

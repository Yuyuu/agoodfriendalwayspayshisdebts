package agoodfriendalwayspayshisdebts.infrastructure.persistence.mongo;

import agoodfriendalwayspayshisdebts.model.activity.Operation;
import agoodfriendalwayspayshisdebts.model.activity.OperationRepository;
import com.vter.infrastructure.persistence.mongo.MongoRepositoryWithUuid;
import org.mongolink.MongoSession;

public class MongoOperationRepository extends MongoRepositoryWithUuid<Operation> implements OperationRepository {

  protected MongoOperationRepository(MongoSession session) {
    super(session);
  }
}

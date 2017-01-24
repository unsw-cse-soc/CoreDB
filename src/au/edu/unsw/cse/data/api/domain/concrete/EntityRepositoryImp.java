package au.edu.unsw.cse.data.api.domain.concrete;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import au.edu.unsw.cse.data.api.domain.abstracts.EntityRepository;

public class EntityRepositoryImp implements EntityRepository {

  private final MongoDatabase mongoDatabase;

  @Inject
  public EntityRepositoryImp(MongoDatabase mongoDatabase) {
    this.mongoDatabase = mongoDatabase;
  }

  @Override
  public void create(Document entitty, String collection) {
    MongoCollection<Document> col = mongoDatabase.getCollection(collection);
    col.insertOne(entitty);
    if (entitty.containsKey("clientId")) {
      entitty.remove("clientId");
    }
    if (entitty.containsKey("_id")) {
      String id = entitty.getObjectId("_id").toString();
      entitty.append("id", id);
      entitty.remove("_id");
    }
  }

  @Override
  public Document get(String id, String clientId, String collection) {
    MongoCollection<Document> col = mongoDatabase.getCollection(collection);
    BasicDBObject query = new BasicDBObject();
    query.put("_id", new ObjectId(id));
    query.put("clientId", clientId);
    Document dbObj = col.find(query).first();
    if (dbObj.containsKey("clientId")) {
      dbObj.remove("clientId");
    }
    if (dbObj.containsKey("_id")) {
      dbObj.append("id", id);
      dbObj.remove("_id");
    }
    return dbObj;
  }

  @Override
  public List<Document> getAll(String clientId, String collection) {
    MongoCollection<Document> col = mongoDatabase.getCollection(collection);
    BasicDBObject query = new BasicDBObject();
    query.put("clientId", clientId);
    List<Document> objects = col.find(query).into(new ArrayList<Document>());
    objects.forEach(object -> {
      if (object.containsKey("clientId")) {
        object.remove("clientId");
      }
      if (object.containsKey("_id")) {
        String id = object.getObjectId("_id").toString();
        object.append("id", id);
        object.remove("_id");
      }
    });
    return objects;
  }

}

package au.edu.unsw.cse.data.api.domain.concrete;

import au.edu.unsw.cse.data.api.domain.abstracts.DatabaseRepository;
import au.edu.unsw.cse.data.api.domain.entity.Client;
import au.edu.unsw.cse.data.api.domain.entity.Database;
import javax.inject.Inject;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import com.mongodb.MongoClient;

public class DatabaseRepositoryImp extends RepositoryImp<Database> implements DatabaseRepository {

  @Inject
  public DatabaseRepositoryImp(Morphia morphia, MongoClient mongoClient) {
    super(morphia, mongoClient, Database.class);
  }

  @Override
  public Database get(String clientId, String databaseName) {
    final Query<Database> query = getDataStore("dataApi").createQuery(Database.class);
    query.and(query.criteria("name").equalIgnoreCase(databaseName),
        query.criteria("client").equal(new Key<>(Client.class, "clients", new ObjectId(clientId))));
    Database database = query.get();
    return database;
  }
}

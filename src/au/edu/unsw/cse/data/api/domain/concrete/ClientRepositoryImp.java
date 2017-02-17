package au.edu.unsw.cse.data.api.domain.concrete;

import javax.inject.Inject;

import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import com.mongodb.MongoClient;

import au.edu.unsw.cse.data.api.domain.abstracts.ClientRepository;
import au.edu.unsw.cse.data.api.domain.entity.Client;

public class ClientRepositoryImp extends RepositoryImp<Client> implements ClientRepository {

  @Inject
  public ClientRepositoryImp(Morphia morphia, MongoClient mongoClient) {
    super(morphia, mongoClient, Client.class);
  }

  @Override
  public Client get(String name, String secret) {
    final Query<Client> query = getDataStore("dataApi").createQuery(Client.class);
    query.and(query.criteria("name").equal(name), query.criteria("secret").equal(secret));
    Client client = query.get();
    return client;
  }

  @Override
  public Client getByName(String name) {
    final Query<Client> query = getDataStore("dataApi").createQuery(Client.class);
    query.and(query.criteria("name").equalIgnoreCase(name));
    Client client = query.get();
    return client;
  }

}

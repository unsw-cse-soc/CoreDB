package au.edu.unsw.cse.data.api.domain.concrete;

import javax.inject.Inject;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;

import au.edu.unsw.cse.data.api.domain.abstracts.UserRepository;
import au.edu.unsw.cse.data.api.domain.entity.Client;
import au.edu.unsw.cse.data.api.domain.entity.User;

public class UserRepositoryImp extends RepositoryImp<User> implements UserRepository {

  @Inject
  public UserRepositoryImp(Datastore datastore) {
    super(datastore, User.class);
  }

  @Override
  public User get(String userName) {
    final Query<User> query = datastore.createQuery(User.class);
    query.and(query.criteria("email").equalIgnoreCase(userName));
    User user = query.get();
    return user;
  }

  @Override
  public User get(String userName, String password) {
    final Query<User> query = datastore.createQuery(User.class);
    query.and(query.criteria("userName").equalIgnoreCase(userName),
        query.criteria("password").equal(password));
    User user = query.get();
    return user;
  }

  @Override
  public User getByUserNameClientId(String userName, String clientId) {
    Query<User> query = datastore.find(typeParameterClass).disableValidation().field("client")
        .equal(new Key<>(Client.class, "clients", new ObjectId(clientId)));
    return query.get();
  }

}

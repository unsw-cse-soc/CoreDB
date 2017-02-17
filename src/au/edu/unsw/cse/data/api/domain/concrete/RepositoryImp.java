package au.edu.unsw.cse.data.api.domain.concrete;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.aggregation.AggregationPipeline;
import org.mongodb.morphia.query.Query;

import com.mongodb.MongoClient;

import au.edu.unsw.cse.data.api.domain.abstracts.Repository;

public abstract class RepositoryImp<T extends au.edu.unsw.cse.data.api.domain.entity.Entity>
    implements Repository<T> {

  protected final Morphia morphia;
  protected final MongoClient mongoClient;
  final Class<T> typeParameterClass;

  public RepositoryImp(Morphia morphia, MongoClient mongoClient, Class<T> typeParameterClass) {
    this.morphia = morphia;
    this.mongoClient = mongoClient;
    this.typeParameterClass = typeParameterClass;
  }

  protected Datastore getDataStore(String databaseName) {
    return morphia.createDatastore(mongoClient, databaseName);
  }

  @Override
  public T get(String databaseName, String id) {
    ObjectId objectId = new ObjectId(id);
    T entity = getDataStore(databaseName).get(typeParameterClass, objectId);
    return entity;
  }

  @Override
  public List<T> getAll(String databaseName) {
    final Query<T> query = getDataStore(databaseName).createQuery(typeParameterClass);
    final List<T> entities = query.asList();
    return entities;
  }

  @Override
  public Query<T> getQueryable(String databaseName) {
    return getDataStore(databaseName).createQuery(typeParameterClass);
  }

  @Override
  public AggregationPipeline getAggregation(String databaseName, Query<T> query) {
    return getDataStore(databaseName).createAggregation(typeParameterClass).match(query);
  }

  @Override
  public void create(String databaseName, T entity) {
    getDataStore(databaseName).save(entity);
  }

  @Override
  public void delete(String databaseName, String id) {
    T entity = get(databaseName, id);
    getDataStore(databaseName).delete(entity);
  }
}

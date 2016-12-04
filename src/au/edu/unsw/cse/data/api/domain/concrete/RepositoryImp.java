package au.edu.unsw.cse.data.api.domain.concrete;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import au.edu.unsw.cse.data.api.domain.abstracts.Repository;

public abstract class RepositoryImp<T> implements Repository<T> {
	protected final Datastore datastore;
	final Class<T> typeParameterClass;

	public RepositoryImp(Datastore datastore, Class<T> typeParameterClass) {
		this.datastore = datastore;
		this.typeParameterClass = typeParameterClass;
	}

	@Override
	public T get(String id) {
		ObjectId objectId = new ObjectId(id);
		T entity = datastore.get(typeParameterClass, objectId);
		return entity;
	}

	@Override
	public List<T> getAll() {
		final Query<T> query = datastore.createQuery(typeParameterClass);
		final List<T> entities = query.asList();
		return entities;
	}

	@Override
	public void create(T entity) {
		datastore.save(entity);
	}

	@Override
	public void delete(String id) {
		T entity = get(id);
		datastore.delete(entity);
	}
}

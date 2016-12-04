package au.edu.unsw.cse.data.api.domain.concrete;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
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

	}

	@Override
	public Document get(String id, String clientId, String collection) {
		MongoCollection<Document> col = mongoDatabase.getCollection(collection);
		BasicDBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(id));
		query.put("clientId", clientId);
		Document dbObj = col.find(query).first();
		return dbObj;
	}

	@Override
	public List<Document> getAll(String clientId, String collection) {
		MongoCollection<Document> col = mongoDatabase.getCollection(collection);
		BasicDBObject query = new BasicDBObject();
		query.put("clientId", clientId);
		List<Document> objects = col.find(query).into(new ArrayList<Document>());
		return objects;
	}

}

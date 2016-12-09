package au.edu.unsw.cse.data.api.domain.concrete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import au.edu.unsw.cse.data.api.domain.abstracts.EntityRepository;
import au.edu.unsw.cse.data.api.domain.abstracts.RelationRepository;
import au.edu.unsw.cse.data.api.domain.entity.EntityRelation;

public class EntityRepositoryImp implements EntityRepository {

	private final MongoDatabase mongoDatabase;

	private final RelationRepository relationRepository;

	@Inject
	public EntityRepositoryImp(MongoDatabase mongoDatabase, RelationRepository relationRepository) {
		this.mongoDatabase = mongoDatabase;
		this.relationRepository = relationRepository;
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

	@Override
	public List<Document> get(String id, List<String> includes, String clientId, String collection) {
		List<EntityRelation> relations = relationRepository.get(id, includes);
		Map<String, List<ObjectId>> requiredEntities = new HashMap<String, List<ObjectId>>();
		relations.forEach(relation -> {
			for (int i = 0; i < relation.getPath().length; i++) {
				String itemType = relation.getTypes()[i + 1];
				String entityId = relation.getPath()[i];
				if (!requiredEntities.containsKey(itemType)) {
					requiredEntities.put(itemType, new ArrayList<ObjectId>() {
						{
							add(new ObjectId(entityId));
						}
					});
				} else {
					if (!requiredEntities.get(itemType).stream().filter(p -> p.toString().equalsIgnoreCase(entityId))
							.findAny().isPresent()) {
						requiredEntities.get(itemType).add(new ObjectId(entityId));
					}
				}
			}
		});
		// add source
		requiredEntities.put(relations.get(0).getTypes()[0], new ArrayList<ObjectId>() {
			{
				add(new ObjectId(id));
			}
		});
		List<Document> entities = new LinkedList<Document>();
		// get all entities
		requiredEntities.forEach((key, value) -> {
			MongoCollection<Document> col = mongoDatabase.getCollection(String.format("%s_%s", clientId, key));
			BasicDBObject inQuery = new BasicDBObject("$in", value.toArray());
			BasicDBObject query = new BasicDBObject("_id", inQuery);
			entities.addAll(col.find(query).into(new ArrayList<Document>()));
		});
		List<Document> results = new LinkedList<Document>();
		relations.forEach(relation -> {
			if (relation.getPath().length == 1) {
				Document source = results.stream().filter(doc -> doc.getObjectId("_id").toString().equalsIgnoreCase(id))
						.findAny()
						.orElse(entities.stream()
								.filter(entity -> entity.getObjectId("_id").toString().equalsIgnoreCase(id)).findFirst()
								.get());
				int indexOfSourceType = ArrayUtils.indexOf(relation.getTypes(), source.getString("type"));
				String detinationType = relation.getTypes()[indexOfSourceType + 1];
				Document destination = entities.stream().filter(entity -> entity.getObjectId("_id").toString()
						.equalsIgnoreCase(relation.getPath()[indexOfSourceType])).findFirst().get();
				if (source.containsKey(detinationType)) {
					Document[] docArray = source.get(detinationType, Document[].class);
					source.replace(detinationType, ArrayUtils.add(docArray, destination));
				} else {
					source.append(detinationType, new Document[] { destination });
				}
				if (!results.stream().filter(doc -> doc.getObjectId("_id").toString().equalsIgnoreCase(id)).findAny()
						.isPresent()) {
					results.add(source);
				}
				if (!results.stream().filter(doc -> doc.getObjectId("_id").toString()
						.equalsIgnoreCase(destination.getObjectId("_id").toString())).findAny().isPresent()) {
					results.add(destination);
				}
			} else {
				for (int i = 0; i < relation.getPath().length - 1; i++) {
					if (includes.contains(relation.getTypes()[i + 1])
							&& includes.contains(relation.getTypes()[i + 2])) {
						String sourceId = relation.getPath()[i];
						String destId = relation.getPath()[i + 1];

						Document source = results.stream()
								.filter(doc -> doc.getObjectId("_id").toString().equalsIgnoreCase(sourceId)).findAny()
								.orElse(entities.stream().filter(
										entity -> entity.getObjectId("_id").toString().equalsIgnoreCase(sourceId))
										.findFirst().get());
						String detinationType = relation.getTypes()[i + 2];
						if (!source.containsKey(detinationType)
								|| !Arrays.asList(source.get(detinationType, Document[].class)).stream()
										.filter(doc -> doc.get("_id").toString().equalsIgnoreCase(destId)).findAny()
										.isPresent()) {
							Document detDoc = entities.stream()
									.filter(doc -> doc.getObjectId("_id").toString().equalsIgnoreCase(destId))
									.findFirst().get();
							if (!source.containsKey(detinationType)) {
								source.append(detinationType, new Document[] { detDoc });
							} else {
								Document[] docArray = source.get(detinationType, Document[].class);
								source.replace(detinationType, ArrayUtils.add(docArray, detDoc));
							}
							if (!results.stream()
									.filter(doc -> doc.getObjectId("_id").toString().equalsIgnoreCase(sourceId))
									.findAny().isPresent()) {
								results.add(source);
							}
						}
					}
				}
			}
		});
		return results;
	}
}

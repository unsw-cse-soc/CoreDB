package au.edu.unsw.cse.data.api.domain.abstracts;

import java.util.List;
import java.util.Map;

import org.bson.Document;

public interface EntityRepository {

  void create(Document entity, String collection);

  Document get(String id, String clientId, String collection);

  List<Document> filter(String clientId, String collection, Map<String, String> fields);

  List<Document> get(String id, List<String> includes, String clientId, String collection);

  List<Document> getAll(String clientId, String collection);
}

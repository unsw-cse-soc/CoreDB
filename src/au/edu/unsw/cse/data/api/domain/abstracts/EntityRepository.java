package au.edu.unsw.cse.data.api.domain.abstracts;

import java.util.List;

import org.bson.Document;

public interface EntityRepository {

  void create(Document entitty, String collection);

  Document get(String id, String clientId, String collection);

  List<Document> getAll(String clientId, String collection);
}

package au.edu.unsw.cse.data.api.index.concrete;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import au.edu.unsw.cse.data.api.domain.entity.EntityRelation;
import au.edu.unsw.cse.data.api.index.abstracts.EntityIndex;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.inject.Inject;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;

public class ElasticSearchEntityIndex implements EntityIndex {

  private final TransportClient client;

  @Inject
  public ElasticSearchEntityIndex(TransportClient client) {
    this.client = client;
  }

  @Override
  public IndexResponse createIndex(Map<String, Object> entity, String databaseId, String entityType,
      String entityId) {
    IndexResponse response =
        client.prepareIndex(databaseId, entityType, entityId).setSource(entity).get();
    return response;
  }

  @Override
  public void update(String index, String type, String id, String fieldName,
      Map<String, Object> fields) throws ExecutionException, InterruptedException, IOException {
    UpdateRequest updateRequest = new UpdateRequest(index, type, id)
        .doc(jsonBuilder().startObject().field(fieldName, fields).endObject());
    client.update(updateRequest).get();
  }

  @Override
  public Map<String, Object> get(String index, String type, String id) {
    GetResponse response = client.prepareGet(index, type, id).get();
    return response.getSource();
  }

  @Override
  public void indexRelations(List<EntityRelation> relations, String indexName) {
    relations.stream().parallel().forEach(relation -> {
      if (relation.getPath().length == 1) {
        try {
          String parentId = relation.getSource();
          String parentType = relation.getEntityTypes()[0];
          String childType = relation.getEntityTypes()[1];
          String relationName = relation.getRelationNames()[0];
          Map<String, Object> obj = get(indexName, childType, relation.getPath()[0]);
          update(indexName, parentType, parentId, relationName, obj);
        } catch (ExecutionException | InterruptedException | IOException e) {
          e.printStackTrace();
        }
      } else {
        for (int i = relation.getPath().length - 1; i > 0; i--) {
          try {
            String type = relation.getEntityTypes()[i + 1];
            String parentId = relation.getPath()[i - 1];
            String relationName = relation.getPath()[i];
            String parentType = relation.getEntityTypes()[i];
            Map<String, Object> obj = get(indexName, type, relation.getPath()[i]);
            update(indexName, parentType, parentId, relationName, obj);
          } catch (ExecutionException | InterruptedException | IOException e) {
            e.printStackTrace();
          }
        }
        try {
          String sourceId = relation.getSource();
          String sourceType = relation.getPath()[0];
          String childId = relation.getPath()[0];
          String childType = relation.getPath()[1];
          String relationName = relation.getRelationNames()[0];
          Map<String, Object> obj = get(indexName, childType, childId);
          update(indexName, sourceType, sourceId, relationName, obj);
        } catch (ExecutionException | InterruptedException | IOException e) {
          e.printStackTrace();
        }
      }
    });
  }
}

package au.edu.unsw.cse.data.api.domain.concrete;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import com.mongodb.MongoClient;

import au.edu.unsw.cse.data.api.domain.abstracts.RelationRepository;
import au.edu.unsw.cse.data.api.domain.entity.EntityRelation;

public class RelationRepositoryImp extends RepositoryImp<EntityRelation>
    implements RelationRepository {

  @Inject
  public RelationRepositoryImp(Morphia morphia, MongoClient mongoClient) {
    super(morphia, mongoClient);
  }

  @Override
  public void create(String databaseName, EntityRelation entity) {
    Datastore dataStore = getDataStore("databaseName");
    List<EntityRelation> paths = dataStore.find(EntityRelation.class).field("path")
        .endsWithIgnoreCase(entity.getSource()).asList();
    List<EntityRelation> updatedPaths = new LinkedList<>();
    paths.forEach(path -> {
      EntityRelation newPath = new EntityRelation();
      newPath.setSource(path.getSource());
      newPath.setPath(ArrayUtils.add(Arrays.copyOf(path.getPath(), path.getPath().length),
          entity.getPath()[0]));
      newPath.setEntityTypes(
          ArrayUtils.add(Arrays.copyOf(path.getEntityTypes(), path.getEntityTypes().length),
              entity.getEntityTypes()[1]));
      newPath.setRelationNames(
          ArrayUtils.add(Arrays.copyOf(path.getRelationNames(), path.getRelationNames().length),
              path.getRelationNames()[0]));
      newPath.setRelationTypes(
          ArrayUtils.add(Arrays.copyOf(path.getRelationTypes(), path.getRelationTypes().length),
              path.getRelationTypes()[0]));
      updatedPaths.add(newPath);
    });
    updatedPaths.add(entity);
    dataStore.save(updatedPaths);
  }

  @Override
  public List<EntityRelation> get(String databaseName, String source, List<String> types) {
    String[] typesArray = new String[types.size()];
    typesArray = types.toArray(typesArray);
    final Query<EntityRelation> query =
        getDataStore("databaseName").createQuery(EntityRelation.class).field("source")
            .equalIgnoreCase(source).filter("types in", typesArray);
    List<EntityRelation> relations = query.asList();
    return relations;
  }
}

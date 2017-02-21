package au.edu.unsw.cse.data.api.domain.abstracts;

import java.util.List;

import au.edu.unsw.cse.data.api.domain.entity.EntityRelation;

public interface RelationRepository extends Repository<EntityRelation> {
  List<EntityRelation> get(String databaseName, String source, List<String> types);
}

package au.edu.unsw.cse.data.api.domain.entity;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;

@Entity("relations")
public class EntityRelation extends au.edu.unsw.cse.data.api.domain.entity.Entity {
  private String source;
  @Embedded
  private String[] path;
  @Embedded
  private String[] entityTypes;
  @Embedded
  private String[] relationNames;
  @Embedded
  private String[] relationTypes;

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String[] getPath() {
    return path;
  }

  public void setPath(String[] path) {
    this.path = path;
  }

  public String[] getEntityTypes() {
    return entityTypes;
  }

  public void setEntityTypes(String[] types) {
    this.entityTypes = types;
  }

  public String[] getRelationNames() {
    return relationNames;
  }

  public void setRelationNames(String[] relationNames) {
    this.relationNames = relationNames;
  }

  public String[] getRelationTypes() {
    return relationTypes;
  }

  public void setRelationTypes(String[] relationTypes) {
    this.relationTypes = relationTypes;
  }

}

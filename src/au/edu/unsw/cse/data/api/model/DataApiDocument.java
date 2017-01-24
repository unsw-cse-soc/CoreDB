package au.edu.unsw.cse.data.api.model;

import org.bson.Document;

public class DataApiDocument extends Document {

  /**
   *
   */
  private static final long serialVersionUID = -5136857427184835926L;

  @Override
  public String toJson() {
    if (this.containsKey("clientId")) {
      this.remove("clientId");
    }
    if (this.containsKey("_id")) {
      String id = this.getObjectId("_id").toString();
    }
    return super.toJson();
  }

}

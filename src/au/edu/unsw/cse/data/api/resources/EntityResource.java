package au.edu.unsw.cse.data.api.resources;

import au.edu.unsw.cse.data.api.domain.abstracts.DatabaseRepository;
import au.edu.unsw.cse.data.api.domain.entity.Database;
import au.edu.unsw.cse.data.api.index.abstracts.EntityIndex;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.bson.Document;

import com.fasterxml.jackson.databind.node.ObjectNode;

import au.edu.unsw.cse.data.api.domain.abstracts.EntityRepository;
import au.edu.unsw.cse.data.api.security.AppUser;
import au.edu.unsw.cse.data.api.security.Secured;
import au.edu.unsw.cse.data.api.security.UserInfo;

@Path("entity")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EntityResource {

  private final EntityRepository entityRepository;
  private final DatabaseRepository databaseRepository;
  private final EntityIndex entityIndex;

  @Inject
  public EntityResource(EntityRepository entityRepository, EntityIndex entityIndex,
      DatabaseRepository databaseRepository) {
    this.entityRepository = entityRepository;
    this.entityIndex = entityIndex;
    this.databaseRepository = databaseRepository;
  }

  @POST
  @Path("/schema/{name}")
  @Secured
  public Response createSchema(@AppUser UserInfo userInfo, @PathParam("name") String name,
      ObjectNode schema) {
    Document newSchema = new Document();
    newSchema.append("schema_name", name);
    schema.fields().forEachRemaining(field -> {
      newSchema.append(field.getKey(), field.getValue().asText());
    });
    newSchema.append("createdBy", userInfo.getName());
    newSchema.append("updatedBy", userInfo.getName());
    newSchema.append("clientId", userInfo.getClientId());
    entityRepository.create(newSchema, "schemas");
    return Response.ok().build();
  }

  @POST
  @Path("/{database}/{type}")
  @Secured
  public Response create(@AppUser UserInfo userInfo, @PathParam("type") String type,
      @PathParam("database") String databaseName, ObjectNode entity) {
    Database database = databaseRepository.get(userInfo.getClientId(), databaseName);
    if (database == null) {
      // return error
    }
    Document document = new Document();
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> mapObject = mapper.convertValue(entity, Map.class);
    // create mongo object
    document.putAll(mapObject);
    document.append("type", type);
    document.append("createdBy", userInfo.getName());
    document.append("updatedBy", userInfo.getName());
    document.append("clientId", userInfo.getClientId());
    document.append("databaseId", database.getId());
    // store mongo object
    entityRepository.create(document, String.format("%s_%s", userInfo.getClientId(), type));
    String insertedId = document.getObjectId("_id").toString();
    entityIndex.createIndex(mapObject, database.getId(), type, insertedId



    );
    entity.fields().forEachRemaining(field -> {
    });
    return Response.ok(document).build();
    // List<Document> schemas = entityRepository.filter(userInfo.getClientId(), "schemas",
    // Stream
    // .of(new String[] {"clientId", userInfo.getClientId()},
    // new String[] {"schema_name", type})
    // .collect(Collectors.toMap(s -> s[0], s -> s[1])));
    // if (schemas == null || schemas.isEmpty()) {
    // return Response.status(Status.BAD_REQUEST).build();
    // } else {
    // Document schema = schemas.get(0);
    // entity.fields().forEachRemaining(field -> {
    // // switch (schema.getString("")) {
    // // case value:
    //
    // // break;
    //
    // // default:
    // // break;
    // // }
    // document.append(field.getKey(), field.getValue().asText());
    // });
    // document.append("type", type);
    // document.append("createdBy", userInfo.getName());
    // document.append("updatedBy", userInfo.getName());
    // document.append("clientId", userInfo.getClientId());
    // entityRepository.create(document, String.format("%s_%s", userInfo.getClientId(), type));
    // return Response.ok(document).build();
  }


  @GET
  @Path("/get/{type}/{id}")
  @Secured
  public Response get(@AppUser UserInfo userInfo, @PathParam("type") String type,
      @PathParam("id") String id, @QueryParam("include") List<String> includes) {
    List<Document> documents = entityRepository.get(id, includes, userInfo.getClientId(),
        String.format("%s_%s", userInfo.getClientId(), type));
    return Response.ok(documents).build();
  }

  @GET
  @Path("/list/{type}")
  @Secured
  public Response getAll(@AppUser UserInfo userInfo, @PathParam("type") String type) {
    List<Document> documents = entityRepository.getAll(userInfo.getClientId(),
        String.format("%s_%s", userInfo.getClientId(), type));
    return Response.ok(documents).build();
  }
}

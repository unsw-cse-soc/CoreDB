package au.edu.unsw.cse.data.api.resources;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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

  @Inject
  public EntityResource(EntityRepository entityRepository) {
    this.entityRepository = entityRepository;
  }

  @POST
  @Path("/create/{type}")
  @Secured
  public Response create(@AppUser UserInfo userInfo, @PathParam("type") String type,
      ObjectNode entity) {
    Document document = new Document();
    entity.fields().forEachRemaining(key -> {
      document.append(key.getKey(), key.getValue().asText());
    });
    document.append("createdBy", userInfo.getName());
    document.append("updatedBy", userInfo.getName());
    document.append("clientId", userInfo.getClientId());
    entityRepository.create(document, String.format("%s_%s", userInfo.getClientId(), type));
    return Response.ok(document).build();
  }

  @GET
  @Path("/get/{type}/{id}")
  @Secured
  public Response get(@AppUser UserInfo userInfo, @PathParam("type") String type,
      @PathParam("id") String id) {
    Document document = entityRepository.get(id, userInfo.getClientId(),
        String.format("%s_%s", userInfo.getClientId(), type));
    return Response.ok(document).build();
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

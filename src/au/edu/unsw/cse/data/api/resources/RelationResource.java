package au.edu.unsw.cse.data.api.resources;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.collections4.IteratorUtils;
import org.mongodb.morphia.query.Query;

import au.edu.unsw.cse.data.api.domain.abstracts.RelationRepository;
import au.edu.unsw.cse.data.api.domain.entity.EntityRelation;
import au.edu.unsw.cse.data.api.index.abstracts.EntityIndex;
import au.edu.unsw.cse.data.api.model.CreateRelationBindingModel;
import au.edu.unsw.cse.data.api.security.Secured;

@Path("relation")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RelationResource {

  private final RelationRepository relationRepository;
  private final EntityIndex entityIndex;

  @Inject
  public RelationResource(RelationRepository relationRepository, EntityIndex entityIndex) {
    this.relationRepository = relationRepository;
    this.entityIndex = entityIndex;
  }

  @POST
  @Path("/{database}/{sourceType}/{destinationType}")
  @Secured
  public Response create(@PathParam("database") String database,
      @PathParam("sourceType") String sourceType,
      @PathParam("destinationType") String destinationType, CreateRelationBindingModel model) {
    EntityRelation newRelation = new EntityRelation();
    newRelation.setPath(new String[] {model.getDestination()});
    newRelation.setSource(model.getSource());
    newRelation.setEntityTypes(new String[] {sourceType, destinationType});
    newRelation.setRelationNames(new String[] {model.getName()});
    newRelation.setEntityTypes(new String[] {model.getType()});
    relationRepository.create(newRelation);
    Query<EntityRelation> query =
        relationRepository.getQueryable().field("path").endsWithIgnoreCase(newRelation.getSource());
    List<EntityRelation> mainPath =
        IteratorUtils.toList(relationRepository.getAggregation(query).unwind("path")
            .group("_id",
                org.mongodb.morphia.aggregation.Group.grouping("len",
                    org.mongodb.morphia.aggregation.Accumulator.accumulator("$sum", 1)))
            .sort(org.mongodb.morphia.query.Sort.descending("len")).out(EntityRelation.class));
    entityIndex.indexRelations(mainPath, database);
    return Response.ok().build();
  }
}

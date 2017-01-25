package au.edu.unsw.cse.data.api.resources;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import au.edu.unsw.cse.data.api.domain.abstracts.RelationRepository;
import au.edu.unsw.cse.data.api.domain.entity.EntityRelation;
import au.edu.unsw.cse.data.api.model.CreateRelationBindingModel;
import au.edu.unsw.cse.data.api.security.Secured;

@Path("relation")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RelationResource {

	private RelationRepository relationRepository;

	@Inject
	public RelationResource(RelationRepository relationRepository) {
		this.relationRepository = relationRepository;
	}

	@POST
	@Path("/create/{sourceType}/{destinationType}")
	@Secured
	public Response create(@PathParam("sourceType") String sourceType,
			@PathParam("destinationType") String destinationType, CreateRelationBindingModel model) {
		EntityRelation newRelation = new EntityRelation();
		newRelation.setPath(new String[] { model.getDestination() });
		newRelation.setSource(model.getSource());
		newRelation.setTypes(new String[] { sourceType, destinationType });
		relationRepository.create(newRelation);
		return Response.ok().build();
	}
}

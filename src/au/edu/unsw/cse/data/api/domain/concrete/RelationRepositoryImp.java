package au.edu.unsw.cse.data.api.domain.concrete;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import au.edu.unsw.cse.data.api.domain.abstracts.RelationRepository;
import au.edu.unsw.cse.data.api.domain.entity.EntityRelation;

public class RelationRepositoryImp extends RepositoryImp<EntityRelation> implements RelationRepository {

	@Inject
	public RelationRepositoryImp(Datastore datastore) {
		super(datastore, EntityRelation.class);
	}

	@Override
	public void create(EntityRelation entity) {
		List<EntityRelation> paths = this.datastore.find(EntityRelation.class).field("path")
				.endsWithIgnoreCase(entity.getSource()).asList();
		List<EntityRelation> updatedPaths = new LinkedList<EntityRelation>();
		paths.forEach(path -> {
			EntityRelation newPath = new EntityRelation();
			newPath.setSource(path.getSource());
			newPath.setPath(ArrayUtils.add(Arrays.copyOf(path.getPath(), path.getPath().length), entity.getPath()[0]));
			newPath.setTypes(
					ArrayUtils.add(Arrays.copyOf(path.getTypes(), path.getTypes().length), entity.getTypes()[1]));
			updatedPaths.add(newPath);
		});
		updatedPaths.add(entity);
		this.datastore.save(updatedPaths);
	}

	@Override
	public List<EntityRelation> get(String source, List<String> types) {
		String[] typesArray = new String[types.size()];
		typesArray = types.toArray(typesArray);
		final Query<EntityRelation> query = datastore.createQuery(EntityRelation.class).field("source")
				.equalIgnoreCase(source).filter("types in", typesArray);
		List<EntityRelation> relations = query.asList();
		return relations;
	}
}

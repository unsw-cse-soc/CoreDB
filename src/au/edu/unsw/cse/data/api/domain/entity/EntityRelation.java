package au.edu.unsw.cse.data.api.domain.entity;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;

@Entity("relations")
public class EntityRelation extends au.edu.unsw.cse.data.api.domain.entity.Entity {
	private String source;
	@Embedded
	private String[] path;
	@Embedded
	private String[] types;

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

	public String[] getTypes() {
		return types;
	}

	public void setTypes(String[] types) {
		this.types = types;
	}

}

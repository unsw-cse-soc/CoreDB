package au.edu.unsw.cse.data.api.domain.abstracts;

import au.edu.unsw.cse.data.api.domain.entity.Database;

public interface DatabaseRepository extends Repository<Database> {

  Database get(String clientId, String databaseName);
}

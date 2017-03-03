/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.controller;

import coredb.sql.SQLStatement;
import coredb.unit.Action;
import coredb.unit.CRUD;
import coredb.unit.EntityInstance;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @author vmc
 */
public class EntityControllerBase extends EntityControllerCommon implements IEntityControllerBase {
	//protected DatabaseConnection databaseConnection = null;

	// checked by vmc @553
	public EntityControllerBase(String pathToFile) throws SQLException {
		super(pathToFile);
		/*
		ConfigurationValues.init(pathToFile);
		this.databaseConnection = new DatabaseConnection(
			ConfigurationValues.dbURL,
			ConfigurationValues.databaseType,
			ConfigurationValues.databaseDriver,
			ConfigurationValues.databaseName,
			ConfigurationValues.user,
			ConfigurationValues.password);
		 */
	}

	/**
	 * This constructor constructs an EntityController.
	 * 
	 * @param dbURL DB connection URL
	 * @param databaseType the type of DB server 
	 * @param databaseDriver the DB driver
	 * @param databaseName the name of database 
	 * @param user user name to connect database
	 * @param password user password to connect database
	 * @throws SQLException
	 */
	// checked by vmc @108
	public EntityControllerBase(String dbURL, String databaseType,
			String databaseDriver, String databaseName, String user,
			String password) throws SQLException {
		super(dbURL, databaseType, databaseDriver, databaseName, user, password);
		/*
		this.databaseConnection = 
		new DatabaseConnection(dbURL, databaseType, databaseDriver, databaseName, user, password);
		 */
	}

	/**
	 *This method saves EntityInstance
	 *
	 * @param entity the EntityInstance need to save
	 * @return the changeSetNumber of this transaction
	 */
	// unchecked
	public synchronized <T extends EntityInstance> boolean saveEntity(T entity) {
		String SQL = entity.makeSQLOnIdentifiableAttributes(databaseConnection);
		List<EntityInstance> entities = readEntities(entity.getDynaClass().getName(),SQL);

		if (entities.size() == 0)		return databaseConnection.create((EntityInstance)entity);
		else if (entities.size() == 1)	return databaseConnection.update((EntityInstance)entity);
		else {
			System.out.println("vmc:saveEntity something not right");
			return false;
		}
	}

	/**
	 * This method saves a list of EntityInstances
	 * 
	 * @param entities the list of EntityInstances need to save
	 * @return the changeSetNumber of this transaction
	 */
	// unchecked
	public <T extends EntityInstance> boolean saveEntities(List<T> entities) {
		boolean allsaved = true;
		for (@SuppressWarnings("unchecked")
			 Iterator<EntityInstance> i=(Iterator<EntityInstance>) entities.iterator(); i.hasNext(); ) {
			allsaved &= saveEntity(i.next());
		}
		return allsaved;
	}

    /**
     * This function creates an entity in the database.
	 * 
	 * @param entity the entity that will be created in the database.
     * @return true if successful, otherwise false.
     */
	// CHECKED BY VMC @324
	public <T extends EntityInstance> boolean createEntity(T entity) {
		return databaseConnection.create((EntityInstance)entity);
	}

    /**
     * This function creates a list of entities in the database.
     * 
     * @param entities the list of entities that will be created in the database.
     * @return true if successful, otherwise false.
     */
	// CHECKED BY VMC @324
	public boolean createEntities(List<? extends EntityInstance> entities) {
        return databaseConnection.create(entities);
	}
    
    /**
     * This function reads a list of entities from the database.
     *
     * @param tableName the name of the table
     * @param sql the sql statement used to read EntityInstances
	 * @return returnns list of entity instances.
     */
	// CHECKED BY VMC @712
    public List<EntityInstance> readEntities(String tableName, SQLStatement sql) {
		return readEntities(tableName, sql == null ? "" : sql.toString().trim());
	}

	/**
     * This function reads a list of entities from the database.
     *
     * @param tableName the name of the table
	 * @param cshString the sql statement used to read EntityInstances
     * @return returnns list of entity instances.
	 */
	// CHECKED BY VMC @712
    public List<EntityInstance> readEntities(String tableName, String cshString) {
		return databaseConnection.read(tableName,cshString);
    }

    /**
     * This function updates an entity in the database.
     * 
     * @param entity the entity that will be updated in the database.
     * @return returns true if successful, otherwise false.
     */
	// CHECKED BY VMC @324
	public <T extends EntityInstance> boolean updateEntity(T entity) {
		return databaseConnection.update(entity);
	}

    /**
     * This function updates a list of entities in the database.
     *
     * @param entities the list of entities that will be updated in the database.
     * @return returns true if successful, otherwise false.
     */
	// CHECKED BY VMC @324
	public boolean updateEntities(List<? extends EntityInstance> entities) {
        return databaseConnection.update(entities);
	}

    /**
     * This function deletes an entity from the database.
     * 
     * @param entity the entity that will be deleted from the database.
     * @return returns true if successful, otherwise false.
     */
	// CHECKED BY VMC @324
	public <T extends EntityInstance> boolean deleteEntity(T entity) {
		return databaseConnection.delete(entity);
	}

    /**
     * This function deletes a list of entities from the database.
     *
     * @param entities the list of entities that will be deleted from the database.
     * @return returns true if successful, otherwise false.
     */
	// CHECKED BY VMC @324
	public boolean deleteEntities(List<? extends EntityInstance> entities) {
        return databaseConnection.delete(entities);
	}

	/**
	 * This method processes a list of Actions
     * 
	 * @param actions a list of Actions need to process
	 */
	//IMPLEMENTED BY SEAN
	protected void bulkActions(List<Action> actions) {
		for (Action action : actions) {
			if (CRUD.CREATE.equals(action.getActionType())) {
				this.createEntity(action.getEntityInstance());
			} else if (CRUD.READ.equals(action.getActionType())) {
				this.updateEntity(action.getEntityInstance());
			} else if (CRUD.UPDATE.equals(action.getActionType())) {
				this.updateEntity(action.getEntityInstance());
			} else if (CRUD.DELETE.equals(action.getActionType())) {
				this.deleteEntity(action.getEntityInstance());
			} else {
				System.err.println("YOUR CRUD ACTION IS NOT SUPPORTED");
				System.exit(0);
			}
		}
		// bugs fix
        /**
         * code might be used in the future
         * IMPLEMENTED BY SEAN
         */
        /*
        List<EntityInstance> createInstances = new LinkedList<EntityInstance>();
        List<EntityInstance> updateInstances = new LinkedList<EntityInstance>();

        for(Action action : actions){
            if (CRUD.CREATE.equals(action.getActionType())) {createInstances.add(action.getEntityInstance());}
            else if(CRUD.READ.equals(action.getActionType()) || CRUD.UPDATE.equals(action.getActionType())
                                                             || CRUD.DELETE.equals(action.getActionType())){
                updateInstances.add(action.getEntityInstance());
            }else {
				System.err.println("YOUR CRUD ACTION IS NOT SUPPORTED");
				System.exit(0);
			}
        }

        ecb.createEntities(createInstances);
        ecb.updateEntities(updateInstances);
        */
	}
}
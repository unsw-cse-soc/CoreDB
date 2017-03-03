/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.controller;

import java.util.List;

import coredb.sql.SQLStatement;
import coredb.unit.AttributeClass;
import coredb.unit.ClassToTables;
import coredb.unit.EntityInstance;
/**
 *
 * @author vmc
 */
public interface IEntityControllerTracing extends IEntityControllerCommon {
	

	/**
	 * This method creates EntityInstance, inserts a data row into database
	 *
	 * @param <T>
	 * @param userId the ID of user who launched this transaction
	 * @param entity the EntityInstance need to create
	 * @return the changeSetNumber of this transaction
	 */
	public <T extends EntityInstance> int createEntityWithTracing(String userId, T entity);
	
	/**
     * Create entities: This method inserts a list of data rows into database.
     * 
     * @param entities the list of entities that will be created in the database.
     * @param userId the id of current user
     * @return returns the changeSetNumber of this create transaction
     */
	public int createEntitiesWithTracing(String userId, List<? extends EntityInstance> entities);
	
	/**
     * Read entity instances from DB (Tracing). This method reads data rows
     * from DB based on given conditional statement
     * 
	 * @param tableName the name of the table
     * @param sql the sql statement used to read EntityInstances
	 * @param userId the id of current user
     * @return returns a list of Entities
     */
	public List<EntityInstance> readEntitiesWithTracing(String userId, String tableName, SQLStatement sql);
	
	/**
     * Read entity instances from DB (Tracing). This method reads data rows
     * from DB based on given conditional statement
     *
	 * @param tableName the name of the table
     * @param sql the sql statement used to read EntityInstances
	 * @param userId the id of current user
     * @return returns a list of Entities
     */
	public List<EntityInstance> readEntitiesWithTracing(String userId, String tableName, String sql);
	
	/**
     * Update an entity: This method updates a data row to database.
     *
	 * @param <T>
	 * @param entity the entity that will be updated in the database.
     * @param userId the id of user
     * @return returns the changeSetNumber of this transaction
     */
	public <T extends EntityInstance> int updateEntityWithTracing(String userId, T entity);
	
	/**
     * Update entities: This method updates a list of data rows to database.
     *
     * @param entities the list of entities that will be created in the database.
     * @param userId the id of current user
     * @return returns the changeSetNumber of this transaction
     */
	public int updateEntitiesWithTracing(String userId, List<? extends EntityInstance> entities);
	
	/**
     * Delete entity from database. This method deletes data row from database.
     * 
	 * @param <T>
	 * @param entity the entity that will be deleted from the database.
     * @param userId the id of current user
     * @return returns the changeSetNumber of this transaction
     */
	public <T extends EntityInstance> int deleteEntityWithTracing(String userId, T entity);
	
	 /**
     * Delete entities from database. This method deletes a list of data rows from database.
     * 
     * @param entities the list of entities that will be deleted from the database.
     * @param userId the id of current user
     * @return returns the changeSetNumber of this transaction
     */
    public int deleteEntitiesWithTracing(String userId, List<? extends EntityInstance> entities);
    
    /**
	 *This method traces EntityInstances of the table whose name is tableName
     * 
	 * @param tableName the name of the table
	 * @param userId the id of current user
	 * @return returns historical data (a list of EntityInstances include version details)
	 */
	public List<EntityInstance> traceEntities( String userId, String tableName);
	
	/**
	 *This method traces the latest records from database 
	 *
	 * @param tableName the name of the table will be traced
	 * @param sql the sql statement used by tracing
	 * @param userId the Id of user who launched this transaction
	 * @return the latest records (a list of EntityInstances include version details) from database 
	 */
	public List<EntityInstance> traceEntities(String tableName, SQLStatement sql, String userId);
	
	/**
	 * This method traces historical data back
     * 
	 * @param tableName the name of the table
	 * @param entityMappingId the mappingId stored mappingTable related to EntityInstance of baseTable 
	 * @param minCSN the min csn of this entity
	 * @param currentCSN the csn user want to trace
	 * @param maxCSN the max csn of this entity
	 * @param userId the id of current user
	 * @return returns historical data (a list of EntityInstances include version details) based on given parameters
	 */
	public List<EntityInstance> traceEntities(String tableName, String entityMappingId, int minCSN,int currentCSN,int maxCSN,String userId);
	
	/**
	 * This method traces historical(csn range is between startCSN and endCSN) data back
	 * @param tableName the name of table which need to read
	 * @param entityMappingId the mappingId stored mappingTable related to EntityInstance of baseTable 
	 * @param startCSN the lower csn of range
	 * @param endCSN the upper csn of range
	 * @param userId the id of current user
	 * @return returns historical data (a list of EntityInstances include version details) based on given parameters
	 */
	public List<EntityInstance> traceEntities(String tableName, String entityMappingId, int startCSN,int endCSN,String userId);
	
	/**
	 * This method creates a mapping tableName (tracing)
	 *
	 * @param leftTable reference tableName 1
	 * @param leftAcl reference attributes from table1
	 * @param rightTable reference tableName 2
     * @param rightAcl reference attributes from table2
	 * @return mapping tableName for the relation between table1 and table2
	 */
	public ClassToTables generateMappingTableWithTracing(ClassToTables leftTable, AttributeClass[] leftAcl, ClassToTables rightTable, AttributeClass[] rightAcl);
	
	/**
     * This method reads the latest changesetnumber from DB
     * @return returns the latest changesetnumber
     */
	public int getLatestChangeSetNumber();
	
	/**
	 * This method will generate a new changeSetNumber
	 *
	 * @param userId the id of current user
	 * @return returns the new created changeSetNumber
	 */
	public int generateLatestChangeSetNumber(String userId);
}

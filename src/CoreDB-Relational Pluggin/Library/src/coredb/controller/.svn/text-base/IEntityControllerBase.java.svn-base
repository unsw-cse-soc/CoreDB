/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.controller;

import coredb.sql.SQLStatement;
import coredb.unit.EntityInstance;
import java.util.List;

/**
 *
 * @author vmc
 */
public interface IEntityControllerBase extends IEntityControllerCommon {

	/**
     * This function creates an entity in the database.
	 *
     * @param <T> 
     * @param entity the entity that will be created in the database.
     * @return true if successful, otherwise false.
     */
	public <T extends EntityInstance> boolean createEntity(T entity);

	/**
     * This function creates a list of entities in the database.
     *
     * @param entities the list of entities that will be created in the database.
     * @return true if successful, otherwise false.
     */
	public boolean createEntities(List<? extends EntityInstance> entities);

	 /**
     * This function reads a list of entities from the database.
     *
     * @param tableName the name of the table
     * @param sql the sql statement used to read EntityInstances
	 * @return returnns list of entity instances.
     */
	public List<EntityInstance> readEntities(String tableName, SQLStatement sql);

	/**
     * This function reads a list of entities from the database.
     *
     * @param tableName the name of the table
	 * @param cshString the sql statement used to read EntityInstances
     * @return returnns list of entity instances.
	 */
	public List<EntityInstance> readEntities(String tableName, String cshString);
    
	/**
     * This function updates an entity in the database.
     *
     * @param <T>
     * @param entity the entity that will be updated in the database.
     * @return returns true if successful, otherwise false.
     */
	public <T extends EntityInstance> boolean updateEntity(T entity);

	/**
     * This function updates a list of entities in the database.
     *
     * @param entities the list of entities that will be updated in the database.
     * @return returns true if successful, otherwise false.
     */
	public boolean updateEntities(List<? extends EntityInstance> entities);

    /**
     * This function deletes an entity from the database.
     *
     * @param <T>
     * @param entity the entity that will be deleted from the database.
     * @return returns true if successful, otherwise false.
     */
	public <T extends EntityInstance> boolean deleteEntity(T entity);
    
	/**
     * This function deletes a list of entities from the database.
     *
     * @param entities the list of entities that will be deleted from the database.
     * @return returns true if successful, otherwise false.
     */
	public boolean deleteEntities(List<? extends EntityInstance> entities);
}
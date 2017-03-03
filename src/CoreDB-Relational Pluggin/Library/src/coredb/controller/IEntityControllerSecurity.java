/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.controller;

import java.util.List;

import coredb.database.DatabaseConnection;
import coredb.security.ResultSecurity;
import coredb.security.ResultTokens;
import coredb.sql.SQLStatement;
import coredb.unit.AttributeClass;
import coredb.unit.ClassToTables;
import coredb.unit.EntityInstance;
import coredb.unit.EntitySecurityClass;

/**
 *
 * @author vmc
 */
public interface IEntityControllerSecurity extends IEntityControllerCommon {
	
	/**
	 * This method deploys all system built-in tables required by security. 
	 *
	 * @param userTokens the list of tokens
	 * @param classToTablesList the list of table need to be created
	 * @param dropTablesFirst if it is true, the existed tables in database will be replaced by the new ones.
	 * @param continueOnError if it is true, errors caused by the deployment will be ignored
	 * @return the status of the deployment, returns true if action is successful otherwise return false
	 */
	public boolean deployDefinitionListWithSecurity(List<EntityInstance> userTokens, List<ClassToTables> classToTablesList,boolean dropTablesFirst, boolean continueOnError);

	/**
	 * This method creates a new user.
	 *
	 * @param creator the executer of this action
	 * @param user the user Instance will be created
	 * @return returns the created user Instance
	 */
	public EntityInstance createUser(EntityInstance creator, EntityInstance user);

	/**
	 * This method retrieves a existed user instance in database. 
	 *
	 * @param user the user Instance (not included user id) will be retrieved
	 * @return the first item of a list of entities
	 */
	public EntityInstance readUser(EntityInstance user);

	/**
	 * This method retrieves a existed user instance in database. 
	 *
	 * @param userName the account name
	 * @param userPassword the user password
	 * @return the user Instance (stored in database)
	 */
	public EntityInstance readUser(String userName, String userPassword);
	
	/**
	 * This method creates a data row(EntityInstance) into database with security checking
	 * 
	 * @param creator the executer of this action
	 * @param entity the EntityInstance will be created
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	public ResultSecurity createEntityWithSecurity(EntityInstance creator, EntityInstance entity);

	/**
	 * This method creates a data row(EntityInstance) into database with security checking
	 * 
	 * @param creator the executer of this action
	 * @param entity the EntityInstance will be created
	 * @param tokens the tokens will be assigned to the new EntityInstance
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	public ResultSecurity createEntityWithSecurity(EntityInstance creator, EntityInstance entity, List<EntityInstance> tokens);
	
	/**
	 * This method creates a list of data rows(EntityInstances) into database with Security checking
	 * 
	 * @param creator the executer of this action
	 * @param entities the list of EntityInstances will be created
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	public ResultSecurity createEntitiesWithSecurity(EntityInstance creator, List<EntityInstance> entities);

	/**
	 * This method creates a list of data rows(EntityInstances) into database with Security checking
	 * 
	 * @param creator the executer of this action
	 * @param entities the list of EntityInstances will be created
	 * @param tokens the tokens will be assigned to the new EntityInstances
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	public ResultSecurity createEntitiesWithSecurity(EntityInstance creator, List<EntityInstance> entities, List<EntityInstance> tokens);
	
	/**
	 * This method reads a list of data rows(EntityInstances) from Database with Security checking
	 * 
	 * @param reader the executer of this action
	 * @param tableName the name of the table 
	 * @param sql the SQLStatement object used to read
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	public ResultSecurity readEntitiesWithSecurity(EntityInstance reader, String tableName, SQLStatement sql);
	
	 /**
     * This method updates a data row(EntityInstance) of database with Security checking
     * 
     * @param updator the executer of this action
     * @param entity the EntityInstance will be updated
     * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
     */
	public ResultSecurity updateEntityWithSecurity(EntityInstance updator, EntityInstance entity);
	
	/**
	 * This method updates a list of data rows(EntityInstances) of database with Security checking
	 * 
	 * @param updator the executer of this action
	 * @param entities the list of EntityInstances will be updated
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	public ResultSecurity updateEntitiesWithSecurity(EntityInstance updator, List<EntityInstance> entities) ;
	
	/**
     * This method deletes a data row(EntityInstance) from database with Security checking.
     * 
     * @param deletor the executer of this action
     * @param entity the EntityInstance will be deleted
     * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
     */
	public ResultSecurity deleteEntityWithSecurity(EntityInstance deletor, EntityInstance entity);

	/**
	 * This method deletes a list of data rows(EntityInstances) from database with Security checking
	 * 
	 * @param deletor the executer of this action
	 * @param entities the list of EntityInstances will be deleted
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	public ResultSecurity deleteEntitiesWithSecurity(EntityInstance deletor, List<EntityInstance> entities);

	/**
     * This method drops all tables from the database.
	 *
     */
	public void dropAllTables();

	/**
     * This method drops all tables from the database.
     * @param user the executor of dropping tables
     */
	public void dropAllTables(EntityInstance user);

	/**
     * This method drops a list of tables from the database.
     * @param tableNames the given list of table Names
     */
	public void dropTables(List<String> tableNames);

	/**
     * This method drops a list of tables from the database.
     * @param user the executor of dropping tables
     * @param tableNames the given list of table Names
     */
	public void dropAllTables(EntityInstance user, List<String> tableNames);
	
	/**
	 * This method adds a group permission as a list of tokens to a specified user
	 * 
	 * @param creator the executer of this action
	 * @param receiver the user who will be given by a group permission
	 * @param group the group name of the tokens
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	public ResultSecurity addUserToGroup(EntityInstance creator, EntityInstance receiver, String group);

	/**
	 * This method removes a group permission as a list of tokens from a specified user
	 * 
	 * @param deletor the executer of this action
	 * @param deletee the user whose group tokens will be removed
	 * @param group the group name of the tokens
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	public ResultSecurity removeUserFromGroup(EntityInstance deletor, EntityInstance deletee, String group);

	/**
	 * This method adds a list of tokens onto a list of Columns
	 *
	 * @param creator the executer of this action
	 * @param tableName the name of table
	 * @param columns the list of columns
	 * @param tokens the list of tokens
	 * @return  the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	public ResultSecurity addTokensToColumns(EntityInstance creator, String tableName, AttributeClass[] columns, List<EntityInstance> tokens);

	/**
	 * This method removes a list of tokens from a list of Columns
	 *
	 * @param deletor the executer of this action
	 * @param tableName the name of table
	 * @param columns the list of columns
	 * @param tokens the list of tokens
	 * @return  the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	public ResultSecurity removeTokensFromColumns(EntityInstance deletor, String tableName, AttributeClass[] columns, List<EntityInstance> tokens);	

	/**
	 * This method adds a list of tokens onto a list of data rows
	 * 
	 * @param creator the executer of this action
	 * @param rows the list of data rows
	 * @param tokens the list of tokens
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	public ResultSecurity addTokensToRows(EntityInstance creator, List<EntityInstance> rows, List<EntityInstance> tokens);

	/**
	 * This method removes a list of tokens from a list of data rows
	 * 
	 * @param deletor the executer of this action
	 * @param rows the list of data rows
	 * @param tokens the list of tokens
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	public ResultSecurity removeTokensFromRows(EntityInstance deletor, List<EntityInstance> rows, List<EntityInstance> tokens) ;
	
	/**
	 * This method retrieves all tokens belonging to given user
	 * 
	 * @param reader the executer of this action
	 * @param user the given user
	 * @return returns all tokens belonging to user. Tokens will be categorized by grant and deny ones.
	 */
    public ResultTokens getTokensByUser(EntityInstance reader, EntityInstance user);
	
	/**
	 * This method retrieves all tokens belonging to given row
	 * 
	 * @param reader the given reader
	 * @param row the given row
	 * @return returns all tokens belonging to row. Tokens will be categorized by grant and deny ones.
	 */
    public ResultTokens getTokensByRow(EntityInstance reader, EntityInstance row);
	
	/**
	 * This method retrieves all tokens belonging to given column
	 * 
	 * @param reader the given reader
	 * @param tableName the name of table which owns the given column
	 * @param column the given column
	 * @return returns all tokens belonging to column. Tokens will be categorized by grant and deny ones.
	 */
    public ResultTokens getTokensByColumn(EntityInstance reader, String tableName, AttributeClass column);
	
	/**
	 * This method retrieves all tokens belonging to given Group
	 * 
	 * @param reader the given reader
	 * @param group the given group
	 * @return returns all tokens belonging to given Group. Tokens will be categorized by grant and deny ones.
	 */
    public ResultTokens getTokensByGroup(EntityInstance reader, String group);
	
	/**
	 * This method read all users who are belong to the Group 'group'
	 * @param reader the given reader
	 * @param group the given group name
	 * @return a list of users who register as a member of 'group'
	 */
	public List<EntityInstance> getUsersByGroup(EntityInstance reader, String group);
	
	/**
	 * This method adds a list of tokens onto a user
	 * 
	 * @param creator the executer of this action
	 * @param receiver the user who will be given by a list of tokens
	 * @param tokens the list of tokens
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	public ResultSecurity addTokensToUser(EntityInstance creator, EntityInstance receiver, List<EntityInstance> tokens);
	
	/**
	 * This method remove a list of tokens from a user
	 * 
	 * @param deletor the executer of this action
	 * @param deletee the user whose tokens will be removed.
	 * @param tokens the list of tokens
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	public ResultSecurity removeTokensFromUser(EntityInstance deletor, EntityInstance deletee, List<EntityInstance> tokens);
	
	/**
	 * This method reflects an EntitySecurityClass from a table schema in database.
	 * 
	 * @param databaseConnection configured database connection 
     * @param tableName the name of table which schema will be transformed into EntitySecurityClass object
     * @param reRead refresh the database handler or not.
     * @return return an EntitySecurityClass object
	 */
	public EntitySecurityClass getEntitySecurityClass(DatabaseConnection databaseConnection, String tableName, boolean reRead);
}

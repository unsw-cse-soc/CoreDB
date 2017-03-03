/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.controller;

import coredb.database.DatabaseConnection;
import coredb.mode.Mode;
import coredb.unit.AttributeClass;
import coredb.unit.ClassToTables;
import coredb.unit.EntityClass;
import coredb.unit.EntityInstance;
import java.util.List;
import org.apache.commons.beanutils.DynaClass;

/**
 *
 * @author vmc
 */
public interface IEntityControllerCommon {

	/**
     * @param reRead If reRead is true, this function internally calls refresh().
	 * @param mode [Mode.TRACING|Mode.Security]
     */
	public void initializeMode(boolean reRead, Mode mode);

    /**
     * Reread database handler and schema information from the database
     */
    public void refresh();
    
    /**
     * Deploy the definition list to the database. This method creates a list of
     * tables
     * @param classToTablesList a list of tables need to create
     * @return true if process successful, otherwise return false
     */
    public boolean deployDefinitionList(List<ClassToTables> classToTablesList);

    /**
     * Deploy the definition list to the database. This method creates a list of
     * tables
     * @param classToTablesList the list of tables need to create
     * @param dropTablesFirst if this param is true, drop tables first if exist
     * @param continueOnError
     * @return true if process successful, otherwise return false
     */
    public boolean deployDefinitionList(List<ClassToTables> classToTablesList,
            boolean dropTablesFirst, boolean continueOnError);

    /**
	 * Get database connection
	 * @return database connection
	 */
    public DatabaseConnection getDatabaseConnection();


	/**
	 * Read the table name from the database
	 * @param reRead
	 * @return the list of table Names in the current DB
	 */
    public List<String> readTableNameList(boolean reRead);

	 /**
     * This function drops a list of tableName from the database.
     * @param tableNames a list of tableName names need to drop
     */
    public void dropTables(List<String> tableNames);

    /**
     * Drop all tables. This method drops all tables of current data schema from DB
     */
    public void dropAllTables();

    /**
     * Create EnityInstance for Table 'tableName'.
     * @param tableName the name of table
     * @return the entity instance of given table
     */
    public EntityInstance createEntityInstanceFor(String tableName);

	/**
     * Create EnityInstance for Table 'tableName'.
     * @param tableName the name of table
	 * @param reRead
	 * @return the entity instance of given table
     */
    public EntityInstance createEntityInstanceFor(String tableName, boolean reRead);


	/**
     * This function gets dynaclass for a tableName from the database.
	 * @param tableName the name of the table
     * @return the DynaClass of the table
     */
	// checked by vmc @322
    public DynaClass getDynaClassFor(String tableName);

	/**
	 * This method creates a mapping table
	 * 
	 * @param leftTable reference table 1
	 * @param leftAcl reference attributes from table1
	 * @param rightTable reference table 2
     * @param rightAcl reference attributes from table2
	 * @return mapping table for the relation between table1 and table2
	 */
    public ClassToTables generateMappingTable(ClassToTables leftTable, AttributeClass[] leftAcl, ClassToTables rightTable, AttributeClass[] rightAcl);


	/**
	 * This method executes a arbitrary sql statement and return result as a list of EntityInstance
     *
	 * @param sql the sql statement used to read EntityInstances
	 * @return returns a list of EntityInstances
	 */
    //public List<EntityInstance> executeArbitrarySQL(String sql);

    /**
	 * This method executes a arbitrary sql statement and return result as a list of EntityInstance
     *
	 * @param queryName the name of query defined by user. The name of result
     * list of EntityInstances will be in form of queryName+"_LazyDynaClass"
	 * @param sql the sql statement used to read EntityInstances
	 * @return returns a list of EntityInstances
	 */
    public List<EntityInstance> executeArbitrarySQL(String queryName, String sql);

	/**
     * This function gets the AttributeClass of column 'attributeName' of the table 'tableName'
     *
     * @param tableName the name of the table that Attribute attached
     * @param attributeName the name of AttributeClass (Column)
     * @return returns the AttributeClass of column 'attributeName'
     */
	// checked by vmc @259
    public AttributeClass getAttributeClass(String tableName, String attributeName);

	/**
	 *This function gets the AttributeClass of column 'attributeName' of the given EntityInstance
	 * @param entity the EntityInstance 
	 * @param attributeName the name of AttributeClass (Column)
	 * @return returns the AttributeClass of column 'attributeName'
	 */
	// checked by vmc @259
    public AttributeClass getAttributeClass(EntityInstance entity, String attributeName);

    /**
	 * This function gets the EntityClass of the table 'tableName'
     *
	 * @param databaseConnection the current working databaseConnection in use
     * @param tableName the name of the table
     * @param reRead If reRead is true, this function internally calls refresh().
     * @return returns the EntityClass of table 'tableName'
	 */
	// CHECKED BY VMC @462
    public EntityClass getEntityClass(DatabaseConnection databaseConnection, String tableName, boolean reRead);

	/**
     * This method create a sql statement to select PKs of the given EntityInstance
     *
     * @param databaseConnection the current working databaseConnection in use
     * @param entityInstance the given EntityInstance
     * @return returns the sql statement to select PKs of the given EntityInstance
	 */
    //public String getCompoundStatementOnPrimaryKeys(DatabaseConnection databaseConnection, EntityInstance entityInstance);
}
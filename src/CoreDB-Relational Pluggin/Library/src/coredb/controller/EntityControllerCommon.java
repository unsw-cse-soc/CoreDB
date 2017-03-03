/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.controller;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.beanutils.DynaClass;

import coredb.config.Configuration;
import coredb.config.ConfigurationValues;
import coredb.config.Configuration.Column_Info_Table;
import coredb.config.Configuration.Column_Security_Grant_Table;
import coredb.config.Configuration.R;
import coredb.database.DatabaseConnection;
import coredb.database.DatabaseConnectionHelper;
import coredb.database.DatabaseHandler;
import coredb.mode.Mode;
import coredb.mode.UserDataInformationForMode;
import coredb.security.CoreTableDefinitionClass;
import coredb.security.SecurityCRUD;
import coredb.sql.Q;
import coredb.unit.AttributeClass;
import coredb.unit.ClassToTables;
import coredb.unit.EntityClass;
import coredb.unit.EntityInstance;
import coredb.unit.IndexClass;

/**
 * 
 * @author vmc
 */
public class EntityControllerCommon implements IEntityControllerCommon {
	protected DatabaseConnection databaseConnection = null;

	// checked by vmc @553
	public EntityControllerCommon(String pathToFile) throws SQLException {
		ConfigurationValues.init(pathToFile);
		this.databaseConnection = new DatabaseConnection(
			ConfigurationValues.dbURL,
			ConfigurationValues.databaseType,
			ConfigurationValues.databaseDriver,
			ConfigurationValues.databaseName,
			ConfigurationValues.user,
			ConfigurationValues.password
		);
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
	public EntityControllerCommon(String dbURL, String databaseType,
			String databaseDriver, String databaseName, String user,
			String password) throws SQLException {
		ConfigurationValues.init(dbURL, databaseType, databaseDriver, databaseName, user, password);
		this.databaseConnection = new DatabaseConnection(
				ConfigurationValues.dbURL,
				ConfigurationValues.databaseType,
				ConfigurationValues.databaseDriver,
				ConfigurationValues.databaseName,
				ConfigurationValues.user,
				ConfigurationValues.password
		);
	}

	public EntityControllerCommon(EntityControllerCommon ecc) {
		this.databaseConnection = ecc.databaseConnection;
	}

	/**
     * This method refreshes or forced-reads the schema information from the
     * database. This flush out any out-of-date the internal schema information
     * stored memory. This function can slow the performance by 200ms. So only
     * use when you know what you are doing !!!
     */
	// checked by vmc @259
	public void refresh() { this.databaseConnection.refresh(); }

    /**
     * This function deploys a list of class definitions to the database and
     * returns a database handler.
     *
     * @param classToTablesList the list of tables need to be created.
     * @return DatabaseHandler
     */
	// CHECKED BY VMC @77
	public boolean deployDefinitionList(List<ClassToTables> classToTablesList) {
		return this.deployDefinitionList(classToTablesList, false, false);
	}

    /**
     * This function deploys a list of class definitions to the database and
     * returns a database handler.
     *
     * @param classToTablesList the list of tables need to be created.
     * @param dropTablesFirst drop existing tableName first.
     * @param continueOnError continue to the next tableName if there is an error.
     * @return DatabaseHandler
     */
	// CHECKED BY VMC @207
	public boolean deployDefinitionList(List<ClassToTables> classToTablesList, boolean dropTablesFirst, boolean continueOnError) {
        try {
		    DatabaseHandler databaseHandler = this.databaseConnection.deploySchema(classToTablesList,dropTablesFirst, continueOnError);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
		return true;
	}


    /**
	 * This function return the database connection.
	 * @return DatabaseConnection
	 */
	// checked by vmc @324
	public DatabaseConnection getDatabaseConnection() {
		return this.databaseConnection;
	}

	  /**
     * This function returns a list of tableName names from the database
     * (if reRead is true) or from memory (if reRead false).
     * @param reRead If reRead is true, this function internally calls refresh().
     * @return a list of tableName name.
     */
	// checked by vmc @282
    public List<String> readTableNameList(boolean reRead) {
        return this.databaseConnection.readTableNameList(reRead);
    }

    /**
     * This function drops a list of tableName from the database.
     * @param tableNames a list of tableName names need to drop
     */
	// checked by vmc @316
    public void dropTables(List<String> tableNames) {
        this.databaseConnection.dropTables(tableNames);
    }

    /**
     * This function drops all tables from the database.
     */
	// checked by vmc @316
    public void dropAllTables() {
        this.databaseConnection.dropAllTables();
    }

    /**
     * This method creates an entity instance for a tableName in the database.
	   * @param tableName the name of the table
     * @param caseSensitive specify is case sensitive, or not
     * @return entity instance for the table
     */
	// checked by vmc @322
	public EntityInstance createEntityInstanceFor(String tableName, boolean caseSensitive) {
        return this.databaseConnection.createEntityInstanceFor(tableName, caseSensitive);
    }

    /**
     * This function create entity instance for a tableName.
     *
     * @param tableName the name of the table
     * @return entity instance for the table
     */
  	// checked by vmc @271
    public EntityInstance createEntityInstanceFor(String tableName) {
		return this.createEntityInstanceFor(tableName, false);
    }

    /**
     * This function gets dynaclass for a tableName from the database.
	 * @param tableName the name of the table
     * @return the DynaClass of the table
     */
	// checked by vmc @322
    public DynaClass getDynaClassFor(String tableName) {
        return this.databaseConnection.getDynaClassFor(tableName);
    }

	/**
	 * This method shows how to create mapping tables
	 *
	 * @param leftTable reference tableName 1
	 * @param leftAcl reference attributes from table1
	 * @param rightTable reference tableName 2
     * @param rightAcl reference attributes from table2
	 * @return mapping tableName for the relation between table1 and table2
	 */
	// checked by vmc @694
	public ClassToTables generateMappingTable(ClassToTables leftTable, AttributeClass[] leftAcl, ClassToTables rightTable, AttributeClass[] rightAcl) {

		String mappingTableName = leftTable.getName() + Configuration.TABLE_DELIMITER + rightTable.getName();
		List<AttributeClass> acl = new LinkedList<AttributeClass>();

		// add rowID
		String rowIdName = Configuration.Public_Attribute.PrefixOfRowId + mappingTableName + Configuration.Public_Attribute.SubfixOfRowId;
		AttributeClass rowId = new AttributeClass(rowIdName, String.class);
		rowId.setPrimaryKey(true);
		acl.add(rowId);

		// add columns into mapping tableName
		for(int i = 0; i < leftAcl.length; i ++) {
		    String columnName = leftTable.getName() + Configuration.TABLE_DELIMITER + leftAcl[i].getName();
		    AttributeClass column = new AttributeClass(columnName, leftAcl[i].getType(),	leftAcl[i].getSqlType());

			acl.add(column);
		}

		for(int i = 0; i < rightAcl.length; i ++) {
			String columnName = rightTable.getName() + Configuration.TABLE_DELIMITER + rightAcl[i].getName();
			AttributeClass column = new AttributeClass(columnName, rightAcl[i].getType(),	rightAcl[i].getSqlType());

			acl.add(column);
		}

		// add index
		List<IndexClass> indexes = new LinkedList<IndexClass>();
		IndexClass index = new IndexClass(acl.toArray(new AttributeClass[0]), true);
		index.setUnique(true);
		indexes.add(index);

		return new EntityClass(mappingTableName, acl.toArray(new AttributeClass[0]), indexes.toArray(new IndexClass[0]));
/*
		ClassToTables mappingClass = (tracing == false ? new EntityClass(mappingTableName, acl.toArray(new AttributeClass[0]), indexes.toArray(new IndexClass[0]))
													   : new EntityTracingClass(mappingTableName, acl.toArray(new AttributeClass[0]), indexes.toArray(new IndexClass[0])));

		return mappingClass;
 */
	}

	/**
	 * This method executes a arbitrary sql statement and return result as a list of EntityInstance
     *
	 * @param sql the sql statement used to read EntityInstances
	 * @return returns a list of EntityInstances
	 */
	// /*
    public List<EntityInstance> executeArbitrarySQL(String sql){
		return DatabaseConnectionHelper.executeArbitraryQueryCommon(databaseConnection, Q.ARBITRARY_SQL_NAME, sql, false);
	}
	// */

	/**
	 *This method executes a pure sql statement and return a list of data rows
	 *
	 * @param queryName the name of this query
	 * @param sql the pure sql statement
	 * @return a list of EntityInstance
	 */
	// /*
    public List<EntityInstance> executeArbitrarySQL(String queryName, String sql){
		return DatabaseConnectionHelper.executeArbitraryQueryCommon(databaseConnection, queryName, sql, false);
    }
	// */

	/**
     * This function gets the AttributeClass of column 'attributeName' of the table 'tableName'
     *
     * @param tableName the name of the table that Attribute attached
     * @param attributeName the name of AttributeClass (Column)
     * @return returns the AttributeClass of column 'attributeName'
     */
	// checked by vmc @259
    public AttributeClass getAttributeClass(String tableName, String attributeName) {
    	return DatabaseConnection.getAttributeClass(tableName, attributeName);

    }

    /**
     * This function gets an attribute class given tableName name and attribute name.
     *
     * @param entity the given EntityInstance
     * @param attributeName the name of Attribute
     * @return returns the AttributeClass of column 'attributeName'
     */
    public AttributeClass getAttributeClass(EntityInstance entity, String attributeName) {
    	return DatabaseConnection.getAttributeClass(entity.getDynaClass().getName(), attributeName);
    }

    /**
	 * This function gets the EntityClass of the table 'tableName'
     *
	 * @param databaseConnection the current working databaseConnection in use
     * @param tableName the name of the table
     * @param reRead If reRead is true, this function internally calls refresh().
     * @return returns the EntityClass of table 'tableName'
	 */
	// CHECKED BY VMC @462
	public EntityClass getEntityClass(DatabaseConnection databaseConnection, String tableName, boolean reRead){
		return databaseConnection.getEntityClass(tableName, reRead);
	}

	/**
     * This method create a sql statement to select PKs of the given EntityInstance
     *
     * @param databaseConnection the current working databaseConnection in use
     * @param entityInstance the given EntityInstance
     * @return returns the sql statement to select PKs of the given EntityInstance
	 */
/*
	public String getCompoundStatementOnPrimaryKeys(DatabaseConnection databaseConnection, EntityInstance entityInstance) {
		return entityInstance.makeSQLOnIdentifiableAttributes(databaseConnection);
	}
 */
/*
	public void initializeMode(boolean reRead, Mode mode) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
 */
	public void initializeMode(boolean reRead, Mode mode) {
		List<ClassToTables> cttList_Sys = CoreTableDefinitionClass.createCoreTableDefinitionClassList(mode);

		List<String> tableList          = this.readTableNameList(reRead);
		HashMap<String,String> map      = new HashMap<String, String>();
		for(String table : tableList){map.put(table, table);}

		List<ClassToTables> cttList     = new LinkedList<ClassToTables>();
		for(ClassToTables ctt : cttList_Sys){
			if(map.get(ctt.getName().toLowerCase()) == null){
				cttList.add(ctt);
			}
		}
//Describer.describeClassToTables(cttList);

		this.deployDefinitionList(cttList,true,true);

		if (Mode.SECURITY == mode) {
			/* COREDB-165 */
			// if column security grant table is already exist. donot deploy security 
			// tables but to initialise SecurityCRUD
			if(map.get(Column_Security_Grant_Table.makeColumnSecurityGrantTableName().toLowerCase()) != null) {
				SecurityCRUD.initialize(getDatabaseConnection());
			} else {
				this.storeTableAndColumnInformation(cttList);
				UserDataInformationForMode.initializeUserInfo(this.databaseConnection, mode);
				UserDataInformationForMode.initializeDefaultUserTokenInfo(this.databaseConnection, mode);
			}
		} else if(Mode.TRACING == mode) {
			UserDataInformationForMode.initializeUserInfo(this.databaseConnection, mode);
			UserDataInformationForMode.initializeDefaultUserTokenInfo(this.databaseConnection, mode);
		}
	}

	/**
	 *
	 * @param classToTablesList
	 */
	// checked by vmc @197
	protected void storeTableAndColumnInformation(List<ClassToTables> classToTablesList) {
		String tableColumnInfoTableName = Configuration.Column_Info_Table.makeColumnInfoTableName();
//System.out.println(tableColumnInfoTableName);
		for (ClassToTables ctt : classToTablesList) {
            for(AttributeClass ac : ctt.getAttributeClassList()){
                EntityInstance entityInstance = this.databaseConnection.createEntityInstanceFor(tableColumnInfoTableName, false);
                entityInstance.set(Column_Info_Table.COLUMN_TABLENAME,  R.refractor(ctt.getName()));
                entityInstance.set(Column_Info_Table.COLUMN_COLUMNNAME, R.refractor(ac.getName()));
                
             // #COREDB-69
               // System.out.println(ac.getType().getName());
                entityInstance.set(Column_Info_Table.getColumnProgramingDataType(), ac.getType().getName());
				entityInstance.set(Column_Info_Table.getColumnVersion(), 0);
             // #/COREDB-69  
								
				List<String[]> columnNameAndOperatorPairs = new LinkedList<String[]>();
				columnNameAndOperatorPairs.add(new String[]{Column_Info_Table.COLUMN_TABLENAME, Q.E});
				columnNameAndOperatorPairs.add(new String[]{Column_Info_Table.COLUMN_COLUMNNAME, Q.E});
				
				String SQL = Q.SELECT + Q.STAR + Q.FROM + tableColumnInfoTableName +
						     Q.WHERE  + entityInstance.makeSQLOnColumns(databaseConnection, columnNameAndOperatorPairs);
//System.out.println(SQL);
				synchronized (this) {
					int size = this.executeArbitrarySQL(SQL).size();
					if (size == 0) databaseConnection.create(entityInstance);
				}
            }
		}
	}

	public List<ClassToTables> getClassToTables(List<String> tableNameList) {
    	List<ClassToTables> classToTables  = new LinkedList<ClassToTables>();

    	for(String tableName : tableNameList) {
    		classToTables.add(databaseConnection.getEntityClass(tableName, false));
    	}
    	return classToTables;
	}
}

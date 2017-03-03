/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.ddlutils.DatabaseOperationException;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.PlatformInfo;
import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Index;
import org.apache.ddlutils.model.IndexColumn;
import org.apache.ddlutils.model.Table;
import org.apache.ddlutils.model.TypeMap;

import coredb.config.Configuration;
import coredb.sql.CompoundStatementManual;
import coredb.sql.Q;
import coredb.sql.SQLFactory;
import coredb.unit.AttributeClass;
import coredb.unit.ClassToTables;
import coredb.unit.EntityClass;
import coredb.unit.EntityInstance;
import coredb.unit.IndexClass;
import draft.PublicFunctions;

/**
 * This class defines methods of database connection
 * @author vmc
 *
 */

// checked by vmc @455
public class DatabaseConnection {
	private Platform platform               = null;
    private PlatformInfo platformInfo       = null;
    private DataSource dataSource           = null;
	private Connection connection           = null;
	private String databaseName             = null;
    private DatabaseHandler databaseHandler = null;
    private SchemaInfo schemaInfo = null;

	/**
	 * This constructor constructs a database connection class.
	 * 
	 * @param dbURL DB connection URL
	 * @param databaseType the type of DB server 
	 * @param databaseDriver the driver of DB server 
	 * @param databaseName the name of DB schema
	 * @param user user name using to connect DB
	 * @param password user password using to connect DB
	 * @throws SQLException
	 */
	//checked by vmc @259
	public DatabaseConnection(String dbURL, String databaseType,
			String databaseDriver, String databaseName, String user,
			String password) throws SQLException {
		try {
			Class.forName(databaseDriver);
			this.databaseName       = databaseName;
			this.dataSource         = new DataSourceFactory(dbURL, user, password);	            
			this.platform           = PlatformFactory.createNewPlatformInstance(dataSource);    
            this.connection         = dataSource.getConnection();   
            this.databaseHandler    = new DatabaseHandler(platform.readModelFromDatabase(databaseName));
            this.platformInfo       = platform.getPlatformInfo();
            this.schemaInfo         = SchemaInfo.getInstance(databaseHandler);
            this.platform.setDelimitedIdentifierModeOn(false);
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(DatabaseConnection.class.getName()).log(
					Level.SEVERE, null, ex);
		}
        platformInfo.setDefaultSize(Types.CHAR, 1);
        platformInfo.setMaxColumnNameLength(255);
        platformInfo.setMaxTableNameLength(127);
        platformInfo.setMaxIdentifierLength(127);
		platformInfo.setAutoCommitModeForLastIdentityValueReading(false);
  		//platformInfo.setAutoCommitModeForLastIdentityValueReading(false);
		// <vmc>
		/*
		platformInfo.addNativeTypeMapping(Types.DATE,"date",Types.DATE);
		platformInfo.addNativeTypeMapping(Types.TIME,"time",Types.TIME);
		platformInfo.addNativeTypeMapping(Types.TIMESTAMP,"timestamp",Types.TIMESTAMP);
		System.out.printf("vmc:DatabaseConnection %15s\n",platformInfo.getNativeType(Types.DATE));
		System.out.printf("vmc:DatabaseConnection %15s\n",platformInfo.getNativeType(Types.TIME));
		System.out.printf("vmc:DatabaseConnection %15s\n",platformInfo.getNativeType(Types.TIMESTAMP));
		*/
		//platformInfo.setMaxConstraintNameLength(127);
		//platformInfo.setMaxForeignKeyNameLength(127);
		// </vmc>
	}
	
	/**
	 * The function retrieves the JDBC native type code customized by DDLUTILS.
	 * DDLUTILS designed the JDBC native type code to resolve the issue of compatibility 
	 * between JAVA type and JDBC type based on different database platform. Therefore,
	 * the native JDBC type is the real data type we need to concern in the operations.
	 * 
	 * @param jdbcTypeCode the original JDBC type from database
	 * @return the JDBC native type customized by DDLUTILS.
	 */
	// < #COREDB-103>
	public int getJDBCNativeTypeCodeByJDBCOriginalTypeCode(int jdbcTypeCode){
		return this.platformInfo.getTargetJdbcType(jdbcTypeCode);
	}
	// < #/COREDB-103>
	
    /**
     * This function deploys database schema into database and
     * returns a database handler.
     *
     * @param cttl the list of tables need to be created.
     * @param dropTablesFirst drop existing table first.
     * @param continueOnError continue to the next table if there is an error.
     * @return DatabaseHandler
     */
	//checked by vmc @599
	public synchronized DatabaseHandler deploySchema(List<ClassToTables> cttl, boolean dropTablesFirst, boolean continueOnError) {
        this.getDatabaseHandler(true);
        if (dropTablesFirst) {
            DatabaseHandler newDatabaseHandler = new DatabaseHandler();
            for (ClassToTables ctt : cttl) {
            	newDatabaseHandler.addTables(ctt.toTables());
				//this.dropAutoIncrementSequence(ctt);
			}
            this.platform.createTables(connection, newDatabaseHandler.getDatabase(), dropTablesFirst, continueOnError);

        } else {
            DatabaseHandler newDatabaseHandler = new DatabaseHandler();
            for (ClassToTables ctt : cttl) { newDatabaseHandler.addTables(ctt.toTables()); }
			this.platform.createTables(connection, newDatabaseHandler.getDatabase(), dropTablesFirst, continueOnError);

            try {
                this.databaseHandler.mergeDatabase(newDatabaseHandler.getDatabase());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        this.refresh();
        return databaseHandler;
	}


    /**
     * This method return a connection to the database. It is used by
     * external performance testing only. User should not use this method directly.
     * @return  Connection
     */
	// checked by vmc @322
    public Connection getConnection()           { return this.connection;       }

    /**
     * This method returns the database handler.
     * @return DatabaseHandler
     */
	// checked by vmc @322
	private DatabaseHandler getDatabaseHandler() { return this.databaseHandler;  }

    /**
     * This method re-read the database and returns the database handler.
     * @return DatabaseHandler
     */
	// checked by vmc @322
    private DatabaseHandler getDatabaseHandler(boolean reRead) {
        if (reRead) {
            this.databaseHandler.setDatabase(this.platform.readModelFromDatabase(databaseName));
        }
        return this.getDatabaseHandler();
    }

    /**
     * This method reads the schema information from the memory and
     * returns the database schema information.
     * @return SchemaInfo
     */
	// checked by vmc @322
    private SchemaInfo getSchemaInfo() { return this.schemaInfo; }
    
    /**
     * This method re-reads the schema information from the database and
     * returns the database schema information.
     * @param reRead The effect of reRead is set to true is equal to refresh.
     * @see refresh()
     * @return SchemaInfo
     */
	// checked by vmc @322
    private SchemaInfo getSchemaInfo(boolean reRead) {
        if (reRead) {
            this.schemaInfo = SchemaInfo.getInstance(getDatabaseHandler(reRead));
        }
        return this.getSchemaInfo();
    }

    /**
     * This method refreshes or forced-reads the schema information from the
     * database. This flush out any out-of-date the internal schema information
     * stored memory. This function can slow the performance by 200ms. So only
     * use when you know what you are doing !!!
     */
	// checked by vmc @322
    public void refresh() {
        this.schemaInfo      = this.getSchemaInfo(true);
    }

	/**
	 * 
	 * @param sql
	 * @return
	 */
	public int executeArbitraryUpdate(String sql){
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			return ps.executeUpdate();
		} catch (SQLException ex) {
			Logger.getLogger(PublicFunctions.class.getName()).log(Level.SEVERE, null, ex);
		}
		return -1;
	}
    /**
     * This function returns a list of table names from the database
     * (if reRead is true) or from memory (if reRead false).
     * @param reRead The effect of reRead is set to true is equal to refresh.
     * @see #refresh()
     * @return a list of table name.
     * @since
     * If reRead is true, this function internally calls refresh().
     */
	// checked by vmc @322
    public List<String> readTableNameList(boolean reRead) {
        if (reRead) refresh();
		List<String> tableNameList = new LinkedList<String>();
		Table[] tables = this.getDatabaseHandler().getTables();
		for (Table table : tables) {
			tableNameList.add(table.getName());
		}
		return tableNameList;
	}

    /**
     * This function drops a list of table from the database.
     * @param tableNames a list of table names
     */
	// checked by vmc @322
    public void dropTables(List<String> tableNames){
        this.refresh();
        for (String tableName: tableNames) {
            Table table = this.databaseHandler.findTable(tableName);
            platform.dropTable(connection, databaseHandler.getDatabase(), table, false);
        }
        this.refresh();
    }

    public void dropAutoIncrementSequence(ClassToTables ctt) {
    	String tableName = ctt.getName();
    	List<AttributeClass> autoIncrementColumns = new LinkedList<AttributeClass>();
    	for(AttributeClass ac : ctt.getAttributeClassList()){
    		if(ac.isAutoIncrement()) autoIncrementColumns.add(ac);
    	}
        String sql_dropSeq = "";
        for(AttributeClass column : autoIncrementColumns){
        	sql_dropSeq = Q.DROP + Q.SEQUENCE + tableName + Configuration.TABLE_DELIMITER + column.getName() + Configuration.TABLE_DELIMITER + Q.SEQUENCE_SUFFIX + ";";
        }
        Statement statement;
		try {
			statement = connection.createStatement();
			System.err.println(sql_dropSeq);
			statement.executeUpdate(sql_dropSeq);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
     * This function drops all tables from the database.
     */
	// checked by vmc @322
	public void dropAllTables() {
        this.refresh();
		platform.dropTables(connection, this.getDatabaseHandler().getDatabase(), false);
        this.refresh();
	}


    /**
     * This function gets dynaclass for a table from the database.
     * @param tableName the name of this table
     * @return DynaClass
     */
	// checked by vmc @322
    public DynaClass getDynaClassFor(String tableName) {
        return this.databaseHandler.getDatabase().getDynaClassFor(SchemaInfo.refactorTableName(tableName));
    }

    /**
     * This function create an entity instance for a table in the database.
     * @param tableName the name of this table
     * @param caseSensitive if it is true, method will match table in database by its name case sensitively
     * @return the entity instance for this table
     */
	// checked by vmc @322
    public EntityInstance createEntityInstanceFor(String tableName, boolean caseSensitive) {
        return this.databaseHandler.createEntityInstanceFor(tableName, caseSensitive);
    }

    /**
     * This function creates a bean in the database.
     * @param bean is the bean that will be created in the database.
     * @return true if successful, otherwise false.
     */
	// checked by vmc @324
    public boolean create(DynaBean bean) {
        try{
            this.platform.insert(this.connection,this.databaseHandler.getDatabase(), bean);
            return true;
        } catch (DatabaseOperationException e) {
			System.out.println("CoreDB: Database integrity exception during "	+
							   "the data insertion. No data has inserted into the database.");
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		} 
        return false;
	}


    /**
     * This function creates a list of beans in the database.
     * @param beans is the list of beans that will be created in the database.
     * @return true if successful, otherwise false.
     */
	// checked by vmc @259
	public boolean create(List<? extends DynaBean> beans) {
        try {
		    this.platform.insert(connection, this.databaseHandler.getDatabase(), beans);
			return true;
        } catch (DatabaseOperationException e) {
			System.out.println("CoreDB: Database integrity exception during "	+
							   "the data insertion. No data has inserted into the database.");
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
            e.printStackTrace();
        } 
		return false;
	}

	/**
	 * This function read data (select *) from table using given conditional sql statement 
	 * 
	 * @param tableName the name of table
	 * @param cshString the given conditional sql statement 
	 * @return a list of EntityInstances
	 */
	public List<EntityInstance> read(String tableName, String cshString) {

		List<EntityInstance> result = new LinkedList<EntityInstance>();
        String sql = SQLFactory.makeSQL_findEntities(tableName, cshString);
        ResultSet resultSet = null;
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            resultSet = ps.executeQuery();
            result	  = DatabaseConnectionHelper.convertResultSetWithNonUniqueAttributesToEntityInstance(this, tableName, resultSet);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }
	
    /**
     * This function updates a bean in the database.
     * @param bean is the bean that will be updated in the database.
     * @return true if successful, otherwise false.
     */
	// checked by vmc @259
    public boolean update(DynaBean bean) {
        try{
		    this.platform.update(connection,databaseHandler.getDatabase(), bean);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
	}

    /**
     * This function updates a list of beans in the database.
     * @param beans is the list of beans that will be updated in the database.
     * @return true if successful, otherwise false.
     */
	// checked by vmc @139
	public boolean update(List<? extends DynaBean> beans) {
        for (DynaBean bean : beans) if (!update(bean)) return false;
        return true;
	}
    /**
     * This function deletes a bean from the database.
     * @param bean is the bean that will be deleted from the database.
     * @return true if successful, otherwise false.
     */
	// checked by vmc @139
	public boolean delete(DynaBean bean) {
        try {
            this.platform.delete(connection,databaseHandler.getDatabase(), bean);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
	}

    /**
     * This function deletes a list of beans from the database.
     * @param beans is the list of beans that will be deleted from the database.
     * @return true if successful, otherwise false.
     */
	// checked by vmc @139
	public boolean delete(List<? extends DynaBean> beans) {
        for (DynaBean bean : beans) if (!delete(bean)) return false;
        return true;
	}
	
	/**
	 * This function return a table by the user request
	 * @param tableName the requested table name
	 * @param reRead refresh the database handler or not
	 * @return the requested table, if no result, return null
	 */
	// checked by vmc @455
	public Table getTable(String tableName, boolean reRead){
		Table[] tables = this.getAllTables(reRead);
		for(Table table : tables){
			if(table.getName().equals(SchemaInfo.refactorTableName(tableName))){
				return table;
			}
		}
		return null;
	}

	/**
	 * This function return all tables in the database handler
	 * @param reRead
	 * @return a array of tables
	 */
	// checked by vmc @455
	public Table[] getAllTables(boolean reRead){
		return this.getDatabaseHandler(reRead).getTables();
	}
	
	/**
	 * Alter a existed table and add it into the database
	 * @param entityClass a table definition
	 */
	public void alterTable(EntityClass entityClass) {
		Database database = getDatabaseHandler().getDatabase();
		Table newTable = entityClass.toTable();
		Table oldTable = database.findTable(entityClass.getName());
		if(oldTable!=null) database.removeTable(oldTable);
		database.addTable(newTable);	
		this.platform.alterTables(getDatabaseHandler().getDatabase(), false);
		this.refresh();
		
	}
	
	public void alterColumn(String tableName, AttributeClass oldAttribute, AttributeClass newAttribute) {
		CompoundStatementManual csm = new CompoundStatementManual();
		
		csm.addConditionalStatement(Q.ALTER + Q.TABLE + tableName + Q.DROP + Q.COLUMN + oldAttribute.getName() + Q.SEMICOLON);
		this.executeArbitraryUpdate(csm.toSQLStatement());
		
		int size;
		if(newAttribute.getSize() != 0) size = newAttribute.getSize();
		else size = this.platformInfo.getDefaultSize(TypeMap.getJdbcTypeCode(newAttribute.getSqlType()));
		
		csm.clear();
		csm.addConditionalStatement(Q.ALTER + Q.TABLE + tableName + Q.ADD + Q.COLUMN + newAttribute.getName() + Q.SPACE + newAttribute.getSqlType() + Q.LEFT_PARENTHESIS + size+ Q.RIGHT_PARENTHESIS + Q.SEMICOLON);
		this.executeArbitraryUpdate(csm.toSQLStatement());
		this.refresh();
		/*
		csm.addConditionalStatement(Q.ALTER + Q.TABLE + tableName + Q.RENAME + Q.COLUMN + oldAttribute.getName() + Q.TO + newAttribute.getName() + Q.SEMICOLON);
		int size = this.platformInfo.getDefaultSize(TypeMap.getJdbcTypeCode(newAttribute.getSqlType()));		
		csm.addConditionalStatement(Q.ALTER + Q.TABLE + tableName + Q.ALTER + Q.COLUMN + newAttribute.getName() + Q.TYPE + newAttribute.getSqlType() + Q.LEFT_PARENTHESIS + size+ Q.RIGHT_PARENTHESIS);
		PublicFunctions.executeArbitraryUpdate(this, csm.toSQLStatement());
		this.refresh();
		 */
	}
	
	public void setAutoCommit(boolean isAutoCommit) {
		try {
			this.connection.setAutoCommit(isAutoCommit);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public List<AttributeClass> getPrimaryAttributes(String tableName) {
		List<AttributeClass> primaryKeyAttributes = new LinkedList<AttributeClass>();
		AttributeClass[] acs = this.getEntityClass(tableName, false).getAttributeClassList();

    	for(AttributeClass ac : acs) if(ac.isPrimaryKey()) primaryKeyAttributes.add(ac);
		return primaryKeyAttributes;
	}
	
	
	
	/**
	 * This function generates a entityclass from the database.The
	 * structure of entityclass will be matched with the tableName schema
	 * requested by tableName name.
	 * 
     * @param tableName the tableName schema will be transformed into entityclass
     * @param reRead refresh the database handler or not.
     * @return retuens the entityclass of table which name is tableName
	 */
	// CHECKED BY VMC @462
	public EntityClass getEntityClass(String tableName, boolean reRead){
		List<IndexClass> indexClassList = new LinkedList<IndexClass>();
		HashMap<String, AttributeClass> attributeMap = new LinkedHashMap<String, AttributeClass>();
		Table table = getTable(tableName, reRead);
		
		for(Column column : table.getColumns()){
			AttributeClass attribute = new AttributeClass(column.getName(),JDBCTypeConverter.toJAVAType(SchemaInfo.refactorColumnType(tableName, column.getName())));
			//AttributeClass attribute = new AttributeClass(column.getName(),JDBCTypeConverter.toJAVAType(SchemaInfo.refactorColumnType(tableName, column.getName())));
			attribute.setAutoIncrement(column.isAutoIncrement());
			attribute.setPrimaryKey(column.isPrimaryKey());
			attributeMap.put(column.getName(), attribute);
		}

		for(Index index : table.getIndices()){
	      	List<AttributeClass> indexAttributeList = new LinkedList<AttributeClass>();
            for(IndexColumn indexColumn : index.getColumns()){
            	AttributeClass attribute = attributeMap.get(indexColumn.getName());
            	if(attribute != null){
            		indexAttributeList.add(attribute);
            	}
            }
            IndexClass indexClass = new IndexClass(indexAttributeList.toArray(new AttributeClass[0]),index.getName(), index.isUnique());
            //indexClass.setName(index.getName().substring(tableName.getName().length()+1));
            indexClassList.add(indexClass);
		}
	    return new EntityClass(table.getName(), attributeMap.values().toArray(new AttributeClass[0]), indexClassList.toArray(new IndexClass[0]));
	}

	/**
	 *
	 * @param tableName
	 * @param attributeName
	 * @return AttributeClass
	 */
	public static AttributeClass getAttributeClass(String tableName, String attributeName) {
        return new AttributeClass(SchemaInfo.refactorColumnName(tableName, attributeName),JDBCTypeConverter.toJAVAType(SchemaInfo.refactorColumnType(tableName, attributeName)));
    }

}
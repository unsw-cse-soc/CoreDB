/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.controller;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import coredb.config.Configuration;
import coredb.database.DatabaseConnectionHelper;
import coredb.database.JDBCTypeConverter;
import coredb.database.SchemaInfo;
import coredb.sql.CompoundStatementManual;
import coredb.sql.ConditionalStatement;
import coredb.sql.Q;
import coredb.sql.SQLFactory;
import coredb.sql.SQLStatement;
import coredb.tracing.TracingCRUD;
import coredb.tracing.TracingCSN;
import coredb.unit.Action;
import coredb.unit.AttributeClass;
import coredb.unit.CRUD;
import coredb.unit.ClassToTables;
import coredb.unit.EntityClass;
import coredb.unit.EntityInstance;
import coredb.unit.EntityTracingClass;

/**
 * 
 * @author vmc
 */
public class EntityControllerTracing extends EntityControllerCommon implements IEntityControllerTracing {
	private EntityControllerBase ecb = null;
	/**
	 * This constructor constructs an EntityController.
	 *
	 * @param pathToFile the path of database configuration file
	 * @throws SQLException
	 */
	// checked by vmc @108
    public EntityControllerTracing(String pathToFile) throws SQLException {
		super(pathToFile);
		ecb = new EntityControllerBase(pathToFile);
		ecb.databaseConnection.getConnection().close();
		ecb.databaseConnection = super.databaseConnection;
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
	public EntityControllerTracing(String dbURL, String databaseType, String databaseDriver, String databaseName, String user, String password) throws SQLException {
		super(dbURL, databaseType, databaseDriver, databaseName, user, password);
		ecb = new EntityControllerBase(dbURL, databaseType, databaseDriver, databaseName, user, password);
		ecb.databaseConnection.getConnection().close();
		ecb.databaseConnection = super.databaseConnection;
	}

//	/**
//     * @param reRead If reRead is true, this function internally calls refresh().
//	 * @param mode [Mode.TRACING|Mode.Security]
//     */
//	// checked by vmc @932
//	@Override
//	public void initializeMode(boolean reRead, Mode mode) {
//		List<ClassToTables> cttList_Sys = CoreTableDefinitionClass.createCoreTableDefinitionClassList(mode);
//
//		List<String> tableList          = ecb.readTableNameList(reRead);
//		HashMap<String,String> map      = new HashMap<String, String>();
//		for(String table : tableList){map.put(table, table);}
//
//		List<ClassToTables> cttList     = new LinkedList<ClassToTables>();
//		for(ClassToTables ctt : cttList_Sys){
//			if(map.get(ctt.getName().toLowerCase()) == null){
//				cttList.add(ctt);
//			}
//		}
////Describer.describeClassToTables(cttList);
//
//		ecb.deployDefinitionList(cttList,true,true);
//
//		if (Mode.SECURITY == mode) this.storeTableAndColumnInformation(cttList);
//
//		UserDataInformationForMode.initializeUserInfo(ecb.databaseConnection, mode);
//		UserDataInformationForMode.initializeDefaultUserTokenInfo(ecb.databaseConnection, mode);
//	}
//
//	/**
//	 *
//	 * @param classToTablesList
//	 */
//	// checked by vmc @197
//	protected void storeTableAndColumnInformation(List<ClassToTables> classToTablesList) {
//		String tableColumnInfoTableName = Configuration.Column_Info_Table.makeColumnInfoTableName();
////System.out.println(tableColumnInfoTableName);
//		for (ClassToTables ctt : classToTablesList) {
//            for(AttributeClass ac : ctt.getAttributeClassList()){
//                EntityInstance entityInstance = ecb.databaseConnection.createEntityInstanceFor(tableColumnInfoTableName, false);
//                entityInstance.set(Configuration.Column_Info_Table.COLUMN_TABLENAME,  R.refractor(ctt.getName()));
//                entityInstance.set(Configuration.Column_Info_Table.COLUMN_COLUMNNAME, R.refractor(ac.getName()));
//				ecb.saveEntityWithTracing(entityInstance);
//            }
//		}
//	}

	/**
	 *This method saves EntityInstance
	 *
	 * @param <T> 
	 * @param entity the EntityInstance need to save
	 * @param userId the ID of user who launched this transaction
	 * @return the changeSetNumber of this transaction
	 */
	// unchecked
	public synchronized <T extends EntityInstance> int saveEntity(String userId, T entity) {
		String SQL = entity.makeSQLOnIdentifiableAttributes(ecb.databaseConnection);
		List<EntityInstance> entities = ecb.readEntities(entity.getDynaClass().getName(),SQL);
		if (entities.size() == 0)		return createEntityWithTracing(userId,(EntityInstance)entity);
		else if (entities.size() == 1)	return updateEntityWithTracing(userId,(EntityInstance)entity);
		else {
			System.out.println("vmc:saveEntity something not right");
			return -1;
		}
	}

	/**
	 * This method saves a list of EntityInstances
	 * 
	 * @param entities the list of EntityInstances need to save
	 * @param userId the ID of user who launched this transaction
	 * @return the changeSetNumber of this transaction
	 */
	// unchecked : this method is not working correctly at the moment.
	// It will take time to design it correct. For now not expose to external
	// It is used by trust api only.
	public <T extends EntityInstance> int saveEntities(String userId, List<T> entities) {
		for (@SuppressWarnings("unchecked")
			Iterator<EntityInstance> i=(Iterator<EntityInstance>) entities.iterator(); i.hasNext(); ) {
			ecb.saveEntity(i.next());
		}
		return -1;
	}

	/**
	 * This method creates EntityInstance, inserts a data row into database
	 *
	 * @param <T>
	 * @param userId the ID of user who launched this transaction
	 * @param entity the EntityInstance need to create
	 * @return the changeSetNumber of this transaction
	 */
	// checked by vmc @487
	public <T extends EntityInstance> int createEntityWithTracing(String userId, T entity) {
		List<EntityInstance> entities = new LinkedList<EntityInstance>();
		entities.add(entity);
		return createEntitiesWithTracing(userId, entities);
	}

	/**
     * Create entities: This method inserts a list of data rows into database.
     * 
     * @param entities the list of entities that will be created in the database.
     * @param userId the id of current user
     * @return returns the changeSetNumber of this create transaction
     */
	// checked by vmc @487
	public int createEntitiesWithTracing(String userId, List<? extends EntityInstance> entities) {
		int changeSetNumber  = this.generateLatestChangeSetNumber(userId);
		return createEntities(changeSetNumber, entities);
	}

	/**
	 * Create entities: This method inserts a list of data rows into database.
     * 
     * @param entities the list of entities that will be created in the database.
     * @param changeSetNumber the changeSetNumber of this transaction
	 * @return the changeSetNumber of this transaction
	 */
	// checked by vmc @487
	protected int createEntities(int changeSetNumber, List<? extends EntityInstance> entities) {
		List<EntityInstance[]> instance_mappings		= new LinkedList<EntityInstance[]>();
		for (EntityInstance entityInstance : entities) {
			instance_mappings.add(new EntityInstance[]{entityInstance,null});

		}
		List<Action> actions = TracingCRUD.makeTracingInstances(ecb.databaseConnection, instance_mappings, CRUD.CREATE, changeSetNumber);

		ecb.bulkActions(actions);
		return changeSetNumber;

	}

	/**
     * Read entity instances from DB (Tracing). This method reads data rows
     * from DB based on given conditional statement
     * 
	 * @param tableName the name of the table
     * @param sql the sql statement used to read EntityInstances
	 * @param userId the id of current user
     * @return returns a list of Entities
     */
	public List<EntityInstance> readEntitiesWithTracing(String userId, String tableName, SQLStatement sql) {
		return this.readEntitiesWithTracing(userId,tableName, sql== null ? "" : sql.toString().trim());
	}

    /**
     * Read entity instances from DB (Tracing). This method reads data rows
     * from DB based on given conditional statement
     *
	 * @param tableName the name of the table
     * @param sql the sql statement used to read EntityInstances
	 * @param userId the id of current user
     * @return returns a list of Entities
     */
	public List<EntityInstance> readEntitiesWithTracing(String userId, String tableName, String sql) {
		List<EntityInstance> entities		= ecb.readEntities(tableName, sql);

		List<EntityInstance[]> instance_mappings		= new LinkedList<EntityInstance[]>();
		for (EntityInstance entityInstance : entities) {
			List<EntityInstance> mapping = ecb.readEntities(
				Configuration.Tracing_Table.makeMappingTableNameWithPrefixSuffix(tableName), 
				//ecb.getCompoundStatementOnPrimaryKeys(ecb.databaseConnection, entityInstance));
				entityInstance.makeSQLOnIdentifiableAttributes(ecb.databaseConnection));
			instance_mappings.add(new EntityInstance[]{entityInstance,mapping.get(0)});
		}
		int changeSetNumber  = this.generateLatestChangeSetNumber(userId);
		List<Action> actions = TracingCRUD.makeTracingInstances(ecb.databaseConnection, instance_mappings, CRUD.READ, changeSetNumber);

		ecb.bulkActions(actions);
		return entities;
	}
	
	/**
     * Update an entity: This method updates a data row to database.
     *
	 * @param entity the entity that will be updated in the database.
     * @param userId the id of user
     * @return returns the changeSetNumber of this transaction
     */
	public <T extends EntityInstance> int updateEntityWithTracing(String userId, T entity) {
		List<EntityInstance> entities = new LinkedList<EntityInstance>();
		entities.add(entity);
		return updateEntitiesWithTracing(userId, entities);
	}

	/**
     * Update entities: This method updates a list of data rows to database.
     *
     * @param entities the list of entities that will be created in the database.
     * @param userId the id of current user
     * @return returns the changeSetNumber of this transaction
     */
	public int updateEntitiesWithTracing(String userId, List<? extends EntityInstance> entities) {
		int changeSetNumber  = this.generateLatestChangeSetNumber(userId);
		return updateEntities(changeSetNumber, entities);
	}

	/**
	 * Update entities: This method updates a list of data rows to database.
     *
     * @param entities the list of entities that will be updated in the database.
     * @param changeSetNumber the changeSetNumber of this transaction
	 * @return returns the changeSetNumber of this transaction
	 */
	protected int updateEntities(int changeSetNumber, List<? extends EntityInstance> entities) {
		List<EntityInstance[]> instance_mappings = new LinkedList<EntityInstance[]>();
		for (EntityInstance entityInstance : entities) {
            String tableName = entityInstance.getDynaClass().getName();
			List<EntityInstance> mapping = ecb.readEntities(
				Configuration.Tracing_Table.makeMappingTableNameWithPrefixSuffix(tableName),
				//ecb.getCompoundStatementOnPrimaryKeys(ecb.databaseConnection, entityInstance));
				entityInstance.makeSQLOnIdentifiableAttributes(ecb.databaseConnection));
			instance_mappings.add(new EntityInstance[]{entityInstance,mapping.get(0)});
		}
		List<Action> actions = TracingCRUD.makeTracingInstances(ecb.databaseConnection, instance_mappings, CRUD.UPDATE, changeSetNumber);

        ecb.bulkActions(actions);
		return changeSetNumber;
	}

	/**
     * Delete entity from database. This method deletes data row from database.
     * 
	 * @param entity the entity that will be deleted from the database.
     * @param userId the id of current user
     * @return returns the changeSetNumber of this transaction
     */
	public <T extends EntityInstance> int deleteEntityWithTracing(String userId, T entity) {
		List<EntityInstance> entities = new LinkedList<EntityInstance>();
		entities.add(entity);
		return deleteEntitiesWithTracing(userId, entities);
	}

	 /**
     * Delete entities from database. This method deletes a list of data rows from database.
     * 
     * @param entities the list of entities that will be deleted from the database.
     * @param userId the id of current user
     * @return returns the changeSetNumber of this transaction
     */
	public int deleteEntitiesWithTracing(String userId, List<? extends EntityInstance> entities) {
		int changeSetNumber  = this.generateLatestChangeSetNumber(userId);
		return deleteEntities(changeSetNumber, entities);
	}

	/**
	 *  Delete entities from database. This method deletes a list of data rows from database.
	 * 
	 * @param changeSetNumber the changeSetNumber of this transaction
	 * @param entities the list of entities that will be deleted from the database
	 * @return returns the changeSetNumber of this transaction
	 */
	protected int deleteEntities(int changeSetNumber, List<? extends EntityInstance> entities) {
		/** Make tracing record **/
		List<EntityInstance[]> instance_mappings		= new LinkedList<EntityInstance[]>();
		for (EntityInstance entityInstance : entities) {
			List<EntityInstance> mapping = ecb.readEntities(
				Configuration.Tracing_Table.makeMappingTableNameWithPrefixSuffix(entityInstance.getDynaClass().getName()),
				//ecb.getCompoundStatementOnPrimaryKeys(ecb.databaseConnection, entityInstance));
				entityInstance.makeSQLOnIdentifiableAttributes(ecb.databaseConnection));
			instance_mappings.add(new EntityInstance[]{entityInstance,mapping.get(0)});
		}
		List<Action> actions = TracingCRUD.makeTracingInstances(ecb.databaseConnection, instance_mappings, CRUD.DELETE, changeSetNumber);

		/** Execute the actions **/
		ecb.bulkActions(actions);
		return changeSetNumber;
	}

	/**
	 *This method traces EntityInstances of the table whose name is tableName
     * 
	 * @param tableName the name of the table
	 * @param userId the id of current user
	 * @return returns historical data (a list of EntityInstances include version details)
	 */
	public List<EntityInstance> traceEntities(String tableName, String userId) {
		return traceEntities(tableName,new CompoundStatementManual(), userId);
	}

	/**
	 *This method traces the latest records from database 
	 *
	 * @param tableName the name of the table will be traced
	 * @param sql the sql statement used by tracing
	 * @param userId the Id of user who launched this transaction
	 * @return the latest records (a list of EntityInstances include version details) from database 
	 */
	// checked by vmc @999
	public List<EntityInstance> traceEntities(String tableName, SQLStatement sql, String userId) {
		String builder = SQLFactory.makeSQL_findInitialTraceEntities(tableName, sql, ecb.databaseConnection);
        return DatabaseConnectionHelper.executeArbitraryQueryForCoreDB(ecb.databaseConnection, Configuration.makeLazyDynaClassName(tableName), builder);
	}

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
	// checked by vmc @936
	public List<EntityInstance> traceEntities(String tableName, String entityMappingId, int minCSN,int currentCSN,int maxCSN,String userId) {
		if(currentCSN > maxCSN){ currentCSN = maxCSN; }
		if(currentCSN < minCSN){ currentCSN = minCSN; }
		return traceEntities(tableName, entityMappingId, currentCSN, maxCSN, userId);
	}

	/**
	 * This method traces historical(csn range is between startCSN and endCSN) data back
	 * @param tableName the name of table which need to read
	 * @param entityMappingId the mappingId stored mappingTable related to EntityInstance of baseTable 
	 * @param startCSN the lower csn of range
	 * @param endCSN the upper csn of range
	 * @param userId the id of current user
	 * @return returns historical data (a list of EntityInstances include version details) based on given parameters
	 */
	// checked by vmc @946
	public List<EntityInstance> traceEntities(String tableName, String entityMappingId, int startCSN,int endCSN,String userId) {
		TracingCSN tracingCSN = new TracingCSN();
		return tracingCSN.tracingUniqueEntities(ecb.databaseConnection, tableName, entityMappingId, startCSN, endCSN, userId);
	}
    
	// BUGS PENDING
	/**
	 * This method creates a mapping tableName (tracing)
	 *
	 * @param leftTable reference tableName 1
	 * @param leftAcl reference attributes from table1
	 * @param rightTable reference tableName 2
     * @param rightAcl reference attributes from table2
	 * @return mapping tableName for the relation between table1 and table2
	 */
	public ClassToTables generateMappingTableWithTracing(ClassToTables leftTable, AttributeClass[] leftAcl,
											  ClassToTables rightTable, AttributeClass[] rightAcl) {
		EntityClass ec = (EntityClass)ecb.generateMappingTable(leftTable, leftAcl, rightTable, rightAcl);
		EntityTracingClass etc = new EntityTracingClass(ec.getName(), ec.getAttributeClassList(), ec.getIndexClassList());
		return etc;
	}
    
	/**
     * This method reads the latest changesetnumber from DB
     * @return returns the latest changesetnumber
     */
	// CHECKED BY VMC @712
	public int getLatestChangeSetNumber() {
		int changeSetNumber           = 0;
		String TABLE_NAME             = Configuration.Coredb_CSN_Table.TABLE_NAME;
		String CSN_NAME               = Configuration.Coredb_CSN_Table.COLUMN_CSN;
		Class attributeType           = JDBCTypeConverter.toJAVAType(SchemaInfo.refactorColumnType(TABLE_NAME, CSN_NAME));
		AttributeClass COLUMN_CSN     = new AttributeClass(CSN_NAME,attributeType);
	
        String SQL = SQLFactory.makeSQL_Max(TABLE_NAME, COLUMN_CSN);
		
        // read largest changeSetNumber from DB
        List<EntityInstance> entities = new LinkedList<EntityInstance>();
        Connection connection = ecb.databaseConnection.getConnection();
        ResultSet resultSet = null;
        try {
            PreparedStatement ps = connection.prepareStatement(SQL);
            resultSet = ps.executeQuery();
            //entities = DatabaseConnectionHelper.convertResultSetWithUniqueAttributeToEntityInstance(databaseConnection, TABLE_NAME, resultSet);
            entities = DatabaseConnectionHelper.convertResultSetWithUniqueAttributeToEntityInstance(ecb.databaseConnection, TABLE_NAME, resultSet);
        } catch (SQLException ex) {
            Logger.getLogger(EntityControllerTracing.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(entities.size() == 1) {
        	EntityInstance entity     = entities.get(0);
            changeSetNumber           = (Integer) entity.get(CSN_NAME);
        }
		return changeSetNumber;
	}
	
	/**
	 * This method will generate a new changeSetNumber
	 *
	 * @param userId the id of current user
	 * @return returns the new created changeSetNumber
	 */
	// checked by vmc @768
	public int generateLatestChangeSetNumber(String userId) {
		String TABLE_NAME   = Configuration.Coredb_CSN_Table.TABLE_NAME;
		String COREDB_ID	= Configuration.Coredb_CSN_Table.COLUMN_COREDB_ID;
		String CSN			= Configuration.Coredb_CSN_Table.COLUMN_CSN;
		String USERID		= Configuration.Coredb_CSN_Table.COLUMN_USERID;
		String TIMESTAMP	= Configuration.Coredb_CSN_Table.COLUMN_TIMESTAMP;
		
		UUID uuid = UUID.randomUUID();
		EntityInstance entity = ecb.createEntityInstanceFor(TABLE_NAME);
		entity.set(COREDB_ID, uuid);
		entity.set(USERID, userId);
		entity.set(TIMESTAMP, new Date(System.currentTimeMillis()));
		ecb.createEntity(entity);

		Class attributeType = JDBCTypeConverter.toJAVAType(SchemaInfo.refactorColumnType(TABLE_NAME, COREDB_ID));
        AttributeClass ac = new AttributeClass(COREDB_ID,attributeType);

		List<EntityInstance> eis = ecb.readEntities(TABLE_NAME,new ConditionalStatement(ac,Q.E, uuid.toString()));
		if (eis.size() == 1) {
			return (Integer)eis.get(0).get(CSN);
		}
		return -1;
	}
}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.controller;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import coredb.config.Configuration;
import coredb.config.Configuration.Column_Info_Table;
import coredb.config.Configuration.Column_Security_Deny_Table;
import coredb.config.Configuration.Column_Security_Grant_Table;
import coredb.config.Configuration.Mapping_Table;
import coredb.config.Configuration.Row_Security_Deny_Table;
import coredb.config.Configuration.Row_Security_Grant_Table;
import coredb.config.Configuration.Token_Info_Table;
import coredb.config.Configuration.User_Info_Table;
import coredb.config.Configuration.User_Token_Table;
import coredb.database.DatabaseConnection;
import coredb.database.SchemaInfo;
import coredb.mode.RootPermission;
import coredb.security.EntityInstanceBucket;
import coredb.security.ResultSecurity;
import coredb.security.ResultSecurityPassedFailed;
import coredb.security.ResultSecuritySystemList;
import coredb.security.ResultTokens;
import coredb.security.SecurityCRUD;
import coredb.security.TokenFactory;
import coredb.sql.CompoundStatementAuto;
import coredb.sql.CompoundStatementManual;
import coredb.sql.ConditionalStatement;
import coredb.sql.Q;
import coredb.sql.SQLStatement;
import coredb.unit.AttributeClass;
import coredb.unit.CRUD;
import coredb.unit.ClassToTables;
import coredb.unit.EntityInstance;
import coredb.unit.EntitySecurityClass;
import coredb.utils.Describer;
import coredb.utils.HelperWithSecurity;

/**
 * 
 * @author vmc
 */
public class EntityControllerSecurity extends EntityControllerCommon implements IEntityControllerSecurity {
	private EntityControllerBase	ecb	= null;
	private EntityControllerTracing ect = null;

	/**
	 * This constructor constructs an EntityController.
	 *
	 * @param pathToFile the path of database configuration file
	 * @throws SQLException
	 */
	// checked by vmc @108
    public EntityControllerSecurity(String pathToFile) throws SQLException {
		super(pathToFile);
		ecb = new EntityControllerBase(pathToFile);
		ecb.databaseConnection.getConnection().close();
		ecb.databaseConnection = super.databaseConnection;

		ect = new EntityControllerTracing(pathToFile);
		ect.databaseConnection.getConnection().close();
		ect.databaseConnection = super.databaseConnection;

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
	public EntityControllerSecurity(String dbURL, String databaseType, String databaseDriver, String databaseName, String user, String password) throws SQLException {
		super(dbURL, databaseType, databaseDriver, databaseName, user, password);

		ecb = new EntityControllerBase(dbURL, databaseType, databaseDriver, databaseName, user, password);
		ecb.databaseConnection.getConnection().close();
		ecb.databaseConnection = super.databaseConnection;

		ect = new EntityControllerTracing(dbURL, databaseType, databaseDriver, databaseName, user, password);
		ect.databaseConnection.getConnection().close();
		ect.databaseConnection = super.databaseConnection;

	}

	/**
	 *This method deploys all system built-in tables required by security
	 *
	 * @param classToTablesList the list of table need to create
	 * @param dropTablesFirst if it is true API will drop tables first
	 * @param continueOnError if it is true API will ignore errors
	 * @return the status of the deployment, returns true if action is successful otherwise return false
	 */
	// checked by vmc @1498
	public boolean deployDefinitionListWithSecurity(List<EntityInstance> userTokens,
		List<ClassToTables> classToTablesList,boolean dropTablesFirst, boolean continueOnError) {

		boolean result = this.deployDefinitionList(classToTablesList,dropTablesFirst,continueOnError);
		if (result) {
			LinkedList<ClassToTables> newEntityClasses = new LinkedList<ClassToTables>();
			for(ClassToTables classToTable : classToTablesList){
				if(HelperWithSecurity.hasRowSecurityTables(databaseConnection, classToTable.getName())){
					String mappingTableName          = Mapping_Table.makeMappingTableName(classToTable.getName());
					String rowDenySecurityTableName  = Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(classToTable.getName());
					String rowGrantSecurityTableName = Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(classToTable.getName());

					newEntityClasses.add(databaseConnection.getEntityClass(mappingTableName, false));
					newEntityClasses.add(databaseConnection.getEntityClass(rowDenySecurityTableName, false));
					newEntityClasses.add(databaseConnection.getEntityClass(rowGrantSecurityTableName, false));
				}
			}
			newEntityClasses.addAll(classToTablesList);
			ect.storeTableAndColumnInformation(newEntityClasses);

			EntityInstance root         = this.createEntityInstanceFor(User_Info_Table.makeUserInfoTableName());
			root.set(User_Info_Table.getColumnUserName(), RootPermission.ROOTUSER);
			root.set(User_Info_Table.getColumnPassword(), RootPermission.ROOTUSERPASSWORD);
			root = this.readUser(root);

			// #COREDB-57
			RootPermission.deployRootPermissionToColumns(databaseConnection);
			// #/COREDB-57

			for(ClassToTables classToTables : classToTablesList) {
				EntitySecurityClass entitySecurityClass = HelperWithSecurity.getEntitySecurityClass(databaseConnection, classToTables.getName(), false);
				List<AttributeClass> primaryAttributes = new LinkedList<AttributeClass>();
				List<AttributeClass> nonPrimaryAttributes = new LinkedList<AttributeClass>();

				// #COREDB-55
				List<EntityInstance> defaultTokens = TokenFactory.getDefaultGrantTokens(databaseConnection) ;
				// #/COREDB-55

				for(AttributeClass attribute : entitySecurityClass.getAttributeClassList()) {
					if(attribute.isPrimaryKey()) primaryAttributes.add(attribute);
					else nonPrimaryAttributes.add(attribute);
				}

				// DEFAULT TOKENS TO PRIMARY
				if (primaryAttributes.size() > 0) {
					List<EntityInstance> columnTokenPairs = this.generateTokenColumnPairs(entitySecurityClass.getName(), primaryAttributes.toArray(new AttributeClass[0]), defaultTokens, '+').getPassedEntities();
					this.getDatabaseConnection().create(columnTokenPairs);
				} else {
					throw new Error(this.getClass().getName() + "@deployDefinitionListWithSecurity:No parimary keys in the table " + classToTables.getName() + ", must have one at least");
				}

				// USER TOKENS TO NON-PRIMARY
				if (nonPrimaryAttributes.size() > 0) {
					List<EntityInstance> columnTokenPairs = this.generateTokenColumnPairs(entitySecurityClass.getName(), nonPrimaryAttributes.toArray(new AttributeClass[0]), userTokens, '+').getPassedEntities();
  					this.getDatabaseConnection().create(columnTokenPairs);
				}
			}	
			SecurityCRUD.initialize(super.databaseConnection);

			return true;
		} else {
			return false;
		}
    }

	/**
     * This function drops all tables from the database.
     */
	@Override
	public void dropAllTables() {
		System.out.println("dropAllTables() has been disabled in release v3.2 (security), Please call the alternative method dropAllTables(EntityInstance user)");
	}

	/**
     * This function drops all tables from the database.
     * @param user the executor of dropping tables
     */
	public void dropAllTables(EntityInstance user) {
		try {
			// checking then
	        EntityInstance validatedUser = this.readUser(user);
			
			if(isRootUser(validatedUser, CRUD.DELETE)) super.dropAllTables();
			else System.out.println("This user has no root delete permission needed to drop table");
		} catch (Exception exception){
			System.out.println("Drop tables failed");
			exception.printStackTrace();
		}
	}

	/**
     * This function drops a list of tables from the database.
     * @param tableNames the given list of table Names
     */
	@Override
	public void dropTables(List<String> tableNames) {
		System.out.println("dropTables(List<String> tableNames) has been disabled in release v3.2 (security), Please call the alternative method dropAllTables(EntityInstance user, List<String> tableNames)");
	}

	/**
     * This function drops a list of tables from the database.
     * @param user the executor of dropping tables
     * @param tableNames the given list of table Names
     */
	public void dropAllTables(EntityInstance user, List<String> tableNames) {
		try {
			// checking then
			EntityInstance validatedUser = this.readUser(user);
			
			if(isRootUser(validatedUser, CRUD.DELETE)) super.dropTables(tableNames);
			else System.out.println("This user has no root delete permission needed to drop table");
		} catch (Exception exception){
			System.out.println("Drop tables failed");
			exception.printStackTrace();
		}
	}


    // <CRUD ON USERS>..........................................................

	/**
	 *This method creates a user
	 *
	 * @param creator the executer of this action
	 * @param user the user Instance will be created
	 * @return returns the created user Instance
	 */
	// checked by vmc @1123
	public EntityInstance createUser(EntityInstance creator, EntityInstance user) {
		@SuppressWarnings("unused")
		ResultSecurity result = createEntityWithSecurity(creator, user, null);	// COREDB-166
		addTokensToUser(creator, user, TokenFactory.getDefaultGrantTokens(databaseConnection));
//		addUserToGroup(creator, user, Configuration.DEFAULT);
		return readUser(user);
	}

	/**
	 *This method get the first item of a list of EntityInstance
	 *
	 * @param entities the list of EntityInstance
	 * @param zeroErrorMessage the error message to represent the size of entities is 0
	 * @param manyErrorMessage the error message to represent the size of entities is greater than 1
	 * @return the first item of a list of entities
	 */
	// checked by vmc @1123
	private EntityInstance getOnlyOne(List<EntityInstance> entities, String zeroErrorMessage, String manyErrorMessage) {
		if (entities.size() == 0) {
			System.out.println(zeroErrorMessage);
			return null;
		} else if (entities.size() == 1) {
			return entities.get(0);
		} else {
			System.out.println(manyErrorMessage);
			return null;
		}
	}

	/**
	 * This method reads user Instance
	 *
	 * @param userInstance the user Instance will be read
	 * @return the user Instance (stored in database)
	 */
	// checked by vmc @1123
	public EntityInstance readUser(EntityInstance userInstance) {
		String userName		= (String)userInstance.get(User_Info_Table.getColumnUserName());
		String userPassword = (String)userInstance.get(User_Info_Table.getColumnPassword());
		return readUser(userName, userPassword);
	}

	/**
	 *This method reads user Instance
	 *
	 * @param userName the given userName
	 * @param userPassword the given userPassword
	 * @return the user Instance (stored in database)
	 */
	// checked by vmc @1123
	public EntityInstance readUser(String userName, String userPassword) {
		EntityInstance user = null;
		
		try {
		    user = databaseConnection.createEntityInstanceFor(User_Info_Table.makeUserInfoTableName(), true);
		} catch (NullPointerException ex) {
			System.out.println("The User_Info table does not exsit in DB");
			ex.printStackTrace();
		}
		user.set(User_Info_Table.COLUMN_USERNAME, userName);
		user.set(User_Info_Table.COLUMN_PASSWORD, userPassword);

		CompoundStatementAuto csa = new CompoundStatementAuto(Q.AND);
		csa.addCompoundStatement(user.makeSQLOnColumn(databaseConnection, User_Info_Table.COLUMN_USERNAME, Q.E));
		csa.addCompoundStatement(user.makeSQLOnColumn(databaseConnection, User_Info_Table.COLUMN_PASSWORD, Q.E));

		List<EntityInstance> users = ecb.readEntities(User_Info_Table.makeUserInfoTableName(), csa);
		return getOnlyOne(users,"UserInstance does not exist","UserInstance match too many");
	}

	/**
	 *
	 * @param userId
	 * @param crud
	 * @param entities
	 * @param ignoreFailed
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	private ResultSecurity crudEntitiesWithSecurity(String userId, Character crud, List<EntityInstance> entities, List<EntityInstance> tokens, boolean ignoreFailed) {
		//int changeSetNumber = this.generateLatestChangeSetNumber(userId);
		int changeSetNumber = 100000;
		List<EntityInstance> allPassedEntities = new LinkedList<EntityInstance>();
		List<EntityInstance> allPassedSystemEntities = new LinkedList<EntityInstance>();
		List<EntityInstance> allFailedEntities = new LinkedList<EntityInstance>();

		EntityInstanceBucket entityInstanceBucket = new EntityInstanceBucket(entities);
		
		while (entityInstanceBucket.getIndexes().size() > 0) {
			List<EntityInstance> bucket = entityInstanceBucket.popNextBucket();
			String tableName = bucket.get(0).getDynaClass().getName();

			ResultSecuritySystemList passedSystemFailedEntities = null;
			if			(CRUD.CREATE == crud) {
				if (tokens == null || tokens.size() == 0)
					passedSystemFailedEntities = SecurityCRUD.generateEntitiesWithSecurityForCreateAction(databaseConnection,	userId, tableName, bucket);
				else {
					tokens.addAll(RootPermission.getRootTokens(databaseConnection));// COREDB-166 add root tokens
					passedSystemFailedEntities = SecurityCRUD.generateEntitiesWithSecurityForCreateAction(databaseConnection,	userId, tableName, bucket, tokens);
				}
			} else if	(CRUD.READ	 == crud) {
				System.out.println("Error: Not Supposed To Be Used Here.");
				System.exit(0);
			} else if	(CRUD.UPDATE == crud) {
				passedSystemFailedEntities = SecurityCRUD.generateEntitiesWithSecurityForUpdateAction(databaseConnection,	userId, tableName, bucket);
			} else if	(CRUD.DELETE == crud) {
				passedSystemFailedEntities = SecurityCRUD.generateEntitiesWithSecurityForDeleteAction(databaseConnection,	userId, tableName, bucket);
			}
			allPassedEntities.addAll(passedSystemFailedEntities.getAllPassedEntities());
			allPassedSystemEntities.addAll(passedSystemFailedEntities.getAllPassedSystemEntities());
			allFailedEntities.addAll(passedSystemFailedEntities.getAllFailedEntities());
		}

//Describer.describeEntityInstances(allPassedSystemEntities);

		ResultSecurity resultSecurity	    = crudSecurityGeneral(crud, changeSetNumber, allPassedEntities, allFailedEntities, ignoreFailed);
		//@SuppressWarnings("unused")
		ResultSecurity resultSystemSecurity = crudSecurityGeneral(crud, changeSetNumber, allPassedSystemEntities, allFailedEntities, ignoreFailed);

		//<#coredb-58 value="Unreadable Value">
		if		(CRUD.CREATE == crud)	 ;
		else if	(CRUD.READ   == crud)	 ;
		else if	(CRUD.UPDATE == crud)	resultSecurity = HelperWithSecurity.setUnreadableColumns(databaseConnection, userId, resultSecurity);
		else if	(CRUD.DELETE == crud)	resultSecurity = HelperWithSecurity.setUnreadableColumns(databaseConnection, userId, resultSecurity);
		//</#coredb-58>
		
        return resultSecurity;
	}

	/**
	 * 
	 * @param crud
	 * @param changeSetNumber
	 * @param allPassedEntities
	 * @param allFailedEntities
	 * @param ignoreFailed
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	// draft
	private ResultSecurity crudSecurityGeneral(Character crud, int changeSetNumber, List<EntityInstance> allPassedEntities, List<EntityInstance> allFailedEntities, boolean ignoreFailed) {
		if ((ignoreFailed || allFailedEntities.size()==0 ) && !allPassedEntities.isEmpty()) {
			String tableName = allPassedEntities.get(0).getDynaClass().getName();
			String expectedMappingTableName = Configuration.Mapping_Table.makeMappingTableName(tableName);
			
			/* set the argument is false to fix the problem mentioned in COREDBUSERS-18*/
			String existingMappingTableName = this.getEntityClass(databaseConnection, tableName, false).getName();
			boolean tracing = expectedMappingTableName.equals(existingMappingTableName);
			if (tracing) {
				if		(CRUD.CREATE == crud)	ect.createEntities(changeSetNumber, allPassedEntities);
				else if (CRUD.UPDATE == crud)	ect.updateEntities(changeSetNumber, allPassedEntities);
				else if (CRUD.DELETE == crud)	ect.deleteEntities(changeSetNumber, allPassedEntities);
				else	System.out.println("CRUD " + crud + " is not supported.");
			} else {
				if		(CRUD.CREATE == crud)	ecb.createEntities(allPassedEntities);
				else if (CRUD.UPDATE == crud)	ecb.updateEntities(allPassedEntities);
				else if (CRUD.DELETE == crud)	ecb.deleteEntities(allPassedEntities);
				else	System.out.println("CRUD " + crud + " is not supported.");
			}
			return new ResultSecurity(allPassedEntities,allFailedEntities,changeSetNumber);
		} else {
			return new ResultSecurity(allPassedEntities,allFailedEntities,-1);
		}
	}

	/**
	 * This method creates a data row(EntityInstance) into database with security checking
	 * 
	 * @param creator the executer of this action
	 * @param entity the EntityInstance will be created
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	public ResultSecurity createEntityWithSecurity(EntityInstance creator, EntityInstance entity) {
		return createEntityWithSecurity(creator, entity, null, false);
	}

	/**
	 * This method creates a data row(EntityInstance) into database with security checking
	 * 
	 * @param creator the executer of this action
	 * @param entity the EntityInstance will be created
	 * @param tokens the tokens will be assigned to the new EntityInstance
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	public ResultSecurity createEntityWithSecurity(EntityInstance creator, EntityInstance entity, List<EntityInstance> tokens) {
		return createEntityWithSecurity(creator, entity, tokens, false);
	}
	
	/**
	 * 
	 * @param creator the executer of this action
	 * @param entity
	 * @param ignoreFailed
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	private ResultSecurity createEntityWithSecurity(EntityInstance creator, EntityInstance entity, List<EntityInstance> tokens, boolean ignoreFailed) {
 		List<EntityInstance> entities = new LinkedList<EntityInstance>();
		entities.add(entity);
		return createEntitiesWithSecurity(creator, entities, tokens, ignoreFailed);
	}
	
	/**
	 * This method creates a list of data rows(EntityInstances) into database with Security checking
	 * 
	 * @param creator the executer of this action
	 * @param entities the list of EntityInstances will be created
	 * @param tokens the tokens will be assigned to the new EntityInstances
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	public ResultSecurity createEntitiesWithSecurity(EntityInstance creator, List<EntityInstance> entities, List<EntityInstance> tokens) {
		return createEntitiesWithSecurity(creator, entities, tokens, false);
	}

	/**
	 * This method creates a list of data rows(EntityInstances) into database with Security checking
	 * 
	 * @param creator the executer of this action
	 * @param entities the list of EntityInstances will be created 
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	public ResultSecurity createEntitiesWithSecurity(EntityInstance creator, List<EntityInstance> entities) {
		return createEntitiesWithSecurity(creator, entities, null, false);
	}
	/**
	 * 
	 * @param creator the executer of this action
	 * @param entities
	 * @param ignoreFailed
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	private ResultSecurity createEntitiesWithSecurity(EntityInstance creator, List<EntityInstance> entities, List<EntityInstance> tokens, boolean ignoreFailed) {
		ResultSecurity resultSecurity = new ResultSecurity(new LinkedList<EntityInstance>(), entities, -1);
		
		EntityInstance validated_creator = this.readUser(creator);
		if(validated_creator != null){
			String creatorId = (String)creator.get(Configuration.User_Info_Table.getColumnUserID());
			resultSecurity = crudEntitiesWithSecurity(creatorId, CRUD.CREATE, entities, tokens, ignoreFailed);
		}
		
		return resultSecurity;
	}

	/**
	 * This method reads a list of data rows(EntityInstances) from Database with Security checking
	 * 
	 * @param reader the executer of this action
	 * @param tableName the name of the table 
	 * @param sql the sql statement used to read
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	/*
	public ResultSecurity readEntitiesWithSecurity(EntityInstance reader, String tableName, String sql) {
		return readEntitiesWithSecurity(reader, tableName, sql);
	}
	 */

	/**
	 * This method reads a list of data rows(EntityInstances) from Database with Security checking
	 * 
	 * @param reader the executer of this action
	 * @param tableName the name of the table 
	 * @param sql the SQLStatement object used to read
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	public ResultSecurity readEntitiesWithSecurity(EntityInstance reader, String tableName, SQLStatement sql) {
		ResultSecurity resultSecurity = new ResultSecurity(new LinkedList<EntityInstance>(), new LinkedList<EntityInstance>(), -1);
		
		EntityInstance validated_reader = this.readUser(reader);
		if(validated_reader != null){
			String readerId = (String)reader.get(Configuration.User_Info_Table.getColumnUserID());
            //ResultSecurityPassedFailed rspf = SecurityCRUD.readEntitiesSlow(databaseConnection, readerId, tableName, sql);
            ResultSecurityPassedFailed rspf = SecurityCRUD.readEntities(databaseConnection, readerId, tableName, sql);

            resultSecurity = new ResultSecurity(rspf,0);
		} 
		
        return resultSecurity;
	}

	/**
     * This method updates a data row(EntityInstance) of database with Security checking
     * 
     * @param updator the executer of this action
     * @param entity the EntityInstance will be updated
     * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
     */
	public ResultSecurity updateEntityWithSecurity(EntityInstance updator, EntityInstance entity) {
		return this.updateEntityWithSecurity(updator, entity, false);
	}
	
	/**
	 * 
	 * @param updator the executer of this action
	 * @param entity
	 * @param ignoredFailed
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	private ResultSecurity updateEntityWithSecurity(EntityInstance updator, EntityInstance entity, boolean ignoredFailed) {
		List<EntityInstance> entities = new LinkedList<EntityInstance>();
		entities.add(entity);
		return this.updateEntitiesWithSecurity(updator, entities, ignoredFailed);
	}
	
	/**
	 * This method updates a list of data rows(EntityInstances) of database with Security checking
	 * 
	 * @param updator the executer of this action
	 * @param entities the list of EntityInstances will be updated
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	public ResultSecurity updateEntitiesWithSecurity(EntityInstance updator, List<EntityInstance> entities) {
		return this.updateEntitiesWithSecurity(updator, entities, false);
	}
	
	/**
	 * 
	 * @param updator the executer of this action
	 * @param entities
	 * @param ignoreFailed
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	private ResultSecurity updateEntitiesWithSecurity(EntityInstance updator, List<EntityInstance> entities, boolean ignoreFailed) {
		ResultSecurity resultSecurity = new ResultSecurity(new LinkedList<EntityInstance>(), entities, -1);
		
		EntityInstance validated_updator = this.readUser(updator);
		if(validated_updator != null){
			String updatorId = (String)updator.get(Configuration.User_Info_Table.getColumnUserID());
			resultSecurity = crudEntitiesWithSecurity(updatorId, CRUD.UPDATE, entities, null,ignoreFailed); // COREDB-166 is not used here //
		}
		
        return resultSecurity;
	}
	
	/**
     * This method deletes a data row(EntityInstance) from database with Security checking.
     * 
     * @param deletor the executer of this action
     * @param entity the EntityInstance will be deleted
     * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
     */
	public ResultSecurity deleteEntityWithSecurity(EntityInstance deletor, EntityInstance entity) {
		return deleteEntityWithSecurity(deletor, entity, false);
	}
	
	/**
	 * 
	 * @param deletor the executer of this action
	 * @param entity
	 * @param ignoreFailed
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	private ResultSecurity deleteEntityWithSecurity(EntityInstance deletor, EntityInstance entity, boolean ignoreFailed) {
		List<EntityInstance> entities = new LinkedList<EntityInstance>();
		entities.add(entity);
		return deleteEntitiesWithSecurity(deletor, entities, ignoreFailed);
	}

	/**
	 * This method deletes a list of data rows(EntityInstances) from database with Security checking
	 * 
	 * @param deleter
	 * @param entities the list of EntityInstances will be deleted
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	public ResultSecurity deleteEntitiesWithSecurity(EntityInstance deleter, List<EntityInstance> entities) {
		return this.deleteEntitiesWithSecurity(deleter, entities, false);
	}

	/**
	 * 
	 * @param deletor the executer of this action
	 * @param entities
	 * @param ignoreFailed
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	private ResultSecurity deleteEntitiesWithSecurity(EntityInstance deleter, List<EntityInstance> entities, boolean ignoreFailed) {
	    ResultSecurity resultSecurity = new ResultSecurity(new LinkedList<EntityInstance>(), entities, -1);
		
		EntityInstance validated_deletor = this.readUser(deleter);
		if(validated_deletor != null){
			String deletorId = (String)deleter.get(Configuration.User_Info_Table.getColumnUserID());
			resultSecurity = crudEntitiesWithSecurity(deletorId, CRUD.DELETE, entities, null, ignoreFailed); // COREDB-166 is not used here //

		}
		
		return resultSecurity;
	}

	/**
	 * This method adds a group permission as a list of tokens to a specified user
	 * 
	 * @param creator the executer of this action
	 * @param receiver the user who will be given by a group permission
	 * @param group the group name of the tokens
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 *
	 */
	// checked by vmc @1213
	public ResultSecurity addUserToGroup(EntityInstance creator, EntityInstance receiver, String group) {
		ResultSecurity resultSecurity = new ResultSecurity(new LinkedList<EntityInstance>(), new LinkedList<EntityInstance>(), -1);
		
		EntityInstance validated_creator  = this.readUser(creator);
		EntityInstance validated_receiver = this.readUser(receiver);
		if(validated_creator != null && validated_receiver != null) {
			String receiverId	= (String)receiver.get(Configuration.User_Info_Table.getColumnUserID());
			List<EntityInstance> tokens = this.getTokensFromGroup(group);
			if(tokens.size() > 0) {
				synchronized (this) {
					List<EntityInstance> userTokenPairs = new LinkedList<EntityInstance>();
					for(EntityInstance token : tokens) {
						String tokenId = (String)token.get(Token_Info_Table.getColumnTokenID());
						EntityInstance userTokenPair = this.createEntityInstanceFor(User_Token_Table.makeUserTokenTableName());
						userTokenPair.set(User_Token_Table.getColumnTokenID(), tokenId);
						userTokenPair.set(User_Token_Table.getColumnUserID(), receiverId);
						
						String SQL = userTokenPair.makeSQLOnNonPrimaryWithEqual(databaseConnection);
						int size = ecb.readEntities(User_Token_Table.makeUserTokenTableName(), SQL).size();
						if (size == 0) userTokenPairs.add(userTokenPair);
					}
					resultSecurity = this.createEntitiesWithSecurity(creator, userTokenPairs, null); // COREDB-166 need default tokens
				}
			} else {
				System.out.println(EntityControllerSecurity.class.getName() + "@addUserToGroup: The group does not exist");
			}
		} else {
			System.out.println(
				"Error: " +
				"can not validate creator with the given username and password or \n" +
				"can not validate user with the given username and password"
			);
		}
		
		return resultSecurity;
	}

	/**
	 * This method removes a group permission as a list of tokens from a specified user
	 * 
	 * @param deletor the executer of this action
	 * @param deletee the user whose group tokens will be removed
	 * @param group the group name of the tokens
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	// checked by vmc @1213
	public ResultSecurity removeUserFromGroup(EntityInstance deletor, EntityInstance deletee, String group) {
		ResultSecurity resultSecurity = new ResultSecurity(new LinkedList<EntityInstance>(), new LinkedList<EntityInstance>(), -1);
		
		EntityInstance validated_deletor = this.readUser(deletor);
		EntityInstance validated_deletee = this.readUser(deletee);
		if(validated_deletor != null && validated_deletee != null) {
			String deleteeId = (String)deletee.get(Configuration.User_Info_Table.getColumnUserID());
			List<EntityInstance> tokens = this.getTokensFromGroup(group);
			
			if(tokens.size() > 0) {
				synchronized (this) {
					List<EntityInstance> userTokenPairs = new LinkedList<EntityInstance>();
					for(EntityInstance token : tokens) {
						String tokenId = (String)token.get(Token_Info_Table.getColumnTokenID());
						CompoundStatementManual usersTokens_CSM = new CompoundStatementManual();
						usersTokens_CSM.addConditionalStatement(User_Token_Table.getColumnUserID() + Q.E + Q.QUOT + deleteeId + Q.QUOT);
						usersTokens_CSM.addConditionalStatement(Q.AND);
						usersTokens_CSM.addConditionalStatement(User_Token_Table.getColumnTokenID() + Q.E + Q.QUOT + tokenId + Q.QUOT);
						userTokenPairs.addAll(ecb.readEntities(User_Token_Table.makeUserTokenTableName(), usersTokens_CSM));
					}
					resultSecurity = this.deleteEntitiesWithSecurity(deletor, userTokenPairs);
				}
			}else {
				System.out.println(EntityControllerSecurity.class.getName() + "@removeUserFromGroup: The group does not exist");
			}
		}else {
			System.out.println(
				"Error: " +
				"can not validate creator with the given username and password or \n" +
				"can not validate user with the given username and password"
			);
		}
		
		return resultSecurity;
	}
	
	/**
	 * This method reads the list of tokens that belonged to given Group
	 * 
	 * @param group the given Group name
	 * @return the list of token Instances that belonged to given Group
	 */
	// checked by vmc @1213
	private List<EntityInstance> getTokensFromGroup(String group){
		CompoundStatementManual tokens_CSM = new CompoundStatementManual();
		tokens_CSM.addConditionalStatement(Token_Info_Table.getColumnGroup() + Q.E + Q.QUOT + group + Q.QUOT);
		return ecb.readEntities(Token_Info_Table.makeTokenInfoTableName(), tokens_CSM);
	}
	
	// </SECURITY: TOKEN_USER >..........................................

	/**
	 * This method adds a list of tokens onto a list of Columns
	 *
	 * @param creator the executer of this action
	 * @param tableName the name of table
	 * @param columns the list of Columns
	 * @param tokens the list of tokens
	 * @return  the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	// </SECURITY: TOKEN_USER >.............................................
	public ResultSecurity addTokensToColumns(EntityInstance creator, String tableName, AttributeClass[] columns, List<EntityInstance> tokens) {
		ResultSecurity resultSecurity = new ResultSecurity(new LinkedList<EntityInstance>(), new LinkedList<EntityInstance>(), -1);
		//int changeSetNumber  = this.generateLatestChangeSetNumber(userId);
		EntityInstance validated_creator = this.readUser(creator);
		if(validated_creator != null) {
			// the 3nd argument is true : means this method called by creating
			// #COREDB-55
			//getNonPrimaryKeyColumns(columns, "addTokensToColumns");
			tableName = SchemaInfo.refactorTableName(tableName);
			ResultSecurity resultSecurity_Generate = this.generateTokenColumnPairs(tableName, columns, tokens, '+');
			// #/COREDB-55
			resultSecurity = this.createEntitiesWithSecurity(creator, resultSecurity_Generate.getPassedEntities(),null);
			resultSecurity.getFailedEntities().addAll(resultSecurity_Generate.getFailedEntities());
			
		} else {
			System.out.println("Can not validate user with the given username and password");
		}
		
		return resultSecurity;
	}

	/**
	 * This method deletes a list of tokens from a list of Columns
	 *
	 * @param deletor the executer of this action
	 * @param tableName the name of table
	 * @param columns the list of Columns
	 * @param tokens the list of tokens
	 * @return  the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	public ResultSecurity removeTokensFromColumns(EntityInstance deletor, String tableName, AttributeClass[] columns, List<EntityInstance> tokens) {
		ResultSecurity resultSecurity = new ResultSecurity(new LinkedList<EntityInstance>(), new LinkedList<EntityInstance>(), -1);
		
		// int changeSetNumber = this.generateLatestChangeSetNumber(userId);
		
		EntityInstance validated_deletor = this.readUser(deletor);
		if(validated_deletor != null){
			// the 3nd argument is false : means this method called by removing
			// #COREDB-55
			//getNonPrimaryKeyColumns(columns, "removeTokensFromColumns");
			tableName = SchemaInfo.refactorTableName(tableName);
			ResultSecurity resultSecurity_Generated = this.generateTokenColumnPairs(tableName, columns, tokens, '-');
			// #/COREDB-55
			resultSecurity = this.deleteEntitiesWithSecurity(deletor, resultSecurity_Generated.getPassedEntities());
			resultSecurity.getFailedEntities().addAll(resultSecurity_Generated.getFailedEntities());
			
		} else {
			System.out.println("Can not validate user with the given username and password");
		}
		
		return resultSecurity;
	}
	/**
	 * 
	 */
	// checked by vmc @1249
	private AttributeClass[] getNonPrimaryKeyColumns(AttributeClass[] columns, String methodName){
		List<AttributeClass> columnsWithoutPrimaryKey = new LinkedList<AttributeClass>();
		for(AttributeClass attribute : columns){
			if(attribute.isPrimaryKey())    System.out.println(this.getClass().getName() + "@" + methodName + ": Security should be invalid on primray keys");
			else                        	columnsWithoutPrimaryKey.add(attribute);
		}
		return columnsWithoutPrimaryKey.toArray(new AttributeClass[0]);
		
	}
	// #/COREDB-55
	
	/**
	 * This method generates a list of Column-Token-Pair.
	 * 
	 * @param tableName name of the table these columns belonged
	 * @param columns given list of columns
	 * @param tokens given list of tokens
	 * @return a list of ColumnTokenPair instances
	 */
	// checked by vmc @1352
	private synchronized ResultSecurity generateTokenColumnPairs(String tableName, AttributeClass[] columns, List<EntityInstance> tokens, char sign) {
		List<EntityInstance> passedTokenRowPairs = new LinkedList<EntityInstance>();
		List<EntityInstance> failedTokenRowPairs = new LinkedList<EntityInstance>();
		ResultSecurity resultSecurity            = new ResultSecurity(passedTokenRowPairs, failedTokenRowPairs, -1);
		
		for (int i=0; i<columns.length; i++) {
			int columnId = getColumnId(tableName, columns[i]);

			for (Iterator<EntityInstance> j= tokens.iterator(); j.hasNext(); ) {
				EntityInstance token = j.next();

				int size = -1;
				CompoundStatementAuto sqlStatementAuto = new CompoundStatementAuto(Q.AND);
				EntityInstance tableColumnTokenPair;
				List<EntityInstance> resultPairs = null;
				if ((Boolean)token.get(Token_Info_Table.getColumnPermission())) {
					tableColumnTokenPair = this.createEntityInstanceFor(Column_Security_Grant_Table.makeColumnSecurityGrantTableName());
					tableColumnTokenPair.set(Column_Security_Grant_Table.getColumnColumnID(), columnId);
					tableColumnTokenPair.set(Column_Security_Grant_Table.getColumnTokenID(),token.get(Token_Info_Table.getColumnTokenID()));

					sqlStatementAuto.addCompoundStatement(tableColumnTokenPair.makeSQLOnColumn(databaseConnection,Column_Security_Grant_Table.getColumnColumnID() , Q.E));
					sqlStatementAuto.addCompoundStatement(tableColumnTokenPair.makeSQLOnColumn(databaseConnection,Column_Security_Grant_Table.getColumnTokenID() , Q.E));
					resultPairs = ecb.readEntities(Column_Security_Grant_Table.makeColumnSecurityGrantTableName(), sqlStatementAuto.toSQLStatement());
					size = resultPairs.size();

				} else {
					tableColumnTokenPair = this.createEntityInstanceFor(Column_Security_Deny_Table.makeColumnSecurityDenyTableName());
					tableColumnTokenPair.set(Column_Security_Deny_Table.getColumnColumnID(), columnId);
					tableColumnTokenPair.set(Column_Security_Deny_Table.getColumnTokenID(),token.get(Token_Info_Table.getColumnTokenID()));
					
					sqlStatementAuto.addCompoundStatement(tableColumnTokenPair.makeSQLOnColumn(databaseConnection,Column_Security_Deny_Table.getColumnColumnID() , Q.E));
					sqlStatementAuto.addCompoundStatement(tableColumnTokenPair.makeSQLOnColumn(databaseConnection,Column_Security_Deny_Table.getColumnTokenID() , Q.E));
					resultPairs = ecb.readEntities(Column_Security_Deny_Table.makeColumnSecurityDenyTableName(), sqlStatementAuto.toSQLStatement());
					size = resultPairs.size();

				}
				
				if(sign == '+') {
                	if(size == 0) passedTokenRowPairs.add(tableColumnTokenPair);
                	else          failedTokenRowPairs.add(tableColumnTokenPair);
                } 
				if(sign == '-') {
                    if(size > 0)  passedTokenRowPairs.addAll(resultPairs);
                	else          failedTokenRowPairs.add(tableColumnTokenPair);
                }
			}
		}
		
		return resultSecurity;
	}

	/**
	 * This method read the columnID 
	 * 
	 * @param tableName
	 * @param column
	 * @return the Column Id
	 */
	// checked by vmc @1316
	private synchronized int getColumnId(String tableName, AttributeClass column) {

		CompoundStatementAuto sqlStatementAuto = new CompoundStatementAuto(Q.AND);
		sqlStatementAuto.addCompoundStatement(Column_Info_Table.getColumnTableName() + Q.E + Q.QUOT + tableName + Q.QUOT);
		sqlStatementAuto.addCompoundStatement(Column_Info_Table.getColumnColumnName() + Q.E + Q.QUOT + column.getName() + Q.QUOT);
	//	System.out.println(sqlStatementAuto.toSQLStatement());
		List<EntityInstance> entities = ecb.readEntities(Column_Info_Table.makeColumnInfoTableName(), sqlStatementAuto);

		if(entities.size() == 1) return (Integer)entities.get(0).get(Column_Info_Table.makeColumnInfoTableColumnNameCoredbId());
		else                     throw new Error( this.getClass().getName() + "@getColumnId: The column id is not unqiue" );
	}

/*
	public void printSecurityTables(DatabaseConnection databaseConnection, String tableName, boolean reRead) {
		
		List<String> securityTableNames = new LinkedList<String>();
		securityTableNames.add(Configuration.Column_Security_Grant_Table.makeColumnSecurityGrantTableName());
		securityTableNames.add(Configuration.Column_Security_Deny_Table.makeColumnSecurityDenyTableName());
		
		securityTableNames.add(Configuration.Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(tableName));
		securityTableNames.add(Configuration.Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(tableName));
		
		securityTableNames.add(Configuration.Column_Info_Table.makeColumnInfoTableName());
		securityTableNames.add(Configuration.Token_Info_Table.makeTokenInfoTableName());
		securityTableNames.add(Configuration.User_Info_Table.makeUserInfoTableName());
		securityTableNames.add(Configuration.User_Token_Table.makeUserTokenTableName());
		
		for(String securityTableName : securityTableNames) {
			Describer.describeTable(databaseConnection, securityTableName, "");
		}
 	}
//*/

	/**
	 * This method adds a list of tokens onto a list of data rows
	 *
	 * @param creator the executer of this action
	 * @param rows the list of data rows
	 * @param tokens the list of tokens
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	public ResultSecurity addTokensToRows(EntityInstance creator, List<EntityInstance> rows, List<EntityInstance> tokens) {
		ResultSecurity rowTokenPairs = new ResultSecurity(new LinkedList<EntityInstance>(), new LinkedList<EntityInstance>(), -1);
		
		EntityInstance validated_creator = this.readUser(creator);
		if(validated_creator != null){
			if((isRootUser(validated_creator, CRUD.CREATE))) {
				rowTokenPairs = this.generateTokenRowPairs(tokens, rows, true);
				if(!rowTokenPairs.getPassedEntities().isEmpty()) {
					this.getDatabaseConnection().create(rowTokenPairs.getPassedEntities());
				}
    		} else {
    			System.out.println("This user has no permission to add tokens to rows");
    		}
			
		} else {
			System.out.println("Can not validate user with the given username and password");
		}
		
		return rowTokenPairs;
	}
	
	/**
	 * This method adds a list of tokens onto a data rows
	 *
	 * @param creator the executer of this action
	 * @param row the data rows
	 * @param tokens the list of tokens
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	public ResultSecurity addTokensToRow(EntityInstance creator, EntityInstance row, List<EntityInstance> tokens) {
		List<EntityInstance> rows = new LinkedList<EntityInstance>();
		rows.add(row);
		return addTokensToRows(creator, rows, tokens);
	}

	/**
	 * This method removes a list of tokens from a list of data rows
	 *
	 * @param deletor the executer of this action
	 * @param rows the list of data rows
	 * @param tokens the list of tokens
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	public ResultSecurity removeTokensFromRows(EntityInstance deletor, List<EntityInstance> rows, List<EntityInstance> tokens) {
		ResultSecurity rowTokenPairs = new ResultSecurity(new LinkedList<EntityInstance>(), new LinkedList<EntityInstance>(), -1);
		
		EntityInstance validated_deletor = this.readUser(deletor);
		if(validated_deletor != null){
			if((isRootUser(validated_deletor, CRUD.DELETE))) {
				
				rowTokenPairs = this.generateTokenRowPairs(tokens, rows, false);
				if(!rowTokenPairs.getPassedEntities().isEmpty()) {
					this.getDatabaseConnection().delete(rowTokenPairs.getPassedEntities());
				}
				
    		} else { System.out.println("This user has no permission to add tokens to rows");}
			
		}else{ System.out.println("Can not validate user with the given username and password");}
		
		return rowTokenPairs;
	}

	/**
	 * This method generates a list of RowTokenPair instances based on given list of rows and a list of tokens
	 *
	 * @param rows given list of rows
	 * @param tokens given list of tokens
	 * @param isCreateMode mode identifier, true means create mode otherwise delete mode
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	private ResultSecurity generateTokenRowPairs(List<EntityInstance> tokens, List<EntityInstance> rows, boolean isCreateMode) {
		List<EntityInstance> passedTokenRowPairs = new LinkedList<EntityInstance>();
		List<EntityInstance> failedTokenRowPairs = new LinkedList<EntityInstance>();
		ResultSecurity resultSecurity            = new ResultSecurity(passedTokenRowPairs, failedTokenRowPairs, -1);

		EntityInstanceBucket rowBucket = new EntityInstanceBucket(rows);
		while (rowBucket.getIndexes().size() > 0) {
			List<EntityInstance> bucket = rowBucket.popNextBucket();
			String baseTableName = bucket.get(0).getDynaClass().getName();
            if(HelperWithSecurity.hasRowSecurityTables(this.getDatabaseConnection(), baseTableName)) {
               for(EntityInstance row : bucket){
    		       // read EntityID
    			   String sql = row.makeSQLOnIdentifiableAttributes(this.databaseConnection);
    			   String mappingTableName = Configuration.Mapping_Table.makeMappingTableName(baseTableName);
    			   List<EntityInstance> mappingEntityInstances = ecb.readEntities(mappingTableName, sql);
    			   String entityId;
    			   if(mappingEntityInstances.size() == 1) {
    			       entityId = mappingEntityInstances.get(0).get(Configuration.Tracing_Table.makeMappingTableColumnNameCoredbId(baseTableName)).toString();
    			   } else continue;// might be need to throw an exception
  
                   for(EntityInstance token : tokens){
    			       String tokenId  = (String)token.get(Token_Info_Table.getColumnTokenID());
    			       
    				   EntityInstance tableRowTokenPair = null;
    				   int size = -1;
    				   CompoundStatementAuto sqlStatementAuto = new CompoundStatementAuto(Q.AND);
    				   List<EntityInstance> resultPairs = null;
    				   if ((Boolean)token.get(Token_Info_Table.getColumnPermission())) {
    				       tableRowTokenPair = this.createEntityInstanceFor(Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(baseTableName));
    				       tableRowTokenPair.set(Row_Security_Grant_Table.getColumnEntityInstanceID(), entityId);
    				       tableRowTokenPair.set(Row_Security_Grant_Table.getColumnTokenID(), tokenId);
    						
    					   sqlStatementAuto.addCompoundStatement(tableRowTokenPair.makeSQLOnColumn(databaseConnection,Row_Security_Grant_Table.getColumnEntityInstanceID() , Q.E));
    					   sqlStatementAuto.addCompoundStatement(tableRowTokenPair.makeSQLOnColumn(databaseConnection,Row_Security_Grant_Table.getColumnTokenID() , Q.E));
    					   resultPairs = ecb.readEntities(Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(baseTableName), sqlStatementAuto.toSQLStatement());
    					   size = resultPairs.size();
    					} else {
    						tableRowTokenPair = this.createEntityInstanceFor(Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(baseTableName));
    						tableRowTokenPair.set(Row_Security_Deny_Table.getColumnEntityInstanceID(), entityId);
    						tableRowTokenPair.set(Row_Security_Deny_Table.getColumnTokenID(), tokenId);
    						
    						sqlStatementAuto.addCompoundStatement(tableRowTokenPair.makeSQLOnColumn(databaseConnection,Row_Security_Deny_Table.getColumnEntityInstanceID() , Q.E));
    						sqlStatementAuto.addCompoundStatement(tableRowTokenPair.makeSQLOnColumn(databaseConnection,Row_Security_Deny_Table.getColumnTokenID() , Q.E));
    						resultPairs = ecb.readEntities(Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(baseTableName), sqlStatementAuto.toSQLStatement());
    						size = resultPairs.size();
    					}
    					
                        if(isCreateMode) {
                        	if(size == 0) passedTokenRowPairs.add(tableRowTokenPair);
                        	else          failedTokenRowPairs.add(tableRowTokenPair);
                        } else {
                        	if(size > 0)  passedTokenRowPairs.addAll(resultPairs);
                        	else          failedTokenRowPairs.add(tableRowTokenPair);
                        }
    				}
                }
			}
		}
		
		return resultSecurity;
	}
	
	/**
	 * This method gets all tokens belonging to given user
	 * 
	 * @param reader the given reader
	 * @param user the given user
	 * @return returns all tokens belonging to user, split tokens into grant and deny ones.
	 */
    public ResultTokens getTokensByUser(EntityInstance reader, EntityInstance user) {
    	List<EntityInstance> tokens      = new LinkedList<EntityInstance>();
    	List<EntityInstance> grantTokens = new LinkedList<EntityInstance>();
    	List<EntityInstance> denyTokens  = new LinkedList<EntityInstance>();
    	ResultTokens resultTokens        = new ResultTokens(grantTokens, denyTokens);
    	
    	EntityInstance validatedReader = this.readUser(reader);
    	
    	if(validatedReader != null) {
    		if((isRootUser(validatedReader, CRUD.UPDATE))) {
    			String userTokenTableName = User_Token_Table.makeUserTokenTableName();
        		AttributeClass ac_userID = DatabaseConnection.getAttributeClass(userTokenTableName, User_Token_Table.getColumnUserID());
        		String userID = (String) user.get(User_Info_Table.getColumnUserID());
        		
        		CompoundStatementManual findToken   = new CompoundStatementManual();

        		findToken.addConditionalStatement(Token_Info_Table.getColumnTokenID());
        		findToken.addConditionalStatement(Q.IN);
        		findToken.addConditionalStatement(Q.LEFT_PARENTHESIS);
        		
                findToken.addConditionalStatement(Q.SELECT + Q.DISTINCT + User_Token_Table.getColumnTokenID());
                findToken.addConditionalStatement(Q.FROM);
                findToken.addConditionalStatement(userTokenTableName);
                findToken.addConditionalStatement(Q.WHERE);
                findToken.addConditionalStatement(new ConditionalStatement(ac_userID, Q.E, userID));
        		
                findToken.addConditionalStatement(Q.RIGHT_PARENTHESIS);
        		
        		tokens = this.databaseConnection.read(Token_Info_Table.makeTokenInfoTableName(), findToken.toSQLStatement());
    		} else {
    			System.out.println("This user has no permission to read tokens belong to user [" + user.get(User_Info_Table.COLUMN_USERNAME) + "]");
    			return resultTokens;
    		}
    		
    	} else {
    		System.out.println("Can not validate user with the given username and password");
    		return resultTokens;
    	}
    
    	while (!tokens.isEmpty()) {
    		EntityInstance token = tokens.get(0);
			if((Boolean)token.get(Token_Info_Table.getColumnPermission())) grantTokens.add(token);
			else                                                           denyTokens.add(token);
			
			tokens.remove(0);
		}
    	
    	return resultTokens;
	}
    
    /**
	 * This method gets all tokens belonging to given row
	 * 
	 * @param reader the given reader
	 * @param row the given row
	 * @return returns all tokens belonging to row, split tokens into grant and deny ones.
	 */
	//vmc not need for now
    public ResultTokens getTokensByRow(EntityInstance reader, EntityInstance row) {
    	List<EntityInstance> grantTokens = new LinkedList<EntityInstance>();
    	List<EntityInstance> denyTokens  = new LinkedList<EntityInstance>();
    	ResultTokens resultTokens        = new ResultTokens(grantTokens, denyTokens);
    	
    	EntityInstance validatedReader = this.readUser(reader);
    	
    	if(validatedReader != null) {
    		if((isRootUser(validatedReader, CRUD.READ))) {
    			String baseTableName          = row.getDynaClass().getName();
    			String rowTokenGrantTableName = Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(baseTableName);
    			String rowTokenDenyTableName  = Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(baseTableName);
    			
    			// read EntityID
 			    String sql = row.makeSQLOnIdentifiableAttributes(this.databaseConnection);
 			    String mappingTableName = Configuration.Mapping_Table.makeMappingTableName(baseTableName);
 			    List<EntityInstance> mappingEntityInstances = ecb.readEntities(mappingTableName, sql);
 			    String entityId;
 			    if(mappingEntityInstances.size() == 1) {
 			        entityId = mappingEntityInstances.get(0).get(Configuration.Tracing_Table.makeMappingTableColumnNameCoredbId(baseTableName)).toString();
 			    } else {
 			    	return resultTokens;
 			    }
    			
 			    // get grant and deny tokens
 			    AttributeClass ac_GrantEntityID = DatabaseConnection.getAttributeClass(rowTokenGrantTableName, Row_Security_Grant_Table.getColumnEntityInstanceID());
 			    AttributeClass ac_DenyEntityID  = DatabaseConnection.getAttributeClass(rowTokenDenyTableName,  Row_Security_Deny_Table.getColumnEntityInstanceID());
    			grantTokens = getTokensByRow(rowTokenGrantTableName, Row_Security_Grant_Table.getColumnTokenID(), ac_GrantEntityID, entityId);
    			denyTokens  = getTokensByRow(rowTokenDenyTableName,  Row_Security_Deny_Table.getColumnTokenID(), ac_DenyEntityID,  entityId);
        		
    			resultTokens = new ResultTokens(grantTokens, denyTokens);
    		} else { System.out.println("This user has no root permission");}
    		
    	} else { System.out.println("Can not validate user with the given username and password");}
    	
    	return resultTokens;
	}
    
    private List<EntityInstance> getTokensByRow(String rowTokenTableName, String columnTokenIdName, AttributeClass ac_EntityID, String entityId) {
		
		CompoundStatementManual findToken   = new CompoundStatementManual();
		findToken.addConditionalStatement(Token_Info_Table.getColumnTokenID());
		findToken.addConditionalStatement(Q.IN);
		findToken.addConditionalStatement(Q.LEFT_PARENTHESIS);
		
        findToken.addConditionalStatement(Q.SELECT + Q.DISTINCT + columnTokenIdName);
        findToken.addConditionalStatement(Q.FROM);
        findToken.addConditionalStatement(rowTokenTableName);
        findToken.addConditionalStatement(Q.WHERE);
        findToken.addConditionalStatement(new ConditionalStatement(ac_EntityID, Q.E, entityId));
		
        findToken.addConditionalStatement(Q.RIGHT_PARENTHESIS);
		
		return this.databaseConnection.read(Token_Info_Table.makeTokenInfoTableName(), findToken.toSQLStatement());
	}
    
    /**
	 * This method gets all tokens belonging to given column
	 * 
	 * @param reader the given reader
	 * @param tableName the name of table own the given column
	 * @param column the given column
	 * @return returns all tokens belonging to column, split tokens into grant and deny ones.
	 */
    public ResultTokens getTokensByColumn(EntityInstance reader, String tableName, AttributeClass column) {
    	tableName                        = SchemaInfo.refactorTableName(tableName);
    	String columnName                = SchemaInfo.refactorColumnName(tableName, column.getName());
    	List<EntityInstance> grantTokens = new LinkedList<EntityInstance>();
    	List<EntityInstance> denyTokens  = new LinkedList<EntityInstance>();
    	ResultTokens resultTokens        = new ResultTokens(grantTokens, denyTokens);
    	
    	EntityInstance validatedReader = this.readUser(reader);
    	
    	if(validatedReader != null) {
    		if((isRootUser(validatedReader, CRUD.READ))) {
    			String columnTokenGrantTableName = Column_Security_Grant_Table.makeColumnSecurityGrantTableName();
    			String columnTokenDenyTableName  = Column_Security_Deny_Table.makeColumnSecurityDenyTableName();
    			
    			// read columnId
    			AttributeClass ac_tableName      = DatabaseConnection.getAttributeClass(Column_Info_Table.makeColumnInfoTableName(), Column_Info_Table.getColumnTableName());
    			AttributeClass ac_columnName     = DatabaseConnection.getAttributeClass(Column_Info_Table.makeColumnInfoTableName(), Column_Info_Table.getColumnColumnName());
    			int columnId;
    			CompoundStatementManual findColumn   = new CompoundStatementManual();
    			findColumn.addConditionalStatement(new ConditionalStatement(ac_tableName, Q.E, tableName));
    			findColumn.addConditionalStatement(Q.AND);
    			findColumn.addConditionalStatement(new ConditionalStatement(ac_columnName, Q.E, columnName));
 			    List<EntityInstance> columnInstances = ecb.readEntities(Column_Info_Table.makeColumnInfoTableName(), findColumn);
 			    if(columnInstances.size() == 1) {
 			        columnId = (Integer) columnInstances.get(0).get(Column_Info_Table.makeColumnInfoTableColumnNameCoredbId());
 			    } else 	return resultTokens;
    			
 			    // get grant and deny tokens
    			AttributeClass ac_GrantColumnID = DatabaseConnection.getAttributeClass(columnTokenGrantTableName, Column_Security_Grant_Table.getColumnColumnID());
    			AttributeClass ac_DenyColumnID  = DatabaseConnection.getAttributeClass(columnTokenDenyTableName,  Column_Security_Deny_Table.getColumnColumnID());
        		grantTokens                     = getTokensByColumn(columnTokenGrantTableName, Column_Security_Grant_Table.getColumnTokenID(), ac_GrantColumnID, columnId);
        		denyTokens                      = getTokensByColumn(columnTokenDenyTableName, Column_Security_Deny_Table.getColumnTokenID(), ac_DenyColumnID, columnId);
        		
        		resultTokens = new ResultTokens(grantTokens, denyTokens);
    		} else {
    			System.out.println("This user has not root permission");
    		}
    		
    	} else {
    		System.out.println("Can not validate user with the given username and password");
    	}
    	
    	return resultTokens;
	}
    
    private List<EntityInstance> getTokensByColumn(String columnTokenTableName, String columnTokenIdName, AttributeClass ac_ColumnID, int columnId) {
		
		CompoundStatementManual findToken   = new CompoundStatementManual();
		findToken.addConditionalStatement(Token_Info_Table.getColumnTokenID());
		findToken.addConditionalStatement(Q.IN);
		findToken.addConditionalStatement(Q.LEFT_PARENTHESIS);
		
        findToken.addConditionalStatement(Q.SELECT + Q.DISTINCT + columnTokenIdName);
        findToken.addConditionalStatement(Q.FROM);
        findToken.addConditionalStatement(columnTokenTableName);
        findToken.addConditionalStatement(Q.WHERE);
        findToken.addConditionalStatement(new ConditionalStatement(ac_ColumnID, Q.E, columnId));
		
        findToken.addConditionalStatement(Q.RIGHT_PARENTHESIS);
		
		return this.databaseConnection.read(Token_Info_Table.makeTokenInfoTableName(), findToken.toSQLStatement());
	}
    
	/**
	 * This method gets all tokens belonging to given Group
	 * 
	 * @param reader the given reader
	 * @param group the given Group
	 * @return returns all tokens belonging to given Group, split tokens into grant and deny ones.
	 */
    public ResultTokens getTokensByGroup(EntityInstance reader, String group) {
    	List<EntityInstance> tokens      = new LinkedList<EntityInstance>();
    	List<EntityInstance> grantTokens = new LinkedList<EntityInstance>();
    	List<EntityInstance> denyTokens  = new LinkedList<EntityInstance>();
    	ResultTokens resultTokens        = new ResultTokens(grantTokens, denyTokens);
    	
    	EntityInstance validatedReader = this.readUser(reader);
    	
    	if(validatedReader != null) {
    		if((isRootUser(validatedReader, CRUD.READ))) {
    			
                tokens = getTokensFromGroup(group);
    		} else {
    			System.out.println("This user has no permission to read tokens belong to of Groups");
    			return resultTokens;
    		}
    	} else {
    		System.out.println("Can not validate user with the given username and password");
    		return resultTokens;
    	}
    	
    	while (!tokens.isEmpty()) {
    		EntityInstance token = tokens.get(0);
			if((Boolean)token.get(Token_Info_Table.getColumnPermission())) grantTokens.add(token);
			else                                                           denyTokens.add(token);
			
			tokens.remove(0);
		}
    	
    	return resultTokens;
    }
    
    /**
     * This method validates whether does reader has root permission
     * @param reader the given reader
     * @return return true if reader has root permission, otherwise returns false.
     */
	
	// agreed to implement
	//private boolean isRootUser(EntityInstance reader, CRUD crud) {
	//private boolean isRootUser(EntityInstance reader) {
	private boolean isRootUser(EntityInstance reader, Character crud) {
		EntityInstance validatedReader = readUser((String)reader.get(User_Info_Table.getColumnUserName()), (String)reader.get(User_Info_Table.getColumnPassword()));
		EntityInstance rootUser        = readUser(RootPermission.ROOTUSER, RootPermission.ROOTUSERPASSWORD);
		
		if(rootUser.equals(validatedReader)) return true;
		
		List<EntityInstance> tokens = getTokensByUser(rootUser, validatedReader).getGrantTokens();
		for (EntityInstance token : tokens) {
			String tokenName = (String)token.get(Token_Info_Table.getColumnTokenName());
			if ((crud.equals(CRUD.CREATE) && tokenName.equalsIgnoreCase(RootPermission.ROOT$C$G))
			||  (crud.equals(CRUD.READ)   && tokenName.equalsIgnoreCase(RootPermission.ROOT$R$G))
			||  (crud.equals(CRUD.UPDATE) && tokenName.equalsIgnoreCase(RootPermission.ROOT$U$G)) 
			||  (crud.equals(CRUD.DELETE) && tokenName.equalsIgnoreCase(RootPermission.ROOT$D$G))
			) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * This method read all users who are belong to the Group 'group'
	 * @param reader the given reader
	 * @param group the given Group name
	 * @return a list of users who are belong to the Group 'group'
	 */
	public List<EntityInstance> getUsersByGroup(EntityInstance reader, String group) {

    	List<EntityInstance> users = new LinkedList<EntityInstance>();
    	
    	EntityInstance validatedReader = this.readUser(reader);
    	if(validatedReader != null) {
    		if(isRootUser(validatedReader, CRUD.READ)) {
    			AttributeClass ac_group = DatabaseConnection.getAttributeClass(Token_Info_Table.makeTokenInfoTableName(), Token_Info_Table.getColumnGroup());
    			
    			CompoundStatementManual findUser   = new CompoundStatementManual();

        		findUser.addConditionalStatement(User_Info_Table.getColumnUserID());
        		findUser.addConditionalStatement(Q.IN);
        		findUser.addConditionalStatement(Q.LEFT_PARENTHESIS);
        		
                findUser.addConditionalStatement(Q.SELECT + Q.DISTINCT + User_Token_Table.getColumnUserID());
                findUser.addConditionalStatement(Q.FROM);
                findUser.addConditionalStatement(User_Token_Table.makeUserTokenTableName());
                findUser.addConditionalStatement(Q.COMMA);
                findUser.addConditionalStatement(Token_Info_Table.makeTokenInfoTableName());
                findUser.addConditionalStatement(Q.WHERE);
                findUser.addConditionalStatement(User_Token_Table.makeUserTokenTableName() + Q.PERIOD + User_Token_Table.getColumnTokenID() 
                		+ Q.E + Token_Info_Table.makeTokenInfoTableName() + Q.PERIOD + Token_Info_Table.getColumnTokenID());
                findUser.addConditionalStatement(Q.AND);
                findUser.addConditionalStatement(new ConditionalStatement(ac_group, Q.E, group));
        		
                findUser.addConditionalStatement(Q.RIGHT_PARENTHESIS);
                
                users.addAll(this.getDatabaseConnection().read(User_Info_Table.makeUserInfoTableName(), findUser.toSQLStatement()));
    		}
    		else {
    			System.out.println("this reader has no root permission, only root users can call this function");
    		}
    	} else {
    		System.out.println("Can not validate user with the given username and password");
    	}
    	
    	return users;
	}

	/**
	 * This method adds a list of tokens onto a user
	 * 
	 * @param creator the executer of this action
	 * @param receiver the user will receive the list of tokens
	 * @param tokens the list of tokens
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	public ResultSecurity addTokensToUser(EntityInstance creator, EntityInstance receiver, List<EntityInstance> tokens) {
		ResultSecurity userTokenPairs = new ResultSecurity(new LinkedList<EntityInstance>(), new LinkedList<EntityInstance>(), -1);
		
		EntityInstance validated_creator = this.readUser(creator);
		if(validated_creator != null){
			if(isRootUser(validated_creator, CRUD.CREATE)) {
				userTokenPairs = this.generateUserTokenPairs(receiver, tokens, true);
				if(!userTokenPairs.getPassedEntities().isEmpty()) {
					this.getDatabaseConnection().create(userTokenPairs.getPassedEntities());
				}
				
    		} else {
    			System.out.println("This user has no permission to add tokens to receiver");
    		}
		} else {
			System.out.println("Can not validate user with the given username and password");
		}
		
		return userTokenPairs;
	}
	
	/**
	 * This method remove a list of tokens from a user
	 * 
	 * @param deletor the executer of this action
	 * @param deletee the user will lose the list of tokens
	 * @param tokens the list of tokens
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	public ResultSecurity removeTokensFromUser(EntityInstance deletor, EntityInstance deletee, List<EntityInstance> tokens) {
		ResultSecurity userTokenPairs = new ResultSecurity(new LinkedList<EntityInstance>(), new LinkedList<EntityInstance>(), -1);
		
		EntityInstance validated_deletor = this.readUser(deletor);
		if(validated_deletor != null){
			if((isRootUser(validated_deletor, CRUD.DELETE))) {
				userTokenPairs = this.generateUserTokenPairs(deletee, tokens, false);
				if(!userTokenPairs.getPassedEntities().isEmpty()) this.getDatabaseConnection().delete(userTokenPairs.getPassedEntities());
    		} else {
    			System.out.println("This user has no permission to remove tokens from deletee");
    		}
		} else {
			System.out.println("Can not validate user with the given username and password");
		}
		
		return userTokenPairs;
	}

	/**
	 * This method generates a list of UserTokenPair instances based on given user and a list of tokens
	 *
	 * @param receiver given user 
	 * @param tokens given list of tokens
	 * @param isCreateMode mode identifier, true means create mode otherwise delete mode
	 * @return the result of Security checking( a ResultSecurity object), it includes a list of approved EntityInstances,
	 * a list of failed EntityInstances and the changeSetNumber of this transaction.
	 */
	private ResultSecurity generateUserTokenPairs(EntityInstance receiver, List<EntityInstance> tokens, boolean isCreateMode) {
		List<EntityInstance> passedTokenUserPairs = new LinkedList<EntityInstance>();
		List<EntityInstance> failedTokenUserPairs = new LinkedList<EntityInstance>();
		ResultSecurity resultSecurity            = new ResultSecurity(passedTokenUserPairs, failedTokenUserPairs, -1);
		
		receiver      = this.readUser(receiver);
        String userId = (String) receiver.get(User_Info_Table.getColumnUserID());
		
        for(EntityInstance token : tokens){
        	String tokenId  = (String)token.get(Token_Info_Table.getColumnTokenID());
        	CompoundStatementAuto sqlStatementAuto = new CompoundStatementAuto(Q.AND);
        	sqlStatementAuto.addCompoundStatement(token.makeSQLOnColumn(databaseConnection,Token_Info_Table.getColumnTokenID(), Q.E));
		    if(!ecb.readEntities(Token_Info_Table.makeTokenInfoTableName(), sqlStatementAuto.toSQLStatement()).isEmpty()) {
		    	EntityInstance tableUserTokenPair = null;
				int size = -1;
				sqlStatementAuto = new CompoundStatementAuto(Q.AND);
				List<EntityInstance> resultPairs = null;
						
				tableUserTokenPair = this.createEntityInstanceFor(User_Token_Table.makeUserTokenTableName());
			    tableUserTokenPair.set(User_Token_Table.getColumnUserID(), userId);
				tableUserTokenPair.set(User_Token_Table.getColumnTokenID(), tokenId);
						
				sqlStatementAuto.addCompoundStatement(tableUserTokenPair.makeSQLOnColumn(databaseConnection,User_Token_Table.getColumnUserID(), Q.E));
				sqlStatementAuto.addCompoundStatement(tableUserTokenPair.makeSQLOnColumn(databaseConnection,User_Token_Table.getColumnTokenID(), Q.E));
				resultPairs = ecb.readEntities(User_Token_Table.makeUserTokenTableName(), sqlStatementAuto.toSQLStatement());
				size = resultPairs.size();
					
	            if(isCreateMode) {
	                if(size == 0) passedTokenUserPairs.add(tableUserTokenPair);
	             	else {
						failedTokenUserPairs.add(tableUserTokenPair);
						System.out.println("CoreDB: Cannot add the following entity instance");
						Describer.describeEntityInstance(tableUserTokenPair);
					}
	            } else {
	                if(size > 0)  passedTokenUserPairs.addAll(resultPairs);
	             	else {
						failedTokenUserPairs.add(tableUserTokenPair);
						System.out.println("CoreDB: Cannot delete the following entity instance");
						Describer.describeEntityInstance(tableUserTokenPair);
					}
	            }
		    } else {
				System.out.println("TOKEN : ID = " + tokenId + " is not exist in DB, Please create it first");
			}
		}
		
        return resultSecurity;
	}
	
	/**
	 * This function generates a EntitySecurityClass from the database.The
	 * structure of EntitySecurityClass will be matched with the tableName schema
	 * requested by tableName name.
	 * 
	 * @param databaseConnection current working database Connection 
     * @param tableName the tableName schema will be transformed into EntitySecurityClass
     * @param reRead refresh the database handler or not.
     * @return retuens the EntitySecurityClass of table which name is tableName
	 */
	public  EntitySecurityClass getEntitySecurityClass(DatabaseConnection databaseConnection, String tableName, boolean reRead){
		return HelperWithSecurity.getEntitySecurityClass(databaseConnection, tableName, reRead);
	}
}

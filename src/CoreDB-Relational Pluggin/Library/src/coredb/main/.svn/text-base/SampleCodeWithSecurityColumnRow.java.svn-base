/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.main;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import coredb.config.Configuration;
import coredb.config.Configuration.Token_Info_Table;
import coredb.config.Configuration.User_Info_Table;
import coredb.controller.EntityControllerSecurity;
import coredb.controller.IEntityControllerCommon;
import coredb.controller.IEntityControllerSecurity;
import coredb.mode.Mode;
import coredb.security.ResultSecurity;
import coredb.security.ResultTokens;
import coredb.security.TokenFactory;
import coredb.sql.CompoundStatementAuto;
import coredb.sql.CompoundStatementManual;
import coredb.sql.Q;
import coredb.sql.SQLStatement;
import coredb.unit.AttributeClass;
import coredb.unit.ClassToTables;
import coredb.unit.EntityClass;
import coredb.unit.EntityInstance;
import coredb.unit.EntitySecurityClass;
import coredb.utils.Describer;

/**
 *	
 * @author vmc
 */
public class SampleCodeWithSecurityColumnRow {

	private IEntityControllerSecurity ec                 = null;

	private static final String TESTTABLE_TABLENAME      = "TESTTABLE";
	private static final String TESTTABLE_COLUMN_STAFFID = "STAFFID";
	private static final String TESTTABLE_COLUMN_NAME    = "NAME";
	private static final String TESTTABLE_COLUMN_PHONE   = "PHONE";

	@SuppressWarnings("unchecked")
	private static HashMap<String, Class> testTable_dataType = new HashMap<String, Class>();

	private static final String ROOTNAME                 = "ROOT";
	private static final String ROOTPASS                 = "ROOT";
	private static final String USERNAME                 = "john";
	private static final String USERPASS                 = "john345";
	
	/**
	 * This method do initialisation of EntityController and dataType of test table
	 * @param path the path of DB configuration file
	 * @throws SQLException
	 */
	public SampleCodeWithSecurityColumnRow (String path) throws SQLException{
		// Initialisation
		ec = new EntityControllerSecurity(path);
		ec.initializeMode(true, Mode.SECURITY);
		
		// Initialise the dataType of test table  
		testTable_dataType.put(TESTTABLE_COLUMN_STAFFID, Integer.class);
		testTable_dataType.put(TESTTABLE_COLUMN_NAME, String.class);
		testTable_dataType.put(TESTTABLE_COLUMN_PHONE, String.class);

	}
	

	public static void main(String[] args) throws SQLException {
		SampleCodeWithSecurityColumnRow testSample = new SampleCodeWithSecurityColumnRow("/tmp/credential.txt");

		// Create user
		testSample.createUser(null, null);

		// Read : readUser(EntityInstance user);
		testSample.readUser(null);

		// Read : readUser(String userName, String userPass);
		testSample.readUser(null, null);

		// Deploy 'base table'
		testSample.deployDefinitionListWithSecurity(null, null, false, false);

		// Add user to group
		testSample.addUserToGroup(null, null, null);
		
		// Add tokens to columns
		testSample.addTokensToColumns(null, null, null, null);

		// Do CRUD
		testSample.doCRUD();
		
		// create three data rows again for the following testing
		testSample.createEntitiesWithSecurity(null, null);
		testSample.createEntityWithSecurity(null, null);

		// Remove tokens from columns
		testSample.removeTokensFromColumns(null, null, null, null);
		
		// remove user from group
		testSample.removeUserFromGroup(null, null, null);
		
		// add tokens to user
		testSample.addTokensToUser(null, null, null);
		
		// remove tokens from user
		testSample.removeTokensFromUser(null, null, null);
		
		// get tokens of given column
		testSample.getTokensByColumn(null, null, null);
		
		// add tokens to a list of rows
		testSample.addTokensToRows(null, null, null);
		
		// remove tokens from a list of rows
		testSample.removeTokensFromRows(null, null, null);
		
		// get all users of given Group
		testSample.getUsersByGroup(null, null);
		
		// get tokens of given Group
		testSample.getTokensByGroup(null, null);
		
		// get tokens of given data row
		testSample.getTokensByRow(null, null);
		
		// get tokens of given column
		testSample.getTokensByColumn(null, null, null);
		
		// get tokens of given user
		testSample.getTokensByUser(null, null);

	}
	
	/**
	 * This method show how to create a user using createUser(EntityInstance creator, EntityInstance user)
	 * The new user info : (USERID : john, USERNAME : john, USERPASS : john345)
	 * @return
	 */
	private EntityInstance createUser(EntityInstance creator, EntityInstance user) {
		System.out.println("\n\n========================= Create User (USERID : john, USERNAME : john, USERPASS : john345) =========================\n\n");
		
		// get root user : only users who hold ROOT Grant Create Token can create users.
		creator = ec.readUser(ROOTNAME, ROOTPASS);
		
		EntityInstance userInstance = ec.createEntityInstanceFor(User_Info_Table.makeUserInfoTableName());
		userInstance.set(Configuration.User_Info_Table.getColumnUserID(), USERNAME); // USERNAME = "john"; 
		userInstance.set(Configuration.User_Info_Table.getColumnUserName(), USERNAME);
		userInstance.set(Configuration.User_Info_Table.getColumnPassword(), USERPASS);
		
		user = ec.createUser(creator, userInstance);
		Describer.describeEntityInstance(user);
		
		return null;
	}

	/**
	 * This method shows how to read user info from DB
	 * 
	 * user : (USERID : john, USERNAME : john, USERPASS : john345)
	 */
	private EntityInstance readUser(EntityInstance user) {
		System.out.println("\n\n========================= Read User (EntityInstance user -- (USERID : john, USERNAME : john, USERPASS : john345)) =========================\n\n");
		
		// create EntityInstance for User_Info table
		user = ec.createEntityInstanceFor(User_Info_Table.makeUserInfoTableName());
		user.set(Configuration.User_Info_Table.getColumnUserName(), USERNAME);
		user.set(Configuration.User_Info_Table.getColumnPassword(), USERPASS);
		
		user = ec.readUser(user);
		Describer.describeEntityInstance(user);
		
		return null;

	}

	/**
	 * This method shows how to read user info from DB
	 * 
	 * user : (USERID : john, USERNAME : john, USERPASS : john345)
	 */
	private EntityInstance readUser(String userName, String userPassword) {
		System.out.println("\n\n========================= Read User (USERNAME : john, USERPASS : john345) =========================\n\n");
		
		userName     = USERNAME;
		userPassword = USERPASS;
		
		EntityInstance user = ec.readUser(userName, userPassword);
		Describer.describeEntityInstance(user);
		
		return null;

	}
	
	/**
	 * This method shows how to deploy table into DB.
	 * 
	 * The table "test" will create and all default tokens will 
	 * apply to its columns.
	 * 
	 */
	private boolean deployDefinitionListWithSecurity(List<EntityInstance> tokens, List<ClassToTables> classToTablesList,boolean dropTablesFirst, boolean continueOnError) {
		System.out.println("\n\n========================= Deploy Table 'test' =========================\n\n");
		classToTablesList = new LinkedList<ClassToTables>();
		
		// the definition of table 'test'
		List<AttributeClass> attributes = new LinkedList<AttributeClass>();
		AttributeClass primaryAttribute = new AttributeClass(TESTTABLE_COLUMN_STAFFID, testTable_dataType.get(TESTTABLE_COLUMN_STAFFID));
		primaryAttribute.setPrimaryKey(true);
		primaryAttribute.setAutoIncrement(true);
		attributes.add(primaryAttribute);
		attributes.add(new AttributeClass(TESTTABLE_COLUMN_NAME,  testTable_dataType.get(TESTTABLE_COLUMN_NAME)));
		attributes.add(new AttributeClass(TESTTABLE_COLUMN_PHONE, testTable_dataType.get(TESTTABLE_COLUMN_PHONE)));
		EntitySecurityClass entitySecurityClass = new EntitySecurityClass(TESTTABLE_TABLENAME, attributes.toArray(new AttributeClass[0]), null);
		classToTablesList.add(entitySecurityClass);
         
		// get all default tokens
		tokens = ec.getDatabaseConnection().read(Token_Info_Table.makeTokenInfoTableName(), "GROUPTYPE = 'DEFAULT'");

		// Plz think carefully before set the last two arguments in the value of 'true'. 
		ec.deployDefinitionListWithSecurity(tokens,classToTablesList, false, false);
		Describer.describeTableSchema((IEntityControllerCommon)ec, TESTTABLE_TABLENAME);
		
		return true;

	}

	/**
	 * This method shows how to create a EntityInstance using 
	 * createEntityWithSecurity(EntityInstance creator, EntityInstance entity)
	 * 
	 */
	private ResultSecurity createEntityWithSecurity(EntityInstance creator, EntityInstance entity) {
		System.out.println("\n\n========================= create a EntityInstance of test table) =========================\n\n");
		
		creator                       = ec.readUser(USERNAME, USERPASS);
		
		EntityInstance entityInstance = ec.createEntityInstanceFor(TESTTABLE_TABLENAME);
		entityInstance.set(TESTTABLE_COLUMN_NAME, "Sean");
		entityInstance.set(TESTTABLE_COLUMN_PHONE, "0430106176");

		ResultSecurity resultSecurity = ec.createEntityWithSecurity(creator, entityInstance);
		resultSecurity.describePassedFailedEntities();
		
		return null;

	}

	/**
	 * This method shows how to create a list of EntityInstances using 
	 * createEntitiesWithSecurity(EntityInstance creator, List<EntityInstance> entities)
	 * 
	 */
	private ResultSecurity createEntitiesWithSecurity(EntityInstance creator, List<EntityInstance> entities){
		System.out.println("\n\n========================= create a list of EntityInstances of test table) =========================\n\n");
		
		creator                              = ec.readUser(USERNAME, USERPASS);
		
		EntityInstance entityInstance1       = ec.createEntityInstanceFor(TESTTABLE_TABLENAME);
		entityInstance1.set(TESTTABLE_COLUMN_NAME, "Stephen");
		entityInstance1.set(TESTTABLE_COLUMN_PHONE, "0430106177");

		EntityInstance entityInstance2       = ec.createEntityInstanceFor(TESTTABLE_TABLENAME);
		entityInstance2.set(TESTTABLE_COLUMN_NAME, "Van");
		entityInstance2.set(TESTTABLE_COLUMN_PHONE, "0430106178");
		
		List<EntityInstance> entityInstances = new LinkedList<EntityInstance>();
		entityInstances.add(entityInstance1);
		entityInstances.add(entityInstance2);

		ResultSecurity resultSecurity = ec.createEntitiesWithSecurity(creator, entityInstances);
        resultSecurity.describePassedFailedEntities();
		
		return null;
	}

	/**
	 * This method shows how to read a list of EntityInstances based on given SQLStatement
	 * 
	 */
	private ResultSecurity readEntitiesWithSecurity(EntityInstance reader, String tableName, SQLStatement sql) {
		System.out.println("\n\n========================= read a list of EntityInstances of test table) =========================\n\n");
		
		reader    = ec.readUser(USERNAME, USERPASS);
		tableName = TESTTABLE_TABLENAME;
		
		// set the selecting conditions
		CompoundStatementAuto csa = new CompoundStatementAuto(Q.OR);
		csa.addCompoundStatement(TESTTABLE_COLUMN_NAME + Q.E + Q.QUOT + "Van" +Q.QUOT);
		csa.addCompoundStatement(TESTTABLE_COLUMN_NAME + Q.E + Q.QUOT + "Sean" +Q.QUOT);
		csa.addCompoundStatement(TESTTABLE_COLUMN_NAME + Q.E + Q.QUOT + "Stephen" +Q.QUOT);

		ResultSecurity resultSecurity = ec.readEntitiesWithSecurity(reader, tableName, csa);
        resultSecurity.describePassedFailedEntities();
		
		return null;
	}

	/**
	 * This method shows how to update a EntityInstance using 
	 * updateEntityWithSecurity(EntityInstance updator, EntityInstance entity);
	 * 
	 */
	private ResultSecurity updateEntityWithSecurity(EntityInstance updator, EntityInstance entity) {
		System.out.println("\n\n========================= update a EntityInstance of test table) =========================\n\n");
		
		updator                       = ec.readUser(USERNAME, USERPASS);
		
		// set the selecting conditions to read EntityInstance which need to update
		CompoundStatementManual csm   = new 		CompoundStatementManual();
		csm.addConditionalStatement(TESTTABLE_COLUMN_NAME + Q.E + Q.QUOT + "Van" +Q.QUOT);
		
		List<EntityInstance> entityInstances = ec.getDatabaseConnection().read(TESTTABLE_TABLENAME, csm.toSQLStatement());
		
		ResultSecurity resultSecurity = null;
		EntityInstance entityInstance = null;
		if(entityInstances!=null && entityInstances.size()>0){
			entityInstance = entityInstances.get(0);
			entityInstance.set(TESTTABLE_COLUMN_NAME, "SuperVan");// do updating
			resultSecurity = ec.updateEntityWithSecurity(updator, entityInstance);
		}else{
			resultSecurity = ec.updateEntityWithSecurity(updator, ec.createEntityInstanceFor(TESTTABLE_TABLENAME));
		}
 
        resultSecurity.describePassedFailedEntities();
		
		return null;

	}

	/**
	 * This method shows how to update a list of EntityInstances using 
	 * updateEntitiesWithSecurity(EntityInstance updator, List<EntityInstance> entities);
	 * 
	 */
	@SuppressWarnings("null")
	private ResultSecurity updateEntitiesWithSecurity(EntityInstance updator, List<EntityInstance> entities) {
		System.out.println("\n\n========================= update a list of EntityInstances of test table) =========================\n\n");
		
		updator                   = ec.readUser(USERNAME, USERPASS);
		
		// set the selecting conditions to read EntityInstances which need to update
		CompoundStatementAuto csa = new CompoundStatementAuto(Q.OR);
		csa.addCompoundStatement(TESTTABLE_COLUMN_NAME + Q.E + Q.QUOT + "Stephen" +Q.QUOT);
		csa.addCompoundStatement(TESTTABLE_COLUMN_NAME + Q.E + Q.QUOT + "Sean" +Q.QUOT);

		List<EntityInstance> entityInstances = ec.getDatabaseConnection().read(TESTTABLE_TABLENAME, csa.toSQLStatement());
		
		for(EntityInstance entityInstance : entityInstances){
			entityInstance.set(TESTTABLE_COLUMN_NAME, "Super" + entityInstance.get(TESTTABLE_COLUMN_NAME).toString());// do updating
		}

		if(entityInstances == null && entityInstances.isEmpty()){
			entityInstances = new LinkedList<EntityInstance>();
			entityInstances.add(ec.createEntityInstanceFor(TESTTABLE_TABLENAME));
		}

		ResultSecurity resultSecurity = ec.updateEntitiesWithSecurity(updator, entityInstances);
        resultSecurity.describePassedFailedEntities();
		
		return null;
	}

	/**
	 * This method shows how to delete a EntityInstances using 
	 * deleteEntityWithSecurity(EntityInstance deletor, EntityInstance entity);
	 * 
	 */
	private ResultSecurity deleteEntityWithSecurity(EntityInstance deletor, EntityInstance entity) {
		System.out.println("\n\n========================= delete a EntityInstances of test table) =========================\n\n");
		
		deletor = ec.readUser(USERNAME, USERPASS);
		
		// set the selecting conditions to read EntityInstance which need to delete
		CompoundStatementManual csm = new CompoundStatementManual();
		csm.addConditionalStatement(TESTTABLE_COLUMN_NAME + Q.E + Q.QUOT + "SuperVan" +Q.QUOT);
		
		List<EntityInstance> entityInstances = ec.getDatabaseConnection().read(TESTTABLE_TABLENAME, csm.toSQLStatement());//ec.readEntities(TESTTABLE_TABLENAME, csm);
		
		ResultSecurity resultSecurity = null;
		EntityInstance entityInstance        = null;
		if(entityInstances!=null && entityInstances.size()>0){
			entityInstance = entityInstances.get(0);
			resultSecurity = ec.deleteEntityWithSecurity(deletor, entityInstance);
		}else{
			resultSecurity = ec.deleteEntityWithSecurity(deletor, ec.createEntityInstanceFor(TESTTABLE_TABLENAME));
		}
		
        resultSecurity.describePassedFailedEntities();
		
		return null;
	}

	/**
	 * This method shows how to delete a list of EntityInstances using 
	 * deleteEntitiesWithSecurity(EntityInstance deletor, List<EntityInstance> entities);
	 * 
	 */
	private ResultSecurity deleteEntitiesWithSecurity(EntityInstance deletor, List<EntityInstance> entities) {
		System.out.println("\n\n========================= delete a list of EntityInstances of test table) =========================\n\n");
		
		deletor = ec.readUser(USERNAME, USERPASS);
		
		// set the selecting conditions to read EntityInstances which need to delete
		CompoundStatementAuto csa = new CompoundStatementAuto(Q.OR);
		csa.addCompoundStatement(TESTTABLE_COLUMN_NAME + Q.E + Q.QUOT + "SuperStephen" +Q.QUOT);
		csa.addCompoundStatement(TESTTABLE_COLUMN_NAME + Q.E + Q.QUOT + "SuperSean" +Q.QUOT);

		List<EntityInstance> entityInstances = ec.getDatabaseConnection().read(TESTTABLE_TABLENAME, csa.toSQLStatement());//ec.readEntities(TESTTABLE_TABLENAME, csa);

		if(entityInstances == null){
			entityInstances = new LinkedList<EntityInstance>();
			entityInstances.add(ec.createEntityInstanceFor(TESTTABLE_TABLENAME));
		}

		ResultSecurity resultSecurity = ec.deleteEntitiesWithSecurity(deletor, entityInstances);
        resultSecurity.describePassedFailedEntities();
		
		return null;
		
	}

	/**
	 * This method shows how to add a user to one group.
	 * 
	 */
	private ResultSecurity addUserToGroup(EntityInstance creator, EntityInstance receiver, String group) {
		System.out.println("\n\n========================= Add User To Group (add user(USERNAME, USERPASS) to the ROOT group) =========================\n\n");
		
		creator  = ec.readUser(ROOTNAME, ROOTPASS);
		receiver = ec.readUser(USERNAME, USERPASS);
		
		group    = "ROOT";
		
		// After add user to Root Group, all tokens of Root group will assign to this user
		ResultSecurity resultSecurity = ec.addUserToGroup(creator, receiver, group);
		resultSecurity.describePassedFailedEntities();
		
		return null;

	}

	/**
	 * This method shows how to remove a user from one group.
	 * 
	 */
	private ResultSecurity removeUserFromGroup(EntityInstance deletor, EntityInstance deletee, String group) {
		System.out.println("\n\n========================= Remove User From Group (remove user(USERNAME, USERPASS) from the ROOT group) =========================\n\n");
		
		deletor  = ec.readUser(ROOTNAME, ROOTPASS);
		deletee  = ec.readUser(USERNAME, USERPASS);
		
		group    = "ROOT";
		
		List<EntityInstance> userTokenPairs = ec.removeUserFromGroup(deletor, deletee, group).getPassedEntities();
		Describer.describeEntityInstances(userTokenPairs);
		
		return null;

	}

	/**
	 * This method shows how to add a list of tokens to a list of columns.
	 * 
	 */
	private ResultSecurity addTokensToColumns(EntityInstance creator, String tableName, AttributeClass[] columns, List<EntityInstance> tokens) {
		System.out.println("\n\n========================= Add Tokens (Default tokens) To Columns (all columns of test table) =========================\n\n");
		
		creator                   = ec.readUser(ROOTNAME, ROOTPASS);
		
		EntityClass entityClass   = ec.getEntityClass(ec.getDatabaseConnection(), TESTTABLE_TABLENAME, false);
		tableName                 = entityClass.getName();
		columns                   = entityClass.getAttributeClassList();
		
		ResultTokens resultTokens = ec.getTokensByGroup(creator, "DEFAULT");
		tokens                    = new LinkedList<EntityInstance>();
		tokens.addAll(resultTokens.getGrantTokens());
		tokens.addAll(resultTokens.getDenyTokens());
		
		ResultSecurity resultSecurity = ec.addTokensToColumns(creator, tableName, columns, tokens);
		resultSecurity.describePassedFailedEntities();
		
		return null;

	}

	/**
	 * This method shows how to remove a list of tokens from a list of columns.
	 * 
	 */
	private ResultSecurity removeTokensFromColumns(EntityInstance deletor, String tableName, AttributeClass[] columns, List<EntityInstance> tokens) {
		System.out.println("\n\n========================= Remove Tokens (Default tokens) from Columns (all columns of test table) =========================\n\n");
		
		deletor = ec.readUser(ROOTNAME, ROOTPASS);
		
		EntityClass entityClass   = ec.getEntityClass(ec.getDatabaseConnection(), TESTTABLE_TABLENAME, false);
		tableName                 = entityClass.getName();
		columns                   = entityClass.getAttributeClassList();
		
		ResultTokens resultTokens = ec.getTokensByGroup(deletor, "DEFAULT");
		tokens                    = new LinkedList<EntityInstance>();
		tokens.addAll(resultTokens.getDenyTokens());
		tokens.addAll(resultTokens.getGrantTokens());
		
		ResultSecurity resultSecurity = ec.removeTokensFromColumns(deletor, tableName, columns, tokens);
		resultSecurity.describePassedFailedEntities();
		
		return null;

	}

	/**
	 * This method shows how to add Tokens to a list of Rows
	 * 
	 * If users do not add/remove any token onto/from rows, there is no row security policies applied on API.
	 */
	private ResultSecurity addTokensToRows(EntityInstance creator, List<EntityInstance> rows, List<EntityInstance> tokens) {
		System.out.println("\n\n========================= Add Tokens(DENYGROUP$R$D) to a list of Rows(all rows) =========================\n\n");
		
		creator   = ec.readUser(ROOTNAME, ROOTPASS);
		
		rows      = ec.getDatabaseConnection().read(TESTTABLE_TABLENAME, new CompoundStatementManual().toSQLStatement());//ec.readEntitiesWithSecurity(creator, TESTTABLE_TABLENAME, new CompoundStatementManual()).getPassedEntities();
		tokens    = new LinkedList<EntityInstance>();
		tokens.add(TokenFactory.DefaultTokens.getTOKEN_DENYGROUP$R$D(ec.getDatabaseConnection()));

		ResultSecurity resultSecurity = ec.addTokensToRows(creator, rows, tokens);
		resultSecurity.describePassedFailedEntities();
		return null;

	}

	/**
	 * This method shows how to remove Tokens from a list of Rows
	 * 
	 * If users do not add/remove any token onto/from rows, there is no row security policies applied on API.
	 */
	private ResultSecurity removeTokensFromRows(EntityInstance deletor, List<EntityInstance> rows, List<EntityInstance> tokens) {
		System.out.println("\n\n========================= Remove Tokens(DENYGROUP$R$D) from a list of Rows(all rows) =========================\n\n");
	
        deletor   = ec.readUser(ROOTNAME, ROOTPASS);
		
		rows      = ec.getDatabaseConnection().read(TESTTABLE_TABLENAME, new CompoundStatementManual().toSQLStatement());
		tokens    = new LinkedList<EntityInstance>();
		tokens.add(TokenFactory.DefaultTokens.getTOKEN_DENYGROUP$R$D(ec.getDatabaseConnection()));

		ResultSecurity resultSecurity = ec.removeTokensFromRows(deletor, rows, tokens);
		resultSecurity.describePassedFailedEntities();
		return null;
	}



	private void doCRUD(){
		// Create
		createEntityWithSecurity(null, null);
		createEntitiesWithSecurity(null, null);

		// Read
		readEntitiesWithSecurity(null, null, null);

		// Update
		updateEntityWithSecurity(null, null);
		updateEntitiesWithSecurity(null, null);

		// Delete
		deleteEntityWithSecurity(null, null);
		deleteEntitiesWithSecurity(null, null);
	}

	/**
	 * This method shows how to add tokens to given User
	 */
	private ResultSecurity addTokensToUser(EntityInstance creator, EntityInstance receiver, List<EntityInstance> tokens) {
        System.out.println("\n\n========================= Add Tokens (Default tokens) To User (root) =========================\n\n");
		
		creator                   = ec.readUser(ROOTNAME, ROOTPASS);
		receiver                  = ec.readUser(ROOTNAME, ROOTPASS);

		ResultTokens resultTokens = ec.getTokensByGroup(creator, "DEFAULT");
		tokens                    = new LinkedList<EntityInstance>();
		tokens.addAll(resultTokens.getGrantTokens());
		tokens.addAll(resultTokens.getDenyTokens());
		
		ResultSecurity resultSecurity = ec.addTokensToUser(creator, receiver, tokens);
		resultSecurity.describePassedFailedEntities();
		
		return null;
	}

	/**
	 * This method shows how to remove tokens from a user
	 */
	private ResultSecurity removeTokensFromUser(EntityInstance deletor, EntityInstance deletee, List<EntityInstance> tokens) {
		 System.out.println("\n\n========================= Remove Tokens (Default tokens) from User (john) =========================\n\n");
			
		 deletor                   = ec.readUser(ROOTNAME, ROOTPASS);
		 deletee                   = ec.readUser(USERNAME, USERPASS);

		 ResultTokens resultTokens = ec.getTokensByGroup(deletor, "DEFAULT");
		 tokens                    = new LinkedList<EntityInstance>();
		 tokens.addAll(resultTokens.getGrantTokens());
		 tokens.addAll(resultTokens.getDenyTokens());
			
		 ResultSecurity resultSecurity = ec.addTokensToUser(deletor, deletee, tokens);
		 resultSecurity.describePassedFailedEntities();
		 return null;
	}
	
	/**
	 * This method shows how to get tokens of given Column
	 */
	private ResultTokens getTokensByColumn(EntityInstance reader, String tableName, AttributeClass column) {
		 System.out.println("\n\n========================= Read tokens of given column =========================\n\n");
			
		 reader                   = ec.readUser(ROOTNAME, ROOTPASS);
		 
		 EntityClass entityClass   = ec.getEntityClass(ec.getDatabaseConnection(), TESTTABLE_TABLENAME, false);
		 tableName                 = entityClass.getName();
		 column                   = entityClass.getAttributeClassList()[0];

		 ResultTokens resultTokens = ec.getTokensByColumn(reader, tableName, column);
		 resultTokens.describeGrantDenyTokens();
			
		 return null;
	}

	/**
	 * This method shows how to get tokens of given Group
	 */
	private ResultTokens getTokensByGroup(EntityInstance reader, String group) {
		 System.out.println("\n\n========================= Read tokens of given column =========================\n\n");
			
		 reader                   = ec.readUser(ROOTNAME, ROOTPASS);
		 
		 ResultTokens resultTokens = ec.getTokensByGroup(reader, "DEFAULT");
		 resultTokens.describeGrantDenyTokens();
			
		 return null;
	}

	/**
	 * This method shows how to get tokens of given Row
	 */
	private ResultTokens getTokensByRow(EntityInstance reader, EntityInstance row) {
		System.out.println("\n\n========================= Read tokens of given Row =========================\n\n");
		
		reader   = ec.readUser(ROOTNAME, ROOTPASS);
		
		row      = ec.readEntitiesWithSecurity(reader, TESTTABLE_TABLENAME, new CompoundStatementManual()).getPassedEntities().get(0);
		ResultTokens resultTokens = ec.getTokensByRow(reader, row);
		resultTokens.describeGrantDenyTokens();

		return null;
	}

	/**
	 * This method shows how to get tokens of given User
	 */
	private ResultTokens getTokensByUser(EntityInstance reader, EntityInstance user) {
        System.out.println("\n\n========================= Read tokens of given User (root)=========================\n\n");
		
		reader   = ec.readUser(ROOTNAME, ROOTPASS);
		
		ResultTokens resultTokens = ec.getTokensByUser(reader, reader);
		resultTokens.describeGrantDenyTokens();

		return null;
	}

	/**
	 * This method shows how to get users of given Group
	 */
	private List<EntityInstance> getUsersByGroup(EntityInstance reader, String group) {
        System.out.println("\n\n========================= Read users of given Group (ROOT) =========================\n\n");
		
		reader   = ec.readUser(ROOTNAME, ROOTPASS);
		
		List<EntityInstance> users = ec.getUsersByGroup(reader, "ROOT");
		Describer.describeEntityInstances(users);

		return null;
	}

}

package coredb.test;


import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import junit.framework.Assert;

import org.apache.commons.beanutils.DynaClass;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import coredb.config.Configuration;
import coredb.config.Configuration.Column_Info_Table;
import coredb.config.Configuration.Column_Security_Deny_Table;
import coredb.config.Configuration.Column_Security_Grant_Table;
import coredb.config.Configuration.Token_Info_Table;
import coredb.config.Configuration.User_Info_Table;
import coredb.config.Configuration.User_Token_Table;
import coredb.controller.EntityControllerSecurity;
import coredb.controller.IEntityControllerSecurity;
import coredb.database.DatabaseConnection;
import coredb.database.SchemaInfo;
import coredb.mode.Mode;
import coredb.security.ResultSecurity;
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
import coredb.utils.Helper;
import coredb.utils.TestAssist;

public class TestSampleCodeWithSecurityColumn {
	private  IEntityControllerSecurity ec                 = null;
	private static String  COLUMNSECURITY_TABLENAME      = null;
	private static String  COLUMNSECURITY_TOKENID        = null;
	private static String  COLUMNSECURITY_COLUMNID       = null;
	
	private static final String TESTTABLE_TABLENAME      = "TESTTABLE";
	private static final String TESTTABLE_COLUMN_STAFFID = "STAFFID";
	private static final String TESTTABLE_COLUMN_NAME    = "NAME";
	private static final String TESTTABLE_COLUMN_PHONE   = "PHONE";
	
	private static HashMap<String, Class> testTable_dataType = new HashMap<String, Class>();
	
	private static final String ROOTNAME                 = "ROOT";
	private static final String ROOTPASS                 = "ROOT";
	private static final String USERNAME                 = "USERNAME_1";
	private static final String USERPASS                 = "USERPASS_1";

	private static void printTag(String tag){
	       System.out.println(tag);
	}

	/////////////////////////////////////////////assistant methods//////////////////////////////////////////////////////////
	
	
	private EntityInstance createUser() {
		EntityInstance creator = ec.readUser(ROOTNAME, ROOTPASS);
		EntityInstance userInstance = ec.createEntityInstanceFor(User_Info_Table.makeUserInfoTableName());
		userInstance.set(Configuration.User_Info_Table.getColumnUserID(), USERNAME);
		userInstance.set(Configuration.User_Info_Table.getColumnUserName(), USERNAME);
		userInstance.set(Configuration.User_Info_Table.getColumnPassword(), USERPASS);
		EntityInstance user = ec.createUser(creator, userInstance);
		return user;
	}
	private List<EntityInstance> addUserToGroup() {
		EntityInstance creator  = ec.readUser(ROOTNAME, ROOTPASS);
		EntityInstance receiver = ec.readUser(USERNAME, USERPASS);
		String group    = "ROOT";
		List<EntityInstance> userTokenPairs = ec.addUserToGroup(creator, receiver, group).getPassedEntities();
		return userTokenPairs;
	}
	private List<EntityInstance> createEntityInstances() {
		EntityInstance creator                              = ec.readUser(USERNAME, USERPASS);
		List<EntityInstance> entities = new LinkedList<EntityInstance>();
		
		EntityInstance entityInstance1       = ec.createEntityInstanceFor(TESTTABLE_TABLENAME);
		entityInstance1.set(TESTTABLE_COLUMN_NAME, "Stephen");
		entityInstance1.set(TESTTABLE_COLUMN_PHONE, "0430106177");	
		entities.add(entityInstance1);
		
		EntityInstance entityInstance2       = ec.createEntityInstanceFor(TESTTABLE_TABLENAME);
		entityInstance2.set(TESTTABLE_COLUMN_NAME, "Van");
		entityInstance2.set(TESTTABLE_COLUMN_PHONE, "0430106178");	
		entities.add(entityInstance2);
		
		List<EntityInstance> expectedInstances = ec.createEntitiesWithSecurity(creator, entities).getPassedEntities();
		return expectedInstances;
	}
	private List<EntityInstance> createEntity() {
		EntityInstance creator   = ec.readUser(USERNAME, USERPASS);	
		EntityInstance entityInstance = ec.createEntityInstanceFor(TESTTABLE_TABLENAME);
		entityInstance.set(TESTTABLE_COLUMN_NAME, "Sean");
		entityInstance.set(TESTTABLE_COLUMN_PHONE, "0430106176");	
		List<EntityInstance> expectedInstances = ec.createEntityWithSecurity(creator, entityInstance).getPassedEntities();
		return expectedInstances;
	}
	
//	private void deployTestTable() {
////		 List<String> tableNames = ec.readTableNameList(true);
////		 List<String> droptableNames = new LinkedList<String>();
////		 for(String tableName : tableNames) {
////			 if(tableName.contains(TESTTABLE_TABLENAME.toLowerCase()))
////			     droptableNames.add(tableName);
////		 }
////		 dropTables(droptableNames);
//		dropAllTables();
//		TestSampleCodeWithSecurity test = new TestSampleCodeWithSecurity();
//		try {
//			test.setUp();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		List<ClassToTables> classToTablesList = new LinkedList<ClassToTables>();	
//		List<AttributeClass> attributes = new LinkedList<AttributeClass>();	
//		AttributeClass primaryAttribute = new AttributeClass(TESTTABLE_COLUMN_STAFFID, testTable_dataType.get(TESTTABLE_COLUMN_STAFFID));
//		primaryAttribute.setPrimaryKey(true);
//		primaryAttribute.setAutoIncrement(true);
//		attributes.add(primaryAttribute);
//		attributes.add(new AttributeClass(TESTTABLE_COLUMN_NAME, testTable_dataType.get(TESTTABLE_COLUMN_NAME)));
//		attributes.add(new AttributeClass(TESTTABLE_COLUMN_PHONE, testTable_dataType.get(TESTTABLE_COLUMN_PHONE)));		
//		EntitySecurityClass entitySecurityClass = new EntitySecurityClass(TESTTABLE_TABLENAME, attributes.toArray(new AttributeClass[0]), null);
//		classToTablesList.add(entitySecurityClass);			
//
//		List<EntityInstance> defaultTokens = TokenFactory.getDefaultGrantTokens(ec.getDatabaseConnection());
//		ec.deployDefinitionListWithSecurity(new LinkedList<EntityInstance>(),classToTablesList, false, false);
//	}
	
	public static void  main(String[] args) throws Exception{
		TestSampleCodeWithSecurityColumn test = new TestSampleCodeWithSecurityColumn();
		test.setUp();
		
		test.createUser(null, null);
		
		test.readUser(null);
		test.readUser(null, null);
		
		test.addUserToGroup(null, null, null);
		
		test.addTokensToColumns(null, null, null, null);
		
		test.createEntityWithSecurity(null,null);
		test.createEntitiesWithSecurity(null,null);
		
		test.readEntitiesWithSecurity(null, null, null);
		
		test.removeTokensFromColumns(null, null, null, null);
		
		test.removeUserFromGroup(null, null, null);
		
		test.updateEntitiesWithSecurity(null, null);
		test.testHiddenValue(); // this test method will call updateEntityWithSecurity(null, null);
		
		test.deleteEntityWithSecurity(null, null);
		test.deleteEntitiesWithSecurity(null, null);
		
		System.out.println("========END========");
		test.tearDown();
	}
	
	@Before
	public void setUp() throws Exception {
		ec = new EntityControllerSecurity("/tmp/credential.txt");
		testTable_dataType.put(TESTTABLE_COLUMN_STAFFID, Integer.class);
		testTable_dataType.put(TESTTABLE_COLUMN_NAME, String.class);
		testTable_dataType.put(TESTTABLE_COLUMN_PHONE, String.class);
	}
	
	public void begin(){
		//ec.dropAllTables(ec.readUser(ROOTNAME,ROOTPASS));
		ec.initializeMode(true, Mode.SECURITY);
		deployDefinitionListWithSecurity(null,false,false);
	}
	
	public void end(){
		ec.dropAllTables(ec.readUser(ROOTNAME,ROOTPASS));
	}
	
	@After
	public void tearDown() throws Exception {
		ec = null;
	}

	
	public ResultSecurity addTokensToColumns(EntityInstance creator, String tableName, AttributeClass[] columns, List<EntityInstance> tokens) {
		/**
		 * before test
		 */
		begin();
		List<EntityInstance> needDeleteEntities = new LinkedList<EntityInstance>();
		EntityInstance user = createUser();
		needDeleteEntities.add(user);
		needDeleteEntities.addAll(addUserToGroup());
		
		printTag("<TESTING addTokensToColumns(EntityInstance creator, String tableName, AttributeClass[] columns, List<EntityInstance> tokens)>");
		printTag("Add default Tokens to the columns of TESTTABLE");
		// Test Data Initialization
		creator                   = ec.readUser(ROOTNAME, ROOTPASS);
		EntityClass entityClass   = ec.getEntityClass(ec.getDatabaseConnection(), TESTTABLE_TABLENAME, false);
		tableName                 = entityClass.getName();
		columns                   = Helper.primaryKeyFilter(entityClass.getAttributeClassList());
		tokens                    = ec.getTokensByGroup(creator, "DEFAULT").getGrantTokens();//ec.getDatabaseConnection().read(Token_Info_Table.makeTokenInfoTableName(), "GROUPTYPE = 'DEFAULT'");//ec.readEntities(Token_Info_Table.makeTokenInfoTableName(), "GROUPTYPE = 'DEFAULT'");	
		List<EntityInstance> tokenColumnPairs = ec.addTokensToColumns(creator, tableName, columns, tokens).getPassedEntities();
        
		List<EntityInstance> acturalTokenColumnPairs = new LinkedList<EntityInstance>();
		// Test Code:
		CompoundStatementManual csm1 = new CompoundStatementManual();
		csm1.addConditionalStatement(Column_Info_Table.getColumnTableName() + Q.E + Q.QUOT + tableName + Q.QUOT);
		csm1.addConditionalStatement(Q.AND + Q.LEFT_PARENTHESIS);
		CompoundStatementManual csm2 = new CompoundStatementManual();
		for(AttributeClass column : columns){
			if(csm2.getCompoundStatements().size()>0){
				csm2.addConditionalStatement(Q.OR);
			}
			csm2.addConditionalStatement(Column_Info_Table.getColumnColumnName() + Q.E + Q.QUOT + column.getName() + Q.QUOT);
		}
		csm1.addConditionalStatement(csm2);
		csm1.addConditionalStatement(Q.RIGHT_PARENTHESIS);
		List<EntityInstance> columnInfoInstances      = ec.getDatabaseConnection().read(Column_Info_Table.makeColumnInfoTableName(), csm1.toSQLStatement());//ec.readEntities(Column_Info_Table.makeColumnInfoTableName(), csm1);

		Assert.assertTrue(tokens.size() > 0);
		for(EntityInstance token : tokens){
			String tokenId = (String)token.get(Token_Info_Table.getColumnTokenID());
			Boolean permission = (Boolean)token.get(Token_Info_Table.getColumnPermission());
			initializeColumnSecurityProperities(permission);
			CompoundStatementManual csm3 = new CompoundStatementManual();
			csm3.addConditionalStatement(COLUMNSECURITY_TOKENID + Q.E + Q.QUOT + tokenId + Q.QUOT);
			csm3.addConditionalStatement(Q.AND + Q.LEFT_PARENTHESIS);
			CompoundStatementManual csm4 = new CompoundStatementManual();
			for(EntityInstance columnInfoInstance : columnInfoInstances){
				Integer columnId = (Integer)columnInfoInstance.get(Column_Info_Table.makeColumnInfoTableColumnNameCoredbId());
				if(csm4.getCompoundStatements().size() > 0) {
					csm4.addConditionalStatement(Q.OR);
				}
				csm4.addConditionalStatement(COLUMNSECURITY_COLUMNID + Q.E + columnId);
			}
			csm3.addConditionalStatement(csm4);
			csm3.addConditionalStatement(Q.RIGHT_PARENTHESIS);
			acturalTokenColumnPairs.addAll(ec.getDatabaseConnection().read(COLUMNSECURITY_TABLENAME, csm3.toSQLStatement()));
		}
		
		Describer.describeEntityInstances(tokenColumnPairs);//System.exit(0);
		// because pk column StaffID can't add token onto it by user
		Assert.assertTrue(TestAssist.isSubSet(ec.getDatabaseConnection(), tokenColumnPairs, acturalTokenColumnPairs));
		
		printTag("</PASSED TESTING addTokensToColumns(EntityInstance creator, String tableName, AttributeClass[] columns, List<EntityInstance> tokens)>\n");
//		Describer.describeEntityInstances(acturalTokenColumnPairs);
		
		/**
		 * after test
		 */
		end();
//		ec.removeUserFromGroup(ec.readUser(ROOTNAME, ROOTPASS), user, Configuration.DEFAULT);
//		ec.deleteEntitiesWithSecurity(ec.readUser(ROOTNAME, ROOTPASS), needDeleteEntities);
		return null;
	}

	
	public ResultSecurity addTokensToRows(EntityInstance creatorId, List<EntityInstance> rows, List<EntityInstance> tokens) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public ResultSecurity addUserToGroup(EntityInstance creator, EntityInstance receiver, String group) {
		/**
		 * before test
		 */
		begin();
		List<EntityInstance> needDeleteEntities = new LinkedList<EntityInstance>();
		EntityInstance user = createUser();
		needDeleteEntities.add(user);
		
		printTag("<TESTING addUserToGroup(EntityInstance creator, EntityInstance receiver, String group)>");
		printTag("Add default Tokens to user whose name is USERNAME");
		// Test Data Initialization
		creator  = ec.readUser(ROOTNAME, ROOTPASS);
		receiver = ec.readUser(USERNAME, USERPASS);
		group    = "ROOT";
		List<EntityInstance> userTokenPairs = addUserToGroup();
		
		// Test Code
		HashSet<String> userTokenPairsSet= new HashSet<String>();
        for(EntityInstance userTokenPair : userTokenPairs){
        	userTokenPairsSet.add((String)userTokenPair.get(User_Token_Table.getColumnUserID()) + (String)userTokenPair.get(User_Token_Table.getColumnTokenID()));
        }	
		CompoundStatementManual csm = new CompoundStatementManual();
		csm.addConditionalStatement(Token_Info_Table.getColumnGroup() + Q.E + Q.QUOT + group + Q.QUOT);
		List<EntityInstance> tokens  = ec.getDatabaseConnection().read(Token_Info_Table.makeTokenInfoTableName(), csm.toSQLStatement());
		CompoundStatementAuto csa    = new CompoundStatementAuto(Q.OR);
		for(EntityInstance token : tokens) {
			String tokenId               = (String)token.get(Token_Info_Table.getColumnTokenID());
			csa.addCompoundStatement(User_Token_Table.getColumnTokenID() + Q.E + Q.QUOT + tokenId +Q.QUOT);
		}
		csm = new CompoundStatementManual();
		csm.addConditionalStatement(csa);
		csm.addConditionalStatement(Q.AND + User_Token_Table.getColumnUserID() + Q.E + Q.QUOT + receiver.get(User_Info_Table.getColumnUserID()) + Q.QUOT);
		List<EntityInstance> userTokenPairsForActual = ec.getDatabaseConnection().read(User_Token_Table.makeUserTokenTableName(), csm.toSQLStatement());
		Describer.describeEntityInstances(userTokenPairsForActual);
		
		// <<< START Assert
		Assert.assertTrue(TestAssist.compare(ec.getDatabaseConnection(), userTokenPairs, userTokenPairsForActual));
		// <<< END Assert
		printTag("</PASSED TESTING addUserToGroup(EntityInstance creator, EntityInstance receiver, String group)>\n");
		
		/**
		 * after test
		 */
		end();
//		ec.removeUserFromGroup(ec.readUser(ROOTNAME, ROOTPASS), user, Configuration.DEFAULT);
//		ec.deleteEntitiesWithSecurity(ec.readUser(ROOTNAME, ROOTPASS), needDeleteEntities);
		return null;
	}

	
	public ResultSecurity createEntitiesWithSecurity(EntityInstance creator, List<EntityInstance> entities) {
		/**
		 * before test
		 */
		begin();
		List<EntityInstance> needDeleteEntities = new LinkedList<EntityInstance>();
		EntityInstance user = createUser();
		needDeleteEntities.add(user);
		needDeleteEntities.addAll(addUserToGroup());
		needDeleteEntities.addAll(createEntity());
		
		printTag("<TESTING createEntitiesWithSecurity(EntityInstance creator, List<EntityInstance> entities)>");
		printTag("user USERNAME creates a list of EntityInstances onto TESTTABLE");
		creator                              = ec.readUser(USERNAME, USERPASS);
		
		List<EntityInstance> expectedInstances = createEntityInstances();//ec.createEntitiesWithSecurity(creator, entities).getPassedEntities()
		needDeleteEntities.addAll(expectedInstances);
		
		CompoundStatementManual csm   = new CompoundStatementManual();
		csm.addConditionalStatement(Q.LEFT_PARENTHESIS);
		csm.addConditionalStatement(SchemaInfo.refactorColumnName(TESTTABLE_TABLENAME, TESTTABLE_COLUMN_NAME) + Q.E + Q.QUOT + "Stephen" + Q.QUOT);
		csm.addConditionalStatement(Q.AND);
		csm.addConditionalStatement(SchemaInfo.refactorColumnName(TESTTABLE_TABLENAME, TESTTABLE_COLUMN_PHONE) + Q.E + Q.QUOT + "0430106177" + Q.QUOT);
		csm.addConditionalStatement(Q.RIGHT_PARENTHESIS + Q.OR + Q.LEFT_PARENTHESIS);
		csm.addConditionalStatement(SchemaInfo.refactorColumnName(TESTTABLE_TABLENAME, TESTTABLE_COLUMN_NAME) + Q.E + Q.QUOT + "Van" + Q.QUOT);
		csm.addConditionalStatement(Q.AND);
		csm.addConditionalStatement(SchemaInfo.refactorColumnName(TESTTABLE_TABLENAME, TESTTABLE_COLUMN_PHONE) + Q.E + Q.QUOT + "0430106178" + Q.QUOT);
		csm.addConditionalStatement(Q.RIGHT_PARENTHESIS);
		
		List<EntityInstance> actualInstances =  ec.getDatabaseConnection().read(SchemaInfo.refactorTableName(TESTTABLE_TABLENAME), csm.toSQLStatement());
		
		// <<< START Assert
		Assert.assertTrue(TestAssist.compare(ec.getDatabaseConnection(), expectedInstances, actualInstances));
		// <<< END Assert

		Describer.describeEntityInstances(actualInstances);
		printTag("</PASSED TESTING createEntitiesWithSecurity(EntityInstance creator, List<EntityInstance> entities)>\n");
		
		/**
		 * after test
		 */
		end();
//		ec.removeUserFromGroup(ec.readUser(ROOTNAME, ROOTPASS), user, Configuration.DEFAULT);
//		ec.deleteEntitiesWithSecurity(ec.readUser(ROOTNAME, ROOTPASS), needDeleteEntities);
		return null;
	}

	
	public ResultSecurity createEntityWithSecurity(EntityInstance creator, EntityInstance entity) {
		/**
		 * before test
		 */
		begin();
		List<EntityInstance> needDeleteEntities = new LinkedList<EntityInstance>();
		EntityInstance user = createUser();
		needDeleteEntities.add(user);
		needDeleteEntities.addAll(addUserToGroup());
		
		printTag("<TESTING createEntityWithSecurity(EntityInstance creator, EntityInstance entity)>");
		printTag("user USERNAME creates a EntityInstance onto TESTTABLE");

		List<EntityInstance> expectedInstances = createEntity();
		needDeleteEntities.addAll(expectedInstances);
		
		CompoundStatementManual csm   = new CompoundStatementManual();
		csm.addConditionalStatement(SchemaInfo.refactorColumnName(TESTTABLE_TABLENAME, TESTTABLE_COLUMN_NAME) + Q.E + Q.QUOT + "Sean" + Q.QUOT);
		csm.addConditionalStatement(Q.AND);
		csm.addConditionalStatement(SchemaInfo.refactorColumnName(TESTTABLE_TABLENAME, TESTTABLE_COLUMN_PHONE) + Q.E + Q.QUOT + "0430106176" + Q.QUOT);
		List<EntityInstance> actualInstances =  ec.getDatabaseConnection().read(SchemaInfo.refactorTableName(TESTTABLE_TABLENAME), csm.toSQLStatement());

		// <<< START Assert
		Assert.assertTrue(TestAssist.compare(ec.getDatabaseConnection(), expectedInstances, actualInstances));
		// <<< END Assert
		
		Describer.describeEntityInstances(actualInstances);
		printTag("<TESTING createEntityWithSecurity(EntityInstance creator, EntityInstance entity)>\n");
		
		/**
		 * after test
		 */
		end();
//		ec.removeUserFromGroup(ec.readUser(ROOTNAME, ROOTPASS), user, Configuration.DEFAULT);
//		ec.deleteEntitiesWithSecurity(ec.readUser(ROOTNAME, ROOTPASS), needDeleteEntities);
		return null;
		
	}
	
	public EntityInstance createUser(EntityInstance creator, EntityInstance user) {
		/**
		 * before test
		 */
		begin();
		List<EntityInstance> needDeleteEntities = new LinkedList<EntityInstance>();
		
		printTag("<TESTING createUser(EntityInstance creator, EntityInstance user)>");
		// Test Data Initialization
		user = createUser();
		needDeleteEntities.add(user);
		
		// Test Code
		CompoundStatementManual csm = new CompoundStatementManual();
		csm.addConditionalStatement(User_Info_Table.getColumnUserName() + Q.E + Q.QUOT + USERNAME + Q.QUOT);
		csm.addConditionalStatement(Q.AND);
		csm.addConditionalStatement(User_Info_Table.getColumnPassword() + Q.E + Q.QUOT + USERPASS + Q.QUOT);
		EntityInstance userForAcutal = ec.getDatabaseConnection().read(User_Info_Table.makeUserInfoTableName(), csm.toSQLStatement()).get(0);
		Describer.describeEntityInstance(userForAcutal);
		
		// <<< START Assert
		Assert.assertTrue(TestAssist.compare(this.ec.getDatabaseConnection(), user, userForAcutal));
		// <<< END Assert
		
		printTag("</PASSED TESTING createUser(EntityInstance creator, EntityInstance user)>\n");
		
		/**
		 * after test
		 */
		end();
//		ec.removeUserFromGroup(ec.readUser(ROOTNAME, ROOTPASS), user, Configuration.DEFAULT);
//		ec.deleteEntitiesWithSecurity(ec.readUser(ROOTNAME, ROOTPASS), needDeleteEntities);
		
		return null;
		
	}

	
	public ResultSecurity deleteEntitiesWithSecurity(EntityInstance deletor, List<EntityInstance> entities) {
		/**
		 * before test
		 */
		begin();
		List<EntityInstance> needDeleteEntities = new LinkedList<EntityInstance>();
		EntityInstance user = createUser();
		needDeleteEntities.add(user);
		needDeleteEntities.addAll(addUserToGroup());
		needDeleteEntities.addAll(createEntity());
		needDeleteEntities.addAll(createEntityInstances());
		
		printTag("<TESTING deleteEntitiesWithSecurity(EntityInstance deletor, List<EntityInstance> entities)>");
		printTag("user USERNAME deletes a list of EntityInstances of TESTTABLE -- NAME is SuperStephen or SuperSean");
		
		deletor = ec.readUser(USERNAME, USERPASS);
		CompoundStatementManual csm = new CompoundStatementManual();	
		csm.addConditionalStatement(TESTTABLE_COLUMN_NAME + Q.E + Q.QUOT + "SuperStephen" +Q.QUOT);
		csm.addConditionalStatement(Q.OR);
		csm.addConditionalStatement(TESTTABLE_COLUMN_NAME + Q.E + Q.QUOT + "SuperSean" +Q.QUOT);
		
		List<EntityInstance> entityInstances = ec.getDatabaseConnection().read(TESTTABLE_TABLENAME, csm.toSQLStatement());
		printTag("before deleting");
		Describer.describeEntityInstances(entityInstances);
		
		if(entityInstances == null){
			entityInstances = new LinkedList<EntityInstance>();
			entityInstances.add(ec.createEntityInstanceFor(TESTTABLE_TABLENAME));
		}		
		ec.deleteEntitiesWithSecurity(deletor, entityInstances).getPassedEntities();
		
		csm = new CompoundStatementManual();	
		csm.addConditionalStatement(TESTTABLE_COLUMN_NAME + Q.E + Q.QUOT + "SuperStephen" +Q.QUOT);
		csm.addConditionalStatement(Q.OR);
		csm.addConditionalStatement(TESTTABLE_COLUMN_NAME + Q.E + Q.QUOT + "SuperSean" +Q.QUOT);
		List<EntityInstance> actualInstances = ec.getDatabaseConnection().read(SchemaInfo.refactorTableName(TESTTABLE_TABLENAME), csm.toSQLStatement());
		Assert.assertEquals(actualInstances.size(), 0);
		
		printTag("after deleting");
		Describer.describeEntityInstances(actualInstances);
		printTag("</PASSED TESTING deleteEntitiesWithSecurity(EntityInstance deletor, List<EntityInstance> entities)>\n");
		
		/**
		 * after test
		 */
		end();
//		ec.removeUserFromGroup(ec.readUser(ROOTNAME, ROOTPASS), user, Configuration.DEFAULT);
//		ec.deleteEntitiesWithSecurity(ec.readUser(ROOTNAME, ROOTPASS), needDeleteEntities);
		return null;
	}

	
	public ResultSecurity deleteEntityWithSecurity(EntityInstance deletor, EntityInstance entity) {
		/**
		 * before test
		 */
		begin();
		List<EntityInstance> needDeleteEntities = new LinkedList<EntityInstance>();
		EntityInstance user = createUser();
		needDeleteEntities.add(user);
		needDeleteEntities.addAll(addUserToGroup());
		needDeleteEntities.addAll(createEntity());
		needDeleteEntities.addAll(createEntityInstances());
		
		printTag("<TESTING deleteEntityWithSecurity(EntityInstance deletor, EntityInstance entity)>");
		printTag("user USERNAME deletes a EntityInstance of TESTTABLE -- NAME is SuperVan");
		
		deletor = ec.readUser(USERNAME, USERPASS);
		CompoundStatementManual csm = new CompoundStatementManual();	
		csm.addConditionalStatement(TESTTABLE_COLUMN_NAME + Q.E + Q.QUOT + "SuperVan" +Q.QUOT);
		EntityInstance entityInstance        = null;
		List<EntityInstance> entityInstances = ec.getDatabaseConnection().read(TESTTABLE_TABLENAME, csm.toSQLStatement());
		printTag("before deleting");
		Describer.describeEntityInstances(entityInstances);
		
		if(entityInstances!=null && entityInstances.size()>0){
			entityInstance = entityInstances.get(0);
			ec.deleteEntityWithSecurity(deletor, entityInstance).getPassedEntities();
		}
		
		csm = new CompoundStatementManual();	
		csm.addConditionalStatement(TESTTABLE_COLUMN_NAME + Q.E + Q.QUOT + "SuperVan" +Q.QUOT);
		List<EntityInstance> actualInstances = ec.getDatabaseConnection().read(SchemaInfo.refactorTableName(TESTTABLE_TABLENAME), csm.toSQLStatement());
		Assert.assertEquals(actualInstances.size(), 0);
		
		printTag("after deleting");
		Describer.describeEntityInstances(actualInstances);
		printTag("</PASSED TESTING deleteEntityWithSecurity(EntityInstance deletor, EntityInstance entity)>\n");
		
		/**
		 * after test
		 */
		end();
//		ec.removeUserFromGroup(ec.readUser(ROOTNAME, ROOTPASS), user, Configuration.DEFAULT);
//		ec.deleteEntitiesWithSecurity(ec.readUser(ROOTNAME, ROOTPASS), needDeleteEntities);
		return null;

	}

	
	public boolean deployDefinitionListWithSecurity( List<ClassToTables> classToTablesList, boolean dropTablesFirst, boolean continueOnError) {
		printTag("<TESTING deployDefinitionListWithSecurity( List<ClassToTables> classToTablesList, boolean dropTablesFirst, boolean continueOnError)>");
		printTag("Create the testTable");
		// Test Data Initialization
		classToTablesList = new LinkedList<ClassToTables>();	
		List<AttributeClass> attributes = new LinkedList<AttributeClass>();	
		AttributeClass primaryAttribute = new AttributeClass(TESTTABLE_COLUMN_STAFFID, testTable_dataType.get(TESTTABLE_COLUMN_STAFFID));
		primaryAttribute.setPrimaryKey(true);
		primaryAttribute.setAutoIncrement(true);
		attributes.add(primaryAttribute);
		attributes.add(new AttributeClass(TESTTABLE_COLUMN_NAME, testTable_dataType.get(TESTTABLE_COLUMN_NAME)));
		attributes.add(new AttributeClass(TESTTABLE_COLUMN_PHONE, testTable_dataType.get(TESTTABLE_COLUMN_PHONE)));		
		EntitySecurityClass entitySecurityClass = new EntitySecurityClass(TESTTABLE_TABLENAME, attributes.toArray(new AttributeClass[0]), null);
		classToTablesList.add(entitySecurityClass);			

		List<EntityInstance> defaultTokens = TokenFactory.getDefaultGrantTokens(ec.getDatabaseConnection());
		ec.deployDefinitionListWithSecurity(new LinkedList<EntityInstance>(),classToTablesList, false, false);
		
		// Test Code
	    EntityClass entityClass = ec.getEntityClass(ec.getDatabaseConnection(), TESTTABLE_TABLENAME, true);
	    
	    // <<< START Assert
	    Assert.assertEquals(SchemaInfo.refactorTableName(entitySecurityClass.getName()), entityClass.getName());
	    Assert.assertEquals(entitySecurityClass.getAttributeClassList().length, entityClass.getAttributeClassList().length);
	    List<String> columnNames = new LinkedList<String>();
	    for(int i = 0 ; i < entitySecurityClass.getAttributeClassList().length; i++){
	    	AttributeClass expectedAttribute = entitySecurityClass.getAttributeClassList()[i];
	    	AttributeClass actualAttribute   = entityClass.getAttributeClassList()[i];
	    	Assert.assertEquals(expectedAttribute.getName().toLowerCase(), actualAttribute.getName());
	    	Assert.assertEquals(expectedAttribute.getSqlType(), actualAttribute.getSqlType());
	    	Assert.assertEquals(expectedAttribute.isPrimaryKey(), actualAttribute.isPrimaryKey());
	    	Assert.assertEquals(expectedAttribute.isAutoIncrement(), actualAttribute.isAutoIncrement());
	    	columnNames.add( actualAttribute.getName());
	    }
	    
	    CompoundStatementManual csm = new CompoundStatementManual();
	    csm.addConditionalStatement(Column_Info_Table.getColumnTableName() + Q.E + Q.QUOT + entityClass.getName() + Q.QUOT );
	    List<EntityInstance> columnInfoInstances = ec.getDatabaseConnection().read(Column_Info_Table.makeColumnInfoTableName(), csm.toSQLStatement());
	    Assert.assertTrue(columnInfoInstances.size() > 0);
	    for(EntityInstance columnInfoInstance : columnInfoInstances){
	    	Assert.assertTrue(columnNames.contains(columnInfoInstance.get(Column_Info_Table.getColumnColumnName())));
	    }
	    // <<< END Assert
	    
	    List<ClassToTables> classToTables = new LinkedList<ClassToTables>();
	    classToTables.add(entityClass);
	    Describer.describeClassToTables(classToTables);
	    
	    printTag("</PASSED TESTING deployDefinitionListWithSecurity( List<ClassToTables> classToTablesList, boolean dropTablesFirst, boolean continueOnError)>\n");
	    return false;
	}

	
	public ResultSecurity readEntitiesWithSecurity(EntityInstance reader, String tableName, SQLStatement sql) {
		/**
		 * before test
		 */
		begin();
		List<EntityInstance> needDeleteEntities = new LinkedList<EntityInstance>();
		EntityInstance user = createUser();
		needDeleteEntities.add(user);
		needDeleteEntities.addAll(addUserToGroup());
		needDeleteEntities.addAll(createEntity());
		needDeleteEntities.addAll(createEntityInstances());
		
		printTag("<TESTING readEntitiesWithSecurity(EntityInstance reader, String tableName, SQLStatement sql)>");
		printTag("user USERNAME reads a list of EntityInstances from TESTTABLE -- NAME is Van, Sean or Stephen");
		reader    = ec.readUser(USERNAME, USERPASS);
		tableName = TESTTABLE_TABLENAME;
		CompoundStatementAuto csa = new CompoundStatementAuto(Q.OR);	
		csa.addCompoundStatement(TESTTABLE_COLUMN_NAME + Q.E + Q.QUOT + "Van" +Q.QUOT);
		csa.addCompoundStatement(TESTTABLE_COLUMN_NAME + Q.E + Q.QUOT + "Sean" +Q.QUOT);
		csa.addCompoundStatement(TESTTABLE_COLUMN_NAME + Q.E + Q.QUOT + "Stephen" +Q.QUOT);
		
		List<EntityInstance> expectedInstances = ec.readEntitiesWithSecurity(reader, tableName, csa).getPassedEntities();	

		List<EntityInstance> actualInstances   = ec.getDatabaseConnection().read(tableName, csa.toSQLStatement());
		
		// <<< START Assert
		Assert.assertTrue(TestAssist.compare(ec.getDatabaseConnection(), expectedInstances, actualInstances));
		// <<< END Assert
		
		Describer.describeEntityInstances(actualInstances);
		printTag("</PASSED TESTING readEntitiesWithSecurity(EntityInstance reader, String tableName, SQLStatement sql)>\n");
		
		/**
		 * after test
		 */
		end();
//		ec.removeUserFromGroup(ec.readUser(ROOTNAME, ROOTPASS), user, Configuration.DEFAULT);
//		ec.deleteEntitiesWithSecurity(ec.readUser(ROOTNAME, ROOTPASS), needDeleteEntities);
		return null;
	}

	
	public EntityInstance readUser(EntityInstance user) {
		/**
		 * before test
		 */
		begin();
		List<EntityInstance> needDeleteEntities = new LinkedList<EntityInstance>();
		needDeleteEntities.add(createUser());
		
		printTag("<TESTING readUser(EntityInstance user)>");
		printTag("Read user info of the user whose userName = USERNAME and password = USERPASS");
		// Test Data Initialization
		user = ec.createEntityInstanceFor(User_Info_Table.makeUserInfoTableName());
		user.set(Configuration.User_Info_Table.getColumnUserName(), USERNAME);
		user.set(Configuration.User_Info_Table.getColumnUserID(), USERNAME);
		user.set(Configuration.User_Info_Table.getColumnPassword(), USERPASS);
		EntityInstance userForActual = ec.readUser(user);
		Describer.describeEntityInstance(userForActual);
		// <<< START Assert
		Assert.assertTrue(TestAssist.compare(this.ec.getDatabaseConnection(), user, userForActual));
		// <<< END Assert
		printTag("</PASSED TESTING readUser(EntityInstance user)>\n");
		
		/**
		 * after test
		 */
		end();
//		ec.removeUserFromGroup(ec.readUser(ROOTNAME, ROOTPASS), user, Configuration.DEFAULT);
//		ec.deleteEntitiesWithSecurity(ec.readUser(ROOTNAME, ROOTPASS), needDeleteEntities);
		return null;
	}

	
	public EntityInstance readUser(String userName, String userPassword) {
		/**
		 * before test
		 */
		begin();
		List<EntityInstance> needDeleteEntities = new LinkedList<EntityInstance>();
		needDeleteEntities.add(createUser());
		
		printTag("<TESTING readUser(String userName, String userPassword)>");
		printTag("Read user info of the user whose userName = USERNAME and password = USERPASS");
		userName     = USERNAME;
		userPassword = USERPASS;
		EntityInstance user = ec.readUser(userName, userPassword);
		Describer.describeEntityInstance(user);
		// <<< START Assert
		Assert.assertEquals(USERNAME, user.get(User_Info_Table.getColumnUserName()));
		Assert.assertEquals(USERPASS, user.get(User_Info_Table.getColumnPassword()));
		// <<< EDN Assert
		printTag("<PASSED TESTING readUser(String userName, String userPassword)>\n");
		
		/**
		 * after test
		 */
		end();
//		ec.removeUserFromGroup(ec.readUser(ROOTNAME, ROOTPASS), user, Configuration.DEFAULT);
//		ec.deleteEntitiesWithSecurity(ec.readUser(ROOTNAME, ROOTPASS), needDeleteEntities);
		return null;
	}

	
	public ResultSecurity removeTokensFromColumns(EntityInstance deletor, String tableName, AttributeClass[] columns, List<EntityInstance> tokens) {
		/**
		 * before test
		 */
		begin();
		List<EntityInstance> needDeleteEntities = new LinkedList<EntityInstance>();
		EntityInstance user = createUser();
		needDeleteEntities.add(user);
		needDeleteEntities.addAll(addUserToGroup());
		needDeleteEntities.addAll(createEntity());
		needDeleteEntities.addAll(createEntityInstances());
		
		// add token to columns
		EntityInstance creator                   = ec.readUser(ROOTNAME, ROOTPASS);
		EntityClass entityClass   = ec.getEntityClass(ec.getDatabaseConnection(), TESTTABLE_TABLENAME, false);
		tableName                 = entityClass.getName();
		columns                   = Helper.primaryKeyFilter(entityClass.getAttributeClassList());
		tokens                    = ec.getTokensByGroup(creator, "DEFAULT").getGrantTokens();//ec.getDatabaseConnection().read(Token_Info_Table.makeTokenInfoTableName(), "GROUPTYPE = 'DEFAULT'");//ec.readEntities(Token_Info_Table.makeTokenInfoTableName(), "GROUPTYPE = 'DEFAULT'");	
		needDeleteEntities.addAll(ec.addTokensToColumns(creator, tableName, columns, tokens).getPassedEntities());
		
		printTag("<TESTING removeTokensFromColumns(EntityInstance deletor, String tableName, AttributeClass[] columns, List<EntityInstance> tokens)>");
		printTag("user ROOT removes the default Tokens from all columns of TESTTABLE");
		
		// Test Data Initialization
		deletor = ec.readUser(ROOTNAME, ROOTPASS);
		entityClass   = ec.getEntityClass(ec.getDatabaseConnection(), TESTTABLE_TABLENAME, false);
		tableName                 = entityClass.getName();
		columns                   = Helper.primaryKeyFilter(entityClass.getAttributeClassList());
		tokens    = ec.getTokensByGroup(creator, "DEFAULT").getGrantTokens();//ec.getTokensByGroup(creator, "DEFAULT").getGrantTokens();//ec.getTokensByGroup(creator, "DEFAULT").getGrantTokens();//ec.getDatabaseConnection().read(Token_Info_Table.makeTokenInfoTableName(), "GROUPTYPE = 'DEFAULT'");	

System.out.println("<<---VMC: TOKENS ");Describer.describeEntityInstances(tokens);

		List<EntityInstance> tokenColumnPairs = ec.removeTokensFromColumns(deletor, tableName, columns, tokens).getPassedEntities();
		Describer.describeEntityInstances(tokenColumnPairs);
		
		// Test Code
		CompoundStatementManual csm1 = new CompoundStatementManual();
		csm1.addConditionalStatement(Column_Info_Table.getColumnTableName() + Q.E + Q.QUOT + tableName + Q.QUOT);
		csm1.addConditionalStatement(Q.AND + Q.LEFT_PARENTHESIS);
		CompoundStatementManual csm2 = new CompoundStatementManual();
		for(AttributeClass column : columns){
			if(csm2.getCompoundStatements().size()>0){
				csm2.addConditionalStatement(Q.OR);
			}
			csm2.addConditionalStatement(Column_Info_Table.getColumnColumnName() + Q.E + Q.QUOT + column.getName() + Q.QUOT);
		}
		csm1.addConditionalStatement(csm2);
		csm1.addConditionalStatement(Q.RIGHT_PARENTHESIS);
		List<EntityInstance> columnInfoInstances = ec.getDatabaseConnection().read(Column_Info_Table.makeColumnInfoTableName(), csm1.toSQLStatement());

System.out.println("<<----VMC:");System.out.println(csm1); Describer.describeEntityInstances(columnInfoInstances);

		Assert.assertTrue(tokens.size() > 0);
		
		List<EntityInstance> acturalTokenColumnPairs = new LinkedList<EntityInstance>();
		for(EntityInstance token : tokens){
			String tokenId = (String)token.get(Token_Info_Table.getColumnTokenID());
			Boolean permission = (Boolean)token.get(Token_Info_Table.getColumnPermission());
			initializeColumnSecurityProperities(permission);
			CompoundStatementManual csm3 = new CompoundStatementManual();
			csm3.addConditionalStatement(COLUMNSECURITY_TOKENID + Q.E + Q.QUOT + tokenId + Q.QUOT);
			csm3.addConditionalStatement(Q.AND + Q.LEFT_PARENTHESIS);
			CompoundStatementManual csm4 = new CompoundStatementManual();
			for(EntityInstance columnInfoInstance : columnInfoInstances){
				Integer columnId = (Integer)columnInfoInstance.get(Column_Info_Table.makeColumnInfoTableColumnNameCoredbId());
				if(csm4.getCompoundStatements().size() > 0) {
					csm4.addConditionalStatement(Q.OR);
				}
				csm4.addConditionalStatement(COLUMNSECURITY_COLUMNID + Q.E + columnId);
			}
			csm3.addConditionalStatement(csm4);
			csm3.addConditionalStatement(Q.RIGHT_PARENTHESIS);
			acturalTokenColumnPairs.addAll(ec.getDatabaseConnection().read(COLUMNSECURITY_TABLENAME, csm3.toSQLStatement()));
System.out.println("<<<---VMC: "+csm3);Describer.describeEntityInstances(columnInfoInstances);
		}	
		
		// <<< START Assert
		Assert.assertFalse(TestAssist.isSubSet(ec.getDatabaseConnection(), tokenColumnPairs, acturalTokenColumnPairs));
		Assert.assertTrue(TestAssist.noCommonSubSet(ec.getDatabaseConnection(), tokenColumnPairs, acturalTokenColumnPairs));
		// <<< END Assert
		printTag("</PASSED TESTING removeTokensFromColumns(EntityInstance deletor, String tableName, AttributeClass[] columns, List<EntityInstance> tokens)>\n");
	
		/**
		 * after test
		 */
		end();
//		ec.removeUserFromGroup(ec.readUser(ROOTNAME, ROOTPASS), user, Configuration.DEFAULT);
//		ec.deleteEntitiesWithSecurity(ec.readUser(ROOTNAME, ROOTPASS), needDeleteEntities);
		return null;
		
	}

	
	public ResultSecurity removeTokensFromRows(EntityInstance deletorId,
			List<EntityInstance> rows, List<EntityInstance> tokens) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public ResultSecurity removeUserFromGroup(EntityInstance deletor, EntityInstance deletee, String group) {
		/**
		 * before test
		 */
		begin();
		List<EntityInstance> needDeleteEntities = new LinkedList<EntityInstance>();
		EntityInstance user = createUser();
		needDeleteEntities.add(user);
		needDeleteEntities.addAll(addUserToGroup());
		needDeleteEntities.addAll(createEntity());
		needDeleteEntities.addAll(createEntityInstances());
		
		printTag("<TESTING removeUserFromGroup(EntityInstance deletor, EntityInstance deletee, String group)>");
		printTag("user ROOT removes user USERNAME from group 'default'");
		
		// Test Data Initialization
		deletor  = ec.readUser(ROOTNAME, ROOTPASS);
		deletee  = ec.readUser(USERNAME, USERPASS);
		group    = "DEFAULT";
		List<EntityInstance> userTokenPairs = ec.removeUserFromGroup(deletor, deletee, group).getPassedEntities();
		Describer.describeEntityInstances(userTokenPairs);
		
		CompoundStatementManual csm = new CompoundStatementManual();
		csm.addConditionalStatement(Token_Info_Table.getColumnGroup() + Q.E + Q.QUOT + group + Q.QUOT);
		List<EntityInstance> tokens  = ec.getDatabaseConnection().read(Token_Info_Table.makeTokenInfoTableName(), csm.toSQLStatement());
		CompoundStatementAuto csa    = new CompoundStatementAuto(Q.OR);
		for(EntityInstance token : tokens) {
			String tokenId               = (String)token.get(Token_Info_Table.getColumnTokenID());
			csa.addCompoundStatement(User_Token_Table.getColumnTokenID() + Q.E + Q.QUOT + tokenId +Q.QUOT);
		}
		List<EntityInstance> userTokenPairsForActual = ec.getDatabaseConnection().read(User_Token_Table.makeUserTokenTableName(), csa.toSQLStatement());
		
		Assert.assertTrue(userTokenPairsForActual.size() == 0);	
		
		printTag("</PASSED TESTING removeUserFromGroup(EntityInstance deletor, EntityInstance deletee, String group)>\n");
		
		/**
		 * after test
		 */
		end();
//		ec.removeUserFromGroup(ec.readUser(ROOTNAME, ROOTPASS), user, Configuration.DEFAULT);
//		ec.deleteEntitiesWithSecurity(ec.readUser(ROOTNAME, ROOTPASS), needDeleteEntities);
		return null;
	}

	
	@Test
	public ResultSecurity updateEntitiesWithSecurity(EntityInstance updator, List<EntityInstance> entities) {
		/**
		 * before test
		 */
		begin();
		List<EntityInstance> needDeleteEntities = new LinkedList<EntityInstance>();
		EntityInstance user = createUser();
		needDeleteEntities.add(user);
		needDeleteEntities.addAll(addUserToGroup());
		needDeleteEntities.addAll(createEntity());
		needDeleteEntities.addAll(createEntityInstances());
		
		printTag("<TESTING updateEntitiesWithSecurity(EntityInstance updator, List<EntityInstance> entities)>");
		printTag("user USERNAME updates a list of EntityInstances of TESTTABLE -- NAME is Sean or Stephen: add 'Super' at the start");
		
		updator                   = ec.readUser(USERNAME, USERPASS);
		CompoundStatementAuto csa = new CompoundStatementAuto(Q.OR);	
		csa.addCompoundStatement(TESTTABLE_COLUMN_NAME + Q.E + Q.QUOT + "Stephen" +Q.QUOT);
		csa.addCompoundStatement(TESTTABLE_COLUMN_NAME + Q.E + Q.QUOT + "Sean" +Q.QUOT);

		List<EntityInstance> entityInstances = ec.getDatabaseConnection().read(TESTTABLE_TABLENAME, csa.toSQLStatement());
		printTag("before updating");
		Describer.describeEntityInstances(entityInstances);
		
		for(EntityInstance entityInstance : entityInstances){
			entityInstance.set(TESTTABLE_COLUMN_NAME, "Super" + entityInstance.get(TESTTABLE_COLUMN_NAME).toString());
		}
		
		List<EntityInstance> expectedInstances = ec.updateEntitiesWithSecurity(updator, entityInstances).getPassedEntities();
		
		CompoundStatementManual csm = new CompoundStatementManual();	
		csm = new CompoundStatementManual();	
		csm.addConditionalStatement(TESTTABLE_COLUMN_NAME + Q.E + Q.QUOT + "SuperStephen" +Q.QUOT);
		csm.addConditionalStatement(Q.OR);
		csm.addConditionalStatement(TESTTABLE_COLUMN_NAME + Q.E + Q.QUOT + "SuperSean" + Q.QUOT);
		List<EntityInstance> actualInstances = ec.getDatabaseConnection().read(SchemaInfo.refactorTableName(TESTTABLE_TABLENAME), csm.toSQLStatement());
		
		// <<< START Assert
		Assert.assertTrue(TestAssist.compare(ec.getDatabaseConnection(), expectedInstances, actualInstances));
		// <<< END Assert
		
		printTag("after updating");
		Describer.describeEntityInstances(actualInstances);
		printTag("</PASSED TESTING updateEntitiesWithSecurity(EntityInstance updator, List<EntityInstance> entities)>\n");
		/**
		 * after test
		 */
		end();
		return null;
		
	}

	public void testHiddenValue() {
		/**
		 * before test
		 */
		begin();
		List<EntityInstance> needDeleteEntities = new LinkedList<EntityInstance>();
		EntityInstance user = createUser();
		needDeleteEntities.add(user);
		needDeleteEntities.addAll(addUserToGroup());
		needDeleteEntities.addAll(createEntity());
		needDeleteEntities.addAll(createEntityInstances());
		
		printTag("<TESTING testHiddenValue()>");
		
		// remove +R token from column3
		EntityInstance creator      = ec.readUser(ROOTNAME, ROOTPASS);
		EntityClass entityClass     = ec.getEntityClass(ec.getDatabaseConnection(), TESTTABLE_TABLENAME, false);
		String tableName            = entityClass.getName();
		AttributeClass[] columns    = new AttributeClass[]{entityClass.getAttributeClassList()[2]};
		List<EntityInstance> tokens = ec.getDatabaseConnection().read(Token_Info_Table.makeTokenInfoTableName(), "tokenname = 'ROOT$R$G'");
		List<EntityInstance> eee    = ec.removeTokensFromColumns(creator, tableName, columns, tokens).getPassedEntities();
		Describer.describeEntityInstances(eee);
		
		updateEntityWithSecurity(creator, null);
		
		// add +R token to column3
		ec.addTokensToColumns(creator, tableName, columns, tokens).getPassedEntities();
		
		printTag("</PASSED TESTING testHiddenValue()>");
		
		/**
		 * after test
		 */
		end();
//		ec.removeUserFromGroup(ec.readUser(ROOTNAME, ROOTPASS), user, Configuration.DEFAULT);
//		ec.deleteEntitiesWithSecurity(ec.readUser(ROOTNAME, ROOTPASS), needDeleteEntities);
	}
	
	
	public ResultSecurity updateEntityWithSecurity(EntityInstance updator, EntityInstance entity) {
		printTag("<TESTING updateEntityWithSecurity(EntityInstance updator, EntityInstance entity)>");
		printTag("user USERNAME updates a EntityInstance of TESTTABLE -- NAME is Van: change from Van to SuperVan");

		CompoundStatementManual csm = new CompoundStatementManual();	
		csm.addConditionalStatement(TESTTABLE_COLUMN_NAME + Q.E + Q.QUOT + "Van" +Q.QUOT);
		
		List<EntityInstance> entityInstances = ec.readEntitiesWithSecurity(updator, TESTTABLE_TABLENAME, csm).getPassedEntities();
		printTag("before updating");
		Describer.describeEntityInstances(entityInstances);
		
		EntityInstance entityInstance          = null;
		List<EntityInstance> expectedInstances = null;
		if(entityInstances!=null && entityInstances.size()>0){
			entityInstance = entityInstances.get(0);
			entityInstance.set(TESTTABLE_COLUMN_NAME, "SuperVan");
			expectedInstances = ec.updateEntityWithSecurity(updator, entityInstance).getPassedEntities();
		}
		
		csm = new CompoundStatementManual();	
		csm.addConditionalStatement(TESTTABLE_COLUMN_NAME + Q.E + Q.QUOT + "SuperVan" +Q.QUOT);
		List<EntityInstance> actualInstances = ec.readEntitiesWithSecurity(updator, TESTTABLE_TABLENAME, csm).getPassedEntities();
		printTag("after updating");
		Describer.describeEntityInstances(actualInstances);
		
		// <<< START Assert
		Assert.assertTrue(TestAssist.compare(ec.getDatabaseConnection(), expectedInstances, actualInstances));
		// <<< END Assert
		
		printTag("</PASSED TESTING updateEntityWithSecurity(EntityInstance updator, EntityInstance entity)>\n");
		return null;
		
	}
	
	private void initializeColumnSecurityProperities(boolean permission){
		if(permission){
			COLUMNSECURITY_TABLENAME = Column_Security_Grant_Table.makeColumnSecurityGrantTableName();
			COLUMNSECURITY_TOKENID   = Column_Security_Grant_Table.getColumnTokenID();
			COLUMNSECURITY_COLUMNID  = Column_Security_Grant_Table.getColumnColumnID();
		}else{
			COLUMNSECURITY_TABLENAME = Column_Security_Deny_Table.makeColumnSecurityDenyTableName();
			COLUMNSECURITY_TOKENID   = Column_Security_Deny_Table.getColumnTokenID();
			COLUMNSECURITY_COLUMNID  = Column_Security_Deny_Table.getColumnColumnID();
		}
	}

	public int createEntities(String userId,
			List<? extends EntityInstance> entities) {
		return 0;
	}

	public <T extends EntityInstance> int createEntity(String userId, T entity) {
		return 0;
	}

	public int deleteEntities(String userId,
			List<? extends EntityInstance> entities) {
		return 0;
	}

	public <T extends EntityInstance> int deleteEntity(String userId, T entity) {
		return 0;
	}

	public int generateLatestChangeSetNumber(String userId) {
		return 0;
	}

	public ClassToTables generateMappingTableTracing(ClassToTables leftTable,
			AttributeClass[] leftAcl, ClassToTables rightTable,
			AttributeClass[] rightAcl) {
		return null;
	}

	public int getLatestChangeSetNumber() {
		return 0;
	}

	public void initializeMode(boolean reRead, Mode mode) {
		
	}

	public List<EntityInstance> readEntities(String userId, String tableName,
			SQLStatement sql) {
		return null;
	}

	public List<EntityInstance> readEntities(String userId, String tableName,
			String sql) {
		return null;
	}

	public List<EntityInstance> traceEntities(String userId, String tableName) {
		return null;
	}

	public List<EntityInstance> traceEntities(String tableName,
			SQLStatement sql, String userId) {
		return null;
	}

	public List<EntityInstance> traceEntities(String tableName,
			String entityMappingId, int minCSN, int currentCSN, int maxCSN,
			String userId) {
		return null;
	}

	public List<EntityInstance> traceEntities(String tableName,
			String entityMappingId, int startCSN, int endCSN, String userId) {
		return null;
	}

	public int updateEntities(String userId,
			List<? extends EntityInstance> entities) {
		return 0;
	}

	public <T extends EntityInstance> int updateEntity(String userId, T entity) {
		return 0;
	}

	public EntityInstance createEntityInstanceFor(String tableName) {
		return null;
	}

	public EntityInstance createEntityInstanceFor(String tableName,
			boolean reRead) {
		return null;
	}

	public boolean deployDefinitionList(List<ClassToTables> classToTablesList) {
		return false;
	}

	public boolean deployDefinitionList(List<ClassToTables> classToTablesList,
			boolean dropTablesFirst, boolean continueOnError) {
		return false;
	}

	public void dropAllTables() {
		
	}

	public void dropTables(List<String> tableNames) {
		
	}

	public List<EntityInstance> executeArbitrarySQL(String sql) {
		return null;
	}

	public List<EntityInstance> executeArbitrarySQL(String queryName, String sql) {
		return null;
	}

	public ClassToTables generateMappingTable(ClassToTables leftTable,
			AttributeClass[] leftAcl, ClassToTables rightTable,
			AttributeClass[] rightAcl) {
		return null;
	}

	public AttributeClass getAttributeClass(String tableName,
			String attributeName) {
		return null;
	}

	public AttributeClass getAttributeClass(EntityInstance entity,
			String attributeName) {
		return null;
	}

	public String getCompoundStatementOnPrimaryKeys(
			DatabaseConnection databaseConnection, EntityInstance entityInstance) {
		return null;
	}

	public DatabaseConnection getDatabaseConnection() {
		return null;
	}

	public DynaClass getDynaClassFor(String tableName) {
		return null;
	}

	public EntityClass getEntityClass(DatabaseConnection databaseConnection,
			String tableName, boolean reRead) {
		return null;
	}

	public List<String> readTableNameList(boolean reRead) {
		return null;
	}

	public void refresh() {
		
	}

}

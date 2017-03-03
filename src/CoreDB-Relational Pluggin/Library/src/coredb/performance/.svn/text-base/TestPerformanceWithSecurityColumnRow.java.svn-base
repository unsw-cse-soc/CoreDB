/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.performance;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import junit.framework.Assert;
import coredb.config.Configuration;
import coredb.config.Configuration.Column_Info_Table;
import coredb.config.Configuration.Mapping_Table;
import coredb.config.Configuration.Row_Security_Deny_Table;
import coredb.config.Configuration.Row_Security_Grant_Table;
import coredb.config.Configuration.Token_Info_Table;
import coredb.config.Configuration.User_Info_Table;
import coredb.controller.EntityControllerSecurity;
import coredb.database.SchemaInfo;
import coredb.mode.Mode;
import coredb.security.TokenFactory;
import coredb.sql.CompoundStatementManual;
import coredb.sql.Q;
import coredb.unit.AttributeClass;
import coredb.unit.CRUD;
import coredb.unit.ClassToTables;
import coredb.unit.EntityClass;
import coredb.unit.EntityInstance;
import coredb.unit.EntitySecurityClass;

/**
 *
 * @author stephen
 */
public class TestPerformanceWithSecurityColumnRow {
	private static EntityControllerSecurity iecs         = null;
	private int id                                       = 0;
	
	private static final String TESTTABLE_TABLENAME      = "TEST";
	private static final String TESTTABLE_COLUMN_STAFFID = "STAFFID";
	private static final String TESTTABLE_COLUMN_CHARAC  = "CHARAC";
	private static final String TESTTABLE_COLUMN_BYTEA   = "BYTEA";
	private static final String TESTTABLE_COLUMN_NAME    = "NAME";
	private static final String TESTTABLE_COLUMN_PHONE   = "PHONE";
	
	@SuppressWarnings("unchecked")
	private static HashMap<String, Class> testTable_dataType = new HashMap<String, Class>();
	
	private static final String ROOTNAME                 = "ROOT";
	private static final String ROOTPASS                 = "ROOT";
	
	private static EntityInstance root                   = null;
	private static EntityInstance user1                  = null;	
	
	
	private static long startTime                                 = 0;
	private static long endTime                                   = 0;
	private static int  existingEntities                          = 100;
	private static int  userNumbers                               = 10;
	private static int  tokenGroupNumbers                         = 100;
	private static int  tokenGroupNumberWhichApplyOnASingleColumn = 2;
	private static int  tokenGroupNumberWhichApplyOnASingleRow    = 2;
	private static int 	runs                                      = 10;
	
	
	public static void main(String[] args) throws SQLException {
		if (args.length == 3) {
			existingEntities	= Integer.parseInt(args[0]);
			userNumbers			= Integer.parseInt(args[1]);
			tokenGroupNumbers	= Integer.parseInt(args[2]);
		}
	
		TestPerformanceWithSecurityColumnRow test = new TestPerformanceWithSecurityColumnRow();
		test.setup();
		test.performanceTest();
	
		for (int i=1000; i<=5000; i+=1000) {
			test.cleanUp();
			existingEntities = i;
			test.testCreateEntitiesPerformance();
			test.testAddTokensToRowsPerformance();
			test.testReadEntitiesPerformance();
			test.testUpdateEntitiesPerformance();
			test.testDeleteEntitiesPerformance();
			System.out.println("=================");
		}
		
	}
	
	public void performanceTest(){
		printTag("\n++++++++++++++++++++ Performance Test Start ++++++++++++++++++++\n");
		testCreateUsersPerformance();		
		System.out.println();
		testCreateTokensPerformance();
		System.out.println();
		testAddUserToGroupPerformance();
		System.out.println();
		testAddTokensToColumnsPerformance();
		System.out.println();
		testCreateEntitiesPerformance();
		System.out.println();
		testReadEntitiesPerformance();
		System.out.println();
		testUpdateEntitiesPerformance();
		System.out.println();
		testDeleteEntitiesPerformance();
		System.out.println();
	}

	private void testCreateUsersPerformance() {
		printTag("[testCreateUsersPerformance]\n");
		
		List<EntityInstance> users = new LinkedList<EntityInstance>();
		for(int i = 1 ; i <= userNumbers ; i ++){
			EntityInstance user = iecs.createEntityInstanceFor(User_Info_Table.makeUserInfoTableName());
			user.set(User_Info_Table.getColumnUserName(), "X_" + i);
			user.set(User_Info_Table.getColumnUserID(), "X_" + i);
			user.set(User_Info_Table.getColumnPassword(), "X_" + i + "_PASS");
			users.add(user);
		}
		
		startTime = System.currentTimeMillis();
		for(EntityInstance user : users){
			iecs.createUser(root, user);
		}
		endTime    = System.currentTimeMillis();
		String out = String.format("%30s | %10d | %10d | milliseconds\n", "testCreateUsersPerformance", userNumbers, (endTime - startTime)/userNumbers);
		printErrTag(out);
		
		printTag("[/testCreateUsersPerformance]\n");

	}
	
	private void testAddTokensToRowsPerformance() {
		printTag("[testAddTokensToRowsPerformance]\n");
		List<EntityInstance> rows         = iecs.getDatabaseConnection().read(TESTTABLE_TABLENAME, "");
		List<EntityInstance> tokenBag     = new LinkedList<EntityInstance>();
		for(int i = 0 ; i < tokenGroupNumberWhichApplyOnASingleRow; i++){
			int index                     = new Random().nextInt(tokenGroupNumbers)+1;
			tokenBag.addAll(iecs.getTokensByGroup(root, "T_" + index).getGrantTokens());
		}
		
		startTime = System.currentTimeMillis();
		int size = rows.size();
		iecs.addTokensToRows(root, rows, tokenBag);
		endTime    = System.currentTimeMillis();
		String out = String.format("%30s | %10d | %10d | milliseconds\n", "testAddTokensToRowsPerformance", tokenGroupNumberWhichApplyOnASingleColumn, (endTime - startTime)/size);
		printErrTag(out);
		
		printTag("[/testAddTokensToRowsPerformance]\n");
	}
	
	private void testAddTokensToColumnsPerformance() {
		printTag("[testAddTokensToColumnsPerformance]\n");
		AttributeClass[] columns = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
		List<EntityInstance> tokenBag = new LinkedList<EntityInstance>();
		for(int i = 0 ; i < tokenGroupNumberWhichApplyOnASingleColumn; i++){
			int index                   = new Random().nextInt(tokenGroupNumbers)+1;
			tokenBag.addAll(iecs.getTokensByGroup(root, "T_" + index).getGrantTokens());
		}
		
		startTime = System.currentTimeMillis();
		for(AttributeClass column : columns){
			iecs.addTokensToColumns(root, TESTTABLE_TABLENAME, new AttributeClass[]{column}, tokenBag);
		}
		endTime    = System.currentTimeMillis();
		String out = String.format("%30s | %10d | %10d | milliseconds\n", "testAddTokensToColumnsPerformance", tokenGroupNumberWhichApplyOnASingleColumn, (endTime - startTime)/columns.length);
		printErrTag(out);
		
		printTag("[/testAddTokensToColumnsPerformance]\n");
	}
	
	private void testCreateTokensPerformance(){
		printTag("[testCreateTokensPerformance]\n");
		
		List<EntityInstance> tokens = new LinkedList<EntityInstance>();
		for(int i = 1 ; i <= tokenGroupNumbers ; i ++){
			for(Character crud : new Character[]{CRUD.CREATE, CRUD.READ, CRUD.UPDATE, CRUD.DELETE}){
				EntityInstance tokenG = iecs.createEntityInstanceFor(Token_Info_Table.makeTokenInfoTableName());
				EntityInstance tokenD = iecs.createEntityInstanceFor(Token_Info_Table.makeTokenInfoTableName());

				tokenG.set(Token_Info_Table.getColumnTokenName(), "T_" + i + "$" + crud + "$G");
				tokenG.set(Token_Info_Table.getColumnTokenID(), "T_" + i + "$" + crud + "$G");
				tokenG.set(Token_Info_Table.getColumnGroup(), "T_" + i);
				tokenG.set(Token_Info_Table.getColumnCRUDType(), crud);
				tokenG.set(Token_Info_Table.getColumnPermission(), true);
				
				tokenD.set(Token_Info_Table.getColumnTokenName(), "T_" + i + "$" + crud + "$D");
				tokenD.set(Token_Info_Table.getColumnTokenID(), "T_" + i + "$" + crud + "$D");
				tokenD.set(Token_Info_Table.getColumnGroup(), "T_" + i);
				tokenD.set(Token_Info_Table.getColumnCRUDType(), crud);
				tokenD.set(Token_Info_Table.getColumnPermission(), false);
				
				tokens.add(tokenG);
				tokens.add(tokenD);
			}
		}
		
		startTime  = System.currentTimeMillis();
		iecs.createEntitiesWithSecurity(root, tokens);
		endTime    = System.currentTimeMillis();
		String out = String.format("%30s | %10d | %10d | milliseconds\n",  "testCreateTokensPerformance", tokenGroupNumbers, (endTime - startTime)/tokenGroupNumbers);
		printErrTag(out);
		
		printTag("[/testCreateTokensPerformance]\n");
	}
	
	private void testAddUserToGroupPerformance(){
		printTag("[testAddUserToGroupPerformance]\n");
		
		List<EntityInstance> users = new LinkedList<EntityInstance>();
		for(int i = 1 ; i <= userNumbers ; i ++) {
			users.add(iecs.readUser("X_" + i, "X_" + i + "_PASS"));
		}
		
		startTime = System.currentTimeMillis();
		for(EntityInstance user : users){
			String group = "T_" + (new Random().nextInt(tokenGroupNumbers)+1);
			iecs.addUserToGroup(root, user, group);
		}	
		endTime    = System.currentTimeMillis();
		String out = String.format("%30s | %10d | %10d | milliseconds\n", "testAddUserToGroupPerformance", userNumbers, (endTime - startTime)/userNumbers);
		printErrTag(out);
		
		printTag("[/testAddUserToGroupPerformance]\n");
	}

	private void testCreateEntitiesPerformance(){
		printTag("[testCreateEntitiesPerformance]\n");
		
		List<EntityInstance> entityInstances = new LinkedList<EntityInstance>();
		for(id = 1 ; id <= existingEntities; id ++){
			entityInstances.addAll(generateSourceEntities());
		}
		int size = entityInstances.size();
		
		startTime  = System.currentTimeMillis();
		iecs.createEntitiesWithSecurity(user1, entityInstances);
		endTime    = System.currentTimeMillis();
		String out = String.format("%30s | %10d | %10d | milliseconds\n", "testCreateEntitiesPerformance", existingEntities, (endTime - startTime)/size );
		printErrTag(out);
		
		printTag("[/testCreateEntitiesPerformance]\n");
	}
	
	private void testReadEntitiesPerformance(){
		printTag("[testReadEntitiesPerformance]\n");
		
		List<CompoundStatementManual> csmList = new LinkedList<CompoundStatementManual>();
		Random random = new Random();
		int range = existingEntities;
		for (int i=1; i<=runs; i++) {
			CompoundStatementManual csm           = new CompoundStatementManual();
			int staffId = random.nextInt(range)+1;
			csm.addConditionalStatement(TESTTABLE_COLUMN_STAFFID.toLowerCase() + Q.E + staffId);
			csmList.add(csm);
		}
		
		startTime = System.currentTimeMillis();
		for(CompoundStatementManual csm : csmList){
			iecs.readEntitiesWithSecurity(user1, TESTTABLE_TABLENAME, csm);
		}
		endTime    = System.currentTimeMillis();
		String out = String.format("%30s | %10d | %10d | milliseconds\n", "testReadEntitiesPerformance", existingEntities, (endTime - startTime)/runs);
		printErrTag(out);
		
		printTag("[/testReadEntitiesPerformance]\n");
	}
	
	private void testUpdateEntitiesPerformance(){
		printTag("[testUpdateEntitiesPerformance]\n");
		
		List<CompoundStatementManual> csmList = new LinkedList<CompoundStatementManual>();
		List<EntityInstance> entityInstances  = new LinkedList<EntityInstance>();
		Random random = new Random();
		int range = existingEntities;
		for (int i=1; i<=runs; i++) {
			CompoundStatementManual csm       = new CompoundStatementManual();
			int staffId = random.nextInt(range)+1;
			csm.addConditionalStatement(TESTTABLE_COLUMN_STAFFID.toLowerCase() + Q.E + staffId);
			csmList.add(csm);
		}		
		for(CompoundStatementManual csm : csmList){
			entityInstances.addAll(iecs.readEntitiesWithSecurity(user1, TESTTABLE_TABLENAME, csm).getPassedEntities());
		}	
		int size                              = entityInstances.size();
		for(EntityInstance entityInstance :  entityInstances){
			entityInstance.set(TESTTABLE_COLUMN_NAME, "SuperX");
		}
		
		startTime = System.currentTimeMillis();
		iecs.updateEntitiesWithSecurity(user1, entityInstances);
		endTime = System.currentTimeMillis();
		String out = String.format("%30s | %10d | %10d | milliseconds\n", "testUpdateEntitiesPerformance", existingEntities, (endTime - startTime)/size);
		printErrTag(out);
		
		printTag("[/testUpdateEntitiesPerformance]\n");
	}
	
	private void testDeleteEntitiesPerformance(){
		printTag("[testDeleteEntitiesPerformance]\n");
		
		List<CompoundStatementManual> csmList = new LinkedList<CompoundStatementManual>();
		List<EntityInstance> entityInstances  = new LinkedList<EntityInstance>();
		Random random = new Random();
		int range = existingEntities;
		for (int i=1; i<=runs; i++) {
			CompoundStatementManual csm       = new CompoundStatementManual();
			int staffId = random.nextInt(range)+1;
			csm.addConditionalStatement(TESTTABLE_COLUMN_STAFFID.toLowerCase() + Q.E + staffId);
			csmList.add(csm);
		}		
		for(CompoundStatementManual csm : csmList){
			entityInstances.addAll(iecs.readEntitiesWithSecurity(user1, TESTTABLE_TABLENAME, csm).getPassedEntities());
		}	
		int size                              = entityInstances.size();
		
		startTime = System.currentTimeMillis();
		iecs.deleteEntitiesWithSecurity(user1, entityInstances);
		endTime = System.currentTimeMillis();
		String out = String.format("%30s | %10d | %10d | milliseconds\n", "testDeleteEntitiesPerformance", existingEntities, (endTime - startTime)/size);
		printErrTag(out);
		
		printTag("[/testDeleteEntitiesPerformance]\n");	
	}	
	
	private void setup() throws SQLException {
		printTag("\n++++++++++++++++++++++++++++++++++++++START Set Up Test Case++++++++++++++++++++++++++++++++++++++++\n");
		
		iecs = new EntityControllerSecurity("/tmp/credential.txt");
		iecs.dropAllTables(root);
		testTable_dataType.put(TESTTABLE_COLUMN_STAFFID, Integer.class);
		testTable_dataType.put(TESTTABLE_COLUMN_CHARAC, Character.class);
		testTable_dataType.put(TESTTABLE_COLUMN_BYTEA, byte[].class);
		testTable_dataType.put(TESTTABLE_COLUMN_NAME, String.class);
		testTable_dataType.put(TESTTABLE_COLUMN_PHONE, String.class);
		
		iecs.initializeMode(true, Mode.SECURITY);
		deployDefinitionListWithRowSecurity(null,true,true);
		
		root                                    = getRootUser();
		HashMap<String, EntityInstance> userMap = generateNormalUser(new String[][]{ {"User1","User1", "User1Pass"}  });
		user1                                   = userMap.get("User1");	
		
		createTokens("general", CRUD.CREATE, true);
		createTokens("general", CRUD.READ, true);
		createTokens("general", CRUD.UPDATE, true);
		createTokens("general", CRUD.DELETE, true);

		createTokens("general", CRUD.CREATE, false);
		createTokens("general", CRUD.READ, false);
		createTokens("general", CRUD.UPDATE, false);
		createTokens("general", CRUD.DELETE, false);
		
	}
	
	private static void printTag(String tag) {
	    System.out.print(tag);
	}
	
	private static void printErrTag(String tag){
		System.out.print(tag);
	}
	
	private List<ClassToTables> deployDefinitionListWithRowSecurity( List<ClassToTables> classToTablesList, boolean dropTablesFirst, boolean continueOnError) {
		// Test Data Initialization
		classToTablesList = new LinkedList<ClassToTables>();	
		List<AttributeClass> attributes = new LinkedList<AttributeClass>();	
		AttributeClass primaryAttribute = new AttributeClass(TESTTABLE_COLUMN_STAFFID, testTable_dataType.get(TESTTABLE_COLUMN_STAFFID));
		primaryAttribute.setPrimaryKey(true);
		attributes.add(primaryAttribute);
		

		attributes.add(new AttributeClass(TESTTABLE_COLUMN_CHARAC, testTable_dataType.get(TESTTABLE_COLUMN_CHARAC)));	
		attributes.add(new AttributeClass(TESTTABLE_COLUMN_BYTEA, testTable_dataType.get(TESTTABLE_COLUMN_BYTEA)));
		attributes.add(new AttributeClass(TESTTABLE_COLUMN_NAME, testTable_dataType.get(TESTTABLE_COLUMN_NAME)));
		attributes.add(new AttributeClass(TESTTABLE_COLUMN_PHONE, testTable_dataType.get(TESTTABLE_COLUMN_PHONE)));		
		EntitySecurityClass entitySecurityClass = new EntitySecurityClass(TESTTABLE_TABLENAME, attributes.toArray(new AttributeClass[0]), null);
		classToTablesList.add(entitySecurityClass);			

		List<EntityInstance> defaultTokens = TokenFactory.getDefaultGrantTokens(iecs.getDatabaseConnection());
		iecs.deployDefinitionListWithSecurity(defaultTokens,classToTablesList, false, false);
		
		// Test Code
	    EntityClass entityClass = iecs.getEntityClass(iecs.getDatabaseConnection(), TESTTABLE_TABLENAME, true);
	    
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
	    List<EntityInstance> columnInfoInstances = iecs.getDatabaseConnection().read(Column_Info_Table.makeColumnInfoTableName(), csm.toSQLStatement());
	    Assert.assertTrue(columnInfoInstances.size() > 0);
	    for(EntityInstance columnInfoInstance : columnInfoInstances){
	    	Assert.assertTrue(columnNames.contains(columnInfoInstance.get(Column_Info_Table.getColumnColumnName())));
	    }
	    
	    EntityClass mappingEntityClass           = iecs.getEntityClass(iecs.getDatabaseConnection(), Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME), true);
	    EntityClass rowGrantSecurityEntityClass  = iecs.getEntityClass(iecs.getDatabaseConnection(), Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), true);
	    EntityClass denyGrantSecurityEntityClass = iecs.getEntityClass(iecs.getDatabaseConnection(), Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), true);

	    Assert.assertNotNull(mappingEntityClass);
	    Assert.assertNotNull(rowGrantSecurityEntityClass);
	    Assert.assertNotNull(denyGrantSecurityEntityClass);
	    
	    // <<< END Assert
	    
	    List<ClassToTables> classToTables = new LinkedList<ClassToTables>();
	    classToTables.add(entityClass);
	    
	    return classToTables;
	}	
	
	private HashMap<String, EntityInstance> generateNormalUser(String[][] userInfos) {

		HashMap<String, EntityInstance> userMap = new HashMap<String, EntityInstance>();
		EntityInstance root = getRootUser();
		for(String[] userInfo : userInfos) {
			EntityInstance user = iecs.createEntityInstanceFor(User_Info_Table.makeUserInfoTableName());
			user.set(User_Info_Table.getColumnUserID(), userInfo[0]);
			user.set(User_Info_Table.getColumnUserName(), userInfo[1]);
			user.set(User_Info_Table.getColumnPassword(), userInfo[2]);
			iecs.createUser(root, user);
			userMap.put(userInfo[0], iecs.readUser(userInfo[1], userInfo[2]));
		}	
		return userMap;
	}
	
	private EntityInstance getRootUser(){
		EntityInstance root   = iecs.readUser(ROOTNAME,ROOTPASS);
		return root;
	}
	
    private EntityInstance createTokens(String groupName, Character crud, boolean isGrant){
    	EntityInstance token = iecs.createEntityInstanceFor(Token_Info_Table.makeTokenInfoTableName());
    	token.set(Token_Info_Table.getColumnTokenID(), Configuration.Security_Methods.makeGroupPermission(groupName, crud, isGrant));
    	token.set(Token_Info_Table.getColumnTokenName(), Configuration.Security_Methods.makeGroupPermission(groupName, crud, isGrant));
    	token.set(Token_Info_Table.getColumnCRUDType(), crud);
    	token.set(Token_Info_Table.getColumnGroup(), groupName);
    	token.set(Token_Info_Table.getColumnPermission(), isGrant);
    	iecs.createEntityWithSecurity(root, token.clone());
    	return token;
    }
    
	private List<EntityInstance> generateSourceEntities(){
		List<EntityInstance> sourceEntities = new LinkedList<EntityInstance>();
		
		EntityInstance entity1 = iecs.createEntityInstanceFor(TESTTABLE_TABLENAME);
		entity1.set(TESTTABLE_COLUMN_STAFFID, id);
		entity1.set(TESTTABLE_COLUMN_CHARAC, 'Y');
		entity1.set(TESTTABLE_COLUMN_BYTEA, "HelloWorld1".getBytes());
		entity1.set(TESTTABLE_COLUMN_NAME, "Stephen");
		entity1.set(TESTTABLE_COLUMN_PHONE, "0430106177");
		sourceEntities.add(entity1.clone());
		
		return sourceEntities;
	}
	
	private void cleanUp(){
		String sql = Q.DELETE + Q.FROM + TESTTABLE_TABLENAME.toLowerCase();
		iecs.getDatabaseConnection().executeArbitraryUpdate(sql);
		sql = Q.DELETE + Q.FROM + Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME.toLowerCase());
		iecs.getDatabaseConnection().executeArbitraryUpdate(sql);
		sql = Q.DELETE + Q.FROM + Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME.toLowerCase());
		iecs.getDatabaseConnection().executeArbitraryUpdate(sql);
		sql = Q.DELETE + Q.FROM + Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(TESTTABLE_TABLENAME.toLowerCase());
		iecs.getDatabaseConnection().executeArbitraryUpdate(sql);
	}
}

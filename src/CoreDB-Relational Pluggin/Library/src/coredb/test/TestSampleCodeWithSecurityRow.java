package coredb.test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;
import coredb.config.Configuration.Column_Info_Table;
import coredb.config.Configuration.Mapping_Table;
import coredb.config.Configuration.Row_Security_Deny_Table;
import coredb.config.Configuration.Row_Security_Grant_Table;
import coredb.config.Configuration.Token_Info_Table;
import coredb.config.Configuration.Tracing_Table;
import coredb.config.Configuration.User_Info_Table;
import coredb.controller.EntityControllerSecurity;
import coredb.database.DatabaseConnection;
import coredb.database.SchemaInfo;
import coredb.mode.Mode;
import coredb.mode.RootPermission;
import coredb.security.ResultSecurity;
import coredb.security.ResultTokens;
import coredb.security.TokenFactory;
import coredb.security.TokenFactory.DefaultTokens;
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
import coredb.utils.TestAssist;

public class TestSampleCodeWithSecurityRow {
	EntityControllerSecurity iecs = null;
	
	private static final String TESTTABLE_TABLENAME      = "TEST";
	private static final String TESTTABLE_COLUMN_STAFFID = "STAFFID";
	private static final String TESTTABLE_COLUMN_NAME    = "NAME";
	private static final String TESTTABLE_COLUMN_PHONE   = "PHONE";
	
	private static HashMap<String, Class> testTable_dataType = new HashMap<String, Class>();
	
	private static final String ROOTNAME                 = "ROOT";
	private static final String ROOTPASS                 = "ROOT";

	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		TestSampleCodeWithSecurityRow test = new TestSampleCodeWithSecurityRow();
		test.setup();

		test.deployDefinitionListWithSecurity(null, false, false);
  		test.createEntitiesWithSecurity(null, null);
  		test.readEntitiesWithSecurity(null, null, null);
  		test.updateEntitiesWithSecurity(null, null);
		test.deleteEntitiesWithSecurity(null, null);
		test.addTokensToRows(null, null, null);
		test.removeTokensFromRows(null, null, null);
		
		test.getTokensByUser(null, null);
		test.getUsersByGroup(null, null);
		test.getTokensByGroup(null, null);
	}	
	
	public ResultTokens getTokensByGroup(EntityInstance reader, String group) {
		begin();
		printTag("\n<TESTING ResultTokens getTokensByGroup(EntityInstance reader, String group)>\n");
		EntityInstance root                      = getRootUser();
		List<EntityInstance> rootGrantTokens     = RootPermission.getRootTokens(iecs.getDatabaseConnection());
		List<EntityInstance> defaultGrantTokens  = TokenFactory.getDefaultGrantTokens(iecs.getDatabaseConnection());
		List<EntityInstance> defaultDenyTokens   = TokenFactory.getDefaultDenyTokens(iecs.getDatabaseConnection());
		
		ResultTokens resultTokens        = iecs.getTokensByGroup(root, "ROOT");
		System.out.println("--Root Grant Tokens--");
		Describer.describeEntityInstances(resultTokens.getGrantTokens());
		System.out.println("--Root Deny Tokens--");
		Describer.describeEntityInstances(resultTokens.getDenyTokens());
		Assert.assertTrue(resultTokens.getDenyTokens().isEmpty());
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), rootGrantTokens, resultTokens.getGrantTokens()));
		
        resultTokens        = iecs.getTokensByGroup(root, "DEFAULT");
		System.out.println("--DEFAULT Grant Tokens--");
		Describer.describeEntityInstances(resultTokens.getGrantTokens());
		System.out.println("--DEFAULT Deny Tokens--");
		Describer.describeEntityInstances(resultTokens.getDenyTokens());
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), defaultGrantTokens, resultTokens.getGrantTokens()));
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), defaultDenyTokens, resultTokens.getDenyTokens()));
		
		System.out.println("--CREATE DEFAULT DENY TOKENS--");
		defaultDenyTokens   = TokenFactory.getDefaultDenyTokens(iecs.getDatabaseConnection());
		resultTokens        = iecs.getTokensByGroup(root, "DEFAULT");
		System.out.println("--DEFAULT Grant Tokens--");
		Describer.describeEntityInstances(resultTokens.getGrantTokens());
		System.out.println("--DEFAULT Deny Tokens--");
		Describer.describeEntityInstances(resultTokens.getDenyTokens());
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), defaultGrantTokens, resultTokens.getGrantTokens()));
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), defaultDenyTokens, resultTokens.getDenyTokens()));
		
		printTag("\n</PASSED TESTING ResultTokens getTokensByGroup(EntityInstance reader, String group)>\n");
		end();
		return null;
	}
	
	public List<EntityInstance> getUsersByGroup(EntityInstance reader, String group) {
		begin();
		printTag("\n<TESTING List<EntityInstance> getUsersByGroup(EntityInstance reader, String group)>\n");
		EntityInstance root                      = getRootUser();
		List<EntityInstance> normalUsers         = new LinkedList<EntityInstance>();
		HashMap<String, EntityInstance> userMap  = generateNormalUser(new String[][]{ {"User1","User1", "User1Pass"} });
		normalUsers.add(userMap.get("User1"));
		userMap  = generateNormalUser(new String[][]{ {"User2","User2", "User2Pass"} });
		normalUsers.add(userMap.get("User2"));
		userMap  = generateNormalUser(new String[][]{ {"User3","User3", "User3Pass"} });
		normalUsers.add(userMap.get("User3"));
		
		List<EntityInstance> resultUsers         = iecs.getUsersByGroup(root, "ROOT");
		System.out.println("--Root Users--");
		Describer.describeEntityInstances(resultUsers);
		Assert.assertTrue(resultUsers.size() == 1);
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), root, resultUsers.get(0)));
		
		resultUsers         = iecs.getUsersByGroup(root, "DEFAULT");
		System.out.println("--Normal Users--");
		Describer.describeEntityInstances(resultUsers);
		Assert.assertTrue(resultUsers.size() == 3);
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), normalUsers, resultUsers));
		
		printTag("\n</PASSED TESTING List<EntityInstance> getUsersByGroup(EntityInstance reader, String group)>\n");
		end();
		return null;
	}
	
	public ResultTokens getTokensByUser(EntityInstance reader, EntityInstance user) {
		begin();
		printTag("\n<TESTING ResultTokens getTokensByUser(EntityInstance reader, EntityInstance user)>\n");
		EntityInstance root                      = getRootUser();
		HashMap<String, EntityInstance> userMap  = generateNormalUser(new String[][]{ {"User1","User1", "User1Pass"} });
		EntityInstance user1                     = userMap.get("User1");	
		List<EntityInstance> defaultGrantTokens  = TokenFactory.getDefaultGrantTokens(iecs.getDatabaseConnection());
		List<EntityInstance> defaultDenyTokens   = TokenFactory.getDefaultDenyTokens(iecs.getDatabaseConnection());
		
		ResultTokens resultTokens        = iecs.getTokensByUser(root, user1);
		
		System.out.println("--Grant Tokens--");
		Describer.describeEntityInstances(resultTokens.getGrantTokens());
		System.out.println("--Deny Tokens--");
		Describer.describeEntityInstances(resultTokens.getDenyTokens());
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), defaultGrantTokens, resultTokens.getGrantTokens()));
		
		// add default deny tokens to user1
		System.out.println("--add default deny tokens to user1--");
		defaultDenyTokens   = TokenFactory.getDefaultDenyTokens(iecs.getDatabaseConnection());
		iecs.addTokensToUser(root, user1, defaultDenyTokens);
		resultTokens        = iecs.getTokensByUser(root, user1);
		System.out.println("--Grant Tokens--");
		Describer.describeEntityInstances(resultTokens.getGrantTokens());
		System.out.println("--Deny Tokens--");
		Describer.describeEntityInstances(resultTokens.getDenyTokens());
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), defaultGrantTokens, resultTokens.getGrantTokens()));
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), defaultDenyTokens, resultTokens.getDenyTokens()));
		
		// remove default deny tokens from user1
		System.out.println("--remove default deny tokens from user1--");
		defaultDenyTokens   = TokenFactory.getDefaultDenyTokens(iecs.getDatabaseConnection());
		iecs.removeTokensFromUser(root, user1, defaultDenyTokens);
		resultTokens        = iecs.getTokensByUser(root, user1);
		System.out.println("--Grant Tokens--");
		Describer.describeEntityInstances(resultTokens.getGrantTokens());
		System.out.println("--Deny Tokens--");
		Describer.describeEntityInstances(resultTokens.getDenyTokens());
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), defaultGrantTokens, resultTokens.getGrantTokens()));
		Assert.assertTrue( resultTokens.getDenyTokens().isEmpty());
		
		printTag("\n</PASSED TESTING ResultTokens getTokensByUser(EntityInstance reader, EntityInstance user)>\n");
		end();
		return null;
	}
	
	public boolean deployDefinitionListWithSecurity( List<ClassToTables> classToTablesList, boolean dropTablesFirst, boolean continueOnError){
		printTag("<TESTING deployDefinitionListWithSecurity( List<ClassToTables> classToTablesList, boolean dropTablesFirst, boolean continueOnError)>");
		iecs.initializeMode(true, Mode.SECURITY);
		Describer.describeClassToTables(deployDefinitionListWithRowSecurity(null, false, false));
		printTag("</PASSED TESTING deployDefinitionListWithSecurity( List<ClassToTables> classToTablesList, boolean dropTablesFirst, boolean continueOnError)>\n");
		end();
		return true;
	}
	
	private List<ClassToTables> deployDefinitionListWithRowSecurity( List<ClassToTables> classToTablesList, boolean dropTablesFirst, boolean continueOnError) {
		// Test Data Initialization
		classToTablesList = new LinkedList<ClassToTables>();	
		List<AttributeClass> attributes = new LinkedList<AttributeClass>();	
		AttributeClass primaryAttribute = new AttributeClass(TESTTABLE_COLUMN_STAFFID, testTable_dataType.get(TESTTABLE_COLUMN_STAFFID));
		primaryAttribute.setPrimaryKey(true);
		//primaryAttribute.setAutoIncrement(true);
		attributes.add(primaryAttribute);
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
	
	public ResultSecurity createEntitiesWithSecurity(EntityInstance creator, List<EntityInstance> entities) {
		begin();
		printTag("\n<TESTING createEntitiesWithSecurity(EntityInstance creator, List<EntityInstance> entities)>\n");
		

		HashMap<String, EntityInstance> userMap  = generateNormalUser(new String[][]{ {"User1","User1", "User1Pass"} });
		EntityInstance user1                     = userMap.get("User1");	
		List<EntityInstance> sourceEntities      = generateSourceEntitis();
		DatabaseConnection databaseConnection    = iecs.getDatabaseConnection();
		
		// Create source entities
		printTag("\n[User1 : Before Create Entities]");
		printTableStatement(TESTTABLE_TABLENAME, "");
		printTableStatement(Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME), "");
		printTableStatement(Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), "");
		printTableStatement(Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), "");
		iecs.createEntitiesWithSecurity(user1, sourceEntities);
		printTag("\n[User1 : After Create Entities]");
		printTableStatement(TESTTABLE_TABLENAME, "");
		printTableStatement(Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME), "");
		printTableStatement(Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), "");
		printTableStatement(Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), "");
		
		// Since the coredb will clear the source entities after the creation, we need to regenerate source entities
		sourceEntities                           = generateSourceEntitis();

		
		List<EntityInstance> readableEntities = iecs.getDatabaseConnection().read(TESTTABLE_TABLENAME, "");
		Assert.assertTrue(readableEntities.size() == 2);
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), readableEntities, sourceEntities));
		
		List<EntityInstance> mappingEntities  = iecs.getDatabaseConnection().read(Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME), "");
		Assert.assertTrue(mappingEntities.size() == 2);
		Assert.assertEquals(mappingEntities.get(0).get(TESTTABLE_COLUMN_STAFFID), 1);
		Assert.assertEquals(mappingEntities.get(1).get(TESTTABLE_COLUMN_STAFFID), 2);
		
		String sql                                     = " true " + Q.E + "true" + Q.ORDER_BY +Row_Security_Grant_Table.makeRowSecurityGrantTableColumnNameCoredbId(TESTTABLE_TABLENAME);
		List<EntityInstance> rowSecurityGrantEntities  = iecs.getDatabaseConnection().read(Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), sql );
		Assert.assertTrue(rowSecurityGrantEntities.size() == 16);
		Assert.assertEquals(rowSecurityGrantEntities.get(0).get(Row_Security_Grant_Table.getColumnTokenID()), RootPermission.ROOT$C$G);
		Assert.assertEquals(rowSecurityGrantEntities.get(1).get(Row_Security_Grant_Table.getColumnTokenID()), RootPermission.ROOT$R$G);
		Assert.assertEquals(rowSecurityGrantEntities.get(2).get(Row_Security_Grant_Table.getColumnTokenID()), RootPermission.ROOT$U$G);
		Assert.assertEquals(rowSecurityGrantEntities.get(3).get(Row_Security_Grant_Table.getColumnTokenID()), RootPermission.ROOT$D$G);
		
		Assert.assertEquals(rowSecurityGrantEntities.get(4).get(Row_Security_Grant_Table.getColumnTokenID()), DefaultTokens.getTOKEN_GRANTGROUP$C$G(databaseConnection).get(Token_Info_Table.getColumnTokenID()));
		Assert.assertEquals(rowSecurityGrantEntities.get(5).get(Row_Security_Grant_Table.getColumnTokenID()), DefaultTokens.getTOKEN_GRANTGROUP$R$G(databaseConnection).get(Token_Info_Table.getColumnTokenID()));
		Assert.assertEquals(rowSecurityGrantEntities.get(6).get(Row_Security_Grant_Table.getColumnTokenID()), DefaultTokens.getTOKEN_GRANTGROUP$U$G(databaseConnection).get(Token_Info_Table.getColumnTokenID()));
		Assert.assertEquals(rowSecurityGrantEntities.get(7).get(Row_Security_Grant_Table.getColumnTokenID()), DefaultTokens.getTOKEN_GRANTGROUP$D$G(databaseConnection).get(Token_Info_Table.getColumnTokenID()));
		
		Assert.assertEquals(rowSecurityGrantEntities.get(8).get(Row_Security_Grant_Table.getColumnTokenID()), RootPermission.ROOT$C$G);
		Assert.assertEquals(rowSecurityGrantEntities.get(9).get(Row_Security_Grant_Table.getColumnTokenID()), RootPermission.ROOT$R$G);
		Assert.assertEquals(rowSecurityGrantEntities.get(10).get(Row_Security_Grant_Table.getColumnTokenID()), RootPermission.ROOT$U$G);
		Assert.assertEquals(rowSecurityGrantEntities.get(11).get(Row_Security_Grant_Table.getColumnTokenID()), RootPermission.ROOT$D$G);
		
		Assert.assertEquals(rowSecurityGrantEntities.get(12).get(Row_Security_Grant_Table.getColumnTokenID()), DefaultTokens.getTOKEN_GRANTGROUP$C$G(databaseConnection).get(Token_Info_Table.getColumnTokenID()));
		Assert.assertEquals(rowSecurityGrantEntities.get(13).get(Row_Security_Grant_Table.getColumnTokenID()), DefaultTokens.getTOKEN_GRANTGROUP$R$G(databaseConnection).get(Token_Info_Table.getColumnTokenID()));
		Assert.assertEquals(rowSecurityGrantEntities.get(14).get(Row_Security_Grant_Table.getColumnTokenID()), DefaultTokens.getTOKEN_GRANTGROUP$U$G(databaseConnection).get(Token_Info_Table.getColumnTokenID()));
		Assert.assertEquals(rowSecurityGrantEntities.get(15).get(Row_Security_Grant_Table.getColumnTokenID()), DefaultTokens.getTOKEN_GRANTGROUP$D$G(databaseConnection).get(Token_Info_Table.getColumnTokenID()));
		
		List<EntityInstance> rowSecurityDenyEntities  = iecs.getDatabaseConnection().read(Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), "" );
		Assert.assertTrue(rowSecurityDenyEntities.isEmpty());
		
		printTag("\n<PASSED createEntitiesWithSecurity(EntityInstance creator, List<EntityInstance> entities)>\n");
		end();
		return null;
	}
	
	public ResultSecurity readEntitiesWithSecurity(EntityInstance reader, String tableName, SQLStatement sql) {
		begin();
		printTag("\n<TESTING  readEntitiesWithSecurity(EntityInstance reader, String tableName, SQLStatement sql)>\n");
		EntityInstance root                      = getRootUser();
		HashMap<String, EntityInstance> userMap  = generateNormalUser(new String[][]{ {"User1","User1", "User1Pass"},{"User2","User2", "User2Pass"}  });
		EntityInstance user1                     = userMap.get("User1");	
		EntityInstance user2                     = userMap.get("User2");
		iecs.removeUserFromGroup(root, user2, "DEFAULT");
		List<EntityInstance> sourceEntities      = generateSourceEntitis();
		
		iecs.createEntitiesWithSecurity(user1, sourceEntities);
		sourceEntities      = generateSourceEntitis();
		
		List<EntityInstance> readableEntities1 = iecs.readEntitiesWithSecurity(user1, TESTTABLE_TABLENAME, new CompoundStatementManual()).getPassedEntities();
		printTag("\n[User1: Before Read - Read Permission True]");
		Describer.describeEntityInstances(readableEntities1);		
		
		List<EntityInstance> readableEntities2 = iecs.readEntitiesWithSecurity(user2, TESTTABLE_TABLENAME, new CompoundStatementManual()).getPassedEntities();
		printTag("\n[User2: After Read - Read Permission False]");
		Describer.describeEntityInstances(readableEntities2);	
		
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), readableEntities1, sourceEntities));
		Assert.assertTrue(readableEntities2.isEmpty());
		
		printTag("\n<PASSED  readEntitiesWithSecurity(EntityInstance reader, String tableName, SQLStatement sql)>");
		end();
		return null;
	}
	
	public ResultSecurity updateEntitiesWithSecurity(EntityInstance updator, List<EntityInstance> entities) {
		begin();
		printTag("\n<TESTING updateEntitiesWithSecurity(EntityInstance updator, List<EntityInstance> entities)>\n");
		EntityInstance root                      = getRootUser();
		HashMap<String, EntityInstance> userMap  = generateNormalUser(new String[][]{ {"User1","User1", "User1Pass"},{"User2","User2", "User2Pass"}  });
		EntityInstance user1                     = userMap.get("User1");	
		EntityInstance user2                     = userMap.get("User2");
		iecs.removeUserFromGroup(root, user2, "DEFAULT");
		List<EntityInstance> sourceEntities      = generateSourceEntitis();
		
		iecs.createEntitiesWithSecurity(user1, sourceEntities);
		sourceEntities                           = generateSourceEntitis();
		List<EntityInstance> readableEntities    = iecs.readEntitiesWithSecurity(user1, TESTTABLE_TABLENAME, new CompoundStatementManual()).getPassedEntities();
		
		sourceEntities.get(0).set(TESTTABLE_COLUMN_NAME, "Super" + sourceEntities.get(0).get(TESTTABLE_COLUMN_NAME));
		sourceEntities.get(1).set(TESTTABLE_COLUMN_NAME, "Super" + sourceEntities.get(1).get(TESTTABLE_COLUMN_NAME));
		readableEntities.get(0).set(TESTTABLE_COLUMN_NAME, "Super" + readableEntities.get(0).get(TESTTABLE_COLUMN_NAME));
		readableEntities.get(1).set(TESTTABLE_COLUMN_NAME, "Super" + readableEntities.get(1).get(TESTTABLE_COLUMN_NAME));
		
		printTag("\n[User1: Before Update - Update Permission True]");
		printTableStatement(TESTTABLE_TABLENAME, "");
		iecs.updateEntitiesWithSecurity(user1, readableEntities);
		readableEntities = iecs.readEntitiesWithSecurity(user1, TESTTABLE_TABLENAME, new CompoundStatementManual()).getPassedEntities();
		printTag("\n[User1: After Update - Update Permission True]");
		printTableStatement(TESTTABLE_TABLENAME, "");
		
		Assert.assertTrue(readableEntities.size() == 2);
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), sourceEntities, readableEntities));
		
		sourceEntities.get(0).set(TESTTABLE_COLUMN_NAME, "Gaint" + sourceEntities.get(0).get(TESTTABLE_COLUMN_NAME));
		sourceEntities.get(1).set(TESTTABLE_COLUMN_NAME, "Gaint" + sourceEntities.get(1).get(TESTTABLE_COLUMN_NAME));
		readableEntities.get(0).set(TESTTABLE_COLUMN_NAME, "Gaint" + readableEntities.get(0).get(TESTTABLE_COLUMN_NAME));
		readableEntities.get(1).set(TESTTABLE_COLUMN_NAME, "Gaint" + readableEntities.get(1).get(TESTTABLE_COLUMN_NAME));
		
		printTag("\n[User2: Before Update - Update Permission False]");
		printTableStatement(TESTTABLE_TABLENAME, "");
		iecs.updateEntitiesWithSecurity(user2, readableEntities);
		readableEntities = iecs.readEntitiesWithSecurity(user2, TESTTABLE_TABLENAME, new CompoundStatementManual()).getPassedEntities();
		printTag("\n[User2: After Update - Update Permission False]");
		printTableStatement(TESTTABLE_TABLENAME, "");
		
		Assert.assertTrue(readableEntities.isEmpty());
		
		printTag("\n<PASSED updateEntitiesWithSecurity(EntityInstance updator, List<EntityInstance> entities)>\n");
		end();
		return null;
	}

	public ResultSecurity deleteEntitiesWithSecurity(EntityInstance deletor, List<EntityInstance> entities) {
		begin();
		printTag("\n<TESTING deleteEntitiesWithSecurity(EntityInstance deletor, List<EntityInstance> entities)>\n");
		EntityInstance root                      = getRootUser();
		HashMap<String, EntityInstance> userMap  = generateNormalUser(new String[][]{ {"User1","User1", "User1Pass"},{"User2","User2", "User2Pass"}  });
		EntityInstance user1                     = userMap.get("User1");	
		EntityInstance user2                     = userMap.get("User2");
		iecs.removeUserFromGroup(root, user2, "DEFAULT");
		List<EntityInstance> sourceEntities      = generateSourceEntitis();
		
		iecs.createEntitiesWithSecurity(user1, sourceEntities);
		sourceEntities                           = generateSourceEntitis();
		List<EntityInstance> readableEntities    = iecs.readEntitiesWithSecurity(user1, TESTTABLE_TABLENAME, new CompoundStatementManual()).getPassedEntities();
		
		printTag("\n[User2: Before Delete - Delete Permission False]");
		printTableStatement(TESTTABLE_TABLENAME, "");
		printTableStatement(Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME), "");
		printTableStatement(Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), "");
		printTableStatement(Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), "");
		List<EntityInstance> deleteableEntities  = iecs.deleteEntitiesWithSecurity(user2, readableEntities).getPassedEntities();
		printTag("\n[User2: After Delete - Delete Permission False]");
		printTableStatement(TESTTABLE_TABLENAME, "");
		printTableStatement(Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME), "");
		printTableStatement(Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), "");
		printTableStatement(Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), "");
		
		List<EntityInstance> resultEntities                = iecs.getDatabaseConnection().read(TESTTABLE_TABLENAME, "");
		List<EntityInstance> mappingEntities               = iecs.getDatabaseConnection().read(Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME), "");
		List<EntityInstance> rowGrantSecurityEntities      = iecs.getDatabaseConnection().read(Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), "");
		List<EntityInstance> rowDenySecurityEntities       = iecs.getDatabaseConnection().read(Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), "");
		
		Assert.assertTrue(resultEntities.size() == 2);
		Assert.assertTrue(mappingEntities.size() >0);
		Assert.assertTrue(rowGrantSecurityEntities.size() >0);
		Assert.assertTrue(rowDenySecurityEntities.isEmpty());
		
		printTag("\n[User1: Before Delete - Delete Permission True]");
		printTableStatement(TESTTABLE_TABLENAME, "");
		printTableStatement(Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME), "");
		printTableStatement(Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), "");
		printTableStatement(Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), "");
		readableEntities         = iecs.readEntitiesWithSecurity(user1, TESTTABLE_TABLENAME, new CompoundStatementManual()).getPassedEntities();
		deleteableEntities       = iecs.deleteEntitiesWithSecurity(user1, readableEntities).getPassedEntities();
		printTag("\n[User1: After Delete - Delete Permission True]");
		printTableStatement(TESTTABLE_TABLENAME, "");
		printTableStatement(Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME), "");
		printTableStatement(Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), "");
		printTableStatement(Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), "");
			
		resultEntities           = iecs.getDatabaseConnection().read(TESTTABLE_TABLENAME, "");
		mappingEntities          = iecs.getDatabaseConnection().read(Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME), "");
		rowGrantSecurityEntities = iecs.getDatabaseConnection().read(Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), "");
		rowDenySecurityEntities  = iecs.getDatabaseConnection().read(Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), "");
		
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), deleteableEntities, sourceEntities));
		
		Assert.assertTrue(resultEntities.isEmpty());
		Assert.assertTrue(mappingEntities.isEmpty());
		Assert.assertTrue(rowGrantSecurityEntities.isEmpty());
		Assert.assertTrue(rowDenySecurityEntities.isEmpty());
		
		
		printTag("\n<PASSED  deleteEntitiesWithSecurity(EntityInstance deletor, List<EntityInstance> entities)>\n");
		end();
		return null;
	}

	public ResultSecurity addTokensToRows(EntityInstance creatorId, List<EntityInstance> rows, List<EntityInstance> tokens) {
		begin();
		printTag("\n<TESTING addTokensToRows(EntityInstance creatorId, List<EntityInstance> rows, List<EntityInstance> tokens)>\n");
		EntityInstance root                      = getRootUser();
		HashMap<String, EntityInstance> userMap  = generateNormalUser(new String[][]{ {"User1","User1", "User1Pass"} });
		EntityInstance user1                     = userMap.get("User1");	
		List<EntityInstance> sourceEntities      = generateSourceEntitis();
		iecs.createEntitiesWithSecurity(user1, sourceEntities);
		sourceEntities                           = generateSourceEntitis();
		List<EntityInstance> defaultDenyTokens       = TokenFactory.getDefaultDenyTokens(iecs.getDatabaseConnection());
		
		List<EntityInstance> readableEntities = iecs.readEntitiesWithSecurity(user1, TESTTABLE_TABLENAME, new CompoundStatementManual()).getPassedEntities();
		Describer.describeEntityInstances(readableEntities);
		Assert.assertTrue(sourceEntities.size() == 2);
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), readableEntities, sourceEntities));
		
		printTag("\n[User1: Before Add]");
		printTableStatement(Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), "");
		ResultSecurity resultSecurity = iecs.addTokensToRows(user1, readableEntities, defaultDenyTokens);
		Assert.assertTrue(resultSecurity.getPassedEntities().isEmpty());
	
		printTag("\n[User1: After Add]");
		printTableStatement(Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), "");

		printTag("\n[Root: Before Add]");
		printTableStatement(Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), "");
		iecs.addTokensToRows(root, readableEntities, defaultDenyTokens).getPassedEntities();
		printTag("\n[Root: After Add]");
		printTableStatement(Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), "");
		
		readableEntities = iecs.readEntitiesWithSecurity(user1, TESTTABLE_TABLENAME, new CompoundStatementManual()).getPassedEntities();
		CompoundStatementAuto csa = new CompoundStatementAuto(Q.OR);
		for(EntityInstance readableEntity : readableEntities){
			csa.addCompoundStatement(readableEntity.makeSQLOnIdentifiableAttributes(iecs.getDatabaseConnection()));
		}
		
		HashMap<String, Boolean> rowTokenPairMap = new HashMap<String, Boolean>();
		List<EntityInstance> mappingEntities = iecs.getDatabaseConnection().read(Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME), csa.toSQLStatement());
		Assert.assertTrue(mappingEntities.size() == 2);
		csa.clear();
		for(EntityInstance mappingEntity : mappingEntities){
			String entityInstanceId =  (String)mappingEntity.get(Tracing_Table.makeMappingTableColumnNameCoredbId(TESTTABLE_TABLENAME));
			csa.addCompoundStatement( Row_Security_Deny_Table.getColumnEntityInstanceID() + Q.E + Q.QUOT + entityInstanceId + Q.QUOT);
			rowTokenPairMap.put(entityInstanceId + ":" + DefaultTokens.getTOKEN_DENYGROUP$C$D(iecs.getDatabaseConnection()).get(Token_Info_Table.getColumnTokenID()), true);
			rowTokenPairMap.put(entityInstanceId + ":" + DefaultTokens.getTOKEN_DENYGROUP$R$D(iecs.getDatabaseConnection()).get(Token_Info_Table.getColumnTokenID()), true);
			rowTokenPairMap.put(entityInstanceId + ":" + DefaultTokens.getTOKEN_DENYGROUP$U$D(iecs.getDatabaseConnection()).get(Token_Info_Table.getColumnTokenID()), true);
			rowTokenPairMap.put(entityInstanceId + ":" + DefaultTokens.getTOKEN_DENYGROUP$D$D(iecs.getDatabaseConnection()).get(Token_Info_Table.getColumnTokenID()), true);

		}
		Assert.assertTrue(csa.toSQLStatement().length()>0);
		List<EntityInstance> actualTokenRowPairEntities = iecs.getDatabaseConnection().read(Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), csa.toSQLStatement());
		for(EntityInstance actualTokenRowPairEntity : actualTokenRowPairEntities){
			String key = actualTokenRowPairEntity.get(Row_Security_Deny_Table.getColumnEntityInstanceID()) + ":" + actualTokenRowPairEntity.get(Row_Security_Deny_Table.getColumnTokenID());
			Assert.assertNotNull(rowTokenPairMap.get(key));
		}	
		printTag("\n<PASSED addTokensToRows(EntityInstance creatorId, List<EntityInstance> rows, List<EntityInstance> tokens) >\n");
		end();	
		return null;
	}
	
	

	public ResultSecurity removeTokensFromRows(EntityInstance deletorId, List<EntityInstance> rows, List<EntityInstance> tokens) {
		begin();
		printTag("\n<TESTING removeTokensFromRows(EntityInstance deletorId, List<EntityInstance> rows, List<EntityInstance> tokens)>\n");
		EntityInstance root                      = getRootUser();
		HashMap<String, EntityInstance> userMap  = generateNormalUser(new String[][]{ {"User1","User1", "User1Pass"} });
		EntityInstance user1                     = userMap.get("User1");	
		List<EntityInstance> defaultGrantTokens  = TokenFactory.getDefaultGrantTokens(iecs.getDatabaseConnection());
		List<EntityInstance> sourceEntities      = generateSourceEntitis();
		
		iecs.createEntitiesWithSecurity(user1, sourceEntities);
		sourceEntities                           = generateSourceEntitis();

		List<EntityInstance> readableEntities = iecs.readEntitiesWithSecurity(user1, TESTTABLE_TABLENAME, new CompoundStatementManual()).getPassedEntities();
		Assert.assertTrue(sourceEntities.size() == 2);
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), readableEntities, sourceEntities));
	
		printTag("\n[User1: Before Remove]");
		printTableStatement(Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), "");
		ResultSecurity resultSecurity = iecs.removeTokensFromRows(user1, readableEntities, defaultGrantTokens);
		Assert.assertTrue(resultSecurity.getPassedEntities().isEmpty());
		CompoundStatementManual csm = new CompoundStatementManual();
		csm.addConditionalStatement(Row_Security_Grant_Table.getColumnTokenID() + Q.LIKE + Q.QUOT + "grantgroup%" + Q.QUOT);
		List<EntityInstance> actualTokenPairEntities = iecs.getDatabaseConnection().read(Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), csm.toSQLStatement());
		Assert.assertTrue(actualTokenPairEntities.size() == 8);
		printTag("\n[User1: After Remove]");
		printTableStatement(Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), "");

		printTag("\n[Root: Before Remove]");
		printTableStatement(Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), "");
		iecs.removeTokensFromRows(root, readableEntities, defaultGrantTokens);
		csm.clear();
		csm.addConditionalStatement(Row_Security_Grant_Table.getColumnTokenID() + Q.LIKE + Q.QUOT + "grantgroup%" + Q.QUOT);
		actualTokenPairEntities = iecs.getDatabaseConnection().read(Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), csm.toSQLStatement());
		Assert.assertTrue(actualTokenPairEntities.isEmpty());
		printTag("\n[Root: After Remove]");
		printTableStatement(Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), "");
		
		printTag("\n<PASSED removeTokensFromRows(EntityInstance deletorId, List<EntityInstance> rows, List<EntityInstance> tokens)>\n");
		end();	
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	private void setup() throws SQLException {
		iecs = new EntityControllerSecurity("/tmp/credential.txt");
		testTable_dataType.put(TESTTABLE_COLUMN_STAFFID, Integer.class);
		testTable_dataType.put(TESTTABLE_COLUMN_NAME, String.class);
		testTable_dataType.put(TESTTABLE_COLUMN_PHONE, String.class);
	}
	
	private void begin() {
		iecs.initializeMode(true, Mode.SECURITY);
		deployDefinitionListWithRowSecurity(null,true,true);
	}


	private void end() {
		iecs.dropAllTables(iecs.readUser(ROOTNAME,ROOTPASS));
	}
	
	
	private EntityInstance getRootUser(){
		EntityInstance root   = iecs.readUser(ROOTNAME,ROOTPASS);
		return root;
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
	
	private List<EntityInstance> generateSourceEntitis(){
		List<EntityInstance> sourceEntities = new LinkedList<EntityInstance>();
		
		EntityInstance entity1 = iecs.createEntityInstanceFor(TESTTABLE_TABLENAME);
		entity1.set(TESTTABLE_COLUMN_STAFFID, 1);
		entity1.set(TESTTABLE_COLUMN_NAME, "Stephen");
		entity1.set(TESTTABLE_COLUMN_PHONE, "0430106177");
		sourceEntities.add(entity1.clone());
		
		EntityInstance entity2 = iecs.createEntityInstanceFor(TESTTABLE_TABLENAME);
		entity2.set(TESTTABLE_COLUMN_STAFFID, 2);
		entity2.set(TESTTABLE_COLUMN_NAME, "Sean");
		entity2.set(TESTTABLE_COLUMN_PHONE, "0430106178");
		sourceEntities.add(entity2.clone());
		
		return sourceEntities;
	}
	
	private void printTableStatement(String tableName, String sql){
		System.out.print("\n +++ " + tableName + " +++ \n");
		Describer.describeEntityInstances(iecs.getDatabaseConnection().read(tableName, sql));
	}
	
	private static void printTag(String tag) {
	       System.out.println(tag);
	}

}

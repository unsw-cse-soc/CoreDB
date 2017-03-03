/**
 * 
 */
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
import coredb.config.Configuration.User_Info_Table;
import coredb.controller.EntityControllerSecurity;
import coredb.database.DatabaseConnection;
import coredb.database.SchemaInfo;
import coredb.mode.Mode;
import coredb.security.TokenFactory;
import coredb.sql.CompoundStatementAuto;
import coredb.sql.CompoundStatementManual;
import coredb.sql.ConditionalStatement;
import coredb.sql.Q;
import coredb.unit.AttributeClass;
import coredb.unit.ClassToTables;
import coredb.unit.EntityClass;
import coredb.unit.EntityInstance;
import coredb.unit.EntitySecurityClass;
import coredb.utils.Describer;
import coredb.utils.TestAssist;

/**
 * @author sean
 *
 */
public class TestSampleCodeWithSecurityRowLogic {
    EntityControllerSecurity iecs = null;
	
	private static final String TESTTABLE_TABLENAME      = "TESTTABLE";
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
		TestSampleCodeWithSecurityRowLogic test = new TestSampleCodeWithSecurityRowLogic();
		test.setup();
		test.test1(test);
	}

	public void test1(TestSampleCodeWithSecurityRowLogic test) throws SQLException {
		test.begin();
		printTag("+++++++++++++++++++++++++test 1 start+++++++++++++++++++++++++");
		
		/**
		 * create users
		 */
		printTag("--------------create users---------------");
		EntityInstance root                      = test.getRootUser();
		HashMap<String, EntityInstance> userMap  = test.generateNormalUser(new String[][]{ {"User1","User1", "User1Pass"} });
		EntityInstance user1                     = userMap.get("User1");	
		userMap                                  = test.generateNormalUser(new String[][]{ {"User2","User2", "User2Pass"} });
		EntityInstance user2                     = userMap.get("User2");	
		
		List<EntityInstance> defaultTokens       = test.iecs.getTokensByGroup(root, "DEFAULT").getGrantTokens();
		List<EntityInstance> updateDefaultToken  = new LinkedList<EntityInstance>();
		updateDefaultToken.add(TokenFactory.DefaultTokens.getTOKEN_GRANTGROUP$U$G(test.iecs.getDatabaseConnection()));
		List<EntityInstance> deleteDefaultToken  = new LinkedList<EntityInstance>();
		deleteDefaultToken.add(TokenFactory.DefaultTokens.getTOKEN_GRANTGROUP$D$G(test.iecs.getDatabaseConnection()));
		
		/**
		 *  create Entities : E1 by user1, E2 by root
		 */
		printTag("==============create Entities : E1(Name = Stephen) by user1, E2 by root==============");
		List<EntityInstance> sourceEntities      = test.generateSourceEntitis();
		test.iecs.createEntityWithSecurity(user1, sourceEntities.get(0));
		test.iecs.createEntityWithSecurity(root,  sourceEntities.get(1));
		
		/**
		 *  read Entities : E1 by user1, user2, E2 by root
		 */
		printTag("==============read Entities : E1(Name = Stephen) by user1 and user2, E2 by root==============");
		sourceEntities      = test.generateSourceEntitis();
		AttributeClass ac_id = DatabaseConnection.getAttributeClass(TESTTABLE_TABLENAME, TESTTABLE_COLUMN_STAFFID);
		CompoundStatementAuto csa1 = new CompoundStatementAuto(Q.AND);
		csa1.addCompoundStatement(new ConditionalStatement(ac_id, Q.E, 1));
		CompoundStatementAuto csa2 = new CompoundStatementAuto(Q.AND);
		csa2.addCompoundStatement(new ConditionalStatement(ac_id, Q.E, 2));
		List<EntityInstance> entityInstances1 = test.iecs.readEntitiesWithSecurity(user1, TESTTABLE_TABLENAME, csa1).getPassedEntities();
		Assert.assertTrue(entityInstances1.size() == 1);
		Assert.assertTrue(TestAssist.compare(test.iecs.getDatabaseConnection(), sourceEntities.get(0), entityInstances1.get(0)));
		
	    List<EntityInstance> entityInstances2 = test.iecs.readEntitiesWithSecurity(user2, TESTTABLE_TABLENAME, csa1).getPassedEntities();
		Assert.assertTrue(entityInstances2.size() == 1);
		Assert.assertTrue(TestAssist.compare(test.iecs.getDatabaseConnection(), sourceEntities.get(0), entityInstances2.get(0)));
		Assert.assertTrue(test.iecs.readEntitiesWithSecurity(user1, TESTTABLE_TABLENAME, csa2).getPassedEntities().size() == 1);
		List<EntityInstance> entityInstances3 = test.iecs.readEntitiesWithSecurity(root, TESTTABLE_TABLENAME, csa2).getPassedEntities();
		Assert.assertTrue(entityInstances3.size() == 1);
		Assert.assertTrue(TestAssist.compare(test.iecs.getDatabaseConnection(), sourceEntities.get(1), test.iecs.readEntitiesWithSecurity(root, TESTTABLE_TABLENAME, csa2).getPassedEntities().get(0)));
		printTag("PASSED READING");
		
		/**
		 * remove grant token_default from user1
		 */
		printTag("==============remove grant token_default from user1==============");
		test.iecs.removeTokensFromUser(root, user1, defaultTokens);
		printTag("--------------read E1 by user1---------------");
		entityInstances1 = test.iecs.readEntitiesWithSecurity(user1, TESTTABLE_TABLENAME, csa1).getPassedEntities();
		Describer.describeEntityInstances(entityInstances1);
		Assert.assertTrue(entityInstances1.isEmpty());
		printTag("--------------read E1 by user2---------------");
		entityInstances2 = test.iecs.readEntitiesWithSecurity(user2, TESTTABLE_TABLENAME, csa1).getPassedEntities();
		Describer.describeEntityInstances(entityInstances2);
		Assert.assertTrue(entityInstances2.size() == 1);
		printTag("--------------read E1 by root---------------");
		entityInstances3 = test.iecs.readEntitiesWithSecurity(root, TESTTABLE_TABLENAME, csa1).getPassedEntities();
		Describer.describeEntityInstances(entityInstances3);
		Assert.assertTrue(entityInstances3.size() == 1);
		
		/**
		 * remove grant update token_default from user2
		 */
		printTag("==============remove grant update token_default from user2==============");
		test.iecs.removeTokensFromUser(root, user2, updateDefaultToken);
		
		printTag("--------------read E1 by user1---------------");
		entityInstances1 = test.iecs.readEntitiesWithSecurity(user1, TESTTABLE_TABLENAME, csa1).getPassedEntities();
		Describer.describeEntityInstances(entityInstances1);
		Assert.assertTrue(entityInstances1.isEmpty());
		
		printTag("--------------read E1 by user2---------------");
		entityInstances2 = test.iecs.readEntitiesWithSecurity(user2, TESTTABLE_TABLENAME, csa1).getPassedEntities();
		Describer.describeEntityInstances(entityInstances2);
		Assert.assertTrue(entityInstances2.size() == 1);
		
		printTag("--------------update E1(name: to Kevin) by user2---------------");
		EntityInstance entityInstance = entityInstances2.get(0);
		entityInstance.set(TESTTABLE_COLUMN_NAME, "Kevin");
		entityInstances2 = test.iecs.updateEntityWithSecurity(user2, entityInstance).getPassedEntities();
		Describer.describeEntityInstances(entityInstances2);
		Assert.assertTrue(entityInstances2.isEmpty());
		
		printTag("--------------read E1 by root---------------");
		entityInstances3 = test.iecs.readEntitiesWithSecurity(root, TESTTABLE_TABLENAME, csa1).getPassedEntities();
		Describer.describeEntityInstances(entityInstances3);
		Assert.assertTrue(entityInstances3.size() == 1);
		
		printTag("--------------update E1(name: to Kevin) by root---------------");
		entityInstance = entityInstances3.get(0);
		entityInstance.set(TESTTABLE_COLUMN_NAME, "Kevin");
		entityInstances3 = test.iecs.updateEntityWithSecurity(root, entityInstance).getPassedEntities();
		Describer.describeEntityInstances(entityInstances3);
		Assert.assertTrue(entityInstances3.size() == 1);
		
		/**
		 * add token_default(except delete token) to user1
		 */
		printTag("==============add token_default(except delete token) to user1==============");
		test.iecs.addTokensToUser(root, user1, defaultTokens);
		test.iecs.removeTokensFromUser(root, user1, deleteDefaultToken);
		
		printTag("--------------read E1 by user1---------------");
		entityInstances1 = test.iecs.readEntitiesWithSecurity(user1, TESTTABLE_TABLENAME, csa1).getPassedEntities();
		Describer.describeEntityInstances(entityInstances1);
		Assert.assertTrue(entityInstances1.size() == 1);
		
		printTag("--------------delete E1 by user1---------------");
		entityInstances1 = test.iecs.deleteEntitiesWithSecurity(user1, entityInstances1).getPassedEntities();
		Assert.assertTrue(entityInstances1.isEmpty());
		entityInstances1 = test.iecs.readEntitiesWithSecurity(user1, TESTTABLE_TABLENAME, csa1).getPassedEntities();
		Describer.describeEntityInstances(entityInstances1);
		Assert.assertTrue(entityInstances1.size() == 1);
		
		printTag("--------------read E1 by user2---------------");
		entityInstances2 = test.iecs.readEntitiesWithSecurity(user2, TESTTABLE_TABLENAME, csa1).getPassedEntities();
		Describer.describeEntityInstances(entityInstances2);
		Assert.assertTrue(entityInstances2.size() == 1);
		
		printTag("--------------delete E1 by user2---------------");
		entityInstances2 = test.iecs.deleteEntitiesWithSecurity(user2, entityInstances2).getPassedEntities();
		Assert.assertTrue(entityInstances2.size() == 1);
		entityInstances2 = test.iecs.readEntitiesWithSecurity(user2, TESTTABLE_TABLENAME, csa1).getPassedEntities();
		Describer.describeEntityInstances(entityInstances2);
		Assert.assertTrue(entityInstances2.isEmpty());
		
		
		/**
		 * add token_default back to user1 and user2
		 */
		printTag("==============add token_default(except delete token) to user1==============");
		test.iecs.addTokensToUser(root, user1, defaultTokens);
		test.iecs.addTokensToUser(root, user2, defaultTokens);
		
		printTag("--------------read E2 by user1---------------");
		entityInstances1 = test.iecs.readEntitiesWithSecurity(user1, TESTTABLE_TABLENAME, csa2).getPassedEntities();
		Describer.describeEntityInstances(entityInstances1);
		Assert.assertTrue(entityInstances1.size() == 1);
		
		printTag("--------------update E2(name: to Kevin) by user2---------------");
		entityInstance = entityInstances1.get(0);
		entityInstance.set(TESTTABLE_COLUMN_NAME, "Kevin");
		entityInstances1 = test.iecs.updateEntityWithSecurity(user1, entityInstance).getPassedEntities();
		Describer.describeEntityInstances(entityInstances1);
		Assert.assertTrue(entityInstances1.size() == 1);
		
		printTag("--------------read E2 by user2---------------");
		entityInstances2 = test.iecs.readEntitiesWithSecurity(user2, TESTTABLE_TABLENAME, csa2).getPassedEntities();
		Describer.describeEntityInstances(entityInstances2);
		Assert.assertTrue(entityInstances2.size() == 1);
		
		printTag("--------------update E2(name: to Richard) by user2---------------");
		entityInstance = entityInstances2.get(0);
		entityInstance.set(TESTTABLE_COLUMN_NAME, "Richard");
		entityInstances2 = test.iecs.updateEntityWithSecurity(user2, entityInstance).getPassedEntities();
		Describer.describeEntityInstances(entityInstances2);
		Assert.assertTrue(entityInstances2.size() == 1);
		
		printTag("--------------delete E2 by user1---------------");
		entityInstances1 = test.iecs.deleteEntitiesWithSecurity(user1, entityInstances2).getPassedEntities();
		Assert.assertTrue(entityInstances1.size() == 1);
		entityInstances1 = test.iecs.readEntitiesWithSecurity(user1, TESTTABLE_TABLENAME, csa2).getPassedEntities();
		Describer.describeEntityInstances(entityInstances1);
		Assert.assertTrue(entityInstances1.isEmpty());
		
		printTag("--------------delete E2 by user2---------------");
		entityInstances2 = test.iecs.deleteEntitiesWithSecurity(user2, entityInstances2).getPassedEntities();
		Assert.assertTrue(entityInstances2.isEmpty());
		entityInstances2 = test.iecs.readEntitiesWithSecurity(user2, TESTTABLE_TABLENAME, csa2).getPassedEntities();
		Describer.describeEntityInstances(entityInstances2);
		Assert.assertTrue(entityInstances2.isEmpty());
		
		
		printTag("+++++++++++++++++++++++++test 1 END+++++++++++++++++++++++++");
		test.end();
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

	private void setup() throws SQLException {
		iecs = new EntityControllerSecurity("/tmp/credential.txt");
	}
	
	private void begin() {
		testTable_dataType.put(TESTTABLE_COLUMN_STAFFID, Integer.class);
		testTable_dataType.put(TESTTABLE_COLUMN_NAME, String.class);
		testTable_dataType.put(TESTTABLE_COLUMN_PHONE, String.class);
		iecs.initializeMode(true, Mode.SECURITY);
		deployDefinitionListWithRowSecurity(null,true,true);
	}


	private void end() {
		iecs.dropAllTables(iecs.readUser(ROOTNAME,ROOTPASS));
	}
	
	private static void printTag(String tag) {
	       System.out.println("\n"+tag+"\n");
	}
	
	private List<ClassToTables> deployDefinitionListWithRowSecurity( List<ClassToTables> classToTablesList, boolean dropTablesFirst, boolean continueOnError) {
		// Test Data Initialization
		classToTablesList = new LinkedList<ClassToTables>();	
		List<AttributeClass> attributes = new LinkedList<AttributeClass>();	
		AttributeClass primaryAttribute = new AttributeClass(TESTTABLE_COLUMN_STAFFID, testTable_dataType.get(TESTTABLE_COLUMN_STAFFID));
		primaryAttribute.setPrimaryKey(true);
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
}

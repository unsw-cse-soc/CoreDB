/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package draft;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


import junit.framework.Assert;

import coredb.config.Configuration;
import coredb.config.Configuration.Token_Info_Table;
import coredb.config.Configuration.User_Info_Table;
import coredb.config.Configuration.User_Token_Table;
import coredb.controller.EntityControllerCommon;
import coredb.controller.EntityControllerSecurity;
import coredb.controller.IEntityControllerCommon;
import coredb.controller.IEntityControllerSecurity;
import coredb.database.DatabaseConnection;
import coredb.database.SchemaInfo;
import coredb.mode.Mode;
import coredb.mode.RootPermission;
import coredb.security.TokenFactory;
import coredb.security.TokenFactory.DefaultTokens;
import coredb.sql.CompoundStatementManual;
import coredb.sql.Q;
import coredb.unit.AttributeClass;
import coredb.unit.CRUD;
import coredb.unit.ClassToTables;
import coredb.unit.EntityClass;
import coredb.unit.EntityInstance;
import coredb.unit.EntitySecurityClass;
import coredb.utils.Describer;
import coredb.utils.TestAssist;


/**
 *
 * @author vmc
 */
public class SampleCodeWithSecurityColumnLogic {

	private  IEntityControllerSecurity ec                = null;
	
	private static final Character SAMPLEVALUE  = 'A';
	
	private static final String TESTTABLE_TABLENAME      = "TESTTABLE";
	private static final String TESTTABLE_COLUMN_STAFFID = "STAFFID";
	private static final String TESTTABLE_COLUMN_NAME    = "NAME";
	private static final String TESTTABLE_COLUMN_PHONE   = "PHONE";
	
	private static final String ROOTNAME                 = "ROOT";
	private static final String ROOTPASS                 = "ROOT";
	private static final String USERNAME1                = "USERNAME_1";
	private static final String USERPASS1                = "USERPASS_1";
	private static final String USERNAME2                = "USERNAME_2";
	private static final String USERPASS2                = "USERPASS_2";
	private static final String USERNAME3                = "USERNAME_3";
	private static final String USERPASS3                = "USERPASS_3";
	private static final String USERNAME4                = "USERNAME_4";
	private static final String USERPASS4                = "USERPASS_4";
	
	private static final String DEFAULTGROUP             = Configuration.DEFAULT;
	private static final String DENYGROUP                = "DENY";
	
	private static EntityInstance TOKEN_DENYGROUP$C$D    = null;
	private static EntityInstance TOKEN_DENYGROUP$R$D    = null;
	private static EntityInstance TOKEN_DENYGROUP$U$D    = null;
	private static EntityInstance TOKEN_DENYGROUP$D$D    = null;
	
	@SuppressWarnings("unchecked")
	private static HashMap<String, Class> testTable_dataType = new HashMap<String, Class>();
	
	public static void  main(String[] args) throws Exception{
		SampleCodeWithSecurityColumnLogic test = new SampleCodeWithSecurityColumnLogic();
		test.setUp();
		// Case 1
		test.testColumnSecurity1();
		// Case 2
		test.testColumnSecurity2();
		// Case 3
		test.testColumnSecurity3();
		// Case 4 
		test.testColumnSecurity4();
		// Case 5
		test.testColumnSecurity5();
	
	}

	private void begin(String[][] newUsers, String[] userWhoDeployTable) throws Exception  {
		printTag("\n[Begin: Start]\n");
		
		ec.initializeMode(true, Mode.SECURITY);
		createUsers(newUsers);
		createTokens();
		deployDefinitionListWithSecurity(userWhoDeployTable);
		
		printTag("\n[Begin: End]\n");
	}
	
	private void end() {
		ec.dropAllTables();
	}

	/**
	 *                   root_________________                              testtable_______
	 *     				 /  \     	\         \                             /       \       \
	 *     				/    \       \         \                           /         \       \
	 *     			 user1   user2    user3    user4                  staffid       name     phone
	 *               /       /  \       |        \                     /            /  \     /  \ 
	 *             *$G     *$G  *$D   EMPTY      *$D                  *$G         *$G *$D  *$G *$D           
	 * @throws Exception 
	 */	
	private void testColumnSecurity1() throws Exception {
		printTag("\n\n========================= Column Security Case1: Start =========================\n");
		
		begin(new String[][]{{USERNAME1, USERPASS1}, {USERNAME2, USERPASS2}, {USERNAME3, USERPASS3}, {USERNAME4, USERPASS4}}, new String[]{USERNAME1, USERPASS1});
		
		EntityInstance root  = ec.readUser(ROOTNAME, ROOTPASS);
		EntityInstance user1 = ec.readUser(USERNAME1, USERPASS1);
		EntityInstance user2 = ec.readUser(USERNAME2, USERPASS2);
		EntityInstance user3 = ec.readUser(USERNAME3, USERPASS3);
		EntityInstance user4 = ec.readUser(USERNAME4, USERPASS4);
		
		// The tokens we will apply on columns
		List<EntityInstance> tokens = new LinkedList<EntityInstance>();
		tokens.add(TOKEN_DENYGROUP$C$D);
		tokens.add(TOKEN_DENYGROUP$R$D);
		tokens.add(TOKEN_DENYGROUP$U$D);
		tokens.add(TOKEN_DENYGROUP$D$D);

		printTag("\n'UserName_1' is added into 'DEFAULTGROUP'\n");	
		printTag("\n'UserName_2' is added into 'DEFAULTGROUP' and  'DENYGROUP'\n");
		ec.addUserToGroup(root, user2, DENYGROUP);
		printTag("\n'UserName_3' is not in any group\n");
		ec.removeUserFromGroup(root, user3, DEFAULTGROUP);		
		printTag("\n'UserName_4' is added into 'DENYGROUP'\n");
		ec.removeUserFromGroup(root, user4, DEFAULTGROUP);
		ec.addUserToGroup(root, user4, DENYGROUP);
		
		// Add tokens to columns of 'testtable'
		printTag("\nAll columns are attached by the 'TOKEN_DENYGROUP$*$D' and 'TOKEN_GRANTGROUP$*$G'\n");
		EntityClass tableSchema = ec.getEntityClass(ec.getDatabaseConnection(), TESTTABLE_TABLENAME, false);
		ec.addTokensToColumns(root,  tableSchema.getName(), tableSchema.getAttributeClassList(), tokens);
		
		List<EntityInstance> InstancesSet = new LinkedList<EntityInstance>();
		HashMap<Character, List<EntityInstance>> curdSampleAndResultMap = new HashMap<Character, List<EntityInstance>>();
		
		// -------- User 1 --------
		curdSampleAndResultMap = doCRUD(user1);
		List<EntityInstance> sampleInstances                   = curdSampleAndResultMap.get(SAMPLEVALUE);
		List<EntityInstance> createReusltInstances             = curdSampleAndResultMap.get(CRUD.CREATE);
		List<EntityInstance> readResultInstances               = curdSampleAndResultMap.get(CRUD.READ);
		List<EntityInstance> updateResultInstances             = curdSampleAndResultMap.get(CRUD.UPDATE);
		List<EntityInstance> deleteResultInstances             = curdSampleAndResultMap.get(CRUD.DELETE);
		
		// Create - user 1
		InstancesSet.add(sampleInstances.get(0));
		InstancesSet.add(sampleInstances.get(1));
		Assert.assertTrue(TestAssist.compare(ec.getDatabaseConnection(), createReusltInstances, InstancesSet));
		
		// Read - user 1
		InstancesSet.clear();
		InstancesSet.add(sampleInstances.get(0));
		InstancesSet.add(sampleInstances.get(1));
		InstancesSet.add(sampleInstances.get(2));
		Assert.assertTrue(TestAssist.compare(ec.getDatabaseConnection(), readResultInstances, InstancesSet));
		
		// Update - user 1
		InstancesSet.clear();
		InstancesSet.add(sampleInstances.get(3));
		Assert.assertTrue(TestAssist.compare(ec.getDatabaseConnection(), updateResultInstances, InstancesSet));
		
		// Delete - user 1
		Assert.assertTrue(deleteResultInstances.size() == 0);
		
		
		// -------- User 2 --------
		curdSampleAndResultMap            = doCRUD(user2);
		sampleInstances                   = curdSampleAndResultMap.get(SAMPLEVALUE);
		createReusltInstances             = curdSampleAndResultMap.get(CRUD.CREATE);
		readResultInstances               = curdSampleAndResultMap.get(CRUD.READ);
		updateResultInstances             = curdSampleAndResultMap.get(CRUD.UPDATE);
		deleteResultInstances             = curdSampleAndResultMap.get(CRUD.DELETE);	
		InstancesSet.clear();
		
		// -------- User 3 --------
		curdSampleAndResultMap            = doCRUD(user4);
		sampleInstances                   = curdSampleAndResultMap.get(SAMPLEVALUE);
		createReusltInstances             = curdSampleAndResultMap.get(CRUD.CREATE);
		readResultInstances               = curdSampleAndResultMap.get(CRUD.READ);
		updateResultInstances             = curdSampleAndResultMap.get(CRUD.UPDATE);
		deleteResultInstances             = curdSampleAndResultMap.get(CRUD.DELETE);
		InstancesSet.clear();
		
		// -------- User 4 --------
		curdSampleAndResultMap            = doCRUD(user4);
		sampleInstances                   = curdSampleAndResultMap.get(SAMPLEVALUE);
		createReusltInstances             = curdSampleAndResultMap.get(CRUD.CREATE);
		readResultInstances               = curdSampleAndResultMap.get(CRUD.READ);
		updateResultInstances             = curdSampleAndResultMap.get(CRUD.UPDATE);
		deleteResultInstances             = curdSampleAndResultMap.get(CRUD.DELETE);
		InstancesSet.clear();

		end();
		
		printTag("========================= Column Security Case1 : END =========================\n\n");
	}
	
	
	/**
	 *                  root                       testtable_______
	 *     				  |                        /       \       \
	 *     				  |                       /         \       \
	 *     			    user1                staffid       name     phone
	 *                  /   \                 /             |       /  \  \
	 *                *$G   *$D             *$G           *$G    *$G  R$D  U$D          
	 * @throws Exception 
	 */	
	private void testColumnSecurity2() throws Exception {
		printTag("\n\n========================= Column Security Case2: Start =========================\n");
		
		begin(new String[][]{{USERNAME1, USERPASS1}}, new String[]{USERNAME1, USERPASS1});
		
		EntityInstance root  = ec.readUser(ROOTNAME, ROOTPASS);
		EntityInstance user1  = ec.readUser(USERNAME1, USERPASS1);
				
		List<EntityInstance> tokens1 = new LinkedList<EntityInstance>();
		tokens1.add(TOKEN_DENYGROUP$R$D);
		List<EntityInstance> tokens2 = new LinkedList<EntityInstance>();
		tokens2.add(TOKEN_DENYGROUP$U$D);
		
		printTag("\n'UserName_1' is added into 'DEFAULTGROUP' and  'DENYGROUP'\n");
		ec.addUserToGroup(root, user1, DENYGROUP);
		
		EntityClass tableSchema = ec.getEntityClass(ec.getDatabaseConnection(), TESTTABLE_TABLENAME, false);
		printTag("\nThe column 'phone' is attached by 'TOKEN_DENYGROUP$R$D'\n");
		ec.addTokensToColumns(root, tableSchema.getName(), new AttributeClass[]{tableSchema.getAttributeClassList()[2]}, tokens1);	
		printTag("\nThe column 'staffid' is attached by 'TOKEN_DENYGROUP$U$D'\n");
		ec.addTokensToColumns(root, tableSchema.getName(), new AttributeClass[]{tableSchema.getAttributeClassList()[2]}, tokens2);
		
		List<EntityInstance> InstancesSet = new LinkedList<EntityInstance>();
		// do crud
		HashMap<Character, List<EntityInstance>> curdSampleAndResultMap = doCRUD(user1);
		List<EntityInstance> sampleInstances                   = curdSampleAndResultMap.get(SAMPLEVALUE);
		List<EntityInstance> createReusltInstances             = curdSampleAndResultMap.get(CRUD.CREATE);
		List<EntityInstance> readResultInstances               = curdSampleAndResultMap.get(CRUD.READ);
		List<EntityInstance> updateResultInstances             = curdSampleAndResultMap.get(CRUD.UPDATE);
		List<EntityInstance> deleteResultInstances             = curdSampleAndResultMap.get(CRUD.DELETE);
		
		end();
		
		printTag("========================= Column Security Case2 : END =========================\n\n");
		
	}
	
	/**
	 *                  root                       testtable_______
	 *     				  |                        /       \       \
	 *     				  |                       /         \       \
	 *     			    user1                staffid       name     phone
	 *                  /   \                 /            /  \      /  \ 
	 *                *$G   *$D             *$G          *$G U$D  *$G  D$D            
	 * @throws Exception 
	 */	
	private void testColumnSecurity3() throws Exception {
		printTag("\n\n========================= Column Security Case3: Start =========================\n");
		
		begin(new String[][]{{USERNAME1, USERPASS1}}, new String[]{USERNAME1, USERPASS1});
		
		EntityInstance root  = ec.readUser(ROOTNAME, ROOTPASS);
		EntityInstance user1  = ec.readUser(USERNAME1, USERPASS1);
		
		List<EntityInstance> tokens1 = new LinkedList<EntityInstance>();
		tokens1.add(TOKEN_DENYGROUP$U$D);
		List<EntityInstance> tokens2 = new LinkedList<EntityInstance>();
		tokens2.add(TOKEN_DENYGROUP$D$D);
		
		printTag("\n'UserName_1' is added into 'DEFAULTGROUP' and  'DENYGROUP'\n");
		ec.addUserToGroup(root, user1, DENYGROUP);
		
		EntityClass tableSchema = ec.getEntityClass(ec.getDatabaseConnection(), TESTTABLE_TABLENAME, false);
		printTag("\nThe column 'name' is attached by 'TOKEN_DENYGROUP$U$D'\n");
		ec.addTokensToColumns(root, tableSchema.getName(), new AttributeClass[]{tableSchema.getAttributeClassList()[1]}, tokens1);
		printTag("\nThe column 'phone' is attached by 'TOKEN_DENYGROUP$D$D'\n");
		ec.addTokensToColumns(root, tableSchema.getName(), new AttributeClass[]{tableSchema.getAttributeClassList()[2]}, tokens2);

		List<EntityInstance> InstancesSet = new LinkedList<EntityInstance>();
		// do CRUD
		HashMap<Character, List<EntityInstance>> curdSampleAndResultMap = doCRUD(user1);
		List<EntityInstance> sampleInstances                   = curdSampleAndResultMap.get(SAMPLEVALUE);
		List<EntityInstance> createReusltInstances             = curdSampleAndResultMap.get(CRUD.CREATE);
		List<EntityInstance> readResultInstances               = curdSampleAndResultMap.get(CRUD.READ);
		List<EntityInstance> updateResultInstances             = curdSampleAndResultMap.get(CRUD.UPDATE);
		List<EntityInstance> deleteResultInstances             = curdSampleAndResultMap.get(CRUD.DELETE);
		
		end();
		
		printTag("========================= Column Security Case3 : END =========================\n\n");
		
	}
	
	
	/**
	 *                  root                       testtable_______
	 *     				  |                        /       \       \
	 *     				  |                       /         \       \
	 *     			    user1                staffid       name     phone
	 *                  /   \                 /            /        /  
	 *                *$G   *$D             *$G          *$D      *$G              
	 * @throws Exception 
	 */	
	private void testColumnSecurity4() throws Exception {
		printTag("\n\n========================= Column Security Case4: Start =========================\n");
		
		begin(new String[][]{{USERNAME1, USERPASS1}}, new String[]{USERNAME1, USERPASS1});
		
		EntityInstance root  = ec.readUser(ROOTNAME, ROOTPASS);
		EntityInstance user1  = ec.readUser(USERNAME1, USERPASS1);
		
		List<EntityInstance> tokens1 = TokenFactory.getDefaultDenyTokens(ec.getDatabaseConnection());
	
		printTag("\n'UserName_1' is added into 'DEFAULTGROUP' and  'DENYGROUP'\n");
		ec.addUserToGroup(root, user1, DENYGROUP);

		EntityClass tableSchema = ec.getEntityClass(ec.getDatabaseConnection(), TESTTABLE_TABLENAME, false);
		printTag("\nThe column 'name' is attached by 'TOKEN_DENYGROUP$U$D'\n");
		ec.addTokensToColumns(root, tableSchema.getName(), new AttributeClass[]{tableSchema.getAttributeClassList()[1]}, tokens1);


		List<EntityInstance> InstancesSet = new LinkedList<EntityInstance>();
		// do CRUD
		HashMap<Character, List<EntityInstance>> curdSampleAndResultMap = doCRUD(user1);
		List<EntityInstance> sampleInstances                   = curdSampleAndResultMap.get(SAMPLEVALUE);
		List<EntityInstance> createReusltInstances             = curdSampleAndResultMap.get(CRUD.CREATE);
		List<EntityInstance> readResultInstances               = curdSampleAndResultMap.get(CRUD.READ);
		List<EntityInstance> updateResultInstances             = curdSampleAndResultMap.get(CRUD.UPDATE);
		List<EntityInstance> deleteResultInstances             = curdSampleAndResultMap.get(CRUD.DELETE);
		
		end();
		
		printTag("========================= Column Security Case4 : END =========================\n\n");
		
	}
	
	/**
	 *                  root                         User_Info__________________
	 *     				  |                        /       \         \			\
	 *     				  |                       /         \         \			 \
	 *     			    user1                coredbid       UserId    UserName   UserPass
	 *                  /   \                  /            /          /            /
	 *                *$G  Root$*$G           Root$*$G      Root$*$D     Root*$G     Root*$G      
	 * @throws Exception 
	 */	
	private void testColumnSecurity5() throws Exception {
		printTag("\n\n========================= Column Security Case5: Start =========================\n");
		
		begin(new String[][]{{USERNAME1, USERPASS1}, {USERNAME2, USERPASS2}}, new String[]{USERNAME1, USERPASS1});
		
		EntityInstance root  = ec.readUser(ROOTNAME, ROOTPASS);
		EntityInstance user2  = ec.readUser(USERNAME2, USERPASS2);
		EntityInstance user5  = ec.createEntityInstanceFor(User_Info_Table.makeUserInfoTableName());		
		user5.set(User_Info_Table.getColumnUserID(), "Kala");
		user5.set(User_Info_Table.getColumnUserName(), "Kala");
		user5.set(User_Info_Table.getColumnPassword(), "KalaPass");	
		
		List<EntityInstance> tokens = ec.getDatabaseConnection().read(Token_Info_Table.makeTokenInfoTableName(), "GROUPTYPE = 'ROOT'");
		EntityClass entityClass = ec.getEntityClass(ec.getDatabaseConnection(), TESTTABLE_TABLENAME, false);
		
		printTag("\n[ Before give root permission to user2]\n");
		ec.createUser(user2, user5);	
		Describer.describeEntityInstances(ec.getDatabaseConnection().read(User_Info_Table.makeUserInfoTableName(), user5.makeSQLOnColumn(ec.getDatabaseConnection(), User_Info_Table.getColumnUserName(), Q.E)));
     	Assert.assertNull(ec.readUser(user5));
		Assert.assertTrue(ec.addTokensToColumns(user2, entityClass.getName(), entityClass.getAttributeClassList(), tokens).getPassedEntities().isEmpty());	
		ec.removeUserFromGroup(root, user2, Configuration.DEFAULT);
		
		
		// -------- User 2 --------
		List<EntityInstance> InstancesSet = new LinkedList<EntityInstance>();
		// do CRUD
		HashMap<Character, List<EntityInstance>>  curdSampleAndResultMap = doCRUD(user2);
		List<EntityInstance> sampleInstances                   = curdSampleAndResultMap.get(SAMPLEVALUE);
		List<EntityInstance> createReusltInstances             = curdSampleAndResultMap.get(CRUD.CREATE);
		List<EntityInstance> readResultInstances               = curdSampleAndResultMap.get(CRUD.READ);
		List<EntityInstance> updateResultInstances             = curdSampleAndResultMap.get(CRUD.UPDATE);
		List<EntityInstance> deleteResultInstances             = curdSampleAndResultMap.get(CRUD.DELETE);
		
		
		printTag("\n[ After give root permission to user2]\n");
		ec.addUserToGroup(root, user2, RootPermission.GROUPNAME);
		ec.createUser(user2, user5);
		Describer.describeEntityInstances(ec.getDatabaseConnection().read(User_Info_Table.makeUserInfoTableName(), user5.makeSQLOnColumn(ec.getDatabaseConnection(), User_Info_Table.getColumnUserName(), Q.E)));
		Assert.assertNotNull(ec.readUser(user5));
		
		// -------- User 2 --------
		InstancesSet.clear();
		curdSampleAndResultMap = doCRUD(user2);
		sampleInstances                   = curdSampleAndResultMap.get(SAMPLEVALUE);
		createReusltInstances              = curdSampleAndResultMap.get(CRUD.CREATE);
		readResultInstances               = curdSampleAndResultMap.get(CRUD.READ);
		updateResultInstances             = curdSampleAndResultMap.get(CRUD.UPDATE);
		deleteResultInstances             = curdSampleAndResultMap.get(CRUD.DELETE);
		
		end();
		
		printTag("========================= Column Security Case5 : END =========================\n\n");
		
	}

	private void setUp() throws Exception {
		// Initialization
		ec = new EntityControllerSecurity("/tmp/credential.txt");
		ec.dropAllTables();
		testTable_dataType.put(TESTTABLE_COLUMN_STAFFID, Integer.class);
		testTable_dataType.put(TESTTABLE_COLUMN_NAME, String.class);
		testTable_dataType.put(TESTTABLE_COLUMN_PHONE, String.class);
		
	}
	
	private void cleanTestTable() {
		CompoundStatementManual csm = new CompoundStatementManual();
		csm.addConditionalStatement(Q.DELETE + Q.FROM + SchemaInfo.refactorTableName(TESTTABLE_TABLENAME));
		ec.getDatabaseConnection().executeArbitraryUpdate(csm.toSQLStatement());
	}
	
	private HashMap<Character, List<EntityInstance>> doCRUD(EntityInstance user) {
		printTag("\n\n>>>" + user.get(User_Info_Table.getColumnUserName()) + "<<<");
		HashMap<Character, List<EntityInstance>> curdSampleAndResultMap = new HashMap<Character, List<EntityInstance>>();
		cleanTestTable();
		
		// #COREDB-57
		EntityInstance root = ec.readUser(ROOTNAME,ROOTPASS);
		EntityInstance sampleInstance0 = ec.createEntityInstanceFor(TESTTABLE_TABLENAME);
		sampleInstance0.set(TESTTABLE_COLUMN_NAME, "Fred");
		sampleInstance0.set(TESTTABLE_COLUMN_PHONE, "0430106186");
		ec.createEntityWithSecurity(root, sampleInstance0);	
		// #/COREDB-57
		
		List<EntityInstance> sampleInstances  = new LinkedList<EntityInstance>();
		EntityInstance sampleInstance1 = ec.createEntityInstanceFor(TESTTABLE_TABLENAME);
		sampleInstance1.set(TESTTABLE_COLUMN_NAME, "Jackson");
		sampleInstance1.set(TESTTABLE_COLUMN_PHONE, "0430106187");
		
		EntityInstance sampleInstance2 = ec.createEntityInstanceFor(TESTTABLE_TABLENAME);
		sampleInstance2.set(TESTTABLE_COLUMN_NAME, "Kate");
		sampleInstance2.set(TESTTABLE_COLUMN_PHONE, "0430106188");
		
		
		sampleInstances.add(sampleInstance1);
		sampleInstances.add(sampleInstance2);		
		
		curdSampleAndResultMap.put(SAMPLEVALUE, sampleInstances);
		
		// CRUD Create
		printTag("\n+++ Creat +++\n");
		printTag("\n[Before Create]\n");
		Describer.describeTable((EntityControllerCommon)ec, TESTTABLE_TABLENAME, "");
		List<EntityInstance> resultInstances = ec.createEntitiesWithSecurity(user, sampleInstances).getPassedEntities();

		sampleInstances.add(sampleInstance1);
		sampleInstances.add(sampleInstance2);
		
		curdSampleAndResultMap.put(CRUD.CREATE, returnRealResult(sampleInstances));
		printTag("\n[After Create]\n");
		Describer.describeTable((EntityControllerCommon)ec, TESTTABLE_TABLENAME, "");
		
		// CRUD Read
		printTag("\n+++ Read +++\n");
		resultInstances = ec.readEntitiesWithSecurity(user, TESTTABLE_TABLENAME, new CompoundStatementManual()).getPassedEntities();
		curdSampleAndResultMap.put(CRUD.READ, returnRealResult(resultInstances));
		printTag("\n[Result]\n");
		Describer.describeEntityInstances(resultInstances);
		
		// CRUD Update
		printTag("\n+++ Update +++\n");
		printTag("\n[Before Update]\n");
		Describer.describeTable((IEntityControllerCommon)ec, TESTTABLE_TABLENAME, "");
		printTag("\nThe data related with 'Fred' will be updated as 'SuperFred'\n");
		sampleInstances.add(sampleInstance0.clone());
		sampleInstance0 = returnRealResult(sampleInstance0).get(0);
		sampleInstance0.set(TESTTABLE_COLUMN_NAME, "Super" + sampleInstance0.get(TESTTABLE_COLUMN_NAME));		
		resultInstances = ec.updateEntityWithSecurity(user, sampleInstance0).getPassedEntities();
		curdSampleAndResultMap.put(CRUD.UPDATE, returnRealResult(sampleInstance0));
		printTag("\n[After Update]\n");
		sampleInstances.add(sampleInstance0.clone());
		if(resultInstances.isEmpty()) sampleInstance0.set(TESTTABLE_COLUMN_NAME, "Fred");
		//Describer.describeTable((EntityControllerBase)ec, TESTTABLE_TABLENAME, "");
		Describer.describeTable(ec.getDatabaseConnection(), TESTTABLE_TABLENAME, "");

		
		// CRUD Delete
		printTag("\n+++ Delete +++\n");
		printTag("\n[Before Delete]\n");
		Describer.describeTable((IEntityControllerCommon)ec, TESTTABLE_TABLENAME, "");
		ec.deleteEntityWithSecurity(user, sampleInstance0).getPassedEntities();
		resultInstances.clear();
		resultInstances.add(sampleInstance0);
		curdSampleAndResultMap.put(CRUD.DELETE, returnRealResult(sampleInstance0));
		printTag("\n[After Delete]\n");
		Describer.describeTable((IEntityControllerCommon)ec, TESTTABLE_TABLENAME, "");
		
		return curdSampleAndResultMap;
	}
	
	private List<EntityInstance> returnRealResult(EntityInstance entity){
		List<EntityInstance> resultEntitis = new LinkedList<EntityInstance>();
		resultEntitis.add(entity);
		return returnRealResult(resultEntitis);
	}
	
	private List<EntityInstance> returnRealResult(List<EntityInstance> entities) {
		List<EntityInstance> resultEntitis = new LinkedList<EntityInstance>();
		for(EntityInstance entity : entities){
			String sql = entity.makeSQLOnColumn(ec.getDatabaseConnection(), TESTTABLE_COLUMN_NAME, Q.E);
			sql       += Q.AND;
			sql       += entity.makeSQLOnColumn(ec.getDatabaseConnection(), TESTTABLE_COLUMN_PHONE, Q.E);
			resultEntitis.addAll(ec.getDatabaseConnection().read(entity.getDynaClass().getName(), sql));
		}
		return resultEntitis;
	}
	
	private void deployDefinitionListWithSecurity(String[] userinfo) {
		printTag("\n+++ Deploy table by user " + userinfo[0] + " +++\n");
		
		EntityInstance user = ec.readUser(userinfo[0], userinfo[1]);	
		List<ClassToTables> classToTablesList = new LinkedList<ClassToTables>();	
		List<AttributeClass> attributes = new LinkedList<AttributeClass>();	
		AttributeClass primaryAttribute = new AttributeClass(TESTTABLE_COLUMN_STAFFID, testTable_dataType.get(TESTTABLE_COLUMN_STAFFID));
		primaryAttribute.setPrimaryKey(true);
		primaryAttribute.setAutoIncrement(true);
		attributes.add(primaryAttribute);
		attributes.add(new AttributeClass(TESTTABLE_COLUMN_NAME, testTable_dataType.get(TESTTABLE_COLUMN_NAME)));
		attributes.add(new AttributeClass(TESTTABLE_COLUMN_PHONE, testTable_dataType.get(TESTTABLE_COLUMN_PHONE)));		
		EntitySecurityClass entitySecurityClass = new EntitySecurityClass(TESTTABLE_TABLENAME, attributes.toArray(new AttributeClass[0]), null);
		classToTablesList.add(entitySecurityClass);

		List<EntityInstance> userTokens = readTokensFromUser(user);
		ec.deployDefinitionListWithSecurity(userTokens, classToTablesList, false, false);
		Describer.describeTableSchema((EntityControllerCommon)ec, TESTTABLE_TABLENAME);
	
	}
	
	private List<EntityInstance> readTokensFromUser(EntityInstance user){
		CompoundStatementManual csm = new CompoundStatementManual();
		csm.addConditionalStatement(User_Token_Table.getColumnUserID() + Q.E + Q.QUOT + user.get(User_Info_Table.getColumnUserID()) + Q.QUOT);
		List<EntityInstance> userTokenPairs = ec.getDatabaseConnection().read(User_Token_Table.makeUserTokenTableName(), csm.toSQLStatement());
		
		csm = new CompoundStatementManual();
		for(EntityInstance userTokenPair : userTokenPairs){
			if(csm.getCompoundStatements().size()>0) {
				csm.addConditionalStatement(Q.OR);
			}else {
				csm.addConditionalStatement(Q.LEFT_PARENTHESIS);
			}
			csm.addConditionalStatement(Token_Info_Table.getColumnTokenID() + Q.E + Q.QUOT + userTokenPair.get(User_Token_Table.getColumnTokenID()) + Q.QUOT);

		}
		List<EntityInstance> userTokens = null;
		if(csm.getCompoundStatements().size()>0){
			csm.addConditionalStatement(Q.RIGHT_PARENTHESIS + Q.AND + Token_Info_Table.getColumnPermission() + Q.E +"true");
			userTokens = ec.getDatabaseConnection().read(Token_Info_Table.makeTokenInfoTableName(), csm.toSQLStatement());
		}else{
			userTokens = new LinkedList<EntityInstance>();
		}
		return userTokens;
	}
	
	private void createUsers(String[][] newUsers) {
		
		EntityInstance creator = ec.readUser(ROOTNAME, ROOTPASS);	
		for(String[] userinfo : newUsers){
			printTag("\n+++ Creat user " + userinfo[0] + " +++\n");
			EntityInstance user = ec.createEntityInstanceFor(User_Info_Table.makeUserInfoTableName());
			user.set(User_Info_Table.getColumnUserID(), userinfo[0]);
			user.set(User_Info_Table.getColumnUserName(), userinfo[0]);
			user.set(User_Info_Table.getColumnPassword(), userinfo[1]);
			Describer.describeEntityInstance(ec.createUser(creator, user));
		}
		
	}
	
	private void createTokens() {
		printTag("\n+++ Create deny tokens +++\n");
		
		EntityInstance root             = ec.readUser(ROOTNAME, ROOTPASS);
		List<EntityInstance> denyTokens = new LinkedList<EntityInstance>();
		
		TOKEN_DENYGROUP$C$D  = DefaultTokens.getTOKEN_DENYGROUP$C$D(ec.getDatabaseConnection());
		TOKEN_DENYGROUP$R$D  = DefaultTokens.getTOKEN_DENYGROUP$R$D(ec.getDatabaseConnection());
		TOKEN_DENYGROUP$U$D  = DefaultTokens.getTOKEN_DENYGROUP$U$D(ec.getDatabaseConnection());
		TOKEN_DENYGROUP$D$D  = DefaultTokens.getTOKEN_DENYGROUP$D$D(ec.getDatabaseConnection());
		
		TOKEN_DENYGROUP$C$D.set(Token_Info_Table.getColumnGroup(), DENYGROUP);
		TOKEN_DENYGROUP$R$D.set(Token_Info_Table.getColumnGroup(), DENYGROUP);
		TOKEN_DENYGROUP$U$D.set(Token_Info_Table.getColumnGroup(), DENYGROUP);
		TOKEN_DENYGROUP$D$D.set(Token_Info_Table.getColumnGroup(), DENYGROUP);
		
		denyTokens.add(TOKEN_DENYGROUP$C$D);
		denyTokens.add(TOKEN_DENYGROUP$R$D);
		denyTokens.add(TOKEN_DENYGROUP$U$D);
		denyTokens.add(TOKEN_DENYGROUP$D$D);
		
		denyTokens = ec.createEntitiesWithSecurity(root, denyTokens).getPassedEntities();
		Describer.describeEntityInstances(denyTokens);

	}
	
	private static void printTag(String tag) {
	       System.out.print(tag);
	       
	}
}

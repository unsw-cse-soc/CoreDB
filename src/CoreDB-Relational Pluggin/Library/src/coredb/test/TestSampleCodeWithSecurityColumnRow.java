/**
 * 
 */
package coredb.test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


import junit.framework.Assert;
import coredb.config.Configuration;
import coredb.config.Configuration.Column_Info_Table;
import coredb.config.Configuration.Column_Security_Deny_Table;
import coredb.config.Configuration.Column_Security_Grant_Table;
import coredb.config.Configuration.Mapping_Table;
import coredb.config.Configuration.Row_Security_Deny_Table;
import coredb.config.Configuration.Row_Security_Grant_Table;
import coredb.config.Configuration.Token_Info_Table;
import coredb.config.Configuration.Tracing_Table;
import coredb.config.Configuration.User_Info_Table;
import coredb.config.Configuration.User_Token_Table;
import coredb.controller.EntityControllerSecurity;
import coredb.database.SchemaInfo;
import coredb.mode.Mode;
import coredb.mode.RootPermission;
import coredb.security.ResultSecurity;
import coredb.security.TokenFactory;
import coredb.common.UnreadableValue;
import coredb.sql.CompoundStatementManual;
import coredb.sql.Q;
import coredb.sql.SQLStatement;
import coredb.unit.AttributeClass;
import coredb.unit.CRUD;
import coredb.unit.ClassToTables;
import coredb.unit.EntityClass;
import coredb.unit.EntityInstance;
import coredb.unit.EntitySecurityClass;
import coredb.utils.Describer;
import coredb.utils.Helper;
import coredb.utils.TestAssist;


public class TestSampleCodeWithSecurityColumnRow {
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

	
	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		
		TestSampleCodeWithSecurityColumnRow test = new TestSampleCodeWithSecurityColumnRow();
		test.setup();
		printTag("\n<2 users : [Root FULL PERMISSION]  [user1 DEFAULT PERMISSION] >\n");
		printTag("\n<2 Original data rows>\n");

		test.createEntitiesWithSecurity(null, null);
		test.readEntitiesWithSecurity(null, null, null);
	  	test.updateEntitiesWithSecurity(null, null);
	  	test.deleteEntitiesWithSecurity(null, null);
	  	
	  	iecs.dropAllTables(iecs.readUser(ROOTNAME,ROOTPASS));
	}
	
	public void createEntitiesWithSecurity(EntityInstance creator, List<EntityInstance> entities) {
		printTag("\n<TESTING  ResultSecurity createEntitiesWithSecurity(EntityInstance creator, List<EntityInstance> entities)>\n");
		this.testCreateActionWhenUserHasDefaultGrantCRUD();
		this.testCreateActionWhenUserHasNoDefaultGrantCRUD();
		
		this.testCreateActionWhenUserHasDefaultGrantCRUD_AllColumnsHaveDefaultDenyCRUD();
		this.testCreateActionWhenUserHasDefaultDenyCRUD_AllColumnsHaveDefaultDenyCRUD();
		this.testCreateActionWhenRootHasDefaultDenyCRUD_AllColumnsHaveDefaultDenyCRUD();
		
		this.testCreateActionWhenUserHasDefaultGrantCRUD_AllColumnsHaveNoDefaultGrantCRUD();
	}
    
    public ResultSecurity readEntitiesWithSecurity(EntityInstance reader, String tableName, SQLStatement sql) {			
		printTag("\n<TESTING  readEntitiesWithSecurity(EntityInstance reader, String tableName, SQLStatement sql)>\n");
		
  		this.testReadActionWhenUserHasDefaultGrantCRUD();
  		this.testReadActionWhenUserHasNoDefaultGrantCRUD();
		
  		this.testReadActionWhenUserHasDefaultGrantCRUD_AllColumnsHaveDefaultDenyCRUD();
  		this.testReadActionWhenUserHasDefaultGrantCRUD_LastColumnHasNoDefaultGrantCRUD();
  		this.testReadActionWhenUserHasDefaultDenyCRUD_LastColumnHasDefaultDenyCRUD();
		
  		this.testReadActionWhenUserHasDefaultGrantCRUD_FirstRowHasDefaultDenyCRUD();
  		this.testReadActionWhenUserHasDefalutGrantCRUD_FirstRowHasNoDefaultGrantCRUD();
  		this.testReadActionWhenUserHasDefaultDenyCRUD_FirstRowHasDefaultDenyCRUD();
		
  		this.testReadActionWhenUserHasDefalutGrantCRUD_LastColumnHasNoDefaultGrantCRUD_FirstRowHasNoDefaultGrantCRUD();
		this.testReadActionWhenUserHasDefaultDenyCRUD_LastColumnHasDefaultDenyCRUD_FirstRowHasDefaultDenyCRUD();
		
		return null;
		
	}  
    
	public ResultSecurity updateEntitiesWithSecurity(EntityInstance updator, List<EntityInstance> entities) {
		printTag("\n<TESTING  ResultSecurity updateEntitiesWithSecurityAddDenyTokens(EntityInstance updator, List<EntityInstance> entities)>\n");
		this.testUpdateActionWhenUserHasDefaultGrantCRUD();
		
		this.testUpdateActionWhenUserHasDefalutGrantCRUD_LastColumnHasNoDefaultGrantCRUD_LastColumnDataWillNotBeUpdated();
		this.testUpdateActionWhenUserHasDefalutGrantCRUD_LastColumnHasNoDefaultGrantCRUD_LastColumnDataWillBeUpdate();
		this.testUpdateActionWhenUserHasGeneralDenyR_LastColumnHasGeneralDenyR_LastColumnDataWillBeUpdated();
		this.testUpdateActionWhenUserHasGeneralDenyRU_LastColumnHasGeneralDenyRU_LastColumnDataWillBeUpdated();

		this.testUpdateActionWhenUserHasDefalutGrantCRUD_FirstRowHasDefaultGrantCRD();
		this.testUpdateActionWhenUserHasGeneralDenyU_FirstRowHasGeneralDenyU();

		this.testUpdateActionWhenUserHasDefalutGrantCRUD_LastColumnHasNoDefaultGrantCRUD_FirstRowHasDefaultGrantCRD_LastColumnDataWillNotBeUpdated();
		this.testUpdateActionWhenUserHasDefalutGrantCRUD_LastColumnHasNoDefaultGrantCRUD_FirstRowHasDefaultGrantCRD_LastColumnDataWillBeUpdated();
		this.testUpdateActionWhenUserHasGeneralDenyRU_LastColumnHasGeneralDenyR_FirstRowHasGeneralDenyU_LastColumnDataWillBeUpdated();
		
		return null;
		
	}
	
	public ResultSecurity deleteEntitiesWithSecurity(EntityInstance deletor, List<EntityInstance> entities) {
		printTag("\n<TESTING  ResultSecurity deleteEntitiesWithSecurity(EntityInstance deletor, List<EntityInstance> entities)>\n");
	    this.testDeleteActionWhenUserHasDefalutGrantCRUD();
		
	    this.testDeleteActionWhenUserHasDefalutGrantCRUD_LastColumnHasNoDefaultGrantCRUD();		
		this.testDeleteActionWhenUserHasGeneralDenyR_LastColumnHasGeneralDenyR();

		
		this.testDeleteActionWhenUserHasDefalutGrantCRUD_FirstRowHasDefaultGrantCRU();		
		this.testDeleteActionWhenUserHasGeneralDenyD_FirstRowHasGeneralDenyD();
		
		this.testDeleteActionWhenUserHasDefalutGrantCRUD_LastColumnHasNoDefalutGrantCRUD_FirstRowHasDefaultGrantCRU();
		this.testDeleteActionWhenUserHasGeneralDenyRD_LastColumnHasGeneralDenyR_FirstRowHasGeneralDenyD();
	
		return null;
	}	
	
	/** 
	 * 
	 * <br />
	 *  User : DEFAULT$*$+ <br/>
     *  <img src="../../resources/image/create/testUserHasFullPermissionOnCreateAction.png" /> 
     *  
	 */
	public void testCreateActionWhenUserHasDefaultGrantCRUD(){
		//begin();
			    		    	
    	printTag("\n[--------------Test Create Action When User Has Default Grant CRUD--------------]");		
		List<EntityInstance> sourceEntities = generateSourceEntities();
		

    	// Before create action
    	printTag("\n[Before Create]");
		getEntitiesFromDatabase();
		getRowDenySecurityTableContentFromDatabase();
		getRowGrantSecurityTableContentFromDatabase();
		getMappingTableContentFromDatabase();
	   
		// After create action
		printTag("\n[After Create]");
		double tStart = System.currentTimeMillis();
    	ResultSecurity resultSecurity                         = iecs.createEntitiesWithSecurity(user1, sourceEntities);
    	System.err.println("(3)COREDB create node takes " + (System.currentTimeMillis() - tStart) + " ms");
    	sourceEntities                                        = generateSourceEntities();
    	List<EntityInstance> passedEntities                   = getEntitiesFromResultSecurity(resultSecurity, true);
    	List<EntityInstance> failedEntities                   = getEntitiesFromResultSecurity(resultSecurity, false);
		List<EntityInstance> testTableEntitiesAFC             = getEntitiesFromDatabase();
		List<EntityInstance> rowDenySecurityTableEntitiesAFC  = getRowDenySecurityTableContentFromDatabase();
		List<EntityInstance> rowGrantSecurityTableEntitiesAFC = getRowGrantSecurityTableContentFromDatabase();
		List<EntityInstance> mappingTableEntitiesAFC          = getMappingTableContentFromDatabase();
		
		// Test assertion
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), passedEntities, sourceEntities));
		Assert.assertTrue(failedEntities.isEmpty());
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), testTableEntitiesAFC, sourceEntities));
		Assert.assertTrue(rowDenySecurityTableEntitiesAFC.isEmpty());
		List<String> defaultTokenIds = getDefaultAndRootTokenIds(sourceEntities.size());
		for(EntityInstance entity : rowGrantSecurityTableEntitiesAFC){
			Assert.assertTrue(defaultTokenIds.remove(entity.get(Row_Security_Grant_Table.getColumnTokenID())));
		}	
		Assert.assertEquals(mappingTableEntitiesAFC.size(), sourceEntities.size());
		for(int i = 0 ; i < mappingTableEntitiesAFC.size() ; i++) {
			Assert.assertEquals(mappingTableEntitiesAFC.get(i).get(TESTTABLE_COLUMN_STAFFID), sourceEntities.get(i).get(TESTTABLE_COLUMN_STAFFID));
		}
		
		id += sourceEntities.size();
		//end();
	}
	
	
	/** 
	 * 
	 * 
	 * <br />
	 *  User : DEFAULT$*$+ <br/>
     *  <img src="../../resources/image/create/testUserHasFullPermissionAndAllColumnsHaveDefaultDenyTokensOnCreateAction.png" /> 
     *  
	 */
	public void testCreateActionWhenUserHasDefaultGrantCRUD_AllColumnsHaveDefaultDenyCRUD(){
		//begin();
		    		    	
    	printTag("\n[--------------Test Create Action When User Has Default Grant CRUD And All Columns Have Default Deny CRUD--------------]");		
		List<EntityInstance> sourceEntities = generateSourceEntities();
		
		
    	// Add default deny tokens to all columns
    	printTag("\n[Add Default Deny Tokens To All Columns]");
    	List<EntityInstance> denyTokens = TokenFactory.getDefaultDenyTokens(iecs.getDatabaseConnection());
    	AttributeClass[] allColumns        = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	allColumns                         = Helper.primaryKeyFilter(allColumns);
    	iecs.addTokensToColumns(root, TESTTABLE_TABLENAME.toLowerCase(), allColumns, denyTokens);
    	Describer.describeTable(iecs, Column_Security_Deny_Table.makeColumnSecurityDenyTableName());
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	printTag("\n[/Add Default Deny Tokens To All Columns]");
    	
    	// Before create action
    	printTag("\n[Before Create]");
		getEntitiesFromDatabase();
		getRowDenySecurityTableContentFromDatabase();
		getRowGrantSecurityTableContentFromDatabase();
		getMappingTableContentFromDatabase();
	   
		// After create action
		printTag("\n[After Create]");
    	ResultSecurity resultSecurity                         = iecs.createEntitiesWithSecurity(user1, sourceEntities);
    	sourceEntities                                        = generateSourceEntities();
    	List<EntityInstance> passedEntities                   = getEntitiesFromResultSecurity(resultSecurity, true);
    	List<EntityInstance> failedEntities                   = getEntitiesFromResultSecurity(resultSecurity, false);
		List<EntityInstance> testTableEntitiesAFC             = getEntitiesFromDatabase();
		List<EntityInstance> rowDenySecurityTableEntitiesAFC  = getRowDenySecurityTableContentFromDatabase();
		List<EntityInstance> rowGrantSecurityTableEntitiesAFC = getRowGrantSecurityTableContentFromDatabase();
		List<EntityInstance> mappingTableEntitiesAFC          = getMappingTableContentFromDatabase();
		
		// Test assertion
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), passedEntities, sourceEntities));
		Assert.assertTrue(failedEntities.isEmpty());
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), testTableEntitiesAFC, sourceEntities));
		Assert.assertTrue(rowDenySecurityTableEntitiesAFC.isEmpty());
		List<String> defaultTokenIds = getDefaultAndRootTokenIds(2);	
		for(EntityInstance entity : rowGrantSecurityTableEntitiesAFC){
			Assert.assertTrue(defaultTokenIds.remove(entity.get(Row_Security_Grant_Table.getColumnTokenID())));
		}	
		Assert.assertEquals(mappingTableEntitiesAFC.size(), sourceEntities.size());
		for(int i = 0 ; i < mappingTableEntitiesAFC.size() ; i++) {
			Assert.assertEquals(mappingTableEntitiesAFC.get(i).get(TESTTABLE_COLUMN_STAFFID), sourceEntities.get(i).get(TESTTABLE_COLUMN_STAFFID));
		}
		
		// Clean up
    	// Roll back: Add default deny tokens to all columns
    	printTag("\n[Roll Back : Add Default Deny Tokens To All Columns]");
    	denyTokens = TokenFactory.getDefaultDenyTokens(iecs.getDatabaseConnection());
    	allColumns        = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	allColumns        = Helper.primaryKeyFilter(allColumns);
    	iecs.removeTokensFromColumns(root, TESTTABLE_TABLENAME.toLowerCase(), allColumns, denyTokens);
    	Describer.describeTable(iecs, Column_Security_Deny_Table.makeColumnSecurityDenyTableName());
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	printTag("\n[/Roll Back : Add Default Deny Tokens To All Columns]");
    	
		id += sourceEntities.size();
		//end();
	}
	
	/** 
	 *  
	 *  <br/>
	 *  User : Empty <br/>
     *  <img src="../../resources/image/create/testUserHasNoGrantTokensOnCreateAction.png" /> 
     *   
	 */
	public void testCreateActionWhenUserHasNoDefaultGrantCRUD(){
		//begin();
		
		printTag("\n[--------------Test Create Action When User Has No Default Grant CRUD--------------]"); 
    	List<EntityInstance> sourceEntities = generateSourceEntities();
		
		
    	// Remove default grant tokens from user
    	printTag("\n[Remove Default Grant Tokens From User]");
    	iecs.removeTokensFromUser(root, user1, TokenFactory.getDefaultGrantTokens(iecs.getDatabaseConnection()));
    	Describer.describeTable(iecs, User_Token_Table.makeUserTokenTableName());
    	printTag("\n[/Remove Default Grant Tokens From User]");
		
    	// Before create action
    	printTag("\n[Before Create By User]");
		getEntitiesFromDatabase();
		getRowDenySecurityTableContentFromDatabase();
		getRowGrantSecurityTableContentFromDatabase();
		getMappingTableContentFromDatabase();
				
		// After create action
		printTag("\n[After Create By User]");
	   	ResultSecurity resultSecurity                         = iecs.createEntitiesWithSecurity(user1, sourceEntities);
    	sourceEntities                                        = generateSourceEntities();
    	List<EntityInstance> passedEntities                   = getEntitiesFromResultSecurity(resultSecurity, true);
    	List<EntityInstance> failedEntities                   = getEntitiesFromResultSecurity(resultSecurity, false);
		List<EntityInstance> testTableEntitiesAFC             = getEntitiesFromDatabase();
		List<EntityInstance> rowDenySecurityTableEntitiesAFC  = getRowDenySecurityTableContentFromDatabase();
		List<EntityInstance> rowGrantSecurityTableEntitiesAFC = getRowGrantSecurityTableContentFromDatabase();
		List<EntityInstance> mappingTableEntitiesAFC          = getMappingTableContentFromDatabase();
		
		// Test assertion
		Assert.assertTrue(passedEntities.isEmpty());
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), failedEntities, sourceEntities));
		Assert.assertTrue(testTableEntitiesAFC.isEmpty());
		Assert.assertTrue(rowGrantSecurityTableEntitiesAFC.isEmpty());
		Assert.assertTrue(rowDenySecurityTableEntitiesAFC.isEmpty());
		Assert.assertTrue(mappingTableEntitiesAFC.isEmpty());		
		
    	// Clean up
    	// Roll back : Remove default grant tokens from user
    	printTag("\n[Roll Back : Remove Default Grant Tokens From User]");
    	iecs.addTokensToUser(root, user1, TokenFactory.getDefaultGrantTokens(iecs.getDatabaseConnection()));
    	iecs.addTokensToUser(root, user1, TokenFactory.getDefaultGrantTokens(iecs.getDatabaseConnection()));
    	Describer.describeTable(iecs, User_Token_Table.makeUserTokenTableName());
    	printTag("\n[/Roll Back :Remove Default Grant Tokens From User]");
		id += sourceEntities.size();
	//	end();
	}
	
	/**  
	 *  <br/>
	 *   User : DEFAULT$*$+, DEFAULT$*$- <br/>
     *  <img src="../../resources/image/create/testUserHasDefaultDenyTokensAndAllColumnsHaveDefaultDenyTokensOnCreateAction.png" />  
	 */
	public void testCreateActionWhenUserHasDefaultDenyCRUD_AllColumnsHaveDefaultDenyCRUD(){
	//	begin();
		
		printTag("\n[--------------Test Create Action When User Has Default Deny CRUD And All Columns Have Default Deny CRUD--------------]");
    	List<EntityInstance> sourceEntities = generateSourceEntities();
		
		
    	// Add default deny tokens to user1
    	printTag("\n[Add Default Deny Tokens To User]");
    	List<EntityInstance> denyTokens = TokenFactory.getDefaultDenyTokens(iecs.getDatabaseConnection());
    	iecs.addTokensToUser(root, user1, denyTokens);
    	Describer.describeTable(iecs, User_Token_Table.makeUserTokenTableName());
    	printTag("\n[/Add Default Deny Tokens To User]");
    	
    	// Add default deny tokens to all columns
    	printTag("\n[Add Default Deny Tokens To All Columns]");
    	AttributeClass[] allColumns    = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	allColumns                     = Helper.primaryKeyFilter(allColumns);
    	iecs.addTokensToColumns(root, TESTTABLE_TABLENAME.toLowerCase(), allColumns, denyTokens); 
    	Describer.describeTable(iecs, Column_Security_Deny_Table.makeColumnSecurityDenyTableName());
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	printTag("\n[/Add Default Deny Tokens To All Columns]");
		
    	// Before create action
    	printTag("\n[Before Create By User]");
		getEntitiesFromDatabase();
		getRowDenySecurityTableContentFromDatabase();
		getRowGrantSecurityTableContentFromDatabase();
		getMappingTableContentFromDatabase();
				
		// After create action
		printTag("\n[After Create By User]");
		ResultSecurity resultSecurity                         = iecs.createEntitiesWithSecurity(user1, sourceEntities);
    	sourceEntities                                        = generateSourceEntities();
    	List<EntityInstance> passedEntities                   = getEntitiesFromResultSecurity(resultSecurity, true);
    	List<EntityInstance> failedEntities                   = getEntitiesFromResultSecurity(resultSecurity, false);
		List<EntityInstance> testTableEntitiesAFC             = getEntitiesFromDatabase();
		List<EntityInstance> rowDenySecurityTableEntitiesAFC  = getRowDenySecurityTableContentFromDatabase();
		List<EntityInstance> rowGrantSecurityTableEntitiesAFC = getRowGrantSecurityTableContentFromDatabase();
		List<EntityInstance> mappingTableEntitiesAFC          = getMappingTableContentFromDatabase();
		
		// Test assertion
		Assert.assertTrue(passedEntities.isEmpty());
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), failedEntities, sourceEntities));
		Assert.assertTrue(testTableEntitiesAFC.isEmpty());
		Assert.assertTrue(rowGrantSecurityTableEntitiesAFC.isEmpty());
		Assert.assertTrue(rowDenySecurityTableEntitiesAFC.isEmpty());
		Assert.assertTrue(mappingTableEntitiesAFC.isEmpty());

		// Clean up
		// Roll back : Add default deny tokens to user1
    	printTag("\n[Roll Back : Add Default Deny Tokens To User]");
    	denyTokens = TokenFactory.getDefaultDenyTokens(iecs.getDatabaseConnection());
    	iecs.removeTokensFromUser(root, user1, denyTokens);
    	Describer.describeTable(iecs, User_Token_Table.makeUserTokenTableName());
    	printTag("\n[/Roll Back : Add Default Deny Tokens To User]");
    	
    	// Add default deny tokens to all columns
    	printTag("\n[Roll Back : Add Default Deny Tokens To All Columns]");
    	allColumns    = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	allColumns                     = Helper.primaryKeyFilter(allColumns);
    	iecs.removeTokensFromColumns(root, TESTTABLE_TABLENAME.toLowerCase(), allColumns, denyTokens); 
    	Describer.describeTable(iecs, Column_Security_Deny_Table.makeColumnSecurityDenyTableName());
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	printTag("\n[/Roll Back : Add Default Deny Tokens To All Columns]");
    	
		id += sourceEntities.size();
	//	end();
	}
	
	
	/**  
	 *  <br/>
	 *   User : DEFAULT$*$+, DEFAULT$*$-, ROOT$*$+ <br/>
     *  <img src="../../resources/image/create/testRootHasDefaultDenyTokensAndAllColumnsHaveDefaultDenyTokensOnCreateAction.png"/>
	 */
	public void testCreateActionWhenRootHasDefaultDenyCRUD_AllColumnsHaveDefaultDenyCRUD(){
		//begin();
		
		printTag("\n[--------------Test Create Action When Root Has Default Deny CRUD And All Columns Have Default Deny CRUD--------------]");    	
		List<EntityInstance> sourceEntities              = generateSourceEntities();
		
    	// Add default deny tokens to all columns
    	printTag("\n[Add Default Deny Tokens To All Columns]");
    	List<EntityInstance> denyTokens                  = TokenFactory.getDefaultDenyTokens(iecs.getDatabaseConnection());
    	AttributeClass[] allColumns                      = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	allColumns                                       = Helper.primaryKeyFilter(allColumns);
    	iecs.addTokensToColumns(root, TESTTABLE_TABLENAME.toLowerCase(), allColumns, denyTokens);
    	Describer.describeTable(iecs, Column_Security_Deny_Table.makeColumnSecurityDenyTableName());
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	printTag("\n[/Add Default Deny Tokens To All Columns]");
    	
    	// Add default deny tokens to root
    	printTag("\n[Add Default Deny Tokens To User]");
    	iecs.addTokensToUser(root, root, denyTokens);  
    	Describer.describeTable(iecs, User_Token_Table.makeUserTokenTableName());
    	printTag("\n[/Add Default Deny Tokens To User]");
		
    	// Before create action
    	printTag("\n[Before Create By Root]");
		getEntitiesFromDatabase();
		getRowDenySecurityTableContentFromDatabase();
		getRowGrantSecurityTableContentFromDatabase();
		getMappingTableContentFromDatabase();
		
		// After create action
		printTag("\n[After Create By Root]");
		ResultSecurity resultSecurity                         = iecs.createEntitiesWithSecurity(root, sourceEntities);
    	sourceEntities                                        = generateSourceEntities();
    	List<EntityInstance> passedEntities                   = getEntitiesFromResultSecurity(resultSecurity, true);
    	List<EntityInstance> failedEntities                   = getEntitiesFromResultSecurity(resultSecurity, false);
		List<EntityInstance> testTableEntitiesAFC             = getEntitiesFromDatabase();
		List<EntityInstance> rowDenySecurityTableEntitiesAFC  = getRowDenySecurityTableContentFromDatabase();
		List<EntityInstance> rowGrantSecurityTableEntitiesAFC = getRowGrantSecurityTableContentFromDatabase();
		List<EntityInstance> mappingTableEntitiesAFC          = getMappingTableContentFromDatabase();
		
		// Test assertion
		Assert.assertTrue(passedEntities.isEmpty());
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), failedEntities, sourceEntities));
		Assert.assertTrue(testTableEntitiesAFC.isEmpty());
		Assert.assertTrue(rowGrantSecurityTableEntitiesAFC.isEmpty());
		Assert.assertTrue(rowDenySecurityTableEntitiesAFC.isEmpty());
		Assert.assertTrue(mappingTableEntitiesAFC.isEmpty());

		// Clean up
    	// Roll Back : Add default deny tokens to all columns
    	printTag("\n[Roll Back : Add Default Deny Tokens To All Columns]");
    	denyTokens                  = TokenFactory.getDefaultDenyTokens(iecs.getDatabaseConnection());
    	allColumns                  = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	allColumns                  = Helper.primaryKeyFilter(allColumns);
    	iecs.removeTokensFromColumns(root, TESTTABLE_TABLENAME.toLowerCase(), allColumns, denyTokens);
    	Describer.describeTable(iecs, Column_Security_Deny_Table.makeColumnSecurityDenyTableName());
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	printTag("\n[/Roll Back : Add Default Deny Tokens To All Columns]");
    	
    	// Add default deny tokens to root
    	printTag("\n[Roll Back : Add Default Deny Tokens To User]");
    	iecs.removeTokensFromUser(root, root, denyTokens);  
    	Describer.describeTable(iecs, User_Token_Table.makeUserTokenTableName());
    	printTag("\n[/Roll Back : Add Default Deny Tokens To User]");
		
		id += sourceEntities.size();
		//end();
	}
	
	/** 
	 *  
	 *  <br/>
	 *   User : DEFAULT$*$+ <br/>
     *  <img src="../../resources/image/create/testUserHasFullPermissionAndAllColumnsHaveNoDefaultGrantTokensOnCreateAction.png" /> 
     *   
	 */
	public void testCreateActionWhenUserHasDefaultGrantCRUD_AllColumnsHaveNoDefaultGrantCRUD(){
	//	begin();
		
		printTag("\n[--------------Test Create Action When User Has Default Grant CRUD And All Columns Have No Default Grant CRUD--------------]");    	
		List<EntityInstance> sourceEntities              = generateSourceEntities();
		
		// Remove default grant tokens from all columns
    	printTag("\n[Remove Default Grant Tokens From All Columns]");
    	List<EntityInstance> grantTokens                 = TokenFactory.getDefaultGrantTokens(iecs.getDatabaseConnection());
    	AttributeClass[] allColumns                      = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	allColumns                                       = Helper.primaryKeyFilter(allColumns);
    	iecs.removeTokensFromColumns(root, TESTTABLE_TABLENAME.toLowerCase(), allColumns, grantTokens);
    	Describer.describeTable(iecs, Column_Security_Grant_Table.makeColumnSecurityGrantTableName());
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	printTag("\n[/Remove Default Grant Tokens From All Columns]");
		
    	// Before create action
    	printTag("\n[Before Create By Root]");
		getEntitiesFromDatabase();
		getRowDenySecurityTableContentFromDatabase();
		getRowGrantSecurityTableContentFromDatabase();
		getMappingTableContentFromDatabase();
		
		// After create action
		printTag("\n[After Create By Root]");
		ResultSecurity resultSecurity                         = iecs.createEntitiesWithSecurity(user1, sourceEntities);
    	sourceEntities                                        = generateSourceEntities();
    	List<EntityInstance> passedEntities                   = getEntitiesFromResultSecurity(resultSecurity, true);
    	List<EntityInstance> failedEntities                   = getEntitiesFromResultSecurity(resultSecurity, false);
		List<EntityInstance> testTableEntitiesAFC             = getEntitiesFromDatabase();
		List<EntityInstance> rowDenySecurityTableEntitiesAFC  = getRowDenySecurityTableContentFromDatabase();
		List<EntityInstance> rowGrantSecurityTableEntitiesAFC = getRowGrantSecurityTableContentFromDatabase();
		List<EntityInstance> mappingTableEntitiesAFC          = getMappingTableContentFromDatabase();

		// Test assertion
		Assert.assertTrue(passedEntities.isEmpty());
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), failedEntities, sourceEntities));
		Assert.assertTrue(testTableEntitiesAFC.isEmpty());
		Assert.assertTrue(rowGrantSecurityTableEntitiesAFC.isEmpty());
		Assert.assertTrue(rowDenySecurityTableEntitiesAFC.isEmpty());
		Assert.assertTrue(mappingTableEntitiesAFC.isEmpty());
    	
		// Clean up
		// Roll Back : Remove default grant tokens from all columns
    	printTag("\n[Roll Back : Remove Default Grant Tokens From All Columns]");
    	grantTokens                 = TokenFactory.getDefaultGrantTokens(iecs.getDatabaseConnection());
    	allColumns                  = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	allColumns                  = Helper.primaryKeyFilter(allColumns);
    	iecs.addTokensToColumns(root, TESTTABLE_TABLENAME.toLowerCase(), allColumns, grantTokens);
    	Describer.describeTable(iecs, Column_Security_Grant_Table.makeColumnSecurityGrantTableName());
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	printTag("\n[/Roll Back : Remove Default Grant Tokens From All Columns]");
		
		
		id += sourceEntities.size();
	//	end();
	}
    
	
	
	
	
	
	/** 
	 * 
	 * <br/>
	 *  User : DEFAULT$*$+ <br/>
     *  <img src="../../resources/image/read/testUserHasFullPermissionOnReadAction.png" /> 
     *   
	 */
	public void testReadActionWhenUserHasDefaultGrantCRUD(){
		//begin();
		
		printTag("\n[--------------Test Read Action When User Has Grant Default CRUD--------------]");
		List<EntityInstance> sourceEntities     = generateSourceEntities();
		iecs.createEntitiesWithSecurity(user1, sourceEntities);
		sourceEntities                          = generateSourceEntities();
		CompoundStatementManual csm             = new CompoundStatementManual();
		csm.addConditionalStatement(makeSqlStatementByStaffId());
		
		// Start to read
    	printTag("\n[Start To Read]");
		ResultSecurity resultSecurity           = iecs.readEntitiesWithSecurity(user1, TESTTABLE_TABLENAME, csm);
		List<EntityInstance> passedEntities     = getEntitiesFromResultSecurity(resultSecurity, true);
		List<EntityInstance> failedEntities     = getEntitiesFromResultSecurity(resultSecurity, false);

		// Test assertion
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), passedEntities, sourceEntities));
		Assert.assertTrue(failedEntities == null);
		
		id += sourceEntities.size();
		//end();
	}
	
	/** 
	 * 
	 * <br/>
	 *  User : DEFAULT$*$+ <br/>
     *  <img src="../../resources/image/read/testUserHasFullPermissionAndAllColumnsHaveDefaultDenyTokensOnReadAction.png" /> 
     *   
	 */
	public void testReadActionWhenUserHasDefaultGrantCRUD_AllColumnsHaveDefaultDenyCRUD(){
	//	begin();
		
		printTag("\n[--------------Test Read Action When User Has Default Grant CRUD And All Columns Have Default Deny CRUD--------------]");
		List<EntityInstance> sourceEntities     = generateSourceEntities();
		iecs.createEntitiesWithSecurity(user1, sourceEntities);
		sourceEntities                          = generateSourceEntities();
		CompoundStatementManual csm             = new CompoundStatementManual();
		csm.addConditionalStatement(makeSqlStatementByStaffId());
		
    	// Add default deny tokens to all columns
    	printTag("\n[Add Default Deny Tokens To All Columns]");
    	List<EntityInstance> denyTokens         = TokenFactory.getDefaultDenyTokens(iecs.getDatabaseConnection());
    	AttributeClass[] allColumns             = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	allColumns                              = Helper.primaryKeyFilter(allColumns);
    	iecs.addTokensToColumns(root, TESTTABLE_TABLENAME.toLowerCase(), allColumns, denyTokens); 
    	Describer.describeTable(iecs, Column_Security_Deny_Table.makeColumnSecurityDenyTableName());
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	printTag("\n[/Add Default Deny Tokens To All Columns]");
		
    	// Start to read
    	printTag("\n[Start To Read]");
		ResultSecurity resultSecurity           = iecs.readEntitiesWithSecurity(user1, TESTTABLE_TABLENAME, csm);
		List<EntityInstance> passedEntities     = getEntitiesFromResultSecurity(resultSecurity, true);
		List<EntityInstance> failedEntities     = getEntitiesFromResultSecurity(resultSecurity, false);

		// Test assertion
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), passedEntities, sourceEntities));
		Assert.assertTrue(failedEntities == null);
    	
		// Clean up
    	// Roll Back : Add default deny tokens to all columns
    	printTag("\n[Roll Back : Add Default Deny Tokens To All Columns]");
    	denyTokens         = TokenFactory.getDefaultDenyTokens(iecs.getDatabaseConnection());
    	allColumns         = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	allColumns         = Helper.primaryKeyFilter(allColumns);
    	iecs.removeTokensFromColumns(root, TESTTABLE_TABLENAME.toLowerCase(), allColumns, denyTokens); 
    	Describer.describeTable(iecs, Column_Security_Deny_Table.makeColumnSecurityDenyTableName());
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	printTag("\n[/Roll Back : Add Default Deny Tokens To All Columns]");
		id += sourceEntities.size();
	//	end();
	}
	
	/** 
	 * 
	 * <br/>
	 *  User : DEFAULT$*$+ <br/>
     *  <img src="../../resources/image/read/testUserHasFullPermissionAndFirstRowHasDefaultDenyTokensOnReadAction.png" /> 
     *   
	 */
	public void testReadActionWhenUserHasDefaultGrantCRUD_FirstRowHasDefaultDenyCRUD(){ 
	//	begin();
		
		printTag("\n[--------------Test Read Action When UserHas Default Grant CRUD And First Row Has Default Deny CRUD--------------]");
		List<EntityInstance> sourceEntities     = generateSourceEntities();
		iecs.createEntitiesWithSecurity(user1, sourceEntities);
		sourceEntities                          = generateSourceEntities();
		CompoundStatementManual csm             = new CompoundStatementManual();
		csm.addConditionalStatement(makeSqlStatementByStaffId());
		
    	// Add default deny tokens to the first row
    	printTag("\n[Add Default Deny Tokens To The First Row]");
    	List<EntityInstance> denyTokens         = TokenFactory.getDefaultDenyTokens(iecs.getDatabaseConnection());
    	List<EntityInstance> rows               = new LinkedList<EntityInstance>();
    	rows.add(sourceEntities.get(0));
    	iecs.addTokensToRows(root, rows, denyTokens);
    	Describer.describeTable(iecs, Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME));
    	Describer.describeTable(iecs, Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME));
    	printTag("\n[/Add Default Deny Tokens To The First Row]");
    	
    	// Start to read
    	printTag("\n[Start To Read]");
		ResultSecurity resultSecurity           = iecs.readEntitiesWithSecurity(user1, TESTTABLE_TABLENAME, csm);
		List<EntityInstance> passedEntities     = getEntitiesFromResultSecurity(resultSecurity, true);
		List<EntityInstance> failedEntities     = getEntitiesFromResultSecurity(resultSecurity, false);

		// Test assertion
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), passedEntities, sourceEntities));
		Assert.assertTrue(failedEntities == null);
    	
		// Clean up
    	// Roll Back : Add default deny tokens to the first row
    	printTag("\n[Roll Back : Add Default Deny Tokens To The First Row]");
    	denyTokens         = TokenFactory.getDefaultDenyTokens(iecs.getDatabaseConnection());
    	rows               = new LinkedList<EntityInstance>();
    	rows.add(sourceEntities.get(0));
    	iecs.removeTokensFromRows(root, rows, denyTokens);
    	Describer.describeTable(iecs, Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME));
    	Describer.describeTable(iecs, Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME));
    	printTag("\n[/Roll Back : Add Default Deny Tokens To The First Row]");
		id += sourceEntities.size();
	//	end();
	}
	
	/** 
	 * 
	 * <br/>
	 *  User : Empty <br/>
     *  <img src="../../resources/image/read/testUserHasNoGrantTokensOnReadAction.png" /> 
     *   
	 */
	public void testReadActionWhenUserHasNoDefaultGrantCRUD(){
		//begin();
		
		printTag("\n[--------------Test Read Action When User Has No Default Grant CRUD--------------]");
		List<EntityInstance> sourceEntities      = generateSourceEntities();
		iecs.createEntitiesWithSecurity(user1, sourceEntities);
		sourceEntities                           = generateSourceEntities();
		CompoundStatementManual csm             = new CompoundStatementManual();
		csm.addConditionalStatement(makeSqlStatementByStaffId());
		
		// Remove all default tokens from user
		printTag("[Remove All Default Grant Tokens From User]");
		iecs.removeTokensFromUser(root, user1, TokenFactory.getDefaultGrantTokens(iecs.getDatabaseConnection()));
    	Describer.describeTable(iecs, User_Token_Table.makeUserTokenTableName());
    	printTag("\n[/Remove All Default Grant Tokens From User]");
		
		// Start to read
    	printTag("\n[Start To Read]");
		ResultSecurity resultSecurity           = iecs.readEntitiesWithSecurity(user1, TESTTABLE_TABLENAME, csm);
		List<EntityInstance> passedEntities     = getEntitiesFromResultSecurity(resultSecurity, true);
		List<EntityInstance> failedEntities     = getEntitiesFromResultSecurity(resultSecurity, false);
		
		// Test assertion
		Assert.assertTrue(passedEntities.isEmpty());
		Assert.assertTrue(failedEntities == null);
    	
		// Clean up
		// Roll Back : Remove all default tokens from user
		printTag("[Roll Back : Remove All Default Grant Tokens From User]");
		iecs.addTokensToUser(root, user1, TokenFactory.getDefaultGrantTokens(iecs.getDatabaseConnection()));
    	Describer.describeTable(iecs, User_Token_Table.makeUserTokenTableName());
    	printTag("\n[/Roll Back : Remove All Default Grant Tokens From User]");
		
		id += sourceEntities.size();
		//end();
	}
	
	/** 
	 * 
	 * <br/>
	 *  User : DEFAULT$*$+ <br/>
     *  <img src="../../resources/image/read/testUserHasFullPermissionAndLastColumnHasNoDefaultGrantTokensOnReadAction.png" /> 
     *   
	 */
	public void testReadActionWhenUserHasDefaultGrantCRUD_LastColumnHasNoDefaultGrantCRUD(){
		//begin();
		
		printTag("\n[--------------Test Read Action When User Has Default Grant CRUD And Last Column Has No Default Grant CRUD--------------]");
		List<EntityInstance> sourceEntities     = generateSourceEntities();
		iecs.createEntitiesWithSecurity(user1, sourceEntities);
		sourceEntities                          = generateSourceEntities();
		CompoundStatementManual csm             = new CompoundStatementManual();
		csm.addConditionalStatement(makeSqlStatementByStaffId());
		
		// Remove default grant tokens from last column
    	printTag("\n[Remove Default Grant Tokens From Column Phone]");
    	List<EntityInstance> grantTokens        = TokenFactory.getDefaultGrantTokens(iecs.getDatabaseConnection());
    	AttributeClass[] allColumns             = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	AttributeClass[] columns                = {allColumns[allColumns.length-1]};
    	iecs.removeTokensFromColumns(root, TESTTABLE_TABLENAME.toLowerCase(), columns, grantTokens);
    	Describer.describeTable(iecs, Column_Security_Grant_Table.makeColumnSecurityGrantTableName());
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	printTag("\n[/Remove Default Grant Tokens From Column Phone]");
    	
    	// Start to read
    	printTag("\n[Start To Read]");
		ResultSecurity resultSecurity           = iecs.readEntitiesWithSecurity(user1, TESTTABLE_TABLENAME, csm);
		List<EntityInstance> passedEntities     = getEntitiesFromResultSecurity(resultSecurity, true);
		List<EntityInstance> failedEntities     = getEntitiesFromResultSecurity(resultSecurity, false);
		
		//Test assertion
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), passedEntities, sourceEntities));
		Assert.assertTrue(failedEntities == null);
		Assert.assertTrue(passedEntities.size() == 2);
		Assert.assertTrue(passedEntities.get(0).get(TESTTABLE_COLUMN_PHONE) instanceof UnreadableValue);
		Assert.assertTrue(passedEntities.get(1).get(TESTTABLE_COLUMN_PHONE) instanceof UnreadableValue);
    	
		// Clean up
		// Roll Back : Remove default grant tokens from last column
    	printTag("\n[Roll Back : Remove Default Grant Tokens From Column Phone]");
    	grantTokens                      = TokenFactory.getDefaultGrantTokens(iecs.getDatabaseConnection());
    	allColumns                       = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	AttributeClass[] columnsRollBack = {allColumns[allColumns.length-1]};
    	iecs.addTokensToColumns(root, TESTTABLE_TABLENAME.toLowerCase(), columnsRollBack, grantTokens);
    	Describer.describeTable(iecs, Column_Security_Grant_Table.makeColumnSecurityGrantTableName());
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	printTag("\n[/Roll Back : Remove Default Grant Tokens From Column Phone]");
    	
		
		id += sourceEntities.size();
		//end();	
	}
	
	/** 
	 * 
	 * <br/>
	 *  User : DEFAULT$*$+  DEFAULT$*$- <br/>
     *  <img src="../../resources/image/read/testUserHasDefaultDenyTokensAndLastColumnHasDefaultDenyTokensOnReadAction.png" /> 
     *   
	 */
	public void testReadActionWhenUserHasDefaultDenyCRUD_LastColumnHasDefaultDenyCRUD(){
		//begin();
		
		printTag("\n[--------------Test Read Action When User Has Default Deny CRUD And Last Column Has Default Deny CRUD--------------]");
		List<EntityInstance> sourceEntities      = generateSourceEntities();
		iecs.createEntitiesWithSecurity(user1, sourceEntities);
		sourceEntities                           = generateSourceEntities();
		CompoundStatementManual csm             = new CompoundStatementManual();
		csm.addConditionalStatement(makeSqlStatementByStaffId());
		
    	// Add default deny tokens to user
    	printTag("\n[Add Default Deny Tokens To User]");
    	List<EntityInstance> denyTokens          = TokenFactory.getDefaultDenyTokens(iecs.getDatabaseConnection());
    	iecs.addTokensToUser(root, user1, denyTokens);
    	Describer.describeTable(iecs, User_Token_Table.makeUserTokenTableName());
    	printTag("\n[/Add Default Deny Tokens To User]");
    	
    	// Add default deny tokens to last column
    	printTag("\n[Add Default Deny Tokens To Column Phone]");
    	AttributeClass[] allColumns             = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	AttributeClass[] columns                = {allColumns[allColumns.length-1]};
    	iecs.addTokensToColumns(root, TESTTABLE_TABLENAME.toLowerCase(), columns, denyTokens);
    	Describer.describeTable(iecs, Column_Security_Deny_Table.makeColumnSecurityDenyTableName());
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	printTag("\n[/Add Default Deny Tokens To Column Phone]");
    	
		// Start to read
    	printTag("\n[Start To Read]");
		ResultSecurity resultSecurity           = iecs.readEntitiesWithSecurity(user1, TESTTABLE_TABLENAME, csm);
		List<EntityInstance> passedEntities     = getEntitiesFromResultSecurity(resultSecurity, true);
		List<EntityInstance> failedEntities     = getEntitiesFromResultSecurity(resultSecurity, false);
		
		// Test assertion
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), passedEntities, sourceEntities));
		Assert.assertTrue(failedEntities == null);
		Assert.assertTrue(passedEntities.get(0).get(TESTTABLE_COLUMN_PHONE) instanceof UnreadableValue);
		Assert.assertTrue(passedEntities.get(1).get(TESTTABLE_COLUMN_PHONE) instanceof UnreadableValue);
    	
		// Clean up
    	// Roll Back : Add default deny tokens to user
    	printTag("\n[Roll Back : Add Default Deny Tokens To User]");
    	denyTokens          = TokenFactory.getDefaultDenyTokens(iecs.getDatabaseConnection());
    	iecs.removeTokensFromUser(root, user1, denyTokens);
    	Describer.describeTable(iecs, User_Token_Table.makeUserTokenTableName());
    	printTag("\n[/Roll Back : Add Default Deny Tokens To User]");
    	
    	// Add default deny tokens to last column
    	printTag("\n[Roll Back : Add Default Deny Tokens To Column Phone]");
    	allColumns                        = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	AttributeClass[] columnsRollBack  = {allColumns[allColumns.length-1]};
    	iecs.removeTokensFromColumns(root, TESTTABLE_TABLENAME.toLowerCase(), columnsRollBack, denyTokens);
    	Describer.describeTable(iecs, Column_Security_Deny_Table.makeColumnSecurityDenyTableName());
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	printTag("\n[/Roll Back : Add Default Deny Tokens To Column Phone]");
    	
		id += sourceEntities.size();
		//end();
	}
	
	/** 
	 * 
	 * <br/>
	 *  User : DEFAULT$*$+ <br/>
     *  <img src="../../resources/image/read/testUserHasFullPermissionAndFirstRowHasNoDefaultGrantTokensOnReadAction.png" /> 
     *   
	 */
	public void testReadActionWhenUserHasDefalutGrantCRUD_FirstRowHasNoDefaultGrantCRUD(){
		//begin();
		
		printTag("\n[--------------Test Read Action When User Has Defalut Grant CRUD And First Row Has No Default Grant CRUD--------------]");
		List<EntityInstance> sourceEntities     = generateSourceEntities();
		iecs.createEntitiesWithSecurity(user1, sourceEntities);
		sourceEntities                          = generateSourceEntities();
		
		CompoundStatementManual csm             = new CompoundStatementManual();
		csm.addConditionalStatement(makeSqlStatementByStaffId());
		
		// Remove default grant tokens from first row
    	printTag("\n[Remove Default Grant Tokens From First Row]");
    	List<EntityInstance> grantTokens        = TokenFactory.getDefaultGrantTokens(iecs.getDatabaseConnection());
    	List<EntityInstance> rows               = new LinkedList<EntityInstance>();
    	rows.add(sourceEntities.get(0));
    	iecs.removeTokensFromRows(root, rows, grantTokens);
    	Describer.describeTable(iecs, Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME));
    	Describer.describeTable(iecs, Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(TESTTABLE_TABLENAME));
    	printTag("\n[/Remove Default Grant Tokens From First Row]");
    	
    	// Start to read
    	printTag("\n[Start To Read]");
		ResultSecurity resultSecurity           = iecs.readEntitiesWithSecurity(user1, TESTTABLE_TABLENAME, csm);
		List<EntityInstance> passedEntities     = getEntitiesFromResultSecurity(resultSecurity, true);
		List<EntityInstance> failedEntities     = getEntitiesFromResultSecurity(resultSecurity, false);
		
		//Test assertion
		Assert.assertTrue(passedEntities.size() == 1);
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), passedEntities.get(0), sourceEntities.get(1)));
		Assert.assertTrue(failedEntities == null);
    	
		// Clean up
		// Roll Back : Remove default grant tokens from first row
    	printTag("\n[Roll Back : Remove Default Grant Tokens From First Row]");
    	grantTokens        = TokenFactory.getDefaultGrantTokens(iecs.getDatabaseConnection());
    	rows               = new LinkedList<EntityInstance>();
    	rows.add(sourceEntities.get(0));
    	iecs.addTokensToRows(root, rows, grantTokens);
    	Describer.describeTable(iecs, Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME));
    	Describer.describeTable(iecs, Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(TESTTABLE_TABLENAME));
    	printTag("\n[/Roll Back : Remove Default Grant Tokens From First Row]");
		id += sourceEntities.size();
		//end();	
	}
	
	/** 
	 * 
	 * <br/>
	 *  User : DEFAULT$*$+  DEFAULT$*$- <br/>
     *  <img src="../../resources/image/read/testUserHasDefaultDenyTokensAndFirstRowHasDefaultDenyTokensOnReadAction.png" /> 
     *   
	 */
	public void testReadActionWhenUserHasDefaultDenyCRUD_FirstRowHasDefaultDenyCRUD(){
		//begin();
		
		printTag("\n[--------------Test Read Action When User Has Default Deny CRUD And First Row Has Default Deny CRUD--------------]");
		List<EntityInstance> sourceEntities      = generateSourceEntities();
		iecs.createEntitiesWithSecurity(user1, sourceEntities);
		sourceEntities                           = generateSourceEntities();
		CompoundStatementManual csm             = new CompoundStatementManual();
		csm.addConditionalStatement(makeSqlStatementByStaffId());
		
    	// Add default deny tokens to user
    	printTag("\n[Add Default Deny Tokens To User]");
    	List<EntityInstance> denyTokens          = TokenFactory.getDefaultDenyTokens(iecs.getDatabaseConnection());
    	iecs.addTokensToUser(root, user1, denyTokens);
    	Describer.describeTable(iecs, User_Token_Table.makeUserTokenTableName());
    	printTag("\n[/Add Default Deny Tokens To User]");
    	
    	// Add default deny tokens to the first row
    	printTag("\n[Add Default Deny Tokens To The First Row]");
    	List<EntityInstance> rows               = new LinkedList<EntityInstance>();
    	rows.add(sourceEntities.get(0));
    	iecs.addTokensToRows(root, rows, denyTokens);
    	Describer.describeTable(iecs, Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME));
    	Describer.describeTable(iecs, Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME));
    	printTag("\n[/Add Default Deny Tokens To The First Row]");
    	
		// Start to read
    	printTag("\n[Start To Read]");
		ResultSecurity resultSecurity           = iecs.readEntitiesWithSecurity(user1, TESTTABLE_TABLENAME, csm);
		List<EntityInstance> passedEntities     = getEntitiesFromResultSecurity(resultSecurity, true);
		List<EntityInstance> failedEntities     = getEntitiesFromResultSecurity(resultSecurity, false);
		
		// Test assertion
		Assert.assertTrue(passedEntities.size() == 1);
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), passedEntities.get(0), sourceEntities.get(1)));
		Assert.assertTrue(failedEntities == null);		
    	
		// Clean up
    	// Roll Back : Add default deny tokens to user
    	printTag("\n[Roll Back : Add Default Deny Tokens To User]");
    	denyTokens          = TokenFactory.getDefaultDenyTokens(iecs.getDatabaseConnection());
    	iecs.removeTokensFromUser(root, user1, denyTokens);
    	Describer.describeTable(iecs, User_Token_Table.makeUserTokenTableName());
    	printTag("\n[/Roll Back : Add Default Deny Tokens To User]");
    	
    	// Roll Back : Add default deny tokens to the first row
    	printTag("\n[Roll Back : Add Default Deny Tokens To The First Row]");
    	rows               = new LinkedList<EntityInstance>();
    	rows.add(sourceEntities.get(0));
    	iecs.removeTokensFromRows(root, rows, denyTokens);
    	Describer.describeTable(iecs, Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME));
    	Describer.describeTable(iecs, Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME));
    	printTag("\n[/Roll Back : Add Default Deny Tokens To The First Row]");
		id += sourceEntities.size();
		//end();
	}
	
	/** 
	 * 
	 * <br/>
	 *  User : DEFAULT$*$+  DEFAULT$*$- <br/>
     *  <img src="../../resources/image/read/testUserHasDefaultDenyTokensAndLastColumnHasDefaultDenyTokensAndFirstRowHasDefaultDenyTokensOnReadAction.png" /> 
     *   
	 */
	public void testReadActionWhenUserHasDefaultDenyCRUD_LastColumnHasDefaultDenyCRUD_FirstRowHasDefaultDenyCRUD(){
		//begin();
		
		printTag("\n[--------------Test Read Action When User Has Default Deny CRUD And Last Column Has Default Deny CRUD And First Row Has Default Deny CRUD--------------]");
		List<EntityInstance> sourceEntities      = generateSourceEntities();
		iecs.createEntitiesWithSecurity(user1, sourceEntities);
		sourceEntities                           = generateSourceEntities();
		CompoundStatementManual csm             = new CompoundStatementManual();
		csm.addConditionalStatement(makeSqlStatementByStaffId());
		
    	// Add default deny tokens to user
    	printTag("\n[Add Default Deny Tokens To User]");
    	List<EntityInstance> denyTokens          = TokenFactory.getDefaultDenyTokens(iecs.getDatabaseConnection());
    	Describer.describeTable(iecs, User_Token_Table.makeUserTokenTableName());
    	iecs.addTokensToUser(root, user1, denyTokens);
    	Describer.describeTable(iecs, User_Token_Table.makeUserTokenTableName());
    	printTag("\n[/Add Default Deny Tokens To User]");
    	
    	// Add default deny tokens to last column
    	printTag("\n[Add Default Deny Tokens To Column Phone]");
    	AttributeClass[] allColumns             = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	AttributeClass[] columns                = {allColumns[allColumns.length-1]};
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	Describer.describeTable(iecs, Column_Security_Deny_Table.makeColumnSecurityDenyTableName());
    	iecs.addTokensToColumns(root, TESTTABLE_TABLENAME.toLowerCase(), columns, denyTokens);
    	Describer.describeTable(iecs, Column_Security_Deny_Table.makeColumnSecurityDenyTableName());
    	printTag("\n[/Add Default Deny Tokens To Column Phone]");
    	
    	// Add default deny tokens to the first row
    	printTag("\n[Add Default Deny Tokens To The Frist Row]");
    	List<EntityInstance> rows               = new LinkedList<EntityInstance>();
    	rows.add(sourceEntities.get(0));
    	Describer.describeTable(iecs, Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME));
    	Describer.describeTable(iecs, Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME));
    	iecs.addTokensToRows(root, rows, denyTokens);
    	Describer.describeTable(iecs, Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME));
    	printTag("\n[/Add Default Deny Tokens To The Frist Row]");
    	
		// Start to read
    	printTag("\n[Start To Read]");
		ResultSecurity resultSecurity           = iecs.readEntitiesWithSecurity(user1, TESTTABLE_TABLENAME, csm);
		List<EntityInstance> passedEntities     = getEntitiesFromResultSecurity(resultSecurity, true);
		List<EntityInstance> failedEntities     = getEntitiesFromResultSecurity(resultSecurity, false);
		
		// Test assertion
		Assert.assertTrue(passedEntities.size() == 1);
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), passedEntities.get(0), sourceEntities.get(1)));
		Assert.assertTrue(passedEntities.get(0).get(TESTTABLE_COLUMN_PHONE) instanceof UnreadableValue);
		Assert.assertTrue(failedEntities == null);	
    	
		// Clean up
	   	// Roll Back : Add default deny tokens to user
    	printTag("\n[Roll Back : Add Default Deny Tokens To User]");
    	denyTokens          = TokenFactory.getDefaultDenyTokens(iecs.getDatabaseConnection());
    	Describer.describeTable(iecs, User_Token_Table.makeUserTokenTableName());
    	iecs.removeTokensFromUser(root, user1, denyTokens);
    	Describer.describeTable(iecs, User_Token_Table.makeUserTokenTableName());
    	printTag("\n[/Roll Back : Add Default Deny Tokens To User]");
    	
    	// Roll Back : Add default deny tokens to last column
    	printTag("\n[Roll Back : Add Default Deny Tokens To Column Phone]");
    	allColumns                        = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	AttributeClass[] columnsRollBack  = {allColumns[allColumns.length-1]};
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	Describer.describeTable(iecs, Column_Security_Deny_Table.makeColumnSecurityDenyTableName());
    	iecs.removeTokensFromColumns(root, TESTTABLE_TABLENAME.toLowerCase(), columnsRollBack, denyTokens);
    	Describer.describeTable(iecs, Column_Security_Deny_Table.makeColumnSecurityDenyTableName());
    	printTag("\n[/Roll Back : Add Default Deny Tokens To Column Phone]");
    	
    	// Roll Back : Add default deny tokens to the first row
    	printTag("\n[Roll Back : Add Default Deny Tokens To The Frist Row]");
    	rows               = new LinkedList<EntityInstance>();
    	rows.add(sourceEntities.get(0));
    	Describer.describeTable(iecs, Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME));
    	Describer.describeTable(iecs, Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME));
    	iecs.removeTokensFromRows(root, rows, denyTokens);
    	Describer.describeTable(iecs, Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME));
    	printTag("\n[/Roll Back : Add Default Deny Tokens To The Frist Row]");
		id += sourceEntities.size();
		//end();
	}
	
	/** 
	 * 
	 * <br/>
	 *  User : DEFAULT$*$+ <br/>
     *  <img src="../../resources/image/read/testUserHasFullPermissionAndLastColumnHasNoDefaultGrantTokensAndFirstRowHasNoDefaultGrantTokensOnReadAction.png" /> 
     *   
	 */
	public void testReadActionWhenUserHasDefalutGrantCRUD_LastColumnHasNoDefaultGrantCRUD_FirstRowHasNoDefaultGrantCRUD(){
    	//begin();
    	
		printTag("\n[--------------Test Read Action When User Has Defalut Grant CRUD And Last Column Has No Default Grant CRUD And First Row Has No Default Grant CRUD--------------]");
    	List<EntityInstance> sourceEntities      = generateSourceEntities();
		iecs.createEntitiesWithSecurity(user1, sourceEntities);
		sourceEntities                           = generateSourceEntities();
		CompoundStatementManual csm             = new CompoundStatementManual();
		csm.addConditionalStatement(makeSqlStatementByStaffId());
		
    	// Remove default grant tokens from last column
    	printTag("\n[Remove Default Grant Tokens From Column Phone]");
    	AttributeClass[] allColumns             = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	AttributeClass[] columns                = {allColumns[allColumns.length-1]};
    	List<EntityInstance> grantTokens        = TokenFactory.getDefaultGrantTokens(iecs.getDatabaseConnection());
    	iecs.removeTokensFromColumns(root, TESTTABLE_TABLENAME.toLowerCase(), columns, grantTokens);
    	Describer.describeTable(iecs, Column_Security_Grant_Table.makeColumnSecurityGrantTableName());
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	printTag("\n[/Remove Default Grant Tokens From Column Phone]");
    	
    	// Remove default grant tokens from the first row
    	printTag("\n[Remove Default Grant Tokens From The Frist Row]");
    	List<EntityInstance> rows               = new LinkedList<EntityInstance>();
    	rows.add(sourceEntities.get(0));
    	iecs.removeTokensFromRows(root, rows, grantTokens);
    	Describer.describeTable(iecs, Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(TESTTABLE_TABLENAME));
    	Describer.describeTable(iecs, Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME));
    	printTag("\n[/Remove Default Grant Tokens From The Frist Row]");
    	
		// Start to read
    	printTag("\n[Start To Read]");
		ResultSecurity resultSecurity           = iecs.readEntitiesWithSecurity(user1, TESTTABLE_TABLENAME, csm);
		List<EntityInstance> passedEntities     = getEntitiesFromResultSecurity(resultSecurity, true);
		List<EntityInstance> failedEntities     = getEntitiesFromResultSecurity(resultSecurity, false);
		
		// Test assertion
		Assert.assertTrue(passedEntities.size() == 1);
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), passedEntities.get(0), sourceEntities.get(1)));
		Assert.assertTrue(passedEntities.get(0).get(TESTTABLE_COLUMN_PHONE) instanceof UnreadableValue);
		Assert.assertTrue(failedEntities == null);	
    	
		// Clean up
    	// Roll Back : Remove default grant tokens from last column
    	printTag("\n[Roll Back : Remove Default Grant Tokens From Column Phone]");
    	allColumns                              = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	AttributeClass[] columnsRollBack        = {allColumns[allColumns.length-1]};
    	grantTokens                             = TokenFactory.getDefaultGrantTokens(iecs.getDatabaseConnection());
    	iecs.addTokensToColumns(root, TESTTABLE_TABLENAME.toLowerCase(), columnsRollBack, grantTokens);
    	Describer.describeTable(iecs, Column_Security_Grant_Table.makeColumnSecurityGrantTableName());
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	printTag("\n[/Roll Back : Remove Default Grant Tokens From Column Phone]");
    	
    	// Remove default grant tokens from the first row
    	printTag("\n[Roll Back : Remove Default Grant Tokens From The Frist Row]");
    	rows                                    = new LinkedList<EntityInstance>();
    	rows.add(sourceEntities.get(0));
    	iecs.addTokensToRows(root, rows, grantTokens);
    	Describer.describeTable(iecs, Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(TESTTABLE_TABLENAME));
    	Describer.describeTable(iecs, Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME));
    	printTag("\n[/Roll Back : Remove Default Grant Tokens From The Frist Row]");
		id += sourceEntities.size();
		//end();
    }
	
	
	
	
	
	
	/** 
	 * 
	 * <br/>
	 *  User : DEFAULT$*$+ <br/>
     *  <img src="../../resources/image/update/testUserHasFullPermissionOnUpdateAction.png" /> 
     *   
	 */
	public void testUpdateActionWhenUserHasDefaultGrantCRUD(){
		//begin();
		
		printTag("\n[--------------Test User Has Full Permission On Update Action--------------]");
		List<EntityInstance> sourceEntities        = generateSourceEntities();
		iecs.createEntitiesWithSecurity(user1, sourceEntities);
		sourceEntities                             = generateSourceEntities();
		
		
		// Before Update
		printTag("\n[Before Update]");
		List<EntityInstance> willBeUpdatedEntities = getEntitiesFromDatabaseWithSecurity(CRUD.UPDATE);
		getEntitiesFromDatabase();
		
		// After Update
		printTag("\n[After Update Phone To 111111]");
		for(EntityInstance willBeUpdatedEntity : willBeUpdatedEntities){
			willBeUpdatedEntity.set(TESTTABLE_COLUMN_PHONE, "111111");
		}
		ResultSecurity resultSecurity             = iecs.updateEntitiesWithSecurity(user1, willBeUpdatedEntities);
		List<EntityInstance> passedEntities       = getEntitiesFromResultSecurity(resultSecurity, true);
		List<EntityInstance> failedEntities       = getEntitiesFromResultSecurity(resultSecurity, false);
		List<EntityInstance> testTableEntitiesAFU = getEntitiesFromDatabase();

		// Test assertion
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), passedEntities, testTableEntitiesAFU));
		Assert.assertTrue(failedEntities.isEmpty());
    	
		id += sourceEntities.size();
		//end();
	}
	
	/** 
	 * 
	 * <br/>
	 *  User : DEFAULT$*$+ <br/>
     *  <img src="../../resources/image/update/testUserHasFullPermissionAndLastColumnHasNoDefaultGrantTokensAndLastColumnDataWillNotBeUpdatedOnUpdateAction.png" /> 
     *   
	 */
	public void testUpdateActionWhenUserHasDefalutGrantCRUD_LastColumnHasNoDefaultGrantCRUD_LastColumnDataWillNotBeUpdated(){
		//begin();
		
		printTag("\n[-------------Test User Has Full Permission And Last Column Has No Default Grant Tokens And Last Column Data Is Not Updated On Update Action--------------]");
		List<EntityInstance> sourceEntities        = generateSourceEntities();
		iecs.createEntitiesWithSecurity(user1, sourceEntities);
		sourceEntities                             = generateSourceEntities();
		
		
    	// Remove default grant tokens from last column
    	printTag("\n[Remove Default Grant Tokens From Column Phone]");
    	AttributeClass[] allColumns                = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	AttributeClass[] columns                   = {allColumns[allColumns.length-1]};
    	List<EntityInstance> grantTokens           = TokenFactory.getDefaultGrantTokens(iecs.getDatabaseConnection());
    	iecs.removeTokensFromColumns(root, TESTTABLE_TABLENAME.toLowerCase(), columns, grantTokens);
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	Describer.describeTable(iecs, Column_Security_Grant_Table.makeColumnSecurityGrantTableName());
    	printTag("\n[Remove Default Grant Tokens From Column Phone]");
		
		// Before Update
		printTag("\n[Before Update]");
		List<EntityInstance> willBeUpdatedEntities = getEntitiesFromDatabaseWithSecurity(CRUD.UPDATE);
		getEntitiesFromDatabase();
		
		// After Update
		printTag("\n[After Update Name To SuperX By User]");
		for(EntityInstance willBeUpdatedEntity : willBeUpdatedEntities){
			willBeUpdatedEntity.set(TESTTABLE_COLUMN_NAME, "SuperX");
		}
		ResultSecurity resultSecurity             = iecs.updateEntitiesWithSecurity(user1, willBeUpdatedEntities);
		List<EntityInstance> passedEntities       = getEntitiesFromResultSecurity(resultSecurity, true);
		List<EntityInstance> failedEntities       = getEntitiesFromResultSecurity(resultSecurity, false);
		List<EntityInstance> testTableEntitiesAFU = getEntitiesFromDatabase();

		// Test assertion
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), passedEntities, testTableEntitiesAFU));
		Assert.assertTrue(failedEntities.isEmpty());
    	
		// Clean up
    	// Roll Back : Remove default grant tokens from last column
    	printTag("\n[Roll Back : Remove Default Grant Tokens From Column Phone]");
    	allColumns                       = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	AttributeClass[] columnsRollBack = {allColumns[allColumns.length-1]};
    	grantTokens                      = TokenFactory.getDefaultGrantTokens(iecs.getDatabaseConnection());
    	iecs.addTokensToColumns(root, TESTTABLE_TABLENAME.toLowerCase(), columnsRollBack, grantTokens);
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	Describer.describeTable(iecs, Column_Security_Grant_Table.makeColumnSecurityGrantTableName());
    	printTag("\n[Roll Back : Remove Default Grant Tokens From Column Phone]");
		
		id += sourceEntities.size();
		//end();
	}
	
	/** 
	 * 
	 * <br/>
	 *  User : DEFAULT$*$+ <br/>
     *  <img src="../../resources/image/update/testUserHasFullPermissionAndLastColumnHasNoDefaultGrantTokensAndLastColumnDataWillBeUpdatedOnUpdateAction.png" /> 
     *   
	 */
	public void testUpdateActionWhenUserHasDefalutGrantCRUD_LastColumnHasNoDefaultGrantCRUD_LastColumnDataWillBeUpdate(){
		//begin();
		
		printTag("\n[-------------Test Update Action When User Has Defalut Grant CRUD And Last Column Has No Default Grant CRUD And Last Column Data Will Be Updated--------------]");
		List<EntityInstance> sourceEntities        = generateSourceEntities();
		iecs.createEntitiesWithSecurity(user1, sourceEntities);
		sourceEntities                             = generateSourceEntities();
		
		
    	// Remove default grant tokens from last column
    	printTag("\n[Remove Default Grant Tokens From Column Phone]");
    	AttributeClass[] allColumns                = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	AttributeClass[] columns                   = {allColumns[allColumns.length-1]};
    	List<EntityInstance> grantTokens           = TokenFactory.getDefaultGrantTokens(iecs.getDatabaseConnection());
    	iecs.removeTokensFromColumns(root, TESTTABLE_TABLENAME.toLowerCase(), columns, grantTokens);
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	Describer.describeTable(iecs, Column_Security_Grant_Table.makeColumnSecurityGrantTableName());
    	printTag("\n[/Remove Default Grant Tokens From Column Phone]");
    	
		// Before Update
		printTag("\n[Before Update By User]");
		List<EntityInstance> willBeUpdatedEntities = getEntitiesFromDatabaseWithSecurity(CRUD.UPDATE);
		getEntitiesFromDatabase();
		
		// After Update
		printTag("\n[After Update Phone To 111111 By User]");
		for(EntityInstance willBeUpdatedEntity : willBeUpdatedEntities){
			willBeUpdatedEntity.set(TESTTABLE_COLUMN_PHONE, "111111");
		}
		ResultSecurity resultSecurity             = iecs.updateEntitiesWithSecurity(user1, willBeUpdatedEntities);
		List<EntityInstance> passedEntities       = getEntitiesFromResultSecurity(resultSecurity, true);
		List<EntityInstance> failedEntities       = getEntitiesFromResultSecurity(resultSecurity, false);
		List<EntityInstance> testTableEntitiesAFU = getEntitiesFromDatabase();

		// Test assertion
		Assert.assertTrue(passedEntities.isEmpty());
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), failedEntities, sourceEntities));	
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), testTableEntitiesAFU, sourceEntities));
    	
		// Clean up
    	// Roll Back : Remove default grant tokens from last column
    	printTag("\n[Roll Back : Remove Default Grant Tokens From Column Phone]");
    	allColumns                       = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	AttributeClass[] columnsRollBack = {allColumns[allColumns.length-1]};
    	grantTokens                      = TokenFactory.getDefaultGrantTokens(iecs.getDatabaseConnection());
    	iecs.addTokensToColumns(root, TESTTABLE_TABLENAME.toLowerCase(), columnsRollBack, grantTokens);
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	Describer.describeTable(iecs, Column_Security_Grant_Table.makeColumnSecurityGrantTableName());
    	printTag("\n[/Roll Back : Remove Default Grant Tokens From Column Phone]");
		id += sourceEntities.size();
	//	end();
    }
	
	/** 
	 * 
	 * <br/>
	 *  User : DEFAULT$*$+  GENERAL$Read$- <br/>
     *  <img src="../../resources/image/update/testUserHasGeneralDenyReadTokenAndLastColumnHasGeneralDenyReadTokenAndLastColumnDataWillBeUpdatedOnUpdateAction.png" /> 
     *   
	 */
    public void testUpdateActionWhenUserHasGeneralDenyR_LastColumnHasGeneralDenyR_LastColumnDataWillBeUpdated(){
    	//begin();
    	
		printTag("\n[-------------Test Update Action When User Has General Deny R And Last Column Has General Deny R And Last Column Data Will Be Updated--------------]");
		List<EntityInstance> sourceEntities       = generateSourceEntities();
		iecs.createEntitiesWithSecurity(user1, sourceEntities);
		sourceEntities                            = generateSourceEntities();
		
		
		// Add general deny read tokens to user
    	printTag("\n[Add General Deny Read Token To User]");
    	LinkedList<EntityInstance> generalDenyReadToken = new LinkedList<EntityInstance>();
    	generalDenyReadToken.add(getTokens("general", CRUD.READ, false));	
    	iecs.addTokensToUser(root, user1, generalDenyReadToken);
    	Describer.describeTable(iecs, User_Token_Table.makeUserTokenTableName());
    	printTag("\n[/Add General Deny Read Token To User]");
    	
    	// Add general deny read tokens to last column
    	printTag("\n[Add General Deny Read Token To Column Phone]");
    	AttributeClass[] allColumns             = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	AttributeClass[] columns                = {allColumns[allColumns.length-1]};
    	iecs.addTokensToColumns(root, TESTTABLE_TABLENAME.toLowerCase(), columns, generalDenyReadToken);
    	Describer.describeTable(iecs, Column_Security_Deny_Table.makeColumnSecurityDenyTableName());
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	printTag("\n[/Add General Deny Read Token To Column Phone]");
    		
		// Before Update
		printTag("\n[Before Update By User]");
		List<EntityInstance> willBeUpdatedEntities = getEntitiesFromDatabaseWithSecurity(CRUD.UPDATE);
		getEntitiesFromDatabase();
		
		// After Update
		printTag("\n[After Update Phone To 111111 By User]");
		for(EntityInstance willBeUpdatedEntity : willBeUpdatedEntities){
			willBeUpdatedEntity.set(TESTTABLE_COLUMN_PHONE, "111111");
		}
		ResultSecurity resultSecurity             = iecs.updateEntitiesWithSecurity(user1, willBeUpdatedEntities);
		List<EntityInstance> passedEntities       = getEntitiesFromResultSecurity(resultSecurity, true);
		List<EntityInstance> failedEntities       = getEntitiesFromResultSecurity(resultSecurity, false);
		List<EntityInstance> testTableEntitiesAFU = getEntitiesFromDatabase();

		// Test assertion
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), passedEntities, testTableEntitiesAFU));	
		Assert.assertTrue(failedEntities.isEmpty());
    	
		// Clean up
		// Roll Back : Add general deny read tokens to user
    	printTag("\n[Roll Back : Add General Deny Read Token To User]");
    	generalDenyReadToken = new LinkedList<EntityInstance>();
    	generalDenyReadToken.add(getTokens("general", CRUD.READ, false));	
    	iecs.removeTokensFromUser(root, user1, generalDenyReadToken);
    	Describer.describeTable(iecs, User_Token_Table.makeUserTokenTableName());
    	printTag("\n[/Roll Back : Add General Deny Read Token To User]");
    	
    	// Roll Back : Add general deny read tokens to last column
    	printTag("\n[Roll Back : Add General Deny Read Token To Column Phone]");
    	allColumns                       = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	AttributeClass[] columnsRollBack = {allColumns[allColumns.length-1]};
    	iecs.removeTokensFromColumns(root, TESTTABLE_TABLENAME.toLowerCase(), columnsRollBack, generalDenyReadToken);
    	Describer.describeTable(iecs, Column_Security_Deny_Table.makeColumnSecurityDenyTableName());
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	printTag("\n[/Roll Back : Add General Deny Read Token To Column Phone]");
		id += sourceEntities.size();
    	//end();
    }
    
	/** 
	 * 
	 * <br/>
	 *  User : DEFAULT$*$+  GENERAL$Read$- GENERAL$Update$-<br/>
     *  <img src="../../resources/image/update/testUserHasGeneralDenyReadUpdateTokenAndLastColumnHasGeneralDenyReadUpdateTokenAndLastColumnDataWillBeUpdatedOnUpdateAction.png" /> 
     *   
	 */
    public void testUpdateActionWhenUserHasGeneralDenyRU_LastColumnHasGeneralDenyRU_LastColumnDataWillBeUpdated(){
    	//begin();
    	
    	printTag("\n[-------------Test Update Action When User Has General Deny RU And Last Column Has General Deny RU And Last Column Data Will Be Updated--------------]");
		List<EntityInstance> sourceEntities       = generateSourceEntities();
		iecs.createEntitiesWithSecurity(user1, sourceEntities);
		sourceEntities                            = generateSourceEntities();
		
		
		// Add general deny read tokens to user
    	printTag("\n[Add General Deny Read Token To User]");
    	LinkedList<EntityInstance> generalDenyReadToken = new LinkedList<EntityInstance>();
    	generalDenyReadToken.add(getTokens("general", CRUD.READ, false));	
    	generalDenyReadToken.add(getTokens("general", CRUD.UPDATE, false));	
    	iecs.addTokensToUser(root, user1, generalDenyReadToken);
    	Describer.describeTable(iecs, User_Token_Table.makeUserTokenTableName());
    	printTag("\n[/Add General Deny Read Token To User]");
    	
    	// Add general deny read tokens to last column
    	printTag("\n[Add General Deny Read Token To Column Phone]");
    	AttributeClass[] allColumns             = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	AttributeClass[] columns                = {allColumns[allColumns.length-1]};
    	iecs.addTokensToColumns(root, TESTTABLE_TABLENAME.toLowerCase(), columns, generalDenyReadToken);
    	Describer.describeTable(iecs, User_Token_Table.makeUserTokenTableName());
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	printTag("\n[/Add General Deny Read Token To Column Phone]");
    		
		// Before Update
		printTag("\n[Before Update By User]");
		List<EntityInstance> willBeUpdatedEntities = getEntitiesFromDatabaseWithSecurity(CRUD.UPDATE);
		getEntitiesFromDatabase();
		
		// After Update
		printTag("\n[After Update Phone To 111111 By User]");
		for(EntityInstance willBeUpdatedEntity : willBeUpdatedEntities){
			willBeUpdatedEntity.set(TESTTABLE_COLUMN_PHONE, "111111");
		}
		ResultSecurity resultSecurity             = iecs.updateEntitiesWithSecurity(user1, willBeUpdatedEntities);
		List<EntityInstance> passedEntities       = getEntitiesFromResultSecurity(resultSecurity, true);
		List<EntityInstance> failedEntities       = getEntitiesFromResultSecurity(resultSecurity, false);
		List<EntityInstance> testTableEntitiesAFU = getEntitiesFromDatabase();

		// Test assertion
		Assert.assertTrue(passedEntities.isEmpty());
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), failedEntities, testTableEntitiesAFU));	
    	
		// Roll Back : Add general deny read tokens to user
    	printTag("\n[ Roll Back : Add General Deny Read Token To User]");
    	generalDenyReadToken = new LinkedList<EntityInstance>();
    	generalDenyReadToken.add(getTokens("general", CRUD.READ, false));	
    	generalDenyReadToken.add(getTokens("general", CRUD.UPDATE, false));	
    	iecs.removeTokensFromUser(root, user1, generalDenyReadToken);
    	Describer.describeTable(iecs, User_Token_Table.makeUserTokenTableName());
    	printTag("\n[/ Roll Back : Add General Deny Read Token To User]");
    	
    	// Roll Back : Add general deny read tokens to last column
    	printTag("\n[ Roll Back : Add General Deny Read Token To Column Phone]");
    	allColumns                       = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	AttributeClass[] columnsRollBack = {allColumns[allColumns.length-1]};
    	iecs.removeTokensFromColumns(root, TESTTABLE_TABLENAME.toLowerCase(), columnsRollBack, generalDenyReadToken);
    	Describer.describeTable(iecs, User_Token_Table.makeUserTokenTableName());
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	printTag("\n[/ Roll Back : Add General Deny Read Token To Column Phone]");
    	
		id += sourceEntities.size();
    	//end();
    }
    
	/** 
	 * 
	 * <br/>
	 *  User : DEFAULT$*$+ <br/>
     *  <img src="../../resources/image/update/testUserHasFullPermissionAndFirstRowHasNoDefaultGrantUpdateTokensOnUpdateAction.png" /> 
     *   
	 */
    public void testUpdateActionWhenUserHasDefalutGrantCRUD_FirstRowHasDefaultGrantCRD(){
    	//begin();
    	
    	printTag("\n[-------------Test Update Action When User Has Defalut Grant CRUD And First Row Has Default Grant CRD--------------]");
		List<EntityInstance> sourceEntities        = generateSourceEntities();
		iecs.createEntitiesWithSecurity(user1, sourceEntities);
		sourceEntities                             = generateSourceEntities();
		
		
		// Remove default grant tokens from first row
    	printTag("\n[Remove Default Grant Update Tokens From First Row]");
    	List<EntityInstance> grantUpdateTokens     = new LinkedList<EntityInstance>();
    	grantUpdateTokens.add(TokenFactory.DefaultTokens.getTOKEN_GRANTGROUP$U$G(iecs.getDatabaseConnection()));
    	List<EntityInstance> rows                  = new LinkedList<EntityInstance>();
    	rows.add(sourceEntities.get(0));
    	iecs.removeTokensFromRows(root, rows, grantUpdateTokens);
    	Describer.describeTable(iecs, Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(TESTTABLE_TABLENAME));
    	Describer.describeTable(iecs, Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME));
    	printTag("\n[/Remove Default Grant Update Tokens From First Row]");
    		
		// Before Update
		printTag("\n[Before Update By User]");
		List<EntityInstance> willBeUpdatedEntities = getEntitiesFromDatabaseWithSecurity(CRUD.UPDATE);
		getEntitiesFromDatabase();
		
		// After Update
		printTag("\n[After Update Phone To 111111 By User]");
		for(EntityInstance willBeUpdatedEntity : willBeUpdatedEntities){
			willBeUpdatedEntity.set(TESTTABLE_COLUMN_PHONE, "111111");
		}
		ResultSecurity resultSecurity             = iecs.updateEntitiesWithSecurity(user1, willBeUpdatedEntities);
		List<EntityInstance> passedEntities       = getEntitiesFromResultSecurity(resultSecurity, true);
		List<EntityInstance> failedEntities       = getEntitiesFromResultSecurity(resultSecurity, false);
		List<EntityInstance> testTableEntitiesAFU = getEntitiesFromDatabase();

		// Test assertion
		Assert.assertTrue(passedEntities.size() == 1);
		Assert.assertFalse(TestAssist.compare(iecs.getDatabaseConnection(), passedEntities.get(0), testTableEntitiesAFU.get(1)));
		Assert.assertTrue(failedEntities.size() == 1);
		Assert.assertFalse(TestAssist.compare(iecs.getDatabaseConnection(), failedEntities.get(0), testTableEntitiesAFU.get(0)));
    	
		// Clean up
		// Roll Back : Remove default grant tokens from first row
    	printTag("\n[Roll Back : Remove Default Grant Update Tokens From First Row]");
    	grantUpdateTokens     = new LinkedList<EntityInstance>();
    	grantUpdateTokens.add(TokenFactory.DefaultTokens.getTOKEN_GRANTGROUP$U$G(iecs.getDatabaseConnection()));
    	rows                  = new LinkedList<EntityInstance>();
    	rows.add(sourceEntities.get(0));
    	iecs.addTokensToRows(root, rows, grantUpdateTokens);
    	Describer.describeTable(iecs, Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(TESTTABLE_TABLENAME));
    	Describer.describeTable(iecs, Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME));
    	printTag("\n[/Roll Back : Remove Default Grant Update Tokens From First Row]");
    	
    	id+=sourceEntities.size();
    //	end();
    }
    
	/** 
	 * 
	 * <br/>
	 *  User : DEFAULT$*$+ GENERAL$UPDATE$-<br/>
     *  <img src="../../resources/image/update/testUserHasGeneralDenyUpdateTokenAndFirstRowHasGeneralDenyUpdateTokensOnUpdateAction.png" /> 
     *   
	 */
    public void testUpdateActionWhenUserHasGeneralDenyU_FirstRowHasGeneralDenyU(){
    	//begin();
    	
    	printTag("\n[-------------Test Update Action When User Has General Deny U And First Row Has General Deny U--------------]");
		List<EntityInstance> sourceEntities         = generateSourceEntities();
		iecs.createEntitiesWithSecurity(user1, sourceEntities);
		sourceEntities                              = generateSourceEntities();
		
		
    	// Add general deny token to the first row
    	printTag("\n[Add General Deny Token To The First Row]");
		List<EntityInstance> generalDenyUpdateToken = new LinkedList<EntityInstance>();
		generalDenyUpdateToken.add(getTokens("general", CRUD.UPDATE, false));
    	List<EntityInstance> rows               = new LinkedList<EntityInstance>();
    	rows.add(sourceEntities.get(0));
		iecs.addTokensToRows(root, rows, generalDenyUpdateToken);
		Describer.describeTable(iecs, Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME));
    	Describer.describeTable(iecs, Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME));
		printTag("\n[/Add General Deny Token To The First Row]");
		
    	// Add general deny token  tokens to user
    	printTag("\n[Add General Deny Token  To User]");
    	iecs.addTokensToUser(root, user1, generalDenyUpdateToken);
    	Describer.describeTable(iecs, User_Token_Table.makeUserTokenTableName());
    	printTag("\n[/Add General Deny Token  To User]");
    	
    	// Before Update
		printTag("\n[Before Update By User]");
		List<EntityInstance> willBeUpdatedEntities = getEntitiesFromDatabaseWithSecurity(CRUD.UPDATE);
		getEntitiesFromDatabase();
		
		// After Update
		printTag("\n[After Update Phone To 111111 By User]");
		for(EntityInstance willBeUpdatedEntity : willBeUpdatedEntities){
			willBeUpdatedEntity.set(TESTTABLE_COLUMN_PHONE, "111111");
		}
		ResultSecurity resultSecurity             = iecs.updateEntitiesWithSecurity(user1, willBeUpdatedEntities);
		List<EntityInstance> passedEntities       = getEntitiesFromResultSecurity(resultSecurity, true);
		List<EntityInstance> failedEntities       = getEntitiesFromResultSecurity(resultSecurity, false);
		List<EntityInstance> testTableEntitiesAFU = getEntitiesFromDatabase();
		
		// Test assertion
		Assert.assertTrue(passedEntities.size() == 1);
		Assert.assertFalse(TestAssist.compare(iecs.getDatabaseConnection(), passedEntities.get(0), testTableEntitiesAFU.get(1)));
		Assert.assertTrue(failedEntities.size() == 1);
		Assert.assertFalse(TestAssist.compare(iecs.getDatabaseConnection(), failedEntities.get(0), testTableEntitiesAFU.get(0)));
    	
		// Clean up
    	// Roll Back : Add general deny token to the first row
    	printTag("\n[Roll Back : Add General Deny Token To The First Row]");
		generalDenyUpdateToken = new LinkedList<EntityInstance>();
		generalDenyUpdateToken.add(getTokens("general", CRUD.UPDATE, false));
    	rows                   = new LinkedList<EntityInstance>();
    	rows.add(sourceEntities.get(0));
		iecs.removeTokensFromRows(root, rows, generalDenyUpdateToken);
		Describer.describeTable(iecs, Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME));
    	Describer.describeTable(iecs, Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME));
		printTag("\n[/Roll Back : Add General Deny Token To The First Row]");
		
    	// Roll Back : Add general deny token  tokens to user
    	printTag("\n[Roll Back : Add General Deny Token  To User]");
    	iecs.removeTokensFromUser(root, user1, generalDenyUpdateToken);
    	Describer.describeTable(iecs, User_Token_Table.makeUserTokenTableName());
    	printTag("\n[/Roll Back : Add General Deny Token  To User]");
    	
		id += sourceEntities.size();
    	//end();
    }
    
	/** 
	 * 
	 * <br/>
	 *  User : DEFAULT$*$+ <br/>
     *  <img src="../../resources/image/update/testUserHasFullPermissionAndLastColumnHasNoDefaultGrantTokensAndFirstRowHasNoDefaultGrantUpdateTokensAndLastColumnDataWillNotBeUpdatedOnUpdateAction.png" /> 
     *   
	 */
    public void testUpdateActionWhenUserHasDefalutGrantCRUD_LastColumnHasNoDefaultGrantCRUD_FirstRowHasDefaultGrantCRD_LastColumnDataWillNotBeUpdated(){
    	//begin();
    	
    	printTag("\n[-------------Test User Has General Deny Update Token And Last Column Has No Default Grant Tokens And First Row Has No Default Update Token And Last Column Data Will Not Be Updated On Update Action--------------]");
		List<EntityInstance> sourceEntities         = generateSourceEntities();
		iecs.createEntitiesWithSecurity(user1, sourceEntities);
		sourceEntities                              = generateSourceEntities();
		
		
    	// Remove default grant tokens from last column
    	printTag("\n[Remove Default Grant Tokens From Column Phone]");
    	AttributeClass[] allColumns                = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	AttributeClass[] columns                   = {allColumns[allColumns.length-1]};
    	List<EntityInstance> grantTokens           = TokenFactory.getDefaultGrantTokens(iecs.getDatabaseConnection());
    	iecs.removeTokensFromColumns(root, TESTTABLE_TABLENAME.toLowerCase(), columns, grantTokens);
    	Describer.describeTable(iecs, Column_Security_Grant_Table.makeColumnSecurityGrantTableName());
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	printTag("\n[/Remove Default Grant Tokens From Column Phone]");
		
		// Remove default grant tokens from first row
    	printTag("\n[Remove Default Grant Update Tokens From First Row]");
    	List<EntityInstance> grantUpdateTokens     = new LinkedList<EntityInstance>();
    	grantUpdateTokens.add(TokenFactory.DefaultTokens.getTOKEN_GRANTGROUP$U$G(iecs.getDatabaseConnection()));
    	List<EntityInstance> rows                  = new LinkedList<EntityInstance>();
    	rows.add(sourceEntities.get(0));
    	iecs.removeTokensFromRows(root, rows, grantUpdateTokens);
    	Describer.describeTable(iecs, Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(TESTTABLE_TABLENAME));
    	Describer.describeTable(iecs, Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME));
    	printTag("\n[/Remove Default Grant Update Tokens From First Row]");
    
    	// Before Update
		printTag("\n[Before Update By User]");
		List<EntityInstance> willBeUpdatedEntities = getEntitiesFromDatabaseWithSecurity(CRUD.UPDATE);
		getEntitiesFromDatabase();
		
		// After Update
		printTag("\n[After Update Name To SuperX By User]");
		for(EntityInstance willBeUpdatedEntity : willBeUpdatedEntities){
			willBeUpdatedEntity.set(TESTTABLE_COLUMN_NAME, "SuperX");
		}
		ResultSecurity resultSecurity             = iecs.updateEntitiesWithSecurity(user1, willBeUpdatedEntities);
		List<EntityInstance> passedEntities       = getEntitiesFromResultSecurity(resultSecurity, true);
		List<EntityInstance> failedEntities       = getEntitiesFromResultSecurity(resultSecurity, false);
		List<EntityInstance> testTableEntitiesAFU = getEntitiesFromDatabase();
		
		// Test assertion
		Assert.assertTrue(passedEntities.size() == 1);
		Assert.assertFalse(TestAssist.compare(iecs.getDatabaseConnection(), passedEntities.get(0), testTableEntitiesAFU.get(1)));
		Assert.assertTrue(failedEntities.size() == 1);
		Assert.assertFalse(TestAssist.compare(iecs.getDatabaseConnection(), failedEntities.get(0), testTableEntitiesAFU.get(0)));
    	
		// CLean up
		// Roll Back : Remove default grant tokens from last column
    	printTag("\n[Roll Back : Remove Default Grant Tokens From Column Phone]");
    	allColumns                       = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	AttributeClass[] columnsRollBack = {allColumns[allColumns.length-1]};
    	grantTokens                      = TokenFactory.getDefaultGrantTokens(iecs.getDatabaseConnection());
    	iecs.addTokensToColumns(root, TESTTABLE_TABLENAME.toLowerCase(), columnsRollBack, grantTokens);
    	Describer.describeTable(iecs, Column_Security_Grant_Table.makeColumnSecurityGrantTableName());
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	printTag("\n[/Roll Back : Remove Default Grant Tokens From Column Phone]");
		
		// Roll Back : Remove default grant tokens from first row
    	printTag("\n[Roll Back : Remove Default Grant Update Tokens From First Row]");
    	grantUpdateTokens     = new LinkedList<EntityInstance>();
    	grantUpdateTokens.add(TokenFactory.DefaultTokens.getTOKEN_GRANTGROUP$U$G(iecs.getDatabaseConnection()));
    	rows                  = new LinkedList<EntityInstance>();
    	rows.add(sourceEntities.get(0));
    	iecs.addTokensToRows(root, rows, grantUpdateTokens);
    	Describer.describeTable(iecs, Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(TESTTABLE_TABLENAME));
    	Describer.describeTable(iecs, Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME));
    	printTag("\n[/Roll Back : Remove Default Grant Update Tokens From First Row]");
		id += sourceEntities.size();
    	//end();
    	
    }
    
	/** 
	 * 
	 * <br/>
	 *  User : DEFAULT$*$+ <br/>
     *  <img src="../../resources/image/update/testUserHasFullPermissionAndLastColumnHasNoDefaultGrantTokensAndFirstRowHasNoDefaultGrantUpdateTokensAndLastColumnDataWillBeUpdatedOnUpdateAction.png" /> 
     *   
	 */
    public void testUpdateActionWhenUserHasDefalutGrantCRUD_LastColumnHasNoDefaultGrantCRUD_FirstRowHasDefaultGrantCRD_LastColumnDataWillBeUpdated(){
    	//begin();
    	
    	printTag("\n[-------------Test Update Action When User Has Defalut Grant CRUD And Last Column Has No Default Grant CRUD And First Row Has Default Grant CRD And Last Column Data Will Be Updated--------------]");
		List<EntityInstance> sourceEntities         = generateSourceEntities();
		iecs.createEntitiesWithSecurity(user1, sourceEntities);
		sourceEntities                              = generateSourceEntities();
		
		
    	// Remove default grant tokens from last column
    	printTag("\n[Remove Default Grant Tokens From Column Phone]");
    	AttributeClass[] allColumns                = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	AttributeClass[] columns                   = {allColumns[allColumns.length-1]};
    	List<EntityInstance> grantTokens           = TokenFactory.getDefaultGrantTokens(iecs.getDatabaseConnection());
    	iecs.removeTokensFromColumns(root, TESTTABLE_TABLENAME.toLowerCase(), columns, grantTokens);
    	Describer.describeTable(iecs, Column_Security_Grant_Table.makeColumnSecurityGrantTableName());
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	printTag("\n[/Remove Default Grant Tokens From Column Phone]");
		
		// Remove default grant tokens from first row
    	printTag("\n[Remove Default Grant Update Tokens From First Row]");
    	List<EntityInstance> grantUpdateTokens     = new LinkedList<EntityInstance>();
    	grantUpdateTokens.add(TokenFactory.DefaultTokens.getTOKEN_GRANTGROUP$U$G(iecs.getDatabaseConnection()));
    	List<EntityInstance> rows                  = new LinkedList<EntityInstance>();
    	rows.add(sourceEntities.get(0));
    	iecs.removeTokensFromRows(root, rows, grantUpdateTokens);
    	Describer.describeTable(iecs, Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(TESTTABLE_TABLENAME));
    	Describer.describeTable(iecs, Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME));
    	printTag("\n[/Remove Default Grant Update Tokens From First Row]");
    
    	// Before Update
		printTag("\n[Before Update By User]");
		List<EntityInstance> willBeUpdatedEntities = getEntitiesFromDatabaseWithSecurity(CRUD.UPDATE);
		getEntitiesFromDatabase();
		
		// After Update
		printTag("\n[After Update Name To SuperX By User]");
		for(EntityInstance willBeUpdatedEntity : willBeUpdatedEntities){
			willBeUpdatedEntity.set(TESTTABLE_COLUMN_PHONE, "111111");
		}
		ResultSecurity resultSecurity             = iecs.updateEntitiesWithSecurity(user1, willBeUpdatedEntities);
		List<EntityInstance> passedEntities       = getEntitiesFromResultSecurity(resultSecurity, true);
		List<EntityInstance> failedEntities       = getEntitiesFromResultSecurity(resultSecurity, false);
		List<EntityInstance> testTableEntitiesAFU = getEntitiesFromDatabase();
		
		// Test assertion
		Assert.assertTrue(passedEntities.size() == 0);
		Assert.assertTrue(failedEntities.size() == 2);    	
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), testTableEntitiesAFU, sourceEntities));
    	
		// Clean up
		// Roll Back : Remove default grant tokens from last column
    	printTag("\n[Roll Back : Remove Default Grant Tokens From Column Phone]");
    	allColumns                       = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	AttributeClass[] columnsRollBack = {allColumns[allColumns.length-1]};
    	grantTokens                      = TokenFactory.getDefaultGrantTokens(iecs.getDatabaseConnection());
    	iecs.addTokensToColumns(root, TESTTABLE_TABLENAME.toLowerCase(), columnsRollBack, grantTokens);
    	Describer.describeTable(iecs, Column_Security_Grant_Table.makeColumnSecurityGrantTableName());
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	printTag("\n[/Roll Back : Remove Default Grant Tokens From Column Phone]");
		
		// Roll Back : Remove default grant tokens from first row
    	printTag("\n[Roll Back : Remove Default Grant Update Tokens From First Row]");
    	grantUpdateTokens     = new LinkedList<EntityInstance>();
    	grantUpdateTokens.add(TokenFactory.DefaultTokens.getTOKEN_GRANTGROUP$U$G(iecs.getDatabaseConnection()));
    	rows                  = new LinkedList<EntityInstance>();
    	rows.add(sourceEntities.get(0));
    	iecs.addTokensToRows(root, rows, grantUpdateTokens);
    	Describer.describeTable(iecs, Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(TESTTABLE_TABLENAME));
    	Describer.describeTable(iecs, Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME));
    	printTag("\n[/Roll Back : Remove Default Grant Update Tokens From First Row]");
		
		id += sourceEntities.size();
		//end();
    	
    }
    
	/** 
	 * 
	 * <br/>
	 *  User : DEFAULT$*$+ GENERAL$READ$- GENERAL$UPDATE$-<br/>
     *  <img src="../../resources/image/update/testUserHasGeneralDenyReadUpdateTokenAndLastColumnHasGeneralDenyReadTokenAndFirstRowHasGeneralDenyUpdateTokenAndLastColumnDataWillBeUpdatedOnUpdateAction.png" /> 
     *   
	 */
    public void testUpdateActionWhenUserHasGeneralDenyRU_LastColumnHasGeneralDenyR_FirstRowHasGeneralDenyU_LastColumnDataWillBeUpdated(){
    	//begin();
    	
    	printTag("\n[-------------Test Update Action When User Has General Deny RU And Last Column Has General Deny R And First Row Has General Deny U And Last Column Data Will Be Updated--------------]");
		List<EntityInstance> sourceEntities         = generateSourceEntities();
		iecs.createEntitiesWithSecurity(user1, sourceEntities);
		sourceEntities                              = generateSourceEntities();
		
		
    	// Add general deny update, read tokens to user
    	printTag("\n[Add General Deny Update, Read Tokens To User]");
    	LinkedList<EntityInstance> generalDenyReadToken = new LinkedList<EntityInstance>();
    	LinkedList<EntityInstance> generalDenyUpdateToken = new LinkedList<EntityInstance>();
    	generalDenyReadToken.add(getTokens("general", CRUD.READ, false));	
    	generalDenyUpdateToken.add(getTokens("general", CRUD.UPDATE, false));
    	iecs.addTokensToUser(root, user1, generalDenyReadToken);
    	iecs.addTokensToUser(root, user1, generalDenyUpdateToken);
    	Describer.describeTable(iecs, User_Token_Table.makeUserTokenTableName());
    	printTag("\n[/Add General Deny Update, Read Tokens To User]");
    	
    	// Add general deny read tokens to last column
    	printTag("\n[Add General Deny Read Token To Column Phone]");
    	AttributeClass[] allColumns             = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	AttributeClass[] columns                = {allColumns[allColumns.length-1]};
    	iecs.addTokensToColumns(root, TESTTABLE_TABLENAME.toLowerCase(), columns, generalDenyReadToken);
    	Describer.describeTable(iecs, User_Token_Table.makeUserTokenTableName());
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	printTag("\n[/Add General Deny Read Token To Column Phone]");

    	
    	// Add general deny update token to the first row
    	printTag("\n[Add General Deny Update Token To The First Row]");
    	List<EntityInstance> rows               = new LinkedList<EntityInstance>();
    	rows.add(sourceEntities.get(0));
		iecs.addTokensToRows(root, rows, generalDenyUpdateToken);
		Describer.describeTable(iecs, Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME));
    	Describer.describeTable(iecs, Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME));
    	printTag("\n[/Add General Deny Update Token To The First Row]");
		
		// Before Update
		printTag("\n[Before Update By User]");
		List<EntityInstance> willBeUpdatedEntities = getEntitiesFromDatabaseWithSecurity(CRUD.UPDATE);
		getEntitiesFromDatabase();
		
		// After Update
		printTag("\n[After Update Name To SuperX By User]");
		for(EntityInstance willBeUpdatedEntity : willBeUpdatedEntities){
			willBeUpdatedEntity.set(TESTTABLE_COLUMN_PHONE, "111111");
		}
		ResultSecurity resultSecurity             = iecs.updateEntitiesWithSecurity(user1, willBeUpdatedEntities);
		List<EntityInstance> passedEntities       = getEntitiesFromResultSecurity(resultSecurity, true);
		List<EntityInstance> failedEntities       = getEntitiesFromResultSecurity(resultSecurity, false);
		List<EntityInstance> testTableEntitiesAFU = getEntitiesFromDatabase();
		
		// Test assertion
		Assert.assertTrue(passedEntities.size() == 1);
		Assert.assertTrue(failedEntities.size() == 1);
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), testTableEntitiesAFU, sourceEntities));
		Assert.assertTrue(passedEntities.get(0).get(TESTTABLE_COLUMN_PHONE) instanceof UnreadableValue);
		Assert.assertTrue(failedEntities.get(0).get(TESTTABLE_COLUMN_PHONE) instanceof UnreadableValue);
    	
		// Clean up
		// Roll Back : Add general deny update, read tokens to user
    	printTag("\n[Roll Back : Add General Deny Update, Read Tokens To User]");
    	generalDenyReadToken   = new LinkedList<EntityInstance>();
    	generalDenyUpdateToken = new LinkedList<EntityInstance>();
    	generalDenyReadToken.add(getTokens("general", CRUD.READ, false));	
    	generalDenyUpdateToken.add(getTokens("general", CRUD.UPDATE, false));
    	iecs.removeTokensFromUser(root, user1, generalDenyReadToken);
    	iecs.removeTokensFromUser(root, user1, generalDenyUpdateToken);
    	Describer.describeTable(iecs, User_Token_Table.makeUserTokenTableName());
    	printTag("\n[/Roll Back : Add General Deny Update, Read Tokens To User]");
    	
    	// Roll Back : Add general deny read tokens to last column
    	printTag("\n[Roll Back : Add General Deny Read Token To Column Phone]");
    	allColumns                       = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	AttributeClass[] columnsRollBack = {allColumns[allColumns.length-1]};
    	iecs.removeTokensFromColumns(root, TESTTABLE_TABLENAME.toLowerCase(), columnsRollBack, generalDenyReadToken);
    	Describer.describeTable(iecs, User_Token_Table.makeUserTokenTableName());
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	printTag("\n[/Roll Back : Add General Deny Read Token To Column Phone]");
    	
    	// Roll Back : Add general deny update token to the first row
    	printTag("\n[Roll Back : Add General Deny Update Token To The First Row]");
    	rows               = new LinkedList<EntityInstance>();
    	rows.add(sourceEntities.get(0));
		iecs.removeTokensFromRows(root, rows, generalDenyUpdateToken);
		Describer.describeTable(iecs, Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME));
    	Describer.describeTable(iecs, Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME));
    	printTag("\n[/Roll Back : Add General Deny Update Token To The First Row]");
		
    	id += sourceEntities.size();
    	//end();
    	
    }
    
    
  
	/** 
	 * 
	 * <br/>
	 *  User : DEFAULT$*$+ <br/>
     *  <img src="../../resources/image/delete/testUserHasFullPermissionOnDeleteAction.png" /> 
     *   
	 */
    public void testDeleteActionWhenUserHasDefalutGrantCRUD(){
    	//begin();
    	
    	printTag("\n[--------------Test Delete Action When User Has Defalut Grant CRUD--------------]");	
    	List<EntityInstance> sourceEntities                = generateSourceEntities();
    	iecs.createEntitiesWithSecurity(user1, sourceEntities);
    	sourceEntities                                     = generateSourceEntities();
			
    	// Before delete by user
    	printTag("\n[Before Delete By User]");
		List<EntityInstance> willBeDeletedEntities         = getEntitiesFromDatabaseWithSecurity(CRUD.DELETE);
		getEntitiesFromDatabase();
		getRowDenySecurityTableContentFromDatabase();
		getRowGrantSecurityTableContentFromDatabase();
		getMappingTableContentFromDatabase();
		
		// After delete by user
		printTag("\n[After Delete By User]");	
		ResultSecurity resultSecurity                         = iecs.deleteEntitiesWithSecurity(user1, willBeDeletedEntities);
		List<EntityInstance> passedEntities                   = getEntitiesFromResultSecurity(resultSecurity, true);
		List<EntityInstance> failedEntities                   = getEntitiesFromResultSecurity(resultSecurity, false);
		List<EntityInstance> testTableEntitiesAFD             = getEntitiesFromDatabase();
		List<EntityInstance> rowDenySecurityTableEntitiesAFD  = getRowDenySecurityTableContentFromDatabase();
		List<EntityInstance> rowGrantSecurityTableEntitiesAFD = getRowGrantSecurityTableContentFromDatabase();
		List<EntityInstance> mappingTableEntitiesAFD          = getMappingTableContentFromDatabase();
		
		// Test assertion
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), passedEntities, sourceEntities));
		Assert.assertTrue(failedEntities.isEmpty());
		Assert.assertTrue(testTableEntitiesAFD.isEmpty());
		Assert.assertTrue(rowDenySecurityTableEntitiesAFD.isEmpty());
		Assert.assertTrue(rowGrantSecurityTableEntitiesAFD.isEmpty());
		Assert.assertTrue(mappingTableEntitiesAFD.isEmpty());
    	
		id += sourceEntities.size();
    	//end();

    }
    
	/** 
	 * 
	 * <br/>
	 *  User : DEFAULT$*$+ <br/>
     *  <img src="../../resources/image/delete/testUserHasFullPermissionAndLastColumnHasNoDefaultGrantTokensOnDeleteAction.png" /> 
     *   
	 */
    public void testDeleteActionWhenUserHasDefalutGrantCRUD_LastColumnHasNoDefaultGrantCRUD(){
    	//begin();
    	
    	printTag("\n[--------------Test Delete Action When User Has Defalut Grant CRUD And Last Column Has No Default Grant CRUD--------------]");	
    	List<EntityInstance> sourceEntities                = generateSourceEntities();
    	iecs.createEntitiesWithSecurity(user1, sourceEntities);
    	sourceEntities                                     = generateSourceEntities();
		
    	// Remove default grant tokens from last column
    	printTag("\n[Remove Default Grant Tokens From Column Phone]");
    	AttributeClass[] allColumns                = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	AttributeClass[] columns                   = {allColumns[allColumns.length-1]};
    	List<EntityInstance> grantTokens           = TokenFactory.getDefaultGrantTokens(iecs.getDatabaseConnection());
    	iecs.removeTokensFromColumns(root, TESTTABLE_TABLENAME.toLowerCase(), columns, grantTokens);
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	Describer.describeTable(iecs, Column_Security_Grant_Table.makeColumnSecurityGrantTableName());
    	printTag("\n[/Remove Default Grant Tokens From Column Phone]");
    	
    	// Before delete by user
    	printTag("\n[Before Delete By User]");
		List<EntityInstance> willBeDeletedEntities         = getEntitiesFromDatabaseWithSecurity(CRUD.DELETE);
		List<EntityInstance> testTableEntities             = getEntitiesFromDatabase();
		List<EntityInstance> rowDenySecurityTableEntities  = getRowDenySecurityTableContentFromDatabase();
		List<EntityInstance> rowGrantSecurityTableEntities = getRowGrantSecurityTableContentFromDatabase();
		List<EntityInstance> mappingTableEntities          = getMappingTableContentFromDatabase();
		
		// After delete by user
		printTag("\n[After Delete By User]");	
		ResultSecurity resultSecurity                         = iecs.deleteEntitiesWithSecurity(user1, willBeDeletedEntities);
		List<EntityInstance> passedEntities                   = getEntitiesFromResultSecurity(resultSecurity, true);
		List<EntityInstance> failedEntities                   = getEntitiesFromResultSecurity(resultSecurity, false);
		List<EntityInstance> testTableEntitiesAFD             = getEntitiesFromDatabase();
		List<EntityInstance> rowDenySecurityTableEntitiesAFD  = getRowDenySecurityTableContentFromDatabase();
		List<EntityInstance> rowGrantSecurityTableEntitiesAFD = getRowGrantSecurityTableContentFromDatabase();
		List<EntityInstance> mappingTableEntitiesAFD          = getMappingTableContentFromDatabase();
		
		// Test assertion
		Assert.assertTrue(passedEntities.isEmpty());
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), failedEntities, sourceEntities));
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), testTableEntities, testTableEntitiesAFD));
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), rowGrantSecurityTableEntities, rowGrantSecurityTableEntitiesAFD));
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), rowDenySecurityTableEntities, rowDenySecurityTableEntitiesAFD));
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), mappingTableEntities, mappingTableEntitiesAFD));
    	
		// Clean up
		// Roll Back : Remove default grant tokens from last column
    	printTag("\n[Roll Back : Remove Default Grant Tokens From Column Phone]");
    	allColumns                       = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	AttributeClass[] columnsRollBack = {allColumns[allColumns.length-1]};
    	grantTokens                      = TokenFactory.getDefaultGrantTokens(iecs.getDatabaseConnection());
    	iecs.addTokensToColumns(root, TESTTABLE_TABLENAME.toLowerCase(), columnsRollBack, grantTokens);
    	Describer.describeTable(iecs, Column_Security_Grant_Table.makeColumnSecurityGrantTableName());
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	printTag("\n[/Roll Back : Remove Default Grant Tokens From Column Phone]");
		id += sourceEntities.size();
    	//end();
    }
    
	/** 
	 * 
	 * <br/>
	 *  User : DEFAULT$*$+ GENERAL$READ$- <br/>
     *  <img src="../../resources/image/delete/testUserHasGeneralDenyReadTokenAndLastColumnHasGeneralDenyReadTokenOnDeleteAction.png" /> 
     *   
	 */
    public void testDeleteActionWhenUserHasGeneralDenyR_LastColumnHasGeneralDenyR(){
    	//begin();
    	
		printTag("\n[-------------Test Delete Action When User Has General Deny R And Last Column Has General Deny R--------------]");
		List<EntityInstance> sourceEntities       = generateSourceEntities();
		iecs.createEntitiesWithSecurity(user1, sourceEntities);
		sourceEntities                            = generateSourceEntities();
		
		
		// Add general deny read tokens to user
    	printTag("\n[Add General Deny Read Token To User]");
    	LinkedList<EntityInstance> generalDenyReadToken = new LinkedList<EntityInstance>();
    	generalDenyReadToken.add(getTokens("general", CRUD.READ, false));	
    	iecs.addTokensToUser(root, user1, generalDenyReadToken);
    	Describer.describeTable(iecs, User_Token_Table.makeUserTokenTableName());
    	printTag("\n[/Add General Deny Read Token To User]");
    	
    	// Add general deny read tokens to last column
    	printTag("\n[Add General Deny Read Token To Column Phone]");
    	AttributeClass[] allColumns             = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	AttributeClass[] columns                = {allColumns[allColumns.length-1]};
    	iecs.addTokensToColumns(root, TESTTABLE_TABLENAME.toLowerCase(), columns, generalDenyReadToken);
    	Describer.describeTable(iecs, Column_Security_Deny_Table.makeColumnSecurityDenyTableName());
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	printTag("\n[/Add General Deny Read Token To Column Phone]");
    	
    	// Before delete by user
    	printTag("\n[Before Delete By User]");
		List<EntityInstance> willBeDeletedEntities         = getEntitiesFromDatabaseWithSecurity(CRUD.DELETE);
		getEntitiesFromDatabase();
		getRowDenySecurityTableContentFromDatabase();
		getRowGrantSecurityTableContentFromDatabase();
		getMappingTableContentFromDatabase();
		
		// After delete by user
		printTag("\n[After Delete By User]");	
		ResultSecurity resultSecurity                         = iecs.deleteEntitiesWithSecurity(user1, willBeDeletedEntities);
		List<EntityInstance> passedEntities                   = getEntitiesFromResultSecurity(resultSecurity, true);
		List<EntityInstance> failedEntities                   = getEntitiesFromResultSecurity(resultSecurity, false);
		List<EntityInstance> testTableEntitiesAFD             = getEntitiesFromDatabase();
		List<EntityInstance> rowDenySecurityTableEntitiesAFD  = getRowDenySecurityTableContentFromDatabase();
		List<EntityInstance> rowGrantSecurityTableEntitiesAFD = getRowGrantSecurityTableContentFromDatabase();
		List<EntityInstance> mappingTableEntitiesAFD          = getMappingTableContentFromDatabase();
		
		// Test assertion
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), passedEntities, sourceEntities));
		Assert.assertTrue(failedEntities.isEmpty());
		Assert.assertTrue(testTableEntitiesAFD.isEmpty());
		Assert.assertTrue(rowGrantSecurityTableEntitiesAFD.isEmpty());
		Assert.assertTrue(rowDenySecurityTableEntitiesAFD.isEmpty());
		Assert.assertTrue(mappingTableEntitiesAFD.isEmpty());
    	
		// Clean up
		// Roll Back : Add general deny read tokens to user
    	printTag("\n[Roll Back : Add General Deny Read Token To User]");
    	generalDenyReadToken = new LinkedList<EntityInstance>();
    	generalDenyReadToken.add(getTokens("general", CRUD.READ, false));	
    	iecs.removeTokensFromUser(root, user1, generalDenyReadToken);
    	Describer.describeTable(iecs, User_Token_Table.makeUserTokenTableName());
    	printTag("\n[/Roll Back : Add General Deny Read Token To User]");
    	
    	// Roll Back : Add general deny read tokens to last column
    	printTag("\n[Roll Back : Add General Deny Read Token To Column Phone]");
    	allColumns                       = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	AttributeClass[] columnsRollBack = {allColumns[allColumns.length-1]};
    	iecs.removeTokensFromColumns(root, TESTTABLE_TABLENAME.toLowerCase(), columnsRollBack, generalDenyReadToken);
    	Describer.describeTable(iecs, Column_Security_Deny_Table.makeColumnSecurityDenyTableName());
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	printTag("\n[/Roll Back : Add General Deny Read Token To Column Phone]");
    	
		id += sourceEntities.size();
    	//end();
    }
	
	/** 
	 * 
	 * <br/>
	 *  User : DEFAULT$*$+ <br/>
     *  <img src="../../resources/image/delete/testUserHasFullPermissionAndFirstRowHasNoDefaultGrantDeleteTokenOnDeleteAction.png" /> 
     *   
	 */
    public void testDeleteActionWhenUserHasDefalutGrantCRUD_FirstRowHasDefaultGrantCRU(){
		//begin();
		
		printTag("\n[-------------Test Delete Action When User Has Defalut Grant CRUD And First Row Has Default Grant CRU--------------]");
		List<EntityInstance> sourceEntities        = generateSourceEntities();
		iecs.createEntitiesWithSecurity(user1, sourceEntities);
		sourceEntities                             = generateSourceEntities();
		
		// Remove default grant delete token from first row
		printTag("\n[Remove Default Grant Delete Tokens From First Row]");
		List<EntityInstance> grantDeleteTokens     = new LinkedList<EntityInstance>();
		grantDeleteTokens.add(TokenFactory.DefaultTokens.getTOKEN_GRANTGROUP$D$G(iecs.getDatabaseConnection()));
		List<EntityInstance> rows                  = new LinkedList<EntityInstance>();
		rows.add(sourceEntities.get(0));
		iecs.removeTokensFromRows(root, rows, grantDeleteTokens);
    	Describer.describeTable(iecs, Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME));
		Describer.describeTable(iecs, Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(TESTTABLE_TABLENAME));
		printTag("\n[/Remove Default Grant Delete Tokens From First Row]");
		
		// Before delete by user
    	printTag("\n[Before Delete By User]");
		List<EntityInstance> willBeDeletedEntities         = getEntitiesFromDatabaseWithSecurity(CRUD.DELETE);
		List<EntityInstance> testTableEntities             = getEntitiesFromDatabase();
		List<EntityInstance> rowDenySecurityTableEntities  = getRowDenySecurityTableContentFromDatabase();
		List<EntityInstance> rowGrantSecurityTableEntities = getRowGrantSecurityTableContentFromDatabase();
		List<EntityInstance> mappingTableEntities          = getMappingTableContentFromDatabase();
		
		// After delete by user
		printTag("\n[After Delete By User]");	
		ResultSecurity resultSecurity                         = iecs.deleteEntitiesWithSecurity(user1, willBeDeletedEntities);
		List<EntityInstance> passedEntities                   = getEntitiesFromResultSecurity(resultSecurity, true);
		List<EntityInstance> failedEntities                   = getEntitiesFromResultSecurity(resultSecurity, false);
		List<EntityInstance> testTableEntitiesAFD             = getEntitiesFromDatabase();
		List<EntityInstance> rowDenySecurityTableEntitiesAFD  = getRowDenySecurityTableContentFromDatabase();
		List<EntityInstance> rowGrantSecurityTableEntitiesAFD = getRowGrantSecurityTableContentFromDatabase();
		List<EntityInstance> mappingTableEntitiesAFD          = getMappingTableContentFromDatabase();
		
		// Test assertion
		Assert.assertTrue(passedEntities.size() == 1);
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(),passedEntities.get(0), sourceEntities.get(1)));
		Assert.assertTrue(failedEntities.size() == 1);
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), failedEntities.get(0), sourceEntities.get(0)));
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), testTableEntities, testTableEntitiesAFD));
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), rowGrantSecurityTableEntities, rowGrantSecurityTableEntitiesAFD));
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), rowDenySecurityTableEntities, rowDenySecurityTableEntitiesAFD));
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), mappingTableEntities, mappingTableEntitiesAFD));
    	
		// Clean up
		// Rolll Back : Remove default grant delete token from first row
		printTag("\n[Rolll Back : Remove Default Grant Delete Tokens From First Row]");
		grantDeleteTokens     = new LinkedList<EntityInstance>();
		grantDeleteTokens.add(TokenFactory.DefaultTokens.getTOKEN_GRANTGROUP$D$G(iecs.getDatabaseConnection()));
		rows                  = new LinkedList<EntityInstance>();
		rows.add(sourceEntities.get(0));
		iecs.addTokensToRows(root, rows, grantDeleteTokens);
		Describer.describeTable(iecs, Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(TESTTABLE_TABLENAME));
    	Describer.describeTable(iecs, Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME));
		printTag("\n[/Rolll Back : Remove Default Grant Delete Tokens From First Row]");
		id += sourceEntities.size();
    	//end(); 
		
    }
    
	/** 
	 * 
	 * <br/>
	 *  User : DEFAULT$*$+  GENERAL$DELETE$-<br/>
     *  <img src="../../resources/image/delete/testUserHasGeneralDenyDeleteTokenAndFirstRowHasGeneralDenyDeleteTokensOnDeleteAction.png" /> 
     *   
	 */
    public void testDeleteActionWhenUserHasGeneralDenyD_FirstRowHasGeneralDenyD(){
    	//begin();
    	
    	printTag("\n[-------------Test Delete Action When User Has General Deny D And First Row Has General Deny D--------------]");
		List<EntityInstance> sourceEntities         = generateSourceEntities();
		iecs.createEntitiesWithSecurity(user1, sourceEntities);
		sourceEntities                              = generateSourceEntities();

    	// Add general deny token to the first row
    	printTag("\n[Add General Deny Token To The First Row]");
		List<EntityInstance> generalDenyUpdateToken = new LinkedList<EntityInstance>();
		generalDenyUpdateToken.add(getTokens("general", CRUD.DELETE, false));
    	List<EntityInstance> rows                   = new LinkedList<EntityInstance>();
    	rows.add(sourceEntities.get(0));
		iecs.addTokensToRows(root, rows, generalDenyUpdateToken);
		Describer.describeTable(iecs, Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME));
    	Describer.describeTable(iecs, Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME));
		printTag("\n[/Add General Deny Token To The First Row]");
		
    	// Add general deny token  tokens to user
    	printTag("\n[Add General Deny Token  To User]");
    	iecs.addTokensToUser(root, user1, generalDenyUpdateToken);
    	Describer.describeTable(iecs, User_Token_Table.makeUserTokenTableName());
    	printTag("\n[/Add General Deny Token  To User]");
    	
		// Before delete by user
    	printTag("\n[Before Delete By User]");
		List<EntityInstance> willBeDeletedEntities         = getEntitiesFromDatabaseWithSecurity(CRUD.DELETE);
		List<EntityInstance> testTableEntities             = getEntitiesFromDatabase();
		List<EntityInstance> rowDenySecurityTableEntities  = getRowDenySecurityTableContentFromDatabase();
		List<EntityInstance> rowGrantSecurityTableEntities = getRowGrantSecurityTableContentFromDatabase();
		List<EntityInstance> mappingTableEntities          = getMappingTableContentFromDatabase();
		
		// After delete by user
		printTag("\n[After Delete By User]");	
		ResultSecurity resultSecurity                         = iecs.deleteEntitiesWithSecurity(user1, willBeDeletedEntities);
		List<EntityInstance> passedEntities                   = getEntitiesFromResultSecurity(resultSecurity, true);
		List<EntityInstance> failedEntities                   = getEntitiesFromResultSecurity(resultSecurity, false);
		List<EntityInstance> testTableEntitiesAFD             = getEntitiesFromDatabase();
		List<EntityInstance> rowDenySecurityTableEntitiesAFD  = getRowDenySecurityTableContentFromDatabase();
		List<EntityInstance> rowGrantSecurityTableEntitiesAFD = getRowGrantSecurityTableContentFromDatabase();
		List<EntityInstance> mappingTableEntitiesAFD          = getMappingTableContentFromDatabase();
		
		// Test assertion
		Assert.assertTrue(passedEntities.size() == 1);
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(),passedEntities.get(0), sourceEntities.get(1)));
		Assert.assertTrue(failedEntities.size() == 1);
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), failedEntities.get(0), sourceEntities.get(0)));
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), testTableEntities, testTableEntitiesAFD));
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), rowGrantSecurityTableEntities, rowGrantSecurityTableEntitiesAFD));
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), rowDenySecurityTableEntities, rowDenySecurityTableEntitiesAFD));
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), mappingTableEntities, mappingTableEntitiesAFD));
	
		// Clean up
		// Roll Back : Add general deny token to the first row
    	printTag("\n[Roll Back : Add General Deny Token To The First Row]");
		generalDenyUpdateToken = new LinkedList<EntityInstance>();
		generalDenyUpdateToken.add(getTokens("general", CRUD.DELETE, false));
    	rows                   = new LinkedList<EntityInstance>();
    	rows.add(sourceEntities.get(0));
		iecs.removeTokensFromRows(root, rows, generalDenyUpdateToken);
		Describer.describeTable(iecs, Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME));
    	Describer.describeTable(iecs, Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME));
		printTag("\n[/Roll Back : Add General Deny Token To The First Row]");
		
    	// Roll Back : Add general deny token  tokens to user
    	printTag("\n[Roll Back : Add General Deny Token  To User]");
    	iecs.removeTokensFromUser(root, user1, generalDenyUpdateToken);
    	Describer.describeTable(iecs, User_Token_Table.makeUserTokenTableName());
    	printTag("\n[/Roll Back : Add General Deny Token  To User]");
		
		id +=sourceEntities.size();
    	//end(); 
    }
    
	/** 
	 * 
	 * <br/>
	 *  User : DEFAULT$*$+  GENERAL$DELETE$-<br/>
     *  <img src="../../resources/image/delete/testUserHasFullPermissionAndLastColumnHasNoDefaultGrantTokensAndFirstRowHasNoDefaultGrantDeleteTokensOnDeleteAction.png" /> 
     *   
	 */
    public void testDeleteActionWhenUserHasDefalutGrantCRUD_LastColumnHasNoDefalutGrantCRUD_FirstRowHasDefaultGrantCRU(){
    	//begin();
    	
    	printTag("\n[-------------Test Delete Action When User Has Defalut Grant CRUD And Last Column Has No Defalut Grant CRUD And First Row Has Default Grant CRU--------------]");
		List<EntityInstance> sourceEntities        = generateSourceEntities();
		iecs.createEntitiesWithSecurity(user1, sourceEntities);
		sourceEntities                             = generateSourceEntities();
		
		
    	// Remove default grant tokens from last column
    	printTag("\n[Remove Default Grant Tokens From Column Phone]");
    	AttributeClass[] allColumns                = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	AttributeClass[] columns                   = {allColumns[allColumns.length-1]};
    	List<EntityInstance> grantTokens           = TokenFactory.getDefaultGrantTokens(iecs.getDatabaseConnection());
    	iecs.removeTokensFromColumns(root, TESTTABLE_TABLENAME.toLowerCase(), columns, grantTokens);
    	Describer.describeTable(iecs, Column_Security_Grant_Table.makeColumnSecurityGrantTableName());
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	printTag("\n[/Remove Default Grant Tokens From Column Phone]");

		// Remove default grant delete token from first row
    	printTag("\n[Remove Default Grant Delete Token From First Row]");
    	List<EntityInstance> grantDeleteTokens     = new LinkedList<EntityInstance>();
    	grantDeleteTokens.add(TokenFactory.DefaultTokens.getTOKEN_GRANTGROUP$D$G(iecs.getDatabaseConnection()));
    	List<EntityInstance> rows                  = new LinkedList<EntityInstance>();
    	rows.add(sourceEntities.get(0));
    	iecs.removeTokensFromRows(root, rows, grantDeleteTokens);	
    	Describer.describeTable(iecs, Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(TESTTABLE_TABLENAME));
    	Describer.describeTable(iecs, Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME));
    	printTag("\n[/Remove Default Grant Delete Token From First Row]");

		// Before delete by user
    	printTag("\n[Before Delete By User]");
		List<EntityInstance> willBeDeletedEntities         = getEntitiesFromDatabaseWithSecurity(CRUD.DELETE);
		List<EntityInstance> testTableEntities             = getEntitiesFromDatabase();
		List<EntityInstance> rowDenySecurityTableEntities  = getRowDenySecurityTableContentFromDatabase();
		List<EntityInstance> rowGrantSecurityTableEntities = getRowGrantSecurityTableContentFromDatabase();
		List<EntityInstance> mappingTableEntities          = getMappingTableContentFromDatabase();
		
		// After delete by user
		printTag("\n[After Delete By User]");	
		ResultSecurity resultSecurity                         = iecs.deleteEntitiesWithSecurity(user1, willBeDeletedEntities);
		List<EntityInstance> passedEntities                   = getEntitiesFromResultSecurity(resultSecurity, true);
		List<EntityInstance> failedEntities                   = getEntitiesFromResultSecurity(resultSecurity, false);
		List<EntityInstance> testTableEntitiesAFD             = getEntitiesFromDatabase();
		List<EntityInstance> rowDenySecurityTableEntitiesAFD  = getRowDenySecurityTableContentFromDatabase();
		List<EntityInstance> rowGrantSecurityTableEntitiesAFD = getRowGrantSecurityTableContentFromDatabase();
		List<EntityInstance> mappingTableEntitiesAFD          = getMappingTableContentFromDatabase();
		
		// Test assertion
		Assert.assertTrue(failedEntities.size() == 2);
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(),failedEntities,sourceEntities));
		Assert.assertTrue(passedEntities.isEmpty());
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), testTableEntities, testTableEntitiesAFD));
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), rowGrantSecurityTableEntities, rowGrantSecurityTableEntitiesAFD));
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), rowDenySecurityTableEntities, rowDenySecurityTableEntitiesAFD));
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), mappingTableEntities, mappingTableEntitiesAFD));
    	
		// Clean up
    	// Roll Back : Remove default grant tokens from last column
    	printTag("\n[Roll Back : Remove Default Grant Tokens From Column Phone]");
    	allColumns                       = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	AttributeClass[] columnsRollBack = {allColumns[allColumns.length-1]};
    	grantTokens                      = TokenFactory.getDefaultGrantTokens(iecs.getDatabaseConnection());
    	iecs.addTokensToColumns(root, TESTTABLE_TABLENAME.toLowerCase(), columnsRollBack, grantTokens);
    	Describer.describeTable(iecs, Column_Security_Grant_Table.makeColumnSecurityGrantTableName());
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	printTag("\n[/Roll Back : Remove Default Grant Tokens From Column Phone]");

		// Roll Back : Remove default grant delete token from first row
    	printTag("\n[Roll Back : Remove Default Grant Delete Token From First Row]");
    	grantDeleteTokens     = new LinkedList<EntityInstance>();
    	grantDeleteTokens.add(TokenFactory.DefaultTokens.getTOKEN_GRANTGROUP$D$G(iecs.getDatabaseConnection()));
    	rows                  = new LinkedList<EntityInstance>();
    	rows.add(sourceEntities.get(0));
    	iecs.addTokensToRows(root, rows, grantDeleteTokens);	
    	Describer.describeTable(iecs, Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(TESTTABLE_TABLENAME));
    	Describer.describeTable(iecs, Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME));
    	printTag("\n[/Roll Back : Remove Default Grant Delete Token From First Row]");
		
    	id += sourceEntities.size();
    	//end();  	
    }
    
	/** 
	 * 
	 * <br/>
	 *  User : DEFAULT$*$+  GENERAL$DELETE$- GENERAL$READ$-<br/>
     *  <img src="../../resources/image/delete/testUserHasGeneralDenyReadDeleteTokensAndLastColumnHasGeneralDenyReadTokenAndFirstRowHasGeneralDenyDeleteTokenOnDeleteAction.png" /> 
     *   
	 */
    public void testDeleteActionWhenUserHasGeneralDenyRD_LastColumnHasGeneralDenyR_FirstRowHasGeneralDenyD(){
    	//begin();
    	
    	printTag("\n[-------------Test Delete Action When User Has General Deny RD And Last Column Has General Deny R And First Row Has General Deny D--------------]");
		List<EntityInstance> sourceEntities               = generateSourceEntities();
		iecs.createEntitiesWithSecurity(user1, sourceEntities);
		sourceEntities                                    = generateSourceEntities();
		
		
    	// Add general deny delete, read tokens to user
    	printTag("\n[Add General Deny Delete, Read Tokens To User]");
    	LinkedList<EntityInstance> generalDenyReadToken   = new LinkedList<EntityInstance>();
    	LinkedList<EntityInstance> generalDenyDeleteToken = new LinkedList<EntityInstance>();
    	generalDenyReadToken.add(getTokens("general", CRUD.READ, false));	
    	generalDenyDeleteToken.add(getTokens("general", CRUD.DELETE, false));
    	iecs.addTokensToUser(root, user1, generalDenyReadToken);
    	iecs.addTokensToUser(root, user1, generalDenyDeleteToken);
    	
    	// Add general deny read tokens to last column
    	printTag("\n[Add General Deny Read Token To Column Phone]");
    	AttributeClass[] allColumns             = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	AttributeClass[] columns                = {allColumns[allColumns.length-1]};
    	iecs.addTokensToColumns(root, TESTTABLE_TABLENAME.toLowerCase(), columns, generalDenyReadToken);
    	Describer.describeTable(iecs, Column_Security_Deny_Table.makeColumnSecurityDenyTableName());
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	printTag("\n[/Add General Deny Read Token To Column Phone]");
    	
    	// Add general deny delete token to the first row
    	printTag("\n[Add General Deny Delete Token To The First Row]");
    	List<EntityInstance> rows               = new LinkedList<EntityInstance>();
    	rows.add(sourceEntities.get(0));
		iecs.addTokensToRows(root, rows, generalDenyDeleteToken);
    	Describer.describeTable(iecs, Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME));
    	Describer.describeTable(iecs, Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME));
    	printTag("\n[/Add General Deny Delete Token To The First Row]");
		
		// Before delete by user
    	printTag("\n[Before Delete By User]");
		List<EntityInstance> willBeDeletedEntities         = getEntitiesFromDatabaseWithSecurity(CRUD.DELETE);
		List<EntityInstance> testTableEntities             = getEntitiesFromDatabase();
		List<EntityInstance> rowDenySecurityTableEntities  = getRowDenySecurityTableContentFromDatabase();
		List<EntityInstance> rowGrantSecurityTableEntities = getRowGrantSecurityTableContentFromDatabase();
		List<EntityInstance> mappingTableEntities          = getMappingTableContentFromDatabase();
		
		// After delete by user
		printTag("\n[After Delete By User]");	
		ResultSecurity resultSecurity                         = iecs.deleteEntitiesWithSecurity(user1, willBeDeletedEntities);
		List<EntityInstance> passedEntities                   = getEntitiesFromResultSecurity(resultSecurity, true);
		List<EntityInstance> failedEntities                   = getEntitiesFromResultSecurity(resultSecurity, false);
		List<EntityInstance> testTableEntitiesAFD             = getEntitiesFromDatabase();
		List<EntityInstance> rowDenySecurityTableEntitiesAFD  = getRowDenySecurityTableContentFromDatabase();
		List<EntityInstance> rowGrantSecurityTableEntitiesAFD = getRowGrantSecurityTableContentFromDatabase();
		List<EntityInstance> mappingTableEntitiesAFD          = getMappingTableContentFromDatabase();
		
		// Test assertion
		Assert.assertTrue(passedEntities.size() == 1);
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(),passedEntities.get(0), sourceEntities.get(1)));
		Assert.assertTrue(failedEntities.size() == 1);
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), failedEntities.get(0), sourceEntities.get(0)));
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), testTableEntities, testTableEntitiesAFD));
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), rowGrantSecurityTableEntities, rowGrantSecurityTableEntitiesAFD));
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), rowDenySecurityTableEntities, rowDenySecurityTableEntitiesAFD));
		Assert.assertTrue(TestAssist.compare(iecs.getDatabaseConnection(), mappingTableEntities, mappingTableEntitiesAFD));
    	
		// Clean up
    	// Roll Back : Add general deny delete, read tokens to user
    	printTag("\n[Roll Back : Add General Deny Delete, Read Tokens To User]");
    	generalDenyReadToken   = new LinkedList<EntityInstance>();
    	generalDenyDeleteToken = new LinkedList<EntityInstance>();
    	generalDenyReadToken.add(getTokens("general", CRUD.READ, false));	
    	generalDenyDeleteToken.add(getTokens("general", CRUD.DELETE, false));
    	iecs.removeTokensFromUser(root, user1, generalDenyReadToken);
    	iecs.removeTokensFromUser(root, user1, generalDenyDeleteToken);
    	printTag("\n[/Roll Back : Add General Deny Delete, Read Tokens To User]");
    	
    	// Roll Back : Add general deny read tokens to last column
    	printTag("\n[Roll Back : Add General Deny Read Token To Column Phone]");
    	allColumns                       = iecs.getDatabaseConnection().getEntityClass(TESTTABLE_TABLENAME, false).getAttributeClassList();
    	AttributeClass[] columnsRollBack = {allColumns[allColumns.length-1]};
    	iecs.removeTokensFromColumns(root, TESTTABLE_TABLENAME.toLowerCase(), columnsRollBack, generalDenyReadToken);
    	Describer.describeTable(iecs, Column_Security_Deny_Table.makeColumnSecurityDenyTableName());
    	Describer.describeTable(iecs, Column_Info_Table.makeColumnInfoTableName());
    	printTag("\n[/Roll Back : Add General Deny Read Token To Column Phone]");
    	
    	// Roll Back : Add general deny delete token to the first row
    	printTag("\n[Roll Back : Add General Deny Delete Token To The First Row]");
    	rows  = new LinkedList<EntityInstance>();
    	rows.add(sourceEntities.get(0));
		iecs.removeTokensFromRows(root, rows, generalDenyDeleteToken);
    	Describer.describeTable(iecs, Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME));
    	Describer.describeTable(iecs, Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME));
    	printTag("\n[/Roll Back : Add General Deny Delete Token To The First Row]");

		id += sourceEntities.size();
    	//end();  	
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
	
	private List<EntityInstance> generateSourceEntities(){
		List<EntityInstance> sourceEntities = new LinkedList<EntityInstance>();
		
		EntityInstance entity1 = iecs.createEntityInstanceFor(TESTTABLE_TABLENAME);
		entity1.set(TESTTABLE_COLUMN_STAFFID, id + 1);
		entity1.set(TESTTABLE_COLUMN_CHARAC, 'Y');
		entity1.set(TESTTABLE_COLUMN_BYTEA, "HelloWorld1".getBytes());
		entity1.set(TESTTABLE_COLUMN_NAME, "Stephen");
		entity1.set(TESTTABLE_COLUMN_PHONE, "0430106177");
		sourceEntities.add(entity1.clone());
		
		EntityInstance entity2 = iecs.createEntityInstanceFor(TESTTABLE_TABLENAME);
		entity2.set(TESTTABLE_COLUMN_STAFFID, id + 2);
		entity2.set(TESTTABLE_COLUMN_CHARAC, 'Y');
		entity2.set(TESTTABLE_COLUMN_BYTEA, "HelloWorld2".getBytes());
		entity2.set(TESTTABLE_COLUMN_NAME, "Sean");
		entity2.set(TESTTABLE_COLUMN_PHONE, "0430106178");
		sourceEntities.add(entity2.clone());
		
		return sourceEntities;
	}

	private void setup() throws SQLException {
		printTag("\n++++++++++++++++++++++++++++++++++++++START Set Up Test Case++++++++++++++++++++++++++++++++++++++++\n");
		
		iecs = new EntityControllerSecurity("/tmp/credential.txt");
		testTable_dataType.put(TESTTABLE_COLUMN_STAFFID, Integer.class);
		testTable_dataType.put(TESTTABLE_COLUMN_CHARAC, Character.class);
		testTable_dataType.put(TESTTABLE_COLUMN_BYTEA, byte[].class);
		testTable_dataType.put(TESTTABLE_COLUMN_NAME, String.class);
		testTable_dataType.put(TESTTABLE_COLUMN_PHONE, String.class);	
		//root                                    = getRootUser();
		//iecs.dropAllTables(root);
		iecs.initializeMode(true, Mode.SECURITY);
		deployDefinitionListWithRowSecurity(null,true,true);
		HashMap<String, EntityInstance> userMap = generateNormalUser(new String[][]{ {"User1","User1", "User1Pass"}  });
		user1                                   = userMap.get("User1");	
		root                                    = getRootUser();
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
	       System.out.println(tag);
	}
	
	private List<ClassToTables> deployDefinitionListWithRowSecurity( List<ClassToTables> classToTablesList, boolean dropTablesFirst, boolean continueOnError) {
		// Test Data Initialization
		classToTablesList = new LinkedList<ClassToTables>();	
		List<AttributeClass> attributes = new LinkedList<AttributeClass>();	
		AttributeClass primaryAttribute = new AttributeClass(TESTTABLE_COLUMN_STAFFID, testTable_dataType.get(TESTTABLE_COLUMN_STAFFID));
		primaryAttribute.setPrimaryKey(true);
		attributes.add(primaryAttribute);
		
		AttributeClass charac = new AttributeClass(TESTTABLE_COLUMN_CHARAC, testTable_dataType.get(TESTTABLE_COLUMN_CHARAC));
		charac.setPrimaryKey(true);
		attributes.add(charac);
		
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
	
	private List<String> getDefaultAndRootTokenIds(int rowNumbers) {
		List<String> defalutAndRootTokensIds = new LinkedList<String>();
		while(rowNumbers >0) {
			defalutAndRootTokensIds.add((String) TokenFactory.DefaultTokens.getTOKEN_GRANTGROUP$C$G(iecs.getDatabaseConnection()).get(Row_Security_Grant_Table.getColumnTokenID()));
			defalutAndRootTokensIds.add((String) TokenFactory.DefaultTokens.getTOKEN_GRANTGROUP$R$G(iecs.getDatabaseConnection()).get(Row_Security_Grant_Table.getColumnTokenID()));
			defalutAndRootTokensIds.add((String) TokenFactory.DefaultTokens.getTOKEN_GRANTGROUP$U$G(iecs.getDatabaseConnection()).get(Row_Security_Grant_Table.getColumnTokenID()));
			defalutAndRootTokensIds.add((String) TokenFactory.DefaultTokens.getTOKEN_GRANTGROUP$D$G(iecs.getDatabaseConnection()).get(Row_Security_Grant_Table.getColumnTokenID()));
	
			defalutAndRootTokensIds.add(RootPermission.ROOT$C$G);
			defalutAndRootTokensIds.add(RootPermission.ROOT$R$G);
			defalutAndRootTokensIds.add(RootPermission.ROOT$U$G);
			defalutAndRootTokensIds.add(RootPermission.ROOT$D$G);
			
			rowNumbers--;
		}
		return defalutAndRootTokensIds;
		
	}
	
	private List<EntityInstance> getEntitiesFromDatabase(){
		// Print base table statement
		List<EntityInstance> testTableEntities = iecs.getDatabaseConnection().read(TESTTABLE_TABLENAME, makeSqlStatementByStaffId());
		printTag("\n[The Statement Of Base Table]");
		Describer.describeEntityInstances(testTableEntities);
		printTag("\n[/The Statement Of Base Table]");
		return testTableEntities;
	}
	
	private List<EntityInstance> getRowDenySecurityTableContentFromDatabase(){
		CompoundStatementManual csm            = new CompoundStatementManual();
		csm.addConditionalStatement(makeSqlStatementByStaffId());
		List<EntityInstance> mappingTableIds   = iecs.readEntitiesWithSecurity(root, Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME), csm).getPassedEntities();
		csm.clear();
		for(EntityInstance mappingTableId : mappingTableIds){
			if(!csm.getCompoundStatements().isEmpty()){
				csm.addConditionalStatement(Q.OR);
			}
			csm.addConditionalStatement(Row_Security_Deny_Table.getColumnEntityInstanceID() + Q.E + Q.QUOT + mappingTableId.get(Tracing_Table.makeMappingTableColumnNameCoredbId(TESTTABLE_TABLENAME)) + Q.QUOT);
		}
		List<EntityInstance> rowDenySecurityTableEntities = new LinkedList<EntityInstance>();
		if(!csm.getCompoundStatements().isEmpty()){
			rowDenySecurityTableEntities       = iecs.readEntitiesWithSecurity(root, Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), csm).getPassedEntities();
		}
		printTag("\n[The Statement Of Row Deny Security Table]");
		Describer.describeEntityInstances(rowDenySecurityTableEntities);
		printTag("\n[/The Statement Of Row Deny Security Table]");
		return rowDenySecurityTableEntities;
	}
	
	private List<EntityInstance> getRowGrantSecurityTableContentFromDatabase(){
		CompoundStatementManual csm            = new CompoundStatementManual();
		csm.addConditionalStatement( makeSqlStatementByStaffId());
		
		List<EntityInstance> mappingTableIds   = iecs.readEntitiesWithSecurity(root, Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME), csm).getPassedEntities();
		csm.clear();
		for(EntityInstance mappingTableId : mappingTableIds){
			if(!csm.getCompoundStatements().isEmpty()){
				csm.addConditionalStatement(Q.OR);
			}
			csm.addConditionalStatement(Row_Security_Grant_Table.getColumnEntityInstanceID() + Q.E + Q.QUOT + mappingTableId.get(Tracing_Table.makeMappingTableColumnNameCoredbId(TESTTABLE_TABLENAME)) + Q.QUOT);
		}
		
		List<EntityInstance> rowGrantSecurityTableEntities = new LinkedList<EntityInstance>();
		if(!csm.getCompoundStatements().isEmpty()){
			rowGrantSecurityTableEntities       = iecs.readEntitiesWithSecurity(root, Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), csm).getPassedEntities();
		}
		printTag("\n[The Statement Of Row Grant Security Table]");
		Describer.describeEntityInstances(rowGrantSecurityTableEntities);
		printTag("\n[/The Statement Of Row Grant Security Table]");
		return rowGrantSecurityTableEntities;
	}
	
	private List<EntityInstance> getMappingTableContentFromDatabase(){
		CompoundStatementManual csm            = new CompoundStatementManual();
		csm.addConditionalStatement( makeSqlStatementByStaffId());
		
		List<EntityInstance> mappingTableEntities             = iecs.readEntitiesWithSecurity(root, Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME), csm).getPassedEntities();
		printTag("\n[The Statement Of Mapping Table]");
		Describer.describeEntityInstances(mappingTableEntities);
		printTag("\n[/The Statement Of Mapping Table]");
		return mappingTableEntities;
	}
	
	private List<EntityInstance> getEntitiesFromDatabaseWithSecurity(Character crud){
		CompoundStatementManual csm            = new CompoundStatementManual();
		csm.addConditionalStatement(makeSqlStatementByStaffId());
		
		List<EntityInstance> actionEntities = iecs.readEntitiesWithSecurity(user1, TESTTABLE_TABLENAME, csm).getPassedEntities();		
		if(crud.equals(CRUD.CREATE))      printTag("\n[The Entities Will Be Created]");
		else if(crud.equals(CRUD.READ))   printTag("\n[The Entities Will Be Readed]");
		else if(crud.equals(CRUD.UPDATE)) printTag("\n[The Entities Will Be Updated]");
		else if(crud.equals(CRUD.DELETE)) printTag("\n[The Entities Will Be Deleted]");
		else                              throw new Error("\n[Not Support Action]");
		Describer.describeEntityInstances(actionEntities);
		return actionEntities;
	}
	
	private List<EntityInstance> getEntitiesFromResultSecurity(ResultSecurity resultSecurity, boolean isPassed){
		List<EntityInstance> resultEntities = new LinkedList<EntityInstance>();
		if(isPassed){
			resultEntities = resultSecurity.getPassedEntities();
			// Print table statement
			printTag("\n[The Passed Entities]");
			Describer.describeEntityInstances(resultEntities);
			printTag("\n[/The Passed Entities]");
		} else {
			resultEntities = resultSecurity.getFailedEntities();
			// Print table statement
			printTag("\n[The Failed Entities]");
			Describer.describeEntityInstances(resultEntities);
			printTag("\n[/The Failed Entities]");
		}
		return resultEntities;
	}
	
	private String makeSqlStatementByStaffId(){
		String staffId_Column               = SchemaInfo.refactorColumnName(TESTTABLE_TABLENAME, TESTTABLE_COLUMN_STAFFID);
		String sql                          = staffId_Column + Q.E + (id + 1) + Q.OR + staffId_Column  + Q.E + (id + 2);
		return sql;
	}
	
	private EntityInstance getTokens(String groupName, Character crud, boolean isGrant){
		String sql = Token_Info_Table.getColumnGroup() + Q.E + Q.QUOT + groupName + Q.QUOT
		             + Q.AND + Token_Info_Table.getColumnCRUDType() + Q.E + Q.QUOT + crud + Q.QUOT
		             + Q.AND + Token_Info_Table.getColumnPermission() + Q.IS + isGrant;
		return iecs.getDatabaseConnection().read(Token_Info_Table.makeTokenInfoTableName(), sql).get(0);
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
	
}

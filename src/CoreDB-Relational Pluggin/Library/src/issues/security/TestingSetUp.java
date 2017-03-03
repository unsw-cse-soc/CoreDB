/**
 * 
 */
package issues.security;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import sun.security.krb5.internal.crypto.Des;

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
import coredb.utils.Describer;

/**
 * @author sean
 *
 */
public class TestingSetUp {
	public EntityControllerSecurity iecs         = null;
	public static final String TESTTABLE_TABLENAME      = "TEST";
	public static final String TESTTABLE_COLUMN_STAFFID = "STAFFID";
	public static final String TESTTABLE_COLUMN_CHARAC  = "CHARAC";
	public static final String TESTTABLE_COLUMN_BYTEA   = "BYTEA";
	public static final String TESTTABLE_COLUMN_NAME    = "NAME";
	public static final String TESTTABLE_COLUMN_PHONE   = "PHONE";
		
	@SuppressWarnings("unchecked")
	public HashMap<String, Class> testTable_dataType = new HashMap<String, Class>();
	
	public final String ROOTNAME                 = "ROOT";
	public final String ROOTPASS                 = "ROOT";
	
	public EntityInstance root                   = null;
	public EntityInstance user1                  = null;	
	
	public void setup() throws SQLException {
		printTag("\n< START Set Up Testing environment >\n");
		
		printTag("\n======Initialise EnitityController======\n");
		iecs = new EntityControllerSecurity("/tmp/credential.txt");
		iecs.dropAllTables();
		
		printTag("\n======Initialise Data types of base table======\n");
		testTable_dataType.put(TESTTABLE_COLUMN_STAFFID, Integer.class);
		testTable_dataType.put(TESTTABLE_COLUMN_CHARAC, Character.class);
		testTable_dataType.put(TESTTABLE_COLUMN_BYTEA, byte[].class);
		testTable_dataType.put(TESTTABLE_COLUMN_NAME, String.class);
		testTable_dataType.put(TESTTABLE_COLUMN_PHONE, String.class);
		
		iecs.initializeMode(true, Mode.SECURITY);
		deployDefinitionListWithRowSecurity(null,true,true);
		
		printTag("\n======Initialise Users======\n");
		root                                    = getRootUser();
		Describer.describeEntityInstance(root);
		HashMap<String, EntityInstance> userMap = generateNormalUser(new String[][]{ {"User1","User1", "User1Pass"}  });
		user1                                   = userMap.get("User1");	
		Describer.describeEntityInstance(user1);
		
		printTag("\n======Initialise Tokens======\n");
		createTokens("general", CRUD.CREATE, true);
		createTokens("general", CRUD.READ, true);
		createTokens("general", CRUD.UPDATE, true);
		createTokens("general", CRUD.DELETE, true);

		createTokens("general", CRUD.CREATE, false);
		createTokens("general", CRUD.READ, false);
		createTokens("general", CRUD.UPDATE, false);
		createTokens("general", CRUD.DELETE, false);
		
		Describer.describeTable(iecs, Token_Info_Table.makeTokenInfoTableName());
		
		printTag("\n</ END Set Up Testing environment >\n");
	}
	
	public EntityInstance createTokens(String groupName, Character crud, boolean isGrant){
    	EntityInstance token = iecs.createEntityInstanceFor(Token_Info_Table.makeTokenInfoTableName());
    	token.set(Token_Info_Table.getColumnTokenID(), Configuration.Security_Methods.makeGroupPermission(groupName, crud, isGrant));
    	token.set(Token_Info_Table.getColumnTokenName(), Configuration.Security_Methods.makeGroupPermission(groupName, crud, isGrant));
    	token.set(Token_Info_Table.getColumnCRUDType(), crud);
    	token.set(Token_Info_Table.getColumnGroup(), groupName);
    	token.set(Token_Info_Table.getColumnPermission(), isGrant);
    	iecs.createEntityWithSecurity(root, token.clone());
    	return token;
    }
	
	public List<ClassToTables> deployDefinitionListWithRowSecurity( List<ClassToTables> classToTablesList, boolean dropTablesFirst, boolean continueOnError) {
		printTag("\n< START Deploy base table >\n");
		
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
	    
	    Describer.describeTableSchema(iecs, TESTTABLE_TABLENAME);
	    printTag("\n</ END Deploy base table >\n");
	    return classToTables;
	    
	}	
	
	public void printTag(String tag) {
	       System.out.println(tag);
	}
	
	public EntityInstance getRootUser(){
		EntityInstance root   = iecs.readUser(ROOTNAME,ROOTPASS);
		return root;
	}
	
	public HashMap<String, EntityInstance> generateNormalUser(String[][] userInfos) {

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
}

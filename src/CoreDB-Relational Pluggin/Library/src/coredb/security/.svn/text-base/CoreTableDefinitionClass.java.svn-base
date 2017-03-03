package coredb.security;

import coredb.config.Configuration;
import coredb.unit.AttributeClass;
import coredb.unit.ClassToTables;
import coredb.unit.IndexClass;
import coredb.unit.EntityCoreTableClass;

import coredb.mode.Mode;
import java.util.UUID;
import java.util.LinkedList;
import java.util.List;
import java.sql.Timestamp;

import org.apache.ddlutils.model.TypeMap;


public class CoreTableDefinitionClass {

	/**
	 * mode[Mode.TRACING|MODE.SECURITY]
	 * @param mode
	 * @return the list of tables need be created
	 */
	// checked by vmc @1137
	public static List<ClassToTables> createCoreTableDefinitionClassList(Mode mode){
		List<ClassToTables> scl = new LinkedList<ClassToTables>();
		if (Mode.TRACING == mode) {
			scl.add(CoreTableDefinitionClass.createUserInfoTable());
			scl.add(CoreTableDefinitionClass.createCoredb_CSN());
		} else if (Mode.SECURITY == mode) {
			scl.add(CoreTableDefinitionClass.createUserInfoTable());
			scl.add(CoreTableDefinitionClass.createTokenInfoTable());
			scl.add(CoreTableDefinitionClass.createUserTokenTable());
			scl.add(CoreTableDefinitionClass.createColumnInfoTable());
			scl.add(CoreTableDefinitionClass.createColumnSecurityGrantTable());
			scl.add(CoreTableDefinitionClass.createColumnSecurityDenyTable());
		} else {
			System.out.printf("vmc: %10s\n is not currently supported",mode);
		}
		return scl;
	}
	
	/**
	 * Create a table schema to record the ChangeSetNumber information
	 * @return a entity core table class
	 */
	// checked by vmc @1073
	private static EntityCoreTableClass createCoredb_CSN() {
		LinkedList<AttributeClass> acl	= new LinkedList<AttributeClass>();
		String COREDB_ID                = Configuration.Coredb_CSN_Table.COLUMN_COREDB_ID;
		AttributeClass ac1              = new AttributeClass(COREDB_ID, UUID.class,TypeMap.VARCHAR);
		//ac1.setPrimaryKey(true);
		AttributeClass ac2              = new AttributeClass(Configuration.Coredb_CSN_Table.COLUMN_CSN, Integer.class,TypeMap.INTEGER);
		ac2.setAutoIncrement(true);
		ac2.setPrimaryKey(true);
        acl.add(ac1);
		acl.add(ac2);
		acl.add(new AttributeClass(Configuration.Coredb_CSN_Table.COLUMN_TIMESTAMP, Timestamp.class));
        acl.add(new AttributeClass(Configuration.Coredb_CSN_Table.COLUMN_USERID,	String.class));
		return new EntityCoreTableClass(Configuration.Coredb_CSN_Table.TABLE_NAME, acl.toArray(new AttributeClass[0]), null);
	
	}
	
	/**
	 * Create a table schemas to record the user information
	 * @return a entity core table class
	 */
	// checked by vmc @1073
	private static EntityCoreTableClass createUserInfoTable(){
		LinkedList<AttributeClass> acl = new LinkedList<AttributeClass>();
		String coreDbId                = Configuration.User_Info_Table.makeUserInfoTableColumnNameCoredbId();
		AttributeClass ac1             = new AttributeClass(coreDbId, Integer.class,TypeMap.INTEGER);
		ac1.setPrimaryKey(true);
		ac1.setAutoIncrement(true);
		AttributeClass ac2             = new AttributeClass(Configuration.User_Info_Table.COLUMN_USERID,   String.class,TypeMap.VARCHAR);
		AttributeClass ac3             = new AttributeClass(Configuration.User_Info_Table.COLUMN_USERNAME, String.class,TypeMap.VARCHAR);
		AttributeClass ac4             = new AttributeClass(Configuration.User_Info_Table.COLUMN_PASSWORD, String.class,TypeMap.VARCHAR);
		acl.add(ac1);
        acl.add(ac2);
		acl.add(ac3);
		acl.add(ac4);
        
        IndexClass index               = new IndexClass(new AttributeClass[]{ac2, ac3}, true);
        AttributeClass[] acls          = acl.toArray(new AttributeClass[0]);
		return new EntityCoreTableClass(Configuration.User_Info_Table.makeUserInfoTableName(), acls,new IndexClass[]{index});
		
	}
	
	/**
	 * Create a table schemas to record the matching between user and token
	 * @return a entity core table class
	 */
	// checked by vmc @1073
	private static EntityCoreTableClass createUserTokenTable(){
		LinkedList<AttributeClass> acl = new LinkedList<AttributeClass>();
		String coreDbId                = Configuration.User_Token_Table.makeUserTokenTableColumnNameCoredbId();
		AttributeClass ac1             = new AttributeClass(coreDbId, Integer.class,TypeMap.INTEGER);
		ac1.setPrimaryKey(true);
		ac1.setAutoIncrement(true);
		AttributeClass ac2             = new AttributeClass(Configuration.User_Token_Table.COLUMN_USERID, String.class,TypeMap.VARCHAR);
		AttributeClass ac3             = new AttributeClass(Configuration.User_Token_Table.COLUMN_TOKENID, String.class,TypeMap.VARCHAR);
		acl.add(ac1);
        acl.add(ac2);
        acl.add(ac3);

		IndexClass indexClass          = new IndexClass(new AttributeClass[]{ac2,ac3}, true);
        AttributeClass[] acls          = acl.toArray(new AttributeClass[0]);
		return new EntityCoreTableClass(Configuration.User_Token_Table.makeUserTokenTableName(), acls,new IndexClass[]{indexClass});
		
	}
	
	/**
	 * Create a table schemas to record the token information
	 * @return a entity core table class
	 */
	// checked by vmc @1073
	private static EntityCoreTableClass createTokenInfoTable(){
		LinkedList<AttributeClass> acl = new LinkedList<AttributeClass>();
		String coreDbId                = Configuration.Token_Info_Table.makeTokenInfoTableColumnNameCoredbId();
		AttributeClass ac1             = new AttributeClass(coreDbId, Integer.class,TypeMap.INTEGER);
		ac1.setPrimaryKey(true);
		ac1.setAutoIncrement(true);
		AttributeClass ac2             = new AttributeClass(Configuration.Token_Info_Table.COLUMN_TOKENNAME, String.class,  TypeMap.VARCHAR);
        acl.add(ac1);
        acl.add(ac2);
        acl.add(new AttributeClass(Configuration.Token_Info_Table.COLUMN_TOKENID,   String.class,  TypeMap.VARCHAR));
        acl.add(new AttributeClass(Configuration.Token_Info_Table.COLUMN_GROUP, String.class,  TypeMap.VARCHAR));
		acl.add(new AttributeClass(Configuration.Token_Info_Table.COLUMN_CRUDTYPE,  String.class,  TypeMap.VARCHAR));
		acl.add(new AttributeClass(Configuration.Token_Info_Table.COLUMN_PERMISSION,Boolean.class, TypeMap.BOOLEAN));

        IndexClass indexClass          = new IndexClass(new AttributeClass[]{ac2}, true);
        AttributeClass[] acls          = acl.toArray(new AttributeClass[0]);
		return new EntityCoreTableClass(Configuration.Token_Info_Table.makeTokenInfoTableName(), acls, new IndexClass[]{indexClass});
		
	}
	
	/**
	 * Create a table schemas to record the column information in each base table.
	 * @return a entity core table class
	 */
	// checked by vmc @1073
	private static EntityCoreTableClass createColumnInfoTable(){
		LinkedList<AttributeClass> acl = new LinkedList<AttributeClass>();
		String coreDbId                = Configuration.Column_Info_Table.makeColumnInfoTableColumnNameCoredbId();
		AttributeClass ac1             = new AttributeClass(coreDbId, Integer.class, TypeMap.INTEGER);
		ac1.setPrimaryKey(true);
		ac1.setAutoIncrement(true);
		AttributeClass ac2             = new AttributeClass(Configuration.Column_Info_Table.COLUMN_TABLENAME, String.class, TypeMap.VARCHAR);
		AttributeClass ac3             = new AttributeClass(Configuration.Column_Info_Table.COLUMN_COLUMNNAME, String.class, TypeMap.VARCHAR);
		
		// #COREDB-69
		AttributeClass ac4             = new AttributeClass(Configuration.Column_Info_Table.getColumnProgramingDataType(), String.class);
		AttributeClass ac5             = new AttributeClass(Configuration.Column_Info_Table.getColumnVersion(), Integer.class);
		// #/COREDB-69
		
		acl.add(ac1);
        acl.add(ac2);
        acl.add(ac3);
        
        // #COREDB-69
        acl.add(ac4);
        acl.add(ac5);
		// #/COREDB-69
        
		IndexClass index               = new IndexClass(new AttributeClass[]{ac2, ac3}, true);
        AttributeClass[] acls          = acl.toArray(new AttributeClass[0]);
		return new EntityCoreTableClass(Configuration.Column_Info_Table.makeColumnInfoTableName(), acls, new IndexClass[]{index});
		
	}
	
	/**
	 * Create a table schemas to record the column security of the granted permission one.
	 * @return a entity core table class
	 */
	// checked by vmc @1077
	private static EntityCoreTableClass createColumnSecurityGrantTable(){
		LinkedList<AttributeClass> acl = new LinkedList<AttributeClass>();
		String coreDbId                = Configuration.Column_Security_Grant_Table.makeColumnSecurityGrantTableColumnNameCoredbId();
		AttributeClass ac1 = new AttributeClass(coreDbId, Integer.class,TypeMap.INTEGER);
		ac1.setPrimaryKey(true);
		ac1.setAutoIncrement(true);
		AttributeClass ac2 = new AttributeClass(Configuration.Column_Security_Grant_Table.COLUMN_COLUMNID, Integer.class,TypeMap.INTEGER);
		AttributeClass ac3 = new AttributeClass(Configuration.Column_Security_Grant_Table.COLUMN_TOKENID, String.class,TypeMap.VARCHAR);
		acl.add(ac1);
        acl.add(ac2);
        acl.add(ac3);
        
		AttributeClass[] acls          = acl.toArray(new AttributeClass[0]);
		IndexClass index               = new IndexClass(new AttributeClass[]{ac2, ac3}, true);
		return new EntityCoreTableClass(Configuration.Column_Security_Grant_Table.makeColumnSecurityGrantTableName(), acls,new IndexClass[]{index});
	}
	
	/**
	 * Create a table schemas to record the column security of the denied permission one.
	 * @return a entity core table class
	 */
	// checked by vmc @1073
	private static EntityCoreTableClass createColumnSecurityDenyTable(){
		LinkedList<AttributeClass> acl = new LinkedList<AttributeClass>();
		String coreDbId                = Configuration.Column_Security_Deny_Table.makeColumnSecurityDenyColumnNameCoredbId();
		AttributeClass ac1             = new AttributeClass(coreDbId, Integer.class,TypeMap.INTEGER);
		ac1.setPrimaryKey(true);
		ac1.setAutoIncrement(true);
		AttributeClass ac2             = new AttributeClass(Configuration.Column_Security_Deny_Table.COLUMN_COLUMNID, Integer.class,TypeMap.INTEGER);
		AttributeClass ac3             = new AttributeClass(Configuration.Column_Security_Deny_Table.COLUMN_TOKENID, String.class,TypeMap.VARCHAR);
        acl.add(ac1);
        acl.add(ac2);
        acl.add(ac3);

        IndexClass indexClass = new IndexClass(new AttributeClass[]{ac2,ac3}, true);
        AttributeClass[] acls  = acl.toArray(new AttributeClass[0]);
		return new EntityCoreTableClass(Configuration.Column_Security_Deny_Table.makeColumnSecurityDenyTableName(), acls,new IndexClass[]{indexClass});
	}
}
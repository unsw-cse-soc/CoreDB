package coredb.config;

import coredb.database.SchemaInfo;

public class Configuration {

	/**
	 *  The delimiter specify the format of naming
	 */
	// checked by vmc @694
	public final static String  COREDB				= "COREDB";
	public final static String  TABLE_DELIMITER     = "_";
    public final static String  DEFAULT             = "DEFAULT"; 
	private final static String DELETE_SUFFIX       = "DELETE";

	public static class R {
		public static String refractor(String input) {
			//< #COREDB-90>
			if(ConfigurationValues.databaseType.equalsIgnoreCase("postgresql"))   return input.toLowerCase();
			else if(ConfigurationValues.databaseType.equalsIgnoreCase("derby"))   return input.toUpperCase();
			else if(ConfigurationValues.databaseType.equalsIgnoreCase("mysql"))   return input.toLowerCase();
			else if(ConfigurationValues.databaseType.equalsIgnoreCase("oracle"))  return input.toUpperCase();
			else                                                                  throw new Error("The database is not available for Coredb API, please contact Coredb team!");   
			//< #/COREDB-90>
		}
		
	}

	public static class Delete_Table {
		private static String DELETE = "DELETE";
		public static String makeDeteledTableName(String baseTableName) {
			return R.refractor(COREDB+TABLE_DELIMITER+baseTableName+TABLE_DELIMITER+DELETE);
		}
	}

	public static class Mapping_Table {
		private static String MAPPING = "MAPPING";
		public static String makeMappingTableName(String baseTableName) {
			return R.refractor(COREDB+TABLE_DELIMITER+baseTableName+TABLE_DELIMITER+MAPPING);
		}
	}


	// checked by vmc @896
	public static class Tracing_Table {
		public static final String COLUMN_CRUD	 = "CRUD";
		public static final String COLUMN_MINCSN = "MINCSN";
		public static final String COLUMN_MAXCSN = "MAXCSN";

		public static String getColumnCrud() {
			return R.refractor(COLUMN_CRUD);
		}
		public static String getColumnMinCSN() {
			return COLUMN_MINCSN.toLowerCase();
		}
		public static String getColumnMaxCSN() {
			return COLUMN_MAXCSN.toLowerCase();
		}

		private static final String MAPPING_SUFFIX			= "MAPPING";
		private static final String TRACING_SUFFIX			= "TRACING";
		public static final String COLUMN_TIMESTAMP			= "TIMESTAMP";
		public static final String COLUMN_USERID			= "USERID";
		public static final String COLUMN_CSN				= "CSN";
		public static final String COLUMN_NAME				= "COLUMNNAME";
		public static final String COLUMN_VALUE				= "COLUMNVALUE";

		public static String makeDeleteTableNameWithPrefixSuffix(String tableName) {
			return COREDB + TABLE_DELIMITER + tableName + TABLE_DELIMITER + DELETE_SUFFIX;
		}
		
		public static String makeMappingTableNameWithPrefixSuffix(String tableName) {
			return COREDB + TABLE_DELIMITER + tableName + TABLE_DELIMITER + MAPPING_SUFFIX;
		}

		public static String makeTracingTableNameWithPrefixSuffix(String tableName) {
			return COREDB + TABLE_DELIMITER + tableName + TABLE_DELIMITER + TRACING_SUFFIX;
		}

		public static String makeCoredbColumnNameId(String tableName){
			return Public_Method.makeCoredbColumnNameId(tableName);
		}
		public static String makeMappingTableColumnNameCoredbId(String tableName) {
			return R.refractor(Public_Method.makeCoredbColumnNameId(makeMappingTableNameWithPrefixSuffix(tableName)));
		}

		public static String makeTracingTableColumnNameCoredbId(String tableName) {
			return Public_Method.makeCoredbColumnNameId(makeTracingTableNameWithPrefixSuffix(tableName));
		}

		public static String getColumnName() {
			return R.refractor(COLUMN_NAME);
		}
		public static String getColumnValue() {
			return R.refractor(COLUMN_VALUE); // TOBE FIX LATER;
		}
		public static String getColumnCSN() {
			return R.refractor(COLUMN_CSN);
		}
        public static String getColumnTimestamp(){
        	return R.refractor(COLUMN_TIMESTAMP);
        }
        public static String getColumnUserID(){
        	return R.refractor(COLUMN_USERID);
        }

	}

	/** vmc: checked */
	public static class Coredb_CSN_Table{
		public static final String TABLE_NAME			= COREDB + TABLE_DELIMITER + "CSN";
		public static final String COLUMN_COREDB_ID		= Public_Attribute.PrefixOfRowId + Configuration.Coredb_CSN_Table.TABLE_NAME + Public_Attribute.SubfixOfRowId;

		public static final String COLUMN_CSN			= "CSN";
		public static final String COLUMN_TIMESTAMP		= "TIMESTAMP";
		public static final String COLUMN_USERID		= "USERID";
		public static final String COLUMN_CRUD			= "CRUD";

		///*
		public static String getCSNTableName(){
			return SchemaInfo.refactorTableName(TABLE_NAME);
		}
		//*/
		public static String getColumnCoredbID(){
			return R.refractor(COLUMN_COREDB_ID);
		}
		public static String getColumnCSN(){
			return R.refractor(COLUMN_CSN);
		}
		public static String getColumnTimeStamp(){
			return R.refractor(COLUMN_TIMESTAMP);
		}
		public static String getColumnUserID(){
			return R.refractor(COLUMN_USERID);
		}
		public static String getColumnCRUD(){
			return R.refractor(COLUMN_CRUD);
		}
	}
	
	
	
	/**
	 * for naming query result name
	 * UNCHECKED: ADDED DY SEAN
	 */
	public static final String LAZYDYNACLASS       = "COREDB-LAZYDYNACLASS";
	public static String makeLazyDynaClassName(String queryName){
		return queryName + TABLE_DELIMITER + LAZYDYNACLASS;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static final String ID	= "ID";
	/**
	 * The variables of global setting  
	 * @author Stephen
	 */
	// cleaned by Stephen
	public static class Public_Method{
		public static String makeCoredbColumnNameId(String tableName) {
			return R.refractor(COREDB + TABLE_DELIMITER + tableName + TABLE_DELIMITER + ID);
		}
		
		private static String makeCoreTableName(String tableName){
			return R.refractor(COREDB + TABLE_DELIMITER + tableName);
		}
	}

	public static class Security_Methods{
		private final static String SECURITYGROUP_DELIMTER = "$";
		public final static String GRANT                   = "G";
		public final static String DENY                    = "D";
	    
		public static String getSecurityDelimter(){
			return SECURITYGROUP_DELIMTER;
		}
		public static String makeGroupPermission(String groupName, Character crud, boolean isGrant){
			return groupName + SECURITYGROUP_DELIMTER + crud + SECURITYGROUP_DELIMTER + (isGrant ? GRANT : DENY);
		}
	}
	
	/**
	 * The variables of user information table 
	 * @author Stephen
	 */
	// cleaned by Stephen
	public static class User_Info_Table{
		private static final String  TABLE_NAME      = "USERINFO";
		public  static final String  COLUMN_USERID   = "USERID";
		public  static final String  COLUMN_USERNAME = "USERNAME";
		public  static final String  COLUMN_PASSWORD = "PASSWORD";
		
		public static String makeUserInfoTableName(){
			return Public_Method.makeCoreTableName(TABLE_NAME);
		}
		
		public static String makeUserInfoTableColumnNameCoredbId(){
			return Public_Method.makeCoredbColumnNameId(makeUserInfoTableName());
		}
		
		public static String getColumnUserID(){
			return R.refractor(COLUMN_USERID);
		}
		public static String getColumnUserName(){
			return R.refractor(COLUMN_USERNAME);
		}
		public static String getColumnPassword(){
			return R.refractor(COLUMN_PASSWORD);
		}
	}
	
	/**
	 * The  variables of token information table
	 * @author Stephen
	 */
	// cleaned by Stephen
	public static class Token_Info_Table{
		private static final String TABLE_NAME        = "TOKENINFO";
		public final static String  COLUMN_TOKENID    = "TOKENID";
		public final static String  COLUMN_GROUP      = "GROUPTYPE";
		public final static String  COLUMN_TOKENNAME  = "TOKENNAME";
		public final static String  COLUMN_CRUDTYPE   = "CRUDTYPE";
		public final static String  COLUMN_PERMISSION = "PERMISSION";
		
		public static String getColumnTokenID(){
			return R.refractor(COLUMN_TOKENID);
		}
		public static String getColumnTokenName(){
			return R.refractor(COLUMN_TOKENNAME);
		}
		public static String getColumnGroup(){
			return R.refractor(COLUMN_GROUP);
		}
		public static String getColumnCRUDType(){
			return R.refractor(COLUMN_CRUDTYPE);
		}
		public static String getColumnPermission(){
			return R.refractor(COLUMN_PERMISSION);
		}
		
		public static String makeTokenInfoTableName(){
			return Public_Method.makeCoreTableName(TABLE_NAME);
		}
		
		public static String makeTokenInfoTableColumnNameCoredbId(){
			return Public_Method.makeCoredbColumnNameId(makeTokenInfoTableName());
		}
	}	

	/**
	 * The variables of user and token mapping table
	 * @author Stephen
	 */
	// cleaned by Stephen
	public static class User_Token_Table{
		private static final String TABLE_NAME     = "USERTOKEN";
		public final static String  COLUMN_USERID  = "USERID";
		public final static String  COLUMN_TOKENID = "TOKENID";
		
		public static String makeUserTokenTableName(){
			return Public_Method.makeCoreTableName(TABLE_NAME);
		}
		
		public static String makeUserTokenTableColumnNameCoredbId(){
			return Public_Method.makeCoredbColumnNameId(makeUserTokenTableName());
		}
		
		public static String getColumnUserID(){
			return R.refractor(COLUMN_USERID);
		}
		public static String getColumnTokenID() {
			return R.refractor(COLUMN_TOKENID);
		}
	}

	/**
	 * The variables of column information table 
	 * @author Stephen
	 */
	// cleaned by Stephen
	public static class Column_Info_Table{
		private static final String TABLE_NAME                = "TABLECOLUMNINFO";
		public static final String 	COLUMN_TABLENAME          = "TABLENAME";
		public static final String 	COLUMN_COLUMNNAME         = "COLUMNNAME";
		public static final String 	COLUMN_PROGRMINGDATATYPE  = "PROGRMINGDATATYPE";
		public static final String 	COLUMN_VERSION            = "VERSION";
		
		public static String makeColumnInfoTableName(){
			return Public_Method.makeCoreTableName(TABLE_NAME);
		}
		
		public static String makeColumnInfoTableColumnNameCoredbId(){
			return Public_Method.makeCoredbColumnNameId(makeColumnInfoTableName());
		}
		
		public static String getColumnTableName() {
			return R.refractor(COLUMN_TABLENAME);
		}
		
		public static String getColumnColumnName() {
			return R.refractor(COLUMN_COLUMNNAME);
		}
		
		public static String getColumnProgramingDataType() {
			return R.refractor(COLUMN_PROGRMINGDATATYPE);
		}
		
		public static String getColumnVersion() {
			return R.refractor(COLUMN_VERSION);
		}
	}

	
	/**
	 * The variables of column security table (grant) 
	 * @author Stephen
	 */
	// cleaned by Stephen
	public static class Column_Security_Grant_Table{
		private static final String TABLE_NAME      = "COLUMNSECURITYGRANT";
		public static final String 	COLUMN_COLUMNID = "COLUMNID";
		public static final String 	COLUMN_TOKENID  = "TOKENID";
		
		public static String makeColumnSecurityGrantTableName(){
			return Public_Method.makeCoreTableName(TABLE_NAME);
		}
		public static String getColumnColumnID() {
			return R.refractor(COLUMN_COLUMNID);
		}
		public static String getColumnTokenID() {
			return R.refractor(COLUMN_TOKENID);			
		}
		
		public static String makeColumnSecurityGrantTableColumnNameCoredbId(){
			return Public_Method.makeCoredbColumnNameId(makeColumnSecurityGrantTableName());
		}
		
	}

	/**
	 * The variables of column security table (deny)
	 * @author Stephen
	 */
	// cleaned by Stephen
	public static class Column_Security_Deny_Table{
		private static final String TABLE_NAME      = "COLUMNSECURITYDENY";
		public static final String 	COLUMN_COLUMNID = "COLUMNID";
		public static final String 	COLUMN_TOKENID  = "TOKENID";
		
		public static String makeColumnSecurityDenyTableName(){
			return Public_Method.makeCoreTableName(TABLE_NAME);
		}
		
		public static String makeColumnSecurityDenyColumnNameCoredbId(){
			return Public_Method.makeCoredbColumnNameId( makeColumnSecurityDenyTableName());
		}
		
		public static String getColumnColumnID() {
			return R.refractor(COLUMN_COLUMNID);
		}
		public static String getColumnTokenID() {
			return R.refractor(COLUMN_TOKENID);			
		}
	}

	/**
	 * The variables of row security table (grant) 
	 * @author Stephen
	 */
	// cleaned by Stephen
	public static class Row_Security_Grant_Table{
		public static final String NAME_SUFFIX             = "ROWSECURITYGRANT";
		public static final String COLUMN_ENTITYINSTANCEID = "ENTITYINSTANCEID";
		public static final String COLUMN_TOKENID          = "TOKENID";
		
		public static String makeRowSecurityGrantTableNameWithPrefixSuffix(String tableName){
			return COREDB + TABLE_DELIMITER + tableName + TABLE_DELIMITER + NAME_SUFFIX;
		}	
		
		public static String makeRowSecurityGrantTableColumnNameCoredbId(String tableName){
			return Public_Method.makeCoredbColumnNameId(makeRowSecurityGrantTableNameWithPrefixSuffix(tableName));
		}
		
		public static String getColumnEntityInstanceID(){
			return R.refractor(COLUMN_ENTITYINSTANCEID);
		}
		public static String getColumnTokenID() {
			return R.refractor(COLUMN_TOKENID);			
		}
		public static String getName_Suffix() {
			return R.refractor(NAME_SUFFIX);			
		}
	}

	/**
	 * The variables of row security table (deny)
	 * @author Stephen
	 */
	// cleaned by Stephen
	public static class Row_Security_Deny_Table{
		public static final String NAME_SUFFIX             = "ROWSECURITYDENY";
		public static final String COLUMN_ENTITYINSTANCEID = "ENTITYINSTANCEID";
		public static final String COLUMN_TOKENID          = "TOKENID";
		
		public static String makeRowSecurityDenyTableNameWithPrefixSuffix(String tableName){
			return COREDB + TABLE_DELIMITER + tableName + TABLE_DELIMITER + NAME_SUFFIX;
		}
		
		public static String makeRowSecurityDenyTableColumnNameCoredbId(String tableName){
			return Public_Method.makeCoredbColumnNameId(makeRowSecurityDenyTableNameWithPrefixSuffix(tableName));
		}
		
		public static String getColumnEntityInstanceID(){
			return R.refractor(COLUMN_ENTITYINSTANCEID);
		}
		public static String getColumnTokenID() {
			return R.refractor(COLUMN_TOKENID);			
		}
		public static String getName_Suffix() {
			return R.refractor(NAME_SUFFIX);			
		}
	}


	
	
	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	



	/**
	 * The variables of global setting  
	 * @author Stephen
	 */
	public class Public_Attribute{
		public static final String PrefixOfRowId			= COREDB + TABLE_DELIMITER;
		public static final String SubfixOfRowId			= TABLE_DELIMITER + ID;
		public static final String Root_Permission			= "ROOT" + TABLE_DELIMITER;
	}

	
	
	
	
	



}
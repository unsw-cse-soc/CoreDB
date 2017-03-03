package coredb.database;

import java.util.HashMap;
import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Table;

// checked by vmc @760
public class SchemaInfo {
	private static final SchemaInfo singleton = new SchemaInfo();

    private HashMap<String, String> tableMap;

	private static final String MERGER	= ":";
    private static boolean printonce	= false;

	private SchemaInfo() {}
	/**
	 * SchemaInfo defines the rule of naming in the API. From different databases,
	 * since some of them are case sensitivity vice verse the others, it is necessary to
	 * build up a naming mechanism. What this function will force the user input into case 
	 * insensitivity and resolve the transformation problem between dynabean and database; 
     *
	 * @param databaseHandler
	 * @return the current SchemanInfo
	 */
	// checked by vmc @760
	public static SchemaInfo getInstance(DatabaseHandler databaseHandler){
		Database database = databaseHandler.getDatabase();
		singleton.tableMap = new HashMap<String,String>();
		for(Table table : database.getTables()){
			String key      = table.getName().toLowerCase();
			String value    = table.getName();
			singleton.tableMap.put(key,value);
			if (printonce)  System.err.printf("TABLE %30s -> %30s \n",key ,value);
			for( Column col : table.getColumns()){
				String key_table_column     = table.getName().toLowerCase()+MERGER+col.getName().toLowerCase();
				String value_table_column   = col.getName() + MERGER + col.getType();
			if (printonce)  System.err.printf("TABLE %30s -> %30s \n",key_table_column ,value_table_column);
				singleton.tableMap.put(key_table_column,value_table_column);
			}
		}
		return singleton;
	}

    /**
     * Fetch the real table name from the database  
     * @param tableName table name need to match
     * @return the real table name in database that matched with tableName
     */
	// checked by vmc @760
	public static String refactorTableName(String tableName) { return refactor(tableName,null,0); }

	/**
	* Fetch the real column name from the database  
	* @param  tableName the name of table which the column related
	* @param  coulmnName  the column name need to match
	* @return return the real column name from the database
	// checked by vmc @760
	*/
	public static String refactorColumnName(String tableName, String coulmnName) { return refactor(tableName,coulmnName,0); }

    /**
     *Fetch the real column type from the database  
	 * @param  tableName the name of table which the column related
	 * @param  coulmnName  the column type need to match
	 * @return return the real column type from the database
     */
	// checked by vmc @760
	public static String refactorColumnType(String tableName, String coulmnName) { return refactor(tableName,coulmnName,1); }

    /**
     * Fetch the  real object from the database
     * @param tableName the name of table which the column related
     * @param coulmnName  the column name 
     * @param index return real table name or column name or column type
     * @return the real value
     */
	// checked by vmc @760
	private static String refactor(String tableName, String coulmnName, int index){
		String key;
		String value;

		if(coulmnName==null){
            key = tableName.toLowerCase();
			value = singleton.tableMap.get(key);
			if(value != null) return value;
		} else {
			key = tableName.toLowerCase()+MERGER+coulmnName.toLowerCase();
		}

		value = singleton.tableMap.get(key);

		if(value==null){
            System.err.printf("From SchemaInfo: %10s [%15s, %-15s]\n", " INVALID PROPERITY ",tableName,coulmnName);
            Exception e = new Exception();
            e.printStackTrace();
            //System.exit(0);
            return null;
		} else return value.split(MERGER)[index];
	}
}
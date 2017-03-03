/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.sql;

import java.util.LinkedList;
import java.util.List;

import coredb.config.Configuration;
import coredb.database.DatabaseConnection;
import coredb.database.SchemaInfo;
import coredb.unit.AttributeClass;
import coredb.unit.EntityClass;

/**
 *
 * @author vmc
 */
public class SQLFactory {
    private final static String SELECT_CLAUSE                 = Q.SELECT + Q.STAR;
    private final static String JOIN_BASEDELETE_MAPPING_TABLE = "JOINT_BASEDELETE_MAPPING_TABLE";
    private final static String UNION_BASE_DELETE_TABLE       = "UNION_BASE_DELETE_TABLE";
    /**
     * @param tableName the name of tableName be select from in sql statement
     * @return the select statement  
     */
	// checked by vmc @769
    public static String makeSQL_findEntities(String tableName) {
        String sql = SELECT_CLAUSE + Q.FROM + SchemaInfo.refactorTableName(tableName);
        return sql;
    }

    /**
     * @param tableName the name of tableName be select from in sql statement
     * @param columns list of column need to select
     * @return the select statement  
     */
	// checked by vmc @769
    public static String makeSQL_findEntities(String tableName, List<String> columns) {
    	String selectClause = Q.SELECT;

    	for(int i = 0; i < columns.size(); i ++) {
    		selectClause += columns.get(i);
    		if(i < columns.size() - 1)  selectClause += Q.COMMA;
    	}
		selectClause += Q.FROM;
        return selectClause + SchemaInfo.refactorTableName(tableName);
    }

   /**
    * 
    * @param tableName the name of tableName be select from in sql statement
	* @param csl the where clause of this select statement
    * @return the select statement 
    */
	// checked by vmc @769
    public static String makeSQL_findEntities(String tableName, SQLStatement csl) {
		return makeSQL_findEntities(tableName, Q.STAR, csl.toString());
    }

	/**
	 * This method gets select sql statement base on given condition statement 
	 * @param tableName given table name
	 * @param cslString given condition statement
	 * @return returns select sql statement base on given condition statement 
	 */
	//  % bind to the previous one
	public static String makeSQL_findEntities(String tableName, String cslString) {
		return makeSQL_findEntities(tableName, Q.STAR, cslString);
    }

   /**
    *
    * @param tableName the name of tableName be select from in sql statement
	* @param column
	* @param csh the where clause of this select statement
    * @return the select statement
    */
	// checked by vmc @769
    public static String makeSQL_findEntities(String tableName, String column, SQLStatement csh ) {
		//List<String> columns = new LinkedList<String>();
		//columns.add(column);
		return makeSQL_findEntities(tableName, column, csh.toSQLStatement());
    }
	/**
	 * This method gets select sql statement on given column
	 * @param tableName given table name
	 * @param column given column will be selected
	 * @param cshString the condition statement
	 * @return returns select sql statement on given column
	 */
    public static String makeSQL_findEntities(String tableName, String column, String cshString) {
		List<String> columns = new LinkedList<String>();
		columns.add(column);
		return makeSQL_findEntities(tableName, columns, cshString);
    }


   /**
    * 
    * @param tableName the name of tableName be select from in sql statement
    * @param csh the where clause of this select statement
    * @param columns list of column need to select
    * @return the select statement 
    */
	// checked by vmc @769
    public static String makeSQL_findEntities(String tableName, List<String> columns, SQLStatement csh) {
		/*
		if (csh == null) return makeSQL_findEntities(tableName,columns);
		else {
			String conditionalString = (csh == null || csh.getConditionalStatements().size() == 0 ?
										"" : Q.WHERE + csh.toString());
			return makeSQL_findEntities(tableName, columns) + conditionalString;
		}
		 */
		return makeSQL_findEntities(tableName, columns, csh.toString());
    }

	// %% BIND to previous
	public static String makeSQL_findEntities(String tableName, List<String> columns, String cshString) {
		if (cshString == null) return makeSQL_findEntities(tableName,columns);
		else {
			String conditionalString = (cshString  == null || cshString.equals("") ?
										"" : Q.WHERE + cshString);
			return makeSQL_findEntities(tableName, columns) + conditionalString;
		}
    }

	/**
	 * This method gets select max() sql statement
	 * @param tableName the given table name
	 * @param ac the AttributeName will be selected
	 * @return returns the select max(ac.getName()) sql statement
	 */
	// checked by vmc @769
	public static String makeSQL_Max(String tableName, AttributeClass ac) {
    	String max_sql = Q.SELECT+Q.SPACE+Q.MAX.trim() +
						 Q.LEFT_PARENTHESIS.trim()  + ac.getName() +
						 Q.RIGHT_PARENTHESIS.trim() + Q.FROM +
						 tableName;

    	String SQL = makeSQL_findEntities(tableName) + Q.WHERE + ac.getName() + Q.E +
                     Q.LEFT_PARENTHESIS + max_sql + Q.RIGHT_PARENTHESIS;
    	return SQL;
	}

        /**
         * Make a SQL statment to find the latest tracing records of a table
         * @param tableName the tracing table name
         * @param sql the SQLStatement as conditions to look for the specified result
         * @param databaseConnection database connection
         * @return the SQL statement as String type
         */
	public static String makeSQL_findInitialTraceEntities(String tableName, SQLStatement sql, DatabaseConnection databaseConnection){
            List<String> primarykeys         = getPrimarykeys(databaseConnection.getEntityClass(tableName, false));
            String csnTableName              = Configuration.Coredb_CSN_Table.TABLE_NAME;
            String csnTable_TimeStamp        = Configuration.Coredb_CSN_Table.COLUMN_TIMESTAMP;
            String csnTable_UserId           = Configuration.Coredb_CSN_Table.COLUMN_USERID;
            CompoundStatementManual base_CSM = new CompoundStatementManual();

            SQLStatement union_BASE_DELETE_Statement          = Union_BASE_DELETE_Table(tableName, sql);
            SQLStatement join_BASEDELETE_MAPPING_Statement    = Join_BASEDELETE_MAPPING_Table(tableName, primarykeys, union_BASE_DELETE_Statement);
            SQLStatement join_BASEDELETEMAPPING_CSN_Statement = Join_BASEDELETEMAPPING_CSN_Table(tableName, join_BASEDELETE_MAPPING_Statement);

            base_CSM.addConditionalStatement(Q.SELECT + Q.DISTINCT);
            base_CSM.addConditionalStatement(JOIN_BASEDELETE_MAPPING_TABLE + Q.PERIOD + Q.STAR);
            base_CSM.addConditionalStatement(Q.COMMA);
            base_CSM.addConditionalStatement(csnTableName + Q.PERIOD + csnTable_TimeStamp);
            base_CSM.addConditionalStatement(Q.COMMA);
            base_CSM.addConditionalStatement(csnTableName + Q.PERIOD + csnTable_UserId);
            base_CSM.addConditionalStatement(Q.FROM);
            base_CSM.addConditionalStatement(Q.LEFT_PARENTHESIS);
            base_CSM.addConditionalStatement(join_BASEDELETEMAPPING_CSN_Statement);
            base_CSM.addConditionalStatement(Q.RIGHT_PARENTHESIS);
            base_CSM.addConditionalStatement(Q.ORDER_BY+Configuration.Tracing_Table.getColumnMinCSN());

	    return base_CSM.toSQLStatement();

	}


        private static SQLStatement Union_BASE_DELETE_Table(String tableName, SQLStatement sql){
            String deleteTableName      = Configuration.Tracing_Table.makeDeleteTableNameWithPrefixSuffix(tableName);
            String whereClause          = "";
            if(sql.toSQLStatement().length()>0){
                whereClause = Q.WHERE + sql.toSQLStatement();
            }

            CompoundStatementManual base_CSM = new CompoundStatementManual();
            base_CSM.addConditionalStatement(Q.LEFT_PARENTHESIS);
            base_CSM.addConditionalStatement(SELECT_CLAUSE + Q.FROM + tableName + whereClause);
            base_CSM.addConditionalStatement(Q.RIGHT_PARENTHESIS);
            base_CSM.addConditionalStatement(Q.UNION);
            base_CSM.addConditionalStatement(Q.LEFT_PARENTHESIS);
            base_CSM.addConditionalStatement(SELECT_CLAUSE+ Q.FROM + deleteTableName + whereClause);
            base_CSM.addConditionalStatement(Q.RIGHT_PARENTHESIS);

            return base_CSM;
            
        }

        private static SQLStatement Join_BASEDELETE_MAPPING_Table(String tableName, List<String> primarykeys, SQLStatement union_BASE_DELETE_Statement){
            String mappingTableName                = Configuration.Tracing_Table.makeMappingTableNameWithPrefixSuffix(tableName);
            CompoundStatementManual base_CSM       = new CompoundStatementManual();
            CompoundStatementManual primarykey_Csm = new CompoundStatementManual();

            base_CSM.addConditionalStatement(Q.LEFT_PARENTHESIS);
            base_CSM.addConditionalStatement(union_BASE_DELETE_Statement);
            base_CSM.addConditionalStatement(Q.RIGHT_PARENTHESIS);
            base_CSM.addConditionalStatement(Q.AS + UNION_BASE_DELETE_TABLE);
            base_CSM.addConditionalStatement(Q.LEFT_JOIN);
            base_CSM.addConditionalStatement(mappingTableName);
            base_CSM.addConditionalStatement(Q.USING);
            base_CSM.addConditionalStatement(Q.LEFT_PARENTHESIS);
            for(String primarykey : primarykeys){
                if(primarykey_Csm.getCompoundStatements().size()>0){
                    primarykey_Csm.addConditionalStatement(Q.COMMA);
                }
                primarykey_Csm.addConditionalStatement(primarykey);
            }
            base_CSM.addConditionalStatement(primarykey_Csm);
            base_CSM.addConditionalStatement(Q.RIGHT_PARENTHESIS);

            return base_CSM;

        }

        private static SQLStatement Join_BASEDELETEMAPPING_CSN_Table(String tableName, SQLStatement join_BASEDELETE_MAPPING_Statement){
            String csnTableName                  = Configuration.Coredb_CSN_Table.TABLE_NAME;
            String csnTable_Csn                  = Configuration.Coredb_CSN_Table.COLUMN_CSN;
            String mappingTable_Maxcsn           = Configuration.Tracing_Table.getColumnMaxCSN();
            CompoundStatementManual base_CSM     = new CompoundStatementManual();
            base_CSM.addConditionalStatement(Q.LEFT_PARENTHESIS);
            base_CSM.addConditionalStatement(Q.SELECT + Q.STAR + Q.FROM);
            base_CSM.addConditionalStatement(Q.LEFT_PARENTHESIS);
            base_CSM.addConditionalStatement(join_BASEDELETE_MAPPING_Statement);
            base_CSM.addConditionalStatement(Q.RIGHT_PARENTHESIS);
            base_CSM.addConditionalStatement(Q.RIGHT_PARENTHESIS);
            base_CSM.addConditionalStatement(Q.AS + JOIN_BASEDELETE_MAPPING_TABLE);
            base_CSM.addConditionalStatement(Q.LEFT_JOIN);
            base_CSM.addConditionalStatement(csnTableName);
            base_CSM.addConditionalStatement(Q.ON);
            base_CSM.addConditionalStatement(Q.LEFT_PARENTHESIS);
            base_CSM.addConditionalStatement(csnTableName + Q.PERIOD + csnTable_Csn + Q.E + JOIN_BASEDELETE_MAPPING_TABLE + Q.PERIOD + mappingTable_Maxcsn);
            base_CSM.addConditionalStatement(Q.RIGHT_PARENTHESIS);

            return base_CSM;

        }

        /**
         * Get the primary keys of a table
         * @param baseTableEntityClass the entity class of the table
         * @return a list of primary keys.
         */
        private static List<String>  getPrimarykeys(EntityClass baseTableEntityClass){
            List<String> primarykeys = new LinkedList<String>();
            for(AttributeClass ac : baseTableEntityClass.getAttributeClassList()){
                if(ac.isPrimaryKey()) primarykeys.add(ac.getName());
            }
            return primarykeys;

        }
}
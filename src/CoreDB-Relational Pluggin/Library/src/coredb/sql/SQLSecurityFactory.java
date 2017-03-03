package coredb.sql;


import java.util.List;

import coredb.config.Configuration;
import coredb.config.Configuration.Tracing_Table;
import coredb.database.SchemaInfo;
import coredb.unit.CRUD;

public class SQLSecurityFactory {
	/**
	 * The parameters of COLUMNINFO table ;
	 */
	private final static String COLUMNINFO			      = Configuration.Column_Info_Table.makeColumnInfoTableName();
	private final static String COLUMNINFO_CORDBID        = COLUMNINFO + Q.PERIOD + Configuration.Column_Info_Table.makeColumnInfoTableColumnNameCoredbId();
    private final static String COLUMNINFO_TABLENAME      = COLUMNINFO + Q.PERIOD + Configuration.Column_Info_Table.getColumnTableName();
	private final static String COLUMNINFO_COLUMNNAME     = COLUMNINFO + Q.PERIOD + Configuration.Column_Info_Table.getColumnColumnName();
			
	/**
	 * The parameters of COLUMNSECUIRTDENY table
	 */
	private final static String COLUMNSECURITYDENY 		  = Configuration.Column_Security_Deny_Table.makeColumnSecurityDenyTableName();
	
	/**
	 * The parameters of COLUMNSECURITYGRANT table
	 */
	private final static String COLUMNSECURITYGRANT 	  = Configuration.Column_Security_Grant_Table.makeColumnSecurityGrantTableName();
	
	/**
	 * The parameters of USERTOKEN table
	 */
	private final static String USERTOKEN 			      = Configuration.User_Token_Table.makeUserTokenTableName();
	private final static String USERTOKEN_USERID 		  = USERTOKEN + Q.PERIOD + Configuration.User_Token_Table.getColumnUserID();
	
	/**
	 * The parameters of TOKENINFO table
	 */
	private final static String TOKENINFO			      = Configuration.Token_Info_Table.makeTokenInfoTableName();
	private final static String TOKENINFO_TOKENID         = TOKENINFO + Q.PERIOD + Configuration.Token_Info_Table.getColumnTokenID();
    private final static String TOKENINFO_CRUDTYPE 		  = TOKENINFO + Q.PERIOD + Configuration.Token_Info_Table.getColumnCRUDType();
	private final static String TOKENINFO_PERMISSION 	  = TOKENINFO + Q.PERIOD + Configuration.Token_Info_Table.getColumnPermission();

	private static String space(int level) {
		return String.format("%"+3*level+"s"," ");
	}

    public static String makeSQL_readEntitiesWithRowSecurity(String userId, SQLStatement sql, String tableName, List<String> columnNames){
        String MAPPINGTABLE                      = Configuration.Tracing_Table.makeMappingTableNameWithPrefixSuffix(tableName);
        String MAPPINGID                         = MAPPINGTABLE + Q.PERIOD + Configuration.Tracing_Table.makeMappingTableColumnNameCoredbId(tableName);
        String GRANT_ROW_CLAUSE                  = "GRANT_ROW_CLASUE";
        String GRANT_ROW_CLAUSE_ENTITYINSTANCEID = GRANT_ROW_CLAUSE   + Q.PERIOD + Configuration.Row_Security_Grant_Table.getColumnEntityInstanceID();

        CompoundStatementManual base_CSM         = new CompoundStatementManual();
        CompoundStatementManual columnName_CSM = new CompoundStatementManual();

        for(String columnName : columnNames){
            if(columnName_CSM.getCompoundStatements().size()>0){
                columnName_CSM.addConditionalStatement(Q.COMMA);
            }
            columnName_CSM.addConditionalStatement(tableName + Q.PERIOD + columnName);
        }
        
		int level = 1;
        base_CSM.addConditionalStatement(space(level) + Q.SELECT);
        base_CSM.addConditionalStatement(space(level) + columnName_CSM);
        base_CSM.addConditionalStatement(space(level) + Q.FROM + "\n");
        base_CSM.addConditionalStatement(space(level) + " " + tableName +"\n");
        base_CSM.addConditionalStatement(space(level) + Q.NATURAL_JOIN + "\n");
        
        base_CSM.addConditionalStatement(space(level) +Q.LEFT_PARENTHESIS + "\n");
        base_CSM.addConditionalStatement(space(level+1) + " " + MAPPINGTABLE + "\n");
        base_CSM.addConditionalStatement(space(level+1) + Q.INNER_JOIN+"\n");
        
        base_CSM.addConditionalStatement(space(level+1) +Q.LEFT_PARENTHESIS + "\n" + makeSQL_findRowsBasedOnRowSecurity(level + 2,userId, sql, tableName, CRUD.READ) +"\n"+space(level+1) + Q.RIGHT_PARENTHESIS + Q.AS + GRANT_ROW_CLAUSE +"\n");
        base_CSM.addConditionalStatement(space(level+1) +Q.ON + MAPPINGID + Q.E + GRANT_ROW_CLAUSE_ENTITYINSTANCEID+"\n");
        base_CSM.addConditionalStatement(space(level) +Q.RIGHT_PARENTHESIS);
        
//        System.out.println("+++++++++++++++++++++++++++++++++++");
//        System.out.println(base_CSM.toSQLStatement());
//        System.out.println("+++++++++++++++++++++++++++++++++++");
        return base_CSM.toSQLStatement();
    }

    public static String makeSQL_findRowsBasedOnRowSecurity(String userId, SQLStatement sql, String tableName, Character crud) {
		return makeSQL_findRowsBasedOnRowSecurity(1, userId, sql, tableName, crud);
	}

    private static String makeSQL_findRowsBasedOnRowSecurity(int level, String userId, SQLStatement sql, String tableName, Character crud){
        String ROWSECURITYGRANT                       = Configuration.Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(tableName);
        String ROWSECURITYDENY                        = Configuration.Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(tableName);
        String ROWSECURITYGRANT_ENTITYINSTANCEID      = ROWSECURITYGRANT + Q.PERIOD + Configuration.Row_Security_Grant_Table.getColumnEntityInstanceID();
        String ROWSECURITYDENY_ENTITYINSTANCEID       = ROWSECURITYDENY + Q.PERIOD + Configuration.Row_Security_Deny_Table.getColumnEntityInstanceID();
        String MAPPINGTABLE                           = Configuration.Tracing_Table.makeMappingTableNameWithPrefixSuffix(tableName);
        String MAPPINGID                              = MAPPINGTABLE + Q.PERIOD + Configuration.Tracing_Table.makeMappingTableColumnNameCoredbId(tableName);
        String FIND_MAPPINFID_CLAUSE                  = "FIND_MAPPINFID_CLAUSE";
        String FIND_MAPPINFID_CLAUSE_ENTITYINSTANCEID = FIND_MAPPINFID_CLAUSE + Q.PERIOD + Tracing_Table.makeMappingTableColumnNameCoredbId(tableName);      
        String GRANT_TOKEN_CLASUE                     = "GRANT_TOKEN_CLASUE";
        String DENY_TOKEN_CLASUE                      = "DENY_TOKEN_CLASUE";
        String GRANT_ROW_CLAUSE                       = "GRANT_ROW_CLASUE";
        String DENY_ROW_CLAUSE                        = "DENY_ROW_CLASUE";
        String GRANT_ROW_CLAUSE_ENTITYINSTANCEID      = GRANT_ROW_CLAUSE   + Q.PERIOD + Configuration.Row_Security_Grant_Table.getColumnEntityInstanceID();
        String DENY_ROW_CLAUSE_ENTITYINSTANCEID       = DENY_ROW_CLAUSE    + Q.PERIOD + Configuration.Row_Security_Deny_Table.getColumnEntityInstanceID();

        CompoundStatementManual base_CSM         = new CompoundStatementManual();
        CompoundStatementManual find_MappingId   = new CompoundStatementManual();
        CompoundStatementManual grant_CSM        = new CompoundStatementManual();
        CompoundStatementManual deny_CSM         = new CompoundStatementManual();
        CompoundStatementManual exclude_Grant_From_Deny_CSM  = new CompoundStatementManual();


        find_MappingId.addConditionalStatement(space(level+2)+Q.SELECT + MAPPINGID);
        find_MappingId.addConditionalStatement(Q.FROM+"\n");
        find_MappingId.addConditionalStatement(space(level+2)+Q.SPACE+MAPPINGTABLE+"\n");
        find_MappingId.addConditionalStatement(space(level+2)+Q.NATURAL_JOIN+"\n");
        find_MappingId.addConditionalStatement(space(level+2)+Q.SPACE+tableName+"\n");
        if(sql.toSQLStatement().length()>0) find_MappingId.addConditionalStatement(space(level+2)+Q.WHERE + sql);

        grant_CSM.addConditionalStatement(space(level+1) + Q.SPACE +ROWSECURITYGRANT + "\n");
        grant_CSM.addConditionalStatement(space(level+1)+Q.NATURAL_JOIN + "\n");
        grant_CSM.addConditionalStatement(space(level+1)+Q.LEFT_PARENTHESIS + "\n" + makeSQL_findTokensByUser(level + 2, userId,crud,true) + "\n" +space(level+1)+Q.RIGHT_PARENTHESIS + Q.AS + GRANT_TOKEN_CLASUE + "\n");
        grant_CSM.addConditionalStatement(space(level+1)+Q.INNER_JOIN+"\n");
        grant_CSM.addConditionalStatement(space(level+1)+Q.LEFT_PARENTHESIS +"\n"+ find_MappingId + "\n" +space(level+1)+Q.RIGHT_PARENTHESIS + Q.AS + FIND_MAPPINFID_CLAUSE+"\n");
        grant_CSM.addConditionalStatement(space(level+1)+Q.ON + FIND_MAPPINFID_CLAUSE_ENTITYINSTANCEID + Q.E + ROWSECURITYGRANT_ENTITYINSTANCEID);

        deny_CSM.addConditionalStatement(space(level+1) + Q.SPACE+ROWSECURITYDENY+"\n");
        deny_CSM.addConditionalStatement(space(level+1) +Q.NATURAL_JOIN+"\n");
        deny_CSM.addConditionalStatement(space(level+1) +Q.LEFT_PARENTHESIS +"\n"+ makeSQL_findTokensByUser(level + 2,userId,crud,false) + "\n"+space(level+1) +Q.RIGHT_PARENTHESIS + Q.AS + DENY_TOKEN_CLASUE +"\n");
        deny_CSM.addConditionalStatement(space(level+1) +Q.INNER_JOIN+"\n");
        deny_CSM.addConditionalStatement(space(level+1) +Q.LEFT_PARENTHESIS +"\n"+find_MappingId + "\n"+space(level+1) +Q.RIGHT_PARENTHESIS + Q.AS + FIND_MAPPINFID_CLAUSE+"\n");
        deny_CSM.addConditionalStatement(space(level+1) +Q.ON + FIND_MAPPINFID_CLAUSE_ENTITYINSTANCEID + Q.E + ROWSECURITYDENY_ENTITYINSTANCEID);


        exclude_Grant_From_Deny_CSM.addConditionalStatement(space(level)+Q.LEFT_PARENTHESIS + "\n" + grant_CSM + "\n"+ space(level) + Q.RIGHT_PARENTHESIS + Q.AS + GRANT_ROW_CLAUSE + "\n");
        exclude_Grant_From_Deny_CSM.addConditionalStatement(space(level)+Q.LEFT_JOIN+"\n");
        exclude_Grant_From_Deny_CSM.addConditionalStatement(space(level)+Q.LEFT_PARENTHESIS+"\n" + deny_CSM + "\n" +space(level)+Q.RIGHT_PARENTHESIS + Q.AS + DENY_ROW_CLAUSE +"\n");
        exclude_Grant_From_Deny_CSM.addConditionalStatement(space(level)+Q.ON + GRANT_ROW_CLAUSE_ENTITYINSTANCEID + Q.E + DENY_ROW_CLAUSE_ENTITYINSTANCEID +"\n");
        exclude_Grant_From_Deny_CSM.addConditionalStatement(space(level) +Q.WHERE + DENY_ROW_CLAUSE_ENTITYINSTANCEID  + Q.IS +  Q.NULL);

        base_CSM.addConditionalStatement(space(level)+Q.SELECT + Q.DISTINCT + GRANT_ROW_CLAUSE_ENTITYINSTANCEID);
        base_CSM.addConditionalStatement(Q.FROM + "\n");
        base_CSM.addConditionalStatement(exclude_Grant_From_Deny_CSM);

        return base_CSM.toSQLStatement();

    }

//    /** **
//     * Make a SQL statement to validate the column security
//     * @param userId the user identifier
//     * @param tableName the table which is attached by column security
//     * @param crud the CRUD action type
//     * @return A SQL statement
//     */
//    public static String makeSQL_findColumnsBasedOnColumnSecurity(String userId , String tableName, Character crud){
//        tableName                            = SchemaInfo.refactorTableName(tableName);
//        String EXCLUDE_GRANT_FROM_DENY_CLAUSE= "EXCLUDE_GRANT_FROM_DENY_CLAUSE";
//        String GRANT_TOKEN_CLASUE            = "GRANT_TOKEN_CLASUE";
//        String DENY_TOKEN_CLASUE             = "DENY_TOKEN_CLASUE";
//        String GRANT_COLLUMN_CLAUSE          = "GRANT_COLUMN_CLASUE";
//        String DENY_COLLUMN_CLAUSE           = "DENY_COLUMN_CLASUE";
//        String GRANT_COLLUMN_CLAUSE_COLUMNID = GRANT_COLLUMN_CLAUSE + Q.PERIOD + Configuration.Column_Security_Grant_Table.getColumnColumnID();
//        String DENY_COLLUMN_CLAUSE_COLUMNID  = DENY_COLLUMN_CLAUSE  + Q.PERIOD + Configuration.Column_Security_Deny_Table.getColumnColumnID();
//        
//        int level = 1;
//
//        CompoundStatementManual base_CSM     = new CompoundStatementManual();
//        CompoundStatementManual grant_CSM    = new CompoundStatementManual();
//        CompoundStatementManual deny_CSM     = new CompoundStatementManual();
//        CompoundStatementManual exclude_Grant_From_Deny_CSM  = new CompoundStatementManual();
//
//        grant_CSM.addConditionalStatement(space(level+2) +Q.SPACE+COLUMNSECURITYGRANT+"\n");
//        grant_CSM.addConditionalStatement(space(level+2) +Q.NATURAL_JOIN+"\n");
//        grant_CSM.addConditionalStatement(space(level+2) +Q.LEFT_PARENTHESIS +"\n"+ makeSQL_findTokensByUser(level + 3,userId,crud,true) + "\n"+space(level+2) +Q.RIGHT_PARENTHESIS + Q.AS + GRANT_TOKEN_CLASUE);
//
//        deny_CSM.addConditionalStatement(space(level+2) +Q.SPACE+COLUMNSECURITYDENY+"\n");
//        deny_CSM.addConditionalStatement(space(level+2) +Q.NATURAL_JOIN+"\n");
//        deny_CSM.addConditionalStatement(space(level+2) +Q.LEFT_PARENTHESIS +"\n"+ makeSQL_findTokensByUser(level + 3,userId,crud,false) +"\n"+space(level+2) + Q.RIGHT_PARENTHESIS + Q.AS + DENY_TOKEN_CLASUE);
//
//        exclude_Grant_From_Deny_CSM.addConditionalStatement(space(level+1) +Q.SELECT + Q.STAR + Q.FROM+"\n");
//        exclude_Grant_From_Deny_CSM.addConditionalStatement(space(level+1) +Q.LEFT_PARENTHESIS+ "\n" + grant_CSM +"\n"+space(level+1) +Q.RIGHT_PARENTHESIS + Q.AS + GRANT_COLLUMN_CLAUSE+"\n");
//        exclude_Grant_From_Deny_CSM.addConditionalStatement(space(level+1) +Q.LEFT_JOIN+"\n");
//        exclude_Grant_From_Deny_CSM.addConditionalStatement(space(level+1) +Q.LEFT_PARENTHESIS+"\n" + deny_CSM +"\n"+ space(level+1) +Q.RIGHT_PARENTHESIS + Q.AS + DENY_COLLUMN_CLAUSE +"\n");
//        exclude_Grant_From_Deny_CSM.addConditionalStatement(space(level+1) +Q.ON + GRANT_COLLUMN_CLAUSE_COLUMNID + Q.E + DENY_COLLUMN_CLAUSE_COLUMNID +"\n");
//        exclude_Grant_From_Deny_CSM.addConditionalStatement(space(level+1) +Q.INNER_JOIN+"\n");
//        exclude_Grant_From_Deny_CSM.addConditionalStatement(space(level+1) +Q.SPACE+COLUMNINFO+"\n");
//        exclude_Grant_From_Deny_CSM.addConditionalStatement(space(level+1) +Q.ON + GRANT_COLLUMN_CLAUSE_COLUMNID + Q.E + COLUMNINFO_CORDBID+"\n");
//        exclude_Grant_From_Deny_CSM.addConditionalStatement(space(level+1) +Q.WHERE + DENY_COLLUMN_CLAUSE_COLUMNID  + Q.IS + Q.NULL);
//        exclude_Grant_From_Deny_CSM.addConditionalStatement(Q.AND + COLUMNINFO_TABLENAME + Q.E + Q.QUOT + tableName + Q.QUOT+"\n");
//
//        base_CSM.addConditionalStatement(space(level) +Q.SELECT + Q.DISTINCT + COLUMNINFO_COLUMNNAME);
//        base_CSM.addConditionalStatement(Q.FROM +"\n");
//        base_CSM.addConditionalStatement(space(level) +Q.SPACE +COLUMNINFO+"\n");
//        base_CSM.addConditionalStatement(space(level) +Q.NATURAL_JOIN+"\n");
//        base_CSM.addConditionalStatement(space(level) +Q.LEFT_PARENTHESIS +"\n"+ exclude_Grant_From_Deny_CSM + "\n" +space(level) +Q.RIGHT_PARENTHESIS + Q.AS + EXCLUDE_GRANT_FROM_DENY_CLAUSE+"\n");
//        base_CSM.addConditionalStatement(space(level) +Q.WHERE+ COLUMNINFO_TABLENAME + Q.E + Q.QUOT + tableName + Q.QUOT);
//
////        System.out.println("------------------------------------");
////        System.out.println(base_CSM.toSQLStatement());
////        System.out.println("-------------------------------------");
//        return base_CSM.toSQLStatement();
//
//    }
    
    /**
     * Make a SQL statement to validate the column security
     * @param userId the user identifier
     * @param tableName the table which is attached by column security
     * @param crud the CRUD action type
     * @return A SQL statement
     */
	// checked by vmc @1555
    public static String makeSQL_findColumnsBasedOnColumnSecurity(String userId , String tableName, Character crud){
    	tableName                            = SchemaInfo.refactorTableName(tableName);
    	String GRANT_TOKEN_CLASUE            = "GRANT_TOKEN_CLASUE";
        String DENY_TOKEN_CLASUE             = "DENY_TOKEN_CLASUE";
        String GRANT_COLLUMN_CLAUSE          = "GRANT_COLUMN_CLASUE";
        String DENY_COLLUMN_CLAUSE           = "DENY_COLUMN_CLASUE";
        String GRANT_COLLUMN_CLAUSE_COLUMNID = GRANT_COLLUMN_CLAUSE + Q.PERIOD + Configuration.Column_Security_Grant_Table.getColumnColumnID();
        String DENY_COLLUMN_CLAUSE_COLUMNID  = DENY_COLLUMN_CLAUSE  + Q.PERIOD + Configuration.Column_Security_Deny_Table.getColumnColumnID();
        
        int level = 1;

        CompoundStatementManual base_CSM     = new CompoundStatementManual();
        CompoundStatementManual grant_CSM    = new CompoundStatementManual();
        CompoundStatementManual deny_CSM     = new CompoundStatementManual();
        CompoundStatementManual exclude_Grant_From_Deny_CSM  = new CompoundStatementManual();

        grant_CSM.addConditionalStatement(space(level+2) +Q.SPACE+COLUMNSECURITYGRANT+"\n");
        grant_CSM.addConditionalStatement(space(level+2) +Q.NATURAL_JOIN+"\n");
        grant_CSM.addConditionalStatement(space(level+2) +Q.LEFT_PARENTHESIS +"\n"+ makeSQL_findTokensByUser(level + 3,userId,crud,true) + "\n"+space(level+2) +Q.RIGHT_PARENTHESIS + Q.AS + GRANT_TOKEN_CLASUE);

        deny_CSM.addConditionalStatement(space(level+3) +Q.SPACE+COLUMNSECURITYDENY+"\n");
        deny_CSM.addConditionalStatement(space(level+3) +Q.NATURAL_JOIN+"\n");
        deny_CSM.addConditionalStatement(space(level+3) +Q.LEFT_PARENTHESIS +"\n"+ makeSQL_findTokensByUser(level + 4,userId,crud,false) +"\n"+space(level+3) + Q.RIGHT_PARENTHESIS + Q.AS + DENY_TOKEN_CLASUE);

        exclude_Grant_From_Deny_CSM.addConditionalStatement(space(level+1) +Q.SELECT + Q.DISTINCT+ GRANT_COLLUMN_CLAUSE_COLUMNID + Q.FROM+"\n");
        exclude_Grant_From_Deny_CSM.addConditionalStatement(space(level+1) +Q.LEFT_PARENTHESIS+ "\n" + grant_CSM +"\n"+space(level+1) +Q.RIGHT_PARENTHESIS + Q.AS + GRANT_COLLUMN_CLAUSE+"\n");
        exclude_Grant_From_Deny_CSM.addConditionalStatement(space(level+1) +Q.WHERE + GRANT_COLLUMN_CLAUSE_COLUMNID +"\n");
        exclude_Grant_From_Deny_CSM.addConditionalStatement(space(level+1) +Q.NOT + Q.IN +"\n");
        exclude_Grant_From_Deny_CSM.addConditionalStatement(space(level+1) +Q.LEFT_PARENTHESIS+"\n");
        exclude_Grant_From_Deny_CSM.addConditionalStatement(space(level+2) +Q.SELECT + Q.DISTINCT + DENY_COLLUMN_CLAUSE_COLUMNID+ Q.FROM+"\n");
        exclude_Grant_From_Deny_CSM.addConditionalStatement(space(level+2) +Q.LEFT_PARENTHESIS+"\n" + deny_CSM +"\n"+ space(level+2) +Q.RIGHT_PARENTHESIS + Q.AS + DENY_COLLUMN_CLAUSE +"\n");
        exclude_Grant_From_Deny_CSM.addConditionalStatement(space(level+1) +Q.RIGHT_PARENTHESIS);
        
        
        
        base_CSM.addConditionalStatement(space(level) +Q.SELECT + Q.DISTINCT + COLUMNINFO_COLUMNNAME);
        base_CSM.addConditionalStatement(Q.FROM +"\n");
        base_CSM.addConditionalStatement(space(level) +Q.SPACE +COLUMNINFO+"\n");
        base_CSM.addConditionalStatement(space(level) +Q.WHERE);
        base_CSM.addConditionalStatement(space(level) +Q.SPACE +COLUMNINFO_CORDBID+"\n");
        base_CSM.addConditionalStatement(space(level) +Q.IN+"\n");
        base_CSM.addConditionalStatement(space(level) +Q.LEFT_PARENTHESIS +"\n"+ exclude_Grant_From_Deny_CSM + "\n" +space(level) +Q.RIGHT_PARENTHESIS +"\n");
        base_CSM.addConditionalStatement(space(level) +Q.AND+ COLUMNINFO_TABLENAME + Q.E + Q.QUOT + tableName + Q.QUOT);

//        System.out.println("------------------------------------");
//        System.out.println(base_CSM.toSQLStatement());
//        System.out.println("-------------------------------------");
        return base_CSM.toSQLStatement();

    }

    public static String makeSQL_findRowsByIdentifiableColumnsBasedOnCrud(String userId, String tableName, List<String> columnNames, SQLStatement sql, Character crud){
		return makeSQL_findRowsByIdentifiableColumnsBasedOnCrud(1,userId, sql, tableName, columnNames, crud);
	}
    private static String makeSQL_findRowsByIdentifiableColumnsBasedOnCrud(int level, String userId, SQLStatement csa, String tableName, List<String> columnNames, Character crud){
    	// SECTION 1 SELECT baseTable.* FROM baseTable, MAPPING WHERE baseTable.ppp = mapping.ppp AND tableMAPPING.mid IN
		// SECTION 2 SELECT MID FORM MAPPING WHERE PPP in FUNCTION_FIND(entityInstances)
		// INTERSECT
		// SECTION 3 WHAT I CAN DO WITH ROWS BASED ONCRUD

		if (CRUD.CREATE			== crud)  { throw new Error("COREDB: State Invalid ");  }
		else if (CRUD.READ		== crud)  { throw new Error("COREDB: State Invalid ");  }
		else if (CRUD.UPDATE	== crud)  ;
		else if (CRUD.DELETE	== crud)  ;
		else { throw new Error("COREDB: State Invalid "); }

    	tableName                   = SchemaInfo.refactorTableName(tableName);
    	String MAPPINGTABLE         = Configuration.Tracing_Table.makeMappingTableNameWithPrefixSuffix(tableName);
        String MAPPINGID            = MAPPINGTABLE + Q.PERIOD + Configuration.Tracing_Table.makeMappingTableColumnNameCoredbId(tableName);
    	
    	CompoundStatementManual section1 = new CompoundStatementManual();
    	CompoundStatementManual INTERSECTION = new CompoundStatementManual();
    	CompoundStatementManual section2 = new CompoundStatementManual();
    	CompoundStatementManual section3 = new CompoundStatementManual();
    	
    	section2.addConditionalStatement(space(level + 1) + Q.SELECT + MAPPINGID + Q.FROM + "\n");
    	section2.addConditionalStatement(space(level + 1) + " " + MAPPINGTABLE);
    	section2.addConditionalStatement(space(level + 1) + "\n");
    	section2.addConditionalStatement(space(level + 1) + Q.WHERE + csa + "\n");
    	
    	section3 = makeSQLOfCRUDCanBeDone(level + 1, userId, tableName, crud);
    	
    	INTERSECTION.addConditionalStatement(section2 + space(level + 1) + Q.INTERSECT + "\n" + section3);
    	
    	section1.addConditionalStatement(space(level) + Q.SELECT + tableName + Q.PERIOD + Q.STAR + Q.FROM + "\n");
    	section1.addConditionalStatement(space(level) + " " + tableName + Q.COMMA + MAPPINGTABLE + "\n");
    	section1.addConditionalStatement(space(level) + Q.WHERE);
    	for (int i = 0; i < columnNames.size(); i++) {
    		section1.addConditionalStatement(tableName + Q.PERIOD + columnNames.get(i) + Q.E + MAPPINGTABLE + Q.PERIOD + columnNames.get(i));
    		if(i != columnNames.size() - 1) section1.addConditionalStatement(Q.AND);
		}
    	section1.addConditionalStatement(space(level) + Q.AND + MAPPINGID + "\n");
    	section1.addConditionalStatement(space(level) + Q.IN + "\n");
    	section1.addConditionalStatement(space(level) + Q.LEFT_PARENTHESIS + "\n" + INTERSECTION + space(level) + Q.RIGHT_PARENTHESIS +"\n");

		return section1.toSQLStatement();
	}

    public static CompoundStatementManual makeSQLOfCRUDCanBeDone(String userId, String tableName, Character crud) {
    	return makeSQLOfCRUDCanBeDone(1, userId, tableName, crud);
    }

    private static CompoundStatementManual makeSQLOfCRUDCanBeDone(int level, String userId, String tableName, Character crud) {
    	String ROWSECURITYGRANT              = Configuration.Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(tableName);
        String ROWSECURITYDENY               = Configuration.Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(tableName);
        String MAPPINGTABLE                  = Configuration.Tracing_Table.makeMappingTableNameWithPrefixSuffix(tableName);
        String MAPPINGID                     = MAPPINGTABLE + Q.PERIOD + Configuration.Tracing_Table.makeMappingTableColumnNameCoredbId(tableName);
    	
    	String GRANT_TOKEN_CLASUE            = "GRANT_TOKEN_CLASUE";
        String DENY_TOKEN_CLASUE             = "DENY_TOKEN_CLASUE";
        String GRANT_ROW_CLAUSE              = "GRANT_ROW_CLAUSE";
        String DENY_ROW_CLAUSE               = "DENY_ROW_CLAUSE";
        String GRANT_COLLUMN_CLAUSE_MID      = GRANT_ROW_CLAUSE + Q.PERIOD + Configuration.Row_Security_Grant_Table.getColumnEntityInstanceID();
        String DENY_COLLUMN_CLAUSE_MID       = DENY_ROW_CLAUSE  + Q.PERIOD + Configuration.Row_Security_Deny_Table.getColumnEntityInstanceID();
        
        CompoundStatementManual base_CSM     = new CompoundStatementManual();
        CompoundStatementManual grant_CSM    = new CompoundStatementManual();
        CompoundStatementManual deny_CSM     = new CompoundStatementManual();
        CompoundStatementManual exclude_Grant_From_Deny_CSM  = new CompoundStatementManual();

        grant_CSM.addConditionalStatement(space(level+2) +Q.SPACE + ROWSECURITYGRANT + "\n");
        grant_CSM.addConditionalStatement(space(level+2) +Q.NATURAL_JOIN + "\n");
        grant_CSM.addConditionalStatement(space(level+2) +Q.LEFT_PARENTHESIS +"\n"+ makeSQL_findTokensByUser(level + 3,userId,crud,true) + "\n"+space(level+2) +Q.RIGHT_PARENTHESIS + Q.AS + GRANT_TOKEN_CLASUE);

        deny_CSM.addConditionalStatement(space(level+3) +Q.SPACE+ROWSECURITYDENY+"\n");
        deny_CSM.addConditionalStatement(space(level+3) +Q.NATURAL_JOIN+"\n");
        deny_CSM.addConditionalStatement(space(level+3) +Q.LEFT_PARENTHESIS +"\n"+ makeSQL_findTokensByUser(level + 4,userId,crud,false) +"\n"+space(level+3) + Q.RIGHT_PARENTHESIS + Q.AS + DENY_TOKEN_CLASUE);

        exclude_Grant_From_Deny_CSM.addConditionalStatement(space(level+1) +Q.SELECT + Q.DISTINCT+ GRANT_COLLUMN_CLAUSE_MID + Q.FROM+"\n");
        exclude_Grant_From_Deny_CSM.addConditionalStatement(space(level+1) +Q.LEFT_PARENTHESIS+ "\n" + grant_CSM +"\n"+space(level+1) +Q.RIGHT_PARENTHESIS + Q.AS + GRANT_ROW_CLAUSE+"\n");
        exclude_Grant_From_Deny_CSM.addConditionalStatement(space(level+1) +Q.WHERE + GRANT_COLLUMN_CLAUSE_MID +"\n");
        exclude_Grant_From_Deny_CSM.addConditionalStatement(space(level+1) +Q.NOT + Q.IN +"\n");
        exclude_Grant_From_Deny_CSM.addConditionalStatement(space(level+1) +Q.LEFT_PARENTHESIS+"\n");
        exclude_Grant_From_Deny_CSM.addConditionalStatement(space(level+2) +Q.SELECT + Q.DISTINCT + DENY_COLLUMN_CLAUSE_MID+ Q.FROM+"\n");
        exclude_Grant_From_Deny_CSM.addConditionalStatement(space(level+2) +Q.LEFT_PARENTHESIS+"\n" + deny_CSM +"\n"+ space(level+2) +Q.RIGHT_PARENTHESIS + Q.AS + DENY_ROW_CLAUSE +"\n");
        exclude_Grant_From_Deny_CSM.addConditionalStatement(space(level+1) +Q.RIGHT_PARENTHESIS);
        
        
        base_CSM.addConditionalStatement(space(level) +Q.SELECT + Q.DISTINCT + MAPPINGID);
        base_CSM.addConditionalStatement(Q.FROM +"\n");
        base_CSM.addConditionalStatement(space(level) +Q.SPACE +MAPPINGTABLE+"\n");
        base_CSM.addConditionalStatement(space(level) +Q.WHERE);
        base_CSM.addConditionalStatement(MAPPINGID+"\n");
        base_CSM.addConditionalStatement(space(level) +Q.IN+"\n");
        base_CSM.addConditionalStatement(space(level) +Q.LEFT_PARENTHESIS +"\n"+ exclude_Grant_From_Deny_CSM + "\n" +space(level) +Q.RIGHT_PARENTHESIS +"\n");

        return base_CSM;
	}

	/**
     * Make a SQL statment to find the tokens owned by a user
     * @param userId the user identifier
     * @return A SQL statement
     */
    public static String makeSQL_findTokensByUser(String userId){
        return makeSQL_findTokensByUser(1,userId, null, null).toSQLStatement();

    }

    /**
     * Make a SQL statment to find specified tokens owned by a user
     * @param userId the user identifier
     * @param crud the CRUD action type
     * @param isGrant true if return the token permission is grant; toherwise, return token which permission is deny
     * @return A SQL statement
     */
    private static CompoundStatementManual makeSQL_findTokensByUser(int level, String userId, Character crud, Boolean isGrant){
        CompoundStatementManual base_CSM = new CompoundStatementManual();

        base_CSM.addConditionalStatement(space(level) +Q.SELECT + TOKENINFO_TOKENID);
        base_CSM.addConditionalStatement(Q.FROM +"\n");
        base_CSM.addConditionalStatement(space(level) +Q.SPACE+USERTOKEN +"\n");
        base_CSM.addConditionalStatement(space(level) +Q.NATURAL_JOIN +"\n");
        base_CSM.addConditionalStatement(space(level) + Q.SPACE + TOKENINFO + "\n");
        base_CSM.addConditionalStatement(space(level) +Q.WHERE);
        base_CSM.addConditionalStatement(USERTOKEN_USERID + Q.E + Q.QUOT + userId + Q.QUOT);
        if(crud != null){
            base_CSM.addConditionalStatement(Q.AND);
            base_CSM.addConditionalStatement(TOKENINFO_CRUDTYPE + Q.E + Q.QUOT + crud + Q.QUOT);
        }
        if(isGrant != null){
            base_CSM.addConditionalStatement(Q.AND);
            base_CSM.addConditionalStatement(TOKENINFO_PERMISSION + Q.IS + isGrant);
        }

        return base_CSM;

    }

    public static String makeSQL_findColumnID(String tableName, String columnName){
    	int level = 1;
        CompoundStatementManual findColumnId_CSM = new CompoundStatementManual();
        findColumnId_CSM.addConditionalStatement(space(level)+COLUMNINFO_TABLENAME + Q.E + Q.QUOT + tableName + Q.QUOT);
        findColumnId_CSM.addConditionalStatement(Q.AND);
        findColumnId_CSM.addConditionalStatement(COLUMNINFO_COLUMNNAME + Q.E + Q.QUOT + columnName + Q.QUOT);
        return findColumnId_CSM.toSQLStatement();
    }

    public static String makeSQL_removeTokenFromColumn(List<Integer> columnIds, String tokenId, boolean isGrant){

    	int level = 1;
        String COLUMNSECURITYTABLE_TOKENID;
        String COLUMNSECURITYTABLE_COLUMID;
         if (isGrant){
              COLUMNSECURITYTABLE_TOKENID = Configuration.Column_Security_Grant_Table.getColumnTokenID();
              COLUMNSECURITYTABLE_COLUMID = Configuration.Column_Security_Grant_Table.getColumnColumnID();
         }else{
              COLUMNSECURITYTABLE_TOKENID = Configuration.Column_Security_Deny_Table.getColumnTokenID();
              COLUMNSECURITYTABLE_COLUMID = Configuration.Column_Security_Deny_Table.getColumnColumnID();
         }

         CompoundStatementAuto base_CSA  = new CompoundStatementAuto(Q.AND);
         CompoundStatementAuto columnId_CSA = new CompoundStatementAuto(Q.OR);
         base_CSA.addCompoundStatement(space(level)+COLUMNSECURITYTABLE_TOKENID + Q.E + Q.QUOT + tokenId + Q.QUOT);
         for(Integer columnId : columnIds){
             columnId_CSA.addCompoundStatement(COLUMNSECURITYTABLE_COLUMID + Q.E + columnId);
         }
         base_CSA.addCompoundStatement(columnId_CSA);
         return base_CSA.toSQLStatement();

    }

}

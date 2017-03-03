/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.security;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import coredb.config.Configuration;
import coredb.config.Configuration.Column_Info_Table;
import coredb.config.Configuration.Row_Security_Deny_Table;
import coredb.config.Configuration.Row_Security_Grant_Table;
import coredb.config.Configuration.Token_Info_Table;
import coredb.database.DatabaseConnection;
import coredb.database.DatabaseConnectionHelper;
import coredb.database.SchemaInfo;
import coredb.mode.RootPermission;
import coredb.sql.CompoundStatementAuto;
import coredb.sql.CompoundStatementManual;
import coredb.sql.Q;
import coredb.sql.SQLFactory;
import coredb.sql.SQLSecurityFactory;
import coredb.sql.SQLStatement;
import coredb.unit.AttributeClass;
import coredb.unit.CRUD;
import coredb.unit.EntityClass;
import coredb.unit.EntityInstance;
import coredb.utils.Helper;
import coredb.utils.HelperWithSecurity;


/**
 * This class includes all method to do CRUD actions with Security
 * 
 * @author sean
 */
public class SecurityCRUD {
	static List<EntityInstance> defaultRootTokens = null;

	public static void initialize(DatabaseConnection databaseConnection) {
		SecurityCRUD.defaultRootTokens = generateTokensInformationForEntity(databaseConnection);
	}
	
	/**
	 * This method creates a list of EntityInstances with security checking.
	 * 
	 * @param databaseConnection the current databaseConnection
	 * @param userId the id of the user who are launching this transaction
	 * @param tableName the name of the table of the list of EntityInstances
	 * @param entityInstances the list of EntityInstance need to create
	 * @return returns a list of list of EntityInstances. The first item is the passedEntities and the second one is the failed.
	 */
	// checked by vmc @1450
	///*
	public static ResultSecuritySystemList generateEntitiesWithSecurityForCreateAction(DatabaseConnection databaseConnection, String userId, String tableName, List<EntityInstance> entityInstances) {
		return generateEntitiesWithSecurityForCreateAction(databaseConnection, userId, tableName, entityInstances, defaultRootTokens);
	}
	

	public static ResultSecuritySystemList generateEntitiesWithSecurityForCreateAction(DatabaseConnection databaseConnection, String userId, String tableName, List<EntityInstance> entityInstances, List<EntityInstance> tokens) {
		List<EntityInstance> passedEntities = new LinkedList<EntityInstance>();
		List<EntityInstance> passedSystemEntities = new LinkedList<EntityInstance>();
		List<EntityInstance> failedEntities = new LinkedList<EntityInstance>();
		
		/**
		 * check column level security, creating does not need to validate row level security
		 */
		String columnSecuritySql = SQLSecurityFactory.makeSQL_findColumnsBasedOnColumnSecurity(userId, tableName, CRUD.CREATE); // System.out.println("SecuritySQL " + columnSecuritySql);
		List<EntityInstance> creatableColumns = DatabaseConnectionHelper.executeArbitraryQueryForCoreDB(databaseConnection, columnSecuritySql);

        if(creatableColumns.size() > 0) {		
			HashMap<String,String> hashColumnNames    = SecurityCRUD.hashColumnsName(creatableColumns);
			List<AttributeClass> primaryKeyAttributes = databaseConnection.getPrimaryAttributes(tableName);

			boolean hasRowSecurity		              = HelperWithSecurity.hasRowSecurityTables(databaseConnection, tableName);

        	for (EntityInstance entityInstance : entityInstances) {
				List<String> failedColumnNames = checkColumnSecurity(entityInstance.getAttributeNames(), hashColumnNames);

				// <#COREDB-COLUMN>
    			if (failedColumnNames.isEmpty()) {
					passedEntities.add(entityInstance);
    			} else {
    				failedEntities.add(entityInstance);
    				System.out.println(SecurityCRUD.class.getName() + "@createEntities: User " + userId + " " +
    						"is refused to create by column level security policy -- cannot create following columns");
    				System.out.println(failedColumnNames.toString());
    			}		
				// <#/COREDB-COLUMN>

				// <#COREDB-71 value=ROW>
				if (failedColumnNames.isEmpty() && hasRowSecurity) {
					/**
					 * THE MAPPING TABLE
					 * Generate the row security mapping information for each row
					 */
					String uuid = UUID.randomUUID().toString();
					EntityInstance mappingEntity = databaseConnection.createEntityInstanceFor(Configuration.Mapping_Table.makeMappingTableName(tableName),false);
					mappingEntity.set(Configuration.Tracing_Table.makeMappingTableColumnNameCoredbId(tableName), uuid);
					for(AttributeClass primaryKeyName : primaryKeyAttributes){
						mappingEntity.set(primaryKeyName.getName(), entityInstance.get(primaryKeyName.getName()));
					}
					passedSystemEntities.add(mappingEntity);

					/**
					 * THE ROWSECURITY INFORMATION
					 */
					List<EntityInstance> rowSecurityInstances =
						generateRowSecurityInformationForEntity(databaseConnection, tokens, mappingEntity, entityInstance);
					passedSystemEntities.addAll(rowSecurityInstances);
				}
  				// <#/COREDB-71>
    		}
        } else {
			failedEntities.addAll(entityInstances);
			System.out.println(SecurityCRUD.class.getName() + "@createEntities: User " + userId + "  is refused to create by column level security policy becuase user does not have create tokens");
		}
		ResultSecuritySystemList rssl = new ResultSecuritySystemList(passedEntities, passedSystemEntities, failedEntities);
		return rssl;
	}

	/**
	 * This method reads a list of EntityInstances with security checking.
	 * 
	 * @param databaseConnection the current databaseConnection
	 * @param userId the id of the user who are launching this transaction
	 * @param tableName the name of the table of the list of EntityInstances
	 * @param cs the sql conditional statement used to read.
	 * @return returns a list of list of EntityInstances. The first item is the passedEntities and the second one is the failed.
	 */
	// checked by vmc @1561
	public static ResultSecuritySystemList readEntities(DatabaseConnection databaseConnection, String userId, String tableName, SQLStatement cs) {
		// <COREDB-COLUMN>
  		String columnSecuritySql		= SQLSecurityFactory.makeSQL_findColumnsBasedOnColumnSecurity(userId, tableName, CRUD.READ);
		List<String> readableColumns	= Helper.convertEntityColumnsToStrings(DatabaseConnectionHelper.executeArbitraryQueryForCoreDB(databaseConnection, columnSecuritySql));
		if (readableColumns.size() == 0) {
			System.out.println(SecurityCRUD.class.getName() + "@readEntities: User " + userId + "  is refused to read by column level security policy ");
			return new ResultSecuritySystemList(new LinkedList<EntityInstance>(), null, null);
		}
		// </#COREDB-COLUMN>

		// <#COREDB-71>
		// <#COREDB-?>
		List<EntityInstance> entityInstances = null;
		if(HelperWithSecurity.hasRowSecurityTables(databaseConnection, tableName)) {
			String rowSecurityCheckedSql   = SQLSecurityFactory.makeSQL_readEntitiesWithRowSecurity(userId, cs, tableName, readableColumns);
			entityInstances				   = DatabaseConnectionHelper.executeArbitraryQueryForCoreDB(databaseConnection, SchemaInfo.refactorTableName(tableName), rowSecurityCheckedSql);
		}else{
			String finalSecurityCheckedSql = SQLFactory.makeSQL_findEntities(tableName, readableColumns, cs.toSQLStatement()); 
			entityInstances                = DatabaseConnectionHelper.executeArbitraryQueryForCoreDB(databaseConnection, SchemaInfo.refactorTableName(tableName), finalSecurityCheckedSql);
		}
		// <#COREDB-?>
		// </#COREDB-71>

		// <#COREDB-58>
  		List<EntityInstance> entityInstancesWithUnreadableValues  = HelperWithSecurity.setUnreadableColumns(databaseConnection, tableName, entityInstances, readableColumns);
		// </#COREDB-58>
		return new ResultSecuritySystemList(entityInstancesWithUnreadableValues, null, null);
	}

	/**
	 * This method updates a list of EntityInstances with security checking.
	 * 
	 * @param databaseConnection the current databaseConnection
	 * @param userId the id of the user who are launching this transaction
	 * @param tableName the name of the table of the list of EntityInstances
	 * @param entityInstances the list of EntityInstance need to update
	 * @return returns a list of list of EntityInstances. The first item is the passedEntities and the second one is the failed.
	 */
    public static ResultSecuritySystemList generateEntitiesWithSecurityForUpdateAction(DatabaseConnection databaseConnection, String userId, String tableName, List<EntityInstance> entityInstances) {
    	List<EntityInstance> passedEntities			= new LinkedList<EntityInstance>();
		List<EntityInstance> passedSystemEntities	= new LinkedList<EntityInstance>();
		List<EntityInstance> failedEntities			= new LinkedList<EntityInstance>();
		ResultSecuritySystemList rssl = new ResultSecuritySystemList(passedEntities, passedSystemEntities, failedEntities);

		if (entityInstances.isEmpty())   {
			System.out.println(SecurityCRUD.class.getName() + "@generateEntitiesWithSecurityForUpdateActionOld: Input list of EntityInstances is empty");
			return rssl;
		}

		// <#COREDB-COLUMN>
		String columnSecuritySql = SQLSecurityFactory.makeSQL_findColumnsBasedOnColumnSecurity(userId, tableName, CRUD.UPDATE);
		List<EntityInstance> updatableColumns = DatabaseConnectionHelper.executeArbitraryQueryForCoreDB(databaseConnection, columnSecuritySql);

		if (updatableColumns.isEmpty())  {
			System.out.println(SecurityCRUD.class.getName() + "@generateEntitiesWithSecurityForUpdateAction: User " + userId + "  is refused to update by column level security policy becuase user does not have update tokens");
			failedEntities.addAll(entityInstances);
			return rssl;
		}
		// </#COREDB-COLUMN>

		HashMap<String,String> updatableColumnNames	= SecurityCRUD.hashColumnsName(updatableColumns);// will optimised later
		EntityInstance tableNameInstance		= databaseConnection.createEntityInstanceFor(tableName, false);
		List<String> identifiedColumnNames		= tableNameInstance.getIdentifiableColumnNames(databaseConnection);

		EntityClass entityClass = databaseConnection.getEntityClass(tableName, false);
		for (EntityInstance entityInstance : entityInstances) {

			List<AttributeClass> updatedAttributeClasses = Helper.getUpdatedColumns(databaseConnection, entityInstance, entityClass);
			List<String> updatedAttributeNames = new LinkedList<String>();
			for(AttributeClass ac : updatedAttributeClasses) updatedAttributeNames.add(ac.getName());

			// <COREDB-COLUMN @sub="PER ROW">
			//if(!isPassedColumnSecurity(updatedAttributeNames, updatableColumnNames)) {
			List<String> failedColumnNames = checkColumnSecurity(updatedAttributeNames, updatableColumnNames);
			if(!failedColumnNames.isEmpty()) {
				failedEntities.addAll(entityInstances);
				System.out.println(SecurityCRUD.class.getName() + "@generateEntitiesWithSecurityForUpdateAction: User " + userId + 
						"  is refused to update by column level security policy -- has no permission to update the following columns");
				System.out.println(failedColumnNames.toString());
				return rssl;
			}
			// </#COREDB-COLUMN @sub="PER ROW">


			if(HelperWithSecurity.hasRowSecurityTables(databaseConnection, tableName)) {
				// get the conditional statement to select MID of the given EntityInstance
				CompoundStatementManual csm = new CompoundStatementManual();
				csm.addConditionalStatement(entityInstance.makeSQLOnIdentifiableAttributes(databaseConnection));

				String rowSecurityCheckedSql           = SQLSecurityFactory.makeSQL_findRowsByIdentifiableColumnsBasedOnCrud(userId, tableName, identifiedColumnNames, csm, CRUD.UPDATE);
				List<EntityInstance> updatableEntities = DatabaseConnectionHelper.executeArbitraryQueryForCoreDB(databaseConnection, tableName, rowSecurityCheckedSql);

				int size                               = updatableEntities.size();
				if(size == 1) {
					passedEntities.add(entityInstance);
				} else {
					System.out.println(SecurityCRUD.class.getName() + "@generateEntitiesWithSecurityForUpdateAction: User " + userId + "  is refused to update by row level security policy");
					failedEntities.add(entityInstance);
				}
			} else  {
				passedEntities.addAll(entityInstances);
				break;
			}
		}
		return rssl;
	}
    
	/**
	 * This method deletes a list of EntityInstances with security checking.
	 * 
	 * @param databaseConnection the current databaseConnection
	 * @param userId the id of the user who are launching this transaction
	 * @param tableName the name of the table of the list of EntityInstances
	 * @param entityInstances the list of EntityInstance need to delete
	 * @return returns a list of list of EntityInstances. The first item is the passedEntities and the second one is the failed.
	 */
    public static ResultSecuritySystemList generateEntitiesWithSecurityForDeleteAction(DatabaseConnection databaseConnection, String userId, String tableName, List<EntityInstance> entityInstances) {
    	List<EntityInstance> passedEntities = new LinkedList<EntityInstance>();
		List<EntityInstance> passedSystemEntities = new LinkedList<EntityInstance>();
		List<EntityInstance> failedEntities = new LinkedList<EntityInstance>();
		ResultSecuritySystemList rssl = new ResultSecuritySystemList(passedEntities, passedSystemEntities, failedEntities);
    	
		if(entityInstances.isEmpty()){
		    System.out.println(SecurityCRUD.class.getName() + "@generateEntitiesWithSecurityForDeleteAction: Input list of EntityInstances is empty");
		    return rssl;
		}
		
		// <#COREDB-COLUMN @description="sdfdsfds">
    	String columnSecuritySql                    = SQLSecurityFactory.makeSQL_findColumnsBasedOnColumnSecurity(userId, tableName, CRUD.DELETE);
		List<EntityInstance> deletableColumns       = DatabaseConnectionHelper.executeArbitraryQueryForCoreDB(databaseConnection, columnSecuritySql);
		HashMap<String,String> deletableColumnNames = SecurityCRUD.hashColumnsName(deletableColumns);
		EntityInstance entity                       = databaseConnection.createEntityInstanceFor(tableName, false);
		List<String> identifiedColumnNames          = entity.getIdentifiableColumnNames(databaseConnection);
		List<String> columnNames                    = entity.getAttributeNames();
		
		List<String> failedColumnNames              = checkColumnSecurity(columnNames, deletableColumnNames);
		if(!failedColumnNames.isEmpty()) {
			failedEntities.addAll(entityInstances);
			System.out.println(SecurityCRUD.class.getName() + "@generateEntitiesWithSecurityForDeleteAction : User " + userId + " " +
					"is refused by column security " + "becuase user has no permission to delete following Columns :");
			System.out.println(failedColumnNames.toString());
			
			return rssl;
		}
		// </#COREDB-COLUMN>
		
		// <#COREDB-ROW>
		if(!HelperWithSecurity.hasRowSecurityTables(databaseConnection, tableName)) {
			passedEntities.addAll(entityInstances);
			return rssl;
		}

		// get the conditional statement to select MID of the given list of EntityInstances
		CompoundStatementAuto csa = new CompoundStatementAuto(Q.OR);
		for(EntityInstance entityInstance : entityInstances) {
			CompoundStatementManual csm = new CompoundStatementManual();
			csm.addConditionalStatement(entityInstance.makeSQLOnIdentifiableAttributes(databaseConnection));
			csa.addCompoundStatement(csm);
		}
		
		String rowSecurityCheckedSql           = SQLSecurityFactory.makeSQL_findRowsByIdentifiableColumnsBasedOnCrud(userId, tableName, identifiedColumnNames, csa, CRUD.DELETE);
		List<EntityInstance> deletableEntities = DatabaseConnectionHelper.executeArbitraryQueryForCoreDB(databaseConnection, tableName, rowSecurityCheckedSql);
		int size                               = deletableEntities.size();
		if(size > 0) {
			passedEntities.addAll(deletableEntities);
			// might be throw exception when table has no PK columns, and input have UNREADABLEVALUE columnsableEntities);
			failedEntities.addAll(Helper.removeAll(databaseConnection, entityInstances, deletableEntities));
			if(!failedEntities.isEmpty()) System.out.println(SecurityCRUD.class.getName() + "@generateEntitiesWithSecurityForDeleteAction: This Transaction is failed because some input Entities can't be deleted by User : " + userId);
		} else {
			System.out.println(SecurityCRUD.class.getName() + "@generateEntitiesWithSecurityForDeleteAction: User " + userId + "  is refused to delete by row level security policy -- No input EntityInstance can be deleted");
			failedEntities.addAll(entityInstances);
		}
		// </#COREDB-ROW>	
		
		
		passedSystemEntities.addAll(readRelatedMappingAndSecurityEntities(databaseConnection, passedEntities));
		return rssl;
	}
    
	/**
	 * This method checks whether the leftList is a subset of the list of entity names of the rightlist. 
	 *  
	 * @param crudEDColumnNames the list of string
	 * @param rightList the list of EntityInstances
	 * @return returns true if leftList is a subset of the list of entity names of the rightlist. 
	 */
//	private static boolean isPassedColumnSecurity(List<String> leftList, HashMap<String,String> hashColumnNames) {
//		for (String attributeName : leftList) {
//			if (hashColumnNames.get(attributeName) == null) return false;
//		}
//
//		return true;
//	}
    private static List<String> checkColumnSecurity(List<String> crudEDColumnNames, HashMap<String,String> canCRUDColumns) {
    	List<String> failedColumnNames = new LinkedList<String>();
    	
		while(crudEDColumnNames.size() > 0) {
			if(canCRUDColumns.get(crudEDColumnNames.get(0)) == null) failedColumnNames.add(crudEDColumnNames.get(0));
			
			crudEDColumnNames.remove(0);
		}

		return failedColumnNames;
	}

	/**
	 *
	 * @param columnNames
	 * @return
	 */
	private static HashMap<String,String> hashColumnsName(List<EntityInstance> columnNames) {
		HashMap<String, String> hashColumnNames = new HashMap<String, String>();
		for (EntityInstance column : columnNames) {
			String columnName = column.get(Column_Info_Table.getColumnColumnName()).toString();
			hashColumnNames.put(columnName, columnName);
		}
		return hashColumnNames;
	}
	
	/**
	 * This method gets the related mapping table records and row security table records of given data rows
	 * 
	 *  @param rows the given data rows
	 *  @return returns the related mapping table records and row security table records
	 */
	private static List<EntityInstance> readRelatedMappingAndSecurityEntities(DatabaseConnection databaseConnection, List<EntityInstance> entities) {
		List<EntityInstance> needDeletedEntities = new LinkedList<EntityInstance>();
		List<EntityInstance> rows                = new LinkedList<EntityInstance>();
		for(EntityInstance entity : entities){
			rows.add(entity.clone());
		}
		EntityInstanceBucket rowBucket = new EntityInstanceBucket(rows);
		while (rowBucket.getIndexes().size() > 0) {
			List<EntityInstance> bucket = rowBucket.popNextBucket();
			String baseTableName = bucket.get(0).getDynaClass().getName();
			
			if(HelperWithSecurity.hasRowSecurityTables(databaseConnection, baseTableName)) {
				for(EntityInstance row : bucket){
	        		// read EntityID
	        		String sql = row.makeSQLOnIdentifiableAttributes(databaseConnection);
	        		String mappingTableName = Configuration.Mapping_Table.makeMappingTableName(baseTableName);
	        		List<EntityInstance> mappingEntityInstances = databaseConnection.read(mappingTableName, sql);
	        		needDeletedEntities.addAll(mappingEntityInstances);
	        		
	        		String entityId;
	            	if(mappingEntityInstances.size() == 1) {
	            		entityId = mappingEntityInstances.get(0).get(Configuration.Tracing_Table.makeMappingTableColumnNameCoredbId(baseTableName)).toString();
	            	}else continue;// might be need to throw an exception
	           		
	            	CompoundStatementAuto sqlStatementAuto = new CompoundStatementAuto(Q.AND);
	           		EntityInstance tableRowTokenPair = databaseConnection.createEntityInstanceFor(Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(baseTableName),false);
	           		tableRowTokenPair.set(Row_Security_Grant_Table.getColumnEntityInstanceID(), entityId);
	           		sqlStatementAuto.addCompoundStatement(tableRowTokenPair.makeSQLOnColumn(databaseConnection,Row_Security_Grant_Table.getColumnEntityInstanceID() , Q.E));
	           		
	           		// gets grant token row pairs and deny ones
	           		needDeletedEntities.addAll(databaseConnection.read(Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(baseTableName), sqlStatementAuto.toSQLStatement()));
	           		needDeletedEntities.addAll(databaseConnection.read(Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(baseTableName), sqlStatementAuto.toSQLStatement()));
	        	}
            }
		}
		
		return needDeletedEntities;
	}

//	/**
//	 * This method generates a list of RowTokenPairs based on the given list of EntityInstance, 
//	 * the list of Root Group tokens and the list of Default Group grant tokens  
//	 * 
//	 * @param databaseConnection the current working database connection
//	 * @param entityInstances the give list of EntityInstances
//	 * @return returns generates a list of RowTokenPairs
//	 */
//	public static List<EntityInstance> generateRowSecurityInformationForEntity(DatabaseConnection databaseConnection, List<EntityInstance> entityInstances) {
//		List<EntityInstance> rowSecurityInformationEntities = new LinkedList<EntityInstance>();
//		for(EntityInstance entity : entityInstances) {
//			rowSecurityInformationEntities.addAll(generateRowSecurityInformationForEntity(databaseConnection, entity));
//		}
//		
//		return rowSecurityInformationEntities;
//	}
//	
//	/**
//	 * This method generates a list of RowTokenPairs based on the given list of EntityInstance and the given list of tokens  
//	 * 
//	 * @param databaseConnection the current working database connection
//	 * @param tokens the given list of tokens
//	 * @param entityInstances the give list of EntityInstances
//	 * @return returns generates a list of RowTokenPairs
//	 */
//	public static List<EntityInstance> generateRowSecurityInformationForEntity(DatabaseConnection databaseConnection, List<EntityInstance> tokens, List<EntityInstance> entityInstances) {
//		List<EntityInstance> rowSecurityInformationEntities = new LinkedList<EntityInstance>();
//		for(EntityInstance entity : entityInstances) {
//			rowSecurityInformationEntities.addAll(generateRowSecurityInformationForEntity(databaseConnection, tokens,entity));
//		}
//		
//		return rowSecurityInformationEntities;
//	}

	/**
	 * This method generates a list of RowTokenPairs based on the given EntityInstance, 
	 * the list of Root Group tokens and the list of Default Group grant tokens  
	 * 
	 * @param databaseConnection the current working database connection
	 * @param mappingEntityInstance the related mapping table record of given EntityInstance
	 * @param entity the given EntityInstance
	 * @return returns a list of RowTokenPairs
	 */
	public static List<EntityInstance> generateRowSecurityInformationForEntity(DatabaseConnection databaseConnection, EntityInstance mappingEntityInstance, EntityInstance entity) {
		List<EntityInstance> tokens                         = generateTokensInformationForEntity(databaseConnection);
		return generateRowSecurityInformationForEntity(databaseConnection, tokens, mappingEntityInstance, entity);
	}
	
	/**
	 * This method generates a list of RowTokenPairs based on the given EntityInstance and the given list of tokens
	 * 
	 * @param databaseConnection the current working database connection
	 * @param tokens the given list of tokens
	 * @param mappingEntityInstance the related mapping table record of given EntityInstance
	 * @param entity the given EntityInstance
	 * @return returns a list of RowTokenPairs
	 */
	// checkec by vmc @1528
	public static List<EntityInstance> generateRowSecurityInformationForEntity(DatabaseConnection databaseConnection, List<EntityInstance> tokens, EntityInstance mappingEntityInstance, EntityInstance entity) {
		List<EntityInstance> rowSecurityInformationEntities = new LinkedList<EntityInstance>();
		String baseTableName                                = entity.getDynaClass().getName();
		
		for(EntityInstance token : tokens) {
			String mappingSId	= mappingEntityInstance.get(Configuration.Tracing_Table.makeMappingTableColumnNameCoredbId(baseTableName)).toString();
            String tokenId		= (String)token.get(Token_Info_Table.getColumnTokenID());
            EntityInstance tableRowTokenPair = null;
		    if ((Boolean)token.get(Token_Info_Table.getColumnPermission())) {
		        tableRowTokenPair = databaseConnection.createEntityInstanceFor(Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(baseTableName), false);
			    tableRowTokenPair.set(Row_Security_Grant_Table.getColumnEntityInstanceID(), mappingSId);
			    tableRowTokenPair.set(Row_Security_Grant_Table.getColumnTokenID(), tokenId);
		    } else {
		        tableRowTokenPair = databaseConnection.createEntityInstanceFor(Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(baseTableName), false);
			    tableRowTokenPair.set(Row_Security_Deny_Table.getColumnEntityInstanceID(), mappingSId);
			    tableRowTokenPair.set(Row_Security_Deny_Table.getColumnTokenID(), tokenId);
		    }
			rowSecurityInformationEntities.add(tableRowTokenPair);
		}
		return rowSecurityInformationEntities;
	}

	/**
	 * This method generates the list of Root Group tokens and the list of Default Group grant tokens
	 * 
	 * @param databaseConnection the current working database connection
	 * @return returns a list of tokens includes all tokens of root group and the grant tokens of default group
	 */
	public static List<EntityInstance> generateTokensInformationForEntity(DatabaseConnection databaseConnection) {
		List<EntityInstance> tokens = new LinkedList<EntityInstance>();

	    // add default tokens
		tokens.addAll(RootPermission.getRootTokens(databaseConnection));
		// add root tokens
		tokens.addAll(TokenFactory.getDefaultGrantTokens(databaseConnection));

		return tokens;
	}

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import coredb.config.Configuration;
import coredb.database.DatabaseConnection;
import coredb.database.JDBCTypeConverter;
import coredb.database.SchemaInfo;
import coredb.security.EntityInstanceBucket;
import coredb.security.ResultSecurity;
import coredb.common.UnreadableValue;
import coredb.sql.CompoundStatementAuto;
import coredb.sql.ConditionalStatement;
import coredb.sql.Q;
import coredb.sql.SQLFactory;
import coredb.sql.SQLSecurityFactory;
import coredb.unit.AttributeClass;
import coredb.unit.CRUD;
import coredb.unit.EntityClass;
import coredb.unit.EntityInstance;
import coredb.unit.EntitySecurityClass;
import coredb.database.DatabaseConnectionHelper;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.model.Table;


/**
 * 
 * @author vmc
 */
public class HelperWithSecurity {
	public static List<EntityInstance> setUnreadableColumns(DatabaseConnection databaseConnection, String tableName, List<EntityInstance> entityInstances, List<String> readableColumnNames) {
		List<String> columnNames = databaseConnection.createEntityInstanceFor(tableName, false).getAttributeNames();
		boolean result = columnNames.removeAll(readableColumnNames);
		List<String> unReadableColumnNames = columnNames;
		if(!unReadableColumnNames.isEmpty() && !entityInstances.isEmpty()) {
			for(EntityInstance entity : entityInstances) {
				for(String unreadableColumn : unReadableColumnNames) {
					entity.set(unreadableColumn, new UnreadableValue());
				}
			}
		}
		return entityInstances;
	}
	
	public static ResultSecurity setUnreadableColumns(DatabaseConnection databaseConnection, String userId, ResultSecurity resultSecurity) {
		
		EntityInstanceBucket passedEntityBucket = new EntityInstanceBucket(resultSecurity.getPassedEntities());
        EntityInstanceBucket failedEntityBucket = new EntityInstanceBucket(resultSecurity.getFailedEntities());
		
		List<EntityInstance> passedEntityInstances = setUnreadableColumns(databaseConnection, passedEntityBucket, userId);
		List<EntityInstance> failedEntityInstances = setUnreadableColumns(databaseConnection, failedEntityBucket, userId);
		
		return new ResultSecurity(passedEntityInstances, failedEntityInstances, resultSecurity.getChangeSetNumber());
	}

	private static List<EntityInstance> setUnreadableColumns(DatabaseConnection databaseConnection, EntityInstanceBucket entityBucket, String userId) {
		List<EntityInstance> resultEntities = new LinkedList<EntityInstance>();
		
		while (entityBucket.getIndexes().size() > 0) {
			List<EntityInstance> bucket = entityBucket.popNextBucket();
			
			String tableName = bucket.get(0).getDynaClass().getName();
			String columnSecuritySql = SQLSecurityFactory.makeSQL_findColumnsBasedOnColumnSecurity(userId, tableName, CRUD.READ);
		    List<EntityInstance> readableColumns = DatabaseConnectionHelper.executeArbitraryQueryForCoreDB(databaseConnection, columnSecuritySql);
		    List<String> readableColumnNames = new LinkedList<String>();
			for (EntityInstance column : readableColumns) {
				String columnName = column.get(Configuration.Column_Info_Table.getColumnColumnName()).toString();
				readableColumnNames.add(columnName);
			}

			List<String> columnNames = databaseConnection.createEntityInstanceFor(tableName, false).getAttributeNames();
			boolean result = columnNames.removeAll(readableColumnNames);
			List<String> unReadableColumnNames = columnNames;

			if(!unReadableColumnNames.isEmpty()) {
				for(EntityInstance entity : bucket) {
					for(String unreadableColumn : unReadableColumnNames) {
						entity.set(unreadableColumn, new UnreadableValue());
					}
				}
			}
			
			resultEntities.addAll(bucket);
		}
		
		return resultEntities;
	}
	//</#coredb-58>

	/**
	 * This function generates a entityclass from the database.The
	 * structure of entityclass will be matched with the tableName schema
	 * requested by tableName name.
	 * 
	 * @param databaseConnection current working database Connection 
     * @param tableName the tableName schema will be transformed into entityclass
     * @param reRead refresh the database handler or not.
     * @return retuens the entityclass of table which name is tableName
	 */
	// CHECKED BY VMC @462
	public static EntityClass getEntityClass(DatabaseConnection databaseConnection, String tableName, boolean reRead){
		return databaseConnection.getEntityClass(tableName, reRead);
	}
	
	/**
     * This function gets an attribute class given tableName name and attribute name.
     *
     * @param tableName the name of tableName that Attribute attached
     * @param attributeName the name of Attribute
     * @return AttributeClass
     */
	// CHECKED BY VMC @259
    public static AttributeClass getAttributeClass(String tableName, String attributeName) {
        return new AttributeClass(SchemaInfo.refactorColumnName(tableName, attributeName),JDBCTypeConverter.toJAVAType(SchemaInfo.refactorColumnType(tableName, attributeName)));
    }

	public static String getCompoundStatementOnPrimaryKeys(DatabaseConnection databaseConnection, EntityInstance entityInstance) {
		CompoundStatementAuto statement = new CompoundStatementAuto(Q.AND);

		/**
		 * this block is designed to get right table name when entityInstance is result of tracing
		 */
		String tableName = entityInstance.getDynaClass().getName();
		if(tableName.endsWith(Configuration.LAZYDYNACLASS)){
			tableName = tableName.split(Configuration.TABLE_DELIMITER)[0];
		}
		
		Table table			= databaseConnection.getTable(tableName, false);
		for(Column column : table.getColumns()){
			if (column.isPrimaryKey())  {
				AttributeClass ac	= getAttributeClass(table.getName(),column.getName());
				statement.addCompoundStatement(new ConditionalStatement(ac, Q.E,entityInstance.get(column.getName())));
			}
		}
		return statement.toSQLStatement();
	}

	/**
	 * Convert the Byte array data to the valid data(specified by AttributeClass)
	 * @param ac the AttributeClass object
	 * @param byt the Byte array
	 * @return the valid data as Object
	 */
	// By Stephen
	public static Object convertByteArrayToValidData(AttributeClass ac, byte[] byt){
		String value    = new String(byt);
		Class classType = JDBCTypeConverter.toJAVAType(ac.getSqlType());
		if (classType.equals(Boolean.class))            return Boolean.valueOf(value);
		else if (classType.equals(byte[].class))        return byt;
		else if (classType.equals(Character.class)){
			if(value.length() == 1)                     return value.charAt(0);
			else{
				System.err.printf("JDBC TYPE : %20s IS NOT SUPPORTED BY COREDB API");
				System.exit(0);
				return null;
			}
		}
		else if (classType.equals(Date.class)) 		    return Date.valueOf(value);
		else if (classType.equals(Double.class)) 		return Double.parseDouble(value);
		else if (classType.equals(Integer.class)) 	    return Integer.parseInt(value);
		else if (classType.equals(String.class)) 	    return value;
		else if (classType.equals(Time.class)) 		    return Time.valueOf(value);
		else if (classType.equals(Timestamp.class))     return Timestamp.valueOf(value);
		else{
			System.err.printf("JDBC TYPE : %20s IS NOT SUPPORTED BY COREDB API");
			System.exit(0);
			return null;
		}
	}
	
	/**
	 * Return a list of columns which values are modified in a update action. (ADDED BY SEAN)
	 * @param databaseConnection current working database Connection
	 * @param newEntity the entity instance which is updated by the user
	 * @param entityClass the entity class of the updated entity instance
	 * @return a list of columns which values are modified in a update action.
	 */
	// checked by vmc @923
	public static <T extends EntityClass> List<AttributeClass> getUpdatedColumns(DatabaseConnection databaseConnection, EntityInstance newEntity, T entityClass){
		List<AttributeClass> updatedColumns  = new LinkedList<AttributeClass>();

		CompoundStatementAuto csla = new CompoundStatementAuto(Q.AND);
		for(AttributeClass attribute : entityClass.getAttributeClassList()){
			if(attribute.isPrimaryKey()){
				csla.addCompoundStatement(new ConditionalStatement(attribute, Q.E, newEntity.get(attribute.getName())));
			}
		}

		String tableName                = entityClass.getName();

		List<EntityInstance> eil        = new LinkedList<EntityInstance>();

		String sql                      = SQLFactory.makeSQL_findEntities(tableName, csla);
    	Connection connection           = databaseConnection.getConnection();
        ResultSet resultSet             = null;
        try {
            PreparedStatement ps        = connection.prepareStatement(sql);
            resultSet = ps.executeQuery();
            eil = DatabaseConnectionHelper.convertResultSetWithUniqueAttributeToEntityInstance(databaseConnection, tableName, resultSet);//PublicFunctions.convertResultSetWithUniqueAttributeToEntityInstance(databaseConnection, tableName, resultSet);
        } catch (SQLException ex) {
            Logger.getLogger(HelperWithSecurity.class.getName()).log(Level.SEVERE, null, ex);
        }


		if(eil.size() == 1){
			EntityInstance oldEntity    = eil.get(0);
			for(AttributeClass attributeClass : entityClass.getAttributeClassList()){
				Object oldObject = oldEntity.get(attributeClass.getName());
				Object newObject = newEntity.get(attributeClass.getName());
				
				//<#coredb-58>
				if(newObject != null && newObject.getClass().equals(UnreadableValue.class)) {
					newEntity.set(attributeClass.getName(), oldObject);
					continue;
				}
				//</#coredb-58>
				
				if (JDBCTypeConverter.toJAVAType(SchemaInfo.refactorColumnType(tableName, attributeClass.getName())) == byte[].class 
            			&& Helper.ByteArraysEqual((byte[])oldObject, (byte[])newObject)) continue;
				
				if((oldObject != null && newObject ==null) || (oldObject == null && newObject !=null) ||
				  ((oldObject != null && newObject !=null) && !oldObject.equals(newObject))){
					updatedColumns.add(attributeClass);
				}
			}
		} else {
			System.err.println("Your primary key is not really a primary key. Duplicate Result!");
		}
		return updatedColumns;
	}

	/**
	 * This function generates a EntitySecurityClass from the database.The
	 * structure of EntitySecurityClass will be matched with the tableName schema
	 * requested by tableName name.
	 * 
	 * @param databaseConnection current working database Connection 
     * @param tableName the tableName schema will be transformed into EntitySecurityClass
     * @param reRead refresh the database handler or not.
     * @return retuens the EntitySecurityClass of table which name is tableName
	 */
	public static EntitySecurityClass getEntitySecurityClass(DatabaseConnection databaseConnection, String tableName, boolean reRead){
		EntityClass ec = getEntityClass(databaseConnection, tableName, reRead);
		return new EntitySecurityClass(ec.getName(), ec.getAttributeClassList(), ec.getIndexClassList());
	}

	// #COREDB-71
	public static boolean hasRowSecurityTables(DatabaseConnection databaseConnection, String baseTableName) {
		String rowSecurityTableNameGrant = Configuration.Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(baseTableName);
		String rowSecurityTableNameDeny  = Configuration.Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(baseTableName);
		
		/* set the argument is false to fix the problem mentioned in COREDBUSERS-18*/
		List<String> tableNames = databaseConnection.readTableNameList(false);// does not to refresh
		for (String tableName : tableNames) {
			if(tableName.equalsIgnoreCase(rowSecurityTableNameGrant) || tableName.equalsIgnoreCase(rowSecurityTableNameDeny)) return true; }
		return false;
	}
	// #/COREDB-71
	
	/**
	 * This method splits a list of EntityInstances into a hashmap of EntityInstances by tableName. 
	 * The key of map is the tableName and the value is a list of EntityInstances belong to this table
	 * 
	 * @param entityInstances the list of EntityInstances
	 * @return a hashmap whose key is the tableName and its value is a list of EntityInstances belong to this table
	 */
	/*
	public static HashMap<String, List<EntityInstance>> splitEntityInstancesByTable(List<EntityInstance> entityInstances) {
		HashMap<String, List<EntityInstance>> map = new LinkedHashMap<String, List<EntityInstance>>();
		
		for(EntityInstance entityInstance : entityInstances){
			String tableName = entityInstance.getDynaClass().getName();
			if(map.get(tableName) == null){
				List<EntityInstance> entities = new LinkedList<EntityInstance>();
				entities.add(entityInstance);
				map.put(tableName, entities);
			}else {	map.get(tableName).add(entityInstance);}
		}
		
		return map;
	}
	 */
	
	/**
	 * This method compare two byte[]
	 * 
	 * @param ba1 the left comparer
	 * @param ba2 the right comparer
	 * @return returns true if these two byte[] matched
	 */
	public static boolean ByteArraysEqual(byte[] ba1, byte[] ba2)
	{
		if ( ba1 == null && ba2 == null) return true;
		else if (ba1 != null && ba2 != null) {
		    if ( ba1.length != ba2.length ) return false;
			for(int i=0; i < ba1.length; i++) {
    			if ( ba1[i] != ba2[i] )
	    		return false;
			}
			return true;
		} else return false;
	}
}

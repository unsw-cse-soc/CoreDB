/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.utils;

import java.util.LinkedList;
import java.util.List;

import coredb.config.Configuration;
import coredb.database.DatabaseConnection;
import coredb.database.DatabaseConnectionHelper;
import coredb.database.JDBCTypeConverter;
import coredb.database.SchemaInfo;
import coredb.common.UnreadableValue;
import coredb.sql.CompoundStatementAuto;
import coredb.sql.ConditionalStatement;
import coredb.sql.Q;
import coredb.sql.SQLFactory;
import coredb.unit.AttributeClass;
import coredb.unit.EntityClass;
import coredb.unit.EntityInstance;
import draft.PublicFunctions;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vmc
 */
public class Helper {
	/**
	 * This method compare two byte[]
	 *
	 * @param ba1 the left comparer
	 * @param ba2 the right comparer
	 * @return returns true if these two byte[] matched
	 */
	// checked by vmc @1805
	public static boolean ByteArraysEqual(byte[] ba1, byte[] ba2) {
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
	
	/**
	 * This method compare two date
	 *
	 * @param date1 the left comparer
	 * @param date2 the right comparer
	 * @return returns true if these two date matched
	 */
	// checked by vmc @1805
	public static boolean DateEqualOnDayAcurrate(Date date1, Date date2) {
		if ( date1 == null && date2 == null) return true;
		else if (date1 != null && date2 != null) {
			if(date1.getYear() == date2.getYear() && date1.getMonth() == date2.getMonth() && date1.getDay() == date2.getDay())
				return true;
		} 
		return false;
	}

	/**
	 * This method converts a list of EntityInstance(columns) to a list of string
	 * @param columns the given list of columns
	 * @return a list of strings contain the name of columns
	 */
	public static List<String> convertEntityColumnsToStrings(List<EntityInstance> columns) {
		List<String> columnNames = new LinkedList<String>();
		if (columns != null && !columns.isEmpty()) {
  			for (EntityInstance column : columns) {
  				String columnName = column.get(Configuration.Column_Info_Table.getColumnColumnName()).toString();
  				columnNames.add(columnName);
  			}
		} 
		
		return columnNames;
	}
	
	/**
	 * This method remove all items of rightInstances from leftInstances
	 * @param databaseConnection the current working DB connection
	 * @param leftInstances 
	 * @param rightInstances
	 * @return the list of rest EntityInstances in leftInstances
	 */
	public static List<EntityInstance> removeAll(DatabaseConnection databaseConnection, List<EntityInstance> leftInstances, List<EntityInstance> rightInstances) {
		List<EntityInstance> resultInstances = new LinkedList<EntityInstance>();
		
		while (leftInstances.size() > 0) {
			
			if (!Helper.contains(databaseConnection, rightInstances, leftInstances.get(0))) {
				resultInstances.add(leftInstances.get(0));
			}
			leftInstances.remove(0);
		}
		
		return resultInstances;
	}
	
	/**
	 * This method check whether entityInstances contains entity
	 * @param databaseConnection the current working DB connection
	 * @param entityInstances the given list of EntityInstances
	 * @param entity the given EntityInstance
	 * @return returns true if entityInstances contains entity, otherwise returns false
	 */
	public static boolean contains(DatabaseConnection databaseConnection, List<EntityInstance> entityInstances, EntityInstance entity) {
		
		for (int i = 0; i < entityInstances.size(); i++) {
			if(entityInstances.get(i).equalsOnPrimaryColumns(databaseConnection, entity))
				return true;
		}
		
		return false;
	}
	
	public static AttributeClass[] primaryKeyFilter(AttributeClass[] columns){
		List<AttributeClass> nonPrimarykeyAttributes = new LinkedList<AttributeClass>();
		for(AttributeClass attribute : columns) {
			if(!attribute.isPrimaryKey())    	nonPrimarykeyAttributes.add(attribute);
		}
		return nonPrimarykeyAttributes.toArray(new AttributeClass[0]);
	}

	/**
	 * Return a list of columns which values are modified in a update action. (ADDED BY SEAN)
	 * @param <T>
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
            Logger.getLogger(PublicFunctions.class.getName()).log(Level.SEVERE, null, ex);
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
}

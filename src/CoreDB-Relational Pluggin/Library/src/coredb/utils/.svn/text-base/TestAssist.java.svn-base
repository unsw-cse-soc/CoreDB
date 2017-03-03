/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.utils;

import java.sql.Date;
import java.util.Collection;
import java.util.List;

import coredb.database.DatabaseConnection;
import coredb.common.UnreadableValue;
import coredb.unit.AttributeClass;
import coredb.unit.EntityInstance;
import coredb.unit.EntitySecurityClass;

/**
 *
 * @author vmc
 */
public class TestAssist {
	// o(n)^2 can be make to onlnn later
	public static boolean compare(DatabaseConnection databaseConnection, Collection<EntityInstance> l, Collection<EntityInstance> r) {

		if(!l.isEmpty() && !r.isEmpty() && l.size() == r.size()) {
			for(EntityInstance leftEntity : l) {
				boolean isEqual = false;
				for(EntityInstance rightEntity : r) {
					if(compare(databaseConnection, leftEntity, rightEntity)) {
						isEqual = true;
						break;
					}
				}
				if(isEqual == false) return isEqual;
			}
			return true;
		} else if (l.isEmpty() && r.isEmpty()) return true;
		  else                                 return false;
	}
	
	public static boolean compare(DatabaseConnection databaseConnection, EntityInstance l, EntityInstance r) {

		if (EntityInstance.isSameEntityClass(l, r)) {
            String tableName = l.getDynaClass().getName();
            EntitySecurityClass esc = HelperWithSecurity.getEntitySecurityClass(databaseConnection, tableName, true);
            AttributeClass[] attributeClasses = esc.getAttributeClassList();
            for(AttributeClass ac : attributeClasses) {
            	Object leftObject = l.get(ac.getName());
            	Object rightObject = r.get(ac.getName());
            	
            	if((ac.isPrimaryKey() && (leftObject == null || rightObject == null))) continue;
            	else if ((leftObject == null && rightObject == null)) continue;
            	else if (leftObject.getClass().equals(UnreadableValue.class) || rightObject.getClass().equals(UnreadableValue.class)) continue;
            	else if (leftObject.equals(rightObject)) continue;
            	else if (leftObject instanceof byte[] && rightObject instanceof byte[]) {
//            		String leftString = new String((byte[])leftObject);
//            		String rightString = new String((byte[])rightObject);
//            		if (leftString.equalsIgnoreCase(rightString)) continue;
            		if (Helper.ByteArraysEqual((byte[]) leftObject, (byte[]) rightObject)) continue;
            	}
            	else if (leftObject.getClass().equals(Character.class)) {
            		String leftString = leftObject.toString();
            		String rightString = rightObject.toString();
            		if(leftString.equalsIgnoreCase(rightString)) continue; 
            	}
            	else if (leftObject.getClass().equals(Date.class)) {
//            		String leftString = leftObject.toString();
//            		String rightString = rightObject.toString();
            		if(Helper.DateEqualOnDayAcurrate((Date)leftObject, (Date)rightObject)) continue; 
            	}
            	else return false;
            }
			
			return true;
		} else return false;
	}
	
	public static boolean equalsOnPrimaryColumns(DatabaseConnection databaseConnection, EntityInstance l, EntityInstance r) {

		if(EntityInstance.isSameEntityClass(l, r)) {
            String tableName = l.getDynaClass().getName();
            EntitySecurityClass esc = HelperWithSecurity.getEntitySecurityClass(databaseConnection, tableName, true);
            AttributeClass[] attributeClasses = esc.getAttributeClassList();
            for(AttributeClass ac : attributeClasses) {
            	Object leftObject = l.get(ac.getName());
            	Object rightObject = r.get(ac.getName());
            	
            	if((ac.isPrimaryKey()) && leftObject != null && rightObject != null) {
            	    if(leftObject.equals(rightObject)) continue;
            		else if (leftObject instanceof byte[] && rightObject instanceof byte[]) {
                		String leftString = new String((byte[])leftObject);
                		String rightString = new String((byte[])rightObject);
                		if(leftString.equalsIgnoreCase(rightString)) continue;
                	} 
            		else if (leftObject.getClass().equals(Character.class)) {
                		String leftString = leftObject.toString();
                		String rightString = rightObject.toString();
                		if(leftString.equalsIgnoreCase(rightString)) continue; 
                	} else return false;
            	}
            }
			return true;
		} else return false;
	}
	
	/**
	 * leftList is subSet of rightList
	 * @param databaseConnection
	 * @param leftList
	 * @param rightList
	 * @return returns true if leftList is subset of rightList, otherwise returns false
	 */
	public static boolean isSubSet(DatabaseConnection databaseConnection, List<EntityInstance> leftList, List<EntityInstance> rightList) {
		if(!leftList.isEmpty() && !leftList.isEmpty() && leftList.size() <= rightList.size()) {
			for(EntityInstance leftEntity : leftList) {
				if(!contains(databaseConnection, rightList, leftEntity)) return false;
			}
			return true;
		} else if (leftList.isEmpty() && rightList.isEmpty()) return true;
		else return false;
	}
	
	/**
	 * entities contains entity
	 * @param databaseConnection
	 * @param entities
	 * @param entity
	 * @return returns true if entities contains entity, otherwise returns false
	 */
	public static boolean contains(DatabaseConnection databaseConnection, List<EntityInstance> entities, EntityInstance entity) {
		boolean isEqual = false;
		for(EntityInstance entityInstance : entities) {
			if(compare(databaseConnection, entity, entityInstance)) {
				isEqual = true;
				break;
			}
		}
		
        return isEqual;
	}

	public static boolean noCommonSubSet(DatabaseConnection databaseConnection,	List<EntityInstance> leftList,
			List<EntityInstance> rightList) {
		if(!leftList.isEmpty() && !leftList.isEmpty()) {
			for(EntityInstance leftEntity : leftList) {
				for(EntityInstance rightEntity : rightList) {
					if(compare(databaseConnection, leftEntity, rightEntity)) {
						return false;
					}
				}
			}
			return true;
		} else return true;
	}

	
}

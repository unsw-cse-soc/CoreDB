/**
 * 
 */
package checkers;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.beanutils.DynaProperty;
import coredb.controller.IEntityControllerBase;
import coredb.unit.AttributeClass;
import coredb.unit.ClassToTables;
import coredb.unit.EntityClass;
import coredb.unit.EntityInstance;
import org.apache.ddlutils.model.TypeMap;

/**
 * This class defines assistant methods for testing
 * @author sean
 */
public class AssistantFunctions {

	/**
	 * 
	 * @param left the left entity instance
	 * @param right the right entity instance
	 * @return returns true is left equal to right, otherwise returns false
	 */
	public static boolean isEqual(EntityInstance left, EntityInstance right)
	{
		DynaProperty[] leftProperties = left.getDynaClass().getDynaProperties();
		DynaProperty[] rightProperties = right.getDynaClass().getDynaProperties();
		
		if(leftProperties.length != rightProperties.length) return false;
		
		HashMap<String, Object> map = new LinkedHashMap<String, Object>();
		
		for (DynaProperty property : left.getDynaClass().getDynaProperties())
		{
			map.put(property.getName(), left.get(property.getName()));
		}
			
		for(DynaProperty property : right.getDynaClass().getDynaProperties())
		{
			String name = property.getName();
			if(!(map.get(name) != null && map.get(name).equals(right.get(name))))
			{
				return false;
			}
		}
		return true;
	}
	/**
	 * 
	 * @param left the left list of entity instances
	 * @param right the right list of entity instances
	 * @return returns true is left equal to right, otherwise returns false
	 */
	public static boolean isEqual(List<EntityInstance> left, List<EntityInstance> right)
	{
		if(left.size() != right.size()) return false;
		
		for(EntityInstance leftEntity : left)
		{
			int num = 0;
			for(EntityInstance rightEntity : right)
			{
				//if(isEqual(leftEntity, rightEntity))break;
				if(leftEntity.equals(rightEntity))break;
				else{if(num == right.size() - 1) return false;}
				num ++;
			}
		}
		return true;
		
	}
	/**
	 * Create table
	 * @param iec IEntityController
	 * @param tableName the name of table
	 */
	public static void createTable(IEntityControllerBase iec, String tableName)
	{
		List<AttributeClass> acl = new LinkedList<AttributeClass>();// case 1:student
		AttributeClass ac1 = new AttributeClass("ID",           Integer.class,  TypeMap.INTEGER);
		ac1.setPrimaryKey(true);// is PK
		AttributeClass ac2 = new AttributeClass("PASSPORT",     Integer.class,  TypeMap.INTEGER);
		AttributeClass ac3 = new AttributeClass("NAME",         String.class,   TypeMap.VARCHAR);
		AttributeClass ac4 = new AttributeClass("GRADE",        String.class,   TypeMap.VARCHAR);
		AttributeClass ac5 = new AttributeClass("COURSE_CODE",  String.class,   TypeMap.VARCHAR);
		acl.add(ac1);
		acl.add(ac2);
		acl.add(ac3);
		acl.add(ac4);
		acl.add(ac5);
		
        ClassToTables table = null;
        table = new EntityClass(tableName, acl.toArray(new AttributeClass[0]), null);
        
        List<ClassToTables> entityClasses = new LinkedList<ClassToTables>();
        entityClasses.add(table);
        iec.deployDefinitionList(entityClasses,true,true);
	}
	/**
	 * Create Entities and inserts them into database
	 * @param iec IEntityController
	 * @param tableName the name of table
	 * @return returns a list of entities
	 */
	public static List<EntityInstance> createEntityInstances(IEntityControllerBase iec, String tableName)
	{
		List<EntityInstance> entities = new LinkedList<EntityInstance>();
		
		EntityInstance entity;

		for (int id = 0; id < 1000; id++) {
            entity = iec.createEntityInstanceFor(tableName);

            entity.set("ID", id);
			entity.set("PASSPORT", id);
			entity.set("NAME", "Sean" + id);
			entity.set("GRADE", "DN");
			entity.set("COURSE_CODE", "COMP9331");
			
			iec.createEntity(entity);
			entities.add(entity);
		}
		
		return entities;
	}
}

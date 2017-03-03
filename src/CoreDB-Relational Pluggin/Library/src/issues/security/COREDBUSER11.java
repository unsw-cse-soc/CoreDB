/**
 * 
 */
package issues.security;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import coredb.controller.EntityControllerBase;
import coredb.unit.AttributeClass;
import coredb.unit.ClassToTables;
import coredb.unit.EntityClass;
import coredb.unit.EntityInstance;
import coredb.unit.IndexClass;

/**
 * @author sean
 *
 */
public class COREDBUSER11 {

	private static EntityControllerBase iecb        = null;
	private static final String TESTTABLE_TABLENAME1 = "TESTTABLE1";
	private static final String TESTTABLE_TABLENAME2 = "TESTTABLE2";
	private static final String INTEGER_COLUMN      = "INTEGER_COLUMN";
	private static final String STRING_COLUMN       = "STRING_COLUMN";
	private static final String DOUBLE_COLUMN       = "DOUBLE_COLUMN";
	private static final String BOOLEAN_COLUMN      = "BOOLEAN_COLUMN";
	
	
	private static boolean deployDefinitionList(String tableName) {
		List<ClassToTables> tables = new LinkedList<ClassToTables>();
		/**
		 * TABLE 1: TESTTABLE
		 */
		List<AttributeClass> STU_acl = new LinkedList<AttributeClass>();
		AttributeClass STU_ac1 =  new AttributeClass(INTEGER_COLUMN,       Integer.class);
		STU_ac1.setPrimaryKey(true);
		STU_acl.add(STU_ac1);
		
		AttributeClass STU_ac2 = new AttributeClass(STRING_COLUMN,      String.class);
		STU_acl.add(STU_ac2);
		
		AttributeClass STU_ac3 = new AttributeClass(DOUBLE_COLUMN,      Double.class);
		STU_acl.add(STU_ac3);
		
		AttributeClass STU_ac4 = new AttributeClass(BOOLEAN_COLUMN,     Boolean.class);
		STU_acl.add(STU_ac4);

		IndexClass indexClass = new IndexClass(new AttributeClass[]{STU_ac3, STU_ac4}, true);
		
		ClassToTables student = new EntityClass(tableName, STU_acl.toArray(new AttributeClass[0]), new IndexClass[]{indexClass});
		tables.add(student);
		
        iecb.deployDefinitionList(tables);
        
//        Describer.describeClassToTables(tables);
        
		return true;
	}
	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		begin();
		
		createEntity(iecb);
		createEntities(iecb);
		
	}
	private static void begin() throws SQLException {
		iecb = new EntityControllerBase("/tmp/credential.txt");
		iecb.dropAllTables();
		deployDefinitionList(TESTTABLE_TABLENAME2);
		deployDefinitionList(TESTTABLE_TABLENAME1);
	}

	private static void createEntities(EntityControllerBase iecb2) throws SQLException {
		
		List<EntityInstance> entities = new LinkedList<EntityInstance>();
		EntityInstance entity1 = iecb.createEntityInstanceFor(TESTTABLE_TABLENAME2);
		entity1.set(INTEGER_COLUMN, 1);
		entity1.set(STRING_COLUMN,  "this is the first row");
		entity1.set(DOUBLE_COLUMN,  1.0);
		entity1.set(BOOLEAN_COLUMN, true);
		
		EntityInstance entity2 = iecb.createEntityInstanceFor(TESTTABLE_TABLENAME2);
		entity2.set(INTEGER_COLUMN, 2);
		entity2.set(STRING_COLUMN,  "this is the second row");
		entity2.set(DOUBLE_COLUMN,  1.0);
		entity2.set(BOOLEAN_COLUMN, true);
		
		System.out.println("this is the end");
		if(iecb.getDatabaseConnection().getConnection().isClosed())
			System.out.println("the connection is dead");
		
		EntityInstance entity3 = iecb.createEntityInstanceFor(TESTTABLE_TABLENAME2);
		entity3.set(INTEGER_COLUMN, 3);
		entity3.set(STRING_COLUMN,  "this is the second row");
		entity3.set(DOUBLE_COLUMN,  4.0);
		entity3.set(BOOLEAN_COLUMN, true);
		
		entities.add(entity1);
		entities.add(entity2);
		entities.add(entity3);
        iecb.createEntities(entities);
        
        List<EntityInstance> entityInstances = new LinkedList<EntityInstance>();
        entityInstances.add(entity1);
        entityInstances.add(entity3);
        iecb.createEntities(entityInstances);
		
	}
	private static void createEntity(EntityControllerBase iecb2) throws SQLException {
		
		EntityInstance entity1 = iecb.createEntityInstanceFor(TESTTABLE_TABLENAME1);
		entity1.set(INTEGER_COLUMN, 1);
		entity1.set(STRING_COLUMN,  "this is the first row");
		entity1.set(DOUBLE_COLUMN,  1.0);
		entity1.set(BOOLEAN_COLUMN, true);
		
		//try {
			iecb.createEntity(entity1);
		//} catch (Exception e) {
		//	System.out.println("Catch You");
		//}
		
		EntityInstance entity2 = iecb.createEntityInstanceFor(TESTTABLE_TABLENAME1);
		entity2.set(INTEGER_COLUMN, 2);
		entity2.set(STRING_COLUMN,  "this is the second row");
		entity2.set(DOUBLE_COLUMN,  1.0);
		entity2.set(BOOLEAN_COLUMN, true);
		
		try {
			iecb.createEntity(entity2);
		} catch (Exception e) {
			System.out.println("Catch You");
		}

		EntityInstance entity3 = iecb.createEntityInstanceFor(TESTTABLE_TABLENAME1);
		entity3.set(INTEGER_COLUMN, 3);
		entity3.set(STRING_COLUMN,  "this is the second row");
		entity3.set(DOUBLE_COLUMN,  4.0);
		entity3.set(BOOLEAN_COLUMN, true);

		iecb.createEntity(entity3);
		
	}
}

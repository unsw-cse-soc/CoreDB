/**
 * 
 */
package issues.security;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;
import coredb.controller.EntityControllerBase;
import coredb.controller.IEntityControllerBase;
import coredb.unit.AttributeClass;
import coredb.unit.ClassToTables;
import coredb.unit.EntityClass;
import coredb.unit.EntityInstance;
import coredb.utils.Describer;

/**
 * @author sean
 *
 */
public class COREDB99 {
	private IEntityControllerBase iecb               = null;
	private static final String TESTTABLE_TABLENAME = "TESTTABLE";
	private static final String INTEGER_COLUMN      = "INTEGER_COLUMN";
	private static final String STRING_COLUMN       = "STRING_COLUMN";
	private static final String DOUBLE_COLUMN       = "DOUBLE_COLUMN";
	private static final String DATE_COLUMN         = "DATE_COLUMN";
	private static final String BOOLEAN_COLUMN      = "BOOLEAN_COLUMN";
	private static final String CHAR_COLUMN         = "CHAR_COLUMN";
	private static final String TIME_COLUMN         = "TIME_COLUMN";
	private static final String TIMESTAMP_COLUMN    = "TIMESTAMP_COLUMN";
	private static final String BYTEA_COLUMN        = "BYTEA_COLUMN";
	
	private void setup() {
		try {
			iecb = new EntityControllerBase("/tmp/credential.txt");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		iecb.dropAllTables();
	}
	
	private void tearDown() {
		iecb.dropAllTables();
		iecb = null;
	}
	
	public void begin(){
		 deployDefinitionList(null);
	}
	
	public void end(){
		 List<String> tableNames = new LinkedList<String>();
	     tableNames.add(TESTTABLE_TABLENAME);
	     iecb.dropTables(tableNames);
	}
	
	private List<EntityInstance> generateEntities(IEntityControllerBase iec){
		List<EntityInstance> entityInstances = new LinkedList<EntityInstance>();
		
		// create Time, Date and TimeStamp objects
		Time currentTime = new Time(1, 1, 1);
		Date day = null;
		day = Date.valueOf("1981-01-01");
		Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		
		EntityInstance student;

		// data for 'student'
		student = iec.createEntityInstanceFor(TESTTABLE_TABLENAME);
		student.set(INTEGER_COLUMN,1);
		student.set(STRING_COLUMN, "STU0001");
		student.set(DOUBLE_COLUMN, 88.4);
		student.set(DATE_COLUMN, day);
		student.set(BOOLEAN_COLUMN, false);
//		student.set(CHAR_COLUMN, Character.valueOf('Y'));
//		student.set(TIME_COLUMN, currentTime);
//		student.set(TIMESTAMP_COLUMN, currentTimestamp);
		student.set(BYTEA_COLUMN, "COMP9331".getBytes());

		// add beans into list
		entityInstances.add(student);
		
		 // create entity with null value
        EntityInstance entityInstance = student.clone();
        entityInstance.set(INTEGER_COLUMN, 2);
        entityInstance.set(STRING_COLUMN, null);
        entityInstances.add(entityInstance);
        
        entityInstance = student.clone();
        entityInstance.set(INTEGER_COLUMN, 3);
        entityInstance.set(DOUBLE_COLUMN, null);
        entityInstances.add(entityInstance);
        
        entityInstance = student.clone();
        entityInstance.set(INTEGER_COLUMN, 4);
        entityInstance.set(DATE_COLUMN, null);
        entityInstances.add(entityInstance);
       
        entityInstance = student.clone();
        entityInstance.set(INTEGER_COLUMN, 5);
        entityInstance.set(BOOLEAN_COLUMN, null);
        entityInstances.add(entityInstance);
        
//        entityInstance = student.clone();
//        entityInstance.set(INTEGER_COLUMN, 6);
//        entityInstance.set(CHAR_COLUMN, null);
//        entityInstances.add(entityInstance);
       
//        entityInstance = student.clone();
//        entityInstance.set(INTEGER_COLUMN, 7);
//        entityInstance.set(TIME_COLUMN, null);
//        entityInstances.add(entityInstance);
//       
//        entityInstance = student.clone();
//        entityInstance.set(INTEGER_COLUMN, 8);
//        entityInstance.set(TIMESTAMP_COLUMN, null);
//        entityInstances.add(entityInstance);
       
        entityInstance = student.clone();
        entityInstance.set(INTEGER_COLUMN, 9);
        entityInstance.set(BYTEA_COLUMN, null);
        entityInstances.add(entityInstance);

		return entityInstances;
	}
	
	private static void printTag(String tag){
	       System.out.println(tag);
	}

	public static void  main(String[] args) throws Exception{
		COREDB99 test = new COREDB99();
		test.setup();

		// CRUD
		test.testCRUDOnOracle(null);
		
		System.out.println("end");
		
		test.tearDown();
	}
	
	
	public boolean testCRUDOnOracle(List<? extends EntityInstance> entities) {
		printTag("<TESTING testCRUDOnOracle(List<? extends EntityInstance> entities)>");
		
		begin();
		
		printTag("<--- Create --->\n");
		List<EntityInstance> entityInstances = generateEntities(iecb);
		Describer.describeEntityInstances(entityInstances);
        boolean result = iecb.createEntities(entityInstances);
        
        String sql = "";
        
        printTag("<--- Read --->\n");
        List<EntityInstance> actualEntityInstances = iecb.readEntities(TESTTABLE_TABLENAME, sql);
        Describer.describeEntityInstances(actualEntityInstances);
        Assert.assertTrue(generateEntities(iecb).size() == actualEntityInstances.size());
        
        printTag("<--- Updata --->\n");
        actualEntityInstances.get(0).set(STRING_COLUMN, "the new value");
        iecb.updateEntities(actualEntityInstances);
        actualEntityInstances = iecb.readEntities(TESTTABLE_TABLENAME, sql);
        Assert.assertTrue(generateEntities(iecb).size() == actualEntityInstances.size());
        Describer.describeEntityInstances(actualEntityInstances);
        Assert.assertTrue(((String)actualEntityInstances.get(0).get(STRING_COLUMN)).equalsIgnoreCase("the new value"));
        
        printTag("<--- Delete --->\n");
        iecb.deleteEntities(actualEntityInstances);
        actualEntityInstances = iecb.readEntities(TESTTABLE_TABLENAME, sql);
        Describer.describeEntityInstances(actualEntityInstances);
        Assert.assertTrue(actualEntityInstances.size() == 0);

        end();
        
        printTag("</PASSED TESTING testCRUDOnOracle(List<? extends EntityInstance> entities)>\n");
        return result;
	}


	private boolean deployDefinitionList(List<ClassToTables> classToTablesList) {
		List<ClassToTables> tables = new LinkedList<ClassToTables>();
		/**
		 * TABLE 1: TESTTABLE
		 */
		List<AttributeClass> STU_acl = new LinkedList<AttributeClass>();
		AttributeClass STU_ac1 =  new AttributeClass(INTEGER_COLUMN,       Integer.class);
		STU_ac1.setPrimaryKey(true);
		STU_acl.add(STU_ac1);
		STU_acl.add(new AttributeClass(STRING_COLUMN,      String.class, 222));
		STU_acl.add(new AttributeClass(DOUBLE_COLUMN,      Double.class));
		STU_acl.add(new AttributeClass(DATE_COLUMN,        Date.class));
		STU_acl.add(new AttributeClass(BOOLEAN_COLUMN,     Boolean.class));
//		STU_acl.add(new AttributeClass(CHAR_COLUMN,        Character.class));
//		STU_acl.add(new AttributeClass(TIME_COLUMN,        Time.class));
//		STU_acl.add(new AttributeClass(TIMESTAMP_COLUMN,   Timestamp.class));
		STU_acl.add(new AttributeClass(BYTEA_COLUMN,       byte[].class));
		
		ClassToTables student = new EntityClass(TESTTABLE_TABLENAME, STU_acl.toArray(new AttributeClass[0]), null);
		tables.add(student);
		
        iecb.deployDefinitionList(tables, true,true);
        
		return true;
	}
}
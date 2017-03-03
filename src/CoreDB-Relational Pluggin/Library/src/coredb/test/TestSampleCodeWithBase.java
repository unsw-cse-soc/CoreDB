/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.test;

import coredb.controller.EntityControllerBase;
import coredb.controller.IEntityControllerBase;
import coredb.database.DatabaseConnection;
import coredb.sql.Q;
import coredb.unit.AttributeClass;
import coredb.unit.ClassToTables;
import coredb.unit.EntityClass;
import coredb.unit.EntityInstance;
import coredb.utils.Describer;
import coredb.utils.TestAssist;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

/**
 *
 * @author vmc
 */
public class TestSampleCodeWithBase {
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
		student.set(BOOLEAN_COLUMN, true);
		student.set(CHAR_COLUMN, Character.valueOf('Y'));
		student.set(TIME_COLUMN, currentTime);
		student.set(TIMESTAMP_COLUMN, currentTimestamp);
		student.set(BYTEA_COLUMN, "COMP9331".getBytes());

		// add beans into list
		entityInstances.add(student);
		
		 // create entity with null value
        EntityInstance entityInstance = student.clone();
        entityInstance.set(INTEGER_COLUMN, 2);
        entityInstance.set(STRING_COLUMN, null);
//        iec.createEntity(entityInstance);
        entityInstances.add(entityInstance);
        
        entityInstance = student.clone();
        entityInstance.set(INTEGER_COLUMN, 3);
        entityInstance.set(DOUBLE_COLUMN, null);
//        iec.createEntity(entityInstance);
        entityInstances.add(entityInstance);
        
        entityInstance = student.clone();
        entityInstance.set(INTEGER_COLUMN, 4);
        entityInstance.set(DATE_COLUMN, null);
//        iec.createEntity(entityInstance);
        entityInstances.add(entityInstance);
       
        entityInstance = student.clone();
        entityInstance.set(INTEGER_COLUMN, 5);
        entityInstance.set(BOOLEAN_COLUMN, null);
//        iec.createEntity(entityInstance);
        entityInstances.add(entityInstance);
        
        entityInstance = student.clone();
        entityInstance.set(INTEGER_COLUMN, 6);
        entityInstance.set(CHAR_COLUMN, null);
//        iec.createEntity(entityInstance);
        entityInstances.add(entityInstance);
       
        entityInstance = student.clone();
        entityInstance.set(INTEGER_COLUMN, 7);
        entityInstance.set(TIME_COLUMN, null);
//        iec.createEntity(entityInstance);
        entityInstances.add(entityInstance);
       
        entityInstance = student.clone();
        entityInstance.set(INTEGER_COLUMN, 8);
        entityInstance.set(TIMESTAMP_COLUMN, null);
//        iec.createEntity(entityInstance);
        entityInstances.add(entityInstance);
       
        entityInstance = student.clone();
        entityInstance.set(INTEGER_COLUMN, 9);
        entityInstance.set(BYTEA_COLUMN, null);
//        iec.createEntity(entityInstance);
        entityInstances.add(entityInstance);

		return entityInstances;
	}
	
	private static void printTag(String tag){
	       System.out.println(tag);
	}

	public static void  main(String[] args) throws Exception{
		TestSampleCodeWithBase test = new TestSampleCodeWithBase();
		test.setup();

		// create
		test.createEntities(null);
		
		// read
		test.readEntities(null, null);
		
		// update
		test.updateEntities(null);
		// delete
		test.deleteEntities(null);
		
		// read table name
		test.readTableNameList(false);
		
		// drop
		test.dropTables(null);
		
		// create EntityInstence for table
		test.createEntityInstanceFor(null);
		
		// get AttributeClass
		test.getAttributeClass(null, null);
		
		// get EntityClass
		test.getEntityClass(null, null, false);
		
		System.out.println("end");
		
		test.tearDown();
	}
	
	
	public boolean createEntities(List<? extends EntityInstance> entities) {
		printTag("<TESTING createEntities(List<? extends EntityInstance> entities)>");
		
		begin();
		List<EntityInstance> entityInstances = generateEntities(iecb);
        boolean result = iecb.createEntities(entityInstances);
        
        String sql = "";
        
        List<EntityInstance> actualEntityInstances = iecb.readEntities(TESTTABLE_TABLENAME, sql);
        Assert.assertTrue(TestAssist.compare(iecb.getDatabaseConnection(), entityInstances, actualEntityInstances));
        
        printTag("--- create 9 data rows into database. The first row is normal values and others have one null value column");
        Describer.describeEntityInstances(actualEntityInstances);
        // clean up
        end();
        
        printTag("</PASSED TESTING createEntities(List<? extends EntityInstance> entities)>\n");
        return result;
	}

	public List<EntityInstance> readEntities(String tableName, String cshString) {
		printTag("<TESTING readEntities(String tableName, String cshString)>");
		
		begin();
		List<EntityInstance> entityInstances = generateEntities(iecb);
        iecb.createEntities(entityInstances);
        
        String sql = "";
        
        List<EntityInstance> actualEntityInstances = iecb.readEntities(TESTTABLE_TABLENAME, sql);
        Assert.assertTrue(TestAssist.compare(iecb.getDatabaseConnection(), entityInstances, actualEntityInstances));
        printTag("--- read all data rows from database");
        Describer.describeEntityInstances(actualEntityInstances);
        // clean up
        end();
        
        printTag("</PASSED readEntities(String tableName, String cshString)>\n");
        return actualEntityInstances;
	}

	public boolean updateEntities(List<? extends EntityInstance> entities) {
		printTag("<updateEntities(List<? extends EntityInstance> entities)>");
		begin();
		List<EntityInstance> entityInstances = generateEntities(iecb);
        iecb.createEntities(entityInstances);
        String sql = Q.SPACE + INTEGER_COLUMN + Q.SPACE + Q.E + Q.SPACE + "1";

        
        EntityInstance expectedEntityInstance= iecb.readEntities(TESTTABLE_TABLENAME, sql).get(0);
        
        printTag("[ Before Update " + STRING_COLUMN + "]"); 
        Describer.describeEntityInstance(expectedEntityInstance);
        expectedEntityInstance.set(STRING_COLUMN, "STU000002");
        Assert.assertTrue(iecb.updateEntity(expectedEntityInstance));
        EntityInstance acutalEntityInstance= iecb.readEntities(TESTTABLE_TABLENAME, sql).get(0);
        Assert.assertTrue(TestAssist.compare(iecb.getDatabaseConnection(), expectedEntityInstance, acutalEntityInstance));
        printTag("[ After Update " + STRING_COLUMN + "]");
        Describer.describeEntityInstance(acutalEntityInstance);
        printTag("\n");
        
        printTag("[ Before Update " + DOUBLE_COLUMN + "]"); 
        Describer.describeEntityInstance(expectedEntityInstance);
        expectedEntityInstance.set(DOUBLE_COLUMN, 99.4);
        Assert.assertTrue(iecb.updateEntity(expectedEntityInstance));
        acutalEntityInstance= iecb.readEntities(TESTTABLE_TABLENAME, sql).get(0);
        Assert.assertTrue(TestAssist.compare(iecb.getDatabaseConnection(), expectedEntityInstance, acutalEntityInstance));
        printTag("[ After Update " + DOUBLE_COLUMN + "]");
        Describer.describeEntityInstance(acutalEntityInstance);
        printTag("\n");
        
        printTag("[ Before Update " + DATE_COLUMN + "]"); 
        Describer.describeEntityInstance(expectedEntityInstance);
        expectedEntityInstance.set(DATE_COLUMN, Date.valueOf("2001-01-01"));
        Assert.assertTrue(iecb.updateEntity(expectedEntityInstance));
        acutalEntityInstance= iecb.readEntities(TESTTABLE_TABLENAME, sql).get(0);
        Assert.assertTrue(TestAssist.compare(iecb.getDatabaseConnection(), expectedEntityInstance, acutalEntityInstance));
        printTag("[ After Update " + DATE_COLUMN + "]");
        Describer.describeEntityInstance(acutalEntityInstance);
        printTag("\n");

        printTag("[ Before Update " + BOOLEAN_COLUMN + "]"); 
        Describer.describeEntityInstance(expectedEntityInstance);
        expectedEntityInstance.set(BOOLEAN_COLUMN, false);
        Assert.assertTrue(iecb.updateEntity(expectedEntityInstance));
        acutalEntityInstance= iecb.readEntities(TESTTABLE_TABLENAME, sql).get(0);
        Assert.assertTrue(TestAssist.compare(iecb.getDatabaseConnection(), expectedEntityInstance, acutalEntityInstance));
        printTag("[ After Update " + BOOLEAN_COLUMN + "]");
        Describer.describeEntityInstance(acutalEntityInstance);
        printTag("\n");

        printTag("[ Before Update " + CHAR_COLUMN + "]"); 
        Describer.describeEntityInstance(expectedEntityInstance);
        expectedEntityInstance.set(CHAR_COLUMN, 'N');
        Assert.assertTrue(iecb.updateEntity(expectedEntityInstance));
        acutalEntityInstance= iecb.readEntities(TESTTABLE_TABLENAME, sql).get(0);
        Assert.assertTrue(TestAssist.compare(iecb.getDatabaseConnection(), expectedEntityInstance, acutalEntityInstance));
        printTag("[ After Update " + CHAR_COLUMN + "]");
        Describer.describeEntityInstance(acutalEntityInstance);
        printTag("\n");

        printTag("[ Before Update " + TIME_COLUMN + "]"); 
        Describer.describeEntityInstance(expectedEntityInstance);
        expectedEntityInstance.set(TIME_COLUMN, new Time(1, 1, 21));
        Assert.assertTrue(iecb.updateEntity(expectedEntityInstance));
        acutalEntityInstance= iecb.readEntities(TESTTABLE_TABLENAME, sql).get(0);
        Assert.assertTrue(TestAssist.compare(iecb.getDatabaseConnection(), expectedEntityInstance, acutalEntityInstance));
        printTag("[ After Update " + TIME_COLUMN + "]");
        Describer.describeEntityInstance(acutalEntityInstance);
        printTag("\n");

        printTag("[ Before Update " + TIMESTAMP_COLUMN + "]"); 
        Describer.describeEntityInstance(expectedEntityInstance);
        expectedEntityInstance.set(TIMESTAMP_COLUMN, new Timestamp(System.currentTimeMillis()));
        Assert.assertTrue(iecb.updateEntity(expectedEntityInstance));
        acutalEntityInstance= iecb.readEntities(TESTTABLE_TABLENAME, sql).get(0);
        Assert.assertTrue(TestAssist.compare(iecb.getDatabaseConnection(), expectedEntityInstance, acutalEntityInstance));
        printTag("[ After Update " + TIMESTAMP_COLUMN + "]");
        Describer.describeEntityInstance(acutalEntityInstance);
        printTag("\n");

        printTag("[ Before Update " + BYTEA_COLUMN + "]"); 
        Describer.describeEntityInstance(expectedEntityInstance);
        expectedEntityInstance.set(BYTEA_COLUMN, "COMP9445".getBytes());
        Assert.assertTrue(iecb.updateEntity(expectedEntityInstance));
        acutalEntityInstance= iecb.readEntities(TESTTABLE_TABLENAME, sql).get(0);
        Assert.assertTrue(TestAssist.compare(iecb.getDatabaseConnection(), expectedEntityInstance, acutalEntityInstance));
        printTag("[ After Update " + BYTEA_COLUMN + "]");
        Describer.describeEntityInstance(acutalEntityInstance);
        printTag("\n");
        
        end();
        printTag("</PASSED <updateEntities(List<? extends EntityInstance> entities)>\n");
        return true;
        
	}

	public boolean deleteEntities(List<? extends EntityInstance> entities) {
		printTag("<deleteEntities(List<? extends EntityInstance> entities)>");
		
		begin();
		List<EntityInstance> entityInstances = generateEntities(iecb);
        iecb.createEntities(entityInstances);
        
        String sql = "";
        List<EntityInstance> actualEntityInstances = iecb.readEntities(TESTTABLE_TABLENAME, sql);
        Assert.assertTrue(TestAssist.compare(iecb.getDatabaseConnection(), entityInstances, actualEntityInstances));
        
        printTag("--- before delete");
        Describer.describeEntityInstances(actualEntityInstances);
        
        boolean result = iecb.deleteEntities(entityInstances);
        
        actualEntityInstances = iecb.readEntities(TESTTABLE_TABLENAME, sql);
        printTag("--- after delete");
        Describer.describeEntityInstances(actualEntityInstances);
        // clean up
        end();
        
        printTag("</PASSED deleteEntities(List<? extends EntityInstance> entities)>\n");
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
		STU_acl.add(new AttributeClass(CHAR_COLUMN,        Character.class));
		STU_acl.add(new AttributeClass(TIME_COLUMN,        Time.class));
		STU_acl.add(new AttributeClass(TIMESTAMP_COLUMN,   Timestamp.class));
		STU_acl.add(new AttributeClass(BYTEA_COLUMN,       byte[].class));
		
		ClassToTables student = new EntityClass(TESTTABLE_TABLENAME, STU_acl.toArray(new AttributeClass[0]), null);
		tables.add(student);
		
        iecb.deployDefinitionList(tables);
        
//        Describer.describeClassToTables(tables);
        
		return true;
	}

	public List<String> readTableNameList(boolean reRead) {
		printTag("<readTableNameList(boolean reRead)>");
		
		begin();
		
		List<String> tableNameList = iecb.readTableNameList(true);
		for(String tableName : tableNameList) {
			System.out.println("TableName: " + tableName);
		}
		// clean up
		end();
        
		printTag("</PASSED readTableNameList(boolean reRead)>\n");
		return tableNameList;
	}

	public void dropTables(List<String> tableNames) {
		printTag("<dropTables(List<String> tableNames)>");
		
        begin();
		
		List<String> tableNameList = iecb.readTableNameList(true);
		Assert.assertFalse(tableNameList.isEmpty());
		for(String tableName : tableNameList) {
			System.out.println("TableName: " + tableName);
		}
		
        iecb.dropTables(tableNameList);
        tableNameList = iecb.readTableNameList(true);
		Assert.assertTrue(tableNameList.isEmpty());
		
		printTag("</PASSED dropTables(List<String> tableNames)>\n");
	}

	public EntityInstance createEntityInstanceFor(String tableName) {
		printTag("<createEntityInstanceFor(String tableName)>");
		
		begin();
		EntityInstance entityInstance = iecb.createEntityInstanceFor(TESTTABLE_TABLENAME);
		EntityClass ec = iecb.getEntityClass(iecb.getDatabaseConnection(), TESTTABLE_TABLENAME, true);
		
		List<String> exceptedAttributeClassNames = entityInstance.getAttributeNames();
		AttributeClass[] acturalAttributeClasses = ec.getAttributeClassList();
		HashMap<String, String> actualAttributeclassNames = new HashMap<String, String>();
		for(AttributeClass ac : acturalAttributeClasses) {
			actualAttributeclassNames.put(ac.getName(), ac.getName());
		}

		Describer.describeEntityInstance(entityInstance);
		// checking
		for(String ac_name : exceptedAttributeClassNames) {
			Assert.assertTrue(actualAttributeclassNames.get(ac_name) != null);
		}
		
		// clean up
		end();
		
		printTag("</PASSED createEntityInstanceFor(String tableName)>\n");
		
		return entityInstance;
	}

	public AttributeClass getAttributeClass(String tableName, String attributeName) {
		printTag("<getAttributeClass(String tableName, String attributeName)>");
		
		begin();
		AttributeClass ac = iecb.getAttributeClass(TESTTABLE_TABLENAME, INTEGER_COLUMN);
		Assert.assertTrue(ac.getSqlType().equalsIgnoreCase("Integer"));
		Assert.assertTrue(ac.getName().equalsIgnoreCase(INTEGER_COLUMN));
		
		// clean up
		end();
        
		printTag("</PASSED getAttributeClass(String tableName, String attributeName)>\n");
        return null;
	}

	public EntityClass getEntityClass(DatabaseConnection databaseConnection, String tableName, boolean reRead) {
		printTag("<getEntityClass(DatabaseConnection databaseConnection, String tableName, boolean reRead)>");
		
		begin();
		EntityInstance entityInstance = iecb.createEntityInstanceFor(TESTTABLE_TABLENAME);
		EntityClass ec = iecb.getEntityClass(iecb.getDatabaseConnection(), TESTTABLE_TABLENAME, true);
		
		List<String> exceptedAttributeClassNames = entityInstance.getAttributeNames();
		AttributeClass[] acturalAttributeClasses = ec.getAttributeClassList();
		HashMap<String, String> actualAttributeclassNames = new HashMap<String, String>();
		for(AttributeClass ac : acturalAttributeClasses) {
			actualAttributeclassNames.put(ac.getName(), ac.getName());
		}

		// checking
		for(String ac_name : exceptedAttributeClassNames) {
			Assert.assertTrue(actualAttributeclassNames.get(ac_name) != null);
		}
		
		// clean up
		end();

		printTag("</PASSED getEntityClass(DatabaseConnection databaseConnection, String tableName, boolean reRead)>\n");


        return ec;
	}
}

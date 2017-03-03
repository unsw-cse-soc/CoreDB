package coredb.performance;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import coredb.controller.EntityControllerBase;
import coredb.sql.CompoundStatementManual;
import coredb.sql.Q;
import coredb.unit.AttributeClass;
import coredb.unit.ClassToTables;
import coredb.unit.EntityClass;
import coredb.unit.EntityInstance;

public class TestPerformanceWithBase {
	private static EntityControllerBase iecb         = null;
	private int id                                       = 0;
	
	private static final String TESTTABLE_TABLENAME      = "TEST";
	private static final String TESTTABLE_COLUMN_STAFFID = "STAFFID";
	private static final String TESTTABLE_COLUMN_CHARAC  = "CHARAC";
	private static final String TESTTABLE_COLUMN_BYTEA   = "BYTEA";
	private static final String TESTTABLE_COLUMN_NAME    = "NAME";
	private static final String TESTTABLE_COLUMN_PHONE   = "PHONE";
	
	@SuppressWarnings("unchecked")
	private static HashMap<String, Class> testTable_dataType = new HashMap<String, Class>();
	
	
	private static long startTime                                 = 0;
	private static long endTime                                   = 0;
	private static int  existingEntities                          = 100;
	private static double 	runs                                  = 100;
	
	
	public static void main(String[] args) throws SQLException {
		if (args.length == 1) {
			existingEntities	= Integer.parseInt(args[0]);
		}	
		TestPerformanceWithBase test = new TestPerformanceWithBase();
		test.setup();
		for (int i=1000; i<=5000; i+=1000) {
			test.cleanUp();
			existingEntities = i;
			test.testCreateEntitiesPerformance();
			test.testReadEntitiesPerformance();
			test.testUpdateEntitiesPerformance();
			test.testDeleteEntitiesPerformance();
			System.out.println("=================");
		}
		test.end();
	}

	private void testCreateEntitiesPerformance(){
		printTag("[testCreateEntitiesPerformance]\n");
		
		List<EntityInstance> entityInstances = new LinkedList<EntityInstance>();
		for(id = 1 ; id <= existingEntities; id ++){
			entityInstances.addAll(generateSourceEntities());
		}
		double size = entityInstances.size();
		
		startTime  = System.currentTimeMillis();
		iecb.createEntities(entityInstances);
		endTime    = System.currentTimeMillis();
		String out = String.format("%30s | %10d | %10.3f | milliseconds\n", "testCreateEntitiesPerformance", existingEntities, (endTime - startTime)/size );
		printErrTag(out);
		
		printTag("[/testCreateEntitiesPerformance]\n");
	}
	
	private void testReadEntitiesPerformance(){
		printTag("[testReadEntitiesPerformance]\n");
		
		List<CompoundStatementManual> csmList = new LinkedList<CompoundStatementManual>();
		Random random = new Random();
		int range = existingEntities;
		for (int i=1; i<=runs; i++) {
			CompoundStatementManual csm           = new CompoundStatementManual();
			int staffId = random.nextInt(range)+1;
			csm.addConditionalStatement(TESTTABLE_COLUMN_STAFFID.toLowerCase() + Q.E + staffId);
			csmList.add(csm);
		}
		
		startTime = System.currentTimeMillis();
		for(CompoundStatementManual csm : csmList){
			iecb.readEntities(TESTTABLE_TABLENAME, csm);
		}
		endTime    = System.currentTimeMillis();
		String out = String.format("%30s | %10d | %10.3f | milliseconds\n", "testReadEntitiesPerformance", existingEntities, (endTime - startTime)/runs);
		printErrTag(out);
		
		printTag("[/testReadEntitiesPerformance]\n");
	}
	
	private void testUpdateEntitiesPerformance(){
		printTag("[testUpdateEntitiesPerformance]\n");
		
		List<CompoundStatementManual> csmList = new LinkedList<CompoundStatementManual>();
		List<EntityInstance> entityInstances  = new LinkedList<EntityInstance>();
		Random random = new Random();
		int range = existingEntities;
		for (int i=1; i<=runs; i++) {
			CompoundStatementManual csm       = new CompoundStatementManual();
			int staffId = random.nextInt(range)+1;
			csm.addConditionalStatement(TESTTABLE_COLUMN_STAFFID.toLowerCase() + Q.E + staffId);
			csmList.add(csm);
		}		
		for(CompoundStatementManual csm : csmList){
			entityInstances.addAll(iecb.readEntities(TESTTABLE_TABLENAME, csm));
		}	
		double size                              = entityInstances.size();
		for(EntityInstance entityInstance :  entityInstances){
			entityInstance.set(TESTTABLE_COLUMN_NAME, "SuperX");
		}
		
		startTime = System.currentTimeMillis();
		iecb.updateEntities(entityInstances);
		endTime = System.currentTimeMillis();
		String out = String.format("%30s | %10d | %10.3f | milliseconds\n", "testUpdateEntitiesPerformance", existingEntities, (endTime - startTime)/size);
		printErrTag(out);
		
		printTag("[/testUpdateEntitiesPerformance]\n");
	}
	
	private void testDeleteEntitiesPerformance(){
		printTag("[testDeleteEntitiesPerformance]\n");
		
		List<CompoundStatementManual> csmList = new LinkedList<CompoundStatementManual>();
		List<EntityInstance> entityInstances  = new LinkedList<EntityInstance>();
		Random random = new Random();
		int range = existingEntities;
		for (int i=1; i<=runs; i++) {
			CompoundStatementManual csm       = new CompoundStatementManual();
			int staffId = random.nextInt(range)+1;
			csm.addConditionalStatement(TESTTABLE_COLUMN_STAFFID.toLowerCase() + Q.E + staffId);
			csmList.add(csm);
		}		
		for(CompoundStatementManual csm : csmList){
			entityInstances.addAll(iecb.readEntities(TESTTABLE_TABLENAME, csm));
		}	
		double size                              = entityInstances.size();
		
		startTime = System.currentTimeMillis();
		iecb.deleteEntities(entityInstances);
		endTime = System.currentTimeMillis();
		String out = String.format("%30s | %10d | %10.3f | milliseconds\n", "testDeleteEntitiesPerformance", existingEntities, (endTime - startTime)/size);
		printErrTag(out);
		
		printTag("[/testDeleteEntitiesPerformance]\n");	
	}	
	
	private void setup() throws SQLException {
		printTag("\n++++++++++++++++++++++++++++++++++++++START Set Up Test Case++++++++++++++++++++++++++++++++++++++++\n");
		
		iecb = new EntityControllerBase("/tmp/credential.txt");
		iecb.dropAllTables();
		testTable_dataType.put(TESTTABLE_COLUMN_STAFFID, Integer.class);
		testTable_dataType.put(TESTTABLE_COLUMN_CHARAC, Character.class);
		testTable_dataType.put(TESTTABLE_COLUMN_BYTEA, byte[].class);
		testTable_dataType.put(TESTTABLE_COLUMN_NAME, String.class);
		testTable_dataType.put(TESTTABLE_COLUMN_PHONE, String.class);
		
	//	iecb.initializeMode(true, Mode.SECURITY);
		deployDefinitionListWithRowSecurity(null,true,true);
		
	}
	
	private void end() {
		iecb.dropAllTables();		
	}
	
	private static void printTag(String tag) {
	    System.out.print(tag);
	}
	
	private static void printErrTag(String tag){
		System.out.print(tag);
	}
	
	private void deployDefinitionListWithRowSecurity( List<ClassToTables> classToTablesList, boolean dropTablesFirst, boolean continueOnError) {
		// Test Data Initialization
		classToTablesList = new LinkedList<ClassToTables>();	
		List<AttributeClass> attributes = new LinkedList<AttributeClass>();	
		AttributeClass primaryAttribute = new AttributeClass(TESTTABLE_COLUMN_STAFFID, testTable_dataType.get(TESTTABLE_COLUMN_STAFFID));
		primaryAttribute.setPrimaryKey(true);
		attributes.add(primaryAttribute);
		

		attributes.add(new AttributeClass(TESTTABLE_COLUMN_CHARAC, testTable_dataType.get(TESTTABLE_COLUMN_CHARAC)));	
		attributes.add(new AttributeClass(TESTTABLE_COLUMN_BYTEA, testTable_dataType.get(TESTTABLE_COLUMN_BYTEA)));
		attributes.add(new AttributeClass(TESTTABLE_COLUMN_NAME, testTable_dataType.get(TESTTABLE_COLUMN_NAME)));
		attributes.add(new AttributeClass(TESTTABLE_COLUMN_PHONE, testTable_dataType.get(TESTTABLE_COLUMN_PHONE)));		
		EntityClass entityClass = new EntityClass(TESTTABLE_TABLENAME, attributes.toArray(new AttributeClass[0]), null);	
		classToTablesList.add(entityClass);			

		iecb.deployDefinitionList(classToTablesList,dropTablesFirst,continueOnError);
	 
	}	
	
	private List<EntityInstance> generateSourceEntities(){
		List<EntityInstance> sourceEntities = new LinkedList<EntityInstance>();
		
		EntityInstance entity1 = iecb.createEntityInstanceFor(TESTTABLE_TABLENAME);
		entity1.set(TESTTABLE_COLUMN_STAFFID, id);
		entity1.set(TESTTABLE_COLUMN_CHARAC, 'Y');
		entity1.set(TESTTABLE_COLUMN_BYTEA, "HelloWorld1".getBytes());
		entity1.set(TESTTABLE_COLUMN_NAME, "Stephen");
		entity1.set(TESTTABLE_COLUMN_PHONE, "0430106177");
		sourceEntities.add(entity1.clone());
		
		return sourceEntities;
	}
	
	private void cleanUp(){
		String sql = Q.DELETE + Q.FROM + TESTTABLE_TABLENAME.toLowerCase();
		iecb.getDatabaseConnection().executeArbitraryUpdate(sql);
	}
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.test;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;

import coredb.config.Configuration;
import coredb.controller.EntityControllerTracing;
import coredb.controller.IEntityControllerTracing;
import coredb.mode.Mode;
import coredb.sql.CompoundStatementAuto;
import coredb.sql.CompoundStatementManual;
import coredb.sql.ConditionalStatement;
import coredb.sql.Q;
import coredb.sql.SQLStatement;
import coredb.unit.AttributeClass;
import coredb.unit.ClassToTables;
import coredb.unit.EntityInstance;
import coredb.unit.EntityTracingClass;
import coredb.unit.IndexClass;
import coredb.utils.Describer;

/**
 *
 * @author vmc
 */
public class TestSampleCodeWithTracing {

	private static  EntityControllerTracing iect = null;

    private static final String pathToFile  = "/tmp/credential.txt";
	private static final String USERID      = "User";

	private static final String TABLENAME   = "TestTracingTable";
	private static final String COLUMNNAME1 = "Column1";
	private static final String COLUMNNAME2 = "Column2";
	private static final String COLUMNNAME3 = "Column3";

	private EntityInstance entityInstance1  = null;
	private EntityInstance entityInstance2  = null;

	private TestSampleCodeWithTracing(String path) throws SQLException {
    	iect = new EntityControllerTracing(path);
    }

    private static boolean deployTracingTableSchema(IEntityControllerTracing iec) {

        AttributeClass column1       = new AttributeClass(COLUMNNAME1, Integer.class);
        column1.setPrimaryKey(true);
        AttributeClass column2       = new AttributeClass(COLUMNNAME2, String.class);
        AttributeClass column3       = new AttributeClass(COLUMNNAME3, Boolean.class);
        IndexClass indexClass        = new IndexClass(new AttributeClass[]{column2,column3}, null);

        EntityTracingClass entityTracingClass      = new EntityTracingClass(TABLENAME, new AttributeClass[]{column1,column2,column3}, new IndexClass[]{indexClass});

        List<ClassToTables> entityClassList = new LinkedList<ClassToTables>();
        entityClassList.add(entityTracingClass);
        return  iec.deployDefinitionList(entityClassList);

    }

	private static void printTag(String tag){
	       System.out.println(tag);
	}

    public static void main(String[] args) throws SQLException, InterruptedException{
		TestSampleCodeWithTracing testCode = new TestSampleCodeWithTracing(pathToFile);
		iect.dropAllTables();

        // Initialise API as Tracing mode
        iect.initializeMode(true, Mode.TRACING);

        // Deploy table schema
        deployTracingTableSchema(iect);
        printTag("");

        // create
	    testCode.createEntities(USERID, null);

	    // read
	    testCode.readEntities(USERID, TABLENAME, "sql");

	    testCode.readEntities(USERID, TABLENAME, new CompoundStatementManual());

	    // update
	    testCode.updateEntities(USERID, null);

	    // delete
	    testCode.deleteEntities(USERID, null);

		// TRACING
	    testCode.traceEntities(TABLENAME, USERID);
	    testCode.traceEntities(TABLENAME, new CompoundStatementManual(), USERID);
	    testCode.traceEntities(TABLENAME, "0", 0, 0, 0, USERID);
	    testCode.traceEntities(TABLENAME, "0", 0, 0, USERID);

		// mapping table
		testCode.generateMappingTableTracing(null, null, null, null);

		// changeSetNumber
		testCode.generateLatestChangeSetNumber(USERID);
		
		iect.dropAllTables();
    }

	public <T extends EntityInstance> int createEntity(T entity, String userId) {
		return createEntity(userId, entity);
	}

	public <T extends EntityInstance> int createEntity(String userId, T entity) {
		List<T> entities = new LinkedList<T>();
		entities.add(entity);
		return createEntities(userId, entities);
	}

	public int createEntities(List<? extends EntityInstance> entities, String userId) {
		return createEntities(userId, entities);
	}

	public int createEntities(String userId, List<? extends EntityInstance> entities) {
		printTag("<TESTING createEntities(String userId, List<? extends EntityInstance> entities)>");
		int changeSetNumber = iect.getLatestChangeSetNumber();

		List<EntityInstance> entityInstances = new LinkedList<EntityInstance>();
		entityInstance1 = iect.createEntityInstanceFor(TABLENAME);
	    entityInstance1.set(COLUMNNAME1, 1);
	    entityInstance1.set(COLUMNNAME2, "test1");
	    entityInstance1.set(COLUMNNAME3, true);
	    entityInstances.add(entityInstance1);

	    entityInstance2 = iect.createEntityInstanceFor(TABLENAME);
	    entityInstance2.set(COLUMNNAME1, 2);
	    entityInstance2.set(COLUMNNAME2, "test2");
	    entityInstance2.set(COLUMNNAME3, true);
	    entityInstances.add(entityInstance2);

	    changeSetNumber = iect.createEntitiesWithTracing(USERID, entityInstances);
	    printTag("Base table data");
	    entityInstances = iect.getDatabaseConnection().read(TABLENAME, new CompoundStatementManual().toSQLStatement());
	    Describer.describeEntityInstances(entityInstances);

	    Assert.assertTrue(entityInstances.size() == 2);
	    Assert.assertTrue(entityInstances.get(0).equals(entityInstance1));
	    Assert.assertTrue(entityInstances.get(1).equals(entityInstance2));

	    printTag("</PASSED TESTING createEntities(String userId, List<? extends EntityInstance> entities)>\n");

	    return changeSetNumber;
	}

	public List<EntityInstance> readEntities(String userId, String tableName, SQLStatement sql) {
		printTag("<TESTING readEntities(String userId, String tableName, SQLStatement sql)>");

		CompoundStatementAuto CSA = new CompoundStatementAuto(Q.AND);
		// Add a condition as SQLStatement(the interface of ConditionalStatement)
		AttributeClass column3 = iect.getDatabaseConnection().getEntityClass(TABLENAME, false).getAttributeClassList()[2];
		CSA.addCompoundStatement(new ConditionalStatement(column3, Q.E, Boolean.FALSE));
		// Read
		List<EntityInstance> entityInstances = iect.readEntitiesWithTracing(USERID, TABLENAME, CSA);
		Describer.describeEntityInstances(entityInstances);

        Assert.assertTrue(entityInstances.size() == 0);

        printTag("</PASSED TESTING readEntities(String userId, String tableName, SQLStatement sql)>\n");
		return entityInstances;
	}

	public List<EntityInstance> readEntities(String userId, String tableName, String sql) {
		printTag("<TESTING readEntities(String userId, String tableName, String sql)>");

		sql = COLUMNNAME3 + Q.E + Boolean.TRUE;
		// Read
		List<EntityInstance> entityInstances = iect.readEntitiesWithTracing(USERID, TABLENAME, sql);
		Describer.describeEntityInstances(entityInstances);

		Assert.assertTrue(entityInstances.size() == 2);
		Assert.assertTrue(entityInstances.get(0).equals(entityInstance1));
	    Assert.assertTrue(entityInstances.get(1).equals(entityInstance2));

		printTag("</PASSED TESTING readEntities(String userId, String tableName, String sql)>\n");
		return entityInstances;
	}

	public <T extends EntityInstance> int updateEntity(T entity, String userId) {
		return updateEntity(userId, entity);
	}

	public <T extends EntityInstance> int updateEntity(String userId, T entity) {
		List<T> entities = new LinkedList<T>();
		entities.add(entity);
		return updateEntities(userId, entities);
	}

	public int updateEntities(List<? extends EntityInstance> entities, String userId) {
		return updateEntities(userId, entities);
	}

	public int updateEntities(String userId, List<? extends EntityInstance> entities) {
		printTag("<TESTING updateEntities(String userId, List<? extends EntityInstance> entities)>");
		int changeSetNumber = iect.getLatestChangeSetNumber();

		printTag("Before update");
		List<EntityInstance>  entityInstances = iect.getDatabaseConnection().read(TABLENAME, new CompoundStatementManual().toSQLStatement());
		Describer.describeEntityInstances(entityInstances);
		if(entityInstances.size()>0){
		    EntityInstance entityInstance = entityInstances.get(0);
		    entityInstance.set(COLUMNNAME2, "test1 after update");
		    entityInstance.set(COLUMNNAME3, false);

		    EntityInstance entityInstance1 = entityInstances.get(1);
		    entityInstance1.set(COLUMNNAME2, "test2 after update");
		    entityInstance1.set(COLUMNNAME3, false);
		    changeSetNumber = iect.updateEntitiesWithTracing(USERID, entityInstances);
		}else{
		    printTag("Update failed");
	    }
		printTag("After update");
		entityInstances = iect.getDatabaseConnection().read(TABLENAME, new CompoundStatementManual().toSQLStatement());
		Describer.describeEntityInstances(entityInstances);

		Assert.assertTrue(entityInstances.size() == 2);
		Assert.assertFalse(entityInstances.get(0).equals(entityInstance1));
	    Assert.assertFalse(entityInstances.get(1).equals(entityInstance2));
	    Assert.assertFalse(entityInstances.get(0).get(COLUMNNAME3).equals(true));
	    Assert.assertFalse(entityInstances.get(1).get(COLUMNNAME3).equals(true));

	    printTag("<TESTING updateEntities(String userId, List<? extends EntityInstance> entities)>\n");
		return changeSetNumber;
	}

	public <T extends EntityInstance> int deleteEntity(T entity, String userId) {
		return deleteEntity(userId, entity);
	}

	public <T extends EntityInstance> int deleteEntity(String userId, T entity) {
		List<T> entities = new LinkedList<T>();
		entities.add(entity);
		return deleteEntities(userId, entities);
	}

    public int deleteEntities(String userId, List<? extends EntityInstance> entities) {
    	printTag("<TESTING deleteEntities(String userId, List<? extends EntityInstance> entities)>");
    	int changeSetNumber = iect.getLatestChangeSetNumber();

    	printTag("Before delete");
    	List<EntityInstance>  entityInstances = iect.getDatabaseConnection().read(TABLENAME, new CompoundStatementManual().toSQLStatement());
    	Describer.describeEntityInstances(entityInstances);
    	Assert.assertTrue(entityInstances.size() == 2);
    	if(entityInstances.size()>0){
    	    changeSetNumber = iect.deleteEntitiesWithTracing(USERID, entityInstances);
    	}else{
    	    printTag("Delete failed");
    	}
    	printTag("After delete");
    	entityInstances = iect.getDatabaseConnection().read(TABLENAME, new CompoundStatementManual().toSQLStatement());
    	Describer.describeEntityInstances(entityInstances);

    	Assert.assertTrue(entityInstances.size() == 0);

    	printTag("</PASSED TESTING deleteEntities(String userId, List<? extends EntityInstance> entities)>\n");
    	return changeSetNumber;
	}

	public int deleteEntities(List<? extends EntityInstance> entities, String userId) {
		return deleteEntities(userId, entities);
	}

	public List<EntityInstance> traceEntities(String tableName, String userId) {
        printTag("<TESTING traceEntities(String tableName, String userId):>");


		printTag("The latest CSN tracing record of the whole table");
		List<EntityInstance>  entityInstances = iect.traceEntities(TABLENAME, USERID);
		Describer.describeEntityInstances(entityInstances);

		Assert.assertTrue(entityInstances.size() == 2);
		Assert.assertFalse(entityInstances.get(0).equals(entityInstance1));
	    Assert.assertFalse(entityInstances.get(1).equals(entityInstance2));
	    Assert.assertTrue(entityInstances.get(0).get(COLUMNNAME3.toLowerCase()).equals(false));
	    Assert.assertTrue(entityInstances.get(1).get(COLUMNNAME3.toLowerCase()).equals(false));

		printTag("</PASSED TESTING traceEntities(String tableName, String userId):>\n");
		return entityInstances;
	}

	public List<EntityInstance> traceEntities(String tableName, SQLStatement sql, String userId) {
        printTag("<TESTING traceEntities(String tableName, SQLStatement sql, String userId):>");

		printTag("The sql condition statement is ConditionalStatement(column3, Q.E, Boolean.FALSE);");
		CompoundStatementAuto CSA = new CompoundStatementAuto(Q.AND);
		// Add a condition as SQLStatement(the interface of ConditionalStatement)
		AttributeClass column3 = iect.getDatabaseConnection().getEntityClass(TABLENAME, false).getAttributeClassList()[2];
		CSA.addCompoundStatement(new ConditionalStatement(column3, Q.E, Boolean.FALSE));

		List<EntityInstance>  entityInstances = iect.traceEntities(TABLENAME, CSA, USERID);
		Describer.describeEntityInstances(entityInstances);

		Assert.assertTrue(entityInstances.size() == 2);
		Assert.assertFalse(entityInstances.get(0).equals(entityInstance1));
	    Assert.assertFalse(entityInstances.get(1).equals(entityInstance2));
	    Assert.assertTrue(entityInstances.get(0).get(COLUMNNAME3.toLowerCase()).equals(false));
	    Assert.assertTrue(entityInstances.get(1).get(COLUMNNAME3.toLowerCase()).equals(false));

		printTag("</PASSED TESTING traceEntities(String tableName, SQLStatement sql, String userId):>\n");
		return entityInstances;
	}

	public List<EntityInstance> traceEntities(String tableName, String entityMappingId, int minCSN,int currentCSN,int maxCSN,String userId) {
        printTag("<TESTING traceEntities(String tableName, String entityMappingId, int minCSN,int currentCSN,int maxCSN,String userId):>");

        // read the latest records
		List<EntityInstance>  entityInstances = iect.traceEntities(TABLENAME, USERID);

		printTag("The tracing record of the 2nd row in a range of CSN ");
		String COLUMN_MAPPINGID = Configuration.Tracing_Table.makeMappingTableColumnNameCoredbId(TABLENAME);
		String COLUMN_MAXCSN    = Configuration.Tracing_Table.getColumnMaxCSN();
		String COLUMN_MINCSN    = Configuration.Tracing_Table.getColumnMinCSN();

		EntityInstance secondEntityInstance = entityInstances.get(1);
		String mappingId        = secondEntityInstance.get(COLUMN_MAPPINGID).toString();
		maxCSN                  = Integer.valueOf(secondEntityInstance.get(COLUMN_MAXCSN).toString());
		minCSN                  = Integer.valueOf(secondEntityInstance.get(COLUMN_MINCSN).toString());
		currentCSN              = 1;

		entityInstances          = iect.traceEntities(TABLENAME, mappingId, minCSN, currentCSN, maxCSN, USERID);
		Describer.describeEntityInstances(entityInstances);

		Assert.assertTrue(entityInstances.size() == 4);
		Assert.assertTrue(entityInstances.get(0).get("CRUD").equals('C'));
		// we can do other assertions like the following one
		Assert.assertTrue((Integer)entityInstances.get(0).get(COLUMNNAME1.toLowerCase())==(Integer)(entityInstance2.get(COLUMNNAME1)));
		Assert.assertTrue(((String)entityInstances.get(0).get(COLUMNNAME2.toLowerCase())).equals((String)(entityInstance2.get(COLUMNNAME2))));
		Assert.assertTrue((Boolean)entityInstances.get(0).get(COLUMNNAME3.toLowerCase())==(Boolean)(entityInstance2.get(COLUMNNAME3)));
		Assert.assertTrue(entityInstances.get(1).get("CRUD").equals('R'));
		Assert.assertTrue(entityInstances.get(2).get("CRUD").equals('U'));
		Assert.assertTrue(entityInstances.get(3).get("CRUD").equals('D'));

		printTag("</PASSED TESTING traceEntities(String tableName, String entityMappingId, int minCSN,int currentCSN,int maxCSN,String userId):>\n");
		return entityInstances;
	}

	public List<EntityInstance> traceEntities(String tableName, String entityMappingId, int startCSN,int endCSN,String userId) {
        printTag("<TESTING traceEntities(String tableName, String entityMappingId, int minCSN,int currentCSN,int maxCSN,String userId):>");

		// read the latest records
		List<EntityInstance>  enityInstances = iect.traceEntities(TABLENAME, USERID);
		Describer.describeEntityInstances(enityInstances);

		printTag("The tracing record of the 1st row in a range of CSN (2-4)");
		String COLUMN_MAPPINGID = Configuration.Tracing_Table.makeMappingTableColumnNameCoredbId(TABLENAME);

		EntityInstance firstEntityInstance = enityInstances.get(1);
		String mappingId        = firstEntityInstance.get(COLUMN_MAPPINGID).toString();
		startCSN                = 2;
		endCSN                  = 4;

		enityInstances          = iect.traceEntities(TABLENAME, mappingId, startCSN, endCSN, USERID);
		Describer.describeEntityInstances(enityInstances);

		Assert.assertTrue(enityInstances.size() == 2);
		Assert.assertTrue(enityInstances.get(0).get("CRUD").equals('R'));
		Assert.assertTrue(enityInstances.get(1).get("CRUD").equals('U'));

		printTag("</PASSED TESTING traceEntities(String tableName, String entityMappingId, int minCSN,int currentCSN,int maxCSN,String userId):>\n");
		return enityInstances;
	}

	public ClassToTables generateMappingTableTracing(ClassToTables leftTable, AttributeClass[] leftAcl, ClassToTables rightTable, AttributeClass[] rightAcl) {
		printTag("<TESTING generateMappingTableTracing(ClassToTables leftTable, AttributeClass[] leftAcl, ClassToTables rightTable, AttributeClass[] rightAcl):>");
		ClassToTables mappingTable   = null;

		AttributeClass column1       = new AttributeClass(COLUMNNAME1, Integer.class);
        column1.setPrimaryKey(true);
        AttributeClass column2       = new AttributeClass(COLUMNNAME2, String.class);
        AttributeClass column3       = new AttributeClass(COLUMNNAME3, Boolean.class);
        IndexClass indexClass        = new IndexClass(new AttributeClass[]{column2,column3}, null);
        EntityTracingClass table1    = new EntityTracingClass("TESTTABLE1", new AttributeClass[]{column1,column2,column3}, new IndexClass[]{indexClass});

        AttributeClass column4       = new AttributeClass(COLUMNNAME1, Integer.class);
        column4.setPrimaryKey(true);
        AttributeClass column5       = new AttributeClass(COLUMNNAME2, String.class);
        EntityTracingClass table2    = new EntityTracingClass("TESTTABLE2", new AttributeClass[]{column4,column5}, null);

        mappingTable = iect.generateMappingTableWithTracing(table1, new AttributeClass[]{column1}, table2, new AttributeClass[]{column4});
        mappingTable.describe(0);

        Assert.assertTrue(mappingTable.getAttributeClassList().length == 3);
        Assert.assertTrue(mappingTable.getAttributeClassList()[1].getType().equals(column1.getType()));
        Assert.assertTrue(mappingTable.getAttributeClassList()[2].getType().equals(column4.getType()));

        printTag("</PASSED TESTING generateMappingTableTracing(ClassToTables leftTable, AttributeClass[] leftAcl, ClassToTables rightTable, AttributeClass[] rightAcl):>\n");
        return mappingTable;
	}

	public int getLatestChangeSetNumber() {
		printTag("<TESTING getLatestChangeSetNumber():/>\n");
		return iect.getLatestChangeSetNumber();
	}

	public int generateLatestChangeSetNumber(String userId) {
		int changeSetNumber = getLatestChangeSetNumber();
		printTag("<TESTING generateLatestChangeSetNumber(String userId):>");
		printTag("changesetNumbser is : " + changeSetNumber);

		int newChangeSetNumber = iect.generateLatestChangeSetNumber(userId);
		printTag("changesetNumbser is : " + newChangeSetNumber);
		Assert.assertTrue(newChangeSetNumber == changeSetNumber + 1);

		printTag("<PASSED TESTING generateLatestChangeSetNumber(String userId):>\n");
		return newChangeSetNumber;
	}
}

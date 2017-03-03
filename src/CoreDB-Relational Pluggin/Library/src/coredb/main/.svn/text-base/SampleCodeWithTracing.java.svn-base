/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.main;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

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
 * @author zhaostephen
 */
//public class SampleCodeWithTracing implements IEntityControllerTracing { // To Include For Enforcement
public class SampleCodeWithTracing {
	private static EntityControllerTracing iet            = null;

	private static final String pathToFile  = "/tmp/credential.txt";
	private static final String USERID      = "User";

	private static final String TABLENAME   = "TestTracingTable";
	private static final String COLUMNNAME1 = "Column1";
	private static final String COLUMNNAME2 = "Column2";
	private static final String COLUMNNAME3 = "Column3";
	
	private SampleCodeWithTracing(String path) throws SQLException {
    	iet = new EntityControllerTracing(path);
    }
    
    private static boolean deployTracingTableSchema(IEntityControllerTracing iec) {

        AttributeClass column1       = new AttributeClass(COLUMNNAME1, Integer.class);
        column1.setPrimaryKey(true);
        AttributeClass column2       = new AttributeClass(COLUMNNAME2, String.class);
        AttributeClass column3       = new AttributeClass(COLUMNNAME3, Boolean.class);
        IndexClass indexClass        = new IndexClass(new AttributeClass[]{column2,column3}, null);

        EntityTracingClass entityTracingClass = new EntityTracingClass(TABLENAME, new AttributeClass[]{column1,column2,column3}, new IndexClass[]{indexClass});

        List<ClassToTables> entityClassList   = new LinkedList<ClassToTables>();
        entityClassList.add(entityTracingClass);
        return  iec.deployDefinitionList(entityClassList);

    }
	
	private void doTRACING() {
		traceEntities(TABLENAME, USERID);
		traceEntities(TABLENAME, new CompoundStatementManual(), USERID);
		traceEntities(TABLENAME, "0", 0, 0, 0, USERID);
		traceEntities(TABLENAME, "0", 0, 0, USERID);
		
	}
	
	private static void printTag(String tag){
	       System.out.println(tag);
	}
	    
    public static void main(String[] args) throws SQLException, InterruptedException{
		SampleCodeWithTracing testCode = new SampleCodeWithTracing(pathToFile);
		iet.dropAllTables();
		
        // Initialise API as Tracing mode
        iet.initializeMode(true, Mode.TRACING);
        
        // Deploy table schema
        deployTracingTableSchema(iet);
        printTag("");
        
        // create
	    int changeSetNumber = testCode.createEntities(USERID, null);
	    printTag("changesetNumbser is : " + changeSetNumber);
	    
	    // read
	    List<EntityInstance> entityInstances = testCode.readEntities(USERID, TABLENAME, "sql");
	    changeSetNumber = iet.getLatestChangeSetNumber();
	    printTag("changesetNumbser is : " + changeSetNumber);
	    
	    entityInstances = testCode.readEntities(USERID, TABLENAME, new CompoundStatementManual());
	    changeSetNumber = iet.getLatestChangeSetNumber();
	    printTag("changesetNumbser is : " + changeSetNumber);
	    
	    // update
	    changeSetNumber = testCode.updateEntities(USERID, entityInstances);
	    printTag("changesetNumbser is : " + changeSetNumber);
	    
	    // delete 
	    changeSetNumber = testCode.deleteEntities(USERID, entityInstances);
	    printTag("changesetNumbser is : " + changeSetNumber);
        
		// TRACING
		printTag("\n------------------TRACING---------------------------");
		testCode.doTRACING();
		
		// mapping table
		printTag("\n------------------MAPPING---------------------------");
		testCode.generateMappingTableTracing(null, null, null, null);

		// changeSetNumber
		printTag("\n------------------CHANGESETNUMNBER---------------------------");
		printTag("changesetNumbser is : " + testCode.getLatestChangeSetNumber());
		printTag("changesetNumbser is : " + testCode.generateLatestChangeSetNumber(USERID));
		
		iet.dropAllTables();
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
		printTag("------------------CREATE---------------------------");
		int changeSetNumber = iet.getLatestChangeSetNumber();
		
		printTag("createEntities(String userId, List<? extends EntityInstance> entities):");
		List<EntityInstance> entityInstances = new LinkedList<EntityInstance>();
		EntityInstance entityInstance1 = iet.createEntityInstanceFor(TABLENAME);
	    entityInstance1.set(COLUMNNAME1, 1);
	    entityInstance1.set(COLUMNNAME2, "test1");
	    entityInstance1.set(COLUMNNAME3, true);
	    entityInstances.add(entityInstance1);
	
	    EntityInstance entityInstance2 = iet.createEntityInstanceFor(TABLENAME);
	    entityInstance2.set(COLUMNNAME1, 2);
	    entityInstance2.set(COLUMNNAME2, "test2");
	    entityInstance2.set(COLUMNNAME3, true);
	    entityInstances.add(entityInstance2);
	
	    changeSetNumber = iet.createEntitiesWithTracing(USERID, entityInstances);
	    printTag("Base table data");
	    //entityInstances = iec.readEntitiesWithTracing(TABLENAME, new CompoundStatementManual());
	    entityInstances = iet.getDatabaseConnection().read(TABLENAME, new CompoundStatementManual().toSQLStatement());
	    Describer.describeEntityInstances(entityInstances);
	
	    printTag("");
		return changeSetNumber;
	}
	
	public List<EntityInstance> readEntities(String userId, String tableName, SQLStatement sql) {
		printTag("------------------READ---------------------------");
		printTag("readEntities(String userId, String tableName, SQLStatement sql)");
		
		CompoundStatementAuto CSA = new CompoundStatementAuto(Q.AND);
		// Add a condition as SQLStatement(the interface of ConditionalStatement)
		AttributeClass column3 = iet.getDatabaseConnection().getEntityClass(TABLENAME, false).getAttributeClassList()[2];
		CSA.addCompoundStatement(new ConditionalStatement(column3, Q.E, Boolean.FALSE));
		// Read
		List<EntityInstance> entityInstances = iet.readEntitiesWithTracing(USERID, TABLENAME, CSA);
		Describer.describeEntityInstances(entityInstances);
		printTag("");
		return entityInstances;	
	}
	
	public List<EntityInstance> readEntities(String userId, String tableName, String sql) {
		printTag("------------------READ---------------------------");
		printTag("readEntities(String, String, String):");
		
		sql = COLUMNNAME3 + Q.E + Boolean.TRUE;
		// Read 
		List<EntityInstance> entityInstances = iet.readEntitiesWithTracing(USERID, TABLENAME, sql);
		Describer.describeEntityInstances(entityInstances);
		printTag("");
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
		printTag("------------------UPDATE---------------------------");
		int changeSetNumber = iet.getLatestChangeSetNumber();
		
		printTag("updateEntities(String userId, List<? extends EntityInstance> entities):");
		printTag("Before update");
		List<EntityInstance>  enityInstances = iet.getDatabaseConnection().read(TABLENAME, new CompoundStatementManual().toSQLStatement());
		Describer.describeEntityInstances(enityInstances);
		if(enityInstances.size()>0){
		    EntityInstance entityInstance = enityInstances.get(0);
		    entityInstance.set(COLUMNNAME2, "test1 after update");
		    entityInstance.set(COLUMNNAME3, false);
		    
		    EntityInstance entityInstance1 = enityInstances.get(1);
		    entityInstance1.set(COLUMNNAME2, "test2 after update");
		    entityInstance1.set(COLUMNNAME3, false);
		    changeSetNumber = iet.updateEntitiesWithTracing(USERID, enityInstances);
		}else{
		    printTag("Update failed");
	    }
		printTag("After update");
		enityInstances = iet.getDatabaseConnection().read(TABLENAME, new CompoundStatementManual().toSQLStatement());
		Describer.describeEntityInstances(enityInstances);
		printTag("");
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
    	printTag("------------------DELETE---------------------------");
    	int changeSetNumber = iet.getLatestChangeSetNumber();
    	
    	printTag("deleteEntities(String userId, List<? extends EntityInstance> entities):");
    	printTag("Before delete");
    	List<EntityInstance>  entityInstances = iet.getDatabaseConnection().read(TABLENAME, new CompoundStatementManual().toSQLStatement());
    	Describer.describeEntityInstances(entityInstances);
    	if(entityInstances.size()>0){
    	    changeSetNumber = iet.deleteEntitiesWithTracing(USERID, entityInstances);
    	}else{
    	    printTag("Delete failed");
    	}
    	printTag("After delete");
    	entityInstances = iet.getDatabaseConnection().read(TABLENAME, new CompoundStatementManual().toSQLStatement());
    	Describer.describeEntityInstances(entityInstances);
    	printTag("");
    	return changeSetNumber;
	}
    
	public int deleteEntities(List<? extends EntityInstance> entities, String userId) {
		return deleteEntities(userId, entities);
	}
	
	public List<EntityInstance> traceEntities(String tableName, String userId) {
        printTag("traceEntities(String tableName, String userId):");
		
		printTag("The latest CSN tracing rescord of the whole table");
		List<EntityInstance>  enityInstances = iet.traceEntities(TABLENAME, USERID);
		Describer.describeEntityInstances(enityInstances);
		printTag("");
		return enityInstances;
	}
	
	public List<EntityInstance> traceEntities(String tableName, SQLStatement sql, String userId) {
        printTag("traceEntities(String tableName, SQLStatement sql, String userId):");
		
		printTag("The sql condition statement is ConditionalStatement(column3, Q.E, Boolean.FALSE);");
		CompoundStatementAuto CSA = new CompoundStatementAuto(Q.AND);
		// Add a condition as SQLStatement(the interface of ConditionalStatement)
		AttributeClass column3 = iet.getDatabaseConnection().getEntityClass(TABLENAME, false).getAttributeClassList()[2];
		CSA.addCompoundStatement(new ConditionalStatement(column3, Q.E, Boolean.FALSE));
		
		List<EntityInstance>  enityInstances = iet.traceEntities(TABLENAME, CSA, USERID);
		Describer.describeEntityInstances(enityInstances);
		 
		printTag("");
		return enityInstances;
	}
	
	public List<EntityInstance> traceEntities(String tableName, String entityMappingId, int minCSN,int currentCSN,int maxCSN,String userId) {
        printTag("traceEntities(String tableName, String entityMappingId, int minCSN,int currentCSN,int maxCSN,String userId):");
		
		printTag("The latest CSN tracing rescord of the whole table");
		List<EntityInstance>  enityInstances = iet.traceEntities(TABLENAME, USERID);
		Describer.describeEntityInstances(enityInstances);
		 
		printTag("The tracing record of the 2nd row in a range of CSN ");
		String COLUMN_MAPPINGID = Configuration.Tracing_Table.makeMappingTableColumnNameCoredbId(TABLENAME);
		String COLUMN_MAXCSN    = Configuration.Tracing_Table.getColumnMaxCSN();
		String COLUMN_MINCSN    = Configuration.Tracing_Table.getColumnMinCSN();
		
		EntityInstance secondEntityInstance = enityInstances.get(1);
		String mappingId        = secondEntityInstance.get(COLUMN_MAPPINGID).toString();
		maxCSN                  = Integer.valueOf(secondEntityInstance.get(COLUMN_MAXCSN).toString());
		minCSN                  = Integer.valueOf(secondEntityInstance.get(COLUMN_MINCSN).toString());
		currentCSN              = 1;
		 
		enityInstances          = iet.traceEntities(TABLENAME, mappingId, minCSN, currentCSN, maxCSN, USERID);
		Describer.describeEntityInstances(enityInstances);
		         
		printTag("");
		return enityInstances;
	}
	
	public List<EntityInstance> traceEntities(String tableName, String entityMappingId, int startCSN,int endCSN,String userId) {
        printTag("traceEntities(String tableName, String entityMappingId, int minCSN,int currentCSN,int maxCSN,String userId):");
		
		printTag("The latest CSN tracing rescord of the whole table");
		List<EntityInstance>  enityInstances = iet.traceEntities(TABLENAME, USERID);
		Describer.describeEntityInstances(enityInstances);
		 
		printTag("The tracing record of the 1st row in a range of CSN (2-4)");
		String COLUMN_MAPPINGID = Configuration.Tracing_Table.makeMappingTableColumnNameCoredbId(TABLENAME);
		
		EntityInstance firstEntityInstance = enityInstances.get(1);
		String mappingId        = firstEntityInstance.get(COLUMN_MAPPINGID).toString();
		startCSN                = 2;
		endCSN                  = 4;
		 
		enityInstances          = iet.traceEntities(TABLENAME, mappingId, startCSN, endCSN, USERID);
		Describer.describeEntityInstances(enityInstances);
		         
		printTag("");
		return enityInstances;
	}
	
	public ClassToTables generateMappingTableTracing(ClassToTables leftTable, AttributeClass[] leftAcl, ClassToTables rightTable, AttributeClass[] rightAcl) {
		printTag("generateMappingTableTracing(ClassToTables leftTable, AttributeClass[] leftAcl, ClassToTables rightTable, AttributeClass[] rightAcl):");
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

        mappingTable = iet.generateMappingTableWithTracing(table1, new AttributeClass[]{column1}, table2, new AttributeClass[]{column4});
        
        mappingTable.describe(0);
        return mappingTable;
	}
	
	public int getLatestChangeSetNumber() {
		printTag("getLatestChangeSetNumber():");
		return iet.getLatestChangeSetNumber();
	}
	
	public int generateLatestChangeSetNumber(String userId) {
		printTag("generateLatestChangeSetNumber(String userId):");
		return iet.generateLatestChangeSetNumber(userId);
	}
}
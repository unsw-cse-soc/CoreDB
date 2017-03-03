/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package checkers;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import coredb.controller.EntityControllerBase;
import coredb.controller.IEntityControllerBase;
import coredb.sql.CompoundStatementManual;
import coredb.sql.ConditionalStatement;
import coredb.unit.AttributeClass;
import coredb.unit.ClassToTables;
import coredb.unit.EntityInstance;
import coredb.unit.EntitySecurityClass;
import coredb.unit.Operation;
import coredb.utils.Describer;

/**
 *
 * @author vmc
 */
public class Main {

    private static List<ClassToTables> entityClasses;
    private static List<EntityInstance> entities;

    public static void main(String[] args) throws Exception {
        String option = "test-types";

//    	ConfigurationValues.init(args[0]);
//        String option = args[1];

/*
		System.out.printf("%15s = %-40s\n", "databaseName", databaseName);
		System.out.printf("%15s = %-40s\n", "dbURL", dbURL);
		System.out.printf("%15s = %-40s\n", "databaseType", databaseType);
		System.out.printf("%15s = %-40s\n", "databaseDriver", databaseDriver);
//*/
        if (option.equals("test-driver")) {
            test_driver(1);
        } else if (option.equals("test-types")) {
            test_createTables(1);
            test_createTablesAndEntities(1);
            test_UpdateMappingTable(1);
            test_createEntities(1);
            test_deleteEntities(1);
            test_InsertDataWithoutCreateTables(1);
            test_CRUDWithoutCreate(1);
            test_CRUDWithoutData(1);
            test_CRUD(1);
            test_allCRUD(1);
            test_ReadEntities(1);
        } else if (option.equals("test-randomTables")) {
		    test_RandomTest(1);
        }
    }

    public static boolean test_driver(int indent) {
        try {
    	    System.out.printf("%"+indent+"s %-7s\n"," ","START TO --->test_driver  ");
    	    IEntityControllerBase iec =
            new EntityControllerBase("/tmp/credential.txt");
            System.out.printf("%"+indent+"s %-7s\n"," ","Passed");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.printf("%"+indent+"s %-7s\n"," ","Failed");
            return false;
        }
    }

    /**
     * Create EntityClass: This test case creates tables
     * @return true if process successful
     */
    public static boolean test_createTables(int indent) {
    	try {
    	    System.out.printf("%"+indent+"s %-7s\n"," ","START TO --->test_createTables");
    		//create EntityClasses
    		IEntityControllerBase iec = new EntityControllerBase("/tmp/credential.txt");
    		iec.dropAllTables();
        	entityClasses = TestCode.generateEntityClassList(iec,false, false);
        	iec.deployDefinitionList(entityClasses, true, true);
            System.out.printf("%"+indent+"s %-7s\n"," ","Passed");
            return true;
    	} catch(Exception e) {
    		System.out.printf("%"+indent+"s %-7s\n"," ","Failed");
    		e.printStackTrace();
    		return false;
    	}
    }
    /**
     * Create Initial: This test case creates tables and inserts data
     * @return true if process successful
     */
    public static boolean test_createTablesAndEntities(int indent) {
    	try
    	{
    	    System.out.printf("%"+indent+"s %-7s\n"," ","START TO --->test_createTablesAndEntities");
    		//create EntityClasses and entities
    		EntityControllerBase iec = new EntityControllerBase("/tmp/credential.txt");
    		iec.dropAllTables();
        	entityClasses = TestCode.generateEntityClassList(iec,false, false);
        	iec.deployDefinitionList(entityClasses, true, true);

        	entities = TestCode.generateEntities(iec);
Describer.describeEntityInstances(entities);
        	iec.createEntities(entities);
            System.out.printf("%"+indent+"s %-7s\n"," ","Passed");
            return true;
    	} catch(Exception e) {
    		e.printStackTrace();
    		System.out.printf("%"+indent+"s %-7s\n"," ","Failed");
    		return false;
    	}
    }
    /**
     * Create Entities: This test case inserts data without creates tables
     * @return true if process successful
     */
    public static boolean test_UpdateMappingTable(int indent) {
    	try
    	{
    	    System.out.printf("%"+indent+"s %-7s\n"," ","START TO --->test_UpdateMappingTable");
    		//create entities only
    		IEntityControllerBase iec = new EntityControllerBase("/tmp/credential.txt");

    		//ConditionalStatementHandler csh = new ConditionalStatementHandler();
    		CompoundStatementManual csh = new CompoundStatementManual();
            csh.addConditionalStatement(new ConditionalStatement(new AttributeClass("student_id",Integer.class),Operation.NGE,0));
        	List<EntityInstance> entitiesOfMapping = iec.readEntities("STUDENT_COURSE", new CompoundStatementManual());
        	EntityInstance entityOfMapping = entitiesOfMapping.get(0);
        	entityOfMapping.set("student_id", 7);

        	iec.updateEntity(entityOfMapping);

        	System.out.printf("%"+indent+"s %-7s\n"," ","Passed");
            return true;
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    		System.out.printf("%"+indent+"s %-7s\n"," ","Failed");
    		return false;
    	}
    }
    /**
     * Create Entities: This test case inserts data without creates tables
     * @return true if process successful
     */
    public static boolean test_createEntities(int indent) {
    	try
    	{
    	    System.out.printf("%"+indent+"s %-7s\n"," ","START TO --->test_createEntities");
    		//create entities only
    		IEntityControllerBase iec = new EntityControllerBase("/tmp/credential.txt");
        	//entities = TestCode.generateEntities(iec);
        	iec.deleteEntities(entities);
        	iec.createEntities(entities);

            System.out.printf("%"+indent+"s %-7s\n"," ","Passed");
            return true;
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    		System.out.printf("%"+indent+"s %-7s\n"," ","Failed");
    		return false;
    	}
    }
    /**
     *  create tables and inserts data, then delete data and re-inserts data
     * @return true if process successful
     */
    public static boolean test_deleteEntities(int indent) {
        try {
            System.out.printf("%"+indent+"s %-7s\n"," ","START TO --->test_deleteEntities");
            if (test_createTablesAndEntities(indent+4) && test_createEntities(indent+4)) {
                System.out.printf("%"+indent+"s %-7s\n"," ","Passed");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.printf("%"+indent+"s %-7s\n"," ","failed");
            return false;
        }
        System.out.printf("%"+indent+"s %-7s\n"," ","failed");
        return false;
    }

    /**
     * inserts data before create tables, then creates tables
     * @return true if process successful
     */
    public static boolean test_InsertDataWithoutCreateTables(int indent) {
        try {
            System.out.printf("%"+indent+"s %-7s\n"," ","START TO --->test_InsertDataWithoutCreateTables");
            if (test_createEntities(indent+4) && test_createTablesAndEntities(indent+4)) {
                System.out.printf("%"+indent+"s %-7s\n"," ","Passed");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.printf("%"+indent+"s %-7s\n"," ","Failed");
            return false;
        }
        System.out.printf("%"+indent+"s %-7s\n"," ","Failed");
        return false;
    }
    /**
     * do CRUD on database without creates tables and inserts data
     * @return true if process successful
     */
    public static boolean test_CRUDWithoutCreate(int indent)
    {
    	try {
    	    System.out.printf("%"+indent+"s %-7s\n"," ","START TO --->test_CRUDWithoutCreate");
    		IEntityControllerBase iec = new EntityControllerBase("/tmp/credential.txt");
    	    TestCode.CRUD(iec);
            System.out.printf("%"+indent+"s %-7s\n"," ","Passed");
            return true;
    	} catch(Exception e) {
    		e.printStackTrace();
    		System.out.printf("%"+indent+"s %-7s\n"," ","Failed");
    		return false;
    	}
    }
    /**
     * creates tables and inserts data, then do CRUDs--this is normal test case
     * @return true if process successful
     */
    public static boolean test_CRUDWithoutData(int indent) {
        try {
            System.out.printf("%"+indent+"s %-7s\n"," ","START TO --->test_CRUDWithoutData");
            if (test_createTablesAndEntities(indent+4) && test_CRUDWithoutCreate(indent+4)) {
                    System.out.printf("%"+indent+"s %-7s\n"," ","Passed");
                    return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.printf("%"+indent+"s %-7s\n"," ","Failed");
            return false;
        }
        System.out.printf("%"+indent+"s %-7s\n"," ","Failed");
        return false;
    }

    /**
     * Create tables and then do CRUD
     * @return true if process successful
     */
    public static boolean test_CRUD(int indent) {
        try {
            System.out.printf("%"+indent+"s %-7s\n"," ","START TO --->test_CRUD");
            if (test_createTables(indent+4) && test_CRUDWithoutCreate(indent+4)) {
                System.out.printf("%"+indent+"s %-7s\n"," ","Passed");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.printf("%"+indent+"s %-7s\n"," ","Failed");
            return false;
        }
        System.out.printf("%"+indent+"s %-7s\n"," ","Failed");
        return false;
    }

    /**
     * This method do all CRUD processes test
     * @return true if process successful
     * @throws SQLException
     */
    public static boolean test_allCRUD(int indent) throws SQLException
    {
    	try
    	{
    	System.out.printf("%"+indent+"s %-7s\n"," ","START TO --->test_allCRUD");
    	IEntityControllerBase iec = new EntityControllerBase("/tmp/credential.txt");
    	iec.dropAllTables();
    	iec.refresh();
    	entityClasses = TestCode.generateEntityClassList(iec,false, false);
    	List<ClassToTables> entityClasses1 = TestCode.generateEntityClassList(iec,false, false);
//    	entityClasses1.addAll(entityClasses);
    	iec.deployDefinitionList(entityClasses1, true, true);

//    	entities = TestCode.generateEntities(iec);// will failed, because the column UUID
    	List<EntityInstance> entities1 = TestCode.generateEntities(iec);
//    	entities1.addAll(entities);

    	iec.createEntities(entities1);
//    	iec.createEntities(entities);// will failed, because the column UUID

    	TestCode.CRUD(iec);// works
    	TestCode.CRUD(iec);

    	PerformanceTesting.benchMarkSql(iec, "PERFORMANCE_TEST");

    	iec.deleteEntities(entities1);
//    	iec.deleteEntities(entities);// will failed, because the column UUID

    	PerformanceTesting.benchmark(iec, false, false);

    	TestCode.CRUD(iec);

        System.out.printf("%"+indent+"s %-7s\n"," ","Passed");
        return true;
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		System.out.printf("%"+indent+"s %-7s\n"," ","Failed");
    		return false;
    	}
    }

    /**
     * This method to random test
     * @return true if process successful
     * @throws SQLException
     */
    public static boolean test_RandomTest(int indent) throws SQLException
    {
    	try
    	{
    	    System.out.printf("%"+indent+"s %-7s\n"," ","START TO --->test_RamdonTest");
    		IEntityControllerBase iec = new EntityControllerBase("/tmp/credential.txt");
    		iec.dropAllTables();
    		List<ClassToTables> entityClasses = new LinkedList<ClassToTables>();
    		List<EntitySecurityClass> ecl = RandomTest.generateSchemasByRandom(iec);
    		entityClasses.addAll(ecl);
    		// System.out.println(" -------------------------------- Deploy Scheam Into Database = Start --------------------------------");
    		iec.deployDefinitionList(entityClasses,true,true);
    		// System.out.println(" -------------------------------- Deploy Scheam Into Database = End --------------------------------\n");

    		List<EntityInstance> entitiesRandom = RandomTest.generateEntityInstances(ecl, iec);
    		// System.out.println(" -------------------------------- Insert Data Into Database = Start --------------------------------");
    		iec.createEntities(entitiesRandom);
    		// System.out.println(" -------------------------------- Insert Data Into Database = END --------------------------------\n");

    		// System.out.println(" -------------------------------- Delete Data From Database = Start --------------------------------");
    		iec.deleteEntities(entitiesRandom);
    		// System.out.println(" -------------------------------- Delete Data From Database = END --------------------------------\n");
    	    iec.dropAllTables();
            System.out.printf("%"+indent+"s %-7s\n"," ","Passed");
            return true;
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    		System.out.printf("%"+indent+"s %-7s\n"," ","Failed!");
    		return false;
    	}
    }
    /**
     * this method tests read entities functionality provided by API
     * @return true if process successful
     * @throws SQLException
     */
    public static boolean test_ReadEntities(int indent) throws SQLException {
        try {
            System.out.printf("%"+indent+"s %-7s\n"," ","START TO --->test_ReadEntities");

            IEntityControllerBase iec = new EntityControllerBase("/tmp/credential.txt");

            String tableName = "Xiong";
            AssistantFunctions.createTable(iec, tableName);
            List<EntityInstance> entities1 = AssistantFunctions.createEntityInstances(iec, tableName);

            //ConditionalStatementHandler csh = new ConditionalStatementHandler();
            CompoundStatementManual csh = new CompoundStatementManual();
            csh.addConditionalStatement(new ConditionalStatement(new AttributeClass("ID",Integer.class),Operation.NGE,0));
            List<EntityInstance> entities2 = iec.readEntities(tableName, csh);

            if(AssistantFunctions.isEqual(entities1, entities2)){
                System.out.printf("%"+indent+"s %-7s\n"," ","Passed");
                return true;
            } else {
                System.out.printf("%"+indent+"s %-7s\n"," ","Failed");
                return false;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.printf("%"+indent+"s %-7s\n"," ","Failed");
            return false;

        }
    }
}
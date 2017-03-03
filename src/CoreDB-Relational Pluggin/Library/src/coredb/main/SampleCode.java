/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.main;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import coredb.controller.EntityControllerBase;
import coredb.controller.IEntityControllerBase;
import coredb.database.SchemaInfo;
import coredb.security.EntityInstanceBucket;
import coredb.sql.CompoundStatementAuto;
import coredb.sql.CompoundStatementManual;
import coredb.sql.ConditionalStatement;
import coredb.sql.Q;
import coredb.unit.AttributeClass;
import coredb.unit.ClassToTables;
import coredb.unit.EntityClass;
import coredb.unit.EntityInstance;
import coredb.unit.IndexClass;
import coredb.utils.Describer;

/**
 *
 * @author zhaostephen
 */
public class SampleCode {
    private static final String pathToFile  = "/tmp/credential.txt";

    private static final String TABLENAME   = "TestTable";
    private static final String LEFTABLE    = "LeftTable";
    private static final String RIGHTTABLE  = "RightTable";
    private static final String COLUMNNAME1 = "Column1";
    private static final String COLUMNNAME2 = "Column2";
    private static final String COLUMNNAME3 = "Column3";

    @SuppressWarnings("deprecation")
	public static void main(String[] args) throws SQLException, InterruptedException{
        IEntityControllerBase ecb = new EntityControllerBase(pathToFile);
        ecb.dropAllTables();
        // Deploy table schema
        if(deployTableSchema(ecb)){
        	
            // Get table schema
            getTableSchema(ecb);
            
            // Read out all table names in database
            readTableNameList(ecb);
            
            // CRUD
            createEntities(ecb);
            
            // test for EntityInstanceBucket
            List<EntityInstance> mixedEntityInstances = new LinkedList<EntityInstance>();
            mixedEntityInstances.addAll(ecb.readEntities(TABLENAME, new CompoundStatementManual()));
            mixedEntityInstances.addAll(ecb.readEntities(LEFTABLE, new CompoundStatementManual()));
            mixedEntityInstances.addAll(ecb.readEntities(RIGHTTABLE, new CompoundStatementManual()));
            EntityInstanceBucket bucket = new EntityInstanceBucket(mixedEntityInstances);
            System.out.println("------the sorted list of EntityInstances--------");
            Describer.describeEntityInstances(bucket.getSortEntities());
            System.out.println("------pop the next bunch--------");
            Describer.describeEntityInstances(bucket.popNextBucket());
            System.out.println("------pop the next bunch--------");
            Describer.describeEntityInstances(bucket.popNextBucket());
            System.out.println("------pop the next bunch--------");
            Describer.describeEntityInstances(bucket.popNextBucket());
            
            // READ
            readEntities(ecb);
            
            // Update
            updateEntities(ecb);
            
            // Delete
            deleteEntities(ecb);
            
            // Mapping table
            generateMappingTable(ecb);
            
            // alter table
            altertable(ecb);

        }else{
            throw new Error(SampleCode.class.getName() + ": deployTableSchema is failed");
        }

        ecb.dropAllTables();
    }
    private static boolean deployTableSchema(IEntityControllerBase iec) {

        AttributeClass column1       = new AttributeClass(COLUMNNAME1, Integer.class);
        column1.setAutoIncrement(true);
        column1.setPrimaryKey(true);
        AttributeClass column2       = new AttributeClass(COLUMNNAME2, String.class);
        AttributeClass column3       = new AttributeClass(COLUMNNAME3, Boolean.class);
        IndexClass indexClass        = new IndexClass(new AttributeClass[]{column2,column3}, null);

        EntityClass entityClass      = new EntityClass(TABLENAME, new AttributeClass[]{column1,column2,column3}, new IndexClass[]{indexClass});
        EntityClass leftEntityClass  = new EntityClass(LEFTABLE, new AttributeClass[]{column1,column2,column3}, new IndexClass[]{indexClass});
        EntityClass rightEntityClass = new EntityClass(RIGHTTABLE, new AttributeClass[]{column1,column2,column3}, new IndexClass[]{indexClass});

        List<ClassToTables> entityClassList = new LinkedList<ClassToTables>();
        entityClassList.add(entityClass);
        entityClassList.add(leftEntityClass);
        entityClassList.add(rightEntityClass);
        return  iec.deployDefinitionList(entityClassList);

    }

    private static EntityClass getTableSchema(IEntityControllerBase iec) {
        EntityClass entityClass = iec.getDatabaseConnection().getEntityClass( TABLENAME, false);
        return entityClass;

    }

    private static void readTableNameList(IEntityControllerBase iec) {
        printTag("readTableNameList:");

        List<String> tableNameList = iec.readTableNameList(true);
        for(String tableName : tableNameList){
            System.out.println(tableName);
        }
        printTag("");
    }

    private static void createEntities(IEntityControllerBase iec) {
       printTag("createEntity:");

       List<EntityInstance> entityInstances = new LinkedList<EntityInstance>();
       EntityInstance entityInstance1 = iec.createEntityInstanceFor(TABLENAME);
       entityInstance1.set(COLUMNNAME2, "test1");
       entityInstance1.set(COLUMNNAME3, true);
       entityInstances.add(entityInstance1);

       EntityInstance entityInstance2 = iec.createEntityInstanceFor(TABLENAME);
       entityInstance2.set(COLUMNNAME2, "test2");
       entityInstance2.set(COLUMNNAME3, false);
       entityInstances.add(entityInstance2);
       
       EntityInstance entityInstance3 = iec.createEntityInstanceFor(TABLENAME);
       entityInstance3.set(COLUMNNAME2, "test3");
       entityInstance3.set(COLUMNNAME3, true);
       entityInstances.add(entityInstance3);
       
       EntityInstance entityInstance4 = iec.createEntityInstanceFor(TABLENAME);
       entityInstance4.set(COLUMNNAME2, "test4");
       entityInstance4.set(COLUMNNAME3, false);
       entityInstances.add(entityInstance4);
       
       EntityInstance entityInstance5 = iec.createEntityInstanceFor(LEFTABLE);
       entityInstance5.set(COLUMNNAME2, "test1");
       entityInstance5.set(COLUMNNAME3, true);
       entityInstances.add(entityInstance5);

       EntityInstance entityInstance6 = iec.createEntityInstanceFor(LEFTABLE);
       entityInstance6.set(COLUMNNAME2, "test2");
       entityInstance6.set(COLUMNNAME3, false);
       entityInstances.add(entityInstance6);
       
       EntityInstance entityInstance7 = iec.createEntityInstanceFor(LEFTABLE);
       entityInstance7.set(COLUMNNAME2, "test3");
       entityInstance7.set(COLUMNNAME3, true);
       entityInstances.add(entityInstance7);
       
       EntityInstance entityInstance8 = iec.createEntityInstanceFor(RIGHTTABLE);
       entityInstance8.set(COLUMNNAME2, "test4");
       entityInstance8.set(COLUMNNAME3, false);
       entityInstances.add(entityInstance8);
       
       EntityInstance entityInstance0 = iec.createEntityInstanceFor(RIGHTTABLE);
       entityInstance0.set(COLUMNNAME2, "test4");
       entityInstance0.set(COLUMNNAME3, false);
       entityInstances.add(entityInstance0);
       
       EntityInstance entityInstance9 = iec.createEntityInstanceFor(RIGHTTABLE);
       entityInstance9.set(COLUMNNAME2, "test4");
       entityInstance9.set(COLUMNNAME3, false);
       entityInstances.add(entityInstance9);
       
       iec.createEntities(entityInstances);

       entityInstances = iec.readEntities(TABLENAME, new CompoundStatementManual());
       Describer.describeEntityInstances(entityInstances);
       printTag("");

    }

    private static List<EntityInstance> readEntities(IEntityControllerBase iec) {
      printTag("readEntity:");
      printTag("Common read");

      CompoundStatementAuto CSA = new CompoundStatementAuto(Q.AND);
      // Add a condition as String
      CSA.addCompoundStatement(COLUMNNAME3+ Q.E + Boolean.TRUE);
      // Add a condition as SQLStatement(the interface of ConditionalStatement)
      AttributeClass column3 = iec.getDatabaseConnection().getEntityClass(TABLENAME, false).getAttributeClassList()[2];
      CSA.addCompoundStatement(new ConditionalStatement(column3, Q.E, Boolean.TRUE));
      // Read and describe
      List<EntityInstance> entityInstances = iec.readEntities(TABLENAME, CSA);
      // Describe the result
      Describer.describeEntityInstances(entityInstances);

      printTag("Lazy read");
      CompoundStatementManual CSM = new CompoundStatementManual();
      // Create the condition by manual
      CSM.addConditionalStatement(Q.SELECT    + Q.STAR + Q.FROM + TABLENAME + Q.WHERE);
      CSM.addConditionalStatement(COLUMNNAME3 + Q.E    + Boolean.TRUE);
      // Execute the SQL and get the lazy entity instance which can supoort the complex query
      List<EntityInstance> lazyEntityInstances = iec.executeArbitrarySQL("queryname", CSM.toSQLStatement());
      // Describe the result
      Describer.describeEntityInstances(lazyEntityInstances);
      printTag("");
      return entityInstances;

    }

    private static void updateEntities(IEntityControllerBase iec) {
        printTag("updateEntities:");

        printTag("Before update");
        List<EntityInstance>  enityInstances = iec.readEntities(TABLENAME, new CompoundStatementManual());
        Describer.describeEntityInstances(enityInstances);
        if(enityInstances.size()>0){
            EntityInstance entityInstance = enityInstances.get(0);
            entityInstance.set(COLUMNNAME2, "test1 after update");
            entityInstance.set(COLUMNNAME3, false);
            iec.updateEntities(enityInstances);
        }else{
            printTag("Update failed");
        }
        printTag("After update");
        enityInstances = iec.readEntities(TABLENAME, new CompoundStatementManual());
        Describer.describeEntityInstances(enityInstances);
        printTag("");

    }

    private static void deleteEntities(IEntityControllerBase iec) {
        printTag("deleteEntities:");

        printTag("Before delete");
        List<EntityInstance>  enityInstances = iec.readEntities(TABLENAME, new CompoundStatementManual());
        Describer.describeEntityInstances(enityInstances);
        if(enityInstances.size()>0){
            EntityInstance entityInstance = enityInstances.get(0);
            iec.deleteEntity(entityInstance);
        }else{
            printTag("Delete failed");
        }
        printTag("After delete");
        enityInstances = iec.readEntities(TABLENAME, new CompoundStatementManual());
        Describer.describeEntityInstances(enityInstances);

        printTag("");

    }
    

	public static void altertable(IEntityControllerBase ec) {
		System.out.println("\n --------Before alter table:--------\n");
		Describer.describeTable(ec, TABLENAME);
		
		// Add Column
		System.out.println("\n --------After add a column named 'COLUMNANME4':--------\n");
		AttributeClass newAttribute = new AttributeClass("COLUMNANME4", String.class);
		addColumn(ec, newAttribute);
		Describer.describeTable(ec, TABLENAME);
		
		// Edit Column	
		System.out.println("\n --------After edit a column name from 'COLUMN3' to 'COLUMNANME5':-------- \n");
		AttributeClass oldAttribute = new AttributeClass(SchemaInfo.refactorColumnName(TABLENAME, COLUMNNAME3), Boolean.class);
		AttributeClass editAttribute = new AttributeClass("COLUMNANME5", String.class);
		editColumn(ec, TABLENAME, oldAttribute, editAttribute);
		Describer.describeTable(ec, TABLENAME);
	
		// Delete Column 
		System.out.println("\n --------After delete a column named '" + COLUMNNAME2 + "':-------- \n");
		AttributeClass deleteAttribute = new AttributeClass(COLUMNNAME2, String.class);
		deleteColumn(ec, deleteAttribute);
		Describer.describeTable(ec, TABLENAME);
			
	}
	
	private static void addColumn(IEntityControllerBase ec, AttributeClass newAttribute) {
		// Alter table
		EntityClass alterEntityClass  = ec.getDatabaseConnection().getEntityClass(TABLENAME, false);
		List<AttributeClass> newAttributes = new LinkedList<AttributeClass>();
		for(AttributeClass oldAttribute : alterEntityClass.getAttributeClassList()){
			newAttributes.add(oldAttribute);
		}
		newAttributes.add(newAttribute);
		alterEntityClass.setAttributeClassList(newAttributes.toArray(new AttributeClass[0]));
		ec.getDatabaseConnection().alterTable(alterEntityClass);
		
	}
	
	private static void editColumn(IEntityControllerBase ec, String tableName, AttributeClass oldAttribute, AttributeClass newAttribute) {
		ec.getDatabaseConnection().alterColumn(tableName, oldAttribute, newAttribute);	
		
	}
	
	private static void deleteColumn(IEntityControllerBase ec, AttributeClass deleteAttribute) {
		// Alter table
		EntityClass alterEntityClass  = ec.getDatabaseConnection().getEntityClass(TABLENAME, false);
		List<AttributeClass> newAttributes = new LinkedList<AttributeClass>();
		for(AttributeClass oldAttribute : alterEntityClass.getAttributeClassList()){
			if(!oldAttribute.getName().equals(SchemaInfo.refactorColumnName(TABLENAME,deleteAttribute.getName()))){
				newAttributes.add(oldAttribute);
			}
		}
		alterEntityClass.setAttributeClassList(newAttributes.toArray(new AttributeClass[0]));
		ec.getDatabaseConnection().alterTable(alterEntityClass);
		
	}

    private static void generateMappingTable(IEntityControllerBase iec) {
        EntityClass leftEntityClass = iec.getDatabaseConnection().getEntityClass(LEFTABLE, false);
        EntityClass rightEntityClass = iec.getDatabaseConnection().getEntityClass(RIGHTTABLE, false);
        List<ClassToTables> cttl = new LinkedList<ClassToTables>();
        cttl.add( iec.generateMappingTable(leftEntityClass, new AttributeClass[]{leftEntityClass.getAttributeClassList()[1]}, rightEntityClass, new AttributeClass[]{rightEntityClass.getAttributeClassList()[1]}));
        iec.deployDefinitionList(cttl);

    }

    private static void printTag(String tag){
       System.out.println(tag);
       
    }
}

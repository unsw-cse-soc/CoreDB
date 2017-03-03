/**
 * 
 */
package checkers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.LinkedList;
import java.util.List;

import org.apache.ddlutils.model.TypeMap;

import coredb.controller.IEntityControllerBase;
import coredb.sql.CompoundStatementManual;
import coredb.sql.ConditionalStatement;
import coredb.unit.AttributeClass;
import coredb.unit.ClassToTables;
import coredb.unit.EntityClass;
import coredb.unit.EntityInstance;
import coredb.unit.EntitySecurityClass;
import coredb.unit.EntityTracingClass;
import coredb.unit.IndexClass;
import coredb.unit.Operation;

/**
 * @author sean
 * 
 */
public class PerformanceTesting {
    static int start = 0;
    static int end = 100;

	/**
	 * create 100 student records for testing speed
	 * 
	 * @param iec IEntityControllerBase
	 * @param security is turn security function on, or not
	 * @param tracing is turn Tracing function on, or not
	 */
	public static void benchmark(IEntityControllerBase iec, boolean security, boolean tracing) {
        // TABLE ONE
        String PERFORMANCE_STUDENT = "PERFORMANCE_STUDENT".toLowerCase();

		List<AttributeClass> STU_acl = new LinkedList<AttributeClass>();// case 1:student
		AttributeClass STU_ac1 = new AttributeClass("ID".toLowerCase(),           Integer.class,  TypeMap.INTEGER);
		STU_ac1.setPrimaryKey(true);// is PK
		AttributeClass STU_ac2 = new AttributeClass("PASSPORT".toLowerCase(),     Integer.class,  TypeMap.INTEGER);
		AttributeClass STU_ac3 = new AttributeClass("NAME".toLowerCase(),         String.class,   TypeMap.VARCHAR);
		AttributeClass STU_ac4 = new AttributeClass("GRADE".toLowerCase(),        String.class,   TypeMap.VARCHAR);
		AttributeClass STU_ac5 = new AttributeClass("COURSE_CODE".toLowerCase(),  String.class,   TypeMap.VARCHAR);
		STU_acl.add(STU_ac1);
		STU_acl.add(STU_ac2);
		STU_acl.add(STU_ac3);
		STU_acl.add(STU_ac4);
		STU_acl.add(STU_ac5);
		
        ClassToTables student = null;
        if (security && tracing) /** VMC: TO BE IMPLEMENTED */;
        else if (security)  student = new EntitySecurityClass(PERFORMANCE_STUDENT, STU_acl.toArray(new AttributeClass[0]), null);
        else if (tracing)   student = new EntityTracingClass(PERFORMANCE_STUDENT, STU_acl.toArray(new AttributeClass[0]), null);
        else                student = new EntityClass(PERFORMANCE_STUDENT, STU_acl.toArray(new AttributeClass[0]), null);

        // TABLE TWO
        String PERFORMANCE_STUDENT_INDEX = "PERFORMANCE_STUDENT_INDEX".toLowerCase();
        List<AttributeClass> STU_acl_index = new LinkedList<AttributeClass>();          // case 2: student
		AttributeClass STU_ac1_index = new AttributeClass("ID".toLowerCase(),           Integer.class,  TypeMap.INTEGER);
		STU_ac1_index.setPrimaryKey(true);// is PK
		AttributeClass STU_ac2_index = new AttributeClass("PASSPORT".toLowerCase(),     Integer.class,  TypeMap.INTEGER);
		AttributeClass STU_ac3_index = new AttributeClass("NAME".toLowerCase(),         String.class,   TypeMap.VARCHAR);
		AttributeClass STU_ac4_index = new AttributeClass("GRADE".toLowerCase(),        String.class,   TypeMap.VARCHAR);
		AttributeClass STU_ac5_index = new AttributeClass("COURSE_CODE".toLowerCase(),  String.class,   TypeMap.VARCHAR);
		STU_acl_index.add(STU_ac1_index);
		STU_acl_index.add(STU_ac2_index);
		STU_acl_index.add(STU_ac3_index);
		STU_acl_index.add(STU_ac4_index);
		STU_acl_index.add(STU_ac5_index);

		IndexClass index = new IndexClass(STU_ac1_index, null, false);
		
        ClassToTables student_index = null;
        if (security && tracing) /** VMC: TO BE IMPLEMENTED */;
        else if (security)  student_index = new EntitySecurityClass(PERFORMANCE_STUDENT_INDEX, STU_acl_index.toArray(new AttributeClass[0]), new IndexClass[]{index});
        else if (tracing)   student_index = new EntityTracingClass(PERFORMANCE_STUDENT_INDEX, STU_acl_index.toArray(new AttributeClass[0]), new IndexClass[]{index});
        else                student_index = new EntityClass(PERFORMANCE_STUDENT_INDEX, STU_acl_index.toArray(new AttributeClass[0]), new IndexClass[]{index});

        // TABLE THREE
        String PERFORMANCE_STUDENT_INDEX_2 = "PERFORMANCE_STUDENT_INDEX_2".toLowerCase();
        List<AttributeClass> STU_acl_index_2 = new LinkedList<AttributeClass>();          // case 2: student
		AttributeClass STU_ac1_index_2 = new AttributeClass("ID".toLowerCase(),           Integer.class,  TypeMap.INTEGER);
		STU_ac1_index_2.setPrimaryKey(true);// is PK
		AttributeClass STU_ac2_index_2 = new AttributeClass("PASSPORT".toLowerCase(),     Integer.class,  TypeMap.INTEGER);
		AttributeClass STU_ac3_index_2 = new AttributeClass("NAME".toLowerCase(),         String.class,   TypeMap.VARCHAR);
		AttributeClass STU_ac4_index_2 = new AttributeClass("GRADE".toLowerCase(),        String.class,   TypeMap.VARCHAR);
		AttributeClass STU_ac5_index_2 = new AttributeClass("COURSE_CODE".toLowerCase(),  String.class,   TypeMap.VARCHAR);
		STU_acl_index_2.add(STU_ac1_index_2);
		STU_acl_index_2.add(STU_ac2_index_2);
		STU_acl_index_2.add(STU_ac3_index_2);
		STU_acl_index_2.add(STU_ac4_index_2);
		STU_acl_index_2.add(STU_ac5_index_2);

		index = new IndexClass(STU_ac1_index_2, null, false);
		
        ClassToTables student_index_2 = null;
        if (security && tracing) /** VMC: TO BE IMPLEMENTED */;
        else if (security)  student_index_2 = new EntitySecurityClass(PERFORMANCE_STUDENT_INDEX_2, STU_acl_index_2.toArray(new AttributeClass[0]), new IndexClass[]{index});
        else if (tracing)   student_index_2 = new EntityTracingClass(PERFORMANCE_STUDENT_INDEX_2, STU_acl_index_2.toArray(new AttributeClass[0]), new IndexClass[]{index});
        else                student_index_2 = new EntityClass(PERFORMANCE_STUDENT_INDEX_2, STU_acl_index_2.toArray(new AttributeClass[0]), new IndexClass[]{index});

        List<ClassToTables> entityClasses = new LinkedList<ClassToTables>();
        entityClasses.add(student);
        entityClasses.add(student_index);
        entityClasses.add(student_index_2);
		iec.deployDefinitionList(entityClasses, true, true);

        //iec.refresh();
//CREATE
        multipleSingleCreates(iec, PERFORMANCE_STUDENT);
        multipleSingleCreates(iec, PERFORMANCE_STUDENT_INDEX);
        multipleSingleCreates(iec, PERFORMANCE_STUDENT_INDEX_2);
//READ
        multipleSingleReads(iec, PERFORMANCE_STUDENT);
        multipleSingleReads(iec, PERFORMANCE_STUDENT_INDEX);
        multipleSingleReads(iec, PERFORMANCE_STUDENT_INDEX_2);
//UPDATE
        multipleSingleUpdates(iec, PERFORMANCE_STUDENT);
        multipleSingleUpdates(iec, PERFORMANCE_STUDENT_INDEX);
        multipleSingleUpdates(iec, PERFORMANCE_STUDENT_INDEX_2);
//DELETE
        multipleSingleDeletes(iec, PERFORMANCE_STUDENT);
        multipleSingleDeletes(iec, PERFORMANCE_STUDENT_INDEX);
        multipleSingleDeletes(iec, PERFORMANCE_STUDENT_INDEX_2);
       

        List<String> tableNames = new LinkedList<String>();
        tableNames.add(PERFORMANCE_STUDENT);
        tableNames.add(PERFORMANCE_STUDENT_INDEX);
        tableNames.add(PERFORMANCE_STUDENT_INDEX_2);
        iec.dropTables(tableNames);
	}

    /**
     * do multiple-single Create process
     * 
     * @param iec IEntityControllerBase
     * @param tableName the name of table that we want insert date
     */
	private static void multipleSingleCreates(IEntityControllerBase iec, String tableName) {
        EntityInstance student;

        //List<EntityInstance> eList = new LinkedList<EntityInstance>();
        
        long start_time = System.currentTimeMillis();
        long package_time = 0;
		for (int id = start; id < end; id++) {
            long diff_start_time = System.currentTimeMillis();
            student = iec.createEntityInstanceFor(tableName);
            long diff_end_time = System.currentTimeMillis();
            package_time += (diff_end_time - diff_start_time);

            student.set("ID".toLowerCase(), id);
			student.set("PASSPORT".toLowerCase(), id);
			student.set("NAME".toLowerCase(), "Sean" + id);
			student.set("GRADE".toLowerCase(), "DN");
			student.set("COURSE_CODE".toLowerCase(), "COMP9331");
			//eList.add(student);
			iec.createEntity(student);
		}
		//iec.createEntities(eList);
        long end_time = System.currentTimeMillis();
        long total_time_taken = end_time-start_time;
        System.out.printf("%10s %7s\n","TIME TAKEN---CREATE", total_time_taken, total_time_taken-package_time);
    }

    /**
     * do multiple-single READ process
     * 
     * @param iec IEntityControllerBase
     * @param tableName the name of table that we want read data
     */
    private static void multipleSingleReads(IEntityControllerBase iec, String tableName) {
    	long start_time   = System.currentTimeMillis();
        long package_time = 0;
    	for(int id = start; id < end; id ++) {
            long diff_start_time = System.currentTimeMillis();
            CompoundStatementManual csh = new CompoundStatementManual();
            //ConditionalStatementHandler csh = new ConditionalStatementHandler();
            csh.addConditionalStatement(new ConditionalStatement(new AttributeClass("ID",Integer.class),Operation.E,id+""));
            long diff_end_time = System.currentTimeMillis();
            package_time += (diff_end_time - diff_start_time);
            iec.readEntities(tableName, csh).get(0);
    	}
    	long end_time = System.currentTimeMillis();
        long total_time_taken = end_time - start_time;
        System.out.printf("%10s %7s\n","TIME TAKEN---READ", total_time_taken, total_time_taken-package_time);
    }

    /**
     * do multiple-single UPDATE process
     * 
     * @param iec IEntityControllerBase
     * @param tableName the name of table that we want to update data
     */
    private static void multipleSingleUpdates(IEntityControllerBase iec, String tableName) {
    	EntityInstance student;
    	long start_time = System.currentTimeMillis();
    	for(int id = start; id < end; id ++)
    	{
            //ConditionalStatementHandler csh = new ConditionalStatementHandler();
            CompoundStatementManual csh = new CompoundStatementManual();
            csh.addConditionalStatement(new ConditionalStatement(new AttributeClass("ID".toLowerCase(),Integer.class,TypeMap.INTEGER),"=",id+""));

            student = iec.readEntities(tableName, csh).get(0);
    		student.set("ID".toLowerCase(), id);
			student.set("PASSPORT".toLowerCase(), id);
			student.set("NAME".toLowerCase(), "xiong" + id);
			student.set("GRADE".toLowerCase(), "DN6666666");
			student.set("COURSE_CODE".toLowerCase(), "COMP9331");
			iec.updateEntity(student);
    	}
    	long end_time = System.currentTimeMillis();
    	System.out.printf("%10s %7s\n","TIME TAKEN---UPDATE", end_time-start_time);
    }

    /**
     * do multiple-single DELETE process
     * 
     * @param iec IEntityControllerBase
     * @param tableName the name of table that we want delete data
     */
    private static void multipleSingleDeletes(IEntityControllerBase iec, String tableName) {
    	EntityInstance student;
    	long start_time = System.currentTimeMillis();
    	for(int id = start; id < end; id ++)
    	{
            //ConditionalStatementHandler csh = new ConditionalStatementHandler();
            CompoundStatementManual csh = new CompoundStatementManual();
            csh.addConditionalStatement(new ConditionalStatement(new AttributeClass("ID".toLowerCase(),Integer.class,TypeMap.INTEGER),"=",id+""));
            student = iec.readEntities(tableName, csh).get(0);
    		iec.deleteEntity(student);
    	}
    	long end_time = System.currentTimeMillis();
    	System.out.printf("%10s %7s\n","TIME TAKEN---DELETE", end_time-start_time);
    }
    /**
     * create 1000 student records for testing sql statement speed
     * 
     * @param iec IEntityControllerBase
     * @param tableName  the name of table that we want to to sql queries
     */
    public static void benchMarkSql(IEntityControllerBase iec, String tableName)
    {
		createTableSql(iec, tableName);
		
		multipleSingleCreateSql(iec, tableName);
		multipleSingleReadSql(iec, tableName);
		multipleSingleUpdateSql(iec, tableName);
		multipleSingleDeleteSql(iec, tableName);
		
		dropTableSql(iec, tableName);
    }
    /**
     * Create table using pure sql statement
     * @param iec IEntityControllerBase
     * @param tableName the name of table will created
     */
    private static void createTableSql(IEntityControllerBase iec, String tableName)
    {
    	Connection connection = iec.getDatabaseConnection().getConnection();
    	String sql = "CREATE TABLE "+tableName+
    			" (P_Id int,	LastName varchar(255),FirstName varchar(255), Address varchar(255),	City varchar(255),PRIMARY KEY (P_Id))";
    	try
    	{
    		PreparedStatement ps = connection.prepareStatement(sql);
    		ps.execute();
    	}
    	 catch (Exception ex) {
 			ex.printStackTrace();
 		}
    }
    /**
     * Drop all table from database using pure sql statement
     * @param iec IEntityController
     * @param tableName the name of table will deleted
     */
    private static void dropTableSql(IEntityControllerBase iec, String tableName)
    {
    	Connection connection = iec.getDatabaseConnection().getConnection();
    	String sql = "drop TABLE "+tableName;
    	try
    	{
    		PreparedStatement ps = connection.prepareStatement(sql);
    		ps.execute();
    	}
    	 catch (Exception ex) {
 			ex.printStackTrace();
 		}
    }
    /**
     * This method will do multiple single inserting 
     * data rows into database using pure sql statement
     * @param iec IEntityControllerBase
     * @param tableName the name of table will data row inserted
     */
    private static void multipleSingleCreateSql(IEntityControllerBase iec, String tableName)
    {
		Connection connection = iec.getDatabaseConnection().getConnection();
		
		/**
		 * generate sql statement to insert 100 rows into table
		 */
		String sql = "";
		long start_time = System.currentTimeMillis();
		for (int i = 0; i < end; i ++)
		{
			sql = "insert into " + tableName + " values("+ i +", 'sean','xiong','avoca','syd');";
			try {
				PreparedStatement ps = connection.prepareStatement(sql);

				ps.execute();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
    	
		long end_time = System.currentTimeMillis();
    	System.out.printf("%10s %7s\n","TIME TAKEN---CREATE SQL", end_time-start_time);
    }
    /**
     * This method will do multiple single reading 
     * data rows into database using pure sql statement
     * @param iec IEntityControllerBase
     * @param tableName the name of table will data rows read from
     */
    private static void multipleSingleReadSql(IEntityControllerBase iec, String tableName)
    {
		Connection connection = iec.getDatabaseConnection().getConnection();
		
		/**
		 * generate sql statement to insert 100 rows into table
		 */
		String sql = "";
		long start_time = System.currentTimeMillis();
		for (int i = 0; i < end; i ++)
		{
			sql = "select * from " + tableName + " where P_Id = " + i;
			try {
				PreparedStatement ps = connection.prepareStatement(sql);
				ps.executeQuery();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		long end_time = System.currentTimeMillis();
    	System.out.printf("%10s %7s\n","TIME TAKEN---READ   SQL", end_time-start_time);
    }
    /**
    * This method will do multiple single updating 
    * data rows into database using pure sql statement
    * @param iec IEntityControllerBase
    * @param tableName the name of table will data rows updated
    */
    private static void multipleSingleUpdateSql(IEntityControllerBase iec, String tableName)
    {
		Connection connection = iec.getDatabaseConnection().getConnection();
		
		/**
		 * generate sql statement to insert 100 rows into table
		 */
		String sql = "";
		long start_time = System.currentTimeMillis();
		for (int i = 0; i < end; i ++)
		{
			sql = "update " + tableName + " set FirstName = 'zhao xu' where P_Id = " + i;
			try {
				PreparedStatement ps = connection.prepareStatement(sql);
				ps.execute();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		long end_time = System.currentTimeMillis();
    	System.out.printf("%10s %7s\n","TIME TAKEN---UPDATE SQL", end_time-start_time);
    }
    /**
     * This method will do multiple single deleting 
     * data rows into database using pure sql statement
     * @param iec IEntityControllerBase
     * @param tableName the name of table will data rows deleted
     */
    private static void multipleSingleDeleteSql(IEntityControllerBase iec, String tableName)
    {
		Connection connection = iec.getDatabaseConnection().getConnection();
		
		/**
		 * generate sql statement to insert 100 rows into table
		 */
		String sql = "";
		long start_time = System.currentTimeMillis();
		for (int i = 0; i < end; i ++)
		{
			sql = "delete from " + tableName + " where P_Id = " + i;
			try {
				PreparedStatement ps = connection.prepareStatement(sql);
				ps.execute();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		long end_time = System.currentTimeMillis();
    	System.out.printf("%10s %7s\n","TIME TAKEN---DELETE SQL", end_time-start_time);
    }
}

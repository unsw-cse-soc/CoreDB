package checkers;


import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import coredb.config.Configuration;
import coredb.controller.IEntityControllerBase;
import coredb.database.DatabaseConnection;
import coredb.sql.CompoundStatementManual;
import coredb.sql.ConditionalStatement;
import coredb.sql.Q;
import coredb.unit.AttributeClass;
import coredb.unit.ClassToTables;
import coredb.unit.EntityClass;
import coredb.unit.EntityInstance;
import coredb.unit.IndexClass;
import coredb.unit.Operation;
import coredb.utils.Describer;

/**
 * This class is an example to show how to use this API.
 * 
 * @author Sean
 */
public class TestCode {
	
    public static String TABLE_DELIMETER = Configuration.TABLE_DELIMITER;
    /**
     * This method generates a list of EntityClasses (The definition of table).
     * @param iec IEntityControllerBase
	 * @param security if security is true security functionality will be fired
	 * @param tracing  is tracing is true tracing functionality will be fired
	 * @return a list of EntityClasses (The definition of table)
	 */
	public static List<ClassToTables> generateEntityClassList(IEntityControllerBase iec, boolean security,	boolean tracing) {

		/**
		 * here, we will create three tables, 'student', 'course' and one mapping table of them 'student_course'
		 */
		List<ClassToTables> el = new LinkedList<ClassToTables>();

		/**
		 * TABLE 1: STUDENT
		 */
		List<AttributeClass> STU_acl = new LinkedList<AttributeClass>();
		AttributeClass STU_ac1 =  new AttributeClass("ID",       Integer.class);
		STU_ac1.setPrimaryKey(true);// set attribute 'ID' as PK
		STU_acl.add(STU_ac1);
        AttributeClass STU_ac2 =new AttributeClass("PASSPORT",   String.class); // this attribute is an index
		STU_acl.add(STU_ac2);
		STU_acl.add(new AttributeClass("name",       String.class));
		STU_acl.add(new AttributeClass("GRADE",      Double.class));
		STU_acl.add(new AttributeClass("DOB",        Date.class));
		STU_acl.add(new AttributeClass("MALE",       Boolean.class));
		STU_acl.add(new AttributeClass("GENDER",     Character.class));
		STU_acl.add(new AttributeClass("TIME",       Time.class));
		STU_acl.add(new AttributeClass("TIMESTAMP",  Timestamp.class));
		STU_acl.add(new AttributeClass("UUID",       String.class));// this column tests UUID
		STU_acl.add(new AttributeClass("COURSE_CODE",String.class));

		// create indexes for table 'STUDENT', index1 includes only one column and index2 is a composite index that contains two columns
		IndexClass index1 = new IndexClass(STU_ac2,null,false);
		//index1.setName("ID_Index");// optional
		//index1.setUnique(true);// optional
		IndexClass index2 = new IndexClass(new AttributeClass[]{STU_ac1, STU_ac2}, true);
		
		ClassToTables student = new EntityClass("STUDENT", STU_acl.toArray(new AttributeClass[0]), new IndexClass[]{index1, index2});

        /**
         * TABLE 2: COURSE
         */
		List<AttributeClass> COURSE_acl = new LinkedList<AttributeClass>(); 
		AttributeClass COURSE_ac1 = new AttributeClass("COURSE_CODE",String.class);
		COURSE_ac1.setPrimaryKey(true);
		COURSE_acl.add(COURSE_ac1);
		COURSE_acl.add(new AttributeClass("DESCRIPTION",String.class));

		ClassToTables course = new EntityClass("COURSE", COURSE_acl .toArray(new AttributeClass[0]), null);

		/**
		 * TABLE 3: STUDENT_COURSE(MAPPING TABLE)
         * Create mapping table for 'student' and 'course'.
		 * This mapping table has three columns; two from 'student' another one
         * from 'course'. All columns to be a composite unique index.
		 */
		//ClassToTables STU_COURSE = generateMappingTable(student, new AttributeClass[]{STU_ac1,STU_ac2},course, new AttributeClass[]{COURSE_ac1}, security, tracing);
		ClassToTables STU_COURSE = iec.generateMappingTable(student, new AttributeClass[]{STU_ac1,STU_ac2},course, new AttributeClass[]{COURSE_ac1});
		
		el.add(student);
		el.add(course);
		el.add(STU_COURSE);

		return el;
	}

	/**
	 * This method generates a list of entities (data rows of table)
	 * @param iec the interface of entity controller
	 * @return a list of Entities
	 */
	@SuppressWarnings("deprecation")
	public static List<EntityInstance> generateEntities(IEntityControllerBase iec){
		List<EntityInstance> entityInstances = new LinkedList<EntityInstance>();
		
		// create Time, Date and TimeStamp objects
		Time currentTime = new Time(1, 1, 1);
		DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date day = null;
		day = Date.valueOf("1981-01-01");
		Timestamp currentTimestamp = new Timestamp(1000000000);
		
		/**
		 * here, we will generate two entities(rows) for each table ('student', 'course' and 'student_course')
		 */
		EntityInstance student, course,stu_course;

		// data for 'student'
		student = iec.createEntityInstanceFor("STUDENT");
		student.set("ID",1);
		student.set("PASSPORT", "STU0001");
		student.set("UUID", UUID.randomUUID());
		student.set("NAME".toLowerCase(), "Sean");
		student.set("GRADE", 88.4);
		student.set("DOB", day);
		student.set("MALE", true);
		student.set("GENDER", Character.valueOf('Y'));
		student.set("TIME", currentTime);
		//student.set("TIMESTAMP", currentTimestamp);
		student.set("timEStamp", currentTimestamp);// you can type columns' name in whatever case you want
		student.set("CoURSE_CODE", "COMP9331");

        // data for 'course'
		course = iec.createEntityInstanceFor("COURSE");
		course.set("DESCRIPTION", "Mobile Networking");
		course.set("COURSE_CODE", "COMP9331");


		// data for mapping table 'student_course'
		stu_course = iec.createEntityInstanceFor("STUDENT_COURSE");
		stu_course.set(Configuration.Public_Attribute.PrefixOfRowId + "_STUDENT_COURSE_" + Configuration.Public_Attribute.SubfixOfRowId, UUID.randomUUID());
		stu_course.set("STUDENT_ID", 1);
		stu_course.set("STUDENT_PASSPORT", "STU0001");
		stu_course.set("COURSE_COURSE_CODE", "COMP9021");

		// add beans into list
		entityInstances.add(student);
		entityInstances.add(course);
		entityInstances.add(stu_course);

		return entityInstances;
	}

	/**
	 * interface to test CRUD processes
	 * 
	 * @param iec the interface of entity controller
	 * @throws ParseException 
	 */
	@SuppressWarnings("deprecation")
	public static void CRUD(IEntityControllerBase iec) {
		String tableName = "STUDENT";
		System.out.println("<<<---<<START TO DO CRUD ON TABLE [" + tableName + "]");

		// create Time, Date and TimeStamp objects
		Time currentTime = new Time(1, 1, 1);
		Time currentTime1 = new Time(3, 3, 3);
		DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date day = null, day1 = null;

		day = Date.valueOf("1981-01-01");day1 = Date.valueOf("1981-02-02");
		//try {day = dateFormat1.parse("1981-01-01 00:00:00");day1 = dateFormat1.parse("1981-02-02 00:00:00");}
		//catch (ParseException e) {System.err.println(e.getMessage());e.printStackTrace();}
		Timestamp currentTimestamp = new Timestamp(1000000000);
		Timestamp currentTimestamp1 = new Timestamp(System.currentTimeMillis() + 10000);
		
        CompoundStatementManual csh = new CompoundStatementManual();
		//ConditionalStatementHandler csh = new ConditionalStatementHandler();
		
		/**
		 *  case 1: TEST DATA TYPE -- INTEGER
		 */
		System.out.println("< TEST DATA TYPE>       : INTEGER ");
		AttributeClass COLUMN_ID = DatabaseConnection.getAttributeClass(tableName, "id");
        csh.addConditionalStatement(new ConditionalStatement(COLUMN_ID,Operation.NGE, 1));
		csh.addConditionalStatement(Q.AND);
        csh.addConditionalStatement(new ConditionalStatement(COLUMN_ID,Operation.NL, 2));
        List<EntityInstance> entities = iec.readEntities(tableName, csh);
        
		System.out.println("    < BEFORE UPDATE>    : COLUMN: [id]");
		Describer.describeEntityInstances(entities);
		int index = 1;
		for (EntityInstance entity : entities) {index ++;entity.set("id", index + 10000);}// update data, it will failed because we can't update PK using DynaBean API
		System.out.println("    </BEFORE UPDATE>    : COLUMN: [id]");
		
		iec.updateEntities(entities);// updating
		
		System.out.println("    < AFTER UPDATE>     : COLUMN: [id]");
		System.out.println("        <-AFTER UPDATE>     : COLUMN: [id]");//after update data, but the query statement is un-updated
		entities = iec.readEntities(tableName, csh);
		for (EntityInstance entity : entities) {Describer.describeEntityInstance(entity);}
		System.out.println("        <-AFTER UPDATE>     : COLUMN: [id]");
        System.out.println("        <+AFTER UPDATE>     : COLUMN: [id]");
        csh.clear();
        csh.addConditionalStatement(new ConditionalStatement(COLUMN_ID,Operation.NGE, 1));
		csh.addConditionalStatement(Q.AND);
        csh.addConditionalStatement(new ConditionalStatement(COLUMN_ID,Operation.NL, 2));
		entities = iec.readEntities(tableName, csh);
		for (EntityInstance entity : entities) {Describer.describeEntityInstance(entity);}
		System.out.println("        <+AFTER UPDATE>     : COLUMN: [id]");
		System.out.println("    </AFTER UPDATE>     : COLUMN: [id]");
		
		System.out.println("</TEST DATA TYPE>       : INTEGER \n");
		
		/**
		 *  case 2: TEST DATA TYPE -- STRING
		 */
		System.out.println("< TEST DATA TYPE>       : STRING ");
		csh =  new CompoundStatementManual();
		AttributeClass COLUMN_PASSWORD = DatabaseConnection.getAttributeClass(tableName, "passport");
		AttributeClass COLUMN_NAME = DatabaseConnection.getAttributeClass(tableName, "name");
		csh.addConditionalStatement(new ConditionalStatement(COLUMN_PASSWORD, Operation.LIKE,"%TU%"));
		csh.addConditionalStatement(Q.AND);
		csh.addConditionalStatement(new ConditionalStatement(COLUMN_NAME, Operation.E,"Sean"));
		entities = iec.readEntities(tableName, csh);
		
		System.out.println("    < BEFORE UPDATE>    : COLUMN: [name]");
		Describer.describeEntityInstances(entities);
		for (EntityInstance entity : entities) {entity.set("name", "xiong");}
		System.out.println("    </BEFORE UPDATE>    : COLUMN: [name]");
		
		iec.updateEntities(entities);// updating
		
		System.out.println("    < AFTER UPDATE>     : COLUMN: [name]");
		System.out.println("        <-AFTER UPDATE>     : COLUMN: [name]");
		entities = iec.readEntities(tableName, csh);
		for (EntityInstance entity : entities) {Describer.describeEntityInstance(entity);}
		System.out.println("        <-AFTER UPDATE>     : COLUMN: [name]");
        System.out.println("        <+AFTER UPDATE>     : COLUMN: [name]");
        csh.clear();
        csh.addConditionalStatement(new ConditionalStatement(COLUMN_PASSWORD,Operation.LIKE,"%TU%"));
		csh.addConditionalStatement(Q.AND);
		csh.addConditionalStatement(new ConditionalStatement(COLUMN_NAME,Operation.E,"xiong"));
		entities = iec.readEntities(tableName, csh);
		for (EntityInstance entity : entities) {Describer.describeEntityInstance(entity);}
		System.out.println("        <+AFTER UPDATE>     : COLUMN: [name]");
		System.out.println("    </AFTER UPDATE>     : COLUMN: [name]");
		
		System.out.println("</TEST DATA TYPE>       : STRING \n");
		
		/**
		 *  case 3: TEST DATA TYPE -- DOUBLE
		 */
		System.out.println("< TEST DATA TYPE>       : DOUBLE ");
		//csh = new ConditionalStatementHandler();
		csh =  new CompoundStatementManual();
        AttributeClass COLUMN_GRADE = DatabaseConnection.getAttributeClass(tableName, "grade");
		csh.addConditionalStatement(new ConditionalStatement(COLUMN_GRADE,Operation.E,88.4));
		entities = iec.readEntities(tableName, csh);
		
		System.out.println("    < BEFORE UPDATE>    : COLUMN: [grade]");
		Describer.describeEntityInstances(entities);
		for (EntityInstance entity : entities) {entity.set("grade", 100.00);}
		System.out.println("    </BEFORE UPDATE>    : COLUMN: [grade]");

		iec.updateEntities(entities);// updating
		
		System.out.println("    < AFTER UPDATE>     : COLUMN: [grade]");
		System.out.println("        <-AFTER UPDATE>     : COLUMN: [grade]");
		entities = iec.readEntities(tableName, csh);
		for (EntityInstance entity : entities) {Describer.describeEntityInstance(entity);}
		System.out.println("        <-AFTER UPDATE>     : COLUMN: [grade]");
        System.out.println("        <+AFTER UPDATE>     : COLUMN: [grade]");
        csh.clear();
        csh.addConditionalStatement(new ConditionalStatement(COLUMN_GRADE,Operation.E,100));
		entities = iec.readEntities(tableName, csh);
		for (EntityInstance entity : entities) {Describer.describeEntityInstance(entity);}
		System.out.println("        <+AFTER UPDATE>     : COLUMN: [grade]");
		System.out.println("    </AFTER UPDATE>     : COLUMN: [grade]");
		
		System.out.println("</TEST DATA TYPE>       : DOUBLE \n");
		
		/**
		 *  case 4: TEST DATA TYPE -- DATE
		 */
		System.out.println("< TEST DATA TYPE>       : DATE ");
		//csh = new ConditionalStatementHandler();
		csh =  new CompoundStatementManual();
		AttributeClass COLUMN_DOB = DatabaseConnection.getAttributeClass(tableName, "dob");
		csh.addConditionalStatement(new ConditionalStatement(COLUMN_DOB,Operation.E,day));
		entities = iec.readEntities(tableName, csh);
		
		System.out.println("    < BEFORE UPDATE>    : COLUMN: [dob]");
		Describer.describeEntityInstances(entities);
		for (EntityInstance entity : entities) {entity.set("dob", day1);}
		System.out.println("    </BEFORE UPDATE>    : COLUMN: [dob]");

		iec.updateEntities(entities);// updating
		
		System.out.println("    < AFTER UPDATE>     : COLUMN: [dob]");
		System.out.println("        <-AFTER UPDATE>     : COLUMN: [dob]");
		entities = iec.readEntities(tableName, csh);
		for (EntityInstance entity : entities) {Describer.describeEntityInstance(entity);}
		System.out.println("        <-AFTER UPDATE>     : COLUMN: [dob]");
        System.out.println("        <+AFTER UPDATE>     : COLUMN: [dob]");
        csh.clear();
        csh.addConditionalStatement(new ConditionalStatement(COLUMN_DOB,Operation.E, day1));
		entities = iec.readEntities(tableName, csh);
		for (EntityInstance entity : entities) {Describer.describeEntityInstance(entity);}
		System.out.println("        <+AFTER UPDATE>     : COLUMN: [dob]");
		System.out.println("    </AFTER UPDATE>     : COLUMN: [dob]");
		
		System.out.println("</TEST DATA TYPE>       : DATE \n");
		
		/**
		 *  case 5: TEST DATA TYPE -- BOOLEAN
		 */
		System.out.println("< TEST DATA TYPE>       : BOOLEAN ");
		//csh = new ConditionalStatementHandler();
		csh =  new CompoundStatementManual();
		AttributeClass COLUMN_MALE = DatabaseConnection.getAttributeClass(tableName, "male");
		csh.addConditionalStatement(new ConditionalStatement(COLUMN_MALE,Operation.ISNOT,false));
		entities = iec.readEntities(tableName, csh);
		
		System.out.println("    < BEFORE UPDATE>    : COLUMN: [male]");
		Describer.describeEntityInstances(entities);
		for (EntityInstance entity : entities) {entity.set("male", false);}
		System.out.println("    </BEFORE UPDATE>    : COLUMN: [male]");

		iec.updateEntities(entities);// updating
		
		System.out.println("    < AFTER UPDATE>     : COLUMN: [male]");
		System.out.println("        <-AFTER UPDATE>     : COLUMN: [male]");
		entities = iec.readEntities(tableName, csh);
		for (EntityInstance entity : entities) {Describer.describeEntityInstance(entity);}
		System.out.println("        <-AFTER UPDATE>     : COLUMN: [male]");
        System.out.println("        <+AFTER UPDATE>     : COLUMN: [male]");
        csh.clear();
        csh.addConditionalStatement(new ConditionalStatement(COLUMN_MALE,Operation.IS, false));
		entities = iec.readEntities(tableName, csh);
		for (EntityInstance entity : entities) {Describer.describeEntityInstance(entity);}
		System.out.println("        <+AFTER UPDATE>     : COLUMN: [male]");
		System.out.println("    </AFTER UPDATE>     : COLUMN: [male]");
		
		System.out.println("</TEST DATA TYPE>       : BOOLEAN \n");
		
		/**
		 *  case 6: TEST DATA TYPE -- CHAR
		 */
		System.out.println("< TEST DATA TYPE>       : CHAR ");
		//csh = new ConditionalStatementHandler();
		csh =  new CompoundStatementManual();
		AttributeClass COLUMN_GENDER = DatabaseConnection.getAttributeClass(tableName, "gender");
		csh.addConditionalStatement(new ConditionalStatement(COLUMN_GENDER,Operation.E,'Y'));
		entities = iec.readEntities(tableName, csh);
		
		System.out.println("    < BEFORE UPDATE>    : COLUMN: [gender]");
		Describer.describeEntityInstances(entities);
		for (EntityInstance entity : entities) {entity.set("gender", 'N');}
		System.out.println("    </BEFORE UPDATE>    : COLUMN: [gender]");

		iec.updateEntities(entities);// updating
		
		System.out.println("    < AFTER UPDATE>     : COLUMN: [gender]");
		System.out.println("        <-AFTER UPDATE>     : COLUMN: [gender]");
		entities = iec.readEntities(tableName, csh);
		for (EntityInstance entity : entities) {Describer.describeEntityInstance(entity);}
		System.out.println("        <-AFTER UPDATE>     : COLUMN: [gender]");
        System.out.println("        <+AFTER UPDATE>     : COLUMN: [gender]");
        csh.clear();
        csh.addConditionalStatement(new ConditionalStatement(COLUMN_GENDER,Operation.E, 'N'));
		entities = iec.readEntities(tableName, csh);
		for (EntityInstance entity : entities) {Describer.describeEntityInstance(entity);}
		System.out.println("        <+AFTER UPDATE>     : COLUMN: [gender]");
		System.out.println("    </AFTER UPDATE>     : COLUMN: [gender]");
		
		System.out.println("</TEST DATA TYPE>       : CHAR \n");
		
		/**
		 *  case 7: TEST DATA TYPE -- TIME
		 */
		System.out.println("< TEST DATA TYPE>       : TIME ");
		//csh = new ConditionalStatementHandler();
		csh =  new CompoundStatementManual();
		AttributeClass COLUMN_TIME = DatabaseConnection.getAttributeClass(tableName, "time");
		csh.addConditionalStatement(new ConditionalStatement(COLUMN_TIME,Operation.E,currentTime));
		entities = iec.readEntities(tableName, csh);
		
		System.out.println("    < BEFORE UPDATE>    : COLUMN: [time]");
		Describer.describeEntityInstances(entities);
		for (EntityInstance entity : entities) {entity.set("time", currentTime1);}
		System.out.println("    </BEFORE UPDATE>    : COLUMN: [time]");

		iec.updateEntities(entities);// updating
		
		System.out.println("    < AFTER UPDATE>     : COLUMN: [time]");
		System.out.println("        <-AFTER UPDATE>     : COLUMN: [time]");
		entities = iec.readEntities(tableName, csh);
		for (EntityInstance entity : entities) {Describer.describeEntityInstance(entity);}
		System.out.println("        <-AFTER UPDATE>     : COLUMN: [time]");
        System.out.println("        <+AFTER UPDATE>     : COLUMN: [time]");
        csh.clear();
        csh.addConditionalStatement(new ConditionalStatement(COLUMN_TIME,Operation.E, currentTime1));
		entities = iec.readEntities(tableName, csh);
		for (EntityInstance entity : entities) {Describer.describeEntityInstance(entity);}
		System.out.println("        <+AFTER UPDATE>     : COLUMN: [time]");
		System.out.println("    </AFTER UPDATE>     : COLUMN: [time]");
		
		System.out.println("</TEST DATA TYPE> : TIME \n");
		
		/**
		 *  case 8: TEST DATA TYPE -- TIMESTAMP
		 */
		System.out.println("< TEST DATA TYPE>       : TIMESTAMP ");
		//csh = new ConditionalStatementHandler();
		csh =  new CompoundStatementManual();
		AttributeClass COLUMN_TIMESTAMP = DatabaseConnection.getAttributeClass(tableName, "timestamp");
		csh.addConditionalStatement(new ConditionalStatement(COLUMN_TIMESTAMP,Operation.E,currentTimestamp));
		entities = iec.readEntities(tableName, csh);

		System.out.println("    < BEFORE UPDATE>    : COLUMN: [timestamp]");
		Describer.describeEntityInstances(entities);
		for (EntityInstance entity : entities) { entity.set("timestamp", currentTimestamp1);}
		System.out.println("    </BEFORE UPDATE>    : COLUMN: [timestamp]");

		iec.updateEntities(entities);// updating

		System.out.println("    < AFTER UPDATE>     : COLUMN: [timestamp]");
		System.out.println("        <-AFTER UPDATE>     : COLUMN: [timestamp]");
		entities = iec.readEntities(tableName, csh);
		for (EntityInstance entity : entities) {Describer.describeEntityInstance(entity);}
		System.out.println("        <-AFTER UPDATE>     : COLUMN: [timestamp]");
        System.out.println("        <+AFTER UPDATE>     : COLUMN: [timestamp]");
        csh.clear();
        csh.addConditionalStatement(new ConditionalStatement(COLUMN_TIMESTAMP,Operation.E, currentTimestamp1));
		entities = iec.readEntities(tableName, csh);
		for (EntityInstance entity : entities) {Describer.describeEntityInstance(entity);}
		System.out.println("        <+AFTER UPDATE>     : COLUMN: [timestamp]");
		System.out.println("    </AFTER UPDATE>     : COLUMN: [timestamp]");
		
		System.out.println("</TEST DATA TYPE>       : TIMESTAMP ");
		
		System.out.println("<<<---<<END OF DO CRUD ON TABLE [" + tableName + "]\n");
    }
}

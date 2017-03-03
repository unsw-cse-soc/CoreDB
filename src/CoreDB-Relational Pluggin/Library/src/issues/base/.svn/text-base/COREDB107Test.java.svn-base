package issues.base;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

public class COREDB107Test {
	private final String TESTTABLE1                   = "testtable_1";
	private final String TESTTABLE1_INTEGER_COLUMN    = "TESTTABLE1_INTEGER_COLUMN";
	private final String TESTTABLE1_STRING_COLUMN     = "TESTTABLE1_STRING_COLUMN";
	private final String TESTTABLE1_BOOLEAN_COLUMN    = "TESTTABLE1_BOOLEAN_COLUMN";
	private final String TESTTABLE1_CHARACTER_COLUMN  = "TESTTABLE1_CHARACTER_COLUMN";
	private final String TESTTABLE1_DOUBLE_COLUMN     = "TESTTABLE1_DOUBLE_COLUMN";
	private final String TESTTABLE1_DATE_COLUMN       = "TESTTABLE1_DATE_COLUMN";
	private final String TESTTABLE1_TIME_COLUMN       = "TESTTABLE1_TIME_COLUMN";
	private final String TESTTABLE1_TIMESTAMP_COLUMN  = "TESTTABLE1_TIMESTAMP_COLUMN";
	private final String TESTTABLE1_BYTEA_COLUMN      = "TESTTABLE1_BYTEA_COLUMN";


	
	private final String TESTTABLE2                   = "testtable_2";
	private final String TESTTABLE2_INTEGER_COLUMN    = "TESTTABLE2_INTEGER_COLUMN";
	private final String TESTTABLE2_STRING_COLUMN     = "TESTTABLE2_STRING_COLUMN";
	private final String TESTTABLE2_BOOLEAN_COLUMN    = "TESTTABLE2_BOOLEAN_COLUMN";
	private final String TESTTABLE2_CHARACTER_COLUMN  = "TESTTABLE2_CHARACTER_COLUMN";
	private final String TESTTABLE2_DOUBLE_COLUMN     = "TESTTABLE2_DOUBLE_COLUMN";
	private final String TESTTABLE2_DATE_COLUMN       = "TESTTABLE2_DATE_COLUMN";
	private final String TESTTABLE2_TIME_COLUMN       = "TESTTABLE2_TIME_COLUMN";
	private final String TESTTABLE2_TIMESTAMP_COLUMN  = "TESTTABLE2_TIMESTAMP_COLUMN";
	private final String TESTTABLE2_BYTEA_COLUMN      = "TESTTABLE2_BYTEA_COLUMN";
	
	private IEntityControllerBase iecb ;
	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		COREDB107Test test = new COREDB107Test();
		test.issue1();
	}
	
	private void issue1() throws SQLException{
		this.setupWithError();
	//	this.setupWithNoError();
		this.deployTestTables();
	    Statement sta = iecb.getDatabaseConnection().getConnection().createStatement(); 
	    ResultSet res = sta.executeQuery("SELECT current_database()");
	    res.next();
	    String name = res.getString(1);
	    System.out.println("Connected to database: "+name);
		Assert.assertEquals("coredb", name);
	}

	private void setupWithError() throws SQLException{
		String databaseName   = "coredb";
		String dbURL          = "jdbc:postgresql://localhost:5432/";
		String databaseType   = "postgresql";
		String databaseDriver = "org.postgresql.Driver";
		String user           = "postgres";
		String password	      = "";
		iecb = new EntityControllerBase(dbURL, databaseType, databaseDriver, databaseName, user, password);
		iecb.dropAllTables();
	}
	
	private void setupWithNoError() throws SQLException{
		String databaseName   = "coredb";
		String dbURL          = "jdbc:postgresql://localhost:5432/" + databaseName;
		String databaseType   = "postgresql";
		String databaseDriver = "org.postgresql.Driver";
		String user           = "postgres";
		String password	      = "";
		iecb = new EntityControllerBase(dbURL, databaseType, databaseDriver, databaseName, user, password);
		iecb.dropAllTables();
		iecb.dropAllTables();
	}
	
	private void deployTestTables(){
		AttributeClass testTable1_Integer     = new AttributeClass(TESTTABLE1_INTEGER_COLUMN, Integer.class);
		testTable1_Integer.setAutoIncrement(true);
		testTable1_Integer.setPrimaryKey(true);
		AttributeClass testTable1_String      = new AttributeClass(TESTTABLE1_STRING_COLUMN, String.class);
		AttributeClass testTable1_Boolean     = new AttributeClass(TESTTABLE1_BOOLEAN_COLUMN, Boolean.class);
		AttributeClass testTable1_Character   = new AttributeClass(TESTTABLE1_CHARACTER_COLUMN, Character.class);
		AttributeClass testTable1_Double      = new AttributeClass(TESTTABLE1_DOUBLE_COLUMN, Double.class);
		AttributeClass testTable1_Date        = new AttributeClass(TESTTABLE1_DATE_COLUMN, Date.class);
		AttributeClass testTable1_Time        = new AttributeClass(TESTTABLE1_TIME_COLUMN, Time.class);
		AttributeClass testTable1_TimeStamp   = new AttributeClass(TESTTABLE1_TIMESTAMP_COLUMN, Timestamp.class);
		AttributeClass testTable1_Bytea       = new AttributeClass(TESTTABLE1_BYTEA_COLUMN, byte[].class);
		AttributeClass[] newAttributeClasses1 =  new AttributeClass[]{testTable1_Integer,testTable1_String, testTable1_Boolean, 
				                                                     testTable1_Character, testTable1_Double, testTable1_Date,
				                                                     testTable1_Time, testTable1_TimeStamp, testTable1_Bytea};
		EntityClass testTable1                = new EntityClass(TESTTABLE1,newAttributeClasses1, null);
						
		AttributeClass testTable2_Integer     = new AttributeClass(TESTTABLE2_INTEGER_COLUMN, Integer.class);
		testTable2_Integer.setAutoIncrement(true);
		testTable2_Integer.setPrimaryKey(true);
		AttributeClass testTable2_String      = new AttributeClass(TESTTABLE2_STRING_COLUMN, String.class);
		AttributeClass testTable2_Boolean     = new AttributeClass(TESTTABLE2_BOOLEAN_COLUMN, Boolean.class);
		AttributeClass testTable2_Character   = new AttributeClass(TESTTABLE2_CHARACTER_COLUMN, Character.class);
		AttributeClass testTable2_Double      = new AttributeClass(TESTTABLE2_DOUBLE_COLUMN, Double.class);
		AttributeClass testTable2_Date        = new AttributeClass(TESTTABLE2_DATE_COLUMN, Date.class);
		AttributeClass testTable2_Time        = new AttributeClass(TESTTABLE2_TIME_COLUMN, Time.class);
		AttributeClass testTable2_TimeStamp   = new AttributeClass(TESTTABLE2_TIMESTAMP_COLUMN, Timestamp.class);
		AttributeClass testTable2_Bytea       = new AttributeClass(TESTTABLE2_BYTEA_COLUMN, byte[].class);
		AttributeClass[] newAttributeClasses2 =  new AttributeClass[]{testTable2_Integer,testTable2_String, testTable2_Boolean, 
                													  testTable2_Character, testTable2_Double, testTable2_Date,
                													  testTable2_Time, testTable2_TimeStamp, testTable2_Bytea};	
		EntityClass testTable2                = new EntityClass(TESTTABLE2,newAttributeClasses2, null);
		
		List<ClassToTables> classToTablesList = new LinkedList<ClassToTables>();
		classToTablesList.add(testTable1);
		classToTablesList.add(testTable2);
		iecb.deployDefinitionList(classToTablesList);
	}
}

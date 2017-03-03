package issues.base;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.LazyDynaClass;
import org.apache.ddlutils.model.TypeMap;

import java.sql.ResultSetMetaData;

import junit.framework.Assert;

import coredb.config.Configuration;
import coredb.controller.EntityControllerBase;
import coredb.controller.IEntityControllerBase;
import coredb.database.DatabaseConnection;
import coredb.database.JDBCTypeConverter;
import coredb.sql.CompoundStatementManual;
import coredb.sql.Q;
import coredb.tracing.TracingCRUD;
import coredb.unit.AttributeClass;
import coredb.unit.ClassToTables;
import coredb.unit.EntityClass;
import coredb.unit.EntityInstance;
import coredb.utils.Describer;


public class COREDB103Test {
	
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
	 */
	public static void main(String[] args) {
		try {
			COREDB103Test test = new COREDB103Test();
			test.setup();
			test.issue1();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void issue1() {
		System.out.print("[issue1]\n");
		deployTestTables();
		
		// Create entities
		List<EntityInstance> sourceEntitis = generateResourceEntities();
		iecb.createEntities(sourceEntitis);
		
		// Complicated query
		CompoundStatementManual csm        = new CompoundStatementManual();
		String sql = Q.SELECT + Q.STAR + Q.FROM + TESTTABLE1 + Q.COMMA + TESTTABLE2 + Q.WHERE + TESTTABLE1 + Q.PERIOD + TESTTABLE1_INTEGER_COLUMN + Q.E + TESTTABLE2 + Q.PERIOD + TESTTABLE2_INTEGER_COLUMN;
		System.out.print("<Query : " + sql + ">\n");
		csm.addConditionalStatement(Q.SELECT + Q.STAR + Q.FROM + TESTTABLE1 + Q.COMMA + TESTTABLE2 + Q.WHERE + TESTTABLE1 + Q.PERIOD + TESTTABLE1_INTEGER_COLUMN + Q.E + TESTTABLE2 + Q.PERIOD + TESTTABLE2_INTEGER_COLUMN);
		List<EntityInstance> entityInstances = this.executeArbitraryQuery(iecb.getDatabaseConnection(), TESTTABLE1 +"&"+TESTTABLE2, csm.toSQLStatement());
		Describer.describeEntityInstances(entityInstances);
		System.out.print("</Query : " + sql + ">\n");
		
		// Start assert
		List<Integer> testTable1_Pks        = new LinkedList<Integer>();
		List<Integer> exclude_Pks           = new LinkedList<Integer>();
		List<EntityInstance> union_Entities = iecb.readEntities(TESTTABLE1, "");
		union_Entities.addAll(iecb.readEntities(TESTTABLE2, ""));
		for(EntityInstance entityInstance : union_Entities){
			if(entityInstance.getDynaClass().getName().equalsIgnoreCase(TESTTABLE1)){
				Integer pk_Id = (Integer)entityInstance.get(TESTTABLE1_INTEGER_COLUMN);
				testTable1_Pks.add(pk_Id);
			}else{
				Integer pk_Id = (Integer)entityInstance.get(TESTTABLE2_INTEGER_COLUMN);
				if(!testTable1_Pks.remove(pk_Id)){
					exclude_Pks.add(pk_Id);
				}
			}
		}
		exclude_Pks.addAll(testTable1_Pks);
		Assert.assertTrue(entityInstances.size() >0);
		for(EntityInstance entityInstance :entityInstances){
			Integer pk_Id = (Integer)entityInstance.get(TESTTABLE1_INTEGER_COLUMN);
			Assert.assertTrue(!testTable1_Pks.contains(pk_Id));
		}
		System.out.print("[/issue1]\n");
	}
	
	private List<EntityInstance> generateResourceEntities(){
		EntityInstance t1 = iecb.createEntityInstanceFor(TESTTABLE1);
		EntityInstance t2 = iecb.createEntityInstanceFor(TESTTABLE2);
		EntityInstance t3 = iecb.createEntityInstanceFor(TESTTABLE2);
		
		t1.set(TESTTABLE1_STRING_COLUMN, "ABC");
		t1.set(TESTTABLE1_BOOLEAN_COLUMN, true);
		t1.set(TESTTABLE1_CHARACTER_COLUMN, 'Y');
		t1.set(TESTTABLE1_DOUBLE_COLUMN, 0.02);
		t1.set(TESTTABLE1_DATE_COLUMN, new Date(System.currentTimeMillis()));
		t1.set(TESTTABLE1_TIME_COLUMN, new Time(System.currentTimeMillis()));
		t1.set(TESTTABLE1_TIMESTAMP_COLUMN, new Timestamp(System.currentTimeMillis()));
		t1.set(TESTTABLE1_BYTEA_COLUMN, "Hello".getBytes());
		
		t2.set(TESTTABLE2_STRING_COLUMN, "ABC");
		t2.set(TESTTABLE2_BOOLEAN_COLUMN, true);
		t2.set(TESTTABLE2_CHARACTER_COLUMN, 'Y');
		t2.set(TESTTABLE2_DOUBLE_COLUMN, 0.02);
		t2.set(TESTTABLE2_DATE_COLUMN, new Date(System.currentTimeMillis()));
		t2.set(TESTTABLE2_TIME_COLUMN, new Time(System.currentTimeMillis()));
		t2.set(TESTTABLE2_TIMESTAMP_COLUMN, new Timestamp(System.currentTimeMillis()));
		t2.set(TESTTABLE2_BYTEA_COLUMN, "Hello".getBytes());
		
		List<EntityInstance> entityInstances = new LinkedList<EntityInstance>();
		entityInstances.add(t1);
		entityInstances.add(t2);
		entityInstances.add(t3);
		return entityInstances;
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
	
	private void setup() throws SQLException{
	    iecb = new EntityControllerBase("/tmp/credential.txt");
		iecb.dropAllTables();
	}
	
	private  DynaProperty[]  getDynaProperties(DatabaseConnection databaseConnection, ResultSetMetaData resultSetMetaData) throws SQLException{
		int size = resultSetMetaData.getColumnCount();
		DynaProperty[] properties = new DynaProperty[size];
		for(int i =1 ; i<=size; i++){
			int typeCode          = databaseConnection.getJDBCNativeTypeCodeByJDBCOriginalTypeCode(resultSetMetaData.getColumnType(i));
			String nativeJDBCType = TypeMap.getJdbcTypeName(typeCode);
			DynaProperty property = new DynaProperty(resultSetMetaData.getColumnName(i), JDBCTypeConverter.toJAVAType(nativeJDBCType));
			properties[i-1]       = property;
		}
		return properties;
	}
	
	 public  List<EntityInstance> executeArbitraryQuery(DatabaseConnection databaseConnection, String queryName, String sql){
    	String resultTableName = "";
    	if(databaseConnection.readTableNameList(false).contains(queryName)) resultTableName = queryName;
    	else if(!queryName.endsWith(Configuration.LAZYDYNACLASS))           resultTableName = Configuration.makeLazyDynaClassName(queryName);
    	else                                                                resultTableName = queryName;
      
    	List<EntityInstance> entityInstances = new LinkedList<EntityInstance>();
        Connection connection = databaseConnection.getConnection();

        try {
			PreparedStatement ps  = connection.prepareStatement(sql);
			ResultSet resultSet   = ps.executeQuery();
			entityInstances       = convertResultSetToEntityInstance(databaseConnection, resultTableName, resultSet);
		} catch (SQLException ex) {
			Logger.getLogger(TracingCRUD.class.getName()).log(Level.SEVERE, null, ex);
		}

        return entityInstances;
	}
	
	public  List<EntityInstance> convertResultSetToEntityInstance(DatabaseConnection databaseConnection, String tableName, ResultSet rs) throws SQLException{
	    List<EntityInstance> entityInstances = new LinkedList<EntityInstance>();
	    DynaProperty[] dynaProperties = getDynaProperties(databaseConnection, rs.getMetaData());
	    /**
	     * the block below is design for the case when resultSet is empty.
	     * This method will returns the data structure of query result without any data
	     */
	    EntityInstance instance = null; 
	    while (rs.next()) {
	        if(tableName.endsWith(Configuration.LAZYDYNACLASS)) {
		    	DynaClass dynaClass = new LazyDynaClass(tableName, dynaProperties);
		    	instance = new EntityInstance(dynaClass);
			} else {
				instance = databaseConnection.createEntityInstanceFor(tableName, false);
			}

	        for(int i = 0; i < dynaProperties.length; i ++) {
	    	    String name = dynaProperties[i].getName();
	    	    Object newObject = rs.getObject(name);
	    	    /**
	    	     * the following conversion is because the value of Character in resultSet is a string.
	    	     * Currently we can get the columns type using JDBCTypeConverter when tableName is end with LAZYDYNACLASS
	    	     */
	    	    if (dynaProperties[i].getType().getName().equals(Character.class.getName())) {
	    	    	newObject = ((newObject != null )?newObject.toString().charAt(0) : null);
				}
	    	    instance.set(name, newObject);
	        }
	        entityInstances.add(instance);
	    }
	    
	    return entityInstances;
	}
	

}

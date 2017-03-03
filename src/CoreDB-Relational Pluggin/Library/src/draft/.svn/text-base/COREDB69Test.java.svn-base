package draft;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import coredb.config.Configuration.Column_Info_Table;
import coredb.config.Configuration.Token_Info_Table;
import coredb.controller.EntityControllerSecurity;
import coredb.controller.IEntityControllerSecurity;
import coredb.mode.Mode;
import coredb.mode.RootPermission;
import coredb.sql.CompoundStatementManual;
import coredb.sql.Q;
import coredb.unit.AttributeClass;
import coredb.unit.ClassToTables;
import coredb.unit.EntityInstance;
import coredb.unit.EntitySecurityClass;

public class COREDB69Test {

	private IEntityControllerSecurity iecs = null;
	private HashMap<String, Class> dataTypeMap   = new HashMap<String,Class>();
	private final String COLUMN_STRING      = "COLUMN_STRING";
	private final String COLUMN_INTEGER     = "COLUMN_INTEGER";
	private final String COLUMN_SAMLLBYTEA  = "COLUMN_SAMLLBYTEA";
	//private final String COLUMN_BIGBYTEA    = "COLUMN_BIGBYTEA";
	private final String COLUMN_DATE        = "COLUMN_DATE";
	private final String COLUMN_TIMESTAMP   = "COLUMN_TIMESTAMP";
	private final String COLUMN_TIME        = "COLUMN_TIME";
	private final String COLUMN_DOUBLE      = "COLUMN_DOUBLE";
	private final String COLUMN_BOOLEAN      = "COLUMN_BOOLEAN";
	private final String COLUMN_CHARACTER   = "COLUMN_CHARACTER";
	private String pathToFile;
	
	public void begin(){
		iecs.initializeMode(true, Mode.SECURITY);
		
	}
	
	public void end(){
		iecs.dropAllTables();
		
	}
	
	public void setup( String pathToFile) throws SQLException{
		this.pathToFile = pathToFile;
		iecs = new EntityControllerSecurity(this.pathToFile);
		iecs.dropAllTables();
		dataTypeMap.put(COLUMN_STRING, String.class);
		dataTypeMap.put(COLUMN_INTEGER, Integer.class);
		dataTypeMap.put(COLUMN_SAMLLBYTEA, byte[].class);
	//	dataTypeMap.put(COLUMN_BIGBYTEA, Byte[].class);
		dataTypeMap.put(COLUMN_DATE, Date.class);
		dataTypeMap.put(COLUMN_TIMESTAMP, Timestamp.class);
		dataTypeMap.put(COLUMN_TIME, Time.class);
		dataTypeMap.put(COLUMN_DOUBLE , Double.class);
		dataTypeMap.put(COLUMN_BOOLEAN, Boolean.class);
		dataTypeMap.put(COLUMN_CHARACTER, Character.class);
		
	}
	
	public void COREDB69Test1() throws SQLException {
		begin();	
		
		List<AttributeClass> acl = new LinkedList<AttributeClass>();
		EntityInstance root = iecs.readUser(RootPermission.ROOTUSER, RootPermission.ROOTUSERPASSWORD);
		AttributeClass primaryKey = new AttributeClass(COLUMN_INTEGER, dataTypeMap.get(COLUMN_INTEGER));
		primaryKey.setPrimaryKey(true);
		primaryKey.setAutoIncrement(true);
		acl.add(primaryKey);
		acl.add(new AttributeClass(COLUMN_STRING, dataTypeMap.get(COLUMN_STRING)));
		acl.add(new AttributeClass(COLUMN_SAMLLBYTEA, dataTypeMap.get(COLUMN_SAMLLBYTEA)));
	//	acl.add(new AttributeClass(COLUMN_BIGBYTEA, dataTypeMap.get(COLUMN_BIGBYTEA)));
		acl.add(new AttributeClass(COLUMN_TIMESTAMP, dataTypeMap.get(COLUMN_TIMESTAMP)));
		acl.add(new AttributeClass(COLUMN_TIME, dataTypeMap.get(COLUMN_TIME)));
		acl.add(new AttributeClass(COLUMN_DOUBLE, dataTypeMap.get(COLUMN_DOUBLE)));
		acl.add(new AttributeClass(COLUMN_BOOLEAN, dataTypeMap.get(COLUMN_BOOLEAN)));
		acl.add(new AttributeClass(COLUMN_CHARACTER, dataTypeMap.get(COLUMN_CHARACTER)));
		
		List<ClassToTables> classToTablesList = new LinkedList<ClassToTables>();
		classToTablesList.add(new EntitySecurityClass("Test", acl.toArray(new AttributeClass[0]), null));
		List<EntityInstance> tokens = iecs.getDatabaseConnection().read(Token_Info_Table.makeTokenInfoTableName(), "GroupType ='DEFAULT'");
		iecs.deployDefinitionListWithSecurity(tokens,classToTablesList,false,false);
		
		CompoundStatementManual csm = new CompoundStatementManual();
		csm.addConditionalStatement(Column_Info_Table.getColumnTableName() + Q.E + "'test'");
		List<EntityInstance> columnTableParis= iecs.readEntitiesWithSecurity(root, Column_Info_Table.makeColumnInfoTableName(),csm).getPassedEntities();
		for(EntityInstance columnTablePair : columnTableParis){
			try {
				byte[] value           = (byte[])columnTablePair.get(Column_Info_Table.getColumnProgramingDataType());
				Class acutualDataType  = (Class)new ObjectInputStream(new ByteArrayInputStream(value)).readObject();
				String columnName      = (String)columnTablePair.get(Column_Info_Table.getColumnColumnName());
				Class expectedDataType = dataTypeMap.get(columnName.toUpperCase());
				System.out.println("From database : " + acutualDataType + " --> " + "From attribute: " + expectedDataType);
				Assert.assertEquals(expectedDataType, acutualDataType);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		end();
	}

	public static void main(String[] argv) throws SQLException{
		COREDB69Test test = new COREDB69Test();
		test.setup("/tmp/credential.txt");	
		test.COREDB69Test1();
	}
	

}

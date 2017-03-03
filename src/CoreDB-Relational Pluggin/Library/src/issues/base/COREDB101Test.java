package issues.base;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.ddlutils.DatabaseOperationException;

import coredb.controller.EntityControllerBase;
import coredb.controller.IEntityControllerBase;
import coredb.sql.CompoundStatementManual;
import coredb.sql.Q;
import coredb.unit.AttributeClass;
import coredb.unit.ClassToTables;
import coredb.unit.EntityClass;
import coredb.unit.EntityInstance;

public class COREDB101Test {

	private IEntityControllerBase iecb ;
	
	private final String TESTTABLE1         = "testtable_1";
	private final String TESTTABLE1_COLUMN1 = "testcolumn_1";
	private final String TESTTABLE1_COLUMN2 = "testcolumn_2";
	
	private final String TESTTABLE2    = "testtable2";
	private final String TESTTABLE2_COLUMN1 = "testcolumn_1";
	private final String TESTTABLE2_COLUMN2 = "testcolumn_2";

	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
	
		COREDB101Test test = new COREDB101Test();
		test.issue1();
		//test.issue2();
		
	}
	
	private void issue1(){
		System.out.print("[issue1]\n");
		deployTestTablesForIssue1();
		EntityInstance t1 = iecb.createEntityInstanceFor(TESTTABLE1);
		EntityInstance t2 = iecb.createEntityInstanceFor(TESTTABLE1);
		EntityInstance t3 = iecb.createEntityInstanceFor(TESTTABLE2);
		iecb.createEntity(t1);
		iecb.createEntity(t2);
		iecb.createEntity(t3);
		
		CompoundStatementManual csm = new CompoundStatementManual();
		csm.addConditionalStatement(Q.SELECT + Q.STAR + Q.FROM + TESTTABLE1 + Q.COMMA + TESTTABLE2  + Q.WHERE + TESTTABLE1 + Q.PERIOD + TESTTABLE1_COLUMN1 + Q.NE + TESTTABLE2 + Q.PERIOD + TESTTABLE2_COLUMN1);

		System.out.println(csm.toSQLStatement());
		List<EntityInstance> entities = iecb.executeArbitrarySQL("TestTable1_TestTable2",csm.toSQLStatement());
		EntityInstance entityInstance = entities.get(0);

		System.out.print("The result expected is: \n");
		String out = String.format("%20s | %20s | %20s | %20s\n", TESTTABLE1_COLUMN1, TESTTABLE1_COLUMN2, TESTTABLE2_COLUMN1, TESTTABLE2_COLUMN2);
		out       += String.format("%20s | %20s | %20s | %20s\n", "2","null", "1", "null");
		System.out.print(out);
		System.out.print("The actual result is: \n");
		out		   = String.format("%20s | %20s | %20s | %20s\n", entityInstance.getAttributeNames().get(0), entityInstance.getAttributeNames().get(1), entityInstance.getAttributeNames().get(2), entityInstance.getAttributeNames().get(3));
		out       += String.format("%20s | %20s | %20s | %20s\n", entityInstance.get(TESTTABLE1 + Q.PERIOD + TESTTABLE1_COLUMN1),entityInstance.get(TESTTABLE1_COLUMN2), entityInstance.get(TESTTABLE2 + Q.PERIOD + TESTTABLE2_COLUMN1), entityInstance.get(TESTTABLE2_COLUMN2));
		System.out.print(out);
		//System.out.print("<Bug catched>\n");
		iecb.dropAllTables();
		System.out.println("[/issue1]\n");
	}
	
    @Deprecated
	private void issue2(){
		System.out.print("[issue2]\n");
		deployTestTablesForIssue2();
		EntityInstance t1 = iecb.createEntityInstanceFor(TESTTABLE1);
		System.out.print("<Create a new row which only have one column>\n");
		try{
			iecb.createEntity(t1);
		}catch (DatabaseOperationException e) {
			e.printStackTrace();
			System.out.print("<Bug catched> \n");
		}				
		iecb.dropAllTables();
		System.out.println("[/issue2]\n");
	}
	
	public COREDB101Test(){
		try {
			setup();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void setup() throws SQLException{
	    iecb = new EntityControllerBase("/tmp/credential.txt");
		iecb.dropAllTables();
	}

	private void deployTestTablesForIssue1(){
		AttributeClass testTable1_Ac1         = new AttributeClass(TESTTABLE1_COLUMN1, Integer.class);
		testTable1_Ac1.setAutoIncrement(true);
		testTable1_Ac1.setPrimaryKey(true);
		AttributeClass testTable1_Ac2         = new AttributeClass(TESTTABLE1_COLUMN2, String.class);
		EntityClass testTable1                = new EntityClass(TESTTABLE1, new AttributeClass[]{testTable1_Ac1,testTable1_Ac2}, null);
		
				
		AttributeClass testTable2_Ac1         = new AttributeClass(TESTTABLE2_COLUMN1, Integer.class);
		testTable2_Ac1.setAutoIncrement(true);
		testTable2_Ac1.setPrimaryKey(true);
		AttributeClass testTable2_Ac2         = new AttributeClass(TESTTABLE2_COLUMN2, String.class);
		EntityClass testTable2                = new EntityClass(TESTTABLE2, new AttributeClass[]{testTable2_Ac1,testTable2_Ac2}, null);
		
		List<ClassToTables> classToTablesList = new LinkedList<ClassToTables>();
		classToTablesList.add(testTable1);
		classToTablesList.add(testTable2);
		iecb.deployDefinitionList(classToTablesList);
	}
	
	private void deployTestTablesForIssue2(){
		AttributeClass testTable1_Ac1         = new AttributeClass(TESTTABLE1_COLUMN1, Integer.class);
		testTable1_Ac1.setPrimaryKey(true);
		testTable1_Ac1.setAutoIncrement(true);
		EntityClass testTable1                = new EntityClass(TESTTABLE1, new AttributeClass[]{testTable1_Ac1}, null);
		
		List<ClassToTables> classToTablesList = new LinkedList<ClassToTables>();
		classToTablesList.add(testTable1);
		iecb.deployDefinitionList(classToTablesList);
	}
}

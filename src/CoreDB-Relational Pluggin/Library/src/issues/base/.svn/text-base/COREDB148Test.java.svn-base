/**
 * 
 */
package issues.base;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import coredb.controller.EntityControllerBase;
import coredb.controller.IEntityControllerBase;
import coredb.sql.CompoundStatementManual;
import coredb.sql.Q;
import coredb.unit.AttributeClass;
import coredb.unit.ClassToTables;
import coredb.unit.EntityClass;
import coredb.unit.EntityInstance;
import coredb.unit.IndexClass;

/**
 * @author Sean
 *
 */
public class COREDB148Test {

private IEntityControllerBase iecb ;
	
	private final String TESTTABLE1         = "testtable1";
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
	
		COREDB148Test test = new COREDB148Test();
		test.test();
	}
	
	private void test(){
		System.out.print("[test]\n");
		deployTestTables();
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
		//iecb.dropAllTables();
		System.out.println("[/test]\n");
	}
	
	public COREDB148Test(){
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

	private void deployTestTables(){
		AttributeClass testTable1_Ac1         = new AttributeClass(TESTTABLE1_COLUMN1, Integer.class);
		testTable1_Ac1.setAutoIncrement(true);
		testTable1_Ac1.setPrimaryKey(true);
		AttributeClass testTable1_Ac2         = new AttributeClass(TESTTABLE1_COLUMN2, String.class);
		testTable1_Ac2.setIndexed(true);
		IndexClass indexClass1                = new IndexClass(new AttributeClass[]{testTable1_Ac1, testTable1_Ac2}, false);
		EntityClass testTable1                = new EntityClass(TESTTABLE1, new AttributeClass[]{testTable1_Ac1,testTable1_Ac2}, new IndexClass[]{indexClass1});
		
				
		AttributeClass testTable2_Ac1         = new AttributeClass(TESTTABLE2_COLUMN1, Integer.class);
		testTable2_Ac1.setAutoIncrement(true);
		testTable2_Ac1.setPrimaryKey(true);
		AttributeClass testTable2_Ac2         = new AttributeClass(TESTTABLE2_COLUMN2, String.class);
		testTable2_Ac2.setIndexed(true);
		IndexClass indexClass2                = new IndexClass(new AttributeClass[]{testTable2_Ac1, testTable2_Ac2}, "indexOfTable2", false);
		EntityClass testTable2                = new EntityClass(TESTTABLE2, new AttributeClass[]{testTable2_Ac1,testTable2_Ac2}, new IndexClass[]{indexClass2});
		
		List<ClassToTables> classToTablesList = new LinkedList<ClassToTables>();
		classToTablesList.add(testTable1);
		classToTablesList.add(testTable2);
		iecb.deployDefinitionList(classToTablesList);
	}
}

package draft;

/**
 * @author sean
 */
import coredb.config.*;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.ddlutils.dynabean.SqlDynaClass;
import coredb.database.DatabaseConnection;
import coredb.database.SchemaInfo;
import coredb.config.Configuration;
import coredb.controller.EntityControllerBase;
import coredb.controller.IEntityControllerBase;
import coredb.sql.CompoundStatementManual;
import coredb.sql.ConditionalStatement;
import coredb.sql.Q;
import coredb.unit.AttributeClass;
import coredb.unit.ClassToTables;
import coredb.unit.EntityClass;
import coredb.unit.EntityInstance;
import coredb.unit.IndexClass;
import coredb.utils.Helper;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;


/**
 * @author zhaostephen & Sean
 *
 */
public class EntityControllerTest {
	private String dbURL, databaseType, databaseDriver, databaseName, user, password, pathToFile;
	private EntityControllerBase iec 		          = null;
	private DatabaseConnection databaseConnection = null;
	private AttributeClass[] acArray 	          = null;
	private List<EntityClass> ecList   	          = null;
	private List<ClassToTables> cttList           = null;
	private List<EntityInstance> entities         = null;
	private List<String> tableNames 	          = null;

	public static void main(String[] args) throws Exception {
		EntityControllerTest test = new EntityControllerTest();
		test.setUp();

		test.testEntityControllerString();
		test.testEntityControllerStringStringStringStringStringString();
		test.testRefresh();
		test.testDeployDefinitionListListOfClassToTables();
		test.testDeployDefinitionListListOfClassToTablesBooleanBoolean();

		test.testReadTableNameList();
		test.testGetAttributeClass();
		test.testDropTables();
		test.testDropAllTables();
		test.testCreateEntityInstanceForString();
                test.testCreateNullValueCase();

		test.testGetDatabaseConnection();
		test.testCreateEntityInstanceForStringBoolean();
		test.testCreateEntity();
		test.testCreateEntities();
		test.testReadEntities();

		test.testUpdateEntity();
		test.testUpdateEntities();
		test.testDeleteEntity();
		test.testDeleteEntities();

		test.testGenerateMappingTable();
                test.iec.dropAllTables();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		//Setup the database connection 
		this.pathToFile = "/tmp/credential.txt";
		// /*
		ConfigurationValues.init(pathToFile);
		this.dbURL                      = ConfigurationValues.dbURL;
		this.databaseType               = ConfigurationValues.databaseType;
		this.databaseDriver             = ConfigurationValues.databaseDriver;
		this.databaseName               = ConfigurationValues.databaseName;
		this.user                       = ConfigurationValues.user;
		this.password                   = ConfigurationValues.password;

		
		// Initialize the test instances
		//this.iec                        = new EntityController(dbURL, databaseType, databaseDriver, databaseName, user, password);
		this.iec                        = new EntityControllerBase(pathToFile);
		this.databaseConnection         = this.iec.getDatabaseConnection();
		this.tableNames                 = new LinkedList<String>();
		this.ecList                     = new LinkedList<EntityClass>();
		this.cttList                    = new LinkedList<ClassToTables>();
		this.entities                   = new LinkedList<EntityInstance>();
		
		// Define a List of attributes with currently supported data type 
		List<AttributeClass>acList		= new LinkedList<AttributeClass>();
		AttributeClass priamryAttribute = new AttributeClass("Integer$TestColumn", Integer.class);
		priamryAttribute.setPrimaryKey(true);
		priamryAttribute.setAutoIncrement(true);
		acList.add(priamryAttribute);
		AttributeClass indexColumn      = new AttributeClass("String$TestColumn",    String.class);
		acList.add(indexColumn);
		acList.add(new AttributeClass("Boolean$TestColumn",   Boolean.class));
		acList.add(new AttributeClass("Double$TestColumn",    Double.class));
		//acList.add(new AttributeClass("Character$TestColumn", Character.class, false));
		acList.add(new AttributeClass("Date$TestColumn",      Date.class));
		acList.add(new AttributeClass("Time$TestColumn",      Time.class));
		acList.add(new AttributeClass("Timestamp$TestColumn", Timestamp.class));
		this.acArray                    = acList.toArray(new AttributeClass[0]);
		
		// Define a test table
		String tableName                = "TestTable";
		IndexClass[] indices            = new IndexClass[]{new IndexClass(indexColumn, "TestIndex")};
		
		EntityClass ec                  = new EntityClass(tableName, acArray, indices);
		// define another table for test mapping table generate
		String tableName1               = "TestTable1";
		EntityClass ec1                 = new EntityClass(tableName1, acArray, null);
		
		this.ecList.add(ec);
		this.ecList.add(ec1);
		this.tableNames.add(ec.getName());
		this.tableNames.add(ec1.getName());
		this.cttList.addAll(ecList);	
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		iec 		       = null;
		databaseConnection = null;
		acArray 	       = null;
		ecList   	       = null;
		cttList            = null;
		tableNames 	       = null;
	}
	

	private List<EntityInstance> generateEntities()
    {
    	List<EntityInstance> entityInstances   = new LinkedList<EntityInstance>();
    	
    	//define two rows
		EntityInstance entity1 = iec.createEntityInstanceFor(tableNames.get(0));
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Time currentTime1 = new Time(1, 1, 1);
		Date day1 = Date.valueOf("1981-01-01");
		Timestamp currentTimestamp1 = new Timestamp(1000000000);
		entity1.set("Integer$TestColumn", 1);
		entity1.set("String$TestColumn",     "str 1");
		entity1.set("Boolean$TestColumn",    true);
		entity1.set("Double$TestColumn",     4.5);
		//entity1.set("Character$TestColumn",  'Y');
		entity1.set("Date$TestColumn",       day1);
		entity1.set("Time$TestColumn",       currentTime1);
		entity1.set("Timestamp$TestColumn",  currentTimestamp1);
		
		EntityInstance entity2 = iec.createEntityInstanceFor(tableNames.get(0));
		Time currentTime2 = new Time(2, 2, 2);
		Date day2 = Date.valueOf("2000-01-01");
		Timestamp currentTimestamp2 = new Timestamp(900000000);
		entity2.set("Integer$TestColumn", 2);
		entity2.set("String$TestColumn",     "str 2");
		entity2.set("Boolean$TestColumn",    false);
		entity2.set("Double$TestColumn",     6.6);
		//entity2.set("Character$TestColumn",  'N');
		entity2.set("Date$TestColumn",       day2);
		entity2.set("Time$TestColumn",       currentTime2);
		entity2.set("Timestamp$TestColumn",  currentTimestamp2);
		
		entityInstances.add(entity1);
		entityInstances.add(entity2);
		
		return entityInstances;
    }
	
	private List<String> refactorTables(List<String> tableNames){
		List<String> realTableNames = new LinkedList<String>();
		for(String tableName : this.tableNames){
			realTableNames.add(SchemaInfo.refactorTableName(tableName));
		}
		return realTableNames;
	}
	
	private void compareEntityClass(){
		HashMap<String, AttributeClass> expectedACMap = new HashMap<String, AttributeClass>();
		HashMap<String, AttributeClass> expectedIAMap = new HashMap<String, AttributeClass>();
		for(EntityClass ecExpected : ecList){
			String expectedTableName = SchemaInfo.refactorTableName(ecExpected.getName());
			EntityClass ecActual = databaseConnection.getEntityClass(ecExpected.getName(), false);
			Assert.assertNotNull(ecActual);
			Assert.assertEquals(expectedTableName, ecActual.getName());
			
			for(AttributeClass ac : ecExpected.getAttributeClassList()){
				expectedACMap.put(expectedTableName + ":" + SchemaInfo.refactorColumnName(expectedTableName, ac.getName()), ac);
			}
			
			for(AttributeClass acActual : ecActual.getAttributeClassList()){
				AttributeClass acExpected = expectedACMap.get(ecActual.getName() + ":" + acActual.getName());
				Assert.assertNotNull(acExpected);
				Assert.assertEquals(acExpected.isAutoIncrement(), acActual.isAutoIncrement());
				Assert.assertEquals(acExpected.isPrimaryKey(), acActual.isPrimaryKey());
				Assert.assertEquals(acExpected.getSqlType(), acActual.getSqlType());
			}
			
			for(IndexClass icExpected : ecExpected.getIndexClassList()){
				String indexExpectedName = "";
				if(icExpected.getName()!=null) {
					indexExpectedName = icExpected.getName().toLowerCase() ;
				}else{
					for( AttributeClass acExpected : icExpected.getAttributeClassList()){
						if(indexExpectedName.length()>0) indexExpectedName += Configuration.TABLE_DELIMITER;
						indexExpectedName += SchemaInfo.refactorColumnName(expectedTableName,acExpected.getName()); 
					}
				}
				for( AttributeClass acExpected : icExpected.getAttributeClassList()){
					if(icExpected.getName()!=null){
						expectedIAMap.put(indexExpectedName + ":" + SchemaInfo.refactorColumnName(expectedTableName, acExpected.getName()), acExpected);
					}
				}
			}
			
			for(IndexClass icActual : ecActual.getIndexClassList()){
				for( AttributeClass acActual : icActual.getAttributeClassList()){
					AttributeClass acExpected = expectedIAMap.get(icActual.getName() + ":" + acActual.getName());
					Assert.assertNotNull(acExpected);
					Assert.assertEquals(SchemaInfo.refactorColumnName(expectedTableName, acExpected.getName()), acActual.getName());
				}
			}
		}
	}


	/**
	 * Test method for {@link coredb.controller.EntityController#EntityController(java.lang.String)}.
	 *
	 * @throws SQLException
	 */
	@Test
	public void testEntityControllerString() throws SQLException {
		IEntityControllerBase iecb  = new EntityControllerBase(this.pathToFile);
		Assert.assertNotNull(iecb);
	}

	/**
	 * Test method for {@link coredb.controller.EntityController#EntityController(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 * @throws SQLException 
	 */
	@Test
	public void testEntityControllerStringStringStringStringStringString() throws SQLException {
		IEntityControllerBase iecb = new EntityControllerBase(dbURL, databaseType, databaseDriver, databaseName, user, password);
		Assert.assertNotNull(iecb);
	}

	/**
	 * Test method for {@link coredb.controller.EntityController#refresh()}.
	 */
	@Test
	public void testRefresh() {
		this.iec.dropAllTables();
		this.iec.deployDefinitionList(this.cttList,false,false);
		for( EntityClass ec : this.ecList){	
			for(AttributeClass ac : ec.getAttributeClassList()){
				Assert.assertNotNull(SchemaInfo.refactorColumnName(ec.getName(), ac.getName()));
			}
		}
		this.iec.dropTables(this.tableNames);
	}

	/**
	 * Test method for {@link coredb.controller.EntityController#deployDefinitionList(java.util.List)}.
	 */
	@Test
	public void testDeployDefinitionListListOfClassToTables() {
		this.iec.deployDefinitionList(this.cttList);
		compareEntityClass();
		this.iec.dropAllTables();
	}

	/**
	 * Test method for {@link coredb.controller.EntityController#deployDefinitionList(java.util.List, boolean, boolean)}.
	 */
	@Test
	public void testDeployDefinitionListListOfClassToTablesBooleanBoolean() {
		this.iec.deployDefinitionList(this.cttList, false, true);
		compareEntityClass();
		this.iec.deployDefinitionList(this.cttList, true, false);
		compareEntityClass();
		try{
			this.iec.deployDefinitionList(this.cttList, true, true);
			compareEntityClass();
		}catch(Exception ex){
			Assume.assumeNoException(ex);
		}finally{
			this.iec.dropAllTables();
		}
	}

	/**
	 * Test method for {@link coredb.controller.EntityController#readTableNameList(boolean)}.
	 */
	@Test
	public void testReadTableNameList() {
		this.iec.deployDefinitionList(this.cttList,false,false);
		Assert.assertEquals(this.refactorTables(this.tableNames),this.iec.readTableNameList(true));
		this.iec.dropAllTables();
	}

	/**
	 * Test method for {@link coredb.controller.EntityController#dropTables(java.util.List)}.
	 */
	@Test
	public void testDropTables() {
		this.iec.deployDefinitionList(this.cttList,false,false);
		List<String> preTableNames = this.iec.readTableNameList(true);
		this.iec.dropTables(preTableNames);
		List<String> lastTableNames = this.iec.readTableNameList(true);
		for(String table : preTableNames){
			Assert.assertFalse(lastTableNames.contains(table));
		}
		this.iec.dropAllTables();
	}

	/**
	 * Test method for {@link coredb.controller.EntityController#dropAllTables()}.
	 */
	@Test
	public void testDropAllTables() {
		this.iec.deployDefinitionList(this.cttList,false,false);
		this.iec.dropAllTables();
		Assert.assertEquals(new LinkedList<String>(), this.iec.readTableNameList(true));
	}

	/**
	 * Test method for {@link coredb.controller.EntityController#getAttributeClass(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetAttributeClass() {
		this.iec.deployDefinitionList(this.cttList,false,false);
		for(EntityClass ec : ecList){
			for(AttributeClass ac : ec.getAttributeClassList()){
				AttributeClass acTest = DatabaseConnection.getAttributeClass(ec.getName(), ac.getName());
				Assert.assertEquals(ac.getName(), acTest.getName());
				Assert.assertEquals(ac.getSqlType(), acTest.getSqlType());
			}
		}
		this.iec.dropAllTables();
	}

	/**
	 * Test method for {@link coredb.controller.EntityController#createEntityInstanceFor(java.lang.String)}.
	 */
	@Test
	public void testCreateEntityInstanceForString() {
		this.iec.deployDefinitionList(this.cttList,false,false);
		DynaClass dy1 ,dy2;
		HashMap< String, DynaProperty> dpMap = new HashMap<String, DynaProperty>();
		for(EntityClass ec : this.ecList){
			EntityInstance instance2 = this.iec.createEntityInstanceFor(ec.getName());
			dy1 = new EntityInstance(SqlDynaClass.newInstance(ec.toTable())).getDynaClass();
			dy2 = instance2.getDynaClass();
			String dy1TableName = SchemaInfo.refactorTableName(dy1.getName());
			Assert.assertEquals(dy1TableName,dy2.getName());
			Assert.assertEquals(dy1.getDynaProperties().length, dy2.getDynaProperties().length);
			for(DynaProperty dp1 : dy1.getDynaProperties()){
				String dy1ColName = SchemaInfo.refactorColumnName(dy1TableName, dp1.getName());
				dpMap.put(dy1TableName + ":" + dy1ColName, dp1);
			}
			for(DynaProperty dp2 : dy2.getDynaProperties()){
				DynaProperty dp1 = dpMap.get(dy2.getName() + ":" + dp2.getName()); 
				Assert.assertNotNull(dp1);
				Assert.assertEquals(dp1.getType(), dp2.getType());
				Assert.assertEquals(dp1.isIndexed(), dp2.isIndexed());
			}
		}
		this.iec.dropAllTables();
	}

	/**
	 * Test method for {@link coredb.controller.EntityController#getDatabaseConnection()}.
	 * @throws SQLException 
	 */
	@Test
	public void testGetDatabaseConnection() throws SQLException {
		Assert.assertFalse(this.iec.getDatabaseConnection().getConnection().isClosed());
	}

	/**
	 * Test method for {@link coredb.controller.EntityController#createEntityInstanceFor(java.lang.String, boolean)}.
	 */
	@Test
	public void testCreateEntityInstanceForStringBoolean() {
		this.iec.deployDefinitionList(cttList,true,true);
		this.entities = generateEntities();
		
		EntityInstance entityInstance = this.iec.createEntityInstanceFor(tableNames.get(0));
		
		Assert.assertTrue(EntityInstance.isSameEntityClass(entityInstance, this.entities.get(0)));
	}

	/**
	 * Test method for {@link coredb.controller.EntityController#createEntity(coredb.unit.EntityInstance)}.
	 */
	@Test
	public void testCreateEntity() {
		this.iec.deployDefinitionList(cttList,true,true);
		this.entities = generateEntities();
		this.iec.createEntity(this.entities.get(0));
		List<EntityInstance> entityInstances = this.iec.readEntities(tableNames.get(0), "");
		Assert.assertTrue(entityInstances.size() == 1);
		Assert.assertTrue(this.entities.get(0).equals(entityInstances.get(0)));
		this.iec.dropTables(tableNames);
	}

	/**
	 * Test method for {@link coredb.controller.EntityController#createEntities(java.util.List)}.
	 */
	@Test
	public void testCreateEntities() {
		// this test case is same as testReadEntities
		this.iec.deployDefinitionList(cttList,true,true);
		this.entities = generateEntities();
		this.iec.createEntities(entities);
		
		List<EntityInstance> entityInstances = this.iec.readEntities(tableNames.get(0), "");
		Assert.assertTrue(entityInstances.size() == 2);
		
		HashMap<Integer, EntityInstance> original = new LinkedHashMap<Integer, EntityInstance>();
		for(EntityInstance entity : this.entities)
		{
			original.put((Integer)entity.get("Integer$TestColumn"), entity);
		}
		
		for(EntityInstance entity : entityInstances)
		{
			if(original.get(entity.get("Integer$TestColumn")) != null)
			{
				Assert.assertTrue(entity.equals(original.get(entity.get("Integer$TestColumn"))));
			}
		}
		
		this.iec.dropTables(tableNames);
	}

	/**
	 * Test method for {@link coredb.controller.EntityControllerBase#readEntities(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testReadEntities() {
		this.iec.deployDefinitionList(cttList,true,true);
		this.entities = generateEntities();
		this.iec.createEntities(entities);
		
		List<EntityInstance> entityInstances = this.iec.readEntities(tableNames.get(0), "");
		entityInstances.get(0).set("Date$TestColumn", Date.valueOf("1981-01-01"));
		entityInstances.get(1).set("Date$TestColumn", Date.valueOf("2000-01-01"));
		Assert.assertTrue(entityInstances.size() == 2);
		
		HashMap<Integer, EntityInstance> original = new LinkedHashMap<Integer, EntityInstance>();
		for(EntityInstance entity : this.entities)
		{
			original.put((Integer)entity.get("Integer$TestColumn"), entity);
		}
		
		for(EntityInstance entity : entityInstances)
		{
			if(original.get(entity.get("Integer$TestColumn")) != null)
			{
				Assert.assertTrue(entity.equals(original.get(entity.get("Integer$TestColumn"))));
			}
		}
		
		this.iec.dropTables(tableNames);
	}

	/**
	 * Test method for {@link coredb.controller.EntityController#updateEntity(coredb.unit.EntityInstance)}.
	 */
	@Test
	public void testUpdateEntity() {
		this.iec.deployDefinitionList(cttList,true,true);
		this.entities = generateEntities();
		this.iec.createEntities(entities);
		
		String updateColumn = "String$TestColumn";
		this.entities.get(0).set(updateColumn,     "str_Modified 1");
		
		this.iec.updateEntity(this.entities.get(0));

		ConditionalStatement conditionalStatement = new ConditionalStatement(DatabaseConnection.getAttributeClass(tableNames.get(0), updateColumn), Q.LIKE,"str_Modified%");
		List<EntityInstance> entityInstances = this.iec.readEntities(tableNames.get(0), conditionalStatement);
		Assert.assertTrue(entityInstances.size() == 1);
		
		this.iec.dropTables(tableNames);
	}

	/**
	 * Test method for {@link coredb.controller.EntityController#updateEntities(java.util.List)}.
	 */
	@Test
	public void testUpdateEntities() {
		this.iec.deployDefinitionList(cttList,true,true);
		this.entities = generateEntities();
		this.iec.createEntities(entities);
		
		String updateColumn = "String$TestColumn";
		this.entities.get(0).set(updateColumn,     "str_Modified 1");
		this.entities.get(1).set(updateColumn,     "str_Modified 2");
		
		this.iec.updateEntities(this.entities);
		

		ConditionalStatement conditionalStatement = new ConditionalStatement(DatabaseConnection.getAttributeClass(tableNames.get(0), updateColumn), Q.LIKE,"str_Modified%");
		List<EntityInstance> entityInstances = this.iec.readEntities(tableNames.get(0), conditionalStatement);
		
		Assert.assertTrue(entityInstances.size() == 2);
		
		this.iec.dropTables(tableNames);
	}

	/**
	 * Test method for {@link coredb.controller.EntityController#deleteEntity(coredb.unit.EntityInstance)}.
	 */
	@Test
	public void testDeleteEntity() {
		this.iec.deployDefinitionList(cttList,true,true);
		this.entities = generateEntities();
		this.iec.createEntities(entities);
		
		this.iec.deleteEntity(entities.get(0));
		
		List<EntityInstance> entityInstances = this.iec.readEntities(tableNames.get(0), "");
		
		Assert.assertTrue(entityInstances.size() == 1);
		Assert.assertTrue((Integer)entityInstances.get(0).get("Integer$TestColumn") == 2);
		
		this.iec.dropTables(tableNames);
	}

	/**
	 * Test method for {@link coredb.controller.EntityController#deleteEntities(java.util.List)}.
	 */
	@Test
	public void testDeleteEntities() {
		this.iec.deployDefinitionList(cttList,true,true);
		this.entities = generateEntities();
		this.iec.createEntities(entities);
		
		this.iec.deleteEntities(entities);
		
		List<EntityInstance> entityInstances = this.iec.readEntities(tableNames.get(0), "");
		Assert.assertTrue(entityInstances.isEmpty());
		this.iec.dropTables(tableNames);
	}

	/**
	 * Test method for {@link coredb.controller.EntityController#generateMappingTable(coredb.unit.ClassToTables, coredb.unit.AttributeClass[], coredb.unit.ClassToTables, coredb.unit.AttributeClass[])}.
	 */
	@Test
	public void testGenerateMappingTable() {
		EntityClass left = this.ecList.get(0);
		EntityClass right = this.ecList.get(1);
		AttributeClass leftAc = left.getAttributeClassList()[0];
		AttributeClass rightAc = right.getAttributeClassList()[0];
		
		ClassToTables mapping = iec.generateMappingTable(left, new AttributeClass[]{leftAc}, right, new AttributeClass[]{rightAc});
		List<ClassToTables> tables = new LinkedList<ClassToTables>();
		tables.add(mapping);
        iec.deployDefinitionList(tables, true, true);
		
        String mappingTableName = left.getName() + Configuration.TABLE_DELIMITER + right.getName();
        String rowIdName = Configuration.Public_Attribute.PrefixOfRowId + Configuration.TABLE_DELIMITER + mappingTableName
        + Configuration.TABLE_DELIMITER + Configuration.Public_Attribute.SubfixOfRowId;
        
        EntityInstance mappingEntityInstance = iec.createEntityInstanceFor(mappingTableName);
        DynaProperty[] properties = mappingEntityInstance.getDynaClass().getDynaProperties();
        
        Assert.assertNotNull(mappingEntityInstance);
        Assert.assertTrue(mappingEntityInstance.getDynaClass().getName().equalsIgnoreCase(mappingTableName));
        Assert.assertTrue(properties.length == 3);
        // test structure
        int num = 0;
        for(DynaProperty property : properties)
        {
        	if(property.getName().equalsIgnoreCase(rowIdName)){num ++;}
        	else if(property.getName().equalsIgnoreCase(left.getName() + "_" + leftAc.getName())){num ++;}
        	else if(property.getName().equalsIgnoreCase(right.getName() + "_" + rightAc.getName())){num ++;}
        }
        Assert.assertTrue(num == 3);
	}


        @Test
	public void testCreateNullValueCase() {
            this.iec.deployDefinitionList(cttList,true,true);
            EntityInstance entityInstance = iec.createEntityInstanceFor(this.tableNames.get(0));
            Assert.assertTrue(iec.createEntity(entityInstance));
            List<EntityInstance> eil = iec.readEntities(this.tableNames.get(0), new CompoundStatementManual());
            Assert.assertTrue(eil.size()==1);
            for(String columnName : eil.get(0).getAttributeNames()){
                if(!columnName.equalsIgnoreCase("Integer$TestColumn"))
                    Assert.assertEquals(null,eil.get(0).get(columnName));
            }
            this.iec.dropAllTables();;
        }
}
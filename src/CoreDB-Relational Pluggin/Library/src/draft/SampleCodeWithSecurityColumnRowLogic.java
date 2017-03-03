package draft;

import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import coredb.config.Configuration.Column_Info_Table;
import coredb.config.Configuration.Mapping_Table;
import coredb.config.Configuration.Row_Security_Deny_Table;
import coredb.config.Configuration.Row_Security_Grant_Table;
import coredb.config.Configuration.Token_Info_Table;
import coredb.config.Configuration.User_Info_Table;
import coredb.controller.EntityControllerSecurity;
import coredb.database.SchemaInfo;
import coredb.mode.Mode;
import coredb.security.ResultSecurity;
import coredb.security.ResultTokens;
import coredb.security.TokenFactory;
import coredb.sql.CompoundStatementAuto;
import coredb.sql.CompoundStatementManual;
import coredb.sql.ConditionalStatement;
import coredb.sql.Q;
import coredb.unit.AttributeClass;
import coredb.unit.CRUD;
import coredb.unit.ClassToTables;
import coredb.unit.EntityClass;
import coredb.unit.EntityInstance;
import coredb.unit.EntitySecurityClass;
import coredb.utils.Describer;

/**
 * @author sean
 *
 */
public class SampleCodeWithSecurityColumnRowLogic {
    EntityControllerSecurity iecs = null;
	
	private static final String TESTTABLE_TABLENAME       = "STAFF";
	private static final String TESTTABLE_COLUMN_STAFFID  = "STAFFID";
	private static final String TESTTABLE_COLUMN_NAME     = "NAME";
	private static final String TESTTABLE_COLUMN_GENDER   = "GENDER";
	private static final String TESTTABLE_COLUMN_DOB      = "DOB";
	private static final String TESTTABLE_COLUMN_CV       = "CV";
	private static final String TESTTABLE_COLUMN_SALARY   = "SALARY";
	private static final String TESTTABLE_COLUMN_ISAUSSIE = "ISAUSSIE";
	
	@SuppressWarnings("unchecked")
	private static HashMap<String, Class> testTable_dataType = new HashMap<String, Class>();
	
	private static final String ROOTNAME                 = "ROOT";
	private static final String ROOTPASS                 = "ROOT";
	private static final String ADMINGROUP               = "ADMIN";
	private static final String DEFAULTGROUP             = "DEFAULT";
	private static final String GENERALGROUP             = "GENERAL";
	private static final String ACADEMICGROUP            = "ACADEMIC";
	
	private static AttributeClass ac_tokenGroup          = null;
	private static AttributeClass ac_tokenType           = null;
	private static AttributeClass ac_tokenPermission     = null;
	
	private static AttributeClass ac_staffid             = null;
	private static AttributeClass ac_name                = null;
	private static AttributeClass ac_gender              = null;
	private static AttributeClass ac_dob                 = null;
	private static AttributeClass ac_cv                  = null;
	private static AttributeClass ac_salary              = null;
	private static AttributeClass ac_isaussie            = null;
	
	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		SampleCodeWithSecurityColumnRowLogic test = new SampleCodeWithSecurityColumnRowLogic();
		test.sampleCode(test);
	}
	
	/**
	 * DO CRUD
	 * <br />
	 * 
	 * Four groups : root, admin ,  academic and general (root > admin > default, adademic and general)<br />
	 * Four users  : Pual, Van,     Sean     and Stephen<br />
	 * Table       : staff (staffid - id, name - string, gender - char, dob - date, cv - bytea, salary - double, isAussie - boolean)<br />
	 * Entities    : E1, E2, E3 and E4 are the records of Pual, Sean, Van and Stephen respectively.<br />
	 * Rules       : <br />
	 *             1) Any record can be deployed CURD actions by root users and its author.<br />
	 *             2) Admin users has +R +C +D permission on all columns, +u on name, cv, isAussie and salary.<br />
	 *             3) default, academic and general users have +c+r+d on all columns, +U permission on name, cv and isAussie <br />
	 *              
	 * pictures: 
	 *  <br />
	 *  The user Root CRUD permission on TEST table
	 *  <br />
	 * <img src="http://vmc-desktop.cse.unsw.edu.au:8080/coredb/secure/attachment/10048/root+CRUD+on+testTable.jpg"/> <br />
	 *  The user Van CRUD permission on TEST table
	 *  <br />
	 * <img src="http://vmc-desktop.cse.unsw.edu.au:8080/coredb/secure/attachment/10051/VAN+CRUD+on+testTable.jpg"/> <br />
	 *  The user Sean CRUD permission on TEST table
	 *  <br />
	 * <img src="http://vmc-desktop.cse.unsw.edu.au:8080/coredb/secure/attachment/10049/Sean+CRUD+on+testTable.jpg"/> <br />
	 *  The user Stepehn CRUD permission on TEST table
	 *  <br />
	 * <img src="http://vmc-desktop.cse.unsw.edu.au:8080/coredb/secure/attachment/10050/stephen+CRUD+on+testTable.jpg"/> <br />
	 */
	@SuppressWarnings("deprecation")
	public void sampleCode(SampleCodeWithSecurityColumnRowLogic test) throws SQLException {
		this.begin();
		
		/**
		 * create users
		 */
		printTag("--------------create users(Van, Sean and Stephen)---------------");
		EntityInstance root                      = test.getRootUser();
		HashMap<String, EntityInstance> userMap  = test.generateNormalUser(new String[][]{ {"Van","Van", "VanPass"} });
		EntityInstance van                       = userMap.get("Van");	
		userMap                                  = test.generateNormalUser(new String[][]{ {"Sean","Sean", "SeanPass"} });
		EntityInstance Sean                      = userMap.get("Sean");	
		userMap                                  = test.generateNormalUser(new String[][]{ {"Stephen","Stephen", "StephenPass"} });
		EntityInstance Stephen                   = userMap.get("Stephen");	
		
		printTag("--------------read the created users back---------------");
		List<EntityInstance> actualUsers         = iecs.readEntitiesWithSecurity(root, User_Info_Table.makeUserInfoTableName(), new CompoundStatementAuto(Q.AND)).getPassedEntities();
		Describer.describeEntityInstances(actualUsers);
		
		/**
		 * configure group
		 */
		printTag("--------------create groups---------------");
		generateGroupTokens(ADMINGROUP,    root);
		generateGroupTokens(DEFAULTGROUP,  root);
		generateGroupTokens(GENERALGROUP,  root);
		generateGroupTokens(ACADEMICGROUP, root);
		
		printTag("--------------read tokens of groups---------------");
		ResultTokens defaultTokens   = test.iecs.getTokensByGroup(root, DEFAULTGROUP);
		ResultTokens adminTokens     = test.iecs.getTokensByGroup(root, ADMINGROUP);
		ResultTokens generalTokens   = test.iecs.getTokensByGroup(root, GENERALGROUP);
		ResultTokens academicTokens  = test.iecs.getTokensByGroup(root, ACADEMICGROUP);
		
		printTag("--------------tokens of default group---------------");
		Describer.describeEntityInstances(defaultTokens.getGrantTokens());
		Describer.describeEntityInstances(defaultTokens.getDenyTokens());
		printTag("--------------tokens of admin group---------------");
		Describer.describeEntityInstances(adminTokens.getGrantTokens());
		Describer.describeEntityInstances(adminTokens.getDenyTokens());
		printTag("--------------tokens of general group---------------");
		Describer.describeEntityInstances(generalTokens.getGrantTokens());
		Describer.describeEntityInstances(generalTokens.getDenyTokens());
		printTag("--------------tokens of academic group---------------");
		Describer.describeEntityInstances(academicTokens.getGrantTokens());
		Describer.describeEntityInstances(academicTokens.getDenyTokens());
		
		/**
		 * configure usrs
		 */
		printTag("--------------addUserToGroup and getTokensByUser---------------");
		iecs.addUserToGroup(root, van,     ADMINGROUP);
		iecs.addUserToGroup(root, Sean,    ACADEMICGROUP);
		iecs.addUserToGroup(root, Stephen, GENERALGROUP);
		
		printTag("--------------tokens of pual(root)---------------");
		ResultTokens tokens = iecs.getTokensByUser(root, root);
		Describer.describeEntityInstances(tokens.getGrantTokens());
		Describer.describeEntityInstances(tokens.getDenyTokens());
		
		printTag("--------------tokens of Van---------------");
		tokens = iecs.getTokensByUser(root, van);
		Describer.describeEntityInstances(tokens.getGrantTokens());
		Describer.describeEntityInstances(tokens.getDenyTokens());
		
		printTag("--------------tokens of sean---------------");
		tokens = iecs.getTokensByUser(root, Sean);
		Describer.describeEntityInstances(tokens.getGrantTokens());
		Describer.describeEntityInstances(tokens.getDenyTokens());
		
		printTag("--------------tokens of stepehn---------------");
		tokens = iecs.getTokensByUser(root, Stephen);
		Describer.describeEntityInstances(tokens.getGrantTokens());
		Describer.describeEntityInstances(tokens.getDenyTokens());
		
		/**
		 * configure column security
		 */
		printTag("--------------removeTokensFromColumns(remove default grant tokens from non-pk columns(except staffid))---------------");
		AttributeClass[] nonPkColumns = new AttributeClass[]{ac_cv, ac_dob, ac_gender, ac_isaussie, ac_name, ac_salary};
		iecs.removeTokensFromColumns(root, TESTTABLE_TABLENAME.toLowerCase(), nonPkColumns, defaultTokens.getGrantTokens());

		printTag("--------------getTokensByColumn (column : name)---------------");
		tokens = iecs.getTokensByColumn(root, TESTTABLE_TABLENAME, ac_name);
		Describer.describeEntityInstances(tokens.getGrantTokens());
		Describer.describeEntityInstances(tokens.getDenyTokens());
		
		printTag("--------------getTokensByColumn (column : staffid)---------------");
		tokens = iecs.getTokensByColumn(root, TESTTABLE_TABLENAME, ac_staffid);
		Describer.describeEntityInstances(tokens.getGrantTokens());
		Describer.describeEntityInstances(tokens.getDenyTokens());
		
		// configure column security for admin user
		List<EntityInstance> adminGrantTokenC = getTokens(ADMINGROUP, CRUD.CREATE, true);
		List<EntityInstance> adminGrantTokenR = getTokens(ADMINGROUP, CRUD.READ,   true);
		List<EntityInstance> adminGrantTokenU = getTokens(ADMINGROUP, CRUD.UPDATE, true);
		List<EntityInstance> adminGrantTokenD = getTokens(ADMINGROUP, CRUD.DELETE, true);
		
		printTag("--------------addTokensToColumns(add admin grant CRUD tokens to non-pk columns(except staffid))---------------");
		iecs.addTokensToColumns(root, TESTTABLE_TABLENAME.toLowerCase(), nonPkColumns, adminGrantTokenC);
		iecs.addTokensToColumns(root, TESTTABLE_TABLENAME.toLowerCase(), nonPkColumns, adminGrantTokenR);
		iecs.addTokensToColumns(root, TESTTABLE_TABLENAME.toLowerCase(), nonPkColumns, adminGrantTokenD);
		iecs.addTokensToColumns(root, TESTTABLE_TABLENAME.toLowerCase(), new AttributeClass[]{ac_name, ac_cv, ac_isaussie, ac_salary}, adminGrantTokenU);
		
		printTag("--------------getTokensByColumn (column : name)---------------");
		tokens = iecs.getTokensByColumn(root, TESTTABLE_TABLENAME, ac_name);
		Describer.describeEntityInstances(tokens.getGrantTokens());
		Describer.describeEntityInstances(tokens.getDenyTokens());
		
		printTag("--------------getTokensByColumn (column : staffid)---------------");
		tokens = iecs.getTokensByColumn(root, TESTTABLE_TABLENAME, ac_staffid);
		Describer.describeEntityInstances(tokens.getGrantTokens());
		Describer.describeEntityInstances(tokens.getDenyTokens());
		
		// configure column security for general user
		List<EntityInstance> generalGrantTokenC = getTokens(GENERALGROUP, CRUD.CREATE, true);
		List<EntityInstance> generalGrantTokenR = getTokens(GENERALGROUP, CRUD.READ,   true);
		List<EntityInstance> generalGrantTokenU = getTokens(GENERALGROUP, CRUD.UPDATE, true);
		List<EntityInstance> generalGrantTokenD = getTokens(GENERALGROUP, CRUD.DELETE, true);
		
		iecs.addTokensToColumns(root, TESTTABLE_TABLENAME.toLowerCase(), nonPkColumns, generalGrantTokenC);
		iecs.addTokensToColumns(root, TESTTABLE_TABLENAME.toLowerCase(), nonPkColumns, generalGrantTokenD);
		iecs.addTokensToColumns(root, TESTTABLE_TABLENAME.toLowerCase(), nonPkColumns, generalGrantTokenR);
		iecs.addTokensToColumns(root, TESTTABLE_TABLENAME.toLowerCase(), new AttributeClass[]{ac_name, ac_cv, ac_isaussie}, generalGrantTokenU);
		
		// configure column security for academic user
		List<EntityInstance> academicGrantTokenC = getTokens(ACADEMICGROUP, CRUD.CREATE, true);
		List<EntityInstance> academicGrantTokenR = getTokens(ACADEMICGROUP, CRUD.READ,   true);
		List<EntityInstance> academicGrantTokenU = getTokens(ACADEMICGROUP, CRUD.UPDATE, true);
		List<EntityInstance> academicGrantTokenD = getTokens(ACADEMICGROUP, CRUD.DELETE, true);
		
		iecs.addTokensToColumns(root, TESTTABLE_TABLENAME.toLowerCase(), nonPkColumns, academicGrantTokenC);
		iecs.addTokensToColumns(root, TESTTABLE_TABLENAME.toLowerCase(), nonPkColumns, academicGrantTokenD);
		iecs.addTokensToColumns(root, TESTTABLE_TABLENAME.toLowerCase(), nonPkColumns, academicGrantTokenR);
		iecs.addTokensToColumns(root, TESTTABLE_TABLENAME.toLowerCase(), new AttributeClass[]{ac_name, ac_cv, ac_isaussie}, academicGrantTokenU);
		

		/**
		 * create Entities : E1 by root, E2 by Van, E3 by Sean and E4 by Stephen
		 */
		printTag("--------------create Entities : E1 by root, E2 by Van, E3 by Sean and E4 by Stephen---------------");
		List<EntityInstance> entities = new LinkedList<EntityInstance>();
		
		EntityInstance E1 = iecs.createEntityInstanceFor(TESTTABLE_TABLENAME);
		E1.set(TESTTABLE_COLUMN_STAFFID,  1);
		E1.set(TESTTABLE_COLUMN_NAME,     "Pual");
		E1.set(TESTTABLE_COLUMN_GENDER,   'M');
		E1.set(TESTTABLE_COLUMN_DOB,      new Date(1950, 10, 10));
		E1.set(TESTTABLE_COLUMN_CV,       "Pual is the head of CSE".getBytes());
		E1.set(TESTTABLE_COLUMN_SALARY,   100000.00);
		E1.set(TESTTABLE_COLUMN_ISAUSSIE, true);
		EntityInstance E1COPY = E1.clone(); // just tell users how to keep a copy in memory
		iecs.createEntityWithSecurity(root, E1);
		Describer.describeEntityInstance(E1COPY);
		
		EntityInstance E2 = iecs.createEntityInstanceFor(TESTTABLE_TABLENAME);
		E2.set(TESTTABLE_COLUMN_STAFFID,  2);
		E2.set(TESTTABLE_COLUMN_NAME,     "Van");
		E2.set(TESTTABLE_COLUMN_GENDER,   'M');
		E2.set(TESTTABLE_COLUMN_DOB,      new Date(1980, 11, 12));
		E2.set(TESTTABLE_COLUMN_CV,       "Van is a senior researcher of CSE".getBytes());
		E2.set(TESTTABLE_COLUMN_SALARY,   75000.00);
		E2.set(TESTTABLE_COLUMN_ISAUSSIE, true);
		EntityInstance E2COPY = E2.clone();
		iecs.createEntityWithSecurity(van, E2);
		Describer.describeEntityInstance(E2COPY);
		
		EntityInstance E3 = iecs.createEntityInstanceFor(TESTTABLE_TABLENAME);
		E3.set(TESTTABLE_COLUMN_STAFFID,  3);
		E3.set(TESTTABLE_COLUMN_NAME,     "Sean");
		E3.set(TESTTABLE_COLUMN_GENDER,   'M');
		E3.set(TESTTABLE_COLUMN_DOB,      new Date(1986, 10, 12));
		E3.set(TESTTABLE_COLUMN_CV,       "Sean is a Research Assistant of CSE".getBytes());
		E3.set(TESTTABLE_COLUMN_SALARY,   50000.00);
		E3.set(TESTTABLE_COLUMN_ISAUSSIE, false);
		EntityInstance E3COPY = E3.clone();
		iecs.createEntityWithSecurity(Sean, E3);
		Describer.describeEntityInstance(E3COPY);
		
		EntityInstance E4 = iecs.createEntityInstanceFor(TESTTABLE_TABLENAME);
		E4.set(TESTTABLE_COLUMN_STAFFID,  4);
		E4.set(TESTTABLE_COLUMN_NAME,     "Stephen");
		E4.set(TESTTABLE_COLUMN_GENDER,   'M');
		E4.set(TESTTABLE_COLUMN_DOB,      new Date(1983, 10, 10));
		E4.set(TESTTABLE_COLUMN_CV,       "Stephen is a Research Assistant of CSE".getBytes());
		E4.set(TESTTABLE_COLUMN_SALARY,   50000.00);
		E4.set(TESTTABLE_COLUMN_ISAUSSIE, false);
		EntityInstance E4COPY = E4.clone();
		iecs.createEntityWithSecurity(Stephen, E4);
		Describer.describeEntityInstance(E4COPY);
		
		/**
		 * configure row security
		 */
		// remove all default grant tokens from all rows
		printTag("--------------remove all default grant tokens from all rows---------------");
		entities.add(E1COPY);
		entities.add(E2COPY);
		entities.add(E3COPY);
		entities.add(E4COPY);
		iecs.removeTokensFromRows(root, entities, defaultTokens.getGrantTokens());
		entities.add(E1COPY);
		entities.add(E2COPY);
		entities.add(E3COPY);
		entities.add(E4COPY);
		
		printTag("--------------getTokensByRow (row : E1)---------------");
		tokens = iecs.getTokensByRow(root, E1);
		Describer.describeEntityInstances(tokens.getGrantTokens());
		
		// add the owner's tokens onto rows
		printTag("--------------add the tokens of owner and the people have higher level than its owner onto rows(E2, E3 and E4)---------------");
		iecs.addTokensToRow(root, E2, adminTokens.getGrantTokens());
		iecs.addTokensToRow(root, E3, adminTokens.getGrantTokens());
		iecs.addTokensToRow(root, E4, adminTokens.getGrantTokens());
		iecs.addTokensToRow(root, E3, academicTokens.getGrantTokens());
		iecs.addTokensToRow(root, E4, generalTokens.getGrantTokens());
		
		printTag("--------------getTokensByRow (row : E2)---------------");
		tokens = iecs.getTokensByRow(root, E2);
		Describer.describeEntityInstances(tokens.getGrantTokens());
		
		printTag("--------------getTokensByRow (row : E3)---------------");
		tokens = iecs.getTokensByRow(root, E3);
		Describer.describeEntityInstances(tokens.getGrantTokens());
		
		printTag("--------------getTokensByRow (row : E4)---------------");
		tokens = iecs.getTokensByRow(root, E4);
		Describer.describeEntityInstances(tokens.getGrantTokens());
		
		
		/**
		 * case 1 :READ
		 */
		printTag("<====== case 1 READ (No Conditional statement) ======>");
		
		printTag("<------ READ by root ------>");
		ResultSecurity result_root = iecs.readEntitiesWithSecurity(root, TESTTABLE_TABLENAME, new CompoundStatementManual());
		Describer.describeEntityInstances(result_root.getPassedEntities());
		
		printTag("<------READ by Van ------>");
		ResultSecurity result_van = iecs.readEntitiesWithSecurity(van, TESTTABLE_TABLENAME, new CompoundStatementManual());
		Describer.describeEntityInstances(result_van.getPassedEntities());
		
		printTag("<------READ by Sean ------>");
		ResultSecurity result_sean = iecs.readEntitiesWithSecurity(Sean, TESTTABLE_TABLENAME, new CompoundStatementManual());
		Describer.describeEntityInstances(result_sean.getPassedEntities());
		
		printTag("<------READ by Stephen ------>");
		ResultSecurity result_stephen = iecs.readEntitiesWithSecurity(Stephen, TESTTABLE_TABLENAME, new CompoundStatementManual());
		Describer.describeEntityInstances(result_stephen.getPassedEntities());
		
		
		/**
		 * case 2 :UPDATE
		 */
		printTag("<====== case 2 :UPDATE ======>");
		
		/**
		 * UPDATE by root
		 */
		
		/**
		 *  Update E1 by root
		 */
		CompoundStatementAuto csa = new CompoundStatementAuto(Q.AND);
		csa.addCompoundStatement(new ConditionalStatement(ac_staffid, Q.E, 1));
		result_root = iecs.readEntitiesWithSecurity(root, TESTTABLE_TABLENAME, csa);
		printTag("<++++++ Before updated(E1) by root ++++++>");
		Describer.describeEntityInstances(result_root.getPassedEntities());
		// do update
		E1 = result_root.getPassedEntities().get(0);
		byte[] newCV = "I am Pual, and I will retired in the end of 2010!".getBytes();
		E1.set(TESTTABLE_COLUMN_CV, newCV);
		printTag("<------ After updated(E1 -- cv) by root ------>");
		result_root = iecs.updateEntityWithSecurity(root, E1);
		result_root = iecs.readEntitiesWithSecurity(root, TESTTABLE_TABLENAME, csa);// read latest records
		Describer.describeEntityInstances(result_root.getPassedEntities());
		
		/**
		 *  Update E2, E3 and E4 by root
		 */
		csa = new CompoundStatementAuto(Q.OR);
		csa.addCompoundStatement(new ConditionalStatement(ac_staffid, Q.E, 2));
		csa.addCompoundStatement(new ConditionalStatement(ac_staffid, Q.E, 3));
		csa.addCompoundStatement(new ConditionalStatement(ac_staffid, Q.E, 4));
		result_root = iecs.readEntitiesWithSecurity(root, TESTTABLE_TABLENAME, csa);
		printTag("<++++++ Before updated(E2, E3 and E4) by root ++++++>");
		Describer.describeEntityInstances(result_root.getPassedEntities());
		
		// do update
		result_root.getPassedEntities().get(0).set(TESTTABLE_COLUMN_NAME, "Super" + (String)result_root.getPassedEntities().get(0).get(TESTTABLE_COLUMN_NAME));
		result_root.getPassedEntities().get(1).set(TESTTABLE_COLUMN_NAME, "Super" + (String)result_root.getPassedEntities().get(1).get(TESTTABLE_COLUMN_NAME));
		result_root.getPassedEntities().get(2).set(TESTTABLE_COLUMN_NAME, "Super" + (String)result_root.getPassedEntities().get(2).get(TESTTABLE_COLUMN_NAME));
		printTag("<------ After updated(E2, E3 and E4 -- name) by root ------>");
		result_root = iecs.updateEntitiesWithSecurity(root, result_root.getPassedEntities());
		result_root = iecs.readEntitiesWithSecurity(root, TESTTABLE_TABLENAME, csa);
		Describer.describeEntityInstances(result_root.getPassedEntities());
		

		/**
		 * UPDATE by van
		 */
		
		/**
		 *  Update E2 by van
		 */
		csa = new CompoundStatementAuto(Q.AND);
		csa.addCompoundStatement(new ConditionalStatement(ac_staffid, Q.E, 2));
		result_van = iecs.readEntitiesWithSecurity(van, TESTTABLE_TABLENAME, csa);
		printTag("<++++++ Before updated(E2) by van ++++++>");
		Describer.describeEntityInstances(result_van.getPassedEntities());
		
		// do update : CV and Salary -- passed
		E2 = result_van.getPassedEntities().get(0);
		newCV = "I am Van, and I will be a lecturer at cse soon!".getBytes();
		E2.set(TESTTABLE_COLUMN_CV, newCV);
		E2.set(TESTTABLE_COLUMN_SALARY, 90000.00);
		E2COPY = E2.clone();
		printTag("<------ After updated(E2 -- CV and Salary) by van ------>");
		result_van = iecs.updateEntityWithSecurity(van, E2);
		result_van = iecs.readEntitiesWithSecurity(van, TESTTABLE_TABLENAME, csa);
		Describer.describeEntityInstances(result_van.getPassedEntities());
		
		// do update : name and DOB -- failed
		E2 = result_van.getPassedEntities().get(0);
		E2.set(TESTTABLE_COLUMN_NAME, "VMC");
		E2.set(TESTTABLE_COLUMN_DOB,  new Date(1981, 12, 12));
		E2COPY = E2.clone();
		printTag("<------ After updated(E2 -- name and DOB) by van ------>");
		result_van = iecs.updateEntityWithSecurity(van, E2);
		result_van = iecs.readEntitiesWithSecurity(van, TESTTABLE_TABLENAME, csa);
		Describer.describeEntityInstances(result_van.getPassedEntities());
		
		/**
		 *  Update E1, E3 and E4 by Van -- two passed and one failed
		 */
		csa = new CompoundStatementAuto(Q.OR);
		csa.addCompoundStatement(new ConditionalStatement(ac_staffid, Q.E, 1));
		csa.addCompoundStatement(new ConditionalStatement(ac_staffid, Q.E, 3));
		csa.addCompoundStatement(new ConditionalStatement(ac_staffid, Q.E, 4));
		result_root = iecs.readEntitiesWithSecurity(root, TESTTABLE_TABLENAME, csa);
		printTag("<++++++ Before updated(E1, E3 and E4 -- salary) by Van ++++++>");
		Describer.describeEntityInstances(result_root.getPassedEntities());
		// do update
		result_root.getPassedEntities().get(0).set(TESTTABLE_COLUMN_SALARY, 120000.00);
		result_root.getPassedEntities().get(1).set(TESTTABLE_COLUMN_SALARY, 120000.00);
		result_root.getPassedEntities().get(2).set(TESTTABLE_COLUMN_SALARY, 120000.00);
		result_van = iecs.updateEntitiesWithSecurity(van, result_root.getPassedEntities());
		printTag("<------ After updated(E1, E3 and E4 -- salary) by Van ------>");
		result_root = iecs.readEntitiesWithSecurity(root, TESTTABLE_TABLENAME, csa);
		Describer.describeEntityInstances(result_root.getPassedEntities());
		
		
		/**
		 * UPDATE by Sean
		 */
		
		/**
		 *  Update E3 by Sean
		 */
		csa = new CompoundStatementAuto(Q.AND);
		csa.addCompoundStatement(new ConditionalStatement(ac_staffid, Q.E, 3));
		result_sean = iecs.readEntitiesWithSecurity(Sean, TESTTABLE_TABLENAME, csa);
		printTag("<++++++ Before updated(E3) by Sean ++++++>");
		Describer.describeEntityInstances(result_sean.getPassedEntities());
		
		// do update : CV and isAussie -- passed
		E3 = result_sean.getPassedEntities().get(0);
		newCV = "I am Sean, and I am working for CSE!".getBytes();
		E3.set(TESTTABLE_COLUMN_CV, newCV);
		E3.set(TESTTABLE_COLUMN_ISAUSSIE, true);
		E3COPY = E3.clone();
		printTag("<------ After updated(E3 -- CV and isAussie) by Sean ------>");
		result_sean = iecs.updateEntityWithSecurity(Sean, E3);
		result_sean = iecs.readEntitiesWithSecurity(Sean, TESTTABLE_TABLENAME, csa);
		Describer.describeEntityInstances(result_sean.getPassedEntities());
		
		// do update : salary -- failed
		E3 = result_sean.getPassedEntities().get(0);
		E3.set(TESTTABLE_COLUMN_SALARY, 60000.00);
		E3COPY = E3.clone();
		printTag("<------ After updated(E3 -- salary) by sean ------>");
		result_sean = iecs.updateEntityWithSecurity(Sean, E3);
		result_sean = iecs.readEntitiesWithSecurity(Sean, TESTTABLE_TABLENAME, csa);
		Describer.describeEntityInstances(result_sean.getPassedEntities());
		
		/**
		 *  Update E1, E2 and E4 by sean -- one passed and two failed
		 */
		csa = new CompoundStatementAuto(Q.OR);
		csa.addCompoundStatement(new ConditionalStatement(ac_staffid, Q.E, 1));
		csa.addCompoundStatement(new ConditionalStatement(ac_staffid, Q.E, 2));
		csa.addCompoundStatement(new ConditionalStatement(ac_staffid, Q.E, 4));
		result_root = iecs.readEntitiesWithSecurity(root, TESTTABLE_TABLENAME, csa);
		printTag("<++++++ Before updated(E1, E3 and E4 -- isAussie) by sean ++++++>");
		Describer.describeEntityInstances(result_root.getPassedEntities());
		
		// do update
		result_root.getPassedEntities().get(0).set(TESTTABLE_COLUMN_ISAUSSIE, false);
		result_root.getPassedEntities().get(1).set(TESTTABLE_COLUMN_ISAUSSIE, false);
		result_root.getPassedEntities().get(2).set(TESTTABLE_COLUMN_ISAUSSIE, false);
		result_sean = iecs.updateEntitiesWithSecurity(Sean, result_root.getPassedEntities());
		printTag("<------ After updated(E1, E3 and E4 -- isAussie) by sean ------>");
		result_root = iecs.readEntitiesWithSecurity(root, TESTTABLE_TABLENAME, csa);
		Describer.describeEntityInstances(result_root.getPassedEntities());
		
		
		/**
		 * test case 3 :DELETE
		 */
		printTag("<====== case 3 :DELETE ======>");
		
		/**
		 * DELETE by Stephen
		 */
		
		/**
		 *  DELETE E1, E2, E3 by Stephen
		 */
		printTag("<------ DELETE E1, E2, E3 by Stephen ------>");
		csa = new CompoundStatementAuto(Q.OR);
		csa.addCompoundStatement(new ConditionalStatement(ac_staffid, Q.E, 1));
		csa.addCompoundStatement(new ConditionalStatement(ac_staffid, Q.E, 2));
		csa.addCompoundStatement(new ConditionalStatement(ac_staffid, Q.E, 3));
		result_root    = iecs.readEntitiesWithSecurity(root, TESTTABLE_TABLENAME, csa);
		printTag("<++++++ before delete (read by root) ++++++>");
		Describer.describeEntityInstances(result_root.getPassedEntities());
		
		result_stephen = iecs.deleteEntitiesWithSecurity(Stephen, result_root.getPassedEntities());
		
		result_root    = iecs.readEntitiesWithSecurity(root, TESTTABLE_TABLENAME, csa);
		printTag("<++++++ after delete (read by root) ++++++>");
		Describer.describeEntityInstances(result_root.getPassedEntities());
		
		/**
		 *  DELETE E4 by Stephen
		 */
		printTag("<------ DELETE E4 by Stephen ------>");
		csa = new CompoundStatementAuto(Q.OR);
		csa.addCompoundStatement(new ConditionalStatement(ac_staffid, Q.E, 4));
		result_root    = iecs.readEntitiesWithSecurity(root, TESTTABLE_TABLENAME, csa);
		printTag("<++++++ before delete (read by root) ++++++>");
		Describer.describeEntityInstances(result_root.getPassedEntities());
		
		result_stephen = iecs.deleteEntitiesWithSecurity(Stephen, result_root.getPassedEntities());
		
		result_root    = iecs.readEntitiesWithSecurity(root, TESTTABLE_TABLENAME, csa);
		printTag("<++++++ after delete (read by root) ++++++>");
		Describer.describeEntityInstances(result_root.getPassedEntities());
		

		/**
		 * DELETE by Sean
		 */
		
		/**
		 *  DELETE E1, E2 by Sean
		 */
		printTag("<------ DELETE E1, E2 by Sean ------>");
		csa = new CompoundStatementAuto(Q.OR);
		csa.addCompoundStatement(new ConditionalStatement(ac_staffid, Q.E, 1));
		csa.addCompoundStatement(new ConditionalStatement(ac_staffid, Q.E, 2));
		result_root    = iecs.readEntitiesWithSecurity(root, TESTTABLE_TABLENAME, csa);
		printTag("<++++++ before delete (read by root) ++++++>");
		Describer.describeEntityInstances(result_root.getPassedEntities());
		
		result_sean    = iecs.deleteEntitiesWithSecurity(Sean, result_root.getPassedEntities());
		
		result_root    = iecs.readEntitiesWithSecurity(root, TESTTABLE_TABLENAME, csa);
		printTag("<++++++ after delete (read by root) ++++++>");
		Describer.describeEntityInstances(result_root.getPassedEntities());
		
		/**
		 *  DELETE E3 by Sean
		 */
		
		printTag("<------ DELETE E3 by Sean ------>");
		csa = new CompoundStatementAuto(Q.OR);
		csa.addCompoundStatement(new ConditionalStatement(ac_staffid, Q.E, 3));
		result_root    = iecs.readEntitiesWithSecurity(root, TESTTABLE_TABLENAME, csa);
		printTag("<++++++ before delete (read by root) ++++++>");
		Describer.describeEntityInstances(result_root.getPassedEntities());
		
		result_sean = iecs.deleteEntitiesWithSecurity(Sean, result_root.getPassedEntities());
		
		result_root    = iecs.readEntitiesWithSecurity(root, TESTTABLE_TABLENAME, csa);
		printTag("<++++++ after delete (read by root) ++++++>");
		Describer.describeEntityInstances(result_root.getPassedEntities());
		
		
		/**
		 * DELETE by van
		 */
		
		/**
		 *  DELETE E1 by van
		 */
		printTag("<------ DELETE E1 by Van ------>");
		csa = new CompoundStatementAuto(Q.OR);
		csa.addCompoundStatement(new ConditionalStatement(ac_staffid, Q.E, 1));
		result_root    = iecs.readEntitiesWithSecurity(root, TESTTABLE_TABLENAME, csa);
		printTag("<++++++ before delete (read by root) ++++++>");
		Describer.describeEntityInstances(result_root.getPassedEntities());
		
		result_van    = iecs.deleteEntitiesWithSecurity(van, result_root.getPassedEntities());
		
		result_root    = iecs.readEntitiesWithSecurity(root, TESTTABLE_TABLENAME, csa);
		printTag("<++++++ after delete (read by root) ++++++>");
		Describer.describeEntityInstances(result_root.getPassedEntities());
		
		/**
		 *  DELETE E2 by van
		 */
		printTag("<------ DELETE E2 by Van ------>");
		csa = new CompoundStatementAuto(Q.OR);
		csa.addCompoundStatement(new ConditionalStatement(ac_staffid, Q.E, 2));
		result_root    = iecs.readEntitiesWithSecurity(root, TESTTABLE_TABLENAME, csa);
		printTag("<++++++ before delete (read by root) ++++++>");
		Describer.describeEntityInstances(result_root.getPassedEntities());
		
		result_van = iecs.deleteEntitiesWithSecurity(van, result_root.getPassedEntities());
		
		result_root    = iecs.readEntitiesWithSecurity(root, TESTTABLE_TABLENAME, csa);
		printTag("<++++++ after delete (read by root) ++++++>");
		Describer.describeEntityInstances(result_root.getPassedEntities());
		
		/**
		 * DELETE by root
		 */
		/**
		 *  DELETE E1 by van
		 */
		printTag("<------ DELETE E1 by root ------>");
		csa = new CompoundStatementAuto(Q.OR);
		csa.addCompoundStatement(new ConditionalStatement(ac_staffid, Q.E, 1));
		result_root    = iecs.readEntitiesWithSecurity(root, TESTTABLE_TABLENAME, csa);
		printTag("<++++++ before delete (read by root) ++++++>");
		Describer.describeEntityInstances(result_root.getPassedEntities());
		
		result_root    = iecs.deleteEntitiesWithSecurity(root, result_root.getPassedEntities());
		
		result_root    = iecs.readEntitiesWithSecurity(root, TESTTABLE_TABLENAME, csa);
		printTag("<++++++ after delete (read by root) ++++++>");
		Describer.describeEntityInstances(result_root.getPassedEntities());
		
		/**
		 * cleanup DB
		 */
		test.end();
	}
	
	
	
	private List<EntityInstance> getTokens(String group, Character type, boolean permission) {

		CompoundStatementAuto csa = new CompoundStatementAuto(Q.AND);
		csa.addCompoundStatement(new ConditionalStatement(ac_tokenGroup,      Q.E, group));
		csa.addCompoundStatement(new ConditionalStatement(ac_tokenPermission, Q.E, permission));
	    csa.addCompoundStatement(new ConditionalStatement(ac_tokenType,       Q.E, type));
	    
	    return iecs.getDatabaseConnection().read(Token_Info_Table.makeTokenInfoTableName(), csa.toSQLStatement());
	}

	private List<EntityInstance> generateGroupTokens(String group, EntityInstance creator) {
		List<EntityInstance> tokens = new LinkedList<EntityInstance>();
		
		if(group.equals(DEFAULTGROUP)) {
			tokens.addAll(TokenFactory.getDefaultGrantTokens(iecs.getDatabaseConnection()));
			tokens.addAll(TokenFactory.getDefaultDenyTokens(iecs.getDatabaseConnection()));
			iecs.createEntitiesWithSecurity(creator, TokenFactory.getDefaultDenyTokens(iecs.getDatabaseConnection()));
		} else {
			tokens.addAll(generateGroupTokens(group, true));
			tokens.addAll(generateGroupTokens(group, false));
			
			iecs.createEntitiesWithSecurity(creator, tokens);
			
			tokens.clear();
			tokens.addAll(generateGroupTokens(group, true));
			tokens.addAll(generateGroupTokens(group, false));
		}
		
		return tokens;
	}
    
	
	private List<EntityInstance> generateGroupTokens(String group, boolean type) {
		List<EntityInstance> tokens = new LinkedList<EntityInstance>();
		
		String   prefixString = "";
		if(type) prefixString = "g";
		else     prefixString = "d";
		
		EntityInstance tokenc = iecs.createEntityInstanceFor(Token_Info_Table.makeTokenInfoTableName());
		tokenc.set(Token_Info_Table.getColumnTokenID(),     prefixString +"_c_" + group);
		tokenc.set(Token_Info_Table.getColumnTokenName(),   prefixString +"_c_" + group);
		tokenc.set(Token_Info_Table.getColumnCRUDType(),    CRUD.CREATE);
		tokenc.set(Token_Info_Table.getColumnPermission(),  type);
		tokenc.set(Token_Info_Table.getColumnGroup(),       group);
		tokens.add(tokenc);
		
		EntityInstance tokenr = iecs.createEntityInstanceFor(Token_Info_Table.makeTokenInfoTableName());
		tokenr.set(Token_Info_Table.getColumnTokenID(),     prefixString +"_r_" + group);
		tokenr.set(Token_Info_Table.getColumnTokenName(),   prefixString +"_r_" + group);
		tokenr.set(Token_Info_Table.getColumnCRUDType(),    CRUD.READ);
		tokenr.set(Token_Info_Table.getColumnPermission(),  type);
		tokenr.set(Token_Info_Table.getColumnGroup(),       group);
		tokens.add(tokenr);
		
		EntityInstance tokenu = iecs.createEntityInstanceFor(Token_Info_Table.makeTokenInfoTableName());
		tokenu.set(Token_Info_Table.getColumnTokenID(),     prefixString +"_u_" + group);
		tokenu.set(Token_Info_Table.getColumnTokenName(),   prefixString +"_u_" + group);
		tokenu.set(Token_Info_Table.getColumnCRUDType(),    CRUD.UPDATE);
		tokenu.set(Token_Info_Table.getColumnPermission(),  type);
		tokenu.set(Token_Info_Table.getColumnGroup(),       group);
		tokens.add(tokenu);
		
		EntityInstance tokend = iecs.createEntityInstanceFor(Token_Info_Table.makeTokenInfoTableName());
		tokend.set(Token_Info_Table.getColumnTokenID(),     prefixString +"_d_" + group);
		tokend.set(Token_Info_Table.getColumnTokenName(),   prefixString +"_d_" + group);
		tokend.set(Token_Info_Table.getColumnCRUDType(),    CRUD.DELETE);
		tokend.set(Token_Info_Table.getColumnPermission(),  type);
		tokend.set(Token_Info_Table.getColumnGroup(),       group);
		tokens.add(tokend);
		
		return tokens;
	}

	private EntityInstance getRootUser(){
		EntityInstance root   = iecs.readUser(ROOTNAME,ROOTPASS);
		return root;
	}
	
	private HashMap<String, EntityInstance> generateNormalUser(String[][] userInfos) {

		HashMap<String, EntityInstance> userMap = new HashMap<String, EntityInstance>();
		EntityInstance root = getRootUser();
		for(String[] userInfo : userInfos) {
			EntityInstance user = iecs.createEntityInstanceFor(User_Info_Table.makeUserInfoTableName());
			user.set(User_Info_Table.getColumnUserID(),   userInfo[0]);
			user.set(User_Info_Table.getColumnUserName(), userInfo[1]);
			user.set(User_Info_Table.getColumnPassword(), userInfo[2]);
			iecs.createUser(root, user);
			userMap.put(userInfo[0], iecs.readUser(userInfo[1], userInfo[2]));
		}	
		return userMap;
	}
	
	private void begin() throws SQLException {
		iecs = new EntityControllerSecurity("/tmp/credential.txt");
		iecs.dropAllTables();
		
		// initialisation 
		testTable_dataType.put(TESTTABLE_COLUMN_STAFFID,   Integer.class);
		testTable_dataType.put(TESTTABLE_COLUMN_NAME,      String.class);
		testTable_dataType.put(TESTTABLE_COLUMN_GENDER ,   Character.class);
		testTable_dataType.put(TESTTABLE_COLUMN_DOB,       Date.class);
		testTable_dataType.put(TESTTABLE_COLUMN_CV,        byte[].class);
		testTable_dataType.put(TESTTABLE_COLUMN_SALARY,    Double.class);
		testTable_dataType.put(TESTTABLE_COLUMN_ISAUSSIE,  Boolean.class);
		iecs.initializeMode(true, Mode.SECURITY);
		deployDefinitionListWithRowSecurity(null,true,true);
		
		EntityInstance token     = iecs.createEntityInstanceFor(Token_Info_Table.makeTokenInfoTableName()); 
		ac_tokenGroup            = iecs.getAttributeClass(token, Token_Info_Table.getColumnGroup());
		ac_tokenType             = iecs.getAttributeClass(token, Token_Info_Table.getColumnCRUDType());
		ac_tokenPermission       = iecs.getAttributeClass(token, Token_Info_Table.getColumnPermission());
		
		EntityInstance staff     = iecs.createEntityInstanceFor(TESTTABLE_TABLENAME); 
		ac_staffid               = iecs.getAttributeClass(staff, TESTTABLE_COLUMN_STAFFID);
		ac_name                  = iecs.getAttributeClass(staff, TESTTABLE_COLUMN_NAME);
		ac_gender                = iecs.getAttributeClass(staff, TESTTABLE_COLUMN_GENDER);
		ac_dob                   = iecs.getAttributeClass(staff, TESTTABLE_COLUMN_DOB);
		ac_cv                    = iecs.getAttributeClass(staff, TESTTABLE_COLUMN_CV);
		ac_salary                = iecs.getAttributeClass(staff, TESTTABLE_COLUMN_SALARY);
		ac_isaussie              = iecs.getAttributeClass(staff, TESTTABLE_COLUMN_ISAUSSIE);
	}


	private void end() {
		iecs.dropAllTables();
	}
	
	private static void printTag(String tag) {
	       System.out.println("\n"+tag+"\n");
	}
	
	private List<ClassToTables> deployDefinitionListWithRowSecurity( List<ClassToTables> classToTablesList, boolean dropTablesFirst, boolean continueOnError) {
		// Test Data Initialization
		classToTablesList = new LinkedList<ClassToTables>();	
		List<AttributeClass> attributes = new LinkedList<AttributeClass>();	
		AttributeClass primaryAttribute = new AttributeClass(TESTTABLE_COLUMN_STAFFID, testTable_dataType.get(TESTTABLE_COLUMN_STAFFID));
		primaryAttribute.setPrimaryKey(true);
		attributes.add(primaryAttribute);
		attributes.add(new AttributeClass(TESTTABLE_COLUMN_NAME,    testTable_dataType.get(TESTTABLE_COLUMN_NAME)));
		attributes.add(new AttributeClass(TESTTABLE_COLUMN_GENDER,  testTable_dataType.get(TESTTABLE_COLUMN_GENDER)));
		attributes.add(new AttributeClass(TESTTABLE_COLUMN_DOB,     testTable_dataType.get(TESTTABLE_COLUMN_DOB)));
		attributes.add(new AttributeClass(TESTTABLE_COLUMN_CV,      testTable_dataType.get(TESTTABLE_COLUMN_CV)));
		attributes.add(new AttributeClass(TESTTABLE_COLUMN_SALARY,  testTable_dataType.get(TESTTABLE_COLUMN_SALARY)));
		attributes.add(new AttributeClass(TESTTABLE_COLUMN_ISAUSSIE,  testTable_dataType.get(TESTTABLE_COLUMN_ISAUSSIE)));
		
		EntitySecurityClass entitySecurityClass = new EntitySecurityClass(TESTTABLE_TABLENAME, attributes.toArray(new AttributeClass[0]), null);
		classToTablesList.add(entitySecurityClass);			

		List<EntityInstance> defaultTokens = TokenFactory.getDefaultGrantTokens(iecs.getDatabaseConnection());
		iecs.deployDefinitionListWithSecurity(defaultTokens,classToTablesList, false, false);
		
		// Test Code
	    EntityClass entityClass = iecs.getEntityClass(iecs.getDatabaseConnection(), TESTTABLE_TABLENAME, true);
	    
	    // <<< START Assert
	    Assert.assertEquals(SchemaInfo.refactorTableName(entitySecurityClass.getName()), entityClass.getName());
	    Assert.assertEquals(entitySecurityClass.getAttributeClassList().length, entityClass.getAttributeClassList().length);
	    List<String> columnNames = new LinkedList<String>();
	    for(int i = 0 ; i < entitySecurityClass.getAttributeClassList().length; i++){
	    	AttributeClass expectedAttribute = entitySecurityClass.getAttributeClassList()[i];
	    	AttributeClass actualAttribute   = entityClass.getAttributeClassList()[i];
	    	Assert.assertEquals(expectedAttribute.getName().toLowerCase(), actualAttribute.getName());
	    	Assert.assertEquals(expectedAttribute.getSqlType(), actualAttribute.getSqlType());
	    	Assert.assertEquals(expectedAttribute.isPrimaryKey(), actualAttribute.isPrimaryKey());
	    	Assert.assertEquals(expectedAttribute.isAutoIncrement(), actualAttribute.isAutoIncrement());
	    	columnNames.add( actualAttribute.getName());
	    }
	    
	    CompoundStatementManual csm = new CompoundStatementManual();
	    csm.addConditionalStatement(Column_Info_Table.getColumnTableName() + Q.E + Q.QUOT + entityClass.getName() + Q.QUOT );
	    List<EntityInstance> columnInfoInstances = iecs.getDatabaseConnection().read(Column_Info_Table.makeColumnInfoTableName(), csm.toSQLStatement());
	    Assert.assertTrue(columnInfoInstances.size() > 0);
	    for(EntityInstance columnInfoInstance : columnInfoInstances){
	    	Assert.assertTrue(columnNames.contains(columnInfoInstance.get(Column_Info_Table.getColumnColumnName())));
	    }
	    
	    EntityClass mappingEntityClass           = iecs.getEntityClass(iecs.getDatabaseConnection(), Mapping_Table.makeMappingTableName(TESTTABLE_TABLENAME), true);
	    EntityClass rowGrantSecurityEntityClass  = iecs.getEntityClass(iecs.getDatabaseConnection(), Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), true);
	    EntityClass denyGrantSecurityEntityClass = iecs.getEntityClass(iecs.getDatabaseConnection(), Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(TESTTABLE_TABLENAME), true);

	    Assert.assertNotNull(mappingEntityClass);
	    Assert.assertNotNull(rowGrantSecurityEntityClass);
	    Assert.assertNotNull(denyGrantSecurityEntityClass);
	    
	    // <<< END Assert
	    
	    List<ClassToTables> classToTables = new LinkedList<ClassToTables>();
	    classToTables.add(entityClass);
	    
	    return classToTables;
	}	
}

package coredb.main.security.casestudy;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import coredb.config.Configuration.User_Info_Table;
import coredb.controller.EntityControllerSecurity;
import coredb.controller.IEntityControllerSecurity;
import coredb.mode.Mode;
import coredb.security.ResultSecurity;
import coredb.sql.CompoundStatementManual;
import coredb.unit.AttributeClass;
import coredb.unit.CRUD;
import coredb.unit.EntityClass;
import coredb.unit.EntityInstance;
import coredb.utils.Describer;

/**
 * @author Stephen
 * <br>
 * There are four users in this use case who are: <p>
 * 1.Admin   : is the system controller who can do modification on the security application. <br>
 * 2.Manager : has the full permission to do CRUD actions on the client and account table. <br>
 * 3.Casher  : has partial permission to do CRUD actions on account table and full permission on client table. <br>
 * 4.Customer: can read data from the customer table only if the record is not in blacklist. <p>
 * In the use case, Account table and customer table will demonstrate how the security application is working on three groups of token 
 * which are customized for the manager, casher and customer.The security routine of these two tables is displayed as follow: 
 * 
 * <h3>The Deployment of Column Security</h3>
 * <table border = '1'><tr><td colspan = '6' align = 'center'>Account table</td></tr>
 * <tr align = 'center'><td>     </td>    <td>id</td>     <td>accountnumber</td>     <td>password</td>     <td>accounttype</td>     <td>balance</td></tr>
 * <tr align = 'center'><td>admin</td>    <td>CRUD</td>   <td>CRUD</td>              <td>CRUD</td>         <td>CRUD</td>            <td>CRUD</td></tr>
 * <tr align = 'center'><td>manager</td>  <td>CRUD</td>   <td>CRUD</td>              <td>CRUD</td>         <td>CRUD</td>            <td>CRUD</td></tr>
 * <tr align = 'center'><td>casher</td>   <td>CRUD</td>   <td>CRD </td>              <td>CUD </td>         <td>CRUD</td>            <td>CRU </td></tr>
 * <tr align = 'center'><td>customer</td> <td>-</td>      <td>-</td>                 <td>-</td>            <td>-</td>               <td>- </td></tr></table>
 * 
 * <br> 
 * <table border = '1' ><tr><td colspan = '8' align = 'center'>Customer table</td></tr>
 * <tr align = 'center'><td>     </td>    <td>id</td>     <td>name</td>     <td>dob</td>     <td>passport</td>     <td>phone</td>     <td>email</td>     <td>isblacklist</td></tr>
 * <tr align = 'center'><td>admin</td>    <td>CRUD</td>   <td>CRUD</td>     <td>CRUD</td>    <td>CRUD</td>         <td>CRUD</td>      <td>CRUD</td>      <td>CRUD</td></tr>
 * <tr align = 'center'><td>manager</td>  <td>CRUD</td>   <td>CRUD</td>     <td>CRUD</td>    <td>CRUD</td>         <td>CRUD</td>      <td>CRUD</td>      <td>CRUD</td></tr>
 * <tr align = 'center'><td>casher</td>   <td>CRUD</td>   <td>CRUD</td>     <td>CRUD</td>    <td>CRUD</td>         <td>CRUD</td>      <td>CRUD</td>      <td>CRUD</td></tr>
 * <tr align = 'center'><td>customer</td> <td>R</td>      <td>R</td>        <td>R</td>       <td>R</td>            <td>RU</td>        <td>RU</td>        <td>-</td></tr></table>
 * 
 * <br>
 * The account table allows manager to do CRUD on all data from columns but not permit customer to visit it. Mostly same as  manager, casher can obtain the permission to do CRUD on account table except:<p>
 * 1. Accountnumber : update.<br>
 * 2. Password : read.<br>
 * 3. Balance: delete.<p>
 * 
 * <br> 
 * The customer table allows staff to do CRUD on all column. The customer can update their phone and email and can read all data from columns except isblacklist.
 *
 * <h3>The Deployment of Row Security</h3>
 * <table border = '1' ><tr><td colspan = '8' align = 'center'>Customer table before isblacklist updated to true</td></tr>
 * <tr align = 'center'><td> </td>    <td>admin</td>     <td>manager</td>     <td>casher</td>     <td>customer</td></tr>
 * <tr align = 'center'><td>row 1</td>    <td>CRUD</td>      <td>CRUD</td>        <td>CRUD</td>       <td>CRUD</td></tr></table>
 * 
 * <br>
 * <table border = '1' ><tr><td colspan = '8' align = 'center'>Customer table after isblacklist updated to true</td></tr>
 * <tr align = 'center'><td> </td>    <td>admin</td>     <td>manager</td>     <td>casher</td>     <td>customer</td></tr>
 * <tr align = 'center'><td>row 1</td>    <td>CRUD</td>      <td>CRUD</td>        <td>CRUD</td>       <td>-</td></tr></table>
 * <br>
 * 
 * The rows of customer table are always operable for manager and casher. 
 * Initially, customer can visit and make actions on the rows of customer table. However, if the value of isblacklist is changed to true in one row,
 * the row security will disallow customer to visit it.
 */
public class SampleUseCaseWithSecurity {
	private AccountTableBuilder  accountTableBuilder;
	private CustomerTableBuilder customerTableBuilder;
	private UserBuilder          userBuilder;
	private TokenGroupBuilder    tokenGroupBuilder;
	
	private IEntityControllerSecurity iecs;
	
	public static void main(String[] args){
		String filePath = "/tmp/credential.txt";
		if(args.length > 0)	filePath = args[0];
		SampleUseCaseWithSecurity usecase = new SampleUseCaseWithSecurity(filePath);
		try {
			usecase.run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public SampleUseCaseWithSecurity(String filePath){
		try {
			this.iecs = new EntityControllerSecurity(filePath);
			iecs.getDatabaseConnection().dropAllTables();
			iecs.initializeMode(false, Mode.SECURITY);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void run() throws IOException{
		System.out.println("Weclome to coredb security usecase sample.\n");
		System.out.println("Building the scenario of usecase...\n");
		buildScenario();
		System.out.println("The scenario of use case is built up.\n");
		
		EntityInstance admin   = userBuilder.getUserIntance(UserBuilder.ADMIN, UserBuilder.ADMINPASS);
		EntityInstance manager = userBuilder.getUserIntance(UserBuilder.MANAGER, UserBuilder.MANAGERPASS);
		EntityInstance casher  = userBuilder.getUserIntance(UserBuilder.CASHER, UserBuilder.CASHERPASS);
		EntityInstance customer  = userBuilder.getUserIntance(UserBuilder.CUSTOMER, UserBuilder.CUSTOMERPASS);
	
		// Only Column Security
		System.out.println("[START: ------------------------------------------------------------------------ Account Table ------------------------------------------------------------------------]");
		createDataONAccountTable(manager, casher);
		readDataFromAccountTable(manager, casher);	
		updateDataOnAccountTable(manager, casher);	
		deleteDataOnAccountTable(manager, casher);
		System.out.println("[END: ------------------------------------------------------------------------ Account Table ------------------------------------------------------------------------]\n");

		
		// Column Security & Row Security
		System.out.println("[START: ------------------------------------------------------------------------ Customer Table ------------------------------------------------------------------------]");
		createDataOnClientTable(admin, manager);
		readDataFromClientTable(customer);
		updateDataOnClientTable(admin, manager);
		readDataFromClientTable(customer);
		System.out.println("[END: ------------------------------------------------------------------------ Customer Table ------------------------------------------------------------------------]\n");
		
	}

	private void createDataOnClientTable(EntityInstance admin, EntityInstance manager) {
		System.out.println("<START Manager: Create>");
		EntityInstance entityInstanceForCreate = iecs.createEntityInstanceFor(CustomerTableBuilder.TABLE_NAME);
		entityInstanceForCreate.set(CustomerTableBuilder.COLUMN_ID, 1);
		entityInstanceForCreate.set(CustomerTableBuilder.COLUMN_NAME, UserBuilder.CUSTOMER);
		entityInstanceForCreate.set(CustomerTableBuilder.COLUMN_PASSPORT, "G19087523");
		entityInstanceForCreate.set(CustomerTableBuilder.COLUMN_DOB, Date.valueOf("1983-09-09"));
		entityInstanceForCreate.set(CustomerTableBuilder.COLUMN_EMAIL, "customer@gmail.com");
		entityInstanceForCreate.set(CustomerTableBuilder.COLUMN_PHONE, "39985466");
		entityInstanceForCreate.set(CustomerTableBuilder.COLUMN_ISBLACKLIST, false);
		iecs.createEntityWithSecurity(manager, entityInstanceForCreate);
		Describer.describeEntityInstances(iecs.getDatabaseConnection().read(CustomerTableBuilder.TABLE_NAME, ""));
		System.out.println("<END Manager: Create>\n");
		
		List<EntityInstance> entityInstances = iecs.readEntitiesWithSecurity(manager, CustomerTableBuilder.TABLE_NAME, new CompoundStatementManual()).getPassedEntities();
		List<EntityInstance> rows = new LinkedList<EntityInstance>();
		rows.addAll(entityInstances);
		iecs.addTokensToRows(admin, rows, tokenGroupBuilder.getGroupTokens(TokenGroupBuilder.MANAGERGROUP, admin, true));
		iecs.addTokensToRows(admin, rows, tokenGroupBuilder.getGroupTokens(TokenGroupBuilder.CASHERGROUP, admin, true));
		iecs.addTokensToRows(admin, rows, tokenGroupBuilder.getGroupTokens(TokenGroupBuilder.CUSTOMERGROUP, admin, true));
		
	}
	
	private void readDataFromClientTable(EntityInstance customer) {
		System.out.println("<START Customer: Read>");
		ResultSecurity resultSecurity = iecs.readEntitiesWithSecurity(customer, CustomerTableBuilder.TABLE_NAME, new CompoundStatementManual());
		resultSecurity.describePassedFailedEntities();
		System.out.println("<End: Read>\n");
	
	}
	
	private void updateDataOnClientTable(EntityInstance admin, EntityInstance manager) {
		System.out.println("<START Manager: Update>");
		List<EntityInstance> entityInstancesForUpdate = iecs.readEntitiesWithSecurity(manager, CustomerTableBuilder.TABLE_NAME, new CompoundStatementManual()).getPassedEntities();
		for(EntityInstance entityInstance : entityInstancesForUpdate){
			entityInstance.set(CustomerTableBuilder.COLUMN_ISBLACKLIST,  true);
		}
		iecs.updateEntitiesWithSecurity(manager, entityInstancesForUpdate);
		Describer.describeEntityInstances(iecs.getDatabaseConnection().read(CustomerTableBuilder.TABLE_NAME, ""));
		System.out.println("<END Manager: Update>\n");

		List<EntityInstance> rows = new LinkedList<EntityInstance>();
		List<EntityInstance> entityInstances = iecs.readEntitiesWithSecurity(manager, CustomerTableBuilder.TABLE_NAME, new CompoundStatementManual()).getPassedEntities();
		rows.addAll(entityInstances);
		iecs.addTokensToRows(admin, rows, tokenGroupBuilder.getGroupTokens(TokenGroupBuilder.CUSTOMERGROUP, admin, false));
	}

	private void createDataONAccountTable(EntityInstance manager, EntityInstance casher) {
		System.out.println("<START Manager: Create");
		EntityInstance entityInstanceForCreate = iecs.createEntityInstanceFor(AccountTableBuilder.TABLE_NAME);
		entityInstanceForCreate.set(AccountTableBuilder.COLUMN_ID, 1);
		entityInstanceForCreate.set(AccountTableBuilder.COLUMN_ACCOUNTNUMBER, "229809890");
		entityInstanceForCreate.set(AccountTableBuilder.COLUMN_PASSWORD, "unipass");
		entityInstanceForCreate.set(AccountTableBuilder.COLUMN_ACOUNTTYPE, "saving");
		entityInstanceForCreate.set(AccountTableBuilder.COLUMN_BALANCE, 30000.0);
		iecs.createEntityWithSecurity(manager, entityInstanceForCreate);
		Describer.describeEntityInstances(iecs.getDatabaseConnection().read(AccountTableBuilder.TABLE_NAME, ""));
		System.out.println("<END Manager: Create>\n");

		System.out.println("<START Caser: Create");
		entityInstanceForCreate = iecs.createEntityInstanceFor(AccountTableBuilder.TABLE_NAME);
		entityInstanceForCreate.set(AccountTableBuilder.COLUMN_ID, 2);
		entityInstanceForCreate.set(AccountTableBuilder.COLUMN_ACCOUNTNUMBER, "229809891");
		entityInstanceForCreate.set(AccountTableBuilder.COLUMN_PASSWORD, "unipass");
		entityInstanceForCreate.set(AccountTableBuilder.COLUMN_ACOUNTTYPE, "term deposit");
		entityInstanceForCreate.set(AccountTableBuilder.COLUMN_BALANCE, 50000.0);
		iecs.createEntityWithSecurity(casher, entityInstanceForCreate);
		Describer.describeEntityInstances(iecs.getDatabaseConnection().read(AccountTableBuilder.TABLE_NAME, ""));
		System.out.println("<END Casher: Create>\n");

	}

	private void readDataFromAccountTable(EntityInstance manager, EntityInstance casher) {
		System.out.println("<START Manager: Read>");
		ResultSecurity resultSecurity = iecs.readEntitiesWithSecurity(manager, AccountTableBuilder.TABLE_NAME, new CompoundStatementManual());
		resultSecurity.describePassedFailedEntities();
		System.out.println("<END Manager: Read>\n");
		
		System.out.println("<START Casher: Read>");
		resultSecurity = iecs.readEntitiesWithSecurity(casher, AccountTableBuilder.TABLE_NAME, new CompoundStatementManual());
		resultSecurity.describePassedFailedEntities();
		System.out.println("<End Casher: Read>\n");

	}
	
	private void updateDataOnAccountTable(EntityInstance manager, EntityInstance casher) {	
		System.out.println("<START Manager: Update>");
		List<EntityInstance> entityInstancesForUpdate = iecs.readEntitiesWithSecurity(manager, AccountTableBuilder.TABLE_NAME, new CompoundStatementManual()).getPassedEntities();
		for(EntityInstance entityInstance : entityInstancesForUpdate){
			entityInstance.set(AccountTableBuilder.COLUMN_ACCOUNTNUMBER,  entityInstance.get(AccountTableBuilder.COLUMN_ACCOUNTNUMBER) + "_update1");
		}
		iecs.updateEntitiesWithSecurity(manager, entityInstancesForUpdate);
		Describer.describeEntityInstances(iecs.getDatabaseConnection().read(AccountTableBuilder.TABLE_NAME, ""));
		System.out.println("<END Manager: Update>\n");
	
		System.out.println("<START Casher: Update>");
		entityInstancesForUpdate = iecs.readEntitiesWithSecurity(casher, AccountTableBuilder.TABLE_NAME, new CompoundStatementManual()).getPassedEntities();
		for(EntityInstance entityInstance : entityInstancesForUpdate){
			entityInstance.set(AccountTableBuilder.COLUMN_ACCOUNTNUMBER,  entityInstance.get(AccountTableBuilder.COLUMN_ACCOUNTNUMBER) + "_update2");
		}
		iecs.updateEntitiesWithSecurity(casher, entityInstancesForUpdate);
		Describer.describeEntityInstances(iecs.getDatabaseConnection().read(AccountTableBuilder.TABLE_NAME, ""));
		System.out.println("<END Cahser: Update>\n");

	}
	
	private void deleteDataOnAccountTable(EntityInstance manager, EntityInstance casher) {		
		System.out.println("<START Cahser: Delete>");
		List<EntityInstance> entityInstancesForDelete = iecs.readEntitiesWithSecurity(manager, AccountTableBuilder.TABLE_NAME, new CompoundStatementManual()).getPassedEntities();
		iecs.deleteEntitiesWithSecurity(casher, entityInstancesForDelete);
		Describer.describeEntityInstances(iecs.getDatabaseConnection().read(AccountTableBuilder.TABLE_NAME, ""));
		System.out.println("<END Casher: Delete>\n");

		System.out.println("<STRAT Manager: Delete>");
		entityInstancesForDelete = iecs.readEntitiesWithSecurity(manager, AccountTableBuilder.TABLE_NAME, new CompoundStatementManual()).getPassedEntities();
		iecs.deleteEntitiesWithSecurity(manager, entityInstancesForDelete);
		Describer.describeEntityInstances(iecs.getDatabaseConnection().read(AccountTableBuilder.TABLE_NAME, ""));
		System.out.println("<END Manager: Delete>\n");
		
	}
	
	private void buildScenario() {		
		this.accountTableBuilder  = new AccountTableBuilder();
		this.customerTableBuilder = new CustomerTableBuilder();
		this.userBuilder          = new UserBuilder(iecs);
		EntityInstance admin   	 = userBuilder.getUserIntance(UserBuilder.ADMIN, UserBuilder.ADMINPASS);
		EntityInstance manager   = userBuilder.getUserIntance(UserBuilder.MANAGER, UserBuilder.MANAGERPASS);
		EntityInstance casher    = userBuilder.getUserIntance(UserBuilder.CASHER, UserBuilder.CASHERPASS);
		EntityInstance customer  = userBuilder.getUserIntance(UserBuilder.CUSTOMER, UserBuilder.CUSTOMERPASS);
		this.tokenGroupBuilder   = new TokenGroupBuilder(iecs, admin);

		
		System.out.println("Security routine is deploying on tables and users...");
		// Add user to different group
		iecs.addUserToGroup(admin, manager, TokenGroupBuilder.MANAGERGROUP);
		iecs.addUserToGroup(admin, casher, TokenGroupBuilder.CASHERGROUP);
		iecs.addUserToGroup(admin, customer, TokenGroupBuilder.CUSTOMERGROUP);	
		printTokensOwnedByUser(manager);
		printTokensOwnedByUser(casher);
		printTokensOwnedByUser(customer);

		// Deploy the account table	and apply security routine for it
		accountTableBuilder.deployTableSchema(iecs, tokenGroupBuilder.getGroupTokens(TokenGroupBuilder.MANAGERGROUP, admin, true));
		accountTableBuilder.deployTableSchema(iecs, tokenGroupBuilder.getGroupTokens(TokenGroupBuilder.CASHERGROUP, admin, true));			
		AttributeClass[] readDenyColumns   = new AttributeClass[]{accountTableBuilder.getAttribute(AccountTableBuilder.COLUMN_PASSWORD)};
		AttributeClass[] updateDenyColumns = new AttributeClass[]{accountTableBuilder.getAttribute(AccountTableBuilder.COLUMN_ACCOUNTNUMBER)};
		AttributeClass[] deleteDenyColumns = new AttributeClass[]{accountTableBuilder.getAttribute(AccountTableBuilder.COLUMN_BALANCE)};
		iecs.addTokensToColumns(admin, AccountTableBuilder.TABLE_NAME, readDenyColumns, tokenGroupBuilder.getTokens(TokenGroupBuilder.CASHERGROUP, CRUD.READ, false));
		iecs.addTokensToColumns(admin, AccountTableBuilder.TABLE_NAME, updateDenyColumns, tokenGroupBuilder.getTokens(TokenGroupBuilder.CASHERGROUP, CRUD.UPDATE, false));
		iecs.addTokensToColumns(admin, AccountTableBuilder.TABLE_NAME, deleteDenyColumns, tokenGroupBuilder.getTokens(TokenGroupBuilder.CASHERGROUP, CRUD.DELETE, false));
		printTokensOwnedByTable(accountTableBuilder.getTableSchema());
		
		// Deploy the client table and apply security routine for it
		customerTableBuilder.deployTableSchema(iecs, tokenGroupBuilder.getGroupTokens(TokenGroupBuilder.MANAGERGROUP, admin, true));
		customerTableBuilder.deployTableSchema(iecs, tokenGroupBuilder.getGroupTokens(TokenGroupBuilder.CASHERGROUP, admin, true));
		customerTableBuilder.deployTableSchema(iecs, tokenGroupBuilder.getGroupTokens(TokenGroupBuilder.CUSTOMERGROUP, admin));		
		AttributeClass[] readableColumns  = new AttributeClass[]{customerTableBuilder.getAttribute(CustomerTableBuilder.COLUMN_NAME), 
																 customerTableBuilder.getAttribute(CustomerTableBuilder.COLUMN_DOB), 
																 customerTableBuilder.getAttribute(CustomerTableBuilder.COLUMN_PASSPORT), 
																 customerTableBuilder.getAttribute(CustomerTableBuilder.COLUMN_PHONE), 
																 customerTableBuilder.getAttribute(CustomerTableBuilder.COLUMN_EMAIL)};	
		AttributeClass[] updatableColumns = new AttributeClass[]{customerTableBuilder.getAttribute(CustomerTableBuilder.COLUMN_PHONE),
																 customerTableBuilder.getAttribute(CustomerTableBuilder.COLUMN_EMAIL)};	
		iecs.removeTokensFromColumns(admin, CustomerTableBuilder.TABLE_NAME, readableColumns, tokenGroupBuilder.getTokens(TokenGroupBuilder.CUSTOMERGROUP, CRUD.READ, false));
		iecs.removeTokensFromColumns(admin, CustomerTableBuilder.TABLE_NAME, updatableColumns, tokenGroupBuilder.getTokens(TokenGroupBuilder.CUSTOMERGROUP, CRUD.UPDATE, false));
		printTokensOwnedByTable(customerTableBuilder.getTableSchema());
	
		
	}
	
	private void printTokensOwnedByTable(EntityClass tableSchema) {
		System.out.println("[START Deploy Security On Table "+ tableSchema.getName() +"]");
		EntityInstance admin   	 = userBuilder.getUserIntance(UserBuilder.ADMIN, UserBuilder.ADMINPASS);
		for(AttributeClass attribute : tableSchema.getAttributeClassList()){
			System.out.println("<START Deploy Security On Column"+ attribute.getName() +">");
			Describer.describeEntityInstances(iecs.getTokensByColumn(admin, tableSchema.getName(), attribute).getGrantTokens());
			Describer.describeEntityInstances(iecs.getTokensByColumn(admin, tableSchema.getName(), attribute).getDenyTokens());
			System.out.println("<END Deploy Security On Column"+ attribute.getName() +">\n");
		}
		System.out.println("[END Deploy Security On Table "+ tableSchema.getName() +"]\n");

	}
	
	private void printTokensOwnedByUser(EntityInstance user) {
		System.out.println("[START Deploy Security To User "+ ((String)user.get(User_Info_Table.getColumnUserName())).toLowerCase() +"] ");
		EntityInstance admin   	 = userBuilder.getUserIntance(UserBuilder.ADMIN, UserBuilder.ADMINPASS);
		Describer.describeEntityInstances(iecs.getTokensByUser(admin, user).getGrantTokens());
		Describer.describeEntityInstances(iecs.getTokensByUser(admin, user).getDenyTokens());
		System.out.println("[END Deploy Security To User "+ ((String)user.get(User_Info_Table.getColumnUserName())).toLowerCase() +"] \n");

	}
	
}



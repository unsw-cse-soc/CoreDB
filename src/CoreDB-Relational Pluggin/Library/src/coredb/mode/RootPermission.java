package coredb.mode;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import coredb.config.Configuration;
import coredb.config.Configuration.Token_Info_Table;
import coredb.database.DatabaseConnection;
import coredb.sql.CompoundStatementAuto;
import coredb.sql.Q;
import coredb.unit.CRUD;
import coredb.unit.EntityInstance;

public class RootPermission {
	public static final String GROUPNAME         = "ROOT";
	public static final String ROOTUSER          = "ROOT";
	public static final String ROOTUSERPASSWORD  = "ROOT";
	private static final String DELIMITER        = Configuration.Security_Methods.getSecurityDelimter();
	public static final String ROOT$C$G         = GROUPNAME + DELIMITER + CRUD.CREATE + DELIMITER + Configuration.Security_Methods.GRANT;
	public static final String ROOT$R$G         = GROUPNAME + DELIMITER + CRUD.READ   + DELIMITER + Configuration.Security_Methods.GRANT;
	public static final String ROOT$U$G         = GROUPNAME + DELIMITER + CRUD.UPDATE + DELIMITER + Configuration.Security_Methods.GRANT;
	public static final String ROOT$D$G         = GROUPNAME + DELIMITER + CRUD.DELETE + DELIMITER + Configuration.Security_Methods.GRANT;
	private static final String[] ROOTTOKENIDS   = new String[]{ROOT$C$G,ROOT$R$G,ROOT$U$G,ROOT$D$G};	
	
	
	/**
	 * This method creates default Root user, default root Tokens and attached all Tokens to Root user and columns of all exsiting tables
	 * @param databaseConnection
	 */
	public static void initializeRootPermission(DatabaseConnection databaseConnection){
		databaseConnection.refresh();

		createRootTokens(databaseConnection);
		createRootUser(databaseConnection);
		attachRootTokensToRootUser(databaseConnection);
		deployRootPermissionToColumns(databaseConnection);
	}

	/**
	 * This method will create a list of Token belonged to Root user
	 * @param databaseConnection
	 * @return true if transaction is successful
	 */
	private static boolean createRootTokens(DatabaseConnection databaseConnection){
		List<EntityInstance> tokenInstances = getRootTokens(databaseConnection);

		return databaseConnection.create(tokenInstances);

	}

	public static List<EntityInstance> getRootTokens(DatabaseConnection databaseConnection) {
		String TOKENIFO                     = Configuration.Token_Info_Table.makeTokenInfoTableName();
		String TOKENIFO_TOKENID             = Configuration.Token_Info_Table.getColumnTokenID();
		String TOKENIFO_TOKENNAME           = Configuration.Token_Info_Table.getColumnTokenName();
		String TOKENIFO_CRUD                = Configuration.Token_Info_Table.getColumnCRUDType();
		String TOKENIFO_TOKENPERMISSION     = Configuration.Token_Info_Table.getColumnPermission();
		String TOKENIFO_GROUPNAME           = Configuration.Token_Info_Table.getColumnGroup();
		List<EntityInstance> tokenInstances = new LinkedList<EntityInstance>();
 		
		// Create permission
		EntityInstance tokenInstance        = databaseConnection.createEntityInstanceFor(TOKENIFO,false);
		tokenInstance.set(TOKENIFO_TOKENID, ROOT$C$G);
		tokenInstance.set(TOKENIFO_TOKENNAME, ROOT$C$G);
		tokenInstance.set(TOKENIFO_CRUD, CRUD.CREATE);
		tokenInstance.set(TOKENIFO_TOKENPERMISSION, true);
		tokenInstance.set(TOKENIFO_GROUPNAME, GROUPNAME);
		tokenInstances.add(tokenInstance);
		
		// Read permission
		tokenInstance                       = databaseConnection.createEntityInstanceFor(TOKENIFO,false);
		tokenInstance.set(TOKENIFO_TOKENID, ROOT$R$G);
		tokenInstance.set(TOKENIFO_TOKENNAME, ROOT$R$G);
		tokenInstance.set(TOKENIFO_CRUD, CRUD.READ);
		tokenInstance.set(TOKENIFO_GROUPNAME, GROUPNAME);
		tokenInstance.set(TOKENIFO_TOKENPERMISSION, true);
		tokenInstances.add(tokenInstance);
		
		// Update permission
		tokenInstance                       = databaseConnection.createEntityInstanceFor(TOKENIFO,false);
		tokenInstance.set(TOKENIFO_TOKENID, ROOT$U$G);
		tokenInstance.set(TOKENIFO_TOKENNAME, ROOT$U$G);
		tokenInstance.set(TOKENIFO_CRUD, CRUD.UPDATE);
		tokenInstance.set(TOKENIFO_GROUPNAME, GROUPNAME);
		tokenInstance.set(TOKENIFO_TOKENPERMISSION, true);
		tokenInstances.add(tokenInstance);
		
		// Delete permission
		tokenInstance                       = databaseConnection.createEntityInstanceFor(TOKENIFO,false);
		tokenInstance.set(TOKENIFO_TOKENID, ROOT$D$G);
		tokenInstance.set(TOKENIFO_TOKENNAME, ROOT$D$G);
		tokenInstance.set(TOKENIFO_CRUD, CRUD.DELETE);
		tokenInstance.set(TOKENIFO_GROUPNAME, GROUPNAME);
		tokenInstance.set(TOKENIFO_TOKENPERMISSION, true);
		tokenInstances.add(tokenInstance);
		
		return tokenInstances;
	}

	/**
	 * This method will create default Root user
	 * @param databaseConnection
	 * @return true if transaction is successful
	 */
	private static  boolean createRootUser(DatabaseConnection databaseConnection){
		EntityInstance userInstance      = databaseConnection.createEntityInstanceFor(Configuration.User_Info_Table.makeUserInfoTableName(),false);
		userInstance.set(Configuration.User_Info_Table.getColumnUserID(), ROOTUSER);
		userInstance.set(Configuration.User_Info_Table.getColumnUserName(), ROOTUSER);
		userInstance.set(Configuration.User_Info_Table.getColumnPassword(), ROOTUSERPASSWORD);	
		return databaseConnection.create(userInstance);
		
	}

	/**
	 * This method will attach the list of default Tokens to Root user
	 * @param databaseConnection
	 * @return true if transaction is successful
	 */
	private static boolean attachRootTokensToRootUser(DatabaseConnection databaseConnection){
		List<EntityInstance> userTokenInstances =  new LinkedList<EntityInstance>();
		for(String tokenId : ROOTTOKENIDS){
			EntityInstance userTokenInstance    = databaseConnection.createEntityInstanceFor(Configuration.User_Token_Table.makeUserTokenTableName(),false);
			userTokenInstance.set(Configuration.User_Token_Table.getColumnTokenID(), tokenId);
			userTokenInstance.set(Configuration.User_Token_Table.getColumnUserID(), ROOTUSER);
			userTokenInstances.add(userTokenInstance);
		}
		return databaseConnection.create(userTokenInstances);
	}
	
	/**
	 * This method will deploy the default list of Root Tokens to columns of all existing tables
	 * @param databaseConnection
	 * @return true if transaction is successful
	 */
	// #COREDB-57
	public static boolean deployRootPermissionToColumns(DatabaseConnection databaseConnection){
	// #/COREDB-57
		String COLUMNINFO                            = Configuration.Column_Info_Table.makeColumnInfoTableName();
		String COLUMNINFO_COLUMNID                   = Configuration.Column_Info_Table.makeColumnInfoTableColumnNameCoredbId();
		String COLUMNSECURITYGRANT                   = Configuration.Column_Security_Grant_Table.makeColumnSecurityGrantTableName();
		String COLUMNSECURITYGRANT_COLUMNID          = Configuration.Column_Security_Grant_Table.getColumnColumnID();
		String COLUMNSECURITYGRANT_TOKENID           = Configuration.Column_Security_Grant_Table.getColumnTokenID();	
		List<EntityInstance> entityInstances         = databaseConnection.read(COLUMNINFO,"");
		List<EntityInstance> columnSecurityInstances = new LinkedList<EntityInstance>();
		
		// #COREDB-57
		List<EntityInstance> tokens                  = databaseConnection.read(Token_Info_Table.makeTokenInfoTableName(), Token_Info_Table.getColumnGroup() + Q.E + Q.QUOT + GROUPNAME + Q.QUOT);
		CompoundStatementAuto csa                    = new CompoundStatementAuto(Q.OR);
		for(EntityInstance token: tokens){
			csa.addCompoundStatement(COLUMNSECURITYGRANT_TOKENID + Q.E + Q.QUOT + token.get(Token_Info_Table.getColumnTokenID()) + Q.QUOT);
		}
		List<EntityInstance> existedColumnSecurityInstances         = databaseConnection.read(COLUMNSECURITYGRANT, csa.toSQLStatement());
		HashMap<Integer, Boolean> existedColumnSecurityInstancesMap = new HashMap<Integer, Boolean>();
		for(EntityInstance existedColumnSecurityInstance : existedColumnSecurityInstances){
			existedColumnSecurityInstancesMap.put((Integer)existedColumnSecurityInstance.get(COLUMNSECURITYGRANT_COLUMNID), true);
		}
		// #/COREDB-57
		
		for(EntityInstance entityInstance : entityInstances){
			Integer columnId                          = (Integer)entityInstance.get(COLUMNINFO_COLUMNID);
		// #COREDB-57
		if(existedColumnSecurityInstancesMap.get(columnId) != null) continue;
		// #/COREDB-57
			for(String tokenId : ROOTTOKENIDS){
				EntityInstance columnSecurityInstance = databaseConnection.createEntityInstanceFor(COLUMNSECURITYGRANT,false);
				columnSecurityInstance.set(COLUMNSECURITYGRANT_COLUMNID, columnId);
				columnSecurityInstance.set(COLUMNSECURITYGRANT_TOKENID, tokenId);
				columnSecurityInstances.add(columnSecurityInstance);
			}
		}
		return databaseConnection.create(columnSecurityInstances);
	}
	
	/*
	public void main(String[] args) throws SQLException{
		IEntityControllerBase iec   = new EntityControllerBase("/tmp/credential.txt");
		iec.dropAllTables();
		//iec.initializeMode(true, Mode.TRACING);
		iec.initializeMode(true, Mode.SECURITY);
		RootPermission.initializeRootPermission(iec.getDatabaseConnection());
	}
	 */

}
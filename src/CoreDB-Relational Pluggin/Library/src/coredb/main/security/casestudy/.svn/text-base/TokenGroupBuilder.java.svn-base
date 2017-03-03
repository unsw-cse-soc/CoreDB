package coredb.main.security.casestudy;

import java.util.LinkedList;
import java.util.List;

import coredb.config.Configuration.Token_Info_Table;
import coredb.controller.IEntityControllerSecurity;
import coredb.mode.RootPermission;
import coredb.sql.CompoundStatementManual;
import coredb.sql.Q;
import coredb.unit.CRUD;
import coredb.unit.EntityInstance;

public class TokenGroupBuilder {
	private List<EntityInstance> managerGroupGrantTokens;
	private List<EntityInstance> managerGroupDenyTokens;
	private List<EntityInstance> casherGroupGrantTokens;
	private List<EntityInstance> casherGroupDenyTokens;
	private List<EntityInstance> customerGroupGrantTokens;
	private List<EntityInstance> customerGroupDenyTokens;
	private IEntityControllerSecurity iecs;
	
	public static final String  MANAGERGROUP  = "MANAGERGROUP";
	public static final String  CASHERGROUP   = "CASHERGROUP";
	public static final String  CUSTOMERGROUP = "CUSTOMERGROUP";
	public static final String  ADMINGROUP    = RootPermission.GROUPNAME;
	
	public TokenGroupBuilder(IEntityControllerSecurity iecs, EntityInstance systemAdmin){
		this.iecs                     = iecs;
		this.managerGroupGrantTokens = generateGroupTokens(MANAGERGROUP, true);
		this.managerGroupDenyTokens  = generateGroupTokens(MANAGERGROUP, false);
		this.casherGroupGrantTokens  = generateGroupTokens(CASHERGROUP, true);
		this.casherGroupDenyTokens   = generateGroupTokens(CASHERGROUP, false);
		this.customerGroupGrantTokens  = generateGroupTokens(CUSTOMERGROUP, true);
		this.customerGroupDenyTokens   = generateGroupTokens(CUSTOMERGROUP, false);
		
		System.out.println("Build four tokens groups which are manager group , casher group and customer group. All of these tokens contain CRUD with grant and deny permission. ");
		
		iecs.createEntitiesWithSecurity(systemAdmin, managerGroupGrantTokens);
		iecs.createEntitiesWithSecurity(systemAdmin, managerGroupDenyTokens);
		iecs.createEntitiesWithSecurity(systemAdmin, casherGroupGrantTokens);
		iecs.createEntitiesWithSecurity(systemAdmin, casherGroupDenyTokens);
		iecs.createEntitiesWithSecurity(systemAdmin, customerGroupGrantTokens);
		iecs.createEntitiesWithSecurity(systemAdmin, customerGroupDenyTokens);
	}
	
	public List<EntityInstance> getGroupTokens(String group, EntityInstance reader) {
		List<EntityInstance> allTokens = new LinkedList<EntityInstance>();
		allTokens.addAll(iecs.getTokensByGroup(reader, group).getGrantTokens());
		allTokens.addAll(iecs.getTokensByGroup(reader, group).getDenyTokens());
		return allTokens;
		
	}

	public List<EntityInstance> getGroupTokens(String group, EntityInstance reader, Boolean isGrant) {
		if(isGrant) return iecs.getTokensByGroup(reader, group).getGrantTokens();
		else        return iecs.getTokensByGroup(reader, group).getDenyTokens();
		
	}
	
	public List<EntityInstance> getTokens(String group, Character crud, boolean isGrant){
		CompoundStatementManual csm = new CompoundStatementManual();
		csm.addConditionalStatement(Token_Info_Table.getColumnTokenName() + Q.E + Q.QUOT + buildTokenName(group, crud, isGrant) + Q.QUOT);
		List<EntityInstance> tokenInstances = iecs.getDatabaseConnection().read(Token_Info_Table.makeTokenInfoTableName(), csm.toSQLStatement());
		return tokenInstances;
	}
	
	
	/**
	 * The token name should be unique and the default value of token id is same as token name;
	 * @param isGrant  if true, will deploy the tokens as grant tokens; otherwise, will deploy them as deny tokens
	 * @return a list of token entity instance
	 */
	private List<EntityInstance> generateGroupTokens(String group, boolean isGrant) {
		List<EntityInstance> tokens = new LinkedList<EntityInstance>();
		Character [] cruds    = new Character[]{CRUD.CREATE, CRUD.READ, CRUD.UPDATE, CRUD.DELETE}; 
		for(Character crud : cruds) {
			String tokenName     = buildTokenName(group, crud, isGrant);
			EntityInstance token = iecs.createEntityInstanceFor(Token_Info_Table.makeTokenInfoTableName());
			token.set(Token_Info_Table.getColumnTokenName(), tokenName);
			token.set(Token_Info_Table.getColumnTokenID(), tokenName);
			token.set(Token_Info_Table.getColumnGroup(), group);
			token.set(Token_Info_Table.getColumnCRUDType(), crud);
			token.set(Token_Info_Table.getColumnPermission(), isGrant);
			tokens.add(token);
		}
		return tokens;
	}
	
	/**
	 * The routine to build token name in this use case is  Group + CRUDtype + Permission
	 * @param group the group name
	 * @param crud the operation
	 * @param isGrant the permission
	 * @return token name
	 */
	private String buildTokenName(String group, Character crud, boolean isGrant){
		return group + "$" + crud + "$" +  (isGrant ? 'G' : 'D');
	}
		
}

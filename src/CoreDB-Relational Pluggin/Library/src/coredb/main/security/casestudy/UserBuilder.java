package coredb.main.security.casestudy;

import coredb.config.Configuration;
import coredb.config.Configuration.User_Info_Table;
import coredb.controller.IEntityControllerSecurity;
import coredb.mode.RootPermission;
import coredb.unit.EntityInstance;

public class UserBuilder {
	public static final String ADMIN        = RootPermission.ROOTUSER;	
	public static final String ADMINPASS    = RootPermission.ROOTUSERPASSWORD;	

	public static final String MANAGER      = "manager";
	public static final String MANAGERPASS  = "managerpass";

	public static final String CASHER       = "casher";
	public static final String CASHERPASS   = "casherpass";

	public static final String CUSTOMER     = "customer";
	public static final String CUSTOMERPASS = "customerpass";
	
	private IEntityControllerSecurity iecs;
	public UserBuilder(IEntityControllerSecurity iecs){	
		this.iecs = iecs;
		EntityInstance systemAdmin = iecs.readUser(RootPermission.ROOTUSER, RootPermission.ROOTUSERPASSWORD);
		EntityInstance bankManager =  iecs.createEntityInstanceFor(User_Info_Table.makeUserInfoTableName());
		EntityInstance bankCasher  =  iecs.createEntityInstanceFor(User_Info_Table.makeUserInfoTableName());
		EntityInstance bankVistor  =  iecs.createEntityInstanceFor(User_Info_Table.makeUserInfoTableName());
		
		bankManager.set(Configuration.User_Info_Table.getColumnUserID(), MANAGER); 
		bankManager.set(Configuration.User_Info_Table.getColumnUserName(),MANAGER);
		bankManager.set(Configuration.User_Info_Table.getColumnPassword(), MANAGERPASS);
		
		bankCasher.set(Configuration.User_Info_Table.getColumnUserID(), CASHER); 
		bankCasher.set(Configuration.User_Info_Table.getColumnUserName(),CASHER);
		bankCasher.set(Configuration.User_Info_Table.getColumnPassword(), CASHERPASS);
		
		bankVistor.set(Configuration.User_Info_Table.getColumnUserID(), CUSTOMER); 
		bankVistor.set(Configuration.User_Info_Table.getColumnUserName(),CUSTOMER);
		bankVistor.set(Configuration.User_Info_Table.getColumnPassword(), CUSTOMERPASS);
		
		System.out.println("Users admin, manager, chaser and customer are building...");
			
		iecs.createUser(systemAdmin, bankManager);
		iecs.createUser(systemAdmin, bankCasher);
		iecs.createUser(systemAdmin, bankVistor);

	}
	
	public EntityInstance getUserIntance(String userName, String userPassword){
		return iecs.readUser(userName, userPassword);
		
	}
}

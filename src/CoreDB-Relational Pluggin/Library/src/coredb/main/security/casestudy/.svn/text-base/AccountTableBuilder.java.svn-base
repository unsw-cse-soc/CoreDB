package coredb.main.security.casestudy;

import java.util.LinkedList;
import java.util.List;

import coredb.unit.AttributeClass;
import coredb.unit.EntitySecurityClass;

public class AccountTableBuilder extends CoredbTable{
	public static final String TABLE_NAME           = "account";		
	public static final String COLUMN_ID            = "id";
	public static final String COLUMN_ACCOUNTNUMBER = "accountnumber";
	public static final String COLUMN_PASSWORD      = "password";
	public static final String COLUMN_ACOUNTTYPE    = "acounttype";
	public static final String COLUMN_BALANCE       = "balance";
	
	public AccountTableBuilder(){
		AttributeClass id            = new AttributeClass(COLUMN_ID, Integer.class);
		id.setPrimaryKey(true);
		AttributeClass accountNumber = new AttributeClass(COLUMN_ACCOUNTNUMBER, String.class);
		AttributeClass password      = new AttributeClass(COLUMN_PASSWORD, String.class);
		AttributeClass acountType    = new AttributeClass(COLUMN_ACOUNTTYPE, String.class);
		AttributeClass balance       = new AttributeClass(COLUMN_BALANCE, Double.class);
		
		List<AttributeClass> attributes = new LinkedList<AttributeClass>();
		attributes.add(id);
		attributes.add(accountNumber);
		attributes.add(password);
		attributes.add(acountType);
		attributes.add(balance);
		System.out.println("The schema of account table is building...");
		tableSchema = new EntitySecurityClass(TABLE_NAME, attributes.toArray(new AttributeClass[]{}), null);
		
	}
		
}

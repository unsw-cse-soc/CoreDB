package coredb.main.security.casestudy;

import java.sql.Date;
import java.util.LinkedList;
import java.util.List;

import coredb.unit.AttributeClass;
import coredb.unit.EntitySecurityClass;

public class CustomerTableBuilder extends CoredbTable{
	public static final String TABLE_NAME         = "customer";		
	public static final String COLUMN_ID          = "id";
	public static final String COLUMN_NAME        = "name";
	public static final String COLUMN_DOB         = "dob";
	public static final String COLUMN_PASSPORT    = "passport";
	public static final String COLUMN_PHONE       = "phone";
	public static final String COLUMN_EMAIL       = "email";
	public static final String COLUMN_ISBLACKLIST = "isblacklist";
 
	public CustomerTableBuilder(){
		AttributeClass id          = new AttributeClass(COLUMN_ID, Integer.class); 
		id.setPrimaryKey(true);
		AttributeClass name        = new AttributeClass(COLUMN_NAME, String.class);
		AttributeClass dob         = new AttributeClass(COLUMN_DOB, Date.class);
		AttributeClass passport    = new AttributeClass(COLUMN_PASSPORT, String.class);
		AttributeClass phone       = new AttributeClass(COLUMN_PHONE, String.class);
		AttributeClass email       = new AttributeClass(COLUMN_EMAIL, String.class);
		AttributeClass isblacklist = new AttributeClass(COLUMN_ISBLACKLIST, Boolean.class);
		
		List<AttributeClass> attributes = new LinkedList<AttributeClass>();
		attributes.add(id);
		attributes.add(name);
		attributes.add(dob);
		attributes.add(passport);
		attributes.add(phone);
		attributes.add(email);
		attributes.add(isblacklist);
		
		System.out.println("The schema of customer table is building...");
		tableSchema = new EntitySecurityClass(TABLE_NAME, attributes.toArray(new AttributeClass[]{}), null);
		
	}
		
}

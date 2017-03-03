package coredb.main.security.casestudy;

import java.util.LinkedList;
import java.util.List;

import coredb.controller.IEntityControllerSecurity;
import coredb.unit.AttributeClass;
import coredb.unit.ClassToTables;
import coredb.unit.EntityClass;
import coredb.unit.EntityInstance;

public class CoredbTable {
	public EntityClass tableSchema;
	
	public AttributeClass[] getPrimaryAttributes(){
		List<AttributeClass> primaryAttribute = new LinkedList<AttributeClass>();
		for(AttributeClass attribute : tableSchema.getAttributeClassList()) {
			if(attribute.isPrimaryKey()) primaryAttribute.add(attribute);
		}
		return primaryAttribute.toArray(new AttributeClass[0]);
	}
	
	public AttributeClass getAttribute(String name){
		AttributeClass resultAttribute = null;
		for(AttributeClass attribute : tableSchema.getAttributeClassList()) {
			if(attribute.getName().equals(name)) resultAttribute = attribute;
		}
		return resultAttribute;
		
	}
	
	public EntityClass getTableSchema() {
		return tableSchema;
		
	}
	
	public boolean deployTableSchema(IEntityControllerSecurity iecs, List<EntityInstance> userTokens) {
		List<ClassToTables> cttls = new LinkedList<ClassToTables>();
		cttls.add(getTableSchema());
		return iecs.deployDefinitionListWithSecurity(userTokens, cttls, true, true);
		
	}
		
}
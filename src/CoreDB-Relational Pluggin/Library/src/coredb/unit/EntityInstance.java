package coredb.unit;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;

import coredb.common.UnreadableValue;
import coredb.config.Configuration;
import coredb.config.Configuration.R;
import coredb.database.DatabaseConnection;
import coredb.database.JDBCTypeConverter;
import coredb.database.SchemaInfo;
import coredb.sql.CompoundStatementAuto;
import coredb.sql.ConditionalStatement;
import coredb.sql.Q;

import coredb.utils.Helper;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.LazyDynaClass;
import org.apache.ddlutils.dynabean.SqlDynaBean;
import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.model.Table;

/**
 * EntityInstance, that relate to a data row of tables in database
 * 
 * @author sean
 *
 */
// checked by vmc @689
public class EntityInstance extends SqlDynaBean {

	private static final long serialVersionUID = -1414235692487384628L;

	/**
	 * Construct a new entityinstance
	 * @param dynaClass is the a base class that every entity instance derived from.
	 */
	// checked by vmc @689
	public EntityInstance(DynaClass dynaClass) {
		super(dynaClass);
	}

	// checked by vmc @689
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EntityInstance) {
			EntityInstance right = (EntityInstance) obj;
			DynaProperty[] leftProperties  = this.getDynaClass().getDynaProperties();
			DynaProperty[] rightProperties = right.getDynaClass().getDynaProperties();

			if (leftProperties.length != rightProperties.length) return false;

			HashMap<String, Object> map = new LinkedHashMap<String, Object>();
			for (DynaProperty property : leftProperties) 
				map.put(property.getName(), this.get(property.getName()));

			for (DynaProperty rightProperty : rightProperties) {
				String name = rightProperty.getName();
				
				//vmc for debug:
				//System.err.printf("%30s =?= %30s\n",map.get(name).toString(),right.get(name).toString());
//				if ((map.get(name) != null && right.get(name) != null && !map.get(name).toString().equals(right.get(name).toString()))
//					|| (map.get(name) == null && right.get(name) != null) || (map.get(name) != null && right.get(name) == null)) 
//					return false;
				
				// temporary used
				Object leftObject  = this.get(name);
				Object rightObject = right.get(name);
				String tableName   = this.getDynaClass().getName();
				if((leftObject == null && rightObject == null) || (leftObject != null && rightObject != null && leftObject.equals(rightObject))) continue;
				else if (JDBCTypeConverter.toJAVAType(SchemaInfo.refactorColumnType(tableName, name)) == byte[].class 
            			&& Helper.ByteArraysEqual((byte[])leftObject, (byte[])rightObject)) continue;
            	else return false;
				// temporary used
			}
			return true;
		}
		return false;
	}
	
	/**
	 * This method compare tow EntityInstance based on PrimaryKey columns
	 * @param databaseConnection the current working DB Connection
	 * @param entity the given EntityInstance need to compare
	 * @return returns true if these tow EntityInstances matched 
	 */
	public boolean equalsOnPrimaryColumns(DatabaseConnection databaseConnection, EntityInstance entity) {

		return equalsOnListOfColumns(databaseConnection, entity, this.getIdentifiableColumns(databaseConnection));
	}
	
	/**
	 * This method compare tow EntityInstance based on a list of given columns
	 * @param databaseConnection the current working DB Connection
	 * @param entity the given EntityInstance need to compare
	 * @param attributeClasses the given list of columns
	 * @return returns true if these tow EntityInstances matched 
	 */
	public boolean equalsOnListOfColumns(DatabaseConnection databaseConnection, EntityInstance entity, AttributeClass[] attributeClasses) {

		if(isSameEntityClass(this, entity)) {
			/**
			 *  cast EntityInstance objects to SqlDynaBeans. 
			 *  Then using get(name) to obtain the object can keep them in DB type
			 */
			String tableName     = this.getDynaClass().getName();
			SqlDynaBean thisBean = this;
			SqlDynaBean dynaBean = entity;
			
            for(AttributeClass ac : attributeClasses) {
            	Object leftObject = thisBean.get(ac.getName());
            	Object rightObject = dynaBean.get(ac.getName());
            	
            	if((leftObject == null && rightObject == null) || (leftObject != null && rightObject != null && leftObject.equals(rightObject))) continue;
            	else if (JDBCTypeConverter.toJAVAType(SchemaInfo.refactorColumnType(tableName, ac.getName())) == byte[].class 
            			&& Helper.ByteArraysEqual((byte[])leftObject, (byte[])rightObject)) continue;
            	else return false;
            }
			return true;
		} else return false;
	}

	/**
	 * compare the structures of two entity instance
	 * @param left the left entity instance
	 * @param right the right entity instance
	 * @return true is the two entity instance have same structure, otherwise returns false
	 */
	// checked by vmc @689
	public static boolean isSameEntityClass(EntityInstance left, EntityInstance right) {
		DynaProperty[] leftProperties = left.getDynaClass().getDynaProperties();
		DynaProperty[] rightProperties = right.getDynaClass().getDynaProperties();

		if (leftProperties.length != rightProperties.length) {
			return false;
		}

		HashMap<String, Object> map = new LinkedHashMap<String, Object>();

		for (DynaProperty property : leftProperties) {
			map.put(property.getName(), property);
		}

		for (DynaProperty rightProperty : rightProperties) {
			String name = rightProperty.getName();
			if (map.get(name) == null || !(map.get(name).getClass().equals(rightProperty.getClass()))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * This method retrieve all the Attribute Names of EntityInstance
	 * @return all the Attribute Names of EntityInstance
	 */
	public List<String> getAttributeNames(){
		List<String> attributeNames = new LinkedList<String>();
		
		for(DynaProperty property : dynaClass.getDynaProperties()){
			attributeNames.add(property.getName());
		}
		
		return attributeNames;
	}
	
	/**
	 * Set the content of entity instance
	 * @param name is the name of the attribute.
	 * @param value is the value of the attribute.
	 */
	// checked by vmc @689
	@Override
	public void set(String name, Object value) {
		if(!dynaClass.getName().endsWith(Configuration.LAZYDYNACLASS)){
			super.set(SchemaInfo.refactorColumnName(dynaClass.getName(), name), value);
		}else{
			super.set(name, value);
		}
	}
	
	/**
	 * Get the value of a attribute from entityinstance
	 * @param name the attribute name
	 * @return Object is the object mapped by name in the entity instance.
	 */
	// checked by vmc @689
	@Override
	public Object get(String name) {
//		/*
		if(!dynaClass.getName().endsWith(Configuration.LAZYDYNACLASS)){
			return super.get(SchemaInfo.refactorColumnName(dynaClass.getName(), name));
		}else {
			return super.get(name);
		}
//		 */
 /*
		Object value = super.get(name);
		if (value != null) {
			return value;
		} else {
			return super.get(SchemaInfo.refactorColumnName(dynaClass.getName(), name));
		}
// */
	}

	@Override
	public int hashCode() {
		int hash = 0;
		
		DynaProperty[] properties  = this.getDynaClass().getDynaProperties();
		for (DynaProperty property : properties) {
			int code = 0;
			String name = property.getName();
			Object obj  = this.get(name);
			if(obj == null)                         code = 0;
			else if (obj instanceof Boolean)        code = (Boolean)obj?0:1;
			else if (obj instanceof Character)      code = ((Character) obj).hashCode();
			else if (obj instanceof Integer)        code = (Integer) obj;
			else if (obj instanceof String)         code = hashCodeString((String) obj);
			else if (obj instanceof Double) {
				Long codeValue = Double.doubleToLongBits((Double)obj);
				code           = (int) (codeValue ^ (codeValue >>> 32));
			}
			else if (obj instanceof UnreadableValue) code = ((UnreadableValue) obj).hashcode();
			else if (obj instanceof byte[])          code = hashCodeBytea((byte[])obj);
			else                                     code = obj.hashCode();
			
			hash = 37 * hash + code;
		}
		//System.out.println("hashcode : " + hash);
		return hash;
	}

	/**
	 * simplified version of how String.hashCode works.
	 * For the actual version see src.jar java/lang/String.java
	 * @return a hash code value for this String.
	 */
	public int hashCodeString(String str) {
	    // inside the String is a char[] val that holds the characters.
	    int hash = 0;
	    
	    char[] val = str.toCharArray();
	    int len = val.length;
	    for ( int i=0; i<len; i++ ) {
	       hash = 31 * hash + val[ i ];
	    }
	    
	    return hash;
    }
	
	/**
	 * Java version of the assembler hashCode used in the BBL Forth compiler.
	 * Because of the 1-bit shift you don't lose data completely on long keys.
	 * The shift breaks up patterns in the data.
	 * @param val
	 * @return a hash code value for the byte[] val byte array in this object.
	 */
	public int hashCodeBytea(byte[] val) {
	   //  byte[] val  holds the data.
	   int hash = 0;
	   int len = val.length;
	   for ( int i=0; i<len; i++ ) {
		   // rotate left and xor (very fast in assembler, a bit clumsy to specify in Java, but a smart compiler might collapse it to two machine instructions)
	       hash <<= 1;
	       if ( hash < 0 ) {
	           hash |= 1;
	       }
	       hash ^= val[ i ];
	    }
	    return hash;
	}
	
	@Override
	public EntityInstance clone() {
		EntityInstance clone = new EntityInstance(getDynaClass());
		for(DynaProperty property : dynaClass.getDynaProperties()){
			String name  = property.getName();
			Object value = this.get(name);
			clone.set(name,value);
		}
		return clone;
	}

	/**
	 * This method copies EntityInstance content 
	 * 
	 * @param from the source EntityInstance
	 * @param to the destination EntityInstance
	 * @return the new to EntityInstance with the copied content
	 */
	public static EntityInstance copy(EntityInstance from, EntityInstance to) {
		for(DynaProperty property : from.getDynaClass().getDynaProperties()){
			String name  = property.getName();
			Object value = from.get(name);
			to.set(name,value);
		}
		return to;
	}

	/**
	 * This method append a list of DynaProperties to current Entity
	 * @param entity the entity need to extend
	 * @param tableName
	 * @param properties the list of DynaProperties will be appended
	 * @return returns a new Entity that is the combination of the old entity and a list of DynaProperties
	 */
    public static EntityInstance appendColumns(EntityInstance entity, String tableName, DynaProperty[] properties) {
		
		List<DynaProperty> newDynaProperties = new LinkedList<DynaProperty>();
		for(DynaProperty property : entity.getDynaClass().getDynaProperties()){
			newDynaProperties.add(property);
		}
		for(DynaProperty property : properties){
			newDynaProperties.add(property);
		}
		
		DynaClass dynaClass = new LazyDynaClass(tableName, newDynaProperties.toArray(new DynaProperty[0]));
		EntityInstance newEntityInstance = new EntityInstance(dynaClass);

		for(DynaProperty property : entity.getDynaClass().getDynaProperties()){
			String name  = property.getName();
			Object value = entity.get(name);
			newEntityInstance.set(name,value);
		}

		return newEntityInstance;
	}

    public String makeDeleteSqlStatementForDeletedEntityInstance(DatabaseConnection databaseConnection) {
		String deletedTableName = Configuration.Delete_Table.makeDeteledTableName(this.getDynaClass().getName());
		return Q.DELETE + Q.FROM + deletedTableName + Q.WHERE +makeSQLOnIdentifiableAttributes(databaseConnection);
	}
/*
	public String makeDeleteSqlStatementForMappingEntityInstance(DatabaseConnection databaseConnection) {
		String mappingTableName = Configuration.Mapping_Table.makeMappingTableName(this.getDynaClass().getName());
		return Q.DELETE + Q.FROM + mappingTableName + Q.WHERE + makeSQLOnIdentifiableAttributes(databaseConnection);
	}
 */

	public String makeSelectSqlStatementForMappingEntityInstance(DatabaseConnection databaseConnection) {
		String mappingTableName = Configuration.Mapping_Table.makeMappingTableName(this.getDynaClass().getName());
		return Q.SELECT + Q.STAR + Q.FROM + mappingTableName + Q.WHERE + makeSQLOnIdentifiableAttributes(databaseConnection);
	}

	// getPrimaryKeys or All key 
	public AttributeClass[] getIdentifiableColumns(DatabaseConnection databaseConnection) {
		List<AttributeClass> attributeClasses = new LinkedList<AttributeClass>();
		
		String tableName = dynaClass.getName();
		Table table	= databaseConnection.getTable(tableName, false);
		for(Column column : table.getColumns()){
			if(!table.hasPrimaryKey() || (table.hasPrimaryKey() && column.isPrimaryKey()))
				attributeClasses.add(new AttributeClass(column.getName(),JDBCTypeConverter.toJAVAType(SchemaInfo.refactorColumnType(tableName, column.getName()))));
		}
		
		return attributeClasses.toArray(new AttributeClass[0]);
	}
	
	// getPrimaryKeys or All key column name
	public List<String> getIdentifiableColumnNames(DatabaseConnection databaseConnection) {
		AttributeClass[] identifiableColumns = getIdentifiableColumns(databaseConnection);
		List<String> identifiableColumnNames = new LinkedList<String>();    
		for (AttributeClass ac : identifiableColumns) {
			
			identifiableColumnNames.add(ac.getName());
		}
		
		return identifiableColumnNames;
	}

	/**
	 * This method gets select sql statement on PK columns 
	 * 
	 * @param databaseConnection the current DB connection
	 * @return return select sql statement on PK columns 
	 */
	// checked by vmc @987
	public String makeSQLOnIdentifiableAttributes(DatabaseConnection databaseConnection) {
		CompoundStatementAuto statement = new CompoundStatementAuto(Q.AND);

		String tableName = dynaClass.getName();
		Table table	= databaseConnection.getTable(tableName, false);
		for(Column column : table.getColumns()){
			if(!table.hasPrimaryKey() || (table.hasPrimaryKey() && column.isPrimaryKey())) {
				AttributeClass ac = new AttributeClass(column.getName(),JDBCTypeConverter.toJAVAType(SchemaInfo.refactorColumnType(tableName, column.getName())));
				statement.addCompoundStatement(new ConditionalStatement(ac,Q.E,this.get(ac.getName())));
			}
		}
		return statement.toSQLStatement();
	}

	/**
	 * This method gets select sql statement on non-PK columns 
	 *
	 * @param databaseConnection
	 * @return return select sql statement on non-PK columns 
	 */
	// checked by vmc @1651
	public String makeSQLOnNonPrimaryWithEqual(DatabaseConnection databaseConnection) {
		String tableName = dynaClass.getName();
		Table table	= databaseConnection.getTable(tableName, false);
		List<String[]> columnNameAndOperatorPairs = new LinkedList<String[]>();
		for (Column column : table.getColumns()) {
			if(!column.isPrimaryKey()) {
				columnNameAndOperatorPairs.add(new String[]{column.getName(), Q.E});
			}
		}
		return this.makeSQLOnColumns(databaseConnection, columnNameAndOperatorPairs);
	}

	/**
	 * This method gets select sql statement on the given column 
	 *
	 * @param databaseConnection
	 * @param columnName
	 * @param operator
	 * @return return select sql statement on the given column 
	 */
	// checked by vmc @1651
	public String makeSQLOnColumn(DatabaseConnection databaseConnection, String columnName, String operator) {
		String refractedColumnName = R.refractor(columnName);
		AttributeClass ac = new AttributeClass(
			refractedColumnName,
			JDBCTypeConverter.toJAVAType(SchemaInfo.refactorColumnType(this.getDynaClass().getName(), refractedColumnName)));
		return new ConditionalStatement(ac,operator,this.get(columnName)).toSQLStatement();
	}

	/**
	 * This method gets select sql statement on the given list of columns 
	 *
	 * @param databseConnection
	 * @param columnNameAndOperatorPairs
	 * @return return select sql statement on the given list of columns 
	 */
	// checked by vmc @1651
	public String makeSQLOnColumns(DatabaseConnection databseConnection, List<String[]> columnNameAndOperatorPairs) {
		CompoundStatementAuto SQL = new CompoundStatementAuto(Q.AND);
		for (Iterator<String[]> i = columnNameAndOperatorPairs.iterator(); i.hasNext(); ) {
			String[] columnNameAndOperator = i.next();
			String sql = this.makeSQLOnColumn(databseConnection, columnNameAndOperator[0], columnNameAndOperator[1]);
			SQL.addCompoundStatement(sql);
		}
		return SQL.toSQLStatement();
	}

}

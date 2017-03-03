/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.sql;

import org.apache.ddlutils.model.TypeMap;

import coredb.unit.AttributeClass;

/**
 *
 * @author vmc
 */
public class ConditionalStatement implements SQLStatement {
    private AttributeClass attributeClass = null;
    private String operation = null;
    private String value = null;

	/**
	 * Create a ConditionalStatement. This method creates a
     * conditional statement for SQL query
	 *
	 * @param tableName
	 * @param attributeName
	 * @param operation
	 * @param value
	 */
	/*
	public ConditionalStatement(String tableName, String attributeName, String operation, Object value) {
		this.attributeClass = PublicFunctions.getAttributeClass(tableName, attributeName);
		this.operation = operation;
        this.value  = (value == null ? null: value.toString());
	}
	 */

	/**
     * Create a ConditionalStatement. This method creates a
     * conditional statement for SQL query
     *
     * @param attributeClass Attribute in the where clause
     * @param operation the Operation in the where clause
     * @param value the search value in the where clause
     */
    public ConditionalStatement(AttributeClass attributeClass, String operation, Object value) {
        this.attributeClass = attributeClass;
        this.operation = operation;
        this.value  = (value == null ? null: value.toString());
    }

	/**
	 *
	 * @return attributeClass
	 */
	public AttributeClass getAttributeClass() {
		return attributeClass;
	}

	/**
	 *
	 * @return operation
	 */
	public String getOperation() {
		return operation;
	}

	/**
	 *
	 * @return value
	 */
	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return toSQLStatement();
	}

    /**
     * Override the method to print out sql statement
     * @return An SQL conditional statement.
     */
    public String toSQLStatement() {
        /**
         * Stephen: Please implement this function.
         * example return fistName == 'value' example
         */
    	String type  = ( this.attributeClass == null ? null : this.attributeClass.getSqlType());
		String op    = this.operation;
    	if(type != null){
    		if(type.equals(TypeMap.BIT) || type.equals(TypeMap.NULL)){
	       		 if(operation.equals(Q.E)){
	       			 op = Q.IS;
	       		 } else if(operation.equals(Q.NE)){
	       			 op = Q.ISNOT;
	       		 } else if(operation.equals(Q.IS)){
	       			 op = Q.IS;
	       		 } else if(operation.equals(Q.ISNOT)){
	       			 op = Q.ISNOT;
	       		 } else{
					 printExceptionTrack(op, "LOGICAL ERROR.");
					 return null;
				 }
    		} 
			return this.attributeClass.getName()+" "+op+" "+formatValue(type);
    	} else{
    		printExceptionTrack("Type", "IS NULL");
			return null;
    	}
    }

	public void clear() {
		this.attributeClass	= null;
		this.operation		= null;
		this.value			= null;
	}

    private String formatValue(String type){
    	int typeCode = TypeMap.getJdbcTypeCode(type);
    	if(TypeMap.isTextType(typeCode) || TypeMap.isDateTimeType(typeCode) ){
    		return " '" + this.value + "' ";
    	}else if(TypeMap.isNumericType(typeCode) || TypeMap.isBinaryType(typeCode)){
    		if(this.operation.equals(Q.LIKE)){
    			printExceptionTrack( Q.LIKE, " IS NOT AVAILABLE FOR THE TYPE " + type);
    			return null;
    		}else{
    			return this.value;
    		}  		
    	}else if( TypeMap.isSpecialType(typeCode) ){
    		printExceptionTrack(type, " IS NOT SUPPORTED IN COREDB, PLEASE CONTACT COREDB TEAM.");
    		return null;
    	}else{
    		printExceptionTrack(type, " IS A INVALID JDBC SQL TYPE.");
    		return null;
    	}
    }

    private void printExceptionTrack(String object, String message){
		System.err.printf("COREDB ERROR: %20s %20s %20s\n",ConditionalStatement.class.toString(),object,message);
		try {throw new Exception();} catch (Exception e) {e.printStackTrace();}
    }
}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.unit;

import org.apache.commons.beanutils.DynaProperty;
import coredb.database.JDBCTypeConverter;

/**
 * The attribute class conserves the column information. 
 * @author vmc
 */

// checked by vmc @722
public class AttributeClass extends DynaProperty {
	private static final long serialVersionUID = 1L;

	private String  sqlType;
	private boolean isPrimaryKey;
	private boolean indexed = false;
	private int size = 0;

	// checked by vmc @473
	@Override
	public boolean isIndexed() {
		return indexed;
	}

	// checked by vmc @473
	public void setIndexed(boolean indexed) {
		this.indexed = indexed;
	}
	private boolean isAutoIncrement = false;

    /**
	 * Display whether the current attribute is auto increment or not
     * @return isAutoIncrement  if the attribute is auto increment, return true; otherwise, return false
     */
	// checked by vmc @473
	public boolean isAutoIncrement() {
		return isAutoIncrement;
	}

    /**
	 * Set whether the current attribute is auto increment or not
     * @param isAutoIncrement is auto increment or not
     */
	// checked by vmc @473
	public void setAutoIncrement(boolean isAutoIncrement) {
		this.isAutoIncrement = isAutoIncrement;
	}

    /**
	 * Display whether the current attribute is primary key or not
     * @return isPrimaryKey  if the attribute is primary key, return true; otherwise, return false
     */
	// checked by vmc @473
	public boolean isPrimaryKey() {
		return isPrimaryKey;
	}

    /**
	 * Set whether the current attribute is primary key or not
     * @param isPrimaryKey is primary key or not
     */
	// checked by vmc @473
	public void setPrimaryKey(boolean isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}

	/**
	 * Set the column size
     * @param size the size of column
     */
	public void setSize(Integer size) {
		this.size = size;
	}
	
	/**
	 * get the column size
     */
	public int getSize() {
		return size;
	}
	
    /**
     * Create an attribute class
     * 
     * @param name attribute name
     * @param type attribute type
     */
	// checked by vmc @473
	@SuppressWarnings("unchecked")
	public AttributeClass(String name, Class type) {
		this(name,type,JDBCTypeConverter.toSQLType(type), false);
    }

	/**
     * Create an attribute class
     *
     * @param name attribute name
     * @param type attribute type
     * @param size the size of column
     */
	public AttributeClass(String name, Class<? extends Object> type, Integer size) {
		this(name,type,JDBCTypeConverter.toSQLType(type));
		this.size = size;
	}
	
    /**
     * Create an attribute class
     * 
     * @param name attribute name
     * @param type attribute type
     * @param sqlType sql type of attribute
     */
	// checked by vmc @473
	@SuppressWarnings("unchecked")
	public AttributeClass(String name, Class type, String sqlType) {
		this(name,type,sqlType, false);
	}

	/**
     * Create an attribute class
     *
     * @param name attribute name
     * @param type attribute type
     * @param isindexed identify this attribute is index or not
     * @deprecated this version of constructor will no longer support in the future
     */
	// checked by vmc @473
	public AttributeClass(String name, Class<? extends Object> type, boolean isindexed) {
		this(name,type,JDBCTypeConverter.toSQLType(type), isindexed);
	}

	/**
     * Create an attribute class
     *
     * @param name attribute name
     * @param type attribute type
     * @param isindexed identify this attribute is index or not
     * @param size the size of column
     * @deprecated this version of constructor will no longer support in the future
     */
	public AttributeClass(String name, Class<? extends Object> type, boolean isindexed, Integer size) {
		this(name,type,JDBCTypeConverter.toSQLType(type), isindexed);
		this.size = size;
	}
	
	 /**
     * Create an attribute class
     * 
     * @param name attribute name
     * @param type attribute type
     * @param sqlType sql type of attribute
     * @param isindexed identify this attribute is index or not
     * @deprecated this version of constructor will no longer support in the future
     */
	// checked by vmc @473
	@SuppressWarnings("unchecked")
	public AttributeClass(String name, Class type, String sqlType, boolean isindexed) {
		super(name,type);
		this.sqlType = sqlType;
		this.indexed = isindexed;
	}
	
    /**
     * Get the sql type associated with the current attribute
     * @return sql type of an attribute
     */
	// checked by vmc @473
	public String getSqlType() { return sqlType; }

	/**
	 * Describe AttributeClass
	 * @param indent
	 */
	// checked by vmc @722
	public void describe(int indent) {
		System.out.printf("%"+indent+"s %20s, %10s , %10s, %10s\n", " ", super.name, sqlType, isPrimaryKey, indexed);
	}

}
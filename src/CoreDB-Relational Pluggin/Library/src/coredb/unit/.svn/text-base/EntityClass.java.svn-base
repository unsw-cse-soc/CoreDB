/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.unit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.model.Index;
import org.apache.ddlutils.model.IndexColumn;
import org.apache.ddlutils.model.IndexImpBase;
import org.apache.ddlutils.model.Table;

import coredb.config.Configuration;


/**
 * The general entityclass which can define the table schema
 * @author vmc
 */
// checked by vmc @720
public class EntityClass implements ClassToTables {
	protected String name;
	protected AttributeClass[] attributeClassList = null;
	protected IndexClass[] indexClassList = null;

	/**
	 * Create a EntityClass. This method creates a EntityClass (the definition of data table)
	 * with given value of properties
	 * 
	 * @param name EntityClass name
	 * @param attributeClassList the list of attributes EntityClass has
	 * @param indexClassList the list of indexes EntityClass has
	 */
	// checked by vmc @720
	public EntityClass(String name, AttributeClass[] attributeClassList, IndexClass[] indexClassList) {
		//this.name = ConfigurationValues.patchName(name);
        //this.name = SchemaInfo.refactorTableName(name);
		boolean hasPK = false;
        for(AttributeClass ac : attributeClassList) {
			if(ac.isPrimaryKey()){hasPK = true; break;}
		}
        if(hasPK == false){
			try {
				throw new Exception("Please set a PK includes at least one column!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        this.name = name;
		this.attributeClassList = (attributeClassList == null ? new AttributeClass[0] : attributeClassList);
		this.indexClassList = (indexClassList == null ? new IndexClass[0] : indexClassList);
	}
	
    /**
     * Add a list of attributes to EntityClass.
     * This method adds a list of columns into data table
     * 
     * @param attributeClassList the list of attributes need added to Table
     * @param table 
     * @return table that all attributes have been added
     */
	// checked by vmc @720
    protected Table addColumnsToTable(AttributeClass[] attributeClassList, Table table) {
        for (AttributeClass attributeClass : attributeClassList) {
			Column column = new Column();
			column.setName(attributeClass.getName());
			column.setType(attributeClass.getSqlType());
			column.setPrimaryKey(attributeClass.isPrimaryKey());
			column.setAutoIncrement(attributeClass.isAutoIncrement());
			if(attributeClass.getSize() != 0) {
				column.setSize(String.valueOf(attributeClass.getSize()));
			}
			table.addColumn(column);
            
			if (attributeClass.isIndexed() == true) {
				Index index = makeIndexPerColumn(false);
//				index.setName(column.getName());
				index.setName(this.name + Configuration.TABLE_DELIMITER + column.getName());

				IndexColumn indexColumn = new IndexColumn(column);
				index.addColumn(indexColumn);
				table.addIndex(index);
			}
			
		}
        return table;
    }

    /**
     * Add the list of attributes to Table
     * 
     * @param indexClassList the list of indexes need added to Table
     * @param table the table needed to add index.
     * @return table that all indexes have been added
     */
	// checked by vmc @720
    protected Table addIndexesToTable(IndexClass[] indexClassList, Table table) {
        for (IndexClass indexClass : indexClassList) {
			Index index = makeIndexPerColumn(indexClass.isUnique());
			
			String indexName = null;
			for (AttributeClass attributeClass : indexClass.getAttributeClassList()) {
				Column column = table.findColumn(attributeClass.getName());
				IndexColumn indexColumn = new IndexColumn(column);
				index.addColumn(indexColumn);
				// indexName = ci.Name_ci+1.Name .....
				indexName = (indexName == null ? column.getName() : indexName + Configuration.TABLE_DELIMITER + column.getName()) ;
			}
			
			indexName = (indexClass.getName() == null ? table.getName()+ Configuration.TABLE_DELIMITER + indexName : table.getName() + Configuration.TABLE_DELIMITER + indexClass.getName()) ;
			index.setName(indexName);
			
			table.addIndex(index);
		}
        return table;
    }

    /**
     * Create tables according to the current entityclass.
     * @return a list of table
     */
	// checked by vmc @720
    public List<Table> toTables() {
        List<Table> tables = new LinkedList<Table>();
        tables.add(this.toTable());
        return tables;
    }

    /**
     * Create a table according to the current entityclass
     * @return return table .
     */
	// checked by vmc @720
	public Table toTable() {
		Table table = new Table();
		table.setName(name);
        table = this.addColumnsToTable(attributeClassList, table);
        table = this.addIndexesToTable(indexClassList, table);
		return table;
	}

	/**
	 * store each index column into a index, and add it into table
	 * @param isUnique
	 * @return
	 */
	// checked by vmc @720
	protected Index makeIndexPerColumn(final boolean isUnique) {
		Index index = new IndexImpBase() {

			private static final long serialVersionUID = 1L;

			public String toVerboseString() {
				// TODO Auto-generated method stub
				return null;
			}
			
			public boolean isUnique() {
				// TODO Auto-generated method stub
				return isUnique;
			}
			
			public boolean equalsIgnoreCase(Index arg0) {
				// TODO Auto-generated method stub
				return true;
			}
			
			@Override
			public Object clone() throws CloneNotSupportedException {
				// TODO Auto-generated method stub
				return null;
			}
		};
		return index;
	}

	/**
	 * Get the attributeclass list
	 * @return  an array of attributes belong to entityclass
	 */
	// checked by vmc @720
	public AttributeClass[] getAttributeClassList() {
		return attributeClassList;
	}

    /**
     * Set the attributeclass list
     * @param attributeClassList  an array of attributes will be seted to entityclass
     */
	// checked by vmc @720
	public void setAttributeClassList(AttributeClass[] attributeClassList) {
		this.attributeClassList = attributeClassList;
	}

    /**
     * Get the list of index in the current entityclass
     * @return an array of Indexes belong to EntityClass
     */
	// checked by vmc @720
	public IndexClass[] getIndexClassList() {
		return indexClassList;
	}

    /**
     * Insert a list of index into the current entityclass
     * @param indexClassList an array of Indexes belong to EntityClass
     */
	// checked by vmc @720
	public void setIndexClassList(IndexClass[] indexClassList) {
		this.indexClassList = indexClassList;
	}

    /**
     * Set entityclass name
     * @param name
     */
	// checked by vmc @720
	public void setName(String name) { this.name = name; }

	/**
	 * Get entityclass name
	 * @return the name of EntityClass
	 */
	// checked by vmc @720
	public String getName() { return name; }

    /**
     * Create the subsidiary tables of the current entity class. This will be called in the security model
     * @param tblName
     * @param cols
     * @return table
     */
	// checked by vmc @720
    protected Table toSubsidiaryTable(String tblName, ArrayList<Column> cols){
	    Table table = new Table();
	    table.setName(tblName);
	    table.addColumns(cols);
	    return table;
	}

    /**
     * Create a column 
     * @param colName	name of column
     * @param dataType  sql type 
     * @return column
     */
	// checked by vmc @720
	protected Column toColumn(String colName, String dataType){
		Column column = new Column();
		column.setName(colName);
		column.setType(dataType);
		return column;
	}

	// checked by vmc @720
	public void describe(int indent) {
		int nextIndent = indent+4;
		System.out.printf("%"+nextIndent+"s TABLE_NAME %20s \n", " ", name);

		System.out.printf("%"+nextIndent+"s AttributeClass\n"," ");
		for (AttributeClass ac : attributeClassList)  {
			ac.describe(nextIndent);
		}
		System.out.printf("%"+nextIndent+"s /AttributeClass\n"," ");

		System.out.printf("%"+nextIndent+"s IndexClass\n"," ");
		for (IndexClass ic : indexClassList)  {
			ic.describe(nextIndent);
		}
		System.out.printf("%"+nextIndent+"s /IndexClass\n"," ");
	}
}

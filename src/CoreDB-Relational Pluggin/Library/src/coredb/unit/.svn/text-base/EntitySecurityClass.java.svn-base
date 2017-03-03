/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.unit;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.model.Index;
import org.apache.ddlutils.model.IndexColumn;
import org.apache.ddlutils.model.Table;
import org.apache.ddlutils.model.TypeMap;
import coredb.config.Configuration;
import coredb.config.Configuration.Tracing_Table;

/**
 * The entityclass which can setup a row security
 * @author vmc
 */
public class EntitySecurityClass extends EntityClass implements ClassToTables {
	
	/**
	 * Create a entityclass with security
	 * 
	 * @param name EntityClass name
	 * @param attributeClassList The list of attributes EntityClass has
	 * @param indexClassList The list of indexes EntityClass has
	 */
	public EntitySecurityClass(String name, AttributeClass[] attributeClassList, IndexClass[] indexClassList) {
		super(name,attributeClassList,indexClassList);
	}

	/**
     * Create the tables according to the current entityclass. If the security is available,
     * the row security tables will also be generated with it.
     * @return the list of tables 
     */
    @Override
	public List<Table> toTables() {    	
		List<Table> tables = new ArrayList<Table>();
		tables.add(super.toTable());
		tables.add(generateMappingTable());
		tables.addAll(generateRowSecurityTables());
		return tables;
	}

	/**
	 *
	 * @return A mapping table for tracing.
	 */
	// checked by vmc @914 (could have error not as good as the tracing)
	private Table generateMappingTable() {
		String rowSecurityMappingTableName                   = Tracing_Table.makeMappingTableNameWithPrefixSuffix(this.getName());
		String rowSecurityMappingTableCoredbId               = Tracing_Table.makeCoredbColumnNameId(rowSecurityMappingTableName);

		AttributeClass rowSecurityMappingTableCoredbIdColumn = new AttributeClass(rowSecurityMappingTableCoredbId, String.class);
		rowSecurityMappingTableCoredbIdColumn.setPrimaryKey(true);

		List<AttributeClass> acl = new LinkedList<AttributeClass>();
		acl.add(rowSecurityMappingTableCoredbIdColumn);
		/** COPY THE PRIMARY KEYS FROM THE BASE TABLE	*/
	    for(AttributeClass ac : super.getAttributeClassList()) if (ac.isPrimaryKey()) acl.add(ac);

        EntityClass ec = new EntityClass(rowSecurityMappingTableName, acl.toArray(new AttributeClass[0]), null);
		return ec.toTable();
	}

	/**
	 *
	 * @return
	 */
	// checked by vmc @914
	private List<Table> generateRowSecurityTables(){
		List<Table> tables                     = new ArrayList<Table>();
        String index_RSG = "INDEX_G"+ UUID.randomUUID().toString().replace('-', '_');

        String index_RSD = "INDEX_D"+ UUID.randomUUID().toString().replace('-', '_');
		
		ArrayList<Column> rowGrantSecurityCols = new ArrayList<Column>();	
		String rowGrantSecurityCoreDbId        = Configuration.Row_Security_Grant_Table.makeRowSecurityGrantTableColumnNameCoredbId(super.getName());
		Column rowGrantSecurityCoredbIdColumn  = this.toColumn(rowGrantSecurityCoreDbId, TypeMap.INTEGER);
		rowGrantSecurityCoredbIdColumn.setPrimaryKey(true);
		rowGrantSecurityCoredbIdColumn.setAutoIncrement(true);
		rowGrantSecurityCols.add(rowGrantSecurityCoredbIdColumn);
        Column column_RSG_EntityInstanceId     = this.toColumn(Configuration.Row_Security_Grant_Table.COLUMN_ENTITYINSTANCEID, TypeMap.VARCHAR);
        Column column_RSG_TokenId              = this.toColumn(Configuration.Row_Security_Grant_Table.COLUMN_TOKENID, TypeMap.VARCHAR);
		rowGrantSecurityCols.add(column_RSG_EntityInstanceId);
		rowGrantSecurityCols.add(column_RSG_TokenId);
        Table table_RSG = this.toSubsidiaryTable(Configuration.Row_Security_Grant_Table.makeRowSecurityGrantTableNameWithPrefixSuffix(super.getName()), rowGrantSecurityCols);
		table_RSG.addIndex(this.createIndexe(new Column[]{column_RSG_EntityInstanceId, column_RSG_TokenId}, index_RSG, true));
        tables.add(table_RSG);

		ArrayList<Column> rowDenySecurityCols  = new ArrayList<Column>();	
		String rowDenySecurityCoreDbId         = Configuration.Row_Security_Deny_Table.makeRowSecurityDenyTableColumnNameCoredbId(super.getName());
		Column rowDenySecurityCoredbIdColumn   = this.toColumn(rowDenySecurityCoreDbId, TypeMap.INTEGER);
		rowDenySecurityCoredbIdColumn.setPrimaryKey(true);
		rowDenySecurityCoredbIdColumn.setAutoIncrement(true);
		rowDenySecurityCols.add(rowDenySecurityCoredbIdColumn);
        Column column_RSD_EntityInstanceid    = this.toColumn(Configuration.Row_Security_Deny_Table.COLUMN_ENTITYINSTANCEID, TypeMap.VARCHAR);
        Column column_RSD_TokenId             = this.toColumn(Configuration.Row_Security_Deny_Table.COLUMN_TOKENID, TypeMap.VARCHAR);
        rowDenySecurityCols.add(column_RSD_EntityInstanceid);
		rowDenySecurityCols.add(column_RSD_TokenId);
        Table table_RSD = this.toSubsidiaryTable(Configuration.Row_Security_Deny_Table.makeRowSecurityDenyTableNameWithPrefixSuffix(super.getName()), rowDenySecurityCols);
		table_RSD.addIndex(this.createIndexe(new Column[]{column_RSD_EntityInstanceid,column_RSD_TokenId}, index_RSD, true));
        tables.add(table_RSD);
        
		return tables;
	}
	
	   /**
    *
    * @param column
    * @param indexName
    * @param isUnique
    * @return
    */
    protected Index createIndexe(Column[] columns, String indexName, boolean isUnique) {
		Index index = super.makeIndexPerColumn(isUnique);
        for(Column column : columns){
            IndexColumn indexColumn = new IndexColumn(column);
            index.addColumn(indexColumn);
        }
		index.setName(indexName);
        return index;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.unit;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.ddlutils.model.Table;

import coredb.config.Configuration;

/**
 * The entity class with tracing functionality
 * @author vmc
 */
// checked by vmc @580
public class EntityTracingClass extends EntityClass implements ClassToTables{
    /**
     * Create a entityclass with tracing functionality
     * 
     * @param name EntityClass name
     * @param attributeClassList the list of attributes EntityClass has
     * @param indexClassList the list of indexes EntityClass has
     */
	// checked by vmc @580
    public EntityTracingClass(String name, AttributeClass[] attributeClassList, IndexClass[] indexClassList) {
        super(name, attributeClassList, indexClassList);
    }

    /**
     * Create the tables according to the current entityclass.
     * @return a list of tables
     */
	// checked by vmc @580
    @Override
    public List<Table> toTables() {
		List<Table> tables = new ArrayList<Table>();
		tables.add(super.toTable());
		tables.add(this.generateMappingTable());
		tables.add(this.generateDeleteTable());
		tables.add(this.generateTracingTable());
        return tables;
	}

    	// checked by vmc @688
	private Table generateMappingTable() {
		String tableName			= Configuration.Tracing_Table.makeMappingTableNameWithPrefixSuffix(this.getName());
		String columnNameCoredbId	= Configuration.Tracing_Table.makeCoredbColumnNameId(tableName);

		AttributeClass coredbId = new AttributeClass(columnNameCoredbId, String.class);
		coredbId.setPrimaryKey(true);

		List<AttributeClass> acl = new LinkedList<AttributeClass>();
		acl.add(coredbId);
		/** COPY THE PRIMARY KEYS FROM THE BASE TABLE	*/
	    for(AttributeClass ac : super.getAttributeClassList()) if (ac.isPrimaryKey()) acl.add(ac);

		acl.add(new AttributeClass(Configuration.Tracing_Table.COLUMN_CRUD, Character.class));
		acl.add(new AttributeClass(Configuration.Tracing_Table.COLUMN_MINCSN, Integer.class));
		acl.add(new AttributeClass(Configuration.Tracing_Table.COLUMN_MAXCSN, Integer.class));


        EntityClass ec = new EntityClass(tableName, acl.toArray(new AttributeClass[0]), null);
		return ec.toTable();
	}

	private Table generateDeleteTable() {
		return new EntityClass(Configuration.Delete_Table.makeDeteledTableName(name),
			attributeClassList, indexClassList).toTable();
	}

	// checked by vmc @792
	private Table generateTracingTable() {
        String tableName				= Configuration.Tracing_Table.makeTracingTableNameWithPrefixSuffix(this.getName());

        List<AttributeClass> acl		= new LinkedList<AttributeClass>();
		String tracingTableColumnCordbId= Configuration.Tracing_Table.makeCoredbColumnNameId(tableName);
        AttributeClass coredbId			= new AttributeClass(tracingTableColumnCordbId, Integer.class);
        coredbId.setPrimaryKey(true);
        coredbId.setAutoIncrement(true);

		String tracingTableColumnNameMappingCoredbId =	Configuration.Tracing_Table.makeMappingTableColumnNameCoredbId(this.getName());

        acl.add(coredbId);
        acl.add(new AttributeClass(tracingTableColumnNameMappingCoredbId,		String.class));
// vmc:timestamp
//        acl.add(new AttributeClass(Configuration.Tracing_Table.COLUMN_TIMESTAMP,Timestamp.class));
//        acl.add(new AttributeClass(Configuration.Tracing_Table.COLUMN_USERID,	String.class));
// /vmc:timestamp
        acl.add(new AttributeClass(Configuration.Tracing_Table.COLUMN_CSN,		Integer.class));
        acl.add(new AttributeClass(Configuration.Tracing_Table.COLUMN_NAME,		String.class));
        acl.add(new AttributeClass(Configuration.Tracing_Table.COLUMN_VALUE,	byte[].class));
        acl.add(new AttributeClass(Configuration.Tracing_Table.COLUMN_CRUD,		Character.class));

        EntityClass ec = new EntityClass(tableName, acl.toArray(new AttributeClass[0]), null);
		return ec.toTable();
	}
}
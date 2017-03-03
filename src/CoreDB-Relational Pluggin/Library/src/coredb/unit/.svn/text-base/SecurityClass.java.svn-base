/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.unit;

import java.util.LinkedList;
import java.util.List;
import org.apache.ddlutils.model.Table;

/**
 * The basic security classes will be initialized in the first running.
 * @author vmc
 */
public class SecurityClass extends EntityClass implements ClassToTables {

    /**
     * This constructor constructs a table with security
     *
     * @param name
     * @param attributeClassList
     * @param indexClassList
     */
	public SecurityClass(String name, AttributeClass[] attributeClassList, IndexClass[] indexClassList) {
		super(name,attributeClassList,indexClassList);
	}

    /**
     * This functions return a list of tables.
     *
     * @return a list of tables.
     */
    @Override
	public List<Table> toTables() {
		List<Table> tables = new LinkedList<Table>();
		tables.add(super.toTable());
		return tables;
	}
}
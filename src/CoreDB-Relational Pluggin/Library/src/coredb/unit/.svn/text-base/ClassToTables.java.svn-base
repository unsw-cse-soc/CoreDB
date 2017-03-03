/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.unit;

import java.util.List;
import org.apache.ddlutils.model.Table;

/**
 *
 * @author vmc
 */
// checked by vmc @348
public interface ClassToTables {
    /**
     * Create the tables according to the current entityclass. If the security is available,
     * the row security tables will also be generated with it.
     * @return the list of tables 
     */
	// checked by vmc @348
	public List<Table> toTables();

    /**
     * Get the name of the current entityclass.
     * @return  the table name
     */
	// checked by vmc @348
    public String getName();
    
	// checked by vmc @348
    public AttributeClass[] getAttributeClassList();

	// checked by vmc @348
	public void describe(int indent);
}
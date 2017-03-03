/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.sql;

/**
 *
 * @author vmc
 */
public interface SQLStatement {
	public String toSQLStatement();
	public void clear();
}

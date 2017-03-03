/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.sql;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author vmc
 */
public class CompoundStatementManual implements SQLStatement {
    private List<String> conditionalStatements = null;

    /**
     * create a ConditionalStatementHandler,
     * default constructor
     */
    public CompoundStatementManual() {
        this.conditionalStatements = new LinkedList<String>();
    }

    
    /**
     * Get the list of conditional statement from the handler
     * @return a list of conditional statement.
     */
    public List<String> getCompoundStatements() { return conditionalStatements; }

    /**
     * Add the conditional statement to the handler
     * @param conditionalStatement a condition needed to add in
     */
    public void addConditionalStatement(SQLStatement conditionalStatement) {
        this.addConditionalStatement(conditionalStatement.toSQLStatement());
    }

	/**
	 *
	 * @param conditionalstatment
	 */
	public void addConditionalStatement(String conditionalstatment) {
        this.conditionalStatements.add(conditionalstatment);
	}

	/**
	 * Clear all the values in this class.
	 */
	public void clear() {
        this.conditionalStatements.clear();
    }

    /**
	 *
     * @return An SQL conditional statement including the key word [where].
     */
    @Override
    public String toString() {
		return toSQLStatement();
    }

	/**
	 *
     * @return An SQL conditional statement including the key word [where].
	 */
	public String toSQLStatement() {
    	String whereClauseString = "";
    	for(String sql : conditionalStatements){
    		whereClauseString += sql;
    	}
        return whereClauseString;
	}
}
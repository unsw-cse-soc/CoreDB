/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.sql;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author vmc
 */
public class CompoundStatementAuto implements SQLStatement {
	private String logic = null;
	private List<String> sqls = new LinkedList<String>();

	/**
	 *
	 * @param logic
	 */
	public CompoundStatementAuto(String logic) {
		this.logic = logic;
	}

	/**
	 *
	 * @param sqlStatement
	 */
	public void addCompoundStatement(SQLStatement sqlStatement) {
		this.sqls.add(sqlStatement.toString());
	}

	/**
	 *
	 * @param sqlStatementString
	 */
	public void addCompoundStatement(String sqlStatementString) {
		this.sqls.add(sqlStatementString);
	}

	public void clear() {
		this.sqls.clear();
	}

	/**
	 *
	 * @return SQL statement
	 */
	@Override
	public String toString() {
		return toSQLStatement();
	}

	/**
	 *
	 * @return SQL statement
	 */
	public String toSQLStatement() {
		if (sqls == null || sqls.size() == 0) return "";

    	String whereClauseString = Q.LEFT_PARENTHESIS;
    	for(Iterator<String> i = sqls.iterator(); i.hasNext(); ){
    		whereClauseString += i.next();
  			if(i.hasNext()) whereClauseString += " " + logic + " ";
    	}
    	whereClauseString += Q.RIGHT_PARENTHESIS;
        return whereClauseString;
	}

	/**
	 *
	 * @param args
	 */
	public void main(String[] args) {
		String statement = String.format("%10s %10s %10s","age", Q.E, "10");
		System.out.println(statement);

		CompoundStatementAuto csl = new CompoundStatementAuto(Q.AND);
		csl.addCompoundStatement(statement);
		csl.addCompoundStatement(statement);
		csl.addCompoundStatement(statement);
		System.out.println(csl);

		CompoundStatementAuto cslOR = new CompoundStatementAuto(Q.OR);
		cslOR.addCompoundStatement(statement);
		cslOR.addCompoundStatement(statement);
		cslOR.addCompoundStatement(statement);
		cslOR.addCompoundStatement(csl);
		System.out.println(cslOR);


		CompoundStatementManual rawSQL = new CompoundStatementManual();
		rawSQL.addConditionalStatement("age = 10");
	}
}

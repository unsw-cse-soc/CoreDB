package coredb.utils;

import java.util.LinkedList;
import java.util.List;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.postgresql.PGResultSetMetaData;


public class CoredbResultSetMetaData {
	private List<String> _baseColumnName = new LinkedList<String>();
	private List<String> _baseSchemaName = new LinkedList<String>();
	private List<String> _baseTableName  = new LinkedList<String>();

	public CoredbResultSetMetaData(ResultSetMetaData resultSetMetaData, String databaseType) throws SQLException{
		reflectResultSet(resultSetMetaData,databaseType);
	}

	private void reflectResultSet(ResultSetMetaData _rsmd, String databaseType) throws SQLException {
		if (databaseType.equalsIgnoreCase("mysql")) {
			castResultSetToMySQLMode(_rsmd);
		}else if (databaseType.equalsIgnoreCase("oracle")) {
			castResultSetToOralceMode(_rsmd);
		}else if (databaseType.equalsIgnoreCase("postgresql")) {
			castResultSetToPostgreSQLMode(_rsmd);
		}else{
			castResultSetToPostgreSQLMode(_rsmd);
		}
	}

	private void castResultSetToPostgreSQLMode(ResultSetMetaData _rsmd) throws SQLException {
		PGResultSetMetaData pgResultSetMetaData = (PGResultSetMetaData)_rsmd;
		for(int id = 1 ; id <= _rsmd.getColumnCount() ; id++){
			_baseColumnName.add(pgResultSetMetaData.getBaseColumnName(id));
			_baseSchemaName.add(pgResultSetMetaData.getBaseSchemaName(id));
			_baseTableName.add(pgResultSetMetaData.getBaseTableName(id));
		}
	}

	private void castResultSetToOralceMode(ResultSetMetaData _rsmd) throws SQLException {
		castResultSetToGeneralMode(_rsmd);
	}

	private void castResultSetToMySQLMode(ResultSetMetaData _rsmd) throws SQLException {
		castResultSetToGeneralMode(_rsmd);
	}

	private void castResultSetToGeneralMode(ResultSetMetaData _rsmd) throws SQLException {
		for(int id = 1 ; id <= _rsmd.getColumnCount() ; id++){
			_baseColumnName.add(_rsmd.getColumnName(id));
			_baseSchemaName.add(_rsmd.getSchemaName(id));
			_baseTableName.add(_rsmd.getTableName(id));
		}
	}

	public String getBaseColumnName(int id) throws SQLException {
		return _baseColumnName.get(id-1);
	}

	public String getBaseSchemaName(int id) throws SQLException {
		return _baseSchemaName.get(id-1);
	}

	public String getBaseTableName(int id) throws SQLException {
		return _baseTableName.get(id-1);
	}

}

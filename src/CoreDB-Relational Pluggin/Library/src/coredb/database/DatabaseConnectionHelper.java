/**
 * 
 */
package coredb.database;

import coredb.utils.*;
import java.sql.Blob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.LazyDynaClass;
import org.apache.ddlutils.model.TypeMap;

import coredb.config.Configuration;
import coredb.config.ConfigurationValues;
import coredb.sql.Q;
import coredb.unit.EntityInstance;
import draft.PublicFunctions;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.RowSetDynaClass;

/**
 * @author sean
 *
 */
public class DatabaseConnectionHelper {
    public static List<EntityInstance> executeArbitraryQueryForCoreDB(DatabaseConnection databaseConnection, String sql){
		return executeArbitraryQueryCommon(databaseConnection, Q.ARBITRARY_SQL_NAME, sql, true);
	}

    public static List<EntityInstance> executeArbitraryQueryForCoreDB(DatabaseConnection databaseConnection, String queryName, String sql){
		return executeArbitraryQueryCommon(databaseConnection, queryName, sql, true);
	}

	public static List<EntityInstance> executeArbitraryQueryCommon(DatabaseConnection databaseConnection, String queryName, String sql, boolean isUnique){
    	String resultTableName = "";
    	if(databaseConnection.readTableNameList(false).contains(queryName)) resultTableName = queryName;
    	else if(!queryName.endsWith(Configuration.LAZYDYNACLASS))           resultTableName = Configuration.makeLazyDynaClassName(queryName);
    	else                                                                resultTableName = queryName;

    	List<EntityInstance> entityInstances = new LinkedList<EntityInstance>();
        Connection connection = databaseConnection.getConnection();

        try {
			PreparedStatement ps  = connection.prepareStatement(sql);
			ResultSet resultSet   = ps.executeQuery();
			entityInstances  =
				isUnique ?
				DatabaseConnectionHelper.convertResultSetWithUniqueAttributeToEntityInstance(databaseConnection, resultTableName, resultSet)
				:
				//PublicFunctions.convertResultSetWithNonUniqueAttributesToEntityInstance(databaseConnection, resultTableName, resultSet);
				DatabaseConnectionHelper.convertResultSetWithNonUniqueAttributesToEntityInstance(databaseConnection, resultTableName, resultSet);
		} catch (SQLException ex) {
			Logger.getLogger(PublicFunctions.class.getName()).log(Level.SEVERE, null, ex);
		}

        return entityInstances;
	}

	public static List<EntityInstance> convertResultSetWithUniqueAttributeToEntityInstance(DatabaseConnection databaseConnection, String tableName, ResultSet rs) throws SQLException{
	    List<EntityInstance> entityInstances = new LinkedList<EntityInstance>();
	    RowSetDynaClass rsdc		= new RowSetDynaClass(rs);
	    DynaProperty properties[]	= rsdc.getDynaProperties();

		@SuppressWarnings("unchecked")
	    Iterator<DynaBean> rows		= rsdc.getRows().iterator();
	    /**
	     * the block below is design for the case when resultSet is empty.
	     * This method will returns the data structure of query result without any data
	     */
	    EntityInstance instance = null; 
	    while (rows.hasNext()) {
	        if(tableName.endsWith(Configuration.LAZYDYNACLASS)) {
		    	DynaClass dynaClass = new LazyDynaClass(tableName, properties);
		    	instance = new EntityInstance(dynaClass);
			} else {
				instance = databaseConnection.createEntityInstanceFor(tableName, false);
			}

	        DynaBean oldRow = rows.next();
	        for(int i = 0; i < properties.length; i ++) {
	    	    String name = properties[i].getName();
	    	    Object newObject = oldRow.get(name);
	    	    /**
	    	     * the following conversion is because the value of Character in resultSet is a string.
	    	     * Currently we can get the columns type using JDBCTypeConverter when tableName is end with LAZYDYNACLASS
	    	     */
	    	    if (!tableName.endsWith(Configuration.LAZYDYNACLASS) && JDBCTypeConverter.toJAVAType(SchemaInfo.refactorColumnType(tableName, name)) == Character.class) {
	    	    	newObject = ((newObject != null )?newObject.toString().charAt(0) : null);
				}
	    	    instance.set(name, newObject);
	        }
	        entityInstances.add(instance);
	    }
	    
	    return entityInstances;
	}

	protected static List<EntityInstance> convertResultSetWithNonUniqueAttributesToEntityInstance(DatabaseConnection databaseConnection, String tableName, ResultSet rs) throws SQLException{
	    List<EntityInstance> entityInstances = new LinkedList<EntityInstance>();
		DynaProperty[] dynaProperties        = null;

		if (tableName.endsWith(Configuration.LAZYDYNACLASS)) dynaProperties = getDynaPropertiesForLazyDynaClass(rs.getMetaData());
		else                                                 dynaProperties = getDynaPropertiesForBaseTable(rs.getMetaData());

	    /**
	     * the block below is design for the case when resultSet is empty.
	     * This method will returns the data structure of query result without any data
	     */
	    EntityInstance instance = null;
	    while (rs.next()) {
	        if(tableName.endsWith(Configuration.LAZYDYNACLASS)) {
		    	DynaClass dynaClass = new LazyDynaClass(tableName, dynaProperties);
		    	instance = new EntityInstance(dynaClass);
			} else {
				instance = databaseConnection.createEntityInstanceFor(tableName, false);
			}

	        for(int i = 0; i < dynaProperties.length; i ++) {
	    	    String name = dynaProperties[i].getName();
	    	    Object newObject = rs.getObject(i+1);
	    	    /**
	    	     * the following conversion is because the value of Character in resultSet is a string.
	    	     * Currently we can get the columns type using JDBCTypeConverter when tableName is end with LAZYDYNACLASS
	    	     */

				String javaType = dynaProperties[i].getType().getName();
				if (newObject == null ) {
					// DO NOTHING
				} else if (javaType.equals(Character.class.getName())) {
	    	    	newObject = newObject.toString().charAt(0);
				} else if (javaType.equals(Boolean.class.getName())) {
					newObject = (newObject.equals(1) || newObject.equals(true) ? true : false);
	    	    }
	    	    instance.set(name, newObject);
	        }
	        entityInstances.add(instance);
	    }
	    return entityInstances;
	}
	
	private static DynaProperty[] getDynaPropertiesForBaseTable(ResultSetMetaData resultSetMetaData) throws SQLException{
		return getDynaProperties(resultSetMetaData, false);
	}

	private static DynaProperty[] getDynaPropertiesForLazyDynaClass(ResultSetMetaData resultSetMetaData) throws SQLException{
		return getDynaProperties(resultSetMetaData, true);
	}
	
	private static DynaProperty[] getDynaProperties( ResultSetMetaData resultSetMetaData, boolean isLazyDynaClass) throws SQLException {
		int size = resultSetMetaData.getColumnCount();
		DynaProperty[] properties         = new DynaProperty[size];
		for(int i = 1 ; i <= size; i++){
			CoredbResultSetMetaData crsmd = new CoredbResultSetMetaData(resultSetMetaData, ConfigurationValues.databaseType);
			Class<?> type = DatabaseConnectionHelper.fromJDBCTypetoJAVAType(ConfigurationValues.databaseType, resultSetMetaData.getColumnClassName(i), resultSetMetaData.getPrecision(i));
			
			if(isLazyDynaClass) properties[i-1] = new DynaProperty(crsmd.getBaseTableName(i) + Q.PERIOD + crsmd.getBaseColumnName(i),type);
			else                properties[i-1] = new DynaProperty(crsmd.getBaseColumnName(i),type);
		}
		return properties;
	}
	
	private static Class<?> fromJDBCTypetoJAVAType(String databaseType, String type, int precision){
		if (databaseType.equals("mysql")) {
			return fromJDBCTypetoJAVATypeWithMySQL(type, precision);
		} else if (databaseType.equals("oracle")) {
			return fromJDBCTypetoJAVATypeWithOracle(type, precision);
		} else if (databaseType.equals("postgresql")) {
			return fromJDBCTypetoJAVATypeWithPostgreSql(type, precision);
		} else {
			System.out.println("CoreDB: database is not supported.");
			return null;
		}
	}
	
	private static Class<?> fromJDBCTypetoJAVATypeWithMySQL(String type, int precision) {
		if (type.toUpperCase().endsWith(TypeMap.BIT))				return Boolean.class;
        else if (type.toUpperCase().endsWith("[B"))                 return byte[].class;
        else if (type.toUpperCase().endsWith(TypeMap.CHAR)) 		return Character.class;
        else if (type.toUpperCase().endsWith(TypeMap.DATE)) 		return Date.class ;
        else if (type.toUpperCase().endsWith(TypeMap.DOUBLE)) 		return Double.class;
        else if (type.toUpperCase().endsWith(TypeMap.INTEGER)) 		return Integer.class ;
        else if (type.toUpperCase().endsWith(TypeMap.TIME)) 		return Time.class ;
        else if (type.toUpperCase().endsWith(TypeMap.TIMESTAMP)) 	return Timestamp.class ;
        else if (type.toUpperCase().endsWith(TypeMap.DECIMAL)) {
        	if(precision == 1)                                      return Boolean.class;
        	else                                                    return Integer.class ;
        }
        else if (type.toUpperCase().endsWith(TypeMap.BOOLEAN))      return Boolean.class;
        else if (type.toUpperCase().endsWith("STRING")) {
        	if(precision == 1)                                      return Character.class ;
        	else                                                    return String.class ;
        }
        else if (type.toUpperCase().endsWith(TypeMap.BLOB)) 	    return Blob.class ;
        else {
            System.err.println("MESSAGE FROM COREDB DEVELOPMENT TEAM: " +
                    "The type that you are requesting "+type+" has not been tested. " +
                    "Please contact the team to provide support.");
			try { throw new Exception(); } catch (Exception e) { e.printStackTrace(); }
            return null;
		}
	}

  private static Class<?> fromJDBCTypetoJAVATypeWithPostgreSql(String type, int precision) {
		if (type.toUpperCase().endsWith(TypeMap.BIT))				return Boolean.class;
        else if (type.toUpperCase().endsWith("[B"))                 return byte[].class;
        else if (type.toUpperCase().endsWith(TypeMap.CHAR)) 		return Character.class;
        else if (type.toUpperCase().endsWith(TypeMap.DATE)) 		return Date.class ;
        else if (type.toUpperCase().endsWith(TypeMap.DOUBLE)) 		return Double.class;
        else if (type.toUpperCase().endsWith(TypeMap.INTEGER)) 		return Integer.class ;
        else if (type.toUpperCase().endsWith(TypeMap.TIME)) 		return Time.class ;
        else if (type.toUpperCase().endsWith(TypeMap.TIMESTAMP)) 	return Timestamp.class ;
        else if (type.toUpperCase().endsWith(TypeMap.DECIMAL)) {
        	if(precision == 1)                                      return Boolean.class;
        	else                                                    return Integer.class ;
        }
        else if (type.toUpperCase().endsWith(TypeMap.BOOLEAN))      return Boolean.class;
        else if (type.toUpperCase().endsWith("STRING")) {
        	if(precision == 1)                                      return Character.class ;
        	else                                                    return String.class ;
        }
        else {
            System.err.println("MESSAGE FROM COREDB DEVELOPMENT TEAM: " +
                    "The type that you are requesting "+type+" has not been tested. " +
                    "Please contact the team to provide support.");
			try { throw new Exception(); } catch (Exception e) { e.printStackTrace(); }
            return null;
		}
	}

	private static Class<?> fromJDBCTypetoJAVATypeWithOracle(String type, int precision) {
		if (type.toUpperCase().endsWith(TypeMap.BIT))				return Boolean.class;
        else if (type.toUpperCase().endsWith("[B"))                 return byte[].class;
        else if (type.toUpperCase().endsWith(TypeMap.CHAR)) 		return Character.class;
        else if (type.toUpperCase().endsWith(TypeMap.DATE)) 		return Date.class ;
        else if (type.toUpperCase().endsWith(TypeMap.DOUBLE)) 		return Double.class;
        else if (type.toUpperCase().endsWith(TypeMap.INTEGER)) 		return Integer.class ;
        else if (type.toUpperCase().endsWith(TypeMap.TIME)) 		return Time.class ;
        else if (type.toUpperCase().endsWith(TypeMap.TIMESTAMP)) 	return Timestamp.class ;
        else if (type.toUpperCase().endsWith(TypeMap.DECIMAL)) {
        	if(precision == 1)                                      return Boolean.class;
        	else                                                    return Integer.class ;
        }
        else if (type.toUpperCase().endsWith(TypeMap.BOOLEAN))      return Boolean.class;
        else if (type.toUpperCase().endsWith("STRING")) {
        	if(precision == 1)                                      return Character.class ;
        	else                                                    return String.class ;
        }
        else if (type.toUpperCase().endsWith(TypeMap.BLOB)) 	    return Blob.class ;
        else {
            System.err.println("MESSAGE FROM COREDB DEVELOPMENT TEAM: " +
                    "The type that you are requesting "+type+" has not been tested. " +
                    "Please contact the team to provide support.");
			try { throw new Exception(); } catch (Exception e) { e.printStackTrace(); }
            return null;
		}
	}

}

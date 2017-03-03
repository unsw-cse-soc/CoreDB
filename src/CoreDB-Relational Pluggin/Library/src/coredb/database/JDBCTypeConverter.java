package coredb.database;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.ddlutils.model.TypeMap;

// checked by vmc @694
public class JDBCTypeConverter {
	/**
	 * the following content is the data type mapping between JAVA and different database sever
	 * 
	 * DOUBLE(P) = DOUBLE PRECISION
	 * 
	 * JDBC-Type      Java-Object-Type      MySQL       postgreSQL	 Oracle      Derby                       MSSQL
	 * 
	 * BIT	          java.lang.Boolean	    TINYINT(1)	BOOLEAN	     BOOLEAN	 CHAR FOR BIT DATA	         BIT
	 * TINYINT	      java.lang.Byte	    TINYINT	    SMALLINT	 SMALLINT	 SMALLINT	                 TINYINT 
	 * SMALLINT	      java.lang.Short	    SMALLINT	SMALLINT	 SMALLINT	 SMALLINT 	                 SMALLINT 
	 * INTEGER	      java.lang.Integer     INTEGER	    INTEGER	     INTEGER	 INTEGER                     INTEGER 
	 * BIGINT	      java.lang.Long        BIGINT	    BIGINT	     NUMERIC	 BIGINT                      BIGINT 
	 * FLOAT	      java.lang.Double	    FLOAT	    DOUBLE(P)    FLOAT	     FLOAT                       FLOAT
	 * DOUBLE	      java.lang.Double	    DOUBLE	    DOUBLE(P)	 DOUBLE(P)	 DOUBLE	                     DOUBLE(P) 
	 * REAL           java.lang.Float       REAL   	    REAL	     REAL	     REAL                        REAL
	 * NUMERIC	      java.math.BigDecimal  NUMERIC	    NUMERIC	     NUMERIC	 NUMERIC	                 NUMERIC  
	 * DECIMAL	      java.math.BigDecimal  DECIMAL	    NUMERIC	     DECIMAL	 DECIMAL	                 DECIMAL 
	 * CHAR	          java.lang.String	    CHAR	    CHAR	     CHAR	     CHAR                        CHAR
	 * VARCHAR	      java.lang.String	    VARCHAR	    VARCHAR	     VARCHAR2	 VARCHAR                     VARCHAR
	 * LONGVARCHAR	  java.lang.String	    VARCHAR	    VARCHAR	     LONG	     LONG VARCHAR                TEXT
	 * DATE       	  java.sql.Date	        DATE	    DATE	     DATE	     DATE                        DATETIME 
	 * TIME       	  java.sql.Time	        TIME	    TIME	     DATE	     TIME                        DATETIME 
	 * TIMESTAMP  	  java.sql.Timestamp	TIMESTAMP	TIMESTAMP	 TIMESTAMP	 TIMESTAMP                   TIMESTAMP
	 * BINARY         byte[]            	BINARY	    BYTEA	     RAW	     CHAR[] FOR BIT DATA         BINARY 
	 * VARBINARY	  byte[]	            VARBINARY	BYTEA	     LONG RAW	 VARCHAR[] FOR BIT DATA      VARBINARY
	 * LONGVARBINARY  byte[]	            VARBINARY	BYTEA	     LONG RAW	 LONG VARCHAR FOR BIT DATA	 IMAGE 
	 * OTHER	      java.lang.Object	    BLOB	    BYTEA	     BLOB	     BLOB	                     IMAGE 
	 * JAVA_OBJECT	  java.lang.Object	    BLOB	    BYTEA	     BLOB	     BLOB 	                     IMAGE 
	 * BLOB	          java.io.InputStream	BLOB	    BYTEA	     BLOB	     BLOB                        IMAGE
	 * CLOB	          java.sql.Clob	        TEXT	    TEXT	     CLOB	     CLOB	                     TEXT
	 */
	
	/**
	 * Convert to JDBC  type, this method converts Java type to JDBC type
	 * @param type the Java type need to convert
	 * @return referential JDBC type
	 */
	// checked by vmc @694
	public static String toSQLType(Class type){												// POSTGRESQL	// MYSQL	//
//      if (type.equals(Array.class)) 				return TypeMap.ARRAY;
//      else if (type.equals(BigDecimal.class)) 	return TypeMap.NUMERIC;
//		if (type.equals(Blob.class))				return TypeMap.LONGVARBINARY;
		//if (type.equals(Blob.class))				return TypeMap.BLOB;
		if (type.equals(Boolean.class)) 		    return TypeMap.BIT;
//      else if (type.equals(Byte.class)) 			return TypeMap.TINYINT; 
//        else if (type.equals(byte[].class)) 		return TypeMap.VARBINARY;
        else if (type.equals(byte[].class)) 		return TypeMap.LONGVARBINARY;			//
		//else if (type.equals(byte[].class))			return TypeMap.VARBINARY;
//        else if (type.equals(Byte[].class)) 		return TypeMap.LONGVARCHAR;
        else if (type.equals(Character.class)) 		return TypeMap.CHAR;					//
//      else if (type.equals(Clob.class)) 			return TypeMap.CLOB;
        else if (type.equals(Date.class)) 			return TypeMap.DATE;					//
        else if (type.equals(Double.class)) 		return TypeMap.DOUBLE;					//
//      else if (type.equals(Float.class)) 			return TypeMap.REAL;
        else if (type.equals(Integer.class)) 		return TypeMap.INTEGER;					//
//      else if (type.equals(Long.class)) 			return TypeMap.BIGINT;
//      else if (type.equals(Null.class)) 			return TypeMap.NULL;
//      else if (type.equals(Object.class)) 		return TypeMap.JAVA_OBJECT;
//      else if (type.equals(Ref.class)) 			return TypeMap.REF;
//      else if (type.equals(Short.class)) 			return TypeMap.SMALLINT;
        else if (type.equals(String.class)) 		return TypeMap.VARCHAR;					//
        else if (type.equals(Time.class)) 			return TypeMap.TIME;
        else if (type.equals(Timestamp.class)) 		return TypeMap.TIMESTAMP;				//
        else {
            System.err.println("MESSAGE FROM COREDB DEVELOPMENT TEAM: " +
                    "The type that you are requesting "+type+" has not been tested. " +
                    "Please contact the team to provide support.");
			try { throw new Exception(); } catch (Exception e) { e.printStackTrace(); }
            System.exit(0);
            return null;
		}
	}
//*/
	/**
	 * Convert to Java type, this method converts JDBC type to Java type
	 * @param type the JDBC type need to convert
	 * @return referential Java type
	 */
///*
	// checked by vmc @ 694
	public static Class toJAVAType(String type){
//      if (type.equals(TypeMap.ARRAY)) 			return Array.class;
//      else if (type.equals(TypeMap.NUMERIC)) 		return BigDecimal.class;
//		if (type.equals(TypeMap.LONGVARBINARY))		return Blob.class;
        //if (type.equals(TypeMap.BLOB))			return Blob.class;
		if (type.equals(TypeMap.BIT))				return Boolean.class;
//      else if (type.equals(TypeMap.TINYINT)) 		return Byte.class;
//        else if (type.equals(TypeMap.VARBINARY)) 	return byte[].class;
        else if (type.equals(TypeMap.LONGVARBINARY))return byte[].class;
//        else if (type.equals(TypeMap.LONGVARCHAR))	return Byte[].class;
        else if (type.equals(TypeMap.CHAR)) 		return Character.class;
//      else if (type.equals(TypeMap.CLOB)) 		return Clob.class;
        else if (type.equals(TypeMap.DATE)) 		return Date.class ;
        else if (type.equals(TypeMap.DOUBLE)) 		return Double.class;
//      else if (type.equals(TypeMap.REAL)) 		return Float.class ;
        else if (type.equals(TypeMap.INTEGER)) 		return Integer.class ;
//      else if (type.equals(TypeMap.BIGINT)) 		return Long.class ;
//      else if (type.equals(TypeMap.NULL)) 		return Null.class ;
//      else if (type.equals(TypeMap.JAVA_OBJECT)) 	return Object.class ;
//      else if (type.equals(TypeMap.REF)) 			return Ref.class ;
//      else if (type.equals(TypeMap.SMALLINT)) 	return Short.class ;
        else if (type.equals(TypeMap.VARCHAR)) 		return String.class ;
        else if (type.equals(TypeMap.TIME)) 		return Time.class ;
        else if (type.equals(TypeMap.TIMESTAMP)) 	return Timestamp.class ;
        else {
            System.err.println("MESSAGE FROM COREDB DEVELOPMENT TEAM: " +
                    "The type that you are requesting "+type+" has not been tested. " +
                    "Please contact the team to provide support.");
			try { throw new Exception(); } catch (Exception e) { e.printStackTrace(); }
            System.exit(0);
            return null;
		}
	}
// */
}

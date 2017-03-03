/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.utils;

import coredb.database.JDBCTypeConverter;
import coredb.unit.AttributeClass;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;

/**
 *
 * @author vmc
 */
public class HelperWithTracing {
	/**
	 * Convert the Byte array data to the valid data(specified by AttributeClass)
	 * @param ac the AttributeClass object
	 * @param byt the Byte array
	 * @return the valid data as Object
	 */
	// CHECKED BY VMC @259
	// By Stephen
	public static Object convertByteArrayToValidData(AttributeClass ac, byte[] byt){
		String value    = new String(byt);
		Class classType = JDBCTypeConverter.toJAVAType(ac.getSqlType());
		if (classType.equals(Boolean.class))            return Boolean.valueOf(value);
		else if (classType.equals(byte[].class))        return byt;
		else if (classType.equals(Character.class)){
			if(value.length() == 1)                     return value.charAt(0);
			else{
				System.err.printf("JDBC TYPE : %20s IS NOT SUPPORTED BY COREDB API");
				System.exit(0);
				return null;
			}
		}
		else if (classType.equals(Date.class)) 		    return Date.valueOf(value);
		else if (classType.equals(Double.class)) 		return Double.parseDouble(value);
		else if (classType.equals(Integer.class)) 	    return Integer.parseInt(value);
		else if (classType.equals(String.class)) 	    return value;
		else if (classType.equals(Time.class)) 		    return Time.valueOf(value);
		else if (classType.equals(Timestamp.class))     return Timestamp.valueOf(value);
		else {
			System.err.printf("JDBC TYPE : %20s IS NOT SUPPORTED BY COREDB API");
			System.exit(0);
			return null;
		}
	}
}

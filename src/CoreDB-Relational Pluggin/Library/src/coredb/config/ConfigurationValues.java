/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package coredb.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class defines user details to build database connection.
 * Please write your own database connection details into a local file, 
 * and this API will read it by call init().
 * @author vmc
 */

// checked by vmc @701
public class ConfigurationValues {
	/*
	public static String databaseName   = "coredb";
	public static String dbURL          = "jdbc:postgresql://soc-server.cse.unsw.edu.au:5432/"+databaseName;
	public static String databaseType   = "postgresql";
	public static String databaseDriver = "org.postgresql.Driver";
	 */

	/*
	public static String databaseName = "coredb";
	public static String dbURL     = "jdbc:derby://localhost:1527/"+databaseName;
	public static String databaseType= "derby";
	public static String databaseDriver = "org.apache.derby.jdbc.ClientDriver";
	 */

	/*
	public static String databaseName = "test";
	public static String dbURL     = "jdbc:mysql://localhost:3306/"+databaseName;
	public static String databaseType= "mysql";
	public static String databaseDriver = "com.mysql.jdbc.Driver";
	 */

	public static String databaseName = null;
	public static String dbURL = null;
	public static String databaseType = null;
	public static String databaseDriver = null;
	public static String user = null;
	public static String password = null;

	/**
	 * Read database connection configuration details from local file
	 * @param pathToFile the location of configuration file
	 */
	// checked by vmc @701
	public static void init(String pathToFile) {
		ConfigurationValues.databaseName	= readUserDetailsFromTxt(pathToFile, "databaseName");
		ConfigurationValues.dbURL			= readUserDetailsFromTxt(pathToFile, "dbURL") +
											  ConfigurationValues.databaseName;
		ConfigurationValues.databaseType	= readUserDetailsFromTxt(pathToFile, "databaseType");
		ConfigurationValues.databaseDriver	= readUserDetailsFromTxt(pathToFile, "databaseDriver");
		ConfigurationValues.user			= readUserDetailsFromTxt(pathToFile, "user");
		ConfigurationValues.password		= readUserDetailsFromTxt(pathToFile, "password");
		
		//	ConfigurationValues.dbURL = "jdbc:oracle:thin:sean/tao@//localhost:1521/XE";
	}
	
	/**
	 * Read database connection configuration details from input parameters
	 * @param dbURL DB connection URL
	 * @param databaseType the type of DB server 
	 * @param databaseDriver the DB driver
	 * @param databaseName the name of database 
	 * @param user user name to connect database
	 * @param password user password to connect database
	 */
	// checked by vmc @1862
	public static void init(String dbURL, String databaseType,
			String databaseDriver, String databaseName, String user,
			String password) {
		ConfigurationValues.databaseDriver = databaseDriver;
		ConfigurationValues.databaseName   = databaseName;
		ConfigurationValues.databaseType   = databaseType;
		ConfigurationValues.dbURL          = dbURL + ConfigurationValues.databaseName;
		ConfigurationValues.user           = user;
		ConfigurationValues.password       = password;
	}

	/**
	 * @param pathToFile the pathToFile of configuration value file
	 * @param name the name of configuration elements
	 * @return the value of configuration elements
	 */
	// checked by vmc @701
	protected static String readUserDetailsFromTxt(String pathToFile, String name) {
		String value = "";
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(pathToFile));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith(name)) {
					value = line.split("=")[1].trim();
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return value;
	}
}
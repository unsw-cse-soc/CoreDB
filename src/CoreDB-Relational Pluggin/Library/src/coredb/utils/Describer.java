/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.apache.commons.beanutils.DynaProperty;
import org.apache.ddlutils.io.DatabaseIO;
import org.apache.ddlutils.model.Database;

import coredb.controller.IEntityControllerCommon;
import coredb.database.DatabaseConnection;
import coredb.database.DatabaseConnectionHelper;
import coredb.unit.ClassToTables;
import coredb.unit.EntityClass;
import coredb.unit.EntityInstance;

/**
 * This is the printer of database and entity objects.
 * @author vmc
 */
public class Describer {
	/**
	 * Print a entityinstance
	 * @param <T>
	 * @param entityInstance the entityinstance need to print
	 */
	public static <T extends EntityInstance> void describeEntityInstance(T entityInstance) {
		if(entityInstance == null) return;
		DynaProperty[] dynaProperties = entityInstance.getDynaClass().getDynaProperties();
		System.out.printf(" T=%35s",entityInstance.getDynaClass().getName());
		for (DynaProperty dynaProperty : dynaProperties) {
			Object obj = entityInstance.get(dynaProperty.getName());
			
			if(obj != null && obj.getClass().equals(byte[].class)) {
				System.out.printf("%1s [%10s %-5s]", " ", dynaProperty.getName(), byteaToString((byte[])obj));
			} else {
				System.out.printf("%1s [%10s %-5s]", " ", dynaProperty.getName(), obj);
			}
		}
        System.out.println();
	}
	
	/**
	 * This method converts bytes to string
	 * @param bytes
	 * @return
	 */
	// this method is not finished yet. might be bugs occurred
	private static String byteaToString(byte[] bytes) {
		String string = null;
		try {
			Class acutualDataType  = (Class)new ObjectInputStream(new ByteArrayInputStream(bytes)).readObject();
			string = acutualDataType.toString();
		} catch (IOException e) {
			string = new String(bytes);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return string;
	}
	
	
    /**
     * Print a list of entityinstances
	 * @param <T>
	 * @param entityInstances the list of EntityInstances need to print
     */
	public static <T extends EntityInstance> void describeEntityInstances(Iterable<T> entityInstances){
		if (entityInstances == null) return;
		for(EntityInstance entityInstance : entityInstances) describeEntityInstance(entityInstance);
	}
	
	/**
	 * Print a property of entityinstance
	 * @param dynaProperty the property need to print
	 */
	private static void describeDynaProperty(DynaProperty dynaProperty) {
		System.out.printf("%4s %10s", " ", dynaProperty.getName());
	}
    /**
     * Write database schema into file /tmp/schema.config
     * @param database the database object
     */
	private static void describeDatabase(Database database) {
		DatabaseIO databaseIO = new DatabaseIO();
		databaseIO.write(database, "/tmp/schema.config");
	}

	public static void describeClassToTables(List<ClassToTables> classes) {
		for (ClassToTables c : classes) c.describe(0);
	}
    /**
     * Print details of tables. This method prints details of all tables of current data schema
     * @param iec the interface of entitycontroller
     * @param reRead If reRead is true, this function internally calls refresh().
     */
    public static void describeTables(IEntityControllerCommon iec, boolean reRead) {
        for (String table : iec.readTableNameList(reRead)) {
            describeTable(iec,table,"---- just describing all tables");
		}
    }

    /**
     * Print details of a table. This method prints details of table whose name is 'tableName'
     * @param iec the interface of entitycontroller
     * @param tableName the name of table need to print
     */
    public static void describeTable(IEntityControllerCommon iec, String tableName) {
        describeTable(iec,tableName,"");
    }
    /**
     * Print details of a table
     * @param iec the interface of entitycontroller
     * @param tableName the name of table needed to print
     * @param message the message needed to attach in the print job
     */
    public static void describeTable(IEntityControllerCommon iec, String tableName, String message) {
    	describeTable(iec.getDatabaseConnection(), tableName, message);
	}

    /**
     * Print details of a table
     * @param databaseConnection the current DB connection
     * @param tableName the name of table needed to print
     * @param message the message needed to attach in the print job
     */
    public static void describeTable(DatabaseConnection databaseConnection, String tableName, String message) {
		try {
			PreparedStatement ps = databaseConnection.getConnection().prepareStatement("SELECT * from "
					+ tableName);

			ResultSet rs = ps.executeQuery();
			System.out.printf("<TABLENAME %-20s @message=%20s>\n" , tableName, message);
			//describeEntityInstances(DatabaseHelper.convertResultSetWithUniqueAttributeToEntityInstance(databaseConnection, tableName, rs));
			describeEntityInstances(DatabaseConnectionHelper.convertResultSetWithUniqueAttributeToEntityInstance(databaseConnection, tableName, rs));
			System.out.printf("</TABLENAME %10s>\n" , tableName);
		} catch (Exception ex) {
            ex.printStackTrace();
		}
	}
	public static void describeTableSchema(IEntityControllerCommon iec, String tableName) {
		EntityClass ec = iec.getEntityClass(iec.getDatabaseConnection(), tableName, true);
		ec.describe(0);
	}
}

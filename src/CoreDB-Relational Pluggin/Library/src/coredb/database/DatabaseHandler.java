/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.database;

import java.util.Collection;
import org.apache.ddlutils.dynabean.SqlDynaException;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Table;
import coredb.unit.EntityInstance;
import org.apache.ddlutils.model.ModelException;

/**
 *
 * @author vmc
 */
// checked by vmc @759
public class DatabaseHandler {
    private Database database = null;

    /**
     * Constructor, this method initialises database with default value
     */
	// checked by vmc @316
    public DatabaseHandler() {
        this.database = new Database();
    }

    /**
     * Constructor, this method initialises database with given value
     * @param database database intent to assign to database handler
     */
	// checked by vmc @316
    public DatabaseHandler(Database database) {
        this.database = database;
    }

    /**
     * 
     * @return return current using database 
     */
	// checked by vmc @259
    public Database getDatabase() {
        return this.database;
    }

    /**
     * This method sets using database
     * @param database database attach to database handler 
     */
	// checked by vmc @316
    public void setDatabase(Database database) {
        this.database = database;
    }

    /**
     * Add tables, this method adds tables to database schema
     * @param tables
     */
	// checked by vmc @355
    @SuppressWarnings("unchecked")
	public void addTables(Collection tables) {
        this.database.addTables(tables);
    }

    /**
     * find table, this method finds table by name
     * @param tableName name of the table need to find
     * @return returns the table that its name is tableName 
     */
	// checked by vmc @355
    public Table findTable(String tableName) {
        return this.database.findTable(tableName);
    }

    /**
     * 
     * @return returns all tables of current using database schema
     */
	// checked by vmc @355
    public Table[] getTables() {
        return this.database.getTables();
    }

    /**
     * Create entity instance for table, this methods creates entity instance for table 
     * whose name is tableName
     * @param tableName name of the table need to create entity instance for
     * @return entity instance of table whose name is tableName
     * @throws SqlDynaException
     */
	// checked by vmc @355
    public EntityInstance createEntityInstanceFor(String tableName) throws SqlDynaException {
        return createEntityInstanceFor(tableName,false);
    }

    /**
     * Create entity instance for table, this methods creates entity instance for table 
     * whose name is tableName
     * @param tableName name of the table need to create entity instance for
     * @param caseSensitive if it is true methods will find table in database by name case sensitively
     * @return entity instance of the table that its name is tableName
     * @throws SqlDynaException
     */
	// checked by vmc @355
    public EntityInstance createEntityInstanceFor(String tableName, boolean caseSensitive) throws SqlDynaException {
        return new EntityInstance(this.database.createDynaBeanFor(tableName, caseSensitive).getDynaClass());
    }

    /**
     * Merge database, this method merges current database and the given database
     * @param database the database need to merged with current database
     * @throws ModelException
     */
	// checked by vmc @355
    public void mergeDatabase(Database database) throws ModelException {
        this.database.mergeWith(database);
    }
}
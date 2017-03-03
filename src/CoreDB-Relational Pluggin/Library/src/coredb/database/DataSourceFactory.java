package coredb.database;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.DataSource;

// checkecd by vmc @196
public class DataSourceFactory implements DataSource{
	
	private String dbURL;
	private String username;
	private String password;
	private PrintWriter out;
	private int seconds;

	/**
	 * @param dbURL the URL use to build db connection
	 * @param username the username used to connect db
	 * @param password the password used to connect db
	 */
	// checkecd by vmc @196
	public DataSourceFactory ( String dbURL,String username, String password){
		this.dbURL = dbURL;
		this.username = username;
		this.password = password;
	}

    /**
     * @return return database connection
     */
	// checkecd by vmc @196
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(dbURL, username, password);
	}

    /**
     * @param username user name used to connect database
     * @param password password used to connect database
     * @return database connection
     */
	// checkecd by vmc @196
	public Connection getConnection(String username, String password)
			throws SQLException {
		Connection connection = DriverManager.getConnection(dbURL, username, password);
		return connection;
	}

    /**
     * @return return the LogWriter
     */
	// checkecd by vmc @196
	public PrintWriter getLogWriter() throws SQLException {
		return out;
	}

    /**
     * set the LogWriter
     */
	// checkecd by vmc @196
	public int getLoginTimeout() throws SQLException {
		return seconds;
	}

    /**
     * set the LogWriter
     * @param out the LogWriter intend to set
     */
	// checkecd by vmc @196
	public void setLogWriter(PrintWriter out) throws SQLException {
		this.out =out;		
	}

	/**
     * set the LoginTimeout
     * @param seconds the time intend to set as LoginTimeout
     */
	public void setLoginTimeout(int seconds) throws SQLException {
		this.seconds =seconds;		
	}
    /**
     * @return default return value is false
     */
	// checkecd by vmc @196
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		return false;
	}

	/**
     * @return default return value is null
     */
	// checkecd by vmc @196
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		return null;
	}
}

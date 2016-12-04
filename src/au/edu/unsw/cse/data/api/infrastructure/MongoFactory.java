package au.edu.unsw.cse.data.api.infrastructure;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.glassfish.hk2.api.Factory;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class MongoFactory implements Factory<MongoDatabase> {

	private MongoClient mongoClient;
	private String dbName;

	public MongoFactory() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream input = classLoader.getResourceAsStream("mongo.properties");
		Properties properties = new Properties();
		try {
			properties.load(input);
			dbName = properties.getProperty("db");
			mongoClient = new MongoClient(properties.getProperty("mongohost"),
					Integer.parseInt(properties.getProperty("mongoport")));
		} catch (IOException e) {

		}
	}

	@Override
	public void dispose(MongoDatabase mongoDatabase) {
		mongoClient.close();
	}

	@Override
	public MongoDatabase provide() {
		return mongoClient.getDatabase(dbName);
	}
}

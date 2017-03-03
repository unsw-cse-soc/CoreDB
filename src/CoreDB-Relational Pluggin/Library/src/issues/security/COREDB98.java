/**
 * 
 */
package issues.security;

import java.sql.SQLException;

import coredb.config.Configuration.User_Token_Table;
import coredb.security.TokenFactory;
import coredb.utils.Describer;

/**
 * @author sean
 *
 */
public class COREDB98 {
	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		TestingSetUp test = new TestingSetUp();
		test.setup();
		
		// Remove default grant tokens from user twice
    	test.printTag("\n[Remove Default Grant Tokens From User]");
    	test.iecs.removeTokensFromUser(test.root, test.user1, TokenFactory.getDefaultGrantTokens(test.iecs.getDatabaseConnection()));
    	test.iecs.removeTokensFromUser(test.root, test.user1, TokenFactory.getDefaultGrantTokens(test.iecs.getDatabaseConnection()));
    	Describer.describeTable(test.iecs, User_Token_Table.makeUserTokenTableName());
    	test.printTag("\n[/Remove Default Grant Tokens From User]");
    	
    	// Roll back : Remove default grant tokens from user twice
    	test.printTag("\n[Roll Back : Remove Default Grant Tokens From User]");
    	test.iecs.addTokensToUser(test.root, test.user1, TokenFactory.getDefaultGrantTokens(test.iecs.getDatabaseConnection()));
    	test.iecs.addTokensToUser(test.root, test.user1, TokenFactory.getDefaultGrantTokens(test.iecs.getDatabaseConnection()));
    	Describer.describeTable(test.iecs, User_Token_Table.makeUserTokenTableName());
    	test.printTag("\n[/Roll Back :Remove Default Grant Tokens From User]");

	}

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.mode;

import java.util.List;

import coredb.database.DatabaseConnection;
import coredb.security.TokenFactory;
import coredb.unit.EntityInstance;


/**
 *
 * @author vmc
 */
public class UserDataInformationForMode {
	public static void initializeUserInfo(DatabaseConnection databaseConnection, Mode mode) {
		if (Mode.TRACING == mode) {
			UserPermission.deployUserPermission(databaseConnection);
		} else if (Mode.SECURITY == mode) {
			RootPermission.initializeRootPermission(databaseConnection);
		} else {
			System.out.printf("%10s %20s\n",mode,"is not supported");
		}
	}

	public static void initializeDefaultUserTokenInfo(DatabaseConnection databaseConnection, Mode mode) {
		if (Mode.TRACING == mode) {
			// DO NOTHING
		} else if (Mode.SECURITY == mode) {
			List<EntityInstance> tokens = TokenFactory.getDefaultTokens(databaseConnection);
			databaseConnection.create(tokens);
		} else {
			System.out.printf("%10s %20s\n",mode,"is not supported");
		}
	}
}

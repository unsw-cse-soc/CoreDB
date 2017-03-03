/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.security;

import coredb.config.Configuration;
import coredb.database.DatabaseConnection;
import coredb.unit.CRUD;
import coredb.unit.EntityInstance;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author vmc
 */
public class TokenFactory {

	public static class DefaultTokens{
		private static final String GRANTGROUP$R$G           = "grantgroup$R$G";
		private static final String DENYGROUP$R$D            = "denygroup$R$D";
		private static final String GRANTGROUP$C$G           = "grantgroup$C$G";
		private static final String DENYGROUP$C$D            = "denygroup$C$D";
		private static final String GRANTGROUP$U$G           = "grantgroup$U$G";
		private static final String DENYGROUP$U$D            = "denygroup$U$D";
		private static final String GRANTGROUP$D$G           = "grantgroup$D$G";
		private static final String DENYGROUP$D$D            = "denygroup$D$D";

		private static EntityInstance TOKEN_GRANTGROUP$C$G   = null;
		private static EntityInstance TOKEN_GRANTGROUP$R$G   = null;
		private static EntityInstance TOKEN_GRANTGROUP$U$G   = null;
		private static EntityInstance TOKEN_GRANTGROUP$D$G   = null;
		private static EntityInstance TOKEN_DENYGROUP$C$D    = null;
		private static EntityInstance TOKEN_DENYGROUP$R$D    = null;
		private static EntityInstance TOKEN_DENYGROUP$U$D    = null;
		private static EntityInstance TOKEN_DENYGROUP$D$D    = null;

		public static EntityInstance getTOKEN_GRANTGROUP$C$G(DatabaseConnection databaseConnection) {
			TOKEN_GRANTGROUP$C$G = databaseConnection.createEntityInstanceFor(Configuration.Token_Info_Table.makeTokenInfoTableName(), false);
			TOKEN_GRANTGROUP$C$G.set(Configuration.Token_Info_Table.COLUMN_TOKENID, GRANTGROUP$C$G);
			TOKEN_GRANTGROUP$C$G.set(Configuration.Token_Info_Table.COLUMN_TOKENNAME, GRANTGROUP$C$G);
			TOKEN_GRANTGROUP$C$G.set(Configuration.Token_Info_Table.COLUMN_GROUP, Configuration.DEFAULT);
			TOKEN_GRANTGROUP$C$G.set(Configuration.Token_Info_Table.COLUMN_CRUDTYPE, CRUD.CREATE);
			TOKEN_GRANTGROUP$C$G.set(Configuration.Token_Info_Table.COLUMN_PERMISSION, true);
			return TOKEN_GRANTGROUP$C$G;
		}

		public static EntityInstance getTOKEN_GRANTGROUP$R$G(DatabaseConnection databaseConnection) {
			TOKEN_GRANTGROUP$R$G = databaseConnection.createEntityInstanceFor(Configuration.Token_Info_Table.makeTokenInfoTableName(), false);
			TOKEN_GRANTGROUP$R$G.set(Configuration.Token_Info_Table.COLUMN_TOKENID, GRANTGROUP$R$G);
			TOKEN_GRANTGROUP$R$G.set(Configuration.Token_Info_Table.COLUMN_TOKENNAME,GRANTGROUP$R$G);
			TOKEN_GRANTGROUP$R$G.set(Configuration.Token_Info_Table.COLUMN_GROUP, Configuration.DEFAULT);
			TOKEN_GRANTGROUP$R$G.set(Configuration.Token_Info_Table.COLUMN_CRUDTYPE, CRUD.READ);
			TOKEN_GRANTGROUP$R$G.set(Configuration.Token_Info_Table.COLUMN_PERMISSION, true);
			return TOKEN_GRANTGROUP$R$G;
		}

		public static EntityInstance getTOKEN_GRANTGROUP$U$G(DatabaseConnection databaseConnection) {
			TOKEN_GRANTGROUP$U$G = databaseConnection.createEntityInstanceFor(Configuration.Token_Info_Table.makeTokenInfoTableName(), false);
			TOKEN_GRANTGROUP$U$G.set(Configuration.Token_Info_Table.COLUMN_TOKENID, GRANTGROUP$U$G);
			TOKEN_GRANTGROUP$U$G.set(Configuration.Token_Info_Table.COLUMN_TOKENNAME, GRANTGROUP$U$G);
			TOKEN_GRANTGROUP$U$G.set(Configuration.Token_Info_Table.COLUMN_GROUP, Configuration.DEFAULT);
			TOKEN_GRANTGROUP$U$G.set(Configuration.Token_Info_Table.COLUMN_CRUDTYPE, CRUD.UPDATE);
			TOKEN_GRANTGROUP$U$G.set(Configuration.Token_Info_Table.COLUMN_PERMISSION, true);
			return TOKEN_GRANTGROUP$U$G;
		}

		public static EntityInstance getTOKEN_GRANTGROUP$D$G(DatabaseConnection databaseConnection) {
			TOKEN_GRANTGROUP$D$G = databaseConnection.createEntityInstanceFor(Configuration.Token_Info_Table.makeTokenInfoTableName(), false);
			TOKEN_GRANTGROUP$D$G.set(Configuration.Token_Info_Table.COLUMN_TOKENID, GRANTGROUP$D$G);
			TOKEN_GRANTGROUP$D$G.set(Configuration.Token_Info_Table.COLUMN_TOKENNAME, GRANTGROUP$D$G);
			TOKEN_GRANTGROUP$D$G.set(Configuration.Token_Info_Table.COLUMN_GROUP, Configuration.DEFAULT);
			TOKEN_GRANTGROUP$D$G.set(Configuration.Token_Info_Table.COLUMN_CRUDTYPE, CRUD.DELETE);
			TOKEN_GRANTGROUP$D$G.set(Configuration.Token_Info_Table.COLUMN_PERMISSION, true);
			return TOKEN_GRANTGROUP$D$G;
		}

		public static EntityInstance getTOKEN_DENYGROUP$C$D(DatabaseConnection databaseConnection) {
			TOKEN_DENYGROUP$C$D = databaseConnection.createEntityInstanceFor(Configuration.Token_Info_Table.makeTokenInfoTableName(), false);
			TOKEN_DENYGROUP$C$D.set(Configuration.Token_Info_Table.COLUMN_TOKENID, DENYGROUP$C$D);
			TOKEN_DENYGROUP$C$D.set(Configuration.Token_Info_Table.COLUMN_TOKENNAME, DENYGROUP$C$D);
			TOKEN_DENYGROUP$C$D.set(Configuration.Token_Info_Table.COLUMN_GROUP, Configuration.DEFAULT);
			TOKEN_DENYGROUP$C$D.set(Configuration.Token_Info_Table.COLUMN_CRUDTYPE, CRUD.CREATE);
			TOKEN_DENYGROUP$C$D.set(Configuration.Token_Info_Table.COLUMN_PERMISSION, false);
			return TOKEN_DENYGROUP$C$D;
		}

		public static EntityInstance getTOKEN_DENYGROUP$R$D(DatabaseConnection databaseConnection) {
			TOKEN_DENYGROUP$R$D = databaseConnection.createEntityInstanceFor(Configuration.Token_Info_Table.makeTokenInfoTableName(), false);
			TOKEN_DENYGROUP$R$D.set(Configuration.Token_Info_Table.COLUMN_TOKENID, DENYGROUP$R$D);
			TOKEN_DENYGROUP$R$D.set(Configuration.Token_Info_Table.COLUMN_TOKENNAME, DENYGROUP$R$D);
			TOKEN_DENYGROUP$R$D.set(Configuration.Token_Info_Table.COLUMN_GROUP, Configuration.DEFAULT);
			TOKEN_DENYGROUP$R$D.set(Configuration.Token_Info_Table.COLUMN_CRUDTYPE, CRUD.READ);
			TOKEN_DENYGROUP$R$D.set(Configuration.Token_Info_Table.COLUMN_PERMISSION, false);
			return TOKEN_DENYGROUP$R$D;
		}

		public static EntityInstance getTOKEN_DENYGROUP$U$D(DatabaseConnection databaseConnection) {
			TOKEN_DENYGROUP$U$D = databaseConnection.createEntityInstanceFor(Configuration.Token_Info_Table.makeTokenInfoTableName(), false);
			TOKEN_DENYGROUP$U$D.set(Configuration.Token_Info_Table.COLUMN_TOKENID, DENYGROUP$U$D);
			TOKEN_DENYGROUP$U$D.set(Configuration.Token_Info_Table.COLUMN_TOKENNAME, DENYGROUP$U$D);
			TOKEN_DENYGROUP$U$D.set(Configuration.Token_Info_Table.COLUMN_GROUP, Configuration.DEFAULT);
			TOKEN_DENYGROUP$U$D.set(Configuration.Token_Info_Table.COLUMN_CRUDTYPE, CRUD.UPDATE);
			TOKEN_DENYGROUP$U$D.set(Configuration.Token_Info_Table.COLUMN_PERMISSION, false);
			return TOKEN_DENYGROUP$U$D;
		}

		public static EntityInstance getTOKEN_DENYGROUP$D$D(DatabaseConnection databaseConnection) {
			TOKEN_DENYGROUP$D$D = databaseConnection.createEntityInstanceFor(Configuration.Token_Info_Table.makeTokenInfoTableName(), false);
			TOKEN_DENYGROUP$D$D.set(Configuration.Token_Info_Table.COLUMN_TOKENID, DENYGROUP$D$D);
			TOKEN_DENYGROUP$D$D.set(Configuration.Token_Info_Table.COLUMN_TOKENNAME, DENYGROUP$D$D);
			TOKEN_DENYGROUP$D$D.set(Configuration.Token_Info_Table.COLUMN_GROUP, Configuration.DEFAULT);
			TOKEN_DENYGROUP$D$D.set(Configuration.Token_Info_Table.COLUMN_CRUDTYPE, CRUD.DELETE);
			TOKEN_DENYGROUP$D$D.set(Configuration.Token_Info_Table.COLUMN_PERMISSION, false);
			return TOKEN_DENYGROUP$D$D;
		}
	}

	public static List<EntityInstance> getDefaultGrantTokens(DatabaseConnection databaseConnection){
		List<EntityInstance> tokenInstances = new LinkedList<EntityInstance>();
		tokenInstances.add(DefaultTokens.getTOKEN_GRANTGROUP$C$G(databaseConnection));
		tokenInstances.add(DefaultTokens.getTOKEN_GRANTGROUP$R$G(databaseConnection));
		tokenInstances.add(DefaultTokens.getTOKEN_GRANTGROUP$U$G(databaseConnection));
		tokenInstances.add(DefaultTokens.getTOKEN_GRANTGROUP$D$G(databaseConnection));
		return tokenInstances;
	}
	
	public static List<EntityInstance> getDefaultDenyTokens(DatabaseConnection databaseConnection){
		List<EntityInstance> tokenInstances = new LinkedList<EntityInstance>();
		tokenInstances.add(DefaultTokens.getTOKEN_DENYGROUP$C$D(databaseConnection));
		tokenInstances.add(DefaultTokens.getTOKEN_DENYGROUP$R$D(databaseConnection));
		tokenInstances.add(DefaultTokens.getTOKEN_DENYGROUP$U$D(databaseConnection));
		tokenInstances.add(DefaultTokens.getTOKEN_DENYGROUP$D$D(databaseConnection));
		return tokenInstances;
	}
	
	public static List<EntityInstance> getDefaultTokens(DatabaseConnection databaseConnection){
		List<EntityInstance> tokenInstances = new LinkedList<EntityInstance>();
		
        tokenInstances.addAll(getDefaultGrantTokens(databaseConnection));
        tokenInstances.addAll(getDefaultDenyTokens(databaseConnection));
		
		return tokenInstances;
	}
}

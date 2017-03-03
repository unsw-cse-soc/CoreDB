/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.tracing;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.beanutils.DynaProperty;

import coredb.config.Configuration;
import coredb.database.DatabaseConnection;
import coredb.database.DatabaseConnectionHelper;
import coredb.sql.CompoundStatementAuto;
import coredb.sql.CompoundStatementManual;
import coredb.sql.ConditionalStatement;
import coredb.sql.Q;
import coredb.sql.SQLFactory;
import coredb.sql.SQLStatement;
import coredb.unit.AttributeClass;
import coredb.unit.CRUD;
import coredb.unit.EntityClass;
import coredb.unit.EntityInstance;
import coredb.utils.HelperWithTracing;

/**
 *
 * @author vmc
 */
public class TracingCSN {

	private DynaProperty property_minCSN    = new DynaProperty(Configuration.Tracing_Table.COLUMN_MINCSN,       Integer.class);
	private DynaProperty property_maxCSN    = new DynaProperty(Configuration.Tracing_Table.COLUMN_MAXCSN,       Integer.class);
	private DynaProperty property_active    = new DynaProperty(Configuration.Tracing_Table.COLUMN_CRUD,       Character.class);
	private DynaProperty property_timeStamp = new DynaProperty(Configuration.Tracing_Table.COLUMN_TIMESTAMP, Timestamp.class);
	private DynaProperty property_userID    = new DynaProperty(Configuration.Coredb_CSN_Table.COLUMN_USERID, String.class);

	/**
	 *
	 *
	 * Trace a turple data in table according with its entityId and the range of change set number
	 *
	 * @param databaseConnection the database connection specified in the entity controller
	 * @param baseTableName  the name of base table
	 * @param mappingId   the identifier of a turple in table.
	 * @param startCSN   the current change set number user try to trace between it and maximum change set number
	 * @param endCSN	 the maximum change set number user try to trace between it and current change set number
	 * @param userId     user id
	 * @return a list of tracing record.
	 */
	// checked by vmc @932
	public List<EntityInstance> tracingUniqueEntities(DatabaseConnection databaseConnection,
		String baseTableName, String mappingId, int startCSN, int endCSN, String userId) {
		int initCSN					 = getInitialCSN(databaseConnection, baseTableName, mappingId);

		EntityInstance initialEntity = buildInitialEntity(databaseConnection,baseTableName,mappingId,startCSN);

		return buildUpdatedEntities(databaseConnection,initialEntity,baseTableName,mappingId,initCSN,startCSN,endCSN);
	}

	/**
	 *
	 * @param databaseConnection
	 * @param baseTableName
	 * @param mappingId
	 * @return
	 */
	// checked by vmc @911
	private static int getInitialCSN(DatabaseConnection databaseConnection, String baseTableName, String mappingId) {
		String mappingTableName					= Configuration.Tracing_Table.makeMappingTableNameWithPrefixSuffix(baseTableName);
		CompoundStatementAuto compoundStatement = new CompoundStatementAuto(Q.AND);
		String mappingId_ColumnName				= Configuration.Tracing_Table.makeMappingTableColumnNameCoredbId(baseTableName);
		AttributeClass column_MappingId         = DatabaseConnection.getAttributeClass(mappingTableName, mappingId_ColumnName);
		compoundStatement.addCompoundStatement(new ConditionalStatement(column_MappingId, Q.E, mappingId));
		String sql = SQLFactory.makeSQL_findEntities(mappingTableName, Configuration.Tracing_Table.getColumnMinCSN(),compoundStatement);
		List<EntityInstance> mappingInstances = DatabaseConnectionHelper.executeArbitraryQueryForCoreDB(databaseConnection, sql);
		return (Integer)(mappingInstances.get(0).get(Configuration.Tracing_Table.getColumnMinCSN()));
	}
	/**
	 *
	 * @param databaseConnection
	 * @param baseTableName
	 * @param mappingId
	 * @param startCSN
	 * @return
	 */
	private EntityInstance buildInitialEntity(DatabaseConnection databaseConnection, String baseTableName, String mappingId, int startCSN) {

		String tracingTableName                   = Configuration.Tracing_Table.makeTracingTableNameWithPrefixSuffix(baseTableName);
		AttributeClass tracing_MappingId_Column   = DatabaseConnection.getAttributeClass(tracingTableName, Configuration.Tracing_Table.makeMappingTableColumnNameCoredbId(baseTableName));
		AttributeClass tracing_CSN_Column         = DatabaseConnection.getAttributeClass(tracingTableName, Configuration.Tracing_Table.COLUMN_CSN);
		AttributeClass tracing_ColumnName_Column  = DatabaseConnection.getAttributeClass(tracingTableName, Configuration.Tracing_Table.COLUMN_NAME);
		AttributeClass tracing_CURD_Column        = DatabaseConnection.getAttributeClass(tracingTableName, Configuration.Tracing_Table.COLUMN_CRUD);


		EntityClass    entityClass   = databaseConnection.getEntityClass(baseTableName, false);
		EntityInstance initialEntity = databaseConnection.createEntityInstanceFor(baseTableName, false);
		
		for (AttributeClass ac : entityClass.getAttributeClassList()) {
			CompoundStatementManual csh = new CompoundStatementManual();
			csh.addConditionalStatement(new ConditionalStatement(tracing_MappingId_Column, Q.E, mappingId));
			csh.addConditionalStatement(Q.AND);
			csh.addConditionalStatement(new ConditionalStatement(tracing_ColumnName_Column, Q.E, ac.getName()));
			csh.addConditionalStatement(Q.AND);
			csh.addConditionalStatement(Q.LEFT_PARENTHESIS);
			csh.addConditionalStatement(new ConditionalStatement(tracing_CSN_Column, Q.LE, startCSN));
			csh.addConditionalStatement(Q.AND);
			csh.addConditionalStatement(Q.LEFT_PARENTHESIS);
			csh.addConditionalStatement(new ConditionalStatement(tracing_CURD_Column, Q.E,CRUD.CREATE));
			csh.addConditionalStatement(Q.OR);
			csh.addConditionalStatement(new ConditionalStatement(tracing_CURD_Column, Q.E,CRUD.UPDATE));
			csh.addConditionalStatement(Q.RIGHT_PARENTHESIS);
			csh.addConditionalStatement(Q.RIGHT_PARENTHESIS);
			csh.addConditionalStatement(Q.ORDER_BY);
			csh.addConditionalStatement(tracing_CSN_Column.getName());
			csh.addConditionalStatement(Q.DESC);
			csh.addConditionalStatement(Q.LIMIT);
			csh.addConditionalStatement("1");		

			String initSQL = SQLFactory.makeSQL_findEntities(tracingTableName,(SQLStatement)csh);

//System.out.println("initSQL "+initSQL);

			List<EntityInstance> initEntities = DatabaseConnectionHelper.executeArbitraryQueryForCoreDB(databaseConnection, "BUILD_INIT_ENITTY",initSQL);
			if (initEntities.size() == 1) {
				EntityInstance unit = initEntities.get(0);
//System.out.print("< "); Describer.describeEntityInstance(unit);
				String name		= (String)unit.get(Configuration.Tracing_Table.getColumnName());
//vmc-byte[]	String value	= (String)unit.get(Configuration.Tracing_Table.getColumnValue());
				Object value	= unit.get(Configuration.Tracing_Table.getColumnValue());
				if (ac.getName().equalsIgnoreCase(name)) initialEntity.set(name,value);
				else {
					System.out.println(ac.getName() + " "+ name);
					System.out.println("VMC:SOME THING IS WRONG HERE");
				}
			} else {
				System.err.printf("THE SQL STATEMENT : %20s SHOULD RETURN AN UNIQUE RESULT\n", initSQL); System.exit(0);
			}
//System.out.print("+ "); Describer.describeEntityInstance(initialEntity);
		}
//System.out.println("FINISH CONTRUCTING");
//Describer.describeEntityInstance(initialEntity);
//System.out.println("/FINISH CONTRUCTING");
		return initialEntity;
	}

	private List<EntityInstance> buildUpdatedEntities(DatabaseConnection databaseConnection, EntityInstance initialEntity,
		String baseTableName, String mappingId, int initCSN, int startCSN, int endCSN) {

		List<EntityInstance> entityInstancePerCSNs = new LinkedList<EntityInstance>();

		String tracingTableName                   = Configuration.Tracing_Table.makeTracingTableNameWithPrefixSuffix(baseTableName);
		AttributeClass tracing_MappingId_Column   = DatabaseConnection.getAttributeClass(tracingTableName, Configuration.Tracing_Table.makeMappingTableColumnNameCoredbId(baseTableName));
		AttributeClass tracing_CSN_Column         = DatabaseConnection.getAttributeClass(tracingTableName, Configuration.Tracing_Table.COLUMN_CSN);
		//
		CompoundStatementManual cslm = new CompoundStatementManual();
		cslm.addConditionalStatement(new ConditionalStatement(tracing_MappingId_Column,Q.E,mappingId));
		cslm.addConditionalStatement(Q.AND);
		cslm.addConditionalStatement(new ConditionalStatement(tracing_CSN_Column,Q.GE,startCSN));
		cslm.addConditionalStatement(Q.AND);
		cslm.addConditionalStatement(new ConditionalStatement(tracing_CSN_Column,Q.LE,endCSN));
		cslm.addConditionalStatement(Q.ORDER_BY);
		cslm.addConditionalStatement(tracing_CSN_Column.getName());
		cslm.addConditionalStatement(Q.ASC);
		//
		String minMaxSQL = SQLFactory.makeSQL_findEntities(tracingTableName,cslm);

//System.out.println("minMaxSQL " + minMaxSQL);

		List<EntityInstance> tracingEntityInstances = DatabaseConnectionHelper.executeArbitraryQueryForCoreDB(databaseConnection, minMaxSQL);

		EntityInstance previousEntityInstancePerCSN	= null;
		String tableName = Configuration.makeLazyDynaClassName(initialEntity.getDynaClass().getName());
		EntityInstance currentEntityInstancePerCSN			=
			EntityInstance.appendColumns(initialEntity.clone(),tableName,new DynaProperty[]{property_minCSN, property_maxCSN, property_active, property_timeStamp, property_userID});

		for (EntityInstance tracingEntityInstance : tracingEntityInstances ) {

//System.out.print("* ");Describer.describeEntityInstance(currentEntityInstancePerCSN);
//System.out.print("- ");Describer.describeEntityInstance(tracingEntityInstance);

			Integer previousCSN = previousEntityInstancePerCSN == null ? null : (Integer)previousEntityInstancePerCSN.get(Configuration.Tracing_Table.COLUMN_MAXCSN);
			Integer currentCSN	= (Integer) tracingEntityInstance.get(Configuration.Tracing_Table.getColumnCSN());
			Character crud		= tracingEntityInstance.get(Configuration.Tracing_Table.getColumnCrud()).toString().charAt(0);
//vmc:timestamp
//			Timestamp timestamp = Timestamp.valueOf(tracingEntityInstance.get("timestamp").toString());
//vmc:timestamp
			/**
			 * the following code blocks might be used
			 */
//			/*
			ConditionalStatement csnStatement = new ConditionalStatement(DatabaseConnection.getAttributeClass(Configuration.Coredb_CSN_Table.getCSNTableName(), Configuration.Coredb_CSN_Table.COLUMN_CSN), Q.E, currentCSN);
			EntityInstance csnInstance = databaseConnection.read(Configuration.Coredb_CSN_Table.getCSNTableName(), csnStatement.toSQLStatement()).get(0);
			Timestamp timestamp        = Timestamp.valueOf(csnInstance.get(Configuration.Tracing_Table.COLUMN_TIMESTAMP).toString());
			String userId    = csnInstance.get(Configuration.Tracing_Table.COLUMN_USERID).toString();
//			*/
			
//System.out.println("previousCSN "+previousCSN);
//System.out.println("currentCSN  "+currentCSN);

			String name		= (String)tracingEntityInstance.get(Configuration.Tracing_Table.getColumnName());
//vmc-byte[]String value	= (String)tracingEntityInstance.get(Configuration.Tracing_Table.getColumnValue());
			//String value	= new String((byte[])tracingEntityInstance.get(Configuration.Tracing_Table.getColumnValue()));
			//byte[] value	= (byte[])tracingEntityInstance.get(Configuration.Tracing_Table.getColumnValue());
			AttributeClass ac = DatabaseConnection.getAttributeClass(baseTableName,name);
			Object value	= HelperWithTracing.convertByteArrayToValidData( ac, (byte[])tracingEntityInstance.get(Configuration.Tracing_Table.getColumnValue()));
//System.out.printf("name %7s value %7s \n" , name,value);
			if (previousEntityInstancePerCSN == null) {
				previousEntityInstancePerCSN = currentEntityInstancePerCSN;
				previousEntityInstancePerCSN.set(name, value);
				previousEntityInstancePerCSN.set(Configuration.Tracing_Table.COLUMN_MINCSN,       initCSN);
				previousEntityInstancePerCSN.set(Configuration.Tracing_Table.COLUMN_MAXCSN,       currentCSN);
				previousEntityInstancePerCSN.set(Configuration.Tracing_Table.COLUMN_CRUD,       crud);
				previousEntityInstancePerCSN.set(Configuration.Tracing_Table.COLUMN_TIMESTAMP, timestamp);
				previousEntityInstancePerCSN.set(Configuration.Coredb_CSN_Table.COLUMN_USERID, userId);
				entityInstancePerCSNs.add(previousEntityInstancePerCSN);
//System.out.print("n ");Describer.describeEntityInstance(currentEntityInstancePerCSN);
			} else if (!currentCSN.equals(previousCSN))  {
				previousEntityInstancePerCSN = currentEntityInstancePerCSN.clone();
				previousEntityInstancePerCSN.set(name, value);
				previousEntityInstancePerCSN.set(Configuration.Tracing_Table.COLUMN_MINCSN,       initCSN);
				previousEntityInstancePerCSN.set(Configuration.Tracing_Table.COLUMN_MAXCSN,       currentCSN);
				previousEntityInstancePerCSN.set(Configuration.Tracing_Table.COLUMN_CRUD,       crud);
				previousEntityInstancePerCSN.set(Configuration.Tracing_Table.COLUMN_TIMESTAMP, timestamp);
				previousEntityInstancePerCSN.set(Configuration.Coredb_CSN_Table.COLUMN_USERID, userId);
				entityInstancePerCSNs.add(previousEntityInstancePerCSN);
//System.out.print("! ");Describer.describeEntityInstance(previousEntityInstancePerCSN);
			} else {
//System.out.print("= ");Describer.describeEntityInstance(previousEntityInstancePerCSN);
				previousEntityInstancePerCSN.set(name, value);
			}
//System.out.println("....................................");
		}

//System.out.println("FINAL");
//Describer.describeEntityInstances(entityInstancePerCSNs);
//System.out.println("/FINAL");
		return entityInstancePerCSNs;
	}
}

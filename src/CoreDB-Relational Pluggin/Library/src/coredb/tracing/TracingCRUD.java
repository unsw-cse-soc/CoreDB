/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.tracing;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import coredb.config.Configuration;
import coredb.database.DatabaseConnection;
import coredb.database.DatabaseConnectionHelper;
import coredb.unit.Action;
import coredb.unit.AttributeClass;
import coredb.unit.CRUD;
import coredb.unit.EntityClass;
import coredb.unit.EntityInstance;
import coredb.utils.Helper;

// checked by vmc @923
public class TracingCRUD {
    /**
     * this method makes a list of actions to responds to Tracing CRUD processes. (IMPLEMENTED BY SEAN)
     * @param databaseConnection current working database Connection 
	 * @param instance_mappings
     * @param crud the action type (CRUD)
     * @param changeSetNumber the changeSetNumber of this transaction
     * @return a list of actions 
     */
	// checked by vmc @923
	public static List<Action> makeTracingInstances(DatabaseConnection databaseConnection, 
				List<EntityInstance[]> instance_mappings, Character crud, int changeSetNumber) {

		List<Action> actions       = new LinkedList<Action>();
		String baseTableName           = null;
		
		if(CRUD.CREATE.equals(crud)){
			for(EntityInstance[] instance_mapping : instance_mappings){
				EntityInstance entityInstance		= instance_mapping[0];
				EntityInstance mappingTableInstance = instance_mapping[1];

				/** BASE TABLE	  */
                baseTableName				= entityInstance.getDynaClass().getName();
				Action baseTableAction  = new Action(crud, entityInstance);
	        	actions.add(baseTableAction);

				/** MAPPING TABLE */
				String mappingTableName				 = Configuration.Tracing_Table.makeMappingTableNameWithPrefixSuffix(baseTableName);
				String mappingTableColumnCoredbId	 = Configuration.Tracing_Table.makeMappingTableColumnNameCoredbId(baseTableName);

String SELECT_SQL = entityInstance.makeSelectSqlStatementForMappingEntityInstance(databaseConnection);
List<EntityInstance> entityInstances = DatabaseConnectionHelper.executeArbitraryQueryForCoreDB(databaseConnection, SELECT_SQL);

EntityInstance mappingInstance = null;
if (entityInstances.size() ==1) {
				mappingInstance       = databaseConnection.read(mappingTableName, entityInstance.makeSQLOnIdentifiableAttributes(databaseConnection)).get(0);
				mappingInstance.set(Configuration.Tracing_Table.COLUMN_CRUD,  crud);
				mappingInstance.set(Configuration.Tracing_Table.COLUMN_MAXCSN,  changeSetNumber);
				actions.add(new Action(CRUD.UPDATE,mappingInstance));
} else if (entityInstances.size()==0){
				mappingInstance       = databaseConnection.createEntityInstanceFor(mappingTableName,false);
                mappingInstance.set(mappingTableColumnCoredbId, UUID.randomUUID().toString());
				mappingInstance.set(Configuration.Tracing_Table.COLUMN_CRUD,  crud);
				mappingInstance.set(Configuration.Tracing_Table.COLUMN_MINCSN,  changeSetNumber);
				mappingInstance.set(Configuration.Tracing_Table.COLUMN_MAXCSN,  changeSetNumber);
				AttributeClass[] acl = databaseConnection.getEntityClass(baseTableName, false).getAttributeClassList();
				for (AttributeClass ac : acl) {
					if (ac.isPrimaryKey()) mappingInstance.set(ac.getName(), entityInstance.get(ac.getName()));
				}
				actions.add(new Action(crud,mappingInstance));
}


/** DELETE TABLE */
String DELETE_SQL = entityInstance.makeDeleteSqlStatementForDeletedEntityInstance(databaseConnection);
int size = databaseConnection.executeArbitraryUpdate(DELETE_SQL);



  				/** TRACING TABLE */
	        	EntityClass entityClass				 = databaseConnection.getEntityClass(baseTableName, false);
	        	AttributeClass[] attributeClasses    = entityClass.getAttributeClassList();
				String uuid							 = (String)mappingInstance.get(mappingTableColumnCoredbId);
	        	actions.addAll(makeTracingCRUDActions(databaseConnection, uuid,entityInstance, attributeClasses, crud, changeSetNumber));
	         }
		} else if(CRUD.READ.equals(crud)){
			for(EntityInstance[] instance_mapping : instance_mappings){
				EntityInstance entityInstance		= instance_mapping[0];
				EntityInstance mappingInstance		= instance_mapping[1];

                baseTableName						= entityInstance.getDynaClass().getName();

				/** MAPPING TABLE */
				mappingInstance.set(Configuration.Tracing_Table.COLUMN_CRUD,  crud);
				mappingInstance.set(Configuration.Tracing_Table.COLUMN_MAXCSN,  changeSetNumber);
	        	actions.add(new Action(crud, mappingInstance));
	        	
				/** TRACING TABLE */
	        	EntityClass entityClass             = databaseConnection.getEntityClass(baseTableName, false);
	        	AttributeClass[] attributeClasses   = entityClass.getAttributeClassList();
				String uuid							= (String)mappingInstance.get(Configuration.Tracing_Table.makeMappingTableColumnNameCoredbId(baseTableName));
  	        	actions.addAll(makeTracingCRUDActions(databaseConnection, uuid, entityInstance, attributeClasses, crud, changeSetNumber));
			}
		} else if(CRUD.UPDATE.equals(crud)){
			
			for(EntityInstance[] instance_mapping : instance_mappings){
				EntityInstance entityInstance		= instance_mapping[0];
				EntityInstance mappingInstance		= instance_mapping[1];

                /** BASE TABLE	  */
                baseTableName				        = entityInstance.getDynaClass().getName();
				Action baseTableAction  = new Action(crud, entityInstance);
	        	actions.add(baseTableAction);

                
	        	/** MAPPING TABLE */
				mappingInstance.set(Configuration.Tracing_Table.COLUMN_CRUD,  crud);
				mappingInstance.set(Configuration.Tracing_Table.COLUMN_MAXCSN,  changeSetNumber);
	        	actions.add(new Action(crud, mappingInstance));

				/** TRACING TABLE */
	        	EntityClass baseEntityClass         = databaseConnection.getEntityClass(baseTableName, false);
	        	AttributeClass[] attributeClasses   = Helper.getUpdatedColumns(databaseConnection, entityInstance, baseEntityClass).toArray(new AttributeClass[0]);
				String uuid							= (String)mappingInstance.get(Configuration.Tracing_Table.makeMappingTableColumnNameCoredbId(baseTableName));
  	        	actions.addAll(makeTracingCRUDActions(databaseConnection, uuid, entityInstance, attributeClasses, crud, changeSetNumber));
	         }
			 
		} else if(CRUD.DELETE.equals(crud)){
			for(EntityInstance[] instance_mapping : instance_mappings){
				EntityInstance entityInstance		= instance_mapping[0];
				EntityInstance mappingInstance		= instance_mapping[1];

				/** BASE TABLE	  */
                baseTableName						= entityInstance.getDynaClass().getName();
                actions.add(new Action(crud, entityInstance));

				/** MAPPING TABLE */
				mappingInstance.set(Configuration.Tracing_Table.COLUMN_CRUD,  crud);
				mappingInstance.set(Configuration.Tracing_Table.COLUMN_MAXCSN,  changeSetNumber);
	        	actions.add(new Action(CRUD.UPDATE, mappingInstance));
	        	

// <MAINTAINING DELETED TABLE>
String deletedTableName = Configuration.Delete_Table.makeDeteledTableName(entityInstance.getDynaClass().getName());
EntityInstance deletedEntityInstance = databaseConnection.createEntityInstanceFor(deletedTableName,false);
databaseConnection.create(EntityInstance.copy(entityInstance,deletedEntityInstance));
// </MAINTAINING DELETED TABLE>

				/** TRACING TABLE */
	        	EntityClass entityClass             = databaseConnection.getEntityClass(baseTableName, false);
	        	AttributeClass[] attributeClasses   = entityClass.getAttributeClassList();
				String uuid							= (String)mappingInstance.get(Configuration.Tracing_Table.makeMappingTableColumnNameCoredbId(baseTableName));
  	        	actions.addAll(makeTracingCRUDActions(databaseConnection, uuid, entityInstance, attributeClasses, crud, changeSetNumber));
			}
		} else {
			System.err.println("<Tracing> other actionTypes except crud have been implemented, should be fixed later");
		}

		return actions;
	}

	/**
	 * This method creates a list of actions applied on tracing table. (IMPLEMENTED BY SEAN)
	 * @param databaseConnection current working database Connection  
	 * @param entityInstance the EntityInstance of base table
	 * @param attributeClassList the list of attribute classes of base table
	 * @param crud action type
	 * @param changeSetNumber the changeSetNumber of this transaction
	 * @param userId the userID
	 * @return a list of actions applied on tracing table
	 */
	// checked by vmc @923
	private static Collection<? extends Action> makeTracingCRUDActions(DatabaseConnection databaseConnection, 
			String uuid,	EntityInstance entityInstance, AttributeClass[] attributeClasses,
			Character crud, int changeSetNumber) {
		List<Action> tracingActions   = new LinkedList<Action>();

		String baseTableName				  = entityInstance.getDynaClass().getName();
        String tracingTableName				  = Configuration.Tracing_Table.makeTracingTableNameWithPrefixSuffix(baseTableName);
		String mappingTableColumnNameCoredbId = Configuration.Tracing_Table.makeMappingTableColumnNameCoredbId(baseTableName);

		for(AttributeClass ac : attributeClasses){
            Object objectValue        = entityInstance.get(ac.getName());

            String valueString;
            if(objectValue != null) { valueString = objectValue.toString(); }
			else { valueString = ""; }

			byte[] value              = valueString.getBytes();
			EntityInstance entityInstanceTracing = databaseConnection.createEntityInstanceFor(tracingTableName, false);
  			entityInstanceTracing.set(mappingTableColumnNameCoredbId,			uuid);
			entityInstanceTracing.set(Configuration.Tracing_Table.COLUMN_NAME,	ac.getName());
			entityInstanceTracing.set(Configuration.Tracing_Table.COLUMN_VALUE,	value);
			entityInstanceTracing.set(Configuration.Tracing_Table.COLUMN_CSN,	changeSetNumber);
			entityInstanceTracing.set(Configuration.Tracing_Table.COLUMN_CRUD,crud);

            tracingActions.add(new Action(CRUD.CREATE, entityInstanceTracing));
		}
		return tracingActions;
	}
}

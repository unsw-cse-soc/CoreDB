/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.security;

import coredb.unit.EntityInstance;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author vmc
 */
public class ResultSecuritySystemList extends ResultSecurityPassedFailed {
	private List<EntityInstance> allPassedSystemEntities = new LinkedList<EntityInstance>();

	/**
	 * This method constructs a ResultSecuritySystemList object
	 * 
	 * @param allPassedEntities the list of passed(Security checking approval) EntityInstances
	 * @param allPassedSystemEntities the list of Passed System EntityInstances(System table data rows)
	 * @param allFailedEntities the list of failed(Security checking failed) EntityInstances
	 */
	// checked by vmc @1517
	public ResultSecuritySystemList(List<EntityInstance> allPassedEntities, List<EntityInstance> allPassedSystemEntities, List<EntityInstance> allFailedEntities) {
		super(allPassedEntities, allFailedEntities);
		this.allPassedSystemEntities = allPassedSystemEntities;
	}

	/**
	 * @return the allPassedEntities
	 */
	// checked by vmc @1517
	public List<EntityInstance> getAllPassedEntities() {
		return allPassedEntities;
	}

	/**
	 * @return the allFailedEntities
	 */
	// checked by vmc @1517
	public List<EntityInstance> getAllFailedEntities() {
		return allFailedEntities;
	}

	/**
	 * @return the allPassedSystemEntities
	 */
	// checked by vmc @1517
	public List<EntityInstance> getAllPassedSystemEntities() {
		return allPassedSystemEntities;
	}

}

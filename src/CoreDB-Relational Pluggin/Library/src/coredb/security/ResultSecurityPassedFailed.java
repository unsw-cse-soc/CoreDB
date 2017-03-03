/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.security;

import coredb.unit.EntityInstance;
import java.util.List;

/**
 *
 * @author vmc
 */
public class ResultSecurityPassedFailed {
	protected List<EntityInstance> allPassedEntities  = null;
	protected List<EntityInstance> allFailedEntities  = null;

	/**
	 * This method constructs a ResultSecurityPassedFailed object
	 * 
	 * @param allPassedEntities the list of passed(Security checking approval) EntityInstances
	 * @param allFailedEntities the list of failed(Security checking failed) EntityInstances
	 */
	// checked by vmc @1517
	public ResultSecurityPassedFailed(List<EntityInstance> allPassedEntities, List<EntityInstance> allFailedEntities) {
		this.allPassedEntities  = allPassedEntities;
		this.allFailedEntities  = allFailedEntities;
	}
}

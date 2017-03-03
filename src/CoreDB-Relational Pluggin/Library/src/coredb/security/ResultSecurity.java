/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.security;

import coredb.unit.EntityInstance;
import coredb.utils.Describer;
import java.util.List;

/**
 *
 * @author vmc
 */
public class ResultSecurity extends ResultSecurityPassedFailed {
	private Integer				 changeSetNumber	= null;

	/**
	 *This method construct a ResultSecurity object
	 *
	 * @param allPassedEntities the list of passed(Security checking approval) EntityInstances
	 * @param allFailedEntities the list of failed(Security checking failed) EntityInstances
	 * @param changeSetNumber the ChangeSetNumber of this transaction
	 */
	// checked by vmc @1176
	public ResultSecurity(List<EntityInstance> allPassedEntities, List<EntityInstance> allFailedEntities, int changeSetNumber) {
		super(allPassedEntities, allFailedEntities);
		this.changeSetNumber	= changeSetNumber;
	}

	/**
	 * This method construct a ResultSecurity object
	 * 
	 * @param rspf the ResultSecurityPassedFailed object
	 * @param changeSetNumber the ChangeSetNumber of this transaction
	 */
	// checked by vmc @1176
	public ResultSecurity(ResultSecurityPassedFailed rspf, int changeSetNumber) {
		super(rspf.allPassedEntities, rspf.allFailedEntities);
		this.changeSetNumber	= changeSetNumber;
	}

	/**
	 *This method gets the list of failed EntityInstances
	 *
	 * @return returns the list of failed EntityInstances
	 */
	// checked by vmc @1176
	public List<EntityInstance> getFailedEntities() { return super.allFailedEntities;  }

	/**
	 * This method gets the list of passed EntityInstances
	 * 
	 * @return return the list of passed EntityInstances
	 */
	// checked by vmc @1176
	public List<EntityInstance> getPassedEntities() { return super.allPassedEntities;  }


	/**
	 * This method gets changeSetNumber of this transaction
	 * 
	 * @return return the ChangeSetNumber of this transaction
	 */
	// checked by vmc @1176
	public int getChangeSetNumber()					{ return changeSetNumber; }

	/**
	 * This method prints PassedEntities
	 */
	// checked by vmc @1176
	public void describePassedEntities() {
		System.out.println("Describe the PassedEntities");
		Describer.describeEntityInstances(allPassedEntities);
	}

	/**
	 * This method prints FailedEntities
	 */
	// checked by vmc @1176
	public void describeFailedEntities() {
		System.out.println("Describe the FailedEntities");
		Describer.describeEntityInstances(allFailedEntities);
	}

	/**
	 *
	 */
	public void describePassedFailedEntities() {
		describePassedEntities();
		describeFailedEntities();
	}
}

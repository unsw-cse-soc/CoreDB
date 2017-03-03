/**
 * 
 */
package coredb.security;

import java.util.List;

import coredb.unit.EntityInstance;
import coredb.utils.Describer;

/**
 * @author seanx
 *
 */
public class ResultTokens {
	private List<EntityInstance> grantTokens = null;
	private List<EntityInstance> denyTokens  = null;
	
	public ResultTokens(List<EntityInstance> grantTokens, List<EntityInstance> denyTokens) {
		this.grantTokens = grantTokens;
		this.denyTokens  = denyTokens;
	}
	
	public List<EntityInstance> getGrantTokens() {
		return grantTokens;
	}
	
	public List<EntityInstance> getDenyTokens() {
		return denyTokens;
	}

	/**
	 * This method prints GrantTokens
	 */
	public void describeGrantTokens() {
		System.out.println("Describe the GrantTokens");
		Describer.describeEntityInstances(grantTokens);
	}

	/**
	 * This method prints DenyTokens
	 */
	public void describeDenyTokens() {
		System.out.println("Describe the DenyTokens");
		Describer.describeEntityInstances(denyTokens);
	}

	/**
	 *
	 */
	public void describeGrantDenyTokens() {
		describeGrantTokens();
		describeDenyTokens();
	}
	
}

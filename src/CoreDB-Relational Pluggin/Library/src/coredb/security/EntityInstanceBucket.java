/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.security;

import coredb.unit.EntityInstance;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author vmc
 */
public class EntityInstanceBucket {
	private List<EntityInstance> sortEntities	= null;
	private List<Integer> bucketSizes		    = null;

	public List<EntityInstance> getSortEntities() {
		return sortEntities;
	}

	public List<Integer> getIndexes() {
		return bucketSizes;
	}

	public EntityInstanceBucket(List<EntityInstance> mixedEntities) {
		this.bucketSizes  = new LinkedList<Integer>();
		this.sortEntities = sortEntityInstancesByTable(mixedEntities);
	}

	public EntityInstanceBucket(List<EntityInstance> mixedEntities, boolean keepOriginal) {
		this.bucketSizes  = new LinkedList<Integer>();
		if (keepOriginal)  this.sortEntities = sortEntityInstancesByTableKeepOriginalList(mixedEntities); // new code
		else               this.sortEntities = sortEntityInstancesByTable(mixedEntities);
	}

	/**
	 * This method pops the first bunch of EntityInstances of the sorted list of EntityInstances
	 * @return returns the first bunch of EntityInstances of the sorted list of EntityInstances
	 */
	// checked by vmc @1192
	public List<EntityInstance> popNextBucket() {
		// sortedEntities <a,a,a,a,b,b,b,b,c,c,c>
		// after popNextBucket <b,b,b,b,c,c,c>
		
		List<EntityInstance> nextBunch = new LinkedList<EntityInstance>();
	    if(sortEntities != null && !sortEntities.isEmpty()) {
	    	int size = this.bucketSizes.get(0);
	    	while (size > 0) {
	    		nextBunch.add(sortEntities.get(0));
				sortEntities.remove(0);
				size --;
	    	}
	    	
	    	// update lenghtList
	    	this.bucketSizes.remove(0);
	    }
	    
		return nextBunch;
	}
	
	/**
	 * This method sorts a list of EntityInstances by tableName and initialise the bucketSizes
	 * 
	 * @param mixedEntities the unsorted list of EntityInstances
	 * @return returns a list of sorted EntityInstances
	 */
	// checked by vmc @1192
	private  List<EntityInstance> sortEntityInstancesByTable(List<EntityInstance> mixedEntities) {
		List<EntityInstance> sortedEntityInstances = new LinkedList<EntityInstance>();
		
        if(mixedEntities != null && !mixedEntities.isEmpty()) {
        	HashMap<String, List<EntityInstance>> map = new LinkedHashMap<String, List<EntityInstance>>();
    		int index = 0;
        	
    		while(mixedEntities.size() > 0){
    			String tableName = mixedEntities.get(0).getDynaClass().getName();
    			if(map.get(tableName) == null){
    				List<EntityInstance> entities = new LinkedList<EntityInstance>();
    				entities.add(mixedEntities.get(0));
    				map.put(tableName, entities);
    				// create a new item to lengthList
    				this.bucketSizes.add(0);
    				index ++;
    			}else {	map.get(tableName).add(mixedEntities.get(0)); }
    			
    			// update the legthList
    			this.bucketSizes.set(index - 1,  this.bucketSizes.get(index - 1) + 1);
    			mixedEntities.remove(0);
    		}
    		
    		Iterator<String> e = map.keySet().iterator();
    		while (e.hasNext()) {
    			sortedEntityInstances.addAll(map.get(e.next()));
    			e.remove();
    		}
			map.clear();
        }
        
		return sortedEntityInstances;
	}
	
	/**
	 * This method sorts a list of EntityInstances by tableName and initialise the bucketSizes
	 * 
	 * @param mixedEntities the unsorted list of EntityInstances
	 * @return returns a list of sorted EntityInstances
	 */
	private  List<EntityInstance> sortEntityInstancesByTableKeepOriginalList(List<EntityInstance> mixedEntities) {
		List<EntityInstance> sortedEntityInstances = new LinkedList<EntityInstance>();
		
        if(mixedEntities != null && !mixedEntities.isEmpty()) {
        	HashMap<String, List<EntityInstance>> map = new LinkedHashMap<String, List<EntityInstance>>();
    		int index = 0;
        	
    		for(EntityInstance entity : mixedEntities) {
    			String tableName = entity.getDynaClass().getName();
    			if(map.get(tableName) == null){
    				List<EntityInstance> entities = new LinkedList<EntityInstance>();
    				entities.add(entity);
    				map.put(tableName, entities);
    				// create a new item to lengthList
    				this.bucketSizes.add(0);
    				index ++;
    			}else {	map.get(tableName).add(entity); }
    			
    			// update the legthList
    			this.bucketSizes.set(index - 1,  this.bucketSizes.get(index - 1) + 1);
    		}
    		
    		Iterator<String> e = map.keySet().iterator();
    		while (e.hasNext()) {
    			sortedEntityInstances.addAll(map.get(e.next()));
    			e.remove();
    		}
			map.clear();
        }
        
		return sortedEntityInstances;
	}
}

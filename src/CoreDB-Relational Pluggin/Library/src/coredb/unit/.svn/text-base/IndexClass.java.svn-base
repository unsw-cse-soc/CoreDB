/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.unit;

//import java.util.UUID;

/**
 *
 * @author vmc
 */
// checked by vmc @723
public class IndexClass {
	private AttributeClass[] _attributeClassList;
  	private String name = null;
	private boolean isUnique;

	// checked by vmc @723
    public boolean isUnique() {
		return isUnique;
	}

	// checked by vmc @723
	public void setUnique(boolean isUnique) {
		this.isUnique = isUnique;
	}

	// checked by vmc @723
	public String getName() {
		return name;
	}


	/**
     * This constructor constructs an Index object.
     * @param _attributeClassList
     */
	// checked by vmc @723
	public IndexClass(AttributeClass[] _attributeClassList) {
		///*
		this._attributeClassList = _attributeClassList;
		this.isUnique = false;
		//this.name = "INDEX_"+ UUID.randomUUID().toString().replace('-', '_');
		// */
	}

	/**
     * This constructor constructs an Index object.
     * @param _attributeClassList
     * @param isUnique this is a unique index if 'isUnique' is true, otherwise index is not unique
     */
	// checked by vmc @723
	public IndexClass(AttributeClass[] _attributeClassList, boolean isUnique) {
		this._attributeClassList = _attributeClassList;
		this.isUnique = isUnique;
		//this.name = "INDEX_"+ UUID.randomUUID().toString().replace('-', '_');
	}

	/**
	 * This constructor constructs an Index object.
	 * 
	 * @param attributeClass the attribute included in this index
	 * @param name the name of this index
	 * @param isUnique this is a unique index if 'isUnique' is true, otherwise index is not unique
	 */
	// checked by vmc @723
	public IndexClass(AttributeClass attributeClass, String name, boolean isUnique) {
		this.name = name;
		this._attributeClassList = new AttributeClass[]{attributeClass};
		this.isUnique = isUnique;
	}

	/**
	 * This constructor constructs an Index object.
	 * 
	 * @param attributeClass the attribute included in this index
	 * @param isUnique this is a unique index if 'isUnique' is true, otherwise index is not unique
	 */
	// checked by vmc @723
	public IndexClass(AttributeClass attributeClass, boolean isUnique) {
		//this.name = "INDEX_"+ UUID.randomUUID().toString().replace('-', '_');
		this._attributeClassList = new AttributeClass[]{attributeClass};
		this.isUnique = isUnique;
	}

	/**
	 * This constructor constructs an Index object.
	 * 
	 * @param attributeClass the attribute included in this index
	 * @param name the name of this index
	 */
	// checked by vmc @723
	public IndexClass(AttributeClass attributeClass, String name) {
		this.name = name;
		this._attributeClassList = new AttributeClass[]{attributeClass};
		this.isUnique = false;
	}
	/**
     * This constructor constructs an Index object.
     * @param _attributeClassList
     * @param name
	 * @param isUnique this is a unique index if 'isUnique' is true, otherwise index is not unique
     */
	// checked by vmc @723
	public IndexClass(AttributeClass[] _attributeClassList, String name, boolean isUnique) {
		this._attributeClassList = _attributeClassList;
		this.name = name;
		this.isUnique = isUnique;
	}

	/**
     * This constructor constructs an Index object.
     * @param _attributeClassList
     * @param name
     */
	// checked by vmc @723
	public IndexClass(AttributeClass[] _attributeClassList, String name) {
		this._attributeClassList = _attributeClassList;
		this.name = name;
		this.isUnique = false;
	}

	/**
	 * Get the attributeclasses which are associated with index.
	 * @return _attributeClassList
	 */
	// checked by vmc @723
	public AttributeClass[] getAttributeClassList() {
		return _attributeClassList;
	}

	/**
	 * Describe AttributeClass
	 *
	 * @param indent
	 */
	// checked by vmc @723
	public void describe(int indent) {
		int nextIndent = indent + 4;
		System.out.printf("%"+nextIndent+"s NAME:%10s ,ISUNIQUE: %10s\n", " ", name,isUnique);
		for (AttributeClass ac : _attributeClassList) {
			ac.describe(nextIndent);
		}
	}
}
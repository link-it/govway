/**
 * 
 */
package org.openspcoop2.utils.serialization.test;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $ Rev: 12563 $, $Date$
 * 
 */
public class InnerClass implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String str;
	private long lng;
	public String getStr() {
		return this.str;
	}
	public void setStr(String str) {
		this.str = str;
	}
	public long getLng() {
		return this.lng;
	}
	public void setLng(long lng) {
		this.lng = lng;
	}
	/**
	 * 
	 */
	public void init() {
		this.str = Math.random() + "";
		this.lng = Math.round(Math.random());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		if(!(obj instanceof InnerClass))
			return false;
		
		InnerClass classObj = (InnerClass) obj;
		if(!Objects.equals(this.str,classObj.getStr()))
			return false;
		if(this.lng != classObj.getLng())
			return false;
		return true;
	}
}

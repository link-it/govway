/**
 * 
 */
package org.openspcoop2.utils.serialization.test;

import java.util.Objects;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author: bussu $
 * @version $ Rev: 12563 $, $Date: 08 mag 2018 $
 * 
 */
public class InnerClass {

	private String str;
	private long lng;
	public String getStr() {
		return str;
	}
	public void setStr(String str) {
		this.str = str;
	}
	public long getLng() {
		return lng;
	}
	public void setLng(long lng) {
		this.lng = lng;
	}
	/**
	 * 
	 */
	public void init() {
		str = Math.random() + "";
		lng = Math.round(Math.random());
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

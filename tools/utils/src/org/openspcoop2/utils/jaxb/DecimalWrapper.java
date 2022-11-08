/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openspcoop2.utils.jaxb;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * DecimalWrapper
 *
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DecimalWrapper implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int minInteger = 0;
	private int minDecimal = 0;
	private int maxInteger = 0;
	private int maxDecimal = 0;
	private Object object;

	public DecimalWrapper(){}
	public DecimalWrapper(int minInteger, int maxInteger, Object object){
		this.minInteger = minInteger;
		this.maxInteger = maxInteger;
		this.object = object;
	}
	public DecimalWrapper(int minInteger, int maxInteger, 
			int minDecimal, int maxDecimal, 
			Object object){
		this.minInteger = minInteger;
		this.maxInteger = maxInteger;
		this.minDecimal = minDecimal;
		this.maxDecimal = maxDecimal;
		this.object = object;
	}
	
	
	public int getMinInteger() {
		return this.minInteger;
	}
	public void setMinInteger(int minInteger) {
		this.minInteger = minInteger;
	}
	public int getMinDecimal() {
		return this.minDecimal;
	}
	public void setMinDecimal(int minDecimal) {
		this.minDecimal = minDecimal;
	}
	public int getMaxInteger() {
		return this.maxInteger;
	}
	public void setMaxInteger(int maxInteger) {
		this.maxInteger = maxInteger;
	}
	public int getMaxDecimal() {
		return this.maxDecimal;
	}
	public void setMaxDecimal(int maxDecimal) {
		this.maxDecimal = maxDecimal;
	}
	
	
	public Object getObject() {
		return this.object;
	}
	public Object getObject(Class<?> c) {
		if(this.object==null){
			return null;
		}
		
		Float fValue = null;
		Double dValue = null;
		Long lValue = null;
		Integer iValue = null;
		Short sValue = null;
		if(this.object instanceof Float){
			fValue = (Float) this.object;
		}
		else if(this.object instanceof Double){
			dValue = (Double) this.object;
		}
		else if(this.object instanceof Long){
			lValue = (Long) this.object;
		}
		else if(this.object instanceof Integer){
			iValue = (Integer) this.object;
		}
		else if(this.object instanceof Short){
			sValue = (Short) this.object;
		}
		else if(this.object instanceof BigInteger){
			BigInteger bigInteger = (BigInteger) this.object;
			if(c.getName().equals(BigInteger.class.getName())){
				return bigInteger;
			}
			else if(c.getName().equals(Float.class.getName())){
				fValue = bigInteger.floatValue();
			}
			else if(c.getName().equals(Double.class.getName())){
				dValue = bigInteger.doubleValue();
			}
			else if(c.getName().equals(Long.class.getName())){
				lValue = bigInteger.longValue();
			}
			else if(c.getName().equals(Integer.class.getName())){
				iValue = bigInteger.intValue();
			}
			else if(c.getName().equals(Short.class.getName())){
				sValue = bigInteger.shortValue();
			}
			else {
				throw new RuntimeException("Tipo impostato dal Wrapper ["+this.object.getClass().getName()+"] non gestito rispetto al tipo richiesto '"+c.getName()+"'");
			}
		}
		else if(this.object instanceof BigDecimal){
			BigDecimal bigDecimal = (BigDecimal) this.object;
			if(c.getName().equals(BigInteger.class.getName())){
				return bigDecimal;
			}
			else if(c.getName().equals(Float.class.getName())){
				fValue = bigDecimal.floatValue();
			}
			else if(c.getName().equals(Double.class.getName())){
				dValue = bigDecimal.doubleValue();
			}
			else if(c.getName().equals(Long.class.getName())){
				lValue = bigDecimal.longValue();
			}
			else if(c.getName().equals(Integer.class.getName())){
				iValue = bigDecimal.intValue();
			}
			else if(c.getName().equals(Short.class.getName())){
				sValue = bigDecimal.shortValue();
			}
			else {
				throw new RuntimeException("Tipo impostato dal Wrapper ["+this.object.getClass().getName()+"] non gestito rispetto al tipo richiesto '"+c.getName()+"'");
			}
		}
		else{
			throw new RuntimeException("Tipo impostato dal Wrapper ["+this.object.getClass().getName()+"] non gestito");
		}
		
		
		if(c.getName().equals(Float.class.getName())){
			if(fValue!=null){
				return (Float) fValue;
			}
			else if(dValue!=null){
				return dValue.floatValue();
			}
			else if(lValue!=null){
				return lValue.floatValue();
			}
			else if(iValue!=null){
				return iValue.floatValue();
			}
			else if(sValue!=null){
				return  sValue.floatValue();
			}
		}
		else if(c.getName().equals(Double.class.getName())){
			if(fValue!=null){
				return fValue.doubleValue();
			}
			else if(dValue!=null){
				return dValue;
			}
			else if(lValue!=null){
				return lValue.doubleValue();
			}
			else if(iValue!=null){
				return iValue.doubleValue();
			}
			else if(sValue!=null){
				return sValue.doubleValue();
			}
		} 
		else if(c.getName().equals(Long.class.getName())){
			if(fValue!=null){
				return fValue.longValue();
			}
			else if(dValue!=null){
				return dValue.longValue();
			}
			else if(lValue!=null){
				return lValue;
			}
			else if(iValue!=null){
				return iValue.longValue();
			}
			else if(sValue!=null){
				return sValue.longValue();
			}
		} 
		else if(c.getName().equals(Integer.class.getName())){
			if(fValue!=null){
				return fValue.intValue();
			}
			else if(dValue!=null){
				return dValue.intValue();
			}
			else if(lValue!=null){
				return lValue.intValue();
			}
			else if(iValue!=null){
				return iValue;
			}
			else if(sValue!=null){
				return sValue.intValue();
			}
		}
		else if(c.getName().equals(Short.class.getName())){
			if(fValue!=null){
				return fValue.shortValue();
			}
			else if(dValue!=null){
				return dValue.shortValue();
			}
			else if(lValue!=null){
				return lValue.shortValue();
			}
			else if(iValue!=null){
				return iValue.shortValue();
			}
			else if(sValue!=null){
				return sValue;
			}
		}
		else{
			throw new RuntimeException("Tipo richiesto ["+c.getName()+"] non gestito");
		}
		
		
		return this.object;
	}
	public void setObject(Object object) {
		this.object = object;
	}
}

/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.generic_project.dao.jdbc.utils;


import java.lang.reflect.Method;
import java.util.Map;

/**
 * AbstractJDBCFetch
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractJDBCFetch implements IJDBCFetch {

	private char charDefault;
	private boolean booleanDefault;
	private byte byteDefault;
	private short shortDefault; 
	private int intDefault;
	private long longDefault;
	private double doubleDefault;
	private float floatDefault;
	
	protected Object getObjectFromMap(Map<String,Object> map,String name){
		if(map==null){
			return null;
		}
		else if(map.containsKey(name)){
			Object o = map.get(name);
			if(o instanceof org.apache.commons.lang.ObjectUtils.Null){
				return null;
			}
			else{
				return o;
			}
		}
		else{
			return null;
		}
	}
	
	protected void setParameter(Object o,String nomeMetodo,Class<?> tipoParametroMetodo,Object value) throws Exception{
//		if(value!=null)
//			System.out.println("Get Metodo ["+nomeMetodo+"] Object["+o.getClass().getName()+"] valore["+value.getClass().getName()+"]");
//		else{
//			System.out.println("Get Metodo ["+nomeMetodo+"] Object["+o.getClass().getName()+"] valore["+null+"]");
//		}
		Method m = o.getClass().getMethod(nomeMetodo, tipoParametroMetodo);
//		System.out.println("Invoco Metodo ["+nomeMetodo+"] Object["+o.getClass().getName()+"] valore["+value+"]");
		
		if(value!=null){
			m.invoke(o, value);
		}
		else{
			
			// NOTA: il set va fatto comunque per annullare il valore precedente.
		
			if(tipoParametroMetodo.getName().equals(char.class.getName())){
				m.invoke(o, this.charDefault);
			}
			else if(tipoParametroMetodo.getName().equals(boolean.class.getName())){
				m.invoke(o, this.booleanDefault);
			}
			else if(tipoParametroMetodo.getName().equals(byte.class.getName())){
				m.invoke(o, this.byteDefault);		
			}
			else if(tipoParametroMetodo.getName().equals(short.class.getName())){
				m.invoke(o, this.shortDefault);		
			}
			else if(tipoParametroMetodo.getName().equals(int.class.getName())){
				m.invoke(o, this.intDefault);		
			}
			else if(tipoParametroMetodo.getName().equals(long.class.getName())){
				m.invoke(o, this.longDefault);		
			}
			else if(tipoParametroMetodo.getName().equals(double.class.getName())){
				m.invoke(o, this.doubleDefault);	
			}
			else if(tipoParametroMetodo.getName().equals(float.class.getName())){
				m.invoke(o, this.floatDefault);	
			}
			else{
				m.invoke(o, value);
			}
			
		}
		
	}

}

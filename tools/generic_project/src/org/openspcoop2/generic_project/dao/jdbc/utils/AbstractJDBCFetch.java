/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
		
			String tipoParametroMetodoName = tipoParametroMetodo.getName()+"";
			
			if(tipoParametroMetodoName.equals(char.class.getName())){
				m.invoke(o, this.charDefault);
			}
			else if(tipoParametroMetodoName.equals(boolean.class.getName())){
				m.invoke(o, this.booleanDefault);
			}
			else if(tipoParametroMetodoName.equals(byte.class.getName())){
				m.invoke(o, this.byteDefault);		
			}
			else if(tipoParametroMetodoName.equals(short.class.getName())){
				m.invoke(o, this.shortDefault);		
			}
			else if(tipoParametroMetodoName.equals(int.class.getName())){
				m.invoke(o, this.intDefault);		
			}
			else if(tipoParametroMetodoName.equals(long.class.getName())){
				m.invoke(o, this.longDefault);		
			}
			else if(tipoParametroMetodoName.equals(double.class.getName())){
				m.invoke(o, this.doubleDefault);	
			}
			else if(tipoParametroMetodoName.equals(float.class.getName())){
				m.invoke(o, this.floatDefault);	
			}
			else{
				m.invoke(o, value);
			}
			
		}
		
	}

}

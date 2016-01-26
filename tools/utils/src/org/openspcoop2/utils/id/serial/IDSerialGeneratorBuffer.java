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

package org.openspcoop2.utils.id.serial;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/**
 * IDSerialGeneratorBuffer
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IDSerialGeneratorBuffer {

	private static Hashtable<String, List<String>> buffer = new Hashtable<String, List<String>>();
	
	private static String getPrefix(Class<?> cIdSerialGenerator){
		if(IDSerialGenerator_alphanumeric.class.getName().equals(cIdSerialGenerator.getName())){
			return "A_";
		}
		else if(IDSerialGenerator_numeric.class.getName().equals(cIdSerialGenerator.getName())){
			return "N_";
		}
		else{
			return "U_";
		}
	}
	
	private static String getKey(Class<?> cIdSerialGenerator,String relativeInfo){
		StringBuffer bf = new StringBuffer();
		bf.append(getPrefix(cIdSerialGenerator));
		if(relativeInfo!=null){
			bf.append(relativeInfo);
		}
		else{
			bf.append("@_NONRELATIVE_@");
		}
		return bf.toString();
	}
	
	protected static String nextValue(Class<?> cIdSerialGenerator,String relativeInfo){
		String key = getKey(cIdSerialGenerator, relativeInfo);
		synchronized(buffer){
			if(buffer.size()<=0 || !buffer.containsKey(key)){
				return null;
			}
			else{
				List<String> l = buffer.get(key);
				if(l==null || l.size()<=0){
					return null;
				}
				else{
					if(l.size()==1){
						l = buffer.remove(key);
					}
					String v = l.remove(0);
					//System.out.println("Return ["+v+"] from Buffer (newSize:"+l.size()+") key["+key+"]");
					return v;
				}
			}
		}
	}
	
	protected static void putAll(List<String> valuesGenerated,Class<?> cIdSerialGenerator,String relativeInfo){
		String key = getKey(cIdSerialGenerator, relativeInfo);
		synchronized(buffer){
			List<String> l = null;
			if(buffer.containsKey(key)){
				l = buffer.get(key);
				//System.out.println("ADD BUFFER ["+valuesGenerated.size()+"] to exists (size:"+l.size()+") key["+key+"]");
			}
			else{
				l = new ArrayList<String>();
				buffer.put(key, l);
				//System.out.println("CREATE BUFFER ["+valuesGenerated.size()+"] from Buffer key["+key+"]");
			}
			l.addAll(valuesGenerated);	
		}
	}
	
	protected static void clearBuffer(){
		synchronized(buffer){
			if(buffer!=null && buffer.size()>0){
				buffer.clear();
			}
		}
	}
	
	protected static void clearBuffer(Class<?> cIdSerialGenerator){
		String prefix = getPrefix(cIdSerialGenerator);
		synchronized(buffer){
			if(buffer!=null && buffer.size()>0){
				Enumeration<String> keys = buffer.keys();
				while (keys.hasMoreElements()) {
					String key = (String) keys.nextElement();
					if(key.startsWith(prefix)){
						buffer.remove(key);
					}
				}
			}
		}
	}

	
}

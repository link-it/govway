/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.id.serial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.SemaphoreLock;

/**
 * IDSerialGeneratorBuffer
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IDSerialGeneratorBuffer {

	private static org.openspcoop2.utils.Semaphore semaphore = new org.openspcoop2.utils.Semaphore("IDSerialGeneratorBuffer");
	private static Map<String, List<String>> buffer = new HashMap<>();
	
	private static String getPrefix(Class<?> cIdSerialGenerator){
		if(cIdSerialGenerator.isAssignableFrom(IDSerialGenerator_alphanumeric.class)){
			return "A_";
		}
		else if(cIdSerialGenerator.isAssignableFrom(IDSerialGenerator_numeric.class)){
			return "N_";
		}
		else{
			return "U_";
		}
	}
	
	private static String getKey(Class<?> cIdSerialGenerator,String relativeInfo){
		StringBuilder bf = new StringBuilder();
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
		//synchronized(buffer){
		SemaphoreLock lock = semaphore.acquireThrowRuntime("nextValue");
		try {
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
		}finally {
			semaphore.release(lock, "nextValue");
		}
	}
	
	protected static void putAll(List<String> valuesGenerated,Class<?> cIdSerialGenerator,String relativeInfo){
		String key = getKey(cIdSerialGenerator, relativeInfo);
		//synchronized(buffer){
		SemaphoreLock lock = semaphore.acquireThrowRuntime("putAll");
		try {
			List<String> l = null;
			if(buffer.containsKey(key)){
				l = buffer.get(key);
				//System.out.println("ADD BUFFER ["+valuesGenerated.size()+"] to exists (size:"+l.size()+") key["+key+"]");
			}
			else{
				l = new ArrayList<>();
				buffer.put(key, l);
				//System.out.println("CREATE BUFFER ["+valuesGenerated.size()+"] from Buffer key["+key+"]");
			}
			l.addAll(valuesGenerated);	
		}finally {
			semaphore.release(lock, "putAll");
		}
	}
	
	protected static void clearBuffer(){
		//synchronized(buffer){
		SemaphoreLock lock = semaphore.acquireThrowRuntime("clearBuffer");
		try {
			if(buffer!=null && buffer.size()>0){
				buffer.clear();
			}
		}finally {
			semaphore.release(lock, "clearBuffer");
		}
	}
	
	protected static void clearBuffer(Class<?> cIdSerialGenerator){
		String prefix = getPrefix(cIdSerialGenerator);
		//synchronized(buffer){
		SemaphoreLock lock = semaphore.acquireThrowRuntime("clearBuffer_class");
		try {
			if(buffer!=null && buffer.size()>0){
				List<String> remove = new ArrayList<>();
				for (String key : buffer.keySet()) {
					if(key.startsWith(prefix)){
						remove.add(key);
					}
				}
				while(!remove.isEmpty()) {
					buffer.remove(remove.remove(0));	
				}
			}
		}finally {
			semaphore.release(lock, "clearBuffer_class");
		}
	}

	
}

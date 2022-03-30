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

package org.openspcoop2.utils.instrument;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * ClientTest
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InstrumentationUtils
{
    public static long memoryUsageOf(final Object obj) {
        return Agent.getInstrumentation().getObjectSize(obj);
    }
    
    public static long deepUsage(final Object obj) {
        return deepUsage(obj, VisibilityFilter.NON_PUBLIC);
    }
    public static long deepUsage(final Object obj, final VisibilityFilter referenceFilter) {
    	return deepUsage(obj, referenceFilter, null);
    }
    public static long deepUsage(final Object obj, Map<String, List<Long>> map) {
        return deepUsage(Agent.getInstrumentation(), new HashSet<Integer>(), obj, VisibilityFilter.NON_PUBLIC, map, -1);
    }
    public static long deepUsage(final Object obj, final VisibilityFilter referenceFilter, Map<String, List<Long>> map) {
        return deepUsage(Agent.getInstrumentation(), new HashSet<Integer>(), obj, referenceFilter, map, -1);
    }
    public static long deepUsage(final Object obj, Map<String, List<Long>> map, int maxLevel) {
        return deepUsage(Agent.getInstrumentation(), new HashSet<Integer>(), obj, VisibilityFilter.NON_PUBLIC, map, maxLevel);
    }
    public static long deepUsage(final Object obj, final VisibilityFilter referenceFilter, Map<String, List<Long>> map, int maxLevel) {
        return deepUsage(Agent.getInstrumentation(), new HashSet<Integer>(), obj, referenceFilter, map, maxLevel);
    }
        
    private static long deepUsage(final Instrumentation instrumentation, final Set<Integer> counted, final Object obj, final VisibilityFilter filter, Map<String, List<Long>> map, int levelMax) throws SecurityException {
        final Stack<Object> stObject = new Stack<Object>();
        final Stack<String> stNames = new Stack<String>();
        final Stack<Integer> stLevels = new Stack<Integer>();
        stObject.push(obj);
        stNames.push("root");
        stLevels.push(0);
        long total = -1L;
        while (!stObject.isEmpty()) {
            final Object o = stObject.pop();
            final String name = stNames.pop();
            final int level = stLevels.pop() + 1;
            if (counted.add(System.identityHashCode(o))) {
            	
            	boolean continuaNavigazione = levelMax==-1 || level<=levelMax;
            	
            	if(!continuaNavigazione) {
            		final long sz = InstrumentationUtils.deepUsage(o, filter);
	                total += sz;
            	}
            	else {
	                final long sz = instrumentation.getObjectSize(o);
	                total += sz;
	                Class<?> clz = o.getClass();
	                
	                if(map!=null) {
	                	String key = name+"_"+clz.getName();
	                	List<Long> l = null;
	                	if(map.containsKey(key)) {
	                		l = map.get(key);
	                	}
	                	else {
	                		l = new ArrayList<Long>();
	                		map.put(key, l);
	                	}
	                	boolean exists = false;
	//                	for (Long lc : l) {
	//						if(lc.longValue() == sz) {
	//							exists=true;
	//							break;
	//						}
	//					}
	                	if(!exists) {
	                		l.add(sz);
	                	}
	                }
	                
	                final Class<?> compType = clz.getComponentType();
	                if (compType != null && !compType.isPrimitive()) {
	                    final Object[] array = (Object[])o;
	                    for (int i = 0; i < array.length; ++i) {
	                        final Object el = array[i];
	                        if (el != null) {
	                        	stObject.push(el);
	                        	stNames.push("[Level"+level+"] compType '"+name+"' "+clz.getName()+" array_"+i);
	                        	stLevels.push(level);
	                        }
	                    }
	                }
	                while (clz != null) {
	                    final Field[] declaredFields = clz.getDeclaredFields();
	                    for (int j = 0; j < declaredFields.length; ++j) {
	                        final Field fld = declaredFields[j];
	                        final int mod = fld.getModifiers();
	                        if ((mod & 0x8) == 0x0 && isOf(filter, mod)) {
	                            final Class<?> fieldClass = fld.getType();
	                            if (!fieldClass.isPrimitive()) {
	                            	try {
	                            		fld.setAccessible(true);
	                            		try {
	                                        final Object subObj = fld.get(o);
	                                        if (subObj != null) {
	                                        	stObject.push(subObj);
	                                        	stNames.push("[Level"+level+"] "+fld.getName());
	                                        	stLevels.push(level);
	                                        }
	                                    }
	                                    catch (IllegalAccessException illAcc) {
	                                        throw new InternalError("Couldn't read " + fld);
	                                    }
	                            	}catch(Throwable t) {}
	                            	
	                            }
	                        }
	                    }
	                    clz = clz.getSuperclass();
	                }
            	}
            }
        }
        return total;
    }
    
    private static boolean isOf(final VisibilityFilter f, final int mod) {
        switch (f) {
            case ALL: {
                return true;
            }
            case PRIVATE_ONLY: {
                return (mod & 0x2) != 0x0;
            }
            case NON_PUBLIC: {
                return (mod & 0x1) == 0x0;
            }
            default: {
                throw new IllegalArgumentException("Illegal filter " + mod);
            }
        }
    }
    
    private InstrumentationUtils() {
    }
    
    public enum VisibilityFilter
    {
        ALL,
        PRIVATE_ONLY, 
        NON_PUBLIC;
    }
    
    public static void printToSystemOut(Map<String, List<Long>> map, boolean sort) throws Exception {
    	print(map, null, sort);
    }
    public static void printToSystemOut(Map<String, List<Long>> map,  File file, boolean sort) throws Exception {
    	print(map, file, sort);
    }
	private static void print(Map<String, List<Long>> map,  File file, boolean sort) throws Exception {
		List<String> keys = new ArrayList<String>();
		keys.addAll(map.keySet());
		//Collections.sort(keys, Comparator.reverseOrder());
		List<String> output = new ArrayList<String>();
		for (String key : keys) {
			List<Long> l = map.get(key);
			if(l.size()==1) {
				output.add("["+l.get(0)+"]=["+key+"]");
			}
			else {
				StringBuilder sb = new StringBuilder();
				for (int i = 1; i < l.size(); i++) {
					sb.append(" [").append(l.get(i)).append("]");
				}
				output.add("["+l.get(0)+"]=["+key+"] more... "+sb.toString());
			}
		}
		if(sort) {
			Collections.sort(output);
		}
		StringBuilder sb = new StringBuilder();
		for (String s : output) {
			if(file!=null) {
				sb.append(s).append("\n");
			}
			else {
				System.out.println(s);
			}
		}
		if(file!=null) {
			FileOutputStream fos =new FileOutputStream(file);
			fos.write(sb.toString().getBytes());
			fos.flush();
			fos.close();
		}
	
	}
}

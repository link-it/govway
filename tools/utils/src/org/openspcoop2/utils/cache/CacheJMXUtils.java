/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.cache;

import org.slf4j.Logger;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.jmx.CostantiJMX;
import org.openspcoop2.utils.jmx.RisorseJMXException;

/**
 * CacheJMXUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CacheJMXUtils {

	private static String JMX_DOMAIN = "org.openspcoop2.utils";
	private static String JMX_TYPE = CostantiJMX.JMX_TYPE;
	
	private static org.openspcoop2.utils.jmx.GestoreRisorseJMX gestoreRisorse = null;
	
	private static synchronized void initGestoreRisorseJMX(Logger log) throws RisorseJMXException{
		if(gestoreRisorse==null)
			gestoreRisorse = new org.openspcoop2.utils.jmx.GestoreRisorseJMX(log);
	}
	
	public static void register(Logger log,AbstractCacheJmx cache) throws UtilsException{
		register(log, cache, null, null, cache.getCacheWrapper().getCacheName());
	}
	public static void register(Logger log,AbstractCacheJmx cache, String jmxName) throws UtilsException{
		register(log,cache, null, null, jmxName);
	}
	public static void register(Logger log,AbstractCacheJmx cache,String jmxDomain, String jmxName) throws UtilsException{
		register(log,cache, jmxDomain, null, jmxName);
	}
	public static void register(Logger log,AbstractCacheJmx cache,String jmxDomain,String jmxType, String jmxName) throws UtilsException{
		try{
			if(jmxName==null){
				throw new Exception("JmxName undefined");
			}
			if(jmxDomain==null){
				jmxDomain = JMX_DOMAIN;
			}
			if(jmxType==null){
				jmxType = JMX_TYPE;
			}
			if(gestoreRisorse==null){
				initGestoreRisorseJMX(log);
			}
			gestoreRisorse.registerMBean(cache.getClass(), jmxDomain, jmxType, jmxName);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public static void unregister(){
		if(gestoreRisorse!=null){
			gestoreRisorse.unregisterMBeans();
		}
	}
}

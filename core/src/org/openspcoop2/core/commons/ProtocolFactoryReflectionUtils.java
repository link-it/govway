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


package org.openspcoop2.core.commons;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.utils.resources.ClassLoaderUtilities;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.resources.MapReader;
import org.slf4j.Logger;

/**
 * ProtocolFactoryReflectionUtils
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProtocolFactoryReflectionUtils
{
	public static void initializeProtocolManager(String protocolloDefault, Logger log) throws CoreException{
		// Protocol initialize
		try{
			Class<?> cProtocolFactoryManager = Class.forName("org.openspcoop2.protocol.engine.ProtocolFactoryManager");
			Object protocolFactoryManager = null;
			try{
				protocolFactoryManager = cProtocolFactoryManager.getMethod("getInstance").invoke(null);
			}catch(Exception pe){
				// ignore
			}
			if(protocolFactoryManager==null){
			
				Class<?> cConfigurazionePdD = Class.forName("org.openspcoop2.protocol.sdk.ConfigurazionePdD");
				Object configurazionePdD = ClassLoaderUtilities.newInstance(cConfigurazionePdD);
				String confDir = null;
				cConfigurazionePdD.getMethod("setConfigurationDir", String.class).invoke(configurazionePdD, confDir);
				cConfigurazionePdD.getMethod("setAttesaAttivaJDBC", long.class).invoke(configurazionePdD, 60);
				cConfigurazionePdD.getMethod("setCheckIntervalJDBC", int.class).invoke(configurazionePdD, 100);
				cConfigurazionePdD.getMethod("setLoader", Loader.class).invoke(configurazionePdD, new Loader());
				cConfigurazionePdD.getMethod("setLog", Logger.class).invoke(configurazionePdD, log);
				
				cProtocolFactoryManager.getMethod("initialize", Logger.class, cConfigurazionePdD, String.class).
					invoke(null, log, configurazionePdD, protocolloDefault);
				
			}
			
		}catch(Exception e){
			throw new CoreException(e.getMessage(),e);
		}
	}
	
	public static List<String> getProtocolli() throws Exception{
		try{
			Class<?> cProtocolFactoryManager = Class.forName("org.openspcoop2.protocol.engine.ProtocolFactoryManager");
			Object protocolFactoryManager = cProtocolFactoryManager.getMethod("getInstance").invoke(null);
			
			@SuppressWarnings("unchecked")
			Enumeration<String> protocolli = (Enumeration<String>) cProtocolFactoryManager.getMethod("getProtocolNames").invoke(protocolFactoryManager);
			List<String> l = new ArrayList<String>();
			while (protocolli.hasMoreElements()) {
				String protocollo = (String) protocolli.nextElement();
				l.add(protocollo);
			}
			return l;
						
		}catch(Exception e){
			throw new CoreException(e.getMessage(),e);
		}
	}
	
	public static String getCodiceIPADefault(IDSoggetto idSoggetto) throws CoreException{
		// Protocol initialize
		try{
			Class<?> cProtocolFactoryManager = Class.forName("org.openspcoop2.protocol.engine.ProtocolFactoryManager");
			Object protocolFactoryManager = cProtocolFactoryManager.getMethod("getInstance").invoke(null);
			
			String protocollo = (String) cProtocolFactoryManager.getMethod("getProtocolByOrganizationType",String.class).invoke(protocolFactoryManager,idSoggetto.getTipo());
			
			Class<?> cProtocolFactory = Class.forName("org.openspcoop2.protocol.sdk.IProtocolFactory");
			Object protocolFactory = cProtocolFactoryManager.getMethod("getProtocolFactoryByName",String.class).invoke(protocolFactoryManager, protocollo);
			
			Class<?> cProtocolTraduttore = Class.forName("org.openspcoop2.protocol.sdk.config.ITraduttore");
			Object protocolTraduttore = cProtocolFactory.getMethod("createTraduttore").invoke(protocolFactory);
			
			return (String) cProtocolTraduttore.getMethod("getIdentificativoCodiceIPADefault", IDSoggetto.class, boolean.class).invoke(protocolTraduttore, idSoggetto, false);
						
		}catch(Exception e){
			throw new CoreException(e.getMessage(),e);
		}
	}
	
	public static String getIdentificativoPortaDefault(IDSoggetto idSoggetto) throws CoreException{
		// Protocol initialize
		try{
			Class<?> cProtocolFactoryManager = Class.forName("org.openspcoop2.protocol.engine.ProtocolFactoryManager");
			Object protocolFactoryManager = cProtocolFactoryManager.getMethod("getInstance").invoke(null);
			
			String protocollo = (String) cProtocolFactoryManager.getMethod("getProtocolByOrganizationType",String.class).invoke(protocolFactoryManager,idSoggetto.getTipo());
			
			Class<?> cProtocolFactory = Class.forName("org.openspcoop2.protocol.sdk.IProtocolFactory");
			Object protocolFactory = cProtocolFactoryManager.getMethod("getProtocolFactoryByName",String.class).invoke(protocolFactoryManager, protocollo);
			
			Class<?> cProtocolTraduttore = Class.forName("org.openspcoop2.protocol.sdk.config.ITraduttore");
			Object protocolTraduttore = cProtocolFactory.getMethod("createTraduttore").invoke(protocolFactory);
			
			return (String) cProtocolTraduttore.getMethod("getIdentificativoPortaDefault", IDSoggetto.class).invoke(protocolTraduttore, idSoggetto);
						
		}catch(Exception e){
			throw new CoreException(e.getMessage(),e);
		}
	}
	
	public static List<String> getOrganizationTypes(String protocollo) throws CoreException{
		// Protocol initialize
		try{
			Class<?> cProtocolFactoryManager = Class.forName("org.openspcoop2.protocol.engine.ProtocolFactoryManager");
			Object protocolFactoryManager = cProtocolFactoryManager.getMethod("getInstance").invoke(null);
			
			@SuppressWarnings("unchecked")
			MapReader<String, List<String>> map = (MapReader<String, List<String>>) cProtocolFactoryManager.getMethod("getOrganizationTypes").invoke(protocolFactoryManager);
			
			return map.get(protocollo);
			
		}catch(Exception e){
			throw new CoreException(e.getMessage(),e);
		}
	}
	
	public static List<String> getServiceTypes(String protocollo) throws CoreException{
		// Protocol initialize
		try{
			Class<?> cProtocolFactoryManager = Class.forName("org.openspcoop2.protocol.engine.ProtocolFactoryManager");
			Object protocolFactoryManager = cProtocolFactoryManager.getMethod("getInstance").invoke(null);
			
			@SuppressWarnings("unchecked")
			HashMap<String, List<String>> map = (HashMap<String, List<String>>) cProtocolFactoryManager.getMethod("_getServiceTypes").invoke(protocolFactoryManager);
			
			return map.get(protocollo);
			
		}catch(Exception e){
			throw new CoreException(e.getMessage(),e);
		}
	}
}

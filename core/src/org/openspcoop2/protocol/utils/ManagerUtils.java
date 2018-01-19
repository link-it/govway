/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it).
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

package org.openspcoop2.protocol.utils;

import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * ManagerUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 13525 $, $Date: 2018-01-09 14:34:45 +0100 (Tue, 09 Jan 2018) $
 */
public class ManagerUtils {

	public static String getDefaultProtocol() throws ProtocolException{
		try{
			Class<?> cProtocolFactoryManager = Class.forName("org.openspcoop2.protocol.engine.ProtocolFactoryManager");
			Object protocolFactoryManager = cProtocolFactoryManager.getMethod("getInstance").invoke(null);
			
			IProtocolFactory<?> pf = (IProtocolFactory<?>) cProtocolFactoryManager.getMethod("getDefaultProtocolFactory").invoke(protocolFactoryManager);
			
			return pf.getProtocol();
						
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	public static String getProtocolByOrganizationType(String type) throws ProtocolException{
		try{
			Class<?> cProtocolFactoryManager = Class.forName("org.openspcoop2.protocol.engine.ProtocolFactoryManager");
			Object protocolFactoryManager = cProtocolFactoryManager.getMethod("getInstance").invoke(null);
			
			String protocollo = (String) cProtocolFactoryManager.getMethod("getProtocolByOrganizationType",String.class).invoke(protocolFactoryManager,type);
			
			return protocollo;
						
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	public static String getDefaultOrganizationType(String protocollo) throws ProtocolException{
		try{
			Class<?> cProtocolFactoryManager = Class.forName("org.openspcoop2.protocol.engine.ProtocolFactoryManager");
			Object protocolFactoryManager = cProtocolFactoryManager.getMethod("getInstance").invoke(null);
			
			IProtocolFactory<?> pf = (IProtocolFactory<?>) cProtocolFactoryManager.getMethod("getProtocolFactoryByName",String.class).invoke(protocolFactoryManager,protocollo);
			
			return pf.createProtocolConfiguration().getTipoSoggettoDefault();
						
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	public static String getDefaultServiceType(String protocollo) throws ProtocolException{
		try{
			Class<?> cProtocolFactoryManager = Class.forName("org.openspcoop2.protocol.engine.ProtocolFactoryManager");
			Object protocolFactoryManager = cProtocolFactoryManager.getMethod("getInstance").invoke(null);
			
			IProtocolFactory<?> pf = (IProtocolFactory<?>) cProtocolFactoryManager.getMethod("getProtocolFactoryByName",String.class).invoke(protocolFactoryManager,protocollo);
			
			return pf.createProtocolConfiguration().getTipoServizioDefault(pf.createProtocolConfiguration().getDefaultServiceBindingConfiguration(null).getDefaultBinding());
						
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
}

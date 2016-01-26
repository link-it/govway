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

package org.openspcoop2.pdd.services.skeleton;

import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.manifest.Openspcoop2;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * IntegrationManagerUtility
 *
 * @author apoli@link.it
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IntegrationManagerUtility {

	public static void readAndSetProtocol(javax.servlet.http.HttpServletRequest req,String pathinfo) throws IntegrationManagerException{
		
		//System.out.println("PATH INFO["+pathinfo+"]");
		String protocol = (String) req.getAttribute(org.openspcoop2.core.constants.Costanti.PROTOCOLLO);
		//System.out.println("PROTOCOLLO ["+protocol+"]");
		if(protocol!=null){
			
			//System.out.println("INIZIALIZZO IM con INPUT PROTOCOL ["+protocol+"]");
			try{
				Openspcoop2 manifest = ProtocolFactoryManager.getInstance().getProtocolManifest(protocol);
				req.setAttribute(org.openspcoop2.core.constants.Costanti.PROTOCOLLO, manifest.getProtocolName());
			} catch(ProtocolException e) {
				throw new RuntimeException(e);
			}
			
		}
		else  {
		
			// rimuovo il nome del contesto, rimane protocol/IntegrationManager
			pathinfo = pathinfo.substring(pathinfo.indexOf("/", 1) + 1);
			// prendo la prima parola: protocol
			String protocolName = pathinfo;
			if(pathinfo.indexOf("/") != -1){
				try{
					String servletPath = pathinfo.substring(0, pathinfo.indexOf("/"));
					if(servletPath.equals("PA") || servletPath.equals("PD") || servletPath.equals("PDtoSOAP") || servletPath.equals("IntegrationManager")) {
						servletPath = "@EMPTY@";//CostantiPdD.OPENSPCOOP_PROTOCOL_EMPTY_CONTEXT;
					}
					//System.out.println("INIZIALIZZO IM con SERVLET PATH ["+servletPath+"]");
					Openspcoop2 manifest = ProtocolFactoryManager.getInstance().getProtocolManifest(servletPath);
					protocolName = manifest.getProtocolName();
				} catch(ProtocolException e) {
					throw new RuntimeException(e);
				}
			}
			
			req.setAttribute(org.openspcoop2.core.constants.Costanti.PROTOCOLLO, protocolName);
			
		}
		
	}
	
}

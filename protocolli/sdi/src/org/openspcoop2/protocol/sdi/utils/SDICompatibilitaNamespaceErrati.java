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
package org.openspcoop2.protocol.sdi.utils;

import org.slf4j.Logger;

/**
 * SDICompatibilitaNamespaceErrati
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SDICompatibilitaNamespaceErrati {

	private static it.gov.fatturapa.sdi.messaggi.v1_0.utils.ProjectInfo pInfo = new it.gov.fatturapa.sdi.messaggi.v1_0.utils.ProjectInfo();
	
	private static final String NAMESPACE_SENZA_GOV = pInfo.getProjectNamespace().replace("www.fatturapa.gov.it", "www.fatturapa.it");
	
	public static byte[] convertiXmlNamespaceSenzaGov(Logger log, byte[] xml){
		try{
			
			String xmlAsAstring = new String(xml);
			if(xmlAsAstring.contains(NAMESPACE_SENZA_GOV)){
				int count = 0;
				while(xmlAsAstring.contains(NAMESPACE_SENZA_GOV) && count<1000){
					count++; // per evitare un loop infinito
					xmlAsAstring = xmlAsAstring.replace(NAMESPACE_SENZA_GOV, pInfo.getProjectNamespace());
				}
				
				//System.out.println("RITORNO MODIFICATO ["+xmlAsAstring+"]");
				return xmlAsAstring.getBytes();
			}
			
		}catch(Exception e){
			log.debug("Compatibilita' Namespace senza Gov abilitata, la modifica al namespace non e' riuscita (Puo' darsi che fosse sbagliato gia' il documento originale, prendere questo messaggio come informazione di debug): "+e.getMessage(),e);
		}
		//System.out.println("RITORNO ORIGINALE");
		return xml;
	}
	
	public static byte[] produciXmlNamespaceSenzaGov(Logger log, byte[] xml){
		try{
			
			String xmlAsAstring = new String(xml);
			if(xmlAsAstring.contains(pInfo.getProjectNamespace())){
				int count = 0;
				while(xmlAsAstring.contains(pInfo.getProjectNamespace()) && count<1000){
					count++; // per evitare un loop infinito
					xmlAsAstring = xmlAsAstring.replace(pInfo.getProjectNamespace(), NAMESPACE_SENZA_GOV);
				}
				
				//System.out.println("RITORNO MODIFICATO ["+xmlAsAstring+"]");
				return xmlAsAstring.getBytes();
			}
			
		}catch(Exception e){
			log.debug("Generazione Namespace senza Gov abilitata, la modifica al namespace non e' riuscita (Puo' darsi che fosse sbagliato gia' il documento originale, prendere questo messaggio come informazione di debug): "+e.getMessage(),e);
		}
		//System.out.println("RITORNO ORIGINALE");
		return xml;
	}
	
}

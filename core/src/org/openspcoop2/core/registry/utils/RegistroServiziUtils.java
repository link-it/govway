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


package org.openspcoop2.core.registry.utils;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/** 
 * RegistroServiziUtils    
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class RegistroServiziUtils {

	public static boolean isRegistroServizi(byte [] doc){
		return RegistroServiziUtils.isRegistroServizi(doc,CostantiRegistroServizi.ROOT_LOCAL_NAME_REGISTRO);
	}
	public static boolean isRegistroServizi(byte [] doc,String localName){
		try{
			org.openspcoop2.message.xml.MessageXMLUtils xmlUtils = org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT;
			Document docXML = xmlUtils.newDocument(doc);
			Element elemXML = docXML.getDocumentElement();
			return RegistroServiziUtils.isRegistroServizi_engine(elemXML,localName);
		}catch(Exception e){
			//System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage());
			return false;
		}
	}
	public static boolean isRegistroServizi(Document docXML){
		return RegistroServiziUtils.isRegistroServizi(docXML,CostantiRegistroServizi.ROOT_LOCAL_NAME_REGISTRO);
	}
	public static boolean isRegistroServizi(Document docXML,String localName){
		try{
			Element elemXML = docXML.getDocumentElement();
			return RegistroServiziUtils.isRegistroServizi_engine(elemXML,localName);
		}catch(Exception e){
			//System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage());
			return false;
		}
	}
	public static boolean isRegistroServizi(Element elemXML,String localName){
		return isRegistroServizi_engine(elemXML,localName);
	}
	public static boolean isRegistroServizi(Node nodeXml,String localName){
		return isRegistroServizi_engine(nodeXml,localName);
	}
	private static boolean isRegistroServizi_engine(Node nodeXml,String localName){
		try{
			//System.out.println("LOCAL["+Costanti.ROOT_LOCAL_NAME_DETTAGLIO_ECCEZIONE+"]vs["+elemXML.getLocalName()+"]  NAMESPACE["+Costanti.TARGET_NAMESPACE+"]vs["+elemXML.getNamespaceURI()+"]");
			if(localName.equals(nodeXml.getLocalName()) && 
					CostantiRegistroServizi.TARGET_NAMESPACE.equals(nodeXml.getNamespaceURI() ) 
				){
				return true;
			}
			else{
				return false;
			}
		}catch(Exception e){
			//System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage());
			return false;
		}
	}
	
	public org.openspcoop2.core.registry.constants.ServiceBinding convertToRegistry(org.openspcoop2.message.constants.ServiceBinding serviceBinding){
		if(serviceBinding==null) {
			return null;
		}
		switch (serviceBinding) {
		case SOAP:
			return org.openspcoop2.core.registry.constants.ServiceBinding.SOAP;
		case REST:
			return org.openspcoop2.core.registry.constants.ServiceBinding.REST;
		}
		return null;
	}
	
	public static org.openspcoop2.message.constants.ServiceBinding convertToMessage(org.openspcoop2.core.registry.constants.ServiceBinding serviceBinding) {
		if(serviceBinding==null) {
			return null;
		}
		switch (serviceBinding) {
		case SOAP:
			return org.openspcoop2.message.constants.ServiceBinding.SOAP;
		case REST:
			return org.openspcoop2.message.constants.ServiceBinding.REST;
		}
		return null;
	}
	
	public static List<String> fillPropertyProtocollo(String propertyName, AccordoServizioParteComune api, String portType, boolean booleanValue) {
		
		List<String> apiValues = new ArrayList<>();
		
		for (ProtocolProperty pp : api.getProtocolPropertyList()) {
			if(propertyName.equals(pp.getName())) {
				String apiValue = booleanValue ? (pp.getBooleanValue()!=null ? pp.getBooleanValue().toString() : "false") : pp.getValue();
				if(apiValue!=null && !apiValues.contains(apiValue)) {
					apiValues.add(apiValue);
				}
				break;
			}
		} 
		
		if(ServiceBinding.REST.equals(api.getServiceBinding())) {
			for (Resource resource : api.getResourceList()) {
				if(resource.sizeProtocolPropertyList()>0) {
					boolean ridefinito = false;					
					for (ProtocolProperty pp : resource.getProtocolPropertyList()) {
						if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE.equals(pp.getName())) {
							String v = pp.getValue(); 
							ridefinito = CostantiDB.MODIPA_PROFILO_RIDEFINISCI.equals(v);
							break;
						}
					}
					if(ridefinito) {
						for (ProtocolProperty pp : resource.getProtocolPropertyList()) {
							if(propertyName.equals(pp.getName())) {
								String apiValue = booleanValue ? (pp.getBooleanValue()!=null ? pp.getBooleanValue().toString() : "false") : pp.getValue();
								if(apiValue!=null && !apiValues.contains(apiValue)) {
									apiValues.add(apiValue);
								}
								break;
							}
						}
					}
				}
			}
		}
		else {
			for (PortType pt : api.getPortTypeList()) {
				if(pt.getNome().equals(portType)) {
					
					for (Operation op : pt.getAzioneList()) {
						if(op.sizeProtocolPropertyList()>0) {
							boolean ridefinito = false;					
							for (ProtocolProperty pp : op.getProtocolPropertyList()) {
								if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE.equals(pp.getName())) {
									String v = pp.getValue(); 
									ridefinito = CostantiDB.MODIPA_PROFILO_RIDEFINISCI.equals(v);
									break;
								}
							}
							if(ridefinito) {
								for (ProtocolProperty pp : op.getProtocolPropertyList()) {
									if(propertyName.equals(pp.getName())) {
										String apiValue = booleanValue ? (pp.getBooleanValue()!=null ? pp.getBooleanValue().toString() : "false") : pp.getValue();
										if(apiValue!=null && !apiValues.contains(apiValue)) {
											apiValues.add(apiValue);
										}
										break;
									}
								}
							}
						}
					}
					
					break;
				}
			}
		}
		
		return apiValues;
	}
}

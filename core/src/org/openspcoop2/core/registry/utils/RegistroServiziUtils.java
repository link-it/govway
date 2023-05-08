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
			return RegistroServiziUtils.isRegistroServiziEngine(elemXML,localName);
		}catch(Exception e){
			/** System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage()); */
			return false;
		}
	}
	public static boolean isRegistroServizi(Document docXML){
		return RegistroServiziUtils.isRegistroServizi(docXML,CostantiRegistroServizi.ROOT_LOCAL_NAME_REGISTRO);
	}
	public static boolean isRegistroServizi(Document docXML,String localName){
		try{
			Element elemXML = docXML.getDocumentElement();
			return RegistroServiziUtils.isRegistroServiziEngine(elemXML,localName);
		}catch(Exception e){
			/** System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage()); */
			return false;
		}
	}
	public static boolean isRegistroServizi(Element elemXML,String localName){
		return isRegistroServiziEngine(elemXML,localName);
	}
	public static boolean isRegistroServizi(Node nodeXml,String localName){
		return isRegistroServiziEngine(nodeXml,localName);
	}
	private static boolean isRegistroServiziEngine(Node nodeXml,String localName){
		try{
			/** System.out.println("LOCAL["+Costanti.ROOT_LOCAL_NAME_DETTAGLIO_ECCEZIONE+"]vs["+elemXML.getLocalName()+"]  NAMESPACE["+Costanti.TARGET_NAMESPACE+"]vs["+elemXML.getNamespaceURI()+"]"); */
			return localName.equals(nodeXml.getLocalName()) && 
					CostantiRegistroServizi.TARGET_NAMESPACE.equals(nodeXml.getNamespaceURI() );
		}catch(Exception e){
			/** System.out.println("NON e' un DOCUMENTO VALIDO: "+e.getMessage()); */
			return false;
		}
	}
	
	public org.openspcoop2.core.registry.constants.ServiceBinding convertToRegistry(org.openspcoop2.message.constants.ServiceBinding serviceBinding){
		if(serviceBinding==null) {
			return null;
		}
		if(org.openspcoop2.message.constants.ServiceBinding.REST.equals(serviceBinding)) {
			return org.openspcoop2.core.registry.constants.ServiceBinding.REST;
		}
		if(org.openspcoop2.message.constants.ServiceBinding.SOAP.equals(serviceBinding)) {
			return org.openspcoop2.core.registry.constants.ServiceBinding.SOAP;
		}
		return null;
	}
	
	public static org.openspcoop2.message.constants.ServiceBinding convertToMessage(org.openspcoop2.core.registry.constants.ServiceBinding serviceBinding) {
		if(serviceBinding==null) {
			return null;
		}
		if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(serviceBinding)) {
			return org.openspcoop2.message.constants.ServiceBinding.REST;
		}
		if(org.openspcoop2.core.registry.constants.ServiceBinding.SOAP.equals(serviceBinding)) {
			return org.openspcoop2.message.constants.ServiceBinding.SOAP;
		}
		return null;
	}
	
	private static String getBooleanValueAsString(ProtocolProperty pp) {
		return pp.getBooleanValue()!=null ? pp.getBooleanValue().toString() : "false";
	}
	public static List<String> fillPropertyProtocollo(String propertyName, AccordoServizioParteComune api, String portType, boolean booleanValue) {
		return fillPropertyProtocollo(propertyName, null, api, portType, booleanValue);
	}
	public static final String PROPERTY_SEPARATOR = " -- ";
	public static List<String> splitPropertyProtocolloResult(String value){
		List<String> l = new ArrayList<>();
		if(value.contains(PROPERTY_SEPARATOR)) {
			String [] tmp = value.split(PROPERTY_SEPARATOR);
			if(tmp!=null && tmp.length>0) {
				for (String s : tmp) {
					l.add(s.trim());
				}
			}
		}
		else {
			l.add(value);
		}
		return l;
	}
	private static void addPropertyProtocolloResult(String propertyName, String propertyName2,
			String apiValue, String apiValue2, List<String> apiValues) {
		
		// backward compatibility
		if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH.equals(propertyName) &&
			apiValue==null) {
			apiValue = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_LOCALE;
		}
		if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH.equals(propertyName2) &&
			apiValue2==null) {
			apiValue2 = CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_LOCALE;
		}
		
		String insertValue = apiValue2!=null ? apiValue+PROPERTY_SEPARATOR+apiValue2 : apiValue;
		
		if(insertValue!=null && !apiValues.contains(insertValue)) {
			apiValues.add(insertValue);
		}
	}
	public static List<String> fillPropertyProtocollo(String propertyName, String propertyName2, AccordoServizioParteComune api, String portType, boolean booleanValue) {
		
		List<String> apiValues = new ArrayList<>();
		
		for (ProtocolProperty pp : api.getProtocolPropertyList()) {
			if(propertyName.equals(pp.getName())) {
				String apiValue = booleanValue ? getBooleanValueAsString(pp) : pp.getValue();
				
				String apiValue2 = getPropertyProtocolloValue(propertyName2, booleanValue, api.getProtocolPropertyList());
				
				addPropertyProtocolloResult(propertyName, propertyName2,
						apiValue, apiValue2, apiValues);
				
				break;
			}
		} 
		
		fillPropertyProtocollo(propertyName, propertyName2, api, portType, booleanValue,
				apiValues);
		
		return apiValues;
	}
	private static String getPropertyProtocolloValue(String propertyName2, boolean booleanValue, List<ProtocolProperty> properties) {
		String apiValue2 = null;
		if(propertyName2!=null) {
			for (ProtocolProperty pp2 : properties) {
				if(propertyName2.equals(pp2.getName())) {
					apiValue2 = booleanValue ? getBooleanValueAsString(pp2) : pp2.getValue();
					break;
				}
			}
		}
		return apiValue2;
	}
	private static void fillPropertyProtocollo(String propertyName, String propertyName2, AccordoServizioParteComune api, String portType, boolean booleanValue,
			List<String> apiValues) {
		if(ServiceBinding.REST.equals(api.getServiceBinding())) {
			fillPropertyProtocolloREST(propertyName, propertyName2, api, booleanValue,
					apiValues);
		}
		else {
			fillPropertyProtocolloSOAP(propertyName, propertyName2, api, portType, booleanValue,
					apiValues);
		}
	}
	private static boolean isRidefinito(List<ProtocolProperty> properties) {
		boolean ridefinito = false;					
		for (ProtocolProperty pp : properties) {
			if(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE.equals(pp.getName())) {
				String v = pp.getValue(); 
				ridefinito = CostantiDB.MODIPA_PROFILO_RIDEFINISCI.equals(v);
				break;
			}
		}
		return ridefinito;
	}
	private static void fillPropertyProtocolloREST(String propertyName, String propertyName2, AccordoServizioParteComune api, boolean booleanValue,
			List<String> apiValues) {
		for (Resource resource : api.getResourceList()) {
			if(resource.sizeProtocolPropertyList()>0) {
				boolean ridefinito = isRidefinito(resource.getProtocolPropertyList());
				if(ridefinito) {
					fillPropertyProtocolloREST(propertyName, propertyName2, booleanValue,
							apiValues, resource);
				}
			}
		}
	}
	private static void fillPropertyProtocolloREST(String propertyName, String propertyName2, boolean booleanValue,
			List<String> apiValues, Resource resource) {
		for (ProtocolProperty pp : resource.getProtocolPropertyList()) {
			if(propertyName.equals(pp.getName())) {
				String apiValue = booleanValue ? getBooleanValueAsString(pp) : pp.getValue();
				
				String apiValue2 = getPropertyProtocolloValue(propertyName2, booleanValue, resource.getProtocolPropertyList());
				
				addPropertyProtocolloResult(propertyName, propertyName2,
						apiValue, apiValue2, apiValues);
				
				break;
			}
		}
	}
	private static void fillPropertyProtocolloSOAP(String propertyName, String propertyName2, AccordoServizioParteComune api, String portType, boolean booleanValue,
			List<String> apiValues) {
		for (PortType pt : api.getPortTypeList()) {
			if(pt.getNome().equals(portType)) {
				
				for (Operation op : pt.getAzioneList()) {
					if(op.sizeProtocolPropertyList()>0) {
						boolean ridefinito = isRidefinito(op.getProtocolPropertyList());
						if(ridefinito) {
							fillPropertyProtocolloSOAP(propertyName, propertyName2, booleanValue,
									apiValues, op);
						}
					}
				}
				
				break;
			}
		}
	}
	private static void fillPropertyProtocolloSOAP(String propertyName, String propertyName2, boolean booleanValue,
			List<String> apiValues, Operation op) {
		for (ProtocolProperty pp : op.getProtocolPropertyList()) {
			if(propertyName.equals(pp.getName())) {
				String apiValue = booleanValue ? getBooleanValueAsString(pp) : pp.getValue();
				
				String apiValue2 = getPropertyProtocolloValue(propertyName2, booleanValue, op.getProtocolPropertyList());

				addPropertyProtocolloResult(propertyName, propertyName2,
						apiValue, apiValue2, apiValues);

				break;
			}
		}
	}
}

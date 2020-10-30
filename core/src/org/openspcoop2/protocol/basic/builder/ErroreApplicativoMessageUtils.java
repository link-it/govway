/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.basic.builder;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;

import org.openspcoop2.core.eccezione.router_details.Dettaglio;
import org.openspcoop2.core.eccezione.router_details.DettaglioRouting;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.slf4j.Logger;
import org.w3c.dom.Element;

/**
 * ErroreApplicativoMessageUtils
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ErroreApplicativoMessageUtils {

	public static void addPrefixToElement(Element elementErroreApplicativo,String prefix){
		//workAround per ovviare poi al problema axiom: NAMESPACE_ERR: An attempt is made to create or change an object in a way which is incorrect with regard to namespaces.
		// che si verifica in detailFaultPulito.addChildElement(eccezioneDetailApplicativo) del metodo insertErroreApplicativoIntoSOAPFault_engine di questa classe
		//elementErroreApplicativo.setPrefix(prefix); 
	}
	

	
	public static void addErroreApplicativoIntoSOAPFaultDetail(SOAPElement erroreApplicativo,
			OpenSPCoop2Message msg, Logger log) throws ProtocolException{
		
		try{
			if(msg==null)
				throw new ProtocolException("Messaggio non presente");
			SOAPBody soapBody = msg.castAsSoap().getSOAPBody();
			if(soapBody==null)
				throw new ProtocolException("SOAPBody non presente");
			SOAPFault fault = null;
			if(soapBody.hasFault()==false)
				throw new ProtocolException("SOAPFault non presente");
			else
				fault = soapBody.getFault();
			if(fault==null)
				throw new ProtocolException("SOAPFault is null");
			
			Detail detail = fault.getDetail();
			if(detail==null){
				detail = fault.addDetail();
				detail = fault.getDetail();
			}
			if(detail!=null){
				detail.addChildElement(erroreApplicativo);
			}
			
            msg.saveChanges();
			
		}catch(Exception e){
			log.error("Errore durante la costruzione del messaggio di errore applicativo (InsertDetail)",e);
			throw new ProtocolException("Errore durante la costruzione del messaggio di errore (InsertDetail)",e);
		}
	}
	
	@Deprecated
	public static void insertErroreApplicativoIntoSOAPFault(SOAPElement erroreApplicativo,
			OpenSPCoop2Message msg, Logger log) throws ProtocolException{
		try{
			if(msg==null)
				throw new ProtocolException("Messaggio non presente");
			SOAPBody soapBody = msg.castAsSoap().getSOAPBody();
			if(soapBody==null)
				throw new ProtocolException("SOAPBody non presente");
			SOAPFault faultOriginale = null;
			if(soapBody.hasFault()==false)
				throw new ProtocolException("SOAPFault non presente");
			else
				faultOriginale = soapBody.getFault();
			if(faultOriginale==null)
				throw new ProtocolException("SOAPFault is null");
						
			QName nameDetail = null;
			if(MessageType.SOAP_12.equals(msg.getMessageType())){
				nameDetail = new QName(org.openspcoop2.message.constants.Costanti.SOAP12_ENVELOPE_NAMESPACE,"Detail");
			}
			else{
				nameDetail = new QName("detail");
			}
			SOAPElement detailsFaultOriginale = null;
			Iterator<?> itDetailsOriginali = faultOriginale.getChildElements(nameDetail);
			if(itDetailsOriginali!=null && itDetailsOriginali.hasNext()){
				detailsFaultOriginale = (SOAPElement) itDetailsOriginali.next();
			}
					
			String faultActor = faultOriginale.getFaultActor(); // in soap1.2 e' il role
			Name faultCode = faultOriginale.getFaultCodeAsName();
			Iterator<?> faultSubCode = null;
			String faultNode = null;
			if(MessageType.SOAP_12.equals(msg.getMessageType())){
				faultSubCode = faultOriginale.getFaultSubcodes();
				faultNode = faultOriginale.getFaultNode();
			}
			String faultString = faultOriginale.getFaultString();
			
			
			soapBody.removeChild(soapBody.getFault());
			
			//msg.saveChanges();
			
			SOAPFault faultPulito = soapBody.addFault();
			if(faultActor != null)
				faultPulito.setFaultActor(faultActor);
			if(faultCode!=null)
				faultPulito.setFaultCode(faultCode);
			if(faultSubCode!=null){
				while (faultSubCode.hasNext()) {
					QName faultSubCodeQname = (QName) faultSubCode.next();
					faultPulito.appendFaultSubcode(faultSubCodeQname);
				}
			}
			if(faultNode!=null){
				faultPulito.setFaultNode(faultNode);
			}
			if(faultString!=null)
				SoapUtils.setFaultString(faultPulito , faultString);
			Detail detailFaultPulito = faultPulito.addDetail();
			detailFaultPulito = faultPulito.getDetail();
		
			if(detailsFaultOriginale!=null){
	            Iterator<?> it = detailsFaultOriginale.getChildElements();
				while (it.hasNext()) {
					Object o = it.next();
					if(o instanceof SOAPElement){
						SOAPElement elem = (SOAPElement) o;
						detailFaultPulito.addChildElement(elem);
					}
				}
			}
			detailFaultPulito.addChildElement(erroreApplicativo);
		
            msg.saveChanges();
			
		}catch(Exception e){
			log.error("Errore durante la costruzione del messaggio di errore applicativo (InsertDetail)",e);
			throw new ProtocolException("Errore durante la costruzione del messaggio di errore (InsertDetail)",e);
		}
	}
	
	
	public static void insertRoutingErrorInSOAPFault(IDSoggetto identitaRouter,String idFunzione,String msgErrore,OpenSPCoop2Message msg,
			Logger log, AbstractXMLUtils xmlUtils) throws ProtocolException{
		
		try{
			if(msg==null)
				throw new ProtocolException("Messaggio non presente");
			
			DettaglioRouting dettaglioRouting = new DettaglioRouting();
			
			
			// dominio
			org.openspcoop2.core.eccezione.router_details.Dominio dominio = new org.openspcoop2.core.eccezione.router_details.Dominio();
			org.openspcoop2.core.eccezione.router_details.DominioSoggetto dominioSoggetto = new org.openspcoop2.core.eccezione.router_details.DominioSoggetto();
			dominioSoggetto.setType(identitaRouter.getTipo());
			dominioSoggetto.setBase(identitaRouter.getNome());
			dominio.setOrganization(dominioSoggetto);
			dominio.setId(identitaRouter.getCodicePorta());
			dominio.setModule(idFunzione);
			dettaglioRouting.setDomain(dominio);
			
			// oraRegistrazione
			dettaglioRouting.setTimestamp(DateManager.getDate());
			
			// dettaglio
			Dettaglio dettaglio = new Dettaglio();
			dettaglio.setDescription(msgErrore);
			dettaglio.setState(org.openspcoop2.core.eccezione.router_details.constants.Costanti.ESITO_ERRORE);
			dettaglioRouting.setDetail(dettaglio);
			
			byte[]xmlDettaglioRouting = org.openspcoop2.core.eccezione.router_details.utils.XMLUtils.generateDettaglioRouting(dettaglioRouting);
			Element elementDettaglioRouting = xmlUtils.newElement(xmlDettaglioRouting);
			addPrefixToElement(elementDettaglioRouting,"op2RoutingDetail");
			
			SOAPFactory sf = SoapUtils.getSoapFactory(msg.getFactory(), msg.getMessageType());
			SOAPElement dettaglioRoutingElementSOAP =  sf.createElement(elementDettaglioRouting);
			
			addErroreApplicativoIntoSOAPFaultDetail(dettaglioRoutingElementSOAP, msg, log);
		}catch(Exception e){
			log.error("Errore durante la costruzione del messaggio di errore",e);
			throw new ProtocolException("Errore durante la costruzione del messaggio di errore",e);
		}
	}
	
}
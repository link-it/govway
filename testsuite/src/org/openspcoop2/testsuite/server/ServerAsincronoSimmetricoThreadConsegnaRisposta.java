/*
 * OpenSPCoop - Customizable API Gateway 
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




package org.openspcoop2.testsuite.server;


import java.io.ByteArrayOutputStream;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.rpc.Stub;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;

import org.apache.axis.Message;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.PrefixedQName;
import org.slf4j.Logger;
import org.openspcoop2.pdd.services.axis14.IntegrationManagerMessage;
import org.openspcoop2.pdd.services.axis14.PD_PortType;
import org.openspcoop2.pdd.services.axis14.PDServiceLocator;
import org.openspcoop2.pdd.services.axis14.ProtocolHeaderInfo;
import org.openspcoop2.testsuite.axis14.Axis14SoapUtils;
import org.openspcoop2.testsuite.core.CostantiTestSuite;
import org.openspcoop2.testsuite.core.SOAPEngine;
import org.openspcoop2.testsuite.core.TestSuiteProperties;
import org.openspcoop2.testsuite.core.Utilities;
import org.openspcoop2.testsuite.core.UtilitiesGestioneMessaggiSoap;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatabaseProperties;


/**
 * Thread per consegna di una risposta asincrona simmetrica.
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ServerAsincronoSimmetricoThreadConsegnaRisposta extends Thread{

	private boolean modalitaAsincrona = false;
	private Logger log = null;
	private TestSuiteProperties testSuiteProperties = null;
	private boolean withAttachments = false;
	private byte[] messaggioRisposta = null;
	private String riferimentoAsincrono = null;
	private String protocollo = null;
	
	public ServerAsincronoSimmetricoThreadConsegnaRisposta(boolean isModalitaAsincrona,Logger logT,
			TestSuiteProperties tP,boolean attach,byte[] m,String id,String protocollo){
		this.modalitaAsincrona = isModalitaAsincrona;
		this.log = logT;
		this.testSuiteProperties = tP;
		this.withAttachments = attach;
		this.messaggioRisposta = m;
		this.riferimentoAsincrono = id;
		this.protocollo = protocollo;
	}

	@Override
	public void run(){
		
		// Attesa prima della generazione della risposta asincrona
		if(this.testSuiteProperties.attendiTerminazioneMessaggi_generazioneRispostaAsincrona()){
			long countTimeout = System.currentTimeMillis() + (1000*this.testSuiteProperties.getTimeoutProcessamentoMessaggiOpenSPCoop());
			DatabaseComponent dbFruitore = 	null;
			DatabaseComponent dbErogatore = null;
			try{
				dbFruitore = DatabaseProperties.getDatabaseComponentFruitore(this.protocollo);
				dbErogatore = DatabaseProperties.getDatabaseComponentErogatore(this.protocollo);
				while(dbErogatore.getVerificatoreMessaggi().countMsgOpenSPCoop(this.riferimentoAsincrono)!=0 && countTimeout>System.currentTimeMillis()){
					try{
						Thread.sleep(this.testSuiteProperties.getCheckIntervalProcessamentoMessaggiOpenSPCoop());
					}catch(Exception e){}
				}
				countTimeout = System.currentTimeMillis() + (1000*this.testSuiteProperties.getTimeoutProcessamentoMessaggiOpenSPCoop());
				while(dbFruitore.getVerificatoreMessaggi().countMsgOpenSPCoop(this.riferimentoAsincrono)!=0 && countTimeout>System.currentTimeMillis()){
					try{
						Thread.sleep(this.testSuiteProperties.getCheckIntervalProcessamentoMessaggiOpenSPCoop());
					}catch(Exception e){}
				}
			}catch(Exception e){
				this.log.info("Attesa terminazione messaggi non riuscita: "+e.getMessage());
			}finally{
				try{
					dbFruitore.close();
				}catch(Exception e){}
				try{
					dbErogatore.close();
				}catch(Exception e){}
			}
		}else{
			try{
				Thread.sleep(this.testSuiteProperties.timeToSleep_generazioneRispostaAsincrona());
			}
			catch(InterruptedException i){}
		}
		
		// Generazione risposta
		try{
			if(this.modalitaAsincrona){
				generazioneRispostaAsincrona_modalitaAsincrona(this.messaggioRisposta, this.riferimentoAsincrono, this.withAttachments, this.protocollo);
			}else{
				generazioneRispostaAsincrona_modalitaSincrona(this.messaggioRisposta, this.riferimentoAsincrono, this.withAttachments, this.protocollo);
			}
		}catch(Exception e){
			this.log.error("Generazione risposta asincrona non riuscita",e);
		}
			
	}

	public void generazioneRispostaAsincrona_modalitaAsincrona(byte[] messaggio, String id, boolean attach, String protocol) {
		
		Message msg = null;
		if (attach) {
			//UtilitiesGestioneMessaggiSoap ser = new UtilitiesGestioneMessaggiSoap();
			//msg = ser.createMessage(messaggio);
			try{
				msg = Axis14SoapUtils.build(messaggio, false);
			}catch(Exception e){
				this.log.error("Errore nella fase di costruzione della risposta [AsincronoSimmetrico_modalitaAsincrona]",e);
			}
		} else {
			
			msg = new Message(messaggio);
		}
		SOAPHeader header = null;
		try {
			header = msg.getSOAPHeader();
		} catch (SOAPException e1) {
			this.log.error("Errore nella fase di getHeader [AsincronoSimmetrico_modalitaAsincrona]",e1);
		}
		try{
			if(msg.getSOAPBody() !=null){
				java.util.Iterator<?> it = msg.getSOAPBody().getChildElements();
				boolean addID = false;
				while(it.hasNext()){
					MessageElement child = (MessageElement) it.next();
					if("idUnivoco".equals(child.getLocalName())){
						addID = true;
						break;
					}
				}
				if(addID){
					MessageElement m = new MessageElement("idUnivocoServer", "test","http://www.openspcoop.org");
					m.setValue("ID-SERVER-"+SOAPEngine.getIDUnivoco());
					msg.getSOAPBody().addChildElement(m);
				}
			}
		} catch (Exception e) {
			System.out.println("Errore durante la generazione dell'id unico: "+e.getMessage());
		}
		
		
		Iterator<?> it=header.getChildElements(new PrefixedQName(new QName(CostantiTestSuite.TAG_NAME)));
		SOAPElement el=(SOAPElement)it.next();
		String headerID=el.getAttribute(CostantiTestSuite.ID_HEADER_ATTRIBUTE);
		String portaCorrelata=Utilities.getPorta(headerID);
		this.log.debug("Porta Correlata arrivata: "+portaCorrelata);
		
		String username = null;
		String password = null;
		try{
			Iterator<?> itCredenziali=header.getChildElements(new PrefixedQName(new QName(CostantiTestSuite.CREDENZIALI_RISPOSTA_ASINCRONA_SIMMETRICA)));
			if(itCredenziali.hasNext()){
				SOAPElement elCredenziali=(SOAPElement)itCredenziali.next();
				username=elCredenziali.getAttribute(CostantiTestSuite.CREDENZIALI_USERNAME);
				password=elCredenziali.getAttribute(CostantiTestSuite.CREDENZIALI_PASSWORD);
				this.log.debug("Crendenziali username["+username+"] password["+password+"]");
			}
		}catch(Exception e){
			this.log.error("Raccolta crendenziali non riuscita",e);
		}
		
		boolean sendAttraversoIntegrationManager = false;
		if(portaCorrelata.startsWith(org.openspcoop2.testsuite.core.CostantiTestSuite.UTILIZZO_INTEGRATION_MANAGER)){
			portaCorrelata = portaCorrelata.substring(org.openspcoop2.testsuite.core.CostantiTestSuite.UTILIZZO_INTEGRATION_MANAGER.length(),portaCorrelata.length());
			sendAttraversoIntegrationManager = true;
		}
		this.log.info("Porta Correlata (sendIM:"+sendAttraversoIntegrationManager+"): "+portaCorrelata);
		
		
	
		if(sendAttraversoIntegrationManager){
			IntegrationManagerMessage msgIM = new IntegrationManagerMessage();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			try {
				msg.writeTo(bout);
				bout.flush();
				bout.close();
			}catch (Exception e) {
				System.out.println("[AsincronoSimmetrico_modalitaAsincrona] Errore durante la trasformazione in byte del messaggio: "+e.getMessage());
			}
			msgIM.setMessage(bout.toByteArray());
			ProtocolHeaderInfo protocolInfo = new ProtocolHeaderInfo();
			protocolInfo.setRiferimentoMessaggio(id);
			msgIM.setProtocolHeaderInfo(protocolInfo);
			try {
				PDServiceLocator locator = new PDServiceLocator();
			    locator.setPDEndpointAddress(this.testSuiteProperties.getOpenSPCoopPDConsegnaRispostaAsincronaSimmetrica(protocol).replace("PD", "IntegrationManager/PD"));
			    this.log.info("Invocazione url del servizio di IntegrationManager ["+locator.getPDAddress()+"] ...");
			    PD_PortType port = locator.getPD();
			    
			    if(username !=null && password!=null){
                    // to use Basic HTTP Authentication: 
                    ((Stub) port)._setProperty(javax.xml.rpc.Call.USERNAME_PROPERTY, username);
                    ((Stub) port)._setProperty(javax.xml.rpc.Call.PASSWORD_PROPERTY, password);
			    }

			    IntegrationManagerMessage msgRispostaIM = port.sendRispostaAsincronaSimmetrica(portaCorrelata, msgIM);
			    			    
			    // Msg di risposta
				Message msgAxisResponse = Axis14SoapUtils.build(msgRispostaIM.getMessage(), false);
				// java.io.ByteArrayOutputStream out =  new java.io.ByteArrayOutputStream();
				//msgAxisResponse.writeTo(out);
				//this.log.info("Messaggio ricevuto: "+out.toString());
				
				if (!Utilities.isOpenSPCoopOKMessage(msgAxisResponse))
					this.log.error("[AsincronoSimmetrico_modalitaAsincrona] Errore durante la ricezione del messaggio (non conforme ad openspcoopPresaInCarico.xsd)");
					
			} catch (Exception e) {
				this.log.error("[AsincronoSimmetrico_modalitaAsincrona] Errore durante la creazione/esecuzione del client IntegrationManager per l'invocazione della porta delegata: "+e.getMessage());
			}    
		}else{
			javax.xml.soap.MimeHeaders mime = msg.getMimeHeaders();
			mime.addHeader(this.testSuiteProperties.getRiferimentoAsincronoTrasporto(), id);
			Service service = new Service();
			
			try {
				Call call = (Call) service.createCall();
				call.setTargetEndpointAddress(this.testSuiteProperties.getOpenSPCoopPDConsegnaRispostaAsincronaSimmetrica(protocol)+portaCorrelata);
				if(username !=null && password!=null){
					call.setUsername(username);
					call.setPassword(password);
				}
				call.invoke(msg);
				Message resp = call.getResponseMessage();
				//java.io.ByteArrayOutputStream out =  new java.io.ByteArrayOutputStream();
				//resp.writeTo(out);
				//this.log.info("Messaggio ricevuto: "+out.toString());
				
				if (!Utilities.isOpenSPCoopOKMessage(resp))
					this.log.error("[AsincronoSimmetrico_modalitaAsincrona] Errore durante la ricezione del messaggio (non conforme ad openspcoopPresaInCarico.xsd)");
	
			} catch (Exception e) {
				this.log.error("[AsincronoSimmetrico_modalitaAsincrona] Errore durante la creazione del client per l'invocazione della porta delegata: "+e.getMessage());
			} 
		}
		
		this.log.info("[AsincronoSimmetrico_modalitaAsincrona] Gestione risposta con id correlato="+id+" inviata alla porta delegata "+portaCorrelata);
	}
	
	
	
	public void generazioneRispostaAsincrona_modalitaSincrona(byte[] messaggio,String id,boolean hasAttachment, String protocol){
		Message msg=null;
		if(hasAttachment){
			UtilitiesGestioneMessaggiSoap ser=new UtilitiesGestioneMessaggiSoap();
			msg=ser.createMessage(messaggio);
		}
		else{
			msg=new Message(messaggio);
		}
		SOAPHeader header = null;
		try {
			header = msg.getSOAPHeader();
		} catch (SOAPException e1) {
			this.log.error("Errore nella fase di getHeader [AsincronoSimmetrico_modalitaSincrono]",e1);
		}
		try{
			if(msg.getSOAPBody() !=null){
				java.util.Iterator<?> it = msg.getSOAPBody().getChildElements();
				boolean addID = false;
				while(it.hasNext()){
					MessageElement child = (MessageElement) it.next();
					if("idUnivoco".equals(child.getLocalName())){
						addID = true;
						break;
					}
				}
				if(addID){
					MessageElement m = new MessageElement("idUnivocoServer", "test","http://www.openspcoop.org");
					m.setValue("ID-SERVER-"+SOAPEngine.getIDUnivoco());
					msg.getSOAPBody().addChildElement(m);
				}
			}
		} catch (Exception e) {
			System.out.println("Errore durante la generazione dell'id unico: "+e.getMessage());
		}
		Iterator<?> it=header.getChildElements(new PrefixedQName(new QName(CostantiTestSuite.TAG_NAME)));
		SOAPElement el=(SOAPElement)it.next();
		String headerID=el.getAttribute(CostantiTestSuite.ID_HEADER_ATTRIBUTE);
		String portaCorrelata=Utilities.getPorta(headerID);
		//this.log.info("Porta Correlata arivata: "+portaCorrelata);
		
		String username = null;
		String password = null;
		try{
			Iterator<?> itCredenziali=header.getChildElements(new PrefixedQName(new QName(CostantiTestSuite.CREDENZIALI_RISPOSTA_ASINCRONA_SIMMETRICA)));
			if(itCredenziali.hasNext()){
				SOAPElement elCredenziali=(SOAPElement)itCredenziali.next();
				username=elCredenziali.getAttribute(CostantiTestSuite.CREDENZIALI_USERNAME);
				password=elCredenziali.getAttribute(CostantiTestSuite.CREDENZIALI_PASSWORD);
				this.log.debug("Crendenziali username["+username+"] password["+password+"]");
			}
		}catch(Exception e){
			this.log.error("Raccolta crendenziali non riuscita",e);
		}
		
		boolean sendAttraversoIntegrationManager = false;
		if(portaCorrelata.startsWith(org.openspcoop2.testsuite.core.CostantiTestSuite.UTILIZZO_INTEGRATION_MANAGER)){
			portaCorrelata = portaCorrelata.substring(org.openspcoop2.testsuite.core.CostantiTestSuite.UTILIZZO_INTEGRATION_MANAGER.length(),portaCorrelata.length());
			sendAttraversoIntegrationManager = true;
		}
		this.log.info("Porta Correlata (sendIM:"+sendAttraversoIntegrationManager+"): "+portaCorrelata);

		if(sendAttraversoIntegrationManager){
			IntegrationManagerMessage msgIM = new IntegrationManagerMessage();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			try {
				msg.writeTo(bout);
				bout.flush();
				bout.close();
			}catch (Exception e) {
				System.out.println("[AsincronoSimmetrico_modalitaSincrona] Errore durante la trasformazione in byte del messaggio: "+e.getMessage());
			}
			msgIM.setMessage(bout.toByteArray());
			ProtocolHeaderInfo protocolInfo = new ProtocolHeaderInfo();
			protocolInfo.setRiferimentoMessaggio(id);
			msgIM.setProtocolHeaderInfo(protocolInfo);
			try {
				PDServiceLocator locator = new PDServiceLocator();
			    locator.setPDEndpointAddress(this.testSuiteProperties.getOpenSPCoopPDConsegnaRispostaAsincronaSimmetrica(protocol).replace("PD", "IntegrationManager/PD"));
			    this.log.info("Invocazione url del servizio di IntegrationManager ["+locator.getPDAddress()+"] ...");
			    PD_PortType port = locator.getPD();
			    
			    if(username !=null && password!=null){
                    // to use Basic HTTP Authentication: 
                    ((Stub) port)._setProperty(javax.xml.rpc.Call.USERNAME_PROPERTY, username);
                    ((Stub) port)._setProperty(javax.xml.rpc.Call.PASSWORD_PROPERTY, password);
			    }
			    
			    IntegrationManagerMessage msgRispostaIM = port.invocaPortaDelegata(portaCorrelata, msgIM);
			    
			    // Msg di risposta
				Message msgAxisResponse = Axis14SoapUtils.build(msgRispostaIM.getMessage(), false);
				//	java.io.ByteArrayOutputStream out =  new java.io.ByteArrayOutputStream();
				//msgAxisResponse.writeTo(out);
				//this.log.info("Messaggio ricevuto: "+out.toString());
				
				if (msgAxisResponse==null)
					this.log.error("[AsincronoSimmetrico_modalitaSincrona] Errore durante la ricezione della risposta");
				  				
			} catch (Exception e) {
				this.log.error("[AsincronoSimmetrico_modalitaSincrona] Errore durante la creazione/esecuzione del client IntegrationManager per l'invocazione della porta delegata: "+e.getMessage(),e);
			}    
		}else{
			javax.xml.soap.MimeHeaders mime=msg.getMimeHeaders();
			mime.addHeader(this.testSuiteProperties.getRiferimentoAsincronoTrasporto(), id);
			
			Service service=new Service();
			try {
				Call call=(Call) service.createCall();
				call.setTargetEndpointAddress(this.testSuiteProperties.getOpenSPCoopPDConsegnaRispostaAsincronaSimmetrica(protocol)+portaCorrelata );
				if(username !=null && password!=null){
					call.setUsername(username);
					call.setPassword(password);
				}
				call.invoke(msg);
				
				Message resp = call.getResponseMessage();
				//java.io.ByteArrayOutputStream out =  new java.io.ByteArrayOutputStream();
				//resp.writeTo(out);
				//this.log.info("Messaggio ricevuto: "+out.toString());
					
				if (resp==null)
					this.log.error("[AsincronoSimmetrico_modalitaSincrona] Errore durante la ricezione della risposta");
					
			} catch (Exception e) {
				this.log.error("[AsincronoSimmetrico_modalitaSincrona] Errore durante la creazione del client per l'invocazione della porta delegata: "+e.getMessage(),e);
			} 
		}
		
		this.log.info("[AsincronoSimmetrico_modalitaSincrona] Gestione risposta con id correlato="+id+" inviata alla porta delegata "+portaCorrelata);
	}
}


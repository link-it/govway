/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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



package org.openspcoop2.testsuite.server;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.SOAPElement;

import org.apache.axis.Message;
import org.apache.axis.message.SOAPHeaderElement;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.integrazione.UtilitiesIntegrazioneWSAddressing;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.testsuite.axis14.Axis14SoapUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;



/**
 * Server Generale utilizzato per profili di collaborazione OneWay, Sincrono e AsincronoAsimmetrico
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@SuppressWarnings("serial")
public class ServerRaccoltaInfoIntegrazione extends ServerCore{

	/**
	 * 
	 */
	public ServerRaccoltaInfoIntegrazione() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public void init() {}


	@Override
	public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException{

		try{

			// Lettura Richiesta
			InputStream in=request.getInputStream();
			int length=request.getContentLength();
			String type=request.getContentType();
			response.setContentType(type); // per ora imposto questo come risposta
			response.setStatus(500);
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			int read=0;
			while((read=in.read()) != -1){
				bout.write(read);
			}
			in.close();
			
			
			// Check WSAddressing
			boolean checkWSAddressing = false;
			String prop = request.getParameter("checkWSA");
			if(prop!=null)
				checkWSAddressing = Boolean.parseBoolean(prop.trim());
			
			
			// Check UserAgent
			boolean checkUserAgent = false;
			String checkUserAgentProp = request.getParameter("checkUserAgent");
			if(checkUserAgentProp!=null)
				checkUserAgent = Boolean.parseBoolean(checkUserAgentProp.trim());
			
			
			String protocollo = request.getParameter("protocol");
			if(protocollo==null){
				protocollo = ProtocolFactoryManager.getInstance().getDefaultProtocolFactory().getProtocol();
			}
			
			
			// Raccolta informazioni dall'header di trasporto.
			String idTrasporto=request.getHeader(this.testsuiteProperties.getIdMessaggioTrasporto());
			this.log.debug("trovato nel trasporto: "+idTrasporto);
			if(idTrasporto==null){
				this.log.error("Id della richiesta non presente nell'header di trasporto");
				returnSOAPFault(response,"Trasporto, id non presente");
				return;
			} 
			String idTipoMittente=request.getHeader(this.testsuiteProperties.getTipoMittenteTrasporto());
			this.log.debug("trovato nel trasporto: "+idTipoMittente);
			if(idTipoMittente==null){
				this.log.error("Tipo mittente della richiesta non presente nell'header di trasporto");
				returnSOAPFault(response,"Trasporto, tipo mittente non presente");
				return;
			} 
			String idMittente=request.getHeader(this.testsuiteProperties.getMittenteTrasporto());
			this.log.debug("trovato nel trasporto: "+idMittente);
			if(idMittente==null){
				this.log.error("Mittente della richiesta non presente nell'header di trasporto");
				returnSOAPFault(response,"Trasporto, mittente non presente");
				return;
			} 
			String idTipoDestinatario=request.getHeader(this.testsuiteProperties.getTipoDestinatarioTrasporto());
			this.log.debug("trovato nel trasporto: "+idTipoDestinatario);
			if(idTipoDestinatario==null){
				this.log.error("Tipo destinatario della richiesta non presente nell'header di trasporto");
				returnSOAPFault(response,"Trasporto, tipo destinatario non presente");
				return;
			} 
			String idDestinatario=request.getHeader(this.testsuiteProperties.getDestinatarioTrasporto());
			this.log.debug("trovato nel trasporto: "+idDestinatario);
			if(idDestinatario==null){
				this.log.error("Destinatario della richiesta non presente nell'header di trasporto");
				returnSOAPFault(response,"Trasporto, destinatario non presente");
				return;
			} 
			String idTipoServizio=request.getHeader(this.testsuiteProperties.getTipoServizioTrasporto());
			this.log.debug("trovato nel trasporto: "+idTipoServizio);
			if(idTipoServizio==null){
				this.log.error("Tipo servizio della richiesta non presente nell'header di trasporto");
				returnSOAPFault(response,"Trasporto, tipo servizio non presente");
				return;
			} 
			String idNomeServizio=request.getHeader(this.testsuiteProperties.getNomeServizioTrasporto());
			this.log.debug("trovato nel trasporto: "+idNomeServizio);
			if(idNomeServizio==null){
				this.log.error("Nome Servizio della richiesta non presente nell'header di trasporto");
				returnSOAPFault(response,"Trasporto, nome servizio non presente");
				return;
			} 
			String idVersioneServizio=request.getHeader(this.testsuiteProperties.getVersioneServizioTrasporto());
			this.log.debug("trovato nel trasporto: "+idVersioneServizio);
			if(idVersioneServizio==null){
				this.log.error("Versione Servizio della richiesta non presente nell'header di trasporto");
				returnSOAPFault(response,"Trasporto, versione servizio non presente");
				return;
			} 
			String idAzione=request.getHeader(this.testsuiteProperties.getAzioneTrasporto());
			this.log.debug("trovato nel trasporto: "+idAzione);
			
			if(checkUserAgent){
				String userAgent=request.getHeader(HttpConstants.USER_AGENT);
				this.log.debug("trovato nel trasporto: "+userAgent);
				if(userAgent==null){
					this.log.error(""+HttpConstants.USER_AGENT+" non presente nell'header di trasporto");
					returnSOAPFault(response,"Trasporto, "+HttpConstants.USER_AGENT+" non presente");
					return;
				} 
				if(userAgent.contains("GovWay")==false){
					this.log.error(""+HttpConstants.USER_AGENT+" con valore non atteso presente nell'header di trasporto (trovato:"+userAgent+" si attendeva un agent che contenesse la stringa GovWay)");
					returnSOAPFault(response,"Trasporto, "+HttpConstants.USER_AGENT+" non valido");
					return;
				} 
			}
			
			String idTransazione=request.getHeader(this.testsuiteProperties.getIDTransazioneTrasporto());
			this.log.debug("trovato nel trasporto: "+idTransazione);
			if(idTransazione==null){
				this.log.error("IdTransazione della richiesta non presente nell'header di trasporto");
				returnSOAPFault(response,"Trasporto, IdTransazione non presente");
				return;
			} 
			
			String xPdD=request.getHeader(CostantiPdD.HEADER_HTTP_X_PDD);
			this.log.debug("trovato nel trasporto: "+xPdD);
			if(xPdD==null){
				this.log.error(""+CostantiPdD.HEADER_HTTP_X_PDD+" non presente nell'header di trasporto");
				returnSOAPFault(response,"Trasporto, "+CostantiPdD.HEADER_HTTP_X_PDD+" non presente");
				return;
			} 
			if(xPdD.contains("GovWay")==false){
				this.log.error(""+CostantiPdD.HEADER_HTTP_X_PDD+" con valore non atteso presente nell'header di trasporto (trovato:"+xPdD+" si attendeva un valore che contenesse la stringa GovWay)");
				returnSOAPFault(response,"Trasporto, "+CostantiPdD.HEADER_HTTP_X_PDD+" non valido");
				return;
			} 
			
			String xDetails=request.getHeader(CostantiPdD.HEADER_HTTP_X_PDD_DETAILS);
			this.log.debug("trovato nel trasporto: "+xDetails);
			if(xDetails==null){
				this.log.error(CostantiPdD.HEADER_HTTP_X_PDD_DETAILS+" non presente nell'header di trasporto");
				returnSOAPFault(response,"Trasporto, "+CostantiPdD.HEADER_HTTP_X_PDD_DETAILS+" non presente");
				return;
			} 
			
	
			// Check informazioni prese dall'header di trasporto che siano presenti anche nella url invocata.
			Enumeration<?> parameters = request.getParameterNames();
			while (parameters.hasMoreElements()) {
				String name = (String) parameters.nextElement();
				this.log.debug("ParameterUrl nome["+name+"] valore["+request.getParameter(name)+"]");
			}
			String param = request.getParameter(this.testsuiteProperties.getIdUrlBased());
			this.log.debug("trovato nella url: "+param);
			if(param==null){
				this.log.error("Id della richiesta non presente nell'url invocata");
				returnSOAPFault(response,"Url, id non presente");
				return;
			} 
			if(param.equals(idTrasporto)==false){
				this.log.error("Id trovato nel trasporto["+idTrasporto+"] diverso da quello trovato nella url["+param+"]");
				returnSOAPFault(response,"Id trovato nel trasporto["+idTrasporto+"] diverso da quello trovato nella url["+param+"]");
				return;
			}
			param = request.getParameter(this.testsuiteProperties.getTipoMittenteUrlBased());
			this.log.debug("trovato nella url: "+param);
			if(param==null){
				this.log.error("Tipo mittente della richiesta non presente nell'url invocata");
				returnSOAPFault(response,"Url, tipo mittente non presente");
				return;
			} 
			if(param.equals(idTipoMittente)==false){
				this.log.error("Tipo mittente trovato nel trasporto["+idTipoMittente+"] diverso da quello trovato nella url["+param+"]");
				returnSOAPFault(response,"Tipo mittente trovato nel trasporto["+idTipoMittente+"] diverso da quello trovato nella url["+param+"]");
				return;
			}
			param = request.getParameter(this.testsuiteProperties.getMittenteUrlBased());
			this.log.debug("trovato nella url: "+param);
			if(param==null){
				this.log.error("Mittente della richiesta non presente nell'url invocata");
				returnSOAPFault(response,"Url, mittente non presente");
				return;
			} 
			if(param.equals(idMittente)==false){
				this.log.error("Mittente trovato nel trasporto["+idMittente+"] diverso da quello trovato nella url["+param+"]");
				returnSOAPFault(response,"Mittente trovato nel trasporto["+idMittente+"] diverso da quello trovato nella url["+param+"]");
				return;
			}
			param = request.getParameter(this.testsuiteProperties.getTipoDestinatarioUrlBased());
			this.log.debug("trovato nella url: "+param);
			if(param==null){
				this.log.error("Tipo destinatario della richiesta non presente nell'url invocata");
				returnSOAPFault(response,"Url, tipo destinatario non presente");
				return;
			} 
			if(param.equals(idTipoDestinatario)==false){
				this.log.error("Tipo destinatario trovato nel trasporto["+idTipoDestinatario+"] diverso da quello trovato nella url["+param+"]");
				returnSOAPFault(response,"Tipo destinatario trovato nel trasporto["+idTipoDestinatario+"] diverso da quello trovato nella url["+param+"]");
				return;
			}
			param = request.getParameter(this.testsuiteProperties.getDestinatarioUrlBased());
			this.log.debug("trovato nella url: "+param);
			if(param==null){
				this.log.error("Destinatario della richiesta non presente nell'url invocata");
				returnSOAPFault(response,"Url, destinatario non presente");
				return;
			} 
			if(param.equals(idDestinatario)==false){
				this.log.error("Destinatario trovato nel trasporto["+idDestinatario+"] diverso da quello trovato nella url["+param+"]");
				returnSOAPFault(response,"Destinatario trovato nel trasporto["+idDestinatario+"] diverso da quello trovato nella url["+param+"]");
				return;
			}
			param = request.getParameter(this.testsuiteProperties.getTipoServizioUrlBased());
			this.log.debug("trovato nella url: "+param);
			if(param==null){
				this.log.error("Tipo servizio della richiesta non presente nell'url invocata");
				returnSOAPFault(response,"Url, tipo servizio non presente");
				return;
			} 
			if(param.equals(idTipoServizio)==false){
				this.log.error("Tipo servizio trovato nel trasporto["+idTipoServizio+"] diverso da quello trovato nella url["+param+"]");
				returnSOAPFault(response,"Tipo servizio trovato nel trasporto["+idTipoServizio+"] diverso da quello trovato nella url["+param+"]");
				return;
			}
			param = request.getParameter(this.testsuiteProperties.getNomeServizioUrlBased());
			this.log.debug("trovato nella url: "+param);
			if(param==null){
				this.log.error("Nome servizio della richiesta non presente nell'url invocata");
				returnSOAPFault(response,"Url, nome servizio non presente");
				return;
			} 
			if(param.equals(idNomeServizio)==false){
				this.log.error("Servizio trovato nel trasporto["+idNomeServizio+"] diverso da quello trovato nella url["+param+"]");
				returnSOAPFault(response,"Servizio trovato nel trasporto["+idNomeServizio+"] diverso da quello trovato nella url["+param+"]");
				return;
			}
			param = request.getParameter(this.testsuiteProperties.getVersioneServizioUrlBased());
			this.log.debug("trovato nella url: "+param);
			if(param==null){
				this.log.error("Versione servizio della richiesta non presente nell'url invocata");
				returnSOAPFault(response,"Url, Versione servizio non presente");
				return;
			} 
			if(param.equals(idVersioneServizio)==false){
				this.log.error("Versione servizio trovato nel trasporto["+idVersioneServizio+"] diverso da quello trovato nella url["+param+"]");
				returnSOAPFault(response,"Versione servizio trovato nel trasporto["+idVersioneServizio+"] diverso da quello trovato nella url["+param+"]");
				return;
			}
			param = request.getParameter(this.testsuiteProperties.getAzioneUrlBased());
			this.log.debug("trovato nella url: "+param);
			if((param==null) && (idAzione!=null)){
				this.log.error("Azione della richiesta non presente nell'url invocata");
				returnSOAPFault(response,"Url, azione non presente");
				return;
			} 
			if(param.equals(idAzione)==false){
				this.log.error("Azione trovato nel trasporto["+idAzione+"] diverso da quello trovato nella url["+param+"]");
				returnSOAPFault(response,"Azione trovato nel trasporto["+idAzione+"] diverso da quello trovato nella url["+param+"]");
				return;
			}
			
			param = request.getParameter(this.testsuiteProperties.getIDTransazioneUrlBased());
			this.log.debug("trovato nella url: "+param);
			if(param==null){
				this.log.error("IdTransazione della richiesta non presente nell'url invocata");
				returnSOAPFault(response,"Url, IdTransazione non presente");
				return;
			} 
			if(param.equals(idTransazione)==false){
				this.log.error("IdTransazione trovato nel trasporto["+idTransazione+"] diverso da quello trovato nella url["+param+"]");
				returnSOAPFault(response,"IdTransazione trovato nel trasporto["+idTransazione+"] diverso da quello trovato nella url["+param+"]");
				return;
			}
			
			String urlPdD=request.getParameter(CostantiPdD.URL_BASED_PDD);
			this.log.debug("trovato nella url: "+urlPdD);
			if(urlPdD==null){
				this.log.error(""+CostantiPdD.URL_BASED_PDD+" non presente nella url");
				returnSOAPFault(response,"Url, "+CostantiPdD.URL_BASED_PDD+" non presente");
				return;
			} 
			if(urlPdD.contains("GovWay")==false){
				this.log.error(""+CostantiPdD.URL_BASED_PDD+" con valore non atteso presente nella url (trovato:"+urlPdD+" si attendeva un valore che contenesse la stringa GovWay)");
				returnSOAPFault(response,"Url, "+CostantiPdD.URL_BASED_PDD+" non valido");
				return;
			} 
			
			String urlDetails=request.getParameter(CostantiPdD.URL_BASED_PDD_DETAILS);
			this.log.debug("trovato nella url: "+urlDetails);
			if(urlDetails==null){
				this.log.error(CostantiPdD.URL_BASED_PDD_DETAILS+" non presente nella url");
				returnSOAPFault(response,"Url, "+CostantiPdD.URL_BASED_PDD_DETAILS+" non presente");
				return;
			} 
			

			// Costruzione messaggio
			Message requestMessage = Axis14SoapUtils.build(bout.toByteArray(), false);
			if(requestMessage==null){
				this.log.error("Ricostruzione messaggio non riuscita");
				returnSOAPFault(response,"Ricostruzione messaggio non riuscita");
				return;
			}
			if(requestMessage.getSOAPHeader()==null){
				this.log.error("Header SOAP non presente");
				returnSOAPFault(response,"Header SOAP non presente");
				return;
			}
			if(requestMessage.getSOAPHeader().hasChildNodes()==false){
				this.log.error("Header SOAP vuoto");
				returnSOAPFault(response,"Header SOAP vuoto");
				return;
			}
			Iterator<?> elements = requestMessage.getSOAPHeader().getChildElements();
			
			// Integrazione Proprietaria GovWay
			boolean find = false;
			boolean findTipoMittente = false;
			boolean findMittente = false;
			boolean findTipoDestinatario = false;
			boolean findDestinatario = false;
			boolean findTipoServizio = false;
			boolean findServizio = false;
			boolean findVersioneServizio = false;
			boolean findAzione = (idAzione==null);
			boolean findID = false;
			boolean findIDTransazione = false;
			boolean findProductVersion = false;
			boolean findProductDetails = false;
			
			// Integrazione WSAddressing
			boolean findMittenteWSA = false;
			boolean findDestinatarioWSA = false;
			boolean findAzioneWSA = (idAzione==null);
			boolean findIDWSA = false;
			
			try{
				while(elements.hasNext()){
					SOAPHeaderElement elem = (SOAPHeaderElement) elements.next();
					this.log.debug("Elemento nell'header name["+elem.getLocalName()+"] prefix["+elem.getPrefix()+"] namespace["+elem.getNamespaceURI()+"]");
					
					
					// Integrazione Proprietaria GovWay
					if(org.openspcoop2.core.integrazione.constants.Costanti.ROOT_LOCAL_NAME_INTEGRATION.equals(elem.getLocalName()) && "gw".equals(elem.getPrefix()) && org.openspcoop2.core.integrazione.constants.Costanti.TARGET_NAMESPACE.equals(elem.getNamespaceURI())){
						find = true;
						Iterator<?> attributes = elem.getAllAttributes();
						while(attributes.hasNext()){
							org.apache.axis.message.PrefixedQName attr = (org.apache.axis.message.PrefixedQName) attributes.next();
							if("mustUnderstand".equals(attr.getLocalName())){
								continue;
							}else if("actor".equals(attr.getLocalName())){
								continue;
							}else if(this.testsuiteProperties.getTipoMittenteSoap().equals(attr.getLocalName())){
								String v = elem.getAttributeValue(attr.getLocalName());
								this.log.debug("trovato nell'header soap: "+v);
								if(v==null){
									throw new Exception("Attributo ["+attr.getLocalName()+"] con valore null");
								}
								if(v.equals(idTipoMittente)==false){
									throw new Exception("Attributo ["+attr.getLocalName()+"] con valore ["+v+"] diverso da quello atteso ["+idTipoMittente+"]");
								}
								findTipoMittente = true;
							}else if(this.testsuiteProperties.getMittenteSoap().equals(attr.getLocalName())){
								String v = elem.getAttributeValue(attr.getLocalName());
								this.log.debug("trovato nell'header soap: "+v);
								if(v==null){
									throw new Exception("Attributo ["+attr.getLocalName()+"] con valore null");
								}
								if(v.equals(idMittente)==false){
									throw new Exception("Attributo ["+attr.getLocalName()+"] con valore ["+v+"] diverso da quello atteso ["+idMittente+"]");
								}
								findMittente = true;
							}else if(this.testsuiteProperties.getTipoDestinatarioSoap().equals(attr.getLocalName())){
								String v = elem.getAttributeValue(attr.getLocalName());
								this.log.debug("trovato nell'header soap: "+v);
								if(v==null){
									throw new Exception("Attributo ["+attr.getLocalName()+"] con valore null");
								}
								if(v.equals(idTipoDestinatario)==false){
									throw new Exception("Attributo ["+attr.getLocalName()+"] con valore ["+v+"] diverso da quello atteso ["+idTipoDestinatario+"]");
								}
								findTipoDestinatario = true;
							}else if(this.testsuiteProperties.getDestinatarioSoap().equals(attr.getLocalName())){
								String v = elem.getAttributeValue(attr.getLocalName());
								this.log.debug("trovato nell'header soap: "+v);
								if(v==null){
									throw new Exception("Attributo ["+attr.getLocalName()+"] con valore null");
								}
								if(v.equals(idDestinatario)==false){
									throw new Exception("Attributo ["+attr.getLocalName()+"] con valore ["+v+"] diverso da quello atteso ["+idDestinatario+"]");
								}
								findDestinatario = true;
							}else if(this.testsuiteProperties.getTipoServizioSoap().equals(attr.getLocalName())){
								String v = elem.getAttributeValue(attr.getLocalName());
								this.log.debug("trovato nell'header soap: "+v);
								if(v==null){
									throw new Exception("Attributo ["+attr.getLocalName()+"] con valore null");
								}
								if(v.equals(idTipoServizio)==false){
									throw new Exception("Attributo ["+attr.getLocalName()+"] con valore ["+v+"] diverso da quello atteso ["+idTipoServizio+"]");
								}
								findTipoServizio = true;
							}else if(this.testsuiteProperties.getNomeServizioSoap().equals(attr.getLocalName())){
								String v = elem.getAttributeValue(attr.getLocalName());
								this.log.debug("trovato nell'header soap: "+v);
								if(v==null){
									throw new Exception("Attributo ["+attr.getLocalName()+"] con valore null");
								}
								if(v.equals(idNomeServizio)==false){
									throw new Exception("Attributo ["+attr.getLocalName()+"] con valore ["+v+"] diverso da quello atteso ["+idNomeServizio+"]");
								}
								findServizio = true;
							}else if(this.testsuiteProperties.getVersioneServizioSoap().equals(attr.getLocalName())){
								String v = elem.getAttributeValue(attr.getLocalName());
								this.log.debug("trovato nell'header soap: "+v);
								if(v==null){
									throw new Exception("Attributo ["+attr.getLocalName()+"] con valore null");
								}
								if(v.equals(idVersioneServizio)==false){
									throw new Exception("Attributo ["+attr.getLocalName()+"] con valore ["+v+"] diverso da quello atteso ["+idVersioneServizio+"]");
								}
								findVersioneServizio = true;
							}else if(this.testsuiteProperties.getAzioneSoap().equals(attr.getLocalName())){
								String v = elem.getAttributeValue(attr.getLocalName());
								this.log.debug("trovato nell'header soap: "+v);
								if(v==null){
									throw new Exception("Attributo ["+attr.getLocalName()+"] con valore null");
								}
								if(v.equals(idAzione)==false){
									throw new Exception("Attributo ["+attr.getLocalName()+"] con valore ["+v+"] diverso da quello atteso ["+idAzione+"]");
								}
								findAzione = true;
							}else if(this.testsuiteProperties.getIdMessaggioSoap().equals(attr.getLocalName())){
								String v = elem.getAttributeValue(attr.getLocalName());
								this.log.debug("trovato nell'header soap: "+v);
								if(v==null){
									throw new Exception("Attributo ["+attr.getLocalName()+"] con valore null");
								}
								if(v.equals(idTrasporto)==false){
									throw new Exception("Attributo ["+attr.getLocalName()+"] con valore ["+v+"] diverso da quello atteso ["+idTrasporto+"]");
								}
								findID = true;
							}else if(this.testsuiteProperties.getIDTransazioneSoap().equals(attr.getLocalName())){
								String v = elem.getAttributeValue(attr.getLocalName());
								this.log.debug("trovato nell'header soap: "+v);
								if(v==null){
									throw new Exception("Attributo ["+attr.getLocalName()+"] con valore null");
								}
								if(v.equals(idTransazione)==false){
									throw new Exception("Attributo ["+attr.getLocalName()+"] con valore ["+v+"] diverso da quello atteso ["+idTransazione+"]");
								}
								findIDTransazione = true;
							}else if(CostantiPdD.HEADER_INTEGRAZIONE_SOAP_PDD_VERSION.equals(attr.getLocalName())){
								String v = elem.getAttributeValue(attr.getLocalName());
								if(v==null){
									throw new Exception("Attributo ["+attr.getLocalName()+"] con valore null");
								}
								if("".equals(v)){
									throw new Exception("Attributo ["+attr.getLocalName()+"] con valore ["+v+"]");
								}
								findProductVersion = true;
							}
							else if(CostantiPdD.HEADER_INTEGRAZIONE_SOAP_PDD_DETAILS.equals(attr.getLocalName())){
								String v = elem.getAttributeValue(attr.getLocalName());
								if(v==null){
									throw new Exception("Attributo ["+attr.getLocalName()+"] con valore null");
								}
								if("".equals(v)){
									throw new Exception("Attributo ["+attr.getLocalName()+"] con valore ["+v+"]");
								}
								findProductDetails = true;
							}
							this.log.debug("Attributo name["+attr.getLocalName()+"] prefix["+attr.getPrefix()+"]");
						}
					}
					
					
					// Campi WSAddressing
					if(checkWSAddressing){
						if("To".equals(elem.getLocalName()) && "wsa".equals(elem.getPrefix()) && "http://www.w3.org/2005/08/addressing".equals(elem.getNamespaceURI()) &&
								org.openspcoop2.core.integrazione.constants.Costanti.TARGET_NAMESPACE_WSA.equals(elem.getActor())){
							String v=elem.getValue();
							this.log.debug("trovato nell'header WSA(to): "+v);
							if(v==null){
								throw new Exception("Valore dell'elemento WSA-To non definito");
							}else{
								String test = UtilitiesIntegrazioneWSAddressing.buildDatiWSATo(idTipoDestinatario,idDestinatario, idTipoServizio,idNomeServizio,Integer.parseInt(idVersioneServizio));
								if(test.equals(v)==false){
									throw new Exception("WSATo con valore ["+v+"] diverso da quello atteso ["+test+"]");
								}
								findDestinatarioWSA=true;
							}
						}
						else if("From".equals(elem.getLocalName()) && "wsa".equals(elem.getPrefix()) && "http://www.w3.org/2005/08/addressing".equals(elem.getNamespaceURI()) &&
								org.openspcoop2.core.integrazione.constants.Costanti.TARGET_NAMESPACE_WSA.equals(elem.getActor())){
							String v=null;
							boolean addressFromFound = false;
							Iterator<?> itFROM = elem.getChildElements();
							while (itFROM.hasNext()) {
								Object o = itFROM.next();
								if(o!=null && (o instanceof SOAPElement) ){
									SOAPElement s = (SOAPElement) o;
									if("Address".equals(s.getLocalName())){
										addressFromFound = true;
										v=s.getValue();
										break;
									}
								}
							}
							this.log.debug("trovato nell'header WSA(from) (epr-address:"+addressFromFound+"): "+v);
							if(!addressFromFound){
								throw new Exception("Elemento Address interno all'elemento WSA-From non definito");
							}
							if(v==null){
								throw new Exception("Valore dell'elemento WSA-From non definito");
							}else{
								String test = UtilitiesIntegrazioneWSAddressing.buildDatiWSAFrom(null,idTipoMittente, idMittente);
								if(test.equals(v)==false){
									throw new Exception("WSAFrom con valore ["+v+"] diverso da quello atteso ["+test+"]");
								}
								findMittenteWSA=true;
							}
						}
						else if("Action".equals(elem.getLocalName()) && "wsa".equals(elem.getPrefix()) && "http://www.w3.org/2005/08/addressing".equals(elem.getNamespaceURI()) &&
								org.openspcoop2.core.integrazione.constants.Costanti.TARGET_NAMESPACE_WSA.equals(elem.getActor())){
							String v=elem.getValue();
							this.log.debug("trovato nell'header WSA(action): "+v);
							if(v==null){
								throw new Exception("Valore dell'elemento WSA-Action non definito");
							}else{
								String test = UtilitiesIntegrazioneWSAddressing.buildDatiWSAAction(idTipoDestinatario,idDestinatario, idTipoServizio,idNomeServizio,Integer.parseInt(idVersioneServizio), idAzione);
								if(test.equals(v)==false){
									throw new Exception("WSAAction con valore ["+v+"] diverso da quello atteso ["+test+"]");
								}
								findAzioneWSA=true;
							}
						}
						else if("MessageID".equals(elem.getLocalName()) && "wsa".equals(elem.getPrefix()) && "http://www.w3.org/2005/08/addressing".equals(elem.getNamespaceURI()) &&
								org.openspcoop2.core.integrazione.constants.Costanti.TARGET_NAMESPACE_WSA.equals(elem.getActor())){
							String v=elem.getValue();
							this.log.debug("trovato nell'header WSA(id): "+v);
							if(v==null){
								throw new Exception("Valore dell'elemento WSA-MessageID non definito");
							}else{
								String test = "uuid:"+idTrasporto;
								if(test.equals(v)==false){
									throw new Exception("WSAMessageID con valore ["+v+"] diverso da quello atteso ["+test+"]");
								}
								findIDWSA=true;
							}
						}
					}
				}
				if(find==false){
					throw new Exception("Integrazione non trovata");
				}
				if(findTipoMittente==false){
					throw new Exception("Integrazione.tipoMittente non trovata");
				}
				if(findMittente==false){
					throw new Exception("Integrazione.mittente non trovata");
				}
				if(findTipoDestinatario==false){
					throw new Exception("Integrazione.tipoDestinatario non trovata");
				}
				if(findDestinatario==false){
					throw new Exception("Integrazione.destinatario non trovata");
				}
				if(findTipoServizio==false){
					throw new Exception("Integrazione.tipoServizio non trovata");
				}
				if(findServizio==false){
					throw new Exception("Integrazione.servizio non trovata");
				}
				if(findVersioneServizio==false){
					throw new Exception("Integrazione.versioneServizio non trovata");
				}
				if(findAzione==false){
					throw new Exception("Integrazione.azione non trovata");
				}
				if(findID==false){
					throw new Exception("Integrazione.id non trovata");
				}
				if(findIDTransazione==false){
					throw new Exception("Integrazione.idTransazione non trovata");
				}
				if(findProductVersion==false){
					throw new Exception("Integrazione."+CostantiPdD.HEADER_INTEGRAZIONE_SOAP_PDD_VERSION+" non trovata");
				}
				if(findProductDetails==false){
					throw new Exception("Integrazione."+CostantiPdD.HEADER_INTEGRAZIONE_SOAP_PDD_DETAILS+" non trovata");
				}
				
				if(checkWSAddressing){
					if(findMittenteWSA==false){
						throw new Exception("Integrazione.wsa.from non trovata");
					}
					if(findDestinatarioWSA==false){
						throw new Exception("Integrazione.wsa.to non trovata");
					}
					if(findAzioneWSA==false){
						throw new Exception("Integrazione.wsa.action non trovata");
					}
					if(findIDWSA==false){
						throw new Exception("Integrazione.wsa.messageId non trovata");
					}
				}
				
			}catch(Exception e){
				this.log.error("Header integrazione non corretto: "+e.getMessage());
				returnSOAPFault(response,"Header integrazione non corretto: "+e.getMessage());
				return;
			}
			
			
			
			// Ritorno risposta
			
			response.setContentLength(length);
			response.setContentType(type);
			response.setStatus(200);
			BufferedOutputStream bos=new BufferedOutputStream(response.getOutputStream());
			bos.write(bout.toByteArray());
			bos.close();

			
			String checkTracciaString = request.getParameter("checkTraccia");
			boolean checkTraccia = true;
			if(checkTracciaString != null) {
				checkTraccia = Boolean.parseBoolean(checkTracciaString.trim());
			}
			
			if(this.testsuiteProperties.traceArrivedIntoDB() && checkTraccia){
				if(idTrasporto!=null){
					this.tracciaIsArrivedIntoDatabase(idTrasporto,idDestinatario,protocollo);
				}
			}
			
			this.log.info("Gestione richiesta con id="+idTrasporto+" effettata, messaggio gestito:\n"+bout.toString());

		}catch(Exception e){
			this.log.error("Errore durante la gestione di una richiesta: "+e.getMessage(),e);
			try{
				returnSOAPFault(response,"Errore durante la gestione di una richiesta: "+e.getMessage());
			}catch(Exception eFinal){
				throw new ServletException("Errore durante la gestione di una richiesta: "+eFinal.getMessage());
			}
			
		}
	}

	@Override
	public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException{
		doGet(request,response);
	}
	
	
	
	public void returnSOAPFault(HttpServletResponse response ,String messaggio) throws Exception{
		response.setContentType(HttpConstants.CONTENT_TYPE_SOAP_1_1);
		ServletOutputStream sout = response.getOutputStream();
		org.apache.axis.soap.MessageFactoryImpl mf = new org.apache.axis.soap.MessageFactoryImpl();
		org.apache.axis.Message msg = (org.apache.axis.Message) mf.createMessage();
		org.apache.axis.message.SOAPEnvelope env = msg.getSOAPEnvelope();
		org.apache.axis.message.SOAPBody bdy = (org.apache.axis.message.SOAPBody) env.getBody();
		bdy.addFault();
		org.apache.axis.message.SOAPFault f = (org.apache.axis.message.SOAPFault) bdy.getFault();
		f.setFaultString(messaggio);
		f.setFaultCode("Server.infoMancante");
		f.setFaultActor("TestSuite");
		msg.writeTo(sout);
	}
}

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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.Name;

import org.apache.axis.Message;
import org.apache.axis.message.PrefixedQName;
import org.apache.axis.message.SOAPHeaderElement;
import org.openspcoop2.pdd.core.integrazione.UtilitiesIntegrazioneWSAddressing;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.testsuite.axis14.Axis14SoapUtils;
import org.openspcoop2.utils.date.UniqueIDGenerator;
import org.w3c.dom.Document;



/**
 * Server Generale utilizzato per profili di collaborazione OneWay, Sincrono e AsincronoAsimmetrico
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@SuppressWarnings("serial")
public class ServerHeaderIntegrazioneRisposta extends ServerCore{

	/**
	 * 
	 */
	public ServerHeaderIntegrazioneRisposta() {
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
			//int length=request.getContentLength();
			//String type=request.getContentType();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			int read=0;
			while((read=in.read()) != -1){
				bout.write(read);
			}
			in.close();
			
			// Lettura parametri
			String idTrasporto=request.getHeader(this.testsuiteProperties.getIdMessaggioTrasporto());
			this.log.debug("trovato nel trasporto: "+idTrasporto);
			if(idTrasporto==null){
				this.log.error("Id della richiesta non presente nell'header di trasporto");
				returnSOAPFault(response.getOutputStream(),"Trasporto, id non presente");
				return;
			} 
			String idTipoMittente=request.getHeader(this.testsuiteProperties.getTipoMittenteTrasporto());
			this.log.debug("trovato nel trasporto: "+idTipoMittente);
			if(idTipoMittente==null){
				this.log.error("Tipo mittente della richiesta non presente nell'header di trasporto");
				returnSOAPFault(response.getOutputStream(),"Trasporto, tipo mittente non presente");
				return;
			} 
			String idMittente=request.getHeader(this.testsuiteProperties.getMittenteTrasporto());
			this.log.debug("trovato nel trasporto: "+idMittente);
			if(idMittente==null){
				this.log.error("Mittente della richiesta non presente nell'header di trasporto");
				returnSOAPFault(response.getOutputStream(),"Trasporto, mittente non presente");
				return;
			} 
			String idTipoDestinatario=request.getHeader(this.testsuiteProperties.getTipoDestinatarioTrasporto());
			this.log.debug("trovato nel trasporto: "+idTipoDestinatario);
			if(idTipoDestinatario==null){
				this.log.error("Tipo destinatario della richiesta non presente nell'header di trasporto");
				returnSOAPFault(response.getOutputStream(),"Trasporto, tipo destinatario non presente");
				return;
			} 
			String idDestinatario=request.getHeader(this.testsuiteProperties.getDestinatarioTrasporto());
			this.log.debug("trovato nel trasporto: "+idDestinatario);
			if(idDestinatario==null){
				this.log.error("Destinatario della richiesta non presente nell'header di trasporto");
				returnSOAPFault(response.getOutputStream(),"Trasporto, destinatario non presente");
				return;
			} 
			String idTipoServizio=request.getHeader(this.testsuiteProperties.getTipoServizioTrasporto());
			this.log.debug("trovato nel trasporto: "+idTipoServizio);
			if(idTipoServizio==null){
				this.log.error("Tipo servizio della richiesta non presente nell'header di trasporto");
				returnSOAPFault(response.getOutputStream(),"Trasporto, tipo servizio non presente");
				return;
			} 
			String idServizio=request.getHeader(this.testsuiteProperties.getServizioTrasporto());
			this.log.debug("trovato nel trasporto: "+idServizio);
			if(idServizio==null){
				this.log.error("Servizio della richiesta non presente nell'header di trasporto");
				returnSOAPFault(response.getOutputStream(),"Trasporto, servizio non presente");
				return;
			} 
			String idAzione=request.getHeader(this.testsuiteProperties.getAzioneTrasporto());
			this.log.debug("trovato nel trasporto: "+idAzione);
			
			String tipoIntegrazione = request.getParameter("tipoIntegrazione");
			if(tipoIntegrazione==null){
				returnSOAPFault(response.getOutputStream(),"Parametro 'tipoIntegrazione' non definito");
			}
			
			String protocollo = request.getParameter("protocol");
			if(protocollo==null){
				protocollo = ProtocolFactoryManager.getInstance().getDefaultProtocolFactory().getProtocol();
			}
			
			// Generazione risposta
			Message msg = Axis14SoapUtils.build_Soap_Empty();
			Name nameTest = new PrefixedQName("http://www.openspcoop2.org","test","test");
			msg.getSOAPBody().addBodyElement(nameTest).setValue("Example");
			msg.getSOAPPartAsBytes();
			
			if("http".equals(tipoIntegrazione)){
				response.setHeader(this.testsuiteProperties.getIdMessaggioTrasporto(), idTrasporto);
				response.setHeader(this.testsuiteProperties.getTipoMittenteTrasporto(), idTipoMittente);
				response.setHeader(this.testsuiteProperties.getMittenteTrasporto(), idMittente);
				response.setHeader(this.testsuiteProperties.getTipoDestinatarioTrasporto(), idTipoDestinatario);
				response.setHeader(this.testsuiteProperties.getDestinatarioTrasporto(), idDestinatario);
				response.setHeader(this.testsuiteProperties.getTipoServizioTrasporto(), idTipoServizio);
				response.setHeader(this.testsuiteProperties.getServizioTrasporto(), idServizio);
				response.setHeader(this.testsuiteProperties.getAzioneTrasporto(), idAzione);
				response.setHeader(this.testsuiteProperties.getIDApplicativoTrasporto(), "ID-APPLICATIVO-RISPOSTA-HTTP-"+UniqueIDGenerator.getUniqueID());
			}
			
			else if("soap".equals(tipoIntegrazione)){
				Name name = new PrefixedQName("http://www.openspcoop2.org/core/integrazione","integrazione","openspcoop");
				org.apache.axis.message.SOAPHeaderElement header = 
					new org.apache.axis.message.SOAPHeaderElement(name);
				header.setActor("http://www.openspcoop2.org/core/integrazione");
				header.setMustUnderstand(false);
				header.addNamespaceDeclaration("SOAP_ENV","http://schemas.xmlsoap.org/soap/envelope/");

				header.setAttribute(this.testsuiteProperties.getIdMessaggioSoap(), idTrasporto);
				header.setAttribute(this.testsuiteProperties.getTipoMittenteSoap(), idTipoMittente);
				header.setAttribute(this.testsuiteProperties.getMittenteSoap(), idMittente);
				header.setAttribute(this.testsuiteProperties.getTipoDestinatarioSoap(), idTipoDestinatario);
				header.setAttribute(this.testsuiteProperties.getDestinatarioSoap(), idDestinatario);
				header.setAttribute(this.testsuiteProperties.getTipoServizioSoap(), idTipoServizio);
				header.setAttribute(this.testsuiteProperties.getServizioSoap(), idServizio);
				header.setAttribute(this.testsuiteProperties.getAzioneSoap(), idAzione);
				header.setAttribute(this.testsuiteProperties.getIDApplicativoSoap(), "ID-APPLICATIVO-RISPOSTA-SOAP-"+UniqueIDGenerator.getUniqueID());
				
				if(msg.getSOAPHeader()==null)
					msg.getSOAPEnvelope().addHeader();
				msg.getSOAPHeader().addChildElement(header);
			}
			
			else if("wsa".equals(tipoIntegrazione)){

				if(msg.getSOAPHeader()==null)
					msg.getSOAPEnvelope().addHeader();
				
				Name nameTo = new PrefixedQName("http://www.w3.org/2005/08/addressing","To","wsa");
				org.apache.axis.message.SOAPHeaderElement headerTo = 
					new org.apache.axis.message.SOAPHeaderElement(nameTo);
				headerTo.setActor("http://www.openspcoop2.org/core/integrazione/wsa");
				headerTo.setMustUnderstand(false);
				headerTo.addNamespaceDeclaration("SOAP_ENV","http://schemas.xmlsoap.org/soap/envelope/");
				headerTo.setValue(UtilitiesIntegrazioneWSAddressing.buildDatiWSATo(idTipoDestinatario,idDestinatario, idTipoServizio,idServizio));
				byte [] headerByteTo = Axis14SoapUtils.msgElementoToByte(headerTo);
				ByteArrayInputStream inputTo = new ByteArrayInputStream(headerByteTo);
				Document documentTo = org.apache.axis.utils.XMLUtils.newDocument(inputTo);
				SOAPHeaderElement elementSenzaXSITypesTo = new SOAPHeaderElement(documentTo.getDocumentElement());
				msg.getSOAPHeader().addChildElement(elementSenzaXSITypesTo);
				
				Name nameFrom = new PrefixedQName("http://www.w3.org/2005/08/addressing","From","wsa");
				org.apache.axis.message.SOAPHeaderElement headerFrom = 
					new org.apache.axis.message.SOAPHeaderElement(nameFrom);
				headerFrom.setActor("http://www.openspcoop2.org/core/integrazione/wsa");
				headerFrom.setMustUnderstand(false);
				headerFrom.addNamespaceDeclaration("SOAP_ENV","http://schemas.xmlsoap.org/soap/envelope/");
				Name nameAddressEPR =  new PrefixedQName("http://www.w3.org/2005/08/addressing","Address","wsa");
				headerFrom.addChildElement(nameAddressEPR).setValue(UtilitiesIntegrazioneWSAddressing.buildDatiWSAFrom(null,idTipoMittente, idMittente));
				byte [] headerByteFrom = Axis14SoapUtils.msgElementoToByte(headerFrom);
				ByteArrayInputStream inputFrom = new ByteArrayInputStream(headerByteFrom);
				Document documentFrom = org.apache.axis.utils.XMLUtils.newDocument(inputFrom);
				SOAPHeaderElement elementSenzaXSITypesFrom = new SOAPHeaderElement(documentFrom.getDocumentElement());
				msg.getSOAPHeader().addChildElement(elementSenzaXSITypesFrom);
				
				if(idAzione!=null){
					Name nameAction = new PrefixedQName("http://www.w3.org/2005/08/addressing","Action","wsa");
					org.apache.axis.message.SOAPHeaderElement headerAction = 
						new org.apache.axis.message.SOAPHeaderElement(nameAction);
					headerAction.setActor("http://www.openspcoop2.org/core/integrazione/wsa");
					headerAction.setMustUnderstand(false);
					headerAction.addNamespaceDeclaration("SOAP_ENV","http://schemas.xmlsoap.org/soap/envelope/");
					headerAction.setValue(UtilitiesIntegrazioneWSAddressing.buildDatiWSAAction(idTipoDestinatario,idDestinatario, idTipoServizio,idServizio, idAzione));
					byte [] headerByteAction = Axis14SoapUtils.msgElementoToByte(headerAction);
					ByteArrayInputStream inputAction = new ByteArrayInputStream(headerByteAction);
					Document documentAction = org.apache.axis.utils.XMLUtils.newDocument(inputAction);
					SOAPHeaderElement elementSenzaXSITypesAction = new SOAPHeaderElement(documentAction.getDocumentElement());
					msg.getSOAPHeader().addChildElement(elementSenzaXSITypesAction);
				}
				
				Name nameRelatesTo = new PrefixedQName("http://www.w3.org/2005/08/addressing","RelatesTo","wsa");
				org.apache.axis.message.SOAPHeaderElement headerRelatesTo = 
					new org.apache.axis.message.SOAPHeaderElement(nameRelatesTo);
				headerRelatesTo.setActor("http://www.openspcoop2.org/core/integrazione/wsa");
				headerRelatesTo.setMustUnderstand(false);
				headerRelatesTo.addNamespaceDeclaration("SOAP_ENV","http://schemas.xmlsoap.org/soap/envelope/");
				headerRelatesTo.setValue(UtilitiesIntegrazioneWSAddressing.buildDatiWSARelatesTo(idTrasporto));
				byte [] headerByteRelatesTo = Axis14SoapUtils.msgElementoToByte(headerRelatesTo);
				ByteArrayInputStream inputRelatesTo = new ByteArrayInputStream(headerByteRelatesTo);
				Document documentRelatesTo = org.apache.axis.utils.XMLUtils.newDocument(inputRelatesTo);
				SOAPHeaderElement elementSenzaXSITypesRelatesTo = new SOAPHeaderElement(documentRelatesTo.getDocumentElement());
				msg.getSOAPHeader().addChildElement(elementSenzaXSITypesRelatesTo);
				
				Name nameMessageId = new PrefixedQName("http://www.w3.org/2005/08/addressing","MessageID","wsa");
				org.apache.axis.message.SOAPHeaderElement headerMessageId = 
					new org.apache.axis.message.SOAPHeaderElement(nameMessageId);
				headerMessageId.setActor("http://www.openspcoop2.org/core/integrazione/wsa");
				headerMessageId.setMustUnderstand(false);
				headerMessageId.addNamespaceDeclaration("SOAP_ENV","http://schemas.xmlsoap.org/soap/envelope/");
				headerMessageId.setValue("uuid:"+"ID-APPLICATIVO-RISPOSTA-WSA-"+UniqueIDGenerator.getUniqueID());
				byte [] headerByteMessageId = Axis14SoapUtils.msgElementoToByte(headerMessageId);
				ByteArrayInputStream inputMessageId = new ByteArrayInputStream(headerByteMessageId);
				Document documentMessageId = org.apache.axis.utils.XMLUtils.newDocument(inputMessageId);
				SOAPHeaderElement elementSenzaXSITypesMessageId = new SOAPHeaderElement(documentMessageId.getDocumentElement());
				msg.getSOAPHeader().addChildElement(elementSenzaXSITypesMessageId);
			}
			
			else{
				returnSOAPFault(response.getOutputStream(),"Parametro 'tipoIntegrazione="+tipoIntegrazione+"' non supportato");
			}

						
			// Echo richiesta in risposta
			msg.saveChanges();
			response.setContentType(request.getContentType());
			msg.writeTo(response.getOutputStream());
			response.getOutputStream().flush();
			response.getOutputStream().close();
			
			
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
			
			//this.log.info("Gestione richiesta con id="+id+" effettata, messaggio gestito:\n"+bout.toString());
			this.log.info("Gestione richiesta con id="+idTrasporto+" effettata");

		}catch(Exception e){
			this.log.error("Errore durante la gestione di una richiesta: "+e.getMessage(),e);
			throw new ServletException("Errore durante la gestione di una richiesta: "+e.getMessage(),e);
		}
	}

	@Override
	public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException{
		doGet(request,response);
	}
	
	public void returnSOAPFault(ServletOutputStream sout,String messaggio) throws Exception{
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

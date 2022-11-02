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
package org.openspcoop2.pdd.core.controllo_traffico.policy;

import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.controllo_traffico.beans.DatiTransazione;
import org.openspcoop2.core.controllo_traffico.constants.TipoFiltroApplicativo;
import org.openspcoop2.message.MessageUtils;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.dynamic.PddPluginLoader;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;
import org.openspcoop2.pdd.core.controllo_traffico.plugins.Dati;
import org.openspcoop2.pdd.core.controllo_traffico.plugins.IRateLimiting;
import org.openspcoop2.pdd.core.handlers.InRequestProtocolContext;
import org.openspcoop2.protocol.sdk.state.URLProtocolContext;
import org.openspcoop2.utils.regexp.RegExpNotFoundException;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.xml.AbstractXPathExpressionEngine;
import org.openspcoop2.utils.xml2json.JsonXmlPathExpressionEngine;
import org.slf4j.Logger;
import org.w3c.dom.Element;

/**     
 * PolicyFiltroApplicativoUtilities
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PolicyFiltroApplicativoUtilities {

	public static String getValore(Logger log,String tipo, String nome, InRequestProtocolContext context, 
			DatiTransazione datiTransazione, boolean forFilter) throws Exception{
		
		OpenSPCoop2Message message = context.getMessaggio();
		
		URLProtocolContext urlProtocolContext = context.getConnettore().getUrlProtocolContext();
	
		String soapAction = context.getConnettore().getSoapAction();
		
		PdDContext pddContext = null;
		if(context!=null && context.getPddContext()!=null) {
			pddContext = context.getPddContext();
		}
		
		InfoConnettoreIngresso connettore = context.getConnettore();
		
		return getValore(log, tipo, nome, 
				datiTransazione, forFilter,
				message, urlProtocolContext, soapAction, pddContext,
				connettore);
	}
	public static String getValore(Logger log,String tipo, String nome, 
			DatiTransazione datiTransazione, boolean forFilter,
			OpenSPCoop2Message message, URLProtocolContext urlProtocolContext, String soapActionParam, PdDContext pddContext,
			InfoConnettoreIngresso connettore) throws Exception{
		
		
		TipoFiltroApplicativo tipoFiltro = TipoFiltroApplicativo.toEnumConstant(tipo);
		
		switch (tipoFiltro) {
		case CONTENT_BASED:
			
			String idTransazione = null;
			if(pddContext!=null) {
				idTransazione = (String)pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
			}
			
			AbstractXPathExpressionEngine xPathEngine = null;
			boolean bufferMessage_readOnly =  OpenSPCoop2Properties.getInstance().isReadByPathBufferEnabled();
			boolean checkSoapBodyEmpty = false; // devo poter fare xpath anche su soapBody empty
			Element element = null;
			String elementJson = null;
			if(message!=null) {
				element = MessageUtils.getContentElement(message, checkSoapBodyEmpty, bufferMessage_readOnly, idTransazione);
				elementJson = MessageUtils.getContentString(message, bufferMessage_readOnly, idTransazione);
			}
			if(element!=null) {
				xPathEngine = new org.openspcoop2.message.xml.XPathExpressionEngine(message.getFactory());
				return AbstractXPathExpressionEngine.extractAndConvertResultAsString(element, xPathEngine, nome,  log);
			}
			else if(elementJson!=null) {
				return JsonXmlPathExpressionEngine.extractAndConvertResultAsString(elementJson, nome, log);
			}
			else {
				return null; // semplicemente non deve matchare il filtro
			}
			
		case URLBASED:
			
			String urlInvocazionePD = urlProtocolContext.getUrlInvocazione_formBased();
			try{
				return RegularExpressionEngine.getStringMatchPattern(urlInvocazionePD, nome);
			}catch(RegExpNotFoundException notFound){
				return null;
			}

		case FORM_BASED:
			
			return urlProtocolContext.getParameterFirstValue(nome);
			
		case HEADER_BASED:
			
			return urlProtocolContext.getHeaderFirstValue(nome);
						
		case SOAPACTION_BASED:
			
			String soapAction = soapActionParam;
			if(soapAction==null) {
				// provo una soluzione veloce di vedere se è presente nell'header di trasporto o nel content-type
				// non so che tipo di message type possiedo
				try{
					soapAction = SoapUtils.getSoapAction(urlProtocolContext, MessageType.SOAP_11, urlProtocolContext.getContentType());
				}catch(Exception e){}
				if(soapAction==null){
					try{
						soapAction = SoapUtils.getSoapAction(urlProtocolContext, MessageType.SOAP_12, urlProtocolContext.getContentType());
					}catch(Exception e){}	
				}
			}
			if(soapAction!=null) {
				soapAction = soapAction.trim();
				if(soapAction.startsWith("\"") && soapAction.length()>1){
					soapAction = soapAction.substring(1);
				}
				if(soapAction.endsWith("\"")  && soapAction.length()>1){
					soapAction = soapAction.substring(0, (soapAction.length()-1));
				}
			}
			return soapAction;
			
		case INDIRIZZO_IP:
			
			if(pddContext!=null && pddContext.containsKey(Costanti.CLIENT_IP_REMOTE_ADDRESS)) {
				return (String) pddContext.getObject(Costanti.CLIENT_IP_REMOTE_ADDRESS);
			}
			return null;
			
		case INDIRIZZO_IP_FORWARDED:
			
			if(pddContext!=null && pddContext.containsKey(Costanti.CLIENT_IP_TRANSPORT_ADDRESS)) {
				return (String) pddContext.getObject(Costanti.CLIENT_IP_TRANSPORT_ADDRESS);
			}
			return null;
			
		case PLUGIN_BASED:
			
			IRateLimiting rateLimitingPlugin = null;
			try{
				rateLimitingPlugin = PddPluginLoader.getInstance().newRateLimiting(nome);
			}catch(Exception e){
				throw e;
			}
			
			String className = null;
			try{
				className = rateLimitingPlugin.getClass().getName();
				
				Dati datiRichiesta = new Dati();
				datiRichiesta.setConnettore(connettore);
				datiRichiesta.setDatiTransazione(datiTransazione);
				datiRichiesta.setMessaggio(message);
				datiRichiesta.setPddContext(pddContext);
				
				if(forFilter){
					return rateLimitingPlugin.estraiValoreFiltro(log,datiRichiesta);
				}
				else{
					return rateLimitingPlugin.estraiValoreCollezionamentoDati(log,datiRichiesta);
				}

			}catch(Exception e){
				throw new Exception("Instance plugin ["+nome+"] [class:"+className+"] error: "+e.getMessage(),e);
			}

		}
		
		throw new Exception("TipoFiltro ["+tipo+"] non gestito");
	}
	
}

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
package org.openspcoop2.pdd.core.controllo_traffico.policy;

import java.util.Map;

import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.controllo_traffico.beans.DatiTransazione;
import org.openspcoop2.core.controllo_traffico.constants.TipoFiltroApplicativo;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.core.controllo_traffico.plugins.Dati;
import org.openspcoop2.pdd.core.controllo_traffico.plugins.IRateLimiting;
import org.openspcoop2.pdd.core.handlers.InRequestProtocolContext;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.regexp.RegExpNotFoundException;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.transport.TransportUtils;
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
		
		
		TipoFiltroApplicativo tipoFiltro = TipoFiltroApplicativo.toEnumConstant(tipo);
		
		switch (tipoFiltro) {
		case CONTENT_BASED:
			AbstractXPathExpressionEngine xPathEngine = null;
			Element element = null;
			String elementJson = null;
			if(ServiceBinding.SOAP.equals(context.getMessaggio().getServiceBinding())){
				element = context.getMessaggio().castAsSoap().getSOAPPart().getEnvelope();
			}
			else{
				if(MessageType.XML.equals(context.getMessaggio().getMessageType())){
					if(context.getMessaggio().castAsRestXml().hasContent()) {
						element = context.getMessaggio().castAsRestXml().getContent();
					}
				}
				else if(MessageType.JSON.equals(context.getMessaggio().getMessageType())){
					if(context.getMessaggio().castAsRestJson().hasContent()) {
						elementJson = context.getMessaggio().castAsRestJson().getContent();
					}
				}
				else{
					//throw new org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound("Filtro '"+tipoFiltro.getValue()+"' non supportato per il message-type '"+context.getMessaggio().getMessageType()+"'");
					return null; // semplicemente non deve matchare il filtro
				}
			}
			if(element!=null) {
				xPathEngine = new org.openspcoop2.message.xml.XPathExpressionEngine(context.getMessaggio().getFactory());
				return AbstractXPathExpressionEngine.extractAndConvertResultAsString(element, xPathEngine, nome,  log);
			}
			else if(elementJson!=null) {
				return JsonXmlPathExpressionEngine.extractAndConvertResultAsString(elementJson, nome, log);
			}
			else {
				return null; // semplicemente non deve matchare il filtro
			}
			
		case URLBASED:
			
			String urlInvocazionePD = context.getConnettore().getUrlProtocolContext().getUrlInvocazione_formBased();
			try{
				return RegularExpressionEngine.getStringMatchPattern(urlInvocazionePD, nome);
			}catch(RegExpNotFoundException notFound){
				return null;
			}

		case FORM_BASED:
			
			Map<String, String> pForm = context.getConnettore().getUrlProtocolContext().getParametersFormBased();
			return TransportUtils.get(pForm, nome);
			
		case HEADER_BASED:
			
			Map<String, String> pTrasporto = context.getConnettore().getUrlProtocolContext().getParametersTrasporto();
			return TransportUtils.get(pTrasporto, nome);
						
		case SOAPACTION_BASED:
			
			String soapAction = context.getConnettore().getSoapAction();
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
			
			if(context.getPddContext()!=null && context.getPddContext().containsKey(Costanti.CLIENT_IP_REMOTE_ADDRESS)) {
				return (String) context.getPddContext().getObject(Costanti.CLIENT_IP_REMOTE_ADDRESS);
			}
			return null;
			
		case INDIRIZZO_IP_FORWARDED:
			
			if(context.getPddContext()!=null && context.getPddContext().containsKey(Costanti.CLIENT_IP_TRANSPORT_ADDRESS)) {
				return (String) context.getPddContext().getObject(Costanti.CLIENT_IP_TRANSPORT_ADDRESS);
			}
			return null;
			
		case PLUGIN_BASED:
			
			String className = ClassNameProperties.getInstance().getRateLimiting(nome);
			if(className==null){
				throw new Exception("Instance plugin ["+nome+"] error: il tipo non è stato registrato nel registro delle classi ('org.openspcoop2.pdd.controlloTraffico.rateLimiting."+
						nome+"' non esiste)");
			}
			try{
				Class<?> classPlugin = Class.forName(className);
				Object o = Utilities.newInstance(classPlugin);
				if(o instanceof IRateLimiting){
					
					Dati datiRichiesta = new Dati();
					datiRichiesta.setConnettore(context.getConnettore());
					datiRichiesta.setDatiTransazione(datiTransazione);
					datiRichiesta.setMessaggio(context.getMessaggio());
					datiRichiesta.setPddContext(context.getPddContext());
					
					if(forFilter){
						return ((IRateLimiting)o).estraiValoreFiltro(log,datiRichiesta);
					}
					else{
						return ((IRateLimiting)o).estraiValoreCollezionamentoDati(log,datiRichiesta);
					}
				}else{
					throw new Exception("ClassType ["+o.getClass().getName()+"] unknown");
				}
			}catch(Exception e){
				throw new Exception("Instance plugin ["+nome+"] [class:"+className+"] error: "+e.getMessage(),e);
			}

		}
		
		throw new Exception("TipoFiltro ["+tipo+"] non gestito");
	}
	
}

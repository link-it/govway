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
package org.openspcoop2.protocol.sdi.utils;

import java.util.Hashtable;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.PreInRequestContext;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.connector.messages.ConnectorInMessage;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.sdi.constants.SDICostanti;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioRiceviFile;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioRiceviNotifica;

/**
 * PreInRequestHandler
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PreInRequestHandler implements org.openspcoop2.pdd.core.handlers.PreInRequestHandler {

	@Override
	public void invoke(PreInRequestContext context) throws HandlerException {
		
		try{
		
			if(!TipoPdD.DELEGATA.equals(context.getTipoPorta())){
				return;
			}
						
			if(context.getTransportContext()==null){
				return;
			}
			
			Hashtable<String, Object> transportContext = context.getTransportContext();
			Object inMessageObject = transportContext.get(PreInRequestContext.SERVLET_REQUEST);
			if(inMessageObject==null || (!(inMessageObject instanceof ConnectorInMessage))){
				return;
			}
			
			ConnectorInMessage inMessage = (ConnectorInMessage) inMessageObject;
			if(inMessage.getURLProtocolContext()==null){
				return;
			}
			
			if(!URLProtocolContext.PDtoSOAP_FUNCTION.equals(inMessage.getURLProtocolContext().getFunction()) &&
					!URLProtocolContext.PDtoSOAP_FUNCTION_GOVWAY.equals(inMessage.getURLProtocolContext().getFunction())){
				return;
			}
			if(!SDICostanti.SDI_PROTOCOL_NAME.equals(inMessage.getURLProtocolContext().getProtocolName())){
				return;
			}
			
			// Appendo l'azione dopo la versione
			String sdiFatturazioneAttiva = "/"+SDICostantiServizioRiceviFile.SDI_SERVIZIO_RICEVI_FILE+"/";
			String sdiFatturazioneAttivaConTipo = "/sdi_"+SDICostantiServizioRiceviFile.SDI_SERVIZIO_RICEVI_FILE+"/";
			boolean attiva = false;
			String sdiFatturazionePassiva = "/"+SDICostantiServizioRiceviNotifica.SDI_SERVIZIO_RICEVI_NOTIFICA+"/";
			String sdiFatturazionePassivaConTipo = "/sdi_"+SDICostantiServizioRiceviNotifica.SDI_SERVIZIO_RICEVI_NOTIFICA+"/";
			@SuppressWarnings("unused")
			boolean passiva = false;
			if( inMessage.getURLProtocolContext().getFunctionParameters().contains(sdiFatturazioneAttiva) 
					||
				inMessage.getURLProtocolContext().getFunctionParameters().contains(sdiFatturazioneAttivaConTipo)){
				String requestUri = inMessage.getURLProtocolContext().getRequestURI();
				String params =  inMessage.getURLProtocolContext().getFunctionParameters();
				if(requestUri.endsWith("/")==false) {
					requestUri = requestUri + "/";
				}
				if(params.endsWith("/")==false) {
					params = params + "/";
				}
				requestUri = requestUri + SDICostantiServizioRiceviFile.SDI_SERVIZIO_RICEVI_FILE_AZIONE_RICEVI_FILE;
				params = params + SDICostantiServizioRiceviFile.SDI_SERVIZIO_RICEVI_FILE_AZIONE_RICEVI_FILE;
				inMessage.getURLProtocolContext().setRequestURI(requestUri);
				inMessage.getURLProtocolContext().setFunctionParameters(params);
				attiva = true;
			}
			else if( inMessage.getURLProtocolContext().getFunctionParameters().contains(sdiFatturazionePassiva)
					||
					inMessage.getURLProtocolContext().getFunctionParameters().contains(sdiFatturazionePassivaConTipo)){
				String requestUri = inMessage.getURLProtocolContext().getRequestURI();
				String params =  inMessage.getURLProtocolContext().getFunctionParameters();
				if(requestUri.endsWith("/")==false) {
					requestUri = requestUri + "/";
				}
				if(params.endsWith("/")==false) {
					params = params + "/";
				}
				requestUri = requestUri + SDICostantiServizioRiceviNotifica.SDI_SERVIZIO_NOTIFICA_ESITO_AZIONE_NOTIFICA_ESITO;
				params = params + SDICostantiServizioRiceviNotifica.SDI_SERVIZIO_NOTIFICA_ESITO_AZIONE_NOTIFICA_ESITO;
				inMessage.getURLProtocolContext().setRequestURI(requestUri);
				inMessage.getURLProtocolContext().setFunctionParameters(params);
				passiva = true;
			}
			else {
				return;
			}
			
			if(attiva) {
				String valoreUrl = null;
				if(inMessage.getURLProtocolContext().getParametersFormBased()!=null &&
						inMessage.getURLProtocolContext().getParametersFormBased().size()>0){
					valoreUrl = inMessage.getURLProtocolContext().getParameterFormBased(SDICostantiServizioRiceviFile.RICEVI_FILE_INTEGRAZIONE_URLBASED_TIPO_FILE);
					if(valoreUrl==null){
						valoreUrl = inMessage.getURLProtocolContext().getParameterFormBased(SDICostantiServizioRiceviFile.RICEVI_FILE_INTEGRAZIONE_URLBASED_TIPO_FILE.toLowerCase());
					}
					if(valoreUrl==null){
						valoreUrl = inMessage.getURLProtocolContext().getParameterFormBased(SDICostantiServizioRiceviFile.RICEVI_FILE_INTEGRAZIONE_URLBASED_TIPO_FILE.toUpperCase());
					}
				}
				
				String valoreHeader = null;
				if(inMessage.getURLProtocolContext().getParametersTrasporto()!=null &&
						inMessage.getURLProtocolContext().getParametersTrasporto().size()>0){
					valoreHeader = inMessage.getURLProtocolContext().getParameterTrasporto(SDICostantiServizioRiceviFile.RICEVI_FILE_INTEGRAZIONE_TRASPORTO_TIPO_FILE_1);
					if(valoreHeader==null){
						valoreHeader = inMessage.getURLProtocolContext().getParameterTrasporto(SDICostantiServizioRiceviFile.RICEVI_FILE_INTEGRAZIONE_TRASPORTO_TIPO_FILE_1.toLowerCase());
					}
					if(valoreHeader==null){
						valoreHeader = inMessage.getURLProtocolContext().getParameterTrasporto(SDICostantiServizioRiceviFile.RICEVI_FILE_INTEGRAZIONE_TRASPORTO_TIPO_FILE_1.toUpperCase());
					}
					if(valoreHeader==null){
						valoreHeader = inMessage.getURLProtocolContext().getParameterTrasporto(SDICostantiServizioRiceviFile.RICEVI_FILE_INTEGRAZIONE_TRASPORTO_TIPO_FILE_2);
					}
					if(valoreHeader==null){
						valoreHeader = inMessage.getURLProtocolContext().getParameterTrasporto(SDICostantiServizioRiceviFile.RICEVI_FILE_INTEGRAZIONE_TRASPORTO_TIPO_FILE_2.toLowerCase());
					}
					if(valoreHeader==null){
						valoreHeader = inMessage.getURLProtocolContext().getParameterTrasporto(SDICostantiServizioRiceviFile.RICEVI_FILE_INTEGRAZIONE_TRASPORTO_TIPO_FILE_2.toUpperCase());
					}
				}
				
				if(valoreUrl==null && valoreHeader==null){
					//return;
					// Bug Fix: OP-752
					// forzo xml che gestiro' comunque come attachment
					valoreHeader = SDICostanti.SDI_TIPO_FATTURA_XML;
				}
				String valore = null;
				if(valoreUrl!=null){
					valore = valoreUrl;
				}
				else{
					valore = valoreHeader;
				}
				
				boolean imbustamentoSOAP = false;
				if(SDICostanti.SDI_TIPO_FATTURA_ZIP.equalsIgnoreCase(valore)){
					imbustamentoSOAP = true;
				}
				else if(SDICostanti.SDI_TIPO_FATTURA_P7M.equalsIgnoreCase(valore)){
					imbustamentoSOAP = true;
				}
				
				// Bug Fix: OP-752
				// USO Comunque il tunnel SOAP altrimenti l'xml viene modificato e la firma non e' piu' valida
				// Uso esattamente il codice sopra con il contentType per zip e p7m:
				imbustamentoSOAP = true;
				
				if(imbustamentoSOAP){
					OpenSPCoop2Properties openSPCoopProperties = OpenSPCoop2Properties.getInstance();
					context.getTransportContext().put(openSPCoopProperties.getTunnelSOAPKeyWord_urlBased(), "true");
					context.getTransportContext().put(openSPCoopProperties.getTunnelSOAPKeyWordMimeType_urlBased(),"application/octet-stream");
				}
			}
			else if(passiva) {
				// Bug Fix: OP-752
				// USO Comunque il tunnel SOAP altrimenti l'xml viene modificato e la firma non e' piu' valida
				// Le notifiche inviate non sono firmate, ma se lo fossero questo fix risolverebbe il problema.
				// Non capisco perche' se cmq attivo questo canale poi l'xpath per mtom non funziona
				OpenSPCoop2Properties openSPCoopProperties = OpenSPCoop2Properties.getInstance();
				context.getTransportContext().put(openSPCoopProperties.getTunnelSOAPKeyWord_urlBased(), "true");
				context.getTransportContext().put(openSPCoopProperties.getTunnelSOAPKeyWordMimeType_urlBased(),"application/octet-stream");
			}
			
		}catch(Exception e){
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("PreInRequestHandler error: "+e.getMessage(),e);
		}
		
		
		
	}

}

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
package org.openspcoop2.pdd.core.behaviour.built_in;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2RestJsonMessage;
import org.openspcoop2.message.OpenSPCoop2RestXmlMessage;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.xml.XPathExpressionEngine;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.GestoreMessaggiException;
import org.openspcoop2.pdd.core.behaviour.Behaviour;
import org.openspcoop2.pdd.core.behaviour.BehaviourEmitDiagnosticException;
import org.openspcoop2.pdd.core.behaviour.BehaviourException;
import org.openspcoop2.pdd.core.behaviour.BehaviourForwardTo;
import org.openspcoop2.pdd.core.behaviour.BehaviourForwardToFilter;
import org.openspcoop2.pdd.core.behaviour.IBehaviour;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.utils.xml.AbstractXPathExpressionEngine;
import org.openspcoop2.utils.xml2json.JsonXmlPathExpressionEngine;
import org.w3c.dom.Element;

/**
 * ServizioApplicativoBustaBehaviour
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServizioApplicativoContentBasedBehaviour extends AbstractCore implements IBehaviour {

	private static final String PATTERN_GENERICO = "pattern";
	private static final String PATTERN_MODE_NOT_FOUND = "notFound";
	private static final String PATTERN_MODE_PREFIX_SA_NAME = "prefix";
	private static final String PATTERN_MODE_SUFFIX_SA_NAME = "suffix";
	private static final String PATTERN_SA_DEFAULT = "saDefault";
	
	@Override
	public Behaviour behaviour(GestoreMessaggi gestoreMessaggioRichiesta, Busta busta,
			PortaApplicativa pa, RequestInfo requestInfo) throws BehaviourException,BehaviourEmitDiagnosticException {
		
		Behaviour behaviour = null;
		BehaviourForwardTo forwardTo = null;
		try{
			behaviour = new Behaviour();
			forwardTo = new BehaviourForwardTo();
			behaviour.getForwardTo().add(forwardTo);
			
			if(busta==null) {
				throw new BehaviourException("Param busta is null");
			}
			
			BehaviourForwardToFilter filter = new BehaviourForwardToFilter();
			IDServizioApplicativo id = new IDServizioApplicativo();
			id.setIdSoggettoProprietario(new IDSoggetto(busta.getTipoDestinatario(),busta.getDestinatario()));
			
			if(pa==null && requestInfo!=null && requestInfo.getProtocolContext()!=null) {
				String nomePA = requestInfo.getProtocolContext().getInterfaceName();
				IDPortaApplicativa idPA = new IDPortaApplicativa();
				idPA.setNome(nomePA);
				ConfigurazionePdDManager configPdDManager = ConfigurazionePdDManager.getInstance(gestoreMessaggioRichiesta.getOpenspcoopstate()!=null ? gestoreMessaggioRichiesta.getOpenspcoopstate().getStatoRichiesta() : null);
				pa = configPdDManager.getPortaApplicativa_SafeMethod(idPA, requestInfo);
			}
			
			String azione = null;
			if(busta!=null) {
				azione = busta.getAzione();
			}
			
			String pattern = null;
			String patternDefault = null;
			NotFoundMode notFoundMode = NotFoundMode.error;
			String prefix = null;
			String suffix = null;
			String saDefault = null;
			if(pa!=null && pa.sizeProprietaList()>0) {
				for (Proprieta proprietaPA : pa.getProprietaList()) {
					String nomeProprietaPA = proprietaPA.getNome().trim().toLowerCase();
					if(PATTERN_GENERICO.toLowerCase().equals(nomeProprietaPA)) {
						patternDefault = proprietaPA.getValore();
					}
					else if(azione!=null && azione.toLowerCase().equals(nomeProprietaPA)) {
						pattern = proprietaPA.getValore();
					}
					else if(PATTERN_MODE_NOT_FOUND.toLowerCase().equals(nomeProprietaPA)) {
						notFoundMode = NotFoundMode.valueOf(proprietaPA.getValore().toLowerCase());
					}
					else if(PATTERN_MODE_PREFIX_SA_NAME.toLowerCase().equals(nomeProprietaPA)) {
						prefix = proprietaPA.getValore();
					}
					else if(PATTERN_MODE_SUFFIX_SA_NAME.toLowerCase().equals(nomeProprietaPA)) {
						suffix = proprietaPA.getValore();
					}
					else if(PATTERN_SA_DEFAULT.toLowerCase().equals(nomeProprietaPA)) {
						saDefault = proprietaPA.getValore();
					}
				}
			}
			
			boolean found = false;
			if(pattern!=null || patternDefault!=null) {
				
				Element element = null;
				String elementJson = null;
				OpenSPCoop2MessageFactory messageFactory = null;
				try{
					OpenSPCoop2Message message = gestoreMessaggioRichiesta.getMessage();
					messageFactory = message.getFactory();
					if(ServiceBinding.SOAP.equals(message.getServiceBinding())){
						OpenSPCoop2SoapMessage soapMessage = message.castAsSoap();
						element = soapMessage.getSOAPPart().getEnvelope();
					}
					else{
						if(MessageType.XML.equals(message.getMessageType())){
							OpenSPCoop2RestXmlMessage xml = message.castAsRestXml();
							element = xml.getContent();	
						}
						else if(MessageType.JSON.equals(message.getMessageType())){
							OpenSPCoop2RestJsonMessage json = message.castAsRestJson();
							elementJson = json.getContent();
						}
					}
				}catch(Exception e){
					throw new GestoreMessaggiException(e.getMessage(),e);
				}
				
				String patternUsed = pattern;
				if(patternUsed==null) {
					patternUsed = patternDefault;
				}
				
				String saNome = null;
				try {
					if(element==null && elementJson==null){
						throw new Exception("Contenuto non disponibile su cui effettuare un match");
					}
					if(element!=null) {
						XPathExpressionEngine xPathEngine = new XPathExpressionEngine(messageFactory);
						saNome = AbstractXPathExpressionEngine.extractAndConvertResultAsString(element, xPathEngine, patternUsed, OpenSPCoop2Logger.getLoggerOpenSPCoopCore());
					}
					else {
						saNome = JsonXmlPathExpressionEngine.extractAndConvertResultAsString(elementJson, patternUsed, OpenSPCoop2Logger.getLoggerOpenSPCoopCore());
					}
					if(saNome==null) {
						throw new Exception("Nessun valore trovato");
					}
				}catch(Exception e) {
					String msgErrore = "Identificazione del Servizio Applicativo tramite pattern ["+patternUsed+"] fallita: "+e.getMessage();
					switch (notFoundMode) {
					case error:
						OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error(msgErrore,e);
						throw new Exception(msgErrore);
					case warning:
						OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error(msgErrore,e);
						break;
					case accept:
						OpenSPCoop2Logger.getLoggerOpenSPCoopCore().debug(msgErrore,e);
						break;
					}
					
				}
								
				if(saNome!=null) {
				
					StringBuilder bf = new StringBuilder();
					if(prefix!=null) {
						bf.append(prefix);
					}
					bf.append(saNome);
					if(suffix!=null) {
						bf.append(suffix);
					}
					id.setNome(bf.toString());
					filter.getAccessListServiziApplicativi().add(id);
					forwardTo.setFilter(filter);
					
					found = true;
				}
					
			}
			
			if(!found && saDefault!=null) {
				
				id.setNome(saDefault);
				filter.getAccessListServiziApplicativi().add(id);
				forwardTo.setFilter(filter);
				
			}
			
		}catch(Exception e){
			throw new BehaviourException(e.getMessage(),e);
		}
		
		
		return behaviour;
	}

}

enum NotFoundMode{
	
	error, warning, accept
	
}

/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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



package org.openspcoop2.protocol.as4.validator;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.soap.SOAPElement;

import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.as4.AS4RawContent;
import org.openspcoop2.protocol.as4.constants.AS4Costanti;
import org.openspcoop2.protocol.basic.validator.ValidazioneSintattica;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaAccordi;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaServizi;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaSoggetti;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazioneErrori;
import org.openspcoop2.protocol.sdk.validator.ValidazioneSintatticaResult;

/**
 * Classe che implementa, in base al protocollo AS4, l'interfaccia {@link org.openspcoop2.protocol.sdk.validator.IValidazioneSintattica}
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AS4ValidazioneSintattica extends ValidazioneSintattica<SOAPElement>{

	public AS4ValidazioneSintattica(IProtocolFactory<SOAPElement> factory, IState state) throws ProtocolException {
		super(factory,state);
	}
	
	/** ValidazioneUtils */
	protected AS4ValidazioneUtils validazioneUtils;
	
	/** SOAPElement senza il contenuto (SoloProtocolloAS4) */
	private AS4RawContent headerElement;
	
	/** Errori di validazione riscontrati sulla busta */
	protected java.util.List<Eccezione> erroriValidazione = new ArrayList<Eccezione>();
	/** Errori di processamento riscontrati sulla busta */
	protected java.util.List<Eccezione> erroriProcessamento = new ArrayList<Eccezione>();
	/** Eventuale codice di errore avvenuto durante il processo di validazione  */
	private CodiceErroreCooperazione codiceErrore;
	/** Eventuale messaggio di errore avvenuto durante il processo di validazione */
	private String msgErrore;
	
	@Override
	public AS4RawContent getBustaRawContent_senzaControlli(
			OpenSPCoop2Message msg) throws ProtocolException {
		try{
			if(this.headerElement!=null) {
				return this.headerElement;
			}
			
			Object o = msg.getContextProperty(AS4Costanti.AS4_CONTEXT_USER_MESSAGE);
			if(o==null) {
				return null;
			}
			if(!(o instanceof UserMessage)) {
				throw new Exception("UserMessage in context message with wrong type (expected:"+UserMessage.class.getName()+" founded:"+o.getClass().getName()+")");
			}
			org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.utils.serializer.JaxbSerializer as4Serializer = 
					new org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.utils.serializer.JaxbSerializer();
			byte[] as4Header = as4Serializer.toByteArray((UserMessage)o);
			SOAPElement soapElementAs4Header = msg.castAsSoap().createSOAPElement(as4Header);
			this.headerElement = new AS4RawContent(soapElementAs4Header);
			return this.headerElement;
		}catch(Exception e){
			this.log.debug("getHeaderProtocollo_senzaControlli error: "+e.getMessage(),e);
			return null;
		}
	}
		
	@Override
	public ValidazioneSintatticaResult<SOAPElement> validaRichiesta(OpenSPCoop2Message msg, Busta datiBustaLettiURLMappingProperties, ProprietaValidazioneErrori proprietaValidazioneErrori) throws ProtocolException{
		
		ValidazioneSintatticaResult<SOAPElement> basicResult = super.validaRichiesta(msg, datiBustaLettiURLMappingProperties, proprietaValidazioneErrori);
		
		this.headerElement = this.getBustaRawContent_senzaControlli(msg);
		
		boolean isValido = this.valida(msg,basicResult.getBusta(), true,null);
		
		ErroreCooperazione errore = null;
		if(this.msgErrore!=null && this.codiceErrore!=null){
			errore = new ErroreCooperazione(this.msgErrore, this.codiceErrore);
		}
		java.util.List<Eccezione> erroriValidazione = null;
		if(this.erroriValidazione.size()>0){
			erroriValidazione = this.erroriValidazione;
		}
		java.util.List<Eccezione> erroriProcessamento = null;
		if(this.erroriProcessamento.size()>0){
			erroriValidazione = this.erroriProcessamento;
		}
		ValidazioneSintatticaResult<SOAPElement> result = new ValidazioneSintatticaResult<SOAPElement>(erroriValidazione, erroriProcessamento, null, 
				basicResult.getBusta(), errore, null, this.headerElement, isValido);
		return result;
		
	}
	
	private boolean valida(OpenSPCoop2Message msg, Busta busta, boolean isRichiesta, Busta bustaRichiesta){
		try{
			this._valida(msg, busta, isRichiesta);
			return true;
		}catch(Exception e) {
			this.msgErrore =  "[ErroreInterno]: "+e.getMessage();
			this.log.error(this.msgErrore,e);
			this.codiceErrore = CodiceErroreCooperazione.FORMATO_NON_CORRETTO;
			return false;
		}
	}
	private void _valida(OpenSPCoop2Message msg, Busta busta, boolean isRichiesta) throws Exception{
		
		if(msg==null){
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_NON_CORRETTO, 
					"Messaggio non presente"));
			return;
		}
		
		Object o = msg.getContextProperty(AS4Costanti.AS4_CONTEXT_USER_MESSAGE);
		if(o==null) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_PRESENTE, 
					"UserMessage non presente"));
			return;
		}
		if(!(o instanceof UserMessage)) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO, 
					"UserMessage in context message with wrong type (expected:"+UserMessage.class.getName()+" founded:"+o.getClass().getName()+")"));
			return;
		}
		UserMessage userMessage = (UserMessage) o;
		
		Object oContent = msg.getContextProperty(AS4Costanti.AS4_CONTEXT_CONTENT);
		if(oContent==null) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_PRESENTE, 
					"Contenuti non presenti"));
			return;
		}
		if(!(oContent instanceof HashMap)) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO, 
					"Contenuti in context message with wrong type (expected:"+HashMap.class.getName()+" founded:"+oContent.getClass().getName()+")"));
			return;
		}
		@SuppressWarnings("unchecked")
		HashMap<String, byte[]> content = (HashMap<String, byte[]>) oContent;
		
		IRegistryReader registryReader = this.getProtocolFactory().getCachedRegistryReader(this.state);
		
		
		// mittente/destinatario
		if(userMessage.getPartyInfo()==null) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.INTESTAZIONE_NON_CORRETTA, 
					"PartyInfo non presente"));
			return;
		}
		
		
		// mittente
		if(userMessage.getPartyInfo().getFrom()==null) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.MITTENTE_NON_PRESENTE, 
					"PartyInfo/From"));
			return;
		}
		if(userMessage.getPartyInfo().getFrom().getPartyIdList()==null || userMessage.getPartyInfo().getFrom().getPartyIdList().size()<=0) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.MITTENTE_NON_PRESENTE, 
					"PartyInfo/From/PartyId"));
			return;
		}
		if(userMessage.getPartyInfo().getFrom().getPartyIdList().size()>1) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.MITTENTE_PRESENTE_PIU_VOLTE, 
					"PartyInfo/From/PartyId"));
			return;
		}
		PartyId from = userMessage.getPartyInfo().getFrom().getPartyId(0);
		if(from==null) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO, 
					"PartyInfo/From/PartyId"));
			return;
		}
		if(from.getBase()==null) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO, 
					"PartyInfo/From/PartyId/Base"));
			return;
		}
		if(from.getType()==null) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.TIPO_MITTENTE_NON_VALORIZZATO, 
					"PartyInfo/From/PartyId/Type"));
			return;
		}
		FiltroRicercaSoggetti filtroSoggetti = new FiltroRicercaSoggetti();
		filtroSoggetti.setProtocolProperties(new ProtocolProperties());
		filtroSoggetti.getProtocolProperties().addProperty(AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ID_BASE,from.getBase());
		List<IDSoggetto> list = null;
		try {
			list = registryReader.findIdSoggetti(filtroSoggetti);
			if(list==null || list.size()<=0) {
				throw new RegistryNotFound();
			}
		}catch(RegistryNotFound notFound) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.MITTENTE_NON_VALIDO, 
					"PartyInfo/From/PartyId/Base"));
			return;
		}
		if(list.size()>1) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.MITTENTE_PRESENTE_PIU_VOLTE, 
					"PartyInfo/From/PartyId/Base"));
			return;
		}
		IDSoggetto idMittente = list.get(0);
		busta.setTipoMittente(idMittente.getTipo());
		busta.setMittente(idMittente.getNome());
		busta.addProperty(AS4Costanti.AS4_BUSTA_MITTENTE_PARTY_ID_BASE, from.getBase());
		busta.addProperty(AS4Costanti.AS4_BUSTA_MITTENTE_PARTY_ID_TYPE, from.getType());
		
		
		// destinatario
		if(userMessage.getPartyInfo().getTo()==null) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.DESTINATARIO_NON_PRESENTE, 
					"PartyInfo/To"));
			return;
		}
		if(userMessage.getPartyInfo().getTo().getPartyIdList()==null || userMessage.getPartyInfo().getTo().getPartyIdList().size()<=0) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.DESTINATARIO_NON_PRESENTE, 
					"PartyInfo/To/PartyId"));
			return;
		}
		if(userMessage.getPartyInfo().getTo().getPartyIdList().size()>1) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.DESTINATARIO_PRESENTE_PIU_VOLTE, 
					"PartyInfo/To/PartyId"));
			return;
		}
		PartyId to = userMessage.getPartyInfo().getTo().getPartyId(0);
		if(to==null) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.DESTINATARIO_NON_VALORIZZATO, 
					"PartyInfo/To/PartyId"));
			return;
		}
		if(to.getBase()==null) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.DESTINATARIO_NON_VALORIZZATO, 
					"PartyInfo/To/PartyId/Base"));
			return;
		}
		if(to.getType()==null) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.TIPO_DESTINATARIO_NON_VALORIZZATO, 
					"PartyInfo/To/PartyId/Type"));
			return;
		}
		filtroSoggetti = new FiltroRicercaSoggetti();
		filtroSoggetti.setProtocolProperties(new ProtocolProperties());
		filtroSoggetti.getProtocolProperties().addProperty(AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ID_BASE,to.getBase());
		list = null;
		try {
			list = registryReader.findIdSoggetti(filtroSoggetti);
			if(list==null || list.size()<=0) {
				throw new RegistryNotFound();
			}
		}catch(RegistryNotFound notFound) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.DESTINATARIO_NON_VALIDO, 
					"PartyInfo/To/PartyId/Base"));
			return;
		}
		if(list.size()>1) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.DESTINATARIO_PRESENTE_PIU_VOLTE, 
					"PartyInfo/To/PartyId/Base"));
			return;
		}
		IDSoggetto idDestinatario = list.get(0);
		busta.setTipoDestinatario(idDestinatario.getTipo());
		busta.setDestinatario(idDestinatario.getNome());
		busta.addProperty(AS4Costanti.AS4_BUSTA_DESTINATARIO_PARTY_ID_BASE, to.getBase());
		busta.addProperty(AS4Costanti.AS4_BUSTA_DESTINATARIO_PARTY_ID_TYPE, to.getType());
		
		
		// servizio/azione/conversationId
		
		if(userMessage.getCollaborationInfo()==null) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.INTESTAZIONE_NON_CORRETTA, 
					"CollaborationInfo non presente"));
			return;
		}
		
		// servizio
		if(userMessage.getCollaborationInfo().getService()==null) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SERVIZIO_NON_PRESENTE, 
					"CollaborationInfo/Service"));
			return;
		}
		if(userMessage.getCollaborationInfo().getService().getBase()==null) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SERVIZIO_NON_VALORIZZATO, 
					"CollaborationInfo/Service/Base"));
			return;
		}
		if(userMessage.getCollaborationInfo().getService().getType()==null) {
			// Il tipo non e' obbligatorio
//			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.TIPO_SERVIZIO_NON_VALORIZZATO, 
//					"CollaborationInfo/Service/Type"));
//			return;
		}
		FiltroRicercaAccordi filtroAccordi = new FiltroRicercaAccordi();
		filtroAccordi.setSoggetto(idDestinatario);
		filtroAccordi.setProtocolProperties(new ProtocolProperties());
		filtroAccordi.getProtocolProperties().addProperty(AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_SERVICE_BASE,
				userMessage.getCollaborationInfo().getService().getBase());
		filtroAccordi.getProtocolProperties().addProperty(AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_SERVICE_TYPE,
				userMessage.getCollaborationInfo().getService().getType());
		List<IDAccordo> listAccordi = null;
		try {
			listAccordi = registryReader.findIdAccordiServizioParteComune(filtroAccordi);
			if(listAccordi==null || listAccordi.size()<=0) {
				throw new RegistryNotFound();
			}
		}catch(RegistryNotFound notFound) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SERVIZIO_NON_VALIDO, 
					"CollaborationInfo/Service"));
			return;
		}
		if(listAccordi.size()>1) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SERVIZIO_PRESENTE_PIU_VOLTE, 
					"CollaborationInfo/Service"));
			return;
		}
		IDAccordo idAccordo = listAccordi.get(0);
		AccordoServizioParteComune aspc = registryReader.getAccordoServizioParteComune(idAccordo,false);
		FiltroRicercaServizi filtroServizi = new FiltroRicercaServizi();
		filtroServizi.setIdAccordoServizioParteComune(idAccordo);
		filtroServizi.setSoggettoErogatore(idDestinatario);
		List<IDServizio> listServizi = null;
		try {
			listServizi = registryReader.findIdAccordiServizioParteSpecifica(filtroServizi);
			if(listServizi==null || listServizi.size()<=0) {
				throw new RegistryNotFound();
			}
		}catch(RegistryNotFound notFound) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SERVIZIO_NON_VALIDO, 
					"CollaborationInfo/ServiceImpl"));
			return;
		}
		if(listServizi.size()>1) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.SERVIZIO_PRESENTE_PIU_VOLTE, 
					"CollaborationInfo/ServiceImpl"));
			return;
		}
		
		IDServizio idServizio = listServizi.get(0);
		busta.setTipoServizio(idServizio.getTipo());
		busta.setServizio(idServizio.getNome());
		busta.addProperty(AS4Costanti.AS4_BUSTA_SERVIZIO_COLLABORATION_INFO_SERVICE_BASE, userMessage.getCollaborationInfo().getService().getBase());
		busta.addProperty(AS4Costanti.AS4_BUSTA_SERVIZIO_COLLABORATION_INFO_SERVICE_TYPE, userMessage.getCollaborationInfo().getService().getType());
		
		
		// azione
		String as4Action = userMessage.getCollaborationInfo().getAction();
		if(as4Action==null) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.AZIONE_NON_PRESENTE, 
					"CollaborationInfo/Action"));
			return;
		}
		if(ServiceBinding.REST.equals(aspc.getServiceBinding())) {
			for (Resource resource : aspc.getResourceList()) {
				if(resource.sizeProtocolPropertyList()>0) {
					for (ProtocolProperty pp : resource.getProtocolPropertyList()) {
						if(AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_ACTION.equals(pp.getName()) &&
								as4Action.equals(pp.getValue())) {
							busta.setAzione(resource.getNome());
							break;
						}
					}
				}
			}
		}
		else {
			AccordoServizioParteSpecifica asps = registryReader.getAccordoServizioParteSpecifica(idServizio, false);
			if(asps.getPortType()!=null) {
				for (PortType pt : aspc.getPortTypeList()) {
					for (Operation azione : pt.getAzioneList()) {
						if(azione.sizeProtocolPropertyList()>0) {
							for (ProtocolProperty pp : azione.getProtocolPropertyList()) {
								if(AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_ACTION.equals(pp.getName()) &&
										as4Action.equals(pp.getValue())) {
									busta.setAzione(azione.getNome());
									break;
								}
							}
						}
					}
				}
			}
			else {
				for (Azione azione : aspc.getAzioneList()) {
					if(azione.sizeProtocolPropertyList()>0) {
						for (ProtocolProperty pp : azione.getProtocolPropertyList()) {
							if(AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_ACTION.equals(pp.getName()) &&
									as4Action.equals(pp.getValue())) {
								busta.setAzione(azione.getNome());
								break;
							}
						}
					}
				}
			}
		}
		if(busta.getAzione()==null) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.AZIONE_NON_VALIDA, 
					"CollaborationInfo/Action"));
			return;
		}
		busta.addProperty(AS4Costanti.AS4_BUSTA_SERVIZIO_COLLABORATION_INFO_ACTION, as4Action);
		
		
		// conversationId
		String as4ConversationId = userMessage.getCollaborationInfo().getConversationId();
		if(as4ConversationId==null) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.COLLABORAZIONE_NON_PRESENTE, 
					"CollaborationInfo/ConversationId"));
			return;
		}
		busta.setCollaborazione(as4ConversationId);
		busta.addProperty(AS4Costanti.AS4_BUSTA_SERVIZIO_COLLABORATION_INFO_CONVERSATION_ID, as4ConversationId);
		
		
		// messageId/RefToMessageId
		
		if(userMessage.getMessageInfo()==null) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.INTESTAZIONE_NON_CORRETTA, 
					"MessageInfo non presente"));
			return;
		}
		
		// messageId (lo salvo sia come id che come property per avere la proprieta' sia in fase di spedizione che in ricezione)
		
		String as4MessageId = userMessage.getMessageInfo().getMessageId();
		if(as4MessageId==null) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_PRESENTE, 
					"MessageInfo/MessageId"));
			return;
		}
		busta.setID(as4MessageId);
		busta.addProperty(AS4Costanti.AS4_BUSTA_SERVIZIO_MESSAGE_INFO_ID, as4MessageId);
		
		// RefToMessageId
		
		String as4RefToMessageId = userMessage.getMessageInfo().getRefToMessageId();
		if(as4RefToMessageId!=null) {
			busta.addProperty(AS4Costanti.AS4_BUSTA_SERVIZIO_MESSAGE_INFO_REF_TO_MESSAGE_ID, as4RefToMessageId);
		}
		
		
		// Message Property
		if(userMessage.getMessageProperties()!=null && userMessage.getMessageProperties().sizePropertyList()>0) {
			for (Property p : userMessage.getMessageProperties().getPropertyList()) {
				busta.addProperty(AS4Costanti.AS4_BUSTA_SERVIZIO_COLLABORATION_MESSAGE_PROPERTY_PREFIX+p.getName(), p.getBase());
			}
		}
		
		
		// Payload
		if(userMessage.getPayloadInfo()==null || userMessage.getPayloadInfo().sizePartInfoList()<=0) {
			this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO, 
					"PayloadInfo non presente"));
			return;
		}
		for (PartInfo partInfo : userMessage.getPayloadInfo().getPartInfoList()) {
			
			if(partInfo.getHref()==null) {
				this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.ALLEGATI_RIFERIMENTO_NON_PRESENTE, 
						"PayloadInfo/Href"));
				return;
			}
			if(content.containsKey(partInfo.getHref())==false) {
				this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.ALLEGATO_NON_PRESENTE, 
						"PayloadInfo/Href"));
				return;
			}
			
			if(partInfo.getPartProperties()==null || partInfo.getPartProperties().sizePropertyList()<=0) {
				this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.ALLEGATI_RIFERIMENTO_NON_PRESENTE, 
						"PayloadInfo/ContentType"));
				return;
			}
			boolean found = false;
			for (Property property : partInfo.getPartProperties().getPropertyList()) {
				if(AS4Costanti.AS4_USER_MESSAGE_PAYLOAD_INFO_PROPERTIES_MIME_TYPE.equals(property.getName())) {
					found=true;
					break;
				}
			}
			if(!found) {
				this.erroriValidazione.add(this.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.ALLEGATI_RIFERIMENTO_NON_PRESENTE, 
						"PayloadInfo/MimeType"));
				return;
			}
		}
	}
	
	@Override
	public Busta getBusta_senzaControlli(OpenSPCoop2Message msg) throws ProtocolException{
		try{
			if(msg==null){
				return null;
			}
			
			Object o = msg.getContextProperty(AS4Costanti.AS4_CONTEXT_USER_MESSAGE);
			if(o==null) {
				return null;
			}
			if(!(o instanceof UserMessage)) {
				return null;
			}
			UserMessage userMessage = (UserMessage) o;
			
			IRegistryReader registryReader = this.getProtocolFactory().getCachedRegistryReader(this.state);
			
			Busta busta = new Busta(AS4Costanti.PROTOCOL_NAME);
			IDSoggetto idMittente = null;
			IDSoggetto idDestinatario = null;
			
			// mittente/destinatario
			if(userMessage.getPartyInfo()!=null) {
				if(userMessage.getPartyInfo().getFrom()!=null) {
					if(userMessage.getPartyInfo().getFrom().getPartyIdList()!=null && userMessage.getPartyInfo().getFrom().getPartyIdList().size()>0) {
						PartyId from = userMessage.getPartyInfo().getFrom().getPartyId(0);
						if(from.getBase()!=null && from.getType()!=null) {
							FiltroRicercaSoggetti filtroSoggetti = new FiltroRicercaSoggetti();
							filtroSoggetti.setProtocolProperties(new ProtocolProperties());
							filtroSoggetti.getProtocolProperties().addProperty(AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ID_BASE,from.getBase());
							List<IDSoggetto> list = null;
							try {
								list = registryReader.findIdSoggetti(filtroSoggetti);
								if(list==null || list.size()<=0) {
									throw new RegistryNotFound();
								}
							}catch(RegistryNotFound notFound) {
							}
							if(list!=null && list.size()>0) {
								idMittente = list.get(0);
								busta.setTipoMittente(idMittente.getTipo());
								busta.setMittente(idMittente.getNome());
								busta.addProperty(AS4Costanti.AS4_BUSTA_MITTENTE_PARTY_ID_BASE, from.getBase());
								busta.addProperty(AS4Costanti.AS4_BUSTA_MITTENTE_PARTY_ID_TYPE, from.getType());
							}
						}
					}
				}
				if(userMessage.getPartyInfo().getTo()!=null) {
					if(userMessage.getPartyInfo().getTo().getPartyIdList()!=null && userMessage.getPartyInfo().getTo().getPartyIdList().size()>0) {
						PartyId to = userMessage.getPartyInfo().getTo().getPartyId(0);
						if(to.getBase()!=null && to.getType()!=null) {
							FiltroRicercaSoggetti filtroSoggetti = new FiltroRicercaSoggetti();
							filtroSoggetti.setProtocolProperties(new ProtocolProperties());
							filtroSoggetti.getProtocolProperties().addProperty(AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_PARTY_ID_BASE,to.getBase());
							List<IDSoggetto> list = null;
							try {
								list = registryReader.findIdSoggetti(filtroSoggetti);
								if(list==null || list.size()<=0) {
									throw new RegistryNotFound();
								}
							}catch(RegistryNotFound notFound) {
							}
							if(list!=null && list.size()>0) {
								idDestinatario = list.get(0);
								busta.setTipoDestinatario(idDestinatario.getTipo());
								busta.setDestinatario(idDestinatario.getNome());
								busta.addProperty(AS4Costanti.AS4_BUSTA_DESTINATARIO_PARTY_ID_BASE, to.getBase());
								busta.addProperty(AS4Costanti.AS4_BUSTA_DESTINATARIO_PARTY_ID_TYPE, to.getType());
							}
						}
					}
				}
			}
			if(idMittente==null || idDestinatario==null) {
				return null;
			}
			
			// servizio/azione/conversationId
			IDServizio idServizio = null;
			IDAccordo idAccordo = null;
			AccordoServizioParteComune aspc = null;
			if(userMessage.getCollaborationInfo()!=null) {
				if(userMessage.getCollaborationInfo().getService()!=null) {
					if(userMessage.getCollaborationInfo().getService().getBase()!=null ) {
						FiltroRicercaAccordi filtroAccordi = new FiltroRicercaAccordi();
						filtroAccordi.setSoggetto(idDestinatario);
						filtroAccordi.setProtocolProperties(new ProtocolProperties());
						filtroAccordi.getProtocolProperties().addProperty(AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_SERVICE_BASE,
								userMessage.getCollaborationInfo().getService().getBase());
						filtroAccordi.getProtocolProperties().addProperty(AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_SERVICE_TYPE,
								userMessage.getCollaborationInfo().getService().getType());
						List<IDAccordo> listAccordi = null;
						try {
							listAccordi = registryReader.findIdAccordiServizioParteComune(filtroAccordi);
							if(listAccordi==null || listAccordi.size()<=0) {
								throw new RegistryNotFound();
							}
						}catch(RegistryNotFound notFound) {
						}
						if(listAccordi!=null && listAccordi.size()>0) {
							idAccordo = listAccordi.get(0);
							aspc = registryReader.getAccordoServizioParteComune(idAccordo,false);
							FiltroRicercaServizi filtroServizi = new FiltroRicercaServizi();
							filtroServizi.setIdAccordoServizioParteComune(idAccordo);
							filtroServizi.setSoggettoErogatore(idDestinatario);
							List<IDServizio> listServizi = null;
							try {
								listServizi = registryReader.findIdAccordiServizioParteSpecifica(filtroServizi);
								if(listServizi==null || listServizi.size()<=0) {
									throw new RegistryNotFound();
								}
							}catch(RegistryNotFound notFound) {
							}
							if(listServizi!=null && listServizi.size()>0) {
								idServizio = listServizi.get(0);
								busta.setTipoServizio(idServizio.getTipo());
								busta.setServizio(idServizio.getNome());
								busta.setVersioneServizio(idServizio.getVersione());
								busta.addProperty(AS4Costanti.AS4_BUSTA_SERVIZIO_COLLABORATION_INFO_SERVICE_BASE, userMessage.getCollaborationInfo().getService().getBase());
								busta.addProperty(AS4Costanti.AS4_BUSTA_SERVIZIO_COLLABORATION_INFO_SERVICE_TYPE, userMessage.getCollaborationInfo().getService().getType());
							}
						}
					}
				}
				if(userMessage.getCollaborationInfo().getAction()!=null) {
					String as4Action = userMessage.getCollaborationInfo().getAction();
					if(ServiceBinding.REST.equals(aspc.getServiceBinding())) {
						for (Resource resource : aspc.getResourceList()) {
							if(resource.sizeProtocolPropertyList()>0) {
								for (ProtocolProperty pp : resource.getProtocolPropertyList()) {
									if(AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_ACTION.equals(pp.getName()) &&
											as4Action.equals(pp.getValue())) {
										busta.setAzione(resource.getNome());
										break;
									}
								}
							}
						}
					}
					else {
						AccordoServizioParteSpecifica asps = registryReader.getAccordoServizioParteSpecifica(idServizio, false);
						if(asps.getPortType()!=null) {
							for (PortType pt : aspc.getPortTypeList()) {
								for (Operation azione : pt.getAzioneList()) {
									if(azione.sizeProtocolPropertyList()>0) {
										for (ProtocolProperty pp : azione.getProtocolPropertyList()) {
											if(AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_ACTION.equals(pp.getName()) &&
													as4Action.equals(pp.getValue())) {
												busta.setAzione(azione.getNome());
												break;
											}
										}
									}
								}
							}
						}
						else {
							for (Azione azione : aspc.getAzioneList()) {
								if(azione.sizeProtocolPropertyList()>0) {
									for (ProtocolProperty pp : azione.getProtocolPropertyList()) {
										if(AS4Costanti.AS4_PROTOCOL_PROPERTIES_USER_MESSAGE_COLLABORATION_INFO_ACTION.equals(pp.getName()) &&
												as4Action.equals(pp.getValue())) {
											busta.setAzione(azione.getNome());
											break;
										}
									}
								}
							}
						}
					}
					busta.addProperty(AS4Costanti.AS4_BUSTA_SERVIZIO_COLLABORATION_INFO_ACTION, as4Action);
				}
				if(userMessage.getCollaborationInfo().getConversationId()!=null) {
					String as4ConversationId = userMessage.getCollaborationInfo().getConversationId();
					busta.setCollaborazione(as4ConversationId);
					busta.addProperty(AS4Costanti.AS4_BUSTA_SERVIZIO_COLLABORATION_INFO_CONVERSATION_ID, as4ConversationId);
				}
			}
			
			
			// messageId/RefToMessageId
			if(userMessage.getMessageInfo()!=null) {
				if(userMessage.getMessageInfo().getMessageId()!=null) {
					String as4MessageId = userMessage.getMessageInfo().getMessageId();
					busta.setID(as4MessageId);
					busta.addProperty(AS4Costanti.AS4_BUSTA_SERVIZIO_MESSAGE_INFO_ID, as4MessageId);
				}
				if(userMessage.getMessageInfo().getRefToMessageId()!=null) {
					String as4RefToMessageId = userMessage.getMessageInfo().getRefToMessageId();
					busta.addProperty(AS4Costanti.AS4_BUSTA_SERVIZIO_MESSAGE_INFO_REF_TO_MESSAGE_ID, as4RefToMessageId);
				}
			}
			
			// Message Property
			if(userMessage.getMessageProperties()!=null && userMessage.getMessageProperties().sizePropertyList()>0) {
				for (Property p : userMessage.getMessageProperties().getPropertyList()) {
					busta.addProperty(AS4Costanti.AS4_BUSTA_SERVIZIO_COLLABORATION_MESSAGE_PROPERTY_PREFIX+p.getName(), p.getBase());
				}
			}

			return busta;
			
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
}

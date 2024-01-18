/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
package org.openspcoop2.pdd.services.core;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazione;
import org.openspcoop2.pdd.core.state.IOpenSPCoopState;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.Tracciamento;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.Integrazione;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.config.IProtocolVersionManager;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.constants.RuoloBusta;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.slf4j.Logger;

/**
 * Informazioni per la gestione della risposta
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RicezioneBusteGestioneRisposta {

	private IOpenSPCoopState openspcoopstate;
	
	private OpenSPCoop2Properties propertiesReader;
	private RegistroServiziManager registroServiziReader;
	private ConfigurazionePdDManager configurazionePdDReader;
	private MsgDiagnostico msgDiag;
	private Tracciamento tracciamento;
	private Logger logCore;
	
	private IDSoggetto identitaPdD;
	private String idMessageRequest;

	private boolean mittenteAnonimo;
	private String implementazionePdDMittente;
	
	private HeaderIntegrazione headerIntegrazioneRichiesta;
	private String[] tipiIntegrazionePA;
		
	private GestoreMessaggi msgRequest;
	private RepositoryBuste repositoryBuste;
	private OpenSPCoop2Message requestMessage;

	private boolean portaStateless;
	private boolean oneWayStateless;
	private boolean oneWayVers11;
	private boolean sincronoStateless = false;
	private boolean asincronoStateless = false;
	private boolean routingStateless;
	
	private boolean richiestaRispostaProtocollo;
	
	private TipoPdD tipoPorta;
	private boolean functionAsRouter;
	
	private PortaApplicativa portaApplicativa;
	private PortaDelegata portaDelegata;
	private IDSoggetto soggettoMittente;
	private IDServizio idServizio;
	private String correlazioneApplicativa;
	
	private PdDContext pddContext;
	private Integrazione infoIntegrazione;
	private Transaction transaction;
	
	private IProtocolFactory<?> protocolFactory;
	private ITraduttore traduttore;
	private IProtocolVersionManager moduleManager;
	
	private Busta bustaRichiesta;
	private java.util.List<Eccezione> erroriValidazione;
	private RuoloBusta ruoloBustaRicevuta;
	private String versioneProtocollo;
	private boolean readQualifiedAttribute;
	private ProprietaManifestAttachments proprietaManifestAttachments;
	
	private MessageSecurityContext messageSecurityContext;
	private FlowProperties flowPropertiesResponse;
	
	private RicezioneBusteParametriGenerazioneBustaErrore parametriGenerazioneBustaErrore;
	private RicezioneBusteParametriInvioBustaErrore parametriInvioBustaErrore;
	private RicezioneBusteGeneratoreBustaErrore ricezioneBusteGeneratoreBustaErrore;
	
	

	public Busta getBustaRichiesta() {
		return this.bustaRichiesta;
	}
	public void setBustaRichiesta(Busta bustaRichiesta) {
		this.bustaRichiesta = bustaRichiesta;
	}
	public IProtocolFactory<?> getProtocolFactory() {
		return this.protocolFactory;
	}
	public void setProtocolFactory(IProtocolFactory<?> protocolFactory) {
		this.protocolFactory = protocolFactory;
	}
	public PdDContext getPddContext() {
		return this.pddContext;
	}
	public void setPddContext(PdDContext pddContext) {
		this.pddContext = pddContext;
	}
	public boolean isFunctionAsRouter() {
		return this.functionAsRouter;
	}
	public void setFunctionAsRouter(boolean functionAsRouter) {
		this.functionAsRouter = functionAsRouter;
	}
	public boolean isRichiestaRispostaProtocollo() {
		return this.richiestaRispostaProtocollo;
	}
	public void setRichiestaRispostaProtocollo(boolean richiestaRispostaProtocollo) {
		this.richiestaRispostaProtocollo = richiestaRispostaProtocollo;
	}
	public HeaderIntegrazione getHeaderIntegrazioneRichiesta() {
		return this.headerIntegrazioneRichiesta;
	}
	public void setHeaderIntegrazioneRichiesta(
			HeaderIntegrazione headerIntegrazioneRichiesta) {
		this.headerIntegrazioneRichiesta = headerIntegrazioneRichiesta;
	}
	public String getIdMessageRequest() {
		return this.idMessageRequest;
	}
	public void setIdMessageRequest(String idRequest) {
		this.idMessageRequest = idRequest;
	}
	public String getImplementazionePdDMittente() {
		return this.implementazionePdDMittente;
	}
	public void setImplementazionePdDMittente(String implementazionePdDMittente) {
		this.implementazionePdDMittente = implementazionePdDMittente;
	}
	public IDSoggetto getIdentitaPdD() {
		return this.identitaPdD;
	}
	public void setIdentitaPdD(IDSoggetto identitaPdD) {
		this.identitaPdD = identitaPdD;
	}
	public Logger getLogCore() {
		return this.logCore;
	}
	public void setLogCore(Logger logCore) {
		this.logCore = logCore;
	}
	public MsgDiagnostico getMsgDiag() {
		return this.msgDiag;
	}
	public void setMsgDiag(MsgDiagnostico msgDiag) {
		this.msgDiag = msgDiag;
	}
	public GestoreMessaggi getMsgRequest() {
		return this.msgRequest;
	}
	public void setMsgRequest(GestoreMessaggi msgRequest) {
		this.msgRequest = msgRequest;
	}
	public OpenSPCoop2Message getRequestMessage() {
		return this.requestMessage;
	}
	public void setRequestMessage(OpenSPCoop2Message requestMessage) {
		this.requestMessage = requestMessage;
	}
	public boolean isOneWayVers11() {
		return this.oneWayVers11;
	}
	public void setOneWayVers11(boolean oneWayVers11) {
		this.oneWayVers11 = oneWayVers11;
	}
	public IOpenSPCoopState getOpenspcoopstate() {
		return this.openspcoopstate;
	}
	public void setOpenspcoopstate(IOpenSPCoopState openspcoopstate) {
		this.openspcoopstate = openspcoopstate;
	}
	public boolean isPortaStateless() {
		return this.portaStateless;
	}
	public void setPortaStateless(boolean portaStateless) {
		this.portaStateless = portaStateless;
	}
	public OpenSPCoop2Properties getPropertiesReader() {
		return this.propertiesReader;
	}
	public void setPropertiesReader(OpenSPCoop2Properties propertiesReader) {
		this.propertiesReader = propertiesReader;
	}
	public RepositoryBuste getRepositoryBuste() {
		return this.repositoryBuste;
	}
	public void setRepositoryBuste(RepositoryBuste repository) {
		this.repositoryBuste = repository;
	}
	public String[] getTipiIntegrazionePA() {
		return this.tipiIntegrazionePA;
	}
	public void setTipiIntegrazionePA(String[] tipiIntegrazionePA) {
		this.tipiIntegrazionePA = tipiIntegrazionePA;
	}
	public boolean isRoutingStateless() {
		return this.routingStateless;
	}
	public void setRoutingStateless(boolean routingStateless) {
		this.routingStateless = routingStateless;
	}
	public PortaDelegata getPortaDelegata() {
		return this.portaDelegata;
	}
	public void setPortaDelegata(PortaDelegata portaDelegata) {
		this.portaDelegata = portaDelegata;
	}
	public IDSoggetto getSoggettoMittente() {
		return this.soggettoMittente;
	}
	public void setSoggettoMittente(IDSoggetto soggettoMittente) {
		this.soggettoMittente = soggettoMittente;
	}
	public IDServizio getIdServizio() {
		return this.idServizio;
	}
	public void setIdServizio(IDServizio idServizio) {
		this.idServizio = idServizio;
	}
	public RegistroServiziManager getRegistroServiziReader() {
		return this.registroServiziReader;
	}
	public void setRegistroServiziReader(
			RegistroServiziManager registroServiziReader) {
		this.registroServiziReader = registroServiziReader;
	}
	public ConfigurazionePdDManager getConfigurazionePdDReader() {
		return this.configurazionePdDReader;
	}
	public void setConfigurazionePdDReader(
			ConfigurazionePdDManager configurazionePdDReader) {
		this.configurazionePdDReader = configurazionePdDReader;
	}
	
	public RicezioneBusteParametriGenerazioneBustaErrore getParametriGenerazioneBustaErrore() {
		return this.parametriGenerazioneBustaErrore;
	}
	public void setParametriGenerazioneBustaErrore(
			RicezioneBusteParametriGenerazioneBustaErrore parametriGenerazioneBustaErrore) {
		this.parametriGenerazioneBustaErrore = parametriGenerazioneBustaErrore;
	}
	public RicezioneBusteParametriInvioBustaErrore getParametriInvioBustaErrore() {
		return this.parametriInvioBustaErrore;
	}
	public void setParametriInvioBustaErrore(RicezioneBusteParametriInvioBustaErrore parametriInvioBustaErrore) {
		this.parametriInvioBustaErrore = parametriInvioBustaErrore;
	}
	
	public ITraduttore getTraduttore() {
		return this.traduttore;
	}
	public void setTraduttore(ITraduttore traduttore) {
		this.traduttore = traduttore;
	}
	public IProtocolVersionManager getModuleManager() {
		return this.moduleManager;
	}
	public void setModuleManager(IProtocolVersionManager moduleManager) {
		this.moduleManager = moduleManager;
	}
	public PortaApplicativa getPortaApplicativa() {
		return this.portaApplicativa;
	}
	public void setPortaApplicativa(PortaApplicativa pa) {
		this.portaApplicativa = pa;
	}
	public java.util.List<Eccezione> getErroriValidazione() {
		return this.erroriValidazione;
	}
	public void setErroriValidazione(java.util.List<Eccezione> erroriValidazione) {
		this.erroriValidazione = erroriValidazione;
	}
	public RuoloBusta getRuoloBustaRicevuta() {
		return this.ruoloBustaRicevuta;
	}
	public void setRuoloBustaRicevuta(RuoloBusta ruoloBustaRicevuta) {
		this.ruoloBustaRicevuta = ruoloBustaRicevuta;
	}
	public boolean isReadQualifiedAttribute() {
		return this.readQualifiedAttribute;
	}
	public void setReadQualifiedAttribute(boolean readQualifiedAttribute) {
		this.readQualifiedAttribute = readQualifiedAttribute;
	}
	public ProprietaManifestAttachments getProprietaManifestAttachments() {
		return this.proprietaManifestAttachments;
	}
	public void setProprietaManifestAttachments(ProprietaManifestAttachments proprietaManifestAttachments) {
		this.proprietaManifestAttachments = proprietaManifestAttachments;
	}
	public Integrazione getInfoIntegrazione() {
		return this.infoIntegrazione;
	}
	public void setInfoIntegrazione(Integrazione infoIntegrazione) {
		this.infoIntegrazione = infoIntegrazione;
	}
	public MessageSecurityContext getMessageSecurityContext() {
		return this.messageSecurityContext;
	}
	public void setMessageSecurityContext(MessageSecurityContext messageSecurityContext) {
		this.messageSecurityContext = messageSecurityContext;
	}
	public FlowProperties getFlowPropertiesResponse() {
		return this.flowPropertiesResponse;
	}
	public void setFlowPropertiesResponse(FlowProperties flowPropertiesResponse) {
		this.flowPropertiesResponse = flowPropertiesResponse;
	}
	public TipoPdD getTipoPorta() {
		return this.tipoPorta;
	}
	public void setTipoPorta(TipoPdD tipoPorta) {
		this.tipoPorta = tipoPorta;
	}
	public Transaction getTransaction() {
		return this.transaction;
	}
	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}
	public Tracciamento getTracciamento() {
		return this.tracciamento;
	}
	public void setTracciamento(Tracciamento tracciamento) {
		this.tracciamento = tracciamento;
	}
	public String getCorrelazioneApplicativa() {
		return this.correlazioneApplicativa;
	}
	public void setCorrelazioneApplicativa(String correlazioneApplicativa) {
		this.correlazioneApplicativa = correlazioneApplicativa;
	}
	public String getVersioneProtocollo() {
		return this.versioneProtocollo;
	}
	public void setVersioneProtocollo(String versioneProtocollo) {
		this.versioneProtocollo = versioneProtocollo;
	}
	public boolean isMittenteAnonimo() {
		return this.mittenteAnonimo;
	}
	public void setMittenteAnonimo(boolean mittenteAnonimo) {
		this.mittenteAnonimo = mittenteAnonimo;
	}
	public boolean isOneWayStateless() {
		return this.oneWayStateless;
	}
	public void setOneWayStateless(boolean oneWayStateless) {
		this.oneWayStateless = oneWayStateless;
	}
	public boolean isSincronoStateless() {
		return this.sincronoStateless;
	}
	public void setSincronoStateless(boolean sincronoStateless) {
		this.sincronoStateless = sincronoStateless;
	}
	public boolean isAsincronoStateless() {
		return this.asincronoStateless;
	}
	public void setAsincronoStateless(boolean asincronoStateless) {
		this.asincronoStateless = asincronoStateless;
	}
	public RicezioneBusteGeneratoreBustaErrore getRicezioneBusteGeneratoreBustaErrore() {
		return this.ricezioneBusteGeneratoreBustaErrore;
	}
	public void setRicezioneBusteGeneratoreBustaErrore(
			RicezioneBusteGeneratoreBustaErrore ricezioneBusteGeneratoreBustaErrore) {
		this.ricezioneBusteGeneratoreBustaErrore = ricezioneBusteGeneratoreBustaErrore;
	}
}

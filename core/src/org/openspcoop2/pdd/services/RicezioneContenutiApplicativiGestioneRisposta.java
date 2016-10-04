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


package org.openspcoop2.pdd.services;

import org.slf4j.Logger;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazione;
import org.openspcoop2.pdd.core.state.IOpenSPCoopState;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.protocol.engine.builder.ErroreApplicativoBuilder;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;

/**
 * Informazioni per la gestione della risposta
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RicezioneContenutiApplicativiGestioneRisposta {

	private IOpenSPCoopState openspcoopstate;
	
	private OpenSPCoop2Properties propertiesReader;
	private RegistroServiziManager registroServiziReader;
	private ConfigurazionePdDManager configurazionePdDReader;
	private MsgDiagnostico msgDiag;
	private Logger logCore;
	private ErroreApplicativoBuilder xmlBuilder;
	
	private IDSoggetto identitaPdD;
	private String idMessageRequest;

	private HeaderIntegrazione headerIntegrazioneRichiesta;
	private HeaderIntegrazione headerIntegrazioneRisposta;
	private String[] tipiIntegrazionePD;
	
	private ProprietaErroreApplicativo proprietaErroreAppl;
	private String servizioApplicativo;
	
	private GestoreMessaggi msgRequest;
	private RepositoryBuste repositoryBuste;

	private boolean portaStateless;
	private boolean oneWayVers11;
	private boolean richiestaAsincronaSimmetricaStateless;
	
	private boolean localForward;
	
	private PortaDelegata portaDelegata;
	private IDSoggetto soggettoMittente;
	private IDServizio idServizio;
	
	private PdDContext pddContext;
	
	private IProtocolFactory protocolFactory;
	
	private Busta bustaRichiesta;
	
	public Busta getBustaRichiesta() {
		return this.bustaRichiesta;
	}
	public void setBustaRichiesta(Busta bustaRichiesta) {
		this.bustaRichiesta = bustaRichiesta;
	}
	public IProtocolFactory getProtocolFactory() {
		return this.protocolFactory;
	}
	public void setProtocolFactory(IProtocolFactory protocolFactory) {
		this.protocolFactory = protocolFactory;
	}
	public PdDContext getPddContext() {
		return this.pddContext;
	}
	public void setPddContext(PdDContext pddContext) {
		this.pddContext = pddContext;
	}
	public boolean isLocalForward() {
		return this.localForward;
	}
	public void setLocalForward(boolean localForward) {
		this.localForward = localForward;
	}
	public HeaderIntegrazione getHeaderIntegrazioneRichiesta() {
		return this.headerIntegrazioneRichiesta;
	}
	public void setHeaderIntegrazioneRichiesta(
			HeaderIntegrazione headerIntegrazioneRichiesta) {
		this.headerIntegrazioneRichiesta = headerIntegrazioneRichiesta;
	}
	public HeaderIntegrazione getHeaderIntegrazioneRisposta() {
		return this.headerIntegrazioneRisposta;
	}
	public void setHeaderIntegrazioneRisposta(
			HeaderIntegrazione headerIntegrazioneRisposta) {
		this.headerIntegrazioneRisposta = headerIntegrazioneRisposta;
	}
	public String getIdMessageRequest() {
		return this.idMessageRequest;
	}
	public void setIdMessageRequest(String idRequest) {
		this.idMessageRequest = idRequest;
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
	public ProprietaErroreApplicativo getProprietaErroreAppl() {
		return this.proprietaErroreAppl;
	}
	public void setProprietaErroreAppl(
			ProprietaErroreApplicativo proprietaErroreAppl) {
		this.proprietaErroreAppl = proprietaErroreAppl;
	}
	public RepositoryBuste getRepositoryBuste() {
		return this.repositoryBuste;
	}
	public void setRepositoryBuste(RepositoryBuste repository) {
		this.repositoryBuste = repository;
	}
	public String getServizioApplicativo() {
		return this.servizioApplicativo;
	}
	public void setServizioApplicativo(String servizioApplicativo) {
		this.servizioApplicativo = servizioApplicativo;
	}
	public String[] getTipiIntegrazionePD() {
		return this.tipiIntegrazionePD;
	}
	public void setTipiIntegrazionePD(String[] tipiIntegrazionePD) {
		this.tipiIntegrazionePD = tipiIntegrazionePD;
	}
	public ErroreApplicativoBuilder getXmlBuilder() {
		return this.xmlBuilder;
	}
	public void setXmlBuilder(ErroreApplicativoBuilder xmlBuilder) {
		this.xmlBuilder = xmlBuilder;
	}
	public boolean isRichiestaAsincronaSimmetricaStateless() {
		return this.richiestaAsincronaSimmetricaStateless;
	}
	public void setRichiestaAsincronaSimmetricaStateless(
			boolean richiestaAsincronaSimmetricaStateless) {
		this.richiestaAsincronaSimmetricaStateless = richiestaAsincronaSimmetricaStateless;
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
}

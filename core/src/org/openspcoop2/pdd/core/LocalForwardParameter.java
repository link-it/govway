/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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

package org.openspcoop2.pdd.core;

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.RichiestaApplicativa;
import org.openspcoop2.pdd.config.RichiestaDelegata;
import org.openspcoop2.pdd.core.state.IOpenSPCoopState;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.Servizio;

/**	
 * LocalForwardParameter
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class LocalForwardParameter {

	private Logger log;
	private IDSoggetto identitaPdD;
	private String idModulo;
	private String idRequest;
	private IOpenSPCoopState openspcoopstate;
	private MsgDiagnostico msgDiag;
	private String implementazionePdDMittente;
	private String implementazionePdDDestinatario;
	private String idPdDMittente;
	private String idPdDDestinatario;	
	private RichiestaDelegata richiestaDelegata;
	private RichiestaApplicativa richiestaApplicativa;
	private PdDContext pddContext;
	private IProtocolFactory protocolFactory;
	private Servizio infoServizio;
	private ConfigurazionePdDManager configurazionePdDReader;
	private String idCorrelazioneApplicativa;
	private RepositoryBuste repositoryBuste;
	private boolean stateless;
	private boolean oneWayVersione11;
	private Busta busta;
	
	public LocalForwardParameter(){}

	public boolean isOneWayVersione11() {
		return this.oneWayVersione11;
	}

	public void setOneWayVersione11(boolean oneWayVersione11) {
		this.oneWayVersione11 = oneWayVersione11;
	}

	public Busta getBusta() {
		return this.busta;
	}

	public void setBusta(Busta busta) {
		this.busta = busta;
	}

	public RichiestaApplicativa getRichiestaApplicativa() {
		return this.richiestaApplicativa;
	}

	public void setRichiestaApplicativa(RichiestaApplicativa richiestaApplicativa) {
		this.richiestaApplicativa = richiestaApplicativa;
	}

	public Logger getLog() {
		return this.log;
	}

	public void setLog(Logger log) {
		this.log = log;
	}
	
	public boolean isStateless() {
		return this.stateless;
	}

	public void setStateless(boolean stateless) {
		this.stateless = stateless;
	}

	public RepositoryBuste getRepositoryBuste() {
		return this.repositoryBuste;
	}

	public void setRepositoryBuste(RepositoryBuste repositoryBuste) {
		this.repositoryBuste = repositoryBuste;
	}

	public String getIdCorrelazioneApplicativa() {
		return this.idCorrelazioneApplicativa;
	}

	public void setIdCorrelazioneApplicativa(String idCorrelazioneApplicativa) {
		this.idCorrelazioneApplicativa = idCorrelazioneApplicativa;
	}

	public ConfigurazionePdDManager getConfigurazionePdDReader() {
		return this.configurazionePdDReader;
	}

	public void setConfigurazionePdDReader(
			ConfigurazionePdDManager configurazionePdDReader) {
		this.configurazionePdDReader = configurazionePdDReader;
	}

	public Servizio getInfoServizio() {
		return this.infoServizio;
	}

	public void setInfoServizio(Servizio infoServizio) {
		this.infoServizio = infoServizio;
	}
	
	public IProtocolFactory getProtocolFactory() {
		return this.protocolFactory;
	}

	public void setProtocolFactory(IProtocolFactory protocolFactory) {
		this.protocolFactory = protocolFactory;
	}

	public IDSoggetto getIdentitaPdD() {
		return this.identitaPdD;
	}

	public void setIdentitaPdD(IDSoggetto identitaPdD) {
		this.identitaPdD = identitaPdD;
	}

	public String getIdModulo() {
		return this.idModulo;
	}

	public void setIdModulo(String idModulo) {
		this.idModulo = idModulo;
	}

	public String getIdRequest() {
		return this.idRequest;
	}

	public void setIdRequest(String idRequest) {
		this.idRequest = idRequest;
	}

	public IOpenSPCoopState getOpenspcoopstate() {
		return this.openspcoopstate;
	}

	public void setOpenspcoopstate(IOpenSPCoopState openspcoopstate) {
		this.openspcoopstate = openspcoopstate;
	}

	public MsgDiagnostico getMsgDiag() {
		return this.msgDiag;
	}

	public void setMsgDiag(MsgDiagnostico msgDiag) {
		this.msgDiag = msgDiag;
	}

	public String getImplementazionePdDMittente() {
		return this.implementazionePdDMittente;
	}

	public void setImplementazionePdDMittente(String implementazionePdDMittente) {
		this.implementazionePdDMittente = implementazionePdDMittente;
	}

	public String getImplementazionePdDDestinatario() {
		return this.implementazionePdDDestinatario;
	}

	public void setImplementazionePdDDestinatario(
			String implementazionePdDDestinatario) {
		this.implementazionePdDDestinatario = implementazionePdDDestinatario;
	}

	public RichiestaDelegata getRichiestaDelegata() {
		return this.richiestaDelegata;
	}

	public void setRichiestaDelegata(RichiestaDelegata richiestaDelegata) {
		this.richiestaDelegata = richiestaDelegata;
	}

	public PdDContext getPddContext() {
		return this.pddContext;
	}

	public void setPddContext(PdDContext pddContext) {
		this.pddContext = pddContext;
	}

	public String getIdPdDMittente() {
		return this.idPdDMittente;
	}

	public void setIdPdDMittente(String idPdDMittente) {
		this.idPdDMittente = idPdDMittente;
	}

	public String getIdPdDDestinatario() {
		return this.idPdDDestinatario;
	}

	public void setIdPdDDestinatario(String idPdDDestinatario) {
		this.idPdDDestinatario = idPdDDestinatario;
	}
}

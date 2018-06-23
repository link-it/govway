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

package org.openspcoop2.protocol.basic.config;

import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.config.IProtocolVersionManager;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.StatoFunzionalitaProtocollo;

/**	
 * BasicVersionManager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class BasicVersionManager extends BasicManager implements IProtocolVersionManager {

	public BasicVersionManager(IProtocolFactory<?> protocolFactory) throws ProtocolException{
		super(protocolFactory);
	}
	
	
	
	/* *********** FUNZIONALITA' OFFERTE DALLA PORTA DI DOMINIO ******************* */
	
	@Override
	public StatoFunzionalitaProtocollo getFiltroDuplicati(ProfiloDiCollaborazione profiloCollaborazione) {
		return StatoFunzionalitaProtocollo.DISABILITATA;
	}

	@Override
	public StatoFunzionalitaProtocollo getConsegnaAffidabile(ProfiloDiCollaborazione profiloCollaborazione) {
		return StatoFunzionalitaProtocollo.DISABILITATA;
	}

	@Override
	public StatoFunzionalitaProtocollo getConsegnaInOrdine(ProfiloDiCollaborazione profiloCollaborazione) {
		return StatoFunzionalitaProtocollo.DISABILITATA;
	}

	@Override
	public StatoFunzionalitaProtocollo getCollaborazione(ProfiloDiCollaborazione profiloCollaborazione) {
		return StatoFunzionalitaProtocollo.DISABILITATA;
	}
	
	@Override
	public StatoFunzionalitaProtocollo getIdRiferimentoRichiesta(ProfiloDiCollaborazione profiloCollaborazione) {
		return StatoFunzionalitaProtocollo.DISABILITATA;
	}

	
	
	
	/* *********** PROFILI ASINCRONI ******************* */
	
	@Override
	public boolean isCorrelazioneRichiestaPresenteRispostaAsincronaSimmetrica() {
		return false;
	}

	@Override
	public boolean isCorrelazioneRichiestaPresenteRichiestaStatoAsincronaAsimmetrica() {
		return false;
	}

	@Override
	public boolean isGenerazioneInformazioniServizioCorrelatoAsincronoSimmetrico() {
		return false;
	}

	@Override
	public boolean isGenerazioneInformazioniServizioCorrelatoAsincronoAsimmetrico() {
		return false;
	}

	@Override
	public String getIdCorrelazioneAsincrona(Busta richiesta) {
		return null;
	}

	@Override
	public String getIdCorrelazioneAsincrona(String rifMsg,
			String collaborazione) {
		return null;
	}

	
	
	/* *********** ECCEZIONI ******************* */
	
	@Override
	public boolean isEccezioniLivelloInfoAbilitato() {
		return false;
	}

	@Override
	public boolean isIgnoraEccezioniLivelloNonGrave() {
		return false;
	}

	
	
	
	/* *********** VALIDAZIONE/GENERAZIONE BUSTE ******************* */
	
	@Override
	public boolean isGenerazioneBusteErrore_strutturaMalformataHeaderProtocollo(){
		return false;
	}
	
	@Override
	public boolean isGenerazioneErroreMessaggioOnewayDuplicato() {
		return false;
	}

	
	
	
	
	
	/* *********** ALTRO ******************* */
    
	@Override
	public boolean isUtilizzoIndirizzoSoggettoPresenteBusta() {
		return false;
	}

}

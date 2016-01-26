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

package org.openspcoop2.protocol.basic.config;

import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
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

	public BasicVersionManager(IProtocolFactory protocolFactory){
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
